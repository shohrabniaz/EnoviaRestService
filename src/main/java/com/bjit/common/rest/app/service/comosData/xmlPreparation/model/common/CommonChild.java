package com.bjit.common.rest.app.service.comosData.xmlPreparation.model.common;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@ToString
@Getter
@Setter
public class CommonChild {
    private String id;
    private String type;
    private String code;
    private String description;
    private Boolean hasChild;

}
