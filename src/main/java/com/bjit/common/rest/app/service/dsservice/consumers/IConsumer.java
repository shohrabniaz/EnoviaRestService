package com.bjit.common.rest.app.service.dsservice.consumers;

import okhttp3.Response;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import com.bjit.common.rest.app.service.dsservice.stores.Cookie;
import com.bjit.common.rest.app.service.dsservice.stores.CookieStore;
import com.bjit.common.rest.app.service.dsservice.stores.PropertyStore;

public interface IConsumer<T> {
    T consume() throws Exception;
    CookieStore getCookieStore();
    void setCookieStore(CookieStore cookieStore);
    PropertyStore getPropertyStore();
    void setPropertyStore(PropertyStore propertyStore);
    <M> void setBusinessObject(M businessModel);
    String getURL();

    default <K> K nextConsumer(IConsumer<K> nextConsumer) throws Exception{
        nextConsumer.setPropertyStore(this.getPropertyStore());
        nextConsumer.setCookieStore(this.getCookieStore());
        return nextConsumer.consume();
    };

    default List<Cookie> mineCookies(Response response){
        List<String> listOfCookies = response.headers("SET-Cookie");
        List<Cookie> cookieList = new ArrayList<>();
        Optional.ofNullable(listOfCookies).filter(cookiesList -> !cookiesList.isEmpty()).ifPresent((cookiesList) -> {

            listOfCookies.forEach((String cookie)->{
                String[] nameAndValueOfTheCookie = cookie.split("=", 2);
                Cookie newCookie = new Cookie();
                newCookie.setName(nameAndValueOfTheCookie[0]);
                newCookie.setValue(nameAndValueOfTheCookie[1]);
                cookieList.add(newCookie);
            });
        });

        return cookieList;
    }

    default void manageCookieStore(List<Cookie> cookies) throws MalformedURLException {
        manageCookieStore(cookies, getBaseFromURL(getURL()));
    }

    default void manageCookieStore(List<Cookie> cookies, String host) throws MalformedURLException {
        this.setCookieStore(this.getCookieStore());


        CookieStore cookieStore = getCookieStore();
        HashMap<String, List<Cookie>> previousCookies = cookieStore.getCookies();
        previousCookies = Optional.ofNullable(previousCookies).orElse(new HashMap<>());

        final HashMap<String, List<Cookie>> priorCookies =  previousCookies;

        Optional.of(previousCookies).filter(prevCookies -> !prevCookies.isEmpty()).ifPresentOrElse(((prevCookies) -> {
            List<Cookie> siteCookies = prevCookies.get(host);
            Optional.ofNullable(siteCookies).filter(hostCookies -> !hostCookies.isEmpty()).ifPresentOrElse(((hostCookies) -> {
                priorCookies.get(host).addAll(cookies.stream().filter(cookie->hostCookies.contains(cookie)).collect(Collectors.toList()));
            }),()->{
                priorCookies.put(host, cookies);
            });
        }),() ->{
            priorCookies.put(host, cookies);
        });
    }

    default PropertyStore setProperties(HashMap<String, String> properties) throws MalformedURLException {
        PropertyStore propertyStore = getPropertyStore();
        HashMap<String, String> allProperties = propertyStore.getProperties();
        allProperties = Optional.ofNullable(allProperties).orElseGet(() -> new HashMap<>());
        allProperties.putAll(properties);
        propertyStore.setProperties(allProperties);
        return propertyStore;
    }

    static String getBaseFromURL(String stringUrl) throws MalformedURLException {
        URL url = new URL(stringUrl);
        String base = url.getProtocol() + "://" + url.getHost();
        return base;
    }
}
