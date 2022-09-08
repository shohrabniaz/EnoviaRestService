/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.app.service.controller.authentication;

import com.bjit.common.code.utility.security.ContextPasswordSecurity;
import com.bjit.common.rest.app.service.context.CreateContext;
import com.bjit.common.rest.app.service.utilities.JSON;
import com.bjit.ewc18x.utils.PropertyReader;
import matrix.db.Context;
import org.apache.log4j.Logger;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;

/**
 * @author BJIT
 */
public final class AuthenticationProcess {

    //private static final Logger AUTHENTICATION_PROCESS_LOGGER = LogManager.getLogger(AuthenticationProcess.class);
    private static final Logger AUTHENTICATION_PROCESS_LOGGER = Logger.getLogger(AuthenticationProcess.class);
    private static final String ENCRYPTING_SALT = ")(*&%^&457984651FGHJKL:erfd65!$%^!@$*";
    private String userId;
    private String password;
    private String host;
    private String isCasEnvironment;
    private String encryptionSalt;

    public void AuthenticateUser(HttpServletRequest httpRequest) throws Exception {
        try {
            this.getUserCredentialsFromHttpRequest(httpRequest);
            this.setEncryptionSalt(ENCRYPTING_SALT);

            IEncryptionProcessors blowFishAlgorithm = new BlowFishEncryption();
            blowFishAlgorithm.setSalt(this.getEncryptionSalt());
            this.decryptUserCredentials(blowFishAlgorithm);
        } catch (Exception exp) {
            AUTHENTICATION_PROCESS_LOGGER.error("Error occurred due to : " + exp.getMessage());
            throw exp;
        }
    }

    public AuthenticationUserModel getAuthenticUserModel(String authenticationModel) throws Exception {
        try {
            //this.getUserCredentialsFromHttpRequest(httpRequest);
            this.setEncryptionSalt(ENCRYPTING_SALT);

            IEncryptionProcessors blowFishAlgorithm = new BlowFishEncryption();
            blowFishAlgorithm.setSalt(this.getEncryptionSalt());
            AuthenticationUserModel decryptUserCredentials = this.decryptUserCredentials(blowFishAlgorithm, authenticationModel);

            //this.setUserId(decryptUserCredentials.getUserId());
            //this.setPassword(decryptUserCredentials.getPassword());
            //this.setHost(decryptUserCredentials.getHost());
            //this.setIsCasEnvironment(decryptUserCredentials.getIsCasContext());
            return decryptUserCredentials;
        } catch (Exception exp) {
            AUTHENTICATION_PROCESS_LOGGER.error("Error occurred due to : " + exp.getMessage());
            throw exp;
        }
    }

    /*public void getAuthenticUserModel(HttpServletRequest httpRequest, String host, String isCasEnvironment) throws Exception {
        try {
            this.getUserCredentialsFromHttpRequest(httpRequest);
            this.setEncryptionSalt(ENCRYPTING_SALT);
            
            IEncryptionProcessors blowFishAlgorithm = new BlowFishEncryption();
            blowFishAlgorithm.setSalt(this.getEncryptionSalt());
            this.decryptUserCredentials(blowFishAlgorithm, host, isCasEnvironment);
        } catch (Exception exp) {
            AUTHENTICATION_PROCESS_LOGGER.error("Error occurred due to : " + exp.getMessage());
            throw exp;
        }
    }*/
    public String AuthenticateUser(HttpServletRequest httpRequest, String host, String isCasEnvironment) throws Exception {
        try {
            this.getUserCredentialsFromHttpRequest(httpRequest, host, isCasEnvironment);

            return getEncryptedUserCredentials();
        } catch (Exception exp) {
            AUTHENTICATION_PROCESS_LOGGER.error("Error occurred due to : " + exp.getMessage());
            throw exp;
        }
    }

