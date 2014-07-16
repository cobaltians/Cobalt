package fr.cobaltians.cobalt.plugin;

import android.content.Context;
import android.util.Log;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by sebastienfamel on 16/07/2014.
 */
public class CobaltPlugin extends CobaltAbstractPlugin {
    /**
     * *****************************************************
     * CONSTRUCTORS
     * *****************************************************
     *
     * @param context
     */
    public CobaltPlugin(Context context) {
        super(context);
    }

    @Override
    public boolean onMessage(JSONObject data) {
        try {
            Log.d(TAG, "data received on AbstractPlugin : " + data.toString());

            String name = data.getString("name");
            if (name.equals("geoloc")) {
                Log.d(TAG, "open an instance of geoloc");
                return true;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return false;
    }
}
