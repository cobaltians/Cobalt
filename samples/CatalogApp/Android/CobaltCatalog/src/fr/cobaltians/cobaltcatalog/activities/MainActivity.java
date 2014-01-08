package fr.cobaltians.cobaltcatalog.activities;

import fr.cobaltians.cobaltcatalog.fragments.MainFragment;
import fr.cobaltians.cobaltcatalog.fragments.SimpleHybridFragment;
import fr.cobaltians.cobalt.activities.HTMLActivity;
import fr.cobaltians.cobalt.fragments.HTMLFragment;
import fr.cobaltians.cobaltcatalog.R;
import fr.cobaltians.cobaltcatalog.R.layout;
import fr.cobaltians.cobaltcatalog.R.menu;
import android.os.Bundle;
import android.view.Menu;

public class MainActivity extends HTMLActivity {

	protected HTMLFragment getFragment(){
		return new MainFragment();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
