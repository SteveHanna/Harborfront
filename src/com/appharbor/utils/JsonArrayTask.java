package com.appharbor.utils;

import org.json.JSONArray;
import android.widget.Toast;
import com.actionbarsherlock.app.SherlockFragmentActivity;

public abstract class JsonArrayTask extends JsonTask<JSONArray>  {

	private SherlockFragmentActivity _activity;
	
	public JsonArrayTask(SherlockFragmentActivity activity, String resourceName) {
		super(JSONArray.class, activity, "https://appharbor.com/applications/" + ((SessionData)activity.getApplication()).getAppSlug() + "/" + resourceName);
		_activity = activity;
	}
	
	@Override
	protected void handlePostExecute(JSONArray result) {
		if (result.length() == 0)
			Toast.makeText(_activity, "No Items Found.", Toast.LENGTH_LONG).show();
	}
}
