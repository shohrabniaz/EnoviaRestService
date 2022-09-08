/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.ewc18x.model;

/**
 *
 * @author Kayum-603
 */
public class ExpandObjectRequestForm {

	private final static long serialVersionUID = 1L;
	protected ExpandObjectForm expndObj;

	/**
	 * Gets the value of the expndObj property.
	 *
	 * @return possible object is {@link ExpandObject }
	 *
	 */
	public ExpandObjectForm getExpndObj() {
		return expndObj;
	}

	/**
	 * Sets the value of the expndObj property.
	 *
	 * @param value allowed object is {@link ExpandObject }
	 *
	 */
	public void setExpndObj(ExpandObjectForm value) {
		this.expndObj = value;
	}

    @Override
    public String toString() {
        return "ExpandObjectRequest{" + "expndObj=" + expndObj + '}';
    }

}
