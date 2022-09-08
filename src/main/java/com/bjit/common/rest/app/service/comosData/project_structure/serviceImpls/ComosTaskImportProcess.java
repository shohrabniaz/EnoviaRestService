/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.app.service.comosData.project_structure.serviceImpls;

import com.bjit.common.rest.app.service.comosData.project_structure.model.*;
import com.bjit.common.rest.app.service.comosData.project_structure.processors.ComosPSProcessor;
import com.bjit.common.rest.app.service.comosData.project_structure.processors.TaskProcessor;
import com.bjit.common.rest.app.service.comosData.project_structure.services.ITaskImportProcess;
import com.bjit.common.rest.app.service.comosData.project_structure.utilities.*;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.controller.subtasks.ITaskSort;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.model.SessionModel;
import com.bjit.common.rest.app.service.model.tnr.TNR;
import com.bjit.common.rest.app.service.utilities.CommonUtilities;
import com.bjit.common.rest.app.service.utilities.JSON;
import com.bjit.common.rest.app.service.utilities.NullOrEmptyChecker;
import com.bjit.common.rest.item_bom_import.xml_mapping_model.ResponseMessageFormaterBean;
import com.bjit.ewc18x.utils.PropertyReader;
import com.matrixone.apps.domain.util.ContextUtil;
import com.matrixone.apps.domain.util.FrameworkException;
import matrix.db.BusinessObject;
import matrix.db.Context;
import matrix.db.Relationship;
import matrix.db.RelationshipType;
import matrix.util.MatrixException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.*;

/**
 * @author omour faruq
 */
@Service
@Qualifier("ComosTaskImportProcess")
public class ComosTaskImportProcess implements ITaskImportProcess<ComosProjectSpaceBean, ResponseMessageFormaterBean> {

    private static final org.apache.log4j.Logger TASK_IMPORT_PROCESS = org.apache.log4j.Logger.getLogger(ComosTaskImportProcess.class);
    private static final Integer numberOfELements = 0;
    private final Boolean deleteAllTasksAndMileStones = false;
    private final Boolean deleteUnSynchedTasksOrMileStones = true;

    @Autowired
    @Qualifier("ComosPSProcessor")
    ComosPSProcessor comosPSProcessor;

    @Autowired
    @Qualifier("ProjectSpaceUtil")
    ProjectSpaceUtil projectSpaceUtil;

    @Autowired
    BeanFactory beanFactory;

    @Autowired
    CommonUtilities commonUtilities;

    @Autowired
    SessionModel sessionModel;

    @Autowired
    @Qualifier("ComosTaskSort")
    ITaskSort taskSort;

