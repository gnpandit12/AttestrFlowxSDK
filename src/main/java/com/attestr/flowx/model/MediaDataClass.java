package com.attestr.flowx.model;

import android.graphics.Bitmap;

/**
 * Copyright (c) 2021 Pegadroid IQ Solutions Pvt. Ltd.
 *
 * @Author Gaurav Naresh Pandit
 * @Since 16/01/23
 **/
public class MediaDataClass {

    private Bitmap mSelectedImageBitmap;
    private String mTag;

    public Bitmap getSelectedImageBitmap() {
        return mSelectedImageBitmap;
    }

    public void setSelectedImageBitmap(Bitmap selectedImageBitmap) {
        this.mSelectedImageBitmap = selectedImageBitmap;
    }

    public String getmTag() {
        return mTag;
    }

    public void setmTag(String mTag) {
        this.mTag = mTag;
    }
}
