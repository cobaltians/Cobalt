package fr.cobaltians.cobaltcatalog.activities;

import fr.cobaltians.cobaltcatalog.fragments.SimpleHybridFragment;

import fr.cobaltians.cobalt.Cobalt;
import fr.cobaltians.cobalt.activities.HTMLActivity;
import fr.cobaltians.cobalt.fragments.HTMLFragment;

public class SimpleHybridActivity extends HTMLActivity {
	
	protected HTMLFragment getFragment() {
		return Cobalt.getInstance(getApplicationContext()).getFragmentForController(SimpleHybridFragment.class, "default", "index.html");
	}
}
