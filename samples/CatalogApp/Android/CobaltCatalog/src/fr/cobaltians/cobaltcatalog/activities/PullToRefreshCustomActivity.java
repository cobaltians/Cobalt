package fr.cobaltians.cobaltcatalog.activities;

import fr.cobaltians.cobaltcatalog.fragments.PullToRefreshCustomFragment;

import fr.cobaltians.cobalt.activities.HTMLActivity;
import fr.cobaltians.cobalt.fragments.HTMLFragment;

public class PullToRefreshCustomActivity extends HTMLActivity {

	protected HTMLFragment getFragment(){
		return new PullToRefreshCustomFragment();
	}
}
