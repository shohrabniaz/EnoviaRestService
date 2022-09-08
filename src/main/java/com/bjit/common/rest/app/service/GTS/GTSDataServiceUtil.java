/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.app.service.GTS;

import com.bjit.common.rest.app.service.utilities.DisableSSLCertificate;
import com.bjit.common.rest.app.service.utilities.NullOrEmptyChecker;
import com.bjit.ewc18x.utils.PropertyReader;
import com.bjit.ex.integration.transfer.util.ApplicationProperties;
import com.bjit.ex.integration.transfer.util.RequestUtil;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Sarwar
 */
public class GTSDataServiceUtil {

    private static final org.apache.log4j.Logger GTSBundleIdResponse_LOGGER = org.apache.log4j.Logger.getLogger(GTSDataServiceUtil.class);

    private HashMap<String, String> abbreviationDataMap = new HashMap<>();

    public GTSDataServiceUtil(String bundleId) throws IOException, MalformedURLException, KeyManagementException, NoSuchAlgorithmException {
        try {
            String table = "all";
            String bundle_id = bundleId;
            String params = "table=" + table + "&" + "bundle_id=" + bundle_id;
            String gtsUrl = ApplicationProperties.getProprtyValue("gts.service.bundleId.url");
            String url = gtsUrl + params;
            System.out.println("URL ::: " + url);
            DisableSSLCertificate.DisableCertificate();
            String responseResult = RequestUtil.getResponse(url);
            System.out.println("Response Result ::: " + responseResult);
            Result result = new Gson().fromJson(responseResult, Result.class);
            HashMap<String, String> LNLanguageIdMap = (HashMap<String, String>) ApplicationProperties.getLNLanguageIDMap();

            if (result.getData().size() > 0) {
                for (int i = 0; i < result.getData().get(0).getDataList().size(); i++) {
                    String lang = result.getData().get(0).getDataList().get(i).getLanguage();
                    String abbreviation = result.getData().get(0).getDataList().get(i).getAbbreviation();

                    if (!lang.equalsIgnoreCase("English") && abbreviation.length() > 0) {
                        String langCode = LNLanguageIdMap.get(lang);
                        if (langCode != null && langCode.length() > 0) {
                            abbreviationDataMap.put(langCode.split(";")[1], abbreviation);

                        }
                    }
                }
                setAbbreviationDataMap(abbreviationDataMap);
                GTSBundleIdResponse_LOGGER.info("Abbreviation Data Map : " + abbreviationDataMap);
            }
        } catch (KeyManagementException | NoSuchAlgorithmException ex) {
            GTSBundleIdResponse_LOGGER.trace(ex);
            throw ex;
        }
    }

    /**
     * Search gts info using bundle id and language
     *
     * @author Sarwar BJIT
     * @param bundleId
     * @param table/Language
     * @return Title from GTS
     * @throws IOException
     * @throws MalformedURLException
     * @throws KeyManagementException
     * @throws NoSuchAlgorithmException
     */
    public static String getGTSTitle(String bundleId, String table) throws IOException, MalformedURLException, KeyManagementException, NoSuchAlgorithmException {
        try {
            GTSBundleIdResponse_LOGGER.debug("Searching GTS title using bundle id:  " + bundleId);
            String params = "table=" + table + "&" + "bundle_id=" + bundleId;

            if (NullOrEmptyChecker.isNullOrEmpty(table)) {
                GTSBundleIdResponse_LOGGER.debug("table Name : " + table);
                return "";
            }

            if (NullOrEmptyChecker.isNullOrEmpty(bundleId)) {
                GTSBundleIdResponse_LOGGER.debug("bundleId : " + bundleId);
                return "";
            }

            String gtsUrl = PropertyReader.getProperty("gts.service.url");
            String url = gtsUrl + params;
            GTSBundleIdResponse_LOGGER.info("URL ::: " + url);
            DisableSSLCertificate.DisableCertificate();
            String responseResult = RequestUtil.getResponse(url);
            GTSBundleIdResponse_LOGGER.debug("Response Result ::: " + responseResult);

            Result result = new Gson().fromJson(responseResult, Result.class);
            String title = "";
            if (result != null && result.getData() != null && !result.getData().isEmpty()) {
                if (result.getData().get(0).getDataList() != null && !result.getData().get(0).getDataList().isEmpty()) {
                    title = result.getData().get(0).getDataList().get(0).getText();
                }
            }

            return title;
        } catch (KeyManagementException | NoSuchAlgorithmException ex) {
            GTSBundleIdResponse_LOGGER.trace(ex.getMessage());
            throw ex;
        }
    }

