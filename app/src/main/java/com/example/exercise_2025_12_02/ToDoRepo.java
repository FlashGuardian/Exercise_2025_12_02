package com.example.exercise_2025_12_02;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ToDoRepo extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Todo.db";
    public static final String TABLE_NAME = "todos";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_DESC = "desc";
    public static final String COLUMN_IS_DONE = "is_done";
    public static final String COLUMN_DEADLINE = "deadline";
    public static final String SQL_CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + "( " +
                    COLUMN_ID + "INTERGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_TITLE + "TEXT, " +
                    COLUMN_DESC + "TEXT, " +
                    COLUMN_IS_DONE + "BOOLEAN, " +
                    COLUMN_DEADLINE + "TEXT " //format
            + " )";

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_TABLE);
    }
}
