/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.app.service.controller.export.expansions;

import com.bjit.common.rest.app.service.controller.export.contracts.IBomExpand;
import com.bjit.common.rest.app.service.controller.export.contracts.IExpand;
import com.bjit.common.rest.app.service.controller.export.model.BomModel;
import com.bjit.mapper.mapproject.builder.MapperBuilder;
import com.bjit.mapper.mapproject.xml_mapping_model.XmlMapElementAttribute;
import com.bjit.mapper.mapproject.xml_mapping_model.XmlMapElementBOMRelationship;
import com.bjit.mapper.mapproject.xml_mapping_model.XmlMapElementObject;
import com.bjit.mapper.mapproject.xml_mapping_model.XmlMapElementObjects;
import java.util.HashMap;
import matrix.db.BusinessObject;
import matrix.db.Context;
import matrix.db.ExpansionWithSelect;
import matrix.util.MatrixException;
import matrix.util.Pattern;
import matrix.util.SelectList;
import org.apache.log4j.Logger;

/**
 *
 * @author BJIT
 */
public class BomExpand implements IBomExpand {

    private BomModel mapper;

    private BomModel getMapper() {
        return mapper;
    }

    private void setMapper(BomModel mapper) {
        this.mapper = mapper;
    }

    private static final Logger BOM_EXPAND_LOGGER = Logger.getLogger(BomExpand.class);

    @Override
    public IExpand __init__(String mapFileType, MapperBuilder mapperBuilder, String mapFileDirectory) throws Exception {
        try {
            BOM_EXPAND_LOGGER.info("Initializing BomModel object");
            this.setMapper((BomModel) mapperBuilder.getMapper(mapFileType, BomModel.class, mapFileDirectory));
            return this;
        } catch (Exception exp) {
            BOM_EXPAND_LOGGER.error(exp);
            throw exp;
        }
    }

    @Override
    public ExpansionWithSelect getExpandedData(Context context, BusinessObject businessObject) throws Exception {
        try {
            BOM_EXPAND_LOGGER.info("Expanding data of business object : " + businessObject.getName());
            XmlMapElementObjects xmlMapElementObjects = this.getMapper().getXmlMapElementObjects();
            return businessObject.expandSelect(context,
                    getRelationshipPattern().getPattern(),
                    getTypePattern().getPattern(),
                    getBusTypeStatement(),
                    getRelationshipStatement(),
                    xmlMapElementObjects.getExpandUp(),
                    xmlMapElementObjects.getExpandDown(),
                    xmlMapElementObjects.getExpandLevel().shortValue(),
                    xmlMapElementObjects.getExpandBusWhereClause(),
                    xmlMapElementObjects.getExpandRelWhereClause(), false);
        } catch (MatrixException exp) {
            BOM_EXPAND_LOGGER.error(exp);
            throw exp;
        }
    }

    @Override
    public Pattern getTypePattern() {
        try {
            BOM_EXPAND_LOGGER.info("'Type Pattern' is being readied");
            StringBuilder typePatternBuilder = new StringBuilder();
            this.getMapper().getXmlMapElementObjects().getXmlMapElementObject().forEach((XmlMapElementObject xmlElementObject) -> {
                String typeName = xmlElementObject.getType();
                BOM_EXPAND_LOGGER.debug("Type name is : " + (typeName.contains(" ") ? "'Name is empty'" : typeName));

                if (typeName.contains(" ")) {
                    typePatternBuilder.append("\"").append(typeName).append("\"").append(",");
                } else {
                    typePatternBuilder.append(typeName).append(",");
                }
            });

            int patternLength = typePatternBuilder.length();
            if (patternLength > 0) {
                typePatternBuilder.deleteCharAt(patternLength - 1);
                return new Pattern(typePatternBuilder.toString());
            }
            return new Pattern("*");
        } catch (Exception exp) {
            BOM_EXPAND_LOGGER.error(exp);
            throw exp;
        }

    }

