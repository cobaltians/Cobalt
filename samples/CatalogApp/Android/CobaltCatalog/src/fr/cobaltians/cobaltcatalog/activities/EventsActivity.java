package fr.cobaltians.cobaltcatalog.activities;

import fr.cobaltians.cobaltcatalog.fragments.EventsFragment;
import fr.cobaltians.cobalt.activities.HTMLActivity;
import fr.cobaltians.cobalt.fragments.HTMLFragment;

public class EventsActivity extends HTMLActivity{
	
	protected HTMLFragment getFragment(){
		return new EventsFragment();
	}

}
