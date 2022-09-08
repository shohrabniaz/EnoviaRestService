package com.bjit.common.rest.app.service.controller.wbsImport;

import com.bjit.common.rest.app.service.controller.lntransfer.*;
import com.bjit.common.rest.app.service.model.wbs.ln.ActivitiesBean;
import com.bjit.common.rest.app.service.model.wbs.ln.ProjectDataBean;
import com.bjit.common.rest.app.service.model.wbs.ln.ProjectTableData;
import com.bjit.common.rest.app.service.model.wbs.ln.TaskBean;
import com.bjit.common.rest.app.service.model.wbs.ln.TaskDataBean;
import com.bjit.common.rest.app.service.model.wbs.ln.TaskTableData;
import com.bjit.common.rest.app.service.payload.common_response.CustomResponseBuilder;
import com.bjit.common.rest.app.service.payload.common_response.IResponse;
import com.bjit.common.rest.app.service.payload.common_response.Status;
import com.bjit.common.rest.app.service.utilities.DateTimeUtils;
import com.bjit.common.rest.app.service.utilities.DisableSSLCertificate;
import com.bjit.common.rest.app.service.utilities.JSON;
import com.bjit.common.rest.app.service.utilities.NullOrEmptyChecker;
import com.bjit.common.rest.app.service.utilities.ServiceRequester;
import com.bjit.ewc18x.utils.PropertyReader;
import com.bjit.project_structure.ProjectAndTaskCreationProcess;
import com.bjit.project_structure.TaskImportProcess;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import net.sf.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author BJIT
 */
@RestController
public class WbsImportController {

    private static final org.apache.log4j.Logger WBS_ENOVIA_FROM_LN_LOGGER = org.apache.log4j.Logger.getLogger(LNTransferController.class);
    
    @RequestMapping(value = "/wbs-import/{projectId}", method = RequestMethod.GET)
    @ResponseStatus(value = HttpStatus.OK)
    public com.bjit.project_structure.model.ProjectDataBean enoviaFromLN(@PathVariable("projectId") String projectId) throws Exception {
        com.bjit.project_structure.model.ProjectDataBean projectData = null;
        try {
            WBS_ENOVIA_FROM_LN_LOGGER.info("Import process Started\n\n\n");

            ProjectAndTaskCreationProcess projectAndTaskCreationProcess = new ProjectAndTaskCreationProcess();
            projectData = projectAndTaskCreationProcess.ProjectAndTaskCreate(0, projectId); // 0 means all project space

            WBS_ENOVIA_FROM_LN_LOGGER.info("Import process completed successfully");

        } catch (Exception exp) {
            WBS_ENOVIA_FROM_LN_LOGGER.error(exp.getMessage());
        }
        return projectData;
    }
    
