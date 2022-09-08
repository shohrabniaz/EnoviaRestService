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
public class Subtasksdeliverable {
    private String deliverableUId;
    private String deliverableName;
    private List<SubtasksStatusGroups> statusGroups;
}
