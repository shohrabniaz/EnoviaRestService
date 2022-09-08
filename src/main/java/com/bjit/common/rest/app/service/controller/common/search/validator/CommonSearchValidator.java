package com.bjit.common.rest.app.service.controller.common.search.validator;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.bjit.common.rest.app.service.context.CreateContext;
import com.bjit.common.rest.app.service.controller.authentication.AuthenticationProcess;
import com.bjit.common.rest.app.service.controller.authentication.AuthenticationUserModel;
import com.bjit.common.rest.app.service.controller.authentication.token.HMAC.WebToken;
import com.bjit.common.rest.app.service.filter.HTTPServletResponseHandler;
import com.bjit.common.rest.app.service.model.common.ItemSearchParamBean;
import com.bjit.common.rest.app.service.model.common.ItemSearchRequestBean;
import com.bjit.common.rest.app.service.model.common.ItemSearchResponseBean;
import com.bjit.common.rest.app.service.payload.common_response.CustomResponseBuilder;
import com.bjit.common.rest.app.service.payload.common_response.IResponse;
import com.bjit.common.rest.app.service.payload.common_response.Status;
import com.bjit.common.rest.app.service.utilities.CommonPropertyReader;
import com.bjit.common.rest.app.service.utilities.DateTimeUtils;
import com.bjit.common.rest.app.service.utilities.NullOrEmptyChecker;
import com.bjit.ewc18x.utils.PropertyReader;
import java.io.IOException;
import java.text.MessageFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import matrix.db.Context;
import matrix.util.MatrixException;
import org.apache.log4j.Logger;
import org.apache.log4j.Priority;
import org.springframework.web.util.ContentCachingResponseWrapper;

/**
 *
 * @author BJIT
 */
public class CommonSearchValidator {
    private static final Logger COMMON_SEARCH_VALIDATOR = Logger.getLogger(CommonSearchValidator.class);
    
    static synchronized public void validateExternalRequester(ItemSearchRequestBean searchRequest) {
        if(!NullOrEmptyChecker.isNullOrEmpty(searchRequest.getRequester())) {
            Map<String, String> searchRequesterMap = PropertyReader.getProperties("common.search.requester", Boolean.TRUE);
            if(!NullOrEmptyChecker.isNullOrEmpty(searchRequesterMap)) {
                if(!searchRequesterMap.containsKey(searchRequest.getRequester().toLowerCase())) {
                    throw new RuntimeException(MessageFormat.format(PropertyReader.getProperty("common.search.requester.error"), searchRequest.getRequester()));
                }
            }
            else {
                throw new NullPointerException("Search request validation error: Couldn't retrive valid requester map from properties");
            }
        }
        else {
            throw new NullPointerException("'Requester' tag is missing or empty!");
        }
    }
    
    static synchronized public void validateParams(ItemSearchRequestBean searchRequest) {
        if(NullOrEmptyChecker.isNullOrEmpty(searchRequest.getParams())) {
            throw new NullPointerException(PropertyReader.getProperty("common.search.param.list.error"));
        }
    }
    
    static synchronized public void validateName(String name) throws RuntimeException {
        if(name.equals("^") || name.contains("^")) {
            throw new RuntimeException(PropertyReader.getProperty("common.search.name.chars.error"));
        }
        if(name.equals("%") || name.contains("%")) {
            throw new RuntimeException(PropertyReader.getProperty("common.search.name.chars.error"));
        }
        if(name.equals("*")) {
            throw new RuntimeException(PropertyReader.getProperty("common.search.name.asterisk.only.error"));
        }
//        if(name.contains("*")) {
//            throw new RuntimeException(PropertyReader.getProperty("common.search.name.asterisk.error"));
//        }
        if(name.length() > Integer.parseInt(PropertyReader.getProperty("common.search.name.length"))) {
            throw new RuntimeException(PropertyReader.getProperty("common.search.name.length.error"));
        }
        if(name.contains(" ")) {
            throw new RuntimeException(PropertyReader.getProperty("common.search.name.space.error"));
        }
    }
    
