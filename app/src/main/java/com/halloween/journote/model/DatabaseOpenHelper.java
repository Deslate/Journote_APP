package com.halloween.journote.model;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

public class DatabaseOpenHelper extends SQLiteOpenHelper {

    public static final String CREATE_ITEM="create table Item("
            +"id integer primary key autoincrement,"
            +"title text,"
            +"contentPath text,"
            +"labels text,"
            +"itemAdditions text)";
    public static final String CREATE_RECORD="create table Record("
            +"recordNumber integer primary key,"
            +"editDate text,"
            +"editorUserId text,"
            +"contentPath text,"
            +"description text)";
    public static final String CREATE_LABEL="create table Label("
            +"labelName primary key,"
            +"properties text)";
    public static final String CREATE_SETTING="create table Setting("
            +"setting text primary key,"
            +"status text)";

    public DatabaseOpenHelper(Context context) {
        super(context, "Journote.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO 构建数据库
        db.execSQL(CREATE_ITEM);
        db.execSQL(CREATE_RECORD);
        db.execSQL(CREATE_LABEL);
        db.execSQL(CREATE_SETTING);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {


    }


}
