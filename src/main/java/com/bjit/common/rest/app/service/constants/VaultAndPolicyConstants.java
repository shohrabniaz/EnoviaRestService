/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.app.service.constants;

import com.bjit.common.rest.app.service.utilities.NullOrEmptyChecker;
import java.util.HashMap;

/**
 *
 * @author BJIT
 */
public class VaultAndPolicyConstants {

    private static HashMap<String, String> VAULT_MAP;
    private static HashMap<String, String> POLICY_MAP;

    private static HashMap<String, String> getVAULT_MAP() {
        return VAULT_MAP;
    }

    private static void setVAULT_MAP(HashMap<String, String> VAULT_MAP) {
        VaultAndPolicyConstants.VAULT_MAP = VAULT_MAP;
    }

    private static HashMap<String, String> getPOLICY_MAP() {
        return POLICY_MAP;
    }

    private static void setPOLICY_MAP(HashMap<String, String> POLICY_MAP) {
        VaultAndPolicyConstants.POLICY_MAP = POLICY_MAP;
    }

    static {
        if (NullOrEmptyChecker.isNullOrEmpty(getVAULT_MAP())) {
            populateVaultMap();
        }

        if (NullOrEmptyChecker.isNullOrEmpty(getPOLICY_MAP())) {
            populatePolicyMap();
        }
    }

    public static String getObjectVault(String objectType) {
        String vault = getVAULT_MAP().get(objectType.toLowerCase());
        return vault;
    }

    public static String getObjectPolicy(String objectType) {
        String policy = getPOLICY_MAP().get(objectType.toLowerCase());
        return policy;
    }

    private static void populateVaultMap() {
        /**
         * Please use small letter case for the types name
         */
        HashMap<String, String> objectVaultMap = new HashMap<>();
        objectVaultMap.put("provide", "vplm");
        objectVaultMap.put("createassembly", "vplm");
        objectVaultMap.put("creatematerial", "vplm");
        objectVaultMap.put("processcontinuouscreatematerial", "vplm");
        objectVaultMap.put("rflvpmlogicalsystemreference", "vplm");
        objectVaultMap.put("schema_log", "vplm");
        objectVaultMap.put("enslogicalequipment", "vplm");
        objectVaultMap.put("hardware product", "eService Production");
        objectVaultMap.put("products", "eService Production");
        objectVaultMap.put("software product", "eService Production");
        objectVaultMap.put("service product", "eService Production");
        objectVaultMap.put("medical device product", "eService Production");
        objectVaultMap.put("product configuration", "eService Production");
        objectVaultMap.put("configuration feature", "eService Production");
        objectVaultMap.put("configuration option", "eService Production");
        
        setVAULT_MAP(objectVaultMap);
    }

    private static void populatePolicyMap() {
        /**
         * Please use small letter case for the types name
         */
        HashMap<String, String> objectPolicyMap = new HashMap<>();
        objectPolicyMap.put("provide", "VPLM_SMB_Definition");
        objectPolicyMap.put("createassembly", "VPLM_SMB_Definition");
        objectPolicyMap.put("creatematerial", "VPLM_SMB_Definition");
        objectPolicyMap.put("processcontinuouscreatematerial", "VPLM_SMB_Definition");
        objectPolicyMap.put("rflvpmlogicalsystemreference", "VPLM_SMB_Definition");
        objectPolicyMap.put("schema_log", "VPLM_SMB_Definition");
        objectPolicyMap.put("enslogicalequipment", "VPLM_SMB_Definition");
        objectPolicyMap.put("hardware product", "Product");
        objectPolicyMap.put("products", "Product");
        objectPolicyMap.put("software product", "Product");
        objectPolicyMap.put("service product", "Product");
        objectPolicyMap.put("medical device product", "Product");
        objectPolicyMap.put("product configuration", "Product Configuration");
        objectPolicyMap.put("configuration feature", "Configuration Feature");
        objectPolicyMap.put("configuration option", "Configuration Option");

        setPOLICY_MAP(objectPolicyMap);
    }
}
