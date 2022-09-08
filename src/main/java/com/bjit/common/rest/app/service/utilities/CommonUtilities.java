/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.app.service.utilities;

import com.bjit.common.code.utility.security.ContextPasswordSecurity;
import com.bjit.common.rest.app.service.context.CreateContext;
import com.bjit.common.rest.app.service.controller.authentication.AuthenticationUserModel;
import com.bjit.common.rest.item_bom_import.utility.bomResponseMessageFormatter.ChildInfo;
import com.bjit.common.rest.item_bom_import.xml_BOM_mapper_model.Attribute;
import com.bjit.common.rest.item_bom_import.xml_BOM_mapper_model.DataType;
import com.bjit.ewc18x.utils.PropertyReader;
import com.digidemic.unitof.UnitOf;
import com.matrixone.apps.domain.util.ContextUtil;
import com.matrixone.apps.domain.util.FrameworkException;
import com.matrixone.apps.domain.util.MqlUtil;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.MessageFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import lombok.extern.log4j.Log4j;
import matrix.db.Context;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;
import org.xml.sax.SAXException;

/**
 *
 * @author BJIT
 */
@Log4j
@Component
//@RequestScope
public class CommonUtilities {

    private static final org.apache.log4j.Logger COMMON_UTIL_LOGGER = org.apache.log4j.Logger.getLogger(CommonUtilities.class);

    private static final List<String> EXCEPTION_LIST = new ArrayList<>();

    static {
        EXCEPTION_LIST.add("java.util.concurrent.ExecutionException:");
        EXCEPTION_LIST.add("java.lang.RuntimeException:");
        EXCEPTION_LIST.add("java.lang.NullPointerException:");
        EXCEPTION_LIST.add("java.lang.NullPointerException");
        EXCEPTION_LIST.add("java.lang.IOException:");
        EXCEPTION_LIST.add("java.lang.NumberFormatException:");
        EXCEPTION_LIST.add("java.net.ConnectException:");
        EXCEPTION_LIST.add("java.lang.Exception:");
        EXCEPTION_LIST.add("Exception:");
        EXCEPTION_LIST.add("Exception :");
        EXCEPTION_LIST.add("Error: #1900068:");
        EXCEPTION_LIST.add("Error: #1500188:");
    }

    public synchronized static String removeExceptions(String error) {
        for (String exception : EXCEPTION_LIST) {
            error = error.replace(exception, "");
        }
        return error.trim();
    }

    public String getEnoviaType(String sourceERP, String sourceObjType) throws IOException {
        try {
            CommonPropertyReader commonPropertyReader = new CommonPropertyReader();
            XmlParse xmlParse = new XmlParse();
            String enoviaType = xmlParse.getPredefinedValue(commonPropertyReader.getPropertyValue("tag.type.mappings"),
                    sourceERP.toUpperCase(),
                    commonPropertyReader.getPropertyValue("attribute.discriminator.objectType"),
                    commonPropertyReader.getPropertyValue("attribute.discriminator.objectType"),
                    sourceObjType);
            return enoviaType;
        } catch (ParserConfigurationException | SAXException | XPathExpressionException ex) {
            return null;
        }
    }

    public Context generateContext() throws Exception {
        try {
            ContextPasswordSecurity contextPasswordSecurity = new ContextPasswordSecurity();

            AuthenticationUserModel userCredentialsModel = new AuthenticationUserModel();

            String contextUserid = contextPasswordSecurity.decryptPassword(PropertyReader.getProperty("context.name"));
            userCredentialsModel.setUserId(contextUserid);

            String contextPassword = contextPasswordSecurity.decryptPassword(PropertyReader.getProperty("context.pass"));
            userCredentialsModel.setPassword(contextPassword);

            userCredentialsModel.setHost(PropertyReader.getProperty("matrix.context.cas.connection.host"));
            userCredentialsModel.setIsCasContext("true");

            CreateContext generateContext = new CreateContext();
            Context context = generateContext.getContext(userCredentialsModel.getUserId(), userCredentialsModel.getPassword(), userCredentialsModel.getHost(), Boolean.parseBoolean(userCredentialsModel.getIsCasContext()));
            return context;
        } catch (Exception exp) {
            log.error(exp.getMessage());
            throw exp;
        }
    }

