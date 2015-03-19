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
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.NavUtils;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * {@link Activity} containing a {@link CobaltFragment}.
 * @author Diane
 */
public abstract class CobaltActivity extends ActionBarActivity {

    protected static final String TAG = CobaltActivity.class.getSimpleName();

    // POP
    private static ArrayList<CobaltActivity> sActivitiesArrayList = new ArrayList<CobaltActivity>();

    // MODAL
    private boolean mWasPushedAsModal;
    private static boolean sWasPushedFromModal = false;

    // ACTION BAR MENU ITEMS
    private HashMap<Integer, String> mMenuItemsHashMap = new HashMap<Integer, String>();

    /************************************************************************************************************************
	 * LIFECYCLE
	 ************************************************************************************************************************/

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(getLayoutToInflate());

        sActivitiesArrayList.add(0, this);

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
    protected void onStart() {
        super.onStart();

        Cobalt.getInstance(getApplicationContext()).onActivityStarted(this);
    }

    public void onAppStarted() {
        Fragment fragment = getSupportFragmentManager().findFragmentById(getFragmentContainerId());
        if (fragment != null
            && CobaltFragment.class.isAssignableFrom(fragment.getClass())) {
            ((CobaltFragment) fragment).sendEvent(Cobalt.JSEventOnAppStarted, null, null);
        }
        else if (Cobalt.DEBUG) Log.i(Cobalt.TAG,    TAG + " - onAppStarted: no fragment container found \n"
                                                    + " or fragment found is not an instance of CobaltFragment. \n"
                                                    + "Drop onAppStarted event.");
    }

