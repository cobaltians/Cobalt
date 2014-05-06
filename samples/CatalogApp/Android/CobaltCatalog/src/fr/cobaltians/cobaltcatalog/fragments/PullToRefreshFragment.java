package fr.cobaltians.cobaltcatalog.fragments;

import org.json.JSONObject;

import fr.cobaltians.cobaltcatalog.R;
import fr.cobaltians.cobalt.fragments.CobaltFragment;

public class PullToRefreshFragment extends CobaltFragment {

	protected int layoutToInflate()
	{
		return R.layout.ptr_rotating_fragment;
	}

	/*
	@Override
	protected void setUpViews(View rootView) {
		mPullRefreshWebView = (PullToRefreshOverScrollWebview) rootView.findViewById(R.id.pull_refresh_webview);
		webView = (OverScrollingWebView) mPullRefreshWebView.getRefreshableView();
	}
	*/
	
	@Override
	public void onStart() {
		super.onStart();
		
		if (getArguments() == null) {
			enablePullToRefresh();
			disableInfiniteScroll();
		}
	}
	
	@Override
	protected void onPullToRefreshRefreshed() { }
	@Override
	protected void onInfiniteScrollRefreshed() { }
	
	// unhandled JS messages
	@Override
	protected void onUnhandledMessage(JSONObject message) { }
	@Override
	protected boolean onUnhandledEvent(String name, JSONObject data, String callback) {
		return false;
	}
	@Override
	protected boolean onUnhandledCallback(String name, JSONObject data) {
		return false;
	}
}
