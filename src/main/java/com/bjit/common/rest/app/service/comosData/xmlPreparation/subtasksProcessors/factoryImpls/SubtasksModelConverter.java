package com.bjit.common.rest.app.service.comosData.xmlPreparation.subtasksProcessors.factoryImpls;

import com.bjit.common.rest.app.service.comosData.project_structure.model.ComosProjectSpaceBean;
import com.bjit.common.rest.app.service.comosData.project_structure.model.ComosTaskBean;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.model.SessionModel;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.model.subtasks.SubtasksServiceResponse;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.model.subtasks.SubtasksStatusGroups;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.model.subtasks.SubtasksStatusList;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.model.subtasks.Subtasksdeliverable;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.subtasksProcessors.factoryServices.IModelConverterAdapter;
import com.bjit.common.rest.app.service.model.tnr.TNR;
import com.bjit.common.rest.app.service.utilities.CommonSearch;
import com.bjit.ex.integration.transfer.util.NullOrEmptyChecker;
import lombok.extern.log4j.Log4j;
import matrix.db.*;
import matrix.util.MatrixException;
import matrix.util.StringList;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.*;


@Component
@Qualifier("SubtasksModelConverter")
@Log4j
public class SubtasksModelConverter implements IModelConverterAdapter<SubtasksServiceResponse, ComosProjectSpaceBean> {

    @Autowired
    BeanFactory beanFactory;

    @Autowired
    ComosProjectSpaceBean comosProjectSpaceBean;

    @Autowired
    @Qualifier("CommonSearch")
    CommonSearch commonSearch;

    @Autowired
    SessionModel sessionModel;

    HashMap<String, String> projectSpaceAttributes = new HashMap<>();
    int iterator_index = 0;

    public ComosProjectSpaceBean convert(SubtasksServiceResponse comosSubtasksServiceResponse) {
        List<ComosTaskBean> taskList = new ArrayList<>();
        List<Subtasksdeliverable> subtasksDeliverables = comosSubtasksServiceResponse.getData().getDeliverables();
        Optional
                .ofNullable(subtasksDeliverables)
                .orElse(new ArrayList<>())
                .forEach((Subtasksdeliverable deliverable) -> {
                    List<SubtasksStatusGroups> statusGroups = deliverable.getStatusGroups();
                    final String deliverableUId = deliverable.getDeliverableUId();
                    log.info("deliverableUId: " + deliverableUId);

                    String taskObjectId = "";
                    try {
                        taskObjectId = searchByObjectId(deliverableUId);
                    } catch (Exception e) {
                        log.error(e);
                    }
                    log.info("ObjectId of " + deliverableUId + ": " + taskObjectId);
                    if (taskObjectId != "") {
                        try {

                            projectSpaceAttributes = expandProjectSpace(taskObjectId);

                        } catch (Exception e) {
                            log.error(e);
                        }

                        HashMap<String, String> finalProjectSpaceAttributes = projectSpaceAttributes;
                        String finalTaskObjectId = taskObjectId;
                        Optional
                                .ofNullable(statusGroups)
                                .orElse(new ArrayList<>())
                                .forEach((SubtasksStatusGroups statusGroup) -> {
                                    String projectSpaceName = finalProjectSpaceAttributes.get("ProjectSpaceName");
                                    getTaskList(statusGroup, taskList, projectSpaceName);
                                    List<HashMap<String, String>> allTaskFound = new ArrayList<>();
                                    try {
                                        allTaskFound = expandTask(finalTaskObjectId);
                                    } catch (Exception e) {
                                        log.error(e);
                                    }

                                    deliverable.setDeliverableName(allTaskFound.get(0).get("taskActivity"));
                                    makeStatusGroupTask(taskList, statusGroup, deliverable, projectSpaceName);
                                    addTaskToList(taskList, allTaskFound, projectSpaceName);


                                });
                    } else {

                        log.info("Task not found as Multiple object Id detected");
                    }
                });
        if (taskList.isEmpty()==false) {
            comosProjectSpaceBean = getProjectSpace(comosProjectSpaceBean, projectSpaceAttributes);
            comosProjectSpaceBean.setTableData(taskList);
        }
        return comosProjectSpaceBean;
    }


