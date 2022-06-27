package com.bjit.common.rest.app.service.comosData.xmlPreparation.model.comosEnovia;

import com.bjit.common.rest.app.service.comosData.xmlPreparation.model.SessionModel;
import com.bjit.common.rest.app.service.model.tnr.TNR;
import com.bjit.common.rest.app.service.utilities.CommonSearch;
import com.bjit.ewc18x.utils.PropertyReader;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.RequestScope;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Log4j
@Service
@RequestScope
public class ComosRuntimeDataBuilder {
    @Autowired
    BeanFactory beanFactory;

    @Autowired
    ComosRuntimeData comosRuntimeData;

    @Autowired
    @Qualifier("CommonSearch")
    CommonSearch commonSearch;

    @Autowired
    SessionModel sessionModel;

    @PostConstruct
    public void init() {
        comosRuntimeData.setAssemblyPlantData(beanFactory.getBean(LogicalItem.class));
        comosRuntimeData.setEquipmentPlantData(beanFactory.getBean(LogicalItem.class));

        comosRuntimeData.setProjectSpaceData(beanFactory.getBean(ProjectStructureData.class));
        comosRuntimeData.getProjectSpaceData().setTnr(beanFactory.getBean(TNR.class));

        comosRuntimeData.setDeliverables(new HashMap<>());
        comosRuntimeData.setConnectedDeliverables(new HashMap<>());
    }

    public void build() {
        System.out.println("Building the Comos Runtime data holder");
    }

    /**
     * No Usage found at all place
     *
     * @param deliverableTask
     */
    public void addDeliverableTask(ProjectStructureData deliverableTask) {
        Deliverables deliverables = Optional
//                .ofNullable(comosRuntimeData.getDeliverables().get(deliverableTask.getTnr().getName()))
                .ofNullable(comosRuntimeData.getDeliverables().get(deliverableTask.getDeliverableId()))
                .orElse(getDeliverables());
        deliverables.setDeliverableTask(deliverableTask);
//        comosRuntimeData.getDeliverables().put(deliverableTask.getTnr().getName(), deliverables);
        comosRuntimeData.getDeliverables().put(deliverableTask.getDeliverableId(), deliverables);
    }

    /**
     * No Usage found at all place
     *
     * @param deliverableTask
     */
    public void addConnectedDeliverableTask(ProjectStructureData deliverableTask) {
        Deliverables deliverables = Optional
//                .ofNullable(comosRuntimeData.getConnectedDeliverables().get(deliverableTask.getTnr().getName()))
                .ofNullable(comosRuntimeData.getConnectedDeliverables().get(deliverableTask.getDeliverableId()))
                .orElse(getDeliverables());
        deliverables.setDeliverableTask(deliverableTask);
//        comosRuntimeData.getConnectedDeliverables().put(deliverableTask.getTnr().getName(), deliverables);
        comosRuntimeData.getConnectedDeliverables().put(deliverableTask.getDeliverableId(), deliverables);
    }

    /**
     * No Usage found at all place
     *
     * @param deliverableId
     * @param deliverable
     */
    public void addDeliverable(String deliverableId, LogicalItem deliverable) {
        Deliverables deliverables = comosRuntimeData.getDeliverables().get(deliverableId);
        Optional
                .ofNullable(deliverables)
                .orElse(getDeliverables())
                .getDeliverablesList()
                .add(deliverable);
    }

    /**
     * No Usage found at all place
     *
     * @param deliverableId
     * @param deliverable
     */
    public void addConnectedDeliverable(String deliverableId, LogicalItem deliverable) {
        Deliverables deliverables = comosRuntimeData.getConnectedDeliverables().get(deliverableId);
        Optional
                .ofNullable(deliverables)
                .orElse(getDeliverables())
                .getDeliverablesList()
                .add(deliverable);
    }

    /**
     * No Usage found at all place
     *
     * @param deliverableTask
     * @param deliverable
     */
    public void addDeliverable(ProjectStructureData deliverableTask, LogicalItem deliverable) {
//        Deliverables deliverables = comosRuntimeData.getDeliverables().get(deliverableTask.getTnr().getName());
        Deliverables deliverables = comosRuntimeData.getDeliverables().get(deliverableTask.getDeliverableId());
        Optional.ofNullable(deliverables).ifPresentOrElse((deliverablesItem) ->
                        deliverablesItem.getDeliverablesList().add(deliverable),
                () -> {
                    Deliverables newDeliverables = getDeliverables();
                    newDeliverables.setDeliverableTask(deliverableTask);
                    newDeliverables.getDeliverablesList().add(deliverable);
//            comosRuntimeData.getDeliverables().put(deliverableTask.getTnr().getName(), newDeliverables);
                    comosRuntimeData.getDeliverables().put(deliverableTask.getDeliverableId(), newDeliverables);
                });
    }

