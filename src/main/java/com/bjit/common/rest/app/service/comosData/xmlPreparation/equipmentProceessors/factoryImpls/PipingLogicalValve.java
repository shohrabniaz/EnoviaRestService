package com.bjit.common.rest.app.service.comosData.xmlPreparation.equipmentProceessors.factoryImpls;

import com.bjit.common.rest.app.service.comosData.xmlPreparation.model.comosxml.RFLP;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.model.equipment.EquipmentChild;
import com.bjit.ewc18x.utils.PropertyReader;
import lombok.extern.log4j.Log4j;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

@Log4j
@Component
@RequestScope
public class PipingLogicalValve extends ValveCategoryFactory {

    @Override
    public String getType() {
//        return "Piping_Logical_Valve";
        return PropertyReader.getProperty("comos.piping.logical.valve.type");
    }

    @Override
    public Long getCurrentSequence() {
        return this.sequence;
    }

    @Override
    @Deprecated
    public RFLP getRFLPData(EquipmentChild parentItem, EquipmentChild childItem, Long sequence) {
        return super.getRFLPData(parentItem, childItem, sequence);
    }
}
