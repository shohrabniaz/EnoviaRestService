/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.app.service.enovia_pdm.service;

import com.bjit.common.rest.app.service.enovia_pdm.exceptions.MastershipChangeException;
import com.bjit.common.rest.app.service.enovia_pdm.exceptions.NoFileFoundException;
import com.bjit.common.rest.app.service.enovia_pdm.handlers.IRequestHandler;
import com.bjit.common.rest.app.service.enovia_pdm.models.*;
import com.bjit.common.rest.app.service.enovia_pdm.models.xml.Item;
import com.bjit.common.rest.app.service.enovia_pdm.processors.interfaces.IFileProcessor;
import com.bjit.common.rest.app.service.enovia_pdm.processors.interfaces.IRequestDataBuilder;
import com.bjit.common.rest.app.service.enovia_pdm.processors.interfaces.IStructureCollector;
import com.bjit.common.rest.app.service.model.tnr.TNR;
import com.bjit.common.rest.app.service.payload.common_response.Status;
import com.bjit.common.rest.app.service.utilities.BusinessObjectOperations;
import com.bjit.common.rest.app.service.utilities.JSON;
import com.bjit.common.rest.app.service.utilities.NullOrEmptyChecker;
import com.bjit.common.rest.app.service.webServiceConsumer.RequestModel;
import com.bjit.ewc18x.utils.PropertyReader;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.matrixone.apps.domain.util.FrameworkException;
import com.matrixone.apps.domain.util.MqlUtil;
import matrix.db.Context;
import matrix.util.MatrixException;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.Map;


/**
 * @author BJIT
 */
//@Component
////@Scope(value = "prototype", proxyMode = ScopedProxyMode.TARGET_CLASS)
//@Scope(value = "prototype")
public class MasterShipChangeService implements IMasterShipChange {

    private final Logger MASTER_SHIP_CHANGE_SERVICE_LOGGER = Logger.getLogger(MasterShipChangeService.class);
    @Autowired
    IFileProcessor fileProcessor;
    @Autowired
    IStructureCollector structureCollector;
    @Autowired
    IRequestDataBuilder requestDataBuilder;
    @Autowired
    JSON json;
    @Autowired
    IRequestHandler requestHandler;

    //    HashMap<String, ServiceRequestSequencer> requestSequencerHashMap;
    List<ServiceRequestSequencer> requestSequencerList;
    HashMap<String, ServiceRequestSequencer> responseSequencerHashMap;
    HashMap<String, String> mapOfEmailAddressByItem;
    List<ResponseModel> parentResponseList;

    @Override
    public List<String> change(Context context) throws Exception {
        responseSequencerHashMap = new HashMap<>();
        requestSequencerList = new ArrayList<>();
        mapOfEmailAddressByItem = new HashMap<>();
        parentResponseList = new ArrayList<>();

        List<HashMap<String, List<ParentChildModel>>> parentChildRelationMapList = collectStructures(context);

        List<String> serviceResponseList = getServiceResponses(context,parentChildRelationMapList);

        return serviceResponseList;
    }

