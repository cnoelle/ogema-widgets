package org.smartrplace.gateway.device;

import org.ogema.core.model.simple.IntegerResource;
import org.ogema.core.model.simple.TimeResource;
import org.ogema.model.prototypes.PhysicalElement;

/** Sensor and actor data for the gateway itself<br>
 * Note that a top-level resource of this type can be obtained/created via the method ResourceHelper#getLocalDevice(ApplicationManager)*/
public interface GatewayDevice extends PhysicalElement {
	/** Each time a pull operation on Git is performed on update shall be written into the resource:
	 * 1: success, no update found
	 * 2: update found, OGEMA restart triggered (more details may be provided with additional positive values)
	 * -1: pull operation failed (more detailed negative values may be defined to provide more information)
	 */
	IntegerResource gitUpdateStatus();
	
	/** Indication of an operating system and bundle restart
	 * 1  : Reboot of the operating system detected (e.g. because power was disconnected for some time or because reboot was
	 * 		initiated from console)
	 * 10 : Restart of the OGEMA framework detected 
	 * 100: API-testing bundle (no custom resources)
	 * 200: Roomcontrol bundle
	 */
	IntegerResource systemRestart();
	
	/** Interval for heartbeat sending to superior instance*/
	TimeResource heartBeatDelay();
	
	IntegerResource activeAlarmSupervision();
	IntegerResource datapointsInAlarmState();
}
