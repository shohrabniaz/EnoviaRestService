package com.bjit.common.rest.app.service.model.extension;

/**
 *
 * @author Mashuk
 */
public class ResponseMessageExtensionBean {
    
    private String extension;
    private String errorMessage;

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        String exceptionInfo = "Exception: ";
        if (errorMessage.contains(exceptionInfo))
            errorMessage = errorMessage.substring(errorMessage.indexOf(exceptionInfo) + exceptionInfo.length());
        this.errorMessage = errorMessage;
    }

}
