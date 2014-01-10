package fr.cobaltians.cobalt.customviews;

import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import fr.cobaltians.cobalt.fragments.HTMLFragment;

public class GestureWebView extends OverScrollingWebView implements OnGestureListener {
	
	/**
	 * Fragment handling gesture events
	 */
	protected HTMLFragment mGestureListener;
	
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
	public HTMLFragment getGestureListener() {
		return mGestureListener;
	}

	public void setGestureListener(HTMLFragment gestureListener) {
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
