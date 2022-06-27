/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.ewc18x.message;

/**
 *
 * @author Kayum-603
 */
public enum MessageType implements EWMessageType{
      /**
     * message type is <code>success</code>.
     */
    SUCCESS("success"),
    /**
     * message type is <code>info</code>.
     */
    INFO("info"),

    /**
     * message type is <code>warning</code>.
     */
    WARNING("warning"),
    /**
     * message type is <code>error</code>.
     */
    ERROR("danger"),
    /**
     * message type is <code>danger</code>.
     */
    DANGER("danger");

    /**
     * message type
     */
    private final String type;

    /**
     * Create EWMessageType instance<br>
     * @param type message type
     */
    private MessageType(String type) {
        this.type = type;
    }

    @Override
    public String getValue() {
        return this.type;
    }

    /**
     * <p>
     * returns message type
     * </p>
     * @see java.lang.Enum#toString()
     */
    @Override
    public String toString() {
        return this.type;
    }
}
