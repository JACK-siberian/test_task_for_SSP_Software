package com.jack.ImageGallery.Objects;

public class Image {
    private long id;
    private int indexNumber;
    private String url;

    private boolean favourites = false;
    private String comment;

    public Image(long id, int indexNumber, String url) {
        this.id = id;
        this.indexNumber = indexNumber;
        this.url = url;
    }

    public long getId() {
        return id;
    }

    public int getIndexNumber() {
        return indexNumber;
    }

    public boolean isFavourites() {
        return favourites;
    }
    public void addToFavourites( String comment) {
        this.favourites = true;
        this.comment = comment;
    }

    public String getComment() {
        return comment;
    }

    public String getUrl() {
        return url;
    }
}