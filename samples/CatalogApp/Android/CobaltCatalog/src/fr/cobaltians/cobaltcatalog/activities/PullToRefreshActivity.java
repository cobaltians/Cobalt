package fr.cobaltians.cobaltcatalog.activities;

import fr.cobaltians.cobaltcatalog.fragments.PullToRefreshFragment;

import fr.cobaltians.cobalt.activities.CobaltActivity;
import fr.cobaltians.cobalt.fragments.HTMLFragment;

public class PullToRefreshActivity extends CobaltActivity {

	protected HTMLFragment getFragment(){
		return new PullToRefreshFragment();
	}
}
