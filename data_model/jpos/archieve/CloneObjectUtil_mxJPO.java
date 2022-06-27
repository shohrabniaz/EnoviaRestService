/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Tomal
 */
import ch.qos.logback.classic.util.ContextInitializer;
import com.dassault_systemes.VPLMJDocumentServices.VPLMJDocumentServices;
import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.util.FrameworkException;
import com.matrixone.apps.domain.util.FrameworkUtil;
import com.matrixone.apps.domain.util.MqlUtil;
import com.matrixone.apps.domain.util.PropertyUtil;
import com.matrixone.apps.program.ProgramCentralUtil;
import com.matrixone.jdl.MatrixRMISession;
import com.matrixone.jdl.MatrixSession;
import com.matrixone.client.fcs.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import matrix.db.Attribute;
import matrix.db.AttributeList;
import matrix.db.AttributeType;
import matrix.db.BusinessInterface;
import matrix.db.BusinessObject;
import matrix.db.BusinessObjectItr;
import matrix.db.BusinessObjectList;
import matrix.db.Context;
import matrix.db.JPO;
import matrix.db.MQLCommand;
import matrix.db.Query;
import matrix.db.State;
import matrix.db.Vault;
import matrix.util.MatrixException;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import com.dassault_systemes.requirements.UnifiedAutonamingServices;
import java.lang.reflect.Method;
import java.util.Arrays;
import matrix.db.BusinessInterfaceList;

public class CloneObjectUtil_mxJPO {

    public String searchProjectKey(Context context, String[] args) throws MatrixException, Exception {
        HashMap programMap = (HashMap) JPO.unpackArgs(args);
        String PsProjectKey = (String) programMap.get("PsProjectKey");
        String result = "";
        try {
            Query quuery = new Query("");
            quuery.setBusinessObjectType("Project Space");
            quuery.setWhereExpression("attribute[PS_Project_Key]==" + PsProjectKey);
            BusinessObjectList list = quuery.evaluate(context);
            BusinessObjectItr itr = new BusinessObjectItr(list);

            while (itr.next()) {
                BusinessObject bo = itr.obj();
                result = bo.getObjectId();
            }
        } catch (MatrixException ex) {
            System.out.println("Search failed " + ex.getMessage());
            throw ex;
        }
        return result;
    }

    public List getObjectByTypeAttribute(Context context, String[] args) throws MatrixException, Exception {
        HashMap programMap = (HashMap) JPO.unpackArgs(args);
        String objectType = (String) programMap.get("objectType");
        String attributeName = (String) programMap.get("attributeName");
        String attributeValue = (String) programMap.get("attributeValue");
        List<String> objectIdList = new ArrayList();
        try {
            Query quuery = new Query("");
            quuery.setBusinessObjectType(objectType);
            quuery.setWhereExpression(attributeName + "==" + attributeValue);
            BusinessObjectList list = quuery.evaluate(context);
            BusinessObjectItr itr = new BusinessObjectItr(list);

            while (itr.next()) {
                BusinessObject bo = itr.obj();
                objectIdList.add(bo.getObjectId());
            }
        } catch (MatrixException ex) {
            System.out.println("Search failed " + ex.getMessage());
            throw ex;
        }
        return objectIdList;
    }

    public List getObjectIdByName(Context context, String[] args) throws MatrixException, Exception {
        HashMap programMap = (HashMap) JPO.unpackArgs(args);
        String name = (String) programMap.get("name");
        List<String> objectIdList = new ArrayList();
        try {
            Query quuery = new Query("");
            quuery.setBusinessObjectName(name);
            BusinessObjectList list = quuery.evaluate(context);
            BusinessObjectItr itr = new BusinessObjectItr(list);

            while (itr.next()) {
                BusinessObject bo = itr.obj();
                objectIdList.add(bo.getObjectId());
            }
        } catch (MatrixException ex) {
            System.out.println("Search failed " + ex.getMessage());
            throw ex;
        }
        return objectIdList;
    }

    public String searchByTypeName(Context context, String[] args) throws MatrixException, Exception {
        HashMap programMap = (HashMap) JPO.unpackArgs(args);
        String name = (String) programMap.get("name");
        String type = (String) programMap.get("type");
        String result = "";
        try {
            Query quuery = new Query("");
            quuery.setBusinessObjectName(name);
            quuery.setBusinessObjectType(type);
            BusinessObjectList list = quuery.evaluate(context);
            BusinessObjectItr itr = new BusinessObjectItr(list);

            while (itr.next()) {
                BusinessObject bo = itr.obj();
                result = bo.getObjectId();
                break;
            }
        } catch (MatrixException ex) {
            System.out.println("Search failed " + ex.getMessage());
            throw ex;
        }
        return result;
    }

