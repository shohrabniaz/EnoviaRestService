/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.app.service.enovia_pdm.processors.interfaces;

import com.bjit.common.rest.app.service.enovia_pdm.models.ParentChildModel;
import com.bjit.common.rest.app.service.enovia_pdm.models.PdmBom;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author BJIT
 */
public interface IRequestDataBuilder {
//    HashMap<String, PdmBom> prepareRequestData(HashMap<String, ParentChildModel> parentChildRelationMap);
    HashMap<String, PdmBom> prepareRequestData(HashMap<String, List<ParentChildModel>> parentChildRelationMap);
}