    public void addConnectedDeliverable(ProjectStructureData deliverableTask, LogicalItem deliverable) {
//        Deliverables deliverables = comosRuntimeData.getConnectedDeliverables().get(deliverableTask.getTnr().getName());
        Deliverables deliverables = comosRuntimeData.getConnectedDeliverables().get(deliverableTask.getDeliverableId());
        Optional.ofNullable(deliverables).ifPresentOrElse(
                (deliverablesItem) -> deliverablesItem.getDeliverablesList().add(deliverable),
                () -> {
                    Deliverables newDeliverables = getDeliverables();
                    newDeliverables.setDeliverableTask(deliverableTask);
                    newDeliverables.getDeliverablesList().add(deliverable);
//                    comosRuntimeData.getConnectedDeliverables().put(deliverableTask.getTnr().getName(), newDeliverables);
                    comosRuntimeData.getConnectedDeliverables().put(deliverableTask.getDeliverableId(), newDeliverables);
                });
    }

    /**
     * No Usage found at all place
     *
     * @return
     */
    public ProjectStructureData getDeliverableTask() {
        TNR tnr = beanFactory.getBean(TNR.class);
        ProjectStructureData projectStructureData = beanFactory.getBean(ProjectStructureData.class);
        projectStructureData.setTnr(tnr);
        return projectStructureData;

    }

    public LogicalItem getDeliverable() {
        return beanFactory.getBean(LogicalItem.class);
    }

    private LogicalItem getDeliverable(String type, String name, String title, String logDevicePosition) {
        LogicalItem deliverable = getDeliverable();
        deliverable.setType(type);
        deliverable.setName(name);
        deliverable.setTitle(title);
        deliverable.setLogDevicePosition(logDevicePosition);
        return deliverable;
    }

    public Deliverables getDeliverables() {
        Deliverables deliverables = beanFactory.getBean(Deliverables.class);
        List<LogicalItem> deliverablesList = new ArrayList<>();
        deliverables.setDeliverablesList(deliverablesList);
        return deliverables;
    }

    /**
     * No Usage found at all place
     *
     * @param type
     * @param name
     * @param title
     * @param logDevicePosition
     * @param itemAttributes
     */
    public void addDeliverable(String type, String name, String title, String logDevicePosition, HashMap<String, String> itemAttributes) {
        Optional
                .ofNullable(itemAttributes)
                .ifPresent((attributeMap) -> {
                    Optional
                            .ofNullable(attributeMap.get(PropertyReader.getProperty("comos.items.deliverable.attribute")))
                            .ifPresent(deliverablesId -> {
                                Deliverables deliverables = Optional
                                        .ofNullable(comosRuntimeData.getDeliverables().get(deliverablesId))
                                        .orElse(getDeliverables());

                                LogicalItem deliverable = getDeliverable(type, name, title, logDevicePosition);
                                deliverables.getDeliverablesList().add(deliverable);

                                comosRuntimeData.getDeliverables().put(deliverablesId, deliverables);

                                addDeliverableTask(deliverablesId);
                            });
                });
    }

    private void addDeliverableTask(String deliverablesId) {
        HashMap<String, String> whereClause = prepareWhereClause(deliverablesId);
        try {
            TNR wantedTask = beanFactory.getBean(TNR.class);
            wantedTask.setType("Task");
            List<HashMap<String, String>> taskList = commonSearch
                    .searchItem(sessionModel.getContext(), wantedTask, whereClause);
            Optional
                    .ofNullable(taskList)
                    .filter(allTask -> !allTask.isEmpty())
                    .get()
                    .stream()
                    .findFirst()
                    .ifPresent((attributeMap) -> {
                        ProjectStructureData task = getTask(attributeMap);
                        task.setDeliverableId(deliverablesId);
                        addDeliverableTask(task);
                    });
        } catch (Exception e) {
            log.error(e);
        }
    }

    private HashMap<String, String> prepareWhereClause(String deliverablesId) {
        HashMap<String, String> whereClause = new HashMap<>();
        whereClause.put("attribute[PRJ_ComosActivityUID]", deliverablesId);
        return whereClause;
    }

    private ProjectStructureData getTask(HashMap<String, String> attributeMap) {
        ProjectStructureData deliverableTask = getDeliverableTask();
        deliverableTask.getTnr().setType(attributeMap.get("type"));
        deliverableTask.getTnr().setName(attributeMap.get("name"));
        deliverableTask.getTnr().setRevision(attributeMap.get("revision"));
        deliverableTask.setObjectId(attributeMap.get("id"));

        return deliverableTask;
    }
}
