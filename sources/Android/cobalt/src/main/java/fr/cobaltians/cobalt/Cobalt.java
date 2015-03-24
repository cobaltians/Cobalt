/**
 *
 * Cobalt
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

package fr.cobaltians.cobalt;

import fr.cobaltians.cobalt.activities.CobaltActivity;
import fr.cobaltians.cobalt.fragments.CobaltFragment;
import fr.cobaltians.cobalt.plugin.CobaltAbstractPlugin;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.util.Log;

import java.io.*;
import java.util.HashMap;
import java.util.Iterator;

import junit.framework.Assert;

import org.json.JSONException;
import org.json.JSONObject;

public class Cobalt {

    // TAG
	public static final String TAG = Cobalt.class.getSimpleName();

    // DEBUG
    public static boolean DEBUG = true;

    // RESOURCES
    private static final String ASSETS_PATH = "file:///android_asset/";

    // INFINITE SCROLL
    public static final int INFINITE_SCROLL_OFFSET_DEFAULT_VALUE = 0;

    /**********************************************************************************************
     * CONFIGURATION FILE
     **********************************************************************************************/

    private final static String CONF_FILE = "cobalt.conf";
    private final static String kControllers = "controllers";
    private final static String kPlugins = "plugins";
    private final static String kAndroid = "android";
    private final static String kDefaultController = "default";
    // TODO: uncomment for Bars
    /*
    public final static String kBars = "bars";
    public final static String kVisible = "visible";
    public final static String kBackgroundColor = "backgroundColor";
    public final static String kIcon = "androidIcon";
    public final static String kTitle = "title";
    public final static String kActions = "actions";
    public final static String kName = "name";
    public final static String kPosition = "androidPosition";
    public final static String kPositionOverflow = "overflow";
    public final static String kPositionTop = "top";
    public final static String kPositionBottom = "bottom";
    */
    public final static String kExtras = "extras";
    public final static String kPage = "page";
    public final static String kActivity = "activity";
    public final static String kPopAsModal = "popAsModal";
    public final static String kPushAsModal = "pushAsModal";
    public final static String kPullToRefresh = "pullToRefresh";
    public final static String kInfiniteScroll = "infiniteScroll";
    public final static String kInfiniteScrollOffset = "infiniteScrollOffset";
    public final static String kSwipe = "swipe";

    /**********************************************************************************************
     * JS KEYWORDS
     **********************************************************************************************/

    // GENERAL
    public final static String kJSAction = "action";
    public final static String kJSCallback = "callback";
    public final static String kJSData = "data";
    public final static String kJSMessage = "message";
    public final static String kJSPage = "page";
    public final static String kJSType = "type";
    public final static String kJSValue = "value";
    public final static String kJSVersion = "version";

    // CALLBACKS
    public final static String JSTypeCallBack = "callback";

    // COBALT IS READY
    public final static String JSTypeCobaltIsReady = "cobaltIsReady";

    // EVENTS
    public final static String JSTypeEvent = "event";
    public final static String kJSEvent = "event";

    // APP EVENTS
    public final static String JSEventOnAppStarted = "onAppStarted";
    public final static String JSEventOnAppBackground = "onAppBackground";
    public final static String JSEventOnAppForeground = "onAppForeground";
    public final static String JSEventOnPageShown = "onPageShown";

    // INTENT
    public final static String JSTypeIntent = "intent";
    public final static String JSActionIntentOpenExternalUrl = "openExternalUrl";
    public final static String kJSUrl = "url";

    // LOG
    public final static String JSTypeLog = "log";

    // NAVIGATION
    public final static String JSTypeNavigation = "navigation";
    public final static String JSActionNavigationPush = "push";
    public final static String JSActionNavigationPop ="pop";
    public final static String JSActionNavigationModal = "modal";
    public final static String JSActionNavigationDismiss = "dismiss";
    public final static String JSActionNavigationReplace = "replace";
    public final static String kJSController = "controller";
    public final static String kJSAnimated = "animated";

    // BACK BUTTON
    public final static String JSEventOnBackButtonPressed = "onBackButtonPressed";
    public final static String JSCallbackOnBackButtonPressed = "onBackButtonPressed";

    // UI
    public final static String JSTypeUI = "ui";
    public final static String kJSUIControl = "control";

    // ALERT
    public final static String JSControlAlert = "alert";
    public final static String kJSAlertTitle = "title";
    public final static String kJSAlertButtons = "buttons";
    public final static String kJSAlertCancelable = "cancelable";
    public final static String kJSAlertButtonIndex  = "index";


    // TODO: uncomment for Bars
    // BARS
    /*
    public final static String JSControlBars = "bars";
    public final static String JSActionButtonPressed = "buttonPressed";
    public final static String kJSBarsButton = "button";
    */

    // DATE PICKER
    public static final String JSControlPicker = "picker";
    public static final String JSPickerDate = "date";
    public static final String kJSDate = "date";
    public static final String kJSDay = "day";
    public static final String kJSMonth = "month";
    public static final String kJSYear = "year";
    public static final String kJSTexts = "texts";
    public static final String kJSTitle ="title";
    public static final String kJSCancel = "cancel";
    //public static final String kJSDelete = "delete";
    public static final String kJSClear = "clear";
    public static final String kJSValidate = "validate";

    // TOAST
    public final static String JSControlToast = "toast";

    // WEB LAYER
    public final static String JSTypeWebLayer = "webLayer";
    public final static String JSActionWebLayerShow = "show";
    public final static String JSActionWebLayerDismiss = "dismiss";
    public final static String kJSWebLayerFadeDuration = "fadeDuration";
    public final static String JSEventWebLayerOnDismiss = "onWebLayerDismissed";

    // PULL TO REFRESH
    public final static String JSEventPullToRefresh = "pullToRefresh";
    public final static String JSCallbackPullToRefreshDidRefresh = "pullToRefreshDidRefresh";

    // INFINITE SCROLL
    public final static String JSEventInfiniteScroll= "infiniteScroll";
    public final static String JSCallbackInfiniteScrollDidRefresh = "infiniteScrollDidRefresh";

    //PLUGIN
    public final static String JSTypePlugin = "plugin";
    public final static String kJSPluginName = "name";

    /**********************************************************************************************
     * MEMBERS
     **********************************************************************************************/

    private static Cobalt sInstance;
    private static Context mContext;

    private String mResourcePath = "www/";

    private int mRunningActivities = 0;
    private boolean mFirstActivityStart = true;

    /**********************************************************************************************
     * CONSTRUCTORS
     **********************************************************************************************/

    private Cobalt(Context context) {
        mContext = context.getApplicationContext();
    }

    public static Cobalt getInstance(Context context) {
        if (sInstance == null) {
            Assert.assertNotNull(TAG + " - getInstance: context could not be null", context);
            sInstance = new Cobalt(context);
        }

        return sInstance;
    }

    /**********************************************************************************************
     * GETTERS / SETTERS
     **********************************************************************************************/
	
	public String getResourcePath() {
		return ASSETS_PATH + mResourcePath;
	}
	
	public void setResourcePath(String resourcePath) {
        if (resourcePath != null) mResourcePath = resourcePath;
        else mResourcePath = "";
	}

    public static Context getAppContext() {
        return mContext;
    }

    /**********************************************************************************************
     * APP LIFECYCLE
     **********************************************************************************************/

    public void onActivityStarted(CobaltActivity activity) {
        if (++mRunningActivities == 1) {
            if (mFirstActivityStart) {
                mFirstActivityStart = false;
                
                activity.onAppStarted();
            }
            else activity.onAppForeground();
        }
    }

    public void onActivityStopped(CobaltActivity activity) {
        if (--mRunningActivities == 0) activity.onAppBackground();
    }

    /**********************************************************************************************
     * CONFIGURATION FILE
     **********************************************************************************************/

    public CobaltFragment getFragmentForController(Class<?> CobaltFragmentClass, String controller, String page) {
        CobaltFragment fragment = null;

        try {
            if (CobaltFragment.class.isAssignableFrom(CobaltFragmentClass)) {
                fragment = (CobaltFragment) CobaltFragmentClass.newInstance();
                Bundle configuration = getConfigurationForController(controller);
                configuration.putString(kPage, page);
                fragment.setArguments(configuration);
            }
            else if (Cobalt.DEBUG) Log.e(Cobalt.TAG, TAG + " - getFragmentForController: " + CobaltFragmentClass.getSimpleName() + " does not inherit from CobaltFragment!");
        }
        catch (java.lang.InstantiationException exception) {
            if (Cobalt.DEBUG) Log.e(Cobalt.TAG, TAG + " - getFragmentForController: InstantiationException");
            exception.printStackTrace();
        }
        catch (IllegalAccessException exception) {
            if (Cobalt.DEBUG) Log.e(Cobalt.TAG, TAG + " - getFragmentForController: IllegalAccessException");
            exception.printStackTrace();
        }

        return fragment;
    }

    public Intent getIntentForController(String controller, String page) {
        Intent intent = null;

        Bundle configuration = getConfigurationForController(controller);

        if (! configuration.isEmpty()) {
            String activity = configuration.getString(kActivity);

            // Creates intent
            Class<?> pClass;
            try {
                pClass = Class.forName(activity);
                // Instantiates intent only if class inherits from Activity
                if (Activity.class.isAssignableFrom(pClass)) {
                    configuration.putString(kPage, page);

                    intent = new Intent(mContext, pClass);
                    intent.putExtra(kExtras, configuration);
                }
                else if (Cobalt.DEBUG) Log.e(Cobalt.TAG, TAG + " - getIntentForController: " + activity + " does not inherit from Activity!");
            }
            catch (ClassNotFoundException exception) {
                if (Cobalt.DEBUG) Log.e(Cobalt.TAG, TAG + " - getIntentForController: " + activity + " class not found for id " + controller + "!");
                exception.printStackTrace();
            }
        }

        return intent;
    }

    private Bundle getConfigurationForController(String controller) {
        Bundle bundle = new Bundle();

        JSONObject configuration = getConfiguration();

        // Gets configuration
        try {
            JSONObject controllers = configuration.getJSONObject(kControllers);

            String activity;
            // TODO: uncomment for Bars
            //JSONObject actionBar;
            boolean enablePullToRefresh;
            boolean enableInfiniteScroll;
            int infiniteScrollOffset;
            // TODO: add enableGesture

            if (controller != null
                && controllers.has(controller)) {
                activity = controllers.getJSONObject(controller).getString(kAndroid);
                //actionBar = controllers.getJSONObject(controller).optJSONObject(kBars);
                enablePullToRefresh = controllers.getJSONObject(controller).optBoolean(kPullToRefresh);
                enableInfiniteScroll = controllers.getJSONObject(controller).optBoolean(kInfiniteScroll);
                infiniteScrollOffset = controllers.getJSONObject(controller).optInt(kInfiniteScrollOffset, INFINITE_SCROLL_OFFSET_DEFAULT_VALUE);
            }
            else {
                activity = controllers.getJSONObject(kDefaultController).getString(kAndroid);
                //actionBar = controllers.getJSONObject(kDefaultController).optJSONObject(kBars);
                enablePullToRefresh = controllers.getJSONObject(kDefaultController).optBoolean(kPullToRefresh);
                enableInfiniteScroll = controllers.getJSONObject(kDefaultController).optBoolean(kInfiniteScroll);
                infiniteScrollOffset = controllers.getJSONObject(kDefaultController).optInt(kInfiniteScrollOffset, INFINITE_SCROLL_OFFSET_DEFAULT_VALUE);
            }

            bundle.putString(kActivity, activity);
            //if (actionBar != null) bundle.putString(kBars, actionBar.toString());
            bundle.putBoolean(kPullToRefresh, enablePullToRefresh);
            bundle.putBoolean(kInfiniteScroll, enableInfiniteScroll);
            bundle.putInt(kInfiniteScrollOffset, infiniteScrollOffset);

            return bundle;
        }
        catch (JSONException exception) {
            if (Cobalt.DEBUG) Log.e(Cobalt.TAG,     TAG + " - getConfigurationForController: check cobalt.conf. Known issues: \n "
                                                    + "\t - controllers field not found or not a JSONObject \n "
                                                    + "\t - " + controller + " controller not found and no " + kDefaultController + " controller defined \n "
                                                    + "\t - " + controller + " or " + kDefaultController + "controller found but no " + kAndroid + "defined \n ");
            exception.printStackTrace();
        }

        return bundle;
    }

    /**********************************************************************************************
     * PLUGINS FILE
     **********************************************************************************************/

    public HashMap<String, Class<? extends CobaltAbstractPlugin>> getPlugins() {
        HashMap<String, Class<? extends CobaltAbstractPlugin>> pluginsMap = new HashMap<String, Class<? extends CobaltAbstractPlugin>>();

        try {
            JSONObject configuration = getConfiguration();
            JSONObject plugins = configuration.getJSONObject(kPlugins);
            Iterator<String> pluginsIterator = plugins.keys();

            while (pluginsIterator.hasNext()) {
                String pluginName = pluginsIterator.next();
                try {
                    JSONObject plugin = plugins.getJSONObject(pluginName);
                    String pluginClassName = plugin.getString(kAndroid);

                    try {
                        Class<?> pluginClass = Class.forName(pluginClassName);
                        if (CobaltAbstractPlugin.class.isAssignableFrom(pluginClass)) {
                            pluginsMap.put(pluginName, (Class<? extends CobaltAbstractPlugin>) pluginClass);
                        }
                        else if (Cobalt.DEBUG) Log.e(Cobalt.TAG, TAG + " - getPlugins: " + pluginClass + " does not inherit from CobaltAbstractActivity!\n" + pluginName + " plugin message will not be processed.");
                    }
                    catch (ClassNotFoundException exception) {
                        if (Cobalt.DEBUG) {
                            Log.e(Cobalt.TAG, TAG + " - getPlugins: " + pluginClassName + " class not found!\n" + pluginName + " plugin message will not be processed.");
                            exception.printStackTrace();
                        }
                    }
                }
                catch (JSONException exception) {
                    if (Cobalt.DEBUG) {
                        Log.e(Cobalt.TAG, TAG + " - getPlugins: " + pluginName + " field is not a JSONObject or does not contain an android field or is not a String.\n" + pluginName + " plugin message will not be processed.");
                        exception.printStackTrace();
                    }
                }
            }
        }
        catch (JSONException exception) {
            if (Cobalt.DEBUG) {
                Log.w(Cobalt.TAG, TAG + " - getPlugins: plugins field of cobalt.conf not found or not a JSONObject.");
                exception.printStackTrace();
            }
        }

        return pluginsMap;
    }
    
    /**********************************************************************************************
     * HELPER METHODS
     **********************************************************************************************/

    private JSONObject getConfiguration() {
        String configuration = readFileFromAssets(mResourcePath + CONF_FILE);

        try {
            return new JSONObject(configuration);
        }
        catch (JSONException exception) {
            if (Cobalt.DEBUG) Log.e(Cobalt.TAG, TAG + " - getConfiguration: check cobalt.conf. File is missing or not at " + ASSETS_PATH + mResourcePath + CONF_FILE);
            exception.printStackTrace();
        }

        return new JSONObject();
    }

    private String readFileFromAssets(String file) {
        try {
            AssetManager assetManager = mContext.getAssets();
            InputStream inputStream = assetManager.open(file);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder fileContent = new StringBuilder();
            int character;

            while ((character = bufferedReader.read()) != -1) {
                fileContent.append((char) character);
            }

            return fileContent.toString();
        }
        catch (FileNotFoundException exception) {
            if (Cobalt.DEBUG) Log.e(Cobalt.TAG, TAG + " - readFileFromAssets: " + file + "not found.");
        }
        catch (IOException exception) {
            if (Cobalt.DEBUG) Log.e(Cobalt.TAG, TAG + " - readFileFromAssets: IOException");
            exception.printStackTrace();
        }

        return "";
    }
}
