/**
 * ﻿Copyright 2014-2018 Fraunhofer-Gesellschaft zur Förderung der angewandten Wissenschaften e.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/**
 * Copyright 2009 - 2016
 *
 * Fraunhofer-Gesellschaft zur Förderung der angewandten Wissenschaften e.V.
 *
 * Fraunhofer IWES
 *
 * All Rights reserved
 */
/**
 * Copyright 2009 - 2014
 *
 * Fraunhofer-Gesellschaft zur Förderung der angewandten Wissenschaften e.V.
 *
 * Fraunhofer IIS Fraunhofer ISE Fraunhofer IWES
 *
 * All Rights reserved
 */
package de.iwes.sim.roomsimservice.device;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.ogema.apps.roomsim.service.api.RoomInsideSimulation;
import org.ogema.apps.roomsim.service.api.RoomSimConfigPattern;
import org.ogema.apps.roomsim.service.api.SingleRoomSimulation;
import org.ogema.apps.roomsim.service.api.impl.SingleRoomSimulationImpl;
import org.ogema.core.application.ApplicationManager;
import org.ogema.core.application.Timer;
import org.ogema.core.model.Resource;
import org.ogema.core.resourcemanager.ResourceDemandListener;
import org.ogema.model.devices.buildingtechnology.Thermostat;
import org.ogema.model.devices.sensoractordevices.SensorDevice;
import org.ogema.model.locations.Room;
import org.ogema.model.sensors.DoorWindowSensor;
import org.ogema.model.sensors.TemperatureSensor;
import org.ogema.tools.resourcemanipulator.timer.CountDownDelayedExecutionTimer;
import org.ogema.tools.simulation.service.api.SimulationServiceAdmin;
import org.ogema.tools.simulation.service.api.model.SimulatedQuantity;
import org.ogema.tools.simulation.service.api.model.SimulationConfiguration;
import org.ogema.tools.simulation.service.apiplus.SimulationBase;

import de.iwes.sim.roomsimservice.logic.RoomConfig;
import de.iwes.sim.roomsimservice.logic.RoomVolume;
import de.iwes.sim.roomsimservice.logic.UserNumOut;
import de.iwes.sim.roomsimservice.subautoconfig.SubAutoConfigUtil;
import de.iwes.util.resource.ValueResourceHelper;

/**
 * A simulation provider, that simulates e.g. a particular sort of devices. 
 * In this example, a the power generated by a PV plant is simulated.  
 */
public class RoomsimulationServiceSimulation extends SimulationBase<RoomSimConfigPattern, RoomsimulationServicePattern> {
	
	private static final long SIM_UPDATE_INTERVAL = 4000;
	@Override
	protected long getDefaultUpdateInterval(){return SIM_UPDATE_INTERVAL;}

	private final Map<String,SingleRoomSimulationImpl> simulatedObjects = Collections.synchronizedMap(new HashMap<String,SingleRoomSimulationImpl>());
	private final Map<String, List<RoomInsideSimulation<?>>> openListeners = Collections.synchronizedMap(new HashMap<String, List<RoomInsideSimulation<?>>>());
	private final SimulationServiceAdmin simulationServiceAdmin;
	
	@SuppressWarnings("unchecked")
	public RoomsimulationServiceSimulation(final ApplicationManager am, SimulationServiceAdmin simulationServiceAdmin) {
		super(am, RoomsimulationServicePattern.class, true, RoomSimConfigPattern.class);
		this.simulationServiceAdmin = simulationServiceAdmin;
		
		// FIXME unregister listener
		String s = System.getProperty("org.ogema.sim.simulateRemoteGateway");
		if((s!=null)&&Boolean.parseBoolean(s)) {
			SubAutoConfigUtil.addRooms(this, am);
			am.getResourceAccess().addResourceDemand(Room.class, new ResourceDemandListener<Resource>() {
				@Override
				public void resourceAvailable(Resource arg0) {
					SubAutoConfigUtil.addRooms(RoomsimulationServiceSimulation.this, am);
				}
				@Override
				public void resourceUnavailable(Resource arg0) {}
			});
		}
	}	

	@Override
	public String getProviderId() {
		return "Room Simulation Connector";
	}
	
	@Override
	public Class<? extends Resource> getSimulatedType() {
		return Room.class;
	}

	@Override
	public void buildConfigurations(RoomsimulationServicePattern pattern, List<SimulationConfiguration> cfgs, RoomSimConfigPattern simPattern) {
		// Add here configuration values that can be edited by the user, see example below
		RoomVolume volume = new RoomVolume(pattern.volume);
		cfgs.add(volume);
		cfgs.add(new RoomConfig(simPattern, appManager.getResourceAccess(), this));
	}
	
