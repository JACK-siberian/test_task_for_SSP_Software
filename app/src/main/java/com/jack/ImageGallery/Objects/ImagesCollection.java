package com.jack.ImageGallery.Objects;

import java.util.ArrayList;

public class ImagesCollection {
    private ArrayList<Image> arrayImages;

    public ImagesCollection(ArrayList<Image> arrayImages) {
        this.arrayImages = arrayImages;
    }

    public ArrayList<Image> getArrayFavouritesImages() {
        if ( arrayImages != null) {
            ArrayList<Image> favourites = new ArrayList<>();
            for (Image cur : arrayImages) {
                if (cur.isFavourites()) {
                    favourites.add(cur);
                }
            }
            return favourites;
        }
        return null;
    }

    public ArrayList<Image> getArrayImages() {
        return arrayImages;
    }

    public void addImage( Image image) {
        if ( arrayImages == null)
            arrayImages = new ArrayList<>();
        arrayImages.add(image);
    }

    public int getSize() {
        return arrayImages != null ? arrayImages.size() : 0;
    }

    public Image findImageByID( int imageID) {
        if ( arrayImages != null) {
            for ( Image cur : arrayImages) {
                if ( cur.getId() == imageID ) {
                    return cur;
                }
            }
        }
        return null;
    }
}