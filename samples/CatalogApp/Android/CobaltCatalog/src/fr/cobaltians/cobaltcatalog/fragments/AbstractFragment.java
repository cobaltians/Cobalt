package fr.cobaltians.cobaltcatalog.fragments;

import fr.cobaltians.cobalt.Cobalt;
import fr.cobaltians.cobalt.activities.CobaltActivity;
import fr.cobaltians.cobalt.fragments.CobaltFragment;

import android.support.v7.app.ActionBar;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by sebastien on 01/09/2014.
 */
public abstract class AbstractFragment extends CobaltFragment {

    protected final static String TAG = "AbstractFragment";

    private final static String EVENT_SET_TEXTS = "setTexts";
    private final static String TITLE_KEY = "title";

    @Override
    protected boolean onUnhandledEvent(String event, JSONObject data, String callback) {
        if (event.equals(EVENT_SET_TEXTS)) {
            try {
                final String title = data.getString(TITLE_KEY);
                final ActionBar actionBar = ((CobaltActivity) mContext).getSupportActionBar();
                if (actionBar != null) {
                    ((CobaltActivity) mContext).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            actionBar.setTitle(title);
                        }
                    });
                }
                else if (Cobalt.DEBUG) Log.e(TAG, "onUnhandledEvent: setTexts event received but no action bar displayed");
            }
            catch (JSONException e) {
                if (Cobalt.DEBUG) Log.e(TAG, "onUnhandledEvent: missing title field for event setTexts");
                e.printStackTrace();
            }
            return true;
        }
        else return false;
    }
}
