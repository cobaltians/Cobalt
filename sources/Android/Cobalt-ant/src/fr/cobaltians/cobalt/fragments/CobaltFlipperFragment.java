/**
 *
 * CobaltFlipperFragment
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

package fr.cobaltians.cobalt.fragments;

import fr.cobaltians.cobalt.Cobalt;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;
import fr.cobaltians.cobalt.customviews.GestureWebView;
import fr.cobaltians.cobalt.customviews.IGestureListener;

/**
 * {@link CobaltFragment} having swipe feature if enabled.
 * @extends CobaltFragment
 * @implements IGestureListener
 * @author Sebastien
 */
public class CobaltFlipperFragment extends CobaltFragment implements IGestureListener {

    private final static String TAG = CobaltFlipperFragment.class.getSimpleName();

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
	protected void addWebView() {
		if(mWebView == null) {
			mWebView = new GestureWebView(mContext);
			setWebViewSettings(this);
		}
		
		super.addWebView();
	}
	
	@Override
	protected boolean onUnhandledCallback(String name, JSONObject data) {
		return false;
	}
	
	@Override
	protected boolean onUnhandledEvent(String name, JSONObject data, String callback) {
		return false;
	}
	
	@Override
	protected void onUnhandledMessage(JSONObject message) { }
	
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
			&& getArguments().getBoolean(Cobalt.kSwipe)) {
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
					jsonObj.put(Cobalt.kJSType, Cobalt.JSTypeEvent);
					if (direction == GESTURE_SWIPE_LEFT) {
						jsonObj.put(Cobalt.kJSEvent, JSEventSwipeLeft);
						if (Cobalt.DEBUG) Log.i(Cobalt.TAG, TAG + " - swipe: next");
					}
					else if (direction == GESTURE_SWIPE_RIGHT) {
						jsonObj.put(Cobalt.kJSEvent, JSEventSwipeRight);
						if (Cobalt.DEBUG) Log.i(Cobalt.TAG, TAG + " - swipe: previous");
					}
					sendMessage(jsonObj);
				}
				catch (JSONException exception) {
					exception.printStackTrace();
				}
			}
		});
	}

	@Override
	protected void onPullToRefreshRefreshed() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void onInfiniteScrollRefreshed() {
		// TODO Auto-generated method stub
		
	}
}
