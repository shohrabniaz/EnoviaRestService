package com.bjit.common.rest.app.service.controller.autoNumber;

import com.bjit.common.rest.app.service.payload.common_response.Status;
import lombok.Data;
import lombok.ToString;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

import java.util.List;

@Component
//@RequestScope
@Scope("prototype")
@Data
@ToString
public class NumberGeneratorResponse {
    private String type;
    private Status status;
    private List<String> name;
    private List<String> error;
}
