package com.bjit.common.rest.app.service.comosData.xmlPreparation.model.assigneeService;

import com.bjit.common.code.utility.dsapi.auth.model.CSRFResponse;
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
public class DSAssigneeResponseModel {
    private String success;
    private String statusCode;
    private String error;
    private CSRFResponse csrf;
    private List<DSTaskAssigneeDataElements> data;
    private List<Object> definitions;
}
