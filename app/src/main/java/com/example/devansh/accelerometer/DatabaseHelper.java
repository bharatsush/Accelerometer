package com.example.devansh.accelerometer;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * Created by Devansh on 23-06-2017.
 */

public class DatabaseHelper extends SQLiteOpenHelper {

    //long rowid;
    float result[] = {0,0,0};
    //float res[] = {0,0,0};
    //int pid;
    //String tm;
    int cnt;

    private static final String DATABASE_NAME = "Accel.db";
    private static final int DATABASE_VERSION = 1;

    protected static final String TABLE_Acc = "Accelerometer";
    protected static final String TABLE_Ana = "Analysis";//Table name

    //Columns name
    protected static final String id = "SNo";
    protected static final String XAxis = "XAxis";
    protected static final String YAxis = "YAxis";
    protected static final String ZAxis = "ZAxis";

    protected static final String id1 = "SNo";
    protected static final String XAxis1 = "XAxis";
    protected static final String YAxis1 = "YAxis";
    protected static final String ZAxis1 = "ZAxis";
    protected static final String time = "Time";

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

        String CREATE_Ana_Table = "CREATE TABLE "
                + TABLE_Ana + "( " + id1 + " INTEGER PRIMARY KEY, " + XAxis1
                + " REAL, " + YAxis1
                + " REAL, " + ZAxis1 + " REAL, " + time + " TEXT);";

        db.execSQL(CREATE_Acc_Table);
        db.execSQL(CREATE_Ana_Table);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion != newVersion) {
            // Simplest implementation is to drop all old tables and recreate them
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_Acc);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_Ana);
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
            db.insertOrThrow(TABLE_Acc, null, values);
        }
    }

    public class getAll implements Runnable {

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
            result[0] = cursor.getFloat(cursor.getColumnIndex(XAxis));
            result[1] = cursor.getFloat(cursor.getColumnIndex(YAxis));
            result[2] = cursor.getFloat(cursor.getColumnIndex(ZAxis));
            //cursor.moveToFirst();
            //pid = cursor.getInt(cursor.getColumnIndex(id));
            cursor.close();
        }
    }

    public class getsome implements Runnable {

        public void run() {
            android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
            float r[] = {0, 0};
            float s[] = {0, 0};
            float t[] = {0, 0};
            SQLiteDatabase db = getReadableDatabase();
            String Accelerometer_SELECT_QUERY =
                    String.format("SELECT %s,%s,%s,%s FROM %s;",
                            XAxis, YAxis, ZAxis, id, TABLE_Acc);
            Cursor cursor = db.rawQuery(Accelerometer_SELECT_QUERY, null);
            int count = cursor.getCount();
            int i = 0;
            cursor.moveToPosition(count - 2);
            do {
                r[i] = cursor.getFloat(cursor.getColumnIndex(XAxis));
                s[i] = cursor.getFloat(cursor.getColumnIndex(YAxis));
                t[i] = cursor.getFloat(cursor.getColumnIndex(ZAxis));
                i++;
            } while (cursor.moveToNext());
            float xa = r[0] - r[1];
            float ya = s[0] - s[1];
            float za = t[0] - t[1];
            if (xa >= 2 || xa <= -2 || ya >= 2 || ya <= -2 || za >= 2 || za <= -2) {
                input(r[1], s[1], t[1]);
            }
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

    void input(float xb, float yb, float zb){
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String currentDateTime = dateFormat.format(new Date());
        SQLiteDatabase datab = getWritableDatabase();
        ContentValues values1 = new ContentValues();
        values1.put(XAxis1, xb);
        values1.put(YAxis1, yb);
        values1.put(ZAxis1, zb);
        values1.put(time,currentDateTime);
        datab.insert(TABLE_Ana, null, values1);
    }


    /*public void qry() {

        String Accelerometer_SELECT_QUERY1 =
                String.format("SELECT %s,%s,%s,%s,%s FROM %s;",
                        XAxis1, YAxis1, ZAxis1, id1, time, TABLE_Ana);

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(Accelerometer_SELECT_QUERY1, null);
        cnt = cursor.getCount();
        cursor.moveToLast();
        res[0] = cursor.getFloat(cursor.getColumnIndex(XAxis1));
        res[1] = cursor.getFloat(cursor.getColumnIndex(YAxis1));
        res[2] = cursor.getFloat(cursor.getColumnIndex(ZAxis1));
        tm = cursor.getString(cursor.getColumnIndex(time));
        cursor.moveToFirst();
        pid = cursor.getInt(cursor.getColumnIndex(id1));
        cursor.close();
    } */
}
