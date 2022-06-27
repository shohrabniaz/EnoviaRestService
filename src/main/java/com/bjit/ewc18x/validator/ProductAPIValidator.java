/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.ewc18x.validator;

import com.bjit.common.rest.app.service.model.modelVersion.MVResponseMessageFormatterBean;
import com.bjit.common.rest.app.service.model.tnr.TNR;
import com.bjit.common.rest.app.service.utilities.CommonSearch;
import com.bjit.common.rest.app.service.utilities.NullOrEmptyChecker;
import com.bjit.common.rest.item_bom_import.item_import.AttributeBusinessLogic;
import com.bjit.common.rest.item_bom_import.xml_mapping_model.ItemImportXmlMapElementAttribute;
import com.bjit.ewc18x.utils.PropertyReader;
import com.bjit.mapper.mapproject.util.Constants;
import com.matrixone.apps.domain.util.FrameworkException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import matrix.db.Context;

/**
 *
 * @author Arifur
 */
public class ProductAPIValidator extends CustomValidator {

    private static final org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(ProductAPIValidator.class);

    /**
     * checkTheImportedItems
     *
     * @param context
     * @param successFulItemList
     * @param errorItemList
     */
    public void checkTheImportedItems(Context context, List<MVResponseMessageFormatterBean> successFulItemList, List<MVResponseMessageFormatterBean> errorItemList) {
        List<MVResponseMessageFormatterBean> successfulList = Optional.ofNullable(successFulItemList).orElse(new ArrayList<>());

        List<MVResponseMessageFormatterBean> tempSuccessfulList = new ArrayList<>();

        List<MVResponseMessageFormatterBean> errorList = Optional.ofNullable(errorItemList).orElse(new ArrayList<>());

        successfulList.stream().parallel().forEach((MVResponseMessageFormatterBean responseItem) -> {

            TNR tnr = responseItem.getTnr();

            try {

                CommonSearch commonSearch = new CommonSearch();
                commonSearch.searchItem(context, tnr);
            } catch (FrameworkException ex) {
                errorProneHardwareProductChecking(tnr, ex, responseItem, tempSuccessfulList, errorList);
            } catch (Exception ex) {
                errorProneHardwareProductChecking(tnr, ex, responseItem, tempSuccessfulList, errorList);
            }
        });

        successfulList.removeAll(tempSuccessfulList);
    }

    /**
     * errorProneHardwareProductChecking
     *
     * @param tnr
     * @param ex
     * @param responseItem
     * @param tempSuccessfulList
     * @param errorList
     */
    private void errorProneHardwareProductChecking(TNR tnr, Exception ex, MVResponseMessageFormatterBean responseItem, List<MVResponseMessageFormatterBean> tempSuccessfulList, List<MVResponseMessageFormatterBean> errorList) {
        String error = "BusinessObject " + tnr.getType() + " " + tnr.getName() + " " + tnr.getRevision() + " not imported due to unknown error";
        logger.error(ex);
        logger.error(error);
        tempSuccessfulList.add(responseItem);

        responseItem.setErrorMessage(error);
        responseItem.setObjectId(null);

        errorList.add(responseItem);
    }

    /**
     * validating product type and source
     *
     * @param productType
     * @param source
     * @return boolean
     */
    public boolean isProductTypeAndSourceValid(String productType, String source) {
        return (source == null ? Constants.PRODUCT_TYPE_SOURCE_MAP.get(productType) == null : source.equals(Constants.PRODUCT_TYPE_SOURCE_MAP.get(productType)));
    }


    /**
     * null or empty checker for marketing name
     *
     * @param value value cannot be 'null' string and should be valid string
     * @return boolean
     */
    public boolean isValidMarketingName(String value) {
        return isValidString(value) && !value.trim().equalsIgnoreCase("null");
    }

    /**
     * validate attribute value according to data type
     *
     * @param attributeBusinessLogic
     * @param elementAttribute
     * @param attributeValue
     * @param dataType
     * @param sourceName
     * @return
     */
    public String getValidatedAttributeValue(AttributeBusinessLogic attributeBusinessLogic, ItemImportXmlMapElementAttribute elementAttribute, String attributeValue, String dataType, String sourceName) {
        String defaultAttributeValue = attributeValue;
        if (dataType.equalsIgnoreCase("date")) {

            if (NullOrEmptyChecker.isNull(elementAttribute.getDataFormat())) {
                throw new NullPointerException("Data format is missing for the 'Date' type attribute '" + sourceName + "'");
            }

            String timeZone = elementAttribute.getDataFormat().getTimezone();

            logger.debug("Locale is : " + timeZone);

            String changedFormat = NullOrEmptyChecker.isNullOrEmpty(timeZone)
                    ? attributeBusinessLogic.changeDateType(attributeValue, elementAttribute.getDataFormat().getSourceFormat(), elementAttribute.getDataFormat().getDestinationFormat())
                    : attributeBusinessLogic.changeDateType(attributeValue, elementAttribute.getDataFormat().getSourceFormat(), elementAttribute.getDataFormat().getDestinationFormat(), timeZone);

            defaultAttributeValue = NullOrEmptyChecker.isNullOrEmpty(attributeValue) ? attributeValue : changedFormat;
        } else if (dataType.equalsIgnoreCase("float") && !NullOrEmptyChecker.isNullOrEmpty(attributeValue)) {
            logger.info("\n\n#####   Data type: float ###\n\n");
            try {
                float attributeValueFloat = Float.parseFloat(attributeValue);
                logger.info("attributeValueFloat: " + attributeValueFloat);
            } catch (NumberFormatException e) {
                String errorMessage = MessageFormat.format(PropertyReader.getProperty("unsupported.value.exception"), "'" + attributeValue + "'", "'" + sourceName + "'");
                throw new RuntimeException(errorMessage);
            }
        }
        return defaultAttributeValue;
    }
}
