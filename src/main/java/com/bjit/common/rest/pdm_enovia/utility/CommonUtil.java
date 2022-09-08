package com.bjit.common.rest.pdm_enovia.utility;

import com.bjit.common.rest.app.service.utilities.BusinessObjectOperations;
import com.bjit.common.rest.app.service.model.createobject.CreateObjectBean;
import com.bjit.common.rest.app.service.model.tnr.TNR;
import com.bjit.common.rest.app.service.utilities.NullOrEmptyChecker;
import com.bjit.common.rest.pdm_enovia.formatter.AttributeFormatter;
import com.bjit.common.rest.pdm_enovia.result.ResultUtil;
import com.bjit.ewc18x.utils.PropertyReader;
import com.matrixone.apps.domain.util.FrameworkException;
import com.matrixone.apps.domain.util.MqlUtil;
import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import matrix.db.Context;
import matrix.util.MatrixException;
import org.apache.log4j.Logger;

/**
 *
 * @author Mashuk/BJIT
 */
public class CommonUtil {

    private static final Logger COMMON_UTIL_LOGGER = Logger.getLogger(CommonUtil.class);
    private static HashMap<String, String> MAP_DIRECTORY;
    public static HashMap<String, String> attributeNameMap;

    public static CreateObjectBean checkCreateObjectBean(CreateObjectBean createObjectBean) {
        return createObjectBean;
    }

    public static TNR validateTNR(TNR tnr) {
        if (NullOrEmptyChecker.isNull(tnr)) {
            return null;
        }
        return tnr;
    }

    public static boolean convertStringToBoolean(String booleanString) {
        if (booleanString.equalsIgnoreCase("true")) {
            return true;
        } else {
            return false;
        }
    }

    public static CreateObjectBean updateItemAttributesAndProperties(CreateObjectBean itemObject, String itemType) throws MatrixException {
        if (!NullOrEmptyChecker.isNullOrEmpty(itemObject.getTnr().getType())) {
            itemObject.getAttributes().put("Commercial Item Family", itemObject.getTnr().getType());
        }
        if (!NullOrEmptyChecker.isNullOrEmpty(itemObject.getTnr().getName())) {
            itemObject.getAttributes().put("External Id", itemObject.getTnr().getName());
        }
        if (itemObject.getAttributes().containsKey("Translation name")) {
            if (!NullOrEmptyChecker.isNullOrEmpty(itemObject.getAttributes().get("Translation name"))) {
                String translationName = itemObject.getAttributes().get("Translation name");
                itemObject.getAttributes().put("Translation name", AttributeFormatter.getBundleIdFromTranslationName(translationName));
            }
        }
        itemObject.getTnr().setType(itemType);

        return itemObject;
    }

    public static void moveFile(File sourceFile, String destDir)
            throws Exception {
        createDirectory(destDir);
        File moveFile = new File(sourceFile.getAbsolutePath());
        boolean isMoved = moveFile.renameTo(new File(destDir
                + moveFile.getName()));
        if (isMoved) {
            moveFile.delete();
            COMMON_UTIL_LOGGER.info("'" + sourceFile.getName() + "' has been moved to '" + destDir + "' directory");
        } else {
            long sTime = System.currentTimeMillis();
            moveFile.renameTo(new File(destDir + moveFile.getName() + sTime + ".bac"));
            COMMON_UTIL_LOGGER.info("'" + sourceFile.getName() + "' has been moved to '" + destDir + "' directory");
        }
    }

    public static void createDirectory(String destDir) throws Exception {
        File destFolder = new File(destDir);
        if (!destFolder.exists()) {
            boolean isDirectoryCreated = destFolder.mkdirs();
            if (isDirectoryCreated) {
                COMMON_UTIL_LOGGER.debug(destFolder.getAbsolutePath()
                        + " Directory created.");
            } else {
                throw new Exception(destFolder.getAbsolutePath()
                        + " Directory creation failed!");
            }
        }
    }

