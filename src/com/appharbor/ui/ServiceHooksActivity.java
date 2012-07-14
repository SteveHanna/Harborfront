package com.appharbor.ui;

import java.util.ArrayList;
import org.json.JSONArray;
import com.actionbarsherlock.view.Window;
import com.appharbor.R;
import com.appharbor.utils.ArrayUtils;
import com.appharbor.utils.JsonArrayTask;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class ServiceHooksActivity extends BaseActivity{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.service_hooks);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		new ServiceHooksTask().execute();
	}

	private class ServiceHooksTask extends JsonArrayTask {

		public ServiceHooksTask() {
			super(ServiceHooksActivity.this, "servicehooks");
		}

		@Override
		protected void handlePostExecute(JSONArray result) {
			super.handlePostExecute(result);
			
			ListView buildsView = (ListView)findViewById(R.id.serviceHooksList);
			ArrayList<String> names = ArrayUtils.select(result, "value");
			buildsView.setAdapter(new ArrayAdapter<String>(ServiceHooksActivity.this, android.R.layout.simple_list_item_1, names));
		}
	}
}
