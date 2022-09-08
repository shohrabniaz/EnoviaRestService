/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.app.service.manager.GTS;

import com.bjit.common.rest.app.service.GTS.translation.TranslationService;
import com.bjit.common.rest.app.service.model.Translation.BundleAndText;
import com.bjit.common.rest.app.service.model.Translation.Translation;
import com.bjit.common.rest.app.service.payload.common_response.Status;
import com.bjit.common.rest.app.service.payload.translation_response.ITranslationResponse;
import com.bjit.common.rest.app.service.payload.translation_response.TranslationResponseBuilder;
import com.bjit.ewc18x.utils.PropertyReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import matrix.db.Context;
import org.apache.commons.lang.time.FastDateFormat;
import org.apache.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

/**
 * purpose: to translation enovia title according to gts text and abbreviation
 * value
 *
 * @author BJIT
 */
public final class TranslationManager {

    private final TranslationService translationService;

    private static final Logger TRANSLATION_MANAGER_LOGGER = Logger.getLogger(TranslationManager.class);
    private String translationLanguage;
    private final String modificationStartDate;
    private final String modificationEndDate;
    private final ITranslationResponse responseBuilder;
    private TranslationConfig translationConfig;

    public TranslationManager(TranslationService translationService) throws Exception {
        this.translationConfig = new TranslationConfig();
        this.translationService = translationService;
        this.modificationStartDate = translationConfig.getLastUpdateTimestamp();
        this.modificationEndDate = FastDateFormat.getInstance("yyyy-MM-dd HH:mm:ss").format(System.currentTimeMillis());
        this.responseBuilder = new TranslationResponseBuilder();
    }

    /**
     * To set translation language
     *
     * @param translationLanguage
     */
    public void setTranslationLanguage(String translationLanguage) {
        this.translationLanguage = translationLanguage;
    }

    /**
     * To get Translation from gts
     *
     * @param language
     * @return
     * @throws java.lang.Exception
     */
    public ResponseEntity<LinkedHashMap> getTranslationFromGts() throws Exception {
        String gtsUrl = PropertyReader.getProperty("gts.service.url");
        String gtsTranslationsUrl = gtsUrl + "start_date=" + modificationStartDate + "&end_date=" + modificationEndDate + "&mode=details&language=" + this.translationLanguage;
        TRANSLATION_MANAGER_LOGGER.info("GTS Translation URL : " + gtsTranslationsUrl);
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<LinkedHashMap> translationDetails = null;
        try {
            translationDetails = restTemplate.getForEntity(gtsTranslationsUrl, LinkedHashMap.class);
        } catch (RestClientException e) {
            TRANSLATION_MANAGER_LOGGER.info("Exception occured during translation fetch, cause" + e.getMessage());
            throw new Exception("Exception occured during translation information fetch, cause" + e.getMessage());
        }

        return translationDetails;
    }

    /**
     * To get Translation from bundle details list
     *
     * @param gtsBundleDetailsList
     * @return
     * @throws java.lang.Exception
     */
    /* Sample Data to parse:
        {
        "65262": {
                "English": {
                    "text": "AKID ENGLISH",
                    "abbreviation": "AKID ENGLISH"
                }
            },
        "65263": {
                "English": {
                    "text": "Another Text",
                    "abbreviation": "Another Abbreviation"
                }
            }
        } */
    public Translation getTranslationFromBundleDetailsList(List<Map<String, Object>> gtsBundleDetailsList) throws Exception {
        if (gtsBundleDetailsList == null) {
            throw new Exception("Bundle details list null!");
        }
        Translation translation = new Translation();
        List<HashMap<String, BundleAndText>> bundleAndTextList = new ArrayList();
        HashMap<String, BundleAndText> translationMap = new HashMap();
        try {
            gtsBundleDetailsList.forEach((Map<String, Object> gtsBundleDetails) -> {
                Set<String> bundleIds = gtsBundleDetails.keySet();
                for (String bundleId : bundleIds) {
                    Map<String, Object> translationObj = (Map<String, Object>) gtsBundleDetails.get(bundleId);
                    Map<String, Object> textAndAttribute = (Map<String, Object>) translationObj.get(translationLanguage);
                    BundleAndText bundleAndText = new BundleAndText();
                    bundleAndText.setText((String) textAndAttribute.get("text"));
                    bundleAndText.setAbbreviation((String) textAndAttribute.get("abbreviation"));
                    translationMap.put(bundleId, bundleAndText);
                }
            });

            bundleAndTextList.add(translationMap);
            translation.setBundleAndText(bundleAndTextList);
        } catch (Exception e) {
            TRANSLATION_MANAGER_LOGGER.info("Exception occured during translation parsing, cause: " + e.getMessage());
            throw new Exception("Exception occured during translation parsing, cause: " + e.getMessage());
        }

        return translation;
    }

