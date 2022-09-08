package com.bjit.common.rest.app.service.controller.autoNumber;

import lombok.Data;
import lombok.ToString;
import matrix.db.Context;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
//@RequestScope
@Scope("prototype")
@Data
@ToString
public class NumberGenerationModel {
    private String type;
    private Integer objectCount;
    private Context context;
    private String format;
}
