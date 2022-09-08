/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.app.service.enovia_pdm.processors.interfaces;

import com.bjit.common.rest.app.service.enovia_pdm.models.ParentChildModel;
import com.bjit.common.rest.app.service.enovia_pdm.models.xml.Item;
import java.util.HashMap;
import java.util.List;
import matrix.db.Context;

/**
 *
 * @author BJIT
 */
public interface IStructureCollector {
    HashMap<String, List<ParentChildModel>> fetchStructure(Context context, Item enoviaExportItem) throws Exception;
//    HashMap<String, ParentChildModel> mergeStructure(HashMap<String, List<ParentChildModel>> parentChildModelListMap)throws Exception;
    HashMap<String, List<ParentChildModel>> mergeStructure(HashMap<String, List<ParentChildModel>> parentChildModelListMap)throws Exception;
}