    public Context generateContext(String userId, String password, String host) throws Exception {
        try {
            //ContextPasswordSecurity contextPasswordSecurity = new ContextPasswordSecurity();

            AuthenticationUserModel userCredentialsModel = new AuthenticationUserModel();

            // String contextUserid = contextPasswordSecurity.decryptPassword(PropertyReader.getProperty("context.name"));
            userCredentialsModel.setUserId(userId);

            // String contextPassword = contextPasswordSecurity.decryptPassword(PropertyReader.getProperty("context.pass"));
            userCredentialsModel.setPassword(password);

            userCredentialsModel.setHost(host);
            userCredentialsModel.setIsCasContext("true");

            CreateContext generateContext = new CreateContext();
            Context context = generateContext.getContext(userCredentialsModel.getUserId(), userCredentialsModel.getPassword(), userCredentialsModel.getHost(), Boolean.parseBoolean(userCredentialsModel.getIsCasContext()));
            return context;
        } catch (Exception exp) {
            //TASK_IMPORT_PROCESS.error(exp.getMessage());
            throw exp;
        }
    }

    public synchronized void doStartTransaction(Context context) throws RuntimeException, InterruptedException, FrameworkException {
        /*---------------------------------------- ||| Start Transaction ||| ----------------------------------------*/
        Boolean transactionStartErrorOccured = true;
        Instant transactionStartTime = Instant.now();
        String transactionStartMessage = "Starting Transaction";
        do {
            Exception retryingException = null;
            try {
                COMMON_UTIL_LOGGER.debug(transactionStartMessage);
                ContextUtil.startTransaction(context, true);
                transactionStartErrorOccured = false;
            } catch (Exception exp) {
                Thread.sleep(1000);
                COMMON_UTIL_LOGGER.error(exp);
                transactionStartMessage = "Retrying to Start Transaction Again";
                COMMON_UTIL_LOGGER.info(transactionStartMessage);
                retryingException = exp;
            }
            Instant transactionStartRetryingTime = Instant.now();
            long duration = DateTimeUtils.getDuration(transactionStartTime, transactionStartRetryingTime);
            if ((duration / 60000) == 1) {
                throw new RuntimeException(retryingException);
            }
        } while (transactionStartErrorOccured);
    }

    public synchronized void doCommitTransaction(Context context) throws RuntimeException, InterruptedException, FrameworkException {
        /*---------------------------------------- ||| Commit Transaction ||| ----------------------------------------*/
        Boolean transactionCommitErrorOccured = true;
        Instant transactionCommitTime = Instant.now();
        String transactionCommitMessage = "Committing Transaction";
        do {
            Exception retryingException = null;
            try {
                COMMON_UTIL_LOGGER.debug(transactionCommitMessage);
                ContextUtil.commitTransaction(context);
                transactionCommitErrorOccured = false;
            } catch (Exception exp) {
                Thread.sleep(1000);
                COMMON_UTIL_LOGGER.error(exp);
                transactionCommitMessage = "Retrying to Commit Transaction Again";
                COMMON_UTIL_LOGGER.info(transactionCommitMessage);
                retryingException = exp;
            }
            Instant transactionCommitRetryingTime = Instant.now();
            long duration = DateTimeUtils.getDuration(transactionCommitTime, transactionCommitRetryingTime);
            if ((duration / 60000) == 1) {
                throw new RuntimeException(retryingException);
            }
        } while (transactionCommitErrorOccured);
    }

    public synchronized void doAbortTransaction(Context context) throws RuntimeException, InterruptedException, FrameworkException {
        /*---------------------------------------- ||| Commit Transaction ||| ----------------------------------------*/
        Boolean transactionAbortErrorOccured = true;
        Instant transactionAbortTime = Instant.now();
        String transactionAbortMessage = "Aborting Transaction";
        do {
            Exception retryingException = null;
            try {
                COMMON_UTIL_LOGGER.debug(transactionAbortMessage);
                ContextUtil.abortTransaction(context);
                transactionAbortErrorOccured = false;
            } catch (Exception exp) {
                Thread.sleep(1000);
                COMMON_UTIL_LOGGER.error(exp);
                transactionAbortMessage = "Retrying to Abort Transaction Again";
                COMMON_UTIL_LOGGER.info(transactionAbortMessage);
                retryingException = exp;
            }
            Instant transactionAbortRetryingTime = Instant.now();
            long duration = DateTimeUtils.getDuration(transactionAbortTime, transactionAbortRetryingTime);
            if ((duration / 60000) == 1) {
                throw new RuntimeException(retryingException);
            }
        } while (transactionAbortErrorOccured);
    }

