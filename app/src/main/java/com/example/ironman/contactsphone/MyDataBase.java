package com.example.ironman.contactsphone;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Ironman on 06.12.2016.
 */
public class MyDataBase extends SQLiteOpenHelper {

    public MyDataBase(Context context, String dbname, SQLiteDatabase.CursorFactory factory, int dbversion) {
        super(context, dbname, factory, dbversion);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table  tableContactsPhone(ID INTEGER PRIMARY KEY ,PHONE TEXT,BLOCKPHONE TEXT);");
        db.execSQL("create table  block(ID INTEGER PRIMARY KEY ,number TEXT);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

}
