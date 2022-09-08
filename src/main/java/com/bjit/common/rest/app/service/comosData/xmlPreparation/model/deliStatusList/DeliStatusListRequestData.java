package com.bjit.common.rest.app.service.comosData.xmlPreparation.model.deliStatusList;

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
public class DeliStatusListRequestData {

    @NotNull(message = "Compass Id is mandatory")
    @NotBlank(message = "Compass Id can't be empty")
    private String compassId;

    @NotNull(message = "Mill Id is mandatory")
    @NotBlank(message = "Mill Id can't be empty")
    private String millId;

    @NotNull(message = "Equipment Id is mandatory")
    @NotBlank(message = "Equipment Id can't be empty")
    private String equipmentId;

    @NotNull(message = "lnReceiver is mandatory")
    @NotBlank(message = "lnReceiver can't be empty")
    private String lnReceiver;
}
