package fr.cobaltians.cobalt.plugin;

import android.content.Context;
import android.util.Log;
import fr.cobaltians.cobalt.fragments.CobaltFragment;
import junit.framework.Assert;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by sebastienfamel on 15/07/2014.
 */
public abstract class CobaltAbstractPlugin {

    // TAG
    public static final String TAG = CobaltAbstractPlugin.class.getSimpleName();

    /********************************************************
     * MEMBERS
     *******************************************************/

    private static CobaltPlugin sInstance;
    private final Context mContext;
    private CobaltFragment mListener;

    /********************************************************
     * CONSTRUCTORS
     *******************************************************/

    public CobaltAbstractPlugin(Context context) {
        mContext = context.getApplicationContext();
    }

    public static CobaltPlugin getInstance(Context context) {
        if (sInstance == null) {
            Assert.assertNotNull(TAG + " - getInstance : context could not be null", context);
            sInstance = new CobaltPlugin(context);
        }
        return sInstance;
    }

    public abstract boolean onMessage(JSONObject data);
}
