package com.app.imagegalery;

import android.graphics.Bitmap;

public class Image {

    String name;
    String comment;
    Bitmap bitmap;

    public Image(String name, Bitmap bitmap, String comment) {
        this.name = name;
        this.bitmap = bitmap;
        this.comment = comment;
    }

    public Image() {}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

}