    protected List<String> getServiceResponses(Context context,List<HashMap<String, List<ParentChildModel>>> parentChildRelationMapList) {
        // Should be in parallel processing
        List<String> serviceResponseList = new ArrayList<>();
        parentChildRelationMapList.forEach((HashMap<String, List<ParentChildModel>> parentChildRelationMap) -> {

            Map.Entry<String,List<ParentChildModel>> entry = parentChildRelationMap.entrySet().iterator().next();
            List<ParentChildModel> firstParentChildModel = entry.getValue();
            String parentName = firstParentChildModel.get(0).getParentData().getTnr().getName();

            ServiceRequestSequencer serviceRequestSequencer = getServiceRequestSequencer(parentChildRelationMap);

            HashMap<String, PdmBom> preparedRequestData = requestDataBuilder.prepareRequestData(parentChildRelationMap);

            List<PdmBom> bomStructureList = new ArrayList<>(preparedRequestData.values());


            removeCommercialItemInformation(bomStructureList);



//            String serializeData = json.serialize(bomStructureList);
//            String serializeData = new JSON(true).serialize(bomStructureList);
            Gson gson = new GsonBuilder().create();
            String serializeData = gson.toJson(bomStructureList);


            try {
                RequestModel preparedRequest = requestHandler.prepareRequest(serializeData);

                String property = PropertyReader.getProperty("enovia.pdm.integration.service.mock.response");
                boolean mockResponse = Boolean.parseBoolean(property);

                String response = requestHandler.sendRequest(preparedRequest, mockResponse);

                ResponseModel pdmMastershipResponse = gson.fromJson(response,ResponseModel.class);

                if (pdmMastershipResponse.getStatus().name().equalsIgnoreCase("OK")) {
                    setPDMRevisionForRevisedItems(context, parentChildRelationMap, pdmMastershipResponse);
                }
                else{
                    pdmMastershipResponse.setMail(mapOfEmailAddressByItem.get(parentName));
                    parentResponseList.add(pdmMastershipResponse);
                }

                response = emptyResponseHandler(response, serviceRequestSequencer);
                responseSequencerHashMap.put(response, serviceRequestSequencer);

                serviceResponseList.add(response);

            } catch (Exception ex) {
                MASTER_SHIP_CHANGE_SERVICE_LOGGER.error(ex);
                throw new RuntimeException(ex);
            }
        });
        return serviceResponseList;
    }

