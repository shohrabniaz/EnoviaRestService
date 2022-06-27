package com.bjit.common.rest.app.service.comosData.xmlPreparation.equipmentProceessors.constants;

import org.springframework.stereotype.Component;

@Component
public class SubUnitConstants implements IConstantType{
    public String TYPE = "RFLVPMLogicalSystemReference";
    public String DISCIPLINE = "RFLVPMLogicalSystemReference";
    public String INSTANCE_TYPE = "RFLVPMLogicalSystemInstance";
    public String INSTANCE_DISCIPLINE = "RFLVPMLogicalSystemInstance";

    @Override
    public String getConstantType() {
        return "SubUnitConstants";
    }

    @Override
    public void setConstants(String type, String discipline, String instanceType, String instanceDiscipline) {
        this.TYPE = type;
        this.DISCIPLINE = discipline;
        this.INSTANCE_TYPE = instanceType;
        this.INSTANCE_DISCIPLINE = instanceDiscipline;
    }
}