    @Override
    public ResponseMessageFormaterBean projectAndTaskCreate(ComosProjectSpaceBean importBean) throws Exception {
        ResponseMessageFormaterBean responseMessage = null;

        if (NullOrEmptyChecker.isNull(importBean)) {
            return responseMessage;
        }

        ComosActivitiesBean comosActivitiesBean = getComosActivitiesBean(importBean);

        HashMap<String, String> taskNamesAndIds = new HashMap<>();
        try {
//            Context context = generateContext();
            Context context = commonUtilities.generateContext();
            sessionModel.setContext(context);

//            Context context = ContextGeneration.createContext();
            if (!context.isConnected()) {
                throw new Exception("Context couldn't be connected");
            }

            try {
                TASK_IMPORT_PROCESS.info("Starting the transaction");
//                ContextUtil.startTransaction(context, true);
                try {
//                    ComosPSProcessor comosPSProcessor = new ComosPSProcessor();
                    String newProjectSpaceObjectId = comosPSProcessor.createProjectSpace(context, comosActivitiesBean);
                    TASK_IMPORT_PROCESS.debug("New projectSpace Id : " + newProjectSpaceObjectId);

                    responseMessage = getTnr(context, newProjectSpaceObjectId);
                    TASK_IMPORT_PROCESS.info("New projectSpace is : " + responseMessage.getTnr().toString());

                    String projectSpaceName = responseMessage.getTnr().getName();

//                    taskNamesAndIds.put(importBean.getProjectCode(), newProjectSpaceObjectId);
                    taskNamesAndIds.put(projectSpaceName, newProjectSpaceObjectId);

                    Optional.ofNullable(importBean.getTableData()).ifPresent(listOfComosTaskBean -> listOfComosTaskBean.forEach(comosTaskBean -> comosTaskBean.setProject(projectSpaceName)));

//                    ProjectSpaceUtil projectSpaceUtil = new ProjectSpaceUtil();
                    HashMap<String, String> projectSpaceProperties = getCreateOrUpdatePropertiesAndSetInterfaces(context, newProjectSpaceObjectId, Constants.UPDATE, comosActivitiesBean);
                    projectSpaceUtil.updateItem(context, newProjectSpaceObjectId, projectSpaceProperties);
                    HashMap<String, ActivityModel> projectSpaceExpandedMap = projectSpaceUtil.expandTask(context, newProjectSpaceObjectId);

                    List<ComosTaskBean> sortTasksAndMileStonesData = taskSort.sortTasksAndMileStonesData(importBean.getTableData());
                    JSON json = new JSON(Boolean.FALSE);

//                    System.out.println("######################################");
//                    System.out.println("######################################");
//                    System.out.println(json.serialize(sortTasksAndMileStonesData));
//                    System.out.println("######################################");
//                    System.out.println("######################################");

                    importBean.setTableData(sortTasksAndMileStonesData);

                    createTaskOrMileStone(context, importBean, newProjectSpaceObjectId, taskNamesAndIds, projectSpaceExpandedMap, projectSpaceUtil);

                    TASK_IMPORT_PROCESS.info("Committing the transaction");
//                    ContextUtil.commitTransaction(context);
                } catch (KeyManagementException | NoSuchAlgorithmException exp) {
                    TASK_IMPORT_PROCESS.error("Aborting the transaction");
                    TASK_IMPORT_PROCESS.error(exp);
//                    ContextUtil.abortTransaction(context);
                    throw exp;
                } catch (Exception exp) {
                    TASK_IMPORT_PROCESS.error("Aborting the transaction");
                    TASK_IMPORT_PROCESS.error(exp);
//                    ContextUtil.abortTransaction(context);
                    throw exp;
                }
            } catch (Exception exp) {
                TASK_IMPORT_PROCESS.error(exp);
                throw exp;
            }
        } catch (Exception exp) {
            TASK_IMPORT_PROCESS.error(exp);
            throw exp;
        }
        return responseMessage;
    }

    private ResponseMessageFormaterBean getTnr(Context context, String newProjectSpaceObjectId) throws MatrixException {
        BusinessObject businessObject = new BusinessObject(newProjectSpaceObjectId);
        TNR tnr = beanFactory.getBean(TNR.class);
        businessObject.open(context);
        tnr.setType(businessObject.getTypeName());
        tnr.setName(businessObject.getName(context));
        tnr.setRevision(businessObject.getRevision());
        businessObject.close(context);

        ResponseMessageFormaterBean responseMessage = beanFactory.getBean(ResponseMessageFormaterBean.class);

        responseMessage.setTnr(tnr);
        responseMessage.setObjectId(newProjectSpaceObjectId);

        return responseMessage;
    }

