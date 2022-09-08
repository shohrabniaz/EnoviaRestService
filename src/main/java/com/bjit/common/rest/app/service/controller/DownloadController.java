/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.app.service.controller;

import com.bjit.common.rest.app.service.background.rnp.RnPResponseHandler;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author BJIT
 */
@RestController
@RequestMapping(path = "/download")
public class DownloadController {

    private static final org.apache.log4j.Logger DOWNLOAD_CONTROLLER_LOGGER = org.apache.log4j.Logger.getLogger(DownloadController.class);

    @Lazy
    @Autowired
    RnPResponseHandler rnpResponseHandler;

    @GetMapping("/rnp/{reportName}")
    public Object downloadRnPReport(HttpServletResponse response, @PathVariable String reportName) throws Exception {
        try {
            return rnpResponseHandler.getDownloadableReport(reportName, response);
        } catch (Exception exp) {
            DOWNLOAD_CONTROLLER_LOGGER.error(exp);
            throw exp;
        }
    }

    @GetMapping("/hiemlli/{reportName}")
    public Object downloadHimelliReport(HttpServletResponse response, @PathVariable String reportName) throws Exception {
        try {
            return rnpResponseHandler.getDownloadableReport(reportName, response);
        } catch (Exception exp) {
            DOWNLOAD_CONTROLLER_LOGGER.error(exp);
            throw exp;
        }
    }

