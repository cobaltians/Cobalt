/**
 *
 * PullToRefreshOverScrollWebview
 * Cobalt
 *
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Cobaltians
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *
 */

package fr.cobaltians.cobalt.customviews;

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

import fr.cobaltians.cobalt.R;

/**
 * {@link PullToRefreshBase<T>} using a customized Web view to handle pull to refresh and the infinite scroll.
 * @author Diane
 *
 */
public class PullToRefreshOverScrollWebview extends PullToRefreshBase<OverScrollingWebView> {
	
	private static final OnRefreshListener<OverScrollingWebView> mRefreshListener = new OnRefreshListener<OverScrollingWebView>() {
		@Override
		public void onRefresh(PullToRefreshBase<OverScrollingWebView> refreshView) {
			refreshView.getRefreshableView().reload();
		}
	};

	private float mWebviewScale = 1;
	private final WebChromeClient mWebChromeClient = new WebChromeClient() {

		@Override
		public void onProgressChanged(WebView view, int newProgress) {
			if (newProgress == 100) {
				onRefreshComplete();
			}
		}
	};
	
	/********************************************************************************************************************************
	 * CONSTRUCTORS
	 *******************************************************************************************************************************/
	
	public PullToRefreshOverScrollWebview(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		setOnRefreshListener(mRefreshListener);
		getRefreshableView().setWebChromeClient(mWebChromeClient);
	}

	public PullToRefreshOverScrollWebview(Context context, PullToRefreshBase.Mode mode, PullToRefreshBase.AnimationStyle animStyle) {
		super(context, mode, animStyle);
		
		setOnRefreshListener(mRefreshListener);
		getRefreshableView().setWebChromeClient(mWebChromeClient);
	}

	public PullToRefreshOverScrollWebview(Context context, PullToRefreshBase.Mode mode) {
		super(context, mode);
		
		setOnRefreshListener(mRefreshListener);
		getRefreshableView().setWebChromeClient(mWebChromeClient);
	}

	public PullToRefreshOverScrollWebview(Context context) {
		super(context);
		
		setOnRefreshListener(mRefreshListener);
		getRefreshableView().setWebChromeClient(mWebChromeClient);
	}

	@Override
	public PullToRefreshBase.Orientation getPullToRefreshScrollDirection() {
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

	/********************************************************************************************************************************
	 * PULL TO REFRESH
	 *******************************************************************************************************************************/
	
	@Override
	protected boolean isReadyForPullStart() {
		return getRefreshableView().getScrollY() == 0;
	}

	@Override
	protected boolean isReadyForPullEnd() {
		// TODO: scaling problem
		float contentHeight = (float) Math.floor(getRefreshableView().getContentHeight() /* * getRefreshableView().getScale()*/);
		return getRefreshableView().getScrollY() >= (contentHeight - getRefreshableView().getHeight());
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

	/*************************************************
	 * WEB VIEW SCALE
	 ************************************************/
	public float getWebviewScale() {
		return mWebviewScale;
	}

	public void setWebviewScale(float mWebviewScale) {
		this.mWebviewScale = mWebviewScale;
	}
	
	@TargetApi(VERSION_CODES.GINGERBREAD)
	final class MInternalWebViewSDK9 extends OverScrollingWebView {

		// Web view doesn't always scroll back to its edge, so we add some fuzziness
		static final int OVERSCROLL_FUZZY_THRESHOLD = 2;

		// Web view seems quite reluctant to overscroll, so we use a scale factor to increase its value
		static final float OVERSCROLL_SCALE_FACTOR = 1.5f;

		public MInternalWebViewSDK9(Context context, AttributeSet attrs) {
			super(context, attrs);
		}

		@Override
		protected boolean overScrollBy(	int deltaX, int deltaY, 
										int scrollX, int scrollY, 
										int scrollRangeX, int scrollRangeY, 
										int maxOverScrollX, int maxOverScrollY, 
										boolean isTouchEvent) {
			final boolean returnValue = super.overScrollBy(	deltaX, deltaY, 
															scrollX, scrollY, 
															scrollRangeX, scrollRangeY, 
															maxOverScrollX, maxOverScrollY, 
															isTouchEvent);
			
			// Does the hard work...
			OverscrollHelper.overScrollBy(	PullToRefreshOverScrollWebview.this, 
											deltaX, scrollX, 
											deltaY, scrollY, 
											getScrollRange(), 
											OVERSCROLL_FUZZY_THRESHOLD, OVERSCROLL_SCALE_FACTOR, 
											isTouchEvent);

			return returnValue;
		}

		private int getScrollRange() {
			// TODO: scaling problem
			return (int) Math.max(0, Math.floor(getRefreshableView().getContentHeight() /* * getRefreshableView().getScale()*/) - (getHeight() - getPaddingBottom() - getPaddingTop()));
		}
	}
}
