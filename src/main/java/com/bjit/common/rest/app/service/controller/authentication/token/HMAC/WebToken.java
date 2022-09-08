/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.app.service.controller.authentication.token.HMAC;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.bjit.common.rest.app.service.controller.authentication.AuthenticationProcess;
import com.bjit.common.rest.app.service.controller.authentication.AuthenticationUserModel;
import com.bjit.common.rest.app.service.utilities.NullOrEmptyChecker;
import com.bjit.ewc18x.utils.PropertyReader;
import java.util.Date;
//import org.apache.logging.log4j.Logger;
//import org.apache.logging.log4j.LogManager;
import org.apache.log4j.Logger;

/**
 *
 * @author omour.faruq
 */
public class WebToken {

    private final String key = "@$aJ1546n%%l*o&$A^*%^E*$*^%dT^#$r#$^r%*^s#%p@$#o&#@*C@%#%I^^@o&^";
    private final byte[] secret = key.getBytes();
    private final Algorithm algorithm = Algorithm.HMAC256(secret);
    private DecodedJWT jwt;
    private static final String AUTHENTICATION_SCHEME = "Bearer ";

    //private static final Logger WEB_TOKEN_LOGGER = LogManager.getLogger(WebToken.class);
    private static final Logger WEB_TOKEN_LOGGER = Logger.getLogger(WebToken.class);

    private int tokenExpireTime() {
        String tokenInMinute = PropertyReader.getProperty("token.expire.time.in.minute");
        if (isNullOrEmpty(tokenInMinute)) {
            tokenInMinute = "1";
        }

        int tokenExpireTime = Integer.parseInt(tokenInMinute) * 60 * 1000;
        return tokenExpireTime;
    }

    public String CreateToken(String userId, String password) {
        try {
            System.out.println("\n\n\n");
            WEB_TOKEN_LOGGER.debug("--------------- ||| Token Generation Process Has Been Started ||| ---------------");
            WEB_TOKEN_LOGGER.debug("#################################################################################");

            JWTCreator.Builder jwtCreator = JWT.create();
            String jsonWebToken = jwtCreator
                    .withIssuedAt(new Date(System.currentTimeMillis()))
                    .withIssuer("BJIT")
                    .withSubject("Transactions")
                    .withAudience("Web Services")
                    .withExpiresAt(new Date(System.currentTimeMillis() + tokenExpireTime()))
                    .withClaim("UserId", userId)
                    .withClaim("Password", password)
                    .sign(algorithm);

            String token = AUTHENTICATION_SCHEME + jsonWebToken;
            WEB_TOKEN_LOGGER.info("Token is : " + token);
            return token;
        } catch (JWTCreationException | IllegalArgumentException exp) {
            WEB_TOKEN_LOGGER.error(exp.getMessage());
            throw exp;
        } finally {
            WEB_TOKEN_LOGGER.debug("--------------- ||| Token Generation Process Has Been Completed ||| ---------------");
            WEB_TOKEN_LOGGER.debug("###################################################################################");
            System.out.println("\n\n\n");
        }
    }

    private Boolean isNullOrEmpty(String tokenInMinute) {
        return tokenInMinute == null || tokenInMinute.equalsIgnoreCase("");
    }

    public String CreateToken(String userId, String password, String host, String isCas) {
        try {
            System.out.println("\n\n\n");
            WEB_TOKEN_LOGGER.debug("--------------- ||| Token Generation Process Has Been Started ||| ---------------");
            WEB_TOKEN_LOGGER.debug("#################################################################################");
            JWTCreator.Builder jwtCreator = JWT.create();
            String jsonWebToken = jwtCreator
                    .withIssuedAt(new Date(System.currentTimeMillis()))
                    .withIssuer("BJIT")
                    .withSubject("Transactions")
                    .withAudience("Web Services")
                    .withExpiresAt(new Date(System.currentTimeMillis() + tokenExpireTime()))
                    .withClaim("UserId", userId)
                    .withClaim("Password", password)
                    .withClaim("host", host)
                    .withClaim("isCas", isCas)
                    .sign(algorithm);

            String token = "Bearer " + jsonWebToken;
            WEB_TOKEN_LOGGER.debug("Token is : " + token);
            return token;
        } catch (JWTCreationException | IllegalArgumentException exp) {
            WEB_TOKEN_LOGGER.error(exp.getMessage());
            throw exp;
        } finally {
            WEB_TOKEN_LOGGER.debug("--------------- ||| Token Generation Process Has Been Completed ||| ---------------");
            WEB_TOKEN_LOGGER.debug("###################################################################################");
            System.out.println("\n\n\n");
        }
    }

