package fr.cobaltians.cobaltcatalog.activities;

import fr.cobaltians.cobaltcatalog.fragments.PullToRefreshFragment;

import fr.cobaltians.cobalt.activities.CobaltActivity;
import fr.cobaltians.cobalt.fragments.CobaltFragment;

public class PullToRefreshActivity extends CobaltActivity {

	protected CobaltFragment getFragment(){
		return new PullToRefreshFragment();
	}
}
