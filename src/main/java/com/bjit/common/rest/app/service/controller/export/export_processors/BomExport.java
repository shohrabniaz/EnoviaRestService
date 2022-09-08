/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.app.service.controller.export.export_processors;

import com.bjit.common.rest.app.service.controller.export.contracts.IBomExpand;
import com.bjit.common.rest.app.service.controller.export.contracts.IExpand;
import com.bjit.common.rest.app.service.controller.export.contracts.IExport;
import com.bjit.common.rest.app.service.utilities.NullOrEmptyChecker;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import matrix.db.BusinessObjectWithSelect;
import matrix.db.ExpansionWithSelect;
import matrix.db.RelationshipWithSelect;
import matrix.db.RelationshipWithSelectItr;
import org.apache.log4j.Logger;

/**
 *
 * @author BJIT
 */
public class BomExport implements IExport {

    private IBomExpand bomExpand;
    private ExpansionWithSelect expandedObjectWithSelectStatements;

    private static final Logger BOM_EXPORT_LOGGER = Logger.getLogger(BomExport.class);

    public IBomExpand getBomExpand() {
        return bomExpand;
    }

    public void setBomExpand(IBomExpand bomExpand) {
        this.bomExpand = bomExpand;
    }

    private ExpansionWithSelect getExpandedObjectWithSelectStatements() {
        return expandedObjectWithSelectStatements;
    }

    private void setExpandedObjectWithSelectStatements(ExpansionWithSelect expandedObject) {
        this.expandedObjectWithSelectStatements = expandedObject;
    }

    @Override
    public IExport __init__(IExpand expandObject, ExpansionWithSelect expandedObjectWithSelect) {
        BOM_EXPORT_LOGGER.info("Initializing BOMExport");
        this.setBomExpand((IBomExpand) expandObject);
        this.setExpandedObjectWithSelectStatements(expandedObjectWithSelect);
        return this;
    }

    @Override
    public List<Map<String, String>> process() {
        try {
            BOM_EXPORT_LOGGER.info("Processing BomExport");
            BusinessObjectWithSelect rootObjectWithSelectStatements = this.getExpandedObjectWithSelectStatements().getRootWithSelect();
            List<Map<String, String>> expandedResult = new ArrayList<>();

            expandedResult = populateRootObject(rootObjectWithSelectStatements, expandedResult);

            RelationshipWithSelectItr relationshipWithSelectItr = new RelationshipWithSelectItr(this.getExpandedObjectWithSelectStatements().getRelationships());
            while (relationshipWithSelectItr.next()) {
                RelationshipWithSelect relationWithSelect = relationshipWithSelectItr.obj();
                BusinessObjectWithSelect childObjectWithSelectStatements = relationWithSelect.getTarget();
                expandedResult = populateChildObject(childObjectWithSelectStatements, relationWithSelect, expandedResult);
            }

            return expandedResult;
        } catch (Exception exp) {
            BOM_EXPORT_LOGGER.error(exp);
            throw exp;
        }
    }

    private List<Map<String, String>> populateRootObject(BusinessObjectWithSelect rootObjectWithSelectStatements, List<Map<String, String>> expandedResult) {
        try {
            BOM_EXPORT_LOGGER.info("Populating root object");
            LinkedHashMap<String, String> rootObjectProperties = addElementSelectables(rootObjectWithSelectStatements);

            rootObjectProperties.put("Rel Name", "");
            rootObjectProperties.put("Position", "");
            rootObjectProperties.put("Qty", "");

            expandedResult.add(rootObjectProperties);
            BOM_EXPORT_LOGGER.debug("Root data is : " + rootObjectProperties);
            return expandedResult;
        } catch (Exception exp) {
            BOM_EXPORT_LOGGER.error(exp);
            throw exp;
        }
    }

    private List<Map<String, String>> populateChildObject(BusinessObjectWithSelect childObjectWithSelectStatements, RelationshipWithSelect relationWithSelect, List<Map<String, String>> expandedResult) {
        try {
            BOM_EXPORT_LOGGER.info("Populating child object");
            LinkedHashMap<String, String> childObjectProperties = addElementSelectables(childObjectWithSelectStatements);

            this.getBomExpand().getRelationshipSelectables().forEach((String key, String value) -> {
                String typeRelationshipData = relationWithSelect.getSelectData(key);
                BOM_EXPORT_LOGGER.debug("Relationship key is '" + key + "' data : '" + typeRelationshipData + "' value : '" + value + "'");
                //childObjectProperties.put(value, NullOrEmptyChecker.isNullOrEmpty(typeRelationshipData) ? "" : typeRelationshipData.equalsIgnoreCase("name") ? "Rel" + value : typeRelationshipData);

                if (NullOrEmptyChecker.isNullOrEmpty(typeRelationshipData)) {
                    childObjectProperties.put(value, "");
                } else {
                    childObjectProperties.put((typeRelationshipData.equalsIgnoreCase("name") ? ("Rel" + value) : value), typeRelationshipData);
                }
            });

            BOM_EXPORT_LOGGER.debug("Child data is : " + childObjectProperties);
            expandedResult.add(childObjectProperties);
            return expandedResult;
        } catch (Exception exp) {
            BOM_EXPORT_LOGGER.error(exp);
            throw exp;
        }
    }

    private LinkedHashMap<String, String> addElementSelectables(BusinessObjectWithSelect rootObjectWithSelectStatements) {
        LinkedHashMap<String, String> objectProperties = new LinkedHashMap<>();
        this.getBomExpand().getElementSelectables().forEach((String key, String value) -> {
            String typeAttributeData = rootObjectWithSelectStatements.getSelectData(key);
            BOM_EXPORT_LOGGER.debug("Element key is '" + key + "' data : '" + typeAttributeData + "' value : '" + value + "'");
            objectProperties.put(value, NullOrEmptyChecker.isNullOrEmpty(typeAttributeData) ? "" : typeAttributeData);
        });
        return objectProperties;
    }
}
