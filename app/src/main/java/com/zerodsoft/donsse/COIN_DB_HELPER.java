package com.zerodsoft.donsse;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;

import java.util.LinkedList;
import java.util.List;

public class COIN_DB_HELPER extends SQLiteOpenHelper
{
    private static COIN_DB_HELPER sInstance;
    private static final int DB_VERSION = 1;
    private static final String DB_NAME = "coindata.db";
    private static final String CHANGED_PATH = Environment.getExternalStorageDirectory() + "/com.zerodsoft.donsse/";
    private static final String CREATE_TABLE =
            "CREATE TABLE IF NOT EXISTS coins (" +
                    "_ID INTEGER primary key autoincrement," +
                    "DATE TEXT not null," +
                    "IMAGE_ADDRESS TEXT not null," +
                    "TEN_SMALL INTEGER not null," +
                    "TEN_BIG INTEGER not null," +
                    "FIFTY INTEGER not null," +
                    "ONEHUNDRED INTEGER not null," +
                    "FIVEHUNDRED INTEGER not null" +
                    ");";


    private static final String DROP_TABLE =
            "DROP TABLE IF EXISTS coins";

    public static synchronized COIN_DB_HELPER getInstance(Context context)
    {
        if (sInstance == null)
        {
            sInstance = new COIN_DB_HELPER(context.getApplicationContext());
        }
        return sInstance;
    }


    public COIN_DB_HELPER(Context context)
    {
        super(context, CHANGED_PATH + DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) //create table
    {
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) // 미사용
    {

    }

    public void deleteAll(SQLiteDatabase db) //drop table
    {
        db.execSQL(DROP_TABLE);
        onCreate(db);
    }

    public List<CardItem> datalist()
    {
        List<CardItem> datalinkedlist = new LinkedList<>();
        SQLiteDatabase DB = this.getReadableDatabase();
        Cursor cursor = DB.rawQuery("SELECT * FROM coins", null);
        CardItem carditem;

        if (cursor.moveToFirst())
        {
            do
            {
                carditem = new CardItem();
                carditem.setID(cursor.getInt(0));
                carditem.setDate(cursor.getString(1));
                carditem.setImageaddress(cursor.getString(2));
                carditem.setWon_10s(cursor.getInt(3));
                carditem.setWon_10b(cursor.getInt(4));
                carditem.setWon_50(cursor.getInt(5));
                carditem.setWon_100(cursor.getInt(6));
                carditem.setWon_500(cursor.getInt(7));
                datalinkedlist.add(carditem);
            } while (cursor.moveToNext());
        }

        return datalinkedlist;
    }
}
