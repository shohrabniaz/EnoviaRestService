package com.bjit.common.rest.app.service.comosData.xmlPreparation.equipmentProceessors.factoryImpls;

import com.bjit.common.rest.app.service.comosData.exceptions.EquipmentStructureException;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.consumer.ntlmAuth.IComosData;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.equipmentProceessors.XMLAttributeGenerator;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.equipmentProceessors.factoryServices.IComosFactories;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.equipmentProceessors.factoryServices.IComosItemTypeUtils;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.equipmentProceessors.factoryServices.IFilenameGenerator;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.equipmentProceessors.factoryServices.IXMLDataFactory;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.model.EquipmentRequestData;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.model.comosEnovia.ComosRuntimeDataBuilder;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.model.comosEnovia.ComosServiceResponses;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.model.comosxml.RFLP;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.model.equipment.EquipmentChild;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.model.equipment.EquipmentServiceResponse;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.projectStructureProcessors.factoryImpls.ProjectStructurePreparation;
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
import java.util.Optional;

@Component
@Qualifier("EquipmentStructurePreparation")
@RequestScope
public class EquipmentStructurePreparation implements IStructurePreparation<HashMap<String, RFLP>, EquipmentServiceResponse, EquipmentRequestData> {

    private static final org.apache.log4j.Logger EQUIPMENT_STRUCTURE_PREPARATION_LOGGER = org.apache.log4j.Logger.getLogger(EquipmentStructurePreparation.class);

    @Autowired
    IComosFactories comosFactories;

    @Autowired
    IFilenameGenerator filenameGenerator;

    @Autowired
    XMLAttributeGenerator xmlAttributeGenerator;

    @Autowired
    @Qualifier("ComosFileReader")
    IFileReader fileReader;

    @Value("classpath:data/comos.json")
    Resource resourceFile;

    @Autowired
    @Qualifier("EquipmentServiceConsumer")
    IComosData<EquipmentRequestData> equipmentServiceConsumer;

    @Autowired
    ComosServiceResponses serviceResponses;

    @Autowired
    ComosRuntimeDataBuilder comosRuntimeDataBuilder;

    private Long sequence = Long.parseLong(PropertyReader.getProperty("comos.structure.preparation.sequence"));

    @Override
    public HashMap<String, RFLP> prepareStructure(EquipmentRequestData requestData) throws IOException {
        try {
            EquipmentServiceResponse responseModel = getServiceData(requestData);

            comosRuntimeDataBuilder.build();

            IComosItemTypeUtils comosItemUnitUtil = comosFactories.getComosItemTypeUtilsMap().get("Unit");
            String prefix = comosItemUnitUtil.getPrefix();
            Integer level = comosItemUnitUtil.getLevel();

            HashMap<String, String> levelWiseFileName = filenameGenerator.setMillIdAndEquipmentId(responseModel.getData().getMillId(), responseModel.getData().getEquipmentId(), prefix, level);
            HashMap<String, RFLP> structureMap = new HashMap<>();

            structureMap.put(levelWiseFileName.get(prefix + "_" + level), xmlAttributeGenerator.getRflp());
            EquipmentChild rootItem = responseModel.getData().getComosModel();

//        prepareChildDecorator(gson, serviceResponse, comosParent);
            prepareStructure(structureMap, levelWiseFileName, rootItem);

            return structureMap;
        } catch (Exception exp) {
            EQUIPMENT_STRUCTURE_PREPARATION_LOGGER.error(exp.getMessage());
            throw exp;
        }
    }

    @Override
    public EquipmentServiceResponse getServiceData(EquipmentRequestData equipmentRequestData) throws IOException {
        Boolean readFromFile = Boolean.parseBoolean(PropertyReader.getProperty("comos.get.json.data.from.file"));
        String jsonData = readFromFile ? fileReader.readFile(resourceFile.getFile()) : equipmentServiceConsumer.getComosData(equipmentRequestData);

        EquipmentServiceResponse comosResponseModel = getComosResponseModel(jsonData);
        serviceResponses.setEquipmentServiceResponse(comosResponseModel);
        return comosResponseModel;
    }

    protected HashMap<String, RFLP> prepareStructure(HashMap<String, RFLP> xmlStructureMap, HashMap<String, String> levelWiseFileName, EquipmentChild item) {
        try {
            HashMap<String, IXMLDataFactory> ixmlDataFactoryHashMap = comosFactories.getDataFactoryMap();
            String type = item.getType();
            IXMLDataFactory ixmlDataFactory = ixmlDataFactoryHashMap.get(type);
            Optional.ofNullable(ixmlDataFactory).ifPresent((IXMLDataFactory dataFactory) -> {
                dataFactory.getXMLData(xmlStructureMap, levelWiseFileName, item, ++sequence);
                IComosItemTypeUtils comosItemTypeUtil = comosFactories.getComosItemTypeUtilsMap().get(type);
                sequence = comosItemTypeUtil.getCurrentSequence();

                Optional.ofNullable(item.getChilds()).orElse(new ArrayList<>()).forEach((EquipmentChild child) -> {
                    prepareStructure(xmlStructureMap, levelWiseFileName, child);
                });
            });
            return xmlStructureMap;
        } catch (Exception exp) {
            EQUIPMENT_STRUCTURE_PREPARATION_LOGGER.error(exp);
            throw exp;
        }
    }

    private EquipmentServiceResponse getComosResponseModel(String jsonString) {
        try {
            GsonBuilder builder = new GsonBuilder();
            builder.serializeNulls();
            Gson gson = builder.create();
            EquipmentServiceResponse serviceResponse = gson.fromJson(jsonString, EquipmentServiceResponse.class);

            Optional.ofNullable(serviceResponse.getData()).orElseThrow(() -> new EquipmentStructureException(serviceResponse.getMessage()));

            return serviceResponse;
        } catch (Exception exp) {
            EQUIPMENT_STRUCTURE_PREPARATION_LOGGER.error(exp);
            throw exp;
        }
    }
}
