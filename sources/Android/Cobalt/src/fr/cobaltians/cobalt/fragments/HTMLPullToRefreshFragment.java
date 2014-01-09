package fr.cobaltians.cobalt.fragments;

import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
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
 * {@link HTMLFragment} that may have Pull To Refresh or/and InfiniteScroll features if those are activated.
 * @extends HTMLFragment
 * @implements IScrollListener
 * @author Diane
 */
public class HTMLPullToRefreshFragment extends HTMLFragment implements IScrollListener {

	/**
	 * the webview that may have the PullToRefresh and/or InfiniteScroll features.
	 */
	protected PullToRefreshOverScrollWebview mPullRefreshWebView;

	//PULL TO REFRESH
	private static String JSNamePullToRefreshRefresh = "pullToRefreshRefresh";
	private static String JSNamePullToRefreshDidRefresh = "pullToRefreshDidRefresh";
	private static String JSNamePullToRefreshCancelled = "pullToRefreshCancelled";

	//INFINITE SCROLL
	private static String JSNameInfiniteScrollRefresh = "infiniteScrollRefresh";
	private static String JSNameInfiniteScrollDidRefresh = "infiniteScrollDidRefresh";
	private static String JSNameInfiniteScrollCancelled = "infiniteScrollCancelled";
	private boolean infiniteScrollRefreshing = false;

	private boolean infiniteScrollActive = false;
	
	@Override
	public void onStart() {
		super.onStart();
		//webview has been added. -> set up its listener
		if(mPullRefreshWebView != null)
		{
			mPullRefreshWebView.setOnRefreshListener(new OnRefreshListener<OverScrollingWebView>() {
				@Override
				public void onRefresh(
						PullToRefreshBase<OverScrollingWebView> refreshView) {
					refreshWebView();
				}
			});
		}
		
		setFeaturesWantedActive();
	}

	@Override
	protected int getLayoutToInflate()
	{
		return R.layout.html_ptr_fragment;
	}

	/**
	 * @discussion You must set mPullRefreshWebView instead of webView in a HTMLPullToRefreshFragment. webView will be set automatically.
	 */
	@Override
	protected void setUpViews(View rootView) {
		webViewPlaceholder = (FrameLayout)rootView.findViewById(R.id.webViewPlaceholder);
	}

	@Override
	protected void addWebview()
	{
		if(mPullRefreshWebView == null)
		{
			//Create the webview since it has not been created before.
			mPullRefreshWebView = new PullToRefreshOverScrollWebview(mContext);
			if(webView == null)
			{
				//set webview as the refreshableView of mPullRefreshWebView.
				webView = mPullRefreshWebView.getRefreshableView();
			}
			setWebViewSettings(this);
		}

		if(webViewPlaceholder != null)
		{
			webViewPlaceholder.addView(mPullRefreshWebView);
		}
		else 
		{
			if(mDebug) Log.e(getClass().getSimpleName(), "ERROR : you must set up webViewPlaceholder in setUpViews !");
		}
	}

	@Override
	protected void removeWebviewFromPlaceholder()
	{
		if (webViewPlaceholder != null)
		{
			if(mPullRefreshWebView != null)
			{
				// Remove the WebView from the old placeholder
				webViewPlaceholder.removeView(mPullRefreshWebView);
			}
		}
		else 
		{
			if(mDebug) Log.e(getClass().getSimpleName(), "ERROR : you must set up webViewPlaceholder in setUpViews !");
		}
	}

	/**
	 * Customizes the pullToRefresh Loading View.
	 * @param pullLabel : text to be shown when user is pulling
	 * @param refreshingLabel : text to be shown while the pullToRefresh is refreshing
	 * @param releaseLabel : text to be shown when refreshing is done
	 * @param lastUpdatedLabel : text of the lastUpdateLabel
	 * @param loadingDrawable : the drawable to show
	 * @details To customize the animation of the loadingDrawable or the text color of the labels, it must be indicated in the layout.
	 * @example ptr:ptrAnimationStyle="flip|rotate"
	 */
	protected void setCustomTitlesAndImage(String pullLabel,String refreshingLabel,String releaseLabel,String lastUpdatedLabel,Drawable loadingDrawable,Typeface typeface)
	{
		LoadingLayoutProxy llp = ((LoadingLayoutProxy) mPullRefreshWebView.getLoadingLayoutProxy());
		if(lastUpdatedLabel != null)
			llp.setLastUpdatedLabel(lastUpdatedLabel);
		if(pullLabel != null)
			llp.setPullLabel(pullLabel);
		if(refreshingLabel != null)
			llp.setRefreshingLabel(refreshingLabel);
		if(releaseLabel != null)
			llp.setReleaseLabel(releaseLabel);
		if(loadingDrawable != null)
			llp.setLoadingDrawable(loadingDrawable);
		if(typeface != null)
			llp.setTextTypeface(typeface);
	}

