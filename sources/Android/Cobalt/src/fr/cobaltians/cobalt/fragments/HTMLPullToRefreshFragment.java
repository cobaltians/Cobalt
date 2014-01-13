package fr.cobaltians.cobalt.fragments;

import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.widget.FrameLayout;

import com.handmark.pulltorefresh.library.LoadingLayoutProxy;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;

import fr.cobaltians.cobalt.customviews.IScrollListener;
import fr.cobaltians.cobalt.customviews.OverScrollingWebView;
import fr.cobaltians.cobalt.customviews.PullToRefreshOverScrollWebview;
import fr.cobaltians.cobalt.webViewClients.ScaleWebViewClient;
import fr.cobaltians.cobalt.R;

/**
 * {@link HTMLFragment} may having pull-to-refresh or/and infinite scroll features if activated.
 * @extends HTMLFragment
 * @implements IScrollListener
 * @author Diane
 */
public abstract class HTMLPullToRefreshFragment extends HTMLFragment implements IScrollListener {
	
	/*************************************************************************************
	 * JS MESSAGES
	 ************************************************************************************/
	
	// PULL TO REFRESH
	private static String JSEventPullToRefresh = "pullToRefresh";
	private static String JSCallbackPullToRefreshDidRefresh = "pullToRefreshDidRefresh";

	// INFINITE SCROLL
	private static String JSEventInfiniteScroll= "infiniteScroll";
	private static String JSCallbackInfiniteScrollDidRefresh = "infiniteScrollDidRefresh";
	
	/**********************************************************************
	 * MEMBERS
	 *********************************************************************/
	
	// Web view may having pull-to-refresh and/or infinite scroll features.
	protected PullToRefreshOverScrollWebview mPullToRefreshWebView;

	private boolean mInfiniteScrollRefreshing = false;
	private boolean mInfiniteScrollEnabled = false;
	
	/*********************************************************************************************
	 * LIFECYCLE
	 ********************************************************************************************/
	
	@Override
	public void onStart() {
		super.onStart();
		
		// Web view has been added, set up its listener
		mPullToRefreshWebView.setOnRefreshListener(new OnRefreshListener<OverScrollingWebView>() {
			@Override
			public void onRefresh(PullToRefreshBase<OverScrollingWebView> refreshView) {
				refreshWebView();
			}
		});
		
		setFeaturesWantedActive();
	}

	/*************************************************************************************
	 * COBALT
	 ************************************************************************************/
	
	@Override
	protected void addWebView() {
		if (mPullToRefreshWebView == null) {
			mPullToRefreshWebView = new PullToRefreshOverScrollWebview(mContext);
			if (mWebView == null) {
				// Set Web view as the refreshable view of pull-to-refresh Web view.
				mWebView = mPullToRefreshWebView.getRefreshableView();
			}
			setWebViewSettings(this);
		}

		if (mWebViewPlaceholder != null) {
			mWebViewPlaceholder.addView(mPullToRefreshWebView);
		}
		else {
			if(mDebug) Log.e(getClass().getSimpleName(), "You must set up webViewPlaceholder in setUpViews!");
		}
	}
	
	@Override
	protected void removeWebViewFromPlaceholder() {
		if (mWebViewPlaceholder != null) {
			if (mPullToRefreshWebView != null) {
				mWebViewPlaceholder.removeView(mPullToRefreshWebView);
			}
		}
		else  {
			if (mDebug) Log.e(getClass().getSimpleName(), "You must set up webViewPlaceholder in setUpViews!");
		}
	}
	
	@Override
	protected int getLayoutToInflate() {
		return R.layout.html_ptr_fragment;
	}

	@Override
	protected void setUpViews(View rootView) {
		mWebViewPlaceholder = (FrameLayout) rootView.findViewById(R.id.webViewPlaceholder);
	}

