package com.bjit.common.rest.app.service.comosData.xmlPreparation.equipmentProceessors.constants;

import org.springframework.stereotype.Component;

@Component
public class HVACLogicalBranch implements IConstantType{
    public String TYPE = "HVAC_Logical_Branch";
    public String DISCIPLINE = "HVAC_Logical_Part";
    public String INSTANCE_TYPE = "HVAC_Logical_Branch_Inst";
    public String INSTANCE_DISCIPLINE = "HVAC_Logical_Branch_Inst";

    @Override
    public String getConstantType() {
        return "HVACLogicalBranch";
    }

    @Override
    public void setConstants(String type, String discipline, String instanceType, String instanceDiscipline) {
        this.TYPE = type;
        this.DISCIPLINE = discipline;
        this.INSTANCE_TYPE = instanceType;
        this.INSTANCE_DISCIPLINE = instanceDiscipline;
    }
}
