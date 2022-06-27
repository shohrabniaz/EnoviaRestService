package com.bjit.common.rest.pdm_enovia.bom.comparison.exception;
/**
 * @author Ashikur Rahman / BJIT
 */
public class ParsingException extends Exception {
	private static final long serialVersionUID = -1888042708424853498L;
	
	private String message;

	public ParsingException(String message) {
		super();
		this.message = message;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}