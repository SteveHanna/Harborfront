package com.appharbor.ui;

import com.appharbor.R;
import com.appharbor.utils.RestClient;
import com.appharbor.utils.RestClient.RequestMethod;
import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

public class RegistrationService extends IntentService{

	public RegistrationService() {
		super("RegistrationService");
	}

	@Override
	protected void onHandleIntent(Intent intent) {

		RestClient client =  new RestClient(getString(R.string.url_registration));
		client.AddParam("registrationKey", intent.getStringExtra("regId"));
		client.AddParam("deviceKey", intent.getStringExtra("deviceId"));
		
		try{
			client.Execute(RequestMethod.POST);
			if (client.getResponseCode() != 200)
				throw new Exception();
	
		}catch (Exception e) {
			Log.e("RegistrationService", e.getMessage());
		}
	}
}
