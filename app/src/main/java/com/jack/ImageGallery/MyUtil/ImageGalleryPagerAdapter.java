package com.jack.ImageGallery.MyUtil;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jack.ImageGallery.Objects.Image;
import com.jack.ImageGallery.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class ImageGalleryPagerAdapter extends PagerAdapter {

    private ArrayList<Image> images;
    private Context context;
    private boolean shuffleMode;

    public ImageGalleryPagerAdapter(Context context, ArrayList<Image> images, boolean shuffleMode){
        this.context = context;
        this.images = images;
        this.shuffleMode = shuffleMode;
        setShuffleMode(shuffleMode);
    }

    @Override
    public int getCount() {
        if ( images != null)
            return images.size();
        else
            return 0;
    }
    @Override
    public int getItemPosition(Object object) {
        /*View v = (View) object;
        long viewTag = (long)v.getTag();
        for ( Image image : images)
            if ( viewTag == image.getId())
                return POSITION_UNCHANGED;*/

        return POSITION_NONE;
    }
    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        if ( images != null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            View itemGallery = inflater.inflate(R.layout.item_image_gallery, null);


            ImageView imageView = (ImageView) itemGallery.findViewById(R.id.ivImage);
            imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            LinearLayout frameComment = (LinearLayout) itemGallery.findViewById(R.id.frameComment);

            Image curImage = images.get(position);
            itemGallery.setTag(curImage.getId());
            new ImageBackgroundDownloader(imageView)
                    .execute(curImage.getUrl());

            String comment;
            if ( (comment = curImage.getComment()) != null) {
                frameComment.setVisibility(View.VISIBLE);
                TextView textView = (TextView) frameComment.findViewById(R.id.tvComment);
                textView.setText(comment);
            } else {
                frameComment.setVisibility(View.GONE);
            }
            container.addView(itemGallery, 0);
            return itemGallery;
        }
        else
            return null;
    }

    public void setShuffleMode(boolean mode) {
        if (mode)
            Collections.shuffle(images);
        else
            Collections.sort(images, new Comparator<Image>() {
                @Override
                public int compare(Image lhs, Image rhs) {
                    return lhs.getIndexNumber() - rhs.getIndexNumber();
                }
            });
    }

    public void updateAdapter(ArrayList<Image> images) {
        this.images = images;
        setShuffleMode(shuffleMode);
    }

    public long getImageId(int position) {
        if ( images != null) {
            return images.get( position).getId();
        }
        return -1;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }
}