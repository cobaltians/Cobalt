/**
 *
 * CobaltActivity
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

import fr.cobaltians.cobalt.Cobalt;
import fr.cobaltians.cobalt.R;
import fr.cobaltians.cobalt.fragments.CobaltFragment;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * {@link Activity} containing a {@link CobaltFragment}.
 * @author Diane
 */
public abstract class CobaltActivity extends ActionBarActivity {

    protected static final String TAG = CobaltActivity.class.getSimpleName();
    private static boolean sWasPushedFromModal = false;

    private boolean mWasPushedAsModal;

    /***************************************************************************************************************
	 * LIFECYCLE
	 ***************************************************************************************************************/

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(getLayoutToInflate());

        Bundle bundle = getIntent().getExtras();
        Bundle extras = (bundle != null) ? bundle.getBundle(Cobalt.kExtras) : null;

        if (extras != null && extras.containsKey(Cobalt.kBars)) {
            try {
                JSONObject actionBar = new JSONObject(extras.getString(Cobalt.kBars));
                setupActionBar(actionBar);
            }
            catch (JSONException exception) {
                if (Cobalt.DEBUG) Log.e(Cobalt.TAG, TAG + " - onCreate: action bar configuration parsing failed. " + extras.getString(Cobalt.kBars));
                exception.printStackTrace();
            }
        }

