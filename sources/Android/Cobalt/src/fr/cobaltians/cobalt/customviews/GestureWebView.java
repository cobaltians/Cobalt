/**
 *
 * GestureWebView
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

import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import fr.cobaltians.cobalt.fragments.CobaltFragment;

public class GestureWebView extends OverScrollingWebView implements OnGestureListener {
	
	/**
	 * Fragment handling gesture events
	 */
	protected CobaltFragment mGestureListener;
	
	/**
	 * Handles gesture events
	 */
	private GestureDetector mGestureDetector;
	
	/*********************************************************************************
	 * CONSTRUCTORS
	 ********************************************************************************/
	public GestureWebView(Context context) {
		super(context);
		setGestureDetector(context);
	}

	public GestureWebView(Context context, AttributeSet attributes) {
		super(context, attributes);
		setGestureDetector(context);
	}
	
	public GestureWebView(Context context, AttributeSet attributes,int defaultStyle) {
		super(context, attributes, defaultStyle);
		setGestureDetector(context);
	}
	
	/****************************************************************************
	 * GETTERS / SETTERS
	 ***************************************************************************/
	// Gesture listener
	public CobaltFragment getGestureListener() {
		return mGestureListener;
	}

	public void setGestureListener(CobaltFragment gestureListener) {
		mGestureListener = gestureListener;
	}

	// Gesture detector
	protected void setGestureDetector(Context context) {
		mGestureDetector = new GestureDetector(context, this);
	}
	
	/************************************************************************************************************************************************************************
	 * GESTURE EVENTS
	 ***********************************************************************************************************************************************************************/
	@Override
	public boolean onTouchEvent(MotionEvent event) {
	    return mGestureDetector.onTouchEvent(event) || super.onTouchEvent(event);
	}
	
	/**
	 * These methods notify gesture listener about gestures
	 * Only swipe left/right gesture is already implemented
	 */
	@Override
	public boolean onFling(MotionEvent start, MotionEvent stop, float velocityX, float velocityY) {
		if(	mGestureListener != null 
			&& IGestureListener.class.isAssignableFrom(mGestureListener.getClass())) {
			
			// Swipe left/right gesture
			if (velocityX > velocityY) {
				((IGestureListener) mGestureListener).onSwipeGesture(start.getX() > stop.getX() ? IGestureListener.GESTURE_SWIPE_LEFT : IGestureListener.GESTURE_SWIPE_RIGHT);
			}
		}
		
		return true;
	}
	
	// Unhandled gestures
	@Override
	public boolean onDown(MotionEvent e) { return false; }
	@Override
	public void onLongPress(MotionEvent e) { }
	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) { return false; }
	@Override
	public void onShowPress(MotionEvent e) { }
	@Override
	public boolean onSingleTapUp(MotionEvent e) { return false; }	
}
