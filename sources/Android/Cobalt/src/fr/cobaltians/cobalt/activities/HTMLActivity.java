/**
 *
 * HTMLActivity
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

package fr.cobaltians.cobalt.activities;

import fr.cobaltians.cobalt.BuildConfig;
import fr.cobaltians.cobalt.Cobalt;
import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import fr.cobaltians.cobalt.fragments.HTMLFragment;
import fr.cobaltians.cobalt.fragments.HTMLWebLayerFragment;
import fr.cobaltians.cobalt.R;

/**
 * {@link Activity} containing a {@link HTMLFragment}.
 * @author Diane
 */
public abstract class HTMLActivity extends FragmentActivity {

    protected static final String TAG = HTMLActivity.class.getSimpleName();

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
				if (BuildConfig.DEBUG) Log.e(Cobalt.TAG, TAG + " - onCreate: fragment container not found");
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
	 * Returns a new instance of the contained fragment. 
	 * This method should be overridden in subclasses.
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
	 * Called when back button is pressed. 
	 * This method should NOT be overridden in subclasses.
	 */
	@Override
	public void onBackPressed() {
		HTMLFragment fragment = (HTMLFragment) getSupportFragmentManager().findFragmentById(getFragmentContainerId());
		if (fragment != null) {
			fragment.askWebViewForBackPermission();
		}
	}

	/**
	 * Called from the contained {@link HTMLFragment} when the Web view has authorized the back event. 
	 * This method should NOT be overridden in subclasses.
	 */
	public void back() {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				backWithSuper();
			}
		});
	}

	private void backWithSuper() {
		super.onBackPressed();
	}

	/*****************************************************************************************************************
	 * Web Layer dismiss
	 ****************************************************************************************************************/

	/**
	 * Called when a {@link HTMLPopUpWebview} has been dismissed. 
	 * This method may be overridden in subclasses.
	 */
	public void onWebLayerDismiss(String page, JSONObject data) {
		HTMLFragment fragment = (HTMLFragment) getSupportFragmentManager().findFragmentById(getFragmentContainerId());
		if (fragment != null) {
			fragment.onWebLayerDismiss(page, data);
		}
	}
}
