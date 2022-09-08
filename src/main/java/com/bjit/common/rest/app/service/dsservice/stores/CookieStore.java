package com.bjit.common.rest.app.service.dsservice.stores;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public class CookieStore {
    HashMap<String, List<Cookie>> cookies;

    public HashMap<String, List<Cookie>> getCookies() {
        cookies = Optional.ofNullable(cookies).filter(cookies -> !cookies.isEmpty()).orElseGet(() -> new HashMap<>());
        return cookies;
    }

    public void setCookies(HashMap<String, List<Cookie>> cookies) {
        this.cookies = cookies;
    }
}