    private void setPDMRevisionForRevisedItems(Context context, HashMap<String, List<ParentChildModel>> parentChildRelationMap, ResponseModel pdmMastershipResponse) {
        parentChildRelationMap.forEach((parentName, parentChildModelList)->{
            ChildData parentData = parentChildModelList.get(0).getParentData();
            if(NullOrEmptyChecker.isNullOrEmpty(parentData.getTnr().getRevision())){
                pdmMastershipResponse.getItems().forEach(item->{
                    BusinessObjectOperations businessObjectOperations = new BusinessObjectOperations();
                    try {
                        businessObjectOperations.updateObject(context,parentData.getId(),"MBOM_MBOMPDM.MBOM_PDM_Revision",item.getTnr().getRevision());
                    } catch (FrameworkException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                });
            }
        });

    }

    private void removeCommercialItemInformation(List<PdmBom> bomStructureList) {
        bomStructureList.forEach(bomStructure -> bomStructure.getChildItems());
        bomStructureList
                .stream()
                .map(bom -> bom.getChildItems())
                .flatMap(List::stream)
                .filter(childItem -> childItem.getOwner().equalsIgnoreCase("Commercial item"))
                .forEach(childItem -> {
                    //childItem.setType("commercial items");
//                    childItem.setType("Nut");
//                    childItem.setType(null);
//                    childItem.setName(null);
                   // childItem.setRevision("00");
//                    childItem.setOwner(null);
                    childItem.setAttributes(null);
                });
    }

    private String emptyResponseHandler(String response, ServiceRequestSequencer serviceRequestSequencer) {
        ResponseModel responseModel = json.deserialize(response, ResponseModel.class);
        if (responseModel.getStatus().equals(Status.FAILED)) {
            if (Optional.ofNullable(responseModel.getMessages()).isEmpty()) {
//                ServiceRequestSequencer serviceRequestSequencer = getServiceRequestSequencer(parentChildRelationMap);

                Item exportItem = serviceRequestSequencer.getExportItem();
                Message newMessage = new Message(new TNR(exportItem.getType(), exportItem.getName(), exportItem.getRevision()), "PDM responded empty response for " + serviceRequestSequencer.getFilename() + " file");

                responseModel.setMessages(List.of(newMessage));

                return json.serialize(responseModel);
            }
        }
        return response;
    }

    private ServiceRequestSequencer getServiceRequestSequencer(HashMap<String, List<ParentChildModel>> parentChildRelationMap) {
        ServiceRequestSequencer serviceRequestSequencer = requestSequencerList
                .stream()
                .filter(requestSequencer -> requestSequencer.getStructure().equals(parentChildRelationMap))
                .findAny()
                .get();
        return serviceRequestSequencer;
    }

    @Override
    public HashMap<String, ServiceRequestSequencer> getResponseSequencerHashMap() {
        return responseSequencerHashMap;
    }

    @Override
    public HashMap<String, String> getmapOfEmailAddressByItem() {
        return mapOfEmailAddressByItem;
    }

    @Override
    public List<ResponseModel> getparentResponseList() {
        return parentResponseList;
    }

    protected List<HashMap<String, List<ParentChildModel>>> collectStructures(Context context) throws IOException {
        HashMap<String, Item> processXMLFiles = fileProcessor.processXMLFiles();
        if (processXMLFiles.isEmpty()) {
            throw new NoFileFoundException("No file found in " + fileProcessor.getFileDirectory());
        }
        List<HashMap<String, List<ParentChildModel>>> parentChildRelationMapList = new ArrayList<>();
        // Should be in parallel processing

        processXMLFiles.forEach((String fileName, Item enoviaExportItem) -> {
            try {
                HashMap<String, List<ParentChildModel>> parentChildRelationMap = structureCollector.fetchStructure(context, enoviaExportItem);
                HashMap<String, List<ParentChildModel>> mergeStructure = structureCollector.mergeStructure(parentChildRelationMap);
                parentChildRelationMapList.add(mergeStructure);
                ServiceRequestSequencer serviceRequestSequencer = new ServiceRequestSequencer(fileName, mergeStructure, enoviaExportItem);
//                requestSequencerHashMap.put(enoviaExportItem.getName(), serviceRequestSequencer);
                setUserEmailbyItem(context , enoviaExportItem.getUsername(), enoviaExportItem.getName());
                requestSequencerList.add(serviceRequestSequencer);
                //                moveFileToOld(fileName);
            } catch (MatrixException ex) {
                errorResponseGenerate(fileName, enoviaExportItem, ex);
                //throw mastershipChangeException;
            } catch (Exception ex) {
                errorResponseGenerate(fileName, enoviaExportItem, ex);
                MASTER_SHIP_CHANGE_SERVICE_LOGGER.error(ex);
                //throw new RuntimeException(ex);
            }
        });
        return parentChildRelationMapList;
    }
    
    private void setUserEmailbyItem(Context context , String username, String itemName){
        String emailAddress = "";
        StringBuilder commandBuilder = new StringBuilder();
        try {
//          Example MQL Command: "print person coexusr1 select email"
            commandBuilder.append("print person ").append(username).append(" select email dump");
            emailAddress = MqlUtil.mqlCommand(context, commandBuilder.toString());
            mapOfEmailAddressByItem.put(itemName,emailAddress);
            MASTER_SHIP_CHANGE_SERVICE_LOGGER.info("Email Address : " + emailAddress);
            
        }
        catch (Exception ex) {
            MASTER_SHIP_CHANGE_SERVICE_LOGGER.error(ex.getMessage());
        }
    }



    List<ResponseModel> listOfResponseModel = new ArrayList<>();
    public List<ResponseModel> getUnsuccessfulProcesses(){
        return this.listOfResponseModel;
    }
    private void errorResponseGenerate(String fileName, Item enoviaExportItem, Exception ex) {
        TNR tnr = new TNR(enoviaExportItem.getType(), enoviaExportItem.getName(), enoviaExportItem.getRevision());

        Message message = new Message(tnr, ex.getMessage());
        List<Message> messageList = new ArrayList<>();
        messageList.add(message);

        MASTER_SHIP_CHANGE_SERVICE_LOGGER.error(ex);

        ResponseModel responseModel = new ResponseModel(Status.FAILED, null, messageList, null);

        listOfResponseModel.add(responseModel);

        HashMap<String, List<ResponseModel>> response = new HashMap<>();
        response.put("unsuccessful", listOfResponseModel);

        MastershipChangeException mastershipChangeException = new MastershipChangeException(ex);
        mastershipChangeException.setErrorException(response);
        moveFileToError(fileName);
    }

    @Override
    public void moveFileToOld(String fileName) {
        String fileSource = fileProcessor.getFileDirectory() + "/" + fileName;
        fileProcessor.moveToFolder(fileSource, "old");
        fileProcessor.deleteFile(fileSource);
    }

    @Override
    public void moveFileToError(String fileName) {
        String fileSource = fileProcessor.getFileDirectory()+ "/" + fileName;
        fileProcessor.moveToFolder(fileSource, "error");
        fileProcessor.deleteFile(fileSource);
    }
}
