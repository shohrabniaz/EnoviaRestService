/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.bjit.common.rest.app.service.maturity.mvStateChange;
import com.bjit.common.rest.app.service.model.MaturityChange.MaturityChangeResponse;

/**
 *
 * @author BJIT
 */
public interface MVStateChangeService {

    public MaturityChangeResponse changeStateLifecycle(String objPhyId, String targatedState) throws Exception;

}
