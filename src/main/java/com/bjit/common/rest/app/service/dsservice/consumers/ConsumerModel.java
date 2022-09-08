package com.bjit.common.rest.app.service.dsservice.consumers;

import okhttp3.Response;
import okhttp3.ResponseBody;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;
import java.util.Optional;
import com.bjit.common.rest.app.service.dsservice.serviceclient.HttpClientBuilder;
import com.bjit.common.rest.app.service.dsservice.stores.Cookie;
import com.bjit.common.rest.app.service.dsservice.stores.CookieStore;
import com.bjit.common.rest.app.service.dsservice.stores.PropertyStore;
import com.bjit.common.rest.app.service.utilities.JSON;
import com.bjit.ewc18x.utils.PropertyReader;

public abstract class ConsumerModel<T> implements IConsumer<T> {
    private static final org.apache.log4j.Logger LOGGER = org.apache.log4j.Logger.getLogger(ConsumerModel.class);
    
    private CookieStore cookieStore;
    private PropertyStore propertyStore;
    private String serializedBusinessObject;

    @Override
    public CookieStore getCookieStore() {
        return Optional.ofNullable(this.cookieStore).orElseGet(() -> new CookieStore());
    }

    @Override
    public void setCookieStore(CookieStore cookieStore) {
        this.cookieStore = cookieStore;
    }

    @Override
    public PropertyStore getPropertyStore() {
        return Optional.ofNullable(this.propertyStore).orElseGet(() -> new PropertyStore());
    }

    @Override
    public void setPropertyStore(PropertyStore propertyStore) {
        this.propertyStore = propertyStore;
    }

    protected void manageCookies(Response response) throws MalformedURLException {
        List<Cookie> cookies = mineCookies(response);
        manageCookieStore(cookies);
    }

    protected T getResponseModel(Response response, Class<T> classType) throws IOException {
        manageCookies(response);

        ResponseBody body = response.body();
        Optional.ofNullable(body).orElseThrow(() -> new NullPointerException(this.getClass().getCanonicalName() + " " + PropertyReader.getProperty("ds.service.response.null.or.empty")));

        String responseData = body.string();
        Optional.of(responseData).filter(data -> !data.isEmpty()).orElseThrow(() -> new NullPointerException(this.getClass().getCanonicalName() + " " + PropertyReader.getProperty("ds.service.response.null.or.empty")));
        LOGGER.info("Response from " + this.getURL());
        LOGGER.info(responseData);

        JSON jsonParser = new JSON();
        T deserializedResponse = jsonParser.deserialize(responseData, classType);

        return deserializedResponse;
    }

    protected HttpClientBuilder getClientBuilder() {
        HttpClientBuilder httpClientBuilder = new HttpClientBuilder().setCookieStore(getCookieStore());
        return httpClientBuilder;
    }

    @Override
    public <M> void setBusinessObject(M businessModel){
        this.serializedBusinessObject = new JSON(false).serialize(businessModel);
        LOGGER.info("Request Data for " + this.getURL());
        LOGGER.info(serializedBusinessObject);
    }

    protected String getSerializedBusinessObject(){
        return this.serializedBusinessObject;
    }
}