    public String AuthenticateUser(HttpServletRequest httpRequest, String host, String isCasEnvironment, Boolean is2ndGeneration) throws Exception {
        try {

            if (is2ndGeneration) {
                ContextPasswordSecurity contextPasswordSecurity = new ContextPasswordSecurity();
                String userId = contextPasswordSecurity.decryptPassword(httpRequest.getHeader("userId"));
                String password = contextPasswordSecurity.decryptPassword(httpRequest.getHeader("password"));
                setUserCredentials(userId, password, host, isCasEnvironment);
            } else

            this.getUserCredentialsFromHttpRequest(httpRequest, host, isCasEnvironment);

            return getEncryptedUserCredentials();
        } catch (Exception exp) {
            AUTHENTICATION_PROCESS_LOGGER.error("Error occurred due to : " + exp.getMessage());
            throw exp;
        }
    }

    private String getEncryptedUserCredentials() throws Exception {
        this.setEncryptionSalt(ENCRYPTING_SALT);

        IEncryptionProcessors blowFishAlgorithm = new BlowFishEncryption();
        blowFishAlgorithm.setSalt(this.getEncryptionSalt());

        AuthenticationUserModel authenticateModel = new AuthenticationUserModel();

        authenticateModel.setUserId(this.getUserId());
        authenticateModel.setPassword(this.getPassword());
        authenticateModel.setHost(this.getHost());
        authenticateModel.setIsCasContext(this.getIsCasEnvironment());

        JSON json = new JSON();
        String serializedAuthenticationModel = json.serialize(authenticateModel);

        AUTHENTICATION_PROCESS_LOGGER.debug("Encryption has been started");
        String encryptUserCredentials = this.encryptUserCredentials(blowFishAlgorithm, serializedAuthenticationModel);
        AUTHENTICATION_PROCESS_LOGGER.debug("Encryption has been completed");
        return encryptUserCredentials;
    }

    public String AuthenticateUserAndTokenWithBackEndUser(HttpServletRequest httpRequest, String host, String isCasEnvironment) throws Exception {
        try {
            this.getUserCredentialsFromHttpRequest(httpRequest, host, isCasEnvironment);
            return getEncryptedCredentials();
        } catch (Exception exp) {
            AUTHENTICATION_PROCESS_LOGGER.error("Error occurred due to : " + exp.getMessage());
            throw exp;
        }
    }

    public String AuthenticateUserAndTokenWithBackEndUser(HttpServletRequest httpRequest, String host, String isCasEnvironment, Boolean is2ndGeneration) throws Exception {
        try {
            if (is2ndGeneration) {
                ContextPasswordSecurity contextPasswordSecurity = new ContextPasswordSecurity();
                String userId = contextPasswordSecurity.decryptPassword(httpRequest.getHeader("userId"));
                String password = contextPasswordSecurity.decryptPassword(httpRequest.getHeader("password"));
                setUserCredentials(userId, password, host, isCasEnvironment);
            } else
                this.getUserCredentialsFromHttpRequest(httpRequest, host, isCasEnvironment);

            return getEncryptedCredentials();
        } catch (Exception exp) {
            AUTHENTICATION_PROCESS_LOGGER.error("Error occurred due to : " + exp.getMessage());
            throw exp;
        }
    }

    private String getEncryptedCredentials() throws Exception {
        this.setEncryptionSalt(ENCRYPTING_SALT);

        IEncryptionProcessors blowFishAlgorithm = new BlowFishEncryption();
        blowFishAlgorithm.setSalt(this.getEncryptionSalt());

        AuthenticationUserModel authenticateModel = new AuthenticationUserModel();

        ContextPasswordSecurity contextPasswordSecurity = new ContextPasswordSecurity();
        authenticateModel.setUserId(contextPasswordSecurity.decryptPassword(PropertyReader.getProperty("pdm.integration.user.cred.username")));
        authenticateModel.setPassword(contextPasswordSecurity.decryptPassword(PropertyReader.getProperty("pdm.integration.user.cred.pass")));
        authenticateModel.setHost(this.getHost());
        authenticateModel.setIsCasContext(this.getIsCasEnvironment());

        JSON json = new JSON();
        String serializedAuthenticationModel = json.serialize(authenticateModel);

        AUTHENTICATION_PROCESS_LOGGER.debug("Encryption has been started");
        String encryptUserCredentials = this.encryptUserCredentials(blowFishAlgorithm, serializedAuthenticationModel);
        AUTHENTICATION_PROCESS_LOGGER.debug("Encryption has been completed");
        return encryptUserCredentials;
    }