    @RequestMapping(value={"/wbs-search/project"}, method={RequestMethod.GET}, produces={"application/json"})
    @ResponseBody
    public ResponseEntity<?> searchAllProjectsFromLN() {
        
        WBS_ENOVIA_FROM_LN_LOGGER.info("---------------------- ||| PROJECT SEARCH START ||| ----------------------");
        WBS_ENOVIA_FROM_LN_LOGGER.info("##########################################################################");
        
        Instant wbsSearchStartTime = Instant.now();
        IResponse responseBuilder = new CustomResponseBuilder();
        String buildResponse;
        
        try {
            if(Boolean.parseBoolean(PropertyReader.getProperty("ln.disable.ssl.certificate"))) {
                WBS_ENOVIA_FROM_LN_LOGGER.info("Dissabling SSL certificates");
                try {
                    DisableSSLCertificate.DisableCertificate();
                } catch(KeyManagementException | NoSuchAlgorithmException kme) {
                    WBS_ENOVIA_FROM_LN_LOGGER.error("Error occured when disabling ssl certificates : " + kme.getMessage());
                }
            }
            
            String LNReceiver = PropertyReader.getProperty("project.space.ln.receiver");
            String sender = PropertyReader.getProperty("project.space.request.sender.to.ln");
            
            Optional.ofNullable(LNReceiver)
                    .orElseThrow(() -> new NullPointerException("LN Receiver property for project structure import not found for " + PropertyReader.getEnvironmentName()));
            
            Optional.ofNullable(sender)
                    .orElseThrow(() -> new NullPointerException("Request Sender property for project structure import not found for " + PropertyReader.getEnvironmentName()));

            String projectQuery = "<Query>\n"
                            + "	SELECT distinct(PROJECT.ID) as ProjectCode, PROJECT_LC.DSC as Description FROM PROJECT\n"
                            + "	inner join PROJECT_MV on PROJECT.NID=PROJECT_MV.NID and PROJECT.ID_VARIATION_ID=PROJECT_MV.ID_VARIATION_ID\n"
                            + "	inner join PROJECT_LC on PROJECT_LC.NID=PROJECT_MV.NID and PROJECT_LC.LOCALE='default' WHERE \n"
                            + "	PROJECT.S_CD='open' AND PROJECT.PROJ_HRCHY_TYP in ('Sub Project', 'Single Project');\n"
                            + "</Query>\n"
                            + "<Sender>" + sender + "</Sender>\n"
                            + "<Receiver>" + LNReceiver + "</Receiver>";

            WBS_ENOVIA_FROM_LN_LOGGER.info("Getting project data from service");

            ProjectDataBean projectSpaceData;
            
            projectSpaceData = this.getDataFromService(PropertyReader.getProperty("project.space.ln.wbs.query.url"), projectQuery, ProjectDataBean.class);
            
            if(!NullOrEmptyChecker.isNull(projectSpaceData.getError())) {
                if (projectSpaceData.getError().length() > 0) {
                    throw new Exception(projectSpaceData.getError());
                }
            }
            
            if(NullOrEmptyChecker.isNullOrEmpty(projectSpaceData.getTableData())) {
                projectSpaceData.setTableData(new ArrayList<>());
            }

            buildResponse = responseBuilder.setData(projectSpaceData.getTableData()).setStatus(Status.OK).buildResponse();
            WBS_ENOVIA_FROM_LN_LOGGER.debug(buildResponse);
            return new ResponseEntity(buildResponse, HttpStatus.OK);
        } catch (Exception exp) {
            WBS_ENOVIA_FROM_LN_LOGGER.error("Error occured during project search: " + exp.getMessage());
            buildResponse = responseBuilder.addErrorMessage(exp.getMessage()).setStatus(Status.FAILED).buildResponse();
            WBS_ENOVIA_FROM_LN_LOGGER.debug(buildResponse);
            return new ResponseEntity(buildResponse, HttpStatus.NOT_ACCEPTABLE);
        } finally {
            Instant wbsSearchEndTime = Instant.now();
            long duration = DateTimeUtils.getDuration(wbsSearchStartTime, wbsSearchEndTime);
            
            WBS_ENOVIA_FROM_LN_LOGGER.info("LN WBS Project Search Process has taken : '" + duration + "' milli-seconds");
            WBS_ENOVIA_FROM_LN_LOGGER.info("########################################################################");
            WBS_ENOVIA_FROM_LN_LOGGER.info("---------------------- ||| PROJECT SEARCH END ||| ----------------------");
        }
    }
    