    public static void moveFileToErrorLocation(TNR documentTNR, String fileName, ResultUtil resultUtil) {
        try {
            CommonUtil.moveFile(new File(PropertyReader.getProperty("stmt.checkin.file.upload.directory") + fileName), PropertyReader.getProperty("stmt.checkin.file.error.directory"));
        } catch (Exception ex) {
            resultUtil.addErrorResult(documentTNR.getName(), documentTNR, "Error: " + ex);
            COMMON_UTIL_LOGGER.error(">>>>> Error: " + ex);
        }
    }

    public static void moveFileToErrorLocation(TNR documentTNR, List<HashMap<String, String>> fileMapList, ResultUtil resultUtil) {
        if (!NullOrEmptyChecker.isNull(fileMapList)) {
            for (HashMap<String, String> fileMap : fileMapList) {
                if (fileMap.containsKey("fileName")) {
                    try {
                        CommonUtil.moveFile(new File(PropertyReader.getProperty("stmt.checkin.file.upload.directory") + fileMap.get("fileName")),
                                PropertyReader.getProperty("stmt.checkin.file.error.directory"));
                    } catch (Exception ex) {
                        resultUtil.addErrorResult(documentTNR.getName(), documentTNR, "Error: " + ex);
                        COMMON_UTIL_LOGGER.error(">>>>> Error: " + ex);
                    }
                }
            }
        }
    }

    public static String getSystemDate() {
        DateFormat dateFormat = new SimpleDateFormat("dd/M/yyyy hh:mm:ss");
        Date date = new Date();
        return dateFormat.format(date); //2016/11/16 12:08:43
    }

    public static String getCurrentSystemDate() {
        Date date = new Date();
        String dateFormat = "ddMMyyyy";
        SimpleDateFormat targetDateFormat = new SimpleDateFormat(dateFormat);
        return targetDateFormat.format(date);
    }

    public static String populateMapDirectoryFromObject(CreateObjectBean createObjectBean) {
        CreateObjectBean validatedCreateObjectBean = validateCreateObjectBean(createObjectBean);
        if (NullOrEmptyChecker.isNullOrEmpty(MAP_DIRECTORY)) {
            MAP_DIRECTORY = PropertyReader.getProperties("import.object.erp.map", true);
            COMMON_UTIL_LOGGER.debug("Directory Map " + MAP_DIRECTORY);
        }
        if (NullOrEmptyChecker.isNullOrEmpty(validatedCreateObjectBean.getSource())) {
            return MAP_DIRECTORY.get("common");
        } else {
            return MAP_DIRECTORY.get(validatedCreateObjectBean.getSource() + "." + validatedCreateObjectBean.getTnr().getType());
        }
    }

    public static String populateMapDirectoryFromObject(CreateObjectBean createObjectBean, Boolean calledFromInsiders) {
        if (calledFromInsiders) {
            CreateObjectBean validatedCreateObjectBean = createObjectBean;
            if (NullOrEmptyChecker.isNullOrEmpty(MAP_DIRECTORY)) {
                MAP_DIRECTORY = PropertyReader.getProperties("import.object.erp.map", true);
                COMMON_UTIL_LOGGER.debug("Directory Map " + MAP_DIRECTORY);
            }
            if (NullOrEmptyChecker.isNullOrEmpty(validatedCreateObjectBean.getSource())) {
                return MAP_DIRECTORY.get("common");
            } else {
                return MAP_DIRECTORY.get(validatedCreateObjectBean.getSource() + "." + validatedCreateObjectBean.getTnr().getType());
            }
        } else {
            return populateMapDirectoryFromObject(createObjectBean);
        }
    }

    public static String populateMapDirectoryFromSourceAndType(String source, String type) {
        if (NullOrEmptyChecker.isNullOrEmpty(MAP_DIRECTORY)) {
            MAP_DIRECTORY = PropertyReader.getProperties("import.object.erp.map", true);
            COMMON_UTIL_LOGGER.debug("Directory Map " + MAP_DIRECTORY);
        }
        if (!NullOrEmptyChecker.isNullOrEmpty(source) && !NullOrEmptyChecker.isNullOrEmpty(type)) {
            return MAP_DIRECTORY.get(source + "." + type);
        } else {
            return MAP_DIRECTORY.get("common");
        }
    }