    public String AuthenticateUser(String userId, String password, String host, Boolean isCasEnvironment) throws Exception {
        try {

            this.setEncryptionSalt(ENCRYPTING_SALT);

            IEncryptionProcessors blowFishAlgorithm = new BlowFishEncryption();
            blowFishAlgorithm.setSalt(this.getEncryptionSalt());

            AuthenticationUserModel authenticateModel = new AuthenticationUserModel();

            this.setUserId(userId);
            this.setPassword(password);
            this.setHost(host);
            this.setIsCasEnvironment(isCasEnvironment.toString());

            authenticateModel.setUserId(this.getUserId());
            authenticateModel.setPassword(this.getPassword());
            authenticateModel.setHost(this.getHost());
            authenticateModel.setIsCasContext(this.getIsCasEnvironment());

            JSON json = new JSON();
            String serializedAuthenticationModel = json.serialize(authenticateModel);

            return this.encryptUserCredentials(blowFishAlgorithm, serializedAuthenticationModel);
        } catch (Exception exp) {
            AUTHENTICATION_PROCESS_LOGGER.error("Error occurred due to : " + exp.getMessage());
            throw exp;
        }
    }

    public void AuthenticateUser(String userId, String password, String host, String isCasEnvironment) throws Exception {
        try {
            this.setEncryptionSalt(ENCRYPTING_SALT);

            IEncryptionProcessors blowFishAlgorithm = new BlowFishEncryption();
            blowFishAlgorithm.setSalt(this.getEncryptionSalt());
            this.decryptUserCredentials(blowFishAlgorithm, userId, password, host, isCasEnvironment);
        } catch (Exception exp) {
            AUTHENTICATION_PROCESS_LOGGER.error("Error occurred due to : " + exp.getMessage());
            throw exp;
        }
    }

    private void getUserCredentialsFromHttpRequest(HttpServletRequest httpRequest) {
        System.out.println("\n");
        AUTHENTICATION_PROCESS_LOGGER.debug("--------------- ||| Getting credentials from HTTPHeader process has been started ||| ---------------");
        AUTHENTICATION_PROCESS_LOGGER.debug("####################################################################################################");
        try {
            AUTHENTICATION_PROCESS_LOGGER.info("Searching for user credentials");
            this.setUserId(httpRequest.getHeader("userId"));
            this.setPassword(httpRequest.getHeader("password"));
            this.setHost(httpRequest.getHeader("host"));
            this.setIsCasEnvironment(httpRequest.getHeader("isCasEnvironment"));
        } catch (Exception exp) {
            AUTHENTICATION_PROCESS_LOGGER.error("Error Occurred due to : " + exp.getMessage());
            throw exp;
        } finally {
            AUTHENTICATION_PROCESS_LOGGER.debug("--------------- ||| Getting credentials from HTTPHeader process has been completed ||| ---------------");
            AUTHENTICATION_PROCESS_LOGGER.debug("######################################################################################################");
            System.out.println("\n");
        }
    }

    private void setUserCredentials(String userId, String password, String host, String isCasEnvironment) {
        AUTHENTICATION_PROCESS_LOGGER.debug("--------------- ||| Getting credentials from HTTPHeader process has been started ||| ---------------");
        AUTHENTICATION_PROCESS_LOGGER.debug("####################################################################################################");
        try {
            AUTHENTICATION_PROCESS_LOGGER.info("Searching for user credentials");
            this.setUserId(userId);
            this.setPassword(password);
            this.setHost(host);
            this.setIsCasEnvironment(isCasEnvironment);
        } catch (Exception exp) {
            AUTHENTICATION_PROCESS_LOGGER.error("Error Occurred due to : " + exp.getMessage());
            throw exp;
        }
    }

