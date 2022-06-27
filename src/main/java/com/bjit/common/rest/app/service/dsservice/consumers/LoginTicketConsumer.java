package com.bjit.common.rest.app.service.dsservice.consumers;

import okhttp3.Response;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.HashMap;
import com.bjit.common.rest.app.service.dsservice.serviceclient.Method;
import com.bjit.common.rest.app.service.dsservice.models.login.LoginTicketModel;
import com.bjit.common.rest.app.service.dsservice.stores.PropertyStore;
import com.bjit.ewc18x.utils.PropertyReader;

public class LoginTicketConsumer extends ConsumerModel<LoginTicketModel> {

    @Override
    public LoginTicketModel consume() throws IOException {
        Response response = getClientBuilder()
                .setUrl(getURL())
                .setParameters("action", "get_auth_params")
                .setMethod(Method.GET)
                .build();

        LoginTicketModel loginTicketModel = getResponseModel(response, LoginTicketModel.class);
        managePropertyStore(loginTicketModel);

        return loginTicketModel;
    }

    private void managePropertyStore(LoginTicketModel loginTicketModel) throws MalformedURLException {
        HashMap<String, String> properties = new HashMap<>();
        properties.put("lt", loginTicketModel.getLt());

        PropertyStore propertyStore = setProperties(properties);
        setPropertyStore(propertyStore);
    }

    @Override
    public String getURL() {
//        "https://dsd2v21xpassport.plm.valmet.com/3dpassport/login"
        String passportUrl = PropertyReader.getProperty("ds.service.base.url.passport");
        String loginTicketUrl = PropertyReader.getProperty("ds.service.url.login.ticket");
        String ticketUrl = passportUrl + loginTicketUrl;
        return ticketUrl;
    }
}
