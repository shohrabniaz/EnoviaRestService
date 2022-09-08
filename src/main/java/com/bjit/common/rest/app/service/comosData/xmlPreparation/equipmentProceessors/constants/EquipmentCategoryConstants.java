package com.bjit.common.rest.app.service.comosData.xmlPreparation.equipmentProceessors.constants;

import org.springframework.stereotype.Component;

@Component
public class EquipmentCategoryConstants implements IConstantType {
    public String TYPE = "EnsLogicalEquipment";
    public String DISCIPLINE = "EnsLogicalEquipment";
    public String INSTANCE_TYPE = "EnsLogicalEquipmentInst";
    public String INSTANCE_DISCIPLINE = "EnsLogicalEquipmentInst";

    @Override
    public String getConstantType() {
        return "EquipmentCategoryConstants";
    }

    @Override
    public void setConstants(String type, String discipline, String instanceType, String instanceDiscipline) {
        this.TYPE = type;
        this.DISCIPLINE = discipline;
        this.INSTANCE_TYPE = instanceType;
        this.INSTANCE_DISCIPLINE = instanceDiscipline;
    }
}