    public static String getGTSTitle(String bundleId, String primaryLang, String secondaryLang, HashMap<String, String> languageMap) throws IOException, MalformedURLException, KeyManagementException, NoSuchAlgorithmException {
        try {
            if (NullOrEmptyChecker.isNullOrEmpty(bundleId)) {
                GTSBundleIdResponse_LOGGER.debug("bundleId : " + bundleId);
                return "";
            }

            String params = "table=all" + "&" + "bundle_id=" + bundleId;

            String gtsUrl = PropertyReader.getProperty("gts.service.url");
            String url = gtsUrl + params;
            DisableSSLCertificate.DisableCertificate();
            String responseResult = RequestUtil.getResponse(url);
            Result result = new Gson().fromJson(responseResult, Result.class);
            String title = "";
            if (result != null && result.getData() != null && !result.getData().isEmpty()) {
                List<GTSInfo> dataList = result.getData().get(0).getDataList();
                if (!NullOrEmptyChecker.isNull(dataList)) {
                    HashMap<String, GTSInfo> gtsInfoMap = new HashMap<>();
                    dataList.forEach(gtsInfo -> {
                        gtsInfoMap.put(gtsInfo.getLanguage(), gtsInfo);
                    });
                    String primaryTitle = "";
                    boolean isEnglishAlreadyIncluded = false;
                    if (!NullOrEmptyChecker.isNull(gtsInfoMap.get(languageMap.get(primaryLang)))) {
                        primaryTitle = primaryLang.toUpperCase() + ": " + gtsInfoMap.get(languageMap.get(primaryLang)).getText();;
                        if (primaryLang.equalsIgnoreCase("en")) {
                            isEnglishAlreadyIncluded = true;
                        }
                    } else {
                        primaryTitle = "EN: " + gtsInfoMap.get("English").getText();;
                        isEnglishAlreadyIncluded = true;
                    }
                    if (!NullOrEmptyChecker.isNullOrEmpty(secondaryLang)) {
                        String secondaryTitle = "";
                        if (!NullOrEmptyChecker.isNull(gtsInfoMap.get(languageMap.get(secondaryLang)))
                                && !(secondaryLang.equalsIgnoreCase("en") && isEnglishAlreadyIncluded)) {
                            secondaryTitle = "\n" + secondaryLang.toUpperCase() + ": " + gtsInfoMap.get(languageMap.get(secondaryLang)).getText();
                        } else {
                            if (!isEnglishAlreadyIncluded) {
                                secondaryTitle = "\n" + "EN: " + gtsInfoMap.get("English").getText();
                            }
                        }
                        title = primaryTitle + secondaryTitle;
                    } else {
                        return primaryTitle;
                    }
                }
            }

            return title;
        } catch (KeyManagementException | NoSuchAlgorithmException ex) {
            throw ex;
        }
    }

