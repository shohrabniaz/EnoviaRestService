package com.bjit.common.rest.app.service.dsservice.models.itemduplication;

public class CreatedItemError {

    private String errorCode;
    private String errorMessage;

    /**
     * @return the errorCode
     */
    public String getErrorCode() {
        return errorCode;
    }

    /**
     * @param errorCode the errorCode to set
     */
    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    /**
     * @return the errorMessage
     */
    public String getErrorMessage() {
        return errorMessage;
    }

    /**
     * @param errorMessage the errorMessage to set
     */
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String toString() {
        StringBuilder strBuilder = new StringBuilder();
        strBuilder.append("ErrorMessage")
                .append(this.getErrorMessage())
                .append(".");
        return strBuilder.toString();
    }
}
