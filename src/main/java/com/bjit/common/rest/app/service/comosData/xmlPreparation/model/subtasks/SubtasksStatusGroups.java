package com.bjit.common.rest.app.service.comosData.xmlPreparation.model.subtasks;


import lombok.*;
import org.springframework.context.annotation.Scope;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@ToString
//@Builder
@Getter
@Setter
@Scope("prototype")
public class SubtasksStatusGroups {
    private String id;
    private String name;
    private String description;
    private String version;
    private String parentComosActivityUID;
    private List<SubtasksStatusList> statusList;
}
