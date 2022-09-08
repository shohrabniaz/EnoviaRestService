/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.app.service.controller.item.interfaces;

import com.bjit.common.rest.app.service.model.createobject.CreateObjectBean;
import com.bjit.common.rest.item_bom_import.xml_mapping_model.ItemImportMapping;
import java.util.HashMap;

/**
 *
 * @author BJIT
 */
public interface IAttributeBusinessLogic {
    HashMap<String, String> businessLogic(ItemImportMapping mapper, CreateObjectBean createObjectBean) throws Exception;
}
