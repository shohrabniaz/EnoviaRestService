package com.bjit.common.rest.app.service.comosData.xmlPreparation.model.projectStructure.search;

import lombok.*;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Data
public class Task {
    private String activity;
    private String parentActivity;
    private String type;
    private String description;
    private short level;
}
