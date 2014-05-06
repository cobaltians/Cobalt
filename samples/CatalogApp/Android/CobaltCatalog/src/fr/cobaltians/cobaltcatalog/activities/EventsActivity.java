package fr.cobaltians.cobaltcatalog.activities;

import fr.cobaltians.cobaltcatalog.fragments.EventsFragment;

import fr.cobaltians.cobalt.activities.CobaltActivity;
import fr.cobaltians.cobalt.fragments.CobaltFragment;

public class EventsActivity extends CobaltActivity {
	
	protected CobaltFragment getFragment(){
		return new EventsFragment();
	}

}
