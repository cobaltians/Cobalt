package fr.cobaltians.cobaltcatalog.activities;

import fr.cobaltians.cobaltcatalog.fragments.PullToRefreshFragment;

import fr.cobaltians.cobalt.activities.HTMLActivity;
import fr.cobaltians.cobalt.fragments.HTMLFragment;

public class PullToRefreshActivity extends HTMLActivity {

	protected HTMLFragment getFragment(){
		return new PullToRefreshFragment();
	}
}
