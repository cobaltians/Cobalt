package fr.cobaltians.cobaltcatalog.activities;

import fr.cobaltians.cobaltcatalog.fragments.SimpleHybridFragment;

import fr.cobaltians.cobalt.Cobalt;
import fr.cobaltians.cobalt.activities.CobaltActivity;
import fr.cobaltians.cobalt.fragments.CobaltFragment;

public class SimpleHybridActivity extends CobaltActivity {
	
	protected CobaltFragment getFragment() {
		return Cobalt.getInstance(this).getFragmentForController(SimpleHybridFragment.class, "default", "index.html");
	}
}
