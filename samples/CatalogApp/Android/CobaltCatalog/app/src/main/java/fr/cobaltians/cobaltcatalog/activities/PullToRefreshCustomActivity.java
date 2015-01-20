package fr.cobaltians.cobaltcatalog.activities;

import fr.cobaltians.cobaltcatalog.fragments.PullToRefreshCustomFragment;

import fr.cobaltians.cobalt.fragments.CobaltFragment;

public class PullToRefreshCustomActivity extends AbstractActivity {

	protected CobaltFragment getFragment(){
		return new PullToRefreshCustomFragment();
	}
}
