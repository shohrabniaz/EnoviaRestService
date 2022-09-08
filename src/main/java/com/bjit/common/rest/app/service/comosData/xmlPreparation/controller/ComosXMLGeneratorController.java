package com.bjit.common.rest.app.service.comosData.xmlPreparation.controller;

//import com.bjit.common.rest.app.service.comosData.utils.ComosUtilities;
//import com.bjit.common.rest.app.service.comosData.xmlPreparation.builder.ComosXMLBuilder;
//import com.bjit.common.rest.app.service.comosData.xmlPreparation.model.comosxml.RFLP;
//import com.bjit.common.rest.app.service.comosData.xmlPreparation.model.equipment.EquipmentServiceResponse;
//import com.bjit.common.rest.app.service.comosData.xmlPreparation.utilServices.IServiceConsumer;
//import org.springframework.beans.factory.BeanFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Qualifier;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.HashMap;
import org.apache.log4j.Logger;

/*@RestController
@RequestMapping("/comos2/v1")*/
public class ComosXMLGeneratorController {

    /*private static final Logger ComosXMLGeneratorController_LOGGER = Logger.getLogger(ComosXMLGeneratorController.class);
    @Autowired
    BeanFactory beanFactory;

    @Autowired
    @Qualifier("JSONFileConsumer")
    IServiceConsumer<EquipmentServiceResponse> comosServiceConsumer;

    @Autowired
    ComosUtilities comosUtilities;

    @GetMapping("/xml/generator")
    public void createJAXBModel() throws IOException {

//        final Handler<ResponseNotifications, List<GreetingsNotificationEntity>> serviceResponseModel = BeanFactoryAnnotationUtils.qualifiedBeanOfType(beanFactory, Handler.class, "ResponseHandler");
        ComosXMLBuilder comosXMLBuilder = beanFactory.getBean(ComosXMLBuilder.class);
        HashMap<String, RFLP> RFLPMap = comosXMLBuilder.prepareComosXML(comosServiceConsumer.getServiceResponse());
        comosUtilities.writeXMLFile(RFLPMap);
        ComosXMLGeneratorController_LOGGER.info("XML file generation has been finished");

    }*/
}
