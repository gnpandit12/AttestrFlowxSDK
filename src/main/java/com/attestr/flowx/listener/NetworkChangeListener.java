package com.attestr.flowx.listener;

/**
 * Copyright (c) 2021 Pegadroid IQ Solutions Pvt. Ltd.
 *
 * @Author Gaurav Naresh Pandit
 * @Since 16/02/22
 **/
public interface NetworkChangeListener {

    void onNetworkAvailable();
    void onNetworkLost();

}
