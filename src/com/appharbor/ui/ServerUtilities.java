package com.appharbor.ui;

import com.appharbor.R;
import com.appharbor.utils.RestClient;
import com.appharbor.utils.RestClient.RequestMethod;
import com.google.android.gcm.GCMRegistrar;

import android.content.Context;
import android.util.Log;

public class ServerUtilities {

	public static boolean register(Context context, String registrationId){
		 
        RestClient client =  new RestClient(context.getString(R.string.url_registration));
		client.AddParam("key", registrationId);
		
		try{
			client.Execute(RequestMethod.POST);
			if (client.getResponseCode() != 200)
				throw new Exception();
	
			GCMRegistrar.setRegisteredOnServer(context, true);
			return true;
		}catch (Exception e) {
			Log.e("ServerUtilities", e.getMessage());
		}
		
		return false;
	}
	
	public static boolean unregister(Context context, String registrationId){
		 
        RestClient client =  new RestClient(context.getString(R.string.url_unregister));
		client.AddParam("key", registrationId);
		
		try{
			client.Execute(RequestMethod.DELETE);
			if (client.getResponseCode() != 200)
				throw new Exception();
	
			GCMRegistrar.setRegisteredOnServer(context, false);
			return true;
		}catch (Exception e) {
			Log.e("ServerUtilities", e.getMessage());
		}
		
		return false;
	}
}