package fr.cobaltians.cobaltcatalog.fragments;

import fr.cobaltians.cobalt.fragments.CobaltFragment;

import org.json.JSONObject;

public class PullToRefreshFragment extends CobaltFragment {

	@Override
	protected boolean onUnhandledEvent(String name, JSONObject data, String callback) {
		return false;
	}

	@Override
	protected boolean onUnhandledCallback(String name, JSONObject data) {
		return false;
	}

    @Override
    protected void onUnhandledMessage(JSONObject message) { }
}