    public void onAppForeground() {
        Fragment fragment = getSupportFragmentManager().findFragmentById(getFragmentContainerId());
        if (fragment != null
            && CobaltFragment.class.isAssignableFrom(fragment.getClass())) {
            ((CobaltFragment) fragment).sendEvent(Cobalt.JSEventOnAppForeground, null, null);
        }
        else if (Cobalt.DEBUG) Log.i(Cobalt.TAG,    TAG + " - onAppForeground: no fragment container found \n"
                                                    + " or fragment found is not an instance of CobaltFragment. \n"
                                                    + "Drop onAppForeground event.");
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

    @Override
    protected void onStop() {
        super.onStop();

        Cobalt.getInstance(getApplicationContext()).onActivityStopped(this);
    }

    public void onAppBackground() {
        Fragment fragment = getSupportFragmentManager().findFragmentById(getFragmentContainerId());
        if (fragment != null
            && CobaltFragment.class.isAssignableFrom(fragment.getClass())) {
            ((CobaltFragment) fragment).sendEvent(Cobalt.JSEventOnAppBackground, null, null);
        }
        else if (Cobalt.DEBUG) Log.i(Cobalt.TAG,    TAG + " - onAppBackground: no fragment container found \n"
                                                    + " or fragment found is not an instance of CobaltFragment. \n"
                                                    + "Drop onAppBackground event.");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        sActivitiesArrayList.remove(0);
    }

    /**************************************************************************************************
     * MENU
     **************************************************************************************************/

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        Bundle bundle = getIntent().getExtras();
        Bundle extras = (bundle != null) ? bundle.getBundle(Cobalt.kExtras) : null;

        if (extras != null && extras.containsKey(Cobalt.kBars)) {
            try {
                JSONObject actionBar = new JSONObject(extras.getString(Cobalt.kBars));
                JSONArray actions = actionBar.optJSONArray(Cobalt.kActions);
                if (actions != null) return setupOptionsMenu(menu, actions);
            }
            catch (JSONException exception) {
                if (Cobalt.DEBUG) Log.e(Cobalt.TAG, TAG + " - onCreate: action bar configuration parsing failed. " + extras.getString(Cobalt.kBars));
                exception.printStackTrace();
            }
        }

        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mMenuItemsHashMap.containsKey(item.getItemId())) {
            String name = mMenuItemsHashMap.get(item.getItemId());

            Fragment fragment = getSupportFragmentManager().findFragmentById(getFragmentContainerId());
            if (fragment != null
                    && CobaltFragment.class.isAssignableFrom(fragment.getClass())) {
                try {
                    JSONObject data = new JSONObject();
                    data.put(Cobalt.kJSAction, Cobalt.JSActionButtonPressed);
                    data.put(Cobalt.kJSBarsButton, name);

                    JSONObject message = new JSONObject();
                    message.put(Cobalt.kJSType, Cobalt.JSTypeUI);
                    message.put(Cobalt.kJSUIControl, Cobalt.JSControlBars);
                    message.put(Cobalt.kJSData, data);

                    ((CobaltFragment) fragment).sendMessage(message);
                }
                catch(JSONException exception) { exception.printStackTrace(); }
            }
            else if (Cobalt.DEBUG) Log.i(Cobalt.TAG,    TAG + " - onOptionsItemSelected: no fragment container found \n"
                    + " or fragment found is not an instance of CobaltFragment. \n"
                    + "Drop " + name + "bars button pressed event.");

            return true;
        }
        else return super.onOptionsItemSelected(item);
    }

    /***************************************************************************************************************************************************************
	 * COBALT
     ***************************************************************************************************************************************************************/

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
        LinearLayout bottomActionBar = (LinearLayout) findViewById(R.id.bottom_actionbar);

        if (actionBar == null) {
            if (Cobalt.DEBUG) Log.w(Cobalt.TAG, TAG + "setupActionBar: activity does not have an action bar.");
            return;
        }

        // Reset
        actionBar.setLogo(null);
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setTitle(null);
        actionBar.setDisplayShowTitleEnabled(false);
        bottomActionBar.setVisibility(View.GONE);

        // Color
        String backgroundColor = configuration.optString(Cobalt.kBackgroundColor);
        try {
            if (backgroundColor.length() == 0) throw new IllegalArgumentException();
            int colorInt = Color.parseColor(backgroundColor);
            actionBar.setBackgroundDrawable(new ColorDrawable(colorInt));
            bottomActionBar.setBackgroundColor(colorInt);
        }
        catch (IllegalArgumentException exception) {
            if (Cobalt.DEBUG) Log.w(Cobalt.TAG, TAG + "setupActionBar: backgroundColor " + backgroundColor + " format not supported, use #RRGGBB.");
            exception.printStackTrace();
        }

        // Icon
        String icon = configuration.optString(Cobalt.kIcon);
        try {
            if (icon.length() == 0) throw new IllegalArgumentException();
            String[] split = icon.split(":");
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
        if (title.length() != 0) {
            actionBar.setTitle(title);
            actionBar.setDisplayShowTitleEnabled(true);
        }
        else if (Cobalt.DEBUG) Log.w(Cobalt.TAG, TAG + "setupActionBar: title is empty.");

        // Visibility
        boolean visible = configuration.optBoolean(Cobalt.kVisible, true);
        if (visible) actionBar.show();
        else actionBar.hide();
    }
    
    private boolean setupOptionsMenu(Menu menu, JSONArray actions) {
        ActionBar actionBar = getSupportActionBar();
        LinearLayout bottomActionBar = (LinearLayout) findViewById(R.id.bottom_actionbar);
        boolean showBottomActionBar = false;

        if (actionBar == null) {
            if (Cobalt.DEBUG) Log.w(Cobalt.TAG, TAG + "setupOptionsMenu: activity does not have an action bar.");
            return false;
        }

        int menuItemsAddedToTop = 0;
        int menuItemsAddedToOverflow = 0;

        int length = actions.length();

        for (int i = 0; i < length; i++) {
            try {
                JSONObject action = actions.getJSONObject(i);
                final String name = action.getString(Cobalt.kName);
                String title = action.getString(Cobalt.kTitle);
                String icon = action.optString(Cobalt.kIcon);
                String position = action.optString(Cobalt.kPosition, Cobalt.kPositionTop);
                boolean visible = action.optBoolean(Cobalt.kVisible, true);

                if (position.equals(Cobalt.kPositionTop)) {
                    if (icon.isEmpty()) throw new JSONException("Actions positionned at top or bottom must specify an icon attribute. Current: " + icon);
                    String[] split = icon.split(":");
                    if (split.length != 2) throw new JSONException("androidIcon " + icon + " format not supported, use com.example.app:icon.");
                    int resId = getResources().getIdentifier(split[1], "drawable", split[0]);
                    if (resId == 0) throw new JSONException("androidIcon " + icon + " not found.");

                    MenuItem menuItem = menu.add(Menu.NONE, i, menuItemsAddedToTop++, title);
                    menuItem.setIcon(resId);
                    if (menuItemsAddedToTop > 2) MenuItemCompat.setShowAsAction(menuItem, MenuItem.SHOW_AS_ACTION_IF_ROOM);
                    else MenuItemCompat.setShowAsAction(menuItem, MenuItem.SHOW_AS_ACTION_ALWAYS);
                    menuItem.setVisible(visible);

                    mMenuItemsHashMap.put(i, name);
                }
                else if (position.equals(Cobalt.kPositionOverflow)) {
                    MenuItem menuItem = menu.add(Menu.NONE, i, menuItemsAddedToOverflow++, title);
                    MenuItemCompat.setShowAsAction(menuItem, MenuItem.SHOW_AS_ACTION_NEVER);
                    menuItem.setVisible(visible);

                    mMenuItemsHashMap.put(i, name);
                }
                else if (position.equals(Cobalt.kPositionBottom)) {
                    if (icon.isEmpty()) throw new JSONException("Actions positionned at top or bottom must specify an icon attribute. Current: " + icon);
                    String[] split = icon.split(":");
                    if (split.length != 2) throw new JSONException("androidIcon " + icon + " format not supported, use com.example.app:icon.");
                    int resId = getResources().getIdentifier(split[1], "drawable", split[0]);
                    if (resId == 0) throw new JSONException("androidIcon " + icon + " not found.");

                    ImageButton button = new ImageButton(this);
                    button.setImageResource(resId);
                    button.setBackgroundResource(R.drawable.menu_item_background_dark);
                    ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(   ViewGroup.LayoutParams.WRAP_CONTENT,
                                                                                        getResources().getDimensionPixelSize(R.dimen.bottom_actionbar_button_size));
                    button.setLayoutParams(layoutParams);
                    int padding = getResources().getDimensionPixelSize(R.dimen.bottom_actionBar_button_padding);
                    button.setPadding(padding, padding, padding, padding);
                    // TODO: add accessibility tooltip
                    //if (title != null) button.setContentDescription(title);
                    if (visible) {
                        button.setVisibility(View.VISIBLE);
                        showBottomActionBar = true;
                    }
                    else button.setVisibility(View.GONE);

                    button.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            Fragment fragment = getSupportFragmentManager().findFragmentById(getFragmentContainerId());
                            if (fragment != null
                                && CobaltFragment.class.isAssignableFrom(fragment.getClass())) {
                                try {
                                    JSONObject data = new JSONObject();
                                    data.put(Cobalt.kJSAction, Cobalt.JSActionButtonPressed);
                                    data.put(Cobalt.kJSBarsButton, name);

                                    JSONObject message = new JSONObject();
                                    message.put(Cobalt.kJSType, Cobalt.JSTypeUI);
                                    message.put(Cobalt.kJSUIControl, Cobalt.JSControlBars);
                                    message.put(Cobalt.kJSData, data);

                                    ((CobaltFragment) fragment).sendMessage(message);
                                }
                                catch(JSONException exception) { exception.printStackTrace(); }
                            }
                            else if (Cobalt.DEBUG) Log.i(Cobalt.TAG,    TAG + " - onBarsButtonClick: no fragment container found \n"
                                                                        + " or fragment found is not an instance of CobaltFragment. \n"
                                                                        + "Drop " + name + "bars button pressed event.");
                        }
                    });

                    bottomActionBar.addView(button);
                }
                else throw new JSONException("androidPosition attribute must be top, overflow or bottom.");
            }
            catch (JSONException exception) {
                if (Cobalt.DEBUG) Log.w(Cobalt.TAG, TAG + "setupActionBar: actions " + actions.toString() + " format not supported, use at least {\n"
                                                    + "\tname: \"name\",\n"
                                                    + "\ttitle: \"title\",\n"
                                                    + "}");
                exception.printStackTrace();
            }
        }

        if (actionBar != null && actionBar.isShowing() && showBottomActionBar) bottomActionBar.setVisibility(View.VISIBLE);

        // true to display menu
        return true;
    }

	/**********************************************************************************************
	 * Back
	 **********************************************************************************************/

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

    /*********************************************************************************************************************
	 * Web Layer dismiss
	 *********************************************************************************************************************/

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

    public void popTo(String controller, String page){
        Intent intent = Cobalt.getInstance(this).getIntentForController(controller, page);
        if (intent != null) {
            Bundle bundleNewActivity = intent.getBundleExtra(Cobalt.kExtras);
            String newActivity = bundleNewActivity.getString(Cobalt.kActivity);

            ArrayList<CobaltActivity> activityToFinish = new ArrayList<CobaltActivity>();
            Boolean foundPage = false;
            int maxPageId = -1;
            int oldPageId = -1;

            for (int i = 0; i < sActivitiesArrayList.size(); i++) {
                CobaltActivity activity = sActivitiesArrayList.get(i);

                Intent intentStack = activity.getIntent();

                if (!intentStack.hasCategory(Intent.CATEGORY_LAUNCHER)) {
                    Bundle bundle = intentStack.getBundleExtra(Cobalt.kExtras);
                    String oldActivity = bundle.getString(Cobalt.kActivity);
                    if (newActivity.equals(oldActivity)) {
                        maxPageId = i;
                        String oldPage = bundle.getString(Cobalt.kPage);
                        if (oldPage.equals(page)) {
                            foundPage = true;
                            oldPageId = i;
                        }
                    }
                }
            }
            if (!foundPage) this.startActivity(intent);
            else {
                if (maxPageId == oldPageId) {
                    for (int index = 0; index <= maxPageId ; index++) {
                        activityToFinish.add(sActivitiesArrayList.get(index));
                    }
                    this.startActivity(intent);
                    for (CobaltActivity activity : activityToFinish) {
                        activity.finish();
                    }
                }
                else {
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    NavUtils.navigateUpTo(this, intent);
                }
            }
        }
        else if (Cobalt.DEBUG) Log.e(Cobalt.TAG,  TAG + " - push: Unable to push " + controller + " controller");
    }
}
