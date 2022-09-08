package com.bjit.common.rest.app.service.comosData.xmlPreparation.assemblyProcessors.factoryImpls;

import com.bjit.common.rest.app.service.comosData.xmlPreparation.equipmentProceessors.factoryImpls.EquipmentCategoryFactory;
import com.bjit.ewc18x.utils.PropertyReader;
import lombok.extern.log4j.Log4j;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;


@Log4j
@Component
@RequestScope
public class EquipmentFactory extends EquipmentCategoryFactory {
    @Override
    public String getType() {
        return PropertyReader.getProperty("comos.assembly.equipment.factory.type");
    }

    @Override
    public String getPrefix() {
        return PropertyReader.getProperty("comos.assembly.equipment.factory.prefix");
    }

    @Override
    public Integer getLevel() {
        return Integer.parseInt(PropertyReader.getProperty("comos.assembly.equipment.factory.level"));
    }
}
