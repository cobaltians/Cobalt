package fr.cobaltians.cobalt.customviews;

import fr.cobaltians.cobalt.Cobalt;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

/**
 * Created by sebastien on 31/07/2014.
 */
public class CobaltSwipeRefreshLayout extends SwipeRefreshLayout {

    private static final String TAG = CobaltSwipeRefreshLayout.class.getSimpleName();
    private static final int Y_SCROLL_BUFFER = 5;

    private OverScrollingWebView mWebView;

    public CobaltSwipeRefreshLayout(Context context) {
        super(context);
    }

    public CobaltSwipeRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setWebView(OverScrollingWebView webView) {
       mWebView = webView;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        if (mWebView == null) {
            if (Cobalt.DEBUG) Log.w(Cobalt.TAG, TAG + " - onInterceptTouchEvent: WebView is null.");
        }
        else {
            if (mWebView.getScrollY() <= Y_SCROLL_BUFFER) return super.onInterceptTouchEvent(event);
        }

        return false;
    }
}
