package com.bjit.common.rest.app.service.search.drawing;

import com.bjit.common.rest.app.service.model.drawing.response.*;
import com.bjit.mapper.mapproject.jsonOutput.Items;
import com.matrixone.apps.domain.DomainConstants;
import matrix.db.*;
import matrix.util.SelectList;
import matrix.util.StringList;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author BJIT
 */
@Service
public class DrawingDataServiceImpl implements DrawingDataService {

    private static final org.apache.log4j.Logger LOGGER = org.apache.log4j.Logger.getLogger(DrawingDataServiceImpl.class);
    private static final String OBJECT_ID = "objectid";
    private static final String BUS_OBJECT_TYPE = "type";
    private static final String BUS_OBJECT_NAME = "name";
    private static final String BUS_OBJECT_REVISION = "revision";
    private static final String REL_TYPE_TASK = "Task Deliverable";
    private static final String TYPE_TASK = "Task";
    private static final String ATTRIBUTE_TITLE = "attribute[Title]";
    private static final String ATTRIBUTE_PRJ_COMPASS_ID = "attribute[PRJ_CompassID]";
    private static final String ATTRIBUTE_PRJ_COMOS_ACTIVITY_UID = "attribute[PRJ_ComosActivityUID]";
    private static final String ATTRIBUTE_PRJ_COMOS_PROJECT_UID = "attribute[PRJ_ComosProjectUID]";
    private static final String ATTRIBUTE_PRJ_MILL_ID = "attribute[PRJ_MillID]";
    private static final String ATTRIBUTE_PRJ_EQUIPMENT_ID = "attribute[PRJ_EquipmentID]";
    private static final String REL_TYPE_PROJECT = "Subtask";
    private static final String TYPE_PROJECT = "Project Space,Task";
    private static final int LEVEL_PROJECT = 99;
    private static final short LEVEL_TASK = 1;
    StringList rSelects = new StringList();