    @RequestMapping(value={"/wbs-search/project/{projectId}"}, method={RequestMethod.GET}, produces={"application/json"})
    @ResponseBody
    public ResponseEntity<?> searchProjectFromLN(@PathVariable(value = "projectId") String projectId) {
        
        WBS_ENOVIA_FROM_LN_LOGGER.info("---------------------- ||| PROJECT SEARCH ||| ----------------------");
        WBS_ENOVIA_FROM_LN_LOGGER.info("####################################################################");
        
        Instant wbsSearchStartTime = Instant.now();
        IResponse responseBuilder = new CustomResponseBuilder();
        String buildResponse;
        
        try {
            if(Boolean.parseBoolean(PropertyReader.getProperty("ln.disable.ssl.certificate"))) {
                WBS_ENOVIA_FROM_LN_LOGGER.info("Dissabling SSL certificates");
                try {
                    DisableSSLCertificate.DisableCertificate();
                } catch(KeyManagementException | NoSuchAlgorithmException kme) {
                    WBS_ENOVIA_FROM_LN_LOGGER.error("Error occured when disabling ssl certificates : " + kme.getMessage());
                }
            }
            
            String LNReceiver = PropertyReader.getProperty("project.space.ln.receiver");
            String sender = PropertyReader.getProperty("project.space.request.sender.to.ln");
            
            Optional.ofNullable(LNReceiver)
                    .orElseThrow(() -> new NullPointerException("LN Receiver property for project structure import not found for " + PropertyReader.getEnvironmentName()));
            
            Optional.ofNullable(sender)
                    .orElseThrow(() -> new NullPointerException("Request Sender property for project structure import not found for " + PropertyReader.getEnvironmentName()));

            String projectQuery = "<Query>\n"
                            + "     SELECT distinct(PROJECT.ID) as ProjectCode, PROJECT_LC.DSC as Description FROM PROJECT\n"
                            + "     inner join PROJECT_MV on PROJECT.NID=PROJECT_MV.NID and PROJECT.ID_VARIATION_ID=PROJECT_MV.ID_VARIATION_ID\n"
                            + "     inner join PROJECT_LC on PROJECT_LC.NID=PROJECT_MV.NID and PROJECT_LC.LOCALE='default' WHERE \n"
                            + "     PROJECT.S_CD='open' AND PROJECT.ID='" + projectId + "' AND PROJECT.PROJ_HRCHY_TYP in ('Sub Project', 'Single Project');\n"
                            + "</Query>\n"
                            + "<Sender>" + sender + "</Sender>\n"
                            + "<Receiver>" + LNReceiver + "</Receiver>";

            WBS_ENOVIA_FROM_LN_LOGGER.info("Getting project data from service");

            ProjectDataBean projectSpaceData;

            try {
                projectSpaceData = this.getDataFromService(PropertyReader.getProperty("project.space.ln.wbs.query.url"), projectQuery, ProjectDataBean.class);
                if(!NullOrEmptyChecker.isNull(projectSpaceData.getError())) {
                    if (projectSpaceData.getError().length() > 0) {
                        projectSpaceData = new ProjectDataBean();
                    }
                }
            } catch (Exception exp) {
                WBS_ENOVIA_FROM_LN_LOGGER.error("Error occured during project search: " + exp.getMessage());
                try {
                    ActivitiesBean activitiesBean;
                    if (!NullOrEmptyChecker.isNullOrEmpty(projectId)) {
                        ProjectTableData projectTableData = this.getDataFromService(PropertyReader.getProperty("project.space.ln.wbs.query.url"), projectQuery, ProjectTableData.class);
                        activitiesBean = projectTableData.getTableData();
                    } else {
                        activitiesBean = this.getDataFromService(PropertyReader.getProperty("project.space.ln.wbs.query.url"), projectQuery, ActivitiesBean.class);
                    }

                    projectSpaceData = new ProjectDataBean();
                    List<ActivitiesBean> tableData = new ArrayList<>();
                    tableData.add(activitiesBean);
                    projectSpaceData.setTableData(tableData);
                } catch (Exception e) {
                    WBS_ENOVIA_FROM_LN_LOGGER.error("Error occured during project search: " + exp.getMessage());
                    projectSpaceData = new ProjectDataBean();
                }
            }
            
            if(NullOrEmptyChecker.isNullOrEmpty(projectSpaceData.getTableData())) {
                projectSpaceData.setTableData(new ArrayList<>());
            }

            buildResponse = responseBuilder.setData(projectSpaceData.getTableData()).setStatus(Status.OK).buildResponse();
            WBS_ENOVIA_FROM_LN_LOGGER.debug(buildResponse);
            return new ResponseEntity(buildResponse, HttpStatus.OK);
        } catch (Exception exp) {
            WBS_ENOVIA_FROM_LN_LOGGER.error(exp);
            buildResponse = responseBuilder.addErrorMessage(exp.getMessage()).setStatus(Status.FAILED).buildResponse();
            WBS_ENOVIA_FROM_LN_LOGGER.debug(buildResponse);
            return new ResponseEntity(buildResponse, HttpStatus.NOT_ACCEPTABLE);
        } finally {
            Instant wbsSearchEndTime = Instant.now();
            long duration = DateTimeUtils.getDuration(wbsSearchStartTime, wbsSearchEndTime);
            
            WBS_ENOVIA_FROM_LN_LOGGER.info("LN WBS Project Search Process has taken : '" + duration + "' milli-seconds");
            WBS_ENOVIA_FROM_LN_LOGGER.info("########################################################################");
            WBS_ENOVIA_FROM_LN_LOGGER.info("---------------------- ||| PROJECT SEARCH END ||| ----------------------");
        }
    }
    
