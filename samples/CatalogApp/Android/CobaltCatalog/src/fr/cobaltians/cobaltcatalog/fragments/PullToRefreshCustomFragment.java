package fr.cobaltians.cobaltcatalog.fragments;

import org.json.JSONObject;

import fr.cobaltians.cobalt.fragments.HTMLPullToRefreshFragment;
import fr.cobaltians.cobaltcatalog.R;

public class PullToRefreshCustomFragment extends HTMLPullToRefreshFragment {
	
	@Override
	protected int getLayoutToInflate() {
		return R.layout.ptr_flip_fragment;
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
		
		setCustomTitlesAndImage("Relâchez pour actualiser", 
				"Chargement...", 
				"Relâchez pour actualiser", 
				null, 
				mContext.getResources().getDrawable(R.drawable.ic_launcher),
				null);
		
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
	protected void onUnhandledMessage(JSONObject message) {
		
	}
	@Override
	protected boolean onUnhandledEvent(String name, JSONObject data, String callback) {
		return false;
	}
	@Override
	protected boolean onUnhandledUi(String control, JSONObject data, String callback) {
		return false;
	}
	@Override
	protected boolean onUnhandledCallback(String name, JSONObject data) {
		return false;
	}
}
