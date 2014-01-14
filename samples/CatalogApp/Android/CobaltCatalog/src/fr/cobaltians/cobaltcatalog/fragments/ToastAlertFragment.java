package fr.cobaltians.cobaltcatalog.fragments;

import org.json.JSONObject;

import android.view.View;
import android.widget.Toast;
import fr.cobaltians.cobalt.customviews.OverScrollingWebView;
import fr.cobaltians.cobalt.fragments.HTMLFragment;
import fr.cobaltians.cobaltcatalog.R;

public class ToastAlertFragment extends HTMLFragment {

	@Override
	protected int getLayoutToInflate() {
		return R.layout.simple_hybrid_fragment;
	}

	/*
	@Override
	protected void setUpViews(View rootView) {
		webView = (OverScrollingWebView) rootView.findViewById(R.id.webView);
	}
	*/
	
	public void alertDialogClickedButton(long tag,int buttonIndex)
	{
		Toast.makeText(mContext, "tag = "+tag+" || buttonIndex = "+(-1-buttonIndex), Toast.LENGTH_SHORT).show();
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
}
