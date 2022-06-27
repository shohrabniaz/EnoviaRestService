/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.app.service.controller.export.contracts;

import java.util.HashMap;
import matrix.util.Pattern;
import matrix.util.SelectList;

/**
 *
 * @author BJIT
 */
public interface IBomExpand extends IExpand {
    Pattern getTypePattern();
    Pattern getRelationshipPattern();
    HashMap<String, String> getElementSelectables();
    HashMap<String, String> getRelationshipSelectables();
    SelectList getBusTypeStatement();
    SelectList getRelationshipStatement();
}