    public void escapeOperationOn(Context context) throws FrameworkException {
        escapeOperation(context, "on");
    }

    private synchronized void escapeOperation(Context context, String escapeOperation) throws FrameworkException {
        try {
            String setEscape = "set escape " + escapeOperation;
            COMMON_UTIL_LOGGER.info("Escape status : " + setEscape);
            MqlUtil.mqlCommand(context, setEscape);
        } catch (FrameworkException ex) {
            COMMON_UTIL_LOGGER.error(ex);
            throw ex;
        }
    }

    public void escapeOperationOff(Context context) throws FrameworkException {
        escapeOperation(context, "off");
    }

    public Double getUsageCoefficient(ChildInfo childInfo, Double childQuantity, String parentName) {

        Double numberOfUnit = Double.valueOf(childInfo.getChildNoOfUnit());//.parseDouble(childInfo.getChildNoOfUnit());
        //Double childQuantity = Double.valueOf(childInfo.getChildQuantity());
        String childLength = childInfo.getLength();
        String childWidth = childInfo.getWidth();

        double usageCoEfficent = 0;
        String inventoryUnit = childInfo.getChildInventoryUnit();
        List lengthUnitList = Arrays.asList(PropertyReader.getProperty("length.units").split("\\|"));
        List areaUnitList = Arrays.asList(PropertyReader.getProperty("area.units").split("\\|"));
        List massUnitList = Arrays.asList(PropertyReader.getProperty("mass.units").split("\\|"));
        List volumeUnitList = Arrays.asList(PropertyReader.getProperty("volume.units").split("\\|"));
//        List forbiddenImperialUnits = Arrays.asList(PropertyReader.getProperty("forbidden.imperial.units").split("\\|"));

        if (areaUnitList.contains(inventoryUnit)) {
            Optional.ofNullable(childLength).filter(length -> !length.isEmpty()).orElseThrow(() -> new NullPointerException("Length could not be null or empty of " + childInfo.getChildTNR().getName() + " under " + parentName));
            Optional.ofNullable(childWidth).filter(length -> !length.isEmpty()).orElseThrow(() -> new NullPointerException("Width could not be null or empty of " + childInfo.getChildTNR().getName() + " under " + parentName));
            usageCoEfficent = quantityCalculationForLengthAndArea(this.unitConversion("mm2", Double.parseDouble(childLength) * Double.parseDouble(childWidth)), numberOfUnit, this.unitConversion(inventoryUnit, (double) childQuantity), usageCoEfficent, childInfo, parentName);
        } else if (lengthUnitList.contains(inventoryUnit)) {
            Optional.ofNullable(childLength).filter(length -> !length.isEmpty()).orElseThrow(() -> new NullPointerException("Length could not be null or empty of " + childInfo.getChildTNR().getName() + " under " + parentName));
            usageCoEfficent = quantityCalculationForLengthAndArea(this.unitConversion("mm", Double.parseDouble(childLength)), numberOfUnit, this.unitConversion(inventoryUnit, (double) childQuantity), usageCoEfficent, childInfo, parentName);
        } else if (massUnitList.contains(inventoryUnit) || volumeUnitList.contains(inventoryUnit)) {
            usageCoEfficent = this.unitConversion(inventoryUnit, Double.valueOf(childInfo.getChildQuantity()));
            childInfo.setChildQuantity(1);
        }
        return usageCoEfficent;
    }

