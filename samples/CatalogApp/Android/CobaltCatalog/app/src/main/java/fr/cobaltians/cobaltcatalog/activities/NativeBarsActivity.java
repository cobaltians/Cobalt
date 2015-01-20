package fr.cobaltians.cobaltcatalog.activities;

import fr.cobaltians.cobaltcatalog.fragments.NativeBarsFragment;

import fr.cobaltians.cobalt.activities.CobaltActivity;
import fr.cobaltians.cobalt.fragments.CobaltFragment;

/**
 * Created by sebastien on 13/10/2014.
 */
public class NativeBarsActivity extends CobaltActivity {
    @Override
    protected CobaltFragment getFragment() {
        return new NativeBarsFragment();
    }
}
