package com.bjit.common.rest.app.service.comosData.xmlPreparation.model;

import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

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
public class EquipmentRequestData {
    
    @NotNull(message = "Compass Id is mandatory")
    @NotBlank(message = "Compass Id can't be empty")
    private String compassId;
    
    @NotNull(message = "Mill Id is mandatory")
    @NotBlank(message = "Mill Id can't be empty")
    private String millId;

    @NotNull(message = "Equipment Id is mandatory")
    @NotBlank(message = "Equipment Id can't be empty")
    private String equipmentId;

    @NotNull(message = "Device Structure Level is mandatory")
    @NotBlank(message = "Device Structure Level can't be empty")
    @DecimalMin(value = "0", message = "Level 0 will return all leveled data")
    @DecimalMax(value = "4", message = "Maximum value should be 4")
    private String comosDeviceStructureLevel;

    @NotNull(message = "Category is mandatory")
    @NotBlank(message = "Category can't be empty")
    @DecimalMin(value = "0", message = "Minimum value should be 0")
    @DecimalMax(value = "7", message = "Maximum value should be 7")
    private String category;
}
