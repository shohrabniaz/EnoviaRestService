package com.bjit.common.rest.app.service.comosData.xmlPreparation.model.projectSearch;

import com.bjit.common.rest.app.service.comosData.xmlPreparation.model.common.ComosCommonResponse;

import lombok.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@ToString
//@Builder
@Getter
@Setter
@Component
@Qualifier("ProjectSearchServiceResponse")
@Scope("prototype")
public class ProjectSearchServiceResponse extends ComosCommonResponse {
    private List<ProjectSearchData> data;
}
