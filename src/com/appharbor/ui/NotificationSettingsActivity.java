package com.appharbor.ui;

import org.json.JSONArray;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.CheckBox;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.ActionMode;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.Window;
import com.appharbor.R;
import com.appharbor.utils.JsonArrayTask;
import com.appharbor.utils.RestClient;
import com.appharbor.utils.SessionData;
import com.appharbor.utils.RestClient.RequestMethod;
import com.google.android.gcm.GCMRegistrar;

public class NotificationSettingsActivity extends SherlockFragmentActivity{
	private ActionMode mMode;
	private JSONArray _serviceHooks;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.notification_settings);

		if (GCMRegistrar.getRegistrationId(this).equals("")){
			Toast.makeText(this, "There was a problem registering your device with Google Cloud Messaging.", Toast.LENGTH_LONG).show();
			finish();	
		}
		else
			new ServiceHooksTask().execute();
	}

	private String getUrl(){
		return getString(R.string.url_build_notification) + "?key=" + GCMRegistrar.getRegistrationId(this);
	}

	private String doesServiceHookExist(){

		try{
			for (int i = 0; i < _serviceHooks.length(); i++) {
				if (_serviceHooks.getJSONObject(i).getString("value").equals(getUrl()))
					return _serviceHooks.getJSONObject(i).getString("url");
			}	
			return null;
		} catch (Exception e) {
			return null;
		}
	}

	private class ServiceHooksTask extends JsonArrayTask{

		public ServiceHooksTask() {
			super(NotificationSettingsActivity.this, "servicehooks");
		}

		@Override
		protected void onPostExecute(JSONArray result) {
			_serviceHooks = result;
			((CheckBox)findViewById(R.id.enableNotification)).setChecked(doesServiceHookExist() != null);
			mMode = startActionMode(new AnActionModeOfEpicProportions());
			setSupportProgressBarIndeterminateVisibility(false);
		}
	}

	private final class AnActionModeOfEpicProportions implements ActionMode.Callback {

		public boolean onCreateActionMode(ActionMode mode, Menu menu) {

			menu.add("Done")
			.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

			return true;
		}

		public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
			return false;
		}

		public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
			mMode.getMenu().clear();
			mMode.setSubtitle("loading...");
			setSupportProgressBarIndeterminateVisibility(true);
			new RegistrationTask().execute();
			return true;
		}

		public void onDestroyActionMode(ActionMode mode) { 
			finish();
		}
	}

	//At this point we are only concerned with registering for AppHarbor notifications. 
	private class RegistrationTask extends AsyncTask<String, Void, Boolean>{

		@Override
		protected Boolean doInBackground(String... params) {

			String serviceHookUrl = doesServiceHookExist();
			boolean isRegisteredInAH = serviceHookUrl != null;

			SessionData app = (SessionData)getApplication();

			if(isRegisteredInAH && !((CheckBox)findViewById(R.id.enableNotification)).isChecked()){

				RestClient client = new RestClient(serviceHookUrl, app.getToken());

				try {
					client.Execute(RequestMethod.DELETE);
					if (client.getResponseCode() != 204)
						throw new Exception(client.getErrorMessage());
					else
						return true;
				} catch (Exception e) {
					Log.e("error", e.getMessage());
					return false;
				}
			}else if (!isRegisteredInAH && ((CheckBox)findViewById(R.id.enableNotification)).isChecked()){

				RestClient client = new RestClient("https://appharbor.com/applications/" + app.getAppSlug() + "/servicehooks", app.getToken());
				client.AddParam("url", getUrl());

				try {
					client.Execute(RequestMethod.POST);
					if (client.getResponseCode() != 201)
						throw new Exception(client.getErrorMessage());
					else
						return true;
				} catch (Exception e) {
					Log.e("error", e.getMessage());
					return false;
				}
			} else 
				return false;
		}

		@Override
		protected void onPostExecute(Boolean shouldRegisterOrUnregister) {
			if (shouldRegisterOrUnregister){

			}

			setSupportProgressBarIndeterminateVisibility(false);
			mMode.finish();
		}
	}
}
