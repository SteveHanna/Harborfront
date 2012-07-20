package com.appharbor.ui;

import java.util.ArrayList;

import org.json.JSONArray;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.OnNavigationListener;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.SubMenu;
import com.actionbarsherlock.view.Window;
import com.appharbor.R;
import com.appharbor.utils.*;
import com.appharbor.utils.RestClient.RequestMethod;
import com.google.android.gcm.GCMRegistrar;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

public class DashboardActivity extends SherlockFragmentActivity implements IConfirmationDialogHandler, MenuItem.OnMenuItemClickListener {

	private static final String tag = "DashboardActivity";
	private String _appName;
	private String _appSlug;
	private String _token;

	private AsyncTask<Void, Void, Void> mRegisterTask;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.main);

		try{
			GCMRegistrar.checkDevice(this);
			GCMRegistrar.checkManifest(this);
			//registerReceiver(mHandleMessageReceiver, new IntentFilter("com.appharbor.DISPLAY_MESSAGE"));
			
			final String regId = GCMRegistrar.getRegistrationId(this);
			if (regId.equals("")) {
				GCMRegistrar.register(this, SensitiveStrings.SENDER_ID);
				loadPage();
			}
			else{

				if (GCMRegistrar.isRegisteredOnServer(this)){
					loadPage();
				}
				else{
					final Context context = this;
					mRegisterTask = new AsyncTask<Void, Void, Void>() {

						@Override
						protected Void doInBackground(Void... params) {
							boolean registered = ServerUtilities.register(context, regId);

							if (!registered) {
								GCMRegistrar.unregister(context);
							}
							return null;
						}

						@Override
						protected void onPostExecute(Void result) {
							mRegisterTask = null;
							loadPage();
						}

					};
					mRegisterTask.execute(null, null, null);
				}
			}
		} catch (Exception e) {
			Toast.makeText(this, "There was a problem registering your device on GCM.", Toast.LENGTH_LONG).show();
			loadPage();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		SubMenu subMenu = menu.addSubMenu("Action Item");
		subMenu.add(Menu.NONE, 1, 0, "Add Application").setOnMenuItemClickListener(this);
		subMenu.add(Menu.NONE, 2, 1, "Edit Application").setOnMenuItemClickListener(this);
		//subMenu.add(Menu.NONE, 3, 2, "Remove Application").setOnMenuItemClickListener(this);
		subMenu.add(Menu.NONE, 4, 3, "Notification Settings").setOnMenuItemClickListener(this);

		MenuItem item = subMenu.getItem();
		item.setTitle("Options");
		item.setIcon(R.drawable.ic_action_arrow_down);
		item.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

		return true;
	}

	private void loadPage(){

		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
		_token = preferences.getString("token", null);

		if (_token == null) //if we can't find the token, go to the login page and get it
			startActivityForResult(new Intent(this, LoginActivity.class), 2);
		else{
			((SessionData)getApplication()).setToken(_token);
			new DashboardTask().execute(_token);
		}
	}

	public void onDashButtonClick(View v){

		switch (v.getId()) {
		case R.id.home_btn_builds:
			startActivity(new Intent(this, BuildsActivity.class));
			break;
		case R.id.home_btn_collaborators:
			startActivity(new Intent(this, CollaboratorsActivity.class));
			break;
		case R.id.home_btn_config_variables:
			startActivity(new Intent(this, ConfigVariablesActivity.class));
			break;
		case R.id.home_btn_errors:
			startActivity(new Intent(this, ErrorsActivity.class));
			break;
		case R.id.home_btn_hostnames:
			startActivity(new Intent(this, HostnamesActivity.class));
			break;
		case R.id.home_btn_service_hooks:
			startActivity(new Intent(this, ServiceHooksActivity.class));
			break;

		default:
			Log.e(tag, "Button was clicked with no where to go.");
			return;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (resultCode == 2)
			loadPage();
	}

	private class DashboardTask extends AsyncTask<String, Void, JSONArray>{

		@Override
		protected JSONArray doInBackground(String... params) {	 

			RestClient client = new RestClient("https://appharbor.com/applications", params[0]);

			try {
				client.Execute(RequestMethod.GET);
				return new JSONArray(client.getResponse());
			} catch (Exception e) {
				Log.e(tag, e.getMessage());
			}

			return null;
		}

		@Override
		protected void onPostExecute(final JSONArray result) {

			try{
				ActionBar aBar = getSupportActionBar();

				ArrayList<String> appNames = ArrayUtils.select(result, "name");
				ArrayAdapter<String> adapter = new ArrayAdapter<String>(aBar.getThemedContext(), R.layout.sherlock_spinner_item, appNames);

				aBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
				aBar.setDisplayShowTitleEnabled(false);

				aBar.setListNavigationCallbacks(adapter, new OnNavigationListener() {
					public boolean onNavigationItemSelected(int itemPosition, long itemId) {

						try {
							_appSlug = result.getJSONObject(itemPosition).getString("slug");
							((SessionData)getApplication()).setAppSlug(_appSlug);

							_appName = result.getJSONObject(itemPosition).getString("name");
						} catch (Exception e) {
							Log.e(tag, e.getMessage());
						}

						return true;
					}
				});

				findViewById(R.id.dashboard).setVisibility(View.VISIBLE);

			} catch (Exception e) {
				Log.e(tag, e.getMessage());
			} finally{
				setSupportProgressBarIndeterminateVisibility(false);
			}
		}
	}

	public void doPositiveClick() {
		setSupportProgressBarIndeterminateVisibility(true);
		new DeleteAppTask().execute(_appSlug);
	}

	public boolean onMenuItemClick(MenuItem item) {

		switch (item.getItemId()) {
		case 1:
			startActivityForResult(new Intent(DashboardActivity.this, NewApplicationActivity.class), 2);
			break;
		case 2:
			Intent i = new Intent(DashboardActivity.this, NewApplicationActivity.class);
			i.putExtra("appName", _appName);
			i.putExtra("appSlug", _appSlug);
			startActivityForResult(i, 2);
			break;
		case 3:
			DialogFragment newFragment = ConfirmationDialogFragment.newInstance("Title", "Are you sure you want to remove this application?");
			newFragment.show(getSupportFragmentManager(), "dialog");
			break;
		case 4:
			startActivity(new Intent(DashboardActivity.this, NotificationSettingsActivity.class));
			break;
		default:
			return false;
		}

		return true;
	}

	private class DeleteAppTask extends AsyncTask<String, Void, Void>{

		@Override
		protected Void doInBackground(String... params) {

			RestClient client = new RestClient("https://appharbor.com/applications/" + params[0], ((SessionData)getApplication()).getToken());

			try {
				client.Execute(RequestMethod.DELETE);
			} catch (Exception e) {
				Log.e(tag, e.getMessage());
			}

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			loadPage();
		}
	}
	//	
	//	private final BroadcastReceiver mHandleMessageReceiver = new BroadcastReceiver() {
	//		@Override
	//		public void onReceive(Context context, Intent intent) {
	//			String newMessage = intent.getExtras().getString("message");
	//			Log.d(tag, newMessage);
	//		}
	//	};
	//	

	@Override
	protected void onDestroy() {
		if (mRegisterTask != null) {
			mRegisterTask.cancel(true);
		}

		//unregisterReceiver(mHandleMessageReceiver);
		GCMRegistrar.onDestroy(this);
		super.onDestroy();
	}
}