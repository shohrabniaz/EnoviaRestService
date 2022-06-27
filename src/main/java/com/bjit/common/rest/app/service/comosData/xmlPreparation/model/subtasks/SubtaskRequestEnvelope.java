package com.bjit.common.rest.app.service.comosData.xmlPreparation.model.subtasks;


import com.bjit.common.rest.app.service.comosData.xmlPreparation.model.SubtasksRequestData;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Data
@ToString
@Component
@Scope("prototype")
@NoArgsConstructor
@AllArgsConstructor
public class SubtaskRequestEnvelope {
    private SubtasksRequestData subtasksRequestData;
}
