package fr.cobaltians.cobalt.customviews;

import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import fr.cobaltians.cobalt.fragments.HTMLFragment;

public class SwipeWebView extends OverScrollingWebView implements OnGestureListener {

	/**
	 * the fragment that handles the scroll events of the SwipeWebView
	 */
	protected HTMLFragment mSwipeListener;
	
	/**
	 * Detector that handles the gesture events of the SwipeWebView
	 */
	private GestureDetector mGestureDetector;
	
	/*
	 * CONSTRUCTORS
	 */
	public SwipeWebView(Context context) {
		super(context);
		setGestureDetector(context);
	}

	public SwipeWebView(Context context, AttributeSet attrs,int defStyle) {
		super(context, attrs, defStyle);
		setGestureDetector(context);
	}

	public SwipeWebView(Context context, AttributeSet attrs) {
		super(context, attrs);
		setGestureDetector(context);
	}

	/*
	 * GETTERS/SETTERS
	 */
	public HTMLFragment getSwipeListener() {
		return mSwipeListener;
	}

	public void setSwipeListener(HTMLFragment swipeListener) {
		mSwipeListener = swipeListener;
	}

	protected void setGestureDetector(Context context) {
		mGestureDetector = new GestureDetector(context, this);
	}
	
	/**
	 * This methods passes all informations about the swipe of the SwipeWebView to the mSwipeListener
	 */
	@Override
	public boolean onTouchEvent(MotionEvent event) {
	    return mGestureDetector.onTouchEvent(event) || super.onTouchEvent(event);
	}
	
	@Override
	public boolean onFling(MotionEvent start, MotionEvent stop, float velocityX, float velocityY) {
		if(mSwipeListener != null && ISwipeListener.class.isAssignableFrom(mSwipeListener.getClass())) {
			((ISwipeListener) mSwipeListener).onSwipeGesture(start.getX() > stop.getX() ? true : false);
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
