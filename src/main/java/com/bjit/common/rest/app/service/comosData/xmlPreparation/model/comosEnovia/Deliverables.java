package com.bjit.common.rest.app.service.comosData.xmlPreparation.model.comosEnovia;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;

@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Component
@Scope("prototype")
public class Deliverables {
    private List<LogicalItem> deliverablesList;
    private ProjectStructureData deliverableTask;
}
