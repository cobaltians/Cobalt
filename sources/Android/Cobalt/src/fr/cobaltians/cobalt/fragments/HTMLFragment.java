package fr.cobaltians.cobalt.fragments;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
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
import fr.cobaltians.cobalt.activities.HTMLActivity;
import fr.cobaltians.cobalt.customviews.OverScrollingWebView;
import fr.cobaltians.cobalt.database.LocalStorage;
import fr.cobaltians.cobalt.webViewClients.ScaleWebViewClient;
import fr.cobaltians.cobalt.R;

/**
 * {@link Fragment} allowing interactions between native and Web
 * 
 * @author Diane Moebs
 */
public abstract class HTMLFragment extends Fragment {
	
	// RESOURCES
	private final static String ASSETS_PATH = "file:///android_asset/";
	private final static String kResourcePath = "resourcePath";
		
	// CONFIGURATION FILE
	private final static String CONF_FILE = "cobalt.conf";
	private final static String kAndroidController = "androidController";
	public final static String kExtras = "extras";
	private final static String kPage = "page";
	protected final static String kPullToRefresh = "pullToRefresh";
	protected final static String kInfiniteScroll = "infiniteScroll";
	protected final static String kSwipe = "swipe";
	
	/*********************************************************************************
	 * JS MESSAGES
	 ********************************************************************************/
	
	// GENERAL
	protected final static String kJSAction = "action";
	protected final static String kJSCallback = "callback";
	protected final static String kJSData = "data";
	protected final static String kJSPage = "page";
	protected final static String kJSType = "type";
	protected final static String kJSValue = "value";
	
	// CALLBACKS
	protected final static String JSTypeCallBack = "callback";
	
	// COBALT IS READY
	protected final static String JSTypeCobaltIsReady = "cobaltIsReady";
	
	// EVENTS
	protected final static String JSTypeEvent = "event";
	protected final static String kJSEvent = "name"; // TODO: DISCUSS WITH GUILLAUME

	// BACK BUTTON
	private final static String JSEventOnBackButtonPressed = "onBackButtonPressed";
	private final static String JSCallbackOnBackButtonPressed = "onBackButtonPressed";
		
	// LOG
	protected final static String JSTypeLog = "log";
	
	// NAVIGATION
	protected final static String JSTypeNavigation = "navigation";
	protected final static String JSActionNavigationPush = "push";
	protected final static String JSActionNavigationPop ="pop";
	protected final static String JSActionNavigationModale = "modale";
	protected final static String JSActionNavigationDismiss = "dismiss";
	protected final static String kJSNavigationController = "controller";
	protected final static String JSNavigationControllerDefault = "default";
	
	// UI
	protected final static String JSTypeUI = "ui";
	protected final static String kJSUIControl = "control";
		
	// ALERT
	protected final static String JSControlAlert = "alert";
	protected final static String kJSAlertID = "id";
	protected final static String kJSAlertTitle = "title";
	protected final static String kJSAlertMessage = "message";
	protected final static String kJSAlertButtons = "buttons";
	protected final static String kJSAlertCancelable = "cancelable";
	protected final static String kJSAlertButtonIndex  = "index";
	
	// DATE PICKER
	protected static final String JSControlPicker = "picker";
	protected static final String JSPickerDate = "date";
	protected static final String kJSDate = "date";
	protected static final String kJSDay = "day";
	protected static final String kJSMonth = "month";
	protected static final String kJSYear = "year";
	
	// TOAST
	protected final static String JSControlToast = "toast";

	// WEB LAYER
	protected final static String JSTypeWebLayer = "webLayer";
	protected final static String JSActionWebLayerShow = "show";
	protected final static String JSActionWebLayerDismiss = "dismiss";
	protected final static String kJSWebLayerFadeDuration = "fadeDuration";
	protected final static String JSEventWebLayerOnDismiss = "onWebLayerDismissed";
	
	/*********************************************************
	 * MEMBERS
	 ********************************************************/
	protected boolean mDebug = false;
	
