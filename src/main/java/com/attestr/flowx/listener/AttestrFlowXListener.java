package com.attestr.flowx.listener;

import java.util.HashMap;
import java.util.Map;

/**
 * Copyright (c) 2021 Pegadroid IQ Solutions Pvt. Ltd.
 * @Author Gaurav Naresh Pandit
 **/
public interface AttestrFlowXListener {

    /**
     * Flow completion handler method, invoked after the flow completes successfully
     * @param data Completion response data
     */
    void onFlowXComplete(Map<String, Object> data);


    /**
     * Flow skip handler method, invoked when flow is terminated by the user forcefully
     * @param data Represents the currently running or last successful stage id
     */
    void onFlowXSkip(Map<String, Object> data);

    /**
     * Flow error handler method invoked if flow runs into irrecoverable errors
     * @param data Error response
     */
    void onFlowXError(Map<String, Object> data);

}
