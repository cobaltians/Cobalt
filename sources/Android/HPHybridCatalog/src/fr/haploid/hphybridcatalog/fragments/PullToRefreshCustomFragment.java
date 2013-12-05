package fr.haploid.hphybridcatalog.fragments;

import fr.haploid.androidnativebridge.fragments.HTMLPullToRefreshFragment;
import fr.haploid.hphybridcatalog.R;

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
}
