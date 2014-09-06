package com.ibnia.dashclock.bixi.extension;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.google.android.apps.dashclock.api.DashClockExtension;
import com.google.android.apps.dashclock.api.ExtensionData;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.preference.PreferenceManager;
import android.util.Log;

//http://developer.android.com/guide/topics/location/strategies.html
//http://goodenoughpractices.blogspot.ca/2011/08/query-by-proximity-in-android.html

public class BixiExtension extends DashClockExtension implements OnSharedPreferenceChangeListener{

	private LocationTracker locationTracker;
	private DataManager dataMng;
	private Timer timer;
	// The minimum time between updates in milliseconds
	private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 10; // 1 heure
	private String uri;

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		// Get preference value.
		Log.d(BixiExtension.class.getName(),"Creation");
		locationTracker = new LocationTracker(this);
		dataMng = new DataManager(this);
		
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		uri = sp.getString(BixiSettingsActivity.PREF_CITY, getString(R.string.pref_city_default));
		sp.registerOnSharedPreferenceChangeListener(this);

		timer = new Timer();
		timer.schedule(new TimerTask() {

			public void run() {
				syncData();
			}
		}, MIN_TIME_BW_UPDATES);

		super.onCreate();
	}

	public void syncData() {
		RequestHelper requestHelper = new RequestHelper(this);
		requestHelper.execute(uri);
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		Log.d(BixiExtension.class.getName(),"Destruction");
		locationTracker.stopTracking();
		dataMng.close();
		super.onDestroy();
	}

	@Override
	protected void onUpdateData(int reason) {
		Log.d(BixiExtension.class.getName(),"Update extension");

		boolean visibility = false;
		String status = "-";
		String title = "";
		String body = "";
		String description = "";

		ExtensionData data = new ExtensionData();

		// check if GPS enabled     
		if(locationTracker.canGetLocation()){

			//Look for the nearest Bixi spots in the database
			List<BixiSpot> spotsFound = dataMng.getNearestSpotsWithBikes(locationTracker.getLatitude(), locationTracker.getLongitude());
			
			Log.d(BixiExtension.class.getName(),"Number of spots found: " + spotsFound.size());

			if(spotsFound.size() > 0)
			{
				visibility = true; 
				
				BixiSpot nearestSpot = spotsFound.get(0);
				
				String bikeText = "bike";
				String dockText = "dock";

				if(nearestSpot.getNbBikes() > 1)
					bikeText = "bikes";
				
				if(nearestSpot.getNbEmptyDocks() > 1)
					dockText = "docks";
				
				status = nearestSpot.getNbBikes() + " " + bikeText + "\n" + nearestSpot.getNbEmptyDocks() + " " + dockText;
				title = nearestSpot.getNbBikes() + " " +bikeText + " available at " + nearestSpot.getName();

				if(nearestSpot.getNbEmptyDocks() == 0)
				{
					spotsFound = dataMng.getNearestSpotsWithoutEmptyDocks(locationTracker.getLatitude(), locationTracker.getLongitude());
					nearestSpot = spotsFound.get(0);
					
					if(nearestSpot.getNbEmptyDocks() > 1)
						dockText = "docks";

					body = "There are no empty docks avaiblable at " + nearestSpot.getName() + 
							" but you can find " + nearestSpot.getNbEmptyDocks() + " empty " + dockText + "at " +
							nearestSpot.getName() + ".";
					description = "The nearest Bixi spot did not have any empty docks." +
							"Another spot, where some docks are available have, been provided.";
				}
				else
				{	
					body = nearestSpot.getNbEmptyDocks() + " empty " + dockText + " available.";
					description = "The nearest Bixi spot have empty docks.";
				}
			}
		}

		//Present the data
		data.icon(R.drawable.ic_launcher);
		data.visible(visibility);
		data.status(status);
		data.expandedTitle(title);
		data.expandedBody(body);
		data.contentDescription(description);;

		// Publish the extension data update.
		publishUpdate(data);

	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
		// TODO Auto-generated method stub
		setUri(sharedPreferences.getString(key, getString(R.string.pref_city_default)));
		syncData();
		Log.d(BixiExtension.class.getName(), "Test: " + uri);
	}
	
	public void setUri(String value)
	{
		this.uri = value;
	}
}
