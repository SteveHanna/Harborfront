package com.appharbor.adapters;

import org.json.JSONArray;
import org.json.JSONObject;
import android.app.Activity;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.appharbor.R;

public class TestsAdapter extends JsonArrayAdapter{

	private Activity _activity;
	
	public TestsAdapter(Activity a, JSONArray tests) {
		super(tests);
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
			JSONObject test = (JSONObject) getItem(position); 

			((TextView)convertView.findViewById(R.id.listItemTitle)).setText(test.getString("name"));

			String status = test.getString("status");
			TextView statusText = ((TextView)convertView.findViewById(R.id.listItemSub));
			statusText.setText(status);

			if (status.equals("Passed"))
				statusText.setTextColor(Color.parseColor("#669900"));
			else if (status.equals("Failed"))
				statusText.setTextColor(Color.parseColor("#CC0000"));

		}catch (Exception e) {
			Log.e("TestsAdapter", e.getMessage());
		}

		return convertView;
	}
}