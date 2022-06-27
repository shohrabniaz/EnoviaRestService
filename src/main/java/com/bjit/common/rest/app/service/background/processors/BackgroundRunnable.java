/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.app.service.background.processors;

import org.apache.log4j.Logger;

/**
 *
 * @author BJIT
 * @param <T>
 */
public class BackgroundRunnable<T> implements Runnable {

    private static final Logger BACKGROUND_RUNNABLE_LOGGER = Logger.getLogger(BackgroundRunnable.class);
    final IBackGroundProcessor backGroundProcessor;

    public BackgroundRunnable(IBackGroundProcessor backGroundProcessor){
        this.backGroundProcessor = backGroundProcessor;
    }
    
//    public void __init__(IBackGroundProcessor backGroundProcessor) {
//        this.backGroundProcessor = backGroundProcessor;
//    }

    public void backGroundProcess(/*IBackGroundProcessor backGroundProcessor*/) throws Exception {
        try {
            T file = (T) backGroundProcessor.process();
            try {
                IResponseSender responseSender = (IResponseSender) backGroundProcessor;
                sendResponse(responseSender, file);
            } catch (Exception exp) {
                BACKGROUND_RUNNABLE_LOGGER.error(exp.getMessage() + " (Response sender is not exists)");
            }
        } catch (Exception exp) {
            throw new RuntimeException(exp);
        }
    }

    private void sendResponse(IResponseSender responseSender, T object) throws Exception {
        try {
            responseSender.send(object);
        } catch (Exception exp) {
            BACKGROUND_RUNNABLE_LOGGER.error(exp.getMessage());
        }
    }

    @Override
    public void run() {
        try {
            backGroundProcess(/*backGroundProcessor*/);
        } catch (Exception ex) {
            BACKGROUND_RUNNABLE_LOGGER.error(ex.getMessage());
        }
    }
}
