package fr.cobaltians.cobaltcatalog.activities;

import fr.cobaltians.cobalt.activities.HTMLActivity;
import fr.cobaltians.cobalt.fragments.HTMLFragment;
import fr.cobaltians.cobaltcatalog.fragments.ModalFragment;

public class ModalActivity extends HTMLActivity {

	@Override
	protected HTMLFragment getFragment() {
		return new ModalFragment();
	}

}
