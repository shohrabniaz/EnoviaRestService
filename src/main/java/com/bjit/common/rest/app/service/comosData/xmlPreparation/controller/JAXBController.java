package com.bjit.common.rest.app.service.comosData.xmlPreparation.controller;

//import com.bjit.common.rest.app.service.comosData.xmlPreparation.model.comosxml.RFLP;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//import javax.xml.bind.JAXBContext;
//import javax.xml.bind.Unmarshaller;
//import java.io.File;
//import org.apache.log4j.Logger;
//
//@RestController
//@RequestMapping("/import/jaxb")
public class JAXBController {

//    private static final Logger JAXBController_LOGGER = Logger.getLogger(JAXBController.class);
//
//    @GetMapping("/xml/create")
//    public String createJAXBModel() {
//
//        try {
//            File file = new File("C:\\Users\\BJIT\\Desktop\\jaxb\\PlantTemplate.xml");
//            JAXBContext jaxbContext = JAXBContext.newInstance(RFLP.class);
//
//            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
//            RFLP rflp = (RFLP) jaxbUnmarshaller.unmarshal(file);
//            JAXBController_LOGGER.info("PlantTemplate.xml is working okay");
//            System.out.println("Fine it is working");
//        } catch (Exception exp) {
//            System.out.println(exp.getMessage());
//            JAXBController_LOGGER.info(exp.getMessage());
//
//        }
//
//        return "";
//    }
}
