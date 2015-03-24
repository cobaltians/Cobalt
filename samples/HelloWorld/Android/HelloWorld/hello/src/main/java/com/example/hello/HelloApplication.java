package com.example.hello;

import android.app.Application;
import fr.cobaltians.cobalt.Cobalt;

/**
 * Created by sebastien on 29/07/2014.
 */
public class HelloApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Cobalt.getInstance(this).setResourcePath("www/common/");
    }
}
