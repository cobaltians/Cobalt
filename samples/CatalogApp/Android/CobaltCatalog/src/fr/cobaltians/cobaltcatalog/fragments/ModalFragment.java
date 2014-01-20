package fr.cobaltians.cobaltcatalog.fragments;

import org.json.JSONObject;

import fr.cobaltians.cobalt.fragments.HTMLFragment;

public class ModalFragment extends HTMLFragment {

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
	@Override
	protected void onPullToRefreshRefreshed() {
	}
	@Override
	protected void onInfiniteScrollRefreshed() {		
	}
}
