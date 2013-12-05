package fr.haploid.hphybridcatalog.fragments;

import fr.haploid.androidnativebridge.fragments.HTMLPullToRefreshFragment;
import fr.haploid.hphybridcatalog.R;

public class PullToRefreshFragment extends HTMLPullToRefreshFragment {

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
}
