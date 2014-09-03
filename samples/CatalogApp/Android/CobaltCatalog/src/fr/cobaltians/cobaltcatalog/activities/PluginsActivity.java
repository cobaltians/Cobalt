package fr.cobaltians.cobaltcatalog.activities;

import fr.cobaltians.cobaltcatalog.fragments.SimpleHybridFragment;

import fr.cobaltians.cobalt.fragments.CobaltFragment;


public class PluginsActivity extends AbstractActivity {

    @Override
    protected CobaltFragment getFragment() {
        return new SimpleHybridFragment();
    }
}