    /**
     * This method is called to update gts translations which are not updated in
     * enovia
     *
     * @param context
     * @param translation
     * @return ResponseEntity
     * @throws java.lang.Exception
     */
    public ResponseEntity updateObjectsTitle(Context context, Translation translation) throws Exception {
        if (translation == null) {
            throw new Exception("Translation is null!");
        }
        String buildResponse = "";

        List<String> successMessages = new ArrayList<>();
        List<String> errorMessages = new ArrayList<>();
        try {
            Set<String> bundleIdSet = new HashSet<>();
            long startAPITime = System.currentTimeMillis();
            for (int i = 0; i < translation.getBundleAndText().size(); i++) {
                translation.getBundleAndText().get(i)
                        .forEach((key, value) -> {
                            bundleIdSet.add(key);
                        });
            }
            // ignore if any repeatitive bundle id is there
            List<String> bundleIds = new ArrayList<>();
            bundleIds.addAll(bundleIdSet);
            Map<String, String> finalPhysicalIdFromBundleID = new LinkedHashMap<>();
            String bundleIdQueryRequestCount = translationConfig.getBundleIdQueryRequestCount();

            TRANSLATION_MANAGER_LOGGER.info("Total Bundle Ids: " + bundleIds.size());
            /* Fixing implemented for [VSIX-4324] - Full Translation from GTS
                    to update enovia Title and shortname in English*/
            for (int i = 0; i < bundleIds.size(); i += Integer.parseInt(bundleIdQueryRequestCount)) {
                int limit = (bundleIds.size() - i) > Integer.parseInt(bundleIdQueryRequestCount) ? i + Integer.parseInt(bundleIdQueryRequestCount) : bundleIds.size();
                List<String> bundleIdSubList = bundleIds.subList(i, limit);
                TRANSLATION_MANAGER_LOGGER.info("Total Requested bundle ids in search : " + bundleIdSubList.size());
                Map<String, String> physicalIdFromBundleID = translationService.getPhysicalIdFromBundleID(context, bundleIdSubList);
                TRANSLATION_MANAGER_LOGGER.info("Total Physical Ids found : " + physicalIdFromBundleID.size());

                finalPhysicalIdFromBundleID.putAll(physicalIdFromBundleID);
            }

            TRANSLATION_MANAGER_LOGGER.info("Total Object Item to modify : " + finalPhysicalIdFromBundleID.size());
            if (finalPhysicalIdFromBundleID.isEmpty()) {
                buildResponse = responseBuilder
                        .setStatus(Status.OK)
                        .setMessage("No machtching item was found to update for this request")
                        .setData("[]")
                        .buildResponse();
                TRANSLATION_MANAGER_LOGGER.info(buildResponse);

                return new ResponseEntity<>(buildResponse, HttpStatus.OK);
            }

            Map<String, BundleAndText> bundleTitleMap = new HashMap<>();
            translation.getBundleAndText().forEach(bundleTextMap
                    -> bundleTextMap.forEach(
                            (bundleId, bundleAndText)
                            -> {
                        TRANSLATION_MANAGER_LOGGER.info("Requested Bundle Id : " + bundleId);
                        TRANSLATION_MANAGER_LOGGER.info("Requested Title : " + bundleAndText.getText() + ", Requested  ShortTitle : " + bundleAndText.getAbbreviation());
                        bundleTitleMap.put(bundleId, bundleAndText);
                    }));

            TRANSLATION_MANAGER_LOGGER.info("Total Bundle Id : " + bundleTitleMap.size());
            long startQueryTime = 0;
            if (!bundleTitleMap.isEmpty()) {
                TRANSLATION_MANAGER_LOGGER.info("Update Query Execution Start -------------- ");
                startQueryTime = System.currentTimeMillis();
                finalPhysicalIdFromBundleID.forEach((objectId, bundleId) -> {
                    String responseValue = translationService.updateTitleForBundles(context, objectId, bundleTitleMap.get(bundleId));
                    if (responseValue.equals("Success")) {
                        successMessages.add(responseValue);
                        int j = successMessages.size();
                        TRANSLATION_MANAGER_LOGGER.info("Successfully Updated Object Item " + j + " : " + objectId + " And BundleId : " + bundleId);
                    } else {
                        errorMessages.add(responseValue);
                        TRANSLATION_MANAGER_LOGGER.info("Update failed for, objectId: " + objectId + ", bundleId: " + bundleId);
                    }
                });

            }

            if (successMessages.isEmpty() && errorMessages.isEmpty()) {
                buildResponse = responseBuilder
                        .setStatus(Status.OK)
                        .setMessage("Total matching item found " + finalPhysicalIdFromBundleID.size() + " , Successfully update 0 , Failed to update " + finalPhysicalIdFromBundleID.size() + "")
                        .setData("[]")
                        .buildResponse();
                TRANSLATION_MANAGER_LOGGER.info(buildResponse);
                return new ResponseEntity<>(buildResponse, HttpStatus.OK);

            }
            long endQueryTime = System.currentTimeMillis();
            TRANSLATION_MANAGER_LOGGER.info("Update Query Execution End ----------");
            TRANSLATION_MANAGER_LOGGER.info("Update Query Execution Total Time :  " + (endQueryTime - startQueryTime));
            TRANSLATION_MANAGER_LOGGER.info("API Execution End ---- ");
            long endAPITime = System.currentTimeMillis();
            TRANSLATION_MANAGER_LOGGER.info("API Execution Total Time(ms) :  " + (endAPITime - startAPITime));

            if (finalPhysicalIdFromBundleID.size() > successMessages.size()) {
                buildResponse = responseBuilder
                        .setStatus(Status.PARTIAL)
                        .setMessage("Total matching item found " + finalPhysicalIdFromBundleID.size() + " , Successfully Title Update " + successMessages.size() + ", Failed to Update " + (finalPhysicalIdFromBundleID.size() - successMessages.size()) + ".")
                        .setData("[]")
                        .buildResponse();
                TRANSLATION_MANAGER_LOGGER.info(buildResponse);
                return new ResponseEntity<>(buildResponse, HttpStatus.OK);
            } else if (finalPhysicalIdFromBundleID.size() == successMessages.size()) {
                buildResponse = responseBuilder
                        .setStatus(Status.OK)
                        .setMessage("Total matching item found " + finalPhysicalIdFromBundleID.size() + " , Successfully Title Update " + successMessages.size() + ", Failed to Update 0 .")
                        .setData("[]")
                        .buildResponse();
                TRANSLATION_MANAGER_LOGGER.info(buildResponse);
                return new ResponseEntity<>(buildResponse, HttpStatus.OK);
            } else if (finalPhysicalIdFromBundleID.size() == errorMessages.size()) {
                buildResponse = responseBuilder
                        .setStatus(Status.FAILED)
                        .setMessage("Total matching item found " + finalPhysicalIdFromBundleID.size() + " , Successfully Title Update 0, Failed to Update " + errorMessages + ".")
                        .setData("[]")
                        .buildResponse();
                TRANSLATION_MANAGER_LOGGER.info(buildResponse);
                return new ResponseEntity<>(buildResponse, HttpStatus.OK);
            } else {
                buildResponse = responseBuilder
                        .setStatus(Status.OK)
                        .setMessage("Total matching item found " + finalPhysicalIdFromBundleID.size() + " , Successfully Title Update " + successMessages.size() + ", Failed to Update " + (finalPhysicalIdFromBundleID.size() - successMessages.size()) + ".")
                        .setData("[]")
                        .buildResponse();
                TRANSLATION_MANAGER_LOGGER.info(buildResponse);
                return new ResponseEntity<>(buildResponse, HttpStatus.OK);
            }
        } catch (Exception e) {
            buildResponse = responseBuilder.setStatus(Status.FAILED).addErrorMessage(e.getMessage()).setData("[]").buildResponse();
            TRANSLATION_MANAGER_LOGGER.error(buildResponse);
            return new ResponseEntity<>(buildResponse, HttpStatus.NOT_ACCEPTABLE);
        }

    }

    /**
     * To update scheduler timestamp
     *
     * @throws java.lang.Exception
     */
    public void updateSchedulerTimestamp() throws Exception {
        translationConfig.updateSchedulerTimestamp(modificationEndDate);
    }

}
