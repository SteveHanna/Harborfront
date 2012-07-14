package com.appharbor;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import com.appharbor.R;
import com.appharbor.ui.DashboardActivity;
import com.appharbor.ui.ServerUtilities;
import com.appharbor.utils.SensitiveStrings;
import com.google.android.gcm.GCMBaseIntentService;
import com.google.android.gcm.GCMRegistrar;

public class GCMIntentService extends GCMBaseIntentService {

    private static final String TAG = "GCMIntentService";
	
    public GCMIntentService() {
        super(SensitiveStrings.SENDER_ID);
    }

    @Override
    protected void onRegistered(Context context, String registrationId) {
        Log.i(TAG, "Device registered: regId = " + registrationId);
        //displayMessage(context, getString(R.string.gcm_registered));
        //ServerUtilities.register(context, registrationId);
       ServerUtilities.register(this, registrationId);
    }

    @Override
    protected void onUnregistered(Context context, String registrationId) {
        Log.i(TAG, "Device unregistered");
        //displayMessage(context, getString(R.string.gcm_unregistered));
        if (GCMRegistrar.isRegisteredOnServer(context)) {
        	GCMRegistrar.setRegisteredOnServer(context, false);
            ServerUtilities.unregister(context, registrationId);
        } else {
            // This callback results from the call to unregister made on
            // ServerUtilities when the registration to the server failed.
            Log.i(TAG, "Ignoring unregister callback");
        }
    }

    @Override
    protected void onMessage(Context context, Intent intent) {
        Log.i(TAG, "Received message");

        String status = intent.getStringExtra("buildStatus");
        String appName = intent.getStringExtra("appName");
        
        generateNotification(context, "Build " + status, "Build " + status + " for " + appName);
    }

    @Override
    protected void onDeletedMessages(Context context, int total) {
        Log.i(TAG, "Received deleted messages notification");
        String message = "deleted";
        //displayMessage(context, message);
        // notifies user
        generateNotification(context, "title", message);
    }

    @Override
    public void onError(Context context, String errorId) {
        Log.i(TAG, "Received error: " + errorId);
       // displayMessage(context, "error", "error");
    }

    @Override
    protected boolean onRecoverableError(Context context, String errorId) {
        // log message
        Log.i(TAG, "Received recoverable error: " + errorId);
        //displayMessage(context, "error", "recoverable");
        return super.onRecoverableError(context, errorId);
    }

    /**
     * Issues a notification to inform the user that server has sent a message.
     */
    private static void generateNotification(Context context, String title, String message) {
		String ns = Context.NOTIFICATION_SERVICE;
		NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(ns);
		
		Notification notification = new Notification(R.drawable.ic_launcher, title, System.currentTimeMillis());
		Context contextApp = context.getApplicationContext();
		
		Intent notificationIntent = new Intent(contextApp, DashboardActivity.class);
		PendingIntent contentIntent = PendingIntent.getActivity(contextApp, 0, notificationIntent, 0);

		notification.setLatestEventInfo(contextApp, title, message, contentIntent);
		mNotificationManager.notify(1, notification);	
    }
}
