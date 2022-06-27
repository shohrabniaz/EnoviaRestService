package com.bjit.common.rest.app.service.comosData.xmlPreparation.assemblyProcessors.factoryImpls;

import com.bjit.common.rest.app.service.aspects.annotations.LogExecutionTime;
import com.bjit.common.rest.app.service.comosData.exceptions.AssemblyStructureException;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.consumer.ntlmAuth.IComosData;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.equipmentProceessors.XMLAttributeGenerator;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.equipmentProceessors.factoryServices.IComosFactories;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.equipmentProceessors.factoryServices.IComosItemTypeUtils;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.equipmentProceessors.factoryServices.IFilenameGenerator;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.equipmentProceessors.factoryServices.IXMLDataFactory;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.model.AssemblyRequestData;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.model.assembly.AssemblyServiceResponse;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.model.comosEnovia.ComosRuntimeDataBuilder;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.model.comosEnovia.ComosServiceResponses;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.model.comosxml.RFLP;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.model.equipment.EquipmentChild;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.utilServices.IFileReader;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.utilServices.IStructurePreparation;
import com.bjit.ewc18x.utils.PropertyReader;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Log4j
@Component
@Qualifier("AssemblyStructurePreparation")
@RequestScope
//@Scope("prototype")
public class AssemblyStructurePreparation implements IStructurePreparation<HashMap<String, RFLP>, AssemblyServiceResponse, AssemblyRequestData> {

    @Autowired
    IComosFactories comosFactories;

    @Autowired
    IFilenameGenerator filenameGenerator;

    @Autowired
    XMLAttributeGenerator xmlAttributeGenerator;

    @Autowired
    @Qualifier("AssemblyServiceConsumer")
    IComosData<AssemblyRequestData> assemblyServiceConsumer;

    @Autowired
    @Qualifier("ComosFileReader")
    IFileReader fileReader;

    @Value("classpath:data/Assembly_Structure.json")
    Resource resourceFile;

    @Autowired
    ComosServiceResponses serviceResponses;

    @Autowired
    ComosRuntimeDataBuilder comosRuntimeDataBuilder;

    private Long sequence = Long.parseLong(PropertyReader.getProperty("comos.structure.preparation.sequence"));

    @Override
    public HashMap<String, RFLP> prepareStructure(AssemblyRequestData assemblyRequestData) throws IOException {
        try {
            AssemblyServiceResponse assemblyServiceResposne = getServiceData(assemblyRequestData);

            comosRuntimeDataBuilder.build();

            String prefix = PropertyReader.getProperty("comos.assembly.prefix");
            Integer level = 1;

            HashMap<String, String> levelWiseFileName = filenameGenerator.setMillIdAndEquipmentId(assemblyServiceResposne.getData().getMillId(), assemblyServiceResposne.getData().getEquipmentId(), prefix, level);
            HashMap<String, RFLP> structureMap = new HashMap<>();

            structureMap.put(levelWiseFileName.get(prefix + "_" + level), xmlAttributeGenerator.getRflp());
            EquipmentChild rootItem = assemblyServiceResposne.getData().getComosModel();

            prepareStructure(structureMap, levelWiseFileName, rootItem);
            return structureMap;
        } catch (Exception exp) {
            log.error(exp.getMessage());
            throw exp;
        }
    }

    @Override
    public AssemblyServiceResponse getServiceData(AssemblyRequestData requestData) throws IOException {
        AssemblyServiceResponse assemblyResponseData = getAssemblyServiceResponse(requestData);
        serviceResponses.setAssemblyServiceResponse(assemblyResponseData);
        return assemblyResponseData;
    }

    @LogExecutionTime
    private AssemblyServiceResponse getAssemblyServiceResponse(AssemblyRequestData requestData) throws IOException {
        boolean readFromFile = Boolean.parseBoolean(PropertyReader.getProperty("comos.get.json.data.from.file"));
        String assemblyStructureData = readFromFile ? fileReader.readFile(resourceFile.getFile()) : assemblyServiceConsumer.getComosData(requestData);
        return getAssemblyResponseData(assemblyStructureData);
    }

    @LogExecutionTime
    protected HashMap<String, RFLP> prepareStructure(HashMap<String, RFLP> xmlStructureMap, HashMap<String, String> levelWiseFileName, EquipmentChild item) {
        try {
            if (!Optional.ofNullable(item.getIsAParentItem()).isPresent()) {
                item.setIsAParentItem(false);
            }

            HashMap<String, IXMLDataFactory> ixmlDataFactoryHashMap = comosFactories.getDataFactoryMap();
            String type = new StringBuilder().append(PropertyReader.getProperty("comos.assembly.prefix")).append(item.getType()).toString();
            IXMLDataFactory ixmlDataFactory = ixmlDataFactoryHashMap.get(type);
//            System.out.println("Type : " + type);
            Optional
                    .ofNullable(ixmlDataFactory)
                    .ifPresentOrElse((IXMLDataFactory dataFactory) -> {
                                dataFactory.getXMLData(xmlStructureMap, levelWiseFileName, item, sequence);
                                IComosItemTypeUtils comosItemTypeUtil = comosFactories.getComosItemTypeUtilsMap().get(type);
                                sequence = comosItemTypeUtil.getCurrentSequence();

                                Optional
                                        .ofNullable(item.getChilds())
                                        .orElse(new ArrayList<>())
                                        .forEach(child -> prepareStructure(xmlStructureMap, levelWiseFileName, child));
                            }, () ->
                                    Optional
                                            .ofNullable(item.getChilds())
                                            .orElse(new ArrayList<>())
                                            .forEach(child -> prepareStructure(xmlStructureMap, levelWiseFileName, child))

                    );
            return xmlStructureMap;
        } catch (Exception exp) {
            log.error(exp);
            throw exp;
        }
    }

    private AssemblyServiceResponse getAssemblyResponseData(String jsonString) {
        try {
            GsonBuilder builder = new GsonBuilder();
            builder.serializeNulls();
            Gson gson = builder.create();

            AssemblyServiceResponse serviceResponse = gson.fromJson(jsonString, AssemblyServiceResponse.class);

            Optional.ofNullable(serviceResponse.getData()).orElseThrow(() -> new AssemblyStructureException(serviceResponse.getMessage()));

            EquipmentChild plantItem = serviceResponse.getData().getComosModel();
            List<EquipmentChild> childs = plantItem.getChilds();
//            plantItem.getChilds().clear();

            List<EquipmentChild> logicalAssembly = new ArrayList<>();
            List<EquipmentChild> children = prepareNewStructure(logicalAssembly, childs);
            plantItem.getChilds().clear();
            plantItem.getChilds().addAll(children);
            return serviceResponse;
        } catch (Exception exp) {
            log.error(exp);
            throw exp;
        }
    }

    private List<EquipmentChild> prepareNewStructure(List<EquipmentChild> logicalAssemblyList, List<EquipmentChild> childItems) {
        childItems.forEach(childItem -> {
            Optional.ofNullable(childItem.getType())
                    .filter(type -> type.equals("LogicalAssembly"))
                    .ifPresent((String action) -> logicalAssemblyList.add(childItem));

            log.debug("Child item type : " + childItem.getType());
            log.debug("Child ThreeDx type : " + childItem.getThreeDxObjectType());

            Optional
                    .ofNullable(childItem.getChilds())
                    .ifPresent((childItemList) -> prepareNewStructure(logicalAssemblyList, childItemList));
        });
        return logicalAssemblyList;
    }
}
