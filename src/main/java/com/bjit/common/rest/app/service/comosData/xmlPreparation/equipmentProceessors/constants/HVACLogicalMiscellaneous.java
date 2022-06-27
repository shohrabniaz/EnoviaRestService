package com.bjit.common.rest.app.service.comosData.xmlPreparation.equipmentProceessors.constants;

import org.springframework.stereotype.Component;

@Component
public class HVACLogicalMiscellaneous implements IConstantType{
    public String TYPE = "HVAC_Logical_Miscellaneous";
    public String DISCIPLINE = "HVAC_Logical_Part";
    public String INSTANCE_TYPE = "HVAC_Logical_Miscellaneous_Inst";
    public String INSTANCE_DISCIPLINE = "HVAC_Logical_Miscellaneous_Inst";

    @Override
    public String getConstantType() {
        return "HVACLogicalMiscellaneous";
    }

    @Override
    public void setConstants(String type, String discipline, String instanceType, String instanceDiscipline) {
        this.TYPE = type;
        this.DISCIPLINE = discipline;
        this.INSTANCE_TYPE = instanceType;
        this.INSTANCE_DISCIPLINE = instanceDiscipline;
    }
}
