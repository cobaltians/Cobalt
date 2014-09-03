package fr.cobaltians.cobaltcatalog.activities;

import fr.cobaltians.cobaltcatalog.fragments.ModalFragment;

import fr.cobaltians.cobalt.fragments.CobaltFragment;

public class ModalActivity extends AbstractActivity {

	@Override
	protected CobaltFragment getFragment() {
		return new ModalFragment();
	}

}
