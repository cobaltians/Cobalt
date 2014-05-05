package fr.cobaltians.cobaltcatalog.activities;

import fr.cobaltians.cobaltcatalog.fragments.PullToRefreshCustomFragment;

import fr.cobaltians.cobalt.activities.CobaltActivity;
import fr.cobaltians.cobalt.fragments.HTMLFragment;

public class PullToRefreshCustomActivity extends CobaltActivity {

	protected HTMLFragment getFragment(){
		return new PullToRefreshCustomFragment();
	}
}
