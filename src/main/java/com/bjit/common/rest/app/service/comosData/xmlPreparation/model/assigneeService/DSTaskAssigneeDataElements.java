package com.bjit.common.rest.app.service.comosData.xmlPreparation.model.assigneeService;

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
public class DSTaskAssigneeDataElements {
    private String name;
    private String firstname;
    private String lastname;
    private String fullname;
    private String email;
    private String currentState;
    private String Title;
}
