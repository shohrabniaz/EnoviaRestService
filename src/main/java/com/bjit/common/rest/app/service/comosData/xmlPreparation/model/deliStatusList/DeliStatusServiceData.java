package com.bjit.common.rest.app.service.comosData.xmlPreparation.model.deliStatusList;

import lombok.*;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@ToString
@Getter
@Setter
public class DeliStatusServiceData {
    private String activity;
    private String deliUId;
    private String deliCode;
    private String deliDescription;
    private String deliType;
    private String statusGroup;
    private String statusGroupVersion;
    private String deviceUId;
    private List<DeliStatus> statusList;
    private String assingnees;
}
