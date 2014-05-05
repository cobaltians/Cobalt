package fr.cobaltians.cobaltcatalog.activities;

import fr.cobaltians.cobaltcatalog.fragments.CallbacksFragment;

import fr.cobaltians.cobalt.activities.CobaltActivity;
import fr.cobaltians.cobalt.fragments.HTMLFragment;

public class CallbacksActivity extends CobaltActivity {

    protected HTMLFragment getFragment() {
        return new CallbacksFragment();
    }
}
