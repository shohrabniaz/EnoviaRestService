package com.bjit.common.rest.app.service.controller;

import com.bjit.common.rest.app.service.utilities.NullOrEmptyChecker;
import com.bjit.common.rest.item_bom_import.bom_import.MANItemBOMImport;
import com.bjit.common.rest.pdm_enovia.mapper.ItemMapper;
import com.bjit.ewc18x.utils.PropertyReader;
import com.bjit.mapper.mapproject.expand.ObjectTypesAndRelations;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import org.apache.log4j.Appender;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
//import org.apache.logging.log4j.Logger;
//import org.apache.logging.log4j.LogManager;

@Controller
public class IndexController {

    private static final org.apache.log4j.Logger INDEX_CONTROLLER_LOGGER = org.apache.log4j.Logger.getLogger(IndexController.class);
    //private static final Logger INDEX_CONTROLLER_LOGGER = LogManager.getLogger(IndexController.class);

    @GetMapping("/")
    public String index(Map<String, Object> model) {
        try {
            INDEX_CONTROLLER_LOGGER.info("Loading environment values");
            PropertyReader.loadEnvironment();
            INDEX_CONTROLLER_LOGGER.info("Environment values loaded");
            String environmentName = PropertyReader.getEnvironmentName();
            String builtVersion = PropertyReader.getProperty("enovia.webservice.release.build");
            model.put("environmentName", NullOrEmptyChecker.capitalizeFirstLetter(environmentName));
            model.put("builtVersion", Optional.ofNullable(builtVersion).orElse(""));
            return "welcome";
        } catch (Exception exp) {
            INDEX_CONTROLLER_LOGGER.error(exp.getMessage());
            throw exp;
        }
    }

    @GetMapping("/error")
    public String error(HttpServletRequest request, Map<String, Object> model) {
        String errorStatus = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE).toString();
        String errorMessage = request.getAttribute(RequestDispatcher.ERROR_MESSAGE).toString();
        String errorRequestURI = request.getAttribute(RequestDispatcher.INCLUDE_SERVLET_PATH).toString();
        String error = request.getAttribute(RequestDispatcher.ERROR_EXCEPTION).toString();

        INDEX_CONTROLLER_LOGGER.debug("Error : " + error);
        INDEX_CONTROLLER_LOGGER.debug("Error Request URI : " + errorRequestURI);
        INDEX_CONTROLLER_LOGGER.debug("Error Message : " + errorMessage);
        INDEX_CONTROLLER_LOGGER.debug("Error Status : " + errorStatus);

        model.put("status", errorStatus);
        model.put("message", errorMessage);
        model.put("path", errorRequestURI);
        model.put("error", error);

        return "error";
    }

    @GetMapping("/reloadCache")
    public String reloadCache(HttpServletRequest request, Map<String, Object> model) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
        ItemMapper.valMap.clear();
        MANItemBOMImport.bomRelationship = null;
        ObjectTypesAndRelations.RnPmaps.clear();
        ObjectTypesAndRelations.itemImportMapper = null;
        return "reloadCache";
    }

    @GetMapping("/testing")
    public String testing(HttpServletRequest request) throws Exception {
        throw new Exception("Testing Phase");
    }

    @GetMapping("/logs/serviceLogs")
    public String serviceLogs(HttpServletRequest request, Map<String, Object> model) {

        String contextPath = request.getRequestURL().toString();

        Enumeration e = Logger.getRootLogger().getAllAppenders();
        List<LogFileModel> logFileList = new ArrayList<>();
        while (e.hasMoreElements()) {
            Appender fileAppender = (Appender) e.nextElement();
            if (fileAppender instanceof FileAppender) {
                LogFileModel logfile = new LogFileModel();

                String logFilePath = ((FileAppender) fileAppender).getFile().replace("\\", "/");

                logfile.setFilePath(contextPath + "/file?filePath=" + logFilePath);

                String[] logFilePathSplited = logFilePath.split("/");
                String logFileName = logFilePathSplited[logFilePathSplited.length - 1];
                logfile.setFileName(logFileName);

                logFileList.add(logfile);
            }
        }
        model.put("logFileItemList", logFileList);

        return "serviceLogs";
    }

    @GetMapping("/logs/serviceLogs/file")
    public String serviceLogsFileView(HttpServletRequest request, @RequestParam String filePath, Map<String, Object> model) throws IOException {

        String data = new String(Files.readAllBytes(Paths.get(filePath)));
//        BufferedReader reader;
//        String data = "";
//        try {
//            reader = new BufferedReader(new FileReader(filePath));
//            data = reader.readLine();
//            while (data != null) {
//                System.out.println(data);
//                // read next line
//                data = reader.readLine() + "\n";
//                
//            }
//            reader.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        String[] split = filePath.split("\\.");
        String type = split[split.length - 1];

        String logField = null;
        if (type.equalsIgnoreCase("log")) {
            data = data.replaceAll("(\r\n|\n)", "<br />");
            logField = "logTxtData";
        } else {
            logField = "logHtmlData";
        }

        model.put(logField, data);

        return "serviceLogsFromFile";
    }

    class LogFileModel {

        public String fileName;
        public String filePath;

        public String getFileName() {
            return fileName;
        }

        public void setFileName(String fileName) {
            this.fileName = fileName;
        }

        public String getFilePath() {
            return filePath;
        }

        public void setFilePath(String filePath) {
            this.filePath = filePath;
        }
    }
}
