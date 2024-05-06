package com.attestr.flowx.utils;

import com.attestr.flowx.model.response.common.ResponseData;
import com.attestr.flowx.model.response.common.StageInitData;

/**
 * Copyright (c) 2021 Pegadroid IQ Solutions Pvt. Ltd.
 *
 * @Author Gaurav Naresh Pandit
 * @Since 06/01/23
 **/
public class StageReady {

    private Double retryAttempts = 0.0;
    private ResponseData<StageInitData> stageReadyResponse;

    public StageReady() {
        stageReadyResponse = GlobalVariables.stageReadyResponse;
    }

    public int getRemainingRetryAttempts() {
        retryAttempts = (Double) stageReadyResponse.getData().getMetadata().get("retryAttempts");
        assert retryAttempts != null;
        return  (int) retryAttempts.doubleValue();
    }

}
