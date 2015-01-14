package fr.cobaltians.cobaltcatalog.fragments;

import fr.haploid.WebservicesPlugin.WebservicesInterface;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SimpleHybridFragment extends AbstractFragment implements WebservicesInterface {

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

    @Override
    public JSONObject treatData(JSONObject data, JSONObject process) {
        try {
            String extension = process.getString("ext");
            JSONObject dataInData = data.getJSONObject("data");
            JSONObject responseData = dataInData.getJSONObject("responseData");
            JSONArray results = responseData.getJSONArray("results");

            if (results.length()>0) {
                JSONArray resultsTreat = null;
                for (int i = 0 ; i < results.length() ; i++) {
                    JSONObject item = results.getJSONObject(i);
                    String url = item.getString("url");

                    int urlLength = url.length();
                    int extensionLength = extension.length();

                    String urlExtension = url.substring(urlLength - extensionLength);
                    if (urlExtension.equals(extension)) {
                        if (resultsTreat == null)
                            resultsTreat = new JSONArray();
                        resultsTreat.put(item);
                    }
                }
                responseData.put("results", resultsTreat);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return data;
    }

    @Override
    public boolean handleError(JSONObject call, JSONObject response) {
        return true;
    }

    @Override
    public boolean storeValue(String value, String key) {
        return false;
    }

    @Override
    public String storedValueForKey(String key) {
        return null;
    }
}
