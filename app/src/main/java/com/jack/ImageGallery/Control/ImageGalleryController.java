package com.jack.ImageGallery.Control;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.jack.ImageGallery.MyUtil.DBHelper;
import com.jack.ImageGallery.MyUtil.JsonHelper;
import com.jack.ImageGallery.Objects.Image;
import com.jack.ImageGallery.Objects.ImagesCollection;

import java.util.ArrayList;
import java.util.HashMap;

public class ImageGalleryController {
    private ImagesCollection imagesCollection;
    private Context context;
    private OnImageGalleryListener onImageGalleryListener;
    private SQLiteDatabase db;

    public interface OnImageGalleryListener {
        void updateAdapter();
    }

    public ImageGalleryController( Context context) {
        this.context = context;
        setOnImageGalleryListener((OnImageGalleryListener)context);
        imagesCollection = new ImagesCollection(new JsonHelper( context,"image_list.json").getImageArray());
        initFavourites();
    }

    public void setOnImageGalleryListener(OnImageGalleryListener onImageGalleryListener) {
        this.onImageGalleryListener = onImageGalleryListener;
    }

    private ArrayList<HashMap<String, Object>> getFavouritesArray() {
        db = getDBConn();
        String[] columns = {
                DBHelper.columnImageId,
                DBHelper.columnComment
        };
        Cursor c = db.query( DBHelper.tableFavourites, columns, null, null, null, null, null);

        if (c.moveToFirst()) {
            ArrayList<HashMap<String, Object>> favourites = new ArrayList<>(c.getCount());

            int image_idColIndex = c.getColumnIndex( DBHelper.columnImageId);
            int commentColIndex = c.getColumnIndex( DBHelper.columnComment);

            do {
                HashMap<String, Object> hashMap = new HashMap<>();

                hashMap.put( "imageID", c.getInt(image_idColIndex));
                hashMap.put( "comment", c.getString(commentColIndex));

                favourites.add( hashMap);

            } while (c.moveToNext());
            c.close();
            return favourites;
        }
        c.close();
        return null;
    }

    public void initFavourites() {
        ArrayList<HashMap<String, Object>> favourites = getFavouritesArray();
        if ( favourites != null) {
            for ( HashMap<String, Object> item : favourites) {
                int imageID = (int) item.get("imageID");
                String comment = (String) item.get("comment");
                Image image = imagesCollection.findImageByID(imageID);
                if ( image != null)
                    image.addToFavourites(comment);
            }
        }
    }

    public int isFavourite( int imageId) {
        db = getDBConn();
        String selection = DBHelper.columnImageId + "= ?";
        String[] selectionArgs = {String.valueOf(imageId)};
        Cursor c = db.query( DBHelper.tableFavourites, null, selection, selectionArgs, null, null, null);

        if (c.moveToFirst()) {
            int rowID = c.getInt( c.getColumnIndex(DBHelper.columnID));
            c.close();
            return rowID;
        }
        c.close();
        return -1;
    }

    public void addToFavourites( int imageId, String comment) {
        int rowID;
        if ( (rowID=isFavourite(imageId)) == -1) {
            ContentValues cv = new ContentValues();
            cv.put(DBHelper.columnImageId, imageId);
            cv.put(DBHelper.columnComment, comment);

            db = getDBConn();
            db.insert(DBHelper.tableFavourites, null, cv);
            imagesCollection.findImageByID(imageId).addToFavourites(comment);
            onImageGalleryListener.updateAdapter();
        }
        else {
            ContentValues cv = new ContentValues();
            cv.put(DBHelper.columnImageId, imageId);
            cv.put(DBHelper.columnComment, comment);

            db = getDBConn();
            String where = DBHelper.columnID + "=?";
            String[] whereArgs = { String.valueOf(rowID) };
            db.update(DBHelper.tableFavourites, cv, where, whereArgs);
            imagesCollection.findImageByID(imageId).addToFavourites(comment);
            onImageGalleryListener.updateAdapter();
        }
    }

    public ImagesCollection getImagesCollection() {
        return imagesCollection;
    }

    public SQLiteDatabase getDBConn() {
        if ( db == null) {
            DBHelper dbHelper = new DBHelper(context);
            return dbHelper.getWritableDatabase();
        }
        else
            return db;
    }
}