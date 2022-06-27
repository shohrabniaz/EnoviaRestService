package com.bjit.common.rest.app.service.service.document;


import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.bjit.common.rest.app.service.exception.FileStorageException;
import com.bjit.common.rest.app.service.exception.MyFileNotFoundException;
import com.bjit.common.rest.app.service.property.document.FileStorageProperties;

//@Service
public class FileStorageService {
//    private final Path fileStorageLocation;
//    
//    @Autowired
//    FileStorageProperties fileStorageProperties;
//    
//    @Autowired
//    public FileStorageService(FileStorageProperties fileStorageProperties) throws Exception {
//    	
//        this.fileStorageLocation = Paths.get(fileStorageProperties.getUploadDir())
//                .toAbsolutePath().normalize();
//
//        try {
//            Files.createDirectories(this.fileStorageLocation);
//        } catch (Exception ex) {
//            throw new FileStorageException("Could not create the directory where the uploaded files will be stored.", ex);
//        }
//    }
//
//    public String storeFile(MultipartFile file) throws Exception {
//        // Normalize file name
//        String fileName = StringUtils.cleanPath(file.getOriginalFilename());
//
//        try {
//            // Check if the file's name contains invalid characters
//            if(fileName.contains("..")) {
//                throw new FileStorageException("Sorry! Filename contains invalid path sequence " + fileName);
//            	//return null;
//            }
//
//            // Copy file to the target location (Replacing existing file with the same name)
//            Path targetLocation = this.fileStorageLocation.resolve(fileName);
//            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
//
//            return fileName;
//        } catch (IOException ex) {
//            throw new FileStorageException("Could not store file " + fileName + ". Please try again!", ex);
//        }
//    }
//
//    public Resource loadFileAsResource(String fileName) throws Exception {
//        try {
//            Path filePath = this.fileStorageLocation.resolve(fileName).normalize();
//            Resource resource = new UrlResource(filePath.toUri());
//            if(resource.exists()) {
//                return resource;
//            } else {
//                throw new MyFileNotFoundException("File not found " + fileName);
//            	//return null;
//            }
//        } catch (MalformedURLException ex) {
//            throw new Exception("File not found " + fileName, ex);
//        }
//    }
//    public List<String> populateFileList() {
//    	System.out.println("-----Folder------"+fileStorageProperties.getUploadDir());
//        List <String> listOfFileName = new ArrayList<>();
//        File folder = new File(fileStorageProperties.getUploadDir());
//        File[] listOfFiles = folder.listFiles();
//
//        for (int i = 0; i < listOfFiles.length; i++) {
//          if (listOfFiles[i].isFile()) {
//            System.out.println("File " + listOfFiles[i].getName());
//            listOfFileName.add(listOfFiles[i].getName());
//          }
//        }
//        return listOfFileName;
//    }
//    
//    public  String getRootFolder(){
//    	   return  fileStorageProperties.getUploadDir();
//    }
}
