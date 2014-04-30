package fr.cobaltians.cobaltcatalog.fragments;

import fr.cobaltians.cobaltcatalog.R;

import fr.cobaltians.cobalt.Cobalt;
import fr.cobaltians.cobalt.fragments.HTMLFragment;

import android.util.Log;
import android.webkit.JavascriptInterface;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SimpleHybridFragment extends HTMLFragment {
	
	@Override
	protected int getLayoutToInflate()
	{
		return R.layout.simple_hybrid_fragment;
	}
	
	/*
	@Override
	protected void setUpViews(View rootView) 
	{
		webView = (OverScrollingWebView) rootView.findViewById(R.id.webView);
	}
	*/
	
	@JavascriptInterface
	public boolean handleMessageSentByJavaScript(String messageJS)
	{	
		JSONObject jsonObj;
		try 
		{
			jsonObj = new JSONObject(messageJS);
			if(jsonObj != null)
			{
				String type = jsonObj.optString(Cobalt.kJSType);

				//TYPE = EVENT
				if(type != null && type.length() >0 && type.equals(Cobalt.JSTypeEvent))
				{
					String name = jsonObj.optString(Cobalt.kJSEvent);
					if(name != null && name.length() >0 && name.equals("getBigData"))
					{
						final int value = jsonObj.optInt(Cobalt.kJSValue);

						String callbackId = jsonObj.optString(Cobalt.kJSCallback);
						if(callbackId != null && callbackId.length() >0)
						{
							JSONArray a = generateBigData(value);
							JSONObject data = new JSONObject();
							data.put(Cobalt.kJSValue, a);
							sendCallback(callbackId, data);
						}
						return true;

					}
				}	
			}
		} catch (JSONException e1) {
			Log.e(getClass().getName(),"JSON EXCEPTION FOR JSON : "+messageJS);
			e1.printStackTrace();
		}
		return super.handleMessageSentByJavaScript(messageJS);
	}
	
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
