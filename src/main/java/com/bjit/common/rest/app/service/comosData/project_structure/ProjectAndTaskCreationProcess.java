/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.app.service.comosData.project_structure;

import com.bjit.common.rest.app.service.comosData.project_structure.utilities.ComosProjectStructureXMLMapUtils;
import com.bjit.project_structure.model.ActivityModel;
import com.bjit.project_structure.utilities.ProjectSpaceUtil;
import com.bjit.ex.integration.transfer.util.ApplicationProperties;
import com.bjit.project_structure.processors.TaskProcessor;
import com.bjit.project_structure.utilities.JSON;
import com.bjit.project_structure.utilities.ServiceRequester;
import com.bjit.project_structure.model.ActivitiesBean;
import com.bjit.project_structure.model.TaskBean;
import com.bjit.project_structure.model.TaskDataBean;
import com.bjit.project_structure.model.TaskTableData;
import com.bjit.project_structure.utilities.NullOrEmptyChecker;
import com.bjit.project_structure.utilities.ObjectUtility;
import com.bjit.project_structure.utilities.Constants;
import com.bjit.project_structure.utilities.ReflectionUtilities;
import com.matrixone.apps.domain.util.FrameworkException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import matrix.db.BusinessObject;
import matrix.db.Context;
import matrix.db.Relationship;
import matrix.db.RelationshipType;
import matrix.util.MatrixException;
import net.sf.json.JSONObject;

/**
 *
 * @author omour faruq
 */
public class ProjectAndTaskCreationProcess {

