package fr.cobaltians.cobaltcatalog.activities;

import fr.cobaltians.cobaltcatalog.fragments.ToastAlertFragment;

import fr.cobaltians.cobalt.activities.HTMLActivity;
import fr.cobaltians.cobalt.fragments.HTMLFragment;

public class ToastAlertActivity extends HTMLActivity {

	protected HTMLFragment getFragment(){
		return new ToastAlertFragment();
	}
}
