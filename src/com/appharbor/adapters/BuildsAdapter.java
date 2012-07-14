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
import com.appharbor.utils.DateFormatUtil;

public class BuildsAdapter extends JsonArrayAdapter{

	private Activity _activity;
	private String _deployedCommitId;
	
	public BuildsAdapter(Activity a, JSONArray builds, String commitIdOfDeployedBuild) {
		super(builds);
		_activity = a;
		_deployedCommitId = commitIdOfDeployedBuild;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		if (convertView == null){
			LayoutInflater inflater =  _activity.getLayoutInflater();
			convertView = inflater.inflate(R.layout.simple_multi_line_item, null);
		}

		try{
			JSONObject build = (JSONObject)getItem(position); 

			String message = build.getJSONObject("commit").getString("message");
			((TextView)convertView.findViewById(R.id.listItemTitle)).setText(message);

			String status = build.getString("status");
			TextView statusText = ((TextView)convertView.findViewById(R.id.listItemSub));
			statusText.setTextColor(getColorBasedOnStatus(status));
			if (build.getJSONObject("commit").getString("id").equals(_deployedCommitId))
				 status += " - Active";
			statusText.setText(status);

			if (!build.isNull("deployed"))
				((TextView)convertView.findViewById(R.id.ListItemStatus)).setText(DateFormatUtil.parseAndFormatJsonDate(build.getString("deployed")));
			else
				((TextView)convertView.findViewById(R.id.ListItemStatus)).setText("");

		}catch (Exception e) {
			Log.e("BuildsAdapter", e.getMessage());
		}

		return convertView;
	}
	
	private int getColorBasedOnStatus(String status){
		if (status.equals("Succeeded"))
			return Color.parseColor("#669900");
		else if (status.equals("Failed"))
			return Color.parseColor("#CC0000");
		else
			return Color.BLACK;
	}
}
