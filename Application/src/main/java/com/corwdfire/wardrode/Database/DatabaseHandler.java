package com.corwdfire.wardrode.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.corwdfire.wardrode.Model.FavoriteItem;
import com.corwdfire.wardrode.Model.ImageItem;

import java.util.ArrayList;

/**
 * Created by rahul on 11/23/16.
 */

public class DatabaseHandler extends SQLiteOpenHelper {

    private static final int DB_VERSION = 2;
    private static final String DB_NAME = "wardrobe";

    private static final String CLOTH_TABLE = "cloths";
    private static final String FAVORITE_TABLE = "favorite";

    private static final String ID = "_id";
    public static final String IMAGE_URL = "image_url";
    public static final String CLOTH_TYPE = "cloth_type";
    public static final String POSITION = "position";

    public static final String FAVORITE_SHIRT_ID = "fav_shirt_id";
    public static final String FAVORITE_PANT_ID = "fav_pant_id";

    public DatabaseHandler(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE = "CREATE TABLE " + CLOTH_TABLE + " ( " + ID + " INTEGER PRIMARY KEY  , " + IMAGE_URL
                + " INTEGER , " + CLOTH_TYPE + " INTEGER , " + POSITION + " INTEGER )";
        String CREATE_FAV_TABLE = "CREATE TABLE " + FAVORITE_TABLE + " (  " + FAVORITE_SHIRT_ID + " INATGER , " + FAVORITE_PANT_ID + " INTEGER )";
        db.execSQL(CREATE_TABLE);
        db.execSQL(CREATE_FAV_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + CLOTH_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + FAVORITE_TABLE);
        onCreate(db);
    }

    public long addCloths(ContentValues contentValues) {
        SQLiteDatabase db = getWritableDatabase();
        long result = db.insert(CLOTH_TABLE, null, contentValues);
        db.close();
        return result;
    }

    public ArrayList<ImageItem> getCloths(int type) {
        ArrayList<ImageItem> list = new ArrayList<>();
        String selectQuery = "SELECT  * FROM " + CLOTH_TABLE + " WHERE " + CLOTH_TYPE + " =  " + type;
        SQLiteDatabase db = null;
        Cursor cursor = null;
        try {
            db = getWritableDatabase();
            cursor = db.rawQuery(selectQuery, null);
            if (cursor.moveToFirst()) {
                do {
                    ImageItem item = new ImageItem();
                    item.setImageUrl(cursor.getLong(cursor.getColumnIndex(IMAGE_URL)));
                    item.setType(cursor.getInt(cursor.getColumnIndex(CLOTH_TYPE)));
                    item.setPosition(cursor.getInt(cursor.getColumnIndex(POSITION)));
                    item.setId(cursor.getInt(cursor.getColumnIndex(ID)));
                    list.add(item);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e(getClass().getName(), e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            if (db != null) {
                db.close();
            }
        }
        return list;
    }


    public long addFavorites(ContentValues contentValues) {
        SQLiteDatabase db = getWritableDatabase();
        long result = db.insert(FAVORITE_TABLE, null, contentValues);
        db.close();
        return result;
    }

    public int removeFavorite(long pantId, long shirtId) {
        SQLiteDatabase db = getWritableDatabase();
        int result = db.delete(FAVORITE_TABLE, FAVORITE_PANT_ID + " = ?" + " AND " + FAVORITE_SHIRT_ID + " = ?", new String[]{String.valueOf(pantId), String.valueOf(shirtId)});
        db.close();
        return result;
    }

    public ArrayList<FavoriteItem> getFavorites() {
        ArrayList<FavoriteItem> list = new ArrayList<>();
        String selectQuery = "SELECT  * FROM " + FAVORITE_TABLE;
        SQLiteDatabase db = null;
        Cursor cursor = null;
        try {
            db = getWritableDatabase();
            cursor = db.rawQuery(selectQuery, null);
            if (cursor.moveToFirst()) {
                do {
                    FavoriteItem item = new FavoriteItem();
                    item.setPantId(cursor.getLong(cursor.getColumnIndex(FAVORITE_PANT_ID)));
                    item.setShirtId(cursor.getInt(cursor.getColumnIndex(FAVORITE_SHIRT_ID)));
                    list.add(item);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e(getClass().getName(), e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            if (db != null) {
                db.close();
            }
        }
        return list;
    }
}

