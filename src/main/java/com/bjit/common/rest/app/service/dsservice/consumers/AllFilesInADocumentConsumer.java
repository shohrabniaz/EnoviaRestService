package com.bjit.common.rest.app.service.dsservice.consumers;

import okhttp3.Response;
import okhttp3.ResponseBody;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Optional;
import com.bjit.common.rest.app.service.dsservice.serviceclient.Method;
import com.bjit.common.rest.app.service.utilities.JSON;
import com.bjit.common.rest.app.service.dsservice.models.documentcheckin.documentresponse.DocumentResponseModel;
import com.bjit.ewc18x.utils.PropertyReader;
import java.text.MessageFormat;

public class AllFilesInADocumentConsumer extends ConsumerModel<DocumentResponseModel> {

    private static final org.apache.log4j.Logger LOGGER = org.apache.log4j.Logger.getLogger(AllFilesInADocumentConsumer.class);
    String docId;

    public AllFilesInADocumentConsumer(String docId) {
        this.docId = docId;
    }

    @Override
    public DocumentResponseModel consume() throws Exception {
        Response response = getClientBuilder()
                .setUrl(getURL())
                .setMethod(Method.GET)
                .setHeaders("Accept", "application/json")
                .setHeaders("Content-Type", "application/json")
                .setHeaders(getPropertyStore().getProperties().get("csrf-field-name"), getPropertyStore().getProperties().get("csrf-filed-value"))
                .setHeaders("SecurityContext", getPropertyStore().getProperties().get("securityContext"))
                .build();

        DocumentResponseModel getDocumentResponseModel = getResponseModel(response, DocumentResponseModel.class);
        managePropertyStore(getDocumentResponseModel);

        String success = getDocumentResponseModel.getSuccess();
        if (!Boolean.parseBoolean(success)) {
            throw new RuntimeException(getDocumentResponseModel.getError());
        }

        return getDocumentResponseModel;
    }

    protected void managePropertyStore(DocumentResponseModel DocumentResponseModel) throws MalformedURLException {
//        HashMap<String, String> properties = new HashMap<>();
//        properties.put("ticket-param-name", "");
//        properties.put("fcs-job-ticket", "");
//
//        PropertyStore propertyStore = setProperties(properties);
//        setPropertyStore(propertyStore);
    }

    protected DocumentResponseModel getDocumentResponse(ResponseBody body) throws IOException {
        Optional.ofNullable(body).orElseThrow(() -> new NullPointerException(this.getClass().getCanonicalName() + " " + PropertyReader.getProperty("ds.service.response.null.or.empty")));

        String serviceResponse = body.string();
        Optional.of(serviceResponse).filter(data -> !data.isEmpty()).orElseThrow(() -> new NullPointerException(this.getClass().getCanonicalName() + " " + PropertyReader.getProperty("ds.service.response.null.or.empty")));
        LOGGER.info("Response from " + this.getURL());
        LOGGER.info(serviceResponse);

        JSON jsonParser = new JSON();
        DocumentResponseModel documentResponseModel = jsonParser.deserialize(serviceResponse, DocumentResponseModel.class);

        return documentResponseModel;
    }

    @Override
    public String getURL() {
//        "https://dsd2v21xspace.plm.valmet.com/3dspace/resources/v1/modeler/documents/5D2B6C1200000D8861277B8A000086E6/files"
        String threedUrl = PropertyReader.getProperty("ds.service.base.url.3dspace");
        String allFilesUrl = PropertyReader.getProperty("ds.service.url.for.all.files.in.a.document.search");
        String allFilesInADocument = threedUrl + allFilesUrl;
        String allFileResource = MessageFormat.format(allFilesInADocument, this.docId);
        LOGGER.info(allFilesInADocument);
        return allFileResource;
    }
}
