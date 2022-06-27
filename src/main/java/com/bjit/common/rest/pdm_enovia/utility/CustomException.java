/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.pdm_enovia.utility;

/**
 *
 * @author Administrator
 */
public class CustomException extends Exception {

	private static final long serialVersionUID = 1L;

	/**
     *
     * @param message
     */
    public CustomException(String message) {
        super(message);
    }

}
