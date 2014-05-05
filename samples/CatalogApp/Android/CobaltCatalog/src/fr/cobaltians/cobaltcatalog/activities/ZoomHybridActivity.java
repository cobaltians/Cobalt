package fr.cobaltians.cobaltcatalog.activities;

import fr.cobaltians.cobaltcatalog.fragments.ZoomHybridFragment;

import fr.cobaltians.cobalt.activities.HTMLActivity;
import fr.cobaltians.cobalt.fragments.HTMLFragment;

public class ZoomHybridActivity extends HTMLActivity{
	
	protected HTMLFragment getFragment() {
        return new ZoomHybridFragment();
    }
}
