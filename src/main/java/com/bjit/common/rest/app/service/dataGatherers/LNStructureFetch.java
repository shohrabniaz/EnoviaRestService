package com.bjit.common.rest.app.service.dataGatherers;

import com.bjit.compareBOM.Action.BOMCompareAction;
import com.bjit.compareBOM.MultiLevelBomDataModel.BomLineBean;
import com.bjit.compareBOM.MultiLevelBomDataModel.MultilevelBomDetailsBean;
import org.apache.log4j.Logger;

/**
 *
 * @author Tahmid
 */
public class LNStructureFetch {

    private static final Logger LN_STRUCTURE_LOGGER = Logger.getLogger(LNStructureFetch.class);

    public static Object getLNStructure(String type, String name, String rev, String expandLevel) {
        BomLineBean lnRootItem = null;
        try {
            BOMCompareAction bomCompareAction = new BOMCompareAction();
            MultilevelBomDetailsBean lnBOMDetail = bomCompareAction.fatchMultiLevelBOMfromLN(type, name, rev, expandLevel);
            lnRootItem = lnBOMDetail.getStructure();
        } catch (Exception e) {
            LN_STRUCTURE_LOGGER.error("Error::---> ", e);
            LN_STRUCTURE_LOGGER.error(e.getMessage());
            return e.getMessage();
        }
        return lnRootItem;
    }
}
