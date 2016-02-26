package com.jack.ImageGallery.MyUtil;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {
    public static final String nameDB = "FavouritesDB";
    public static final int curVersionDB = 1;

    public static final String tableFavourites = "favourites";
    public static final String columnID = "id";
    public static final String columnImageId = "image_id";
    public static final String columnComment = "comment";

    public DBHelper(Context context) {
        super(context, nameDB, null, curVersionDB);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table "+ tableFavourites +" ("
                + columnID + " integer primary key autoincrement,"
                + columnImageId + " integer,"
                + columnComment + " text" + ");");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // For future
    }
}