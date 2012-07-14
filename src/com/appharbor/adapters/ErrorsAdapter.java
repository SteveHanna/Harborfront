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
import com.appharbor.utils.DateFormatUtil;

public class ErrorsAdapter extends JsonArrayAdapter{

	private Activity _activity; 
	public ErrorsAdapter(Activity a, JSONArray errors) {
		super(errors);
		_activity = a;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		if (convertView == null){
			LayoutInflater inflater = _activity.getLayoutInflater();
			convertView = inflater.inflate(R.layout.simple_multi_line_item, null);
		}

		try{
			JSONObject error =  (JSONObject) getItem(position); 

			((TextView)convertView.findViewById(R.id.listItemTitle)).setText(error.getJSONObject("exception").getString("message"));
			((TextView)convertView.findViewById(R.id.listItemSub)).setText(error.getString("request_path"));
			((TextView)convertView.findViewById(R.id.ListItemStatus)).setText(DateFormatUtil.parseAndFormatJsonDate(error.getString("date")));
			
		}catch (Exception e) {
			Log.e("ErrorsAdapter", e.getMessage());
		}

		return convertView;
	}
}