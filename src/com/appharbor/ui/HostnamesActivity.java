package com.appharbor.ui;

import org.json.JSONArray;
import com.actionbarsherlock.view.Window;
import com.appharbor.R;
import com.appharbor.adapters.HostnamesAdapter;
import com.appharbor.utils.JsonArrayTask;
import android.os.Bundle;
import android.widget.ListView;

public class HostnamesActivity extends BaseActivity{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.hostnames);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		
		new HostnamesTask().execute();
	}

	private class HostnamesTask extends JsonArrayTask {

		public HostnamesTask() {
			super(HostnamesActivity.this, "hostnames");
		}

		@Override
		protected void handlePostExecute(JSONArray result) {
			super.handlePostExecute(result);
			
			ListView buildsView = (ListView)findViewById(R.id.hostnamesList);
			buildsView.setAdapter(new HostnamesAdapter(HostnamesActivity.this, result));	
		}
	}
}