    public Double getUsageCoefficient(ChildInfo childInfo, Double childQuantity, String parentName, String source) {

        Double numberOfUnit = Double.valueOf(childInfo.getChildNoOfUnit());//.parseDouble(childInfo.getChildNoOfUnit());
        //Double childQuantity = Double.valueOf(childInfo.getChildQuantity());
        String childLength = childInfo.getLength();
        String childWidth = childInfo.getWidth();

        double usageCoEfficent = 0;
        String inventoryUnit = childInfo.getChildInventoryUnit();
        List lengthUnitList = Arrays.asList(PropertyReader.getProperty("length.units").split("\\|"));
        List areaUnitList = Arrays.asList(PropertyReader.getProperty("area.units").split("\\|"));
        List massUnitList = Arrays.asList(PropertyReader.getProperty("mass.units").split("\\|"));
        List volumeUnitList = Arrays.asList(PropertyReader.getProperty("volume.units").split("\\|"));
//        List forbiddenImperialUnits = Arrays.asList(PropertyReader.getProperty("forbidden.imperial.units").split("\\|"));

        if (areaUnitList.contains(inventoryUnit)) {
            Optional.ofNullable(childLength).filter(length -> !length.isEmpty()).orElseThrow(() -> new NullPointerException("Length could not be null or empty of " + childInfo.getChildTNR().getName() + " under " + parentName));
            Optional.ofNullable(childWidth).filter(length -> !length.isEmpty()).orElseThrow(() -> new NullPointerException("Width could not be null or empty of " + childInfo.getChildTNR().getName() + " under " + parentName));
            usageCoEfficent = quantityCalculationForLengthAndArea(this.unitConversion(PropertyReader.getProperty(source + ".area.base.unit")/*"mm2"*/, Double.parseDouble(childLength) * Double.parseDouble(childWidth)), numberOfUnit, this.unitConversion(inventoryUnit, (double) childQuantity), usageCoEfficent, childInfo, parentName);
        } else if (lengthUnitList.contains(inventoryUnit)) {
            Optional.ofNullable(childLength).filter(length -> !length.isEmpty()).orElseThrow(() -> new NullPointerException("Length could not be null or empty of " + childInfo.getChildTNR().getName() + " under " + parentName));
            usageCoEfficent = quantityCalculationForLengthAndArea(this.unitConversion(PropertyReader.getProperty(source + ".length.base.unit")/*"mm"*/, Double.parseDouble(childLength)), numberOfUnit, this.unitConversion(inventoryUnit, (double) childQuantity), usageCoEfficent, childInfo, parentName);
        } else if (massUnitList.contains(inventoryUnit) || volumeUnitList.contains(inventoryUnit)) {
            usageCoEfficent = this.unitConversion(inventoryUnit, Double.valueOf(childInfo.getChildQuantity()));
            childInfo.setChildQuantity(1);
        }
        return usageCoEfficent;
    }

    private double quantityCalculationForLengthAndArea(double lengthOrArea, double noOfUnit, double childQuantityFloat, double usageCoEfficient, ChildInfo childInfo, String parentName) throws NumberFormatException, RuntimeException {
        double quantityCalculationResult = lengthOrArea * noOfUnit;
        Integer preciseValue = Integer.parseInt(PropertyReader.getProperty("bom.quantityCalculation.and.netQuantity.precision"));
        quantityCalculationResult = new BigDecimal(quantityCalculationResult).setScale(preciseValue, RoundingMode.HALF_EVEN).doubleValue();
        double childQuantityDouble = new BigDecimal(childQuantityFloat).setScale(preciseValue, RoundingMode.HALF_EVEN).doubleValue();
        if (quantityCalculationResult == 0) {
            usageCoEfficient = childQuantityFloat;
            childInfo.setChildQuantity(1);
        } else if (quantityCalculationResult == childQuantityDouble) {
            usageCoEfficient = lengthOrArea;
            childInfo.setChildQuantity((int) Math.round(noOfUnit));
        } else {
            throw new RuntimeException(MessageFormat.format(PropertyReader.getProperty("bom.quantityCalculationResult.and.netQuantity.is.inEqual"), parentName, childInfo.getChildTNR().getName()));
        }
        return usageCoEfficient;
    }

