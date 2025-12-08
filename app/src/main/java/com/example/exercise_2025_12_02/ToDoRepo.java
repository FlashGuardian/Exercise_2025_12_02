package com.example.exercise_2025_12_02;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class ToDoRepo extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "Todo.db";
    private static final String TODO_TABLE_NAME = "todo";
    private static final String COLUMN_TITLE = "title";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_DESCR = "descr";
    private static final String COLUMN_IS_DONE = "is_done";
    private static final String COLUMN_DEADLINE = "deadline";
    private static final String CONTACT_TABLE_NAME = "contact";
    private static final String COLUMN_PHONE = "phone";
    private static final String COLUMN_TODO_ID = "todo_id";
    private static final String COLUMN_CONTACT_ID = "contact_id";
    private static final String SQL_CREATE_TODO_TABLE =
            "CREATE TABLE " + TODO_TABLE_NAME + "( " +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_TITLE + " TEXT, " +
                    COLUMN_DESCR + " TEXT, " +
                    COLUMN_IS_DONE + " BOOLEAN, " +
                    COLUMN_DEADLINE + " TEXT " +//format HH:mm dd/MM/yyyy
            " );";
    private static final String SQL_DELETE_TODO_TABLE = "DROP TABLE IF EXISTS " + TODO_TABLE_NAME;
    private static final String SQL_DELETE_CONTACT_TABLE = "DROP TABLE IF EXISTS " + CONTACT_TABLE_NAME;
    private static final String SQL_CREATE_CONTACT_TABLE =
            "CREATE TABLE " + CONTACT_TABLE_NAME + "( " +
                    COLUMN_CONTACT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_TODO_ID + " INTEGER, " +
                    COLUMN_PHONE + " TEXT, " +
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
        todoValues.put(COLUMN_DESCR, item.getDescription());
        todoValues.put(COLUMN_IS_DONE, item.getStatus() ? 1 : 0);
        todoValues.put(COLUMN_DEADLINE, item.getDeadline().format(item.getDateTimeFormatter()));
        db.insert(TODO_TABLE_NAME, null, todoValues);
        //contact_table
        ArrayList<String> relatedContacts = item.getRelatedContacts();
        if (relatedContacts != null) {
            for (String contact : item.getRelatedContacts()) {
                ContentValues contactValues = new ContentValues();
                contactValues.put(COLUMN_TODO_ID, itemID);
                contactValues.put(COLUMN_PHONE, contact);
                db.insert(CONTACT_TABLE_NAME, null, contactValues);
            }
        }
        db.close();
    }

    public boolean update(int itemID, TodoItem item) {
        SQLiteDatabase db = this.getWritableDatabase();
        //todo_table
        ContentValues todoValues = new ContentValues();
        todoValues.put(COLUMN_TITLE, item.getTitle());
        todoValues.put(COLUMN_DESCR, item.getDescription());
        todoValues.put(COLUMN_IS_DONE, item.getStatus() ? 1 : 0);
        todoValues.put(COLUMN_DEADLINE, item.getDeadline().format(item.getDateTimeFormatter()));
        db.insert(TODO_TABLE_NAME, null, todoValues);
        int rowAffected = db.update(TODO_TABLE_NAME, todoValues,
                COLUMN_ID + "= ?", new String[]{String.valueOf(itemID)});
        //contact_table //todo proper logic for updating contacts
        db.delete(CONTACT_TABLE_NAME, COLUMN_TODO_ID + "= ?", new String []{String.valueOf(itemID),});
        ArrayList<String> relatedContacts = item.getRelatedContacts();
        if (relatedContacts != null) {
            for (String contact : item.getRelatedContacts()) {
                ContentValues contactValues = new ContentValues();
                contactValues.put(COLUMN_TODO_ID, itemID);
                contactValues.put(COLUMN_PHONE, contact);
                db.insert(CONTACT_TABLE_NAME, null, contactValues);
            }
        }

        db.close();
        return rowAffected > 0;
    }

    public boolean delete(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        //todo_table
        int rowAffected = db.delete(TODO_TABLE_NAME, COLUMN_ID + "= ?",
                new String[]{String.valueOf(id)});

        //contact_table
        db.delete(CONTACT_TABLE_NAME, COLUMN_TODO_ID + "= ?",
                new String[]{String.valueOf(id)});
        db.close();
        return rowAffected > 0;
    }

    public ArrayList<TodoItem> loadAll() {
        SQLiteDatabase db = this.getWritableDatabase();
        String[] projection = {
                COLUMN_ID,
                COLUMN_TITLE,
                COLUMN_DESCR,
                COLUMN_IS_DONE,
                COLUMN_DEADLINE,
        };
        Cursor cursor = db.query(TODO_TABLE_NAME, projection, null,
                null, null, null, null);
        final TodoItem justToGetFormatter = new TodoItem(-1, "", "", LocalDateTime.now());
        ArrayList<TodoItem> items = new ArrayList<TodoItem>();
        while (cursor.moveToNext()) {
            int id = cursor.getInt(0);
            String title = cursor.getString(1);
            String desc = cursor.getString(2);
            Boolean isDone = cursor.getInt(3)==1;
            LocalDateTime deadline = LocalDateTime.parse(cursor.getString(4),justToGetFormatter.getDateTimeFormatter());

            //get related contacts
            String[] contactProjections ={
                    COLUMN_PHONE
            };
            Cursor contactCursor = db.query(CONTACT_TABLE_NAME, contactProjections, COLUMN_TODO_ID + "= ?",
                    new String[] {String.valueOf(id)}, null, null, null);
            ArrayList<String> relatedContacts = new ArrayList<>();
            while (cursor.moveToNext()) {
                String phone = cursor.getString(2);
                relatedContacts.add(phone);
            }
            contactCursor.close();

            items.add(new TodoItem(id, title, desc,deadline, isDone, relatedContacts));
        }
        cursor.close();
        db.close();
        return items;
    }

    public TodoItem getById(int id){
        SQLiteDatabase db = this.getWritableDatabase();
        String[] projection = {
                COLUMN_ID,
                COLUMN_TITLE,
                COLUMN_DESCR,
                COLUMN_IS_DONE
        };
        Cursor cursor = db.query(TODO_TABLE_NAME, projection,
                COLUMN_ID + "= ?", new String[]{String.valueOf(id)}, null, null, null);
        final TodoItem justToGetFormatter = new TodoItem(-1, "", "", LocalDateTime.now());
        ArrayList<TodoItem> items = new ArrayList<TodoItem>();
        if(cursor.moveToFirst()){
            String title = cursor.getString(1);
            String desc = cursor.getString(2);
            Boolean isDone = cursor.getInt(3)==1;
            LocalDateTime deadline = LocalDateTime.parse(cursor.getString(4),justToGetFormatter.getDateTimeFormatter());

            //get related contacts
            String[] contactProjections ={
                    COLUMN_PHONE
            };
            Cursor contactCursor = db.query(CONTACT_TABLE_NAME, contactProjections, COLUMN_TODO_ID + "= ?",
                    new String[] {String.valueOf(id)}, null, null, null);
            ArrayList<String> relatedContacts = new ArrayList<>();
            while (cursor.moveToNext()) {
                String phone = cursor.getString(2);
                relatedContacts.add(phone);
            }
            contactCursor.close();
            db.close();
            cursor.close();
            return new TodoItem(id, title, desc,deadline, isDone, relatedContacts);
        }
        return null;
    }
}
