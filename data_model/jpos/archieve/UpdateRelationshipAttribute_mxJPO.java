import com.matrixone.MCADIntegration.server.MCADServerResourceBundle;
import com.matrixone.apps.domain.DomainRelationship;
import com.matrixone.apps.domain.util.MqlUtil;
import matrix.db.Context;
import matrix.db.JPO;
import matrix.db.MQLCommand;
import matrix.db.Relationship;
import matrix.util.*;
import java.util.*;


/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Kayum-603
 */
public class UpdateRelationshipAttribute_mxJPO {
     protected MCADServerResourceBundle serverResourceBundle = null;
    public static void updateRelationshipAttributeWithConnID(Context context, String[] args) throws Exception {
          try {
             HashMap programMap = (HashMap) JPO.unpackArgs(args);
             //String relationshipId = (String) programMap.get("relationshipId");
             String connectionID = (String) programMap.get("connectionID");
             String attributeName = (String) programMap.get("attributeName");
             String attributeValue = (String) programMap.get("attributeValue");
             
             String mqlStatement = "";
             com.matrixone.apps.domain.util.ContextUtil.startTransaction ( context , true );
             MQLCommand objMQL = new MQLCommand();
             objMQL.open(context);
             mqlStatement = "modify connection " + connectionID + " '" + attributeName+"' '"+attributeValue+"'";
             String result = MqlUtil.mqlCommand(context, objMQL, mqlStatement);
             objMQL.close(context);
             com.matrixone.apps.domain.util.ContextUtil.commitTransaction ( context );
        }
        catch(Exception exp){
            com.matrixone.apps.domain.util.ContextUtil.abortTransaction ( context );
            throw exp;
        }  
        
       }
    
//      public String getRelatedObjects(Context context, String[] args) throws Exception {
//        HashMap programMap = (HashMap) JPO.unpackArgs(args);
//        String objectId = (String) programMap.get("objectId");
//        String relationshipName = (String) programMap.get("relationshipName");
//        
//        DomainObject domainObject = new DomainObject(objectId);
//        
//
//        
//        StringList objectSelects = new StringList();
//        objectSelects.addElement(DomainObject.SELECT_TYPE);
//        objectSelects.addElement(DomainObject.SELECT_NAME);
//        objectSelects.addElement(DomainObject.SELECT_REVISION);
//        
//        
//        //String ObjectName = domainObject.getInfo(context, "name");
//
//        
////        MapList classificationList = domainObject.getRelatedObjects(context, relationshipName, "*", objectSelects, null,
////                true,//boolean getTo,
////                false,//boolean getFrom,
////                (short) 1, null, null, 0);
////        return classificationList.toString();
////        
////        //return ObjectName;
//
//
//              Relationship relationship = new Relationship("44480.35242.18944.64095");
//              DomainRelationship domainRelationship = new DomainRelationship("44480.35242.18944.64095");
//              domainRelationship.open(context);
//              domainRelationship.setAttributeValue(context, "Quantity", "4.0");
//              //relationship.u
//             //AttributeList attributes =
//             return "done";
//    }
//        public static void updateRelationshipAttributeWithTNR(Context context, String[] args) throws Exception {
//          try {
//             String mqlStatement = "";
//             String fromObType = "Product Line";
//             String fromObName = "SoapTest";
//             String fromObRevision = "-";
//             String toObType = "Document";
//             String toObName = "SoapTestDocument-13380170559";
//             String toObRevision = "0";
//             String relName = "Reference Document";
//             String atrName = "Document Classification";
//             String atrValue = "s_tandered_changed";
//             
//             com.matrixone.apps.domain.util.ContextUtil.startTransaction ( context , true );
//             MQLCommand objMQL = new MQLCommand();
//             objMQL.open(context);
//             mqlStatement = "modify connection bus '"+fromObType+"' '"+fromObName+"' '" +fromObRevision+ "' to '"+toObType+ "' '"+toObName+ "' '" + toObRevision+ "' relationship '"+relName+"' '"+atrName+"' '"+atrValue+"'";
//             String result = MqlUtil.mqlCommand(context, objMQL, mqlStatement);
//             objMQL.close(context);
//             com.matrixone.apps.domain.util.ContextUtil.commitTransaction ( context );
//        }
//        catch(Exception exp){
//            com.matrixone.apps.domain.util.ContextUtil.abortTransaction ( context );
//            throw exp;
//        }  
//        
//       }
    
