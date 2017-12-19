package com.ideotic.edioticideas.aaya;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Mukul on 14-05-2016.
 */
public class AayaDatabase {

    //Database Variables
    public static final String USER_NAME = "users_name";
    public static final String USER_ADD = "users_address";
    public static final String USER_CONTACT = "user_contact";
    public static final String USER_DOB = "user_dateOfBirth";
    public static final String VISUALLY_IMPAIRED = "YES_NO";

    private static final String DATABASE_NAME = "aayaDatabase";
    private static final String TABLE_NAME = "users";
    private static final int DATABASE_VERSION = 1;

    private DbHelper helper;
    private final Context passedContext;
    private SQLiteDatabase database;


    private static class DbHelper extends SQLiteOpenHelper {

        //constructor
        public DbHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }


        @Override
        public void onCreate(SQLiteDatabase db) {
            String createDatabaseQuery = "CREATE  TABLE " + TABLE_NAME +
                    " (" + USER_NAME + " VARCHAR, " + USER_ADD + " VARCHAR, " + USER_CONTACT + " VARCHAR, "
                    + USER_DOB + " VARCHAR, " + VISUALLY_IMPAIRED + " VARCHAR)";
            db.execSQL(createDatabaseQuery);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
            onCreate(db);
        }
    }

    //constructor
    public AayaDatabase(Context c) {
        passedContext = c;
    }

    //method to open database
    public AayaDatabase open() throws SQLException {
        helper = new DbHelper(passedContext);
        database = helper.getWritableDatabase();
        return this;
    }

    //method to close database
    public void close() {
        helper.close();
    }

    //Creating Entry of row
    public long createEntry(String name, String address, String phone, String birth, String vImp) {
        ContentValues cv = new ContentValues();
        cv.put(USER_NAME, name);
        cv.put(USER_ADD, address);
        cv.put(USER_CONTACT, phone);
        cv.put(USER_DOB, birth);
        cv.put(VISUALLY_IMPAIRED, vImp);
        return database.insert(TABLE_NAME, null, cv);

    }

    public String getData() {
        String[] columns = new String[]{USER_NAME, USER_ADD, USER_CONTACT, USER_DOB, VISUALLY_IMPAIRED};
        Cursor c = database.query(TABLE_NAME, columns, null, null, null, null, null, null);
        String result = "";
        int iName = c.getColumnIndex(USER_NAME);
        int iAdd = c.getColumnIndex(USER_ADD);
        int iCon = c.getColumnIndex(USER_CONTACT);
        int iDob = c.getColumnIndex(USER_DOB);
        int iVimp = c.getColumnIndex(VISUALLY_IMPAIRED);

        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
            result = result + c.getString(iName) + " " + c.getString(iAdd) + " " + c.getString(iCon)
                    + " " + c.getString(iDob) + " " + c.getString(iVimp) + "\n";
        }
        return result;
    }
}
