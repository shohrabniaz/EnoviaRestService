package com.bjit.common.rest.app.service.comosData.xmlPreparation.controller.equipment;

import com.bjit.common.code.utility.expand.Expand;
import com.bjit.common.code.utility.expand.builders.ExpandBuilder;
import com.bjit.common.code.utility.expand.model.ExpandModel;
import com.bjit.common.code.utility.expand.services.IExpandBuilder;
import com.bjit.common.code.utility.mail.builders.MailModelBuilder;
import com.bjit.common.code.utility.mail.constants.MailContentType;
import com.bjit.common.code.utility.mail.impls.Mail;
import com.bjit.common.code.utility.mail.models.MailModel;
import com.bjit.common.code.utility.mail.services.IMail;
import com.bjit.common.code.utility.mail.services.IMailModelBuilder;
import com.bjit.common.rest.app.service.comosData.responseHandler.HandleResponse;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.integrationProcessors.IComosStructureCollector;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.model.AssemblyRequestData;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.model.DeliverableTaskAndLogicalItemMap;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.model.EquipmentRequestData;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.model.SessionModel;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.model.assembly.AssemblyRequestBuilder;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.model.assembly.AssemblyRequestEnvelope;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.model.assembly.AssemblyServiceResponse;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.model.comosEnovia.ComosIntegration;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.model.comosEnovia.Deliverables;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.model.comosEnovia.EnoviaRequest;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.model.comosEnovia.LogicalItem;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.model.comosxml.RFLP;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.model.equipment.EquipmentRequestBuilder;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.model.equipment.EquipmentRequestEnvelope;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.model.equipment.EquipmentServiceResponse;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.utilServices.IComosFileWriter;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.utilServices.IFileReader;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.utilServices.IStructurePreparation;
import com.bjit.common.rest.app.service.model.tnr.TNR;
import com.bjit.common.rest.app.service.utilities.CommonSearch;
import com.bjit.common.rest.app.service.utilities.IJSON;
import lombok.extern.log4j.Log4j;
import matrix.db.BusinessObject;
import matrix.db.ExpansionWithSelect;
import matrix.db.RelationshipWithSelect;
import matrix.db.RelationshipWithSelectList;
import matrix.util.MatrixException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.function.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Log4j
@Service
public class ComosDeliConService {
    @Autowired
    SessionModel sessionModel;
    @Autowired
    EquipmentRequestBuilder equipmentRequestBuilder;
    @Autowired
    @Qualifier("EquipmentStructurePreparation")
    IStructurePreparation<HashMap<String, RFLP>, EquipmentServiceResponse, EquipmentRequestData> structurePreparation;
    @Autowired
    HandleResponse handleResponse;
    @Autowired
    AssemblyRequestBuilder assemblyRequestBuilder;
    @Autowired
    @Qualifier("AssemblyStructurePreparation")
    IStructurePreparation<HashMap<String, RFLP>, AssemblyServiceResponse, AssemblyRequestData> assemblyStructurePreparation;
    @Autowired
    Environment env;
    @Autowired
    IJSON json;
    @Autowired
    @Qualifier("ComosFileWriter")
    IComosFileWriter fileWriter;
    @Autowired
    IComosStructureCollector comosStructureCollector;
    @Autowired
    CommonSearch commonSearch;
    private final Predicate<LogicalItem> getLogicalItemsId = logicalItem -> {
        try {
            String logicalItemId = Optional
                    .ofNullable(logicalItem.getItemId())
                    .orElse(commonSearch
                            .searchItem(sessionModel.getContext(), new TNR(logicalItem.getType(), logicalItem.getName(), null))
                            .stream()
                            .findFirst()
                            .get()
                            .get("id"));

            logicalItem.setItemId(logicalItemId);
            return true;
        } catch (Exception e) {
            log.error(e);
            sessionModel.setErrorList(e.getMessage());
        }
        return false;
    };
    @Autowired
    @Qualifier("ComosFileReader")
    IFileReader fileReader;
    Supplier<ExpandModel> getExpandModel = () -> {
        IExpandBuilder expandBuilder = new ExpandBuilder();

        return expandBuilder
                .setObjectType("Task")
                .setObjectType("Project Space")

                .setObjectAttributes("name")
                .setObjectAttributes("type")
                .setRelationshipType("Subtask")
                .setRelationAttributes("name")

                .setExpandToParents(Boolean.TRUE)
                .setExpandToChildren(Boolean.FALSE)
                .setExpansionLevel((short) 99)
                .build();
    };
    Function<RelationshipWithSelect, String> getObjectTypeName = (relationshipWithSelect) -> {
        BusinessObject from = relationshipWithSelect.getFrom();
        try {
            from.open(sessionModel.getContext());
            String type = from.getTypeName();
            String name = from.getName();
            from.close(sessionModel.getContext());
            return type + "_" + name;
        } catch (MatrixException e) {
            throw new RuntimeException(e);
        }
    };
    Function<ExpansionWithSelect, String> getProjectSpace = (ExpansionWithSelect expandedData) -> {
        return Optional.of(expandedData)
                .stream()
                .map(ExpansionWithSelect::getRelationships)
                .map(RelationshipWithSelectList::getIterator)
                .map(iterable -> StreamSupport.stream(iterable.spliterator(), false).collect(Collectors.toList()))
                .flatMap(List::stream)
                .map(getObjectTypeName)
                .peek(System.out::println)
                .filter(typeName -> typeName.split("_")[0].equals("Project Space"))
                .map(typeName -> typeName.split("_")[1])
                .peek((projectSpace) -> log.info("Project Space is : " + projectSpace))
                .findAny()
                .orElse("");
    };

