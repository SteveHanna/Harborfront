package com.appharbor.utils;

import java.util.ArrayList;

import org.json.JSONArray;

import android.util.Log;

public class ArrayUtils {

	public static ArrayList<String> select(JSONArray array, String accessorName) {
		
		ArrayList<String> apps = new ArrayList<String>();
		try{
			for (int i = 0; i < array.length(); i++) {
				apps.add(array.getJSONObject(i).getString(accessorName));
			}
		}catch (Exception e) {
			Log.e("ArrayUtils", e.getMessage());
		}
		
		return apps;
	}
}
