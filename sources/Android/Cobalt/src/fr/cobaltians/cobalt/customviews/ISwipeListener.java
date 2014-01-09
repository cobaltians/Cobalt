package fr.cobaltians.cobalt.customviews;

/**
 * Interface that provides a method to listen to the swipe of an SwipeWebView
 * @author Sebastien
 *
 */
public interface ISwipeListener {

	/**
	 * Called after the SwipeWebView has swiped.
	 * @param direction true if swipe from right to left
	 * 					false otherwise
	 */
	void onSwipeGesture(boolean direction);
}
