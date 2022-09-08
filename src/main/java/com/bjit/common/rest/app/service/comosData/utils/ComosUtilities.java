package com.bjit.common.rest.app.service.comosData.utils;

import com.bjit.common.rest.app.service.comosData.xmlPreparation.model.comosxml.RFLP;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.model.equipment.EquipmentServiceResponse;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ComosUtilities {
    @Autowired
    Environment env;

    public List<String> getRespondedFiles(String directory, String extension) throws IOException {
        List<String> fileList = Files.walk(Paths.get(directory))
                .filter(Files::isRegularFile)
                .filter((Path filePath) -> filePath.toString().endsWith(extension))
                .map(Path::toString)
                .collect(Collectors.toList());
        return fileList;
    }

    public EquipmentServiceResponse readFile(String filename) throws FileNotFoundException {
        BufferedReader bufferedReader = new BufferedReader(new FileReader(filename));

        Gson gson = new Gson();
        EquipmentServiceResponse serviceResponse = gson.fromJson(bufferedReader, EquipmentServiceResponse.class);

        return serviceResponse;
    }

    public void writeXMLFile(HashMap<String, RFLP> rflpMap) {
        rflpMap.forEach((String key, RFLP value) -> {

            JAXBContext jaxbContext = null;
            try {
                jaxbContext = JAXBContext.newInstance(RFLP.class);
                Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
                StringWriter sw = new StringWriter();
                jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
                jaxbMarshaller.marshal(value, sw);
                String xmlString = sw.toString();

                String fileName = new SimpleDateFormat("yyyyMMddHHmm'" + env.getProperty("comos.generated.file.extension") + "'").format(new Date());

                try (PrintWriter out = new PrintWriter(env.getProperty("comos.generated.file.directory") + key + "_" + fileName)) {
                    out.println(xmlString);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            } catch (JAXBException e) {
                e.printStackTrace();
            }
        });
    }
}
