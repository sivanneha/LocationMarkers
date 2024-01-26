package com.example.myapplication;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "LocationsDB";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_LOCATIONS = "locations";
    private static final String KEY_ID = "id";
    private static final String KEY_LATITUDE = "latitude";
    private static final String KEY_LONGITUDE = "longitude";

    private static final String CREATE_TABLE_LOCATIONS = "CREATE TABLE " + TABLE_LOCATIONS + "("
            + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + KEY_LATITUDE + " REAL,"
            + KEY_LONGITUDE + " REAL"
            + ")";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_LOCATIONS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LOCATIONS);
        onCreate(db);
    }

    public long insertLocation(LocationData locationData) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_LATITUDE, locationData.getLatitude());
        values.put(KEY_LONGITUDE, locationData.getLongitude());
        long id = db.insert(TABLE_LOCATIONS, null, values);
        db.close();
        return id;
    }

    public List<LocationData> getAllLocations() {
        List<LocationData> locationList = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_LOCATIONS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                LocationData locationData = new LocationData();
                int idIndex = cursor.getColumnIndex(KEY_ID);
                int latitudeIndex = cursor.getColumnIndex(KEY_LATITUDE);
                int longitudeIndex = cursor.getColumnIndex(KEY_LONGITUDE);

                if (idIndex != -1 && latitudeIndex != -1 && longitudeIndex != -1) {
                    locationData.setId(cursor.getInt(idIndex));
                    locationData.setLatitude(cursor.getDouble(latitudeIndex));
                    locationData.setLongitude(cursor.getDouble(longitudeIndex));
                    locationList.add(locationData);
                }
            } while (cursor.moveToNext());
            cursor.close();
        }
        db.close();
        return locationList;
    }
    public void deleteLocation(double latitude, double longitude) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("locations", "latitude = ? AND longitude = ?", new String[]{String.valueOf(latitude), String.valueOf(longitude)});
        db.close();
    }
}
