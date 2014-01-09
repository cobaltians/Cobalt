package fr.cobaltians.cobalt.customviews;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.webkit.WebView;
import fr.cobaltians.cobalt.fragments.HTMLFragment;

public class OverScrollingWebView extends WebView {

	/**
	 * Fragment handling scroll events
	 */
	protected HTMLFragment mScrollListener;

	public OverScrollingWebView(Context context) {
		super(context);
	}

	public OverScrollingWebView(Context context, AttributeSet attributes) {
		super(context, attributes);
	}
	
	public OverScrollingWebView(Context context, AttributeSet attributes, int defaultStyle) {
		super(context, attributes, defaultStyle);
	}
	
	public HTMLFragment getScrollListener() {
		return mScrollListener;
	}

	public void setScrollListener(HTMLFragment scrollListener) {
		mScrollListener = scrollListener;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			int scrollY = getScrollY();

			// TODO: this is INSANE! Scrolls the webview one pixel down then one pixel up to avoid the freeze feeling...
			scrollTo(0, getScrollY() + 1);
			scrollTo(0, scrollY);
		}
		
		return super.onTouchEvent(event);
	}

	/**
	 * Notifies listener of scrolling
	 */
	@Override
	protected void onScrollChanged(int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
		super.onScrollChanged(scrollX, scrollY, oldScrollX, oldScrollY);
		
		if(	mScrollListener != null 
			&& IScrollListener.class.isAssignableFrom(mScrollListener.getClass())) {
			((IScrollListener) mScrollListener).onOverScrolled(scrollX, scrollY, oldScrollX, oldScrollY);
		}
	}	
}