    private static final org.apache.log4j.Logger PROJECT_AND_TASK_CREATION_PROCESS_LOGGER = org.apache.log4j.Logger.getLogger(ProjectAndTaskCreationProcess.class);
    private static Integer numberOfELements = 0;
//    private final Boolean deleteAllTasksAndMileStones = false;
    private final Boolean deleteUnSynchedTasksOrMileStones = true;

//    public static void main(String[] args) throws KeyManagementException, NoSuchAlgorithmException, Exception {
////        new ProjectAndTaskCreationProcess().initializeTestData();
//    }

//    public ProjectDataBean ProjectAndTaskCreate(Integer numberOfElementsAsProject, String projectSpace) throws KeyManagementException, NoSuchAlgorithmException, Exception {
//        ProjectDataBean projectSpcaeData;
//        try {
//            numberOfELements = numberOfElementsAsProject;
//            projectSpcaeData = this.ProjectAndTaskCreate(projectSpace);
//            numberOfELements = 0;
//        } catch (Exception exp) {
//            PROJECT_AND_TASK_CREATION_PROCESS_LOGGER.error(exp);
//            throw new RuntimeException(exp);
//        }
//        return projectSpcaeData;
//    }

//    public ProjectDataBean ProjectAndTaskCreate(String projectSpace) throws KeyManagementException, NoSuchAlgorithmException, Exception {
//
//        ProjectDataBean projectSpaceData;
//        try {
//            String host = ApplicationProperties.getProprtyValue("wbs.context.host");
//
//            Context context = ContextGeneration.createContext();
//            if (!context.isConnected()) {
//                throw new Exception("Context couldn't be connected");
//            }
//
//            //ContextUtil.startTransaction(context, true);
//            try {
////                if (Boolean.parseBoolean(ApplicationProperties.getProprtyValue("disable.ssl.certificate"))) {
////                    PROJECT_AND_TASK_CREATION_PROCESS_LOGGER.info("Dissabling SSL certificates");
////                    DisableSSLCertificate.DisableCertificate();
////                }
//                String projectQuery = getProjectSpaceQuery(projectSpace);
//                PROJECT_AND_TASK_CREATION_PROCESS_LOGGER.info("Getting project data from service");
//
//                try {
//                    projectSpaceData = this.getDataFromService(ApplicationProperties.getProprtyValue("ln.project.or.task.or.milestone.fetching.url"), projectQuery, ProjectDataBean.class);
//                    if (projectSpaceData.getError().length() > 0) {
//                        return projectSpaceData;
//                    }
//                } catch (Exception exp) {
//                    PROJECT_AND_TASK_CREATION_PROCESS_LOGGER.error(exp);
//
//                    ActivitiesBean activitiesBean;
//                    if (!NullOrEmptyChecker.isNullOrEmpty(projectSpace)) {
//                        ProjectTableData projectTableData = this.getDataFromService(ApplicationProperties.getProprtyValue("ln.project.or.task.or.milestone.fetching.url"), projectQuery, ProjectTableData.class);
//                        activitiesBean = projectTableData.getTableData();
//                    } else {
//                        activitiesBean = this.getDataFromService(ApplicationProperties.getProprtyValue("ln.project.or.task.or.milestone.fetching.url"), projectQuery, ActivitiesBean.class);
//                    }
//
//                    projectSpaceData = new ProjectDataBean();
//                    List<ActivitiesBean> tableData = new ArrayList<>();
//                    tableData.add(activitiesBean);
//                    projectSpaceData.setTableData(tableData);
//                }
//
//                if (numberOfELements > 0) {
//                    projectSpaceData.setTableData(getNumberOfElements(projectSpaceData.getTableData(), numberOfELements));
//                }
//
//                HashMap<String, String> taskNamesAndIds = new HashMap<>();
//
//                projectSpaceData.getTableData().forEach((ActivitiesBean activitiesBean) -> {
//                    try {
//                        String newProjectSpaceObjectId = PSProcessor.createProjectSpace(context, activitiesBean);
//                        PROJECT_AND_TASK_CREATION_PROCESS_LOGGER.debug("New projectSpace Id : " + newProjectSpaceObjectId);
//
//                        taskNamesAndIds.put(activitiesBean.getProjectCode(), newProjectSpaceObjectId);
//
//                        ProjectSpaceUtil projectSpaceUtil = new ProjectSpaceUtil();
//
//                        HashMap<String, String> projectSpaceProperties = getCreateOrUpdatePropertiesAndSetInterfaces(context, newProjectSpaceObjectId, Constants.UPDATE, activitiesBean);
//                        projectSpaceUtil.updateItem(context, newProjectSpaceObjectId, projectSpaceProperties);
//						HashMap<String, ActivityModel> projectSpaceExpandedMap = projectSpaceUtil.expandTask(context, newProjectSpaceObjectId);
//                        String projectCode = activitiesBean.getProjectCode();
//                        PROJECT_AND_TASK_CREATION_PROCESS_LOGGER.debug("Project Code : " + projectCode);
//
//                        TaskDataBean taskData;
//                        try {
////                            taskData = getTaskOrMileStoneData(getTaskQuery(projectCode));
//                            taskData = getTaskOrMileStoneDataFromFile("simple_tasks.json");
//                        } catch (Exception exp) {
//                            PROJECT_AND_TASK_CREATION_PROCESS_LOGGER.error(exp);
//                            taskData = null;
//                        }
//
//                        //HashMap<String, ActivityModel> createTask = createTaskOrMileStone(context, taskData, newProjectSpaceObjectId, taskNamesAndIds, projectSpaceExpandedMap, projectSpaceUtil);
//                        TaskDataBean mileStoneData;
//                        try {
////                            mileStoneData = getTaskOrMileStoneData(getMileStoneQuery(projectCode));
//                            mileStoneData = getTaskOrMileStoneDataFromFile("simple_mile_stone.json");
//                        } catch (Exception exp) {
//                            PROJECT_AND_TASK_CREATION_PROCESS_LOGGER.error(exp);
//                            mileStoneData = null;
//                        }
//
//                        //HashMap<String, ActivityModel> createMileStone = createTaskOrMileStone(context, mileStoneData, newProjectSpaceObjectId, taskNamesAndIds, projectSpaceExpandedMap, projectSpaceUtil);
//                        if (NullOrEmptyChecker.isNull(taskData) && NullOrEmptyChecker.isNull(mileStoneData)) {
//                            PROJECT_AND_TASK_CREATION_PROCESS_LOGGER.error("No task and milestone found");
//                            return;
//                        } else if (NullOrEmptyChecker.isNull(taskData) || NullOrEmptyChecker.isNull(mileStoneData)) {
//                            if (NullOrEmptyChecker.isNull(taskData)) {
//                                taskData = mileStoneData;
//                            }
//                        } else {
//                            taskData.mergeTaskData(mileStoneData);
//                        }
//
//                        List<TaskBean> sortTasksAndMileStonesData = sortTasksAndMileStonesData(taskData.getTableData());
//                        taskData.setTableData(sortTasksAndMileStonesData);
//                        HashMap<String, ActivityModel> createMileStone = createTaskOrMileStone(context, taskData, newProjectSpaceObjectId, taskNamesAndIds, projectSpaceExpandedMap, projectSpaceUtil);
//                    } catch (Exception exp) {
//                        PROJECT_AND_TASK_CREATION_PROCESS_LOGGER.error(exp);
//                        throw new RuntimeException(exp);
//                    }
//                });
//
//                //ContextUtil.commitTransaction(context);
//            } catch (KeyManagementException | NoSuchAlgorithmException exp) {
//                PROJECT_AND_TASK_CREATION_PROCESS_LOGGER.error(exp);
//                //ContextUtil.abortTransaction(context);
//                throw exp;
//            } catch (Exception exp) {
//                PROJECT_AND_TASK_CREATION_PROCESS_LOGGER.error(exp);
//                //ContextUtil.abortTransaction(context);
//                throw exp;
//            }
//
//        } catch (Exception exp) {
//            PROJECT_AND_TASK_CREATION_PROCESS_LOGGER.error(exp);
//            throw exp;
//        }
//        return projectSpaceData;
//    }

//    public void initializeTestData() throws NoSuchAlgorithmException, Exception {
//        ProjectAndTaskCreationProcess U101XL001 = new ProjectAndTaskCreationProcess();
//        U101XL001.ProjectAndTaskCreate(0, "101X0001");
//    }

//    public String getProjectSpaceQuery(String projectSpace) {
////                String projectQuery = NullOrEmptyChecker.isNullOrEmpty(projectSpace)
////                        ? "<Query>\n"
////                        + "	SELECT distinct(PROJECT.ID) as ProjectCode, PROJECT_LC.DSC as Description FROM PROJECT\n"
////                        + "	inner join PROJECT_MV on PROJECT.NID=PROJECT_MV.NID and PROJECT.ID_VARIATION_ID=PROJECT_MV.ID_VARIATION_ID\n"
////                        + "	inner join PROJECT_LC on PROJECT_LC.NID=PROJECT_MV.NID and PROJECT_LC.LOCALE='default' WHERE \n"
////                        + "	PROJECT.S_CD='open' AND PROJECT.PROJ_HRCHY_TYP in ('Sub Project', 'Single Project');\n"
////                        + "</Query>\n"
////                        + "<Receiver>LNRxDEV</Receiver>"
////                        + "<Sender>Enovia</Sender>"
////                        : "<Query>\n"
////                        + "     SELECT distinct(PROJECT.ID) as ProjectCode, PROJECT_LC.DSC as Description FROM PROJECT\n"
////                        + "     inner join PROJECT_MV on PROJECT.NID=PROJECT_MV.NID and PROJECT.ID_VARIATION_ID=PROJECT_MV.ID_VARIATION_ID\n"
////                        + "     inner join PROJECT_LC on PROJECT_LC.NID=PROJECT_MV.NID and PROJECT_LC.LOCALE='default' WHERE \n"
////                        + "     PROJECT.S_CD='open' AND PROJECT.ID='" + projectSpace + "' AND PROJECT.PROJ_HRCHY_TYP in ('Sub Project', 'Single Project');\n"
////                        + "</Query>\n"
////                        + "<Receiver>LNRxDEV</Receiver>"
////                        + "<Sender>Enovia</Sender>";
//        return NullOrEmptyChecker.isNullOrEmpty(projectSpace)
//                ? "<Query>\n"
//                + "	SELECT distinct(PROJECT.ID) as ProjectCode, PROJECT_LC.DSC as Description FROM PROJECT\n"
//                + "	inner join PROJECT_MV on PROJECT.NID=PROJECT_MV.NID and PROJECT.ID_VARIATION_ID=PROJECT_MV.ID_VARIATION_ID\n"
//                + "	inner join PROJECT_LC on PROJECT_LC.NID=PROJECT_MV.NID and PROJECT_LC.LOCALE='default' WHERE \n"
//                + "	PROJECT.S_CD='open' AND PROJECT.PROJ_HRCHY_TYP in ('Sub Project', 'Single Project');\n"
//                + "</Query>\n"
//                + "<Receiver>" + ApplicationProperties.getProprtyValue("project.space.ln.receiver") + "</Receiver>"
//                + "<Sender>" + ApplicationProperties.getProprtyValue("project.space.request.sender.to.ln") + "</Sender>"
//                : "<Query>\n"
//                + "     SELECT distinct(PROJECT.ID) as ProjectCode, PROJECT_LC.DSC as Description FROM PROJECT\n"
//                + "     inner join PROJECT_MV on PROJECT.NID=PROJECT_MV.NID and PROJECT.ID_VARIATION_ID=PROJECT_MV.ID_VARIATION_ID\n"
//                + "     inner join PROJECT_LC on PROJECT_LC.NID=PROJECT_MV.NID and PROJECT_LC.LOCALE='default' WHERE \n"
//                + "     PROJECT.S_CD='open' AND PROJECT.ID='" + projectSpace + "' AND PROJECT.PROJ_HRCHY_TYP in ('Sub Project', 'Single Project');\n"
//                + "</Query>\n"
//                + "<Receiver>" + ApplicationProperties.getProprtyValue("project.space.ln.receiver") + "</Receiver>"
//                + "<Sender>" + ApplicationProperties.getProprtyValue("project.space.request.sender.to.ln") + "</Sender>";
//
////                String projectQuery = NullOrEmptyChecker.isNullOrEmpty(projectSpace)
////                        ? MessageFormat.format(ApplicationProperties.getProprtyValue("all.project.space.fetch.from.ln"), ApplicationProperties.getProprtyValue("project.space.ln.receiver"), ApplicationProperties.getProprtyValue("project.space.request.sender.to.ln"))
////                        : MessageFormat.format(ApplicationProperties.getProprtyValue("single.project.space.fetch.from.ln"), projectSpace, ApplicationProperties.getProprtyValue("project.space.ln.receiver"), ApplicationProperties.getProprtyValue("project.space.request.sender.to.ln"));
//    }
//
//    public String getTaskQuery(String projectCode) {
////                        String taskQuery = "<Query>EXEC dbo.Proj_Act @Project='" + projectCode + "', @company='1001';</Query>\n"
////                                + "<Receiver>LNRxDEV</Receiver>" + "<Sender>Enovia</Sender>";
//        return "<Query>EXEC dbo.Proj_Act @Project='" + projectCode + "', @company='1001';</Query>\n"
//                + "<Receiver>" + ApplicationProperties.getProprtyValue("project.space.ln.receiver") + "</Receiver>" + "<Sender>" + ApplicationProperties.getProprtyValue("project.space.request.sender.to.ln") + "</Sender>";
////                        String taskQuery = MessageFormat.format(ApplicationProperties.getProprtyValue("project.spece.specific.task.query"), projectCode, ApplicationProperties.getProprtyValue("project.space.request.company"), ApplicationProperties.getProprtyValue("project.space.ln.receiver"), ApplicationProperties.getProprtyValue("project.space.request.sender.to.ln"));
//    }
//
//    public String getMileStoneQuery(String projectCode) {
////                        String mileStoneQuery = "<Query>EXEC dbo.Proj_Milestone @Project='" + projectCode + "', @company='1001';</Query>\n"
////                                + "<Receiver>LNRxDEV</Receiver>" + "<Sender>Enovia</Sender>";
//
//        return "<Query>EXEC dbo.Proj_Milestone @Project='" + projectCode + "', @company='1001';</Query>\n"
//                + "<Receiver>" + ApplicationProperties.getProprtyValue("project.space.ln.receiver") + "</Receiver>" + "<Sender>" + ApplicationProperties.getProprtyValue("project.space.request.sender.to.ln") + "</Sender>";
////                        String mileStoneQuery = MessageFormat.format(ApplicationProperties.getProprtyValue("project.spece.specific.milestone.query"), projectCode, ApplicationProperties.getProprtyValue("project.space.request.company"), ApplicationProperties.getProprtyValue("project.space.ln.receiver"), ApplicationProperties.getProprtyValue("project.space.request.sender.to.ln"));;
//
//    }
//
//    @Deprecated
//    private void populateTheTaskList(HashMap<String, List<TaskBean>> parentChildRelationShip, List<TaskBean> taskList, String taskName, HashMap<String, String> traversed) {
//
//        if (traversed.containsKey(taskName)) {
//            return;
//        }
//        traversed.put(taskName, taskName);
//
//        List<TaskBean> childTaskList = parentChildRelationShip.get(taskName);
//
//        if (NullOrEmptyChecker.isNullOrEmpty(childTaskList)) {
//            return;
//        }
//
//        taskList.addAll(childTaskList);
//
//        PROJECT_AND_TASK_CREATION_PROCESS_LOGGER.debug("Parent Task Name : " + taskName);
//        childTaskList.forEach((TaskBean task) -> {
//            String childTaskName = task.getActivity();
//            populateTheTaskList(parentChildRelationShip, taskList, childTaskName, traversed);
//        });
//    }

