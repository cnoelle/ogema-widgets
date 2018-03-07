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
 * Copyright 2014 - 2018
 *
 * Fraunhofer-Gesellschaft zur Förderung der angewandten Wissenschaften e.V.
 *
 * Fraunhofer IWES/Fraunhofer IEE
 */

package org.ogema.widgets.name.service.impl;

import org.ogema.core.model.Resource;
import org.ogema.core.model.simple.BooleanResource;
import org.ogema.core.model.simple.IntegerResource;
import org.ogema.core.model.simple.StringResource;
import org.ogema.core.resourcemanager.pattern.ResourcePattern;
import org.ogema.messaging.basic.services.config.model.EmailConfiguration;

public class EmailSenderPattern extends ResourcePattern<EmailConfiguration> {

	public EmailSenderPattern(Resource match) {
		super(match);
	}

	public final StringResource userName = model.userName();
	public final StringResource email = model.email();
	public final StringResource password = model.password();
	public final StringResource serverURL = model.serverURL();
	public final IntegerResource port = model.port();
	public final BooleanResource active = model.active();
	
	@Override
	public boolean accept() {
		return active.getValue();
	}
	
}