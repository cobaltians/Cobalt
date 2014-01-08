package fr.cobaltians.cobaltcatalog.activities;

import android.view.Menu;
import fr.cobaltians.cobaltcatalog.fragments.PullToRefreshCustomFragment;
import fr.cobaltians.cobalt.activities.HTMLActivity;
import fr.cobaltians.cobalt.fragments.HTMLFragment;
import fr.cobaltians.cobaltcatalog.R;

public class PullToRefreshCustomActivity extends HTMLActivity {

	protected HTMLFragment getFragment(){
		return new PullToRefreshCustomFragment();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
