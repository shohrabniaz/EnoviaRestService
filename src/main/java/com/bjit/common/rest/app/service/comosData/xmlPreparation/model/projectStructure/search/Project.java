package com.bjit.common.rest.app.service.comosData.xmlPreparation.model.projectStructure.search;


import lombok.*;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Data
public class Project {
    private String name;
    private String description;
    private List<Task> activityList;
}