    public String getAttributeValue(BusinessObject parentItem, Context context, String attributename) throws MatrixException {
        Attribute deliverableTaskActivityType = parentItem.getAttributeValues(context, attributename);
        String attributeValue = deliverableTaskActivityType.getValue();
        return attributeValue;
    }

    public HashMap<String, String> makeSingleTaskAttributemMap(String taskActivity, String taskActivityType, String taskActivityTitle, String taskComosActivityUID, String taskActivityStatus) {
        HashMap<String, String> singleTaskAttributes = new HashMap<>();
        singleTaskAttributes.put("taskActivity", taskActivity);
        singleTaskAttributes.put("taskActivityType", taskActivityType);
        singleTaskAttributes.put("taskErpType", taskActivityType);
        singleTaskAttributes.put("taskActivityTitle", taskActivityTitle);
        singleTaskAttributes.put("taskComosActivityUID", taskComosActivityUID);
        singleTaskAttributes.put("taskActivityStatus", taskActivityStatus);
        return singleTaskAttributes;
    }

    public HashMap<String, String> getTaskAttributeMap(BusinessObject parentItem, Context context) throws MatrixException {
        parentItem.open(context);
//        HashMap<String, String> singleTaskAttributes = new HashMap<>();

        String taskActivity = parentItem.getName();
        String taskActivityStatus = getCurrentState(context, parentItem);
        String taskActivityType = getAttributeValue(parentItem, context, "PRJ_Activity_Type");
        String taskComosActivityUID = getAttributeValue(parentItem, context, "PRJ_ComosActivityUID");
        String taskActivityTitle = getAttributeValue(parentItem, context, "Title");
        HashMap<String, String> singleTaskAttributes = makeSingleTaskAttributemMap(taskActivity, taskActivityType, taskActivityTitle, taskComosActivityUID, taskActivityStatus);

//        singleTaskAttributes.put("taskActivity", taskActivity);
//        singleTaskAttributes.put("taskActivityType", taskActivityType);
//        singleTaskAttributes.put("taskErpType", taskActivityType);
//        singleTaskAttributes.put("taskActivityTitle", taskActivityTitle);
//        singleTaskAttributes.put("taskComosActivityUID", taskComosActivityUID);
//        singleTaskAttributes.put("taskActivityStatus", taskActivityStatus);
        return singleTaskAttributes;

    }

    public ExpansionWithSelect getExpandQueryResult(String rootId, Context context, String expansionTypes, String expantionEnds) throws MatrixException {
        BusinessObject deliverableTask = new BusinessObject(rootId);
        StringList objSelect = getObjetAttributeList();
        StringList relSelect = getRelationAttributeList();
        deliverableTask.open(context);

        ExpansionWithSelect expandSelect = deliverableTask
                .expandSelect(context,
                        expansionTypes,
                        expantionEnds,
                        objSelect, relSelect, true, false, (short) 99);

        return expandSelect;
    }

