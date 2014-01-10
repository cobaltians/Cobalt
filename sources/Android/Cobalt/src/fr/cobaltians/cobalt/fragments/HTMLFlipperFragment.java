package fr.cobaltians.cobalt.fragments;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;
import fr.cobaltians.cobalt.customviews.GestureWebView;
import fr.cobaltians.cobalt.customviews.IGestureListener;

/**
 * {@link HTMLFragment} having swipe feature if enabled.
 * @extends HTMLFragment
 * @implements IGestureListener
 * @author Sebastien
 */
public class HTMLFlipperFragment extends HTMLFragment implements IGestureListener {
	
	private final static String JSEventSwipeLeft = "swipeLeft";
	private final static String JSEventSwipeRight = "swipeRight";
	
	private boolean mSwipeEnabled = false;
	
	/********************************************************
	 * LIFECYCLE
	 *******************************************************/
	
	@Override
	public void onStart() {
		super.onStart();
		
		// WebView has been added, set up its listener
		((GestureWebView) mWebView).setGestureListener(this);
		
		setFeaturesWantedActive();
	}
	
	/********************************************************
	 * COBALT
	 *******************************************************/
	
	@Override
	protected void addWebview() {
		if(mWebView == null) {
			mWebView = new GestureWebView(mContext);
			setWebViewSettings(this);
		}
		
		super.addWebview();
	}

	@Override
	protected void onUnhandledMessage(JSONObject message) {
		
	}
	
	/********************************************************
	 * SWIPE
	 *******************************************************/
	public void enableSwipe() {
		mSwipeEnabled = true;
	}

	public void disableSwipe() {
		mSwipeEnabled = false;
	}
	
	public boolean isSwipeEnabled() {
		return mSwipeEnabled;
	}
	
	private void setFeaturesWantedActive() {
		if(	getArguments() != null 
			&& getArguments().getBoolean(kSwipe)) {
			enableSwipe();
		}
		else {
			disableSwipe();
		}
	}
	
	/*************************************************************************************
	 * GESTURE LISTENER
	 ************************************************************************************/
	@Override
	public void onSwipeGesture(int direction) {
		swipe(direction);
	}

	private void swipe(final int direction) {
		mHandler.post(new Runnable() {
			@Override
			public void run() {
				JSONObject jsonObj = new JSONObject();
				try {
					jsonObj.put(kJSType,JSTypeEvent);
					if (direction == GESTURE_SWIPE_LEFT) {
						jsonObj.put(kJSEvent, JSEventSwipeLeft);
						if (mDebug) Log.i(getClass().getSimpleName(), "swipe: next");
					}
					else if (direction == GESTURE_SWIPE_RIGHT) {
						jsonObj.put(kJSEvent, JSEventSwipeRight);
						if (mDebug) Log.i(getClass().getSimpleName(), "swipe: previous");
					}
					executeScriptInWebView(jsonObj);
				}
				catch (JSONException exception) {
					exception.printStackTrace();
				}
			}
		});
	}
}
