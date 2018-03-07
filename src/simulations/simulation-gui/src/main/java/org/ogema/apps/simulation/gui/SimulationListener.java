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

/**
 * Copyright 2009 - 2014
 *
 * Fraunhofer-Gesellschaft zur F�rderung der angewandten Wissenschaften e.V.
 *
 * Fraunhofer IIS Fraunhofer ISE Fraunhofer IWES
 *
 * All Rights reserved
 */
package org.ogema.apps.simulation.gui;

import java.util.List;
import java.util.Map;

import org.ogema.apps.simulation.gui.configuration.ConfigModal;
import org.ogema.apps.simulation.gui.configuration.CreateModal;
import org.ogema.apps.simulation.gui.configuration.PlotsModal;
import org.ogema.apps.simulation.gui.configuration.SimQuModal;
import org.ogema.apps.simulation.gui.templates.SimulationAccordionItem;
import org.ogema.core.model.Resource;
import org.ogema.tools.simulation.service.api.SimulationProvider;
import org.ogema.tools.simulation.service.api.SimulationProviderListener;
import org.ogema.tools.simulation.service.api.SimulationService;
import org.ogema.tools.simulation.service.api.SimulationServiceAdmin;

import de.iwes.widgets.api.extended.WidgetPageBase;
import de.iwes.widgets.html.accordion.Accordion;
import de.iwes.widgets.html.alert.Alert;
import de.iwes.widgets.html.complextable.DynamicTable;

class SimulationListener implements SimulationProviderListener<Resource> {
	
	private final List<SimulationProvider<?>> providers;
	private final DynamicTable<String> table;
	private final Accordion acc;
	private final WidgetPageBase<?> page;
	private final ConfigModal modal;
	private final CreateModal createModal;
	private final SimQuModal simQuModal;
	private final PlotsModal plotsModal;
	private final Alert alert;
	private final Map<String,SimulationAccordionItem> accItems;
	private final SimulationServiceAdmin service;

	SimulationListener(List<SimulationProvider<?>> providers, WidgetPageBase<?> page, DynamicTable<String> table, Accordion acc,Map<String,SimulationAccordionItem> accItems, 
			ConfigModal modal, CreateModal createModal, SimQuModal simQuModal, PlotsModal plotsModal, Alert alert, SimulationServiceAdmin service) {
		this.providers = providers;
		this.table = table;
		this.acc = acc;
		this.page = page;
		this.modal = modal;
		this.createModal = createModal;
		this.simQuModal = simQuModal;
		this.plotsModal = plotsModal;
		this.accItems = accItems;
		this.alert=alert;
		this.service = service;
		service.registerListener(this);
	}
	
	void close() {
		try {
			service.deregisterListener(this);
		} catch (Exception e) {}
	}
	
	@Override
	public void simulationAvailable(SimulationProvider<?> provider) {
		if (!providers.contains(provider)) {
			providers.add(provider);   // order of entries is important here... the addRow method accesses the providers list to get a reference to provider
			final Utils utils = Utils.getInstance(5000);
			if (utils != null)
				table.addItem(utils.getValidWidgetId(provider.id()), null);
			SimulationAccordionItem item = new SimulationAccordionItem(provider, page,modal,createModal,simQuModal,plotsModal,alert);
			acc.addItem(provider, item.getPanel(), false,null);
			accItems.put(provider.id(),item);
			
		}
	}

	@Override
	public void simulationUnavailable(SimulationProvider<?> provider) {
		if (providers.remove(provider)) {
			final Utils utils = Utils.getInstance();
			if (utils != null)
				table.removeRow(utils.getValidWidgetId(provider.id()),null);
		}
		acc.removeItem(provider.id(), null);
		accItems.remove(provider.id());
	}

	@Override
	public Class<Resource> getType() {
		return Resource.class;
	}

	public List<SimulationProvider<?>> getSimulationProviders() {
		return providers;
	}


}
