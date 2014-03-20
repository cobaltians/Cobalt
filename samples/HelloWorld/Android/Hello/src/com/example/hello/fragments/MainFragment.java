package com.example.hello.fragments;

import org.json.JSONObject;

import com.example.hello.R;

import fr.cobaltians.cobalt.fragments.HTMLFragment;

public class MainFragment extends HTMLFragment {
	
	@Override
	protected int getLayoutToInflate() {
		return R.layout.activity_main;
	}

	@Override
	protected boolean onUnhandledCallback(String callback, JSONObject data) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected boolean onUnhandledEvent(String event, JSONObject data, String callback) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected void onUnhandledMessage(JSONObject message) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void onPullToRefreshRefreshed() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void onInfiniteScrollRefreshed() {
		// TODO Auto-generated method stub
		
	}
}