    private void getUserCredentialsFromHttpRequest(HttpServletRequest httpRequest, String host, String isCasEnvironment) {
        //System.out.println("\n");
        AUTHENTICATION_PROCESS_LOGGER.debug("--------------- ||| Getting credentials from HTTPHeader process has been started ||| ---------------");
        AUTHENTICATION_PROCESS_LOGGER.debug("####################################################################################################");
        try {
            AUTHENTICATION_PROCESS_LOGGER.info("Searching for user credentials");
            this.setUserId(httpRequest.getHeader("userId"));
            this.setPassword(httpRequest.getHeader("password"));
            this.setHost(host);
            this.setIsCasEnvironment(isCasEnvironment);
        } catch (Exception exp) {
            AUTHENTICATION_PROCESS_LOGGER.error("Error Occurred due to : " + exp.getMessage());
            throw exp;
        } finally {
            //AUTHENTICATION_PROCESS_LOGGER.info("--------------- ||| Getting credentials from HTTPHeader process has been completed ||| ---------------");
            //AUTHENTICATION_PROCESS_LOGGER.info("######################################################################################################");
            //System.out.println("\n");
        }
    }

    private void decryptUserCredentials(IEncryptionProcessors blowFishDecryption) throws Exception {
        System.out.println("\n");
        AUTHENTICATION_PROCESS_LOGGER.debug("--------------- ||| Decryption process has been started ||| ---------------");
        AUTHENTICATION_PROCESS_LOGGER.debug("###########################################################################");
        try {
            this.setUserId(blowFishDecryption.decrypt(this.getUserId()));
            this.setPassword(blowFishDecryption.decrypt(this.getPassword()));
            this.setHost(blowFishDecryption.decrypt(this.getHost()));
            this.setIsCasEnvironment(blowFishDecryption.decrypt(this.getIsCasEnvironment()));
        } catch (InvalidKeyException | IllegalBlockSizeException | BadPaddingException |
                 UnsupportedEncodingException exp) {
            AUTHENTICATION_PROCESS_LOGGER.error("Error occurred due to : " + exp.getMessage());
            throw exp;
        } finally {
            AUTHENTICATION_PROCESS_LOGGER.debug("--------------- ||| Decryption process has been completed ||| ---------------");
            AUTHENTICATION_PROCESS_LOGGER.debug("#############################################################################");
            System.out.println("\n");
        }
    }

    private AuthenticationUserModel decryptUserCredentials(IEncryptionProcessors blowFishDecryption, String authenticationModel) throws Exception {
        System.out.println("\n");
        AUTHENTICATION_PROCESS_LOGGER.debug("--------------- ||| Decryption process has been started ||| ---------------");
        AUTHENTICATION_PROCESS_LOGGER.debug("###########################################################################");
        try {
            String getAuthenticationModel = blowFishDecryption.decrypt(authenticationModel);

            JSON json = new JSON();
            AuthenticationUserModel objAuthenticationModel = json.deserialize(getAuthenticationModel, AuthenticationUserModel.class);
            return objAuthenticationModel;

        } catch (InvalidKeyException | IllegalBlockSizeException | BadPaddingException |
                 UnsupportedEncodingException exp) {
            AUTHENTICATION_PROCESS_LOGGER.error("Error occurred due to : " + exp.getMessage());
            throw exp;
        } finally {
            AUTHENTICATION_PROCESS_LOGGER.debug("--------------- ||| Decryption process has been completed ||| ---------------");
            AUTHENTICATION_PROCESS_LOGGER.debug("#############################################################################");
            System.out.println("\n");
        }
    }

    private void decryptUserCredentials(IEncryptionProcessors blowFishDecryption, String host, String isCas) throws Exception {
        System.out.println("\n");
        AUTHENTICATION_PROCESS_LOGGER.debug("--------------- ||| Decryption process has been started ||| ---------------");
        AUTHENTICATION_PROCESS_LOGGER.debug("###########################################################################");
        try {
            //this.setUserId(blowFishDecryption.decrypt(this.getUserId()));
            this.setUserId(blowFishDecryption.decrypt(this.getUserId()));
            //this.setPassword(blowFishDecryption.decrypt(this.getPassword()));
            this.setPassword(blowFishDecryption.decrypt(this.getPassword()));
            this.setHost(host);
            this.setIsCasEnvironment(isCas);
        } catch (InvalidKeyException | IllegalBlockSizeException | BadPaddingException |
                 UnsupportedEncodingException exp) {
            AUTHENTICATION_PROCESS_LOGGER.error("Error occurred due to : " + exp.getMessage());
            throw exp;
        } finally {

            AUTHENTICATION_PROCESS_LOGGER.debug("--------------- ||| Decryption process has been completed ||| ---------------");
            AUTHENTICATION_PROCESS_LOGGER.debug("#############################################################################");
            System.out.println("\n");
        }
    }

