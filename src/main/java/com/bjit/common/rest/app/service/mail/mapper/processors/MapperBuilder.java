package com.bjit.common.rest.app.service.mail.mapper.processors;

import com.bjit.common.rest.app.service.mail.rnp.RnPMailConfigPreparation;
import com.bjit.common.rest.app.service.utilities.FileUtilities;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import org.apache.log4j.Logger;

public class MapperBuilder<T> {
    private static final Logger MAPPER_BUILDER_LOGGER = Logger.getLogger(MapperBuilder.class);
    public T getData(String fileLocation, Class<T> targetClass) throws Exception {
        T targetedObject;

        try {
            targetedObject = getObjectFromResource(fileLocation, targetClass);
        } catch (JAXBException | NullPointerException | IOException jaxExp) {
            try {
                targetedObject = getObjectFromDrive(fileLocation, targetClass);
            } catch (Exception exp) {
                MAPPER_BUILDER_LOGGER.error(exp);
                throw exp;
            }
        }
        return targetedObject;
    }

    private Unmarshaller getUnmarshaller(Class<T> targetClass) throws JAXBException {
        JAXBContext jaxbContext = JAXBContext.newInstance(targetClass);
        return jaxbContext.createUnmarshaller();
    }

    protected T getObjectFromResource(String fileLocation, Class<T> targetClass) throws JAXBException, IOException {
        try {
            final String mapData = FileUtilities.getResourceFileData(fileLocation);
            StringReader mappingXmlMapStringReader = new StringReader(mapData);
            Unmarshaller jaxbUnmarshaller = getUnmarshaller(targetClass);
            T object = (T) jaxbUnmarshaller.unmarshal(mappingXmlMapStringReader);
            return object;
        } catch (JAXBException | NullPointerException | IOException exp) {
            MAPPER_BUILDER_LOGGER.error(exp);
            throw exp;
        }
    }

    protected T getObjectFromDrive(String fileLocation, Class<T> targetClass) throws JAXBException {
        try {
            final File file = FileUtilities.getFile(fileLocation);
            Unmarshaller jaxbUnmarshaller = getUnmarshaller(targetClass);
            T object = (T) jaxbUnmarshaller.unmarshal(file);
            return object;
        } catch (JAXBException exp) {
            MAPPER_BUILDER_LOGGER.error(exp);
            throw exp;
        }
    }
}
