package com.appharbor.utils;

import com.appharbor.ui.DashboardActivity;

import android.app.Application;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class SessionData extends Application{

	private static String _token;
	private static String _appSlug;
	
	public String getToken() {
		
		if (_token == null){
			SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
			_token = preferences.getString("token", null);
			
			if (_token == null){
				Intent i = new Intent(this, DashboardActivity.class);
				i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(i);
			}
		}
		
		return _token;
	}
	
	public void setToken(String token) {
		_token = token;
	}

	public String getAppSlug() {
		
		if (_appSlug == null)
			startActivity(new Intent(this, DashboardActivity.class));
			
		return _appSlug;
	}

	public void setAppSlug(String appSlug) {
		_appSlug = appSlug;
	}
}
