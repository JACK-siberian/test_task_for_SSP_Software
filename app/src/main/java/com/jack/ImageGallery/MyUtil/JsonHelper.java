package com.jack.ImageGallery.MyUtil;

import android.content.Context;
import android.util.Log;

import com.jack.ImageGallery.Objects.Image;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class JsonHelper {
    private final String TAG = "JsonHelper";
    private Context context;
    private JSONObject jsonObject;

    public JsonHelper( Context context, String file) {
        this.context = context;
        String json_source = loadAssetTextAsString(context, file);
        try {
            jsonObject = new JSONObject(json_source);
        } catch (JSONException e) {
            Log.e(TAG, e.getMessage());
            jsonObject = null;
        }
    }

    public JSONObject getJsonObject() {
        return jsonObject;
    }

    public JSONArray getJSONArray( String name) {
        try {
            return jsonObject.getJSONArray(name);
        } catch (JSONException e) {
            Log.e(TAG, e.getMessage());
            return null;
        }
    }

    public ArrayList<Image> getImageArray() {
        if ( jsonObject != null)
            try {
                JSONArray image_list = jsonObject.getJSONArray("image_list");
                ArrayList<Image> arrayImages = new ArrayList<>(image_list.length());
                for (int i = 0; i < image_list.length(); i++) {
                    JSONObject arrayElement = image_list.getJSONObject(i);

                    arrayImages.add(new Image(
                            arrayElement.getLong("id"),
                            arrayElement.getInt("number"),
                            arrayElement.getString("comment"),
                            arrayElement.getString("url")
                    ));
                }
                return arrayImages;
            } catch (JSONException e) {
                Log.e(TAG, e.getMessage());
            }
        return null;
    }

    private String loadAssetTextAsString(Context context, String name) {
        BufferedReader in = null;
        try {
            StringBuilder buf = new StringBuilder();
            InputStream is = context.getAssets().open(name);
            in = new BufferedReader(new InputStreamReader(is));

            String str;
            boolean isFirst = true;
            while ( (str = in.readLine()) != null ) {
                if (isFirst)
                    isFirst = false;
                else
                    buf.append('\n');
                buf.append(str);
            }
            return buf.toString();
        } catch (IOException e) {
            Log.e(TAG, "Error opening asset " + name);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    Log.e(TAG, "Error closing asset " + name);
                }
            }
        }
        return null;
    }
}