    public static CreateObjectBean validateCreateObjectBean(CreateObjectBean createObjectBean) {
        String errorMessage;
        if (NullOrEmptyChecker.isNull(createObjectBean)) {
            errorMessage = "No data found in the bean object";
            COMMON_UTIL_LOGGER.error(errorMessage);
            throw new NullPointerException(errorMessage);
        }
        BusinessObjectOperations businessObjectOperations = new BusinessObjectOperations();
        businessObjectOperations.validateTNR(createObjectBean.getTnr(), !createObjectBean.getIsAutoName(), !createObjectBean.getIsAutoName());
        if (NullOrEmptyChecker.isNullOrEmpty(createObjectBean.getAttributes())) {
            errorMessage = "There is no attribute presents in the request";
            COMMON_UTIL_LOGGER.error(errorMessage);
            throw new NullPointerException(errorMessage);
        }
        return createObjectBean;
    }

    public static String formatErrorMessage(String errorMsg) {
        for (Map.Entry<String, String> entry : attributeNameMap.entrySet()) {
            String[] splittedValues = entry.getKey().split("\\.");
            String attributeName = splittedValues.length == 2 ? splittedValues[1] : splittedValues[0];
            if (errorMsg.contains(attributeName)) {
                errorMsg = errorMsg.replace(attributeName, entry.getValue());
            }
        }
        return errorMsg;
    }

    public static FileFilter getFileFilter(File sourceDirPath,
            final String fileExtension) throws Exception {
        if (fileExtension == null || fileExtension.isEmpty()) {
            throw new Exception("File extension is required! ");
        }
        FileFilter filter = new FileFilter() {
            @Override
            public boolean accept(File file) {
                return file.isFile()
                        && file.getName().toLowerCase()
                                .endsWith(fileExtension.toLowerCase());
            }
        };
        return filter;
    }

    public static void addInterface(Context context, String businessObjectID, String interfaceName) throws FrameworkException, InterruptedException {
        StringBuilder commandBuilder = new StringBuilder();
        commandBuilder.append("mod bus ").append(businessObjectID)
                .append(" add interface ").append(interfaceName);

        String interfaceAdditionCommand = commandBuilder.toString();
        COMMON_UTIL_LOGGER.info(interfaceAdditionCommand);
        //Thread.sleep(Long.parseLong(Optional.ofNullable(PropertyReader.getProperty("bus.modification.thread.sleep.time.in.millis")).orElse("0")));
        MqlUtil.mqlCommand(context, interfaceAdditionCommand);
    }

    public static void checkIfIterfaceExists(Context context, String businessObjectID, String interfaceName) throws FrameworkException, InterruptedException {
        StringBuilder commandBuilder = new StringBuilder();
        commandBuilder.append("print bus ").append(businessObjectID)
                .append(" select interface dump |");
        String existingInterfaceCheckQuery = commandBuilder.toString();
        COMMON_UTIL_LOGGER.info(existingInterfaceCheckQuery);
        String itemInterface = MqlUtil.mqlCommand(context, existingInterfaceCheckQuery);
        List<String> existingInterfaceList = Arrays.asList(itemInterface.split("\\|"));
        if (!NullOrEmptyChecker.isNullOrEmpty(existingInterfaceList)) {
            if (!existingInterfaceList.contains(interfaceName)) {
                CommonUtil.addInterface(context, businessObjectID, interfaceName);
            }
        }
    }

