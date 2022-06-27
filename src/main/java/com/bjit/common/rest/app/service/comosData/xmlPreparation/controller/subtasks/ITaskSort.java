package com.bjit.common.rest.app.service.comosData.xmlPreparation.controller.subtasks;

import com.bjit.common.rest.app.service.comosData.project_structure.model.ComosTaskBean;

import java.util.List;

public interface ITaskSort {
    List<ComosTaskBean> sortTasksAndMileStonesData(List<ComosTaskBean> taskAndMileStoneData);
}
