/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.bjit.common.rest.app.service.service.modelVersionService;

import com.bjit.common.code.utility.dsapi.dscfgwithdsmfg.ConfigContextAndEvolutionService;
import com.bjit.common.code.utility.dsapi.dscfgwithdsmfg.impl.ConfigContextAndEvolutionServiceImpl;
import com.bjit.common.code.utility.dsapi.dscfgwithdsmfg.impl.ServiceType;
import com.bjit.common.code.utility.dsapi.dscfgwithdsmfg.model.ConfigContextAndEvolution;
import com.bjit.common.code.utility.dsapi.dscfgwithdsmfg.model.ResponseConfigContextAndEvolution;
import com.bjit.common.code.utility.dsapi.dslc.sharing.addUserGroupToObj.AddUserGroupToObjService;
import com.bjit.common.code.utility.dsapi.dslc.sharing.addUserGroupToObj.impl.AddUserGroupToObjServiceImpl;
import com.bjit.common.code.utility.dsapi.dslc.sharing.addUserGroupToObj.model.CollabSpace;
import com.bjit.common.code.utility.dsapi.dslc.sharing.addUserGroupToObj.model.SharingResponseModel;
import com.bjit.common.code.utility.dsapi.dslc.sharing.addUserGroupToObj.model.Sharings;
import com.bjit.common.code.utility.dsapi.dslc.sharing.addUserGroupToObj.model.User;
import com.bjit.common.code.utility.dsapi.dslc.sharing.addUserGroupToObj.model.UserGroup;
import com.bjit.common.code.utility.dsapi.dslc.sharing.addUserGroupToObj.model.UserGroupRequestModel;
import com.bjit.common.code.utility.dsapi.dslc.versioning.VersionCreateService;
import com.bjit.common.code.utility.dsapi.dslc.versioning.impl.VersionCreateServiceImpl;
import com.bjit.common.code.utility.dsapi.dslc.versioning.model.VersionCreateRequest;
import com.bjit.common.code.utility.dsapi.dslc.versioning.model.VersionCreateRequestAttributes;
import com.bjit.common.code.utility.dsapi.dslc.versioning.model.VersionCreateResponse;
import com.bjit.common.code.utility.dsapi.dsmfg.CreateMfgItemService;
import com.bjit.common.code.utility.dsapi.dsmfg.impl.CreateMfgItemServiceImpl;
import com.bjit.common.code.utility.dsapi.dsmfg.model.CreateMfgItemRequestModel;
import com.bjit.common.code.utility.dsapi.dsmfg.model.CreateMfgItemResponseModel;
import com.bjit.common.code.utility.dsapi.dsmfg.model.Item;
import com.bjit.common.code.utility.dsapi.general.AddInterfaceService;
import com.bjit.common.code.utility.dsapi.general.impl.AddInterfaceServiceImpl;
import com.bjit.common.code.utility.dsapi.general.model.RequestModel;
import com.bjit.common.code.utility.dsapi.general.model.ResponseModel;
import com.bjit.common.code.utility.dsapi.ipclassification.classifiedItem.ClassifiedItemService;
import com.bjit.common.code.utility.dsapi.ipclassification.classifiedItem.FetchInformation;
import com.bjit.common.code.utility.dsapi.ipclassification.classifiedItem.ModifyAttributesService;
import com.bjit.common.code.utility.dsapi.ipclassification.classifiedItem.impl.ClassifiedItemServiceImpl;
import com.bjit.common.code.utility.dsapi.ipclassification.classifiedItem.impl.FetchInformationImpl;
import com.bjit.common.code.utility.dsapi.ipclassification.classifiedItem.impl.ModifyAttributesServiceImpl;
import com.bjit.common.code.utility.dsapi.ipclassification.classifiedItem.model.ClassificationAtrributesResponse;
import com.bjit.common.code.utility.dsapi.ipclassification.classifiedItem.model.ItemClassifyRequestModel;
import com.bjit.common.code.utility.dsapi.ipclassification.classifiedItem.model.ItemClassifyResponseModel;
import com.bjit.common.code.utility.dsapi.ipclassification.classifiedItem.model.ModifyRequestModel;
import com.bjit.common.code.utility.dsapi.ipclassification.classifiedItem.model.ModifyResponseModel;
import com.bjit.common.code.utility.dsapi.ipclassification.classifiedItem.model.ObjectToClassify;
import com.bjit.common.code.utility.dsapi.ipclassification.classifiedItem.model.PathParamRequest;
import com.bjit.common.code.utility.dsapi.mfgInstance.FetchMfgItemInstancesService;
import com.bjit.common.code.utility.dsapi.mfgInstance.FetchMfgItemInstancesServiceImpl;
import com.bjit.common.code.utility.dsapi.mfgInstance.MFGInstanceService;
import com.bjit.common.code.utility.dsapi.mfgInstance.MFGInstanceServiceImpl;
import com.bjit.common.code.utility.dsapi.mfgInstance.model.Instance;
import com.bjit.common.code.utility.dsapi.mfgInstance.model.MFGInstanceResponse;
import com.bjit.common.code.utility.dsapi.mfgInstance.model.Member;
import com.bjit.common.code.utility.dsapi.mfgInstance.model.ReferencedObject;
import com.bjit.common.code.utility.dsapi.mfgInstance.model.RequestInstances;
import com.bjit.common.code.utility.dsapi.portfolio.ModelRevisionsDetailsImpl;
import com.bjit.common.code.utility.dsapi.portfolio.ReviseService;
import com.bjit.common.code.utility.dsapi.portfolio.impl.ReviseServiceImpl;
import com.bjit.common.code.utility.dsapi.portfolio.model.GraphRequest;
import com.bjit.common.code.utility.dsapi.portfolio.model.Product;
import com.bjit.common.code.utility.dsapi.portfolio.model.ReviseRequestModel;
import com.bjit.common.code.utility.dsapi.portfolio.model.ReviseResponseModel;
import com.bjit.common.code.utility.dsapi.portfolio.model.Version;
import com.bjit.common.code.utility.http.client.CSRFTokenGenerationService;
import com.bjit.common.code.utility.http.client.model.CSRFToken;
import com.bjit.common.code.utility.statechange.impl.StateChangeImpl;
import com.bjit.common.code.utility.statechange.model.Data;
import com.bjit.common.code.utility.statechange.model.Response;
import com.bjit.common.code.utility.statechange.model.State;
import com.bjit.common.rest.app.service.controller.modelVersion.atoncontext.Model;
import com.bjit.common.rest.app.service.controller.modelVersion.atoncontext.ProductsItemCodeService;
import com.bjit.common.rest.app.service.controller.modelVersion.atoncontext.ProductsItemCodeServiceImpl;
import com.bjit.common.rest.app.service.controller.modelVersion.atoncontext.SearchConfigService;
import com.bjit.common.rest.app.service.controller.modelVersion.atoncontext.SearchConfigServiceImpl;
import com.bjit.common.rest.app.service.model.itemImport.ObjectDataBean;
import com.bjit.common.rest.app.service.model.modelVersion.MVCreateAttributeModel;
import com.bjit.common.rest.app.service.model.modelVersion.MVCreateRequestModel;
import com.bjit.common.rest.app.service.model.modelVersion.MVCreateUpdateMemberResponse;
import com.bjit.common.rest.app.service.model.modelVersion.MVCreateUpdateResponse;
import com.bjit.common.rest.app.service.model.modelVersion.MVCreateUpdateResponseFormatter;
import com.bjit.common.rest.app.service.model.modelVersion.MVDataTree;
import com.bjit.common.rest.app.service.model.modelVersion.MVItemsModel;
import com.bjit.common.rest.app.service.model.modelVersion.MVUpdateRequestModel;
import com.bjit.common.rest.app.service.model.tnr.TNR;
import com.bjit.common.rest.app.service.utilities.BusinessObjectOperations;
import com.bjit.common.rest.app.service.utilities.CommonSearch;
import com.bjit.common.rest.app.service.utilities.CommonUtilities;
import com.bjit.common.rest.app.service.utilities.DsServiceCall;
import com.bjit.common.rest.app.service.utilities.JSON;
import com.bjit.common.rest.app.service.utilities.NullOrEmptyChecker;
import com.bjit.common.rest.item_bom_import.item_import.MANItemImport;
import com.bjit.common.rest.item_bom_import.xml_mapping_model.ResponseMessageFormaterBean;
import com.bjit.ewc18x.utils.PropertyReader;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import matrix.db.Context;
import matrix.db.MQLCommand;
import matrix.util.MatrixException;
import org.apache.http.Header;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.stereotype.Service;

/**
 *
 * @author Ahmad R Farabi [PLM Team, BJIT] <ahmad.farabi@bjitgroup.com>
 */
//model version create, get, update, item create, bom create, add interface, maturity state change

@Service
public class MVImportServiceImpl implements MVImportService {

    String source;
    String securityContext;
    String owner;
    String SOURCE_ATON = PropertyReader.getProperty("aton.integration.source");
    String adminUser = "coexusr1";

    private static final org.apache.log4j.Logger MODEL_VERSION_CREATION_SERVICE_LOGGER = org.apache.log4j.Logger.getLogger(MVImportServiceImpl.class);

    SearchConfigService search = new SearchConfigServiceImpl();
    
