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

import fr.cobaltians.cobalt.fragments.HTMLFragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.util.Log;

import java.io.*;

import org.json.JSONException;
import org.json.JSONObject;

public class Cobalt {

    // TAG
	public static final String TAG = Cobalt.class.getSimpleName();

    // RESOURCES
    private final static String ASSETS_PATH = "file:///android_asset/";

    /********************************************************
     * CONFIGURATION FILE
     *******************************************************/

    private final static String CONF_FILE = "cobalt.conf";
    private final static String kDefaultController = "default";
    private final static String kAndroidController = "androidController";
    public final static String kExtras = "extras";
    public final static String kPage = "page";
    private final static String kActivity = "activity";
    public final static String kPullToRefresh = "pullToRefresh";
    public final static String kInfiniteScroll = "infiniteScroll";
    public final static String kSwipe = "swipe";

    /********************************************************
     * JS KEYWORDS
     *******************************************************/

    // GENERAL
    public final static String kJSAction = "action";
    public final static String kJSCallback = "callback";
    public final static String kJSData = "data";
    public final static String kJSMessage = "message";
    public final static String kJSPage = "page";
    public final static String kJSType = "type";
    public final static String kJSValue = "value";

    // CALLBACKS
    public final static String JSTypeCallBack = "callback";

    // COBALT IS READY
    public final static String JSTypeCobaltIsReady = "cobaltIsReady";

    // EVENTS
    public final static String JSTypeEvent = "event";
    public final static String kJSEvent = "event";

    // INTENT
    public final static String JSTypeIntent = "intent";
    public final static String JSActionOpenExternalUrl = "openExternalUrl";
    public final static String kJSUrl = "url";

    // LOG
    public final static String JSTypeLog = "log";

    // NAVIGATION
    public final static String JSTypeNavigation = "navigation";
    public final static String JSActionNavigationPush = "push";
    public final static String JSActionNavigationPop ="pop";
    public final static String JSActionNavigationModal = "modal";
    public final static String JSActionNavigationDismiss = "dismiss";
    public final static String kJSController = "controller";

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
    public static final String kJSDelete = "delete";
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

    /********************************************************
     * MEMBERS
     *******************************************************/

    private static Cobalt sInstance;
    private final Context mContext;
    private String mResourcePath = "www/";

    /********************************************************
     * CONSTRUCTORS
     *******************************************************/

    private Cobalt(Context context) {
        mContext = context;
    }

    public static Cobalt getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new Cobalt(context);
        }

        return sInstance;
    }

    /********************************************************
     * GETTERS / SETTERS
     *******************************************************/
	
	public String getResourcePath() {
		return ASSETS_PATH + mResourcePath;
	}
	
	public void setResourcePath(String resourcePath) {
		if (resourcePath != null) {
			mResourcePath = resourcePath;
		}
	}

    /********************************************************************************************************************
     * CONFIGURATION FILE
     *******************************************************************************************************************/

    public HTMLFragment getFragmentForController(Class<?> HTMLFragmentClass, String controller, String page) {
        HTMLFragment fragment = null;

        try {
            if (HTMLFragment.class.isAssignableFrom(HTMLFragmentClass)) {
                fragment = (HTMLFragment) HTMLFragmentClass.newInstance();

                Bundle configuration = getConfigurationForController(controller);

                if (configuration != null) {
                    configuration.putString(kPage, page);
                    fragment.setArguments(configuration);
                }
            }
            else if (BuildConfig.DEBUG) Log.e(Cobalt.TAG, TAG + " - getFragmentForController: " + HTMLFragmentClass.getSimpleName() + " does not inherit from HTMLFragment!");
        }
        catch (java.lang.InstantiationException exception) {
            exception.printStackTrace();
        }
        catch (IllegalAccessException exception) {
            exception.printStackTrace();
        }

        return fragment;
    }

    public Intent getIntentForController(String controller, String page) {
        Intent intent = null;

        Bundle configuration = getConfigurationForController(controller);

        if (configuration != null) {
            String activity = configuration.getString(kActivity);

            if (activity != null) {
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
                    else if (BuildConfig.DEBUG) Log.e(Cobalt.TAG, TAG + " - getIntentForController: " + activity + " does not inherit from Activity!");
                }
                catch (ClassNotFoundException exception) {
                    if (BuildConfig.DEBUG) Log.e(Cobalt.TAG, TAG + " - getIntentForController: " + activity + " class not found for id " + controller + "!");
                    exception.printStackTrace();
                }
            }
        }

        return intent;
    }

    private Bundle getConfigurationForController(String controller) {
        Bundle bundle = null;

        JSONObject configuration = getConfiguration();
        String activity = null;
        boolean enablePullToRefresh = false;
        boolean enableInfiniteScroll = false;
        // TODO: add enableGesture

        // Gets configuration
        try {
            if (controller != null
                && configuration.has(controller)) {
                activity = configuration.getJSONObject(controller).getString(kAndroidController);
                enablePullToRefresh = configuration.getJSONObject(controller).optBoolean(kPullToRefresh);
                enableInfiniteScroll = configuration.getJSONObject(controller).optBoolean(kInfiniteScroll);
            }
            else {
                activity = configuration.getJSONObject(kDefaultController).getString(kAndroidController);
                enablePullToRefresh = configuration.getJSONObject(kDefaultController).optBoolean(kPullToRefresh);
                enableInfiniteScroll = configuration.getJSONObject(kDefaultController).optBoolean(kInfiniteScroll);
            }
        }
        catch (JSONException exception) {
            if (BuildConfig.DEBUG) Log.e(Cobalt.TAG,    TAG + " - getConfigurationForController: check cobalt.conf. Known issues: \n "
                                                        + "\t - " + controller + " controller not found and no " + kDefaultController + " controller defined \n "
                                                        + "\t - " + controller + " or " + kDefaultController + "controller found but no " + kAndroidController + "defined \n ");
            exception.printStackTrace();
            return bundle; // null
        }

        bundle = new Bundle();
        bundle.putString(kActivity, activity);
        bundle.putBoolean(kPullToRefresh, enablePullToRefresh);
        bundle.putBoolean(kInfiniteScroll, enableInfiniteScroll);

        return bundle;
    }

    private JSONObject getConfiguration() {
        String configuration = readFileFromAssets(mResourcePath + CONF_FILE);

        try {
            JSONObject jsonObj = new JSONObject(configuration);
            return jsonObj;
        }
        catch (JSONException exception) {
            if (BuildConfig.DEBUG) Log.e(Cobalt.TAG, TAG + " - getConfiguration: check cobalt.conf. File is missing or not at " + ASSETS_PATH + mResourcePath + CONF_FILE);
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
                fileContent.append((char) character) ;
            }

            return fileContent.toString();
        }
        catch (FileNotFoundException exception) {
            if (BuildConfig.DEBUG) Log.e(Cobalt.TAG, TAG + " - readFileFromAssets: " + file + "not found.");
        }
        catch (IOException exception) {
            exception.printStackTrace();
        }

        return new String();
    }
}
