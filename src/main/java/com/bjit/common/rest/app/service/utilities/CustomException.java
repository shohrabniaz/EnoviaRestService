/*
 * Copyright 2018 BJIT Limited All rights reserved.
 */
package com.bjit.common.rest.app.service.utilities;



/**
 * 
 * @author Sudeepta
 * @developer Sudeepta
 * @version 1.0
 * @since 2018-12-14
 */
public class CustomException extends Exception {

    /** serialization token */
    private static final long serialVersionUID = 210312428976488969L;
   
    
    /** The error message */
    private String message;
    
    /** The details information of the error */
    private String details;

    /**
     * Default Constructor
     */
    public CustomException() {
        super();
    }

    /**
     * Constructor with error message
     * @param message the error message
     */
    public CustomException(String message) {
        super(message);
        this.message = message;
    }


    /**
     * Constructor with application status, error message and details information
     * @param status the enum constant with the specified numeric value
     * @param message the error message
     * @param details the details information of the error
     */
    public CustomException(String message, String details) {
        super(message);
        this.message = message;
 
        this.details = details;
    }

    @Override
    public String getMessage() {
        return message;
    }

    /**
     *
     * @return the enum constant of this type with the specified name.
     */


    /**
     *
     * @return details information of the error
     */
    public String getDetails() {
        return details;
    }
}
