package fr.cobaltians.cobaltcatalog.fragments;

import fr.cobaltians.cobaltcatalog.R;

import fr.cobaltians.cobalt.fragments.CobaltFragment;

import org.json.JSONObject;

public class SimpleHybridFragment extends CobaltFragment {
	
	@Override
	protected int getLayoutToInflate() {
		return R.layout.simple_hybrid_fragment;
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