    public String searchTask(Context context, String[] args) throws MatrixException, Exception {
        HashMap programMap = (HashMap) JPO.unpackArgs(args);
        String taskName = (String) programMap.get("taskName");
        MQLCommand objMQL = new MQLCommand();
        objMQL.open(context);
        String sMQLStatement = "temp query bus 'Task' '" + taskName + "' * select id dump;";
        String result = MqlUtil.mqlCommand(context, objMQL, sMQLStatement);
        objMQL.close(context);

        return result.equals(null) || result.equals("") ? "" : result.split(",")[3];
    }

    /*public String getObjectIdByName(Context context, String[] args) throws MatrixException, Exception {
        HashMap programMap = (HashMap) JPO.unpackArgs(args);
        String type = (String) programMap.get("type");
        String name = (String) programMap.get("name");
        String revision = (String) programMap.get("revision");

        String mqlStatement = null;
        String result = null;
        
        Boolean revisionCheck = revision == null;
        if(revisionCheck){
            mqlStatement = "temp query bus '" + type + "' '" + name + "' '*' limit 1 select id dump;";
        }
        else{
            mqlStatement = "print bus '" + type + "' '" + name + "' '" + revision + "' select id dump;";
        }
        
        try{
            MQLCommand mqlCmd = new MQLCommand();
            mqlCmd.open(context);
            result = MqlUtil.mqlCommand(context, mqlCmd, mqlStatement);
            mqlCmd.close(context);
        }catch(Exception exp){
            result = null;
        }
        
        if(!ProgramCentralUtil.isNullString(result)){
            if(revisionCheck){
                return result.split(",")[3];
            }
            return result;
        }

        return "";
    }*/
    public String getSymbolicTypeNameJpo(Context context, String[] args) throws MatrixException, Exception {
        HashMap programMap = (HashMap) JPO.unpackArgs(args);
        String objectId = (String) programMap.get("objectId");
        DomainObject domainObject = new DomainObject(objectId);
        System.out.println(domainObject.getInfo(context, DomainConstants.SELECT_TYPE) + "----" + domainObject.getInfo(context, DomainConstants.SELECT_POLICY));
        String symbolicTypeName = PropertyUtil.getAliasForAdmin(context, "Type", domainObject.getInfo(context, DomainConstants.SELECT_TYPE), true);//
        String symbolicPolicyName = PropertyUtil.getAliasForAdmin(context, "Policy", domainObject.getInfo(context, DomainConstants.SELECT_POLICY), true);//


        return "symbolicTypeName : '" + symbolicTypeName + "'    symbolicPolicyName :'" + symbolicPolicyName + "'";
    }
    
    public String getAutoNameBySymbolicTypePolicyJpo(Context context, String[] args) throws MatrixException, Exception {
        HashMap programMap = (HashMap) JPO.unpackArgs(args);
        String symbolicTypeName = (String) programMap.get("symbolicTypeName");
        String symbolicPolicyName = (String) programMap.get("symbolicPolicyName");

        String projectName = FrameworkUtil.autoName(context,
                symbolicTypeName,
                null,
                symbolicPolicyName,
                null,
                null,
                true,
                true);
        return projectName;
    }

    public String getAutoNameJpo(Context context, String[] args) throws MatrixException, Exception {
        HashMap programMap = (HashMap) JPO.unpackArgs(args);
        String objectId = (String) programMap.get("objectId");
        DomainObject domainObject = new DomainObject(objectId);
        System.out.println(domainObject.getInfo(context, DomainConstants.SELECT_TYPE) + "----" + domainObject.getInfo(context, DomainConstants.SELECT_POLICY));
        String symbolicTypeName = PropertyUtil.getAliasForAdmin(context, "Type", domainObject.getInfo(context, DomainConstants.SELECT_TYPE), true);//
        String symbolicPolicyName = PropertyUtil.getAliasForAdmin(context, "Policy", domainObject.getInfo(context, DomainConstants.SELECT_POLICY), true);//

        String projectName = FrameworkUtil.autoName(context,
                symbolicTypeName,
                null,
                symbolicPolicyName,
                null,
                null,
                true,
                true);
        System.out.println(projectName);
        return projectName;
    }

