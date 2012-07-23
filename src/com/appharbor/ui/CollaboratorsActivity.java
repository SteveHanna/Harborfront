package com.appharbor.ui;

import org.json.JSONArray;
import android.os.Bundle;
import android.widget.ListView;
import com.actionbarsherlock.view.Window;
import com.appharbor.R;
import com.appharbor.adapters.CollaboratorsAdapter;
import com.appharbor.utils.JsonArrayTask;

public class CollaboratorsActivity extends BaseActivity {
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.collaborators);		
		setSupportProgressBarIndeterminateVisibility(true);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		
		new CollaboratorsTask().execute();
	}
	
	private class CollaboratorsTask extends JsonArrayTask{

		public CollaboratorsTask() {
			super(CollaboratorsActivity.this, "collaborators");
		}

		@Override
		protected void handlePostExecute(JSONArray result) {
			super.handlePostExecute(result);
			
			ListView collaboratorsList = (ListView)findViewById(R.id.collaboratorsList);
			collaboratorsList.setAdapter(new CollaboratorsAdapter(CollaboratorsActivity.this, result));
		}
	}
}
