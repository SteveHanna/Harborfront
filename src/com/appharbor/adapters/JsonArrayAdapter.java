package com.appharbor.adapters;

import org.json.JSONArray;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public abstract class JsonArrayAdapter extends BaseAdapter{

	private JSONArray _array;
	
	public JsonArrayAdapter(JSONArray array){
		_array = array;
	}
	
	public int getCount() {
		return _array.length();
	}

	public Object getItem(int position) {
		
		try{
			return _array.get(position);
		}catch (Exception e) {
			Log.e("JsonArrayAdapter", e.getMessage());
			return null;
		}
	}

	public long getItemId(int position){
		return position;
	}

	public abstract View getView(int position, View arg1, ViewGroup arg2);
}
