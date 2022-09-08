package com.bjit.common.rest.app.service.comosData.xmlPreparation.model.comosEnovia;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Component
@Scope("prototype")
public class LogicalItem {
    private String name;
    private String type;
    private String title;
    private String logDevicePosition;
    private String itemId;
    private String relationId;
}
