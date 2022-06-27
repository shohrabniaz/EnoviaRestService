package com.bjit.common.rest.app.service.comosData.xmlPreparation.model.subtasks;


import lombok.*;
import org.springframework.context.annotation.Scope;

@NoArgsConstructor
@AllArgsConstructor
@ToString
//@Builder
@Getter
@Setter
@Scope("prototype")
public class SubtasksStatusList {
    private String id;
    private String name;
    private String description;
    private String readyNess;
    private String completed;
    private String baselineStartDate;
    private String baselineEndDate;
    private String plannedStartDate;
    private String plannedEndDate;
    private String actualStartDate;
    private String actualEndDate;
    private String parentComosActivityUID;
}
