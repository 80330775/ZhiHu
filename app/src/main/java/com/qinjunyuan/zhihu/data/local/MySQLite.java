package com.qinjunyuan.zhihu.data.local;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MySQLite extends SQLiteOpenHelper {
    private static final String LATEST_NEWS = "create table mainPage("
            + "id integer primary key autoincrement,"
            + "mId integer,"
            + "time integer,"
            + "date text,"
            + "type text,"
            + "itemImage text,"
            + "itemTitle text,"
            + "contentTitle text,"
            + "contentImage text,"
            + "contentSource text,"
            + "content text)";

    private static final String SKID_MENU = "create table skidMenu("
            + "id integer primary key autoincrement,"
            + "mId integer,"
            + "name text)";

    public MySQLite(Context context) {
        super(context, "ZhiHu.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(LATEST_NEWS);
        db.execSQL(SKID_MENU);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists mainPage");
        db.execSQL("drop table if exists skidMenu");
        onCreate(db);
    }
}
