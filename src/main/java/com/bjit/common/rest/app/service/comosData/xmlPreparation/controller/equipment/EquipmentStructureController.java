package com.bjit.common.rest.app.service.comosData.xmlPreparation.controller.equipment;

import com.bjit.common.rest.app.service.comosData.exceptions.EquipmentStructureException;
import com.bjit.common.rest.app.service.comosData.responseHandler.HandleResponse;

import com.bjit.common.rest.app.service.comosData.xmlPreparation.model.EquipmentRequestData;

import com.bjit.common.rest.app.service.comosData.xmlPreparation.integrationProcessors.IComosStructureCollector;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.model.*;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.model.assembly.AssemblyRequestBuilder;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.model.assembly.AssemblyRequestEnvelope;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.model.assembly.AssemblyServiceResponse;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.model.comosEnovia.ComosIntegration;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.model.comosEnovia.Deliverables;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.model.comosEnovia.LogicalItem;

import com.bjit.common.rest.app.service.comosData.xmlPreparation.model.comosxml.RFLP;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.model.equipment.EquipmentRequestBuilder;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.model.equipment.EquipmentRequestEnvelope;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.model.equipment.EquipmentServiceResponse;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.utilServices.IComosFileWriter;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.utilServices.IFileReader;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.utilServices.IStructurePreparation;

import org.apache.log4j.Logger;

import com.bjit.common.rest.app.service.model.tnr.TNR;
import com.bjit.common.rest.app.service.payload.common_response.IResponse;
import com.bjit.common.rest.app.service.payload.common_response.Status;
import com.bjit.common.rest.app.service.utilities.CommonSearch;
import com.bjit.common.rest.app.service.utilities.IJSON;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.BeanFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import org.springframework.core.env.Environment;

@Log4j
@RestController
@Validated
@RequestMapping("/comos/v1")
public class EquipmentStructureController {

    //    private static final Logger EquipmentStructureController_LOGGER = Logger.getLogger(EquipmentStructureController.class);
    @Autowired
    @Qualifier("ComosFileWriter")
    IComosFileWriter fileWriter;

    @Autowired
    @Qualifier("ComosFileReader")
    IFileReader fileReader;

    @Autowired
    @Qualifier("EquipmentStructurePreparation")
    IStructurePreparation<HashMap<String, RFLP>, EquipmentServiceResponse, EquipmentRequestData> structurePreparation;

    @Autowired
    @Qualifier("AssemblyStructurePreparation")
    IStructurePreparation<HashMap<String, RFLP>, AssemblyServiceResponse, AssemblyRequestData> assemblyStructurePreparation;


    @Autowired
    HandleResponse handleResponse;

    @Autowired
    IComosStructureCollector comosStructureCollector;

    @Autowired
    Environment env;

    @Autowired
    SessionModel sessionModel;

    @Autowired
    CommonSearch commonSearch;
    //    Function<String, Boolean> connectTaskAndLogicalItem = deliverablesMapFileData -> {
//
//        DeliverableTaskAndLogicalItemMap deserialize = json.deserialize(deliverablesMapFileData, DeliverableTaskAndLogicalItemMap.class);
//        List<Deliverables> deliverablesOfTaskAndLogicalItem = deserialize.getDeliverablesOfTaskAndLogicalItem();
//        return false;
//
//    };
//    BiFunction<String, HashMap<String, DeliverableTaskAndLogicalItemMap>, HashMap<String, DeliverableTaskAndLogicalItemMap>> getFilenameAndDataMap = (absolutePath, filenameAndDataMap) -> {
//        try {
//            filenameAndDataMap.put(absolutePath, json.deserialize(fileReader.readFile(absolutePath), DeliverableTaskAndLogicalItemMap.class));
//        } catch (IOException e) {
//            log.error(e);
//        }
//
//        return filenameAndDataMap;
//    };
    private final Predicate<LogicalItem> getLogicalItemsId = logicalItem -> {

        try {
            String logicalItemId = Optional.ofNullable(logicalItem.getItemId()).orElse(commonSearch.searchItem(sessionModel.getContext(), new TNR(logicalItem.getType(), logicalItem.getName(), null)).stream().findFirst().get().get("id"));
            logicalItem.setItemId(logicalItemId);
            return true;
        } catch (Exception e) {
            log.error(e);
            sessionModel.setErrorList(e.getMessage());
        }
        return false;
    };
    @Autowired
    IJSON json;
    @Autowired
    EquipmentRequestBuilder equipmentRequestBuilder;
    @Autowired
    AssemblyRequestBuilder assemblyRequestBuilder;
    @Autowired
    @Qualifier("CustomResponseBuilder")
    IResponse responseBuilder;
    @Autowired
    BeanFactory beanFactory;

