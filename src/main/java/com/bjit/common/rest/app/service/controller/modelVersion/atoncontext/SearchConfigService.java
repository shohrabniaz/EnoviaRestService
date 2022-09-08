package com.bjit.common.rest.app.service.controller.modelVersion.atoncontext;

import com.bjit.common.rest.app.service.model.tnr.TNR;
import com.matrixone.apps.domain.util.FrameworkException;
import java.util.HashMap;
import java.util.List;
import matrix.db.Context;

/**
 *
 * @author Touhidul Islam
 */
public interface SearchConfigService {

    public abstract AtonResponseModel execute(AtonRequestModel reqModel,
                                              String contextItemType,
                                              Context context, String source) throws Exception;

    public String getTopMfgItem(TNR m, String contextItemType, Context context) throws FrameworkException, Exception;
  
    public List<HashMap<String, String>> getMfgItemInfo(TNR m,
                                                        String sourceRevision,
                                                        String topItemType,
                                                        String childItemType,
                                                        Context context) throws FrameworkException, Exception;
  
}
