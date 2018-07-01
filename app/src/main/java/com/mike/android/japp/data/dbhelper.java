package com.mike.android.japp.data;

/**
 * Created by LENOVO V310 on 7/1/2018.
 */
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.mike.android.japp.data.dbcontract.Entry;

public class dbhelper extends SQLiteOpenHelper {

    public static final String LOG_TAG = dbhelper.class.getSimpleName();
    private static final String DATABASE_NAME = "shelter.db";
    private static final int DATABASE_VERSION = 1;

    public dbhelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String SQL_CREATE_PETS_TABLE =  "CREATE TABLE " + Entry.TABLE_NAME + " ("
                + Entry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + Entry.COLUMN_ENTRY_TITLE + " TEXT NOT NULL, "
                + Entry.COLUMN_ENTRY + " TEXT);";

        // Execute the SQL statement
        db.execSQL(SQL_CREATE_PETS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
// this is the first version
    }
}
