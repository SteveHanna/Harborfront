package com.appharbor.ui;

import org.json.JSONArray;
import org.json.JSONObject;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.Tab;
import com.actionbarsherlock.app.ActionBar.TabListener;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.MenuItem.OnMenuItemClickListener;
import com.actionbarsherlock.view.Window;
import com.appharbor.R;
import com.appharbor.adapters.TestsAdapter;
import com.appharbor.utils.ConfirmationDialogFragment;
import com.appharbor.utils.DateFormatUtil;
import com.appharbor.utils.IConfirmationDialogHandler;
import com.appharbor.utils.RestClient;
import com.appharbor.utils.SessionData;
import com.appharbor.utils.RestClient.RequestMethod;

public class BuildDetailsActivity extends BaseActivity implements IConfirmationDialogHandler{
	
	private static final String tag = "BuildDetailsActivity";
	private String _buildUrl;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.build);
		
		ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		
        ActionBar.Tab tab = actionBar.newTab();
        tab.setText("Build Details");
        tab.setTabListener(listener);
        actionBar.addTab(tab);
        		
        ActionBar.Tab tab2 = actionBar.newTab();
        tab2.setText("Tests");
        tab2.setTabListener(listener);
        actionBar.addTab(tab2);
        
		Intent intent = getIntent();
		_buildUrl = intent.getStringExtra("buildUrl");
		new BuildDetailsTask().execute(((SessionData)getApplication()).getToken(), _buildUrl);
	}

	@Override
    public boolean onCreateOptionsMenu(Menu menu) {

        menu.add("Deploy")
        	.setOnMenuItemClickListener(new OnMenuItemClickListener() {
				public boolean onMenuItemClick(MenuItem item) {
					ConfirmationDialogFragment.newInstance("Deploy", "Are you sure you want to deploy this build?").show(getSupportFragmentManager(), tag);
					return true;
				}
			})
            .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
        return true;
    }
	
	public void doPositiveClick() {
		setSupportProgressBarIndeterminateVisibility(true);
		new DeployBuildTask().execute(((SessionData)getApplication()).getToken(), _buildUrl);
	}
	
	private class DeployBuildTask extends AsyncTask<String, Void, Void>{

		private String _error; 
		
		@Override
		protected Void doInBackground(String... params) {
			RestClient client = new RestClient(params[1] + "/deploy");
			client.AddHeader("Authorization", "BEARER " + params[0]);
		
			try {
				client.Execute(RequestMethod.POST);
				
				if (client.getResponseCode() != 200)
					_error = client.getErrorMessage();
				
			} catch (Exception e) {
				Log.e(tag, e.getMessage());
				_error = e.getMessage();
			}

			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			
			if (_error != null)
				Toast.makeText(BuildDetailsActivity.this, "error: " + _error, Toast.LENGTH_LONG).show();
			else 
				Toast.makeText(BuildDetailsActivity.this, "This build was successfully queued for deployment.", Toast.LENGTH_LONG).show();
			
			setSupportProgressBarIndeterminateVisibility(false);
		}
	}
	
	private TabListener listener = new TabListener() {
		
		public void onTabUnselected(Tab tab, FragmentTransaction ft) { }
		
		public void onTabSelected(Tab tab, FragmentTransaction ft) {

			if (tab.getText().equals("Tests")){
				ListView view = (ListView) findViewById(R.id.testsListView); //.setVisibility(View.GONE);
				view.setVisibility(View.VISIBLE);
				if (view.getCount() == 0)
					Toast.makeText(BuildDetailsActivity.this, "No Tests Found.", Toast.LENGTH_LONG).show();
				
				findViewById(R.id.testsLayout).setVisibility(View.VISIBLE);
				findViewById(R.id.buildDetailsLayout).setVisibility(View.GONE);
			} else{
				
				findViewById(R.id.testsLayout).setVisibility(View.GONE);
				findViewById(R.id.buildDetailsLayout).setVisibility(View.VISIBLE);
			}
		}
		
		public void onTabReselected(Tab tab, FragmentTransaction ft) { }
	};
	
	private class BuildDetailsTask extends AsyncTask<String, Void, JSONObject> {
		@Override
		protected JSONObject doInBackground(String... params) {	 

			RestClient client = new RestClient(params[1]);
			client.AddParam("access_token", params[0]);

			try {
				client.Execute(RequestMethod.GET);
				return new JSONObject(client.getResponse());

			} catch (Exception e) {
				Log.e(tag, e.getMessage());
			}

			return null;
		}

		@Override
		protected void onPostExecute(final JSONObject result) {
			try{
				((TextView)findViewById(R.id.commitMessage)).setText(result.getJSONObject("commit").getString("message"));
				((TextView)findViewById(R.id.status)).setText(result.getString("status"));
				((TextView)findViewById(R.id.createdOn)).setText(DateFormatUtil.parseAndFormatJsonDate(result.getString("created")));
				
				if (!result.isNull("deployed"))
					((TextView)findViewById(R.id.deployedOn)).setText(DateFormatUtil.parseAndFormatJsonDate(result.getString("deployed")));
				else{
					findViewById(R.id.deployedOnLabel).setVisibility(View.GONE);
					findViewById(R.id.deployedOn).setVisibility(View.GONE);
				}
				
				new TestsTask().execute(((SessionData)getApplication()).getToken(), result.getString("tests_url"));

			} catch(Exception e) {
				Log.e(tag, e.getMessage());
			}
		}
	}

	private class TestsTask extends AsyncTask<String, Void, JSONArray>{

		@Override
		protected JSONArray doInBackground(String... params) {
			RestClient client = new RestClient(params[1]);
			client.AddParam("access_token", params[0]);

			try {
				client.Execute(RequestMethod.GET);
				return new JSONArray(client.getResponse());

			} catch (Exception e) {
				Log.e(tag, e.getMessage());
			}

			return null;
		}

		@Override
		protected void onPostExecute(JSONArray result) {

			if (result.length() > 0){

				findViewById(R.id.testsLayout).setVisibility(View.VISIBLE);

				try {
					ListView tests = (ListView)findViewById(R.id.testsListView);
					tests.setAdapter(new TestsAdapter(BuildDetailsActivity.this, result));

				} catch (Exception e) {
					Log.e(tag, e.getMessage());
				}
			}
			
			setSupportProgressBarIndeterminateVisibility(false);
		}
	}
}
