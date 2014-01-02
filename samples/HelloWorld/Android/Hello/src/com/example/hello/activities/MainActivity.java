package com.example.hello.activities;

import com.example.hello.fragments.MainFragment;

import fr.haploid.androidnativebridge.activities.HTMLActivity;
import fr.haploid.androidnativebridge.fragments.HTMLFragment;





public class MainActivity extends HTMLActivity {

	
	protected HTMLFragment getFragment(){
		
		return new MainFragment();
		
		
		
	}

}
