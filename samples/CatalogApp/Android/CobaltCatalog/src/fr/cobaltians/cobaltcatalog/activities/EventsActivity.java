package fr.cobaltians.cobaltcatalog.activities;

import fr.cobaltians.cobaltcatalog.fragments.EventsFragment;

import fr.cobaltians.cobalt.activities.CobaltActivity;
import fr.cobaltians.cobalt.fragments.HTMLFragment;

public class EventsActivity extends CobaltActivity {
	
	protected HTMLFragment getFragment(){
		return new EventsFragment();
	}

}