	@JavascriptInterface
	public boolean handleMessageSentByJavaScript(String message) {	
		try {
			JSONObject jsonObj = new JSONObject(message);
			String type = jsonObj.getString(kJSType);
			// TYPE = CALLBACK
			if (type.equals(JSTypeCallBack)) {
				String callbackID = jsonObj.getString(kJSCallback);
				if (callbackID.equals(JSCallbackPullToRefreshDidRefresh)) {
					mHandler.post(new Runnable() {
						@Override
						public void run() {
							onPullToRefreshDidRefresh();
						}
					});
					return true;
				}
				else if (callbackID.equals(JSCallbackInfiniteScrollDidRefresh)) {
					mHandler.post(new Runnable() {
						@Override
						public void run() {
							onInfiniteScrollDidRefresh();
						}
					});
					return true;
				}
			}
		} 
		catch (JSONException exception) {
			if (mDebug) Log.e(getClass().getName(), "handleMessageSentByJavaScript: JSON exception for \n" + message);
			exception.printStackTrace();
		}
		
		return super.handleMessageSentByJavaScript(message);
	}
	
	@Override
	protected void onUnhandledMessage(JSONObject message) {
		
	}
	
	/******************************************************************************************************************************
	 * PULL TO REFRESH
	 *****************************************************************************************************************************/
	
	/**
	 * Enables pull-to-refresh feature
	 * mPullRefreshWebView must be set
	 */
	public void enablePullToRefresh() {
		if (mPullToRefreshWebView != null) {
			mPullToRefreshWebView.setMode(Mode.PULL_FROM_START);
		}
		else if(mDebug) Log.e(getClass().getSimpleName(), "Unable to enable pull-to-refresh feature. mPullToRefreshWebView must be set.");
	}

	/**
	 * Disables pull-to-refresh feature
	 * mPullRefreshWebView must be set 
	 */
	public void disablePullToRefresh() {
		if(mPullToRefreshWebView != null) {
			mPullToRefreshWebView.setMode(Mode.DISABLED);
		}
		else if(mDebug) Log.e(getClass().getSimpleName(), "Unable to disable pull-to-refresh feature. mPullToRefreshWebView must be set.");
	}

	/**
	 * Returns a boolean to know if pull-to-refresh feature is enabled
	 * @return 	true if pull-to-refresh is enabled, 
	 * 			false otherwise
	 */
	public boolean isPullToRefreshEnabled() {
		return ! (mPullToRefreshWebView.getMode() == Mode.DISABLED);
	}
	
	/**
	 * Customizes pull-to-refresh loading view.
	 * @param pullLabel: text shown when user pulling
	 * @param refreshingLabel: text shown while refreshing
	 * @param releaseLabel: text shown when refreshed
	 * @param lastUpdatedLabel: text shown for the last update
	 * @param loadingDrawable: drawable shown when user pulling
	 * @details loadingDrawable animation or labels text color customization must be done in the layout.
	 * @example ptr:ptrAnimationStyle="flip|rotate"
	 */
	protected void setCustomTitlesAndImage(	String pullLabel, String refreshingLabel, String releaseLabel, String lastUpdatedLabel, 
											Drawable loadingDrawable, Typeface typeface) {
		LoadingLayoutProxy loadingLayoutProxy = ((LoadingLayoutProxy) mPullToRefreshWebView.getLoadingLayoutProxy());
		if (lastUpdatedLabel != null) {
			loadingLayoutProxy.setLastUpdatedLabel(lastUpdatedLabel);
		}
		if (pullLabel != null) {
			loadingLayoutProxy.setPullLabel(pullLabel);
		}
		if (refreshingLabel != null) {
			loadingLayoutProxy.setRefreshingLabel(refreshingLabel);
		}
		if (releaseLabel != null) {
			loadingLayoutProxy.setReleaseLabel(releaseLabel);
		}
		if (loadingDrawable != null) {
			loadingLayoutProxy.setLoadingDrawable(loadingDrawable);
		}
		if (typeface != null) {
			loadingLayoutProxy.setTextTypeface(typeface);
		}
	}
	
	/**
	 * Customizes pull-to-refresh last updated label
	 * @param text: text of last updated label
	 */
	protected void setLastUpdatedLabel(String text) {
		LoadingLayoutProxy loadingLayoutProxy = (LoadingLayoutProxy) mPullToRefreshWebView.getLoadingLayoutProxy();
		if (text != null) {
			loadingLayoutProxy.setLastUpdatedLabel(text);
		}
	}
	
