package com.appharbor.ui;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.ActionMode;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.appharbor.R;
import com.appharbor.utils.RestClient;
import com.appharbor.utils.SessionData;
import com.appharbor.utils.RestClient.RequestMethod;


public class NewApplicationActivity extends SherlockFragmentActivity{
	private ActionMode mMode;
	private String _appSlug;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.new_application);

		String appName = getIntent().getStringExtra("appName");
		_appSlug = getIntent().getStringExtra("appSlug");

		mMode = startActionMode(new AnActionModeOfEpicProportions());

		if (appName == null){
			mMode.setTitle("New Application");
			initRegionSpinner();
		}else {
			((EditText) findViewById(R.id.appName)).setText(appName);
			mMode.setTitle("Edit Application");
		}
	}

	private void initRegionSpinner(){

		findViewById(R.id.regionLabel).setVisibility(View.VISIBLE);

		Spinner spinner = (Spinner) findViewById(R.id.regions);
		spinner.setVisibility(View.VISIBLE);

		String[] regions = new String[2];
		regions[0] = "amazon-web-services::us-east-1";
		regions[1] = "amazon-web-services::eu-west-1";

		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, regions);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(adapter);
	}

	private final class AnActionModeOfEpicProportions implements ActionMode.Callback {

		public boolean onCreateActionMode(ActionMode mode, Menu menu) {
			menu.add("Done").setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
			return true;
		}

		public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
			return false;
		}

		public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
			
			String appName =((TextView)findViewById(R.id.appName)).getText().toString();
			String region = (String)((Spinner)findViewById(R.id.regions)).getSelectedItem();
					
			new NewApplicationTask().execute(appName, region);
			return true;
		}

		public void onDestroyActionMode(ActionMode mode) { 
			finish();
		}
	}

	private final class NewApplicationTask extends AsyncTask<String, Void, Void> {

		private String _error;

		@Override
		protected Void doInBackground(String... params) {	 

			SessionData application = (SessionData)getApplication();

			String url = "https://appharbor.com/applications";
			if (_appSlug != null)
				url += "/" + _appSlug;
						
			RestClient client = new RestClient(url, application.getToken());
			client.AddParam("name", params[0]);
			
			if (_appSlug == null)
				client.AddParam("region_identifier", params[1]);
			
			try {
				client.Execute( _appSlug == null? RequestMethod.POST: RequestMethod.PUT);
				if (client.getResponseCode() > 204){
					_error = client.getErrorMessage();
				}
			} catch (Exception e) {
				_error = e.getMessage();
			}

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {

			if (_error != null ){
				Log.e("JsonArrayTask", _error);
				Toast.makeText(NewApplicationActivity.this, _error, Toast.LENGTH_LONG).show();
			}
			
			setResult(2);
			mMode.finish();
		}
	}
}