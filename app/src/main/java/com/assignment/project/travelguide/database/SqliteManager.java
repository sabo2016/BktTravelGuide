package com.assignment.project.travelguide.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;

import com.assignment.project.travelguide.database.model.Place;
import com.assignment.project.travelguide.database.repository.PlaceRepository;
import com.assignment.project.travelguide.database.repository.Repository;
import com.assignment.project.travelguide.database.repository.RouteDetailRepository;
import com.assignment.project.travelguide.database.repository.RouteRepository;

import java.io.File;

/**
 * Created by ukesh on 3/20/17.
 */

public class SqliteManager extends SQLiteOpenHelper {
    private static SqliteManager sqliteManager = null;
    private static final String DATABASE_PATH = Environment.getExternalStorageDirectory() + File.separator;
    private static final String DATABASE_NAME = "travel_guide.db";

    private SqliteManager(Context context) {
        super(context, DATABASE_PATH + DATABASE_NAME, null, 1);
    }

    public static SqliteManager getInstance(Context context) {
        if (sqliteManager == null) {
            sqliteManager = new SqliteManager(context);
        }
        return sqliteManager;
    }


    @Override
    public void onCreate(SQLiteDatabase db) {

        try {
            db.execSQL(PlaceRepository.CREATE_QUERY);
            db.execSQL(RouteRepository.CREATE_QUERY);
            db.execSQL(RouteDetailRepository.CREATE_QUERY);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        try {
            db.execSQL(PlaceRepository.DROP_QUERY);
            db.execSQL(RouteRepository.DROP_QUERY);
            db.execSQL(RouteDetailRepository.DROP_QUERY);

            onCreate(db);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
