/**
 * Copyright 2009 - 2014
 *
 * Fraunhofer-Gesellschaft zur Förderung der angewandten Wissenschaften e.V.
 *
 * Fraunhofer IIS
 * Fraunhofer ISE
 * Fraunhofer IWES
 *
 * All Rights reserved
 */
package org.ogema.model.protectedaction;

import org.ogema.core.model.simple.StringResource;
import org.ogema.model.prototypes.Data;

/** Note: This model shall only be accessible by administration applications. Via the 
 * action FixDestinationCopyAction that can be accessed by applications that got user approval
 * the copy action can be performed.*/
public interface CopyFileToMusic extends Data {
	/**All files below this root can be commanded to be copied into the music data area*/
	StringResource sourceRoot();
	
	/**If the copy action needs to be called with differing parameters (e.g. source)
	 * use NonP version. Add MusicDestinationParameters as decorator if required.*/
	FixDestinationCopyAction run();
	
	/**Parameters that cannot be overridden by the application via run.parameters*/
	FixDestinationCopyParameters nonOverridable();
	/**Parameters that can be overridden by the application via run.parameters*/
	FixDestinationCopyParameters defaultParameters();
	
	MusicDestinationParameters nonOverridableMusicParam();
	MusicDestinationParameters defaultMusicParam();
}