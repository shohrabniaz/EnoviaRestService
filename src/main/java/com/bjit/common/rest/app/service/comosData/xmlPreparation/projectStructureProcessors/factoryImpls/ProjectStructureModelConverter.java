package com.bjit.common.rest.app.service.comosData.xmlPreparation.projectStructureProcessors.factoryImpls;

import com.bjit.common.rest.app.service.comosData.project_structure.model.ComosTaskBean;
import com.bjit.common.rest.app.service.comosData.project_structure.model.ComosProjectSpaceBean;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.model.projectStructure.ProjectStructureChild;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.model.projectStructure.ProjectStructureData;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.model.projectStructure.ProjectStructureServiceResponse;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.projectStructureProcessors.factoryServices.IModelConverterAdapter;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

@Component
@Qualifier("ProjectStructureModelConverter")
public class ProjectStructureModelConverter implements IModelConverterAdapter<ProjectStructureServiceResponse, ComosProjectSpaceBean> {

    @Autowired
    BeanFactory beanFactory;

    @Override
    public ComosProjectSpaceBean convert(ProjectStructureServiceResponse comosProjectStructureServiceResponse) {
        ProjectStructureChild projectStructure = comosProjectStructureServiceResponse.getData().getProjectStructure();

        ComosProjectSpaceBean comosProjectSpaceBean = getProjectSpace(comosProjectStructureServiceResponse);
        String projectSpaceName = projectStructure.getCode();
        List<ProjectStructureChild> projectSpaceChildren = projectStructure.getChilds();
        List<ComosTaskBean> taskList = getTaskList(null, projectSpaceName, projectSpaceChildren, new ArrayList<>());
        comosProjectSpaceBean.setTableData(taskList);

        return comosProjectSpaceBean;
    }

    private ComosProjectSpaceBean getProjectSpace(ProjectStructureServiceResponse comosProjectStructureServiceResponse){

        ComosProjectSpaceBean comosProjectSpaceBean = beanFactory.getBean(ComosProjectSpaceBean.class);

        ProjectStructureData projectSpaceData = comosProjectStructureServiceResponse.getData();

        comosProjectSpaceBean.setMillId(projectSpaceData.getMillId());
        comosProjectSpaceBean.setEquipmentId(projectSpaceData.getEquipmentId());
        comosProjectSpaceBean.setPlantId(projectSpaceData.getPlantId());
        comosProjectSpaceBean.setProjectId(projectSpaceData.getProjectId());
        comosProjectSpaceBean.setLayerId(projectSpaceData.getLayerId());
        comosProjectSpaceBean.setMillHierarchyId(projectSpaceData.getMillHierarchyId());

        ProjectStructureChild projectStructure = projectSpaceData.getProjectStructure();

        comosProjectSpaceBean.setProjectCode(projectStructure.getCode());
        comosProjectSpaceBean.setTitle(projectStructure.getDescription());
        comosProjectSpaceBean.setErpSubProject(projectStructure.getCode());
        comosProjectSpaceBean.setComosProjectUID(projectStructure.getId());

        return comosProjectSpaceBean;
    }

    private List<ComosTaskBean> getTaskList(String parentName, String projectSpaceName, List<ProjectStructureChild> children, List<ComosTaskBean> taskBeanList){

        Optional.ofNullable(children).orElse(new ArrayList<>()).forEach((ProjectStructureChild child) -> {
            child.getId();

//            BeanFactoryAnnotationUtils.qualifiedBeanOfType(beanFactory, Handler.class, "ResponseHandler");

            ComosTaskBean comosTaskBean = beanFactory.getBean(ComosTaskBean.class);
            comosTaskBean.setActivity(child.getCode());
            comosTaskBean.setParentActivity(parentName);
            comosTaskBean.setProject(projectSpaceName);
            comosTaskBean.setActivityTitle(child.getDescription());
            comosTaskBean.setComosActivityUID(child.getId());
            comosTaskBean.setActivityType(child.getType());
            comosTaskBean.setActivityStatus("Released");
            comosTaskBean.setErpType(child.getType());

            Optional.ofNullable(child.getProperties()).filter(properties -> !properties.isEmpty()).ifPresent(properties -> {
                Optional
                        .ofNullable(properties.get("plannedDeliveryDate"))
                        .filter(plannedDeliveryDate -> !plannedDeliveryDate.isEmpty())
                        .ifPresent(plannedDeliveryDate -> setPlannedDeliveryDate.accept(plannedDeliveryDate, comosTaskBean));

                Optional
                        .ofNullable(properties.get("lastModifiedInTable"))
                        .filter(lastModifiedInTable -> !lastModifiedInTable.isEmpty())
                        .ifPresent(lastModifiedInTable -> setLastModifiedDate.accept(lastModifiedInTable, comosTaskBean));
            });

            taskBeanList.add(comosTaskBean);

            Optional.ofNullable(child.getChilds())
                    .ifPresent((nextChildren) -> this.getTaskList(child.getCode(), projectSpaceName, nextChildren, taskBeanList));

        });

        return taskBeanList;
    }

    BiConsumer<String, ComosTaskBean> setPlannedDeliveryDate = (deliveryDate, taskBean) -> {
        taskBean.setPlannedDeliveryDate(deliveryDate);
    };

    BiConsumer<String, ComosTaskBean> setLastModifiedDate = (deliveryDate, taskBean) -> {
        taskBean.setPlannedDeliveryDate(deliveryDate);
    };
}