package com.attestr.flowx.listener;

/**
 * Copyright (c) 2021 Pegadroid IQ Solutions Pvt. Ltd.
 *
 * @Author Gaurav Naresh Pandit
 * @Since 21/02/23
 **/
public interface MediaImagesListener {
    void selectImage(String type);
    void removeImageAndMediaId(int position, String type);
}
