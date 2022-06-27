/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.app.service.utilities;

/**
 *
 * @author BJIT
 */
public enum Conditionals {
    EQUALS("=="),
    NOT_EQUALS("!="),
    GREATER_THAN(">"),
    GREATER_THAN_OR_EQUAL(">="),
    LESSER_THAN("<"),
    LESSER_THAN_OR_EQUAL("<="),
    AND("&&"),
    OR("||");

    public final String condition;

    private Conditionals(String label) {
        this.condition = label;
    }
}
