package com.bjit.common.rest.app.service.controller.authentication.token;

import java.security.*;

public interface IToken {
    void setAlgorithm();
    String getToken(String userId, String password) throws Exception;
    String sign(PrivateKey privateKey, String message) throws Exception;
    void verify(String token) throws Exception;
    <T>boolean verify(T key, String message, String signature) throws Exception;
    String GetPropertyFromToken(String token, String claimName) throws Exception;
}
