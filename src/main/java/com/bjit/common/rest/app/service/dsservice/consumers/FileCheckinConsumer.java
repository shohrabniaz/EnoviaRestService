package com.bjit.common.rest.app.service.dsservice.consumers;

import okhttp3.Response;
import okhttp3.ResponseBody;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Optional;
import com.bjit.common.rest.app.service.dsservice.serviceclient.Method;
import com.bjit.common.rest.app.service.dsservice.stores.PropertyStore;
import com.bjit.ewc18x.utils.PropertyReader;

public class FileCheckinConsumer extends ConsumerModel<String> {
    private static final org.apache.log4j.Logger LOGGER = org.apache.log4j.Logger.getLogger(FileCheckinConsumer.class);

    private final String fileLocations;
    private String fcsjobTicket;

    public FileCheckinConsumer(String fileLocations) {
        this.fileLocations = fileLocations;
    }

    public FileCheckinConsumer(String fileLocations, String fcsjobTicket) {
        this.fileLocations = fileLocations;
        this.fcsjobTicket = fcsjobTicket;
    }

    @Override
    public String consume() throws Exception {
//        List<File> files = Optional.ofNullable(this.fileLocations).filter(filelist -> !filelist.isEmpty()).orElseGet(() -> new ArrayList<>()).stream().map(filedirectory -> new File(filedirectory)).collect(Collectors.toList());

        Response response = getClientBuilder()
                .setUrl(getURL())
                .setMethod(Method.POST)
                .readFromForm(true)
//                .setFormData(getPropertyStore().getProperties().get("ticket-param-name"), getPropertyStore().getProperties().get("fcs-job-ticket"))
                .setFormData(getPropertyStore().getProperties().get("ticket-param-name"), Optional.ofNullable(this.fcsjobTicket).filter(jobTicket -> !jobTicket.isEmpty()).orElseGet(() -> getPropertyStore().getProperties().get("fcs-job-ticket")))
                .fileUpload("filename", new File(this.fileLocations))
                //                .fileUpload(fileMap)
                //                .fileUpload("filename", files)
                .setHeaders(getPropertyStore().getProperties().get("csrf-field-name"), getPropertyStore().getProperties().get("csrf-filed-value"))
                .setHeaders("Accept", "application/json")
                .setHeaders("Content-Type", "application/json")
                .build();

        ResponseBody body = response.body();
        String checkinTicket = getResponseModel(response, String.class);
        managePropertyStore(checkinTicket);

        return checkinTicket;
    }

    private void managePropertyStore(String checkinTicket) throws MalformedURLException {
        HashMap<String, String> properties = new HashMap<>();
        properties.put("file-checkin-ticket", checkinTicket);

        PropertyStore propertyStore = setProperties(properties);
        setPropertyStore(propertyStore);
    }

    @Override
    protected String getResponseModel(Response response, Class<String> classType) throws IOException {
        manageCookies(response);

        ResponseBody body = response.body();
        Optional.ofNullable(body).orElseThrow(() -> new NullPointerException("Service responded null"));

        String responseData = body.string();
        LOGGER.info(responseData);

        return responseData;
    }

    @Override
    public String getURL() {
//        "https://dsd2v21xfcscentral.plm.valmet.com/enoviafcs/servlet/fcs/checkin"
        String fcsUrl = PropertyReader.getProperty("ds.service.base.url.fcs");
        String checkinUrl = PropertyReader.getProperty("ds.service.url.checkin.file.checkin");
        String fcsCheckinUrl = fcsUrl + checkinUrl;
        LOGGER.info(fcsCheckinUrl);
        return fcsCheckinUrl;
    }
}
