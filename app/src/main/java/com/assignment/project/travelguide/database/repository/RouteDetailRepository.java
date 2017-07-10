package com.assignment.project.travelguide.database.repository;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.assignment.project.travelguide.database.model.BaseModel;
import com.assignment.project.travelguide.database.model.Route;
import com.assignment.project.travelguide.database.model.RouteDetail;

import java.util.ArrayList;

/**
 * Created by ukesh on 3/22/17.
 */

public class RouteDetailRepository extends Repository {

    public static final String TABLE_NAME = "ROUTE_DETAIL";
    public static final String CREATE_QUERY = "CREATE TABLE " +
            TABLE_NAME +
            " ( " +
            " _ID INTEGER NOT NULL, " +
            " ROUTE_ID INTEGER NOT NULL, " +
            " LATITUDE DOUBLE NOT NULL, " +
            " LONGITUDE DOUBLE NOT NULL " +
            " )";
    public static final String DROP_QUERY = "DROP TABLE " + TABLE_NAME;

    public RouteDetailRepository(Context context) {
        super(context);
    }

    @Override
    public long insert(BaseModel baseModel) {
        RouteDetail routeDetail = (RouteDetail) baseModel;
        ContentValues contentValues = new ContentValues();
        contentValues.put("LATITUDE", routeDetail.getLatitude());
        contentValues.put("LONGITUDE", routeDetail.getLongitude());
        contentValues.put("ROUTE_ID", routeDetail.getRouteId());


        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        long result = sqLiteDatabase.insertOrThrow(PlaceRepository.TABLE_NAME, null, contentValues);
        sqLiteDatabase.close();
        return result;
    }

    @Override
    public ArrayList<BaseModel> select(BaseModel baseModel) {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();

        ArrayList<BaseModel> arrayList = new ArrayList<BaseModel>();
        RouteDetail route = (RouteDetail) baseModel;

        Cursor cursor = sqLiteDatabase.query(TABLE_NAME, null, "ROUTE_ID=? ", new String[]{String.valueOf(route.getRouteId())}, null, null, "_ID");
        if (cursor.moveToFirst()) {
            do {
                RouteDetail route1 = new RouteDetail();

                route1.setLatitude(cursor.getDouble(cursor.getColumnIndex("LATITUDE")));
                route1.setLongitude(cursor.getDouble(cursor.getColumnIndex("LONGITUDE")));

                arrayList.add(route1);
            } while (cursor.moveToNext());
        }
        cursor.close();
        sqLiteDatabase.close();

        return arrayList;
    }

    public ArrayList<RouteDetail> selectQuery(int routeId) {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();

        ArrayList<RouteDetail> arrayList = new ArrayList<RouteDetail>();

        Cursor cursor = sqLiteDatabase.query(TABLE_NAME, null, "ROUTE_ID=? ", new String[]{String.valueOf(Math.abs(routeId))}, null, null, "_ID" + " " + ((0 < routeId) ? "ASC" : "DESC"));
        if (cursor.moveToFirst()) {
            do {
                RouteDetail route1 = new RouteDetail();

                route1.setLatitude(cursor.getDouble(cursor.getColumnIndex("LATITUDE")));
                route1.setLongitude(cursor.getDouble(cursor.getColumnIndex("LONGITUDE")));

                arrayList.add(route1);
            } while (cursor.moveToNext());
        }
        cursor.close();
        sqLiteDatabase.close();

        return arrayList;
    }

}
