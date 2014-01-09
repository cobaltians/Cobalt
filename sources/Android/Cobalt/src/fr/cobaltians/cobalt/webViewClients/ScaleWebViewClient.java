package fr.cobaltians.cobalt.webViewClients;

import android.webkit.WebView;
import android.webkit.WebViewClient;
import fr.cobaltians.cobalt.fragments.HTMLFragment;
import fr.cobaltians.cobalt.fragments.HTMLPullToRefreshFragment;

public class ScaleWebViewClient extends WebViewClient {

	/**
	 * Fragment handling scale events of the OverScrollingWebView
	 */
	protected HTMLPullToRefreshFragment mScaleListener;
	
	public HTMLFragment getScaleListener() {
		return mScaleListener;
	}

	public void setScaleListener(HTMLPullToRefreshFragment scaleListener) {
		mScaleListener = scaleListener;
	}
	
	@Override
	public void onScaleChanged(WebView webview, float oldScale, float newScale) {
		super.onScaleChanged(webview, oldScale, newScale);
		
		if(mScaleListener != null) {
			mScaleListener.notifyScaleChange(oldScale, newScale);
		}
	}
}
