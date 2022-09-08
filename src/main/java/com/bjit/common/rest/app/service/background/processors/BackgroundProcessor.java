package com.bjit.common.rest.app.service.background.processors;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.apache.log4j.Logger;

public class BackgroundProcessor<T> {

    private static final Logger BACKGROUND_PROCESSOR_LOGGER = Logger.getLogger(BackgroundProcessor.class);

    public static ExecutorService EXECUTOR_SERVICE = Executors.newFixedThreadPool(2);

    public BackgroundResponse backGroundProcess(IBackGroundProcessor backGroundProcessor, Boolean isBackgroundProcess) throws Exception {

        BackgroundResponse<T> backgroundResponse = new BackgroundResponse<>();
        if (isBackgroundProcess) {
            Thread newThread = new Thread(() -> {
                try {
//                    T process = (T) backGroundProcessor.process();
//                    try{
//                        IResponseSender responseSender = (IResponseSender) backGroundProcessor;
//                        sentResponse(responseSender, process);
//                    }
//                    catch(Exception exp){
//                        BACKGROUND_PROCESSOR_LOGGER.error("Response sender is not exists");
//                    }
                    BackgroundRunnable<T> backgroundRunnable = new BackgroundRunnable(backGroundProcessor);
//                    backgroundRunnable.backGroundProcess(backGroundProcessor);
                    backgroundRunnable.backGroundProcess();

                } catch (Exception exp) {
                    throw new RuntimeException(exp);
                }
            });
            newThread.start();
            backgroundResponse.setResponse("true");
        } else {
            T process = (T) backGroundProcessor.process();
            backgroundResponse.setResponse("false");
            backgroundResponse.setObject(process);
        }
        return backgroundResponse;
    }

    public BackgroundResponse threadPoolBackGroundProcess(IBackGroundProcessor backGroundProcessor, Boolean isBackgroundProcess) throws Exception {

        BackgroundResponse<T> backgroundResponse = new BackgroundResponse<>();
        if (isBackgroundProcess) {
            BackgroundRunnable<T> backgroundRunnable = new BackgroundRunnable(backGroundProcessor);
//            backgroundRunnable.__init__(backGroundProcessor);
            EXECUTOR_SERVICE.execute(backgroundRunnable);
            backgroundResponse.setResponse(isBackgroundProcess.toString());
        } else {
            T process = (T) backGroundProcessor.process();
            backgroundResponse.setResponse(isBackgroundProcess.toString());
            backgroundResponse.setObject(process);
            System.out.println(process);
        }
        return backgroundResponse;
    }

//    private void sentResponse(IResponseSender responseSender, T object) throws Exception {
//        try {
//            responseSender.send(object);
//        } catch (Exception exp) {
//            BACKGROUND_PROCESSOR_LOGGER.error(exp.getMessage());
//        }
//    }
}
