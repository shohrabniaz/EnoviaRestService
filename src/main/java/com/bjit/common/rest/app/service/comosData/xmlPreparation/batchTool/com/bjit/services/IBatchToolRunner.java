package com.bjit.common.rest.app.service.comosData.xmlPreparation.batchTool.com.bjit.services;

import java.util.HashMap;
import java.util.List;

public interface IBatchToolRunner {
    String getTool();
    void run() throws RuntimeException;
//    HashMap<String, List<String>> uniqueFileSeparation();
//    void takingFileList();
}