    public List<HashMap<String, String>> expandTask(String rootId) throws Exception {
        List<HashMap<String, String>> allTaskFromDeliverable = new ArrayList<>();

        Context context = sessionModel.getContext();

        ExpansionWithSelect expandSelect = getExpandQueryResult(rootId, context, "Subtask,Task", "Task");

        HashMap<String, String> rootTaskAttributes = new HashMap<>();
        rootTaskAttributes = getRootItemInfo(expandSelect.getRootWithSelect());
        allTaskFromDeliverable.add(rootTaskAttributes);

        RelationshipWithSelectList relationships = expandSelect.getRelationships();

        relationships.getIterator().forEach((RelationshipWithSelect relationshipWithSelect) -> {
            try {
                relationshipWithSelect.open(context);
                BusinessObject parentItem = relationshipWithSelect.getFrom();

                HashMap<String, String> singleTaskAttributes = getTaskAttributeMap(parentItem, context);
                allTaskFromDeliverable.add(singleTaskAttributes);
                relationshipWithSelect.close(context);

            } catch (MatrixException ex) {
                log.error(ex);
            }
        });
        log.info("===================================================");
        log.info(allTaskFromDeliverable);
        log.info("===================================================");
        return allTaskFromDeliverable;

    }

    public String getCurrentState(Context context, BusinessObject businessObject) throws MatrixException {
        String StateName = "";
        for (State state : businessObject.getStates(context)) {
            if (state.isCurrent()) {
                StateName = state.getName();
                break;
            }
//            log.info("State name =====>" + state.getName());
        }
        return StateName;
    }


    private HashMap<String, String> getRootItemInfo(BusinessObjectWithSelect rootWithSelect) {

//        HashMap<String, String> singleTaskAttributes = new HashMap<>();

        String rootTaskActivity = rootWithSelect.getSelectData("name");
        String rootTaskActivityType = rootWithSelect.getSelectData("attribute[PRJ_Activity_Type]");
        String rootTaskActivityTitle = rootWithSelect.getSelectData("attribute[Title]");
        String rootTaskComosActivityUID = rootWithSelect.getSelectData("attribute[PRJ_ComosActivityUID]");
        String rootTaskActivityStatus = rootWithSelect.getSelectData("current");

        HashMap<String, String> singleTaskAttributes = makeSingleTaskAttributemMap(rootTaskActivity, rootTaskActivityType, rootTaskActivityTitle, rootTaskComosActivityUID, rootTaskActivityStatus);
//        singleTaskAttributes.put("taskActivityStatus", taskActivityStatus);
//        singleTaskAttributes.put("taskActivity", rootTaskActivity);
//        singleTaskAttributes.put("taskErpType", taskActivityType);
//        singleTaskAttributes.put("taskActivityType", taskActivityType);
//        singleTaskAttributes.put("taskActivityTitle", taskActivityTitle);
//        singleTaskAttributes.put("taskComosActivityUID", taskComosActivityUID);

        return singleTaskAttributes;

    }

    public HashMap<String, String> expandProjectSpace(String rootId) throws Exception {
        HashMap<String, String> projectSpaceAttributes = new HashMap<>();
        try {

            Context context = sessionModel.getContext();
            ExpansionWithSelect expandSelect = getExpandQueryResult(rootId, context, "Subtask", "Task,Project Space");

            RelationshipWithSelectList relationships = expandSelect.getRelationships();

            relationships.getIterator().forEach((RelationshipWithSelect relationshipWithSelect) -> {
                try {
                    relationshipWithSelect.open(context);
                    BusinessObject parentItem = relationshipWithSelect.getFrom();
                    parentItem.open(context);

                    if (parentItem.getTypeName().contains("Project Space")) {
                        String deliverableTaskProjectSpace = parentItem.getName();
                        String projectSpace_ERPSubProject = getAttributeValue(parentItem, context, "PRJ_ERPSubProject");
                        String projectSpace_Title = getAttributeValue(parentItem, context, "Title");
                        String projectSpace_CompasId = getAttributeValue(parentItem, context, "PRJ_CompassID");
                        String projectSpace_ComosProjectUID = getAttributeValue(parentItem, context, "PRJ_ComosProjectUID");

                        projectSpaceAttributes.put("ProjectSpaceName", deliverableTaskProjectSpace);
                        projectSpaceAttributes.put("projectSpace_ERPSubProject", projectSpace_ERPSubProject);
                        projectSpaceAttributes.put("projectSpace_Title", projectSpace_Title);
                        projectSpaceAttributes.put("projectSpace_CompasId", projectSpace_CompasId);
                        projectSpaceAttributes.put("projectSpace_ComosProjectUID", projectSpace_ComosProjectUID);
                        log.info("==========================================projectSpace_CompasId: "+projectSpace_CompasId);
                        relationshipWithSelect.close(context);
                    }

                } catch (MatrixException ex) {
                    log.error(ex);
                }
            });

        } catch (Exception e) {
            log.error(e);
        }

        return projectSpaceAttributes;

    }