    BiFunction<String, ExpandModel, ExpansionWithSelect> getExpandedData = (taskId, expandModel) -> {
        try {
            return new Expand().expand(sessionModel.getContext(), taskId, expandModel);
        } catch (Exception e) {
            log.error(e);
            throw new RuntimeException(e);
        }
    };

    Consumer<String> findOutProjectSpace = (taskId) -> {
        if (Optional.ofNullable(sessionModel.getProjectSpace()).isEmpty()) {
            Optional.of(taskId)
                    .stream()
                    .peek(taskObjectId -> log.info("Task object id is : " + taskObjectId))
                    .map(task -> getExpandModel.get())
                    .map(expandModel -> getExpandedData.apply(taskId, expandModel))
                    .map(getProjectSpace)
                    .findAny()
                    .ifPresent((projectSpace) -> sessionModel.setProjectSpace(projectSpace));
        }
    };

    public void buildStructureWithAlreadyImportedItems(ComosIntegration comosIntegrationRequestData) throws IOException {
        sessionModel.setDoNotGenerateLogicalItemsXMLFile(true);

        EquipmentRequestEnvelope equipmentRequestEnvelopeData = equipmentRequestBuilder.getEquipmentRequestEnvelopeData(comosIntegrationRequestData);
        EquipmentRequestData equipmentRequestData = equipmentRequestEnvelopeData.getEquipmentRequestData();

        HashMap<String, RFLP> prepareEquipmentStructure = structurePreparation.prepareStructure(equipmentRequestData);
        sessionModel.setXmlMapFileName(prepareEquipmentStructure);

        String buildResponse = handleResponse.prepareResponse(equipmentRequestData, equipmentRequestEnvelopeData, 0);

        AssemblyRequestEnvelope assemblyRequestEnvelopeData = assemblyRequestBuilder.getAssemblyRequestEnvelopeData(comosIntegrationRequestData);
        AssemblyRequestData assemblyRequestData = assemblyRequestEnvelopeData.getAssemblyRequestData();

        HashMap<String, RFLP> prepareAssemblyStructure = assemblyStructurePreparation.prepareStructure(assemblyRequestData);
        sessionModel.setXmlMapFileName(prepareAssemblyStructure);

        buildResponse = handleResponse.prepareResponse(assemblyRequestData, assemblyRequestEnvelopeData, 0);

        buildStructureWhenStructureMapIsPresent(sessionModel.getXmlMapFileName());
    }