    @Override
    public Pattern getRelationshipPattern() {
        try {
            BOM_EXPAND_LOGGER.info("'Relationship Pattern' is being readied");
            StringBuilder relationshipPatternBuilder = new StringBuilder();
            this.getMapper().getXmlMapElementBOMRelationships().getXmlMapElementBOMRelationship().forEach((XmlMapElementBOMRelationship relationship) -> {
                String relationshipName = relationship.getRelName();
                BOM_EXPAND_LOGGER.debug("Type name is : " + (relationshipName.contains(" ") ? "'Name is empty'" : relationshipName));

                if (relationshipName.contains(" ")) {
                    relationshipPatternBuilder.append("\"").append(relationshipName).append("\"").append(",");
                } else {
                    relationshipPatternBuilder.append(relationshipName).append(",");
                }
            });

            int patternLength = relationshipPatternBuilder.length();
            if (patternLength > 0) {
                relationshipPatternBuilder.deleteCharAt(patternLength - 1);
                return new Pattern(relationshipPatternBuilder.toString());
            }
            return new Pattern("*");
        } catch (Exception exp) {
            BOM_EXPAND_LOGGER.error(exp);
            throw exp;
        }
    }

    @Override
    public HashMap<String, String> getElementSelectables() {
        try {
            BOM_EXPAND_LOGGER.info("Getting element selectables");
            HashMap<String, String> elementSelectables = new HashMap<>();
            this.getMapper().getXmlMapElementObjects().getXmlMapElementObject().forEach((XmlMapElementObject elementObject) -> {
                elementObject.getXmlMapElementAttributes().getXmlMapElementAttribute().forEach((XmlMapElementAttribute elementAttribute) -> {
                    elementSelectables.put(elementAttribute.getSelectable(), elementAttribute.getFieldLabel());
                });
            });

            BOM_EXPAND_LOGGER.debug("Element selectables : " + elementSelectables);
            return elementSelectables;
        } catch (Exception exp) {
            BOM_EXPAND_LOGGER.error(exp);
            throw exp;
        }
    }

    @Override
    public HashMap<String, String> getRelationshipSelectables() {
        try {
            BOM_EXPAND_LOGGER.info("Getting relationship selectables");
            HashMap<String, String> relationshipSelectables = new HashMap<>();
            this.getMapper().getXmlMapElementBOMRelationships().getXmlMapElementBOMRelationship().forEach((XmlMapElementBOMRelationship elementRelationship) -> {
                elementRelationship.getXmlMapElementAttributes().getXmlMapElementAttribute().forEach((XmlMapElementAttribute elementAttribute) -> {
                    relationshipSelectables.put(elementAttribute.getSelectable(), elementAttribute.getFieldLabel());
                });
            });

            BOM_EXPAND_LOGGER.debug("Relation Selectables : " + relationshipSelectables);
            return relationshipSelectables;
        } catch (Exception exp) {
            BOM_EXPAND_LOGGER.error(exp);
            throw exp;
        }
    }

    @Override
    public SelectList getBusTypeStatement() {
        try {
            BOM_EXPAND_LOGGER.info("Getting bus select statement");
            SelectList businessObjectAttributeSelectList = new SelectList();
            getElementSelectables().forEach((String key, String value) -> {
                businessObjectAttributeSelectList.add(key);
            });

            BOM_EXPAND_LOGGER.debug("Bus select list : " + businessObjectAttributeSelectList);
            return businessObjectAttributeSelectList;
        } catch (Exception exp) {
            BOM_EXPAND_LOGGER.error(exp);
            throw exp;
        }
    }

    @Override
    public SelectList getRelationshipStatement() {
        try {
            BOM_EXPAND_LOGGER.info("Getting relationship select statement");
            SelectList relationAttributeSelectList = new SelectList();
            getRelationshipSelectables().forEach((String key, String value) -> {
                relationAttributeSelectList.add(key);
            });

            BOM_EXPAND_LOGGER.debug("Relationship select list : " + relationAttributeSelectList);
            return relationAttributeSelectList;
        } catch (Exception exp) {
            BOM_EXPAND_LOGGER.error(exp);
            throw exp;
        }
    }
}
