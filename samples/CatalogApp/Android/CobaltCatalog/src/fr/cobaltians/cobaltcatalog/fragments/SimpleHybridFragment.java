package fr.cobaltians.cobaltcatalog.fragments;

import fr.cobaltians.cobaltcatalog.R;

import fr.cobaltians.cobalt.fragments.CobaltFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SimpleHybridFragment extends AbstractFragment {

	private JSONArray generateBigData(int size)
	{
		JSONArray a = new JSONArray();
		for(int i = 0 ; i < size ; i++)
		{
			String name = (i%2 == 0) ? "Léo" : "Popi";
			String imageName = "img/ic_launcher.png";
			double age = (i <= 100) ? i : i/100.0;
			
			JSONObject j = new JSONObject();
			try {
				j.put("username", name);
				j.put("userimage", imageName);
				j.put("userage", age);
				a.put(j);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		return a;
	}

	//  unhandled JS messages
	@Override
	protected void onUnhandledMessage(JSONObject message) {

    }

	@Override
	protected boolean onUnhandledEvent(String name, JSONObject data, String callback) {
        if (super.onUnhandledEvent(name, data, callback)) return true;
		else return false;
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
