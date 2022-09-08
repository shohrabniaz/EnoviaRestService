/**
 *
 */
package com.bjit.mapper.mapproject.util;

import com.bjit.ewc18x.utils.PropertyReader;
import java.io.File;

import org.apache.log4j.Logger;

import com.bjit.mapper.mapproject.report_mapping_model.ReportMappingData;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.function.BiConsumer;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.io.FilenameUtils;

/**
 * @author TAREQ SEFATI
 *
 */
public class CommonUtil {

    private static final Logger Log = Logger.getLogger(CommonUtil.class);

    public static String createOutputDirectory() {
        String userHome = PropertyReader.getProperty("rnp.report.file.generation.location");
        //String outputFilePath = PropertyReader.getProperty("ebom.reports.folder.path");
        String outputFilePath = userHome + File.separator + "generated_reports" + File.separator;
        File files = new File(outputFilePath);
        if (!files.exists()) {
            if (files.mkdirs()) {
                Log.debug("Directories are created.");
                return outputFilePath;
            } else {
                Log.debug("Failed to create directories.");
                return "";
            }
        } else {
            Log.debug("Directories already created.");
            return outputFilePath;
        }
    }

    public static String selectTemplateFile(String lang) {
        String templateFilePathWithName = "";
        ReportMappingData reportMappingData = new ReportMappingData();
        //String templateLanguage = reportMappingData.getTemplateLang();
        String templateLanguage = lang;
        if (templateLanguage.equalsIgnoreCase(Constants.ENGLISH)) {
            //templateFilePathWithName  = new java.io.File(".").getCanonicalPath().concat("\\jasperTemplate\\").concat("BOM-table2.0_english.jasper");
            templateFilePathWithName = PropertyReader.getProperty("template.file.directory.english");
            Log.debug("Template file: " + templateFilePathWithName);
        } else if (templateLanguage.equalsIgnoreCase(Constants.FINNISH)) {
            //templateFilePathWithName = new java.io.File(".").getCanonicalPath().concat("\\jasperTemplate\\").concat("BOM-table2.0_finnish.jasper");
            templateFilePathWithName = PropertyReader.getProperty("template.file.directory.finnish");
            Log.debug("Template file: " + templateFilePathWithName);
        } else {
            //templateFilePathWithName = new java.io.File(".").getCanonicalPath().concat("\\jasperTemplate\\").concat("BOM-table2.0_english.jasper");
            templateFilePathWithName = PropertyReader.getProperty("template.file.directory.english");
            Log.debug("No suitable template file found. Using default file: " + templateFilePathWithName);
        }
        return templateFilePathWithName;
    }

    public static String getFileNameSuffix() {
        return String.valueOf(System.currentTimeMillis());
    }

    public static String getFileNameSuffix(String type, String name) {
        return type + "_" + name + "_" + getFileNameSuffix();
    }

    public static String getFileNameWithReportId(String type, String name, String reportId) {
        return type + "_" + name + "_" + reportId;
    }

    public static String getFileNameFromPath(String str) {
        str = FilenameUtils.getName(str);
        str = str.substring(0, str.lastIndexOf('.'));
        return str;
    }

    public static String generateOutputFileName(String directory, String fileNameSuffix) {
        return directory + fileNameSuffix;
    }

    public static String getJsonStringForReport(String allData) {
        String data = "";
        String[] effectiveData = allData.split(":", 2);
        // System.out.println(effectiveData[1]);
        data = effectiveData[1].substring(0, effectiveData[1].length() - 1);
        //System.out.println("JSON Data String:\n" + data);
        return data;
    }

    public static String generateReportDownloadLinks(HttpServletRequest request, String reportId, String format) {
        String link = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort()
                + request.getContextPath() + "/export/download?requestId=" + reportId + "&format=" + format;
        Log.debug("Report download link: " + link);
        return link;
    }

    public static String getResultsFromBomDataResponse(String responseString) {
        JsonElement jelement = new JsonParser().parse(responseString);
        JsonObject jObject = jelement.getAsJsonObject();
        jObject = jObject.getAsJsonObject("data");
        JsonArray jsonArray = jObject.getAsJsonArray("results");
        String resultString = jsonArray.toString();
        jsonArray = null;
        jObject = null;
        jelement = null;
        return resultString;
    }

    public static HashMap getRootInfoMapFromBomDataResponse(String responseString) {
        Gson gson = new Gson();
        HashMap rootItemParams = new HashMap();
        JsonElement jelement = new JsonParser().parse(responseString);
        JsonObject jObject = jelement.getAsJsonObject();
        jObject = jObject.getAsJsonObject("data");
        jObject = jObject.getAsJsonObject("rootItemInfo");
        rootItemParams = (HashMap) gson.fromJson(jObject.toString(), rootItemParams.getClass());
        jObject = null;
        jelement = null;
        gson = null;
        return rootItemParams;
    }

    public static HashMap getInfoMapFromBomDataResponse(String responseString, String paramName) {
        Gson gson = new Gson();
        HashMap rootItemParams = new HashMap();
        JsonElement jelement = new JsonParser().parse(responseString);
        JsonObject jObject = jelement.getAsJsonObject();
        jObject = jObject.getAsJsonObject("data");
        jObject = jObject.getAsJsonObject(paramName);
        rootItemParams = (HashMap) gson.fromJson(jObject.toString(), rootItemParams.getClass());
        jObject = null;
        jelement = null;
        gson = null;
        return rootItemParams;
    }

    /*
    * Added in order to enable exception throwing
    * from lambda foreach block.
    **/
    @FunctionalInterface
    public interface ThrowingConsumer<K, V> extends BiConsumer<String, String> {

        void acceptThrows(String key, String value) throws Exception;

        @Override
        default void accept(final String key, final String value) {
            try {
                acceptThrows(key, value);
            } catch (final Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * This methods read text file and returns all texts
     *
     * @param file
     * @return String
     * @throws java.io.IOException
     */
    public static String getStringFromTextFile(File file) throws IOException {
        StringBuilder sb = new StringBuilder();
        try (InputStream in = new FileInputStream(file); BufferedReader br = new BufferedReader(new InputStreamReader(in))) {

            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line).append(System.lineSeparator());
            }
        }

        return sb.toString();
    }
}
