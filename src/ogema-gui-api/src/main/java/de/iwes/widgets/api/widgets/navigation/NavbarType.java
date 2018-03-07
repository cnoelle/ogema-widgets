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

package de.iwes.widgets.api.widgets.navigation;

/**
 * Provides customization options for the navigation bar.
 */
public class NavbarType {
	
	public static final NavbarType DEFAULT = new NavbarType("navbar-default");
	public static final NavbarType INVERSE = new NavbarType("navbar-inverse");
	/**
	 * Note: FIXED_TOP should not be used on its own, but in conjunction with DEFAULT or INVERSE
	 */
	public static final NavbarType FIXED_TOP = new NavbarType("navbar-fixed-top"); // buggy
	
	private final String classname;
	
	public NavbarType(String classname) {
		this.classname = classname;
	}
	
	public String getClassname() {
		return classname;
	}
}
