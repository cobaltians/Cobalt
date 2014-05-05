package fr.cobaltians.cobaltcatalog.activities;

import fr.cobaltians.cobaltcatalog.fragments.ModalFragment;

import fr.cobaltians.cobalt.activities.HTMLActivity;
import fr.cobaltians.cobalt.fragments.HTMLFragment;

public class ModalActivity extends HTMLActivity {

	@Override
	protected HTMLFragment getFragment() {
		return new ModalFragment();
	}

}
