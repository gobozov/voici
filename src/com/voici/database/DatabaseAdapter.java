package com.voici.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import com.voici.data.User;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: gb
 * Date: 11.01.13
 * Time: 23:33
 * To change this template use File | Settings | File Templates.
 */
public class DatabaseAdapter {

    public static final int VK = 1;
    public static final int GMAIL = 2;
    public static final int FACEBOOK = 3;


    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "SPEECHPRO";

    private static final String TABLE_USERS = "T_USERS";
    private static final String FIELD_ID = "ID";
    private static final String FIELD_LOGIN = "LOGIN";
    private static final String FIELD_PASSWORD = "PASSWORD";
    private static final String FIELD_KEY = "KEY";
    private static final String FIELD_SITE = "SITE";


    private static final String CREATE_TABLE = "create table " + TABLE_USERS + "(" +
            FIELD_ID + " integer primary key autoincrement, " +
            FIELD_LOGIN + " text not null, " +
            FIELD_PASSWORD + " text not null, " +
            FIELD_KEY + " text not null, " +
            FIELD_SITE + " integer not null)";


    private DatabaseHelper dbHelper;
    private SQLiteDatabase database;

    public DatabaseAdapter(Context context) {
        this.dbHelper = new DatabaseHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        if (dbHelper != null)
            dbHelper.close();
    }


    public boolean addUser(User user, int site) {
        try {
            ContentValues initialValues = new ContentValues();
            initialValues.put(FIELD_LOGIN, user.getName());
            initialValues.put(FIELD_PASSWORD, user.getPassword());
            initialValues.put(FIELD_KEY, user.getKey());
            initialValues.put(FIELD_SITE, site);
            database.insert(TABLE_USERS, null, initialValues);
            return true;
        } catch (SQLiteException e) {
            return false;
        }
    }


    public boolean deleteUser(User user) {
        try {
            Log.d("voici", "Delete user " + user.getName() + " id = " + user.getId());
            int result = database.delete(TABLE_USERS, FIELD_ID + "=?", new String[]{String.valueOf(user.getId())});
            Log.d("voici", "result = " + result);
            return true;
        } catch (SQLiteException e) {
            return false;
        }
    }

    public List<User> getAllUsers(int site) {
        List<User> users = new ArrayList<User>();
        Cursor cursor = database.query(true, TABLE_USERS, null, FIELD_SITE + "=?", new String[]{String.valueOf(site)}, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                User user = new User();
                user.setId(cursor.getInt(0));
                user.setName(cursor.getString(1));
                user.setPassword(cursor.getString(2));
                user.setKey(cursor.getString(3));
                users.add(user);
            } while (cursor.moveToNext());
            cursor.close();
        }

        return users;

    }


    public User getUserById(int id) {
        User user = null;
        Cursor cursor = database.query(true, TABLE_USERS, null, FIELD_ID + "=?", new String[]{String.valueOf(id)}, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                user = new User();
                user.setId(cursor.getInt(0));
                user.setName(cursor.getString(1));
                user.setPassword(cursor.getString(2));
                user.setKey(cursor.getString(3));
            } while (cursor.moveToNext());
            cursor.close();

        }
        return user;
    }

    public boolean updateUser(int id, User newUser) {
        try {
            ContentValues cv = new ContentValues();
            cv.put(FIELD_LOGIN, newUser.getName());
            cv.put(FIELD_PASSWORD, newUser.getPassword());
            cv.put(FIELD_KEY, newUser.getKey());
            database.update(TABLE_USERS, cv, FIELD_ID + "=?", new String[]{String.valueOf(id)});

            return true;
        } catch (SQLiteException e){
            return false;
        }



    }


    private class DatabaseHelper extends SQLiteOpenHelper {

        private DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {
            sqLiteDatabase.execSQL(CREATE_TABLE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
            sqLiteDatabase.execSQL("drop table if exists " + TABLE_USERS);
            onCreate(sqLiteDatabase);
        }
    }


}
