package com.appharbor.adapters;

import org.json.JSONArray;
import org.json.JSONObject;
import com.appharbor.R;
import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class CollaboratorsAdapter extends JsonArrayAdapter{

	private Activity _activity;
	
	public CollaboratorsAdapter(Activity a, JSONArray collaborators) {
		super(collaborators);
		_activity = a;
	}

	@Override
	public View getView(int position, View row, ViewGroup parent) {

		if (row == null){
			LayoutInflater inflater =  _activity.getLayoutInflater();
			row = inflater.inflate(R.layout.simple_multi_line_item, null);
			row.findViewById(R.id.ListItemStatus).setVisibility(View.GONE);
		}

		try{
			JSONObject collaborator = (JSONObject)getItem(position); 

			String name = collaborator.getJSONObject("user").getString("name");
			((TextView)row.findViewById(R.id.listItemTitle)).setText(name);

			((TextView)row.findViewById(R.id.listItemSub)).setText(collaborator.getString("role"));

		}catch (Exception e) {
			Log.e("CollaboratorsAdapter", e.getMessage());
		}

		return row;
	}
}