    private ComosActivitiesBean getComosActivitiesBean(ComosProjectSpaceBean importBean) {
        ComosActivitiesBean comosActivitiesBean = beanFactory.getBean(ComosActivitiesBean.class);

        comosActivitiesBean.setTitle(importBean.getTitle());
        comosActivitiesBean.setCompassId(importBean.getCompassId());
        comosActivitiesBean.setErpSubProject(importBean.getErpSubProject());
        comosActivitiesBean.setComosProjectUID(importBean.getComosProjectUID());
        comosActivitiesBean.setMillId(importBean.getMillId());
        comosActivitiesBean.setEquipmentId(importBean.getEquipmentId());
        comosActivitiesBean.setPlantId(importBean.getPlantId());
        comosActivitiesBean.setProjectId(importBean.getProjectId());
        comosActivitiesBean.setLayerId(importBean.getLayerId());
        comosActivitiesBean.setMillHierarchyId(importBean.getMillHierarchyId());

        return comosActivitiesBean;
    }

    private HashMap<String, ActivityModel> createTaskOrMileStone(Context context, ComosTaskDataBean taskData, String newProjectSpaceObjectId, HashMap<String, String> taskNamesAndIds, HashMap<String, ActivityModel> projectSpaceExpandedMap, ProjectSpaceUtil projectSpaceUtil) throws MatrixException {

        if (NullOrEmptyChecker.isNull(taskData)) {
            throw new NullPointerException("'TaskData' is empty");
        }

        if (NullOrEmptyChecker.isNullOrEmpty(taskData.getTableData())) {
            BusinessObject projectSpaceObject = new BusinessObject(newProjectSpaceObjectId);
            projectSpaceObject.open(context);
            String projectSpaceName = projectSpaceObject.getName();
            projectSpaceObject.close(context);
            projectSpaceObject = null;
            throw new NullPointerException("No Task or MileStone found under '" + projectSpaceName + "' Project Space");
        }

        taskData.getTableData().forEach((ComosTaskBean task) -> {
            try {
                String activityTaskName = task.getActivity();
                TASK_IMPORT_PROCESS.debug("Task name : " + activityTaskName);

                if (NullOrEmptyChecker.isNullOrEmpty(task.getActivityType())) {
                    TASK_IMPORT_PROCESS.warn("'" + activityTaskName + "' skipping the task as the type of the task is empty");
                    return;
                }

                String parentActivityName = task.getParentActivity();
                String parentIdFromLNJsonResponse = NullOrEmptyChecker.isNull(task.getParentActivity()) ? newProjectSpaceObjectId : taskNamesAndIds.get(parentActivityName);

                if (NullOrEmptyChecker.isNullOrEmpty(parentIdFromLNJsonResponse)) {
                    String parentActivity = task.getParentActivity();
                    ActivityModel parentActivityModel = projectSpaceExpandedMap.get(parentActivity);
                    if (NullOrEmptyChecker.isNull(parentActivityModel)) {
                        TASK_IMPORT_PROCESS.warn("'" + activityTaskName + "' has been being skipped as it's parent '" + parentActivity + "' has not found in the system");
                        return;
                    }

                    parentIdFromLNJsonResponse = parentActivityModel.getActivityId();
                    if (NullOrEmptyChecker.isNullOrEmpty(parentIdFromLNJsonResponse)) {
                        throw new RuntimeException("Faulty ProjectSpace and Task structure");
                    }
                }

                String activityName = task.getActivity();
                String activityType = task.getActivityType();

                TASK_IMPORT_PROCESS.debug("Task parent activity : " + parentActivityName);
                TASK_IMPORT_PROCESS.debug("Task parent id : " + parentIdFromLNJsonResponse);
                TASK_IMPORT_PROCESS.debug("Activity Type : '" + activityType + "'");

                TASK_IMPORT_PROCESS.debug("################");
                TASK_IMPORT_PROCESS.debug("# FOUND A TASK #");
                TASK_IMPORT_PROCESS.debug("################");

//                if (projectSpaceExpandedMap.containsKey(activityName)) {
                if (projectSpaceExpandedMap.containsKey(task.getComosActivityUID())) {
                    ActivityModel activityModel = projectSpaceExpandedMap.get(task.getComosActivityUID());
                    String v6ParentId = activityModel.getParentId();
                    String taskName = activityModel.getActivityName();
                    if (/*activityModel.getParentName().equalsIgnoreCase(parentActivityName) && */v6ParentId.equalsIgnoreCase(parentIdFromLNJsonResponse)) {
                        HashMap<String, String> createOrUpdatePropertiesAndSetInterfaces = getCreateOrUpdatePropertiesAndSetInterfaces(context, activityModel.getActivityId(), Constants.UPDATE, task);
                        updateTask(context, taskNamesAndIds, createOrUpdatePropertiesAndSetInterfaces, projectSpaceUtil, activityModel.getActivityName(), parentActivityName, activityModel, task);

                    } else {
                        disConnectAndReConnecteTask(context, projectSpaceUtil, taskNamesAndIds, activityModel, parentIdFromLNJsonResponse, task);
                    }
                } else {
                    createTask(context, projectSpaceUtil, taskNamesAndIds, projectSpaceExpandedMap, parentIdFromLNJsonResponse, task, activityName, parentActivityName);
                }
            } catch (Exception exp) {
                TASK_IMPORT_PROCESS.error(exp);
                throw new RuntimeException(exp);
            }
        });

        HashMap<String, ActivityModel> projectSpaceAndTasksInV6 = new HashMap<>(projectSpaceExpandedMap);
        Set<String> tasksInV6 = projectSpaceAndTasksInV6.keySet();
        Set<String> tasksInLN = taskNamesAndIds.keySet();

        tasksInV6.removeAll(tasksInLN);
        tasksInV6.forEach((String removedTask) -> {

            boolean fullSynchronization = Boolean.parseBoolean(PropertyReader.getProperty("full.synchronization.by.deleting.or.disconnecting.tasks.or.milestones"));

            if (fullSynchronization) {
                if (deleteUnSynchedTasksOrMileStones) {
                    projectSpaceUtil.deleteTaskAndMileStone(context, projectSpaceExpandedMap.get(removedTask));
                } else {
                    disconnectTask(context, projectSpaceExpandedMap.get(removedTask));
                }
            }

            projectSpaceExpandedMap.remove(removedTask);
        });
        taskNamesAndIds.clear();

        return projectSpaceExpandedMap;
    }