	@Override
	public void buildQuantities(RoomsimulationServicePattern pattern, List<SimulatedQuantity> quantities, RoomSimConfigPattern simPattern) {
		UserNumOut temp = new UserNumOut(simPattern);
		quantities.add(temp);
	}
	
	@Override
	public String getDescription() {
		return "Room simulation offering a service for components that contribute/consume thermal enery and humidity. Simulated room"
				+ "does not provide any simulated quantities, but may serve as location for other"
				+ "simulated devices.";
	}
	
	/** 
	 * Perform the actual simulation here. The targetPattern points to the simulated resource (typically a device). 
	 * The configPattern points to the simulation configuration resource indicating the 
	 * simulation time interval etc.
	 * @param timeStep time since last simulation step in milliseconds
	 */
	@Override
	public void simTimerElapsed(RoomsimulationServicePattern targetPattern, RoomSimConfigPattern configPattern, Timer t, long timeStep) {
		SingleRoomSimulationImpl logic = simulatedObjects.get(targetPattern.model.getLocation());
		logic.step(t.getExecutionTime(), timeStep);
		t.setTimingInterval(configPattern.updateInterval.getValue());

	}

	
	@Override
	protected void initSimulation(RoomsimulationServicePattern targetPattern, RoomSimConfigPattern configPattern) {

		SingleRoomSimulationImpl logic = new SingleRoomSimulationImpl(targetPattern,
				configPattern, appManager.getLogger());
		simulatedObjects.put(targetPattern.model.getLocation(), logic);
		List<RoomInsideSimulation<?>> open = openListeners.get(targetPattern.model.getLocation());
		if(open != null) {
			logger.info("After start of room simulation processing "+open.size()+" simulations.");
			addAllOpenListeners(logic, open);
		} else {
			logger.info("Starting room simulation "+targetPattern.model.getLocation()+" without open simulations.");
		}

		// initialize and activate also the optional fields in targetPattern
		if(ValueResourceHelper.setIfNewCelsius(configPattern.simulatedTemperature, 20.0f) |
				ValueResourceHelper.setIfNew(configPattern.simulatedHumidity, 0.55f) |
				ValueResourceHelper.setIfNew(configPattern.personInRoomNonPersistent, 0) |
				ValueResourceHelper.setIfNew(targetPattern.volume, 50f)) { //50 m^3
			targetPattern.model.activate(true);
		}
		if(ValueResourceHelper.setIfNew(configPattern.updateInterval, 10000)) {
			configPattern.model.activate(true);
		};
	}
	
	@Override
	protected void removeSimulation(RoomsimulationServicePattern targetPattern, RoomSimConfigPattern configPattern) {
		SingleRoomSimulationImpl logic = simulatedObjects.remove(targetPattern.model.getLocation());
		if (logic != null)
			logic.close();
	}
	
