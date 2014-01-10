package fr.cobaltians.cobalt.customviews;

/**
 * Interface providing a method to listen to the swipe of an SwipeWebView
 * @author Sebastien
 */
public interface IGestureListener {

	public final static int GESTURE_SWIPE_LEFT = 0;		// Swipe from right to left
	public final static int GESTURE_SWIPE_RIGHT = 1;	// Swipe from left to right
	
	/**
	 * Called after the SwipeWebView has swiped.
	 * @param direction true if swipe from right to left
	 * 					false otherwise
	 */
	void onSwipeGesture(int direction);
}
