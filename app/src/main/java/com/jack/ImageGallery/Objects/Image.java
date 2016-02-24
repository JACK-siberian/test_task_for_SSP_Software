package com.jack.ImageGallery.Objects;

public class Image {
    private long id;
    private int number;
    private String comment;
    private String url;

    public Image(long id, int number, String comment, String url) {
        this.id = id;
        this.number = number;
        this.comment = comment;
        this.url = url;
    }

    public long getId() {
        return id;
    }

    public int getNumber() {
        return number;
    }

    public String getComment() {
        return comment;
    }

    public String getUrl() {
        return url;
    }
}