    public String getAutoNameOfManObjectType(Context context, String[] args) throws MatrixException, Exception {
        HashMap programMap = (HashMap) JPO.unpackArgs(args);
        String typePackage = (String) programMap.get("typePackage");
        String type = (String) programMap.get("type");
        String generatedAutoName = "";
        try {
            //final Class<UnifiedAutonamingServices> unifiedAutonamingServicesClass = UnifiedAutonamingServices.class;
            Class[] argTypes = new Class[]{Context.class, String.class, String.class};
            System.out.println("Type :" + type + "Package :" + typePackage);
            Object[] arguments = new Object[]{context, typePackage, type};
            final Method getAutonamedId = UnifiedAutonamingServices.class.getDeclaredMethod("getAutonamedId", (argTypes));
            getAutonamedId.setAccessible(true);
            final Object[] objects = Arrays.copyOfRange(arguments, 1, arguments.length);
            generatedAutoName = (String) getAutonamedId.invoke(null, arguments);
            System.out.println("Generated AutoNAME : " + generatedAutoName);
        } catch (Exception exp) {
            exp.printStackTrace();
        }
        return generatedAutoName;
    }

    public void updateProperty(Context context, String objectId, HashMap propertyMap) throws FrameworkException {
        try {
            Set keySetPropertyMap = propertyMap.keySet();
            DomainObject domainObject = DomainObject.newInstance(context, objectId);
            for (Object keyEntry : keySetPropertyMap) {
                System.out.println("Property Undate :" + (String) keyEntry + " Value:" + propertyMap.get((String) keyEntry));
                System.out.println("Setting current as:" + propertyMap.get((String) keyEntry));
                domainObject.setState(context, (String) propertyMap.get((String) keyEntry));
            }
        } catch (FrameworkException ef) {
            throw ef;
        }
    }

    public void updateObject(Context context, String[] args) throws MatrixException, Exception {
        HashMap programMap = (HashMap) JPO.unpackArgs(args);
        String objectId = (String) programMap.get("objectId");
        HashMap attributeMap = (HashMap) programMap.get("attributeMap");
        HashMap propertyMap = (HashMap) programMap.get("propertyMap");
        System.out.println("Updating object!!!!");

        Set keySetAttributeMap = attributeMap.keySet();

        updateProperty(context, objectId, propertyMap);

        try {
            BusinessObject businessObject = new BusinessObject(objectId);
            AttributeList attributeList = new AttributeList();
            AttributeType attributeType = null;
            Attribute attribute = null;
            for (Object key : keySetAttributeMap) {
                attributeType = new AttributeType((String) key);
                attribute = new Attribute(attributeType, (String) attributeMap.get(key));
                // System.out.println("Key :" + (String) key + "Value :" + attributeMap.get(key));
                attributeList.addElement(attribute);
            }
            businessObject.setAttributeValues(context, attributeList);
        } catch (MatrixException ex) {
            System.out.println("Updating failed reasons :" + ex.getMessage());
            throw ex;
        }
    }

    public List updateObjectMultiAttribute(Context context, String[] args) throws MatrixException, Exception {
        HashMap programMap = (HashMap) JPO.unpackArgs(args);
        String objectId = (String) programMap.get("objectId");
        HashMap attributeMap = (HashMap) programMap.get("attributeMap");

        List<Object> errorList = new ArrayList();
        errorList.add(objectId);

        Set keySetAttributeMap = attributeMap.keySet();

        StringBuilder updateAttributes = new StringBuilder();
        String mqlStatement = null;
        MQLCommand objMQL = new MQLCommand();
        objMQL.open(context);

        HashMap<Object, String> errorMap = new HashMap();
        for (Object key : keySetAttributeMap) {
            updateAttributes
                    .append("\"")
                    .append(key)
                    .append("\"")
                    .append(" ")
                    .append("\"")
                    .append(attributeMap.get(key))
                    .append("\" ");
        }
        try {
            mqlStatement = "escape modify bus '" + objectId + "' " + updateAttributes.toString();
            MqlUtil.mqlCommand(context, objMQL, mqlStatement);
        } catch (Exception exp) {
            String exception = exp.getMessage();
            if (exception.contains(": ")) {
                try {
                    String[] onlyCause = exception.split(": ");
                    errorMap.put("Error", onlyCause[onlyCause.length - 1]);
                } catch (Exception ex) {
                    errorMap.put("Error", "Error message not found");
                }
            }
        } finally {
            updateAttributes.setLength(0);
        }
//        }
        objMQL.close(context);

        errorList.add(errorMap);
        errorList.add(mqlStatement);

        return errorList;
    }

