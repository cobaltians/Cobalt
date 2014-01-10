package fr.cobaltians.cobalt.activities;

import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import fr.cobaltians.cobalt.fragments.HTMLFragment;
import fr.cobaltians.cobalt.fragments.HTMLPopUpWebview;
import fr.cobaltians.cobalt.R;

/**
 * {@link Activity} containing a {@link HTMLFragment}.
 * 
 * @author Diane
 */
public abstract class HTMLActivity extends FragmentActivity {

	protected final boolean mDebug = false;

	/**************************************************************************************************************************
	 * LIFECYCLE
	 *************************************************************************************************************************/
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(getLayoutToInflate());

		if (savedInstanceState == null) {
			HTMLFragment fragment = getFragment();

			Bundle bundle = getIntent().getExtras();
			if (bundle != null 
				&& bundle.containsKey(HTMLFragment.kExtras)) {
				fragment.setArguments(bundle.getBundle(HTMLFragment.kExtras));
			}

			if (findViewById(getFragmentContainerId()) != null) {
				android.support.v4.app.FragmentTransaction fragmentTransition;
				fragmentTransition = getSupportFragmentManager().beginTransaction().replace(getFragmentContainerId(), fragment);
				fragmentTransition.commit();
			} 
			else {
				if (mDebug) Log.e(getClass().getSimpleName(), "onCreate: fragment container not found");
			}
		}
	}

	/*********************************************************
	 * MENU
	 ********************************************************/
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Adds items to the action bar if it's present.
		getMenuInflater().inflate(R.menu.activity_html, menu);
		return true;
	}

	/*************************************
	 * COBALT
	 ************************************/

	/*************************************
	 * Ui
	 ************************************/

	/**
	 * Returns a new instance of the contained fragment. This method should be
	 * overridden in subclasses.
	 * 
	 * @return a new instance of the fragment contained.
	 */
	protected abstract HTMLFragment getFragment();

	protected int getLayoutToInflate() {
		return R.layout.activity_html;
	}

	public int getFragmentContainerId() {
		return R.id.fragment_container;
	}

	/*****************************************************************************************************************
	 * Back
	 ****************************************************************************************************************/

	/**
	 * Called when back button is pressed. This method should NOT be overridden
	 * in subclasses.
	 */
	@Override
	public void onBackPressed() {
		HTMLFragment fragment = (HTMLFragment) getSupportFragmentManager().findFragmentById(getFragmentContainerId());
		if (fragment != null) {
			fragment.askWebViewForBackPermission();
		}
	}

	/**
	 * Called from the contained {@link HTMLFragment} when the webview has
	 * authorized the back event. This method should NOT be overridden in
	 * subclasses.
	 */
	public void goBack() {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				goBackWithSuper();
			}
		});
	}

	private void goBackWithSuper() {
		super.onBackPressed();
	}

	/*****************************************************************************************************************
	 * Back
	 ****************************************************************************************************************/

	/**
	 * Called when a {@link HTMLPopUpWebview} has been dismissed. This method
	 * may be overridden in subclasses.
	 */
	public void onWebPopupDismiss(String page, JSONObject data) {
		HTMLFragment fragment = (HTMLFragment) getSupportFragmentManager().findFragmentById(getFragmentContainerId());
		if (fragment != null) {
			fragment.onWebPopupDismiss(page, data);
		}
	}
}
