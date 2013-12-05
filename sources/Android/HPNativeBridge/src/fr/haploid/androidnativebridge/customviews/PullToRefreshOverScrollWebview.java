package fr.haploid.androidnativebridge.customviews;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.util.AttributeSet;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import com.handmark.pulltorefresh.library.OverscrollHelper;
import com.handmark.pulltorefresh.library.PullToRefreshBase;

import fr.haploid.androidnativebridge.R;

/**
 * {@link PullToRefreshBase<T>} that uses a customized webView to handle the pullToRefresh and the InfiniteScroll.
 * @author Diane
 *
 */
public class PullToRefreshOverScrollWebview extends PullToRefreshBase<OverScrollingWebView> {

	private float mWebviewScale = 1;
	
	private static final OnRefreshListener<OverScrollingWebView> defaultOnRefreshListener = new OnRefreshListener<OverScrollingWebView>() {
		@Override
		public void onRefresh(PullToRefreshBase<OverScrollingWebView> refreshView) {
			refreshView.getRefreshableView().reload();
		}
	};

	private final WebChromeClient defaultWebChromeClient = new WebChromeClient() {

		@Override
		public void onProgressChanged(WebView view, int newProgress) {
			if (newProgress == 100) {
				onRefreshComplete();
			}
		}
	};
	
	
	/*
	 * CONSTRUCTORS
	 */
	
	public PullToRefreshOverScrollWebview(Context context, AttributeSet attrs) {
		super(context, attrs);
		setOnRefreshListener(defaultOnRefreshListener);
		getRefreshableView().setWebChromeClient(defaultWebChromeClient);
	}

	public PullToRefreshOverScrollWebview(Context context,com.handmark.pulltorefresh.library.PullToRefreshBase.Mode mode,com.handmark.pulltorefresh.library.PullToRefreshBase.AnimationStyle animStyle) {
		super(context, mode, animStyle);
		setOnRefreshListener(defaultOnRefreshListener);
		getRefreshableView().setWebChromeClient(defaultWebChromeClient);
	}

	public PullToRefreshOverScrollWebview(Context context,
			com.handmark.pulltorefresh.library.PullToRefreshBase.Mode mode) {
		super(context, mode);
		setOnRefreshListener(defaultOnRefreshListener);
		getRefreshableView().setWebChromeClient(defaultWebChromeClient);
	}

	public PullToRefreshOverScrollWebview(Context context) {
		super(context);
		setOnRefreshListener(defaultOnRefreshListener);
		getRefreshableView().setWebChromeClient(defaultWebChromeClient);
	}

	@Override
	public com.handmark.pulltorefresh.library.PullToRefreshBase.Orientation getPullToRefreshScrollDirection() {
		return Orientation.VERTICAL;
	}

	@Override
	protected OverScrollingWebView createRefreshableView(Context context,AttributeSet attrs) {
		OverScrollingWebView webView;
		if (VERSION.SDK_INT >= VERSION_CODES.GINGERBREAD) {
			webView = new MInternalWebViewSDK9(context, attrs);
		} else {
			webView = new OverScrollingWebView(context, attrs);
		}

		webView.setId(R.id.webview);
		return webView;
	}

	@Override
	protected boolean isReadyForPullStart() {
		return getRefreshableView().getScrollY() == 0;
	}

	@Override
	protected boolean isReadyForPullEnd() {
		//TODO -> scale problem...
		float exactContentHeight = (float) Math.floor(getRefreshableView().getContentHeight()  /* * getRefreshableView().getScale()*/);
		return getRefreshableView().getScrollY() >= (exactContentHeight - getRefreshableView().getHeight());
	}

	@Override
	protected void onPtrRestoreInstanceState(Bundle savedInstanceState) {
		super.onPtrRestoreInstanceState(savedInstanceState);
		getRefreshableView().restoreState(savedInstanceState);
	}

	@Override
	protected void onPtrSaveInstanceState(Bundle saveState) {
		super.onPtrSaveInstanceState(saveState);
		getRefreshableView().saveState(saveState);
	}

	public float getWebviewScale() {
		return mWebviewScale;
	}

	public void setWebviewScale(float mWebviewScale) {
		this.mWebviewScale = mWebviewScale;
	}

	@TargetApi(9)
	final class MInternalWebViewSDK9 extends OverScrollingWebView {

		// WebView doesn't always scroll back to it's edge so we add some
		// fuzziness
		static final int OVERSCROLL_FUZZY_THRESHOLD = 2;

		// WebView seems quite reluctant to overscroll so we use the scale
		// factor to scale it's value
		static final float OVERSCROLL_SCALE_FACTOR = 1.5f;

		public MInternalWebViewSDK9(Context context, AttributeSet attrs) {
			super(context, attrs);
		}

		@Override
		protected boolean overScrollBy(int deltaX, int deltaY, int scrollX, int scrollY, int scrollRangeX,
				int scrollRangeY, int maxOverScrollX, int maxOverScrollY, boolean isTouchEvent) {

			final boolean returnValue = super.overScrollBy(deltaX, deltaY, scrollX, scrollY, scrollRangeX,
					scrollRangeY, maxOverScrollX, maxOverScrollY, isTouchEvent);

			// Does all of the hard work...
			OverscrollHelper.overScrollBy(PullToRefreshOverScrollWebview.this, deltaX, scrollX, deltaY, scrollY,
					getScrollRange(), OVERSCROLL_FUZZY_THRESHOLD, OVERSCROLL_SCALE_FACTOR, isTouchEvent);

			return returnValue;
		}

		private int getScrollRange() {
			//TODO -> scale problem...
			return (int) Math.max(0, Math.floor(getRefreshableView().getContentHeight() /* * getRefreshableView().getScale()*/)
					- (getHeight() - getPaddingBottom() - getPaddingTop()));
		}
		
//		@Override
//		protected void onOverScrolled(int scrollX, int scrollY, boolean clampedX, boolean clampedY)
//		{
//			super.onOverScrolled(scrollX, scrollY, clampedX, clampedY);
//			if(mScrollListener != null && IScrollListener.class.isAssignableFrom(mScrollListener.getClass()))
//			{
//				((IScrollListener) mScrollListener).onOverScrolled(scrollX, scrollY, clampedX, clampedY);
//			}
//		}
		
		@Override
		protected void onScrollChanged(int l, int t, int oldl, int oldt) {
			super.onScrollChanged(l, t, oldl, oldt);
			if(mScrollListener != null && IScrollListener.class.isAssignableFrom(mScrollListener.getClass()))
			{
				((IScrollListener) mScrollListener).onOverScrolled(l, t, oldl, oldt);
			}
		}	
	}

}
