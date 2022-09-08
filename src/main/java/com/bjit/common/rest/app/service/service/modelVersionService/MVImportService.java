/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.bjit.common.rest.app.service.service.modelVersionService;

import com.bjit.common.rest.app.service.controller.modelVersion.atoncontext.Model;
import com.bjit.common.rest.app.service.model.itemImport.ObjectDataBean;
import com.bjit.common.rest.app.service.model.modelVersion.MVCreateUpdateResponseFormatter;
import com.bjit.common.rest.app.service.model.modelVersion.MVDataTree;
import com.bjit.common.rest.app.service.model.modelVersion.MVUpdateRequestModel;
import com.bjit.common.rest.app.service.model.tnr.TNR;
import com.bjit.common.rest.app.service.utilities.DsServiceCall;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import matrix.db.Context;

/**

 @author Ahmad R Farabi [PLM Team, BJIT] <ahmad.farabi@bjitgroup.com>
 */
public interface MVImportService
{

    public List<MVCreateUpdateResponseFormatter> createMV(
            List<MVDataTree> dataTree, DsServiceCall dsCall) throws Exception;

    public List<MVCreateUpdateResponseFormatter> updateMV(
            MVUpdateRequestModel model, String mvPhysicalId,
            DsServiceCall dsCall) throws Exception;

    public List<MVCreateUpdateResponseFormatter> getMV(String mvPhysicalId,
                                                       DsServiceCall dsCall) throws Exception;

    public List<HashMap<String, String>> createItems(Context context,
                                                     ObjectDataBean objectDataBean) throws Exception;

//    public List<HashMap<String, String>> createBom(Context context,
//                                                   List<CreateBOMBeanExtended> createBOMBeanList) throws Exception;

    public List<HashMap<String, String>> setEvolution(
            List<HashMap<String, String>> setEvolutionParams,
            DsServiceCall dsCall, Boolean isConfigured) throws Exception;

    public List<HashMap<String, String>> addAtonInterface(
            List<HashMap<String, String>> interfaceParams, String interfaceName,
            DsServiceCall dsCall) throws Exception;

    public Boolean isInterfaceExist(Context context, TNR tnr,
                                    String interfaceName) throws Exception;

    public Boolean updateInterface(Context context,
                                   HashMap<String, String> interfaceParams) throws Exception;

    public List<HashMap<String, String>> changeMaturityState(
            List<HashMap<String, String>> statusParams, DsServiceCall dsCall) throws Exception;

    public Map<String, Map<String, Boolean>> checkForRevisions(
            List<Model> models, Map<String, List<String>> queueList,
            DsServiceCall dsCall, Context context) throws Exception;

    public List<HashMap<String, String>> reviseMV(
            List<HashMap<String, String>> revParams, DsServiceCall dsCall) throws Exception;

    public List<HashMap<String, String>> createItemInstance(
            List<HashMap<String, String>> instanceParams, DsServiceCall dsCall) throws Exception;

    public HashMap<String, String> getChildInfoFromTopItem(
            String topItemPhysicalId, DsServiceCall dsCall) throws Exception;

    public TNR getTNRfromPhysicalId(Context context, String physicalId) throws Exception;

    public Boolean updateTopItemSourceVersion(Context context,
                                              HashMap<String, String> param) throws Exception;

    public Boolean updateContextItemInfo(Context context,
                                         HashMap<String, String> param) throws Exception;

    public Boolean updateMaturityState(Context context,
                                       List<HashMap<String, String>> param) throws Exception;

    public List<HashMap<String, String>> classifyItem(
            List<HashMap<String, String>> classParams, String classId,
            DsServiceCall dsCall) throws Exception;

    public List<HashMap<String, String>> updateClassifyItem(
            List<HashMap<String, String>> classParams, DsServiceCall dsCall,
            Context context) throws Exception;

    public Boolean checkClassified(
            String mvPhysicalId, String classId, DsServiceCall dsCall) throws Exception;

    public List<HashMap<String, String>> mfgItemCreate(
            List<HashMap<String, String>> itemParamsList, DsServiceCall dsCall) throws Exception;

    public List<HashMap<String, String>> isInstanceExists(String topPhysicalId,
                                                          String childPhysicalId,
                                                           Context context);
    
    public void setSourceAndCS(String securityContex, String source,
                               String owner) throws Exception;

    public List<HashMap<String, String>> createVersionOfMfgItem(
            List<HashMap<String, String>> versionParams, DsServiceCall dsCall,
            Context context) throws Exception;

    public Boolean updateChildItemInfo(Context context,
                                       HashMap<String, String> param) throws Exception;

    public Boolean updateOwnershipOfModel(Context context,
                                          String modelId) throws Exception;

    public Boolean addUserGroupToMV(
            List<HashMap<String, String>> versionParams, DsServiceCall dsCall) throws Exception;

}

