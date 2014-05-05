package fr.cobaltians.cobaltcatalog.activities;

import fr.cobaltians.cobaltcatalog.fragments.MainFragment;

import fr.cobaltians.cobalt.activities.HTMLActivity;
import fr.cobaltians.cobalt.fragments.HTMLFragment;

public class MainActivity extends HTMLActivity {

    protected HTMLFragment getFragment() {
        return new MainFragment();
    }
}
