package fr.cobaltians.cobaltcatalog.fragments;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import fr.cobaltians.cobalt.customviews.OverScrollingWebView;
import fr.cobaltians.cobalt.fragments.HTMLFragment;
import fr.cobaltians.cobaltcatalog.R;

public class ZoomHybridFragment extends HTMLFragment {

	private Button zoomInButton,zoomOutButton;
	private int zoomLevel;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		zoomLevel = 10;
	}

	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = super.onCreateView(inflater, container, savedInstanceState);
		return view;
	}
	
	
	@Override
	protected int getLayoutToInflate() {
		return R.layout.zoom_hybrid_fragment;
	}


	@Override
	protected void setUpViews(View rootView) {
		//webView = (OverScrollingWebView) rootView.findViewById(R.id.webView);
		super.setUpViews(rootView);
		zoomInButton = (Button) rootView.findViewById(R.id.zoomInButton);
		zoomOutButton = (Button) rootView.findViewById(R.id.zoomOutButton);
	}

	@Override
	protected void setUpListeners() {
		zoomInButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				zoomOutButton.setEnabled(true);
				zoomLevel++;

				if(zoomLevel >= 20)
				{
					zoomInButton.setEnabled(false);
				}
				setZoomLevelInWebView(zoomLevel);
			}
		});

		zoomOutButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				zoomInButton.setEnabled(true);
				zoomLevel--;

				if(zoomLevel <= 5)
				{
					zoomOutButton.setEnabled(false);
				}
				setZoomLevelInWebView(zoomLevel);
			}
		});
	}


	private void setZoomLevelInWebView(int nZoomLevel)
	{
		/*
		JSONObject obj = new JSONObject();
		try {
			obj.put(kJSType, JSTypeEvent);
			obj.put(kJSName, JSNameSetZoom);
			obj.put(kJSValue, nZoomLevel);
			executeScriptInWebView(obj);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		*/
	}


	@Override
	protected void onUnhandledMessage(JSONObject message) {
		
	}
}