    @Override
    public void setSourceAndCS(String securityContext, String source, String owner) throws Exception {
        this.source = source;
        this.securityContext = securityContext;
        if (!NullOrEmptyChecker.isNullOrEmpty(owner) && owner.equalsIgnoreCase(adminUser)) {
            this.owner = null;
        } else {
            this.owner = owner;
        }
    }
    
    @Override
    public List<MVCreateUpdateResponseFormatter> createMV(
            List<MVDataTree> dataTree, DsServiceCall dsCall) throws Exception {

        MODEL_VERSION_CREATION_SERVICE_LOGGER.info(" ------------------ START: MV create service -----------");
        List<MVItemsModel> data = new ArrayList<>();
        dataTree.forEach((item) -> {
            MVCreateAttributeModel attr = new MVCreateAttributeModel();
            TNR tnr = item.getItem().getTnr();
            attr.setName(tnr.getName());
            attr.setTitle(item.getItem().getAttributes().get("title"));
            attr.setDescription(item.getItem().getAttributes().get("description") == null ? "" : item.getItem().getAttributes().get("description"));
            attr.setBasePrice(item.getItem().getAttributes().get("basePrice") == null ? "0.0" : item.getItem().getAttributes().get("basePrice"));
             //revision will always be default if source aton
            String rev = "";
            if(!source.equalsIgnoreCase(SOURCE_ATON)){
                rev = tnr.getRevision();
            }
            data.add(new MVItemsModel(rev, tnr.getType(), attr));
        });

        MVCreateRequestModel model = new MVCreateRequestModel(data);

        List<MVCreateUpdateResponseFormatter> results = new ArrayList<>();
        try {
            String url = PropertyReader.getProperty("ds.service.base.url.3dspace") + PropertyReader.getProperty("ds.service.modelversion.get.url");
            //System.out.println("url="+url);
            ObjectMapper mapper = new ObjectMapper();
            String resp = dsCall.callMVService(url, mapper.writeValueAsString(model), "POST");
            //System.out.println("resp="+resp);

            mapper = new ObjectMapper();
            MVCreateUpdateResponse respBean = mapper.readValue(resp, MVCreateUpdateResponse.class);
            List<MVCreateUpdateMemberResponse> member = respBean.getMember();

            member.forEach((item) -> {
                MVCreateUpdateResponseFormatter mvresp = new MVCreateUpdateResponseFormatter();
                TNR tnr = new TNR(item.getType(), item.getName(), item.getRevision());
                mvresp.setTnr(tnr);
                mvresp.setModelPhysicalId(item.getModelID());
                mvresp.setMvPhysicalId(item.getId());
                results.add(mvresp);
            });
            return results;
        } catch (JsonParseException e) {
            throw new Exception("MV error: Unexpected response");
        } catch (RuntimeException e) {
            throw new Exception("MV error: " + e);
        } catch (Exception e) {
            throw e;
        }
    }

