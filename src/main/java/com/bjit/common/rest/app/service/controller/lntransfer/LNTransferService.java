/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.app.service.controller.lntransfer;

import com.bjit.common.rest.app.service.lntransfer.LNResponseMessageFormater;
import com.bjit.common.rest.app.service.model.itemTransfer.LNTransferRequestModel;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author Emrul
 */
public interface LNTransferService {

    public Map<LNResponseMessageFormater, String> itemTransfer(LNTransferRequestModel lnTransferRequestModel, String type, String level) throws Exception;

    public Map<LNResponseMessageFormater, String> bomTransfer(LNTransferRequestModel lnTransferRequestModel, String type, String level) throws Exception;

    public void gtsNigtlyUpdateToLN();
}
