package com.appharbor.utils;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.appharbor.utils.RestClient.RequestMethod;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

public abstract class JsonTask<T> extends AsyncTask<String, Void, T> {

	private Class<T> _typeClass;
	private String _url;
	private SherlockFragmentActivity _activity;
	private String _error;
	
	public JsonTask(Class<T> typeClass, SherlockFragmentActivity activity, String url){
		_typeClass = typeClass;
		_url = url;
		_activity = activity;
	}
	
	@Override
	protected T doInBackground(String... params) {	 

		SessionData application = (SessionData)_activity.getApplication();

		RestClient client = new RestClient(_url);
		client.AddHeader("Authorization", "BEARER " + application.getToken());
		
		try {
			client.Execute(RequestMethod.GET);
			if (client.getResponseCode() >= 400){
				_error = client.getErrorMessage();
				return null;
			}
			
			return _typeClass.getConstructor(String.class).newInstance(client.getResponse());

		} catch (Exception e) {
			_error = e.getMessage();
			return null;
		}
	}
	
	@Override
	protected void onPostExecute(final T result) {

		if (result == null ){
			Log.e("JsonArrayTask", _error);
			Toast.makeText(_activity, _error, Toast.LENGTH_LONG).show();
		} else
			handlePostExecute(result);			
				
		_activity.setSupportProgressBarIndeterminateVisibility(false);	
	}
	
	protected abstract void handlePostExecute(T result);
}