	public void stop() {
		synchronized (simulatedObjects) {
			for (SingleRoomSimulationImpl srsi : simulatedObjects.values()) {
				try {
					srsi.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			simulatedObjects.clear();
		}
		synchronized (openListeners) {
			for (List<RoomInsideSimulation<?>> ris : openListeners.values()) {
				for (RoomInsideSimulation<?> s: ris) {
					try {
						s.close();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
			openListeners.clear();
		}
		super.stop();
	}
	
	public SingleRoomSimulation registerRoomSimulation(Room room, RoomInsideSimulation<?> listener) {
		List<RoomInsideSimulation<?>> open;
		synchronized (openListeners) {
			open = openListeners.get(room.getLocation());
			if(open == null) {
				open = Collections.synchronizedList(new ArrayList<RoomInsideSimulation<?>>());
				openListeners.put(room.getLocation(), open);
			}
		}
		synchronized (open) {
			if(!open.contains(listener)) {
				open.add(listener);
			}
		}
		SingleRoomSimulationImpl srs = getWorkingRoomSimulation(room);
		if(srs != null) {
			logger.info("Roomsim processes directly "+listener.getProvider().getProviderId());
			addAllOpenListeners(srs, open);
		} else {
			logger.info("Roomsim postpones "+listener.getProvider().getProviderId());			
		}
		return srs;
	}
	
	private void addAllOpenListeners(SingleRoomSimulationImpl srs, List<RoomInsideSimulation<?>> open) {
		synchronized (open) {
			for(RoomInsideSimulation<?> rc: open) {
				rc.simulationAvailable(srs);
				srs.registerInsideRoomComponent(rc);
			}
			open.clear();
		}
	}

	private SingleRoomSimulationImpl getWorkingRoomSimulation(Room room) {
		synchronized (simulatedObjects) {
			for(Entry<String, SingleRoomSimulationImpl> rp: simulatedObjects.entrySet()) {
				if(rp.getKey().equals(room.getLocation())) {
					//HumSensePattern hsp = rp.getRoomElement(rp.rcsapp.humSim.getSimulationPatterns());
					//if(hsp != null) return rp;
					//else return null;
					return rp.getValue();
				}
			}
		}
		return null;
	}

	public boolean unregisterRoomSimulation(Room room, RoomInsideSimulation<?> pattern) {
		List<RoomInsideSimulation<?>> open = openListeners.get(room.getLocation());
		if(open != null) {
			open.remove(pattern);
		}		
		SingleRoomSimulationImpl srs = getWorkingRoomSimulation(room);
		if(srs != null) {
			srs.unregisterInsideRoomComponent(pattern);
			return true;
		}
		return false;
	}
	
	@Override
	public Resource createSimulatedObject(String deviceLocation) {
		final Room result = (Room)(super.createSimulatedObject(deviceLocation));
		String s = AccessController.doPrivileged(new PrivilegedAction<String>() {

			@Override
			public String run() {
				return System.getProperty("org.ogema.sim.simulateRemoteGateway");
			}
			
		});
		if((s!=null)&&Boolean.parseBoolean(s)) {
			addDevicesForRoom(result);
			
			/** Here we have to wait until room-simulation is really up*/
			/*long delay = Long.getLong("org.ogema.sim.simulationdelay", 5000l);
			if(delay < 5000l) delay = 5000l;
			new CountDownDelayedExecutionTimer(appManager, delay) {
				
				@Override
				public void delayedExecution() {
					//SubAutoConfigUtil.configureHMThermostat(result, simulationServiceAdmin, appManager);
					SubAutoConfigUtil.configureHMDeviceSimulation(result,simulationServiceAdmin, appManager,
							Thermostat.class, "Thermostat simulation _Room Simulation_", null);
					SubAutoConfigUtil.configureHMDeviceSimulation(result,simulationServiceAdmin, appManager,
							SensorDevice.class, "HM TH-Sensor simulation _Room Simulation_",
							TemperatureSensor.class);
					SubAutoConfigUtil.configureHMDeviceSimulation(result,simulationServiceAdmin, appManager,
							DoorWindowSensor.class, "Window-Door Sensor simulation _Room Simulation_", null);
				}
			};*/
			
			/*long simDelay = Long.getLong("org.ogema.sim.simulationdelay", 5000);
			new CountDownDelayedExecutionTimer(appManager, simDelay) {
				@Override
				public void delayedExecution() {
					//SubAutoConfigUtil.configureHMThermostat(result, simulationServiceAdmin, appManager);
					SubAutoConfigUtil.configureHMDeviceSimulation(result,simulationServiceAdmin, appManager,
							Thermostat.class, "Thermostat simulation _Room Simulation_", null);
					SubAutoConfigUtil.configureHMDeviceSimulation(result,simulationServiceAdmin, appManager,
							SensorDevice.class, "HM TH-Sensor simulation _Room Simulation_",
							TemperatureSensor.class);
					SubAutoConfigUtil.configureHMDeviceSimulation(result,simulationServiceAdmin, appManager,
							DoorWindowSensor.class, "Window-Door Sensor simulation _Room Simulation_", null);
				}
			};*/
		}
		return result;
	}
	
	public void addDevicesForRoom(final Room room) {
		long delay = Long.getLong("org.ogema.sim.simulationdelay", 5000l);
		if(delay < 5000l) delay = 5000l;
		new CountDownDelayedExecutionTimer(appManager, delay) {
			
			@Override
			public void delayedExecution() {
				//SubAutoConfigUtil.configureHMThermostat(result, simulationServiceAdmin, appManager);
				SubAutoConfigUtil.configureHMDeviceSimulation(room,simulationServiceAdmin, appManager,
						Thermostat.class, "Thermostat simulation _Room Simulation_", null);
				SubAutoConfigUtil.configureHMDeviceSimulation(room,simulationServiceAdmin, appManager,
						SensorDevice.class, "HM TH-Sensor simulation _Room Simulation_",
						TemperatureSensor.class);
				SubAutoConfigUtil.configureHMDeviceSimulation(room,simulationServiceAdmin, appManager,
						DoorWindowSensor.class, "Window-Door Sensor simulation _Room Simulation_", null);
			}
		};
		
	}
}
