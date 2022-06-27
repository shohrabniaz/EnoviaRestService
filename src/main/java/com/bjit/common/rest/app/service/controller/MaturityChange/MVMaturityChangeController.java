/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.bjit.common.rest.app.service.controller.MaturityChange;

import com.bjit.common.code.utility.statechange.model.Data;
import com.bjit.common.code.utility.statechange.model.Response;
import com.bjit.common.code.utility.statechange.model.State;
import com.bjit.common.rest.app.service.maturity.mvStateChange.MVStateChangeServiceImpl;
import com.bjit.common.rest.app.service.model.MaturityChange.MaturityChangeResponse;
import com.bjit.common.rest.app.service.payload.common_response.CustomResponseBuilder;
import com.bjit.common.rest.app.service.payload.common_response.IResponse;
import com.bjit.common.rest.app.service.payload.common_response.Status;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Fazley Rabbi-11372 Date: 17-06-2022
 *
 */
@RestController
@RequestMapping(path = "/maturity")
public class MVMaturityChangeController {

    @Autowired
    MVStateChangeServiceImpl mVStateChangeServiceImpl;

    @ResponseBody
    @PostMapping(path = "/stateChange/promote")
    public ResponseEntity<?> changeMaturityStatus(@RequestBody State state) throws Exception {
        IResponse responseBuilder = new CustomResponseBuilder();
        String response;
        List<MaturityChangeResponse> responseList = new ArrayList<>();
        try {
            for (Data data : state.getData()) {
                //state change process call
                MaturityChangeResponse result = mVStateChangeServiceImpl.changeStateLifecycle(data.getId(), data.getNextState());
                responseList.add(result);
            }
            response = responseBuilder.setData(responseList).setStatus(Status.OK).buildResponse();
        } catch (Exception e) {
            response = responseBuilder.addErrorMessage(e.getMessage()).setStatus(Status.FAILED).buildResponse();
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
