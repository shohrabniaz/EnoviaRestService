/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.bjit.common.rest.app.service.enovia_pdm.models;

import com.bjit.common.rest.app.service.model.tnr.TNR;
import java.util.HashMap;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 *
 * @author Omour Faruq
 */
@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
@ToString
public class ChildData {
    protected TNR tnr;
    protected String owner;
    protected String id;
    protected HashMap<String, String> propertyMap;
    protected HashMap<String, String> attributeMap;
}
