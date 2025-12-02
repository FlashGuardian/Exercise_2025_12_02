package com.example.exercise_2025_12_02;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ToDoRepo extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "Todo.db";
    private static final String TODO_TABLE_NAME = "todo";
    private static final String COLUMN_TITLE = "title";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_DESC = "desc";
    private static final String COLUMN_IS_DONE = "is_done";
    private static final String COLUMN_DEADLINE = "deadline";
    private static final String CONTACT_TABLE_NAME = "contact";
    private static final String COLUMN_PHONE = "phone";
    private static final String COLUMN_TODO_ID = "todo_id";
    private static final String SQL_CREATE_TODO_TABLE =
            "CREATE TABLE " + TODO_TABLE_NAME + "( " +
                    COLUMN_ID + "INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_TITLE + "TEXT, " +
                    COLUMN_DESC + "TEXT, " +
                    COLUMN_IS_DONE + "BOOLEAN, " +
                    COLUMN_DEADLINE + "TEXT " +//format HH:mm dd/MM/yyyy
            " );";
    private static final String SQL_DELETE_TODO_TABLE = "DROP TABLE IF EXISTS " + TODO_TABLE_NAME;
    private static final String SQL_DELETE_CONTACT_TABLE = "DROP TABLE IF EXISTS " + CONTACT_TABLE_NAME;
    private static final String SQL_CREATE_CONTACT_TABLE =
            "CREATE TABLE " + CONTACT_TABLE_NAME + "( " +
                    COLUMN_TODO_ID + "INT, " +
                    COLUMN_PHONE + "TEXT, " +
                    "PRIMARY KEY (" + COLUMN_TODO_ID + ", " + COLUMN_PHONE + "), " +
                    "FOREIGN KEY (" + COLUMN_TODO_ID + ") REFERENCES " + TODO_TABLE_NAME + "(" + COLUMN_ID + ")" +
            " );";

    public ToDoRepo(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_TODO_TABLE);
        db.execSQL(SQL_CREATE_CONTACT_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion,
                          int newVersion) {
        db.execSQL(SQL_DELETE_TODO_TABLE);
        db.execSQL(SQL_DELETE_CONTACT_TABLE);
        onCreate(db);
    }

    public void addNew(TodoItem item) {
        SQLiteDatabase db = this.getWritableDatabase();
        int itemID = item.getItemID();
        //todo_table
        ContentValues todoValues = new ContentValues();
        todoValues.put(COLUMN_ID, itemID);
        todoValues.put(COLUMN_TITLE, item.getTitle());
        todoValues.put(COLUMN_DESC, item.getDescription());
        todoValues.put(COLUMN_IS_DONE, item.getStatus() ? 1 : 0);
        todoValues.put(COLUMN_DEADLINE, item.getDeadline().format(item.getDateTimeFormatter()));
        db.insert(TODO_TABLE_NAME, null, todoValues);
        //contact_table
        for (String contact : item.getRelatedContacts()) {
            ContentValues contactValues = new ContentValues();
            contactValues.put(COLUMN_TODO_ID, itemID);
            contactValues.put(COLUMN_PHONE, contact);
            db.insert(CONTACT_TABLE_NAME, null, contactValues);
        }
        db.close();
    }
}
