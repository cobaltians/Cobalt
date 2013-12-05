package fr.haploid.androidnativebridge.fragments;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.Toast;
import fr.haploid.androidnativebridge.R;
import fr.haploid.androidnativebridge.activities.HTMLActivity;
import fr.haploid.androidnativebridge.customviews.OverScrollingWebView;
import fr.haploid.androidnativebridge.database.LocalStorage;
import fr.haploid.androidnativebridge.webViewClients.ScaleWebViewClient;

/**
 * {@link Fragment} that allows webview's interactions between Java (native side) and JavaScript (web side)
 * 
 * @author Diane Moebs
 */

public class HTMLFragment extends Fragment {

	protected boolean mDebug;
	/**
	 * the bundle key where the all the informations related to the {@link HTMLFragment} are stored
	 */
	public static String kExtras = "kExtras";

	/**
	 * the bundle key where the pageName to load of the {@link HTMLFragment} is stored
	 */
	public static String kPageName = "kPageName";

	/**
	 * the bundle key where the resourcePath of the {@link HTMLFragment} is stored
	 */
	public static String kResourcePath = "kResourcePath";

	/**
	 * the bundle key where the boolean to enable the pullToRefresh feature of the {@link HTMLFragment} is stored
	 */
	public static String kPullToRefresh = "kPullToRefresh";

	/**
	 * the bundle key where the boolean to enable the infiniteScroll feature of the {@link HTMLFragment} is stored
	 */
	public static String kInfiniteScroll = "kInfiniteScroll";

	/**
	 * the bundle key where the boolean to enable the swipe feature of the {@link HTMLFragment} is stored
	 */
	public static String kSwipe = "kSwipe";

	private static String ASSETS_PATH = "file:///android_asset/";

	//CONF FILE
	private static String CONF_FILE = "nativeBridge.conf";
	private static String kAndroidClassName = "androidClassName";

	/**
	 * the key "type" to specify the type of interaction
	 */
	protected static String kJSType = "type";

	/**
	 * type's value => event
	 */
	protected static String JSTypeEvent = "typeEvent";

	/**
	 * type's value => log (for debug use)
	 */
	protected static String JSTypeLog = "typeLog";

	/**
	 * type's value => callback
	 */
	protected static String JSTypeCallBack = "typeCallback";

	/**
	 * type's value => navigation
	 */
	protected static String JSTypeNavigation = "typeNavigation";

	/**
	 * type's value => native alert
	 */
	protected static String JSTypeAlert = "typeAlert";

	/**
	 * type's value => web alert
	 */
	protected static String JSTypeWebAlert = "typeWebAlert";

	/**
	 * type's value => callback
	 */
	private static String JSTypeNativeBridgeReady = "nativeBridgeIsReady";
	//EVENTS
	/**
	 * the key "name" to specify the event
	 */
	protected static String kJSName = "name";

	/**
	 * name's value => toast
	 */
	protected static String JSNameToast = "nameToast";

	/**
	 * name's value => set zoom
	 */
	protected static String JSNameSetZoom = "nameSetZoom";

	/**
	 * name's value => when a webalert has been dismissed
	 */
	protected static String JSOnWebAlertDismissed = "onWebAlertDismissed";

	/**
	 * the key "value" used to pass a value to JavaScript
	 */
	protected static String kJSValue = "value";

	//CALLBACKS
	/**
	 * the key "callbackID" used to pass the callBackId that must be called by JavaScript after the treatments handled by the current request 
	 */
	protected static String kJSCallbackID = "callbackID";

	/**
	 * the key "params" used to pass some datas with the given callback (in type "typeCallback")
	 */
	protected static String kJSParams = "params";

	private static String JSCallbackBackButtonPressed = "onBackButtonPressed";


	//NAVIGATION
	/**
	 * the key "navigationType" to specify the type of navigation that shall be executed
	 */
	protected static String kJSNavigationType = "navigationType";

	/**
	 * a value of "navigationType" => push : next view will be pushed on the stack
	 */
	protected static String JSNavigationTypePush = "push";

	/**
	 * a value of "navigationType" => pop : ask for a pop on the back stack
	 */
	protected static String JSNavigationTypePop ="pop";

	/**
	 * a value of "navigationType" => modale : simulates an iOS kind of navigation with modal views (see documentation for further informations)
	 */
	protected static String JSNavigationTypeModale = "modale";

	/**
	 * a value of "navigationType" => dismiss : simulates an iOS kind of navigation with modal views (see documentation for further informations)
	 */
	protected static String JSNavigationTypeDismiss = "dismiss";

	/**
	 * the key "navigationPageName" => used to pass the page name to be loaded in the webview on the creation of the view
	 */
	protected static String kJSNavigationPageName = "navigationPageName";

	/**
	 * the key "navigationClassId" => used to pass the class name (from the configuration file) to be instantiated on the creation of the view
	 */
	protected static String kJSNavigationClassId = "navigationClassId";

	/**
	 * the key "navigationClassName" => used to store the className to allow the "dismiss" operation on Android
	 */
	protected static String kJSNavigationClassName = "navigationClassName";

	/**
	 * the key "default" => the default class Id where the default className to instantiate will be looked for in the configuration's file.
	 */
	protected static String JSNavigationDefaultClassId = "default";

