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

package de.iwes.widgets.api.extended.plus;

import de.iwes.widgets.api.widgets.OgemaWidget;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;

/**
 * A widget that needs to be initialized from an external source.
 * This is mainly used for the initialization of a widget based on 
 * parameters in the page URL.
 */
public interface InitWidget extends OgemaWidget {

	/**
	 * Called once per page and session when the widget data is requested for the 
	 * first time. 
	 * @param req
	 */
	void init(OgemaHttpRequest req);
	
}
