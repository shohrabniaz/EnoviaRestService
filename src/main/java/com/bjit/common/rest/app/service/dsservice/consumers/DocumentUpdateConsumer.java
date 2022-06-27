package com.bjit.common.rest.app.service.dsservice.consumers;

import com.bjit.common.rest.app.service.dsservice.models.documentcheckin.documentresponse.DocumentResponseModel;
import com.bjit.common.rest.app.service.dsservice.serviceclient.Method;
import com.bjit.ewc18x.utils.PropertyReader;
import okhttp3.Response;

public class DocumentUpdateConsumer extends DocumentCreationConsumer {

    private static final org.apache.log4j.Logger LOGGER = org.apache.log4j.Logger.getLogger(DocumentUpdateConsumer.class);

    private String documentId;

    public DocumentUpdateConsumer(String documentId) {
        this.documentId = documentId;
    }

    @Override
    public DocumentResponseModel consume() throws Exception {
        Response response = getClientBuilder()
                .setUrl(getURL())
                .setMethod(Method.PUT)
                .setBodyData(getSerializedBusinessObject())
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

    @Override
    public String getURL() {
//        "https://dsd2v21xspace.plm.valmet.com/3dspace/resources/v1/modeler/documents/
        String threedUrl = PropertyReader.getProperty("ds.service.base.url.3dspace");
        String documentUrl = PropertyReader.getProperty("ds.service.url.document.update.and.file.attachment");
        String documentUpdateURL = threedUrl + documentUrl + this.documentId;
        LOGGER.info("Document Update URL : " + documentUpdateURL);

        return documentUpdateURL;
    }
}
