package com.ibnia.dashclock.bixi.extension;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.apps.dashclock.api.DashClockExtension;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.SQLException;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

//http://stackoverflow.com/questions/3505930/make-an-http-request-with-android

public class RequestHelper extends AsyncTask<String, String, String> {

	private Context mContext;
	private DataManager dataManager;

	public RequestHelper(Context context) {
		// TODO Auto-generated constructor stub
		mContext = context;
		dataManager = new DataManager(mContext);
	}

	@Override
	protected String doInBackground(String... params) {
		// TODO Auto-generated method stub

		HttpClient httpclient = new DefaultHttpClient();
		HttpResponse response;
		String responseString = null;
		
		Log.d(RequestHelper.class.getName(),"Sending request: " + params[0]);

		try {
			response = httpclient.execute(new HttpGet(params[0]));
			StatusLine statusLine = response.getStatusLine();
			
			if(statusLine.getStatusCode() == HttpStatus.SC_OK){
				ByteArrayOutputStream out = new ByteArrayOutputStream();
				response.getEntity().writeTo(out);
				out.close();
				responseString = out.toString();
			} else{
				//Closes the connection.
				response.getEntity().getContent().close();
				throw new IOException(statusLine.getReasonPhrase());
			}
		} catch (ClientProtocolException e) {
			//TODO Handle problems..
		} catch (IOException e) {
			//TODO Handle problems..
		}
		return responseString;
	}

	@Override
	protected void onPostExecute(String result) {
		// TODO Auto-generated method stub

		try {
			//Parse JSON
			JSONArray jsonArray = new JSONArray(result);
			Log.d(RequestHelper.class.getName(),"Number of entries " + jsonArray.length());
			
			dataManager.clearData();

			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject jsonObject = jsonArray.getJSONObject(i);
				dataManager.addSpot(
						jsonObject.getInt(DatabaseHelper.COLUMN_ID), 
						jsonObject.getString(DatabaseHelper.COLUMN_NAME), 
						jsonObject.getDouble(DatabaseHelper.COLUMN_LAT), 
						jsonObject.getDouble(DatabaseHelper.COLUMN_LONG), 
						jsonObject.getInt(DatabaseHelper.COLUMN_BIKES), 
						jsonObject.getInt(DatabaseHelper.COLUMN_DOCKS), 
						jsonObject.getBoolean(DatabaseHelper.COLUMN_INSTALLED), 
						jsonObject.getBoolean(DatabaseHelper.COLUMN_LOCKED), 
						jsonObject.getBoolean(DatabaseHelper.COLUMN_TEMPORARY));
				}
			//Refresh view
			((BixiExtension)mContext).onUpdateData(DashClockExtension.UPDATE_REASON_CONTENT_CHANGED);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		super.onPostExecute(result);
	}
}