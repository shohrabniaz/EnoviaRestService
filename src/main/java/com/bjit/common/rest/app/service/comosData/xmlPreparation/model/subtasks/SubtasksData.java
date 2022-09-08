package com.bjit.common.rest.app.service.comosData.xmlPreparation.model.subtasks;

import com.bjit.common.rest.app.service.comosData.xmlPreparation.model.common.CommonData;
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
public class SubtasksData extends CommonData {
    private String compassId;
    /**
     * In models no need any extra new lines
     */
    private List<Subtasksdeliverable> deliverables;
}
