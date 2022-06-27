package com.bjit.common.rest.app.service.comosData.xmlPreparation.integrationProcessors;

import com.bjit.common.rest.app.service.aspects.annotations.LogExecutionTime;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.model.DeliverableTaskAndLogicalItemMap;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.model.ProjectStructureRequestData;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.model.SessionModel;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.model.comosEnovia.Deliverables;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.model.comosEnovia.LogicalItem;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.model.projectStructure.ProjectStructureServiceResponse;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.utilServices.IStructurePreparation;
import com.bjit.common.rest.app.service.model.tnr.TNR;
import com.bjit.common.rest.app.service.utilities.CommonSearch;
import com.bjit.common.rest.item_bom_import.xml_mapping_model.ResponseMessageFormaterBean;
import lombok.extern.log4j.Log4j;
import matrix.db.*;
import matrix.util.MatrixException;
import matrix.util.StringList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Log4j
@Service
public class ComosStructureCollector implements IComosStructureCollector {

    @Autowired
    @Qualifier("ProjectStructurePreparation")
    IStructurePreparation<ResponseMessageFormaterBean, ProjectStructureServiceResponse, ProjectStructureRequestData> projectStructurePreparator;

    @Autowired
    SessionModel sessionModel;

    @Autowired
    CommonSearch commonSearch;

    @Autowired
    IConnectTaskAndDeliverable connectTaskAndDeliverable;

    @Override
    @LogExecutionTime
    public Boolean connectItem(String deliverableTaskId, String logicalItemId) {
        try {
            return connectTaskAndDeliverable
                    .connectItem(deliverableTaskId, logicalItemId);
        } catch (MatrixException e) {
            log.error(e);
            sessionModel.equals(e.getMessages());
        }

        return false;
    }

    @Override
    @LogExecutionTime
    public void connectItems(List<Deliverables> deliverablesList) {
        deliverablesList.stream().forEach(deliverables -> {

            List<LogicalItem> notConnectedLogicalItemList = deliverables.getDeliverablesList()
                    .stream()
                    .filter(logicalItem -> Optional.ofNullable(logicalItem.getRelationId()).isEmpty())
                    .collect(Collectors.toList());

            notConnectedLogicalItemList.forEach(logicalItem -> {
                connectItem(deliverables.getDeliverableTask().getObjectId(), logicalItem.getItemId());
            });
        });
    }

    @LogExecutionTime
    @Override
    public List<String> connectItems(HashMap<String, DeliverableTaskAndLogicalItemMap> filenameAndDataMap) {
        List<String> unDeletableFilenames = new ArrayList<>();
        filenameAndDataMap.forEach((filename, deliverableTaskAndLogicalItemMap) -> {
            deliverableTaskAndLogicalItemMap
                    .getDeliverablesOfTaskAndLogicalItem()
                    .stream()
                    .forEach(deliverables -> {
                        List<LogicalItem> notConnectedLogicalItemList = deliverables
                                .getDeliverablesList()
                                .stream()
                                .filter(logicalItem -> Optional.ofNullable(logicalItem.getRelationId()).isEmpty())
                                .collect(Collectors.toList());

                        notConnectedLogicalItemList
                                .stream()
                                .map(logicalItem -> connectItem(deliverables.getDeliverableTask().getObjectId(), logicalItem.getItemId()))
                                .collect(Collectors.toList())
                                .stream()
                                .filter(allConnected -> allConnected.equals(false))
                                .findAny()
                                .ifPresent(notDeletable -> unDeletableFilenames.add(filename));
                    });
        });

        return unDeletableFilenames;
    }

    @Override
    @LogExecutionTime
    public Deliverables expandTask(Deliverables deliItem) throws Exception {
        Context context = sessionModel.getContext();

        StringList objSelect = getObjetAttributeList();
        StringList relSelect = getRelationAttributeList();

        BusinessObject deliverableTask = new BusinessObject(deliItem.getDeliverableTask().getObjectId());
        deliverableTask.open(context);
        ExpansionWithSelect expandSelect = deliverableTask.expandSelect(context, "Task Deliverable", "Piping_Logical_Valve,RFLVPMLogicalSystemReference,EnsLogicalEquipment,RFLVPMLogicalReference,HVAC_Line,HVAC_Logical_Branch,HVAC_Logical_Miscellaneous", objSelect, relSelect, false, true, (short) 1);

        RelationshipWithSelectList relationships = expandSelect.getRelationships();

        relationships.getIterator().forEach((RelationshipWithSelect relationshipWithSelect) -> {
            try {
                relationshipWithSelect.open(context);

                BusinessObject childItem = relationshipWithSelect.getTo();
                childItem.open(context);

                updateDeliItem(deliItem, relationshipWithSelect, childItem);

                relationshipWithSelect.close(context);
            } catch (MatrixException ex) {
                log.error(ex);
            }
        });

        return deliItem;
    }

