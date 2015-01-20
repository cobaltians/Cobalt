package fr.cobaltians.cobaltcatalog;

import fr.cobaltians.cobalt.Cobalt;

import android.app.Application;

/**
 * Created by sebastien on 07/05/2014.
 */
public class CobaltCatalogApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Cobalt.DEBUG = true;
        Cobalt.getInstance(this).setResourcePath("www/common/");
    }
}
