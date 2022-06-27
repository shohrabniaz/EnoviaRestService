package com.bjit.common.rest.app.service.comosData.xmlPreparation.projectStructureSearch;

import com.bjit.common.rest.app.service.comosData.xmlPreparation.model.projectStructure.search.Project;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.model.projectStructure.search.Task;
import com.bjit.common.rest.app.service.utilities.NullOrEmptyChecker;
import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.util.MqlUtil;
import matrix.db.*;
import matrix.util.MatrixException;
import matrix.util.SelectList;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.regex.Pattern;

@Component
public class ComosStructureSearch extends AStructureSearch {

    private static final org.apache.log4j.Logger LOGGER = org.apache.log4j.Logger.getLogger(ComosStructureSearch.class);
    private static final String ATTRIBUTE_TITLE = "attribute[Title]";
    private static final String PRJ_ACTIVITY_TYPE = "attribute[PRJ_Activity_Type]";
    private static final String SUBTASK = "Subtask";
    private static final String TASK_MILESTONE = "Task,Milestone";
    private static final int LEVEL = 99;
    private static final String NAME = "name";

    @Override
    public String getObjectId(Context context, String compassId) throws MatrixException {
        HashMap<String, String> object = new HashMap<>();
        StringBuilder queryBuilder = new StringBuilder();
        String type = "Project Space";
        String name = "*";
        String revision = "*";
        queryBuilder.append("temp query bus ")
                .append(" '").append(type).append("' ")
                .append(" '").append(name).append("' ")
                .append(" '").append(revision).append("' ")
                .append("where \"(attribute[PRJ_CompassID].value==").append(compassId).append(")")
                .append("\"")
                .append(" select id dump |");
        String mqlQuery = queryBuilder.toString();
        String mqlResult = MqlUtil.mqlCommand(context, mqlQuery);
        if (!NullOrEmptyChecker.isNullOrEmpty(mqlResult)) {
            if (mqlResult.contains(type)) {
                String[] splitRows = mqlResult.split("\n");
                String singleRow = splitRows[splitRows.length - 1];
                String[] splitResult = singleRow.split(Pattern.quote("|"), -1);
                object.put("id", splitResult[3]);
            }
        }
        return object.get("id");
    }

    @Override
    public Project getStructure(Context context, String objectId) {
        Project project = new Project();
        List<Task> listBusObj = new ArrayList<>();
        Map<Short, Task> results = new HashMap<>();
        try {
            SelectList selectBusStmts = new SelectList();
            selectBusStmts.add(DomainConstants.SELECT_TYPE);
            selectBusStmts.add(DomainConstants.SELECT_NAME);
            selectBusStmts.add(DomainConstants.SELECT_REVISION);
            selectBusStmts.add(ATTRIBUTE_TITLE);
            selectBusStmts.add(PRJ_ACTIVITY_TYPE);

            SelectList selectBusRelStmts = new SelectList();

            BusinessObject busObject = new BusinessObject(objectId);
            BusinessObjectWithSelect objectWithSelect = busObject.select(context, selectBusStmts);

            //Setting project object
            project.setName(objectWithSelect.getSelectData(NAME));
            project.setDescription(objectWithSelect.getSelectData(ATTRIBUTE_TITLE));

            //Expanding relationship of project object
            ExpansionWithSelect expandSelect = busObject.expandSelect(context, SUBTASK, TASK_MILESTONE, selectBusStmts, selectBusRelStmts, false, true, (short) LEVEL);
            RelationshipWithSelectList list = expandSelect.getRelationships();

            //Removing project object
            RelationshipWithSelectItr relItr = new RelationshipWithSelectItr(expandSelect.getRelationships());

            //Getting list of activities
            gettingActivityList(listBusObj, list, relItr);

            //Setting parent object
            results.put((short) 0, listBusObj.get(0));

            //Setting child activity to parent
            settingChildActivity(listBusObj, results);

            //Setting list of activity to project
            project.setActivityList(listBusObj);
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.error(e.getMessage());
        }
        return project;
    }

    //Getting list of activities
    private void gettingActivityList(List<Task> listBusObj, RelationshipWithSelectList list, RelationshipWithSelectItr relItr) {
        list.forEach(e -> {
            relItr.next();
            RelationshipWithSelect rel = relItr.obj();
            BusinessObjectWithSelect bus = rel.getTarget();
            Task task = new Task();
            task.setActivity(bus.getSelectData(NAME));
            task.setLevel(rel.getLevel());
            task.setDescription(bus.getSelectData(ATTRIBUTE_TITLE));
            task.setType(bus.getSelectData(PRJ_ACTIVITY_TYPE));
            listBusObj.add(task);
        });
    }

    private void settingChildActivity(List<Task> listBusObj, Map<Short, Task> results) {
        for (Task child : listBusObj) {
            short val = (short) (child.getLevel() - 1);
            Task parentObject = results.get(val);
            if (val == 0)
                child.setParentActivity(null);
            else
                child.setParentActivity(parentObject.getActivity());
            results.put((short) (child.getLevel() - 1), parentObject);
            results.put(child.getLevel(), child);
        }
    }
}
