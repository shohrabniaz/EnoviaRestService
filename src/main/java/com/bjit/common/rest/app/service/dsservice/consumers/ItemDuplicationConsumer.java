package com.bjit.common.rest.app.service.dsservice.consumers;

import com.bjit.common.rest.app.service.dsservice.models.itemduplication.ItemDuplicationResponseModel;
import com.bjit.common.rest.app.service.dsservice.serviceclient.Method;
import com.bjit.ewc18x.utils.PropertyReader;
import okhttp3.Response;

public class ItemDuplicationConsumer extends ConsumerModel<ItemDuplicationResponseModel> {

    private static final org.apache.log4j.Logger LOGGER = org.apache.log4j.Logger.getLogger(ItemDuplicationConsumer.class);

    @Override
    public ItemDuplicationResponseModel consume() throws Exception {
        Response response = getClientBuilder()
                .setUrl(getURL())
                .setMethod(Method.POST)
                .setBodyData(getSerializedBusinessObject())
                .setHeaders("Accept", "application/json")
                .setHeaders("Content-Type", "application/json")
                .setHeaders(getPropertyStore().getProperties().get("csrf-field-name"), getPropertyStore().getProperties().get("csrf-filed-value"))
                .setHeaders("SecurityContext", PropertyReader.getProperty("preferred.security.context.dslc"))
                .build();

        ItemDuplicationResponseModel itemDuplicationResponseModel = getResponseModel(response, ItemDuplicationResponseModel.class);
        return itemDuplicationResponseModel;
    }

    @Override
    public String getURL() {
        /*
         *  ds api not working with seperate instance.
         *  "https://dsd2v21xspace.plm.valmet.com/3dspace/resources/v1/modeler/dslc/duplicate"
         */
        String threedUrl = PropertyReader.getProperty("ds.service.base.url.3dspace");
        String itemDuplicateUrl = PropertyReader.getProperty("ds.service.url.item.dslc.item.duplicate");
        String url = threedUrl + itemDuplicateUrl;
        LOGGER.info(url);
        return url;
    }
}
