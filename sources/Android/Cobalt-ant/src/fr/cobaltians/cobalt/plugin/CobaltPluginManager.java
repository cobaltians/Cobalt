/**
 *
 * CobaltPluginManager
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
import android.util.Log;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

public final class CobaltPluginManager {

	// TAG
	private static final String TAG = CobaltPluginManager.class.getSimpleName();
	private static final String GET_INSTANCE_METHOD_NAME = "getInstance";
	
	/********************************************************************************
     * MEMBERS
     ********************************************************************************/
	
	private static CobaltPluginManager sInstance;
	
	private final Context mContext;
	private final HashMap<String, Class<? extends CobaltAbstractPlugin>> mPluginsMap;
	
	/******************************************************************************
     * CONSTRUCTORS
     ******************************************************************************/
	
	private CobaltPluginManager(Context context) {
		mContext = context;
		mPluginsMap = Cobalt.getInstance(mContext).getPlugins();
	}
	
	public static CobaltPluginManager getInstance(Context context) {
		if (sInstance == null) {
			sInstance = new CobaltPluginManager(context);
		}
		
		return sInstance;
	}
	
	/****************************************************************************************************************************************
     * COBALT METHODS
     ****************************************************************************************************************************************/
	
	public boolean onMessage(Context context, CobaltFragment fragment, JSONObject message) {
		try {
			String pluginName = message.getString(Cobalt.kJSPluginName);
			Class<? extends CobaltAbstractPlugin> pluginClass = mPluginsMap.get(pluginName);
			if (pluginClass != null) {
				try {
					Method pluginGetInstanceMethod = pluginClass.getDeclaredMethod(GET_INSTANCE_METHOD_NAME, CobaltPluginWebContainer.class);
					try {
						CobaltPluginWebContainer webContainer = new CobaltPluginWebContainer((Activity) context, fragment);
						CobaltAbstractPlugin plugin = (CobaltAbstractPlugin) pluginGetInstanceMethod.invoke(null, webContainer);
						plugin.onMessage(webContainer, message);
						return true;
					}
					catch (NullPointerException exception) {
						if (Cobalt.DEBUG) {
							Log.e(TAG, "onMessage: " + pluginClass.getSimpleName() + ".getInstance(CobaltPluginWebContainer) method must be static.");
							exception.printStackTrace();
						}
					}
					catch (IllegalAccessException exception) {
						if (Cobalt.DEBUG) exception.printStackTrace();
					}
					catch (InvocationTargetException exception) {
						if (Cobalt.DEBUG) {
							Log.e(TAG, "onMessage: exception thrown by " + pluginClass.getSimpleName() + ".getInstance(CobaltPluginWebContainer) method.");
							exception.printStackTrace();
						}
					}
				}
				catch (NoSuchMethodException exception) {
					if (Cobalt.DEBUG) {
						Log.e(TAG, "onMessage: no method found matching " + pluginClass.getSimpleName() + ".getInstance(CobaltPluginWebContainer).");
						exception.printStackTrace();
					}
				}
			}
			else if (Cobalt.DEBUG) Log.e(TAG, "onMessage: no plugin class found for name " + pluginName + ".");
		}
		catch(JSONException exception) {
			if (Cobalt.DEBUG) {
				Log.e(TAG, "onMessage: name field not found or not a String.");
				exception.printStackTrace();
			}
		}
		
		return false;
	}
	
	public void onFragmentDestroyed(Context context, CobaltFragment fragment) {
		Collection<Class <? extends CobaltAbstractPlugin>> pluginClasses = mPluginsMap.values();
		for (Class <? extends CobaltAbstractPlugin> pluginClass : pluginClasses) {
			
		}
	}
}
