package com.bjit.common.rest.app.service.comosData.xmlPreparation.equipmentProceessors.constants;

import org.springframework.stereotype.Component;

@Component
public class PipeCategoryConstants implements IConstantType{
    public String TYPE = "Piping_Line";
    public String DISCIPLINE = "Piping_Line";
    public String INSTANCE_TYPE = "Piping_Line_Inst";
    public String INSTANCE_DISCIPLINE = "Piping_Line_Inst";

    @Override
    public String getConstantType() {
        return "PipeCategoryConstants";
    }

    @Override
    public void setConstants(String type, String discipline, String instanceType, String instanceDiscipline) {
        this.TYPE = type;
        this.DISCIPLINE = discipline;
        this.INSTANCE_TYPE = instanceType;
        this.INSTANCE_DISCIPLINE = instanceDiscipline;
    }
}