    public List updateObjectAttribute(Context context, String[] args) throws MatrixException, Exception {
        HashMap programMap = (HashMap) JPO.unpackArgs(args);
        String objectId = (String) programMap.get("objectId");
        HashMap attributeMap = (HashMap) programMap.get("attributeMap");

        List<Object> errorList = new ArrayList();
        errorList.add(objectId);

        Set keySetAttributeMap = attributeMap.keySet();

        StringBuilder updateAttributes = new StringBuilder();
        String mqlStatement = null;
        MQLCommand objMQL = new MQLCommand();
        objMQL.open(context);

        HashMap<Object, String> errorMap = new HashMap();

        for (Object key : keySetAttributeMap) {
            updateAttributes
                    .append("'")
                    .append(key)
                    .append("'")
                    .append(" ")
                    .append("'")
                    .append(attributeMap.get(key))
                    .append("'");
            try {
                mqlStatement = "escape modify bus '" + objectId + "' " + updateAttributes.toString();
                MqlUtil.mqlCommand(context, objMQL, mqlStatement);
            } catch (Exception exp) {
                String exception = exp.getMessage();
                if (exception.contains(": ")) {
                    try {
                        String[] onlyCause = exception.split(": ");
                        errorMap.put(key, onlyCause[onlyCause.length - 1]);
                    } catch (Exception ex) {
                        errorMap.put(key, "Error message not found");
                    }
                }
            } finally {
                updateAttributes.setLength(0);
            }
        }
        objMQL.close(context);

        errorList.add(errorMap);

        return errorList;
    }