    @RequestMapping(value={"/wbs-search/project/{projectId}/activity"}, method={RequestMethod.GET}, produces={"application/json"})
    @ResponseBody
    public ResponseEntity<?> searchActivityFromLN(@PathVariable("projectId") String projectId) {
        
        WBS_ENOVIA_FROM_LN_LOGGER.info("---------------------- ||| Activity SEARCH START ||| ----------------------");
        WBS_ENOVIA_FROM_LN_LOGGER.info("###########################################################################");
        
        Instant wbsSearchStartTime = Instant.now();
        IResponse responseBuilder = new CustomResponseBuilder();
        String buildResponse;
        
        try {
            if(Boolean.parseBoolean(PropertyReader.getProperty("ln.disable.ssl.certificate"))) {
                WBS_ENOVIA_FROM_LN_LOGGER.info("Dissabling SSL certificates");
                try {
                    DisableSSLCertificate.DisableCertificate();
                } catch(KeyManagementException | NoSuchAlgorithmException kme) {
                    WBS_ENOVIA_FROM_LN_LOGGER.error("Error occured when disabling ssl certificates : " + kme.getMessage());
                }
            }
            
            String LNReceiver = PropertyReader.getProperty("project.space.ln.receiver");
            String sender = PropertyReader.getProperty("project.space.request.sender.to.ln");
            String company = PropertyReader.getProperty("project.space.request.company");
            
            Optional.ofNullable(LNReceiver)
                    .orElseThrow(() -> new NullPointerException("LN Receiver property for project structure import not found for " + PropertyReader.getEnvironmentName()));
            
            Optional.ofNullable(sender)
                    .orElseThrow(() -> new NullPointerException("Request Sender property for project structure import not found for " + PropertyReader.getEnvironmentName()));
            
            Optional.ofNullable(sender)
                    .orElseThrow(() -> new NullPointerException("Company property for project structure import not found for " + PropertyReader.getEnvironmentName()));
            
            String taskQuery = "<Query>EXEC dbo.Proj_Act @Project='" + projectId + "', @company='" + company + "';</Query>\n"
                                + "<Sender>" + sender + "</Sender>\n"
                                + "<Receiver>" + LNReceiver + "</Receiver>";
            
            TaskDataBean taskData;
                                
            try {
                taskData = getTaskOrMileStoneData(taskQuery);
            } catch (Exception exp) {
                WBS_ENOVIA_FROM_LN_LOGGER.error(exp);
                taskData = new TaskDataBean();
            }
            
            if(NullOrEmptyChecker.isNull(taskData.getTableData())) {
                taskData.setTableData(new ArrayList<>());
            }
            
            String mileStoneQuery = "<Query>EXEC dbo.Proj_Milestone @Project='" + projectId + "', @company='" + company + "';</Query>\n"
                                    + "<Sender>" + sender + "</Sender>\n"
                                    + "<Receiver>" + LNReceiver + "</Receiver>";

            TaskDataBean mileStoneData;
            
            try {
                mileStoneData = getTaskOrMileStoneData(mileStoneQuery);
            } catch (Exception exp) {
                WBS_ENOVIA_FROM_LN_LOGGER.error(exp);
                mileStoneData = new TaskDataBean();
            }
            
            if(NullOrEmptyChecker.isNull(mileStoneData.getTableData())) {
                mileStoneData.setTableData(new ArrayList<>());
            }
            
            if (NullOrEmptyChecker.isNull(taskData) && NullOrEmptyChecker.isNull(mileStoneData)) {
                WBS_ENOVIA_FROM_LN_LOGGER.error("No task and milestone found");
                taskData = new TaskDataBean();
                taskData.setTableData(new ArrayList());
            } else if (NullOrEmptyChecker.isNull(taskData) || NullOrEmptyChecker.isNull(mileStoneData)) {
                if (NullOrEmptyChecker.isNull(taskData)) {
                    taskData = mileStoneData;
                }
            } else {
                taskData.mergeTaskData(mileStoneData);
            }
            
            List<TaskBean> filteredTask = filterOutTaskWithMissingType(taskData.getTableData());
            
            List<TaskBean> sortTasksAndMileStonesData = sortTasksAndMileStonesData(filteredTask);
            
            HashMap<String, TaskBean> activityMap = new HashMap<>();
            for(TaskBean taskBean : sortTasksAndMileStonesData) {
                activityMap.put(taskBean.getActivity(), taskBean);
            }
            
            List<TaskBean> activityList = new ArrayList<>();
            if(!NullOrEmptyChecker.isNullOrEmpty(activityMap)) {
                activityList = new ArrayList<> (activityMap.values());
            }
            
            buildResponse = responseBuilder.setData(activityList).setStatus(Status.OK).buildResponse();
            WBS_ENOVIA_FROM_LN_LOGGER.debug(buildResponse);
            return new ResponseEntity(buildResponse, HttpStatus.OK);
        } catch (Exception exp) {
            WBS_ENOVIA_FROM_LN_LOGGER.error(exp);
            buildResponse = responseBuilder.addErrorMessage(exp.getMessage()).setStatus(Status.FAILED).buildResponse();
            WBS_ENOVIA_FROM_LN_LOGGER.debug(buildResponse);
            return new ResponseEntity(buildResponse, HttpStatus.NOT_ACCEPTABLE);
        } finally {
            Instant wbsSearchEndTime = Instant.now();
            long duration = DateTimeUtils.getDuration(wbsSearchStartTime, wbsSearchEndTime);
            
            WBS_ENOVIA_FROM_LN_LOGGER.info("LN WBS Activity Search Process has taken : '" + duration + "' milli-seconds");
            WBS_ENOVIA_FROM_LN_LOGGER.info("#########################################################################");
            WBS_ENOVIA_FROM_LN_LOGGER.info("---------------------- ||| Activity SEARCH END ||| ----------------------");
        }
        
    }
    
