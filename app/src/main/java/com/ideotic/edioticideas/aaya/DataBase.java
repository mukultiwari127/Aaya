package com.ideotic.edioticideas.aaya;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.sql.SQLException;

/**
 * Created by Shubham on 12-03-2016.
 */
class SingleRowReminder {
    String id;
    String tittle;
    String des;

    public SingleRowReminder(String id, String tittle, String des) {
        this.id = id;
        this.tittle = tittle;
        this.des = des;
    }
}

public class DataBase {

    private final String KEY_ID = "ID";
    private final String KEY_TITLE = "title";
    private final String KEY_DES = "des";




    private final String DATABASE_NAME = "REMINDER";
    private final String TABLENAME = "TITLE";
    private final int VERSION = 1;

    private final String QUERY = "create table " +TABLENAME+" ("+
            KEY_ID+" integer PRIMARY KEY autoincrement DEFAULT 0, "+
            KEY_TITLE + " text, " +
            KEY_DES + " text);";

    private final Context ourContext;


    DataBase(Context context) {
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

            db.execSQL("DROP TABELE "+TABLENAME);
            onCreate(db);
        }
    }

    public DataBase Open() throws SQLException {
        ourHelper = new OurHelper(ourContext);
        ourDataBase = ourHelper.getWritableDatabase();
        return this;

    }

    public void close() throws SQLException {
        ourHelper.close();
    }

    public long createEntry(String id,String title,String desc) throws SQLException{
        ContentValues cv = new ContentValues();
        cv.put(KEY_ID,id);
        cv.put(KEY_DES,desc);
        cv.put(KEY_TITLE,title);
        return ourDataBase.insert(TABLENAME,null,cv);
    }

    public SingleRowReminder getROW(String Id){
        String[] columns = new String[]{KEY_ID,KEY_DES,KEY_TITLE};
        Cursor c = ourDataBase.query(TABLENAME, columns,KEY_ID + " = "+Id, null, null, null, null);
        String ID= null,title = null, desc = null;
        while (c.moveToNext()) {
            if(!c.isAfterLast()) {
                ID = String.valueOf(c.getInt(0));
                desc = c.getString(1);
                title = c.getString(2);

            }
        }
        SingleRowReminder sr = new SingleRowReminder(ID,title,desc);
        //SingleRowReminder sr = null;
        return sr;
    }

}



