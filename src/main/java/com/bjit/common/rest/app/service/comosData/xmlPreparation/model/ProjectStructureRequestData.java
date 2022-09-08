package com.bjit.common.rest.app.service.comosData.xmlPreparation.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@ToString
@Component
@Scope("prototype")
@NoArgsConstructor
@AllArgsConstructor
public class ProjectStructureRequestData {

    @NotNull(message = "Compass Id is mandatory")
    @NotBlank(message = "Compass Id can't be empty")
    private String compassId;

}
