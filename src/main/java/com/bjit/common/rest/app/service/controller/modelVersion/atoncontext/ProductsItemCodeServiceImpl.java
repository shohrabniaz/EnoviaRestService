package com.bjit.common.rest.app.service.controller.modelVersion.atoncontext;

import com.bjit.common.code.utility.dsapi.portfolio.model.Graph;
import com.bjit.common.code.utility.dsapi.portfolio.model.ResponseModel;
import com.bjit.common.code.utility.dsapi.portfolio.model.Version;
import com.bjit.ewc18x.utils.PropertyReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class ProductsItemCodeServiceImpl implements ProductsItemCodeService {

    private static final Logger logger = Logger.getLogger(ProductsItemCodeServiceImpl.class.getName());

    @Override
    public Map<String, Map<String, Boolean>> execute(ResponseModel response, Map<String, List<String>> quesList, String source) {
        ArrayList<Graph> graphs = response.getGraphs();
        Map<String, Map<String, Boolean>> responseMap = new HashMap<>();

        for (String mvName : quesList.keySet()) {
            List<String> atonRevisionsList = quesList.get(mvName);

            // pick one model and search for version
            for (Graph g : graphs) {
                if (g.getItem().getCode().equals(mvName)) {
                    ArrayList<Version> versions = g.getVersions();
                    Map<String, Boolean> mvRevisionsFlag = this.checkRevisionExistence(versions,
                                                                                       atonRevisionsList, source);


                    responseMap.put(mvName, mvRevisionsFlag);
                    break;
                }
            }
        }
        return responseMap;
    }

    private Map<String, Boolean> checkRevisionExistence(ArrayList<Version> versions,
            List<String> atonRevisionsList, String source)
    {
        Map<String, Boolean> revisionFlags = new HashMap<>();
        List<String> versionsExists = new ArrayList<>();

        // first preparing a flag
        for (Version v : versions) {
            String r = null;
            if(source.equalsIgnoreCase(PropertyReader.getProperty("aton.integration.source"))){
                r = v.getAtonVersion();
            } else {
                r = v.getRevision();
            }
            //String status = v.getMOD_AUTLifecycleStatus();
            if (r != null && !r.isEmpty()) {
                versionsExists.add(r);
            }
        }

        // common elements is exist in the system
        for (String searchRev : atonRevisionsList) {
            if (versionsExists.contains(searchRev)) {
                revisionFlags.put(searchRev, true);
            } else {
                revisionFlags.put(searchRev, false);
            }
        }

        return revisionFlags;
    }

}
