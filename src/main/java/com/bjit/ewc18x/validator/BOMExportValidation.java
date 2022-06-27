package com.bjit.ewc18x.validator;

import com.bjit.common.rest.app.service.controller.export.ExportBOM;
import com.bjit.ewc18x.utils.CustomException;
import com.bjit.plmkey.ws.controller.expandobject.ExpandObjectUtil;
import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import javax.servlet.http.HttpServletRequest;
import org.apache.log4j.Logger;

/**
 * This class is used for different validation during BOM Export   
 * @author Ashikur Rahman
 */
public class BOMExportValidation {
    final static Logger logger = Logger.getLogger(BOMExportValidation.class);
    private final String serviceRequestURLPattern = "^(https?)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";
    private final String mandatoryRequestParams = "name|attrs";
    private final String mandatoryAttrs = "Name|Title";
    private final String defaultType = "CreateAssembly";
    /**
     * this method validates a service call
     * basically this method checks the request url 
     * @param URL
     * @return boolean
     * @throws java.lang.Exception
     */
    private boolean isValidURL(String URL) throws Exception {
        return ExpandObjectUtil.hasPatternMatched(URL, serviceRequestURLPattern);
    }
    
    /***
     * This method validates if the request has the mandatory parameters     
     * @param requestParamMap
     * @return boolean
     * @throws Exception
     */
    private boolean isMandatoryRequestParamsFound(Map<String, String[]> requestParamMap) throws Exception {
        if (requestParamMap != null && !requestParamMap.isEmpty()) {
            List<String> mandatoryParamList = new LinkedList<>(Arrays.asList(mandatoryRequestParams.split("\\|")));
            requestParamMap.forEach((key, value) -> {
                mandatoryParamList.removeIf(p -> p.equals(key));
            });
            return mandatoryParamList.isEmpty();
        }
        return false;
    }
    
    /***
     * This method validates if the request has the mandatory parameters     
     * @param attrs
     * @return boolean
     * @throws Exception
     */
    private boolean isMandatoryAttrsFound(String attrs) throws Exception {
        if (attrs != null && attrs.length()>0) {
            List<String> mandatoryParamList = new LinkedList<>(Arrays.asList(mandatoryAttrs.split("\\|")));
            Arrays.stream(attrs.split(",")).forEach(attr -> {
                mandatoryParamList.removeIf(a -> a.equals(attr.trim()));
            });
            return mandatoryParamList.isEmpty();
        }
        return false;
    }
    
    private boolean isRequestURIValid(HttpServletRequest request, String serviceName) throws Exception {
        if(isServiceFound(serviceName)) {
            StringBuilder requestURIbuilder = new StringBuilder();
            requestURIbuilder.append(request.getContextPath()).append("/").append(serviceName);
            //return request.getRequestURI().equals(requestURIbuilder.toString());
            return true;
        }
        return false;
    }
    
    private boolean validateBOMExportServiceCall(HttpServletRequest httpRequest, String serviceName, String attributes) throws Exception {
        try {
            boolean isUrlValid = isValidURL(httpRequest.getRequestURL().toString());
            boolean isURIValid = isRequestURIValid(httpRequest, serviceName);
            boolean isMandatoryParamsFound = isMandatoryRequestParamsFound(httpRequest.getParameterMap());
            boolean isMandatoryAttrsFound = isMandatoryAttrsFound(attributes);
            if(isMandatoryParamsFound && isUrlValid && isURIValid && isMandatoryAttrsFound)
                return true;
        } catch (Exception ex) {
            java.util.logging.Logger.getLogger(ExportBOM.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
    
    public boolean validateBOMExportServiceRequest(HttpServletRequest httpRequest, String serviceName, String attributes) throws Exception {
        return validateBOMExportServiceCall(httpRequest, serviceName, attributes);
    }
    
    
    public boolean isServiceFound(String serviceName) throws Exception {
        return new File(getServicePath()+serviceName+".xml").exists();
    }

    public String getServicePath() throws Exception {
        return getClass().getResource("/services/").getPath();
    }
    
    public String generateRequestId(String requestId) {
        if(requestId == null || requestId.isEmpty()) {
            return Long.toString(System.currentTimeMillis());
        }
        return requestId;
    }
    
    public Map<String, String> getUserCredentials(HttpServletRequest request) throws Exception{
        String user = request.getHeader("user");
        String pass = request.getHeader("pass");
        return getUserCredentials(user, pass);
    }
    
    public Map<String, String> getUserCredentials(String userId, String password) throws Exception{
        Map<String, String> credentialsMap = new HashMap<>();
        if(userId == null || password == null || userId.isEmpty() || password.isEmpty())
            throw new CustomException("User name or Password cannot be empty.");
        credentialsMap.put("user", userId);
        credentialsMap.put("pass", password);
        return credentialsMap;
    }
    
    public File getServiceFile(String serviceName) throws Exception {
        serviceName = serviceName.replace(" ", "");
        return new File(getClass().getResource("/services/" + serviceName + ".xml").getFile());
    }
    
    public String getType(String type) {
        if(type == null || type.isEmpty()) {
            type = defaultType;
        }
        return type;
    }
    
    public String getReportName(String reportName) {
        if(reportName == null || reportName.isEmpty()) {
            reportName = "";
        }
        return reportName;
    }
}
