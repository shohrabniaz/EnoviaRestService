/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.app.service.GTS.translation;

import com.bjit.common.rest.app.service.model.Translation.BundleAndText;
import java.util.List;
import java.util.Map;
import matrix.db.Context;

/**
 * Interface to Translation Service.
 *
 * @author BJIT
 */
public interface TranslationService {

    public Map<String, String> getPhysicalIdFromBundleID(Context context, List<String> bundleIds);

    public String updateTitleForBundles(Context context, String objectId, BundleAndText bundleAndText);

}