    private void sortTheTaskList(HashMap<String, List<TaskBean>> parentChildRelationShip, List<TaskBean> taskList, String taskName, HashMap<String, String> traversed) {

        if (traversed.containsKey(taskName)) {
            PROJECT_AND_TASK_CREATION_PROCESS_LOGGER.debug("Task : '" + taskName + "' is a duplicate task in the task list ");
            return;
        }
        traversed.put(taskName, taskName);

        List<TaskBean> childTaskList = parentChildRelationShip.get(taskName);

        if (NullOrEmptyChecker.isNullOrEmpty(childTaskList)) {
            PROJECT_AND_TASK_CREATION_PROCESS_LOGGER.debug("Task '" + taskName + "' has no children");
            return;
        }

        PROJECT_AND_TASK_CREATION_PROCESS_LOGGER.warn("Searching child tasks for : '" + taskName + "'");
        childTaskList.forEach((TaskBean task) -> {
            taskList.add(task);

            String childTaskName = task.getActivity();
            sortTheTaskList(parentChildRelationShip, taskList, childTaskName, traversed);
        });
    }

    private List<TaskBean> sortTasksAndMileStonesData(List<TaskBean> taskAndMileStoneData) {
        JSON json = new JSON(Boolean.FALSE);

        int totalTasks = taskAndMileStoneData.size();
        PROJECT_AND_TASK_CREATION_PROCESS_LOGGER.info("Tasks before sorting " + json.serialize(taskAndMileStoneData));
        PROJECT_AND_TASK_CREATION_PROCESS_LOGGER.info("Number of Tasks before sorting : " + totalTasks);

        HashMap<String, List<TaskBean>> parentChildRelationShip = new HashMap<>();
        HashMap<String, String> childParentTracker = new HashMap<>();
        taskAndMileStoneData.forEach((TaskBean taskOrMileStone) -> {
            String parentActivity = taskOrMileStone.getParentActivity();
            if (NullOrEmptyChecker.isNullOrEmpty(parentActivity)) {
                parentActivity = "root";
            }

            if (parentChildRelationShip.containsKey(parentActivity)) {
                List<TaskBean> tasksAndMileStonesAsChildren = parentChildRelationShip.get(parentActivity);
                tasksAndMileStonesAsChildren.add(taskOrMileStone);
            } else {
                List<TaskBean> tasksAndMileStonesAsChildren = new ArrayList<>();
                tasksAndMileStonesAsChildren.add(taskOrMileStone);
                parentChildRelationShip.put(parentActivity, tasksAndMileStonesAsChildren);
            }

            childParentTracker.put(taskOrMileStone.getActivity(), parentActivity);
        });

        PROJECT_AND_TASK_CREATION_PROCESS_LOGGER.info("Parent Child Relationship " + json.serialize(parentChildRelationShip));

        /**
         * Starting another new process
         */
        List<TaskBean> rootTasks = parentChildRelationShip.get("root");
        List<TaskBean> sortedTasks = new ArrayList<>();
        HashMap<String, String> traversed = new HashMap<>();

        rootTasks.forEach((TaskBean task) -> {
            String taskName = task.getActivity();
            sortedTasks.add(task);
            PROJECT_AND_TASK_CREATION_PROCESS_LOGGER.warn("Searching child tasks for : '" + taskName + "'");

//            populateTheTaskList(parentChildRelationShip, sortedTasks, taskName, traversed);
            sortTheTaskList(parentChildRelationShip, sortedTasks, taskName, traversed);
        });
        PROJECT_AND_TASK_CREATION_PROCESS_LOGGER.warn("Sorted Tasks : " + json.serialize(sortedTasks));

        int sortedTotalTasks = sortedTasks.size();
        int missingTasksAmount = totalTasks - sortedTotalTasks;
        PROJECT_AND_TASK_CREATION_PROCESS_LOGGER.warn("Missed after sorting : " + missingTasksAmount);

        showTheMissingTaskList(missingTasksAmount, taskAndMileStoneData, sortedTasks);

        return sortedTasks;
    }

