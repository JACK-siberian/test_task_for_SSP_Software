package com.jack.ImageGallery.MyUtil;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import com.jack.ImageGallery.R;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class ImageBackgroundDownloader extends AsyncTask<String, Void, Bitmap> {
    private final String TAG = "ImageDownloader";

    ImageView imageView;

    public ImageBackgroundDownloader(ImageView imageView) {
        this.imageView = imageView;
    }

    @Override
    protected Bitmap doInBackground(String... params) {
        Bitmap bitmap;
        Log.d(TAG, "doInBackground...");
        try {
            URL imageUrl = new URL( params[0]);

            HttpURLConnection conn = (HttpURLConnection)imageUrl.openConnection();
            conn.setConnectTimeout(7000);
            conn.setReadTimeout(7000);

            conn.setInstanceFollowRedirects(true);
            InputStream is=conn.getInputStream();
            bitmap = BitmapFactory.decodeStream(is);
            is.close();
            conn.disconnect();

            return bitmap;
        } catch (Throwable ex) {
            Log.e(TAG, ex.getMessage());
            return null;
        }
    }

    @Override
    protected void onPostExecute(Bitmap result) {
        super.onPostExecute(result);
        if ( result != null) {
            imageView.setImageBitmap(result);
        }
        else {
            //noinspection ResourceType
            imageView.setImageResource(R.raw.error_image);
        }
    }
}