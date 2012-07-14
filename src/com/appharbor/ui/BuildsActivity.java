package com.appharbor.ui;

import java.util.Date;
import org.json.JSONArray;
import org.json.JSONObject;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView;
import android.widget.ListView;
import com.actionbarsherlock.view.Window;
import com.appharbor.R;
import com.appharbor.adapters.BuildsAdapter;
import com.appharbor.utils.DateFormatUtil;
import com.appharbor.utils.JsonArrayTask;

public class BuildsActivity extends BaseActivity {

	private static final String tag = "BuildsActivity";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.builds);

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		new BuildTask().execute();
	}

	private class BuildTask extends JsonArrayTask {

		public BuildTask() {
			super(BuildsActivity.this, "builds");
		}

		@Override
		protected void handlePostExecute(final JSONArray result) {
			super.handlePostExecute(result);

			String commitId = "";

			try{
				Date mostRecentDeployed = null;
				for (int i = 0; i < result.length(); i++) {

					JSONObject build = result.getJSONObject(i);
					if (!build.isNull("deployed")){
						Date d = DateFormatUtil.parse(build.getString("deployed"));
						if (mostRecentDeployed == null || d.after(mostRecentDeployed)){
							mostRecentDeployed =  d;
							commitId = build.getJSONObject("commit").getString("id");
						}
					}
				}
			}catch(Exception e){
				Log.e(tag, e.getMessage());
			}

			ListView buildsView = (ListView)findViewById(R.id.buildsList);

			buildsView.setAdapter(new BuildsAdapter(BuildsActivity.this, result, commitId));
			buildsView.setOnItemClickListener( new OnItemClickListener(){

				public void  onItemClick(AdapterView<?> parent, View view, int position, long id){

					try{
						Intent i = new Intent(BuildsActivity.this, BuildDetailsActivity.class);
						i.putExtra("buildUrl", result.getJSONObject(position).getString("url"));

						startActivity(i);
					}
					catch (Exception e) {
						Log.e(tag, e.getMessage());
					}
				}
			});
		}
	}
}