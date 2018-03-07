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

package de.iwes.widgets.html.appbox;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.json.JSONArray;
import org.json.JSONObject;
import org.ogema.core.administration.AdminApplication;
import org.ogema.core.administration.AdministrationManager;
import org.osgi.framework.Bundle;

import de.iwes.widgets.api.extended.WidgetData;
import de.iwes.widgets.api.widgets.dynamics.TriggeredAction;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;

public class AppBoxData extends WidgetData {
	
	/*
	 * FIXME
	 * This is slightly cumbersome to use... since there is no possibility to pass arguments directly between widgets on client side...
	 * Typically triggered by a {@see org.ogema.tools.widget.html.form.textfield.TextField}.
	 * @param filter
	 * @param caseSensitive
	 * @return
	 */
	public static final TriggeredAction FILTER_APPS(String filter, boolean caseSensitive) {
		Object[] args = new Object[]{ filter, caseSensitive };
		return new TriggeredAction("filterApps", args);
	}
	
	protected final Map<String, String> descriptions = new HashMap<>();
 	protected final AdministrationManager am;
	protected String backgroundColor = null;
	protected String textColor = null;
	/*
	 * Note: if this argument is null, apps are determined by the widget itself,
	 * using the administration manager
	 */
	private List<AdminApplication> adminApps = null; 
//	private boolean passMetaInfo = false;

	public AppBoxData(AppBox box) {
		super(box);
		this.am = box.am;
	}
	
	@Override
	public JSONObject retrieveGETData(OgemaHttpRequest req) {
		Map<String,String> css = new HashMap<>();
		css.put("display", "flex");
		css.put("flex-wrap", "wrap");
		addCssItem(">div", css);		
		JSONObject obj = new JSONObject();
		obj.put("apps", appsList2JSON(req.getReq()));
		if (backgroundColor != null) {
			Map<String,String> backgroundColor = new HashMap<String, String>();
			backgroundColor.put("background-color", this.backgroundColor);
			addCssItem(".app-container", backgroundColor);
		}
		if (textColor != null) {
			Map<String,String> textColor = new HashMap<String, String>();
			textColor.put("color", this.textColor);
			addCssItem(".app-container", textColor);
		}
		return obj;
	}
	
	@Override
	public JSONObject onPOST(String data, OgemaHttpRequest req) {
		throw new UnsupportedOperationException();
	}
	
//	public void setPassMetaInfo(boolean passMetaInfo) {
//		this.passMetaInfo = passMetaInfo;
//	}
//	
//	public boolean isPassMetaInfo() {
//		return passMetaInfo;
//	}

	public List<AdminApplication> getAdminApps() {
		return adminApps;
	}

	public void setAdminApps(Collection<AdminApplication> adminApps) {
		if (adminApps == null)
			this.adminApps = null;
		else
			this.adminApps = new ArrayList<>(adminApps);
	}
	
	public String getBackgroundColor() {
		return backgroundColor;
	}

	public void setBackgroundColor(String backgroundColor) {
		this.backgroundColor = backgroundColor;
	}
	
	public String getTextColor() {
		return textColor;
	}

	public void setTextColor(String textColor) {
		this.textColor = textColor;
	}

	private final JSONArray appsList2JSON(HttpServletRequest req) {
		List<AdminApplication> apps;
		if (adminApps == null)
			apps = am.getAllApps();  // note: this may return more than one app per bundle; if the bundle registers multiple applications
									 // maybe we should filter these, except if multiple of those apps define different start pages?
		else
			apps = adminApps;

		
		// this causes problems if there is more than one app in a single bundle...
		// they have the same name, hence will be displayed the same way on the GUI -> filter by using a map
		//	Map<String, Map> = Map<App Id, App info>		
		JSONArray array = new JSONArray();

		for (AdminApplication entry : apps) {
			if (!entry.isWebAccessAllowed(req))
				continue;
			String name = entry.getID().getBundle().getSymbolicName();
			String fileName = entry.getID().getBundle().getLocation();
			int lastSeperator = fileName.lastIndexOf("/");
			fileName = fileName.substring(lastSeperator + 1, fileName.length());
			boolean needFilter = false;
			for (String filter : Settings.FILTERED_APPS) {
				if (name.startsWith(filter)) {
					needFilter = true;
					for (String exception : Settings.FILTER_EXCEPTIONS) {
						if (name.contains(exception)) {
							needFilter = false;
							break;
						}
					}
					break;
				}
			}
			if (needFilter) {
				continue;
			}
			long id = entry.getBundleRef().getBundleId();

			JSONObject singleApp = new JSONObject();
			singleApp.put("name",name);
			singleApp.put("id",id);
			Bundle bundle = entry.getBundleRef();
			
			// Map<String,String>
			JSONObject metainfo = new JSONObject();
			metainfo.put("File_Name", fileName);

			Dictionary<String, String> bundleDictionary = bundle.getHeaders();
			Enumeration<String> dictionaryEnums = bundleDictionary.keys();
			while (dictionaryEnums.hasMoreElements()) {
				String key = dictionaryEnums.nextElement();
				String element = bundleDictionary.get(key);

				if (!("Import-Package".equals(key) || "Export-Package".equals(key))) {
					String formattedKey = key.replace('-', '_');
					metainfo.put(formattedKey, element);
				}
			}
			singleApp.put("metainfo",metainfo);
			
			String startPage = entry.getWebAccess().getStartUrl();			
			if (startPage != null)
				singleApp.put("startPage", startPage);
			String descr = ((AppBox) widget).getDescription(entry);
			if (descr != null) 
				singleApp.put("tooltip", descr);
			// System.out.println("  Single App \n" + singleApp.toString(4));
			array.put(singleApp);
		}
		return array;
	}
	
}
