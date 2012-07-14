package com.appharbor.adapters;

import org.json.JSONArray;
import org.json.JSONObject;
import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.appharbor.R;

public class ConfigVariablesAdapter extends JsonArrayAdapter{

	private Activity _activity;
	
	public ConfigVariablesAdapter(Activity a, JSONArray configVariables) {
		super(configVariables);
		_activity = a;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		if (convertView == null){
			LayoutInflater inflater =  _activity.getLayoutInflater();
			convertView = inflater.inflate(R.layout.simple_multi_line_item, null);
			convertView.findViewById(R.id.ListItemStatus).setVisibility(View.GONE);
		}

		try{
			JSONObject config = (JSONObject) getItem(position); 

			((TextView)convertView.findViewById(R.id.listItemTitle)).setText(config.getString("key"));
			((TextView)convertView.findViewById(R.id.listItemSub)).setText(config.getString("value"));
			
		}catch (Exception e) {
			Log.e("ConfigVariablesAdapter", e.getMessage());
		}

		return convertView;
	}
}