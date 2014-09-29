package fr.cobaltians.cobaltcatalog.activities;

import fr.cobaltians.cobalt.activities.CobaltActivity;

import android.os.Bundle;
import android.support.v7.app.ActionBar;

public abstract class AbstractActivity extends CobaltActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("");
    }
}
