package com.jack.ImageGallery.Objects;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class ImagesCollection {
    private ArrayList<Image> arrayImages;
    private boolean shuffleMode = false;

    public ImagesCollection(ArrayList<Image> arrayImages) {
        this.arrayImages = arrayImages;
    }

    public ArrayList<Image> getArrayImages() {
        return arrayImages;
    }

    public void addImage( Image image) {
        if ( arrayImages == null)
            arrayImages = new ArrayList<>();
        arrayImages.add(image);
        this.setShuffleMode(shuffleMode);
    }

    public int getSize() {
        return arrayImages != null ? arrayImages.size() : 0;
    }

    public void setShuffleMode( boolean mode) {
        this.shuffleMode = mode;
        if ( arrayImages != null) {
            if (mode) {
                Collections.shuffle(arrayImages);
            }
            else {
                Collections.sort(arrayImages, new Comparator<Image>() {
                    @Override
                    public int compare(Image lhs, Image rhs) {
                        return lhs.getNumber() - rhs.getNumber();
                    }
                });
            }
        }
    }
}