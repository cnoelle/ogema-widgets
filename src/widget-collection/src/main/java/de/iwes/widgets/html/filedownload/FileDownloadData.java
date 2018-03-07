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

package de.iwes.widgets.html.filedownload;

import java.io.File;
import java.math.BigInteger;

import org.json.JSONObject;
import org.ogema.core.security.WebAccessManager;
import org.slf4j.LoggerFactory;

import de.iwes.widgets.api.extended.WidgetData;
import de.iwes.widgets.api.widgets.dynamics.TriggeredAction;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;

import java.security.SecureRandom;

public class FileDownloadData extends WidgetData {

    public static final TriggeredAction STARTDOWNLOAD = new TriggeredAction("download");
    public static final TriggeredAction GET_AND_STARTDOWNLOAD = new TriggeredAction("getAndDownload");
    private final WebAccessManager wam;
    private static final SecureRandom random = new SecureRandom();
    private String url;
    private FileDownloadServlet servlet;
    private String webPath;
    private String customWebPath;
    private boolean customWebPathRegistered;
    private boolean deleteFileAfterDownload = false;
    private boolean active = false;

    /**
     * ********* Constructor
     *
     **********
     * @param id
     */
    public FileDownloadData(FileDownload download, WebAccessManager wam) {
        super(download);
        this.wam = wam;
        customWebPathRegistered = false;
    }

    /**
     * ***** Inherited methods ******
     */
    @Override
    public JSONObject retrieveGETData(OgemaHttpRequest req) {
        JSONObject result;
        result = new JSONObject();
        if (active && !isDisabled() && url != null) {
            result.put("url", url.toString());
        }
        else {
            result.put("url", "");
        }
        	
        return result;
    }

    @Override
    public JSONObject onPOST(String json, OgemaHttpRequest req) {
        return new JSONObject();
    }

    //When session "dies" unregister web-resource
    @Override
    protected void finalize() {
        if (webPath != null) { //Only unregister if registered
            wam.unregisterWebResource(webPath);
        }
        try {
            super.finalize();
        } catch (Throwable ex) {
            LoggerFactory.getLogger(FileDownloadData.class).error(ex.toString());
        }
    }

    private static String nextSessionId() { //Own session-id for download-security
        return new BigInteger(130, random).toString(32);
    }

    /**
     * ******** Public methods *********
     */

    public boolean setFile(File file, String customFileName, boolean forceDownload) {
    	if (webPath != null) {
    		try {
    			wam.unregisterWebResource(webPath);
    		} catch (Exception e) {
    			LoggerFactory.getLogger(getClass()).error("Could not unregister old servlet " + e);
    		}
    	}
    	
        if (wam == null || file == null) {
        	active = false;
            return false;
        } else {
            servlet = new FileDownloadServlet(file, forceDownload, deleteFileAfterDownload);

            if (customWebPath == null) { //If application didn't set webPath
                webPath = "/dl/" + nextSessionId();
                webPath = wam.registerWebResourcePath(webPath, servlet);
            } else {
                if (!customWebPathRegistered) {
                    webPath = customWebPath;
                    webPath = wam.registerWebResourcePath(webPath, servlet);
                    customWebPathRegistered = true;
                }
            }

            url = webPath + "/";

            if (customFileName == null) {
                url += file;
            } else {
                url += customFileName;
            }
            active = true;
            return true;
        }
    }

    public void setWebPath(String customWebPath) {
        this.customWebPath = customWebPath;
    }

    public String getURL() {
        return url;
    }
    
    public void setDeleteFileAfterDownload(boolean doDelete) {
    	deleteFileAfterDownload = doDelete;
    }

}
