package fr.cobaltians.cobaltcatalog.fragments;

import fr.cobaltians.cobaltcatalog.R;

import fr.cobaltians.cobalt.Cobalt;
import fr.cobaltians.cobalt.fragments.CobaltFragment;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

import org.json.JSONException;
import org.json.JSONObject;

public class EventsFragment extends AbstractFragment {

    // ZOOM
    protected final static String JSNameSetZoom = "setZoom";
    protected final static String JSNameHello = "hello";

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
		return R.layout.events_fragment;
	}


	@Override
	protected void setUpViews(View rootView) {
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
		JSONObject data = new JSONObject();
		try {
			data.put(Cobalt.kJSValue, nZoomLevel);
			sendEvent(JSNameSetZoom, data, null);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	//  unhandled JS messages
	@Override
	protected void onUnhandledMessage(JSONObject message) { }
	@Override
	protected boolean onUnhandledEvent(String name, JSONObject data, String callback) {
        if (super.onUnhandledEvent(name, data, callback)) return true;
        else if(name.equals(JSNameHello)) {
            AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
            alert.setMessage("hello world");
            AlertDialog mAlert = alert.create();
            mAlert.setCanceledOnTouchOutside(true);
            mAlert.show();

            return true;
        }
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