    public String CreateToken(String authenticationModel) {
        try {
            System.out.println("\n\n\n");
            WEB_TOKEN_LOGGER.debug("--------------- ||| Token Generation Process Has Been Started ||| ---------------");
            WEB_TOKEN_LOGGER.debug("#################################################################################");
            JWTCreator.Builder jwtCreator = JWT.create();
            String jsonWebToken = jwtCreator
                    .withIssuedAt(new Date(System.currentTimeMillis()))
                    .withIssuer("BJIT")
                    .withSubject("Transactions")
                    .withAudience("Web Services")
                    .withExpiresAt(new Date(System.currentTimeMillis() + tokenExpireTime()))
                    .withClaim("Model", authenticationModel)
                    .sign(algorithm);

            String token = "Bearer " + jsonWebToken;
            //WEB_TOKEN_LOGGER.info("Token is : " + token);
            WEB_TOKEN_LOGGER.info("Token generation has been completed");
            return token;
        } catch (JWTCreationException | IllegalArgumentException exp) {
            WEB_TOKEN_LOGGER.error(exp.getMessage());
            throw exp;
        } finally {
            WEB_TOKEN_LOGGER.debug("--------------- ||| Token Generation Process Has Been Completed ||| ---------------");
            WEB_TOKEN_LOGGER.debug("###################################################################################");
            System.out.println("\n\n\n");
        }
    }

    public String VerifyToken(String token) throws Exception {
        try {
            if (token.contains(AUTHENTICATION_SCHEME)) {
                int schemeIndex = token.indexOf(AUTHENTICATION_SCHEME);
                if (schemeIndex != 0) {
                    throw new Exception("Invalid token. Please provide a valid token");
                }
            } else {
                throw new Exception("Invalid token. Please provide a valid token");
            }

            jwt = JWT.require(algorithm)
                    .build()
                    .verify(token.substring(AUTHENTICATION_SCHEME.length()).trim());
            WEB_TOKEN_LOGGER.debug("Token has verified");

            String newToken = checkTokenExpireTime();
            return NullOrEmptyChecker.isNullOrEmpty(newToken) ? token : newToken;
        } catch (JWTVerificationException | IllegalArgumentException exp) {
            WEB_TOKEN_LOGGER.fatal(exp.getMessage());
            throw exp;
        }
    }

    public String VerifyToken(String token, Boolean isInTransaction) throws Exception {
        if (isInTransaction) {
            try {
                if (token.contains(AUTHENTICATION_SCHEME)) {
                    int schemeIndex = token.indexOf(AUTHENTICATION_SCHEME);
                    if (schemeIndex != 0) {
                        WEB_TOKEN_LOGGER.fatal("Invalid token. Please provide a valid token");
                        throw new Exception("Invalid token. Please provide a valid token");
                    }
                } else {
                    WEB_TOKEN_LOGGER.fatal("Invalid token. Please provide a valid token");
                    throw new Exception("Invalid token. Please provide a valid token");
                }

                Date newExpireTime = new Date(System.currentTimeMillis() + tokenExpireTime());
                jwt = JWT
                        .require(algorithm)
                        .acceptExpiresAt(newExpireTime.getTime())
                        .build()
                        .verify(token.substring(AUTHENTICATION_SCHEME.length()).trim());
                WEB_TOKEN_LOGGER.info("Token has verified");

                String newToken = checkTokenExpireTime();
                return NullOrEmptyChecker.isNullOrEmpty(newToken) ? token : newToken;
            } catch (JWTVerificationException | IllegalArgumentException exp) {
                WEB_TOKEN_LOGGER.error(exp.getMessage());
                throw exp;
            }
        } else {
            return VerifyToken(token);
        }
    }

    public String checkTokenExpireTime() {
        try {
            WEB_TOKEN_LOGGER.debug("Checking token expiration time");
            Date expiresAt = jwt.getExpiresAt();
            Date now = new Date();
            
            long difference = expiresAt.getTime() - now.getTime();
            
            int tokenExpireTime = tokenExpireTime();
            int expireInParcentForNewToken = (int)Math.round(tokenExpireTime * 0.1);


            if (expireInParcentForNewToken >= difference) {
                WEB_TOKEN_LOGGER.info("Generating new token");
                return CreateToken(jwt.getClaim("Model").asString());
            }
            return null;
        } catch (Exception exp) {
            WEB_TOKEN_LOGGER.error("Failed to create a new token");
            WEB_TOKEN_LOGGER.error(exp.getMessage());
            throw exp;
        }
    }

    public String GetPropertyFromToken(String token, String claimName) throws Exception {
        try {
            VerifyToken(token);
            return jwt.getClaim(claimName).asString();
        } catch (Exception exp) {
            WEB_TOKEN_LOGGER.error(exp.getMessage());
            throw exp;
        }
    }
    
    public AuthenticationUserModel getUserCredentials(String token) throws Exception{
        try {
            WebToken webToken = new WebToken();
            AuthenticationProcess userAuthentication = new AuthenticationProcess();
            String authenticationModel = webToken.GetPropertyFromToken(token, "Model");
            AuthenticationUserModel authenticateUser = userAuthentication.getAuthenticUserModel(authenticationModel);
            return authenticateUser;
        } catch (Exception exp) {
            WEB_TOKEN_LOGGER.error(exp.getMessage());
            throw exp;
        }
    }
}
