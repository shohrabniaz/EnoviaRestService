//package com.bjit.common.rest.app.service.controller.comos;
//
//import org.springframework.beans.factory.BeanFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.BeanFactoryAnnotationUtils;
//import org.springframework.beans.factory.annotation.Qualifier;
//import org.springframework.core.env.Environment;
//import org.springframework.web.bind.annotation.*;
//import com.bjit.common.rest.app.service.comosData.xmlPreparation.utilServices.IConverter;
//import com.bjit.common.rest.app.service.comosData.xmlPreparation.utilServices.IFileWriter;
//import com.bjit.common.rest.app.service.comosData.xmlPreparation.utilServices.IStructurePreparation;
//import com.bjit.common.rest.app.service.comosData.xmlPreparation.utilServices.IFileReader;
//import com.bjit.common.rest.app.service.comosData.xmlPreparation.model.comosxml.*;
//import com.bjit.common.rest.app.service.comosData.xmlPreparation.consumer.EquipmentServiceConsumer;
//import com.bjit.common.rest.app.service.comosData.xmlPreparation.model.EquipmentRequestData;
//import javax.validation.Valid;
//import javax.xml.bind.JAXBException;
//import java.io.IOException;
//import java.util.HashMap;
//import java.util.List;
//
////@RestController
////@RequestMapping("/comos/v1/newImplementation")
//public class ComosNewImplementationControllerPrevImpl {
//    @Autowired
//    @Qualifier("ComosFileReader")
//    IFileReader fileReader;
//
//    @Autowired
//    Environment env;
//
//    @Autowired
//    @Qualifier("ComosConverter")
//    IConverter<RFLP> converter;
//
//    @Autowired
//    @Qualifier("ComosFileWriter")
//    IFileWriter fileWriter;
//
//    @Autowired
//    BeanFactory beanFactory;
//
//    @Autowired
//    EquipmentServiceConsumer ntlmAuthenticator;
//
//    @PostMapping("/export/plant/xml")
//    public String createJAXBPlantModel(@Valid @RequestBody EquipmentRequestData requestData) throws IOException {
//
//        IStructurePreparation<HashMap<String, RFLP>> structurePreparation = BeanFactoryAnnotationUtils.qualifiedBeanOfType(beanFactory, IStructurePreparation.class, "ComosStructurePreparation");
//
//        String fileDirectory = env.getProperty("comos.service.response.file.directory");
//        String extension = env.getProperty("comos.response.file.extension");
//
//        List<String> fileList = fileReader.getFileListFromDirectoryWithSpecificExtension(fileDirectory, extension);
////        String jsonData = fileReader.readFile(fileList.get(0));
//
//        String jsonData = ntlmAuthenticator.getComosData(requestData);
//        HashMap<String, RFLP> stringRFLPHashMap = structurePreparation.prepareStructure(jsonData);
//        stringRFLPHashMap.forEach((String filename, RFLP value) -> {
//            try {
//                String xmlData = converter.serializeData(value);
//
//                String destinationDirectory = env.getProperty("comos.generated.file.directory");
//
//                fileWriter.writeFile(destinationDirectory, filename, xmlData);
//            } catch (JAXBException | IOException e) {
//                e.printStackTrace();
//            }
//        });
//
//        return "Done successfully";
//    }
//}
