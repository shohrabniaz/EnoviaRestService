package com.bjit.common.rest.app.service.comosData.xmlPreparation.structureProcessor;

import com.bjit.common.rest.app.service.comosData.xmlPreparation.integrationProcessors.IBatchToolRunner;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.integrationProcessors.IComosStructureCollector;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.model.DeliverableTaskAndLogicalItemMap;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.model.DeliverableTaskAndLogicalItemMapBuilder;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.model.SessionModel;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.model.comosEnovia.ComosRuntimeData;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.model.comosEnovia.Deliverables;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.utilServices.IComosFileWriter;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.utilServices.IFileReader;
import com.bjit.common.rest.app.service.utilities.IJSON;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import org.apache.log4j.Logger;

@Log4j
@Service
public class PrepareStructure implements IPrepareStructure {

    private static final Logger prepareStructure_Logger = Logger.getLogger(PrepareStructure.class);

    @Autowired
    @Qualifier("ComosFileWriter")
    IComosFileWriter fileWriter;

    @Autowired
    @Qualifier("ComosFileReader")
    IFileReader fileReader;
    @Autowired
    IJSON json;
    @Autowired
    DeliverableTaskAndLogicalItemMapBuilder dtlib;
    @Autowired
    Environment env;
    @Autowired
    SessionModel sessionModel;

    @Autowired
    @Qualifier("LogicalStructureBatchToolRunner")
    IBatchToolRunner batchToolRunner;

    @Autowired
    IComosStructureCollector comosStructureCollector;

    private final Function<HashMap<String, Deliverables>, Optional<List<Deliverables>>> flattenedDeliverables = mapData -> Optional.of(mapData
            .values()
            .stream()
            .collect(Collectors.toList()));
    private final Function<Deliverables, Deliverables> expandTask = deliItem -> {
        try {
            return comosStructureCollector.expandTask(deliItem);
        } catch (Exception e) {
            log.error(e);
        }
        return deliItem;
    };
    @Autowired
    ComosRuntimeData comosRuntimeData;
    private final Supplier<List<Deliverables>> findOutLogicalAndTaskItem = ()
            -> Optional.ofNullable(comosRuntimeData.getDeliverables())
                    .filter(deliMap -> !deliMap.isEmpty())
                    .flatMap(flattenedDeliverables)
                    .stream()
                    .flatMap(List::stream)
                    .filter(deliItem -> !deliItem.getDeliverablesList().isEmpty())
                    .filter(deliItem -> Optional.ofNullable(deliItem.getDeliverableTask()).isPresent())
                    .map(expandTask)
                    .map(comosStructureCollector::findLogicalItems)
                    .collect(Collectors.toList());

    @Override
    public Boolean prepareComosStructure() {
        prepareStructure_Logger.info("##############################################################################################");
        prepareStructure_Logger.info("As CATIA is not installed, batchtool run has been stopped");
//       batchToolRunner.run();
        List<Deliverables> deliverablesList = findOutLogicalAndTaskItem.get();
        comosStructureCollector.connectItems(deliverablesList);

        return true;
    }

    @Override
    public <T> Boolean prepareDeliverableTaskAndLogicalItemMap(T requestData, String email, String serviceName) {
        List<Deliverables> deliverablesList = findOutLogicalAndTaskItem.get();

        deliverablesList = updateWithPreviouslyGeneratedDeliverableList(deliverablesList);

        String deliverableTaskAndLogicalItem = json.serialize(dtlib.getDeliverableTaskAndLogicalItemMap(serviceName, email, requestData, deliverablesList));

        fileWriter.writeFile(
                env.getProperty("deliverable.task.and.logical.item.connection.file.directory"),
                sessionModel.getXmlMapFileName(),
                deliverableTaskAndLogicalItem,
                "json");

        return true;
    }

    private List<Deliverables> updateWithPreviouslyGeneratedDeliverableList(List<Deliverables> deliverablesList) {
        try {
            String previouslyGeneratedFileData = fileReader.readFile(env.getProperty("deliverable.task.and.logical.item.connection.file.directory") + sessionModel.getXmlMapFileName() + ".json");
            DeliverableTaskAndLogicalItemMap deliverableTaskAndLogicalItemMap = json.deserialize(previouslyGeneratedFileData, DeliverableTaskAndLogicalItemMap.class);
            deliverablesList.addAll(deliverableTaskAndLogicalItemMap.getDeliverablesOfTaskAndLogicalItem());
        } catch (IOException e) {
            log.error(e);
        }
        return deliverablesList;
    }
}