    @PostMapping("/export/equipment/xml")
    public String equipmentItemsXMLExport(@Valid @RequestBody EquipmentRequestEnvelope requestData) throws IOException {
        try {

            requestData.setConnectInStructure(Boolean.TRUE);


            requestData.setServiceName("Equipment Structure Import");

            HashMap<String, RFLP> prepareStructure = structurePreparation.prepareStructure(requestData.getEquipmentRequestData());
            int numberOfFilesWritten = fileWriter.writeFile(prepareStructure, Boolean.parseBoolean(env.getProperty("logical.structure.xml.generation.in.chunk")));
            sessionModel.setXmlMapFileName(prepareStructure);

            String buildResponse = handleResponse.prepareResponse(requestData.getEquipmentRequestData(), requestData, numberOfFilesWritten);
            return buildResponse;
        } catch (Exception ex) {
            throw new EquipmentStructureException(ex.getMessage(), requestData.getEquipmentRequestData());
        }
    }

//    @GetMapping("/deliverableTask/logicalItem/connect")
//    public void deliverableTaskAndLogicalItemConnect() throws IOException {
//        try {
//            List<String> deliverableTaskAndLogicalItemList = fileReader
//                    .getFileListFromDirectoryWithSpecificExtension(env.getProperty("deliverable.task.and.logical.item.connection.file.directory"), "json");
//
//            HashMap<String, DeliverableTaskAndLogicalItemMap> filenameAndDataMap = new HashMap<>();
//            deliverableTaskAndLogicalItemList
//                    .stream()
//                    .forEach(absolutePath -> getFilenameAndDataMap.apply(absolutePath, filenameAndDataMap));
//
//            Optional.of(filenameAndDataMap)
//                    .stream()
//                    .filter(map -> !map.isEmpty())
//                    .map(comosStructureCollector::connectItems)
//                    .findAny()
//                    .ifPresent(unDeletableFileNames -> {
//                        List<String> allFilenames = new ArrayList(filenameAndDataMap.keySet());
//                        allFilenames.removeAll(unDeletableFileNames);
//                        allFilenames.stream().forEach(fileWriter::deleteFile);
//                    });
//        } catch (Exception ex) {
//            throw ex;
//        }
//    }

    @PostMapping("/deli/connect")
    public String deliConnect(@RequestBody ComosIntegration comosIntegrationRequestData) throws IOException {
        String xmlMapFilename = comosIntegrationRequestData.getMillNEquipmentId();
        ResponseModel responseModel = beanFactory.getBean(ResponseModel.class);
        responseModel.setRequestData(comosIntegrationRequestData);
        try {
            if (Optional.ofNullable(xmlMapFilename).isPresent()) {
                buildStructureWhenStructureMapIsPresent(xmlMapFilename);
            } else {
                buildStructureWithAlreadyImportedItems(comosIntegrationRequestData);
            }
        } catch (Exception exp) {

            responseModel.setErrorMassage(exp.getMessage());
            return responseBuilder.setData(responseModel).setStatus(Status.FAILED).buildResponse();
        }
        responseModel.setResponseMassage("Structure prepared successfully");
        return responseBuilder.setData(responseModel).setStatus(Status.OK).buildResponse();
    }

