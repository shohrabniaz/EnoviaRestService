package com.bjit.common.rest.app.service.comosData.xmlPreparation.model.assigneeService;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;


@Data
@ToString
@Component
@Scope("prototype")
@NoArgsConstructor
@AllArgsConstructor
public class DSAssigneeRequestModel {
    private List<DSAssigneeRequestData> data;
}
