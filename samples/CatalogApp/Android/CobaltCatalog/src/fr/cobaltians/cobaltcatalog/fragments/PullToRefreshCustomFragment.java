package fr.cobaltians.cobaltcatalog.fragments;

import fr.cobaltians.cobalt.fragments.CobaltFragment;

import org.json.JSONObject;

public class PullToRefreshCustomFragment extends CobaltFragment {
	
	@Override
	public void onStart() {
		super.onStart();

        setRefreshColorScheme(  android.R.color.holo_green_dark,
                                android.R.color.holo_red_dark,
                                android.R.color.holo_blue_dark,
                                android.R.color.holo_orange_light);
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
}
