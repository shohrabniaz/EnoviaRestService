package com.bjit.common.rest.app.service.controller.authentication.token.HMAC;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.bjit.common.rest.app.service.controller.authentication.token.IToken;

import java.security.PrivateKey;
import java.util.Date;

//import org.apache.logging.log4j.Logger;
//import org.apache.logging.log4j.LogManager;
import org.apache.log4j.Logger;

public final class HMACToken implements IToken {
    //private static final Logger HMAC_TOKEN_LOGGER = LogManager.getLogger(HMACToken.class);
    private static final Logger HMAC_TOKEN_LOGGER = Logger.getLogger(HMACToken.class);
    //region Global Variables For This Class
    private DecodedJWT jwt;
    private Algorithm algorithm;
    private String HMACKey;



    private byte[] HMACSecretKeysByteArray;

    //endregion

    //region Constructors
    public HMACToken() {
        generateHMAC256Keys();
        setAlgorithm();
    }
    //endregion

    public void generateHMAC256Keys() {
        this.setHMACKey("@$aJ1546n%%l*o&$A^*%^E*$*^%dT^#$r#$^r%*^s#%p@$#o&#@*C@%#%I^^@o&^");
        this.setHMACSecretKeysByteArray(this.getHMACKey().getBytes());
    }

    @Override
    public void setAlgorithm() {
        try{
            this.setAlgorithm(Algorithm.HMAC256(this.getHMACSecretKeysByteArray()));
        }
        catch(IllegalArgumentException exp){
            HMAC_TOKEN_LOGGER.error("Error occurred due to : " + exp.getMessage());
            throw exp;
        }
    }

    @Override
    public String getToken(String userId, String password) {
        try{
           JWTCreator.Builder jwtCreator = JWT.create();
            String jsonWebToken = jwtCreator
                    .withIssuedAt(new Date(System.currentTimeMillis()))
                    .withIssuer("Security-Module")
                    .withSubject("To do transaction")
                    .withAudience("Different Modules")
                    .withExpiresAt(new Date(System.currentTimeMillis() + 20 * 60 * 100000))
                    .withClaim("UserId", userId)
                    .withClaim("Password", password)
                    .sign(this.getAlgorithm());

            return jsonWebToken; 
        }
        catch(JWTCreationException | IllegalArgumentException exp){
            HMAC_TOKEN_LOGGER.error("Error occurred due to : " + exp.getMessage());
            throw exp;
        }
    }

    @Override
    public String sign(PrivateKey privateKey, String message) {
        try{
            return null;
        }
        catch(Exception exp){
            HMAC_TOKEN_LOGGER.error("Error occurred due to : " + exp.getMessage());
            throw exp;
        }
    }

    @Override
    public void verify(String token) {
        try{
           this.setJwt(JWT.require(this.getAlgorithm())
                    .build()
                    .verify(token)); 
        }
        catch(JWTVerificationException exp){
            HMAC_TOKEN_LOGGER.error("Error occurred due to : " + exp.getMessage());
            throw exp;
        }
    }

    @Override
    public <Object> boolean verify(Object key, String message, String signature) {
        try{
            return false;
        }
        catch(Exception exp){
            HMAC_TOKEN_LOGGER.error("Error occurred due to : " + exp.getMessage());
            throw exp;
        }
    }

    @Override
    public String GetPropertyFromToken(String token, String claimName) {
        try{
            verify(token);
            return jwt.getClaim(claimName).asString();
        }
        catch(Exception exp){
            HMAC_TOKEN_LOGGER.error("Error occurred due to : " + exp.getMessage());
            throw exp;
        }
        
    }

    //region Getters and Setters
    public DecodedJWT getJwt() {
        return jwt;
    }

    public void setJwt(DecodedJWT jwt) {
        this.jwt = jwt;
    }

    public Algorithm getAlgorithm() {
        return algorithm;
    }

    public void setAlgorithm(Algorithm algorithm) {
        this.algorithm = algorithm;
    }

    public String getHMACKey() {
        return HMACKey;
    }

    public void setHMACKey(String HMACKey) {
        this.HMACKey = HMACKey;
    }

    public byte[] getHMACSecretKeysByteArray() {
        return HMACSecretKeysByteArray;
    }

    public void setHMACSecretKeysByteArray(byte[] HMACSecretKeysByteArray) {
        this.HMACSecretKeysByteArray = HMACSecretKeysByteArray;
    }
    //endregion
}

