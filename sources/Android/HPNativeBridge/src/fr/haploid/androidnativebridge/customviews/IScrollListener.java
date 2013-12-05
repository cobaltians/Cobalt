package fr.haploid.androidnativebridge.customviews;

/**
 * Interface that provides a method to listen to the scroll of an OverScrollingWebview
 * @author Diane
 *
 */
public interface IScrollListener {

	/**
	 * Called after the OverScrollingWebView has scrolled.
	 * @param scrollX New X scroll value in pixels
	 * @param scrollY New Y scroll value in pixels
	 * @param oldscrollX old X scroll value in pixels
	 * @param oldscrollY old Y scroll value in pixels
	 */
	void onOverScrolled(int scrollX, int scrollY,int oldscrollX, int oldscrollY);
}
