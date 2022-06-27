/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.app.service.comosData.project_structure.processors;

import com.bjit.common.rest.app.service.comosData.exceptions.ProjectStructureException;
import com.bjit.common.rest.app.service.comosData.project_structure.model.ComosActivitiesBean;
import com.bjit.common.rest.app.service.comosData.project_structure.utilities.ComosProjectStructureXMLMapUtils;
import com.bjit.common.rest.app.service.comosData.project_structure.utilities.ComosReflectionUtilities;
import com.bjit.common.rest.app.service.model.tnr.TNR;
import com.bjit.common.rest.app.service.utilities.CommonSearch;
import com.bjit.project_structure.utilities.Constants;
import com.bjit.project_structure.utilities.NullOrEmptyChecker;
import matrix.db.Context;
import matrix.db.JPO;
import matrix.util.MatrixException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author omour faruq
 */
@Component
@Qualifier("ComosPSProcessor")
public class ComosPSProcessor {

    private static final org.apache.log4j.Logger PROJECT_SPACE_LOGGER = org.apache.log4j.Logger.getLogger(ComosPSProcessor.class);

    @Autowired
    @Qualifier("CommonSearch")
    CommonSearch commonSearch;

    @Autowired
    TNR tnr;

    public void checkContext(Context context) {
        if (NullOrEmptyChecker.isNull(context)) {
            String errorMessage = "Context is Null";
            PROJECT_SPACE_LOGGER.error(errorMessage);
            throw new NullPointerException(errorMessage);
        }
    }

    public String searchByTypeName(Context context, String type, String name) throws MatrixException {

        try {
            String errorMessage;
            PROJECT_SPACE_LOGGER.info("Object type : '" + type + "' Object name : '" + name + "'");
            this.checkContext(context);
            if (NullOrEmptyChecker.isNullOrEmpty(type)) {
                errorMessage = "'Type' is Null or Empty";
                PROJECT_SPACE_LOGGER.error(errorMessage);
                throw new NullPointerException(errorMessage);
            }

            if (NullOrEmptyChecker.isNullOrEmpty(name)) {
                errorMessage = "'Name' of '" + type + "' is Null or Empty";
                PROJECT_SPACE_LOGGER.error(errorMessage);
                throw new NullPointerException(errorMessage);

            }

            String[] constructor = {null};
            HashMap params = new HashMap();
            params.put("name", name);
            params.put("type", type);

            String jpoClassName = "CloneObjectUtil";
            String jpoMethodName = "searchByTypeName";

            PROJECT_SPACE_LOGGER.info("JPO class name is : '" + jpoClassName + "' method name is : '" + jpoMethodName + "'");

            String objectId = (String) JPO.invoke(context, jpoClassName, constructor, jpoMethodName, JPO.packArgs(params), String.class);
            PROJECT_SPACE_LOGGER.debug("Object Id : " + objectId);
            return objectId;
        } catch (MatrixException exp) {
            PROJECT_SPACE_LOGGER.error(exp.getMessage());
            throw exp;
        } catch (NullPointerException exp) {
            PROJECT_SPACE_LOGGER.error(exp.getMessage());
            throw exp;
        } catch (Exception exp) {
            PROJECT_SPACE_LOGGER.error(exp.getMessage());
            throw exp;
        }
    }

    public String createProjectSpace(Context context, ComosActivitiesBean activitiesBean) throws Exception {
        /*
        String objectId = searchByTypeName(context, "Project Space", activitiesBean.getProjectCode());
        if (!NullOrEmptyChecker.isNullOrEmpty(objectId)) {

            return objectId;
        }
        
         */
        tnr.setType("Project Space");
        HashMap<String, String> attribute = new HashMap<>();
        attribute.put("attribute[PRJ_CompassID].value", activitiesBean.getCompassId());
        List<HashMap<String, String>> AttributeMapList = new ArrayList<>();
        try {
            AttributeMapList = commonSearch.searchItem(context, tnr, attribute);
            if (AttributeMapList.size() == 1 && !NullOrEmptyChecker.isNullOrEmpty(AttributeMapList.get(0).get("id"))) {
                return AttributeMapList.get(0).get("id");
            } else {
                PROJECT_SPACE_LOGGER.info("Multiple ObjectId found");
                throw new ProjectStructureException("Multiple Object Id Found with this CompassId");
            }

        } catch (Exception e) {
            System.out.println(e);
        }


        String initargs[] = {};
        HashMap<String, String> attributesMap = new ComosProjectStructureXMLMapUtils().getCreateOrUpdateProperties(Constants.PROJECT_SPACE, Constants.CREATE, new ComosReflectionUtilities().getHashMapFromAnnotaionAndValue(activitiesBean));
        String newProjectId = null;
            try {
                Map result = (Map) JPO.invoke(context, "emxProjectSpaceBase", initargs, "createNewProject", JPO.packArgs(attributesMap), Map.class);
                newProjectId = (String) result.get("id");
                PROJECT_SPACE_LOGGER.debug("New project id : " + newProjectId);
            } catch (MatrixException exp) {
                PROJECT_SPACE_LOGGER.error(exp);
                throw exp;
            }


        return newProjectId;
    }
}
