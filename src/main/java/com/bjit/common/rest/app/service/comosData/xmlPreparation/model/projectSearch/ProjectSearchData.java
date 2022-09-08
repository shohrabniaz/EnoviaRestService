package com.bjit.common.rest.app.service.comosData.xmlPreparation.model.projectSearch;

import com.bjit.common.rest.app.service.comosData.xmlPreparation.model.common.CommonData;
import lombok.*;
import org.springframework.context.annotation.Scope;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@ToString
//@Builder
@Getter
@Setter
@Scope("prototype")
public class ProjectSearchData {
    private String compassId;
    private String millId;
    private String equipmentId;

}