	//ALERT
	/**
	 * the key "alertTitle" is used to specify the title of the native alert to generate
	 */
	protected static String kJSAlertTitle ="alertTitle";

	/**
	 * the key "alertMessage" is used to specify the message of the native alert to generate
	 */
	protected static String kJSAlertMessage ="alertMessage";

	/**
	 * the key "alertButtons" is used to specify the titles of the buttons of the native alert to generate
	 *  You can give up to three buttons, but should not give more than two for aesthetic reason.
	 */
	protected static String kJSAlertButtons ="alertButtons";

	/**
	 * the key "alertReceiver" is used to specify if the receiver of the result of the button clicked should be either :
	 * the native side -> the callback method alertDialogClickedButton(alertId,indexOfClickedButton) will be called
	 * the web side -> a callback method will be called on web side (depending on the callbackId) with the index of the clicked button and the id of the alert
	 */
	protected static String kJSAlertCallbackReceiver ="alertReceiver";

	/**
	 * the key "alertId" is used to specify which alert is displayed or clicked (useful when several different alerts are due to be displayed in a same activity)
	 */
	protected static String kJSAlertID  ="alertId";

	/**
	 * the key "index" is used to specify which button was clicked in the given alert.
	 */
	protected static String kJSAlertButtonIndex  ="index";

	/**
	 * the key "alertIsCancelable" is used to specify whether the alert should be cancelable when user clicks on back button
	 */
	protected static String kJSAlertIsCancelable ="alertIsCancelable";

	/**
	 * the value for the key "alertReceiver" => a web callback will be called (with the given Id)
	 */
	protected static String JSAlertCallbackReceiverWeb ="web";

	/**
	 * the value for the key "alertReceiver" => a native callback (the method alertDialogClickedButton(alertId,indexOfClickedButton) will be called)
	 */
	protected static String JSAlertCallbackReceiverNative ="native";


	//WEBALERT
	/**
	 * use the name "show" in the typeWebAlert to show the given webAlert
	 */
	protected static String JSWebAlertShow = "show";

	/**
	 * use the name "dismiss" in the typeWebAlert to dismiss the given webAlert
	 */
	protected static String JSWebAlertDismiss = "dismiss";

	/**
	 * the key for specifying which page should be loaded in the alertWebView
	 */
	protected static String kJSWebAlertPageName = "pageName";

	/**
	 * the key for specifying the fade duration for the webAlert to be shown or dismissed
	 */
	protected static String kJSWebAlertfadeDuration = "fadeDuration";

	//ACTIVITY'S FEATURES
	/**
	 * use this key to specify if the activity that will be shown should have the pullToRefreshFeature active
	 */
	protected static String kJSPullToRefreshActive ="pullToRefresh";

	/**
	 * use this key to specify if the activity that will be shown should have the infiniteScroll active
	 */
	protected static String kJSInfiniteScrollActive ="infiniteScroll";


	//PROPERTIES
	/**
	 * the path where to find the all the resources of the HTML code, the JavaScript libraries and the configuration file
	 */
	protected String ressourcePath;

	/**
	 * the pageName that will be loaded in webView
	 */
	protected String pageName;

	/**
	 * the webView where pageName is loaded
	 */
	protected OverScrollingWebView webView;


	/**
	 * the frameLayout containing the webView
	 */
	protected FrameLayout webViewPlaceholder;

	/**
	 * the application Context
	 */
	protected Context mContext;

	/**
	 * a boolean to know if a content has already been loaded in the webview (just after onCreate) or if the webview is still empty
	 */
	protected boolean webviewContentHasBeenLoaded;
	protected Handler mHandler;

