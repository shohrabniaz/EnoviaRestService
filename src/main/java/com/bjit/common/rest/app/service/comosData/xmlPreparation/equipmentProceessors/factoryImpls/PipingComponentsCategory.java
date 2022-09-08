package com.bjit.common.rest.app.service.comosData.xmlPreparation.equipmentProceessors.factoryImpls;

import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.equipmentProceessors.factoryServices.IComosLogicalXMLDataFactory;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.equipmentProceessors.factoryServices.IComosLogicalInstanceDataFactory;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.equipmentProceessors.factoryServices.IFilenameGenerator;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.equipmentProceessors.XMLAttributeGenerator;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.equipmentProceessors.factoryServices.IComosFactories;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.model.comosxml.RFLVPMItem;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.model.equipment.EquipmentChild;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.model.comosxml.RFLP;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.equipmentProceessors.factoryServices.IRFLPDataFactory;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.equipmentProceessors.factoryServices.IComosItemTypeUtils;
import org.apache.poi.ss.formula.eval.NotImplementedException;

@Log4j
//@Component
////@Scope("prototype")
//@RequestScope

public class PipingComponentsCategory implements IRFLPDataFactory, IComosItemTypeUtils, IComosLogicalXMLDataFactory, IComosLogicalInstanceDataFactory {

    @Autowired
    BeanFactory beanFactory;

    @Autowired
    IFilenameGenerator filenameGenerator;

    @Autowired
    XMLAttributeGenerator xmlAttributeGenerator;

    @Autowired
    IComosFactories comosFactories;

    private Long sequence;

    @Override
    public RFLVPMItem getLogicalData(EquipmentChild item) {
        log.debug("Item type : " + item.getThreeDxObjectType() + "Item name : " + item.getId());
        throw new NotImplementedException("Piping Component Category has not been implemented yet");
    }

    @Override
    public RFLVPMItem getLogicalInstance(EquipmentChild parentItem, EquipmentChild childItem) {
        throw new NotImplementedException("Piping Component Category has not been implemented yet");
    }

    @Override
    public String getType() {
        return "PipingComponentsCategory";
    }

    @Override
    public String getPrefix() {
        return "C";
    }

    @Override
    public Integer getLevel() {
        return 3;
    }

    @Override
    public Long getCurrentSequence() {
        return this.sequence;
    }

    @Deprecated
    @Override
    public RFLP getRFLPData(EquipmentChild parentItem, EquipmentChild childItem, Long sequence) {
        throw new NotImplementedException("Piping Component Category has not been implemented yet");
    }

    @Override
    public RFLP getRFLPData(EquipmentChild parentItem, EquipmentChild childItem, Long sequence, String filename) {
        return null;
    }
}