    @RequestMapping(value={"/wbs-import"}, method={RequestMethod.POST}, produces={"application/json"})
    @ResponseBody
    public ResponseEntity<?> importWBSProjectAndActivityFromLNToEnovia(@RequestBody com.bjit.project_structure.model.WBSImportBean wbsImportBean) {
        
        WBS_ENOVIA_FROM_LN_LOGGER.info("---------------------- ||| LN to ENOVIA WBS IMPORT BEGIN ||| ----------------------");
        WBS_ENOVIA_FROM_LN_LOGGER.info("###################################################################################");
        
        Instant wbsImportStartTime = Instant.now();
        IResponse responseBuilder = new CustomResponseBuilder();
        String buildResponse;
        
        WBS_ENOVIA_FROM_LN_LOGGER.debug("Starting import project: "+ wbsImportBean.getProjectCode());
        
        try {
            TaskImportProcess  taskImport = new TaskImportProcess();
            wbsImportBean = taskImport.projectAndTaskCreate(wbsImportBean);
            
            buildResponse = responseBuilder.setData(wbsImportBean).setStatus(Status.OK).buildResponse();
            WBS_ENOVIA_FROM_LN_LOGGER.debug(buildResponse);
            return new ResponseEntity(buildResponse, HttpStatus.OK);
        } catch (Exception e) {
            buildResponse = responseBuilder.addErrorMessage(e.getMessage()).setStatus(Status.FAILED).buildResponse();
            WBS_ENOVIA_FROM_LN_LOGGER.debug(buildResponse);
            return new ResponseEntity(buildResponse, HttpStatus.OK);
        } finally {
            Instant wbsImportEndTime = Instant.now();
            long duration = DateTimeUtils.getDuration(wbsImportStartTime, wbsImportEndTime);
            
            WBS_ENOVIA_FROM_LN_LOGGER.info("LN WBS Import Process has taken : '" + duration + "' milli-seconds");
            WBS_ENOVIA_FROM_LN_LOGGER.info("#################################################################################");
            WBS_ENOVIA_FROM_LN_LOGGER.info("---------------------- ||| LN to ENOVIA WBS IMPORT END ||| ----------------------");
        }
    }
    
    
    /* ------------------ >>>   Service Utilities <<< ------------------ */
    
