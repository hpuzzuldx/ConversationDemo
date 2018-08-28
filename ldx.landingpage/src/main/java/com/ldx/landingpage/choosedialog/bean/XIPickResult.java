package com.ldx.landingpage.choosedialog.bean;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;

public class XIPickResult {
    private Bitmap bitmap;
    private Uri uri;
    private String path;
    private Throwable error;
    private Intent data;

    public Intent getData() {
        return data;
    }

    public void setData(Intent data) {
        this.data = data;
    }

    public boolean isCamera() {
        return isCamera;
    }

    public void setCamera(boolean camera) {
        isCamera = camera;
    }

    private boolean isCamera;

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public Uri getUri() {
        return uri;
    }

    public XIPickResult setUri(Uri uri) {
        this.uri = uri;
        return this;
    }

    public Throwable getError() {
        return error;
    }

    public XIPickResult setError(Exception error) {
        this.error = error;
        return this;
    }

    public XIPickResult setError(Throwable error) {
        this.error = error;
        return this;
    }

    public XIPickResult setPath(String path) {
        this.path = path;
        return this;
    }

    public String getPath() {
        return path;
    }
}
