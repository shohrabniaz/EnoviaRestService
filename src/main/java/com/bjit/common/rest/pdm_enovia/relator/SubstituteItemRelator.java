package com.bjit.common.rest.pdm_enovia.relator;

import java.util.Map;
import matrix.db.BusinessObject;
import matrix.db.Context;
import matrix.db.Relationship;
import matrix.db.RelationshipList;
import matrix.db.RelationshipType;
import matrix.util.MatrixException;
import org.apache.log4j.Logger;

/**
 *
 * @author Mashuk/BJIT
 */
public class SubstituteItemRelator {
    
    private static final Logger RELATOR_LOGGER = Logger.getLogger(SubstituteItemRelator.class);
    /** Relationship name 'MfgProcessAlternate' **/
    /*public static void relate(Context context, Map<String, String> itemCodeItemTypeMap,
            String parentItem, String alternateItem) throws MatrixException {
        BusinessObject parentBus = new BusinessObject(itemCodeItemTypeMap.get(parentItem));
        BusinessObject childBus = new BusinessObject(itemCodeItemTypeMap.get(alternateItem));
        if (parentBus.exists(context) && childBus.exists(context)) {
            Boolean isRelationExists = isRelationExists(context, childBus, itemCodeItemTypeMap.get(parentItem));
            if (!isRelationExists) {
                RelationshipType relationShipType = new RelationshipType(Constants.relationTypeNameAlternateItem);
                parentBus.connect(context, relationShipType, true, childBus);
            }
        }
        RELATOR_LOGGER.debug("Item/Substitute added : " + itemCodeItemTypeMap.get(parentItem) + "/" + itemCodeItemTypeMap.get(alternateItem));
    }
    
    public static Boolean isRelationExists(Context context, BusinessObject childBus, String parentObjectId) throws MatrixException {
        RelationshipList toRelationshipList = childBus.getToRelationship(context);
        Boolean isAlternate = false;
        for (Relationship toRelationship : toRelationshipList) {
            if (!isAlternate && toRelationship.getTypeName().equals(Constants.relationTypeNameAlternateItem)) {
                if (toRelationship.getFrom().getObjectId().equals(parentObjectId)) {
                    isAlternate = true;
                }
            }
        }
        return isAlternate;
    }*/
}
