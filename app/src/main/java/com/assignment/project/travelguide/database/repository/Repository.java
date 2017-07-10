package com.assignment.project.travelguide.database.repository;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.assignment.project.travelguide.database.SqliteManager;
import com.assignment.project.travelguide.database.model.BaseModel;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by ukesh on 3/21/17.
 */

public abstract class Repository {


    private Context context;

    public Repository(Context context) {
        this.context = context;
    }

    public SQLiteDatabase getWritableDatabase() {
        return SqliteManager.getInstance(context).getWritableDatabase();
    }

    public long insert(BaseModel baseModel) {
        return -1;
    }

    public boolean update(BaseModel baseModel) {
        return false;
    }

    public ArrayList<BaseModel> select(BaseModel baseModel) {
        return null;
    }

    public boolean delete(BaseModel baseModel) {
        return false;
    }


}
