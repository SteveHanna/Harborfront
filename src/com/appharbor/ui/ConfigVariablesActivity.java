package com.appharbor.ui;

import org.json.JSONArray;
import android.os.Bundle;
import android.widget.ListView;
import com.actionbarsherlock.view.Window;
import com.appharbor.R;
import com.appharbor.adapters.ConfigVariablesAdapter;
import com.appharbor.utils.JsonArrayTask;

public class ConfigVariablesActivity extends BaseActivity{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.config_variables);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		
		new ConfigVariablesTask().execute();
	}
	
	private class ConfigVariablesTask extends JsonArrayTask{

		public ConfigVariablesTask() {
			super(ConfigVariablesActivity.this, "configurationvariables");
		}

		@Override
		protected void handlePostExecute(JSONArray result) {
			super.handlePostExecute(result);
			
			ListView list = (ListView)findViewById(R.id.configVariablesList);
			list.setAdapter(new ConfigVariablesAdapter(ConfigVariablesActivity.this, result));
		}
	}
}