	private ArrayList<JSONObject> waitingJavaScriptCallsList;
	private boolean webViewLoaded;
	private boolean nativeBridgeIsReady;
	private boolean preloadOnCreateView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setRetainInstance(true);
		mDebug = false;
		mContext = (Context) getActivity().getApplicationContext();
		waitingJavaScriptCallsList = new ArrayList<JSONObject>();
		webViewLoaded = false;
		webviewContentHasBeenLoaded = false;
		preloadOnCreateView = true;
		mHandler = new Handler();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		nativeBridgeIsReady = false;
		View view = inflater.inflate(getLayoutToInflate(), container,false);
		setUpViews(view);
		setUpListeners();
		return view;
	}	

	@Override
	public void onStart() {
		super.onStart();
		addWebview();
		preloadContent();
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
	}

	@Override
	public void onDetach() {
		super.onDetach();
	}

	@Override
	public void onStop() {
		super.onStop();
		//fragment will rotate (or been destroyed) so we don't preload again the content defined in fragment's arguments
		preloadOnCreateView = false;
		removeWebviewFromPlaceholder();
	}

	/**
	 * This method is called to add the webview in the placeholder (and create it if necessary)
	 * This method SHOULD NOT be overridden in subclasses.
	 */
	protected void addWebview()
	{
		if(webView == null)
		{
			webView = new OverScrollingWebView(mContext);
			setWebViewSettings(this);
		}

		if(webViewPlaceholder != null)
		{
			webViewPlaceholder.addView(webView);
		}
		else 
		{
			if(mDebug) Log.e(getClass().getSimpleName(), "ERROR : you must set up webViewPlaceholder in setUpViews !");
		}		
	}

	/**
	 * This method is called when the fragment is about to rotate. 
	 * This method SHOULD NOT be overridden in subclasses.
	 */
	protected void removeWebviewFromPlaceholder()
	{
		if (webViewPlaceholder != null)
		{
			if(webView != null)
			{
				// Remove the WebView from the old placeholder
				webViewPlaceholder.removeView(webView);
			}
		}
		else 
		{
			if(mDebug) Log.e(getClass().getSimpleName(), "ERROR : you must set up webViewPlaceholder in setUpViews !");
		}
	}


	/**
	 * In this method, the webView state is saved.
	 */
	@Override
	public void onSaveInstanceState(Bundle outState)
	{
		super.onSaveInstanceState(outState);
		if(webView != null)
			webView.saveState(outState);
	}

	/**
	 * In this method, the webView state is restored.
	 */
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		if(webView != null)
			webView.restoreState(savedInstanceState);		
	}

	/**
	 * This method should be overridden in subclasses
	 * @return the identifier of the layout that will be inflated in this fragment
	 */
	protected int getLayoutToInflate()
	{
		return R.layout.html_fragment;
	}

	/**
	 * This method should be overridden in subclasses. 
	 * Here, you set up the fragment's properties depending on the layout that has been inflated
	 * @param rootView the root of the views that has been inflated
	 */
	protected void setUpViews(View rootView)
	{
		webViewPlaceholder = ((FrameLayout)rootView.findViewById(R.id.webViewPlaceholder));
	}

	/**
	 * This method should be overridden in subclasses. 
	 * Here you set up the listeners for the components that were inflated in the given layout (from layoutToInflate) and set up in setUpViews(View rootView)
	 */
	protected void setUpListeners()
	{

	}

	/**
	 * This method returns a new instance of the fragment that will be displayed as the {@link HTMLPopUpWebview} in the fragment container where the current fragment is shown.
	 * This method may be overridden in subclasses if the {@link HTMLPopUpWebview} must implement customized dialogs with native side such as in "public boolean handleMessageSentByJavaScript(String messageJS)".
	 * @param fileName : the filename that will be displayed in the {@link HTMLPopUpWebview}
	 * @return a new instance of the {@link HTMLPopUpWebview} that should be added in this {@link HTMLActivity}
	 */
	protected HTMLPopUpWebview getPopUpWebview(String fileName)
	{
		return new HTMLPopUpWebview();
	}

	private void preloadContent()
	{
		if(preloadOnCreateView)
		{
			if(this.webView != null)
			{
				if(this.getArguments() != null && this.getArguments().containsKey(kResourcePath))
				{
					this.ressourcePath = this.getArguments().getString(kResourcePath);
				}

				if(this.getArguments() != null && this.getArguments().containsKey(kPageName))
				{
					this.pageName = this.getArguments().getString(kPageName);
				}

				if(this.pageName != null && this.ressourcePath != null)
				{
					loadFileContentFromAssets(this.ressourcePath, this.pageName);
					webviewContentHasBeenLoaded = true;
				}
				else
				{
					this.ressourcePath = (this.ressourcePath == null) ?"www/" : this.ressourcePath;
					this.pageName = (this.pageName != null) ? this.pageName : "index.html";
					loadFileContentFromAssets(this.ressourcePath, this.pageName);
				}
			}
		}
	}

	/**
	 * Load the given file in this.webView 
	 * @param pathInAssets : the path in assets where the file named fileName is to be found
	 * @param fileName : the filenName to load in this.webView
	 * @warning All the HTML Files of the whole applications should be found in the same place at the root of this.ressourcePath
	 */
	public void loadFileContentFromAssets(String pathInAssets,String fileName)
	{
		if(webView != null)
		{
			//String hTMLStructure = getFileContentFromAssets(pathInAssets+fileName);
			String baseUrl = ASSETS_PATH+pathInAssets+fileName;
			//webView.loadDataWithBaseURL(baseUrl,hTMLStructure,"text/html","UTF-8",baseUrl);
			//Log.e(getClass().getSimpleName(), "load url "+baseUrl);
			webView.loadUrl(baseUrl);
		}
	}

	/**
	 * sends jsonObj to the JavaScript in this.webView
	 * @param jsonObj : the JSONObject that will be sent to the webView and handled in the JavaScript code.
	 */
	public void executeScriptInWebView(final JSONObject jsonObj)
	{
		if(jsonObj != null)
		{
			if(webViewLoaded || nativeBridgeIsReady)
			{
				mHandler.post(new Runnable() {

					@Override
					public void run() {
						String jsonMessage = jsonObj.toString().replaceAll("[\u2028\u2029]", "");
						String url = "javascript:nativeBridge.execute("+jsonMessage+");";
						if(webView != null)
						{
							//Log.e(getClass().getSimpleName(), "load url "+jsonMessage);
							webView.loadUrl(url);		
						}
						else
						{
							if(mDebug) Log.e(getClass().getSimpleName(), "ERROR : message cannot been sent to empty webview : "+jsonMessage);
						}						
					}
				});
			}
			else
			{
				waitingJavaScriptCallsList.add(jsonObj);
			}
		}
	}


	/**
	 * This method is called when a button is clicked on an alertDialog that was generated by the web.
	 * This method may be overridden in subclasses
	 * @param tag : the alertId of the alert
	 * @param buttonIndex : the clicked button
	 */
	public void alertDialogClickedButton(long tag,int buttonIndex)
	{
		/*
		 * Default implementation
		 */
	}

	/**
	 * Use this method to send an Object (int, String, JSONObject...) to the web with the given callbackId
	 * @param callbackId : the callbackId to call back after having handled native instructions.
	 * @param objectToSend : the params to pass when calling the callback named callbackId
	 */
	public void sendCallbackResponse(final String callbackId, final Object objectToSend)
	{
		if(callbackId != null && callbackId.length() > 0)
		{
			JSONObject obj = new JSONObject();
			try {
				obj.put(kJSType,JSTypeCallBack);
				obj.put(kJSCallbackID, callbackId);
				obj.put(kJSParams, objectToSend);	
				executeScriptInWebView(obj);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * This method is called when the JavaScript sends a message to the native side.
	 * This method should be overridden in subclasses.
	 * @param messageJS : the JSON-message sent by JavaScript.
	 * @return true if the message was handled by the native, false otherwise
	 * @details some basic operations are already implemented : navigation, logs, toasts, native alerts, web alerts
	 * @details this method may be called from a secondary thread.
	 */
	//this method must be public !!!
	@JavascriptInterface
	public boolean handleMessageSentByJavaScript(String messageJS)
	{
		final JSONObject jsonObj;
		//Log.e(getClass().getSimpleName(), messageJS);

		try 
		{
			jsonObj = new JSONObject(messageJS);
			if(jsonObj != null)
			{
				if(jsonObj.has(kJSType))
				{
					String type = jsonObj.optString(kJSType);
					//EVENT
					if(type != null && type.length() >0 && type.equals(JSTypeEvent))
					{
						String name = jsonObj.optString(kJSName);
						if(name != null && name.length() >0 && name.equals(JSNameToast))
						{
							String message = jsonObj.optString(kJSValue);
							if(message != null)
							{
								Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
								return true;
							}
						}
					}
					//LOG
					else if(type != null && type.length() >0 && type.equals(JSTypeLog))
					{
						String message = jsonObj.optString(kJSValue);
						if(message != null)
						{
							Log.w("JS Logs", message);
							return true;
						}
					}
					//NAVIGATE
					else if(type != null && type.length() >0 && type.equals(JSTypeNavigation))
					{
						if(jsonObj.has(kJSNavigationType))
						{
							String navType = jsonObj.optString(kJSNavigationType);
							//PUSH
							if(navType != null && navType.length() >0 && navType.equals(JSNavigationTypePush))
							{
								String activityId = jsonObj.optString(kJSNavigationClassId);
								String pageNamed = jsonObj.optString(kJSNavigationPageName);
								pushWebView(activityId,pageNamed);
								return true;
							}
							//POP
							else if(navType != null && navType.length() >0 && navType.equals(JSNavigationTypePop))
							{
								popWebViewActivity();
								return true;
							}
							//MODALE
							else if(navType != null && navType.length() >0 && navType.equals(JSNavigationTypeModale))
							{
								String activityId = jsonObj.optString(kJSNavigationClassId);
								String pageNamed = jsonObj.optString(kJSNavigationPageName);
								String callBackId = jsonObj.optString(kJSCallbackID);
								presentWebView(activityId, pageNamed, callBackId);
								return true;
							}
							//DISMISS
							else if(navType != null && navType.length() >0 && navType.equals(JSNavigationTypeDismiss))
							{
								String className = jsonObj.optString(kJSNavigationClassName);
								String pageNamed = jsonObj.optString(kJSNavigationPageName);
								dismissWebViewWithActivity(className, pageNamed);
								return true;
							}
						}
					}
					//WEB ALERT
					else if(type != null && type.length() >0 && type.equals(JSTypeWebAlert))
					{
						String name = jsonObj.optString(kJSName);
						if(name != null && name.length() > 0 && name.equals(JSWebAlertShow))
						{
							mHandler.post(new Runnable() {

								@Override
								public void run() {
									showWebAlertWithJSON(jsonObj);
								}
							});
							return true;
						}
					}

					//CALLBACK
					else if(type != null && type.length() >0 && type.equals(JSTypeCallBack))
					{
						String callbackID = jsonObj.optString(kJSCallbackID);
						if(callbackID != null && callbackID.length() >0 && callbackID.equals(JSCallbackBackButtonPressed))
						{
							boolean allowToGoBack = jsonObj.optBoolean(kJSParams);
							handleBackButtonPressed(allowToGoBack);
							return true;
						}
					}
					else if(type != null && type.length() >0 && type.equals(JSTypeAlert))
					{
						showAlertDialogWithJSON(jsonObj);
					}
					else if(type != null && type.length() >0 && type.equals(JSTypeNativeBridgeReady))
					{
						if(mDebug) Log.i(getClass().getSimpleName(), "native bridge is ready. waiting calls : "+waitingJavaScriptCallsList.size());

						nativeBridgeIsReady = true;
						executeWaitingCalls();
					}
				}

			}
		} catch (JSONException e1) {
			if(mDebug) Log.e(getClass().getSimpleName(),"ERROR : CANNOT HANDLE MESSAGE WITH JSON EXCEPTION FOR JSON : #"+messageJS+"#");
			e1.printStackTrace();
		}
		return false;
	}



	/**
	 * This method is called when the backButton is pressed. It asks the webView whether the default action of the backbutton should be fired.
	 * This method should NOT be overridden in subclasses.
	 */
	public void askWebViewForBackPermission()
	{
		JSONObject j = new JSONObject();
		try {
			j.put(kJSType, JSTypeEvent);
			j.put(kJSName, JSCallbackBackButtonPressed);
			j.put(kJSCallbackID, JSCallbackBackButtonPressed);
			executeScriptInWebView(j);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	/**
	 * This method is called from the corresponding {@link HTMLPopUpWebview} when the popup has been dismissed.
	 * This method may be overridden in subclasses.
	 */
	public void onWebPopupDismiss(final String fileName,Object additionalParams)
	{
		mHandler.post(new Runnable() {
			@Override
			public void run() {
				JSONObject obj = new JSONObject();
				try {
					obj.put(kJSType,JSTypeEvent);
					obj.put(kJSName, JSOnWebAlertDismissed);
					obj.put(kJSValue, fileName);
					executeScriptInWebView(obj);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		});
	}


	/**
	 * This method is called when the webView has decided to allow or not the backButton's actions.
	 * @details This method should not be overridden in subclasses
	 * @param allowedToGoBack : if true, the default backButton action will be performed, otherwise, executeActionWhenBackButtonWasInhibited() will be called
	 */
	protected void handleBackButtonPressed(boolean allowedToGoBack)
	{
		if(allowedToGoBack)
		{
			HTMLActivity a = (HTMLActivity)getActivity();
			if(a != null)
			{
				a.goBack();
			}
			else if(mDebug) Log.e(getClass().getSimpleName(), "ERROR : Activity is null !! CANNOT CALL onBackPressed();");

		}
		else 
		{
			executeActionWhenBackButtonWasInhibited();
		}
	}


	/**
	 * This method is called when the backButton action was inhibited by the webView.
	 * @details This method may be overridden in subclasses if a custom action is to be performed.
	 */
	protected void executeActionWhenBackButtonWasInhibited()
	{
		if(mDebug) Log.i(getClass().getSimpleName(), "action onBackPressed inhibited by webView");
	}

	private void pushWebView(String activityId, String pageNamed) {
		Intent i = getIntentForClassId(activityId, pageNamed);
		if(i != null)
		{
			getActivity().startActivity(i);
		}
		else Log.e(getClass().getSimpleName(), "ERROR BAD INTENT TO PUSH NOTHING HAPPENS");
	}

	private void popWebViewActivity() {
		handleBackButtonPressed(true);
	}


	private void presentWebView(String activityId,String pageNamed,String callBackID)
	{
		Intent i = getIntentForClassId(activityId, pageNamed);
		if(i != null)
		{
			getActivity().startActivity(i);
			//send callback to store this.className and HTMLPage to dismiss later !
			JSONObject objectToSend = new JSONObject();
			try {
				objectToSend.put(kJSNavigationPageName, this.pageName);
				objectToSend.put(kJSNavigationClassName, getActivity() != null ? getActivity().getClass().getName() : "ERROR : activity's className couldn't be found.");		
				sendCallbackResponse(callBackID, objectToSend);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}	
	}

	private void dismissWebViewWithActivity(String className,String pageNamed)
	{
		try {
			Class<?> myClass = Class.forName(className);

			//if the class inherits from an activity, we navigate to it
			if(Activity.class.isAssignableFrom(myClass))
			{
				Bundle bundleToAdd = new Bundle();
				bundleToAdd.putString(kPageName, pageNamed);
				bundleToAdd.putString(kResourcePath, this.ressourcePath);

				Intent i = new Intent(mContext,myClass);
				i.putExtra(kExtras, bundleToAdd);

				NavUtils.navigateUpTo(getActivity(), i);
			}
			else
			{
				if(mDebug) Log.e(getClass().getSimpleName(), "ERROR : impossible to dismiss the current activity since "+className+" does not inherit from Activity");
			}
		} catch (ClassNotFoundException e) {
			if(mDebug) Log.e(getClass().getSimpleName(), "catched classNotFound "+e.getLocalizedMessage());
			e.printStackTrace();
		}
	}

	private JSONObject getConfFileContent()
	{
		String confToParse = getFileContentFromAssets(this.ressourcePath+CONF_FILE);
		JSONObject jsonObj;

		if(confToParse != null && confToParse.length() > 0)
		{
			try {
				jsonObj = new JSONObject(confToParse);
				return jsonObj;
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		else
		{
			if(mDebug) Log.e(getClass().getSimpleName(),"ERROR : check your nativeBridge.conf... File is missing or not at this.ressourcePath in assets ?");
		}
		return new JSONObject();
	}

	private Intent getIntentForClassId(String classId,String pageNamed)
	{
		JSONObject confs = getConfFileContent();
		Intent i = null;

		if(confs != null)
		{
			String className = "";
			boolean pullToRefresh = false,infiniteScroll = false;
			try {
				//Find Infos from conf file
				if(classId != null && confs.has(classId))
				{

					className = confs.getJSONObject(classId).optString(kAndroidClassName);
					pullToRefresh = confs.getJSONObject(classId).optBoolean(kJSPullToRefreshActive);
					infiniteScroll = confs.getJSONObject(classId).optBoolean(kJSInfiniteScrollActive);
				}

				if(className == null || className.length() == 0)
				{
					Log.w(getClass().getSimpleName(), "WARNING : className for ID "+classId != null ?classId :"(null)"+"not found. Looking for default class ID");
					if(confs.has(JSNavigationDefaultClassId)){
						className = confs.getJSONObject(JSNavigationDefaultClassId).optString(kAndroidClassName);
						pullToRefresh = confs.getJSONObject(JSNavigationDefaultClassId).optBoolean(kJSPullToRefreshActive);
						infiniteScroll = confs.getJSONObject(JSNavigationDefaultClassId).optBoolean(kJSInfiniteScrollActive);
					}	
					else Log.w(getClass().getSimpleName(), "WARNING : No default key is present in conf file...");
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}

			//CREATE INTENT 
			Bundle bundleToAdd = new Bundle();
			bundleToAdd.putString(kPageName, pageNamed);
			bundleToAdd.putString(kResourcePath, this.ressourcePath);
			bundleToAdd.putBoolean(kPullToRefresh, pullToRefresh);
			bundleToAdd.putBoolean(kInfiniteScroll, infiniteScroll);

			if(className != null && className.length() > 0)
			{
				Class<?> myClass;
				try {
					myClass = Class.forName(className);
					//if the class inherits from an activity, we load it
					if(Activity.class.isAssignableFrom(myClass))
					{
						i=new Intent(mContext,myClass);	
						i.putExtra(kExtras, bundleToAdd);
					}
					else
					{
						if(mDebug) Log.e(getClass().getSimpleName(), "ERROR : impossible to show "+className+" for it does not inherit from Activity");
						i=new Intent(mContext,HTMLActivity.class);	
						i.putExtra(kExtras, bundleToAdd);
					}

				} catch (ClassNotFoundException e) {
					e.printStackTrace();
					if(mDebug) Log.e(getClass().getSimpleName(),"ERROR : classNotFound "+className+" for given ID : "+classId+" !");

					i=new Intent(mContext,HTMLActivity.class);
					i.putExtra(kExtras, bundleToAdd);
				}
			}
			else
			{
				if(mDebug) Log.e(getClass().getSimpleName(),"ERROR : classNotFound for given ID : "+classId+" !");
				i=new Intent(mContext,HTMLActivity.class);
				i.putExtra(kExtras, bundleToAdd);
			}
		}
		return i;
	}


	private void showAlertDialogWithJSON(JSONObject jsonObj)
	{
		if(jsonObj != null)
		{

			try {
				String title = jsonObj.optString(kJSAlertTitle);
				String message = jsonObj.optString(kJSAlertMessage);
				String receiverType = jsonObj.optString(kJSAlertCallbackReceiver);
				boolean isCancellable =jsonObj.optBoolean(kJSAlertIsCancelable,true);
				final int alertId = jsonObj.optInt(kJSAlertID);

				JSONArray buttons = jsonObj.has(kJSAlertButtons) ? jsonObj.getJSONArray(kJSAlertButtons) : new JSONArray();

				//AlertDialog.Builder alert = new AlertDialog.Builder(mContext);    
				AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
				
				alert.setTitle(title);
				alert.setMessage(message);

				AlertDialog mAlert = alert.create();

				//WebCallback
				if(receiverType.length() > 0)
				{
					if(receiverType.equals(JSAlertCallbackReceiverWeb))

					{
						final String callbackId = jsonObj.optString(kJSCallbackID);

						if(buttons.length() == 0)
						{
							mAlert.setButton(DialogInterface.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {	
								@Override
								public void onClick(DialogInterface dialog, int which) {
									if(callbackId.length() > 0)
									{
										JSONObject j = new JSONObject();
										try {
											j.put(kJSAlertID, alertId);
											j.put(kJSAlertButtonIndex,0);
											sendCallbackResponse(callbackId, j);
										} catch (JSONException e) {
											e.printStackTrace();
										}								
									}
								}
							});
						}
						else
						{
							int realSize = Math.min(buttons.length(), 3);
							for(int i = 1 ;i <= realSize ;i++)
							{
								mAlert.setButton(-i, buttons.getString(i-1), new DialogInterface.OnClickListener() {	
									@Override
									public void onClick(DialogInterface dialog, int which) {
										if(callbackId.length() > 0)
										{
											JSONObject j = new JSONObject();
											try {
												j.put(kJSAlertID, alertId);
												j.put(kJSAlertButtonIndex,-1-which);
												sendCallbackResponse(callbackId,j);
											} catch (JSONException e) {
												e.printStackTrace();
											}

										}
									}
								});
							}
						}
					}
					//Native Callback
					else if(receiverType.equals(JSAlertCallbackReceiverNative))
					{
						if(buttons.length() == 0)
						{
							mAlert.setButton(DialogInterface.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {	
								@Override
								public void onClick(DialogInterface dialog, int which) {
									alertDialogClickedButton(alertId, DialogInterface.BUTTON_POSITIVE);
								}
							});
						}
						else
						{

							int realSize = Math.min(buttons.length(), 3);
							for(int i = 1 ;i <= realSize ;i++)
							{
								mAlert.setButton(-i, buttons.getString(i-1), new DialogInterface.OnClickListener() {	
									@Override
									public void onClick(DialogInterface dialog, int which) {
										alertDialogClickedButton(alertId, which);
									}
								});
							}
						}
					}					
				}
				//NO CallBack
				else
				{
					if(buttons.length() == 0)
					{
						mAlert.setButton(DialogInterface.BUTTON_POSITIVE,"OK", new DialogInterface.OnClickListener() {	
							@Override
							public void onClick(DialogInterface dialog, int which) {
							}
						});
					}
					else
					{
						int realSize = Math.min(buttons.length(), 3);
						for(int i = 1 ;i <= realSize ;i++)
						{
							mAlert.setButton(-i, buttons.getString(i-1), new DialogInterface.OnClickListener() {	
								@Override
								public void onClick(DialogInterface dialog, int which) {
								}
							});
						}
					}
				}
				mAlert.setCancelable(isCancellable);
				mAlert.show();

			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

	private void showWebAlertWithJSON(JSONObject obj)
	{
		if(obj != null)
		{
			if(getActivity() != null)
			{
				String pageNamed = obj.optString(kJSWebAlertPageName);
				double fadeDuration = obj.optDouble(kJSWebAlertfadeDuration,0.3);

				HTMLPopUpWebview popUpWebview = getPopUpWebview(pageNamed);
				Bundle bundleToAdd = new Bundle();
				bundleToAdd.putString(kPageName, pageNamed);
				bundleToAdd.putString(kResourcePath, this.ressourcePath);
				popUpWebview.setArguments(bundleToAdd);

				android.support.v4.app.FragmentTransaction fTransition;
				fTransition = getActivity().getSupportFragmentManager().beginTransaction();

				if(fadeDuration > 0)
				{
					fTransition.setCustomAnimations(android.R.anim.fade_in,android.R.anim.fade_out,android.R.anim.fade_in,android.R.anim.fade_out);
				}
				else 
				{
					fTransition.setTransition(FragmentTransaction.TRANSIT_NONE);
				}

				if(HTMLActivity.class.isAssignableFrom(getActivity().getClass()))
				{
					//dismiss a popup if a one is shown before displaying the new popup.
					HTMLActivity activity = (HTMLActivity)getActivity();
					Fragment currentDisplayedFragment = activity.getSupportFragmentManager().findFragmentById(activity.getFragmentContainerId());
					if(HTMLPopUpWebview.class.isAssignableFrom(currentDisplayedFragment.getClass()))
					{
						HTMLPopUpWebview popUpToDismiss = (HTMLPopUpWebview) currentDisplayedFragment;
						popUpToDismiss.dismissWebAlert();
					}

					//show the newly created popup.
					if (activity.findViewById(activity.getFragmentContainerId()) != null) {
						fTransition.add(activity.getFragmentContainerId(),popUpWebview);
						fTransition.commit();
					}
					else if(mDebug)  Log.e(getClass().getSimpleName(), "ERROR : Fragment container not found");
				}
			}
			else if(mDebug) Log.e(getClass().getSimpleName(), "ERROR : Impossible to show a webAlert from a Fragment that has been detached !");

		}
	}

	protected void setWebViewSettings(Object jsInterface)
	{
		if(webView != null)
		{		

			webView.setScrollListener(this);
			webView.setScrollBarStyle(WebView.SCROLLBARS_INSIDE_OVERLAY);
			//allows JS to be called from native
			WebSettings webSettings = webView.getSettings();
			webSettings.setJavaScriptEnabled(true);

			//Enable and setup JS localStorage
			webSettings.setDomStorageEnabled(true); 
			webSettings.setDatabaseEnabled(true);
			webSettings.setDatabasePath(mContext.getFilesDir().getParentFile().getPath()+"/databases/");
			//for JS Ajax calls : disable cache
			//webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE); 
			//webSettings.setAppCacheEnabled(false); 
			//webView.clearCache(false);
			//for JS Ajax calls : enable cross domain calls
			int currentapiVersion = android.os.Build.VERSION.SDK_INT;
			if (currentapiVersion >= android.os.Build.VERSION_CODES.JELLY_BEAN){
				allowAjaxForNewAndroid();
			}

			//fix some focus problems on old devices like HTC Wildfire
			//keyboard was not properly showed on input touch.
			webView.requestFocus(View.FOCUS_DOWN);
			webView.setOnTouchListener(new View.OnTouchListener() {
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					switch (event.getAction()) {
					case MotionEvent.ACTION_DOWN:
					case MotionEvent.ACTION_UP:
						if (!v.hasFocus()) {
							v.requestFocus();
						}
						break;
					}
					return false;
				}
			});

			//add JS interface so that JS can call native functions.
			webView.addJavascriptInterface(jsInterface, "Android");
			webView.addJavascriptInterface(new LocalStorageJavaScriptInterface(mContext), "LocalStorage");

			ScaleWebViewClient wvc = new ScaleWebViewClient() {
				@Override
				public void onPageStarted(WebView view, String url,Bitmap favicon) {
					super.onPageStarted(view, url, favicon);
					webViewLoaded = false;
				}

				@Override
				public void onPageFinished(WebView view, String url) {
					webViewLoaded = true;
					executeWaitingCalls();
				}
			};

			if(HTMLPullToRefreshFragment.class.isAssignableFrom(getClass()))
			{
				wvc.setScaleListener((HTMLPullToRefreshFragment) this);
			}

			webView.setWebViewClient(wvc);
		}
		else
		{
			if(mDebug) Log.e(getClass().getSimpleName(),"ERROR : webView is null and cannot be set");
		}
	}

	private String getFileContentFromAssets(String filename)
	{
		try {
			AssetManager assetManager = mContext.getAssets();
			InputStream ims = assetManager.open(filename);
			BufferedReader bfr = new BufferedReader(new InputStreamReader(ims));

			int c;
			StringBuilder response= new StringBuilder();

			while ((c = bfr.read()) != -1) {
				response.append( (char)c ) ;  
			}
			String result = response.toString();
			return result;

		} catch (FileNotFoundException e1) {
			if(mDebug) Log.e(getClass().getSimpleName(), "ERROR : file not found : "+filename);

		} catch (IOException e) {
			e.printStackTrace();
		}
		return "";
	}


	@TargetApi(16)
	private void allowAjaxForNewAndroid() {
		try {
			webView.getSettings().setAllowUniversalAccessFromFileURLs(true);
		} 
		catch(NullPointerException e) {
		}
	}

	private void executeWaitingCalls()
	{
		if(waitingJavaScriptCallsList.size() > 0)
		{
			int size = waitingJavaScriptCallsList.size();
			for(int i = 0 ; i < size ; i++)
			{
				if(mDebug) Log.e(getClass().getSimpleName(), "execute "+waitingJavaScriptCallsList.get(i).optString(kJSName));
				executeScriptInWebView(waitingJavaScriptCallsList.get(i));
			}
			waitingJavaScriptCallsList.clear();
		}
	}

	public boolean isDebugLogsActivated() {
		return mDebug;
	}

	public void setDebugActivated(boolean mDebug) {
		this.mDebug = mDebug;
	}

	/**
	 * This class is used as a substitution of the local storage in Android webviews
	 * 
	 * @author Diane
	 */
	private class LocalStorageJavaScriptInterface {
		private Context mContext;
		private LocalStorage localStorageDBHelper;
		private SQLiteDatabase database;

		LocalStorageJavaScriptInterface(Context c) {
			mContext = c;
			localStorageDBHelper = LocalStorage.getInstance(mContext);
		}

		/**
		 * This method allows to get an item for the given key
		 * @param key : the key to look for in the local storage
		 * @return the item having the given key
		 */
		@JavascriptInterface
		public String getItem(String key)
		{
			String value = null;
			if(key != null)
			{
				database = localStorageDBHelper.getReadableDatabase();
				Cursor cursor = database.query(LocalStorage.LOCALSTORAGE_TABLE_NAME,
						null, 
						LocalStorage.LOCALSTORAGE_ID + " = ?", 
						new String [] {key},null, null, null);
				if(cursor.moveToFirst())
				{
					value = cursor.getString(1);
				}
				cursor.close();
				database.close();
			}
			return value;
		}

		/**
		 * set the value for the given key, or create the set of datas if the key does not exist already.
		 * @param key
		 * @param value
		 */
		@JavascriptInterface
		public void setItem(String key,String value)
		{
			if(key != null && value != null)
			{
				String oldValue = getItem(key);
				database = localStorageDBHelper.getWritableDatabase();
				ContentValues values = new ContentValues();
				values.put(LocalStorage.LOCALSTORAGE_ID, key);
				values.put(LocalStorage.LOCALSTORAGE_VALUE, value);
				if(oldValue != null)
				{
					database.update(LocalStorage.LOCALSTORAGE_TABLE_NAME, values, LocalStorage.LOCALSTORAGE_ID + " = " + key, null);
				}
				else
				{
					database.insert(LocalStorage.LOCALSTORAGE_TABLE_NAME, null, values);
				}
				database.close();
			}
		}

		/**
		 * removes the item corresponding to the given key
		 * @param key
		 */
		@JavascriptInterface
		public void removeItem(String key)
		{
			if(key != null)
			{
				database = localStorageDBHelper.getWritableDatabase();
				database.delete(LocalStorage.LOCALSTORAGE_TABLE_NAME, LocalStorage.LOCALSTORAGE_ID + " = " + key, null);
				database.close();
			}
		}

		/**
		 * clears all the local storage.
		 */
		@JavascriptInterface
		public void clear()
		{
			database = localStorageDBHelper.getWritableDatabase();
			database.delete(LocalStorage.LOCALSTORAGE_TABLE_NAME, null, null);
			database.close();
		}
	}
}
