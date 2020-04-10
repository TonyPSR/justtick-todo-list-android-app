package com.myapp.tonypsr.to_doapp;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by TonyPSR on 24-02-2018.
 */

public class ToDoDatabaseHelper extends SQLiteOpenHelper {

    private String DATABASE_NAME;
    private String TABLE_NAME = "taskTable";
    private String TABLE_ID = "_id";
    private String TABLE_TASK = "task";
    private String TABLE_DATE = "date";
    private String TABLE_STATUS = "status";


    public String getTABLE_ID() {
        return TABLE_ID;
    }

    public String getTABLE_TASK() {
        return TABLE_TASK;
    }

    public String getTABLE_DATE() {
        return TABLE_DATE;
    }

    public String getTABLE_STATUS() {
        return TABLE_STATUS;
    }




    public ToDoDatabaseHelper(Context context, String DATABASE_NAME) {
        super(context, DATABASE_NAME, null, 4);
    }

    public String getDATABASE_NAME(){
        return DATABASE_NAME;
    }
    public String getTABLE_NAME(){
        return TABLE_NAME;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE "+ TABLE_NAME +" ("+TABLE_ID+" NUMBER PRIMARY KEY,"+TABLE_TASK+" TEXT, "+TABLE_DATE+" TEXT, "+TABLE_STATUS+" TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "+ TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