    @Override
    @LogExecutionTime
    public Boolean expandTask(String taskObjectId, String logicalObjectId) {
        try {
            Context context = sessionModel.getContext();

            StringList objSelect = getObjetAttributeList();
            StringList relSelect = getRelationAttributeList();

            BusinessObject deliverableTask = new BusinessObject(taskObjectId);
            deliverableTask.open(context);
            ExpansionWithSelect expandSelect = deliverableTask.expandSelect(context, "Task Deliverable", "Piping_Logical_Valve,RFLVPMLogicalSystemReference,EnsLogicalEquipment,RFLVPMLogicalReference,HVAC_Line,HVAC_Logical_Branch,HVAC_Logical_Miscellaneous", objSelect, relSelect, false, true, (short) 1);

            RelationshipWithSelectList relationships = expandSelect.getRelationships();

            relationships.getIterator().forEach((RelationshipWithSelect relationshipWithSelect) -> {
                try {
                    relationshipWithSelect.open(context);

                    BusinessObject childItem = relationshipWithSelect.getTo();
                    childItem.open(context);

                    String objectId = childItem.getObjectId();

                    relationshipWithSelect.close(context);
                    if (objectId.equals(logicalObjectId)) {
                        throw new RuntimeException("true");
                    }

                } catch (MatrixException ex) {
                    log.error(ex);
                }
            });
        } catch (RuntimeException exp) {
            return Boolean.parseBoolean(exp.getMessage());
        } catch (Exception exp) {
            log.error(exp.getMessage());
        }

        return Boolean.FALSE;
    }

    @Override
    @LogExecutionTime
    public Deliverables findLogicalItems(Deliverables deliItem) {
        Context context = sessionModel.getContext();
        deliItem.getDeliverablesList()
                .stream()
                .filter(logicalItem -> Optional.ofNullable(logicalItem.getItemId()).isEmpty())
                .forEach(logicalItem -> {
                    try {
                        List<HashMap<String, String>> logicalItemsPropertyMap = commonSearch.searchItem(context, new TNR(logicalItem.getType(), logicalItem.getName(), null));

                        logicalItemsPropertyMap
                                .stream()
                                .filter(map -> !map.isEmpty())
                                .findFirst()
                                .ifPresent(map -> logicalItem.setItemId(map.get("id")));

                    } catch (Exception e) {
                        log.error(e);
                    }
                });
        return deliItem;
    }

    private void updateDeliItem(Deliverables deliItem, RelationshipWithSelect relationshipWithSelect, BusinessObject childItem) {
        HashMap<String, String> attributeMap = getAttributeMap(childItem);
        String externalId = attributeMap.get("PLMEntity.PLM_ExternalID");
        System.out.println("External Id : " + externalId);

        String type = childItem.getTypeName();
        System.out.println("type = : " + type);

        deliItem.getDeliverablesList()
                .stream()
                .filter(deliverableItem -> deliverableItem.getName().equals(externalId))
                .filter(deliverableItem -> deliverableItem.getType().equals(type))
                .findAny()
                .ifPresent(deliLogicalItem -> {
                    deliLogicalItem.setItemId(childItem.getObjectId());
                    deliLogicalItem.setRelationId(relationshipWithSelect.getName());
                });
    }

    private HashMap<String, String> getAttributeMap(BusinessObject childItem) {
        HashMap<String, String> attributesMap = new HashMap<>();
        try {
            childItem.getAttributes(sessionModel.getContext(), getObjetAttributeList()).stream().iterator().forEachRemaining(attribute -> {
                attributesMap.put(attribute.getName(), attribute.getValue());
            });
        } catch (MatrixException e) {
            log.error(e);
        }

        return attributesMap;
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
        objSelect.add("PLMEntity.PLM_ExternalID");
        return objSelect;
    }
}