		if (savedInstanceState == null) {
            CobaltFragment fragment = getFragment();

            if (fragment != null) {
                if (bundle != null) {
                    if (extras != null) fragment.setArguments(extras);

                    mWasPushedAsModal = bundle.getBoolean(Cobalt.kPushAsModal, false);
                    if (mWasPushedAsModal) {
                        sWasPushedFromModal = true;
                        overridePendingTransition(R.anim.modal_open_enter, android.R.anim.fade_out);
                    }
                    else if (bundle.getBoolean(Cobalt.kPopAsModal, false)) {
                        sWasPushedFromModal = false;
                        overridePendingTransition(android.R.anim.fade_in, R.anim.modal_close_exit);
                    }
                    else if (sWasPushedFromModal) overridePendingTransition(R.anim.modal_push_enter, R.anim.modal_push_exit);
                }

                if (findViewById(getFragmentContainerId()) != null) {
                    getSupportFragmentManager().beginTransaction().replace(getFragmentContainerId(), fragment).commit();
                }
                else if (Cobalt.DEBUG) Log.e(Cobalt.TAG, TAG + " - onCreate: fragment container not found");
            }
            else if (Cobalt.DEBUG) Log.e(Cobalt.TAG, TAG + " - onCreate: getFragment() returned null");
		}
	}

    @Override
    public void finish() {
        super.finish();

        if (mWasPushedAsModal) {
            sWasPushedFromModal = false;
            overridePendingTransition(android.R.anim.fade_in, R.anim.modal_close_exit);
        }
        else if (sWasPushedFromModal) overridePendingTransition(R.anim.modal_pop_enter, R.anim.modal_pop_exit);
    }

	/*****************************************************************************************************************
	 * COBALT
     *****************************************************************************************************************/

	/*********************************************
	 * Ui
	 *********************************************/

	/**
	 * Returns a new instance of the contained fragment. 
	 * This method should be overridden in subclasses.
	 * @return a new instance of the fragment contained.
	 */
	protected abstract CobaltFragment getFragment();

	protected int getLayoutToInflate() {
		return R.layout.activity_cobalt;
	}

	public int getFragmentContainerId() {
		return R.id.fragment_container;
	}

    private void setupActionBar(JSONObject configuration) {
        ActionBar actionBar = getSupportActionBar();

        if (actionBar == null) {
            if (Cobalt.DEBUG) Log.w(Cobalt.TAG, TAG + "setupActionBar: activity does not have an action bar");
            return;
        }

        // Reset
        actionBar.setLogo(null);
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setTitle(null);
        actionBar.setDisplayShowTitleEnabled(false);

        // Color
        String backgroundColor = configuration.optString(Cobalt.kBackgroundColor);
        try {
            if (backgroundColor.isEmpty()) throw new IllegalArgumentException();
            int colorInt = Color.parseColor(backgroundColor);
            actionBar.setBackgroundDrawable(new ColorDrawable(colorInt));
            // TODO: setSplitBackgroundDrawable
        }
        catch (IllegalArgumentException exception) {
            if (Cobalt.DEBUG) Log.w(Cobalt.TAG, TAG + "setupActionBar: backgroundColor " + backgroundColor + " format not supported, use #RRGGBB.");
            exception.printStackTrace();
        }

        // Icon
        String icon = configuration.optString(Cobalt.kAndroidIcon);
        try {
            if (icon.isEmpty()) throw new IllegalArgumentException();
            // TODO delete debug log
            Log.d(Cobalt.TAG, TAG + "setupActionBar: split" + icon);
            String[] split = icon.split(":");
            for (int i = 0 ; i < split.length ; i++) Log.d(Cobalt.TAG, TAG + "setupActionBar: split[" + i + "]: " + split[i]);
            if (split.length != 2) throw new IllegalArgumentException();
            int resId = getResources().getIdentifier(split[1], "drawable", split[0]);
            if (resId == 0) Log.w(Cobalt.TAG, TAG + "setupActionBar: androidIcon " + icon + " not found.");
            else {
                actionBar.setLogo(resId);
                actionBar.setDisplayShowHomeEnabled(true);
            }
        }
        catch (IllegalArgumentException exception) {
            if (Cobalt.DEBUG) Log.w(Cobalt.TAG, TAG + "setupActionBar: androidIcon " + icon + " format not supported, use com.example.app:icon.");
            exception.printStackTrace();
        }

        // Title
        String title = configuration.optString(Cobalt.kTitle);
        if (! title.isEmpty()) {
            actionBar.setTitle(title);
            actionBar.setDisplayShowTitleEnabled(true);
        }
        else if (Cobalt.DEBUG) Log.w(Cobalt.TAG, TAG + "setupActionBar: title is empty.");

        // Visibility
        boolean visible = configuration.optBoolean(Cobalt.kVisible, true);
        if (visible) actionBar.show();
        else actionBar.hide();
    }

	/*****************************************************************************************************************
	 * Back
	 *****************************************************************************************************************/

	/**
	 * Called when back button is pressed. 
	 * This method should NOT be overridden in subclasses.
	 */
	@Override
	public void onBackPressed() {
        Fragment fragment = getSupportFragmentManager().findFragmentById(getFragmentContainerId());
        if (fragment != null
            && CobaltFragment.class.isAssignableFrom(fragment.getClass())) {
            ((CobaltFragment) fragment).askWebViewForBackPermission();
        }
        else {
            super.onBackPressed();
            if (Cobalt.DEBUG) Log.i(Cobalt.TAG,     TAG + " - onBackPressed: no fragment container found \n"
                                                    + " or fragment found is not an instance of CobaltFragment. \n"
                                                    + "Call super.onBackPressed()");
        }
	}

	/**
	 * Called from the contained {@link CobaltFragment} when the Web view has authorized the back event.
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
	 *****************************************************************************************************************/

	/**
	 * Called when a {@link CobaltWebLayerFragment} has been dismissed.
	 * This method may be overridden in subclasses.
	 */
	public void onWebLayerDismiss(String page, JSONObject data) {
        CobaltFragment fragment = (CobaltFragment) getSupportFragmentManager().findFragmentById(getFragmentContainerId());
        if (fragment != null) {
            fragment.onWebLayerDismiss(page, data);
		}
        else if (Cobalt.DEBUG) Log.e(Cobalt.TAG,   TAG + " - onWebLayerDismiss: no fragment container found");
	}
}
