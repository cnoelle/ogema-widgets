/**
 * This file is part of the OGEMA widgets framework.
 *
 * OGEMA is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 3
 * as published by the Free Software Foundation.
 *
 * OGEMA is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OGEMA. If not, see <http://www.gnu.org/licenses/>.
 *
 * Copyright 2014 - 2016
 *
 * Fraunhofer-Gesellschaft zur Förderung der angewandten Wissenschaften e.V.
 *
 * Fraunhofer IWES/Fraunhofer IEE
 */
package com.example.driver.template.patternlistener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ogema.core.application.ApplicationManager;
import org.ogema.core.application.Timer;
import org.ogema.core.application.TimerListener;
import org.ogema.core.model.simple.FloatResource;
import org.ogema.core.resourcemanager.ResourceValueListener;
import org.ogema.core.resourcemanager.pattern.PatternListener;

import com.example.driver.template.pattern.TemplateDriverPattern;

public class TemplateDriverListener implements PatternListener<TemplateDriverPattern> {
	
	private final ApplicationManager appManager;
	public final List<TemplateDriverPattern> availablePatterns = new ArrayList<>();
	// we need not synchronize on the map, since patternAvailable/-Unavailable callbacks all share a common thread
	// Map<pattern model resource path, value listener>  
	private final Map<String, ResourceValueListener<FloatResource>> valueListeners = new HashMap<>();
	// Map<pattern model resource path, timer>  
	private final Map<String, Timer> timers = new HashMap<>();
	
 	public TemplateDriverListener(ApplicationManager appManager) {
		this.appManager = appManager;
	}
	
	@Override
	public void patternAvailable(final TemplateDriverPattern pattern) {
		boolean isReadable = pattern.readable.isActive() && pattern.readable.getValue();
		boolean isWriteable = pattern.writeable.isActive() && pattern.writeable.getValue();

		// sample consistency check
		if (!isReadable && !isWriteable)
			throw new IllegalStateException("Driver cannot process communication information: value is neither readable nor writeable: " + pattern.model);
		else if (isReadable && isWriteable)
			throw new IllegalStateException("Driver cannot process communication information: value is both readable and writeable: " + pattern.model);
		
		availablePatterns.add(pattern);
		
		/*TODO: use information from pattern to start communication connection, register
		 * listeners for write commands to device and perform polling according to read
		 * configurations from device.<br>
		 * The following example is just a simple demonstration how this could be done, but for
		 * most drivers more cases and exceptions have to be processed.
		 */
		if(isWriteable) {
			ResourceValueListener<FloatResource> listener = new ResourceValueListener<FloatResource>() {
				
				// whenever the resource value changes, the driver writes the new value to the external device
				@Override
				public void resourceChanged(FloatResource resource) {
					float value = pattern.value.getValue();
					writeToHardware(value, pattern);
				}
				
			};
			pattern.value.addValueListener(listener, true);
			valueListeners.put(pattern.model.getPath(), listener); // remember the listener, so we can later unregister it
		}
		
		long pollingInterval = -1;
		if (pattern.pollingInterval.isActive()) {
			pollingInterval = pattern.pollingInterval.getValue();
		}
		if(isReadable && pollingInterval > 0) {
			Timer timer = appManager.createTimer(pollingInterval, new TimerListener() {
				
				@Override
				public void timerElapsed(Timer timer) {
					float value = readFromHardware(pattern); 
					pattern.value.setValue(value);
				}
				
			});
			timers.put(pattern.model.getPath(), timer);
		}
		
	}
	
	@Override
	public void patternUnavailable(TemplateDriverPattern pattern) {
		// TODO process remove
		
		availablePatterns.remove(pattern);
		ResourceValueListener<FloatResource> listener = valueListeners.remove(pattern.model.getPath());
		if (listener != null)
			pattern.value.removeValueListener(listener);
		Timer timer = timers.remove(pattern.model.getPath());
		if (timer != null)
			timer.destroy();
	}
	

	private void writeToHardware(float value, TemplateDriverPattern pattern) {
		//TODO: perform write operation of new value; the pattern contains all addressing information
	}

	private float readFromHardware(TemplateDriverPattern pattern) {
		// TODO read value from hardware; the pattern contains all addressing information
		return 0;
	}
}
