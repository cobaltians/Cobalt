package com.example.hello.activities;

import com.example.hello.fragments.MainFragment;

import fr.cobaltians.cobalt.Cobalt;
import fr.cobaltians.cobalt.activities.CobaltActivity;
import fr.cobaltians.cobalt.fragments.CobaltFragment;

public class MainActivity extends CobaltActivity {

	protected CobaltFragment getFragment() {
		return Cobalt.getInstance(this).getFragmentForController(MainFragment.class, "default", "index.html");
	}
}
