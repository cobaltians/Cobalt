package fr.cobaltians.cobalt.fragments;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.FragmentTransaction;
import android.graphics.Color;
import android.util.Log;
import android.webkit.JavascriptInterface;
import fr.cobaltians.cobalt.activities.HTMLActivity;

/**
 * A special {@link HTMLFragment} that is presented over the current HTMLFragment as a webAlert.
 * @author Diane
 * @details This class should not be instantiated directly. {@link HTMLFragment} manages it directly with webAlert messages.
 */
public class HTMLPopUpWebview extends HTMLFragment {	

	@Override
	public void onStart() {
		super.onStart();
		if(webView != null)
			webView.setBackgroundColor(Color.TRANSPARENT);
	}

	@Override
	@JavascriptInterface
	public boolean handleMessageSentByJavaScript(String messageJS)
	{
		final JSONObject jsonObj;
		try 
		{
			jsonObj = new JSONObject(messageJS);
			if(jsonObj != null)
			{
				if(jsonObj.has(kJSType))
				{
					String type = jsonObj.optString(kJSType);
					if(type != null && type.length() >0 && type.equals(JSTypeWebLayer))
					{
						String name = jsonObj.optString(kJSAction);
						if(name != null && name.length() > 0 && name.equals(JSActionWebLayerDismiss))
						{
							mHandler.post(new Runnable() {

								@Override
								public void run() {
									dismissWebAlertWithJSON(jsonObj);
								}
							});
							return true;
						}
					}
				}
			}
		} catch (JSONException e1) {
			if(mDebug) Log.e(getClass().getSimpleName(),"ERROR : CANNOT HANDLE MESSAGE WITH JSON EXCEPTION FOR JSON : #"+messageJS+"#");
			e1.printStackTrace();
		}
		return super.handleMessageSentByJavaScript(messageJS);
	}


	private void dismissWebAlertWithJSON(JSONObject obj)
	{
		if(obj != null)
		{
			double fadeDuration = obj.optDouble(kJSWebLayerFadeDuration,0);
			android.support.v4.app.FragmentTransaction fTransition;
			fTransition = getActivity().getSupportFragmentManager().beginTransaction();
			if(fadeDuration >0)
			{
				fTransition.setCustomAnimations(android.R.anim.fade_in,android.R.anim.fade_out,android.R.anim.fade_in,android.R.anim.fade_out);
			}
			else 
			{
				fTransition.setTransition(FragmentTransaction.TRANSIT_NONE);
			}
			fTransition.remove(this);
			fTransition.commit();
		}
	}

	protected void dismissWebAlert()
	{
		android.support.v4.app.FragmentTransaction fTransition;
		fTransition = getActivity().getSupportFragmentManager().beginTransaction();
		fTransition.setTransition(FragmentTransaction.TRANSIT_NONE);
		fTransition.remove(this);
		fTransition.commit();
	}

	@Override
	protected void handleBackButtonPressed(boolean allowedToGoBack) {
		if(allowedToGoBack)
		{
			dismissWebAlertWithJSON(new JSONObject());
		}
	}


	@Override
	public void onDestroy() {
		popupIsDismissed();
		super.onDestroy();
	}

	private void popupIsDismissed()
	{
		if(HTMLActivity.class.isAssignableFrom(getActivity().getClass()))
		{
			HTMLActivity activity = (HTMLActivity) getActivity();
			activity.onWebPopupDismiss(this.pageName,getParamsForDismiss());
		}
	}

	public Object getParamsForDismiss()
	{
		return null;
	}

	@Override
	protected void onUnhandledMessage(JSONObject message) {
		
	}
}