	private void refreshWebView() {
		mHandler.post(new Runnable() {
			@Override
			public void run() {
				try {
					JSONObject jsonObj = new JSONObject();
					jsonObj.put(kJSType, JSTypeEvent);
					jsonObj.put(kJSEvent, JSEventPullToRefresh);
					jsonObj.put(kJSCallback, JSCallbackPullToRefreshDidRefresh);
					executeScriptInWebView(jsonObj);
				} 
				catch (JSONException exception) {
					exception.printStackTrace();
				}
			}
		});
	}
	
	private void onPullToRefreshDidRefresh() {
		mPullToRefreshWebView.onRefreshComplete();
		onPullToRefreshRefreshed();
	}

	/**
	 * This method may be overridden in subclasses.
	 */
	protected abstract void onPullToRefreshRefreshed();
	
	/************************************************************************************
	 * INFINITE SCROLL
	 ***********************************************************************************/
	
	@Override
	public void onOverScrolled(int scrollX, int scrollY,int oldscrollX, int oldscrollY) {
		float density = mContext.getResources().getDisplayMetrics().density;
		// Round density in case it is too precise (and big)
		if (density > 1) {
			density = (float) (Math.floor(density * 10) / 10.0);
		}
		
		int yPosition = (int) ((mWebView.getScrollY() + mWebView.getHeight()) / density);
		if (yPosition >= mWebView.getContentHeight()) {
			infiniteScrollRefresh();
		}
	}
	
	/**
	 * Enables infinite scroll feature
	 */
	public void enableInfiniteScroll() {
		mInfiniteScrollEnabled = true;
	}

	/**
	 * Disables infinite scroll feature
	 */
	public void disableInfiniteScroll() {
		mInfiniteScrollEnabled = false;
	}

	/**
	 * Returns a boolean to know if the infinite scroll feature is enabled
	 * @return 	true if infinite scroll is enabled, 
	 * 			false otherwise
	 */
	public boolean isInfiniteScrollEnabled() {
		return mInfiniteScrollEnabled;
	}

	private void infiniteScrollRefresh() {
		if (mInfiniteScrollEnabled 
			&& ! mInfiniteScrollRefreshing) {
			mHandler.post(new Runnable() {
				@Override
				public void run() {
					try {
						JSONObject jsonObj = new JSONObject();
						jsonObj.put(kJSType,JSTypeEvent);
						jsonObj.put(kJSEvent, JSEventInfiniteScroll);
						jsonObj.put(kJSCallback, JSCallbackInfiniteScrollDidRefresh);
						executeScriptInWebView(jsonObj);
						mInfiniteScrollRefreshing = true;
					} 
					catch (JSONException exception) {
						exception.printStackTrace();
					}
				}
			});
		}
	}
	
	private void onInfiniteScrollDidRefresh() {
		mInfiniteScrollRefreshing = false;
		onInfiniteScrollRefreshed();
	}

	/**
	 * This method may be overridden in subclasses.
	 */
	protected abstract void onInfiniteScrollRefreshed();
	
	/*****************************************************************
	 * HELPERS
	 ****************************************************************/
	
	private void setFeaturesWantedActive() {
		Bundle args = getArguments();
		
		if (args != null) {
			if(args.getBoolean(kPullToRefresh)) {
				enablePullToRefresh();
			}
			else {
				disablePullToRefresh();
			}
			
			mInfiniteScrollEnabled = args.getBoolean(kInfiniteScroll);
		}
		else {
			disablePullToRefresh();
			mInfiniteScrollEnabled = false;
		}
	}

	/**
	 * Fired by {@link ScaleWebViewClient} to inform Web view its scale changed (pull-to-refresh need to know that to show its header appropriately).
	 */
	public void notifyScaleChange(float oldScale, float newScale) {
		mPullToRefreshWebView.setWebviewScale(newScale);
	}
}