    private void buildStructureWithAlreadyImportedItems(ComosIntegration comosIntegrationRequestData) throws IOException {
        sessionModel.setDoNotGenerateLogicalItemsXMLFile(true);

        EquipmentRequestEnvelope equipmentRequestEnvelopeData = equipmentRequestBuilder.getEquipmentRequestEnvelopeData(comosIntegrationRequestData);
        EquipmentRequestData equipmentRequestData = equipmentRequestEnvelopeData.getEquipmentRequestData();

        HashMap<String, RFLP> prepareEquipmentStructure = structurePreparation.prepareStructure(equipmentRequestData);
//        int numberOfFilesWritten = fileWriter.writeFile(prepareEquipmentStructure, Boolean.parseBoolean(env.getProperty("logical.structure.xml.generation.in.chunk")));
        sessionModel.setXmlMapFileName(prepareEquipmentStructure);

        String buildResponse = handleResponse.prepareResponse(equipmentRequestData, equipmentRequestEnvelopeData, 0);

        AssemblyRequestEnvelope assemblyRequestEnvelopeData = assemblyRequestBuilder.getAssemblyRequestEnvelopeData(comosIntegrationRequestData);
        AssemblyRequestData assemblyRequestData = assemblyRequestEnvelopeData.getAssemblyRequestData();

        HashMap<String, RFLP> prepareAssemblyStructure = assemblyStructurePreparation.prepareStructure(assemblyRequestData);
//        numberOfFilesWritten = fileWriter.writeFile(prepareAssemblyStructure, Boolean.parseBoolean(env.getProperty("logical.structure.xml.generation.in.chunk")));
        sessionModel.setXmlMapFileName(prepareAssemblyStructure);

        buildResponse = handleResponse.prepareResponse(assemblyRequestData, assemblyRequestEnvelopeData, 0);

        log.error("############################");
        log.error("############################");
        log.error(sessionModel.getErrorList());
        log.error("############################");
        log.error("############################");

        buildStructureWhenStructureMapIsPresent(sessionModel.getXmlMapFileName());
    }

    private void buildStructureWhenStructureMapIsPresent(String xmlMapFilename) throws IOException {
        String xmlMapFileAbsolutePath = new StringBuilder()
                .append(env.getProperty("deliverable.task.and.logical.item.connection.file.directory"))
                .append(xmlMapFilename)
                .append(".")
                .append("json")
                .toString();

        String readTheIntermediateStructureMapFile = getIntermediateMapData(xmlMapFileAbsolutePath);

        DeliverableTaskAndLogicalItemMap deliverableTaskAndLogicalItemMap = json.deserialize(readTheIntermediateStructureMapFile, DeliverableTaskAndLogicalItemMap.class);

        List<Deliverables> deliverablesOfTaskAndLogicalItem = deliverableTaskAndLogicalItemMap.getDeliverablesOfTaskAndLogicalItem();

        if (Optional.ofNullable(deliverablesOfTaskAndLogicalItem).orElse(new ArrayList<>()).isEmpty()) {
            fileWriter.deleteFile(xmlMapFileAbsolutePath);
            throw new ComosItemNotFoundException("May be Task and Logical Items are missing in the System. Please import them first");
        }

        Optional.ofNullable(deliverablesOfTaskAndLogicalItem)
                .stream()
                .filter(deliverables -> !deliverables.isEmpty())
                .flatMap(List::stream)
                .forEach(deliverables -> {
                    String taskObjectId = deliverables.getDeliverableTask().getObjectId();
                    Optional
                            .ofNullable(deliverables.getDeliverablesList())
                            .orElse(new ArrayList<>())
                            .stream()
                            .filter(logicalItem -> !comosStructureCollector.expandTask(taskObjectId, logicalItem.getItemId()))
                            .filter(logicalItem -> Optional.ofNullable(logicalItem.getRelationId()).isEmpty())
                            .filter(getLogicalItemsId)
                            .forEach(logicalItem -> comosStructureCollector.connectItem(taskObjectId, logicalItem.getItemId()));

                });

        log.info("An email will be sent to '" + deliverableTaskAndLogicalItemMap.getEmail() + "' id");

        try {
            fileWriter.deleteFile(xmlMapFileAbsolutePath);
        } catch (Exception exp) {
            log.error(exp);
        }
    }

    private String getIntermediateMapData(String xmlMapFileAbsolutePath) throws IOException {
        String readTheIntermediateStructureMapFile;
        try{
            readTheIntermediateStructureMapFile = fileReader.readFile(xmlMapFileAbsolutePath);
        }
        catch(IOException exp){
            throw new IOException(exp.getMessage() + ". Nothing to be processed. Please complete the Logical Structure (Equipment and Assembly item) and Project Structure (Project Space and Task and Deliverable) import process first");
        }
        return readTheIntermediateStructureMapFile;
    }

}