     /**
     * 
     * @param args
     * @param context
     * @return
     * @throws Exception 
     */
    public String updateRelatioshipAttributes(Context context, String [] args) throws Exception {
        //logger.debug("Update relationship attribute method");
        System.out.println("Update relationship attribute method");
        HashMap programMap = (HashMap) JPO.unpackArgs(args);
        java.util.List<HashMap<String, String>> attributeListOfMaps = (java.util.List<HashMap<String, String>>) programMap.get("attributeListOfMaps");
        HashMap<String, String> allRelAttrRelNameMap = (HashMap<String, String>) programMap.get("allRelAttrRelNameMap");
        java.util.List<String> relationshipAttrNameList = (java.util.List<String>) programMap.get("relationshipAttrNameList");
        int objectNumber = (int) programMap.get("objectNumber");
        String parentId = null;
        String updateStatus = "";
        String updateMessage = "";
            for (int i = 0; i < relationshipAttrNameList.size(); i++) {
                if (parentId == null) {
                    for (int k = objectNumber; k > 0; k--) {
                        System.out.println("Depth: " + Integer.parseInt(attributeListOfMaps.get(k).get("depth")));
                        if (Integer.parseInt(attributeListOfMaps.get(objectNumber).get("depth")) > Integer.parseInt(attributeListOfMaps.get(k - 1).get("depth"))) {
                            parentId = attributeListOfMaps.get(k - 1).get("id");
                            break;
                        }
                    }
                }
                String objectId = attributeListOfMaps.get(objectNumber).get("id");
                String relationshipAttributeName = relationshipAttrNameList.get(i);
                String relationshipName = allRelAttrRelNameMap.get(relationshipAttrNameList.get(i));
                String relationshipAttributeValue = attributeListOfMaps.get(objectNumber).get(relationshipAttrNameList.get(i));
                System.out.println("objId: " + objectId + ", Parent ID: " + parentId + ",atr Name: " + relationshipAttributeName + ", atr value: " + relationshipAttributeValue + ", relname: " + relationshipName);
                updateMessage = updateSingleRelationshipAttribute(context, objectId, parentId, relationshipName, relationshipAttributeName, relationshipAttributeValue);
                if ("".equals(updateStatus)) {
                    if (updateMessage != null && !"".equals(updateMessage)) {
                        updateStatus = updateMessage;
                    }
                } else if (updateMessage != null && !"".equals(updateMessage)) {
                    updateStatus = updateStatus + ", " + updateMessage;
                }
            }
        
        return updateStatus;
    }
    
    public String updateSingleRelationshipAttribute(Context context, String objectId, String parentId, String relationship, String relAttributeName, String relAtrributeValue) {
        String updateStatus = "";
        try {
            com.matrixone.apps.domain.util.ContextUtil.startTransaction(context, true);
            String mqlStatement = "";
            MQLCommand objMQL = new MQLCommand();
            objMQL.open(context);
            mqlStatement = "modify connection bus " + parentId + " to " + objectId + " relationship '" + relationship + "' '" + relAttributeName + "' '" + relAtrributeValue + "'";
            String result = MqlUtil.mqlCommand(context, objMQL, mqlStatement);
            updateStatus = updateStatus + result;
            System.out.println("Rel attribute update result: " + result);
            objMQL.close(context);
            com.matrixone.apps.domain.util.ContextUtil.commitTransaction(context);
        } catch (NumberFormatException | MatrixException e) {
            com.matrixone.apps.domain.util.ContextUtil.abortTransaction(context);
            System.out.println("Exception Occured while update rel attriburte: " + e.getMessage());
            String[] errorMessage = e.getMessage().trim().split("[\\r\\n]+");
            //logger.debug("Length of error message is: " + errorMessage.length);
            System.out.println("Length of error message is: " + errorMessage.length);
            if (errorMessage.length >= 2) {
                updateStatus = errorMessage[1];
                return updateStatus;
            }
            return e.getMessage().trim();
        }
        return updateStatus;
    }
        
          public static void updateRelationshipAttributeObjectID(Context context, String[] args) throws Exception {
          try {
             Relationship relationship = new Relationship("Logical Features");
             relationship.getAttributeValues(context);
             HashMap programMap = (HashMap) JPO.unpackArgs(args);
             //String relationshipId = (String) programMap.get("relationshipId");
             String fromObjectId = (String) programMap.get("fromObjectId");
             String toObjectId = (String) programMap.get("fromObjectId");
             String relationshipName = (String) programMap.get("relationshipName");
             String attributeName = (String) programMap.get("attributeName");
             String attributeValue = (String) programMap.get("attributeValue"); 
             
             String mqlStatement = "";
             com.matrixone.apps.domain.util.ContextUtil.startTransaction ( context , true );
             MQLCommand objMQL = new MQLCommand();
             objMQL.open(context);
             mqlStatement = "modify connection bus "+fromObjectId+" to "+toObjectId+" relationship '"+relationshipName+"' '"+attributeName+"' '"+attributeValue+"'";
             String result = MqlUtil.mqlCommand(context, objMQL, mqlStatement);
             objMQL.close(context);
             com.matrixone.apps.domain.util.ContextUtil.commitTransaction ( context );
        }
        catch(Exception exp){
            com.matrixone.apps.domain.util.ContextUtil.abortTransaction ( context );
            throw exp;
        }  
        
       }
          
