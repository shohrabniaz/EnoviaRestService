package com.bjit.common.rest.app.service.comosData.xmlPreparation.model.subtasks;

import com.bjit.common.rest.app.service.comosData.xmlPreparation.model.common.ComosCommonResponse;
import lombok.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@NoArgsConstructor
@AllArgsConstructor
@ToString
//@Builder
@Getter
@Setter
@Component
@Qualifier("SubtasksServiceResponse")
@Scope("prototype")
public class SubtasksServiceResponse extends ComosCommonResponse {
    private SubtasksData data;
}
