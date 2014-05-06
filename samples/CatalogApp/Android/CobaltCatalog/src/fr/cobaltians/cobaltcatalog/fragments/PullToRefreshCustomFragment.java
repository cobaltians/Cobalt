package fr.cobaltians.cobaltcatalog.fragments;

import fr.cobaltians.cobaltcatalog.R;

import fr.cobaltians.cobalt.fragments.CobaltFragment;

import org.json.JSONObject;

public class PullToRefreshCustomFragment extends CobaltFragment {
	
	@Override
	public void onStart() {
		super.onStart();
		
		setCustomTitlesAndImage("Pull to refresh...",
                                "Loading...",
                                "Release to refresh...",
                                null,
                                mContext.getResources().getDrawable(R.drawable.ic_launcher),
                                null);
	}

    @Override
    protected int getLayoutToInflate() {
        return R.layout.ptr_flip_fragment;
    }

    @Override
    protected boolean onUnhandledEvent(String name, JSONObject data, String callback) {
        return false;
    }

    @Override
    protected boolean onUnhandledCallback(String name, JSONObject data) {
        return false;
    }

    @Override
    protected void onUnhandledMessage(JSONObject message) {

    }

	@Override
	protected void onPullToRefreshRefreshed() {

    }

	@Override
	protected void onInfiniteScrollRefreshed() {

    }
}