	protected String mRessourcePath;
	protected String mPage;
	
	protected OverScrollingWebView mWebView;
	// TODO: use ViewGroup instead of FrameLayout to allow using different layouts 
	protected FrameLayout mWebViewPlaceholder;
	protected boolean mWebViewContentHasBeenLoaded;
	
	protected Context mContext;
	protected Handler mHandler;

	private ArrayList<JSONObject> mWaitingJavaScriptCallsQueue;
	
	private boolean mPreloadOnCreateView;
	private boolean mWebViewLoaded;
	private boolean mCobaltIsReady;
	
	/**************************************************************************************************************************
	 * LIFECYCLE
	 *************************************************************************************************************************/
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setRetainInstance(true);
		
		mContext = (Context) getActivity().getApplicationContext();
		mHandler = new Handler();
		
		mWaitingJavaScriptCallsQueue = new ArrayList<JSONObject>();
		
		mPreloadOnCreateView = true;
		mWebViewLoaded = false;
		mWebViewContentHasBeenLoaded = false;
		mCobaltIsReady = false;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		
		View view = inflater.inflate(getLayoutToInflate(), container, false);
		setUpViews(view);
		setUpListeners();
		return view;
	}	

	/**
	 * Restores Web view state.
	 */
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		if (mWebView != null) {
			mWebView.restoreState(savedInstanceState);
		}
	}
	
	@Override
	public void onStart() {
		super.onStart();
		
		addWebView();
		preloadContent();
	}

	/**
	 * Saves the Web view state.
	 */
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		
		if(mWebView != null) {
			mWebView.saveState(outState);
		}
	}
	
	@Override
	public void onStop() {
		super.onStop();
		
		// Fragment will rotate or be destroyed, so we don't preload content defined in fragment's arguments again 
		mPreloadOnCreateView = false;
		
		removeWebViewFromPlaceholder();
	}
	
	/****************************************************************************************
	 * Helpers
	 ***************************************************************************************/
	
	/**
	 * This method should be overridden in subclasses.
	 * @return Layout id inflated by this fragment
	 */
	protected int getLayoutToInflate() {
		return R.layout.html_fragment;
	}

	/**
	 * Sets up the fragment's properties according to the inflated layout.
	 * This method should be overridden in subclasses. 
	 * @param rootView: parent view
	 */
	protected void setUpViews(View rootView) {
		// TODO: and if webViewPlaceholder id is null?
		mWebViewPlaceholder = ((FrameLayout) rootView.findViewById(R.id.webViewPlaceholder));
	}

	/**
	 * Sets up listeners for components inflated from the given layout and the parent view.
	 * This method should be overridden in subclasses.
	 */
	protected void setUpListeners() { }
	
	/**
	 * Called to add the Web view in the placeholder (and creates it if necessary).
	 * This method SHOULD NOT be overridden in subclasses.
	 */
	protected void addWebView() {
		if(mWebView == null) {
			mWebView = new OverScrollingWebView(mContext);
			setWebViewSettings(this);
		}
		
		if(mWebViewPlaceholder != null) {
			mWebViewPlaceholder.addView(mWebView);
		}
		else  {
			if(mDebug) Log.e(getClass().getSimpleName(), "addWebView: you must set up Web view placeholder in setUpViews!");
		}		
	}
	
	private void preloadContent() {
		// TODO: resourcePath & mPage setting. Do we keep this behavior?
		Bundle arguments = getArguments();
		
		mRessourcePath = arguments.getString(kResourcePath);
		mPage = arguments.getString(kPage);

		mRessourcePath = (mRessourcePath != null) ? mRessourcePath : "www/";
		mPage = (mPage != null) ? mPage : "index.html";
		
		if (mPreloadOnCreateView) {
			loadFileFromAssets(mRessourcePath, mPage);
		}
	}
	
	/**
	 * Called when fragment is about to rotate or be destroyed
	 * This method SHOULD NOT be overridden in subclasses.
	 */
	protected void removeWebViewFromPlaceholder() {
		if (mWebViewPlaceholder != null
			&& mWebView != null) {
			mWebViewPlaceholder.removeView(mWebView);
		}
		else if(mDebug) Log.e(getClass().getSimpleName(), "removeWebViewFromPlaceholder: you must set up Web view placeholder in setUpViews!");
	}
	
	@SuppressLint("SetJavaScriptEnabled")
	protected void setWebViewSettings(Object javascriptInterface) {
		if(mWebView != null) {		
			mWebView.setScrollListener(this);
			mWebView.setScrollBarStyle(WebView.SCROLLBARS_INSIDE_OVERLAY);
			
			// Enables JS
			WebSettings webSettings = mWebView.getSettings();
			webSettings.setJavaScriptEnabled(true);

			// Enables and setup JS local storage
			webSettings.setDomStorageEnabled(true); 
			webSettings.setDatabaseEnabled(true);
			//@deprecated since API 19. But calling this method have simply no effect for API 19+
			webSettings.setDatabasePath(mContext.getFilesDir().getParentFile().getPath()+"/databases/");
			
			// Enables cross-domain calls for Ajax
			if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
				allowAjax();
			}
			
			// Fix some focus issues on old devices like HTC Wildfire
			// keyboard was not properly showed on input touch.
			mWebView.requestFocus(View.FOCUS_DOWN);
			mWebView.setOnTouchListener(new View.OnTouchListener() {
				@Override
				public boolean onTouch(View view, MotionEvent event) {
					switch (event.getAction()) {
						case MotionEvent.ACTION_DOWN:
						case MotionEvent.ACTION_UP:
							if (! view.hasFocus()) {
								view.requestFocus();
							}
							break;
						default:
							break;
					}
					
					return false;
				}
			});

			// Add JavaScript interface so JavaScript can call native functions.
			mWebView.addJavascriptInterface(javascriptInterface, "Android");
			mWebView.addJavascriptInterface(new LocalStorageJavaScriptInterface(mContext), "LocalStorage");

			ScaleWebViewClient scaleWebViewClient = new ScaleWebViewClient() {
				@Override
				public void onPageStarted(WebView view, String url, Bitmap favicon) {
					super.onPageStarted(view, url, favicon);
					
					mWebViewLoaded = false;
				}

				@Override
				public void onPageFinished(WebView view, String url) {
					mWebViewLoaded = true;
					
					executeWaitingCalls();
				}
			};
			
			if(HTMLPullToRefreshFragment.class.isAssignableFrom(getClass())) {
				scaleWebViewClient.setScaleListener((HTMLPullToRefreshFragment) this);
			}

			mWebView.setWebViewClient(scaleWebViewClient);
		}
		else {
			if(mDebug) Log.e(getClass().getSimpleName(), "setWebViewSettings: Web view is null.");
		}
	}
	
	@TargetApi(android.os.Build.VERSION_CODES.JELLY_BEAN) // 16
	private void allowAjax() {
		try {
			// TODO: see how to restrict only to local files
			mWebView.getSettings().setAllowUniversalAccessFromFileURLs(true);
		} 
		catch(NullPointerException exception) {
			exception.printStackTrace();
		}
	}
	
	/**
	 * Load the given file in the Web view 
	 * @param path: path in assets folder where the file is located.
	 * @param file: file name to load.
	 * @warning All application HTML files should be found in the same subfolder in ressource path
	 */
	public void loadFileFromAssets(String path, String file) {
		if(mWebView != null) {
			mWebView.loadUrl(ASSETS_PATH + path + file);
			mWebViewContentHasBeenLoaded = true;
		}
	}
	
	/*****************************************
	 * LOGGING
	 ****************************************/
	
	public boolean isLoggingEnabled() {
		return mDebug;
	}

	public void enableLogging(boolean debug) {
		mDebug = debug;
	}
	
	/****************************************************************************************
	 * SCRIPT EXECUTION
	 ***************************************************************************************/
	// TODO: find a way to keep in the queue not sent messages
	/**
	 * Sends script to be executed by JavaScript in Web view
	 * @param jsonObj: JSONObject containing script.
	 */
	public void executeScriptInWebView(final JSONObject jsonObj) {
		if (jsonObj != null) {
			if(mCobaltIsReady) {
				mHandler.post(new Runnable() {
					@Override
					public void run() {
						// Line & paragraph separators are not JSON compliant but supported by JSONObject
						String message = jsonObj.toString().replaceAll("[\u2028\u2029]", "");
						String url = "javascript:cobalt.execute(" + message + ");";
						
						if(mWebView != null) {
							mWebView.loadUrl(url);		
						}
						else {
							if(mDebug) Log.e(getClass().getSimpleName(), "executeScriptInWebView: message cannot been sent to empty Web view");
						}						
					}
				});
			}
			else {
				mWaitingJavaScriptCallsQueue.add(jsonObj);
			}
		}
	}

	private void executeWaitingCalls() {
		int mWaitingJavaScriptCallsQueueLength = mWaitingJavaScriptCallsQueue.size();
		
		for (int i = 0 ; i < mWaitingJavaScriptCallsQueueLength ; i++) {
			if (mDebug) Log.i(getClass().getSimpleName(), "executeWaitingCalls: execute " + mWaitingJavaScriptCallsQueue.get(i).toString());
			executeScriptInWebView(mWaitingJavaScriptCallsQueue.get(i));
		}
		
		mWaitingJavaScriptCallsQueue.clear();
	}

	/****************************************************************************************
	 * MESSAGE SENDING
	 ***************************************************************************************/
	/**
	 * Calls the Web callback with an object containing response fields
	 * @param callbackId: the Web callback.
	 * @param data: the object containing response fields
	 */
	public void sendCallback(final String callbackId, final JSONObject data) {
		if(	callbackId != null 
			&& callbackId.length() > 0) {
			try {
				JSONObject jsonObj = new JSONObject();
				jsonObj.put(kJSType, JSTypeCallBack);
				jsonObj.put(kJSCallback, callbackId);
				jsonObj.put(kJSData, data);	
				executeScriptInWebView(jsonObj);
			} 
			catch (JSONException exception) {
				exception.printStackTrace();
			}
		}
	}
	
	public void sendEvent(final String event, final JSONObject data, final String callbackID) {
		if (event != null
			&& event.length() > 0) {
			try {
				JSONObject jsonObj = new JSONObject();
				jsonObj.put(kJSType, JSTypeEvent);
				jsonObj.put(kJSEvent, event);
				jsonObj.put(kJSData, data);
				jsonObj.put(kJSCallback, callbackID);
				executeScriptInWebView(jsonObj);
			}
			catch (JSONException exception) {
				exception.printStackTrace();
			}
		}
	}
	
	/****************************************************************************************
	 * MESSAGE HANDLING
	 ***************************************************************************************/
	
	protected abstract void onUnhandledMessage(JSONObject message);

	/*******************************************************************************************************************************
	 * LOCAL STORAGE
	 ******************************************************************************************************************************/
	/**
	 * Local storage substitution for Web views
	 * @author Diane
	 */
	private class LocalStorageJavaScriptInterface {
		
		private Context mContext;
		private LocalStorage mLocalStorage;
		private SQLiteDatabase mDatabase;

		LocalStorageJavaScriptInterface(Context context) {
			mContext = context;
			mLocalStorage = LocalStorage.getInstance(mContext);
		}

		/**
		 * Gets item for the given key
		 * @param key: key to look for
		 * @return item corresponding to the given key
		 */
		@JavascriptInterface
		public String getItem(String key) {
			String value = null;
			
			if (key != null) {
				mDatabase = mLocalStorage.getReadableDatabase();
				Cursor cursor = mDatabase.query(LocalStorage.LOCALSTORAGE_TABLE_NAME,
												null, 
												LocalStorage.LOCALSTORAGE_ID + " = ?",  new String [] {key}, 
												null, null, null);
				if (cursor.moveToFirst()) {
					value = cursor.getString(1);
				}
				cursor.close();
				mDatabase.close();
			}
			
			return value;
		}

		/**
		 * Sets value for the given key.
		 * @param key
		 * @param value
		 */
		@JavascriptInterface
		public void setItem(String key, String value) {
			if (key != null 
				&& value != null) {
				mDatabase = mLocalStorage.getWritableDatabase();
				
				ContentValues values = new ContentValues();
				values.put(LocalStorage.LOCALSTORAGE_ID, key);
				values.put(LocalStorage.LOCALSTORAGE_VALUE, value);
				
				if (getItem(key) != null) {
					mDatabase.update(	LocalStorage.LOCALSTORAGE_TABLE_NAME, 
										values, 
										LocalStorage.LOCALSTORAGE_ID + " = " + key, 
										null);
				}
				else {
					mDatabase.insert(	LocalStorage.LOCALSTORAGE_TABLE_NAME, null, 
										values);
				}
				mDatabase.close();
			}
		}

		/**
		 * Removes item corresponding to the given key
		 * @param key
		 */
		@JavascriptInterface
		public void removeItem(String key) {
			if(key != null) {
				mDatabase = mLocalStorage.getWritableDatabase();
				mDatabase.delete(	LocalStorage.LOCALSTORAGE_TABLE_NAME, 
									LocalStorage.LOCALSTORAGE_ID + " = " + key, 
									null);
				mDatabase.close();
			}
		}

		/**
		 * Clears local storage.
		 */
		@JavascriptInterface
		public void clear() {
			mDatabase = mLocalStorage.getWritableDatabase();
			mDatabase.delete(LocalStorage.LOCALSTORAGE_TABLE_NAME, null, null);
			mDatabase.close();
		}
	}
	
	// TODO: FROM HERE
	
	/**
	 * This method returns a new instance of the fragment that will be displayed as the {@link HTMLPopUpWebview} in the fragment container where the current fragment is shown.
	 * This method may be overridden in subclasses if the {@link HTMLPopUpWebview} must implement customized dialogs with native side such as in "public boolean handleMessageSentByJavaScript(String messageJS)".
	 * @param fileName : the filename that will be displayed in the {@link HTMLPopUpWebview}
	 * @return a new instance of the {@link HTMLPopUpWebview} that should be added in this {@link HTMLActivity}
	 */
	protected HTMLWebLayerFragment getWebLayerFragment(String fileName) {
		return new HTMLWebLayerFragment();
	}

	/**
	 * This method is called when a button is clicked on an alertDialog that was generated by the web.
	 * This method may be overridden in subclasses
	 * @param tag : the alertId of the alert
	 * @param buttonIndex : the clicked button
	 */
	public void alertDialogClickedButton(long tag, int buttonIndex) {
		
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
					if(type.equals(JSTypeEvent))
					{
						String name = jsonObj.getString(kJSEvent);
						JSONObject data =jsonObj.getJSONObject(kJSData);
						String callback = jsonObj.optString(kJSCallback);
						
						return handleEvent(name, data, callback);			
					}
					
					//LOG
					else if(type.equals(JSTypeLog))
					{
						String message = jsonObj.optString(kJSValue);
						if (mDebug) Log.d("JS Log", message);
						return true;
					}
					
					//NAVIGATE
					else if(type.equals(JSTypeNavigation))
					{
						if(jsonObj.has(kJSAction))
						{
							String navType = jsonObj.optString(kJSAction);
							//PUSH
							if(navType != null && navType.length() >0 && navType.equals(JSActionNavigationPush))
							{
								String params = jsonObj.getString(kJSData);
								JSONObject jsonParams = new JSONObject(params);
								
								String pageNamed = jsonParams.getString(kJSPage);	
								String activityId = jsonParams.optString(kJSNavigationController);

								pushWebView(activityId,pageNamed);
								return true;
							}
							//POP
							else if(navType != null && navType.length() >0 && navType.equals(JSActionNavigationPop))
							{
								popWebViewActivity();
								return true;
							}
							//MODALE
							else if(navType != null && navType.length() >0 && navType.equals(JSActionNavigationModale))
							{
								String params = jsonObj.optString(kJSData);
								JSONObject jsonParams = new JSONObject(params);
								
								String pageNamed = jsonParams.getString(kJSPage);	
								String activityId = jsonParams.optString(kJSNavigationController);
								String callBackId = jsonParams.optString(kJSCallback);
								
								presentWebView(activityId, pageNamed, callBackId);
								return true;
							}
							//DISMISS
							else if(navType != null && navType.length() >0 && navType.equals(JSActionNavigationDismiss))
							{
								String params = jsonObj.optString(kJSData);
								JSONObject jsonParams = new JSONObject(params);

								String className = jsonParams.optString(kJSNavigationController);
								String pageNamed = jsonParams.optString(kJSPage);
								
								dismissWebViewWithActivity(className, pageNamed);
								return true;
							}
							else 
							{
								onUnhandledMessage(jsonObj);
							}
						}
					}
					
					//WEB LAYER
					else if(type.equals(JSTypeWebLayer))
					{
						String name = jsonObj.getString(kJSAction);
						if(name != null && name.length() > 0)
						{
							if (name.equals(JSActionWebLayerShow))
							{
								String params = jsonObj.getString(kJSData);
								final JSONObject jsonParams = new JSONObject(params);
								
								mHandler.post(new Runnable() {

									@Override
									public void run() {
										showWebAlertWithJSON(jsonParams);
									}
								});
								return true;
							}
						}
						else {
							onUnhandledMessage(jsonObj);
						}
					}

					//CALLBACK
					else if(type.equals(JSTypeCallBack))
					{
						String callbackID = jsonObj.getString(kJSCallback);
						JSONObject data =jsonObj.getJSONObject(kJSData);
						
						return handleCallback(callbackID, data);		
					}
					
					// UI
			    	else if (type.equals(JSTypeUI)) 
			    	{
				    	final String control = jsonObj.getString(kJSUIControl);
						JSONObject data =jsonObj.getJSONObject(kJSData);
						String callback = jsonObj.optString(kJSCallback);

						return handleUi(control, data, callback);
			    	}
					
					else if(type.equals(JSTypeCobaltIsReady))
					{
						onReady();
						return true;
					}
					
					else 
					{
						handleUnknowType(jsonObj);
					}
				}

			}
		} catch (JSONException e1) {
			if(mDebug) Log.e(getClass().getSimpleName(),"ERROR : CANNOT HANDLE MESSAGE WITH JSON EXCEPTION FOR JSON : #"+messageJS+"#");
			e1.printStackTrace();
		}
		return false;
	}

	protected void onReady() {
		if(mDebug) Log.i(getClass().getSimpleName(), "cobalt is ready. waiting calls : "+mWaitingJavaScriptCallsQueue.size());

		mCobaltIsReady = true;
		executeWaitingCalls();
	}
	
	protected boolean handleEvent(String name, JSONObject data, String callback) {
		return true;
	}
	
	protected boolean handleUi(String control, JSONObject data, String callback) {
		try {
			if (control.equals(JSControlPicker)) 
			{
				JSONObject params = data.optJSONObject(kJSData);
				final String typeParams = params.optString(kJSType);
				if (typeParams.equals(JSPickerDate)) 
				{
					JSONObject date = params.optJSONObject(kJSDate);

					Calendar cal = Calendar.getInstance();
					int year = cal.get(Calendar.YEAR);
					int month = cal.get(Calendar.MONTH);
					int day = cal.get(Calendar.DAY_OF_MONTH);

					if (date != null) {
						if (date.has(kJSYear)
								&& date.has(kJSMonth)
								&& date.has(kJSDay)) {
							year = date.getInt(kJSYear);
							month = date.getInt(kJSMonth);
							month--;
							day = date.getInt(kJSDay);
						}
					}
					showDatePickerDialog(year, month, day, callback);	
					return true;
				}
				else 
				{
					onUnhandledMessage(data);
				}
			}
			else if(control.equals(JSControlAlert))
			{
				showAlertDialogWithJSON(data, callback);
				return true;
			}
			else if(control.equals(JSControlToast))
			{
				String message = data.optString(kJSAlertMessage);
				
				Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
				return true;
			}
			else {
				onUnhandledMessage(data);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}	
		return false;
	}
	
	protected boolean handleCallback(String name, JSONObject data) {
		try {
			if(name.equals(JSCallbackOnBackButtonPressed)){
				boolean allowToGoBack;
				allowToGoBack = data.getBoolean(kJSData);

				handleBackButtonPressed(allowToGoBack);
				return true;
			}
			else {
				onUnhandledMessage(data);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return false;
	}
	protected void handleUnknowType(JSONObject jsonObj) {
		Log.e(getClass().getSimpleName(),"ERROR : CANNOT HANDLE MESSAGE WITH THIS TYPE IN JSON : #"+jsonObj+"#");
	}

	/**
	 * This method is called when the backButton is pressed. It asks the webView whether the default action of the backbutton should be fired.
	 * This method should NOT be overridden in subclasses.
	 */
	public void askWebViewForBackPermission() {
		sendEvent(JSEventOnBackButtonPressed, null, JSCallbackOnBackButtonPressed);
	}

	/**
	 * This method is called from the corresponding {@link HTMLPopUpWebview} when the popup has been dismissed.
	 * This method may be overridden in subclasses.
	 */
	public void onWebLayerDismiss(final String page, final JSONObject data) {
		mHandler.post(new Runnable() {
			@Override
			public void run() {
				try {
					data.put(kJSValue, page);
				} catch (JSONException e) {
					e.printStackTrace();
				}
				sendEvent(JSEventWebLayerOnDismiss, data, null);
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
				objectToSend.put(kJSPage, mPage);
				objectToSend.put(kJSNavigationController, getActivity() != null ? getActivity().getClass().getName() : "ERROR : activity's className couldn't be found.");		
				sendCallback(callBackID, objectToSend);
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
				bundleToAdd.putString(kPage, pageNamed);
				bundleToAdd.putString(kResourcePath, mRessourcePath);

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
		String confToParse = getFileContentFromAssets(mRessourcePath+CONF_FILE);
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
			if(mDebug) Log.e(getClass().getSimpleName(),"ERROR : check your cobalt.conf... File is missing or not at this.ressourcePath in assets ?");
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
					className = confs.getJSONObject(classId).optString(kAndroidController);
					pullToRefresh = confs.getJSONObject(classId).optBoolean(kPullToRefresh);
					infiniteScroll = confs.getJSONObject(classId).optBoolean(kInfiniteScroll);
				}

				if(className == null || className.length() == 0)
				{
					Log.w(getClass().getSimpleName(), "WARNING : className for ID "+classId != null ?classId :"(null)"+"not found. Looking for default class ID");
					if(confs.has(JSNavigationControllerDefault)){
						className = confs.getJSONObject(JSNavigationControllerDefault).optString(kAndroidController);
						pullToRefresh = confs.getJSONObject(JSNavigationControllerDefault).optBoolean(kPullToRefresh);
						infiniteScroll = confs.getJSONObject(JSNavigationControllerDefault).optBoolean(kInfiniteScroll);
					}	
					else Log.w(getClass().getSimpleName(), "WARNING : No default key is present in conf file...");
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}

			//CREATE INTENT 
			Bundle bundleToAdd = new Bundle();
			bundleToAdd.putString(kPage, pageNamed);
			bundleToAdd.putString(kResourcePath, mRessourcePath);
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


	private void showAlertDialogWithJSON(JSONObject jsonParams, final String callbackId)
	{		
		if(jsonParams != null)
		{

			try {
				
				String title = jsonParams.optString(kJSAlertTitle);
				String message = jsonParams.optString(kJSAlertMessage);
				boolean isCancellable =jsonParams.optBoolean(kJSAlertCancelable,true);
				final int alertId = jsonParams.getInt(kJSAlertID);

				JSONArray buttons = jsonParams.has(kJSAlertButtons) ? jsonParams.getJSONArray(kJSAlertButtons) : new JSONArray();

				AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
				
				alert.setTitle(title);
				alert.setMessage(message);

				AlertDialog mAlert = alert.create();

				// Callback
				if (callbackId.length() != 0) {
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
										sendCallback(callbackId, j);
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
											sendCallback(callbackId,j);
										} catch (JSONException e) {
											e.printStackTrace();
										}

									}
								}
							});
						}
					}
				}
				// NO Callback
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
				try 
				{
					String pageNamed = obj.getString(kJSPage);
					double fadeDuration = obj.optDouble(kJSWebLayerFadeDuration,0.3);

					HTMLWebLayerFragment webLayerFragment = getWebLayerFragment(pageNamed);
					Bundle bundleToAdd = new Bundle();
					bundleToAdd.putString(kPage, pageNamed);
					bundleToAdd.putString(kResourcePath, mRessourcePath);
					webLayerFragment.setArguments(bundleToAdd);

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
						if(HTMLWebLayerFragment.class.isAssignableFrom(currentDisplayedFragment.getClass()))
						{
							HTMLWebLayerFragment webLayerToDismiss = (HTMLWebLayerFragment) currentDisplayedFragment;
							webLayerToDismiss.dismissWebLayer(null);
						}

						//show the newly created popup.
						if (activity.findViewById(activity.getFragmentContainerId()) != null) {
							fTransition.add(activity.getFragmentContainerId(),webLayerFragment);
							fTransition.commit();
						}
						else if(mDebug)  Log.e(getClass().getSimpleName(), "ERROR : Fragment container not found");
					}
				} 
				catch (JSONException e) 
				{
					e.printStackTrace();
				}
			}
			else if(mDebug) Log.e(getClass().getSimpleName(), "ERROR : Impossible to show a webAlert from a Fragment that has been detached !");

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
	
	/*************************************************************************************
     * DATE PICKER
     ************************************************************************************/
    protected void showDatePickerDialog(int year, int month, int day, String callbackID) {
    	Bundle args = new Bundle();
    	args.putInt(HTMLDatePickerFragment.ARG_YEAR, year);
    	args.putInt(HTMLDatePickerFragment.ARG_MONTH, month);
    	args.putInt(HTMLDatePickerFragment.ARG_DAY, day);
    	args.putString(HTMLDatePickerFragment.ARG_CALLBACK_ID, callbackID);
    	
    	HTMLDatePickerFragment newFragment = new HTMLDatePickerFragment();
        newFragment.setArguments(args);
        newFragment.setListener(this);
        
        newFragment.show(getActivity().getSupportFragmentManager(), "datePicker");
    }
    
    protected void sendDate(int year, int month, int day, String callbackID) {
    	try {
    		JSONObject jsonDate = new JSONObject();
    		jsonDate.put(kJSYear, year);
    		month++;
    		jsonDate.put(kJSMonth, month);
    		jsonDate.put(kJSDay, day);
    		
			JSONObject jsonResponse = new JSONObject();
			jsonResponse.put(kJSType, JSTypeCallBack);
			jsonResponse.put(kJSCallback, callbackID);
			jsonResponse.put(kJSData, jsonDate);
			Log.d(getClass().getName(), "sendDate + : " + jsonResponse);
			executeScriptInWebView(jsonResponse);
		} catch (JSONException e) {
			e.printStackTrace();
		}
    }
}