    @Override
    public Items getItemByObjectId(Context context, String objectId) {
        List<Map<String, String>> mapList = new ArrayList<>();
        Map<String, String> stringMap = new HashMap<>();
        Items items = new Items();
        try {
            SelectList selectBusStmts = new SelectList();
            selectBusStmts.add(BUS_OBJECT_TYPE);
            selectBusStmts.add(BUS_OBJECT_NAME);
            selectBusStmts.add(BUS_OBJECT_REVISION);
            BusinessObject busObject = new BusinessObject(objectId);
            BusinessObjectWithSelect objectWithSelect = busObject.select(context, selectBusStmts);
            String type = objectWithSelect.getTypeName(context);
            String name = objectWithSelect.getName(context);
            String revision = objectWithSelect.getRevision();
            stringMap.put(BUS_OBJECT_TYPE, type);
            stringMap.put(BUS_OBJECT_NAME, name);
            stringMap.put(BUS_OBJECT_REVISION, revision);
            stringMap.put(OBJECT_ID, objectId);
            mapList.add(0, stringMap);
            items.setItems(mapList);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
        return items;
    }

    @Override
    public List<Info> getProjectAndTaskInfo(Context context, String itemId) {
        List<Info> infoList = new ArrayList<>();
        try {
            BusinessObject rootObj = new BusinessObject(itemId);
            StringList oSelects = new StringList();

            //Expanding task item
            ExpansionWithSelect expandSelectTask = rootObj.expandSelect(context, REL_TYPE_TASK, TYPE_TASK, geToSelectsForTask(oSelects), rSelects, true, false, LEVEL_TASK);
            RelationshipWithSelectList listTask = expandSelectTask.getRelationships();
            RelationshipWithSelectItr relItrTask = new RelationshipWithSelectItr(expandSelectTask.getRelationships());
            //Setting task info
            settingTaskInfo(context, infoList, listTask, relItrTask);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
        return infoList;
    }

    //Setting task info
    private void settingTaskInfo(Context context, List<Info> infoList, RelationshipWithSelectList listTask, RelationshipWithSelectItr relItrTask) {
        for (int i = 0; i < listTask.size(); i++) {
            relItrTask.next();
            RelationshipWithSelect relTask = relItrTask.obj();
            BusinessObjectWithSelect busTask = relTask.getTarget();
            Info info = new Info();
            Task task = new Task();
            task.setType(busTask.getSelectData(DomainConstants.SELECT_TYPE));
            task.setName(busTask.getSelectData(DomainConstants.SELECT_NAME));
            task.setRevision(busTask.getSelectData(DomainConstants.SELECT_REVISION));
            task.setDescription(busTask.getSelectData(DomainConstants.SELECT_DESCRIPTION));
//            task.setId(busTask.getSelectData(ATTRIBUTE_PRJ_COMOS_ACTIVITY_UID));
            task.setTitle(busTask.getSelectData(ATTRIBUTE_TITLE));
            info.setTask(task);
            //Process to get project info
            processProjectInfo(context, busTask, info);
            infoList.add(info);
        }
    }

    //Process to get project info
    private void processProjectInfo(Context context, BusinessObject busTask, Info info) {
        try {
            if (busTask.getObjectId() != null) {
                BusinessObject projectObj = new BusinessObject(busTask.getObjectId());
                StringList oSelects = new StringList();

                //Expanding project item
                ExpansionWithSelect expandSelectProject = projectObj.expandSelect(context, REL_TYPE_PROJECT, TYPE_PROJECT, geToSelectsForProject(oSelects), rSelects, true, false, (short) LEVEL_PROJECT);
                RelationshipWithSelectList listProject = expandSelectProject.getRelationships();
                RelationshipWithSelectItr relItrProject = new RelationshipWithSelectItr(expandSelectProject.getRelationships());
                //Set project info
                for (int j = 0; j < listProject.size(); j++) {
                    relItrProject.next();
                    RelationshipWithSelect rel = relItrProject.obj();
                    BusinessObjectWithSelect bus = rel.getTarget();
                    Project project = new Project();
                    project.setType(bus.getSelectData(DomainConstants.SELECT_TYPE));
                    project.setName(bus.getSelectData(DomainConstants.SELECT_NAME));
                    project.setRevision(bus.getSelectData(DomainConstants.SELECT_REVISION));
                    project.setDescription(bus.getSelectData(DomainConstants.SELECT_DESCRIPTION));
                    project.setMillId(bus.getSelectData(ATTRIBUTE_PRJ_MILL_ID));
                    project.setEquipmentId(bus.getSelectData(ATTRIBUTE_PRJ_EQUIPMENT_ID));
                    project.setCompassId(bus.getSelectData(ATTRIBUTE_PRJ_COMPASS_ID));
                    project.setTitle(bus.getSelectData(ATTRIBUTE_TITLE));
                    info.setProject(project);
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    //Getting all item
    public void getItemList(Context context, List<ItemData> data, Items items) {
        ItemData itemData = new ItemData();
        for (Map<String, String> itm : items.getItems()) {
            //Set Item Info
            Item item = new Item();
            item.setType(itm.get(DomainConstants.SELECT_TYPE));
            item.setName(itm.get(DomainConstants.SELECT_NAME));
            item.setRevision(itm.get(DomainConstants.SELECT_REVISION));

            //Get project anf task Info
            List<Info> infoList = getProjectAndTaskInfo(context, itm.get(OBJECT_ID));

            //Set ItemData
            itemData.setItem(item);
            itemData.setInfo(infoList);
            data.add(itemData);
        }
    }

    private void commonOSelects(StringList oSelects) {
        oSelects.add(DomainConstants.SELECT_ID);
        oSelects.add(DomainConstants.SELECT_TYPE);
        oSelects.add(DomainConstants.SELECT_NAME);
        oSelects.add(DomainConstants.SELECT_REVISION);
        oSelects.add(DomainConstants.SELECT_DESCRIPTION);
        oSelects.add(ATTRIBUTE_TITLE);
    }

    private StringList geToSelectsForTask(StringList oSelects) {
        commonOSelects(oSelects);
        oSelects.add(ATTRIBUTE_PRJ_COMOS_ACTIVITY_UID);
        return oSelects;
    }

    private StringList geToSelectsForProject(StringList oSelects) {
        commonOSelects(oSelects);
        oSelects.add(ATTRIBUTE_PRJ_COMPASS_ID);
        oSelects.add(ATTRIBUTE_PRJ_COMOS_PROJECT_UID);
        oSelects.add(ATTRIBUTE_PRJ_MILL_ID);
        oSelects.add(ATTRIBUTE_PRJ_EQUIPMENT_ID);
        return oSelects;
    }

}
