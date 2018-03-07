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

package de.iwes.widgets.reswidget.scheduleplot.flot;

import java.util.Collection;

import org.ogema.core.model.Resource;

import de.iwes.widgets.api.widgets.WidgetPage;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;
import de.iwes.widgets.html.plotflot.FlotDataSet;
import de.iwes.widgets.html.plotflot.PlotFlot;
import de.iwes.widgets.html.plotflot.PlotFlotOptions;
import de.iwes.widgets.reswidget.scheduleplot.api.VolatileResourceLogger;

public class ResourcePlotFlot extends PlotFlot implements VolatileResourceLogger<FlotDataSet, ResourceDataFlot>{
	
	private static final long serialVersionUID = 1L;
	private Collection<Resource> defaultResources = null;
	
	/****** Constructor *******/
	
	public ResourcePlotFlot(WidgetPage<?> page, String id, boolean globalWidget) {
		super(page, id, globalWidget);
		setDefaultPollingInterval(5000);
	}

	/***** Inherited methods ******/
	
	@Override
	public ResourcePlotFlotOptions createNewSession() {
		return new ResourcePlotFlotOptions(this);
	}
	
	@Override
	public ResourcePlotFlotOptions getData(OgemaHttpRequest req) {
		return (ResourcePlotFlotOptions) super.getData(req);
	}
	
	@Override
	protected void setDefaultValues(PlotFlotOptions opt) {
		super.setDefaultValues(opt);
		ResourcePlotFlotOptions opt2 = (ResourcePlotFlotOptions) opt;
		if (defaultResources != null)
			opt2.getResourceData().setResources(defaultResources);
	}
	
	
	/****** Public methods ********/
	
	@Override
	public void setDefaultResources(Collection<Resource> resources) {
		this.defaultResources = resources;
	}

	@Override
	public ResourceDataFlot getResourceData(OgemaHttpRequest req) {
		return getData(req).getResourceData();
	}


}
