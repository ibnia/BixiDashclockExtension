package com.ibnia.dashclock.bixi.extension;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {

	  public static final String TABLE_NAME = "bixiSpots";
	  public static final String COLUMN_ID = "id";
	  public static final String COLUMN_NAME = "name";
	  public static final String COLUMN_LAT = "lat";
	  public static final String COLUMN_LONG = "long";
	  public static final String COLUMN_BIKES = "nbBikes";
	  public static final String COLUMN_DOCKS = "nbEmptyDocks";
	  public static final String COLUMN_INSTALLED = "installed";
	  public static final String COLUMN_LOCKED = "locked";
	  public static final String COLUMN_TEMPORARY = "temporary";
	  
	  private static final String DATABASE_NAME = "bixiExtension.db";
	  private static final int DATABASE_VERSION = 1;
	  
	  private static final String PRIMARY_TYPE = " INTEGER PRIMARY KEY";
	  private static final String TEXT_TYPE = " TEXT";
	  private static final String INTEGER_TYPE = " INTEGER";
	  private static final String DOUBLE_TYPE = " DOUBLE";
	  private static final String BOOLEAN_TYPE = " BOOLEAN";
	  private static final String COMMA_SEP = ",";

	  // Database creation sql statement
	  private static final String DATABASE_CREATE = "create table "
	      + TABLE_NAME + "(" 
			  + COLUMN_ID + PRIMARY_TYPE + COMMA_SEP
			  + COLUMN_NAME + TEXT_TYPE + COMMA_SEP 
			  + COLUMN_LAT + DOUBLE_TYPE + COMMA_SEP 
			  + COLUMN_LONG +  DOUBLE_TYPE + COMMA_SEP
			  + COLUMN_BIKES + INTEGER_TYPE + COMMA_SEP
			  + COLUMN_DOCKS + INTEGER_TYPE + COMMA_SEP
			  + COLUMN_INSTALLED + BOOLEAN_TYPE + COMMA_SEP
			  + COLUMN_LOCKED + BOOLEAN_TYPE + COMMA_SEP
			  + COLUMN_TEMPORARY + " )";
	  
	  private static final String DATABASE_DELETE = "DROP TABLE IF EXISTS " + TABLE_NAME;

	  public DatabaseHelper(Context context) {
	    super(context, DATABASE_NAME, null, DATABASE_VERSION);
	  }

	  @Override
	  public void onCreate(SQLiteDatabase database) {
		Log.d(DatabaseHelper.class.getName(),"Database created");
	    database.execSQL(DATABASE_CREATE);
	  }

	  @Override
	  public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	    Log.d(DatabaseHelper.class.getName(),
	        "Upgrading database from version " + oldVersion + " to "
	            + newVersion + ", which will destroy all old data");
	    reCreateDatabase(db);
	  }
	  
	  public void reCreateDatabase(SQLiteDatabase db) {
		  Log.d(DatabaseHelper.class.getName(),"Database recreated");
		  db.execSQL(DATABASE_DELETE);
		  onCreate(db);
	}

}
