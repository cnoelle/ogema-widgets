package org.smartrplace.system.guiappstore.config;

import org.ogema.core.model.simple.StringResource;
import org.ogema.core.model.simple.TimeResource;
import org.ogema.model.prototypes.PhysicalElement;

/** 
 * Access configuration for gateways and groups of them.
 */
public interface GatewayData extends PhysicalElement {
	//@Deprecated
	//ResourceList<GatewayGroupData> groups();
	
	GatewayGroupData installationLevelGroup();
	
	StringResource customer();
	//StringResource comment();
	StringResource guiLink();
	
	/** See {@link InstallAppDevice#installationStatus()}
	 */
	//IntegerResource installationStatus();
	
	/** GatewayId used to read remote slotsDb data*/
	StringResource remoteSlotsGatewayId();
	
	/** Maximum interval of heartbeat signals to avoid sending a connection lost message in milliseconds*/
	TimeResource warningMessageInterval();
	
	/** Interval for sending the entire communication structure requested in milliseconds*/
	TimeResource structureUpdateInterval();
}
