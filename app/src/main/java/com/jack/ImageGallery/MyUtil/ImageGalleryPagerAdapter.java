package com.jack.ImageGallery.MyUtil;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jack.ImageGallery.Objects.Image;
import com.jack.ImageGallery.R;

import java.util.ArrayList;

public class ImageGalleryPagerAdapter extends PagerAdapter {
    private final String TAG = ImageGalleryPagerAdapter.class.getSimpleName();

    private ArrayList<Image> images;
    private Context context;

    public ImageGalleryPagerAdapter(Context context, ArrayList<Image> images){
        this.context = context;
        this.images = images;
    }

    @Override
    public int getCount() {
        return images.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {

        LayoutInflater inflater = LayoutInflater.from(context);
        View itemGallery = inflater.inflate(R.layout.item_image_gallery, null);

        ImageView imageView = (ImageView)itemGallery.findViewById( R.id.ivImage);
        imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        LinearLayout frameComment = (LinearLayout)itemGallery.findViewById( R.id.frameComment);

        Image curImage = images.get(position);
        Log.d(TAG, " instantiateItem № " + position);
        Log.d(TAG, " Item getId: " + curImage.getId());
        Log.d(TAG, " Item getNumber: " + curImage.getNumber());

        new ImageBackgroundDownloader(imageView)
                .execute(curImage.getUrl());

        String comment;
        if ( !(comment = curImage.getComment()).equals("null")) {
            frameComment.setVisibility(View.VISIBLE);
            TextView textView = (TextView)frameComment.findViewById( R.id.tvComment);
            textView.setText( comment);
        }
        else {
            frameComment.setVisibility(View.GONE);
        }
        container.addView(itemGallery, 0);
        return itemGallery;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }
}