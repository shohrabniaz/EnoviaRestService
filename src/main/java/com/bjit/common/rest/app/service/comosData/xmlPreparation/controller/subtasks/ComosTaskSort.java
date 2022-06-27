package com.bjit.common.rest.app.service.comosData.xmlPreparation.controller.subtasks;

import com.bjit.common.rest.app.service.aspects.annotations.LogExecutionTime;
import com.bjit.common.rest.app.service.comosData.project_structure.model.ComosTaskBean;
import com.bjit.common.rest.app.service.utilities.JSON;
import com.bjit.common.rest.app.service.utilities.NullOrEmptyChecker;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Log4j
@Service
@Qualifier("ComosTaskSort")
public class ComosTaskSort implements ITaskSort {
    @LogExecutionTime
    @Override
    public List<ComosTaskBean> sortTasksAndMileStonesData(List<ComosTaskBean> taskAndMileStoneData) {
        JSON json = new JSON(Boolean.FALSE);

        int totalTasks = taskAndMileStoneData.size();
        log.debug("Tasks before sorting " + json.serialize(taskAndMileStoneData));
        log.debug("Number of Tasks before sorting : " + totalTasks);

        HashMap<String, List<ComosTaskBean>> parentChildRelationShip = new HashMap<>();
        HashMap<String, String> childParentTracker = new HashMap<>();
        taskAndMileStoneData.forEach((ComosTaskBean taskOrMileStone) -> {
            String parentActivity = taskOrMileStone.getParentComosActivityUID();
            if (NullOrEmptyChecker.isNullOrEmpty(parentActivity)) {
                parentActivity = "root";
            }

            if (parentChildRelationShip.containsKey(parentActivity)) {
                List<ComosTaskBean> tasksAndMileStonesAsChildren = parentChildRelationShip.get(parentActivity);
                tasksAndMileStonesAsChildren.add(taskOrMileStone);
            } else {
                List<ComosTaskBean> tasksAndMileStonesAsChildren = new ArrayList<>();
                tasksAndMileStonesAsChildren.add(taskOrMileStone);
                parentChildRelationShip.put(parentActivity, tasksAndMileStonesAsChildren);
            }

            childParentTracker.put(taskOrMileStone.getComosActivityUID(), parentActivity);
        });

        log.debug("Parent Child Relationship " + json.serialize(parentChildRelationShip));

        /**
         * Starting another new process
         */
        List<ComosTaskBean> rootTasks = parentChildRelationShip.get("root");
        List<ComosTaskBean> sortedTasks = new ArrayList<>();
        HashMap<String, String> traversed = new HashMap<>();

        rootTasks.forEach((ComosTaskBean task) -> {
            String taskName = task.getComosActivityUID();
            sortedTasks.add(task);
            log.debug("Searching child tasks for : '" + taskName + "'");

//            populateTheTaskList(parentChildRelationShip, sortedTasks, taskName, traversed);
            sortTheTaskList(parentChildRelationShip, sortedTasks, taskName, traversed);
        });
        log.debug("Sorted Tasks : " + json.serialize(sortedTasks));

        int sortedTotalTasks = sortedTasks.size();
        int missingTasksAmount = totalTasks - sortedTotalTasks;
        log.warn("Missed after sorting : " + missingTasksAmount);

        showTheMissingTaskList(missingTasksAmount, taskAndMileStoneData, sortedTasks);

        return sortedTasks;
    }

    private void sortTheTaskList(HashMap<String, List<ComosTaskBean>> parentChildRelationShip, List<ComosTaskBean> taskList, String taskName, HashMap<String, String> traversed) {

        if (traversed.containsKey(taskName)) {
            log.debug("Task : '" + taskName + "' is a duplicate task in the task list ");
            return;
        }
        traversed.put(taskName, taskName);

        List<ComosTaskBean> childTaskList = parentChildRelationShip.get(taskName);

        if (NullOrEmptyChecker.isNullOrEmpty(childTaskList)) {
            log.debug("Task '" + taskName + "' has no children");
            return;
        }

        log.debug("Searching child tasks for : '" + taskName + "'");
        childTaskList.forEach((ComosTaskBean task) -> {
            taskList.add(task);

            String childTaskName = task.getComosActivityUID();
            sortTheTaskList(parentChildRelationShip, taskList, childTaskName, traversed);
        });
    }

    private void showTheMissingTaskList(int missingTasksAmount, List<ComosTaskBean> taskAndMileStoneData, List<ComosTaskBean> sortedTasks) {
        log.debug("##################################################");
        log.debug("# Missed tasks and milestones list after sorting #");
        log.debug("##################################################");
        if (missingTasksAmount > 0) {
            List<ComosTaskBean> originalList = new ArrayList<>(taskAndMileStoneData);
            List<ComosTaskBean> sortedList = new ArrayList<>(sortedTasks);
            originalList.removeAll(sortedList);

            originalList.forEach(action -> {
                log.warn("Task name : '" + action.getActivity() + "' parent name '" + action.getParentActivity() + "'");
            });

        } else if (missingTasksAmount < 0) {
            List<ComosTaskBean> originalList = new ArrayList<>(taskAndMileStoneData);
            List<ComosTaskBean> sortedList = new ArrayList<>(sortedTasks);
            sortedList.removeAll(originalList);

            sortedList.forEach(action -> {
                log.warn("Task name : '" + action.getActivity() + "' parent name '" + action.getParentActivity() + "'");
            });
        }

        log.debug("##################################################");
        log.debug("# Missed tasks and milestones list after sorting #");
        log.debug("##################################################");
    }
}
