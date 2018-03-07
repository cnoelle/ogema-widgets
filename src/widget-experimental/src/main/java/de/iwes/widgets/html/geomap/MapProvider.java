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
package de.iwes.widgets.html.geomap;

import java.util.Arrays;
import java.util.List;

import org.json.JSONObject;

import de.iwes.widgets.api.widgets.localisation.OgemaLocale;
import de.iwes.widgets.template.DisplayTemplate;


// FIXME need a 3-step hierarchy: 
// - library (google, openlayers, leaflet),
// - map type (google; google, osm, bing, ...; google, osm, bing, ...) (probably shared between libraries)
// - display style (roadmap, satellite, terrain, ...) (probably there is a restrictet set per map type; and the libraries will support all (or a subset thereof?)) 
// keys will be set at map type level only
public enum MapProvider {
	
	// do not change the ids -> they have hardcoded counterparts in html and js scripts
	GOOGLE("google", "GoogleMap", Arrays.<DisplayStyle> asList(
			new DisplayItemImpl("roadmap", "Roadmap"),
			new DisplayItemImpl("hybrid", "Satellite with roads"),
			new DisplayItemImpl("satellite", "Satellite"),
			new DisplayItemImpl("terrain", "Terrain")
	)),
	OPENSTREETMAP("openstreetmap", "Open Street Map", Arrays.<DisplayStyle> asList(
			// TODO
			new DisplayItemImpl("road", "Roadmap")
	)),
	OPENTOPOMAP("opentopomap", "OpenTopoMap", Arrays.<DisplayStyle> asList(
			// TODO
			new DisplayItemImpl("topo", "Topographic map")
	)),
	BING("bing", "Bing Map", Arrays.<DisplayStyle> asList(
			// TODO extension
			new DisplayItemImpl("road", "Roadmap"),
			new DisplayItemImpl("aerial", "Aerial"),
			new DisplayItemImpl("aerialWithLabels", "Aerial with Labels")
	));

	// FIXME create generic labelled item class
	public static interface DisplayStyle {
			
		String id();
		String label();
		
	}
	
	public static final DisplayTemplate<DisplayStyle> DISPLAY_STYLE_TEMPLATE = new DisplayTemplate<MapProvider.DisplayStyle>() {

		@Override
		public String getId(DisplayStyle object) {
			return object.id();
		}

		@Override
		public String getLabel(DisplayStyle object, OgemaLocale locale) {
			return object.label();
		}
	};
	
	private final String id;
	private final List<DisplayStyle> displayTypes;
	private final String label;
	
	private MapProvider(String id, String label, List<DisplayStyle> displayTypes) {
		this.id = id;
		this.displayTypes = displayTypes;
		this.label = label;
	}
	
	public String getId() {
		return id;
	}
	
	public List<DisplayStyle> getSupportedDisplayTypes() {
		return displayTypes;
	}
	
	public boolean supportsType(DisplayStyle style) {
		for (DisplayStyle s : displayTypes) {
			if (s == style)
				return true;
		}
		return false;
	}
	
	JSONObject getJson(DisplayStyle displayType) {
		final JSONObject json = new JSONObject();
		json.put("id", id);
		json.put("label", label);
		final JSONObject style = new JSONObject();
		style.put("id", displayType.id());
		style.put("label", displayType.label());
		json.put("displayStyle", style);
		return json;
	}
	
	private static class DisplayItemImpl implements DisplayStyle {
		
		private final String id;
		private final String label;
		
		public DisplayItemImpl(String id, String label) {
			this.id = id;
			this.label = label;
		}

		@Override
		public String id() {
			return id;
		}

		@Override
		public String label() {
			return label;
		}
		
	}
	
}
