/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.app.service.comos.mapper_processor;

import com.bjit.common.rest.app.service.controller.item.mapper_processors.CommonItemMapperProcessor;
import com.bjit.common.rest.app.service.model.createobject.CreateObjectBean;
import com.bjit.common.rest.app.service.utilities.CommonSearch;
import com.bjit.ewc18x.utils.PropertyReader;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import matrix.db.Context;
import org.apache.log4j.Logger;

/**
 *
 * @author BJIT
 */
public class ComosItemMapperProcessor extends CommonItemMapperProcessor {

    private final Logger COMOS_ITEM_MAPPER_PROCESSOR_LOGGER = Logger.getLogger(ComosItemMapperProcessor.class);

    @Override
    protected Boolean searchItemsExistence(CreateObjectBean createObjectBean, Context context) {
        String comosType = createObjectBean.getTnr().getType();
        setV6Type(createObjectBean, comosType);
        
        Boolean isUpdatable = Boolean.FALSE;
        try {
            if (createObjectBean.getIsAutoName()) {
                return false;
            }

            HashMap<String, String> whereClausesMap = new HashMap();
            whereClausesMap.put("type", createObjectBean.getTnr().getType());
            CommonSearch commonSearch = new CommonSearch();
            List<HashMap<String, String>> searchItem = commonSearch.searchItem(context, createObjectBean.getTnr(), whereClausesMap);

            String objectId = searchItem.get(0).get("id");
            this.commonItemParameters.setObjectId(objectId);

            isUpdatable = !searchItem.isEmpty();
            this.commonItemParameters.setItemExists(isUpdatable);
        } catch (Exception exp) {
            COMOS_ITEM_MAPPER_PROCESSOR_LOGGER.error(exp.getMessage());
        }
        finally{
            createObjectBean.getTnr().setType(comosType);
        }
        return isUpdatable;
    }
    
    private void setV6Type(CreateObjectBean createObjectBean, String comosType){
        String v6Type = getV6TypeFromComosType(comosType);
        createObjectBean.getTnr().setType(v6Type);
        
        String comosRevision = Optional.ofNullable(createObjectBean.getTnr().getRevision()).filter(revision -> !revision.isEmpty()).orElse("1.1");
        createObjectBean.getTnr().setRevision(comosRevision);
    }

    protected String getV6TypeFromComosType(String comosType) {
        String v6Type = PropertyReader.getProperty("import.type.map.Enovia.comos." + comosType);
        return v6Type;
    }
}