    public List updateObjectsAttributes(Context context, String[] args) throws MatrixException, Exception {
        HashMap programMap = (HashMap) JPO.unpackArgs(args);
        HashMap objectsMap = (HashMap) programMap.get("objects");
        Iterator it = objectsMap.entrySet().iterator();

        ArrayList<HashMap<String, Object>> finalErrorList = new ArrayList();

        MQLCommand objMQL = new MQLCommand();
        objMQL.open(context);

        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            String objectId = (String) pair.getKey();
            HashMap attributeMap = (HashMap) pair.getValue();
            HashMap<String, Object> errorMap = new HashMap();
            List<String> errorList = new ArrayList();
//            List<Object> errorList = new ArrayList();
            //errorList.add(objectId);

            errorMap.put("objectId", objectId);
            Set keySetAttributeMap = attributeMap.keySet();

            StringBuilder updateAttributes = new StringBuilder();
            String mqlStatement = null;
//            MQLCommand objMQL = new MQLCommand();
//            objMQL.open(context);

            for (Object key : keySetAttributeMap) {
                updateAttributes
                        .append("'")
                        .append(key)
                        .append("'")
                        .append(" ")
                        .append("'")
                        .append(attributeMap.get(key))
                        .append("'");
                try {
                    mqlStatement = "escape modify bus '" + objectId + "' " + updateAttributes.toString();
                    MqlUtil.mqlCommand(context, objMQL, mqlStatement);
                } catch (Exception exp) {
                    String exception = exp.getMessage();
                    if (exception.contains(": ")) {
                        try {
                            String[] onlyCause = exception.split(": ");
                            //errorMap.put(key, onlyCause[onlyCause.length - 1]);
                            errorList.add(onlyCause[onlyCause.length - 1]);
                        } catch (Exception ex) {
                            errorMap.put(key.toString(), "Error message not found");
                        }
                    }
                } finally {
                    updateAttributes.setLength(0);
                }
            }

            //errorList.add(errorMap);
            errorMap.put("message", errorList);
            finalErrorList.add(errorMap);

        }
        objMQL.close(context);
        return finalErrorList;
    }

    public void addInterfaceToObject(Context context, String[] args) throws Exception {
        System.out.println("Adding interface:");
        HashMap programMap = (HashMap) JPO.unpackArgs(args);
        String objectId = (String) programMap.get("objectId");
        String interfaceName = (String) programMap.get("interfaceName");
        //String mqlStatement = null;
        try {
            Vault vault = new Vault("");
            BusinessObject businessObject = new BusinessObject(objectId);
            BusinessInterfaceList businessInterfaces = businessObject.getBusinessInterfaces(context, true);
            for (int i = 0; i < businessInterfaces.size(); i++) {
                BusinessInterface interFace = (BusinessInterface) businessInterfaces.get(i);
                interFace.open(context);
                if (interFace.getName().equalsIgnoreCase(interfaceName)) {
                    System.out.println("Interface already exists");
                    return;
                }
            }
            BusinessInterface businessInterface = new BusinessInterface(interfaceName, vault);
            businessObject.addBusinessInterface(context, businessInterface);
        } catch (MatrixException mex) {
            System.out.println("Interface addition failed");
            throw mex;
        }
    }

    public String projectSapceCreateAndUpdateAttr(Context context, String[] args) throws Exception {
        System.out.println("\n\n-----projectSapceCreateAndUpdateAttr--------");
        String projectTemplateId = "";
        String initargs[] = {};
        HashMap params = new HashMap();
        String newProjectId = "";
        try {
            projectTemplateId = (String) ((HashMap) JPO.unpackArgs(args)).get("ProjectTemplateId");
            System.out.println("Project Template id is : " + projectTemplateId);
        } catch (Exception e) {
            System.out.println("projectTemplateId ::: " + projectTemplateId);
            java.util.logging.Logger.getLogger(CloneObjectUtil_mxJPO.class.getName()).log(java.util.logging.Level.SEVERE, null, e);
            e.printStackTrace();
        }
        if (projectTemplateId.length() > 0) {
            params = setTemplateProjectParam(params, projectTemplateId);
        } else {
            params = setBlankProjectParam(params);
        }
        params.put("objectId", null);
        params.put("Name", "");
        params.put("autoNameCheck", "true");
        params.put("Description", "TESTING.....");
        params.put("BusinessUnitOID", "");
        params.put("ProgramOID", "");
        params.put("BusinessGoalOID", "");
        params.put("BaseCurrency", "Dollar");
        params.put("projectVault", "eService Production");
        params.put("ProjectVisibility", "Members");
        params.put("Policy", "Project Space");
        params.put("ScheduleFrom", "Project Start Date");
        params.put("ProjectDate", "Mar 28, 2028");
        params.put("DefaultConstraintType", "As Soon As Possible");
        params.put("TypeActual", "Project Space");
        params.put("connectRelatedProject", null);
        params.put("timeZone", "-6");
        try {
            com.matrixone.apps.domain.util.ContextUtil.startTransaction(context, true);

            System.out.println("Creating new project");
            Map result = (Map) JPO.invoke(context, "emxProjectSpaceBase", initargs, "createNewProject", JPO.packArgs(params), Map.class);
            newProjectId = (String) result.get("id");
            System.out.println("new Project Id ID " + newProjectId);

            String argsForAddInterface[] = {};
            HashMap argMapForAddInterface = new HashMap();
            argMapForAddInterface.put("objectId", newProjectId);
            argMapForAddInterface.put("interfaceName", "PS_ProjectSpace");

            JPO.invoke(context, "CloneObjectUtil", argsForAddInterface, "addInterfaceToObject", JPO.packArgs(argMapForAddInterface), String.class);
            System.out.println("OK TILL NOW");
            HashMap argsMap = (HashMap) JPO.unpackArgs(args);
            /*Updating project space key value*/
            String PS_Project_Key = (String) argsMap.get("PS_Project_Key");
            System.out.println("PS PROJECT KEY:" + PS_Project_Key);
            BusinessObject projectBus = new BusinessObject(newProjectId);
            AttributeType at = new AttributeType("PS_Project_Key");
            Attribute atrbt = new Attribute(at, PS_Project_Key);
            AttributeList al = new AttributeList();
            al.add(atrbt);
            projectBus.setAttributeValues(context, al);
            com.matrixone.apps.domain.util.ContextUtil.commitTransaction(context);
            System.out.println("\n\nnewProjectId :: " + newProjectId);
        } catch (Exception ex) {
            System.out.println("################ new Project Id ID ################## " + newProjectId);
            com.matrixone.apps.domain.util.ContextUtil.abortTransaction(context);
            java.util.logging.Logger.getLogger(CloneObjectUtil_mxJPO.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
            throw ex;
        }
        return newProjectId;
    }

    private HashMap setTemplateProjectParam(HashMap params, String projectTemplateId) {
        params.put("createProject", "Template");
        params.put("SeachProjectOID", projectTemplateId);
        params.put("financialData", "true");
        params.put("folders", "true");
        return params;
    }

    private HashMap setBlankProjectParam(HashMap params) {
        params.put("createProject", "Blank");
        params.put("SeachProjectOID", null);
        params.put("financialData", null);
        params.put("folders", null);
        return params;
    }

    public String attachDocument(Context context, String[] args) throws Exception {
        HashMap programMap = (HashMap) JPO.unpackArgs(args);

        try {
            // context.setRole("ctx::VPLMProjectLeader.Company Name.Common Space");
            // context.setVault("eService Production");
            // context.setApplication("VPLM");
            VPLMJDocumentServices vplmDocumentServicesJ = VPLMJDocumentServices.getInstance();

            HashMap attachDocumentProperties = new HashMap();
            // context.setSessionId((String)programMap.get("sessionId"));
//            attachDocumentProperties.put("role", "ctx::VPLMProjectLeader.Company Name.Common Space");
//            attachDocumentProperties.put("vault", "eService Production");
//            attachDocumentProperties.put("application", "VPLM");
            // context.setSessionId(programMap.get("sessionId").toString());
            attachDocumentProperties.put("objectId", programMap.get("objectId").toString());
            String[] documentIds = (String[]) programMap.get("documentIds");
            attachDocumentProperties.put("documentIds", documentIds);
            vplmDocumentServicesJ.attachDocuments(context, attachDocumentProperties);

            return "Document Attached";
        } catch (Exception exp) {
            throw exp;
        }
    }

    public String cloneObject(Context context, String[] args) throws Exception {
        try {
            String initargs[] = {};
            String clonedId = JPO.invoke(context, "emxWorkspaceVault", initargs, "cloneObject", args, String.class);

            String collaborationSpace = (String) ((HashMap) JPO.unpackArgs(args)).get("collaborationSpace");

            if ((collaborationSpace != null) && !(collaborationSpace.equalsIgnoreCase(""))) {
                String mqlCommand = "mod bus " + clonedId + " project " + collaborationSpace;
                runMQL(context, mqlCommand);
            }

            return clonedId;
        } catch (Exception exp) {
            throw exp;
        }
    }

    public void runMQL(Context context, String mqlCommand) throws Exception {
        try {
            MQLCommand mqlCmd = new MQLCommand();
            mqlCmd.open(context);
            MqlUtil.mqlCommand(context, mqlCmd, mqlCommand);
            mqlCmd.close(context);
        } catch (Exception exp) {
            throw new Exception("Couldn't run : " + mqlCommand + ". " + exp.getMessage());
        }
    }

    public List updateObjectsAttributesForTitle(Context context, String[] args) throws MatrixException, Exception {
        HashMap programMap = (HashMap) JPO.unpackArgs(args);
        HashMap objectsMap = (HashMap) programMap.get("objects");
        Iterator it = objectsMap.entrySet().iterator();
        ArrayList<HashMap<String, Object>> finalErrorList = new ArrayList();

        MQLCommand objMQL = new MQLCommand();
        objMQL.open(context);

        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            String objectId = (String) pair.getKey();
            HashMap attributeMap = (HashMap) pair.getValue();
            HashMap<String, Object> errorMap = new HashMap();
            List<String> errorList = new ArrayList();
//            List<Object> errorList = new ArrayList();
            //errorList.add(objectId);

            errorMap.put("objectId", objectId);
            Set keySetAttributeMap = attributeMap.keySet();

            StringBuilder updateAttributes = new StringBuilder();
            String mqlStatement = null;
//            MQLCommand objMQL = new MQLCommand();
//            objMQL.open(context);

            for (Object key : keySetAttributeMap) {
                updateAttributes
                        .append("'")
                        .append(key)
                        .append("'")
                        .append(" ")
                        .append("'")
                        .append(attributeMap.get(key))
                        .append("'");
                try {
                    mqlStatement = "escape modify bus '" + objectId + "' " + updateAttributes.toString();
                    MqlUtil.mqlCommand(context, objMQL, mqlStatement);
                } catch (Exception exp) {
                    String exception = exp.getMessage();
                    if (exception.contains(": ")) {
                        try {
                            String[] onlyCause = exception.split(": ");
                            //errorMap.put(key, onlyCause[onlyCause.length - 1]);
                            errorList.add(onlyCause[onlyCause.length - 1]);
                        } catch (Exception ex) {
                            errorMap.put(key.toString(), "Error message not found");
                        }
                    }
                } finally {
                    updateAttributes.setLength(0);
                }
            }

            //errorList.add(errorMap);
            errorMap.put("message", errorList);
            finalErrorList.add(errorMap);

        }
        //objMQL.close(context);
        return finalErrorList;
    }
}