    private <T> T getDataFromService(String serviceUrl, String query, Class<T> classReference) throws Exception {
        try {
            ServiceRequester serviceRequester = new ServiceRequester();
            String dataFromService = serviceRequester.callService(serviceUrl, query);

            String trimmedData = dataFromService.trim();
            JSONObject jsonObject = JSONObject.fromObject(trimmedData);
            String jsonStringData = jsonObject.toString();

            WBS_ENOVIA_FROM_LN_LOGGER.debug("Json Object : " + jsonStringData);

            JSON jsonizer = new JSON();
            T data = jsonizer.deserialize(jsonStringData, classReference);
            return data;
        } catch (Exception exp) {
            throw exp;
        }
    }

    private TaskDataBean getTaskOrMileStoneData(String taskQuery) throws Exception {
        WBS_ENOVIA_FROM_LN_LOGGER.info("Task Query : " + taskQuery);
        WBS_ENOVIA_FROM_LN_LOGGER.info("Getting task data from service");
        TaskDataBean taskData;
        try {
            taskData = getDataFromService(PropertyReader.getProperty("project.space.ln.wbs.query.url"), taskQuery, TaskDataBean.class);
        } catch (Exception exp) {
            WBS_ENOVIA_FROM_LN_LOGGER.error(exp);
            List<TaskBean> tableData = new ArrayList<>();

            TaskTableData taskTableData = getDataFromService(PropertyReader.getProperty("project.space.ln.wbs.query.url"), taskQuery, TaskTableData.class);
            TaskBean taskBean = taskTableData.getTableData();
            tableData.add(taskBean);

            taskData = new TaskDataBean();
            taskData.setTableData(tableData);
        }
        return taskData;
    }
    
    private List<TaskBean> filterOutTaskWithMissingType(List<TaskBean> taskAndMileStoneData) {
        Iterator<TaskBean> iterator = taskAndMileStoneData.iterator();
        
        while(iterator.hasNext()) {
            TaskBean task = iterator.next();
            if(NullOrEmptyChecker.isNullOrEmpty(task.getActivityType())) {
                iterator.remove();
            }
        }
        return taskAndMileStoneData;
    }

