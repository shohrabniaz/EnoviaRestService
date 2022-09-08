/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.app.service.controller.valcon;

/**
 *
 * @author Tomal
 */
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.bjit.common.rest.app.service.model.upload_download.DownloadModel;
import com.bjit.common.rest.app.service.model.upload_download.Product;
import com.bjit.common.rest.app.service.service.document.FileStorageService;

//@RestController
public class JSONService {
//     @Autowired
//	    private FileStorageService fileStorageService;
//	    
//    @GetMapping("/jason/product")
//    @Produces("application/json")
//    public DownloadModel getProductInJSON() {
//        System.out.println("GET CALL");
//        DownloadModel dM = new DownloadModel();
//        dM.setFileName("tomal");
//        dM.setLocation("c:lsdjflasdjfklj");
//        return dM;
//
//    }
//
//    @PostMapping("/jason/product/post")
//    @Consumes("application/json")
//    public Response createProductInJSON(@RequestBody Product product) {
//
//        String result = "Product created : " + product;
//        System.out.println(product.getName() + " Quantity:" + product.getQty());
//        return Response.status(201).entity(result).build();
//
//    }
//
//    @PostMapping("/downloadFileFromUIModelThroughClient")
//    @Consumes("application/json")
//    public String downloadByModel(@RequestBody DownloadModel dM) throws FileNotFoundException, IOException {
//        if (dM == null) {
//            return "NULL";
//        }
//        String fileName = dM.getFileName();
//        String location = dM.getLocation();
//        System.out.println("FILE NAME:" + fileName + "\n LOCATION:" + location);
//        //Resource resource = fileStorageService.loadFileAsResource(fileName);
//        InputStream initialStream = new FileInputStream(
//                new File(fileStorageService.getRootFolder() + fileName));
//        File targetFile = new File(location + fileName);
//        OutputStream outStream = new FileOutputStream(targetFile);
//
//        byte[] buffer = new byte[8 * 1024];
//        int bytesRead;
//        while ((bytesRead = initialStream.read(buffer)) != -1) {
//            outStream.write(buffer, 0, bytesRead);
//        }
//        IOUtils.closeQuietly(initialStream);
//        IOUtils.closeQuietly(outStream);
//        return "DONE";
//    }

}