    public static HashMap<String, String> getTermIdTitleMap(HashSet<String> bundleIdList, String primaryLang, String secondaryLang, HashMap<String, String> languageMap) {
        HashMap<String, String> bundleTitleMap = new HashMap<>();
        int bundleIdLimit = Integer.parseInt(PropertyReader.getProperty("gts.bundle.id.limit.per.request"));
        Iterator<String> bundleListItr = bundleIdList.iterator();
        GTSBundleIdResponse_LOGGER.info("Number of unique Bundle IDs : " + bundleIdList.size());
        try {
            if (NullOrEmptyChecker.isNull(bundleIdList)) {
                GTSBundleIdResponse_LOGGER.debug("bundleId List : " + bundleIdList);
                return bundleTitleMap;
            }
            boolean disableSSLCertificate = Boolean.parseBoolean(PropertyReader.getProperty("gts.disable.ssl.certificate"));
            if (disableSSLCertificate) {
                DisableSSLCertificate.DisableCertificate();
            }
            while (bundleListItr.hasNext()) {
                int count = 0;
                StringBuilder bundleIdThisSlot = new StringBuilder();
                while (bundleListItr.hasNext()) {
                    bundleIdThisSlot
                            .append(bundleListItr.next())
                            .append(";");
                    count++;
                    bundleListItr.remove();
                    if (count == bundleIdLimit) {
                        break;
                    }
                }
                if (bundleIdThisSlot.length() > 0
                        && bundleIdThisSlot.charAt(bundleIdThisSlot.length() - 1) == ';') {
                    bundleIdThisSlot.setLength(bundleIdThisSlot.length() - 1);
                }
                StringBuilder gtsUrl = new StringBuilder(PropertyReader.getProperty("gts.service.url"))
                        .append("table=all&bundle_id=")
                        .append(bundleIdThisSlot.toString());

                String responseResult = RequestUtil.getResponse(gtsUrl.toString());
                Result result = new Gson().fromJson(responseResult, Result.class);
                if (result != null && result.getData() != null && !result.getData().isEmpty()) {
                    List<Data> data = result.getData();
                    data.forEach(dataNode -> {
                        List<GTSInfo> dataList = dataNode.getDataList();
                        if (!NullOrEmptyChecker.isNull(dataList)) {
                            StringBuilder gtsTitle = new StringBuilder();
                            HashMap<String, GTSInfo> gtsInfoMap = new HashMap<>();
                            dataList.forEach(gtsInfo -> gtsInfoMap.put(gtsInfo.getLanguage(), gtsInfo));
                            String primaryTitle = "";
                            boolean isEnglishAlreadyIncluded = false;
                            if (!NullOrEmptyChecker.isNull(gtsInfoMap.get(languageMap.get(primaryLang)))) {
                                primaryTitle = primaryLang.toUpperCase() + ": " + gtsInfoMap.get(languageMap.get(primaryLang)).getText();;
                                if (primaryLang.equalsIgnoreCase("en")) {
                                    isEnglishAlreadyIncluded = true;
                                }
                            } else {
                                primaryTitle = "EN: " + gtsInfoMap.get("English").getText();;
                                isEnglishAlreadyIncluded = true;
                            }
                            if (!NullOrEmptyChecker.isNullOrEmpty(secondaryLang)) {
                                String secondaryTitle = "";
                                if (!NullOrEmptyChecker.isNull(gtsInfoMap.get(languageMap.get(secondaryLang)))
                                        && !(secondaryLang.equalsIgnoreCase("en") && isEnglishAlreadyIncluded)) {
                                    secondaryTitle = "\n" + secondaryLang.toUpperCase() + ": " + gtsInfoMap.get(languageMap.get(secondaryLang)).getText();
                                } else {
                                    if (!isEnglishAlreadyIncluded) {
                                        secondaryTitle = "\n" + "EN: " + gtsInfoMap.get("English").getText();
                                    }
                                }
                                gtsTitle.append(primaryTitle).append(secondaryTitle);
                            } else {
                                gtsTitle.append(primaryTitle);
                            }
                            bundleTitleMap.put(dataNode.getBundleId().toString(), gtsTitle.toString());
                        }
                    });
                }
            }
            return bundleTitleMap;
        } catch (IOException ex) {
            GTSBundleIdResponse_LOGGER.error(ex.getMessage());
            return bundleTitleMap;
        } catch (JsonSyntaxException | KeyManagementException | NoSuchAlgorithmException ex) {
            GTSBundleIdResponse_LOGGER.error(ex.getMessage());
            return bundleTitleMap;
        } catch (Exception ex) {
            GTSBundleIdResponse_LOGGER.error(ex.getMessage());
            return bundleTitleMap;
        }
    }

    public HashMap<String, String> getAbbreviationDataMap() {
        return abbreviationDataMap;
    }

    private void setAbbreviationDataMap(HashMap<String, String> abbreviationDataMap) {
        this.abbreviationDataMap = abbreviationDataMap;
    }

}
