package fr.cobaltians.cobaltcatalog.activities;

import android.view.Menu;
import fr.cobaltians.cobalt.activities.HTMLActivity;
import fr.cobaltians.cobalt.fragments.HTMLFragment;
import fr.cobaltians.cobaltcatalog.R;
import fr.cobaltians.cobaltcatalog.fragments.SimpleHybridFragment;

public class SimpleHybridActivity extends HTMLActivity {
	
	protected HTMLFragment getFragment() {
		return HTMLFragment.getFragmentForController(getApplicationContext(), SimpleHybridFragment.class, "default", "index.html");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
