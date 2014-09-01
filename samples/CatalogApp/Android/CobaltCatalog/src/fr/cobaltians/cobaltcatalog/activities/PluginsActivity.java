package fr.cobaltians.cobaltcatalog.activities;

import android.os.Bundle;
import fr.cobaltians.cobalt.activities.CobaltActivity;
import fr.cobaltians.cobalt.fragments.CobaltFragment;
import fr.cobaltians.cobaltcatalog.fragments.SimpleHybridFragment;

/**
 * Created by sebastien on 01/09/2014.
 */
public class PluginsActivity extends CobaltActivity {

    @Override
    protected CobaltFragment getFragment() {
        return new SimpleHybridFragment();
    }
}
