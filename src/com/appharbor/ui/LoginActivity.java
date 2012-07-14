package com.appharbor.ui;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Window;
import com.appharbor.R;
import com.appharbor.utils.RestClient;
import com.appharbor.utils.SensitiveStrings;
import com.appharbor.utils.RestClient.RequestMethod;


import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebSettings.RenderPriority;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class LoginActivity extends SherlockActivity 
{
	private static final String tag = "LoginActivity";
    private static final String CALLBACK_URL = "appharbor://";
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_webview);
        
        String url = "https://appharbor.com/user/authorizations/new?client_id=" + SensitiveStrings.CLIENT_ID + "&redirect_uri=" + CALLBACK_URL;
        
        WebView webview = (WebView)findViewById(R.id.webview);
        webview.getSettings().setLoadWithOverviewMode(true);
        webview.getSettings().setUseWideViewPort(true);
        webview.getSettings().setBuiltInZoomControls(true);
        webview.getSettings().setJavaScriptEnabled(true);
        webview.getSettings().setRenderPriority(RenderPriority.HIGH);
        webview.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        
        webview.setBackgroundColor(R.color.app_background);
        
        webview.setWebViewClient(new WebViewClient() {
    	
        	@Override
        	public void onPageFinished(WebView view, String url) {
        		super.onPageFinished(view, url);
        		
        		setSupportProgressBarIndeterminateVisibility(false);
        	}
        	
        	@Override
        	public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
        		super.onReceivedError(view, errorCode, description, failingUrl);
        		finish();
        	}
        	
        	@Override
        	public void onPageStarted(WebView view, String url, android.graphics.Bitmap favicon) {
        		
        		setSupportProgressBarIndeterminateVisibility(true);
        		
                String fragment = "code=";
                int start = url.indexOf(fragment);
                if (start > -1) {

                	view.setVisibility(View.GONE);
                	
                	String accessToken = url.substring(start + fragment.length(), url.length());
                	
                	try{

                		String response = new AuthenticateTask().execute(accessToken).get();

                		String tokenName = "access_token=";
                		String token = response.substring(response.indexOf(tokenName) + tokenName.length(), response.indexOf("&token_type")); 

                		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(LoginActivity.this);
                		SharedPreferences.Editor editor = preferences.edit();
                		editor.clear();
                		editor.putString("token", token); 
                		editor.commit();

                		view.stopLoading();
                		setResult(2);
                		finish();

                	}catch (Exception e) {
                		Log.e(tag, e.getMessage());
                	}
                }
        	}
        });

        webview.loadUrl(url);
    }
    
    private class AuthenticateTask extends AsyncTask<String, Void, String>{

		@Override
		protected String doInBackground(String... args) {

			RestClient client = new RestClient("https://appharbor.com/tokens");
     	    client.AddParam("client_id", SensitiveStrings.CLIENT_ID);
     	    client.AddParam("client_secret", SensitiveStrings.SECRET);
     	    client.AddParam("code", args[0]);
			
     	    try {
     	    	client.Execute(RequestMethod.POST);
     	    } catch (Exception e) {
     	    	Log.e(tag, e.getMessage());
     	    }

     	    return client.getResponse();
		}
    }
}