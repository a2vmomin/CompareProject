package com.example.alif.compareproject2;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.nio.CharBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alif on 15-Jul-15.
 */
public class DatabaseAdapter {

    DatabaseHelper helper;

    private Context context;

    public DatabaseAdapter(Context context) {
        this.context = context;
        helper = new DatabaseHelper(context);
    }


    public long insertData(String winner, String loser, int tie, int winner_result, int loser_result, String winner_image, String loser_image){
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseHelper.WINNER, winner);
        contentValues.put(DatabaseHelper.LOSER, loser);
        contentValues.put(DatabaseHelper.TIE, tie);
        contentValues.put(DatabaseHelper.WINNERRESULT, winner_result);
        contentValues.put(DatabaseHelper.LOSERRESULT, loser_result);
        contentValues.put(DatabaseHelper.WINNERIMAGE, winner_image);
        contentValues.put(DatabaseHelper.LOSERIMAGE, loser_image);
        long id = db.insert(DatabaseHelper.TABLE_NAME, null, contentValues);
        Log.d("ID value", String.valueOf(id));
        return id;
    }

    public void deleteData(int position)
    {
        SQLiteDatabase db = helper.getWritableDatabase();
        db.delete(DatabaseHelper.TABLE_NAME, DatabaseHelper.UID + "=" + position, null);
    }

    public List<Information> getAllData()
    {
        SQLiteDatabase db = helper.getWritableDatabase();

        String[] columns = {DatabaseHelper.UID, DatabaseHelper.WINNER, DatabaseHelper.LOSER, DatabaseHelper.WINNERRESULT, DatabaseHelper.LOSERRESULT, DatabaseHelper.WINNERIMAGE, DatabaseHelper.LOSERIMAGE};
        Cursor cursor=db.query(DatabaseHelper.TABLE_NAME, columns, null, null, null, null, null);
        List<Information> data = new ArrayList<>();

        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.UID));
            String winner = cursor.getString(cursor.getColumnIndex(DatabaseHelper.WINNER));
            String loser = cursor.getString(cursor.getColumnIndex(DatabaseHelper.LOSER));
            int winnerResult = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.WINNERRESULT));
            int loserResult = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.LOSERRESULT));
            String winnerImage = cursor.getString(cursor.getColumnIndex(DatabaseHelper.WINNERIMAGE));
            String loserImage = cursor.getString(cursor.getColumnIndex(DatabaseHelper.LOSERIMAGE));
            Information current = new Information();
            current.id = id;
            current.resultText1 = winner;
            current.resultText2 = loser;
            current.winnerTotal = winnerResult;
            current.loserTotal = loserResult;
            OnlineChecking onlineChecking = new OnlineChecking(context);
            if(onlineChecking.isOnline())
            {
                current.winnerImage = winnerImage;
                current.loserImage = loserImage;
            }
            data.add(current);
        }
        return data;
    }

    static class DatabaseHelper extends SQLiteOpenHelper {
        private static final String DATABASE_NAME = "comparedatabase";
        private static final String TABLE_NAME = "COMPARETABLE";
        private static final int DATABASE_VERSION = 3;
        private static final String UID = "_id";
        private static final String WINNER = "winner";
        private static final String LOSER = "loser";
        private static final String TIE = "tie";
        private static final String WINNERRESULT = "winnerResult";
        private static final String LOSERRESULT = "loserResult";
        private static final String WINNERIMAGE = "winnerImage";
        private static final String LOSERIMAGE = "loserImage";
        private static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " (" + UID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + WINNER + " VARCHAR(255) NOT NULL, " + LOSER + " VARCHAR(255) NOT NULL, " + TIE + " INTEGER DEFAULT 0, " + WINNERRESULT + " INTEGER NOT NULL, " + LOSERRESULT + " INTEGER NOT NULL, "+ WINNERIMAGE +" VARCHAR(1000), "+ LOSERIMAGE +" VARCHAR(1000));";
        private static final String DROP_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
        private Context context;

        public DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
            this.context = context;
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            //CREATE TABLE COMPARETABLE (_id INTEGER PRIMARY KEY AUTOINCREMENT, winner VARCHAR(255) NOT NULL, loser VARCHAR(255) NOT NULL, tie INTEGER DEFAULT 0, winnerResult INTEGER NOT NULL, loserResult INTEGER NOT NULL, winnerImage VARCHAR(1000), loserImage VARCHAR(1000));
            try {
                db.execSQL(CREATE_TABLE);
                //Message.message(context, "DB succ");
            } catch (SQLException e) {
                Message.message(context, "" + e);
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            try {
                db.execSQL(DROP_TABLE);
                onCreate(db);
            } catch (SQLException e) {
                Message.message(context, ""+e);
            }
        }
    }


}