    static synchronized public void validateNameMatchList(ItemSearchRequestBean searchRequest, List<ItemSearchResponseBean> results) throws RuntimeException {
        List<ItemSearchParamBean> matchList = new ArrayList<>(searchRequest.getParams());
        searchRequest.getParams().forEach(param -> {
            if(!NullOrEmptyChecker.isNullOrEmpty(param.getName())) {
                String name = param.getName();
                try {
                    if(name.equals("^") || name.contains("^")) {
                        throw new RuntimeException(PropertyReader.getProperty("common.search.name.chars.error"));
                    }
                    if(name.equals("%") || name.contains("%")) {
                        throw new RuntimeException(PropertyReader.getProperty("common.search.name.chars.error"));
                    }
                    if(name.equals("*")) {
                        throw new RuntimeException(PropertyReader.getProperty("common.search.name.asterisk.only.error"));
                    }
//                    if(name.contains("*")) {
//                        throw new RuntimeException(PropertyReader.getProperty("common.search.name.asterisk.error"));
//                    }
                    if(name.length() > Integer.parseInt(PropertyReader.getProperty("common.search.name.length"))) {
                        throw new RuntimeException(PropertyReader.getProperty("common.search.name.length.error"));
                    }
                    if(name.contains(" ")) {
                        throw new RuntimeException(PropertyReader.getProperty("common.search.name.space.error"));
                    }
                } catch (RuntimeException ex) {
                    COMMON_SEARCH_VALIDATOR.error(param.getName() + " Search Error : " + ex.getMessage());
                    results.add(new ItemSearchResponseBean(param.getName(), "Not Found"));
                    matchList.remove(param);
                }
            } else {
                matchList.remove(param);
            }
        });
        searchRequest.setParams(matchList);
    }
    
    static synchronized public Context validateToken(String token) throws Exception{
        COMMON_SEARCH_VALIDATOR.info("Token validation in Common Validator");
        Instant serviceStartTime = Instant.now();
        Context context = null;
        try {
            if (isNullOrEmpty(token)) {
                COMMON_SEARCH_VALIDATOR.fatal("Token can't be null or empty. Please provide a valid token");
                throw new NullPointerException("Please provide a valid token");
            }

            Instant contextCreatingStartTime = Instant.now();
            // Verify token
            WebToken webToken = new WebToken();
            token = webToken.VerifyToken(token);
            COMMON_SEARCH_VALIDATOR.info("Token verified successfully");
            // Find user credentials from token and create context and add to the request-context
            AuthenticationUserModel userCredentialsModel = getUserCredentials(token, webToken);
            context = generateContext(userCredentialsModel);

            if (context == null) {
                COMMON_SEARCH_VALIDATOR.fatal("Couldn't generate context");
                throw new NullPointerException("Couldn't generate context");
            }
            
            Instant serviceEndTime = Instant.now();
            long duration = DateTimeUtils.getDuration(serviceStartTime, serviceEndTime);
            COMMON_SEARCH_VALIDATOR.log(Boolean.parseBoolean(new CommonPropertyReader().getPropertyValue("log.statistics.info")) ? Priority.INFO : Priority.DEBUG, "Time Statistics : Auth has taken '" + duration + "' milli-seconds");
            COMMON_SEARCH_VALIDATOR.info("######################################## completed ########################################");
            
            return context;
            
        } catch (JWTVerificationException | IllegalArgumentException exp) {
            COMMON_SEARCH_VALIDATOR.fatal(exp.getMessage());
            throw new Exception("Invalid Token. Please provide a valid token");
        } catch (MatrixException exp) {
            COMMON_SEARCH_VALIDATOR.fatal(exp.getMessage());
            throw new Exception("Matrix Exception : " + exp.getMessage());
        } catch (NullPointerException exp) {
            COMMON_SEARCH_VALIDATOR.fatal(exp.getMessage());
            throw new Exception("Null Pointer Exception : " + exp.getMessage());
        } catch (Exception exp) {
            COMMON_SEARCH_VALIDATOR.fatal(exp.getMessage());
            throw new Exception("Exception : " + exp.getMessage());
        }
    }
    
    static synchronized private Boolean isNullOrEmpty(String value) {
        return value == null || value.equals("");
    }
    
    static synchronized private AuthenticationUserModel getUserCredentials(String token, WebToken webToken) throws Exception {
        try {
            AuthenticationProcess userAuthentication = new AuthenticationProcess();
            String authenticationModel = webToken.GetPropertyFromToken(token, "Model");
            AuthenticationUserModel authenticateUser = userAuthentication.getAuthenticUserModel(authenticationModel);
            return authenticateUser;
        } catch (Exception exp) {
            COMMON_SEARCH_VALIDATOR.error(exp.getMessage());
            throw exp;
        }
    }
    
    static synchronized private Context generateContext(AuthenticationUserModel userCredentialsModel) throws Exception {
        try {
            CreateContext generateContext = new CreateContext();
            Context context = generateContext.getContext(userCredentialsModel.getUserId(), userCredentialsModel.getPassword(), userCredentialsModel.getHost(), Boolean.parseBoolean(userCredentialsModel.getIsCasContext()));
            return context;
        } catch (Exception exp) {
            COMMON_SEARCH_VALIDATOR.error(exp.getMessage());
            throw exp;
        }
    }
}