    private void decryptUserCredentials(IEncryptionProcessors blowFishDecryption, String userId, String password, String host, String isCas) throws Exception {
        System.out.println("\n");
        AUTHENTICATION_PROCESS_LOGGER.debug("--------------- ||| Decryption process has been started ||| ---------------");
        AUTHENTICATION_PROCESS_LOGGER.debug("###########################################################################");
        try {
            this.setUserId(blowFishDecryption.decrypt(userId));
            this.setPassword(blowFishDecryption.decrypt(password));
            this.setHost(host);
            this.setIsCasEnvironment(isCas);
        } catch (InvalidKeyException | IllegalBlockSizeException | BadPaddingException |
                 UnsupportedEncodingException exp) {
            AUTHENTICATION_PROCESS_LOGGER.error("Error occurred due to : " + exp.getMessage());
            throw exp;
        } finally {
            AUTHENTICATION_PROCESS_LOGGER.debug("--------------- ||| Decryption process has been completed ||| ---------------");
            AUTHENTICATION_PROCESS_LOGGER.debug("#############################################################################");
            System.out.println("\n");
        }
    }

    public Context getContext() throws Exception {
        try {
            CreateContext createContext = new CreateContext();
            return createContext.getContext(this.getUserId(), this.getPassword(), this.getHost(), Boolean.parseBoolean(this.getIsCasEnvironment()), true, true);
        } catch (Exception exp) {
            AUTHENTICATION_PROCESS_LOGGER.error("Error occurred due to : " + exp.getMessage());
            throw exp;
        }
    }

    public Context getContext(String host, Boolean isCas) throws Exception {
        try {
            CreateContext createContext = new CreateContext();
            return createContext.getContext(this.getUserId(), this.getPassword(), host, isCas, true, false);
        } catch (Exception exp) {
            AUTHENTICATION_PROCESS_LOGGER.error("Error occurred due to : " + exp.getMessage());
            throw exp;
        }
    }

    public void encrypteUserData(HttpServletRequest httpRequest) throws Exception {
        System.out.println("\n");
        AUTHENTICATION_PROCESS_LOGGER.debug("--------------- ||| Encryption process has been started ||| ---------------");
        AUTHENTICATION_PROCESS_LOGGER.debug("###########################################################################");
        try {
            this.getUserCredentialsFromHttpRequest(httpRequest);
            this.setEncryptionSalt(ENCRYPTING_SALT);

            IEncryptionProcessors blowFishAlgorithm = new BlowFishEncryption();
            blowFishAlgorithm.setSalt(this.getEncryptionSalt());
            this.encryptUserCredentials(blowFishAlgorithm);
        } catch (Exception exp) {
            AUTHENTICATION_PROCESS_LOGGER.error("Error occurred due to : " + exp.getMessage());
            throw exp;
        } finally {
            AUTHENTICATION_PROCESS_LOGGER.debug("--------------- ||| Encryption process has been completed ||| ---------------");
            AUTHENTICATION_PROCESS_LOGGER.debug("#############################################################################");
            System.out.println("\n");
        }
    }

    public void encrypteUserData(String userCredentials) throws Exception {
        System.out.println("\n");
        AUTHENTICATION_PROCESS_LOGGER.debug("--------------- ||| Encryption process has been started ||| ---------------");
        AUTHENTICATION_PROCESS_LOGGER.debug("###########################################################################");
        try {
            this.setEncryptionSalt(ENCRYPTING_SALT);

            IEncryptionProcessors blowFishAlgorithm = new BlowFishEncryption();
            blowFishAlgorithm.setSalt(this.getEncryptionSalt());
            this.encryptUserCredentials(blowFishAlgorithm);
        } catch (Exception exp) {
            AUTHENTICATION_PROCESS_LOGGER.error("Error occurred due to : " + exp.getMessage());
            throw exp;
        } finally {
            //AUTHENTICATION_PROCESS_LOGGER.info("--------------- ||| Encryption process has been completed ||| ---------------");
            AUTHENTICATION_PROCESS_LOGGER.debug("--------------- ||| Encryption process has been completed ||| ---------------");
            AUTHENTICATION_PROCESS_LOGGER.debug("#############################################################################");
            System.out.println("\n");
        }
    }