    private void showTheMissingTaskList(int missingTasksAmount, List<TaskBean> taskAndMileStoneData, List<TaskBean> sortedTasks) {
        PROJECT_AND_TASK_CREATION_PROCESS_LOGGER.debug("##################################################");
        PROJECT_AND_TASK_CREATION_PROCESS_LOGGER.debug("# Missed tasks and milestones list after sorting #");
        PROJECT_AND_TASK_CREATION_PROCESS_LOGGER.debug("##################################################");
        if (missingTasksAmount > 0) {
            List<TaskBean> originalList = new ArrayList<>(taskAndMileStoneData);
            List<TaskBean> sortedList = new ArrayList<>(sortedTasks);
            originalList.removeAll(sortedList);

            originalList.forEach(action -> {
                PROJECT_AND_TASK_CREATION_PROCESS_LOGGER.warn("Task name : '" + action.getActivity() + "' parent name '" + action.getParentActivity() + "'");
            });

        } else if (missingTasksAmount < 0) {
            List<TaskBean> originalList = new ArrayList<>(taskAndMileStoneData);
            List<TaskBean> sortedList = new ArrayList<>(sortedTasks);
            sortedList.removeAll(originalList);

            sortedList.forEach(action -> {
                PROJECT_AND_TASK_CREATION_PROCESS_LOGGER.warn("Task name : '" + action.getActivity() + "' parent name '" + action.getParentActivity() + "'");
            });
        }

        PROJECT_AND_TASK_CREATION_PROCESS_LOGGER.debug("##################################################");
        PROJECT_AND_TASK_CREATION_PROCESS_LOGGER.debug("# Missed tasks and milestones list after sorting #");
        PROJECT_AND_TASK_CREATION_PROCESS_LOGGER.debug("##################################################");
    }