    private StringList getRelationAttributeList() {
        StringList relSelect = new StringList();
        relSelect.add("name");
        return relSelect;
    }

    private StringList getObjetAttributeList() {
        StringList objSelect = new StringList();
        objSelect.add("type");
        objSelect.add("name");
        objSelect.add("revision");
        objSelect.add("id");
        objSelect.add("attribute[PRJ_ComosActivityUID]");
        objSelect.add("attribute[Title]");
        objSelect.add("current");
        objSelect.add(" attribute[Status]");
        objSelect.add("attribute[PRJ_Activity_Type]");
        return objSelect;
    }

    public String searchByObjectId(String deliverableUId) throws Exception {
        String ObjectId = "";
        TNR tnr = beanFactory.getBean(TNR.class);
        tnr.setType("Task");
        HashMap<String, String> attribute = new HashMap<>();
        attribute.put("attribute[PRJ_ComosActivityUID].value", deliverableUId);
        List<HashMap<String, String>> AttributeMapList = new ArrayList<>();
        try {
            Context context = sessionModel.getContext();
            //temp query bus Task * * where "'attribute[PRJ_ComosActivityUID].value'=='A4EXWNOASM'" select id
            AttributeMapList = commonSearch.searchItem(context, tnr, attribute);

            if (AttributeMapList.size() == 1 && !NullOrEmptyChecker.isNullOrEmpty(AttributeMapList.get(0).get("id"))) {
                ObjectId = AttributeMapList.get(0).get("id");

            } else {
                log.error("==================Multiple Object Id Found with this DeliverableUId=====================");
                throw new Exception();
            }

        } catch (Exception e) {
            log.error(e);
        }
        return ObjectId;
    }


    public void makeStatusGroupTask(List<ComosTaskBean> taskList, SubtasksStatusGroups statusGroup, Subtasksdeliverable deliverable, String projectSpaceName) {
        /**
         * Bean preparation should b    e in a common place
         */
        ComosTaskBean comosTaskBean = beanFactory.getBean(ComosTaskBean.class);
        comosTaskBean.setActivity(statusGroup.getName());
        comosTaskBean.setParentActivity(deliverable.getDeliverableName());
        comosTaskBean.setActivityTitle(statusGroup.getDescription());
        comosTaskBean.setComosActivityUID(statusGroup.getId());
        comosTaskBean.setActivityType("Deliverable");
        comosTaskBean.setErpType("Deliverable");
        comosTaskBean.setActivityStatus("Released");
        comosTaskBean.setProject(projectSpaceName);
        comosTaskBean.setParentComosActivityUID(deliverable.getDeliverableUId());
        taskList.add(comosTaskBean);
    }

