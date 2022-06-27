/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.app.service.controller.item.validators;

import static com.bjit.common.rest.app.service.controller.item.validators.CommonItemValidator.getMAP_DIRECTORY;
import static com.bjit.common.rest.app.service.controller.item.validators.CommonItemValidator.setMAP_DIRECTORY;
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
public class AtonMVValidator extends CommonItemValidator
{

    private static final org.apache.log4j.Logger MV_VALIDATOR_LOGGER = org.apache.log4j.Logger.getLogger(AtonMVValidator.class);

    private final String modelVersionType = "Products";
    @Override
    public CommonItemParameters validateItem(
            CommonItemParameters commonItemParameters) throws NullPointerException, Exception
    {
        TNR tnr = new TNR(modelVersionType, commonItemParameters.getCreateObjectBean().getTnr().getName(), "*");
        setTnr(tnr);
        commonItemParameters.setTnr(tnr);
        setCommonItemParameters(commonItemParameters);

        validateCreateObjectBean(commonItemParameters.getCreateObjectBean(), commonItemParameters.getBusinessObjectOperations(), tnr);
        String xmlMapDirectory = getXmlMapDirectory(tnr);
        MV_VALIDATOR_LOGGER.info("Xml mapper location : " + xmlMapDirectory);

        commonItemParameters.setXmlMapDirectory(xmlMapDirectory);

        return commonItemParameters;
    }

    public void validateCreateObjectBean(CreateObjectBean createObjectBean,
                                         BusinessObjectOperations businessObjectOperations,
                                         TNR tnr) throws NullPointerException
    {
        Optional.ofNullable(createObjectBean)
                .orElseThrow(() -> new NullPointerException("Invalid request. Item not found"));

        Optional.ofNullable(createObjectBean.getAttributes())
                .filter(itemAttributes -> !itemAttributes.isEmpty())
                .orElseThrow(() -> new NullPointerException("Attribute list is empty"));

//        businessObjectOperations.validateTNR(createObjectBean.getTnr(), !createObjectBean.getIsAutoName(), !createObjectBean.getIsAutoName());
        businessObjectOperations.validateTNR(tnr, Boolean.TRUE, Boolean.FALSE);

        validateAttributes(createObjectBean);
    }

    public String getXmlMapDirectory(TNR tnr) throws NullPointerException
    {
        this.setSource(getCommonItemParameters().getSource().toLowerCase());
        String type = tnr.getType();

        String xmlMapperName = Optional.ofNullable(this.getSource())
                .filter(src -> !src.isEmpty())
                .isPresent() ? this.getSource() + "." + type : "common";

        setMAP_DIRECTORY(Optional.ofNullable(getMAP_DIRECTORY())
                .orElse(PropertyReader.getProperties("import.object.erp.map", true)));

        String xmlMapperDirectory = getMAP_DIRECTORY().get(xmlMapperName);

        return Optional.ofNullable(xmlMapperDirectory)
                .filter(directory -> !directory.isEmpty())
                .orElseThrow(()
                        -> new NullPointerException("System could not recognize " + this.getTnr().getType() + " for source : " + this.getSource()));
    }

    private void validateAttributes(CreateObjectBean createObjectBean)
    {
        HashMap<String, String> attr = createObjectBean.getAttributes();
        attr.entrySet().removeIf(entry
                -> entry.getValue() == null || entry.getValue().equals(""));
    }
}
