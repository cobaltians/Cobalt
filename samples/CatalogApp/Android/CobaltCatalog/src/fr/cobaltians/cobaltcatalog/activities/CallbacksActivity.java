package fr.cobaltians.cobaltcatalog.activities;

import fr.cobaltians.cobaltcatalog.fragments.CallbacksFragment;

import fr.cobaltians.cobalt.fragments.CobaltFragment;

public class CallbacksActivity extends AbstractActivity {

    protected CobaltFragment getFragment() {
        return new CallbacksFragment();
    }
}