    private List<TaskBean> sortTasksAndMileStonesData(List<TaskBean> taskAndMileStoneData) {
        JSON json = new JSON(Boolean.FALSE);

        int totalTasks = taskAndMileStoneData.size();
        WBS_ENOVIA_FROM_LN_LOGGER.debug("Tasks before sorting " + json.serialize(taskAndMileStoneData));
        WBS_ENOVIA_FROM_LN_LOGGER.debug("Number of Tasks before sorting : " + totalTasks);

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

        WBS_ENOVIA_FROM_LN_LOGGER.debug("Parent Child Relationship " + json.serialize(parentChildRelationShip));

        /**
         * Starting another new process
         */
        List<TaskBean> rootTasks = parentChildRelationShip.get("root");
        List<TaskBean> sortedTasks = new ArrayList<>();
        HashMap<String, String> traversed = new HashMap<>();

        if(!NullOrEmptyChecker.isNullOrEmpty(rootTasks)) {
            rootTasks.forEach((TaskBean task) -> {
                String taskName = task.getActivity();
                sortedTasks.add(task);
                WBS_ENOVIA_FROM_LN_LOGGER.debug("Searching child tasks for : '" + taskName + "'");

                sortTheTaskList(parentChildRelationShip, sortedTasks, taskName, traversed);
            });
            WBS_ENOVIA_FROM_LN_LOGGER.debug("Sorted Tasks : " + json.serialize(sortedTasks));

            int sortedTotalTasks = sortedTasks.size();
            int missingTasksAmount = totalTasks - sortedTotalTasks;
            WBS_ENOVIA_FROM_LN_LOGGER.debug("Missed after sorting : " + missingTasksAmount);

            showTheMissingTaskList(missingTasksAmount, taskAndMileStoneData, sortedTasks);
        }
        return sortedTasks;
    }

    private void sortTheTaskList(HashMap<String, List<TaskBean>> parentChildRelationShip, List<TaskBean> taskList, String taskName, HashMap<String, String> traversed) {

        if (traversed.containsKey(taskName)) {
            WBS_ENOVIA_FROM_LN_LOGGER.debug("Task : '" + taskName + "' is a duplicate task in the task list ");
            return;
        }
        traversed.put(taskName, taskName);

        List<TaskBean> childTaskList = parentChildRelationShip.get(taskName);

        if (NullOrEmptyChecker.isNullOrEmpty(childTaskList)) {
            WBS_ENOVIA_FROM_LN_LOGGER.debug("Task '" + taskName + "' has no children");
            return;
        }

        WBS_ENOVIA_FROM_LN_LOGGER.warn("Searching child tasks for : '" + taskName + "'");
        childTaskList.forEach((TaskBean task) -> {
            taskList.add(task);

            String childTaskName = task.getActivity();
            sortTheTaskList(parentChildRelationShip, taskList, childTaskName, traversed);
        });
    }

    private void showTheMissingTaskList(int missingTasksAmount, List<TaskBean> taskAndMileStoneData, List<TaskBean> sortedTasks) {
        WBS_ENOVIA_FROM_LN_LOGGER.info("##################################################");
        WBS_ENOVIA_FROM_LN_LOGGER.info("# Missed tasks and milestones list after sorting #");
        WBS_ENOVIA_FROM_LN_LOGGER.info("##################################################");
        if (missingTasksAmount > 0) {
            List<TaskBean> originalList = new ArrayList<>(taskAndMileStoneData);
            List<TaskBean> sortedList = new ArrayList<>(sortedTasks);
            originalList.removeAll(sortedList);

            originalList.forEach(action -> {
                WBS_ENOVIA_FROM_LN_LOGGER.warn("Task name : '" + action.getActivity() + "' parent name '" + action.getParentActivity() + "'");
            });

        } else if (missingTasksAmount < 0) {
            List<TaskBean> originalList = new ArrayList<>(taskAndMileStoneData);
            List<TaskBean> sortedList = new ArrayList<>(sortedTasks);
            sortedList.removeAll(originalList);

            sortedList.forEach(action -> {
                WBS_ENOVIA_FROM_LN_LOGGER.warn("Task name : '" + action.getActivity() + "' parent name '" + action.getParentActivity() + "'");
            });
        }

        WBS_ENOVIA_FROM_LN_LOGGER.info("##################################################");
        WBS_ENOVIA_FROM_LN_LOGGER.info("# Missed tasks and milestones list after sorting #");
        WBS_ENOVIA_FROM_LN_LOGGER.info("##################################################");
    }
}