    private void encryptUserCredentials(IEncryptionProcessors blowFishEncryption) throws Exception {
        System.out.println("\n");
        AUTHENTICATION_PROCESS_LOGGER.debug("--------------- ||| Encryption process has been started ||| ---------------");
        AUTHENTICATION_PROCESS_LOGGER.debug("###########################################################################");
        try {
            this.setUserId(blowFishEncryption.encrypt(this.getUserId()));
            this.setPassword(blowFishEncryption.encrypt(this.getPassword()));
            this.setHost(blowFishEncryption.encrypt(this.getHost()));
            //this.setIsCasEnvironment(blowFishEncryption.encrypt(this.getIsCasEnvironment()));
        } catch (InvalidKeyException | UnsupportedEncodingException | IllegalBlockSizeException | BadPaddingException exp) {
            AUTHENTICATION_PROCESS_LOGGER.error("Error occurred due to : " + exp.getMessage());
            throw exp;
        } finally {
            AUTHENTICATION_PROCESS_LOGGER.debug("--------------- ||| Encryption process has been completed ||| ---------------");
            AUTHENTICATION_PROCESS_LOGGER.debug("#############################################################################");
            System.out.println("\n");
        }
    }

    private String encryptUserCredentials(IEncryptionProcessors blowFishEncryption, String userCredentials) throws Exception {
        System.out.println("\n");
        AUTHENTICATION_PROCESS_LOGGER.debug("--------------- ||| Encryption process has been started ||| ---------------");
        AUTHENTICATION_PROCESS_LOGGER.debug("###########################################################################");
        try {
            return blowFishEncryption.encrypt(userCredentials);
        } catch (InvalidKeyException | UnsupportedEncodingException | IllegalBlockSizeException |
                 BadPaddingException exp) {
            AUTHENTICATION_PROCESS_LOGGER.error("Error occurred due to : " + exp.getMessage());
            throw exp;
        } finally {
            AUTHENTICATION_PROCESS_LOGGER.debug("--------------- ||| Encryption process has been completed ||| ---------------");
            AUTHENTICATION_PROCESS_LOGGER.debug("#############################################################################");
            System.out.println("\n");
        }
    }

    public String getUserId() {
//        AUTHENTICATION_PROCESS_LOGGER.debug("Getting user id");
        return userId;
    }

    private void setUserId(String userId) {
//        AUTHENTICATION_PROCESS_LOGGER.debug("Searching for user id");
        this.userId = userId;
    }

    public String getPassword() {
//        AUTHENTICATION_PROCESS_LOGGER.debug("Getting user password");
        return password;
    }

    private void setPassword(String password) {
//        AUTHENTICATION_PROCESS_LOGGER.debug("Searching for user password");
        this.password = password;
    }

    public String getHost() {
//        AUTHENTICATION_PROCESS_LOGGER.debug("Getting application host");
        return host;
    }

    private void setHost(String host) {
//        AUTHENTICATION_PROCESS_LOGGER.debug("Searching for application host");
        this.host = host;
    }

    public String getIsCasEnvironment() {
//        AUTHENTICATION_PROCESS_LOGGER.debug("Getting type of application");
        return isCasEnvironment;
    }

    private void setIsCasEnvironment(String isCasEnvironment) {
//        AUTHENTICATION_PROCESS_LOGGER.debug("Searching for type of application");
        this.isCasEnvironment = isCasEnvironment;
    }

    private String getEncryptionSalt() {
        return encryptionSalt;
    }

    private void setEncryptionSalt(String encryptionSalt) {
        this.encryptionSalt = encryptionSalt;
    }
}
