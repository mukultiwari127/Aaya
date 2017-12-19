package com.ideotic.edioticideas.aaya;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Shubham on 12-03-2016.
 */


public class DataBaseNotes {

    private final String KEY_ID = "ID";
    private final String KEY_TITLE = "title";
    private final String KEY_BODY = "body";
    private final String KEY_PRIORITY = "priority";


    private final String DATABASE_NAME = "MYNOTES";
    private final String TABLENAME = "NOTES";
    private final int VERSION = 1;

    private final String QUERY = "create table " + TABLENAME + " (" +
            KEY_ID + " integer PRIMARY KEY autoincrement DEFAULT 0, " +
            KEY_TITLE + " text, " +
            KEY_PRIORITY + " text, " +
            KEY_BODY + " text);";

    private final Context ourContext;


    DataBaseNotes(Context context) {
        ourContext = context;
    }

    private OurHelper ourHelper;
    private SQLiteDatabase ourDataBase;


    class OurHelper extends SQLiteOpenHelper {
        public OurHelper(Context context) {
            super(context, DATABASE_NAME, null, VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {

            db.execSQL(QUERY);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

            db.execSQL("DROP TABELE " + TABLENAME);
            onCreate(db);
        }
    }


    public void createEntry(String title, String body, String priority) throws SQLException {
        ourHelper = new OurHelper(ourContext);
        ourDataBase = ourHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(KEY_BODY, body);
        cv.put(KEY_PRIORITY, priority);
        cv.put(KEY_TITLE, title);
        ourDataBase.insert(TABLENAME, null, cv);
        ourHelper.close();
    }

    public List<SingleRow> getROWs() {
        ourHelper = new OurHelper(ourContext);
        ourDataBase = ourHelper.getWritableDatabase();
        List<SingleRow> list = new ArrayList<>();
        String[] columns = new String[]{KEY_ID, KEY_BODY, KEY_TITLE, KEY_PRIORITY};
        Cursor c = ourDataBase.query(TABLENAME, columns, null, null, null, null, null);
        String ID = null, title = null, body = null, priority = null;
        while (c.moveToNext()) {
            ID = String.valueOf(c.getInt(0));
            body = c.getString(1);
            title = c.getString(2);
            priority = c.getString(3);
            list.add(new SingleRow(ID, priority, title, body));
        }
        ourHelper.close();
        if (list.get(0) == null)
            list.add(new SingleRow("0", "0", "no item", ""));
        return list;

    }

}



