package com.appharbor.ui;

import org.json.JSONArray;
import com.actionbarsherlock.view.Window;
import com.appharbor.R;
import com.appharbor.adapters.ErrorsAdapter;
import com.appharbor.utils.JsonArrayTask;
import android.os.Bundle;
import android.widget.ListView;

public class ErrorsActivity extends BaseActivity{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.errors);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		
		new ErrorsTask().execute();
	}

	private class ErrorsTask extends JsonArrayTask {

		public ErrorsTask() {
			super(ErrorsActivity.this, "errors");
		}

		@Override
		protected void handlePostExecute(JSONArray result) {
			super.handlePostExecute(result);
			
			ListView buildsView = (ListView)findViewById(R.id.list);
			buildsView.setAdapter(new ErrorsAdapter(ErrorsActivity.this, result));
		}
	}
}
