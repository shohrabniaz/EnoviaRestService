package com.bjit.common.rest.app.service.comosData.xmlPreparation.integrationProcessors;

import com.bjit.common.rest.app.service.comosData.xmlPreparation.model.AssemblyRequestData;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.model.EquipmentRequestData;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.model.ProjectStructureRequestData;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.model.comosEnovia.ComosIntegration;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.model.comosEnovia.EnoviaRequest;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.model.comosEnovia.LogicalStructureRequestModels;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.function.Function;

@Service
@Qualifier("ComosModelAdapter")
public class ComosModelAdapter implements IComosModelAdapter {
    @Autowired
    BeanFactory beanFactory;

    Function<ComosIntegration, EquipmentRequestData> getEquipmentRequestData = comosRequestData -> {
        EnoviaRequest request = comosRequestData.getRequest();

        EquipmentRequestData equipmentRequestData = beanFactory.getBean(EquipmentRequestData.class);
        equipmentRequestData.setMillId(request.getMillId());
        equipmentRequestData.setEquipmentId(request.getEquipmentId());
        equipmentRequestData.setCategory(request.getCategory());
        equipmentRequestData.setComosDeviceStructureLevel(request.getComosDeviceStructureLevel());
        equipmentRequestData.setCompassId(request.getCompassId());

        return equipmentRequestData;
    };
    Function<ComosIntegration, AssemblyRequestData> getAssemblyRequestData = comosRequestData -> {
        EnoviaRequest request = comosRequestData.getRequest();

        AssemblyRequestData assemblyRequestData = beanFactory.getBean(AssemblyRequestData.class);
        assemblyRequestData.setMillId(request.getMillId());
        assemblyRequestData.setEquipmentId(request.getEquipmentId());
        assemblyRequestData.setCompassId(request.getCompassId());

        return assemblyRequestData;
    };
    Function<ComosIntegration, ProjectStructureRequestData> getProjectStructureRequestData = comosRequestData -> {
        EnoviaRequest request = comosRequestData.getRequest();

        ProjectStructureRequestData projectStructureRequestData = beanFactory.getBean(ProjectStructureRequestData.class);
        projectStructureRequestData.setCompassId(request.getCompassId());

        return projectStructureRequestData;
    };

    @Override
    public LogicalStructureRequestModels getEquipmentStructureServiceRequestData(ComosIntegration requestData, LogicalStructureRequestModels requestModels) {
        requestModels.setEquipmentRequestData(getEquipmentRequestData.apply(requestData));
        return requestModels;
    }

    @Override
    public LogicalStructureRequestModels getAssemblyStructureServiceRequestData(ComosIntegration requestData, LogicalStructureRequestModels requestModels) {
        requestModels.setAssemblyRequestData(getAssemblyRequestData.apply(requestData));
        return requestModels;
    }

    @Override
    public LogicalStructureRequestModels getProjectStructureServiceRequestData(ComosIntegration requestData, LogicalStructureRequestModels requestModels) {
        requestModels.setProjectStructureRequestData(getProjectStructureRequestData.apply(requestData));
        return requestModels;
    }
}
