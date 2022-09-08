import java.util.HashMap;
import matrix.db.JPO;
import matrix.util.MatrixException;
import matrix.db.Attribute;
import matrix.db.AttributeList;
import matrix.db.AttributeType;
import matrix.db.BusinessInterface;
import matrix.db.BusinessObject;
import matrix.db.Context;
import matrix.db.Relationship;
import matrix.db.RelationshipType;
import matrix.db.Vault;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Sajjad
 */
public class CreateRelationship_mxJPO {
    public String connectBO(Context context, String[] args){
        try {
        HashMap programMap = (HashMap) JPO.unpackArgs(args);
        String relationshipName = (String) programMap.get("RelationshipName");
        String interfaceName = (String) programMap.get("InterfaceName");
        String parentID = (String) programMap.get("ParentID");
        String childID = (String) programMap.get("ChildID");
        HashMap<String,String> attributeNameValueMap = (HashMap<String,String>) programMap.get("AttributeNameValueMap");
        String relID = "";
        
            BusinessObject parentBO = new BusinessObject(parentID);
            BusinessObject childBO = new BusinessObject(childID);
            
            RelationshipType relationshipType = new RelationshipType();
            relationshipType.setName(relationshipName);
            
            AttributeList attributeList = createAttributeList(attributeNameValueMap);
            
            Relationship relationship = parentBO.connect(context, relationshipType, true, childBO);
            relationship.open(context);
            if (interfaceName != null && interfaceName.length() > 0) {
                Vault vault = new Vault("");
                BusinessInterface businessInterface = new BusinessInterface(interfaceName, vault);
                relationship.addBusinessInterface(context, businessInterface);
            }            
            relationship.setAttributeValues(context, attributeList);
            relationship.close(context);
            return relationship.getName();
        } catch (Exception ex) {
            return ex.getMessage();
        } 
        //return "relID";
    }
    private AttributeList createAttributeList(HashMap<String,String> attributeNameValueMap){
        AttributeList attributeList = new AttributeList();
        for(String attributeName : attributeNameValueMap.keySet()){
            AttributeType at = new AttributeType(attributeName);
            Attribute attribute = new Attribute(at, attributeNameValueMap.get(attributeName));
            attributeList.addElement(attribute);
        }
        return attributeList;
    }
}
