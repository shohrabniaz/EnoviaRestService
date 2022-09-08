/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.app.service.comosData.project_structure.utilities;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.bjit.common.rest.app.service.utilities.NullOrEmptyChecker;
import matrix.db.BusinessInterface;
import matrix.db.BusinessInterfaceList;
import matrix.db.BusinessObject;
import matrix.db.Context;
import matrix.db.Vault;
import matrix.util.MatrixException;

/**
 *
 * @author BJIT
 */
public class ComosProjectStructureObjectUtility {

    private static final org.apache.log4j.Logger OBJECT_UTILITY_LOGGER = org.apache.log4j.Logger.getLogger(ComosProjectStructureObjectUtility.class);

    public static Boolean isNullOrEmpty(String value) {
        return isNull(value) || value.equalsIgnoreCase("") || value.isEmpty();
    }

    public static Boolean isNullOrEmpty(HashMap checkMap) {
        return isNull(checkMap) || checkMap.isEmpty();
    }

    public static Boolean isNullOrEmpty(List checkList) {
        return isNull(checkList) || checkList.isEmpty();
    }

    public static Boolean isNullOrEmpty(Object[] checkArray) {
        return isNull(checkArray) || checkArray.length == 0;
    }

    public static Boolean isNull(Object checkObject) {
        return checkObject == null;
    }

    public static String capitalizeFirstLetter(String original) {
        if (!isNullOrEmpty(original)) {
            return original.substring(0, 1).toUpperCase() + original.substring(1);
        }
        return original;
    }

    public void addInterface(final Context context, final String buisnessObjectid, String interfaceNamesInXMLMap, String vault) throws MatrixException {

        try {
            List<String> interfaceListFromMap = Arrays.asList(Optional.ofNullable(interfaceNamesInXMLMap).orElse("").trim().split(",")).stream().map(String::trim).collect(Collectors.toList());

            if (NullOrEmptyChecker.isNullOrEmpty(interfaceListFromMap)) {
                return;
            }

            BusinessObject busnessObject = new BusinessObject(buisnessObjectid);
            busnessObject.open(context);
            List<String> existingInterfaceList = getInterfacesInBusinessObject(busnessObject, context);

            OBJECT_UTILITY_LOGGER.debug("#################################################################");
            OBJECT_UTILITY_LOGGER.debug("Business Object : 'Type' : '" + busnessObject.getTypeName() + "' 'Name' : '" + busnessObject.getName() + "' 'Revision' : '" + busnessObject.getRevision() + "'");
            OBJECT_UTILITY_LOGGER.debug("#################################################################");

            interfaceListFromMap.removeAll(existingInterfaceList);

            interfaceListFromMap.forEach((String newInterfaceName) -> {
                try {
                    addInterfaceToTheBusinessObject(context, busnessObject, newInterfaceName, vault);
                } catch (MatrixException ex) {
                    OBJECT_UTILITY_LOGGER.error(ex);
                }
            });
        } catch (MatrixException ex) {
            OBJECT_UTILITY_LOGGER.error(ex);
            throw ex;
        }
    }

    private List<String> getInterfacesInBusinessObject(BusinessObject busnessObject, final Context context) throws MatrixException {
        BusinessInterfaceList existingInterfaceList = busnessObject.getBusinessInterfaces(context, true);
        List<String> existingBusinessObjectInterfaceList = new ArrayList<>();
        existingInterfaceList.stream().forEach((BusinessInterface businessInterface) ->{
            try {
                businessInterface.open(context);
                existingBusinessObjectInterfaceList.add(businessInterface.getName());
                
            } catch (MatrixException ex) {
                OBJECT_UTILITY_LOGGER.error(ex);
            }
        });
        
        return existingBusinessObjectInterfaceList;
    }

    public void addInterfaceToTheBusinessObject(Context context, BusinessObject businessObject, String interfaceName, String vault) throws MatrixException {
        try {
            if (NullOrEmptyChecker.isNullOrEmpty(interfaceName)) {
                return;
            }

            interfaceName = interfaceName.trim();
            Vault vaultObject = new Vault(vault);
            BusinessInterface businessInterface = new BusinessInterface(interfaceName, vaultObject);
            businessObject.addBusinessInterface(context, businessInterface);
            OBJECT_UTILITY_LOGGER.info("Added interface is : '" + interfaceName + "'");
        } catch (MatrixException exp) {
            OBJECT_UTILITY_LOGGER.error(exp.getMessage());
            throw exp;
        } catch (Exception exp) {
            OBJECT_UTILITY_LOGGER.error(exp.getMessage());
            throw exp;
        }
    }
}
