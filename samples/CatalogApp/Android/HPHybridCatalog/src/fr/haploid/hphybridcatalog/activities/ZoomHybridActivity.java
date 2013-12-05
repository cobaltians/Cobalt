package fr.haploid.hphybridcatalog.activities;

import android.view.Menu;
import fr.haploid.androidnativebridge.activities.HTMLActivity;
import fr.haploid.androidnativebridge.fragments.HTMLFragment;
import fr.haploid.hphybridcatalog.R;
import fr.haploid.hphybridcatalog.fragments.ZoomHybridFragment;

public class ZoomHybridActivity extends HTMLActivity{
	
	protected HTMLFragment getFragment(){
		return new ZoomHybridFragment();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
