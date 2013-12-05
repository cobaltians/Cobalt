package fr.haploid.hphybridcatalog.activities;

import fr.haploid.androidnativebridge.activities.HTMLActivity;
import fr.haploid.androidnativebridge.fragments.HTMLFragment;
import fr.haploid.hphybridcatalog.R;
import fr.haploid.hphybridcatalog.R.layout;
import fr.haploid.hphybridcatalog.R.menu;
import fr.haploid.hphybridcatalog.fragments.MainFragment;
import fr.haploid.hphybridcatalog.fragments.SimpleHybridFragment;
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