    public Set<String> getAllRelationshipAttributes(Context paramContext, String[] args) throws Exception {
        java.util.Set<String> relationshipAttributes = new HashSet<String>();
        HashMap programMap = (HashMap) JPO.unpackArgs(args);
        Vector<String> attrVec = (Vector) programMap.get("relatioshipList");
        for (int i = 0; i < attrVec.size(); i++) {
            System.out.println("Relationship: " + attrVec.get(i));
            DomainRelationship domainRelationship = new DomainRelationship(attrVec.get(i).trim());
            Map relAtr = domainRelationship.getAttributeMap(paramContext);
            for (Iterator<Map.Entry<String, String>> it = relAtr.entrySet().iterator(); it.hasNext();) {
                Map.Entry<String, String> entry = it.next();
                relationshipAttributes.add(entry.getKey().trim());
                System.out.println("Rel attr:  " + entry.getKey());
            }
        }
        return relationshipAttributes;
    }

    public Vector getRelationIdsBetweenObjects(Context paramContext, String[] args) throws Exception {

        HashMap programMap = (HashMap) JPO.unpackArgs(args);
        String paramString1 = (String) programMap.get("objId");
        String paramString2 = (String) programMap.get("parentId");

        Vector localVector = new Vector();

        String[] arrayOfString = new String[5];
        arrayOfString[0] = paramString1;
        arrayOfString[1] = "relationship";
        arrayOfString[2] = "id";
        arrayOfString[3] = ("to.id == const'" + paramString2 + "' || from.id == const'" + paramString2 + "'");
        arrayOfString[4] = "|";

        String str1 = executeMQL(paramContext, "expand bus $1 select $2 $3 where $4 dump $5", arrayOfString);
        if (str1.startsWith("true|")) {
            StringTokenizer localStringTokenizer = new StringTokenizer(str1, "\n");
            while (localStringTokenizer.hasMoreElements()) {
                String str2 = (String) localStringTokenizer.nextElement();
                String str3 = str2.substring(str2.lastIndexOf("|") + 1);
                if ((!localVector.contains(str3)) && (!str3.equals(""))) {
                    localVector.addElement(str3);
                }
            }
        }
        return localVector;
    }

    public Vector getRelationNamesBetweenObjects(Context paramContext, String[] args) throws Exception {

        HashMap programMap = (HashMap) JPO.unpackArgs(args);
        String paramString1 = (String) programMap.get("objId");
        String paramString2 = (String) programMap.get("parentId");

        Vector localVector = new Vector();

        String[] arrayOfString = new String[5];
        arrayOfString[0] = paramString1;
        arrayOfString[1] = "relationship";
        arrayOfString[2] = "name";
        arrayOfString[3] = ("to.id == const'" + paramString2 + "' || from.id == const'" + paramString2 + "'");
        arrayOfString[4] = "|";

        String str1 = executeMQL(paramContext, "expand bus $1 select $2 $3 where $4 dump $5", arrayOfString);
        if (str1.startsWith("true|")) {
            StringTokenizer localStringTokenizer = new StringTokenizer(str1, "\n");
            while (localStringTokenizer.hasMoreElements()) {
                String str2 = (String) localStringTokenizer.nextElement();
                String str3 = str2.substring(str2.lastIndexOf("|") + 1);
                if ((!localVector.contains(str3)) && (!str3.equals(""))) {
                    localVector.addElement(str3);
                }
            }
        }
        return localVector;
    }

    public String executeMQL(Context paramContext, String paramString, String[] paramArrayOfString) {
        String str = "";
        try {
            if (paramContext != null) {
                MQLCommand localMQLCommand = new MQLCommand();
                boolean bool = localMQLCommand.executeCommand(paramContext, paramString, paramArrayOfString);
                if (bool) {
                    str = localMQLCommand.getResult();
                    if ((str != null) && (str.length() == 0)) {
                    }
                    str = "true|" + str;
                } else {
                    str = localMQLCommand.getError();
                    str = "false|" + str;
                }
                if (str.endsWith("\n")) {
                    str = str.substring(0, str.lastIndexOf("\n"));
                }
            } else {
                str = "false|" + this.serverResourceBundle.getString("mcadIntegration.Server.Message.InvalidContext");
            }
        } catch (MatrixException localMatrixException) {
            str = "false|" + localMatrixException.getMessage();
        }
        return str;
    }
}
