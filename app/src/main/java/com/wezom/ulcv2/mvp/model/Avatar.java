package com.wezom.ulcv2.mvp.model;

import android.net.Uri;

import java.io.Serializable;

/**
 * Created by usik.a on 07.02.2017.
 */

public class Avatar implements Serializable {

    private Uri uri;
    private int cropMode;

    public Avatar(Uri uri, int cropMode) {
        this.uri = uri;
        this.cropMode = cropMode;
    }

    public Uri getUri() {
        return uri;
    }

    public int getCropMode() {
        return cropMode;
    }
}
