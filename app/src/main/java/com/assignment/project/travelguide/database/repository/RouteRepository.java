package com.assignment.project.travelguide.database.repository;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.assignment.project.travelguide.database.model.BaseModel;
import com.assignment.project.travelguide.database.model.Place;
import com.assignment.project.travelguide.database.model.Route;

import java.net.NoRouteToHostException;
import java.util.ArrayList;

/**
 * Created by ukesh on 3/21/17.
 */

public class RouteRepository extends Repository {
    public static final String TABLE_NAME = "ROUTE";
    public static final String CREATE_QUERY = "CREATE TABLE " +
            TABLE_NAME +
            " ( " +
            "        ROUTE_ID INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
            "        PLACE_ONE INTEGER  NOT NULL,\n" +
            "        PLACE_TWO INTEGER  NOT NULL,\n" +
            "        DISTANCE DOUBLE  NOT NULL" +
            ")";
    public static final String DROP_QUERY = "DROP TABLE " + TABLE_NAME;

    public RouteRepository(Context context) {
        super(context);
    }

    @Override
    public long insert(BaseModel baseModel) {
        Route route = (Route) baseModel;
        ContentValues contentValues = new ContentValues();
        contentValues.put("PLACE_ONE", route.getPlaceOne());
        contentValues.put("PLACE_TWO", route.getPlaceTwo());
        contentValues.put("ROUTE_ID", route.getRouteId());
        contentValues.put("DISTANCE", route.getDistance());


        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        long result = sqLiteDatabase.insertOrThrow(PlaceRepository.TABLE_NAME, null, contentValues);
        sqLiteDatabase.close();
        return result;
    }

    @Override
    public ArrayList<BaseModel> select(BaseModel baseModel) {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();

        ArrayList<BaseModel> arrayList = new ArrayList<BaseModel>();
        Route route = (Route) baseModel;

        Cursor cursor = sqLiteDatabase.query(TABLE_NAME, null, "PLACE_ONE=? AND PLACE_TWO=?", new String[]{String.valueOf(route.getPlaceOne()), String.valueOf(route.getPlaceTwo())}, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                Route route1 = new Route();
                route1.setRouteId(cursor.getInt(cursor.getColumnIndex("ROUTE_ID")));

                arrayList.add(route1);
            } while (cursor.moveToNext());
        }
        cursor.close();
        sqLiteDatabase.close();

        return arrayList;
    }


    public ArrayList<Route> selectQuery(Place place) {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();

        ArrayList<Route> arrayList = new ArrayList<Route>();

        Cursor cursor1 = sqLiteDatabase.query(TABLE_NAME, null, "PLACE_ONE=?", new String[]{String.valueOf(place.getPlaceId())}, null, null, "DISTANCE ASC");
        if (cursor1.moveToFirst()) {
            do {
                Route route = new Route();

                route.setRouteId(cursor1.getInt(cursor1.getColumnIndex("ROUTE_ID")));
                route.setPlaceTwo(cursor1.getInt(cursor1.getColumnIndex("PLACE_TWO")));
                route.setPlaceOne(cursor1.getInt(cursor1.getColumnIndex("PLACE_ONE")));
                route.setDistance(cursor1.getFloat(cursor1.getColumnIndex("DISTANCE")));

                arrayList.add(route);
            } while (cursor1.moveToNext());
        }
        cursor1.close();

        Cursor cursor2 = sqLiteDatabase.query(TABLE_NAME, null, "PLACE_TWO=?", new String[]{String.valueOf(place.getPlaceId())}, null, null, null);
        if (cursor2.moveToFirst()) {
            do {
                Route route = new Route();

                route.setRouteId(cursor2.getInt(cursor2.getColumnIndex("ROUTE_ID")));
                route.setPlaceTwo(cursor2.getInt(cursor2.getColumnIndex("PLACE_ONE")));
                route.setPlaceOne(cursor2.getInt(cursor2.getColumnIndex("PLACE_TWO")));
                route.setDistance(cursor2.getFloat(cursor2.getColumnIndex("DISTANCE")));

                arrayList.add(route);
            } while (cursor2.moveToNext());
        }
        cursor2.close();

        sqLiteDatabase.close();
        return arrayList;
    }

    public int getRouteId(int place1, int place2) {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        Cursor cursor = sqLiteDatabase.query(TABLE_NAME, null, "PLACE_ONE=? AND PLACE_TWO=? ", new String[]{place1 + "", place2 + ""}, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                Route route = new Route();
                return cursor.getInt(cursor.getColumnIndex("ROUTE_ID"));
            } while (cursor.moveToNext());
        }
        cursor.close();

        Cursor cursor2 = sqLiteDatabase.query(TABLE_NAME, null, " PLACE_TWO = ? AND PLACE_ONE = ? ", new String[]{place1 + "", place2 + ""}, null, null, null);
        if (cursor2.moveToFirst()) {
            do {
                Route route = new Route();
                return -cursor2.getInt(cursor2.getColumnIndex("ROUTE_ID"));
            } while (cursor2.moveToNext());
        }
        cursor2.close();
        sqLiteDatabase.close();
        return 0;
    }
}
