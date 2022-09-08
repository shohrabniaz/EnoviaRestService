package com.bjit.common.rest.app.service.controller.valcon;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.StreamingOutput;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.bjit.common.rest.app.service.exception.MyFileNotFoundException;
import com.bjit.common.rest.app.service.model.upload_download.DownloadModel;
import com.bjit.common.rest.app.service.payload.upload_download.UploadFileResponse;



//@RestController
public class FileController {
//
//	   private static final Logger logger = LoggerFactory.getLogger(FileController.class);
//
//	    @Autowired
//	    private com.bjit.common.rest.app.service.service.document.FileStorageService fileStorageService;
//	    
//
//	    @PostMapping("/uploadFile")
//	    public com.bjit.common.rest.app.service.payload.upload_download.UploadFileResponse uploadFile(@RequestParam("file") MultipartFile file)  {
//	        String fileName = null;
//	        String fileDownloadUri = null;
//			try {
//				fileName = fileStorageService.storeFile(file);
//				fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
//		                .path("/downloadFile/")
//		                .path(fileName)
//		                .toUriString();
//			} catch (Exception e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//
//	         
//
//	        return new UploadFileResponse(fileName, fileDownloadUri,
//	                file.getContentType(), file.getSize());
//	    }
//
//	    @PostMapping("/uploadMultipleFiles")
//	    public List<UploadFileResponse> uploadMultipleFiles(@RequestParam("files") MultipartFile[] files) {
//            System.out.println("UPLOAD CONTROLLER");
//	        return Arrays.asList(files)
//	                .stream()
//	                .map(file -> uploadFile(file))
//	                .collect(Collectors.toList());
//	    }
//        
//        @GetMapping("/downloadFileFromUI") 
//        public ResponseEntity downloadFileFromUI ( 
//                @RequestParam("fileName") String fileName,
//                @RequestParam("location") String location,  HttpServletRequest request) throws Exception {
//            System.out.println("FILE NAME ajax:"+fileName+ "\n LOCATION ajax:"+location);
//            //Resource resource = fileStorageService.loadFileAsResource(fileName);
//            InputStream initialStream = new FileInputStream(
//            new File(fileStorageService.getRootFolder()+fileName));
//            File targetFile = new File(location+fileName);
//            OutputStream outStream = new FileOutputStream(targetFile);
//
//            byte[] buffer = new byte[8 * 1024];
//            int bytesRead;
//            while ((bytesRead = initialStream.read(buffer)) != -1) {
//                outStream.write(buffer, 0, bytesRead);
//            }
//            IOUtils.closeQuietly(initialStream);
//            IOUtils.closeQuietly(outStream);
//            return new ResponseEntity(HttpStatus.OK);
//	    }
//         @GetMapping("/downloadFileThroughClient") 
//        public Response downloadFile() {
//            File file = new File(fileStorageService.getRootFolder()+"Assignment_two.war");
//            if (file.exists()) {
//                System.out.println("FILE EXISTS SIZE:"+file.getAbsolutePath());
//            }
//            ResponseBuilder response = Response.ok((Object) file);
//            response.header("Content-Disposition", "attachment;filename=Assignment_two.war");
//            return response.build();
//        }
//        @GetMapping("/downloadFileFromUIModel") 
//        public ResponseEntity downloadFileFromUI2 (@ModelAttribute("downloadModel") DownloadModel dM, HttpServletRequest request) throws Exception {
//            if (dM==null) {
////                return ;
//            }
//              Resource resource = fileStorageService.loadFileAsResource(dM.getFileName());
//
//        // Try to determine file's content type
//        String contentType = null;
//        try {
//            contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
//        } catch (IOException ex) {
//            logger.info("Could not determine file type.");
//        }
//
//        // Fallback to the default content type if type could not be determined
//        if(contentType == null) {
//            contentType = "application/octet-stream";
//        }
//
//        return ResponseEntity.ok()
//                .contentType(MediaType.parseMediaType(contentType))
//                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
//                .body(resource);
////            String fileName = dM.getFileName();
////            String location = dM.getLocation();
////            System.out.println("FILE NAME:"+fileName+ "\n LOCATION:"+location);
////            //Resource resource = fileStorageService.loadFileAsResource(fileName);
////            InputStream initialStream = new FileInputStream(
////            new File(fileStorageService.getRootFolder()+fileName));
////            File targetFile = new File(location+fileName);
////            OutputStream outStream = new FileOutputStream(targetFile);
////
////            byte[] buffer = new byte[8 * 1024];
////            int bytesRead;
////            while ((bytesRead = initialStream.read(buffer)) != -1) {
////                outStream.write(buffer, 0, bytesRead);
////            }
////            IOUtils.closeQuietly(initialStream);
////            IOUtils.closeQuietly(outStream);
////            return "DONE";
//	    }
//	    @GetMapping("/downloadFile/{fileName:.+}")
//	    public ResponseEntity<Resource> downloadFile(@PathVariable String fileName, HttpServletRequest request) throws Exception {
//	        // Load file as Resource
//	        Resource resource = fileStorageService.loadFileAsResource(fileName);
//            System.out.println("CONTROLLER for file name in URL fileNAME=..");
//	        // Try to determine file's content type
//	        String contentType = null;
//	        try {
//	            contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
//	        } catch (IOException ex) {
//	            logger.info("Could not determine file type.");
//	        }
//
//	        // Fallback to the default content type if type could not be determined
//	        if(contentType == null) {
//	            contentType = "application/octet-stream";
//	        }
////            return new ResponseEntity<>(HttpStatus.OK);
//	        return ResponseEntity.ok()
//	                .contentType(MediaType.parseMediaType(contentType))
//	                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
//	                .body(resource);
//	    }
//	    
//    @GetMapping("/downloadUrlFile/{fileName}")
//    public ResponseEntity downloadURLFileFile( @PathVariable String fileName,HttpServletRequest request) throws Exception {
//    	System.out.println("---------- TEST------------"+fileName);
//    	//decoded = new java.net.URI(url).getPath();
//    	URL url2 = new URL(URLDecoder.decode( fileName, "UTF-8" ) );
//    	System.out.println("---------- TEST------------");
//    	// URL url= new URL(fileName);
//	      // URI uri = new URI(url.getProtocol(), url.getUserInfo(), url.getHost(), url.getPort(), url.getPath(), url.getQuery(), url.getRef());
//	       
//	       
//    	logger.info(" file type.");
//    	File file = new File("E:\\temp\\url.java");
//    	HttpURLConnection httpConnection = (HttpURLConnection) url2.openConnection();
//    	httpConnection.setRequestMethod("HEAD");
//    	long removeFileSize = httpConnection.getContentLengthLong();
//    	FileUtils.copyURLToFile(
//    			url2, 
//    			  file);
//    	
//    	Resource resource = loadFileAsResource(file.getAbsolutePath());
//    	
//    	 String contentType = null;
//	        try {
//	            contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
//	        } catch (IOException ex) {
//	            logger.info("Could not determine file type.");
//	        }
//
//	        // Fallback to the default content type if type could not be determined
//	        if(contentType == null) {
//	            contentType = "application/octet-stream";
//	        }
//
//	        return ResponseEntity.ok()
//	                .contentType(MediaType.parseMediaType(contentType))
//	                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
//	                .body(resource);
//    	
//    
//    }
//    
//    public Resource loadFileAsResource(String fileName) throws Exception {
//        try {
//            Path filePath =Paths.get(fileName)
//	                .toAbsolutePath().normalize();
//            
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
//    
//    
//    @GetMapping("/downloadUrlFile2/{fileName:.+}")
//    public Response downloadURLFileFile2(@PathVariable String fileUrl, HttpServletRequest request) throws Exception {
//    	Client client = ClientBuilder.newClient();
//    	String url = fileUrl;
//    	final InputStream responseStream = client.target(url).request().get(InputStream.class);
//    	System.out.println(responseStream.getClass());
//    	StreamingOutput output = new StreamingOutput() {
//    	    @Override
//    	    public void write(OutputStream out) throws IOException, WebApplicationException {  
//    	        int length;
//    	        byte[] buffer = new byte[1024];
//    	        while((length = responseStream.read(buffer)) != -1) {
//    	            out.write(buffer, 0, length);
//    	        }
//    	        out.flush();
//    	        responseStream.close();
//    	    }   
//    	};
//    	
//    	return Response.ok(output).header(
//    	        "Content-Disposition", "attachment, filename=\"HelloWorld.java\"").build();
//    	
//    
//    }
//

}