    private TaskDataBean getTaskOrMileStoneData(String taskQuery) throws Exception {
        PROJECT_AND_TASK_CREATION_PROCESS_LOGGER.info("Task Query : " + taskQuery);
        PROJECT_AND_TASK_CREATION_PROCESS_LOGGER.info("Getting task data from service");
        TaskDataBean taskData;
        try {
            taskData = getDataFromService(ApplicationProperties.getProprtyValue("ln.project.or.task.or.milestone.fetching.url"), taskQuery, TaskDataBean.class);
        } catch (Exception exp) {
            PROJECT_AND_TASK_CREATION_PROCESS_LOGGER.error(exp);
            List<TaskBean> tableData = new ArrayList<>();

            TaskTableData taskTableData = getDataFromService(ApplicationProperties.getProprtyValue("ln.project.or.task.or.milestone.fetching.url"), taskQuery, TaskTableData.class);
            TaskBean taskBean = taskTableData.getTableData();
            tableData.add(taskBean);

            taskData = new TaskDataBean();
            taskData.setTableData(tableData);
        }
        return taskData;
    }

    private TaskDataBean getTaskOrMileStoneDataFromFile(String fileName) throws Exception {
        PROJECT_AND_TASK_CREATION_PROCESS_LOGGER.info("Getting task data from service");
        TaskDataBean taskData = getDataFromService(TaskDataBean.class, fileName);
        return taskData;
    }

