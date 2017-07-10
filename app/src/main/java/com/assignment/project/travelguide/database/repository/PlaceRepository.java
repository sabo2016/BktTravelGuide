package com.assignment.project.travelguide.database.repository;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;

import com.assignment.project.travelguide.database.SqliteManager;
import com.assignment.project.travelguide.database.model.BaseModel;
import com.assignment.project.travelguide.database.model.Place;

import java.util.ArrayList;


public class PlaceRepository extends Repository {
    public static final String TABLE_NAME = "PLACE";
    public static final String CREATE_QUERY = "CREATE TABLE " +
            TABLE_NAME +
            "( " +
            "PLACE_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
            " PLACE_NAME TEXT," +
            " LATITUDE TEXT," +
            " LONGITUDE TEXT" +
            ")";
    public static final String DROP_QUERY = "DROP TABLE " + TABLE_NAME;


    public PlaceRepository(Context context) {
        super(context);
    }

    @Override
    public ArrayList<BaseModel> select(BaseModel baseModel) {
        Place wherePlace = (Place) baseModel;

        SQLiteDatabase sqLiteDatabase = getWritableDatabase();

        ArrayList<BaseModel> arrayList = new ArrayList<BaseModel>();

        Cursor cursor = sqLiteDatabase.query(TABLE_NAME, null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                Place place = new Place();
                place.setPlaceId(cursor.getInt(cursor.getColumnIndex("PLACE_ID")));
                place.setPlaceName(cursor.getString(cursor.getColumnIndex("PLACE_NAME")));
                place.setLatitude(cursor.getDouble(cursor.getColumnIndex("LATITUDE")));
                place.setLongitude(cursor.getDouble(cursor.getColumnIndex("LONGITUDE")));

                arrayList.add(place);
            } while (cursor.moveToNext());
        }
        cursor.close();
        sqLiteDatabase.close();

        return arrayList;
    }

    @Override
    public long insert(BaseModel baseModel) {
        Place place = (Place) baseModel;
        ContentValues contentValues = new ContentValues();
        contentValues.put("PLACE_NAME", place.getPlaceName());
        contentValues.put("LATITUDE", place.getLatitude());
        contentValues.put("LONGITUDE", place.getLongitude());


        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        long result = sqLiteDatabase.insertOrThrow(PlaceRepository.TABLE_NAME, null, contentValues);
        sqLiteDatabase.close();

        return result;


    }


    public ArrayList<Place> selectQuery(Place wherePlace) {

        SQLiteDatabase sqLiteDatabase = getWritableDatabase();

        ArrayList<Place> arrayList = new ArrayList<Place>();

        Cursor cursor = sqLiteDatabase.query(TABLE_NAME, null, "PLACE_ID != ?", new String[]{wherePlace.getPlaceId() + ""}, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                Place place = new Place();
                place.setPlaceId(cursor.getInt(cursor.getColumnIndex("PLACE_ID")));
                place.setPlaceName(cursor.getString(cursor.getColumnIndex("PLACE_NAME")));
                place.setLatitude(cursor.getDouble(cursor.getColumnIndex("LATITUDE")));
                place.setLongitude(cursor.getDouble(cursor.getColumnIndex("LONGITUDE")));

                arrayList.add(place);
            } while (cursor.moveToNext());
        }
        cursor.close();
        sqLiteDatabase.close();

        return arrayList;
    }

    public int findNearestPlace(double lat, double longi) {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        Cursor cursor = sqLiteDatabase.query(TABLE_NAME, null, null, null, null, null, null);
        double shortestDistance = 0;
        int shortestPlaceId = 0;

        if (cursor.moveToFirst()) {
            do {
                double latitude = cursor.getDouble(cursor.getColumnIndex("LATITUDE"));
                double longitude = cursor.getDouble(cursor.getColumnIndex("LONGITUDE"));
                int placeId = cursor.getInt(cursor.getColumnIndex("PLACE_ID"));
                ;

                double distance = Math.abs(distance(latitude, lat, longitude, longi, 0, 0));
                if (shortestDistance == 0 || distance < shortestDistance) {
                    shortestDistance = distance;
                    shortestPlaceId = placeId;
                }

            } while (cursor.moveToNext());
        }
        cursor.close();
        sqLiteDatabase.close();

        return shortestPlaceId;
    }

    public static double distance(double lat1, double lat2, double lon1,
                                  double lon2, double el1, double el2) {

        final int R = 6371; // Radius of the earth

        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c * 1000; // convert to meters

        double height = el1 - el2;

        distance = Math.pow(distance, 2) + Math.pow(height, 2);

        return Math.sqrt(distance);
    }


}
