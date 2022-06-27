package com.bjit.common.rest.app.service.comosData.xmlPreparation.equipmentProceessors.constants;

import org.springframework.stereotype.Component;

@Component
public class ValveCategoryConstants implements IConstantType{
    public String TYPE = "Piping_Logical_Valve";
    public String DISCIPLINE = "Piping_Logical_Part";
    public String INSTANCE_TYPE = "Piping_Logical_Valve_Inst";
    public String INSTANCE_DISCIPLINE = "Piping_Logical_Valve_Inst";

    @Override
    public String getConstantType() {
        return "ValveCategoryConstants";
    }

    @Override
    public void setConstants(String type, String discipline, String instanceType, String instanceDiscipline) {
        this.TYPE = type;
        this.DISCIPLINE = discipline;
        this.INSTANCE_TYPE = instanceType;
        this.INSTANCE_DISCIPLINE = instanceDiscipline;
    }
}
