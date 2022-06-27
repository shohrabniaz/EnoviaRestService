package com.bjit.common.rest.app.service.comosData.xmlPreparation.batchTool.com.bjit.comos.consumer;

import lombok.extern.log4j.Log4j;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;

@Log4j
@Service
@Qualifier("ServiceConsumer")
public class ServiceConsumer {
    @Autowired
    Environment env;
    OkHttpClient client = new OkHttpClient();

    public void  callbyfilename() throws IOException {
        File directoryPath = new File(env.getProperty("generated.logical.structure.xml.file.directory"));
        File mil_equipment_id_wise_filesList[]=takingFileList(directoryPath);
        for(File file : mil_equipment_id_wise_filesList) {
            String serviceURL = env.getProperty("comos.filename.service.url");
            post( serviceURL,file.getName());
        }
    }

    public void  callByFilename(String filename) throws IOException {
        String serviceURL = env.getProperty("comos.filename.service.url");
        log.info("Service URL : '" + serviceURL + "'");
        post( serviceURL,filename);
    }


    public String post(String serviceURL, String filename) throws IOException {
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, "{\n\t\n\t\"millNEquipmentId\":\""+filename+"\"\n}");

        Request request = new Request.Builder()
                .url(serviceURL)
                .post(body)
                .addHeader("Content-Type", "application/json")
                .build();

        try (Response response = client.newCall(request).execute()) {
            String service_response = response.body().string();
            log.info(service_response);
            return service_response;

        }


    }
    public File [] takingFileList(File directoryPath){
        //List of all file directories
        File mil_equipment_id_wise_filesList[] = directoryPath.listFiles();
        return mil_equipment_id_wise_filesList;

    }

}
