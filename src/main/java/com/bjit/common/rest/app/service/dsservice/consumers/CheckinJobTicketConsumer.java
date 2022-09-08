package com.bjit.common.rest.app.service.dsservice.consumers;

import okhttp3.Response;

import java.net.MalformedURLException;
import java.util.HashMap;
import com.bjit.common.rest.app.service.dsservice.serviceclient.Method;
import com.bjit.common.rest.app.service.dsservice.stores.PropertyStore;
import com.bjit.common.rest.app.service.dsservice.models.fcsjob.FCSJobTicketsModel;
import com.bjit.ewc18x.utils.PropertyReader;

public class CheckinJobTicketConsumer extends ConsumerModel<FCSJobTicketsModel> {

    @Override
    public FCSJobTicketsModel consume() throws Exception {
        Response response = getClientBuilder()
                .setUrl(getURL())
                .setMethod(Method.PUT)
                .setHeaders(getPropertyStore().getProperties().get("csrf-field-name"), getPropertyStore().getProperties().get("csrf-filed-value"))
                .setHeaders("Accept", "application/json")
                .setHeaders("Content-Type", "application/json")
                .build();
        FCSJobTicketsModel fcsJobTicketModel = getResponseModel(response, FCSJobTicketsModel.class);

        managePropertyStore(fcsJobTicketModel);

        String success = fcsJobTicketModel.getSuccess();
        if (!Boolean.parseBoolean(success)) {
            throw new RuntimeException(fcsJobTicketModel.getError());
        }

        return fcsJobTicketModel;
    }

    private void managePropertyStore(FCSJobTicketsModel fcsJobTicketsModel) throws MalformedURLException {
        HashMap<String, String> properties = new HashMap<>();
        properties.put("ticket-param-name", fcsJobTicketsModel.getData().get(0).getDataelements().getTicketparamname());
        properties.put("fcs-job-ticket", fcsJobTicketsModel.getData().get(0).getDataelements().getTicket());

        PropertyStore propertyStore = setProperties(properties);
        setPropertyStore(propertyStore);
    }

    @Override
    public String getURL() {
//        "https://dsd2v21xspace.plm.valmet.com/3dspace/resources/v1/modeler/documents/files/CheckinTicket"
        String threedUrl = PropertyReader.getProperty("ds.service.base.url.3dspace");
        String checkinJobTicket = PropertyReader.getProperty("ds.service.url.checkin.job.ticket");
        String ticketUrl = threedUrl + checkinJobTicket;
        return ticketUrl;
    }
}
