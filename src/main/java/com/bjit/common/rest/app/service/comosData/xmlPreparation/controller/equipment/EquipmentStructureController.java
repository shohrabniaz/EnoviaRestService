package com.bjit.common.rest.app.service.comosData.xmlPreparation.controller.equipment;

import com.bjit.common.rest.app.service.comosData.exceptions.EquipmentStructureException;
import com.bjit.common.rest.app.service.comosData.responseHandler.HandleResponse;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.model.AssemblyRequestData;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.model.EquipmentRequestData;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.model.ResponseModel;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.model.SessionModel;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.model.assembly.AssemblyServiceResponse;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.model.comosEnovia.ComosIntegration;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.model.comosxml.RFLP;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.model.equipment.EquipmentRequestEnvelope;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.model.equipment.EquipmentServiceResponse;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.utilServices.IComosFileWriter;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.utilServices.IStructurePreparation;
import com.bjit.common.rest.app.service.payload.common_response.IResponse;
import com.bjit.common.rest.app.service.payload.common_response.Status;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.env.Environment;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.io.IOException;
import java.util.HashMap;
import java.util.Optional;

@Log4j
@RestController
@Validated
@RequestMapping("/comos/v1")
public class EquipmentStructureController {
    @Autowired
    @Qualifier("ComosFileWriter")
    IComosFileWriter fileWriter;
    @Autowired
    @Qualifier("EquipmentStructurePreparation")
    IStructurePreparation<HashMap<String, RFLP>, EquipmentServiceResponse, EquipmentRequestData> structurePreparation;
    @Autowired
    @Qualifier("AssemblyStructurePreparation")
    IStructurePreparation<HashMap<String, RFLP>, AssemblyServiceResponse, AssemblyRequestData> assemblyStructurePreparation;
    @Autowired
    HandleResponse handleResponse;
    @Autowired
    Environment env;
    @Autowired
    SessionModel sessionModel;
    @Autowired
    @Qualifier("CustomResponseBuilder")
    IResponse responseBuilder;
    @Autowired
    BeanFactory beanFactory;
    @Autowired
    ComosDeliConService comosDeliConService;
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

    @PostMapping("/deli/connect")
    public String deliConnect(@RequestBody ComosIntegration comosIntegrationRequestData) throws IOException {
        String xmlMapFilename = comosIntegrationRequestData.getMillNEquipmentId();
        ResponseModel responseModel = beanFactory.getBean(ResponseModel.class);
        responseModel.setRequestData(comosIntegrationRequestData);
        try {
            if (Optional.ofNullable(xmlMapFilename).isPresent()) {
                comosDeliConService.buildStructureWhenStructureMapIsPresent(xmlMapFilename);
            } else {
                comosDeliConService.buildStructureWithAlreadyImportedItems(comosIntegrationRequestData);
            }
        } catch (Exception exp) {
            responseModel.setErrorMassage(exp.getMessage());
            return responseBuilder.setData(responseModel).setStatus(Status.FAILED).buildResponse();
        }
        responseModel.setResponseMassage("Structure prepared successfully");
        return responseBuilder.setData(responseModel).setStatus(Status.OK).buildResponse();
    }
}
