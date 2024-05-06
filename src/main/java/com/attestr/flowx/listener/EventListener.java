package com.attestr.flowx.listener;

import androidx.fragment.app.Fragment;

/**
 * Copyright (c) 2021 Pegadroid IQ Solutions Pvt. Ltd.
 * @Author Gaurav Naresh Pandit
 **/
public interface EventListener {
    void updateUI(String STATUS, String stageType, String dataErrorResponse, String response);
    void skipFlow();
    void changeFooterLayoutVisibility(boolean isVisible);
    void addFragment(Fragment fragment);
}