    private void createTask(Context context, ProjectSpaceUtil projectSpaceUtil, HashMap<String, String> taskNamesAndIds, HashMap<String, ActivityModel> projectSpaceExpandedMap, String parentIdFromLNJsonResponse, ComosTaskBean task, String activityName, String parentActivityName) throws Exception {
        HashMap<String, String> taskOrMileStoneCreateProperties = new ComosReflectionUtilities().getHashMapFromAnnotaionAndValue(task);
        taskOrMileStoneCreateProperties.put("ActivityType", taskOrMileStoneCreateProperties.get("ActivityType").equalsIgnoreCase(Constants.MILE_STONE) ? Constants.MILE_STONE : Constants.TASK);
        taskOrMileStoneCreateProperties.put("objectId", parentIdFromLNJsonResponse);
        taskOrMileStoneCreateProperties.put("parentId", parentIdFromLNJsonResponse);
        HashMap<String, String> taskAttributeValueMap = new ComosProjectStructureXMLMapUtils().getCreateOrUpdateProperties(task.getActivityType(), Constants.CREATE, taskOrMileStoneCreateProperties);

        TASK_IMPORT_PROCESS.info("create and connect task with the parent");

        TaskProcessor taskProcessor = new TaskProcessor();
        String newTaskId = taskProcessor.createTask(context, taskAttributeValueMap, task, Boolean.FALSE);
        TASK_IMPORT_PROCESS.debug("New Task Id : " + newTaskId);

        if (NullOrEmptyChecker.isNullOrEmpty(newTaskId)) {
            TASK_IMPORT_PROCESS.warn("'" + task.getActivity() + "' is going to skip as their type is missing");
            return;
        }

        taskNamesAndIds.put(task.getActivity(), newTaskId);

        ActivityModel newChildactivityModel = new ActivityModel();
        newChildactivityModel.setActivityId(newTaskId);
        newChildactivityModel.setActivityName(activityName);
        newChildactivityModel.setActivityType("Task");
        newChildactivityModel.setParentName(parentActivityName);
        newChildactivityModel.setParentId(parentIdFromLNJsonResponse);
        newChildactivityModel.setRelationShipName("SubTask");

        HashMap<String, String> createOrUpdateProperties = getCreateOrUpdatePropertiesAndSetInterfaces(context, newTaskId, Constants.UPDATE, task);
        updateTask(context, taskNamesAndIds, createOrUpdateProperties, projectSpaceUtil, newChildactivityModel.getActivityName(), parentActivityName, newChildactivityModel, task);
    }

