package fr.cobaltians.cobaltcatalog.fragments;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import fr.cobaltians.cobalt.customviews.OverScrollingWebView;
import fr.cobaltians.cobalt.fragments.HTMLFragment;
import fr.cobaltians.cobaltcatalog.R;

public class SimpleHybridFragment extends HTMLFragment {
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View view = super.onCreateView(inflater, container, savedInstanceState);
		
		mRessourcePath = "www/";
		if(!webviewContentHasBeenLoaded)
		{
			loadFileContentFromAssets(mRessourcePath, (this.pageName != null) ? this.pageName : "index.html");
		}
		
		return view;
	}
	
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
				String type = jsonObj.optString(kJSType);

				//TYPE = EVENT
				if(type != null && type.length() >0 && type.equals(JSTypeEvent))
				{
					String name = jsonObj.optString(kJSEvent);
					if(name != null && name.length() >0 && name.equals("getBigData"))
					{
						final int value = jsonObj.optInt(kJSValue);

						String callbackId = jsonObj.optString(kJSCallback);
						if(callbackId != null && callbackId.length() >0)
						{
							JSONArray a = generateBigData(value);
							sendCallbackResponse(callbackId, a);
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

	@Override
	protected void onUnhandledMessage(JSONObject message) {
		
	}
}
