/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.app.service.enovia_pdm.processors;

import com.bjit.common.rest.app.service.enovia_pdm.models.ResponseModel;
import com.bjit.common.rest.app.service.enovia_pdm.models.ServiceRequestSequencer;
import com.bjit.common.rest.app.service.enovia_pdm.service.IMasterShipChange;
import com.bjit.common.rest.app.service.utilities.JSON;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author BJIT
 */
@Component
public class ResponseModelGenerator {

    @Autowired
    JSON json;

    public HashMap<String, List<ResponseModel>> generate(List<String> pdmResponses) {
        HashMap<String, List<ResponseModel>> pdmResponseListMap = new HashMap<>();

        List<ResponseModel> successfulResponses = new ArrayList<>();
        List<ResponseModel> failedResponses = new ArrayList<>();

        pdmResponseListMap.put("successful", successfulResponses);
        pdmResponseListMap.put("unsuccessful", failedResponses);

        pdmResponses.forEach(pdmResponse -> {
            ResponseModel responseModel = json.deserialize(pdmResponse, ResponseModel.class);
            if (responseModel.getStatus().name().equalsIgnoreCase("failed")) {
                failedResponses.add(responseModel);
            } else {
                successfulResponses.add(responseModel);
            }
            
            responseModel.setStatus(null);
        });
        return pdmResponseListMap;
    }

    public HashMap<String, List<ResponseModel>> generate(List<String> pdmResponses, IMasterShipChange masterShipChangeService) {
        HashMap<String, List<ResponseModel>> pdmResponseListMap = new HashMap<>();

        List<ResponseModel> successfulResponses = new ArrayList<>();
        List<ResponseModel> failedResponses = new ArrayList<>();

        failedResponses.addAll(masterShipChangeService.getUnsuccessfulProcesses());

        pdmResponseListMap.put("successful", successfulResponses);
        pdmResponseListMap.put("unsuccessful", failedResponses);

        pdmResponses.forEach(pdmResponse -> {
            String filename = masterShipChangeService.getResponseSequencerHashMap().get(pdmResponse).getFilename();
            ResponseModel responseModel = json.deserialize(pdmResponse, ResponseModel.class);
            if (responseModel.getStatus().name().equalsIgnoreCase("failed")) {
                failedResponses.add(responseModel);
                masterShipChangeService.moveFileToError(filename);
            } else {
                successfulResponses.add(responseModel);
                masterShipChangeService.moveFileToOld(filename);
            }
            responseModel.setStatus(null);
        });
        return pdmResponseListMap;
    }
}
