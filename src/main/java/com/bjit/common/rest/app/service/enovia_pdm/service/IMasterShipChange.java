/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.app.service.enovia_pdm.service;

import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

import com.bjit.common.rest.app.service.enovia_pdm.models.ResponseModel;
import com.bjit.common.rest.app.service.enovia_pdm.models.ServiceRequestSequencer;
import matrix.db.Context;

/**
 *
 * @author BJIT
 */
public interface IMasterShipChange {
    List<String> change(Context context) throws Exception;
    HashMap<String, ServiceRequestSequencer> getResponseSequencerHashMap();
    void moveFileToOld(String fileName);
    void moveFileToError(String fileName);
    HashMap<String, String> getmapOfEmailAddressByItem();
    List<ResponseModel> getparentResponseList();
    List<ResponseModel> getUnsuccessfulProcesses();
}
