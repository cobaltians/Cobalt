/**
 *
 * HTMLWebLayerFragment
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

import fr.cobaltians.cobalt.BuildConfig;
import fr.cobaltians.cobalt.Cobalt;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.FragmentTransaction;
import android.graphics.Color;
import android.util.Log;
import android.webkit.JavascriptInterface;
import fr.cobaltians.cobalt.activities.HTMLActivity;

/**
 * Special {@link HTMLFragment} presented over the current HTMLFragment as a Web layer.
 * @author Diane
 * @details This class should not be instantiated directly. {@link HTMLFragment} manages it directly with Web layer messages.
 */
public class HTMLWebLayerFragment extends HTMLFragment {	

    protected static final String TAG = HTMLWebLayerFragment.class.getSimpleName();

	private JSONObject mData = null;
	
	/******************************************************
	 * LIFECYCLE
	 *****************************************************/
	@Override
	public void onStart() {
		super.onStart();
		
		if(mWebView != null) {
			mWebView.setBackgroundColor(Color.TRANSPARENT);
		}
	}

	@Override
	public void onDestroy() {
		onDismiss();
		
		super.onDestroy();
	}
	
	/*************************************************************************************************************************************
	 * COBALT
	 ************************************************************************************************************************************/
	@Override
	@JavascriptInterface
	public boolean handleMessageSentByJavaScript(String message) {
		try {
			final JSONObject jsonObj = new JSONObject(message);
			String type = jsonObj.optString(Cobalt.kJSType);
			if (type.equals(Cobalt.JSTypeWebLayer)) {
				String action = jsonObj.getString(Cobalt.kJSAction);
				if (action.equals(Cobalt.JSActionWebLayerDismiss)) {
					mHandler.post(new Runnable() {
						@Override
						public void run() {
							dismissWebLayer(jsonObj);
						}
					});
					return true;
				}
			}
		} 
		catch (JSONException exception) {
			if (BuildConfig.DEBUG) Log.e(Cobalt.TAG, TAG + " - handleMessageSentByJavaScript: cannot handle message for JSON \n" + message);
			exception.printStackTrace();
		}
		
		return super.handleMessageSentByJavaScript(message);
	}

	@Override
	protected boolean onUnhandledCallback(String name, JSONObject data) {
		return false;		
	}

	@Override
	protected boolean onUnhandledEvent(String name, JSONObject data,
			String callback) {
		return false;
	}
	
	@Override
	protected void onUnhandledMessage(JSONObject message) { }
	
	@Override
	protected void onBackPressed(boolean allowedToBack) {
		if(allowedToBack) {
			dismissWebLayer(null);
		}
	}
	
	/********************************************************************************************
	 * DISMISS
	 *******************************************************************************************/
	protected void dismissWebLayer(JSONObject jsonObject) {
		if (getActivity() != null) {
			android.support.v4.app.FragmentTransaction fTransition;
			fTransition = getActivity().getSupportFragmentManager().beginTransaction();
			
			if(jsonObject != null) {
				mData = jsonObject.optJSONObject(Cobalt.kJSData);
				double fadeDuration = jsonObject.optDouble(Cobalt.kJSWebLayerFadeDuration, 0);
				
				if(fadeDuration > 0) {
					fTransition.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out, 
													android.R.anim.fade_in, android.R.anim.fade_out);
				}
				else {
					fTransition.setTransition(FragmentTransaction.TRANSIT_NONE);
				}
			}
			else {
				fTransition.setTransition(FragmentTransaction.TRANSIT_NONE);
			}
			
			fTransition.remove(this);
			fTransition.commit();
		}
		else if (BuildConfig.DEBUG) Log.e(Cobalt.TAG, TAG + " - dismissWebLayer: Web layer is not attached to an activity.");
	}

	private void onDismiss() {
		if (HTMLActivity.class.isAssignableFrom(getActivity().getClass())) {
			HTMLActivity activity = (HTMLActivity) getActivity();
			activity.onWebLayerDismiss(mPage, getDataForDismiss());
		}
	}
	
	public JSONObject getDataForDismiss() {
		return mData;
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