    private HashMap<String, ActivityModel> createTaskOrMileStone(Context context, TaskDataBean taskData, String newProjectSpaceObjectId, HashMap<String, String> taskNamesAndIds, HashMap<String, ActivityModel> projectSpaceExpandedMap, ProjectSpaceUtil projectSpaceUtil) throws MatrixException {
//        if (deleteAllTasksAndMileStones) {
//            projectSpaceUtil.deleteTaskAndMileStone(context, projectSpaceExpandedMap);
//        }
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

        taskData.getTableData().forEach((TaskBean task) -> {
            try {
                String activityTaskName = task.getActivity();
                PROJECT_AND_TASK_CREATION_PROCESS_LOGGER.debug("Task name : " + activityTaskName);

                if (NullOrEmptyChecker.isNullOrEmpty(task.getActivityType())) {
                    PROJECT_AND_TASK_CREATION_PROCESS_LOGGER.warn("'" + activityTaskName + "' skipping the task as the type of the task is empty");
                    return;
                }

                String parentActivityName = task.getParentActivity();
                String parentIdFromLNJsonResponse = NullOrEmptyChecker.isNull(task.getParentActivity()) ? newProjectSpaceObjectId : taskNamesAndIds.get(parentActivityName);

                if (NullOrEmptyChecker.isNullOrEmpty(parentIdFromLNJsonResponse)) {
                    String parentActivity = task.getParentActivity();
                    ActivityModel parentActivityModel = projectSpaceExpandedMap.get(parentActivity);
                    if (NullOrEmptyChecker.isNull(parentActivityModel)) {
                        PROJECT_AND_TASK_CREATION_PROCESS_LOGGER.warn("'" + activityTaskName + "' has been being skipped as it's parent '" + parentActivity + "' has not found in the system");
                        return;
                    }

                    parentIdFromLNJsonResponse = parentActivityModel.getActivityId();
                    if (NullOrEmptyChecker.isNullOrEmpty(parentIdFromLNJsonResponse)) {
                        throw new RuntimeException("Faulty ProjectSpace and Task structure");
                    }
                }

                String activityName = task.getActivity();
                String activityType = task.getActivityType();

                PROJECT_AND_TASK_CREATION_PROCESS_LOGGER.info("Task parent activity : " + parentActivityName);
                PROJECT_AND_TASK_CREATION_PROCESS_LOGGER.info("Task parent id : " + parentIdFromLNJsonResponse);
                PROJECT_AND_TASK_CREATION_PROCESS_LOGGER.info("Activity Type : '" + activityType + "'");

                PROJECT_AND_TASK_CREATION_PROCESS_LOGGER.info("################");
                PROJECT_AND_TASK_CREATION_PROCESS_LOGGER.info("# FOUND A TASK #");
                PROJECT_AND_TASK_CREATION_PROCESS_LOGGER.info("################");

                if (projectSpaceExpandedMap.containsKey(activityName)) {
                    ActivityModel activityModel = projectSpaceExpandedMap.get(activityName);
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
                PROJECT_AND_TASK_CREATION_PROCESS_LOGGER.error(exp);
                throw new RuntimeException(exp);
            }
        });

        HashMap<String, ActivityModel> projectSpaceAndTasksInV6 = new HashMap<>(projectSpaceExpandedMap);
        Set<String> tasksInV6 = projectSpaceAndTasksInV6.keySet();
        Set<String> tasksInLN = taskNamesAndIds.keySet();

        tasksInV6.removeAll(tasksInLN);
        tasksInV6.forEach((String removedTask) -> {

            boolean fullSynchronization = Boolean.parseBoolean(ApplicationProperties.getProprtyValue("full.synchronization.by.deleting.or.disconnecting.tasks.or.milestones"));

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

//        if (deleteAllTasksAndMileStones) {
//            projectSpaceUtil.deleteTaskAndMileStone(context, projectSpaceExpandedMap);
//        }
        return projectSpaceExpandedMap;
    }

    private HashMap<String, String> getCreateOrUpdatePropertiesAndSetInterfaces(Context context, String objectIdForAddingInterface, String projectStructureConstant, TaskBean task) throws MatrixException {
        //HashMap<String, String> createOrUpdateProperties = new XMLMapUtilities().getCreateOrUpdateProperties(task.getActivityType(), Constants.UPDATE, new ReflectionUtilities().getHashMapFromAnnotaionAndValue(task));
        HashMap<String, String> createOrUpdateProperties = new ComosProjectStructureXMLMapUtils().getCreateOrUpdateProperties(task.getActivityType(), projectStructureConstant, new ReflectionUtilities().getHashMapFromAnnotaionAndValue(task));
        return addInterfaceToTheObject(createOrUpdateProperties, context, objectIdForAddingInterface);
    }

    private HashMap<String, String> getCreateOrUpdatePropertiesAndSetInterfaces(Context context, String objectIdForAddingInterface, String projectConstants, ActivitiesBean activitiesBean) throws MatrixException {
        HashMap<String, String> createOrUpdateProperties = new ComosProjectStructureXMLMapUtils().getCreateOrUpdateProperties(Constants.PROJECT_SPACE, projectConstants, new ReflectionUtilities().getHashMapFromAnnotaionAndValue(activitiesBean));
        return addInterfaceToTheObject(createOrUpdateProperties, context, objectIdForAddingInterface);
    }

    private HashMap<String, String> addInterfaceToTheObject(HashMap<String, String> createOrUpdateProperties, Context context, String objectIdForAddingInterface) throws MatrixException {
        try {
            String runtimeInterfaceList = createOrUpdateProperties.get("runtimeInterfaceList");
            createOrUpdateProperties.remove("runtimeInterfaceList");
            new ObjectUtility().addInterface(context, objectIdForAddingInterface, runtimeInterfaceList, "");
            return createOrUpdateProperties;
        } catch (MatrixException ex) {
            PROJECT_AND_TASK_CREATION_PROCESS_LOGGER.error(ex);
            throw ex;
        }
    }

    private Boolean disconnectTask(Context context, ActivityModel v6ActivityModel) {
        try {
            String taskName = v6ActivityModel.getActivityName();
            String v6ParentId = v6ActivityModel.getParentId();

            PROJECT_AND_TASK_CREATION_PROCESS_LOGGER.info("Disconnecting '" + taskName + "' from '" + v6ActivityModel.getParentName() + "'");

            BusinessObject v6ParentBusinessObject = new BusinessObject(v6ParentId);
            v6ParentBusinessObject.open(context);

            PROJECT_AND_TASK_CREATION_PROCESS_LOGGER.info("Task id is : " + v6ActivityModel.getActivityId());
            String relationshipId = v6ActivityModel.getRelationshipId();
            Relationship relationShip = new Relationship(relationshipId);
            v6ParentBusinessObject.disconnect(context, relationShip);
            return true;
        } catch (MatrixException ex) {
            PROJECT_AND_TASK_CREATION_PROCESS_LOGGER.debug(ex);
            return false;
        }
    }

    private void createTask(Context context, ProjectSpaceUtil projectSpaceUtil, HashMap<String, String> taskNamesAndIds, HashMap<String, ActivityModel> projectSpaceExpandedMap, String parentIdFromLNJsonResponse, TaskBean task, String activityName, String parentActivityName) throws Exception {
        HashMap<String, String> taskOrMileStoneCreateProperties = new ReflectionUtilities().getHashMapFromAnnotaionAndValue(task);
        taskOrMileStoneCreateProperties.put("ActivityType", taskOrMileStoneCreateProperties.get("ActivityType").equalsIgnoreCase(Constants.MILE_STONE) ? Constants.MILE_STONE : Constants.TASK);
        taskOrMileStoneCreateProperties.put("objectId", parentIdFromLNJsonResponse);
        taskOrMileStoneCreateProperties.put("parentId", parentIdFromLNJsonResponse);
        HashMap<String, String> taskAttributeValueMap = new ComosProjectStructureXMLMapUtils().getCreateOrUpdateProperties(task.getActivityType(), Constants.CREATE, taskOrMileStoneCreateProperties);
        
        PROJECT_AND_TASK_CREATION_PROCESS_LOGGER.info("create and connect task with the parent");
        
        TaskProcessor taskProcessor = new TaskProcessor();
        String newTaskId = taskProcessor.createTask(context, taskAttributeValueMap, task, Boolean.FALSE);
        PROJECT_AND_TASK_CREATION_PROCESS_LOGGER.debug("New Task Id : " + newTaskId);

        if (NullOrEmptyChecker.isNullOrEmpty(newTaskId)) {
            PROJECT_AND_TASK_CREATION_PROCESS_LOGGER.warn("'" + task.getActivity() + "' is going to skip as their type is missing");
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

        projectSpaceExpandedMap.put(activityName, newChildactivityModel);

        HashMap<String, String> createOrUpdateProperties = getCreateOrUpdatePropertiesAndSetInterfaces(context, newTaskId, Constants.UPDATE, task);
        updateTask(context, taskNamesAndIds, createOrUpdateProperties, projectSpaceUtil, newChildactivityModel.getActivityName(), parentActivityName, newChildactivityModel, task);
    }

    private void disConnectAndReConnecteTask(Context context, ProjectSpaceUtil projectSpaceUtil, HashMap<String, String> taskNamesAndIds, ActivityModel activityModel, String parentIdFromLNJsonResponse, TaskBean task) throws MatrixException {
        String taskName = activityModel.getActivityName();
        String v6ParentId = activityModel.getParentId();
        String parentActivityName = task.getParentActivity();
        PROJECT_AND_TASK_CREATION_PROCESS_LOGGER.info("Disconnecting '" + taskName + "' from '" + activityModel.getParentName() + "'");

        BusinessObject v6ParentBusinessObject = new BusinessObject(v6ParentId);
        v6ParentBusinessObject.open(context);

        PROJECT_AND_TASK_CREATION_PROCESS_LOGGER.info("Task id is : " + activityModel.getActivityId());
        String relationshipId = activityModel.getRelationshipId();
        Relationship relationShip = new Relationship(relationshipId);
        v6ParentBusinessObject.disconnect(context, relationShip);

        RelationshipType relationshipType = new RelationshipType();
        relationshipType.setName("Subtask");

        PROJECT_AND_TASK_CREATION_PROCESS_LOGGER.info("Connecting '" + taskName + "' as a child of '" + parentActivityName + "'");

        BusinessObject parentBusinessObject = new BusinessObject(parentIdFromLNJsonResponse);
        BusinessObject childBusinessObject = new BusinessObject(activityModel.getActivityId());
        parentBusinessObject.open(context);
        childBusinessObject.open(context);

        Relationship connect = parentBusinessObject.connect(context, relationshipType, true, childBusinessObject);
        String newRelationshipId = connect.getName();

        PROJECT_AND_TASK_CREATION_PROCESS_LOGGER.info("'" + taskName + "' has connected as a child of '" + parentActivityName + "' by '" + newRelationshipId + "' connection id");

        activityModel.setParentName(parentActivityName);
        activityModel.setParentId(parentIdFromLNJsonResponse);
        activityModel.setRelationshipId(newRelationshipId);

        taskNamesAndIds.put(task.getActivity(), activityModel.getActivityId());
        
        HashMap<String, String> createOrUpdateProperties = getCreateOrUpdatePropertiesAndSetInterfaces(context, activityModel.getActivityId(), Constants.UPDATE, task);
        updateTask(context, taskNamesAndIds, createOrUpdateProperties, projectSpaceUtil, activityModel.getActivityName(), parentActivityName, activityModel, task);
    }

    private void updateTask(Context context, HashMap<String, String> taskNamesAndIds, HashMap<String, String> updateProperties, ProjectSpaceUtil projectSpaceUtil, String activityName, String parentActivityName, ActivityModel activityModel, TaskBean task) throws FrameworkException {
        PROJECT_AND_TASK_CREATION_PROCESS_LOGGER.info("Activity '" + activityName + "' already connected to the parent '" + parentActivityName + "'");
        PROJECT_AND_TASK_CREATION_PROCESS_LOGGER.debug("Task Id : " + activityModel.getActivityId());

        if (NullOrEmptyChecker.isNullOrEmpty(task.getActivityType())) {
            PROJECT_AND_TASK_CREATION_PROCESS_LOGGER.warn("'" + task.getActivity() + "' is going to be skipped as it's type is empty");
            return;
        }

        taskNamesAndIds.put(task.getActivity(), activityModel.getActivityId());
        PROJECT_AND_TASK_CREATION_PROCESS_LOGGER.info("###################");
        PROJECT_AND_TASK_CREATION_PROCESS_LOGGER.info("###################");
        PROJECT_AND_TASK_CREATION_PROCESS_LOGGER.info("# Update The Task #");
        PROJECT_AND_TASK_CREATION_PROCESS_LOGGER.info("###################");
        PROJECT_AND_TASK_CREATION_PROCESS_LOGGER.info("###################");
        projectSpaceUtil.updateItem(context, activityModel.getActivityId(), updateProperties);
    }

    public <T> T getDataFromService(String serviceUrl, String query, Class<T> classReference) throws Exception {
        try {
            String dataFromService = ServiceRequester.callService(serviceUrl, query);

            String trimmedData = dataFromService.trim();
            JSONObject jsonObject = JSONObject.fromObject(trimmedData);
            String jsonStringData = jsonObject.toString();

            PROJECT_AND_TASK_CREATION_PROCESS_LOGGER.debug("Json Object : " + jsonStringData);

            JSON jsonizer = new JSON();
            T data = jsonizer.deserialize(jsonStringData, classReference);
            return data;
        } catch (Exception exp) {
            throw exp;
        }
    }

    public String readFileAsString(String fileName) throws Exception {
        String data = "";
        data = new String(Files.readAllBytes(Paths.get(fileName)));
        return data;
    }

    public <T> T getDataFromService(Class<T> classReference, String fileName) throws Exception {
        try {
            PROJECT_AND_TASK_CREATION_PROCESS_LOGGER.info("Reading data from file");
            String directory = "C:/Users/BJIT/Desktop/ProjectSpaceFiless/" + fileName;
            PROJECT_AND_TASK_CREATION_PROCESS_LOGGER.debug("File directory is : " + directory);

            String dataFromService = readFileAsString(directory);

            String trimmedData = dataFromService.trim();
            JSONObject jsonObject = JSONObject.fromObject(trimmedData);
            String jsonStringData = jsonObject.toString();

            PROJECT_AND_TASK_CREATION_PROCESS_LOGGER.debug("Json Object : " + jsonStringData);

            JSON jsonizer = new JSON();
            T data = jsonizer.deserialize(jsonStringData, classReference);
            return data;
        } catch (Exception exp) {
            throw exp;
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
}
