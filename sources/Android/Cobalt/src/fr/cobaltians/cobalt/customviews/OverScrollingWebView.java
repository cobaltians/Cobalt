package fr.cobaltians.cobalt.customviews;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.webkit.WebView;
import fr.cobaltians.cobalt.fragments.HTMLFragment;

public class OverScrollingWebView extends WebView {

	/**
	 * the fragment that handles the scroll events of the OverScrollingWebView
	 */
	protected HTMLFragment mScrollListener;

	/*
	 * CONSTRUCTORS
	 */
	public OverScrollingWebView(Context context) {
		super(context);
	}


	public OverScrollingWebView(Context context, AttributeSet attrs,int defStyle) {
		super(context, attrs, defStyle);
	}


	public OverScrollingWebView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}


	/*
	 * GETTERS/SETTERS
	 */
	public HTMLFragment getmScrollListener() {
		return mScrollListener;
	}

	public void setScrollListener(HTMLFragment mScrollListener) {
		this.mScrollListener = mScrollListener;
	}


	/**
	 * This methods passes all informations about the scrolling of the OverScrollingWebview to the mScrollListener
	 */
//	@Override
//	protected void onOverScrolled(int scrollX, int scrollY, boolean clampedX, boolean clampedY)
//	{
//		super.onOverScrolled(scrollX, scrollY, clampedX, clampedY);
//		if(mScrollListener != null && IScrollListener.class.isAssignableFrom(mScrollListener.getClass()))
//		{
//			((IScrollListener) mScrollListener).onOverScrolled(scrollX, scrollY, clampedX, clampedY);
//		}
//	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {

		if (event.getAction() == MotionEvent.ACTION_DOWN){
			//Log.d("DEBUG ::: ", "in onTouchEvent() ---> ACTION_DOWN");
			int temp_ScrollY = getScrollY();

			//TODO INSANE : Scrolls the webview one pixel down and one pixel up to avoid the feeling of freeze...
			scrollTo(0, getScrollY() + 1);
			scrollTo(0, temp_ScrollY);

			//Log.d("  DEBUG COORDS::: ", "X=" + Integer.toString(getScrollX()) + " Y=" + Integer.toString(getScrollY()) + " Y2=" + Integer.toString(temp_ScrollY));
		}
		return super.onTouchEvent(event);
	}


	@Override
	protected void onScrollChanged(int l, int t, int oldl, int oldt) {
		super.onScrollChanged(l, t, oldl, oldt);
		if(mScrollListener != null && IScrollListener.class.isAssignableFrom(mScrollListener.getClass()))
		{
			((IScrollListener) mScrollListener).onOverScrolled(l, t, oldl, oldt);
		}
	}	
}
