/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.app.service.controller.bom.expand;

import com.bjit.common.rest.app.service.controller.bom.model.ExpandedModel;
import com.bjit.common.rest.app.service.model.tnr.TNR;
import java.util.ArrayList;
import java.util.List;
import matrix.db.BusinessObject;
import matrix.db.Context;
import matrix.db.ExpansionWithSelect;
import matrix.db.RelationshipWithSelect;
import matrix.db.RelationshipWithSelectList;
import matrix.util.MatrixException;
import matrix.util.StringList;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

/**
 *
 * @author BJIT
 */
@Component
//@RequestScope
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class CommonBOMExpand {

    private static final org.apache.log4j.Logger COMMON_BOM_EXPAND_LOGGER = org.apache.log4j.Logger.getLogger(CommonBOMExpand.class);

    public List<TNR> expand(Context context, String rootItemId, String relationshipType, String itemType, short level) throws Exception {
        StringList objSelect = new StringList();
        objSelect.add("type");
        objSelect.add("name");
        objSelect.add("revision");
        objSelect.add("id");
        StringList relSelect = new StringList();
        relSelect.add("name");
        relSelect.add("id");

        BusinessObject rootBusinessObject = new BusinessObject(rootItemId);
        rootBusinessObject.open(context);
        ExpansionWithSelect expandSelect = rootBusinessObject.expandSelect(context, relationshipType, itemType, objSelect, relSelect, false, true, level);

        RelationshipWithSelectList relationships = expandSelect.getRelationships();

        List<TNR> childTnrList = new ArrayList<>();

        relationships.getIterator().forEach((RelationshipWithSelect relationshipWithSelect) -> {
            try {
                BusinessObject childBusinessObject = relationshipWithSelect.getTo();
                childBusinessObject.open(context);

                TNR tnr = new TNR();
                tnr.setType(childBusinessObject.getTypeName());
                tnr.setName(childBusinessObject.getName());
                tnr.setRevision(childBusinessObject.getRevision());

                childTnrList.add(tnr);
            } catch (MatrixException ex) {
                COMMON_BOM_EXPAND_LOGGER.error(ex);
            }
        });

        return childTnrList;
    }

    public List<ExpandedModel> expandItem(Context context, String rootItemId, String relationshipType, String itemType, short level) throws Exception {
        StringList objSelect = new StringList();
        objSelect.add("type");
        objSelect.add("name");
        objSelect.add("revision");
        objSelect.add("id");
        StringList relSelect = new StringList();
        relSelect.add("name");
        relSelect.add("id");

        BusinessObject rootBusinessObject = new BusinessObject(rootItemId);
        rootBusinessObject.open(context);
        ExpansionWithSelect expandSelect = rootBusinessObject.expandSelect(context, relationshipType, itemType, objSelect, relSelect, false, true, level);

        RelationshipWithSelectList relationships = expandSelect.getRelationships();

        List<ExpandedModel> childExpandedList = new ArrayList<>();

        relationships.getIterator().forEach((RelationshipWithSelect relationshipWithSelect) -> {
            try {
                BusinessObject childBusinessObject = relationshipWithSelect.getTo();
                childBusinessObject.open(context);

                TNR childTnr = new TNR(childBusinessObject.getTypeName(), childBusinessObject.getName(), childBusinessObject.getRevision());

                relationshipWithSelect.open(context);

                ExpandedModel childExpandedModel = new ExpandedModel(childTnr, childBusinessObject.getObjectId(), relationshipWithSelect.getTypeName(), relationshipWithSelect.getName());
                relationshipWithSelect.close(context);

                childExpandedList.add(childExpandedModel);
            } catch (MatrixException exp) {
                COMMON_BOM_EXPAND_LOGGER.error(exp);
                throw new RuntimeException(exp);
            }
        });

        return childExpandedList;
    }
}