    @Override
    public List<MVCreateUpdateResponseFormatter> updateMV(
            MVUpdateRequestModel model, String mvPhysicalId,
            DsServiceCall dsCall) throws Exception {

        MODEL_VERSION_CREATION_SERVICE_LOGGER.info(" ------------------ START: MV update service -----------");

        List<MVCreateUpdateResponseFormatter> results = new ArrayList<>();
        try {
            String url = PropertyReader.getProperty("ds.service.base.url.3dspace") + PropertyReader.getProperty("ds.service.modelversion.get.url") + "/" + mvPhysicalId;
            ObjectMapper mapper = new ObjectMapper();
            String resp = dsCall.callMVService(url, mapper.writeValueAsString(model), "PATCH");
            //System.out.println("resp="+resp);

            mapper = new ObjectMapper();
            MVCreateUpdateResponse respBean = mapper.readValue(resp, MVCreateUpdateResponse.class);
            List<MVCreateUpdateMemberResponse> member = respBean.getMember();

            member.forEach((item) -> {
                try {
                    MVCreateUpdateResponseFormatter mvresp = new MVCreateUpdateResponseFormatter();
                    TNR tnr = new TNR(item.getType(), item.getName(), item.getRevision());
                    mvresp.setTnr(tnr);
                    mvresp.setModelPhysicalId(item.getModelID());
                    mvresp.setMvPhysicalId(item.getId());
                    results.add(mvresp);
                } catch (Exception ex) {
                    Logger.getLogger(MVImportServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
                }
            });
            return results;
        } catch (JsonParseException e) {
            throw new Exception("MV error: Unexpected response");
        } catch (RuntimeException e) {
            throw new Exception("MV error: " + e);
        } catch (Exception e) {
            throw e;
        }
    }

    @Override
    public List<MVCreateUpdateResponseFormatter> getMV(String mvPhysicalId,
            DsServiceCall dsCall) throws Exception {

        MODEL_VERSION_CREATION_SERVICE_LOGGER.info(" ------------------ START: MV get service -----------");

        List<MVCreateUpdateResponseFormatter> results = new ArrayList<>();
        try {
            String url = PropertyReader.getProperty("ds.service.base.url.3dspace") + PropertyReader.getProperty("ds.service.modelversion.get.url") + "/" + mvPhysicalId;
            String resp = dsCall.callMVService(url, null, "GET");
            //System.out.println("resp="+resp);

            ObjectMapper mapper = new ObjectMapper();
            MVCreateUpdateResponse respBean = mapper.readValue(resp, MVCreateUpdateResponse.class);
            List<MVCreateUpdateMemberResponse> member = respBean.getMember();

            member.forEach((item) -> {
                try {
                    MVCreateUpdateResponseFormatter mvresp = new MVCreateUpdateResponseFormatter();
                    TNR tnr = new TNR(item.getType(), item.getName(), item.getRevision());
                    mvresp.setTnr(tnr);
                    mvresp.setModelPhysicalId(item.getModelID());
                    mvresp.setMvPhysicalId(item.getId());
                    results.add(mvresp);
                } catch (Exception ex) {
                    Logger.getLogger(MVImportServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
                }
            });

            return results;
        } catch (JsonParseException e) {
            throw new Exception("MV error: Unexpected response");
        } catch (RuntimeException e) {
            throw new Exception("MV error: " + e);
        } catch (Exception e) {
            throw e;
        }
    }

    @Override
    public List<HashMap<String, String>> createItems(Context context,
            ObjectDataBean objectDataBean) throws Exception {
        MODEL_VERSION_CREATION_SERVICE_LOGGER.info(" ------------------ START: MV Item create service -----------");
        MANItemImport itemImport = new MANItemImport();
        try {
            Instant startItemTime = Instant.now();
            HashMap<String, List<ResponseMessageFormaterBean>> tnrListMap = itemImport.doImport(context, objectDataBean);
            Instant endItemTime = Instant.now();
            MODEL_VERSION_CREATION_SERVICE_LOGGER.info("Time taken by only Items :" + Duration.between(startItemTime, endItemTime).toMillis());
            List<ResponseMessageFormaterBean> successFulItemList = tnrListMap.get("successFullList");
            List<ResponseMessageFormaterBean> errorItemList = tnrListMap.get("errorList");
            Boolean hasSuccessfulList = !NullOrEmptyChecker.isNullOrEmpty(successFulItemList);
            Boolean hasErrorList = !NullOrEmptyChecker.isNullOrEmpty(errorItemList);

            List<HashMap<String, String>> itemList = new ArrayList();

            if (hasSuccessfulList && hasErrorList) {
                errorItemList.forEach((error) -> {
                    HashMap<String, String> itemMap = new HashMap();
                    itemMap.put("type", error.getTnr().getType());
                    itemMap.put("name", error.getTnr().getName());
                    itemMap.put("revision", error.getTnr().getRevision());
                    itemMap.put("physicalId", error.getPhysicalId());
                    itemMap.put("error", error.getErrorMessage());
                    itemList.add(itemMap);
                });
                return itemList;
            } else {
                if (hasSuccessfulList && !hasErrorList) {
                    successFulItemList.forEach((success) -> {
                        HashMap<String, String> itemMap = new HashMap();
                        try {
                            TNR tnr = this.getTNRfromPhysicalId(context, success.getPhysicalId());
                            itemMap.put("type", tnr.getType());
                            itemMap.put("name", tnr.getName());
                            itemMap.put("revision", tnr.getRevision());
                            itemMap.put("physicalId", success.getPhysicalId());
                        }
                        catch (Exception ex) {
                            MODEL_VERSION_CREATION_SERVICE_LOGGER.fatal("Mfg item not found from Physical id");
                            itemMap.put("type", success.getTnr().getType());
                            itemMap.put("name", success.getTnr().getName());
                            itemMap.put("revision", success.getTnr().getRevision());
                            itemMap.put("physicalId", success.getPhysicalId());
                        }
                        itemList.add(itemMap);
                    });
                    return itemList;
                } else {
                    if (!hasSuccessfulList && hasErrorList) {
                        errorItemList.forEach((error) -> {
                            HashMap<String, String> itemMap = new HashMap();
                            itemMap.put("type", error.getTnr().getType());
                            itemMap.put("name", error.getTnr().getName());
                            itemMap.put("revision", error.getTnr().getRevision());
                            itemMap.put("physicalId", error.getPhysicalId());
                            itemMap.put("error", error.getErrorMessage());
                            itemList.add(itemMap);
                        });
                        return itemList;
                    } else {
                        MODEL_VERSION_CREATION_SERVICE_LOGGER.fatal("MV error: Unknown exception occurred");
                        throw new RuntimeException("MV error: Unknown exception occurred");
                    }
                }
            }
        } catch (Exception e) {
            throw e;
        }
    }

//    @Override
//    public List<HashMap<String, String>> createBom(Context context,
//            List<CreateBOMBeanExtended> createBOMBeanList) throws Exception {
//
//        MODEL_VERSION_CREATION_SERVICE_LOGGER.info(" ------------------ START: MV Bom create service -----------");
//        ItemOrBOMAbstractFactory MANFactory = ItemOrBOMImportFactoryProducer.getFactory(ItemOrBOMImportFactoryProducer.ITEM_TYPE_ODI);
//        ItemOrBOMImport MAN_BOMImport = MANFactory.getImportType(ItemOrBOMAbstractFactory.IMPORT_TYPE_BOM);
//        try {
//            HashMap<String, List<ParentInfo>> responseMsgMap = MAN_BOMImport.doImport(context, createBOMBeanList);
//
//            List<ParentInfo> successFulItemList = responseMsgMap.get("Successful");
//            List<ParentInfo> errorItemList = responseMsgMap.get("Error");
//
//            List<HashMap<String, String>> bomList = new ArrayList();
//
//            if ((successFulItemList != null && !successFulItemList.isEmpty()) && (errorItemList != null && !errorItemList.isEmpty())) {
//                createBOMBeanList.forEach((req) -> {
//                    for (ParentInfo error : errorItemList) {
//                        if (req.getItem().compareTo(error.getTnr()) == 0) {
//                            HashMap<String, String> bomMap = new HashMap();
//                            bomMap.put("type", error.getTnr().getType());
//                            bomMap.put("name", error.getTnr().getName());
//                            bomMap.put("revision", error.getTnr().getRevision());
//                            Object[] connArray = error.getConnectionPhysicalIdList().toArray();
//                            for (Object connArray1 : connArray) {
//                                bomMap.put("connectionPhysicalId", connArray1.toString());
//                            }
//                            bomMap.put("error", error.getErrorMessage());
//                            bomList.add(bomMap);
//                            break;
//                        }
//                    }
//                });
//
//                return bomList;
//            } else {
//                if (errorItemList != null && !errorItemList.isEmpty()) {
//                    createBOMBeanList.forEach((req) -> {
//                        for (ParentInfo error : errorItemList) {
//                            if (req.getItem().compareTo(error.getTnr()) == 0) {
//                                HashMap<String, String> bomMap = new HashMap();
//                                bomMap.put("type", error.getTnr().getType());
//                                bomMap.put("name", error.getTnr().getName());
//                                bomMap.put("revision", error.getTnr().getRevision());
//                                Object[] connArray = error.getConnectionPhysicalIdList().toArray();
//                                for (Object connArray1 : connArray) {
//                                    bomMap.put("connectionPhysicalId", connArray1.toString());
//                                }
//                                bomMap.put("error", error.getErrorMessage());
//                                bomList.add(bomMap);
//                                break;
//                            }
//                        }
//                    });
//                    return bomList;
//                } else {
//                    if (successFulItemList != null && !successFulItemList.isEmpty()) {
//                        createBOMBeanList.forEach((req) -> {
//                            for (ParentInfo success : successFulItemList) {
//                                if (req.getItem().compareTo(success.getTnr()) == 0) {
//                                    HashMap<String, String> bomMap = new HashMap();
//                                    bomMap.put("type", success.getTnr().getType());
//                                    bomMap.put("name", success.getTnr().getName());
//                                    bomMap.put("revision", success.getTnr().getRevision());
//                                    Object[] connArray = success.getConnectionPhysicalIdList().toArray();
//                                    for (Object connArray1 : connArray) {
//                                        bomMap.put("connectionPhysicalId", connArray1.toString());
//                                    }
//                                    bomList.add(bomMap);
//                                    break;
//                                }
//                            }
//                        });
//                        return bomList;
//                    } else {
//                        MODEL_VERSION_CREATION_SERVICE_LOGGER.fatal("MV error: Unknown exception occurred");
//                        throw new RuntimeException("MV error: Unknown exception occurred");
//                    }
//                }
//            }
//        } catch (Exception e) {
//            throw e;
//        }
//    }

    @Override
    public List<HashMap<String, String>> setEvolution(
            List<HashMap<String, String>> setEvolutionParams,
            DsServiceCall dsCall, Boolean isConfigured) throws Exception {
        String baseUrl = PropertyReader.getProperty("matrix.context.cas.connection.host.dslc");
//        String securityContext = PropertyReader.getProperty("aton.security.context.dslc");

        CSRFTokenGenerationService service = dsCall.getCSRFTokenService();
        try {
            CSRFToken token = service.getCSRFToken();
            ConfigContextAndEvolutionService conEvoSetService = new ConfigContextAndEvolutionServiceImpl(baseUrl, token,
                    this.securityContext, service.getSimpleHttpClient().getCookieStore(), ServiceType.INTERNAL_TYPE);

            List<ConfigContextAndEvolution> requestModelList = new ArrayList<>();
            setEvolutionParams.forEach((param) -> {
                ConfigContextAndEvolution evo = new ConfigContextAndEvolution(param.get("topItemPhysicalId"),
                        param.get("modelPhysicalId"), param.get("relationPhysicalId"), param.get("modelName"), param.get("modelRevision"), isConfigured);
                requestModelList.add(evo);
            });

            conEvoSetService.setRequestModel(requestModelList);
            List<ResponseConfigContextAndEvolution> response = conEvoSetService.execute();

            List<HashMap<String, String>> evoList = new ArrayList();
            int i = 0;
            if ((response != null && !response.isEmpty())) {
                for (ResponseConfigContextAndEvolution resp : response) {
                    HashMap<String, String> evoMap = new HashMap();
                    evoMap.put("isOkay", String.valueOf(resp.isOkay()));
                    evoMap.put("topItemPhysicalId", resp.getContext().getMfgPhysicalId());
                    evoMap.put("modelPhysicalId", resp.getContext().getModelPhysicalId());
                    evoMap.put("relationshipPhysicalId", resp.getContext().getRelationshipPhysicalId());
                    evoMap.put("modelName", resp.getContext().getModelName());
                    evoMap.put("revision", resp.getContext().getRevision());
                    evoMap.put("error", String.join(", ", resp.getMessageList()));
                    evoList.add(evoMap);
                    i++;
                }
                return evoList;
            } else {
                MODEL_VERSION_CREATION_SERVICE_LOGGER.fatal("MV error: Unknown exception occurred");
                throw new RuntimeException("MV error: Unknown exception occurred");
            }

        } catch (Exception e) {
            throw e;
        }
    }

    @Override
    public List<HashMap<String, String>> addAtonInterface(
            List<HashMap<String, String>> interfaceParams, String interfaceName, DsServiceCall dsCall) throws Exception {
        String baseUrl = PropertyReader.getProperty("matrix.context.cas.connection.host.dslc");
//        String securityContext = PropertyReader.getProperty("aton.security.context.dslc");
        //String interfaceName = PropertyReader.getProperty("aton.integration.modelversion.interface");

        CSRFTokenGenerationService service = dsCall.getCSRFTokenService();
        try {
            CSRFToken token = service.getCSRFToken();

            List<HashMap<String, String>> intList = new ArrayList();

            interfaceParams.forEach((param) -> {
                RequestModel reqModel = new RequestModel();
                List<String> phyIDS = new ArrayList<>();
                phyIDS.add(param.get("mvPhysicalId"));
                reqModel.setPhyIDS(phyIDS);
                Map<String, Map<String, String>> interfaces = new HashMap();
                Map<String, String> attributeValue = new HashMap();
                //attributeValue.put("MOD_AtonVersion", param.get("atonRevision"));
                //attributeValue.put("MOD_Mastership", param.get("mastership"));
                //attributeValue.put("MOD_AUTLifecycleStatus", param.get("status"));
                interfaces.put(interfaceName, attributeValue);
                reqModel.setInterfaces(interfaces);
                String reqStr = new JSON().serialize(reqModel);
                System.out.println(reqStr);

                List<Header> reqHeaders = new ArrayList<>();
                reqHeaders.add(new BasicHeader(token.getName(), token.getValue()));
                reqHeaders.add(new BasicHeader("SecurityContext", this.securityContext));
                reqHeaders.add(new BasicHeader("Content-Type", "application/json"));
                reqHeaders.add(new BasicHeader("Accept", "application/json"));

                AddInterfaceService s = new AddInterfaceServiceImpl(baseUrl, service.getSimpleHttpClient().getCookieStore(),
                        reqHeaders);

                try {
                    ResponseModel response = s.execute(reqModel);
                    if (response.getError() == null) {
                        MODEL_VERSION_CREATION_SERVICE_LOGGER.debug("add interface is successful========" + response);
                        HashMap<String, String> intMap = new HashMap();
                        intMap.put("isOkay", "true");
                        intMap.put("atonRevision", param.get("atonRevision"));
                        intList.add(intMap);
                    } else {
                        if (response.getError() != null) {
                            MODEL_VERSION_CREATION_SERVICE_LOGGER.fatal(response.getError().getMessageException());
                            HashMap<String, String> intMap = new HashMap();
                            intMap.put("isOkay", "false");
                            intMap.put("error", response.getError().getMessage());
                            intList.add(intMap);
                        } else {
                            MODEL_VERSION_CREATION_SERVICE_LOGGER.fatal("MV error: Unknown exception occurred");
                            throw new RuntimeException("MV error: Unknown exception occurred");
                        }
                    }
                } catch (Exception ex) {
                    MODEL_VERSION_CREATION_SERVICE_LOGGER.fatal(ex);
                }
            });
            return intList;
        } catch (Exception e) {
            throw e;
        }
    }

    @Override
    public Boolean isInterfaceExist(Context context, TNR tnr, String interfaceName) throws Exception {
        //String interfaceName = PropertyReader.getProperty("aton.integration.modelversion.interface");
        List<HashMap<String, String>> result = new ArrayList();
        CommonSearch search = new CommonSearch();
        List<String> selectList = new ArrayList();
        selectList.add("interface[" + interfaceName + "]");
        try {
            result = search.searchItem(context, tnr, selectList);
            if (result.get(0).get("interface[" + interfaceName + "]").equalsIgnoreCase("TRUE")) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            throw e;
        }
    }

    @Override
    public Boolean updateInterface(Context context,
            HashMap<String, String> param) throws Exception {
        CommonUtilities commonUtility = new CommonUtilities();
        BusinessObjectOperations boo = new BusinessObjectOperations();

        HashMap<String, String> attributeValue = new HashMap();
        List<String> interfaceAttributesList = Arrays.asList(param.get("interfaceAttributes").split(",", -1));
        for (Entry<String, String> entry : param.entrySet()) {
            if (interfaceAttributesList.contains(entry.getKey())) {
                attributeValue.put(entry.getKey(), entry.getValue());
            }
        }
        //attributeValue.put("MOD_AtonVersion", param.get("atonRevision"));
        attributeValue.put("MOD_Mastership", param.get("mastership"));
        String state = checkStatusForMaturity(param.get("status"));
        attributeValue.put("current", state);
        if (!NullOrEmptyChecker.isNull(owner)) {
            attributeValue.put("owner", owner);
        }

        try {
            commonUtility.doStartTransaction(context);
            Boolean result = boo.updateObject(context, param.get("mvPhysicalId"), attributeValue);
            if (result) {
                commonUtility.doCommitTransaction(context);
                return true;
            } else {
                commonUtility.doAbortTransaction(context);
                return false;
            }
        } catch (Exception e) {
            commonUtility.doAbortTransaction(context);
            throw e;
        }
    }

    @Override
    public Boolean updateMaturityState(Context context,
            List<HashMap<String, String>> paramList) throws Exception {
        CommonUtilities commonUtility = new CommonUtilities();
        BusinessObjectOperations boo = new BusinessObjectOperations();

        try {
            Boolean result = false;
            commonUtility.doStartTransaction(context);
            for (HashMap<String, String> param : paramList) {
                HashMap<String, String> attributeValue = new HashMap();
                String state = checkStatusForMaturity(param.get("status"));
                attributeValue.put("current", state);
                attributeValue.put("Marketing Name", param.get("title"));
                if (!NullOrEmptyChecker.isNull(owner)) {
                    attributeValue.put("owner", owner);
                }
                result = boo.updateObject(context, param.get("mvPhysicalId"), attributeValue);
            }
            if (result) {
                commonUtility.doCommitTransaction(context);
                return true;
            } else {
                commonUtility.doAbortTransaction(context);
                return false;
            }
        } catch (Exception e) {
            commonUtility.doAbortTransaction(context);
            throw e;
        }
    }

    @Override
    public List<HashMap<String, String>> changeMaturityState(
            List<HashMap<String, String>> statusParams, DsServiceCall dsCall) throws Exception {
        String baseUrl = PropertyReader.getProperty("matrix.context.cas.connection.host.dslc");
//        String securityContext = PropertyReader.getProperty("aton.security.context.dslc");

        CSRFTokenGenerationService service = dsCall.getCSRFTokenService();
        try {
            CSRFToken token = service.getCSRFToken();
            List<Header> reqHeaders = new ArrayList<>();
            reqHeaders.add(new BasicHeader(token.getName(), token.getValue()));
            reqHeaders.add(new BasicHeader("SecurityContext", this.securityContext));
            reqHeaders.add(new BasicHeader("Content-Type", "application/json"));
            reqHeaders.add(new BasicHeader("Accept", "application/json"));

            State state = new State();
            ArrayList<Data> dataList = new ArrayList<>();
            statusParams.forEach((param) -> {
                Data data = new Data();
                data.setId(param.get("mvPhysicalId"));
                String nextState = "Product Management";
                data.setNextState(nextState);
                dataList.add(data);
            });
            state.setData(dataList);

            StateChangeImpl fetch = new StateChangeImpl(baseUrl,
                    service.getSimpleHttpClient().getCookieStore(), reqHeaders);
            Response response = fetch.changeState(state);
            List<HashMap<String, String>> statusList = new ArrayList();
            if (response.getResults() != null && !response.getResults().isEmpty()) {
                response.getResults().forEach((resp) -> {
                    MODEL_VERSION_CREATION_SERVICE_LOGGER.debug("change maturity is successful========" + resp);
                    HashMap<String, String> statusMap = new HashMap();
                    statusMap.put("isOkay", "true");
                    statusMap.put("state", resp.getMaturityState());
                    statusList.add(statusMap);
                });
            } else {
                if (response.getErrorReport() != null && !response.getErrorReport().isEmpty()) {
                    response.getErrorReport().forEach((error) -> {
                        MODEL_VERSION_CREATION_SERVICE_LOGGER.fatal(error.getErrorMessage());
                        HashMap<String, String> statusMap = new HashMap();
                        statusMap.put("isOkay", "false");
                        statusMap.put("error", error.getErrorMessage());
                        statusList.add(statusMap);
                    });
                } else {
                    MODEL_VERSION_CREATION_SERVICE_LOGGER.fatal("MV error: Unknown exception occurred");
                    throw new RuntimeException("MV error: Unknown exception occurred");
                }
            }
            return statusList;
        } catch (Exception e) {
            throw e;
        }
    }

    @Override
    public Map<String, Map<String, Boolean>> checkForRevisions(
            List<Model> models, Map<String, List<String>> queueList,
            DsServiceCall dsCall, Context context) throws Exception {
        String baseUrl = PropertyReader.getProperty("matrix.context.cas.connection.host.dslc");
//        String securityContext = PropertyReader.getProperty("aton.security.context.dslc");

        CSRFTokenGenerationService service = dsCall.getCSRFTokenService();
        try {
            CSRFToken token = service.getCSRFToken();
            List<Header> reqHeaders = new ArrayList<>();
            reqHeaders.add(new BasicHeader(token.getName(), token.getValue()));
            reqHeaders.add(new BasicHeader("SecurityContext", this.securityContext));
            reqHeaders.add(new BasicHeader("Content-Type", "application/json"));
            reqHeaders.add(new BasicHeader("Accept", "application/json"));

            Set<String> modelIds = new LinkedHashSet<>();
            for (Model m : models) {
                if (m.getModelExists()) {
                    modelIds.add(m.getModelInfo().getPhysicalid());
                }
            }

            if (!source.equalsIgnoreCase(PropertyReader.getProperty("aton.integration.source"))) {
                CommonSearch search = new CommonSearch();
                for (String mv : modelIds) {
                    List<String> selectList = new ArrayList();
                    selectList.add("attribute[MOD_Mastership]");
                    HashMap<String, String> whereMap = new HashMap();
                    whereMap.put("physicalid", mv);

                    List<HashMap<String, String>> result = search.searchItem(context, new TNR(), whereMap, selectList);
                    if (result.get(0).get("attribute[MOD_Mastership]").equalsIgnoreCase(PropertyReader.getProperty("aton.integration.source"))) {
                        throw new Exception("Items with mastership 'ATON' can not be modified.");
                    }
                }
            }

            ArrayList<String> attr = new ArrayList<>();
            ArrayList<GraphRequest> graphRequestData = new ArrayList<>();
            attr.add("revision");
            attr.add("Aton Version");
            //attr.add("MOD_AtonVersion");
            //attr.add("MOD_AUTLifecycleStatus");

            for (String pid : modelIds) {
                GraphRequest graphRequest = new GraphRequest();
                graphRequest.setAttributes(attr);
                graphRequest.setId(pid);
                graphRequestData.add(graphRequest);
            }

            Product obj = new Product();
            obj.setGraphRequests(graphRequestData);
            ModelRevisionsDetailsImpl fetch = new ModelRevisionsDetailsImpl(baseUrl,
                    service.getSimpleHttpClient().getCookieStore(), reqHeaders);
            com.bjit.common.code.utility.dsapi.portfolio.model.ResponseModel revisions = fetch.getProducts(obj);

            //MODEL_VERSION_CREATION_SERVICE_LOGGER.info(new JSON().serialize(revisions));
            ProductsItemCodeService ser = new ProductsItemCodeServiceImpl();
            Map<String, Map<String, Boolean>> map = ser.execute(revisions, queueList, source);
            //MODEL_VERSION_CREATION_SERVICE_LOGGER.info(map.toString());

            List<Version> latestVersion = com.bjit.common.code.utility.dsapi.portfolio.model.ResponseModel.getLatestRevisions(revisions);
            MODEL_VERSION_CREATION_SERVICE_LOGGER.info(latestVersion.size() + "==" + latestVersion.get(0).getIsLastVersion() + latestVersion.get(0).getId());

            if (latestVersion.get(0).getName().equalsIgnoreCase(models.get(0).getTnr().getName())) {
                Map<String, Boolean> latestMap = new HashMap();
                if (source.equalsIgnoreCase(SOURCE_ATON)) {
                    if (!NullOrEmptyChecker.isNull(latestVersion.get(0).getAtonVersion())) {        
                        latestMap.put(latestVersion.get(0).getAtonVersion(), Boolean.TRUE);
                        map.put("latestRevision", latestMap);
                    } else {
                        map.put("latestRevision", null);
                    }
                 }else{
                     if (!NullOrEmptyChecker.isNull(latestVersion.get(0).getRevision())) {        
                        latestMap.put(latestVersion.get(0).getRevision(), Boolean.TRUE);
                        map.put("latestRevision", latestMap);
                    } else {
                        map.put("latestRevision", null);
                    }
                 }

                latestMap = new HashMap();
                latestMap.put(latestVersion.get(0).getId(), Boolean.TRUE);
                map.put("latestRevisionPhysicalId", latestMap);
            }
            return map;
        } catch (Exception e) {
            throw e;
        }
    }

    @Override
    public List<HashMap<String, String>> reviseMV(
            List<HashMap<String, String>> revParams, DsServiceCall dsCall) throws Exception {
        String baseUrl = PropertyReader.getProperty("matrix.context.cas.connection.host.dslc");
//        String securityContext = PropertyReader.getProperty("aton.security.context.dslc");

        CSRFTokenGenerationService service = dsCall.getCSRFTokenService();
        try {
            CSRFToken token = service.getCSRFToken();
            List<Header> reqHeaders = new ArrayList<>();
            reqHeaders.add(new BasicHeader(token.getName(), token.getValue()));
            reqHeaders.add(new BasicHeader("SecurityContext", this.securityContext));
            reqHeaders.add(new BasicHeader("Content-Type", "application/json"));
            reqHeaders.add(new BasicHeader("Accept", "application/json"));

            ReviseRequestModel reqModel = new ReviseRequestModel();
            ArrayList<com.bjit.common.code.utility.dsapi.portfolio.model.Data> dataList = new ArrayList<>();
            revParams.forEach((param) -> {
                com.bjit.common.code.utility.dsapi.portfolio.model.Data data = new com.bjit.common.code.utility.dsapi.portfolio.model.Data();
                data.setPhysicalid(param.get("mvPhysicalId"));
                HashMap<String, String> map = new HashMap();
                map.put("name", param.get("name"));
                map.put("description", param.get("description"));
                map.put("Base Price", param.get("basePrice"));
                //map.put("MOD_AtonVersion", param.get("revision"));
                map.put("MOD_Mastership", param.get("mastership"));
                //map.put("MOD_AUTLifecycleStatus", param.get("status"));
                data.setModifiedAttributes(map);
                dataList.add(data);
            });
            reqModel.setData(dataList);
            System.out.println(new JSON().serialize(reqModel));

            ReviseService s = new ReviseServiceImpl(baseUrl, service.getSimpleHttpClient().getCookieStore(),
                    reqHeaders);
            ReviseResponseModel response = s.execute(reqModel);

            List<HashMap<String, String>> revList = new ArrayList();
            if (response.getResults() != null && !response.getResults().isEmpty()) {
                response.getResults().forEach((resp) -> {
                    HashMap<String, String> revMap = new HashMap();
                    revMap.put("isOkay", "true");
                    revMap.put("majorId", resp.getMajorid());
                    revMap.put("physicalId", resp.getPhysicalid());
                    revMap.put("derivedFromPhysicalId", resp.getDerivedfromphysicalid());
                    revMap.put("revision", resp.getRevision());
                    revList.add(revMap);
                });
            } else {
                if (response.getReport() != null && !response.getReport().isEmpty()) {
                    response.getReport().forEach((error) -> {
                        MODEL_VERSION_CREATION_SERVICE_LOGGER.fatal(error.getError());
                        HashMap<String, String> revMap = new HashMap();
                        revMap.put("isOkay", "false");
                        revMap.put("error", error.getError());
                        revList.add(revMap);
                    });
                } else {
                    MODEL_VERSION_CREATION_SERVICE_LOGGER.fatal("MV error: Unknown exception occurred");
                    throw new RuntimeException("MV error: Unknown exception occurred");
                }
            }
            return revList;
        } catch (Exception e) {
            throw e;
        }
    }

    @Override
    public List<HashMap<String, String>> createItemInstance(
            List<HashMap<String, String>> instanceParams, DsServiceCall dsCall) throws Exception {
        String baseUrl = PropertyReader.getProperty("matrix.context.cas.connection.host.dslc");
//        String securityContext = PropertyReader.getProperty("aton.security.context.dslc");

        CSRFTokenGenerationService service = dsCall.getCSRFTokenService();
        try {
            CSRFToken token = service.getCSRFToken();
            List<Header> reqHeaders = new ArrayList<>();
            reqHeaders.add(new BasicHeader(token.getName(), token.getValue()));
            reqHeaders.add(new BasicHeader("SecurityContext", this.securityContext));
            reqHeaders.add(new BasicHeader("Content-Type", "application/json"));
            reqHeaders.add(new BasicHeader("Accept", "application/json"));

            List<HashMap<String, String>> itemList = new ArrayList();

            instanceParams.forEach((param) -> {
                ReferencedObject referencedObject1 = new ReferencedObject();
                referencedObject1.setIdentifier(param.get("childItemPhysicalId")); //child id
                referencedObject1.setSource(baseUrl);

                Instance instance1 = new Instance();
                instance1.setReferencedObject(referencedObject1);

                List<Instance> instances = new ArrayList<Instance>();
                instances.add(instance1);

                RequestInstances requestInstances = new RequestInstances();
                requestInstances.setParentId(param.get("topItemPhysicalId")); //top item id
                requestInstances.setInstances(instances);

                MFGInstanceService fetch = new MFGInstanceServiceImpl(baseUrl,
                        reqHeaders, service.getSimpleHttpClient().getCookieStore());

                try {
                    MFGInstanceResponse response = fetch.createMFGInstance(requestInstances);
                    String res = new JSON().serialize(response);
                    if (!NullOrEmptyChecker.isNullOrEmpty(response.getMember())) {
                        MODEL_VERSION_CREATION_SERVICE_LOGGER.debug("instance create is successful========" + response);
                        response.getMember().forEach((resp) -> {
                            HashMap<String, String> itemMap = new HashMap();
                            itemMap.put("type", resp.getType());
                            itemMap.put("name", resp.getName());
                            itemMap.put("connectionPhysicalId", resp.getId());
                            itemMap.put("childItemPhysicalId", resp.getReference());
                            itemMap.put("topItemPhysicalId", param.get("topItemPhysicalId"));
                            itemList.add(itemMap);
                        });
                    } else {
                        MODEL_VERSION_CREATION_SERVICE_LOGGER.fatal(response.getMessage());
                        HashMap<String, String> itemMap = new HashMap();
                        itemMap.put("error", response.getMessage());
                        itemList.add(itemMap);
                    }
                } catch (Exception ex) {
                    MODEL_VERSION_CREATION_SERVICE_LOGGER.fatal(ex);
                }
            });
            return itemList;
        } catch (Exception e) {
            throw e;
        }
    }

    @Override
    public HashMap<String, String> getChildInfoFromTopItem(
            String topItemPhysicalId, DsServiceCall dsCall) throws Exception {
        String baseUrl = PropertyReader.getProperty("matrix.context.cas.connection.host.dslc");
//        String securityContext = PropertyReader.getProperty("aton.security.context.dslc");

        CSRFTokenGenerationService service = dsCall.getCSRFTokenService();
        try {
            CSRFToken token = service.getCSRFToken();
            List<Header> reqHeaders = new ArrayList<>();
            reqHeaders.add(new BasicHeader(token.getName(), token.getValue()));
            reqHeaders.add(new BasicHeader("SecurityContext", this.securityContext));
            reqHeaders.add(new BasicHeader("Content-Type", "application/json"));
            reqHeaders.add(new BasicHeader("Accept", "application/json"));

            FetchMfgItemInstancesService fetch = new FetchMfgItemInstancesServiceImpl(baseUrl, reqHeaders, service.getSimpleHttpClient().getCookieStore());
            HashMap<String, String> itemCheck = new HashMap();
            try {
                MFGInstanceResponse response = fetch.fetchMfgInstances(topItemPhysicalId);
                String res = new JSON().serialize(response);

                if (!NullOrEmptyChecker.isNullOrEmpty(response.getMember())) {
                    MODEL_VERSION_CREATION_SERVICE_LOGGER.debug("instance create is successful========" + response);
                    Member member = FetchMfgItemInstancesServiceImpl.getLatestRevision(response);
                    itemCheck.put("childItemPhysicalId", member.getReference());
                    itemCheck.put("childItemRelationshipId", member.getId());
                    itemCheck.put("childItemName", member.getName());
                } else {
                    MODEL_VERSION_CREATION_SERVICE_LOGGER.fatal(response.getMessage());
                    itemCheck.put("error", response.getMessage());
                }
            } catch (Exception ex) {
                MODEL_VERSION_CREATION_SERVICE_LOGGER.fatal(ex);
            }

            return itemCheck;
        } catch (Exception e) {
            throw e;
        }
    }

    @Override
    public TNR getTNRfromPhysicalId(Context context, String physicalId) throws Exception {
        BusinessObjectOperations boo = new BusinessObjectOperations();

        try {
            TNR tnr = boo.getObjectTNR(context, physicalId);
            if (tnr != null) {
                return tnr;
            } else {
                return null;
            }
        } catch (Exception e) {
            throw e;
        }
    }

    @Override
    public Boolean updateTopItemSourceVersion(Context context,
            HashMap<String, String> param) throws Exception {
        CommonUtilities commonUtility = new CommonUtilities();
        BusinessObjectOperations boo = new BusinessObjectOperations();

        HashMap<String, String> attributeValue = new HashMap();
        attributeValue.put("MBOM_MBOMATON.MBOM_AtonVersion", param.get("latestRevision"));

        try {
            commonUtility.doStartTransaction(context);
            Boolean result = boo.updateObject(context, param.get("topItemPhysicalId"), attributeValue);
            if (result) {
                commonUtility.doCommitTransaction(context);
                return true;
            } else {
                commonUtility.doAbortTransaction(context);
                return false;
            }
        } catch (Exception e) {
            commonUtility.doAbortTransaction(context);
            throw e;
        }
    }

    @Override
    public Boolean updateContextItemInfo(Context context,
            HashMap<String, String> param) throws Exception {
        CommonUtilities commonUtility = new CommonUtilities();
        BusinessObjectOperations boo = new BusinessObjectOperations();

        HashMap<String, String> attributeValue = new HashMap();
        attributeValue.put("current", param.get("status"));
        attributeValue.put("MBOM_MBOMATON.MBOM_AtonVersion", param.get("latestRevision"));
        if (!NullOrEmptyChecker.isNull(owner)) {
            attributeValue.put("owner", owner);
        }
        try {
            commonUtility.doStartTransaction(context);
            Boolean result = boo.updateObject(context, param.get("physicalId"), attributeValue);
            if (result) {
                commonUtility.doCommitTransaction(context);
                return true;
            } else {
                commonUtility.doAbortTransaction(context);
                return false;
            }
        } catch (Exception e) {
            commonUtility.doAbortTransaction(context);
            throw e;
        }
    }

    @Override
    public Boolean updateChildItemInfo(Context context,
            HashMap<String, String> param) throws Exception {
        CommonUtilities commonUtility = new CommonUtilities();
        BusinessObjectOperations boo = new BusinessObjectOperations();

        HashMap<String, String> attributeValue = new HashMap();
        attributeValue.put("PLMEntity.V_Name", param.get("title"));
        attributeValue.put("description", param.get("description"));
        attributeValue.put("current", checkStatusForItems(param.get("status")));
        if (!NullOrEmptyChecker.isNullOrEmpty(param.get("mastership"))) {
            attributeValue.put("MBOM_MBOMATON.MBOM_Mastership", param.get("mastership"));
        }
        if (!NullOrEmptyChecker.isNullOrEmpty(param.get("itemCode"))) {
            attributeValue.put("MBOM_MBOMATON.MBOM_ItemCode", param.get("itemCode"));
        }
        attributeValue.put("MBOM_MBOMATON.MBOM_AtonVersion", param.get("version"));
        if (!NullOrEmptyChecker.isNull(owner)) {
            attributeValue.put("owner", owner);
        }

        try {
            commonUtility.doStartTransaction(context);
            Boolean result = boo.updateObject(context, param.get("physicalId"), attributeValue);
            if (result) {
                commonUtility.doCommitTransaction(context);
                return true;
            } else {
                commonUtility.doAbortTransaction(context);
                return false;
            }
        } catch (Exception e) {
            commonUtility.doAbortTransaction(context);
            throw e;
        }
    }

    @Override
    public List<HashMap<String, String>> classifyItem(
            List<HashMap<String, String>> classParams, String classId, DsServiceCall dsCall) throws Exception {
        String baseUrl = PropertyReader.getProperty("matrix.context.cas.connection.host.dslc");
//        String securityContext = PropertyReader.getProperty("aton.security.context.dslc");

        CSRFTokenGenerationService service = dsCall.getCSRFTokenService();
        try {
            CSRFToken token = service.getCSRFToken();
            List<Header> reqHeaders = new ArrayList<>();
            reqHeaders.add(new BasicHeader(token.getName(), token.getValue()));
            reqHeaders.add(new BasicHeader("SecurityContext", this.securityContext));
            reqHeaders.add(new BasicHeader("Content-Type", "application/json"));
            reqHeaders.add(new BasicHeader("Accept", "application/json"));

            List<HashMap<String, String>> classList = new ArrayList();

            classParams.forEach((param) -> {
                ItemClassifyRequestModel reqModel = new ItemClassifyRequestModel();
                List<ObjectToClassify> objectsToClassify = new ArrayList<>();

                ObjectToClassify products = new ObjectToClassify();
                products.setIdentifier(param.get("mvPhysicalId"));
                products.setType("dspfl:ModelVersion");
                products.setSource(baseUrl);
                products.setRelativePath("/resources/v1/modeler/dspfl/dspfl:ModelVersion/" + param.get("mvPhysicalId"));
                objectsToClassify.add(products);

                HashMap<String, String> classMap = new HashMap();

                reqModel.setClassID(classId);
                reqModel.setObjectsToClassify(objectsToClassify);
                ClassifiedItemService classify = new ClassifiedItemServiceImpl(baseUrl,
                        service.getSimpleHttpClient().getCookieStore(), reqHeaders);
                try {
                    ItemClassifyResponseModel classifiedResponse = classify.execute(reqModel);
                    String res = new JSON().serialize(classifiedResponse);
                    System.out.println(res);

                    if (!NullOrEmptyChecker.isNullOrEmpty(classifiedResponse.getStatusOfObjects())) {
                        MODEL_VERSION_CREATION_SERVICE_LOGGER.debug("classification is successful========" + classifiedResponse);
                        if (classifiedResponse.getStatusOfObjects().containsKey(param.get("mvPhysicalId"))) {
                            //if (classifiedResponse.getStatusOfObjects().get(param.get("mvPhysicalId")).getStatus().equalsIgnoreCase("OK")) {
                            classMap.put("mvPhysicalId", param.get("mvPhysicalId"));
                            classMap.put("isClassified", "true");
                            classList.add(classMap);
                            //}
                        }
                    } else {
                        MODEL_VERSION_CREATION_SERVICE_LOGGER.fatal(classifiedResponse.getMessage());
                        classMap.put("isClassified", "false");
                        classMap.put("error", classifiedResponse.getMessage());
                        classList.add(classMap);
                    }

                } catch (Exception ex) {
                    MODEL_VERSION_CREATION_SERVICE_LOGGER.fatal(ex);
                }
            });
            return classList;
        } catch (Exception e) {
            throw e;
        }
    }

    @Override
    public List<HashMap<String, String>> updateClassifyItem(
            List<HashMap<String, String>> classParams, DsServiceCall dsCall, Context context) throws Exception {
        String baseUrl = PropertyReader.getProperty("matrix.context.cas.connection.host.dslc");
//        String securityContext = PropertyReader.getProperty("aton.security.context.dslc");

        CSRFTokenGenerationService service = dsCall.getCSRFTokenService();
        try {
            CSRFToken token = service.getCSRFToken();
            List<Header> reqHeaders = new ArrayList<>();
            reqHeaders.add(new BasicHeader(token.getName(), token.getValue()));
            reqHeaders.add(new BasicHeader("SecurityContext", this.securityContext));
            reqHeaders.add(new BasicHeader("Content-Type", "application/json"));
            reqHeaders.add(new BasicHeader("Accept", "application/json"));

            // Request Bean preparation
            List<ModifyRequestModel> reqModelList = new ArrayList<ModifyRequestModel>();

            String undefinedError = "";
            List<HashMap<String, String>> classList = new ArrayList();

            for (HashMap<String, String> param : classParams) {
                ModifyRequestModel reqModel = new ModifyRequestModel();
                com.bjit.common.code.utility.dsapi.ipclassification.classifiedItem.model.ReferencedObject referencedObject = new com.bjit.common.code.utility.dsapi.ipclassification.classifiedItem.model.ReferencedObject();
                referencedObject.setIdentifier(param.get("mvPhysicalId"));
                referencedObject.setSource(baseUrl);
                referencedObject.setType("dslib:ClassifiedItem");
                referencedObject.setRelativePath(
                        "resources/v1/modeler/dslib/dslib:ClassifiedItem/" + param.get("mvPhysicalId"));

                Map<String, Object> attributesMap = new HashMap();

                CommonSearch search = new CommonSearch();
                List<String> selectList = new ArrayList();
                selectList.add("cestamp");
                HashMap<String, String> whereMap = new HashMap();
                whereMap.put("physicalid", param.get("mvPhysicalId"));

                try {
                    List<HashMap<String, String>> result = search.searchItem(context, new TNR(), whereMap, selectList);
                    attributesMap.put("cestamp", result.get(0).get("cestamp"));

                    List<String> classAttributes = Arrays.asList(param.get("classAttributes").split(",", -1));

                    for (Entry<String, String> entry : param.entrySet()) {
                        System.out.println(entry.getKey() + " " + entry.getValue());
                        if (classAttributes.contains(entry.getKey())) {
                            attributesMap.put(entry.getKey(), entry.getValue());
                            if (entry.getValue().equalsIgnoreCase("Undefined")) {
                                undefinedError += "'" + entry.getKey() + "' has unsupported value.";
                            }
                        }
                    }
                    //attributesMap.put("Sales statistical group SSG", "test value");
                } catch (Exception ex) {
                    Logger.getLogger(MVImportServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
                }

                reqModel.setReferencedObject(referencedObject);
                reqModel.setAttributes(attributesMap);

                reqModelList.add(reqModel);
            }

            ModifyAttributesService modify = new ModifyAttributesServiceImpl(baseUrl,
                    service.getSimpleHttpClient().getCookieStore(), reqHeaders);

            try {
                ModifyResponseModel response = modify.execute(reqModelList);
                String res = new JSON().serialize(response);
                System.out.println(res);

                if (!NullOrEmptyChecker.isNullOrEmpty(response.getMember())) {
                    MODEL_VERSION_CREATION_SERVICE_LOGGER.debug("classification update is successful========" + response);
                    if (NullOrEmptyChecker.isNullOrEmpty(undefinedError)) {
                        response.getMember().forEach((resp) -> {
                            HashMap<String, String> itemMap = new HashMap();
                            itemMap.put("name", resp.getName());
                            itemMap.put("cestamp", resp.getCestamp());
                            classList.add(itemMap);
                        });
                    } else {
                        for (com.bjit.common.code.utility.dsapi.ipclassification.classifiedItem.model.Member resp : response.getMember()) {
                            HashMap<String, String> itemMap = new HashMap();
                            itemMap.put("name", resp.getName());
                            itemMap.put("cestamp", resp.getCestamp());
                            itemMap.put("error", undefinedError);
                            classList.add(itemMap);
                        }
                    }
                } else {
                    MODEL_VERSION_CREATION_SERVICE_LOGGER.fatal(response.getMessage());
                    HashMap<String, String> itemMap = new HashMap();
                    itemMap.put("error", response.getMessage());
                    classList.add(itemMap);
                }

            } catch (Exception ex) {
                MODEL_VERSION_CREATION_SERVICE_LOGGER.fatal(ex);
            }
            return classList;
        } catch (Exception e) {
            throw e;
        }
    }

    @Override
    public Boolean checkClassified(
            String mvPhysicalId, String classId, DsServiceCall dsCall) throws Exception {
        String baseUrl = PropertyReader.getProperty("matrix.context.cas.connection.host.dslc");
//        String securityContext = PropertyReader.getProperty("aton.security.context.dslc");

        CSRFTokenGenerationService service = dsCall.getCSRFTokenService();
        try {
            CSRFToken token = service.getCSRFToken();
            List<Header> reqHeaders = new ArrayList<>();
            reqHeaders.add(new BasicHeader(token.getName(), token.getValue()));
            reqHeaders.add(new BasicHeader("SecurityContext", this.securityContext));
            reqHeaders.add(new BasicHeader("Content-Type", "application/json"));
            reqHeaders.add(new BasicHeader("Accept", "application/json"));

//            ClassifiedItemLocateRequestModel reqModel = new ClassifiedItemLocateRequestModel();
//            reqModel.setSource(baseUrl);
//            reqModel.setType("dspfl:ModelVersion");
//            reqModel.setIdentifier(mvPhysicalId);
//            reqModel.setRelativePath("/resources/v1/modeler/dspfl/dspfl:ModelVersion/" + mvPhysicalId);
//
//            String reqStr = new JSON().serialize(reqModel);
//            System.out.println(reqStr);
            PathParamRequest param = new PathParamRequest();
            param.setClassifiedItemId(mvPhysicalId);

            FetchInformation classifiedItemInfo = new FetchInformationImpl(baseUrl, service.getSimpleHttpClient().getCookieStore(),
                                                                           reqHeaders);

//            ClassifiedItemLocateService s = new ClassifiedItemLocateServiceImpl(baseUrl, service.getSimpleHttpClient().getCookieStore(),
//                    reqHeaders);
            List<NameValuePair> paramList = new ArrayList();
            BasicNameValuePair mask = new BasicNameValuePair("$mask", "dslib:ClassificationAttributesMask");
            paramList.add(mask);
            BasicNameValuePair classid = new BasicNameValuePair("$classId", classId);
            paramList.add(classid);

            try {
                ClassificationAtrributesResponse response = classifiedItemInfo.execute(param, paramList);
                System.out.println("res:" + response);
                if (!NullOrEmptyChecker.isNullOrEmpty(response.getMember().get(0).getClassificationAttributes().getMember())) {
                    MODEL_VERSION_CREATION_SERVICE_LOGGER.debug("classification is successful========" + response);
                    return true;
                } else {
                    MODEL_VERSION_CREATION_SERVICE_LOGGER.fatal(response.getMessage());
                    return false;
                }
            } catch (Exception ex) {
                MODEL_VERSION_CREATION_SERVICE_LOGGER.fatal(ex);
            }
            return false;
        } catch (Exception e) {
            throw e;
        }
    }

    @Override
    public List<HashMap<String, String>> mfgItemCreate(
            List<HashMap<String, String>> itemParamsList, DsServiceCall dsCall) throws Exception {
        String baseUrl = PropertyReader.getProperty("matrix.context.cas.connection.host.dslc");
//        String securityContext = PropertyReader.getProperty("aton.security.context.dslc");

        List<HashMap<String, String>> itemList = new ArrayList();
        CSRFTokenGenerationService service = dsCall.getCSRFTokenService();
        try {
            CSRFToken token = service.getCSRFToken();
            List<Header> reqHeaders = new ArrayList<>();
            reqHeaders.add(new BasicHeader(token.getName(), token.getValue()));
            reqHeaders.add(new BasicHeader("SecurityContext", this.securityContext));
            reqHeaders.add(new BasicHeader("Content-Type", "application/json"));
            reqHeaders.add(new BasicHeader("Accept", "application/json"));

            List<NameValuePair> paramList = new ArrayList<>();
            paramList.add(new BasicNameValuePair("$mask", "dsmfg:MfgItemMask.Default"));

            CreateMfgItemRequestModel reqModel = new CreateMfgItemRequestModel();

            List<Item> items = new ArrayList<>();

            itemParamsList.forEach((item) -> {
                Item item1 = new Item();
                Map<String, Object> attributes1Map = new HashMap<>();
                attributes1Map.put("title", item.get("title"));
                attributes1Map.put("description", item.get("description"));
                attributes1Map.put("type", item.get("type"));

                Map<String, Map<String, String>> interfaceAttr = new HashMap();
                attributes1Map.put("customerAttributes", interfaceAttr);
                Map<String, String> attr = new HashMap();
                attr.put("MBOM_Mastership", item.get("mastership"));
                attr.put("MBOM_AtonVersion", item.get("version"));
                attr.put("MBOM_ItemCode", item.get("itemCode"));
                interfaceAttr.put("MBOM_MBOMATON", attr);

                item1.setAttributes(attributes1Map);
                items.add(item1);
            });

            reqModel.setItems(items);

            String str = new JSON().serialize(reqModel);
            System.out.println(str);
            // -------- END: request model preparation ------------------------\\

            CreateMfgItemService creationMfgService = new CreateMfgItemServiceImpl(
                    baseUrl, service.getSimpleHttpClient().getCookieStore(), reqHeaders);
            try {
                CreateMfgItemResponseModel responseModel = creationMfgService.execute(reqModel);
                System.out.println("res:" + responseModel);
                if (!NullOrEmptyChecker.isNullOrEmpty(responseModel.getMember())) {
                    MODEL_VERSION_CREATION_SERVICE_LOGGER.debug("item create is successful========");
                    responseModel.getMember().forEach((res) -> {
                        HashMap<String, String> itemMap = new HashMap();
                        itemMap.put("type", res.getType());
                        itemMap.put("name", res.getName());
                        itemMap.put("revision", res.getRevision());
                        itemMap.put("physicalId", res.getId());
                        itemList.add(itemMap);
                    });
                    return itemList;
                } else {
                    responseModel.getMember().forEach((res) -> {
                        HashMap<String, String> itemMap = new HashMap();
                        itemMap.put("error", "Error in item creation");
                        itemList.add(itemMap);
                    });
                    return itemList;
                }
            } catch (Exception ex) {
                MODEL_VERSION_CREATION_SERVICE_LOGGER.fatal(ex);
            }
        } catch (Exception e) {
            throw e;
        }
        return null;
    }

    @Override
    public List<HashMap<String,String>> isInstanceExists(String topPhysicalId, String childPhysicalId, Context context) {
        List<HashMap<String,String>> instList = new ArrayList();
        try {
            String query = "expand bus " + topPhysicalId + " type CreateAssembly from recurse to all select bus where physicalid=='" + childPhysicalId + "' select rel physicalid dump ,";
            System.out.println("query " + query);
            MQLCommand mql = new MQLCommand();
            mql.executeCommand(context, query);
            String result = mql.getResult().trim();
            System.out.println("result =" + result);
            if(!NullOrEmptyChecker.isNullOrEmpty(result)){
                String [] dataArray = result.split(",");
                HashMap<String,String> instMap = new HashMap();
                instMap.put("type", dataArray[3]);
                instMap.put("name", dataArray[4]);
                instMap.put("connectionPhysicalId", dataArray[7]);
                instMap.put("childItemPhysicalId", childPhysicalId);
                instMap.put("topItemPhysicalId", topPhysicalId);
                instMap.put(query, result);
                instList.add(instMap);
            }
            return instList;
        }
        catch (MatrixException ex) {
            Logger.getLogger(MVImportServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
            return instList;
        }
    }

    @Override
    public List<HashMap<String, String>> createVersionOfMfgItem(
            List<HashMap<String, String>> versionParams, DsServiceCall dsCall, Context context) throws Exception {
        String baseUrl = PropertyReader.getProperty("matrix.context.cas.connection.host.dslc");

        CSRFTokenGenerationService service = dsCall.getCSRFTokenService();
        try {
            CSRFToken token = service.getCSRFToken();
            List<Header> reqHeaders = new ArrayList<>();
            reqHeaders.add(new BasicHeader(token.getName(), token.getValue()));
            reqHeaders.add(new BasicHeader("SecurityContext", this.securityContext));
            reqHeaders.add(new BasicHeader("Content-Type", "application/json"));
            reqHeaders.add(new BasicHeader("Accept", "application/json"));

            // Request Bean preparation
            VersionCreateRequest reqModel = new VersionCreateRequest();
            List<VersionCreateRequestAttributes> reqModelList = new ArrayList();

            List<HashMap<String, String>> verList = new ArrayList();
            versionParams.forEach((param) -> {
                VersionCreateRequestAttributes attr = new VersionCreateRequestAttributes();
                attr.setId(param.get("itemPhysicalId"));
                attr.setIdentifier(param.get("itemPhysicalId"));
                attr.setType(param.get("itemType"));
                attr.setSource(baseUrl);
                attr.setRelativePath(
                        "/resources/v1/modeler/dseng/dseng:EngItem/" + param.get("itemPhysicalId"));
                reqModelList.add(attr);
                reqModel.setData(reqModelList);
            });

            VersionCreateService version = new VersionCreateServiceImpl(baseUrl,
                                                                        service.getSimpleHttpClient().getCookieStore(), reqHeaders);

            try {
                VersionCreateResponse response = version.execute(reqModel);
                String res = new JSON().serialize(response);
                System.out.println(res);

                if (!NullOrEmptyChecker.isNullOrEmpty(response.getResults())) {
                    MODEL_VERSION_CREATION_SERVICE_LOGGER.debug("Versioning is successful========" + response);
                    response.getResults().forEach((resp) -> {
                        HashMap<String, String> verMap = new HashMap();
                        verMap.put("physicalId", resp.getId());
                        try {
                            TNR childTnr = this.getTNRfromPhysicalId(context, resp.getId());
                            verMap.put("type", childTnr.getType());
                            verMap.put("name", childTnr.getName());
                            verMap.put("revision", childTnr.getRevision());
                        }
                        catch (Exception ex) {
                            MODEL_VERSION_CREATION_SERVICE_LOGGER.fatal("Versioning failed");
                            verMap.put("type", resp.getType());
                        }
                        verList.add(verMap);
                    });
                } else {
                    MODEL_VERSION_CREATION_SERVICE_LOGGER.fatal("Versioning failed");
                    HashMap<String, String> itemMap = new HashMap();
                    itemMap.put("error", "Item versioning failed");
                    verList.add(itemMap);
                }

            } catch (Exception ex) {
                MODEL_VERSION_CREATION_SERVICE_LOGGER.fatal(ex);
            }
            return verList;
        } catch (Exception e) {
            throw e;
        }
    }

    @Override
    public Boolean updateOwnershipOfModel(Context context, String modelId) throws Exception {
        if(!NullOrEmptyChecker.isNull(owner)){
            CommonUtilities commonUtility = new CommonUtilities();
            BusinessObjectOperations boo = new BusinessObjectOperations();
            HashMap<String, String> attributeValue = new HashMap();
            attributeValue.put("owner", owner);

            try {
                commonUtility.doStartTransaction(context);
                Boolean result = boo.updateObject(context, modelId, attributeValue);
                if (result) {
                    commonUtility.doCommitTransaction(context);
                    return true;
                } else {
                    commonUtility.doAbortTransaction(context);
                    return false;
                }
            } catch (Exception e) {
                commonUtility.doAbortTransaction(context);
                throw e;
            }
        }
        return false;
    }

    @Override
    public Boolean addUserGroupToMV(
            List<HashMap<String, String>> params, DsServiceCall dsCall) throws Exception {
        String baseUrl = PropertyReader.getProperty("matrix.context.cas.connection.host.dslc");

        CSRFTokenGenerationService service = dsCall.getCSRFTokenService();
        try {
            CSRFToken token = service.getCSRFToken();
            List<Header> reqHeaders = new ArrayList<>();
            reqHeaders.add(new BasicHeader(token.getName(), token.getValue()));
            reqHeaders.add(new BasicHeader("SecurityContext", this.securityContext));
            reqHeaders.add(new BasicHeader("Content-Type", "application/json"));
            reqHeaders.add(new BasicHeader("Accept", "application/json"));

            // Request Bean preparation
            List<com.bjit.common.code.utility.dsapi.dslc.sharing.addUserGroupToObj.model.Data> dataList = new ArrayList<>();
            params.forEach((param) -> {
                UserGroup userGroup = new UserGroup();
                userGroup.setUserGroupID(PropertyReader.getProperty("aton.user.group.id"));
                userGroup.setAccess(PropertyReader.getProperty("aton.user.group.access"));
                List<UserGroup> userGroupsList = new ArrayList<>();
                userGroupsList.add(userGroup);
                CollabSpace collabSpace = new CollabSpace();
                if (!NullOrEmptyChecker.isNullOrEmpty(this.securityContext)) {
                    String[] cs = this.securityContext.split("\\.");
                    collabSpace.setCollabSpaceID(cs[2]);
                    collabSpace.setOrgID(cs[1]);
                }
                collabSpace.setAccess(PropertyReader.getProperty("aton.user.group.access"));
                List<CollabSpace> collabSpaceList = new ArrayList<>();
                collabSpaceList.add(collabSpace);
                User user = new User();
                user.setUserID(NullOrEmptyChecker.isNull(owner) ? adminUser : owner);
                user.setAccess(PropertyReader.getProperty("aton.user.group.access"));
                List<User> userList = new ArrayList<>();
                userList.add(user);
                Sharings sharings = new Sharings();
                sharings.setUsers(userList);
                sharings.setCollabSpaces(collabSpaceList);
                sharings.setUserGroups(userGroupsList);
                com.bjit.common.code.utility.dsapi.dslc.sharing.addUserGroupToObj.model.Data data = new com.bjit.common.code.utility.dsapi.dslc.sharing.addUserGroupToObj.model.Data();
                data.setObjectID(param.get("mvPhysicalId"));
                data.setSharings(sharings);

                dataList.add(data);
            });

            UserGroupRequestModel sharingRequestModel = new UserGroupRequestModel();
            sharingRequestModel.setData(dataList);

            AddUserGroupToObjService sharingService = new AddUserGroupToObjServiceImpl(baseUrl, reqHeaders, service.getSimpleHttpClient().getCookieStore());
            try {
                SharingResponseModel response = sharingService.setSharing(sharingRequestModel);
                String res = new JSON().serialize(response);
                System.out.println(res);

                if (!NullOrEmptyChecker.isNullOrEmpty(response.getErrorCode()) && response.getErrorCode().equalsIgnoreCase("0")) {
                    MODEL_VERSION_CREATION_SERVICE_LOGGER.debug("Add user group is successful========" + response);
                    return true;
                } else {
                    MODEL_VERSION_CREATION_SERVICE_LOGGER.fatal("Add user group failed");
                    return false;
                }

            } catch (Exception ex) {
                MODEL_VERSION_CREATION_SERVICE_LOGGER.fatal(ex);
            }
            return false;
        } catch (Exception e) {
            throw e;
        }
    }


    private String checkStatusForMaturity(String sourceStatus) {
        String destState;
        switch (sourceStatus) {
            case "Pilot":
                destState = "Review"; //Review
                break;
            case "Active":
            case "Phase-out":
            case "End-of-Life":
            case "End of Life":
                destState = "Release"; //Release
                break;
            case "Discontinued":
                destState = "Obsolete"; //Obsolete
                break;
            default:
                destState = "Design Engineering"; //Design Engineering
                break;
        }
        return destState;
    }

    private String checkStatusForItems(String sourceStatus) {
        String destStatus;
        switch (sourceStatus) {
            case "Pilot":
                destStatus = "FROZEN";
                break;
            case "Active":
            case "Phase-out":
            case "End-of-Life":
            case "End of Life":
                destStatus = "RELEASED";
                break;
            case "Discontinued":
                destStatus = "OBSOLETE";
                break;
            default:
                destStatus = "IN_WORK";
                break;
        }
        return destStatus;
    }
}
