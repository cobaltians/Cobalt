/**
 *
 * CobaltLocationPlugin
 * Cobalt
 *
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Cobaltians
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *
 */

package fr.cobaltians.cobalt.plugin;

import fr.cobaltians.cobalt.Cobalt;
import fr.cobaltians.cobalt.fragments.CobaltFragment;

import android.app.Activity;
import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by sebastienfamel on 16/07/2014.
 */
public final class CobaltLocationPlugin extends CobaltAbstractPlugin {

	// TAG
	private static final String TAG = CobaltLocationPlugin.class.getSimpleName();

	/***************************************************
     * MEMBERS
     ***************************************************/
	
    private static final String LONGITUDE = "longitude";
    private static final String LATITUDE = "latitude";
    
    /*************************************************************************************************************************
     * CONSTRUCTORS
     *************************************************************************************************************************/
	
	public static CobaltAbstractPlugin getInstance(CobaltPluginWebContainer webContainer, CobaltPluginManager pluginManager) {
    	if (sInstance == null) {
    		sInstance = new CobaltLocationPlugin();
    	}
    	
    	sInstance.addWebContainer(webContainer);
    	sInstance.updatePluginManager(pluginManager);
    	
    	return sInstance;
    }


	/***********************************************************************************************************
     * OVERRIDEN METHODS
     ***********************************************************************************************************/
	
	@Override
	public void onMessage(CobaltPluginWebContainer webContainer, JSONObject message) {
		Activity activity = webContainer.getActivity();
		CobaltFragment fragment = webContainer.getFragment();
		
        LocationManager locationManager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        String provider = locationManager.getBestProvider(criteria, false);
        Location location = locationManager.getLastKnownLocation(provider);

        try {
            String callback = message.getString(Cobalt.kJSCallback);
            if (location != null) {
                JSONObject resultLocation = new JSONObject();
                resultLocation.put(LATITUDE, location.getLatitude());
                resultLocation.put(LONGITUDE, location.getLongitude());
                fragment.sendCallback(callback, resultLocation);
            }
            else if (location == null) {
            	fragment.sendCallback(callback, null);
                if (Cobalt.DEBUG) Log.d(TAG, "location is NULL");
            }
            else if (Cobalt.DEBUG) Log.d(TAG, "onMessage: " + message.toString());

        } 
        catch (JSONException exception) {
        	if (Cobalt.DEBUG) exception.printStackTrace();
        }
	}
}