    private void updateTask(Context context, HashMap<String, String> taskNamesAndIds, HashMap<String, String> updateProperties, ProjectSpaceUtil projectSpaceUtil, String activityName, String parentActivityName, ActivityModel activityModel, ComosTaskBean task) throws FrameworkException {
        TASK_IMPORT_PROCESS.debug("Activity '" + activityName + "' already connected to the parent '" + parentActivityName + "'");
        TASK_IMPORT_PROCESS.debug("Task Id : " + activityModel.getActivityId());

        if (NullOrEmptyChecker.isNullOrEmpty(task.getActivityType())) {
            TASK_IMPORT_PROCESS.warn("'" + task.getActivity() + "' is going to be skipped as it's type is empty");
            return;
        }

        taskNamesAndIds.put(task.getActivity(), activityModel.getActivityId());
        TASK_IMPORT_PROCESS.debug("Update The Task");

        projectSpaceUtil.updateItem(context, activityModel.getActivityId(), updateProperties);
    }

    private Boolean disconnectTask(Context context, ActivityModel v6ActivityModel) {
        try {
            String taskName = v6ActivityModel.getActivityName();
            String v6ParentId = v6ActivityModel.getParentId();

            TASK_IMPORT_PROCESS.info("Disconnecting '" + taskName + "' from '" + v6ActivityModel.getParentName() + "'");

            BusinessObject v6ParentBusinessObject = new BusinessObject(v6ParentId);
            v6ParentBusinessObject.open(context);

            TASK_IMPORT_PROCESS.debug("Task id is : " + v6ActivityModel.getActivityId());
            String relationshipId = v6ActivityModel.getRelationshipId();
            Relationship relationShip = new Relationship(relationshipId);
            v6ParentBusinessObject.disconnect(context, relationShip);
            return true;
        } catch (MatrixException ex) {
            TASK_IMPORT_PROCESS.info(ex);
            return false;
        }
    }