    public void buildStructureWhenStructureMapIsPresent(String xmlMapFilename) throws IOException {

        Object requestData = null;
        String email = "";
        try {
            String xmlMapFileAbsolutePath = new StringBuilder()
                    .append(env.getProperty("deliverable.task.and.logical.item.connection.file.directory"))
                    .append(xmlMapFilename)
                    .append(".")
                    .append("json")
                    .toString();

            String readTheIntermediateStructureMapFile = getIntermediateMapData(xmlMapFileAbsolutePath);

            DeliverableTaskAndLogicalItemMap deliverableTaskAndLogicalItemMap = json.deserialize(readTheIntermediateStructureMapFile, DeliverableTaskAndLogicalItemMap.class);
            requestData = deliverableTaskAndLogicalItemMap.getRequestData();
            email = deliverableTaskAndLogicalItemMap.getEmail();
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
                        findOutProjectSpace.accept(taskObjectId);
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

            sendMail(requestData, email, "Succeeded");
        } catch (Exception exp) {
            log.error(exp);
            sendMail(requestData, email, "Failed");
            throw exp;
        }
    }

    private String getIntermediateMapData(String xmlMapFileAbsolutePath) throws IOException {
        String readTheIntermediateStructureMapFile;
        try {
            readTheIntermediateStructureMapFile = fileReader.readFile(xmlMapFileAbsolutePath);
        } catch (IOException exp) {
            throw new IOException(exp.getMessage() + ". Nothing to be processed. Please complete the Logical Structure (Equipment and Assembly item) and Project Structure (Project Space and Task and Deliverable) import process first");
        }
        return readTheIntermediateStructureMapFile;
    }

    public void sendMail(Object requestData, String sender, String result) {
        try {
            EnoviaRequest comosIntegrationRequestData = json.deserialize(json.serialize(requestData), EnoviaRequest.class);

            String mailTemplateData = getMailTemplate(comosIntegrationRequestData, result);
            System.out.println(mailTemplateData);

            IMailModelBuilder mailModelBuilder = new MailModelBuilder();
            MailModel test_mail = mailModelBuilder
                    .setTo(sender)
                    .setSubject("Deliverable connection status")
                    .setData(mailTemplateData)
                    .setMailContentType(MailContentType.HTML)
                    .build();

            IMail mail = new Mail();

            mail.sendMail(test_mail);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

    private String getMailTemplate(EnoviaRequest comosIntegrationRequestData, String result) {
        return "<!DOCTYPE html>\n" +
                "<html lang=\"en\">\n" +
                "    <head>\n" +
                "        <title>Deliverable Linking Process</title>\n" +
                "        <style>\n" +
                "            .footer {\n" +
                "                position: fixed;\n" +
                "                left: 0;\n" +
                "                bottom: 0;\n" +
                "                width: 100%;\n" +
                "                font-weight: bold;\n" +
                "                font-size: 10px;\n" +
                "            }\n" +
                "            .footer mark {\n" +
                "                font-size: 12px;\n" +
                "            }\n" +
                "        </style>\n" +
                "    </head>\n" +
                "    <body>\n" +
                "        <div class=\"container\">\n" +
                "            Hello,\n" +
                "            <br />\n" +
                "            Deliverable linking process has been completed.<br />\n" +
                "            <b>CompassID : " + comosIntegrationRequestData.getCompassId() + "</b><br />\n" +
                "            <b>millId : " + comosIntegrationRequestData.getMillId() + "</b><br />\n" +
                "            <b>equipmentId : " + comosIntegrationRequestData.getEquipmentId() + "</b><br />\n" +
//                "            <b>comosDeviceStructureLevel : " + comosIntegrationRequestData.getComosDeviceStructureLevel() + "</b><br />\n" +
//                "            <b>category : " + comosIntegrationRequestData.getCategory() + "</b><br />\n" +
                "            <b>project : " + sessionModel.getProjectSpace() + "</b><br />\n" +
                "Deliverable linking has been " + result + "\n" +
                "        </div>\n" +
                "        <div class=\"footer\">\n" +
                "            This email may contain confidential and/or legally privileged information. For any mismatch , please inform concern person immediately\n" +
                "        </div>\n" +
                "    </body>\n" +
                "</html>";
    }
}
