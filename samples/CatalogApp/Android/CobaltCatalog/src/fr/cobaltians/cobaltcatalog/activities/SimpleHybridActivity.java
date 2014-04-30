package fr.cobaltians.cobaltcatalog.activities;

import fr.cobaltians.cobaltcatalog.R;
import fr.cobaltians.cobaltcatalog.fragments.SimpleHybridFragment;

import fr.cobaltians.cobalt.Cobalt;
import fr.cobaltians.cobalt.activities.HTMLActivity;
import fr.cobaltians.cobalt.fragments.HTMLFragment;

import android.view.Menu;

public class SimpleHybridActivity extends HTMLActivity {
	
	protected HTMLFragment getFragment() {
		return Cobalt.getInstance(getApplicationContext()).getFragmentForController(SimpleHybridFragment.class, "default", "index.html");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
