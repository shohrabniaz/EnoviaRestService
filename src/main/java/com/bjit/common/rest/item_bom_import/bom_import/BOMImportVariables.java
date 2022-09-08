/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.item_bom_import.bom_import;

import com.bjit.common.rest.app.service.utilities.CommonPropertyReader;
import com.bjit.common.rest.item_bom_import.utility.BusinessObjectUtil;
import com.bjit.common.rest.item_bom_import.utility.bomResponseMessageFormatter.ChildInfo;
import com.bjit.common.rest.item_bom_import.utility.bomResponseMessageFormatter.ParentInfo;
import com.bjit.common.rest.item_bom_import.xml_BOM_mapper_model.Relationship;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author BJIT
 */
public class BOMImportVariables {

    public int total_connection_processed = 0;
    public int new_connection = 0;
    public int modified_connection = 0;
    public int deleted_connection = 0;
    public int total_child_in_ENOVIA = 0;
    //public int total_line_in_ENOVIA = 0;
    public int total_connection_unprocessed = 0;
    public boolean errorInStructure = false;
    public HashMap<String, Long> bomValidationAndProcessingTimeForParentMap;
    
    public int total_Child_from_PDM = 0;
    public String relName = "";
    public String interfaceName = "";
    public boolean isBOMCreatedSuccessfully = true;
    List<Relationship> relationshipList = null;
    
    
    public List<HashMap<ParentInfo, HashMap<String, ChildInfo>>> parentChildInfoMapList;
    public List<ParentInfo> errorParentInfoList;
    public HashMap<String, List<ParentInfo>> responseMsgMap;
    public CommonPropertyReader commonPropertyReader;
    public BusinessObjectUtil businessObjectUtil;
    public List<ParentInfo> successfulParentInfoList;
}