    public static String getItemTypeByInventoryUnit(String source, String inventoryUnit, String valComponentType, String valComponentMaterialType) throws Exception {
        List<String> ValComponentInventoryUnitList = new ArrayList<>();
        try {
            ValComponentInventoryUnitList = Arrays.asList(PropertyReader.getProperty(source + ".Enovia.inventory.unit.list.val.component").split("\\|"));
        } catch (Exception e) {
            COMMON_UTIL_LOGGER.error("Error for getting inventory unit list for type " + valComponentType + " from properties: " + e.getMessage());
            throw new Exception("Error for getting inventory unit list for type " + valComponentType + " from properties: " + e.getMessage());
        }
        if (NullOrEmptyChecker.isNullOrEmpty(ValComponentInventoryUnitList)) {
            throw new Exception("Error for getting inventory unit list for type " + valComponentType + " from properties");
        }

        List<String> ValComponentMaterialInventoryUnitList = new ArrayList<>();
        try {
            ValComponentMaterialInventoryUnitList = Arrays.asList(PropertyReader.getProperty(source + ".Enovia.inventory.unit.list.val.component.material").split("\\|"));
        } catch (Exception e) {
            COMMON_UTIL_LOGGER.error("Error for getting inventory unit list for type " + valComponentMaterialType + " from properties: " + e.getMessage());
            throw new Exception("Error for getting inventory unit list for type " + valComponentMaterialType + " from properties: " + e.getMessage());
        }
        if (NullOrEmptyChecker.isNullOrEmpty(ValComponentMaterialInventoryUnitList)) {
            throw new Exception("Error for getting inventory unit list for type " + valComponentMaterialType + " from properties");
        }

        if (ValComponentInventoryUnitList.contains(inventoryUnit)) {
            return valComponentType;
        } else if (ValComponentMaterialInventoryUnitList.contains(inventoryUnit)) {
            return valComponentMaterialType;
        } else {
            return "";
        }
    }

    public static boolean checkAllowedCheckInFileTypeList(String source, String checkabletype) throws Exception {
        boolean isAllowed = false;
        List<String> allowedCheckInFileTypeList = new ArrayList<>();
        try {
            String allowedCheckInFileType = PropertyReader.getProperty(source + ".Enovia.allowed.type.for.check.in");
            allowedCheckInFileTypeList = Arrays.asList(allowedCheckInFileType.split("\\|"));
        } catch (Exception e) {
            COMMON_UTIL_LOGGER.error("Error for getting allowed check in type list from properties: " + e.getMessage());
            throw new Exception("Error for getting allowed check in type list from properties:  " + e.getMessage());
        }

        if (NullOrEmptyChecker.isNullOrEmpty(allowedCheckInFileTypeList)) {
            COMMON_UTIL_LOGGER.error("Error for getting allowed check in type list from properties");
            throw new Exception("Error for getting allowed check in type list from properties");
        } else {
            for (String checkInType : allowedCheckInFileTypeList) {
                if (checkInType.isEmpty()) {
                    COMMON_UTIL_LOGGER.error("Error for getting allowed check in type list from properties");
                    throw new Exception("Error for getting allowed check in type list from properties");
                }
                if (checkabletype.equals(checkInType)) {
                    isAllowed = true;
                }
                break;
            }
        }
        return isAllowed;
    }

    public static boolean checkAllowedTypeListForCreateScopeLink(String source, String checkabletype) throws Exception {
        boolean isAllowed = false;
        List<String> allowedScopeLinkTypeList = new ArrayList<>();
        try {
            String allowedScopeLinkType = PropertyReader.getProperty(source + ".Enovia.allowed.type.for.scope.link");
            allowedScopeLinkTypeList = Arrays.asList(allowedScopeLinkType.split("\\|"));
        } catch (Exception e) {
            COMMON_UTIL_LOGGER.error("Error for getting allowed type list for scopelink from properties: " + e.getMessage());
            throw new Exception("Error for getting allowed type list for scopelink from properties:  " + e.getMessage());
        }

        if (NullOrEmptyChecker.isNullOrEmpty(allowedScopeLinkTypeList)) {
            COMMON_UTIL_LOGGER.error("Error for getting allowed type list for scopelink from properties");
            throw new Exception("Error for getting allowed type list for scopelink from properties");
        } else {
            for (String checkInType : allowedScopeLinkTypeList) {
                if (checkInType.isEmpty()) {
                    COMMON_UTIL_LOGGER.error("Error for getting allowed type list for scopelink from properties");
                    throw new Exception("Error for getting allowed type list for scopelink from properties");
                }
                if (checkabletype.equals(checkInType)) {
                    isAllowed = true;
                }
                break;
            }
        }
        return isAllowed;
    }

    public static synchronized String formatFileNotFoundErrorMessage(Exception ex, String fileName) {
        if (ex instanceof FileNotFoundException) {
            return MessageFormat.format(PropertyReader.getProperty("document.file.not.found"), fileName);
        } else {
            return ex.getMessage();
        }
    }
}
