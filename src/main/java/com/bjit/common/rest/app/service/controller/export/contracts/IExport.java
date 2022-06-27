/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.app.service.controller.export.contracts;

import java.util.List;
import java.util.Map;
import matrix.db.ExpansionWithSelect;

/**
 *
 * @author BJIT
 */
public interface IExport {
    IExport __init__(IExpand expandObject, ExpansionWithSelect expandedObject);
    List<Map<String, String>> process();
}