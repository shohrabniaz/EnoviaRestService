package com.bjit.common.rest.app.service.controller.valcon;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.bjit.common.rest.app.service.model.upload_download.UploadModel;
import com.bjit.common.rest.app.service.service.document.FileStorageService;

//@RestController
public class RestUploadController {
//    
//    @Autowired
//    private FileStorageService fileStorageService;
//    
//    private final Logger logger = LoggerFactory.getLogger(RestUploadController.class);
//
//    //Save the uploadeprivated file to this folder
//   
//
//    //Single file upload
//    @PostMapping("/api/upload")
//    // If not @RestController, uncomment this
//    //@ResponseBody
//    public ResponseEntity<?> uploadFile(
//            @RequestParam("file") MultipartFile uploadfile) {
//
//        logger.debug("Single file upload!");
//
//        if (uploadfile.isEmpty()) {
//            return new ResponseEntity("please select a file!", HttpStatus.OK);
//        }
//
//        try {
//
//            saveUploadedFiles(Arrays.asList(uploadfile));
//
//        } catch (IOException e) {
//            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
//        }
//
//        return new ResponseEntity("Successfully uploaded - " +
//                uploadfile.getOriginalFilename(), new HttpHeaders(), HttpStatus.OK);
//
//    }
//
//    // Multiple file upload
//    @PostMapping("/api/upload/multi")
//    public ResponseEntity<?> uploadFileMulti(
//            @RequestParam("extraField") String extraField,
//            @RequestParam("files") MultipartFile[] uploadfiles) {
//
//        logger.debug("Multiple file upload!");
//
//        String uploadedFileName = Arrays.stream(uploadfiles).map(x -> x.getOriginalFilename())
//                .filter(x -> !StringUtils.isEmpty(x)).collect(Collectors.joining(" , "));
//
//        if (StringUtils.isEmpty(uploadedFileName)) {
//            return new ResponseEntity("please select a file!", HttpStatus.OK);
//        }
//
//        try {
//
//            saveUploadedFiles(Arrays.asList(uploadfiles));
//
//        } catch (IOException e) {
//            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
//        }
//
//        return new ResponseEntity("Successfully uploaded - "
//                + uploadedFileName, HttpStatus.OK);
//
//    }
//
//    // maps html form to a Model
//    @PostMapping("/api/upload/multi/model")
//    public ResponseEntity<?> multiUploadFileModel(@ModelAttribute UploadModel model) {
//
//        logger.debug("Multiple file upload! With UploadModel");
//
//        try {
//
//            saveUploadedFiles(Arrays.asList(model.getFiles()));
//
//        } catch (IOException e) {
//            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
//        }
//
//        return new ResponseEntity("Successfully uploaded!", HttpStatus.OK);
//
//    }
//
//    //save file
//    private void saveUploadedFiles(List<MultipartFile> files) throws IOException {
//
//        for (MultipartFile file : files) {
//
//            if (file.isEmpty()) {
//                continue; //next pls
//            }
//
//            byte[] bytes = file.getBytes();
//            Path path = Paths.get(fileStorageService.getRootFolder() + file.getOriginalFilename());
//            Files.write(path, bytes);
//
//        }
//
//    }
}
