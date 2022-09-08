package com.bjit.common.rest.app.service.comosData.xmlPreparation.equipmentProceessors.factoryImpls;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.equipmentProceessors.factoryServices.IComosLogicalXMLDataFactory;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.equipmentProceessors.factoryServices.IComosLogicalInstanceDataFactory;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.equipmentProceessors.factoryServices.IFilenameGenerator;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.model.comosxml.RFLVPMItem;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.model.equipment.EquipmentChild;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.model.comosxml.RFLP;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.equipmentProceessors.factoryServices.IRFLPDataFactory;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.equipmentProceessors.factoryServices.IComosItemTypeUtils;
import com.bjit.ewc18x.utils.PropertyReader;
import lombok.extern.log4j.Log4j;

@Log4j
public class PipingComponentsCategoryFactory implements IRFLPDataFactory, IComosItemTypeUtils, IComosLogicalXMLDataFactory, IComosLogicalInstanceDataFactory {

    @Autowired
    BeanFactory beanFactory;

    @Autowired
    IFilenameGenerator filenameGenerator;

    private Long sequence;

    @Override
    public RFLVPMItem getLogicalData(EquipmentChild item) {
        return null;
    }

    @Override
    public RFLVPMItem getLogicalInstance(EquipmentChild parentItem, EquipmentChild childItem) {
        return null;
    }

    @Override
    public String getType() {
        return PropertyReader.getProperty("comos.piping.components.type");
    }

    @Override
    public String getPrefix() {
        return PropertyReader.getProperty("comos.piping.components.prefix");
    }

    @Override
    public Integer getLevel() {
        //return null;
        return Integer.parseInt(PropertyReader.getProperty("comos.piping.components.level"));
    }

    @Override
    public Long getCurrentSequence() {
        return this.sequence;
    }

    @Deprecated
    @Override
    public RFLP getRFLPData(EquipmentChild parentItem, EquipmentChild childItem, Long sequence) {
        return null;
    }

    @Override
    public RFLP getRFLPData(EquipmentChild parentItem, EquipmentChild childItem, Long sequence, String filename) {
        return null;
    }
}
