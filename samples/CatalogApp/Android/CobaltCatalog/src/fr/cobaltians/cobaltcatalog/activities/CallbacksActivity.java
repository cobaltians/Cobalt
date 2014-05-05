package fr.cobaltians.cobaltcatalog.activities;

import fr.cobaltians.cobaltcatalog.fragments.CallbacksFragment;

import fr.cobaltians.cobalt.activities.HTMLActivity;
import fr.cobaltians.cobalt.fragments.HTMLFragment;

public class CallbacksActivity extends HTMLActivity {

    protected HTMLFragment getFragment() {
        return new CallbacksFragment();
    }
}
