package com.bjit.common.rest.app.service.comosData.xmlPreparation.controller.integration;

import com.bjit.common.rest.app.service.comosData.xmlPreparation.integrationProcessors.ComosStructureCollector;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.model.comosEnovia.ComosIntegration;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.io.IOException;

@RestController
@Validated
@RequestMapping("/comos/v1")
public class ComosIntegrationController {
    private static final Logger COMOS_INTEGRATION_CONTROLLER_LOGGER = Logger.getLogger(ComosIntegrationController.class);

//    @Autowired
//    ComosStructureCollector structureCollector;

    @PostMapping("/export/integrate")
    public String integrate(@Valid @RequestBody ComosIntegration requestData) throws IOException {
//        structureCollector.collectStructure(requestData);
        return "Response not prepared yet";
    }
}