    private void disConnectAndReConnecteTask(Context context, ProjectSpaceUtil projectSpaceUtil, HashMap<String, String> taskNamesAndIds, ActivityModel activityModel, String parentIdFromLNJsonResponse, ComosTaskBean task) throws MatrixException {
        String taskName = activityModel.getActivityName();
        String v6ParentId = activityModel.getParentId();
        String parentActivityName = task.getParentActivity();
        TASK_IMPORT_PROCESS.info("Disconnecting '" + taskName + "' from '" + activityModel.getParentName() + "'");

        BusinessObject v6ParentBusinessObject = new BusinessObject(v6ParentId);
        v6ParentBusinessObject.open(context);

        TASK_IMPORT_PROCESS.debug("Task id is : " + activityModel.getActivityId());
        String relationshipId = activityModel.getRelationshipId();
        Relationship relationShip = new Relationship(relationshipId);
        v6ParentBusinessObject.disconnect(context, relationShip);

        RelationshipType relationshipType = new RelationshipType();
        relationshipType.setName("Subtask");

        TASK_IMPORT_PROCESS.info("Connecting '" + taskName + "' as a child of '" + parentActivityName + "'");

        BusinessObject parentBusinessObject = new BusinessObject(parentIdFromLNJsonResponse);
        BusinessObject childBusinessObject = new BusinessObject(activityModel.getActivityId());
        parentBusinessObject.open(context);
        childBusinessObject.open(context);

        Relationship connect = parentBusinessObject.connect(context, relationshipType, true, childBusinessObject);
        String newRelationshipId = connect.getName();

        TASK_IMPORT_PROCESS.debug("'" + taskName + "' has connected as a child of '" + parentActivityName + "' by '" + newRelationshipId + "' connection id");

        activityModel.setParentName(parentActivityName);
        activityModel.setParentId(parentIdFromLNJsonResponse);
        activityModel.setRelationshipId(newRelationshipId);

        taskNamesAndIds.put(task.getActivity(), activityModel.getActivityId());

        HashMap<String, String> createOrUpdateProperties = getCreateOrUpdatePropertiesAndSetInterfaces(context, activityModel.getActivityId(), Constants.UPDATE, task);
        updateTask(context, taskNamesAndIds, createOrUpdateProperties, projectSpaceUtil, activityModel.getActivityName(), parentActivityName, activityModel, task);
    }

    private HashMap<String, String> getCreateOrUpdatePropertiesAndSetInterfaces(Context context, String objectIdForAddingInterface, String projectStructureConstant, ComosTaskBean task) throws MatrixException {
        HashMap<String, String> createOrUpdateProperties = new ComosProjectStructureXMLMapUtils().getCreateOrUpdateProperties(task.getActivityType(), projectStructureConstant, new ComosReflectionUtilities().getHashMapFromAnnotaionAndValue(task));
        return addInterfaceToTheObject(createOrUpdateProperties, context, objectIdForAddingInterface);
    }

    private HashMap<String, String> getCreateOrUpdatePropertiesAndSetInterfaces(Context context, String objectIdForAddingInterface, String projectConstants, ComosActivitiesBean activitiesBean) throws MatrixException {
        HashMap<String, String> createOrUpdateProperties = new ComosProjectStructureXMLMapUtils().getCreateOrUpdateProperties(Constants.PROJECT_SPACE, projectConstants, new ComosReflectionUtilities().getHashMapFromAnnotaionAndValue(activitiesBean));
        return addInterfaceToTheObject(createOrUpdateProperties, context, objectIdForAddingInterface);
    }

    private HashMap<String, String> addInterfaceToTheObject(HashMap<String, String> createOrUpdateProperties, Context context, String objectIdForAddingInterface) throws MatrixException {
        try {
            String runtimeInterfaceList = createOrUpdateProperties.get("runtimeInterfaceList");
            createOrUpdateProperties.remove("runtimeInterfaceList");
            new ComosProjectStructureObjectUtility().addInterface(context, objectIdForAddingInterface, runtimeInterfaceList, "");
            return createOrUpdateProperties;
        } catch (MatrixException ex) {
            TASK_IMPORT_PROCESS.error(ex);
            throw ex;
        }
    }

    private <T> List<T> getNumberOfElements(List<T> listObject, Integer numberOfELements) {
        if (NullOrEmptyChecker.isNull(numberOfELements) || numberOfELements == 0) {
            return listObject;
        }

        List<T> newList = new ArrayList<>();

        for (int iterator = 0; iterator < numberOfELements; iterator++) {
            newList.add(listObject.get(iterator));
        }

        return newList;
    }

    public HashMap<String, String> getProjectSpaceProperties(ComosActivitiesBean activitiesBean) {
        HashMap<String, String> updateProperties = new ComosProjectStructureXMLMapUtils().getCreateOrUpdateProperties("Project Space", "update", new ComosReflectionUtilities().getHashMapFromAnnotaionAndValue(activitiesBean));

        return updateProperties;
    }
}
