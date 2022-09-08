package com.bjit.common.rest.app.service.dsservice.consumers;

import com.bjit.common.code.utility.security.ContextPasswordSecurity;
import okhttp3.Response;

import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.List;
import com.bjit.common.rest.app.service.dsservice.models.csrf.CSRFTokenResponseModel;
import com.bjit.common.rest.app.service.dsservice.serviceclient.Method;
import com.bjit.common.rest.app.service.dsservice.stores.Cookie;
import com.bjit.common.rest.app.service.dsservice.stores.PropertyStore;
import com.bjit.ewc18x.utils.PropertyReader;

public class CSRFTokenConsumer extends ConsumerModel<CSRFTokenResponseModel>/* implements IConsumer<CSRFTokenResponseModel>*/ {

    @Override
    public CSRFTokenResponseModel consume() throws Exception {

        ContextPasswordSecurity contextPasswordSecurity = new ContextPasswordSecurity();

        String userIdInPropertiesFile = contextPasswordSecurity.decryptPassword(PropertyReader.getProperty("pdm.integration.user.cred.username"));
        String contextUserName = contextPasswordSecurity.decryptPassword(PropertyReader.getProperty("context.name"));
        String passwordInPropertiesFile = contextPasswordSecurity.decryptPassword(PropertyReader.getProperty("pdm.integration.user.cred.pass"));
        String contextPassword = contextPasswordSecurity.decryptPassword(PropertyReader.getProperty("context.pass"));

        Response response = getClientBuilder()
                .setUrl(getURL())
                .setParameters("service", getCSRFUrl())
                .setMethod(Method.POST)
                .setHeaders("Content-Type", "application/x-www-form-urlencoded")
                .setFieldData("username", userIdInPropertiesFile)
                .setFieldData("password", passwordInPropertiesFile)
                .setFieldData("lt", getPropertyStore().getProperties().get("lt"))
                .build();

        List<Cookie> cookies = mineCookies(response);
        manageCookieStore(cookies, IConsumer.getBaseFromURL(getCSRFUrl()));

        CSRFTokenResponseModel csrfTokenModel = getResponseModel(response, CSRFTokenResponseModel.class);

        String success = csrfTokenModel.getSuccess();
        if (!Boolean.parseBoolean(success)) {
            throw new RuntimeException(csrfTokenModel.getError());
        }

        managePropertyStore(csrfTokenModel);

        return csrfTokenModel;
    }

    private void managePropertyStore(CSRFTokenResponseModel CSRFTokenResponseModel) throws MalformedURLException {
        HashMap<String, String> properties = new HashMap<>();
        properties.put("csrf-field-name", CSRFTokenResponseModel.getCsrf().getName());
        properties.put("csrf-filed-value", CSRFTokenResponseModel.getCsrf().getValue());

        PropertyStore propertyStore = setProperties(properties);
        setPropertyStore(propertyStore);
    }

    @Override
    public String getURL() {
//        "https://dsd2v21xpassport.plm.valmet.com/3dpassport/login";
        String passportUrl = PropertyReader.getProperty("ds.service.base.url.passport");
        String loginTicketUrl = PropertyReader.getProperty("ds.service.url.login.ticket");
        String ticketUrl = passportUrl + loginTicketUrl;
        return ticketUrl;
    }

    private String getCSRFUrl() {
//        "https://dsd2v21xspace.plm.valmet.com/3dspace/resources/v1/application/CSRF"
        String threedspace = PropertyReader.getProperty("ds.service.base.url.3dspace");
        String csrfToken = PropertyReader.getProperty("ds.service.url.csrf.token");
        String csrfTokenUrl = threedspace + csrfToken;
        return csrfTokenUrl;
    }
}