    public String convertToRealValues(Attribute attribute, String jsonAttributeValue) {
        COMMON_UTIL_LOGGER.debug("Converting attribute to real values !! ");
        String sourceName = attribute.getSourceName();
        DataType attributeDataType = attribute.getDataType();
        String dataType = attributeDataType.getDataType();
        Double divisor = attributeDataType.getDivisor();
        Integer precision = attributeDataType.getPrecision();

        if (!NullOrEmptyChecker.isNullOrEmpty(dataType) && dataType.equalsIgnoreCase("real")) {
            try {
                double parsedValue = Double.parseDouble(jsonAttributeValue);

                if (!NullOrEmptyChecker.isNull(divisor)) {
                    parsedValue = parsedValue / divisor;
                    if (!NullOrEmptyChecker.isNullOrEmpty(precision)) {
                        parsedValue = new BigDecimal(parsedValue).setScale(precision, RoundingMode.HALF_EVEN).doubleValue();
                    }
                }
                return Double.toString(parsedValue);
            } catch (NumberFormatException exp) {
                COMMON_UTIL_LOGGER.error(exp);
            } catch (NullPointerException exp) {
                COMMON_UTIL_LOGGER.error(exp);
            } catch (Exception exp) {
                COMMON_UTIL_LOGGER.error(exp);
            }
        }
        return jsonAttributeValue;
    }

    public Double unitConversion(String sourceUnit, Double valueToConvert) {
        if (sourceUnit.equalsIgnoreCase("m3")) {
            valueToConvert = new UnitOf.Volume().fromCubicMeters(valueToConvert).toCubicMeters();
        } else if (sourceUnit.equalsIgnoreCase("in3")) {
            valueToConvert = new UnitOf.Volume().fromCubicInches(valueToConvert).toCubicMeters();
        } else if (sourceUnit.equalsIgnoreCase("ft3")) {
            valueToConvert = new UnitOf.Volume().fromCubicFeet(valueToConvert).toCubicMeters();
        } else if (sourceUnit.equalsIgnoreCase("gal")) {
            valueToConvert = new UnitOf.Volume().fromGallonsUS(valueToConvert).toCubicMeters();
        } else if (sourceUnit.equalsIgnoreCase("l")) {
            valueToConvert = new UnitOf.Volume().fromLiters(valueToConvert).toCubicMeters();
        } else if (sourceUnit.equalsIgnoreCase("lb")) {
            valueToConvert = new UnitOf.Mass().fromPounds(valueToConvert).toKilograms();
        } else if (sourceUnit.equalsIgnoreCase("g")) {
            valueToConvert = new UnitOf.Mass().fromGrams(valueToConvert).toKilograms();
        } else if (sourceUnit.equalsIgnoreCase("kg")) {
            valueToConvert = new UnitOf.Mass().fromKilograms(valueToConvert).toKilograms();
        } else if (sourceUnit.equalsIgnoreCase("m")) {
            valueToConvert = new UnitOf.Length().fromMeters(valueToConvert).toMeters();
        } else if (sourceUnit.equalsIgnoreCase("mm")) {
            valueToConvert = new UnitOf.Length().fromMillimeters(valueToConvert).toMeters();
        } else if (sourceUnit.equalsIgnoreCase("ft")) {
            valueToConvert = new UnitOf.Length().fromFeet(valueToConvert).toMeters();
        } else if (sourceUnit.equalsIgnoreCase("in")) {
            valueToConvert = new UnitOf.Length().fromInches(valueToConvert).toMeters();
        } else if (sourceUnit.equalsIgnoreCase("mm2")) {
            valueToConvert = new UnitOf.Area().fromSquareMillimeters(valueToConvert).toSquareMeters();
        } else if (sourceUnit.equalsIgnoreCase("m2")) {
            valueToConvert = new UnitOf.Area().fromSquareMeters(valueToConvert).toSquareMeters();
        } else if (sourceUnit.equalsIgnoreCase("ft2")) {
            valueToConvert = new UnitOf.Area().fromSquareFeet(valueToConvert).toSquareMeters();
        } else if (sourceUnit.equalsIgnoreCase("in2")) {
            valueToConvert = new UnitOf.Area().fromSquareInches(valueToConvert).toSquareMeters();
        }

        return valueToConvert;
    }

    /**
     * Array to Set Utility Method
     *
     * @param <T>
     * @param array
     * @return
     */
    public static <T> Set<T> convertArrayToSet(T array[]) {

        // Create an empty Set
        Set<T> set = new HashSet<>();

        // Iterate through the array
        for (T t : array) {
            // Add each element into the set
            set.add(t);
        }

        // Return the converted Set
        return set;
    }
}
