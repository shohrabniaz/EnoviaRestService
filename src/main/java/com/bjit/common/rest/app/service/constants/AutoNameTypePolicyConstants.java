/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.app.service.constants;

import com.bjit.common.rest.app.service.utilities.NullOrEmptyChecker;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author BJIT
 */
public class AutoNameTypePolicyConstants {

    private static HashMap<String, String> symbolicType;
    private static HashMap<String, String> symbolicPolicy;
    private static Map<String, String> cADTypeDisciplinePackageMap;

    private static HashMap<String, String> getSymbolicType() {
        return symbolicType;
    }

    private static void setSymbolicType(HashMap<String, String> symbolicType) {
        AutoNameTypePolicyConstants.symbolicType = symbolicType;
    }

    private static HashMap<String, String> getSymbolicPolicy() {
        return symbolicPolicy;
    }

    private static void setSymbolicPolicy(HashMap<String, String> symbolicPolicy) {
        AutoNameTypePolicyConstants.symbolicPolicy = symbolicPolicy;
    }

    private static Map<String, String> getcADTypeDisciplinePackageMap() {
        return cADTypeDisciplinePackageMap;
    }

    private static void setcADTypeDisciplinePackageMap(Map<String, String> cADTypeDisciplinePackageMap) {
        AutoNameTypePolicyConstants.cADTypeDisciplinePackageMap = cADTypeDisciplinePackageMap;
    }

    static {
        if (NullOrEmptyChecker.isNullOrEmpty(getSymbolicType())) {
            populateSymbolicType();
        }

        if (NullOrEmptyChecker.isNullOrEmpty(getSymbolicPolicy())) {
            populateSymbolicPolicy();
        }

        if (NullOrEmptyChecker.isNullOrEmpty(getcADTypeDisciplinePackageMap())) {
            populateCADTypeDisciplinePackageMap();
        }
    }

    private static void populateSymbolicType() {
        HashMap<String, String> objectSymbolicType = new HashMap<>();
        objectSymbolicType.put("createassembly", "type_CreateAssembly");
        objectSymbolicType.put("processcontinuouscreatematerial", "type_ProcessContinuousCreateMaterial");
        objectSymbolicType.put("provide", "type_Provide");
        objectSymbolicType.put("processcontinuousprovide", "type_ProcessContinuousProvide");
        objectSymbolicType.put("creatematerial", "type_CreateMaterial");
        objectSymbolicType.put("hardware product", "type_HardwareProduct");
        objectSymbolicType.put("products", "type_Products");
        objectSymbolicType.put("software product", "type_SoftwareProduct");
        objectSymbolicType.put("service product", "type_ServiceProduct");
        objectSymbolicType.put("medical device product", "type_MedicalDeviceProduct");
        objectSymbolicType.put("product configuration", "type_ProductConfiguration");
        objectSymbolicType.put("configuration feature", "type_ConfigurationFeature");
        objectSymbolicType.put("configuration option", "type_ConfigurationOption");

        setSymbolicType(objectSymbolicType);
    }

    private static void populateSymbolicPolicy() {
        HashMap<String, String> objectSymbolicPolicy = new HashMap<>();
        objectSymbolicPolicy.put("createassembly", "policy_VPLM_SMB_Definition");
        objectSymbolicPolicy.put("processcontinuouscreatematerial", "policy_VPLM_SMB_Definition");
        objectSymbolicPolicy.put("provide", "policy_VPLM_SMB_Definition");
        objectSymbolicPolicy.put("processcontinuousprovide", "policy_VPLM_SMB_Definition");
        objectSymbolicPolicy.put("creatematerial", "policy_VPLM_SMB_Definition");
        objectSymbolicPolicy.put("hardware product", "policy_Product");
        objectSymbolicPolicy.put("products", "policy_Product");
        objectSymbolicPolicy.put("software product", "policy_Product");
        objectSymbolicPolicy.put("service product", "policy_Product");
        objectSymbolicPolicy.put("medical device product", "policy_Product");
        objectSymbolicPolicy.put("product configuration", "policy_ProductConfiguration");
        objectSymbolicPolicy.put("configuration feature", "policy_ConfigurationFeature");
        objectSymbolicPolicy.put("configuration option", "policy_ConfigurationOption");

        setSymbolicPolicy(objectSymbolicPolicy);
    }

    private static void populateCADTypeDisciplinePackageMap() {
        Map<String, String> catiaPackageMap = new HashMap<>();
        catiaPackageMap.put("CreateAssembly", "DELAsmAssemblyModelDisciplines");
        catiaPackageMap.put("ElementaryEndItem", "DELAsmAssemblyModelDisciplines");
        catiaPackageMap.put("ProcessContinuousCreateMaterial", "DELAsmAssemblyModelDisciplines");
        catiaPackageMap.put("ProcessContinuousProvide", "DELAsmAssemblyModelDisciplines");
        catiaPackageMap.put("Provide", "DELAsmAssemblyModelDisciplines");
        catiaPackageMap.put("DELLmiGeneralSystemReference", "DELLmiProductionGeneralSystem");
        catiaPackageMap.put("DELLmiProdSystemIOPort", "DELLmiProductionSystemIOPort");
        catiaPackageMap.put("PPRContext", "DELPPRContextModelDisciplines");
        catiaPackageMap.put("PLMBusinessRule", "PLMKnowHowBusinessRule");
        catiaPackageMap.put("VPMReference", "PRODUCTCFG");
        catiaPackageMap.put("3DShape", "PRODUCTDiscipline");

        setcADTypeDisciplinePackageMap(catiaPackageMap);
    }

    public static String getObjectSymbolicType(String objectType) {
        return getSymbolicType().get(objectType.toLowerCase());
    }

    public static String getObjectSymbolicPolicy(String objectType) {
        return getSymbolicPolicy().get(objectType.toLowerCase());
    }

    /**
     * To get CAD Type Discipline Package by object type from configuration
     *
     * @param objectType
     * @return CAD Type Discipline Package
     */
    public static String getCADTypeDisciplinePackageMap(String objectType) {
        return getcADTypeDisciplinePackageMap().get(objectType.toLowerCase());
    }
}
