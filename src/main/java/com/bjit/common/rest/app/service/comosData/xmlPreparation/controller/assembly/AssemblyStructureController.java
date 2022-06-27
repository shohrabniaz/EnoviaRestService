package com.bjit.common.rest.app.service.comosData.xmlPreparation.controller.assembly;

import com.bjit.common.rest.app.service.aspects.annotations.LogExecutionTime;
import com.bjit.common.rest.app.service.comosData.exceptions.AssemblyStructureException;
import com.bjit.common.rest.app.service.comosData.responseHandler.HandleResponse;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.model.AssemblyRequestData;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.model.SessionModel;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.model.assembly.AssemblyRequestEnvelope;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.model.assembly.AssemblyServiceResponse;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.model.comosxml.RFLP;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.utilServices.IComosFileWriter;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.utilServices.IStructurePreparation;
import lombok.extern.log4j.Log4j;
import org.apache.log4j.Logger;
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

@Log4j
@RestController
@Validated
@RequestMapping("/comos/v1")
public class AssemblyStructureController {

    private static final Logger AssemblyStructureController_LOGGER = Logger.getLogger(AssemblyStructureController.class);
    @Autowired
    @Qualifier("ComosFileWriter")
    IComosFileWriter fileWriter;

    @Autowired
    @Qualifier("AssemblyStructurePreparation")
    IStructurePreparation<HashMap<String, RFLP>, AssemblyServiceResponse, AssemblyRequestData> structurePreparation;

    @Autowired
    HandleResponse handleResponse;

    @Autowired
    SessionModel sessionModel;

    @Autowired
    Environment env;

    @LogExecutionTime
    @PostMapping("/export/assembly/xml")
    public String createJAXBPlantModel(@Valid @RequestBody AssemblyRequestEnvelope requestData) throws IOException, AssemblyStructureException {
        try {

            requestData.setConnectInStructure(Boolean.TRUE);

            requestData.setServiceName("Assembly Structure Import");

            HashMap<String, RFLP> prepareStructure = structurePreparation.prepareStructure(requestData.getAssemblyRequestData());
            int numberOfFilesWritten = fileWriter.writeFile(prepareStructure, Boolean.parseBoolean(env.getProperty("logical.structure.xml.generation.in.chunk")));
            sessionModel.setXmlMapFileName(prepareStructure);

            String buildResponse = handleResponse.prepareResponse(requestData.getAssemblyRequestData(), requestData, numberOfFilesWritten);
            return buildResponse;
        } catch (Exception ex) {
            throw new AssemblyStructureException(ex.getMessage(), requestData.getAssemblyRequestData());
        }
    }
}
