package com.example.hello.activities;

import com.example.hello.fragments.MainFragment;

import fr.cobaltians.cobalt.Cobalt;
import fr.cobaltians.cobalt.activities.CobaltActivity;
import fr.cobaltians.cobalt.fragments.HTMLFragment;

public class MainActivity extends CobaltActivity {

	protected HTMLFragment getFragment() {
		return Cobalt.getInstance(getApplicationContext()).getFragmentForController(MainFragment.class, "default", "index.html");
	}
}