	/**
	 * Customizes the pullToRefresh Last update Label
	 * @param lastUpdatedLabel: text of the lastUpdateLabel
	 */
	protected void setLastUpdateLabel(String lastUpdatedLabel)
	{
		LoadingLayoutProxy llp = ((LoadingLayoutProxy) mPullRefreshWebView.getLoadingLayoutProxy());
		if(lastUpdatedLabel != null)
			llp.setLastUpdatedLabel(lastUpdatedLabel);
	}

	/**
	 * enable the pullToRefresh feature
	 * the mPullRefreshWebView must have been set before 
	 */
	public void enablePullToRefresh()
	{
		if(this.mPullRefreshWebView != null)
			this.mPullRefreshWebView.setMode(Mode.PULL_FROM_START);
		else if(mDebug) Log.e(getClass().getSimpleName(), "ERROR : impossible to enable pullToRefresh feature since mPullRefreshWebView is null.");
	}

	/**
	 * disable the pullToRefresh feature
	 * the mPullRefreshWebView must have been set before 
	 */
	public void disablePullToRefresh()
	{
		if(this.mPullRefreshWebView != null)
			this.mPullRefreshWebView.setMode(Mode.DISABLED);
		else if(mDebug) Log.e(getClass().getSimpleName(), "ERROR : impossible to disable pullToRefresh feature since mPullRefreshWebView is null.");
	}

	/**
	 * this methods returns a boolean to know if the infiniteScroll feature is active
	 * @return true if infiniteScroll is active, false otherwise
	 */
	public boolean isPullToRefreshActive()
	{
		return !(this.mPullRefreshWebView.getMode() == Mode.DISABLED);
	}

	/**
	 * enable the infiniteScroll feature
	 */
	public void enableInfiniteScroll()
	{
		this.infiniteScrollActive = true;
	}

	/**
	 * disable the infiniteScroll feature
	 */
	public void disableInfiniteScroll()
	{
		this.infiniteScrollActive = false;
	}

	/**
	 * this methods returns a boolean to know if the infiniteScroll feature is active
	 * @return true if infiniteScroll is active, false otherwise
	 */
	public boolean isInfiniteScrollActive()
	{
		return this.infiniteScrollActive;
	}

	private void setFeaturesWantedActive()
	{
		if(this.mPullRefreshWebView != null)
		{
			if(this.getArguments() != null && this.getArguments().containsKey(kPullToRefresh))
			{
				boolean ptrActive = this.getArguments().getBoolean(kPullToRefresh);
				if(ptrActive)
				{
					enablePullToRefresh();
				}
				else disablePullToRefresh();
			}
			else disablePullToRefresh();

			if(this.getArguments() != null && this.getArguments().containsKey(kInfiniteScroll))
			{
				this.infiniteScrollActive = this.getArguments().getBoolean(kInfiniteScroll,false);
			}
		}
	}


	@JavascriptInterface
	public boolean handleMessageSentByJavaScript(String messageJS)
	{	
		JSONObject jsonObj;
		try 
		{
			jsonObj = new JSONObject(messageJS);
			if(jsonObj != null)
			{
				String type = jsonObj.optString(kJSType);

				//TYPE = EVENT
				if(type != null && type.length() >0 && type.equals(JSTypeCallBack))
				{
					String callbackID = jsonObj.optString(kJSCallback);
					if(callbackID != null && callbackID.length() >0 && callbackID.equals(JSNamePullToRefreshDidRefresh))
					{
						mHandler.post(new Runnable() {
							@Override
							public void run() {
								webViewDidRefresh();
							}
						});
						return true;
					}
					else if(callbackID != null && callbackID.length() >0 && callbackID.equals(JSNameInfiniteScrollDidRefresh))
					{
						mHandler.post(new Runnable() {
							@Override
							public void run() {
								infiniteScrollDidRefresh();
							}
						});
						return true;
					}
				}
			}
		} catch (JSONException e1) {
			if(mDebug) Log.e(getClass().getName(),"JSON EXCEPTION FOR JSON : "+messageJS);
			e1.printStackTrace();
		}
		return super.handleMessageSentByJavaScript(messageJS);
	}



