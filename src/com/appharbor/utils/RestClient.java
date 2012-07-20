package com.appharbor.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

public class RestClient {

    private ArrayList <NameValuePair> params;
    private ArrayList <NameValuePair> headers;

    private String url;

    private int responseCode;
    private String message;

    private String response;

    public String getResponse() {
    	return response;
    }

    public String getErrorMessage() {
        return message;
    }

    public int getResponseCode() {
        return responseCode;
    }
    public RestClient(String url){
    	this(url, null);
    }

    public RestClient(String url, String authToken)
    {
        this.url = url;
        params = new ArrayList<NameValuePair>();
        headers = new ArrayList<NameValuePair>();
        AddHeader("Accept", "application/json");
        
        if (authToken != null)
        	AddHeader("Authorization", "BEARER " + authToken);
    }

    public void AddParam(String name, String value)
    {
        params.add(new BasicNameValuePair(name, value));
    }

    public void AddHeader(String name, String value)
    {
        headers.add(new BasicNameValuePair(name, value));
    }
    
    private String getParameters() throws Exception{

        String combinedParams = "";
        if(!params.isEmpty()){
        	combinedParams += "?";
        	for(NameValuePair p : params)
        	{
        		String paramString = p.getName() + "=" + URLEncoder.encode(p.getValue(),"UTF-8");
        		if(combinedParams.length() > 1)
        			combinedParams  +=  "&" + paramString;
        		else
        			combinedParams += paramString;
        	}
        }

        return combinedParams;
    }

    public void Execute(RequestMethod method) throws Exception
    {
    	HttpUriRequest request;
    	
        switch(method) {
            case GET: {
            	request = new HttpGet(url +  getParameters());
            	break;
            } case POST: {
            	request = new HttpPost(url);

            	if(!params.isEmpty())
            		((HttpPost)request).setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));

            	break;
            } case PUT: {
            	request = new HttpPut(url);

            	if(!params.isEmpty())
            		((HttpPut)request).setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
            	
            	break;
            } case DELETE: {
            	request = new HttpDelete(url);
            	break;
            } default:
            	throw new IllegalArgumentException();
        }
        
        //add headers
        for(NameValuePair h : headers)
            request.addHeader(h.getName(), h.getValue());
        
        executeRequest(request, url);
    }

    private void executeRequest(HttpUriRequest request, String url)
    {
        HttpClient client = new DefaultHttpClient();

        HttpResponse httpResponse;

        try {
            httpResponse = client.execute(request);
            responseCode = httpResponse.getStatusLine().getStatusCode();
            message = httpResponse.getStatusLine().getReasonPhrase();

            HttpEntity entity = httpResponse.getEntity();

            if (entity != null) {

                InputStream instream = entity.getContent();
                response = convertStreamToString(instream);

                // Closing the input stream will trigger connection release
                instream.close();
            }

        } catch (ClientProtocolException e)  {
            client.getConnectionManager().shutdown();
            e.printStackTrace();
        } catch (IOException e) {
            client.getConnectionManager().shutdown();
            e.printStackTrace();
        }
    }
 
    private static String convertStreamToString(InputStream is) {

        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }
    
    public enum RequestMethod { GET, POST, PUT, DELETE }
}

