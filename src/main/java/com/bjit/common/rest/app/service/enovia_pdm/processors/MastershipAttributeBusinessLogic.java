/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.app.service.enovia_pdm.processors;

import com.bjit.common.rest.app.service.enovia_pdm.exceptions.AttributeNotInRequestException;
import com.bjit.common.rest.app.service.enovia_pdm.exceptions.MastershipChangeException;
import com.bjit.common.rest.app.service.enovia_pdm.models.xml_mapping_model.ItemImportValue;
import com.bjit.common.rest.app.service.enovia_pdm.models.xml_mapping_model.ItemImportValues;
import com.bjit.common.rest.app.service.enovia_pdm.models.xml_mapping_model.ItemImportXmlMapElementAttribute;
import com.bjit.common.rest.app.service.enovia_pdm.processors.interfaces.IMapperElementMemento;
import com.bjit.common.rest.app.service.enovia_pdm.processors.interfaces.IMastershipAttributeBusinessLogic;
import com.bjit.ewc18x.utils.PropertyReader;
import com.bjit.ex.integration.transfer.util.NullOrEmptyChecker;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.List;
import org.apache.log4j.Logger;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 *
 * @author BJIT
 */
@Component
@Scope(value = "prototype")
public class MastershipAttributeBusinessLogic implements IMastershipAttributeBusinessLogic {

    private final Logger MASTERSHIP_ATTRIBUTE_BUSINESS_LOGIC_LOGGER = Logger.getLogger(ItemMapperProcessor.class);

    private static final List<String> LENGTH_UNITS = Arrays.asList(PropertyReader.getProperty("length.units").split("\\|"));
    private static final List<String> AREA_UNITS = Arrays.asList(PropertyReader.getProperty("area.units").split("\\|"));
    private static final List<String> VOLUME_UNITS = Arrays.asList(PropertyReader.getProperty("volume.units").split("\\|"));
    private static final List<String> MASS_UNITS = Arrays.asList(PropertyReader.getProperty("mass.units").split("\\|"));

    @Override
    public String getItemAttribute(IMapperElementMemento mapperElementMemento, String attributeName, String value) {
        ItemImportXmlMapElementAttribute itemAttribute = mapperElementMemento.getItemAttribute(attributeName);

        if (!NullOrEmptyChecker.isNull(itemAttribute)) {
            Boolean requestAttribute = itemAttribute.getRequestAttribute();
            if (!NullOrEmptyChecker.isNull(requestAttribute)) {
                if (requestAttribute) {
                    ItemImportValues rangeValues = itemAttribute.getValues();
                    return getRangeValue(rangeValues, value, attributeName);
                } else {
                    throw new AttributeNotInRequestException("Attribute '" + attributeName + "' will not be parsed as PDM request data");
                }
            } else {
                throw new AttributeNotInRequestException("Attribute '" + attributeName + "' will not be parsed as PDM request data");
            }
        } else {
            throw new NullPointerException("No mapping has found for attribute '" + attributeName + "'");
        }
    }

    private String getRangeValue(ItemImportValues rangeValues, String value, String attributeName) throws MastershipChangeException {
        if (!NullOrEmptyChecker.isNull(rangeValues)) {
            String defaultValue = rangeValues.getDefaultValue();
            if (NullOrEmptyChecker.isNullOrEmpty(value)) {
                return defaultValue;
            } else {
                if (NullOrEmptyChecker.isNull(rangeValues.getValue())) {
                    return value;
                }

                for (ItemImportValue itemImportValue : rangeValues.getValue()) {
                    if (itemImportValue.getSrc().equals(value)) {
                        return itemImportValue.getValue();
                    }
                }

                String errorMesage = MessageFormat.format(PropertyReader.getProperty("unsupported.value.exception"), "'" + value + "'", "'" + attributeName + "'");
                throw new MastershipChangeException(errorMesage);
            }
        } else {
            return value;
        }
    }

    @Override
    public String getRelationshipAttribute(IMapperElementMemento mapperElementMemento, String attributeName, String value) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
