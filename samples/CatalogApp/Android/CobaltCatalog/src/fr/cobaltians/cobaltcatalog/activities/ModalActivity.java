package fr.cobaltians.cobaltcatalog.activities;

import fr.cobaltians.cobaltcatalog.fragments.ModalFragment;

import fr.cobaltians.cobalt.activities.CobaltActivity;
import fr.cobaltians.cobalt.fragments.HTMLFragment;

public class ModalActivity extends CobaltActivity {

	@Override
	protected HTMLFragment getFragment() {
		return new ModalFragment();
	}

}
