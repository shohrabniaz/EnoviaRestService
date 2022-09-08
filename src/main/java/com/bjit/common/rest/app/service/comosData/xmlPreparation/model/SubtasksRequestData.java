package com.bjit.common.rest.app.service.comosData.xmlPreparation.model;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class SubtasksRequestData {

    @NotNull(message = "Compass Id is mandatory")
    @NotBlank(message = "Compass Id can't be empty")
    private String compassId;

    @NotNull(message = "Mill Id is mandatory")
    @NotBlank(message = "Mill Id can't be empty")
    private String millId;

    @NotNull(message = "Equipment Id is mandatory")
    @NotBlank(message = "Equipment Id can't be empty")
    private String equipmentId;

//    @NotNull(message = "deliverableUId is mandatory")
//    @NotBlank(message = "deliverableUId can't be empty")
    private String deliverableUId;

    @NotNull(message = "lnReceiver is mandatory")
    @NotBlank(message = "lnReceiver can't be empty")
    private String lnReceiver;
}
