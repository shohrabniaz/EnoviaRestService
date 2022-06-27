package com.bjit.common.rest.app.service.comosData.xmlPreparation.model.common;

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
public class CommonRequestEnvelope {
    private Boolean connectInStructure;
    private String email;
    private String serviceName;
}