    /*@RequestMapping(value = "downloadAsABinaryStream", method = RequestMethod.GET)
    @ResponseBody
    public StreamingResponseBody getSteamingFile(@RequestBody final String filePath, HttpServletResponse response) throws IOException {
        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment; filename=\"ErrorCodeSearchResult.txt\"");
        InputStream inputStream = new FileInputStream(new File("C:\\Users\\BJIT\\Desktop\\test-files\\ErrorCodeSearchResult.txt"));
        return outputStream -> {
            int nRead;
            byte[] data = new byte[1024];
            while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
                outputStream.write(data, 0, nRead);
            }
        };
    }*/
//    @RequestMapping(value = "download", method = RequestMethod.POST)
//    @ResponseBody
//    public byte[] getAsFile(@RequestBody final String filePath, HttpServletResponse response) throws IOException {
//        DOWNLOAD_CONTROLLER_LOGGER.info("Download A Single File");
//        try {
//            File file = new File(filePath);
//            String probeContentType = Files.probeContentType(file.toPath());
//            String fileName = file.getName();
//
//            DOWNLOAD_CONTROLLER_LOGGER.info("File Name : " + fileName);
//            DOWNLOAD_CONTROLLER_LOGGER.info("File Path : " + filePath);
//
//            String applicationType;
//            if (probeContentType == null || probeContentType.equalsIgnoreCase("")) {
//                applicationType = "application/octet-stream";
//            } else {
//                applicationType = probeContentType;
//            }
//
//            DOWNLOAD_CONTROLLER_LOGGER.info("File Type : " + applicationType);
//
//            response.setContentType(applicationType);
//            response.setHeader("Content-Disposition", "attachment; filename=\"" + file.getName() + "\"");
//
//            InputStream inputStream = new FileInputStream(file);
//            return IOUtils.toByteArray(inputStream);
//        } catch (Exception exp) {
//            DOWNLOAD_CONTROLLER_LOGGER.error("Error Occurred : " + exp.getMessage());
//            throw exp;
//        }
//    }
//
//    @RequestMapping(value = "download-list-of-files", method = RequestMethod.POST)
//    @ResponseBody
//    public byte[] getCheckoutASZipFile(@RequestBody final List<String> directoryOrFileList, HttpServletResponse response) throws IOException {
//        DOWNLOAD_CONTROLLER_LOGGER.info("Download A Group of Files");
//        try {
//            response.setContentType("application/octet-stream");
//            response.setHeader("Content-Disposition", "attachment; filename=\"Group-Of-Files.zip\"");
//
//            byte[] toByteArray;
//            try (ByteArrayOutputStream zipAsStream = ZipAsStream.zipAsStreams(directoryOrFileList)) {
//                toByteArray = zipAsStream.toByteArray();
//                return toByteArray;
//            } catch (Exception exp) {
//                DOWNLOAD_CONTROLLER_LOGGER.error(exp.getMessage());
//                throw exp;
//            }
//        } catch (Exception exp) {
//            DOWNLOAD_CONTROLLER_LOGGER.error(exp.getMessage());
//            throw exp;
//        }
//    }
    /*@RequestMapping(value = "seeListType", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public List<String> getFileAsList(){
        List<String> fileList = new ArrayList<>();
        fileList.add("C:\\Users\\BJIT\\Desktop\\test-files\\test-file.css");
        fileList.add("C:\\Users\\BJIT\\Desktop\\test-files\\test-file.swf");
        fileList.add("C:\\Users\\BJIT\\Desktop\\test-files\\test-file.bmp");
        fileList.add("C:\\Users\\BJIT\\Desktop\\test-files\\test-file.bin");
        fileList.add("C:\\Users\\BJIT\\Desktop\\test-files\\test-file.csv");
        fileList.add("C:\\Users\\BJIT\\Desktop\\test-files\\test-file.oga");
        fileList.add("C:\\Users\\BJIT\\Desktop\\test-files\\test-file.eot");
        fileList.add("C:\\Users\\BJIT\\Desktop\\test-files\\test-file.mpkg");
        fileList.add("C:\\Users\\BJIT\\Desktop\\test-files\\test-file.xul");
        fileList.add("C:\\Users\\BJIT\\Desktop\\test-files\\test-file.tif");
        fileList.add("C:\\Users\\BJIT\\Desktop\\test-files\\test-file.midi");
        fileList.add("C:\\Users\\BJIT\\Desktop\\test-files\\test-file.ico");
        fileList.add("C:\\Users\\BJIT\\Desktop\\test-files\\test-file.xml");
        fileList.add("C:\\Users\\BJIT\\Desktop\\test-files\\test-file.ics");
        fileList.add("C:\\Users\\BJIT\\Desktop\\test-files\\test-file.html");
        fileList.add("C:\\Users\\BJIT\\Desktop\\test-files\\test-file.jar");
        fileList.add("C:\\Users\\BJIT\\Desktop\\test-files\\test-file.3g2");
        fileList.add("C:\\Users\\BJIT\\Desktop\\test-files\\test-file.ogv");
        fileList.add("C:\\Users\\BJIT\\Desktop\\test-files\\test-file.otf");
        fileList.add("C:\\Users\\BJIT\\Desktop\\test-files\\test-file.zip");
        fileList.add("C:\\Users\\BJIT\\Desktop\\test-files\\test-file.ogx");
        fileList.add("C:\\Users\\BJIT\\Desktop\\test-files\\test-file.rar");
        fileList.add("C:\\Users\\BJIT\\Desktop\\test-files\\test-file.7z");
        fileList.add("C:\\Users\\BJIT\\Desktop\\test-files\\test-file.tar");
        fileList.add("C:\\Users\\BJIT\\Desktop\\test-files\\test-file.png");
        fileList.add("C:\\Users\\BJIT\\Desktop\\test-files\\test-file.webp");
        fileList.add("C:\\Users\\BJIT\\Desktop\\test-files\\test-file.es");
        fileList.add("C:\\Users\\BJIT\\Desktop\\test-files\\test-file.webm");
        fileList.add("C:\\Users\\BJIT\\Desktop\\test-files\\test-file.woff");
        fileList.add("C:\\Users\\BJIT\\Desktop\\test-files\\test-file.pptx");
        fileList.add("C:\\Users\\BJIT\\Desktop\\test-files\\test-file.mpeg");
        fileList.add("C:\\Users\\BJIT\\Desktop\\test-files\\test-file.doc");
        fileList.add("C:\\Users\\BJIT\\Desktop\\test-files\\test-file.odp");
        fileList.add("C:\\Users\\BJIT\\Desktop\\test-files\\test-file.weba");
        fileList.add("C:\\Users\\BJIT\\Desktop\\test-files\\test-file.odt");
        fileList.add("C:\\Users\\BJIT\\Desktop\\test-files\\test-file.ods");
        fileList.add("C:\\Users\\BJIT\\Desktop\\test-files\\test-file.aac");
        fileList.add("C:\\Users\\BJIT\\Desktop\\test-files\\test-file.tiff");
        fileList.add("C:\\Users\\BJIT\\Desktop\\test-files\\test-file.gif");
        fileList.add("C:\\Users\\BJIT\\Desktop\\test-files\\test-file.vsd");
        fileList.add("C:\\Users\\BJIT\\Desktop\\test-files\\test-file.js");
        fileList.add("C:\\Users\\BJIT\\Desktop\\test-files\\test-file.mid");
        fileList.add("C:\\Users\\BJIT\\Desktop\\test-files\\test-file.arc");
        fileList.add("C:\\Users\\BJIT\\Desktop\\test-files\\test-file.avi");
        fileList.add("C:\\Users\\BJIT\\Desktop\\test-files\\test-file.sh");
        fileList.add("C:\\Users\\BJIT\\Desktop\\test-files\\test-file.epub");
        fileList.add("C:\\Users\\BJIT\\Desktop\\test-files\\test-file.bz");
        fileList.add("C:\\Users\\BJIT\\Desktop\\test-files\\test-file.jpeg");
        fileList.add("C:\\Users\\BJIT\\Desktop\\test-files\\test-file.json");
        fileList.add("C:\\Users\\BJIT\\Desktop\\test-files\\test-file.woff2");
        fileList.add("C:\\Users\\BJIT\\Desktop\\test-files\\test-file.bz2");
        fileList.add("C:\\Users\\BJIT\\Desktop\\test-files\\test-file.3gp");
        fileList.add("C:\\Users\\BJIT\\Desktop\\test-files\\test-file.azw");
        fileList.add("C:\\Users\\BJIT\\Desktop\\test-files\\test-file.htm");
        fileList.add("C:\\Users\\BJIT\\Desktop\\test-files\\test-file.jpg");
        fileList.add("C:\\Users\\BJIT\\Desktop\\test-files\\test-file.xlsx");
        fileList.add("C:\\Users\\BJIT\\Desktop\\test-files\\test-file.rtf");
        fileList.add("C:\\Users\\BJIT\\Desktop\\test-files\\test-file.svg");
        fileList.add("C:\\Users\\BJIT\\Desktop\\test-files\\test-file.ttf");
        fileList.add("C:\\Users\\BJIT\\Desktop\\test-files\\test-file.wav");
        fileList.add("C:\\Users\\BJIT\\Desktop\\test-files\\test-file.docx");
        fileList.add("C:\\Users\\BJIT\\Desktop\\test-files\\test-file.xhtml");
        fileList.add("C:\\Users\\BJIT\\Desktop\\test-files\\test-file.txt");
        fileList.add("C:\\Users\\BJIT\\Desktop\\test-files\\test-file.pdf");
        fileList.add("C:\\Users\\BJIT\\Desktop\\test-files\\test-file.ppt");
        fileList.add("C:\\Users\\BJIT\\Desktop\\test-files\\test-file.abw");
        fileList.add("C:\\Users\\BJIT\\Desktop\\test-files\\test-file.csh");
        fileList.add("C:\\Users\\BJIT\\Desktop\\test-files\\test-file.xls");
        fileList.add("C:\\Users\\BJIT\\Desktop\\test-files\\test-file.ts");
        return fileList;
    }*/
    /**
     *
     * @return @throws IOException
     */
    /*@GetMapping(value = "/downloadAsZip", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public @ResponseBody
    byte[] getStreamASFile(HttpServletResponse response) throws IOException {
        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment; filename=\"Checkout-Files.zip\"");
        List<String> directoryOrFileList = new ArrayList<>();
        directoryOrFileList.add("C:\\Users\\BJIT\\Desktop\\test-files\\ActionTasks");
        directoryOrFileList.add("C:\\Users\\BJIT\\Desktop\\test-files\\antlr");
        directoryOrFileList.add("C:\\Users\\BJIT\\Desktop\\test-files\\API");*/
 /*directoryOrFileList.add("C:\\Users\\BJIT\\Desktop\\test-files\\createWithAttributesAndIds.txt");
        directoryOrFileList.add("C:\\Users\\BJIT\\Desktop\\test-files\\ErrorCodeSearchResult.txt");
        directoryOrFileList.add("C:\\Users\\BJIT\\Desktop\\test-files\\Pro Spring 5, 5th Edition.pdf");
        directoryOrFileList.add("C:\\Users\\BJIT\\Desktop\\test-files\\test.txt");*/

 /*ByteArrayOutputStream zipAsStream = ZipAsStream.zipAsStreams(directoryOrFileList);
        byte[] toByteArray = zipAsStream.toByteArray();
        zipAsStream.close();
        return toByteArray;
    }*/
}
