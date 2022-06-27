/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.app.service.controller.item.validators;

import com.bjit.common.rest.app.service.controller.item.interfaces.IItemValidator;
import com.bjit.common.rest.app.service.model.createobject.CreateObjectBean;
import com.bjit.common.rest.app.service.model.tnr.TNR;
import com.bjit.common.rest.app.service.utilities.BusinessObjectOperations;
import com.bjit.ewc18x.utils.PropertyReader;
import java.util.HashMap;
import java.util.Optional;

/**
 *
 * @author BJIT
 */
public class CommonItemValidator implements IItemValidator {

    private static final org.apache.log4j.Logger COMMON_ITEM_VALIDATOR_LOGGER = org.apache.log4j.Logger.getLogger(CommonItemValidator.class);

    private CommonItemParameters commonItemParameters;
    private TNR tnr;
    private String source;
    private static HashMap<String, String> MAP_DIRECTORY;

    @Override
    public CommonItemParameters validateItem(CommonItemParameters commonItemParameters) throws NullPointerException, Exception {
        setCommonItemParameters(commonItemParameters);
        setTnr(commonItemParameters.getCreateObjectBean().getTnr());

        validateCreateObjectBean(commonItemParameters.getCreateObjectBean(), commonItemParameters.getBusinessObjectOperations());
        String xmlMapDirectory = getXmlMapDirectory();
        COMMON_ITEM_VALIDATOR_LOGGER.info("Xml mapper location : " + xmlMapDirectory);

        commonItemParameters.setXmlMapDirectory(xmlMapDirectory);

        return commonItemParameters;
    }

    public void validateCreateObjectBean(CreateObjectBean createObjectBean, BusinessObjectOperations businessObjectOperations) throws NullPointerException {
        Optional.ofNullable(createObjectBean)
                .orElseThrow(() -> new NullPointerException("Invalid request. Item not found"));

        Optional.ofNullable(createObjectBean.getAttributes())
                .filter(itemAttributes -> !itemAttributes.isEmpty())
                .orElseThrow(() -> new NullPointerException("Attribute list is empty"));

//        businessObjectOperations.validateTNR(createObjectBean.getTnr(), !createObjectBean.getIsAutoName(), !createObjectBean.getIsAutoName());
        businessObjectOperations.validateTNR(createObjectBean.getTnr(), !createObjectBean.getIsAutoName(), Boolean.FALSE);
    }

    public String getXmlMapDirectory() throws NullPointerException {
        this.setSource(getCommonItemParameters().getSource().toLowerCase());
        String type = getCommonItemParameters().getCreateObjectBean().getTnr().getType();

        String xmlMapperName = Optional.ofNullable(this.getSource())
                .filter(src -> !src.isEmpty())
                .isPresent() ? this.getSource() + "." + type : "common";

        setMAP_DIRECTORY(Optional.ofNullable(getMAP_DIRECTORY())
                .orElse(PropertyReader.getProperties("import.object.erp.map", true)));

        String xmlMapperDirectory = getMAP_DIRECTORY().get(xmlMapperName);

        return Optional.ofNullable(xmlMapperDirectory)
                .filter(directory -> !directory.isEmpty())
                .orElseThrow(() -> new NullPointerException("System could not recognize " + this.getTnr().getType() + " for source : " + this.getSource()));
    }

    public CommonItemParameters getCommonItemParameters() {
        return commonItemParameters;
    }

    public void setCommonItemParameters(CommonItemParameters commonItemParameters) {
        this.commonItemParameters = commonItemParameters;
    }

    public TNR getTnr() {
        return tnr;
    }

    public void setTnr(TNR tnr) {
        this.tnr = tnr;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public static HashMap<String, String> getMAP_DIRECTORY() {
        return MAP_DIRECTORY;
    }

    public static void setMAP_DIRECTORY(HashMap<String, String> MAP_DIRECTORY) {
        CommonItemValidator.MAP_DIRECTORY = MAP_DIRECTORY;
    }
}