	private void refreshWebView()
	{
		mHandler.post(new Runnable() {
			@Override
			public void run() {
				JSONObject obj = new JSONObject();
				try {
					obj.put(kJSType,JSTypeEvent);
					obj.put(kJSEvent, JSNamePullToRefreshRefresh);
					obj.put(kJSCallback, JSNamePullToRefreshDidRefresh);
					executeScriptInWebView(obj);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		});
	}



	private void webViewDidRefresh()
	{
		mPullRefreshWebView.onRefreshComplete();
		pullToRefreshHasRefreshed();
	}

	/**
	 * This method is called after the pullToRefresh request has been completed.
	 * This method may be overridden in subclasses.
	 */
	protected void pullToRefreshHasRefreshed()
	{

	}
	
	/**
	 * This method may be called when you want to interrupt the pullToRefresh from refreshing.
	 */
	protected void cancelPullToRefreshRefreshing()
	{
		if(isPullToRefreshActive() && mPullRefreshWebView.isRefreshing())
		{
			mPullRefreshWebView.onRefreshComplete();
			mHandler.post(new Runnable() {
				@Override
				public void run() {
					JSONObject obj = new JSONObject();
					try {
						obj.put(kJSType,JSTypeEvent);
						obj.put(kJSEvent, JSNamePullToRefreshCancelled);
						executeScriptInWebView(obj);
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			});
		}
	}

	/**
	 * This method may be called when you want to interrupt the infiniteScroll from refreshing.
	 */
	protected void cancelInfiniteScrollRefreshing()
	{
		if(isInfiniteScrollActive() && infiniteScrollRefreshing)
		{
			infiniteScrollRefreshing = false;
			mHandler.post(new Runnable() {
				@Override
				public void run() {
					JSONObject obj = new JSONObject();
					try {
						obj.put(kJSType,JSTypeEvent);
						obj.put(kJSEvent, JSNameInfiniteScrollCancelled);
						executeScriptInWebView(obj);
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			});
		}
	}
	
	@Override
	public void onOverScrolled(int scrollX, int scrollY, int oldX, int oldY) {
		//Log.i("overscroll", scrollY+" "+oldY);
		float density = mContext.getResources().getDisplayMetrics().density;
		//round density in case it is too precise (and big)
		if(density > 1)
			density = (float) (Math.floor(density*10)/10.0);
		//Log.i("", "density after "+density+" "+webView.getHeight()/density+" "+webView.getMeasuredHeight());
		int yPos = (int)((webView.getScrollY()+webView.getHeight())/density);
		//Log.i("overscroll", yPos+" "+webView.getContentHeight());
		if(yPos >= webView.getContentHeight())
		{
			infiniteScrollRefresh();
		}
	}

	private void infiniteScrollRefresh()
	{
		if(infiniteScrollActive && !infiniteScrollRefreshing)
		{
			infiniteScrollRefreshing = true;

			mHandler.post(new Runnable() {
				@Override
				public void run() {
					JSONObject obj = new JSONObject();
					try {
						obj.put(kJSType,JSTypeEvent);
						obj.put(kJSEvent, JSNameInfiniteScrollRefresh);
						obj.put(kJSCallback, JSNameInfiniteScrollDidRefresh);
						executeScriptInWebView(obj);

					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			});
		}
	}


	private void infiniteScrollDidRefresh()
	{
		infiniteScrollRefreshing = false;
		infiniteScrollHasRefreshed();
	}

	/**
	 * This method is called after the infiniteScroll request has been completed.
	 * This method may be overridden in subclasses.
	 */
	protected void infiniteScrollHasRefreshed()
	{

	}


	/**
	 * This method is fired by the {@link ScaleWebViewClient} to inform the webview that its scale has changed. (pullToRefresh need to know that to show the pullToRefresh Header appropriately.
	 * @param oldScale
	 * @param newScale
	 */
	public void notifyScaleChange(float oldScale,float newScale)
	{
		mPullRefreshWebView.setWebviewScale(newScale);
	}

	@Override
	protected void onUnhandledMessage(JSONObject message) {
		
	}
}
