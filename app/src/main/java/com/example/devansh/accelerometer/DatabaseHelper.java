package com.example.devansh.accelerometer;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Devansh on 23-06-2017.
 */

public class DatabaseHelper extends SQLiteOpenHelper {

    long rowid;
    float result[] = {0,0,0};
    int pid;
    int cnt;

    private static final String DATABASE_NAME = "Accel.db";
    private static final int DATABASE_VERSION = 1;

    protected static final String TABLE_Acc = "Accelerometer";//Table name

    //Columns name
    protected static final String id = "SNo";
    protected static final String XAxis = "XAxis";
    protected static final String YAxis = "YAxis";
    protected static final String ZAxis = "ZAxis";

    private static DatabaseHelper sInstance;

    public static synchronized DatabaseHelper getInstance(Context context) {
        // Use the application context, which will ensure that you
        // don't accidentally leak an Activity's context.
        // See this article for more information: http://bit.ly/6LRzfx
        if (sInstance == null) {
            sInstance = new DatabaseHelper(context.getApplicationContext());
        }
        return sInstance;
    }

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_Acc_Table = "CREATE TABLE "
                + TABLE_Acc + "( " + id + " INTEGER PRIMARY KEY, " + XAxis
                + " REAL, " + YAxis
                + " REAL, " + ZAxis + " REAL);";

        db.execSQL(CREATE_Acc_Table);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion != newVersion) {
            // Simplest implementation is to drop all old tables and recreate them
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_Acc);
            onCreate(db);
        }
    }

    public class insert implements Runnable{
        float q,w,e;

        public insert(float deltaX, float deltaY, float deltaZ) {
            this.q = deltaX;
            this.w = deltaY;
            this.e = deltaZ;
        }

        @Override
        public void run() {
            android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
            SQLiteDatabase db = getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(XAxis, q);
            values.put(YAxis, w);
            values.put(ZAxis, e);
            rowid = db.insertOrThrow(TABLE_Acc, null, values);
        }
    }

    public class getAll implements Runnable{

        @Override
        public void run() {
            android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
            String Accelerometer_SELECT_QUERY =
                    String.format("SELECT %s,%s,%s,%s FROM %s;",
                            XAxis, YAxis, ZAxis, id, TABLE_Acc);

            SQLiteDatabase db = getReadableDatabase();
            Cursor cursor = db.rawQuery(Accelerometer_SELECT_QUERY, null);
            cnt = cursor.getCount();
            cursor.moveToLast();
            result[0]= cursor.getFloat(cursor.getColumnIndex(XAxis));
            result[1]= cursor.getFloat(cursor.getColumnIndex(YAxis));
            result[2]= cursor.getFloat(cursor.getColumnIndex(ZAxis));
            cursor.moveToFirst();
            pid = cursor.getInt(cursor.getColumnIndex(id));
            cursor.close();
        }
    }

    public class deleteFirstRow implements Runnable{

        @Override
       public void run(){
            android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
                SQLiteDatabase db = getReadableDatabase();
                Cursor cursor = db.query(TABLE_Acc, null, null, null, null, null, null);

                if(cursor.moveToFirst()) {
                    String rowId = cursor.getString(cursor.getColumnIndex(id));

                    db.delete(TABLE_Acc, id + "=?",  new String[]{rowId});
                }
                cursor.close();
        }
    }

}
