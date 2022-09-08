package com.bjit.common.rest.app.service.comosData.xmlPreparation.utilServiceImpls;

import com.bjit.common.rest.app.service.aspects.annotations.LogExecutionTime;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.model.SessionModel;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.model.comosxml.RFLP;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.utilServices.IComosFileWriter;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.utilServices.IConverter;
import lombok.extern.log4j.Log4j;
import org.apache.commons.lang.NotImplementedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import javax.xml.bind.JAXBException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Log4j
@Service
@Qualifier("ComosFileWriter")
public class ComosFileWriter implements IComosFileWriter {

    @Autowired
    SessionModel sessionModel;

    @Override
    @LogExecutionTime
    public final String getXMLFileDirectory (String data) {
        String millBeginningPoint = "mill-id_";
        String millDEndingPoint = "_eq-id_";
        String millId = getExtractedDataFromString(data, millBeginningPoint, millDEndingPoint);

        System.out.println(millId);

        String equipmentDEndingPoint = "_";
        String equipmentId = getExtractedDataFromString(data, millDEndingPoint, equipmentDEndingPoint);

        System.out.println(equipmentId);

        return new StringBuilder()
                .append(millBeginningPoint)
                .append(millId)
                .append(millDEndingPoint)
                .append(equipmentId)
                .toString();
    }
    @Autowired
    @Qualifier("ComosConverter")
    IConverter<RFLP> converter;
    @Autowired
    Environment env;

    public final String getXMLFilesSeparateDirectory(HashMap<String, RFLP> fileHashMap) {
        return new StringBuilder()
                .append(env.getProperty("comos.generated.file.directory"))
                .append(fileHashMap
                        .keySet()
                        .stream()
                        .map(this::getXMLFileDirectory)
                        .findFirst()
                        .get())
                .append("\\")
                .toString();
    }

    @Override
    @LogExecutionTime
    public void writeFile(String directory, String filename, String jsonString) {

        writeFile(directory, filename, jsonString, "xml");
    }

    @Override
    @LogExecutionTime
    public void writeFile(String directory, String filename, String jsonString, String extension) {

        createDirectory(directory);

        String absoluteDirectory = new StringBuilder()
                .append(directory)
                .append(filename)
                .append(".")
                .append(extension)
                .toString();

        log.info(absoluteDirectory);

        try (PrintWriter out = new PrintWriter(absoluteDirectory)) {
            out.println(jsonString);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    @LogExecutionTime
    public String formatString(String notFormattedString) {
        throw new NotImplementedException("String formatting process is now under development process");
    }

    @Override
    @LogExecutionTime
    public int writeFile(HashMap<String, RFLP> stringRFLPHashMap) {
        return writeFile(stringRFLPHashMap, Boolean.FALSE);
    }

    @Override
    @LogExecutionTime
    public int writeFile(HashMap<String, RFLP> stringRFLPHashMap, Boolean prepareSeparateDirectory) {
        String destinationDirectory = prepareSeparateDirectory
                ? getXMLFilesSeparateDirectory(stringRFLPHashMap)
                : env.getProperty("comos.generated.file.directory");

        stringRFLPHashMap.forEach((String filename, RFLP value) -> {
            try {
                String xmlData = converter.serializeData(value);
                writeFile(destinationDirectory, filename, xmlData);
            } catch (JAXBException | IOException e) {
                e.printStackTrace();
            }
        });

        int size = stringRFLPHashMap.size();
        log.info(size + " xml files has been generated in the shared directory ('" + destinationDirectory + "')");
        return size;
    }

    @Override
    @LogExecutionTime
    public Boolean deleteFile(String absoluteFilePath) {

        File file = new File(absoluteFilePath);
        boolean deleted = file.delete();
        String deleteLog = deleted
                ? "Deleted the file: " + file.getName()
                : "Failed to delete the file: " + file.getName();
        log.info(deleteLog);

        return deleted;
    }

    private String getExtractedDataFromString(String data, String beginningPoint, String endingPoint) {
        String regex = "(.*?)";
        String searchingPattern = new StringBuilder()
                .append(beginningPoint)
                .append(regex)
                .append(endingPoint)
                .toString();

        Pattern pattern = Pattern.compile(searchingPattern);
        Matcher matcher = pattern.matcher(data);
        String extracted = null;
        if (matcher.find()) {
            extracted = matcher.group(1);
        }
        return extracted;
    }
}