    public void addTaskToList(List<ComosTaskBean> taskList, List<HashMap<String, String>> allTaskFound, String projectSpaceName) {
        ListIterator<HashMap<String, String>> iterator = allTaskFound.listIterator();

        allTaskFound.forEach(
                (singleTaskAttribute)
                        -> {
                    log.info(singleTaskAttribute);

                    ComosTaskBean comosTaskBean = beanFactory.getBean(ComosTaskBean.class);
                    comosTaskBean.setActivity(singleTaskAttribute.get("taskActivity"));
                    if (iterator_index != allTaskFound.size() - 1) {
                        iterator.next();
//                        comosTaskBean.setParentActivity(allTaskFound.get(iterator_index).get("taskActivity"));
                        HashMap<String, String> parentDataMap = allTaskFound.get(iterator.nextIndex());
                        comosTaskBean.setParentActivity(parentDataMap.get("taskActivity"));
                        comosTaskBean.setParentComosActivityUID(parentDataMap.get("taskComosActivityUID"));
                        iterator_index = iterator.nextIndex();
//                        iterator_index = iterator_index + 1;
                    }
//
                    /**
                     * Variable name shouldn't be 'taskallAttributeNeeded'
                     * Please update the name
                     *
                     * Bean preparation should be in a common place
                     */
                    comosTaskBean.setActivityTitle(singleTaskAttribute.get("taskActivityTitle"));
                    comosTaskBean.setComosActivityUID(singleTaskAttribute.get("taskComosActivityUID"));
                    comosTaskBean.setActivityType(singleTaskAttribute.get("taskActivityType"));
                    comosTaskBean.setErpType(singleTaskAttribute.get("taskActivityType"));
                    comosTaskBean.setActivityStatus(singleTaskAttribute.get("taskActivityStatus"));
                    comosTaskBean.setProject(projectSpaceName);
                    taskList.add(comosTaskBean);

                });

        iterator_index = 0;

    }

    private ComosProjectSpaceBean getProjectSpace(ComosProjectSpaceBean comosProjectSpaceBean, HashMap<String, String> projectSpaceAttributes) {
        comosProjectSpaceBean.setProjectCode(projectSpaceAttributes.get("ProjectSpaceName"));
        comosProjectSpaceBean.setTitle(projectSpaceAttributes.get("projectSpace_Title"));
        comosProjectSpaceBean.setErpSubProject(projectSpaceAttributes.get("projectSpace_ERPSubProject"));
        comosProjectSpaceBean.setComosProjectUID(projectSpaceAttributes.get("projectSpace_ComosProjectUID"));
        comosProjectSpaceBean.setCompassId(projectSpaceAttributes.get("projectSpace_CompasId"));

        return comosProjectSpaceBean;
    }

    private List<ComosTaskBean> getTaskList(SubtasksStatusGroups statusGroup, List<ComosTaskBean> taskBeanList, String projectSpaceName) {
        Optional
                .ofNullable(statusGroup.getStatusList())
                .orElse(new ArrayList<>())
                .forEach((SubtasksStatusList statusList) -> {
                    ComosTaskBean comosTaskBean = beanFactory.getBean(ComosTaskBean.class);
                    comosTaskBean.setActivity(statusList.getName());
                    comosTaskBean.setParentActivity(statusGroup.getName());
                    comosTaskBean.setProject(projectSpaceName);
                    comosTaskBean.setActivityTitle(statusList.getDescription());
                    comosTaskBean.setComosActivityUID(statusList.getId());
                    comosTaskBean.setActivityType("Activity");
                    comosTaskBean.setParentComosActivityUID(statusGroup.getId());


//                        if (statusList.getCompleted() == "1") {
//                            comosTaskBean.setActivityStatus("Released");
//                        } else {
//                            comosTaskBean.setActivityStatus("Released");
//                        }
                    comosTaskBean.setActivityStatus("Released");
                    comosTaskBean.setErpType("Activity");
                    
                    comosTaskBean.setPlannedStartDate(statusList.getPlannedStartDate());
                    comosTaskBean.setPlannedEndDate(statusList.getPlannedEndDate());
                    comosTaskBean.setBaseLineStartDate(statusList.getBaselineStartDate());
                    comosTaskBean.setBaseLineEndDate(statusList.getBaselineEndDate());
                    comosTaskBean.setActualStartDate(statusList.getActualStartDate());
                    comosTaskBean.setActualEndDate(statusList.getActualEndDate());

                    taskBeanList.add(comosTaskBean);
                });

        return taskBeanList;
    }

}
