package com.bjit.common.rest.app.service.comosData.xmlPreparation.equipmentProceessors;

import com.bjit.common.rest.app.service.comosData.xmlPreparation.equipmentProceessors.factoryServices.IComosFactories;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.equipmentProceessors.factoryServices.IXMLDataFactory;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.equipmentProceessors.factoryServices.IComosItemTypeUtils;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.equipmentProceessors.factoryServices.IRFLPDataFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import org.springframework.web.context.annotation.RequestScope;

@Component
@RequestScope
public class ComosItemDataFactoryInitializer implements IComosFactories {

    @Autowired
    List<IXMLDataFactory> dataFactoryList;

    @Autowired
    List<IRFLPDataFactory> RFLPDataFactoryList;

    @Autowired
    List<IComosItemTypeUtils> comosItemTypeList;

    HashMap<String, IXMLDataFactory> dataFactoryMap = new HashMap<>();
    HashMap<String, IRFLPDataFactory> RFLPDataFactoryMap = new HashMap<>();
    HashMap<String, IComosItemTypeUtils> comosItemTypeUtilsMap = new HashMap<>();

    @PostConstruct
    void prepareDataFactoryMap() {
        prepareDataMap();
        prepareRFLPDataMap();
        prepareComosItemTypeUtilMap();
    }

    protected void prepareDataMap() {
        dataFactoryList.forEach((IXMLDataFactory dataFactory) -> {
            dataFactoryMap.put(dataFactory.getType(), dataFactory);
        });
    }

    protected void prepareRFLPDataMap() {
        RFLPDataFactoryList.forEach((IRFLPDataFactory rflpDataFactory) -> {
            RFLPDataFactoryMap.put(rflpDataFactory.getType(), rflpDataFactory);
        });
    }

    protected void prepareComosItemTypeUtilMap() {
        comosItemTypeList.forEach((IComosItemTypeUtils comosItemUtil) -> {
            comosItemTypeUtilsMap.put(comosItemUtil.getType(), comosItemUtil);
        });
    }

    @Override
    public HashMap<String, IXMLDataFactory> getDataFactoryMap() {
        return dataFactoryMap;
    }

    @Override
    public HashMap<String, IRFLPDataFactory> getRFLPDataFactoryMap() {
        return RFLPDataFactoryMap;
    }

    @Override
    public HashMap<String, IComosItemTypeUtils> getComosItemTypeUtilsMap() {
        return comosItemTypeUtilsMap;
    }
}
