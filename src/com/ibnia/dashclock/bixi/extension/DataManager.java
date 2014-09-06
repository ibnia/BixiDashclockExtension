package com.ibnia.dashclock.bixi.extension;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

//http://www.vogella.com/tutorials/AndroidSQLite/article.html
//http://developer.android.com/training/basics/data-storage/databases.html#DbHelper

public class DataManager {
	// Database fields
	private SQLiteDatabase database;
	private DatabaseHelper dbHelper;
	private String[] allColumns = { 
			DatabaseHelper.COLUMN_ID,
			DatabaseHelper.COLUMN_NAME,
			DatabaseHelper.COLUMN_LAT,
			DatabaseHelper.COLUMN_LONG,
			DatabaseHelper.COLUMN_BIKES,
			DatabaseHelper.COLUMN_DOCKS,
			DatabaseHelper.COLUMN_INSTALLED,
			DatabaseHelper.COLUMN_LOCKED,
			DatabaseHelper.COLUMN_TEMPORARY
	};
	public static final int INDEX_ID = 0;
	public static final int INDEX_NAME = 1;
	public static final int INDEX_LAT = 2;
	public static final int INDEX_LONG = 3;
	public static final int INDEX_BIKES = 4;
	public static final int INDEX_DOCKS = 5;
	public static final int INDEX_INSTALLED = 6;
	public static final int INDEX_LOCKED = 7;
	public static final int INDEX_TEMPORARY = 8;
	
	public DataManager(Context context) {
		dbHelper = new DatabaseHelper(context);
	}

	public void close() {
		dbHelper.close();
	}

	public void clearData() {
		database = dbHelper.getWritableDatabase();
		dbHelper.reCreateDatabase(database);
	}

	public void addSpot(int id, String name, double latitude, double longitude,
			int nbBikes, int nbEmptyDocks, boolean installed,
			boolean locked, boolean temporary) throws SQLException {
		// Gets the data repository in write mode
		database = dbHelper.getWritableDatabase();

		// Create a new map of values, where column names are the keys
		ContentValues values = new ContentValues();
		values.put(DatabaseHelper.COLUMN_ID, id);
		values.put(DatabaseHelper.COLUMN_NAME, name);
		values.put(DatabaseHelper.COLUMN_LAT, latitude);
		values.put(DatabaseHelper.COLUMN_LONG, longitude);
		values.put(DatabaseHelper.COLUMN_BIKES, nbBikes);
		values.put(DatabaseHelper.COLUMN_DOCKS, nbEmptyDocks);
		values.put(DatabaseHelper.COLUMN_INSTALLED, installed);
		values.put(DatabaseHelper.COLUMN_LOCKED, locked);
		values.put(DatabaseHelper.COLUMN_TEMPORARY, temporary);

		// Insert the new row, returning the primary key value of the new row
		database.insert(DatabaseHelper.TABLE_NAME,null,values);
	}

	public List<BixiSpot> getNearestSpotsWithBikes(double latitude, double longitude) throws SQLException {
		database = dbHelper.getReadableDatabase();  

		List<BixiSpot> spots = new ArrayList<BixiSpot>();
		
		double fudge = Math.pow(Math.cos(Math.toRadians(latitude)),2);

		Cursor cursor = database.query(DatabaseHelper.TABLE_NAME,
				allColumns, DatabaseHelper.COLUMN_BIKES + " > 0", 
				null, null, null, "((" + latitude + " - " + DatabaseHelper.COLUMN_LAT 
				+ ") * (" + latitude + " - " + DatabaseHelper.COLUMN_LAT
				+ ") +(" + longitude + " - " + DatabaseHelper.COLUMN_LONG
				+ ") * (" + longitude + " - " + DatabaseHelper.COLUMN_LONG
				+ ") * " + fudge + ")");
		
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			BixiSpot spot = new BixiSpot();
			
			spot.setId(cursor.getInt(INDEX_ID));
			spot.setName(cursor.getString(INDEX_NAME));
			spot.setLatitude(cursor.getDouble(INDEX_LAT));
			spot.setLongitude(cursor.getDouble(INDEX_LONG));
			spot.setNbBikes(cursor.getInt(INDEX_BIKES));
			spot.setNbEmptyDocks(cursor.getInt(INDEX_DOCKS));
			spot.setInstalled(cursor.getInt(INDEX_INSTALLED)>0);
			spot.setLocked(cursor.getInt(INDEX_LOCKED)>0);
			spot.setTemporary(cursor.getInt(INDEX_TEMPORARY)>0);
			
			spots.add(spot);
			cursor.moveToNext();
		}
		// make sure to close the cursor
		cursor.close();
		return spots;
	}

	public List<BixiSpot> getNearestSpotsWithoutEmptyDocks(double latitude, double longitude) throws SQLException {
		database = dbHelper.getReadableDatabase();  

		List<BixiSpot> spots = new ArrayList<BixiSpot>();
		
		double fudge = Math.pow(Math.cos(Math.toRadians(latitude)),2);

		Cursor cursor = database.query(DatabaseHelper.TABLE_NAME,
				allColumns, DatabaseHelper.COLUMN_DOCKS + " > 0", 
				null, null, null, "((" + latitude + " - " + DatabaseHelper.COLUMN_LAT 
				+ ") * (" + latitude + " - " + DatabaseHelper.COLUMN_LAT
				+ ") +(" + longitude + " - " + DatabaseHelper.COLUMN_LONG
				+ ") * (" + longitude + " - " + DatabaseHelper.COLUMN_LONG
				+ ") * " + fudge + ")");

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			BixiSpot spot = new BixiSpot();
			
			spot.setId(cursor.getInt(INDEX_ID));
			spot.setName(cursor.getString(INDEX_NAME));
			spot.setLatitude(cursor.getDouble(INDEX_LAT));
			spot.setLongitude(cursor.getDouble(INDEX_LONG));
			spot.setNbBikes(cursor.getInt(INDEX_BIKES));
			spot.setNbEmptyDocks(cursor.getInt(INDEX_DOCKS));
			spot.setInstalled(cursor.getInt(INDEX_INSTALLED)>0);
			spot.setLocked(cursor.getInt(INDEX_LOCKED)>0);
			spot.setTemporary(cursor.getInt(INDEX_TEMPORARY)>0);
			
			spots.add(spot);
			cursor.moveToNext();
		}
		// make sure to close the cursor
		cursor.close();
		return spots;
	}
}
