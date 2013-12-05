package fr.haploid.androidnativebridge.webViewClients;

import android.webkit.WebView;
import android.webkit.WebViewClient;
import fr.haploid.androidnativebridge.fragments.HTMLFragment;
import fr.haploid.androidnativebridge.fragments.HTMLPullToRefreshFragment;

public class ScaleWebViewClient extends WebViewClient {

	/**
	 * the fragment that handles the scale events of the OverScrollingWebView
	 */
	protected HTMLPullToRefreshFragment mScaleListener;
	
	@Override
	public void onScaleChanged(WebView webview, float oldScale, float newScale) {
		super.onScaleChanged(webview, oldScale, newScale);
		if(mScaleListener != null)
		{
			mScaleListener.notifyScaleChange(oldScale, newScale);
		}
	}
	
	
	public HTMLFragment getScaleListener() {
		return mScaleListener;
	}

	public void setScaleListener(HTMLPullToRefreshFragment mScaleListener) {
		this.mScaleListener = mScaleListener;
	}
}
