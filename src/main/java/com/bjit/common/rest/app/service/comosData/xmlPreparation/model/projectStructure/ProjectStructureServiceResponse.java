package com.bjit.common.rest.app.service.comosData.xmlPreparation.model.projectStructure;

import com.bjit.common.rest.app.service.comosData.xmlPreparation.model.assembly.AssemblyServiceResponse;
/**
 *
 * @author Toufiqul Khan-17
 */
import com.bjit.common.rest.app.service.comosData.xmlPreparation.model.common.ComosCommonResponse;
import lombok.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

@NoArgsConstructor
@AllArgsConstructor
@ToString
@Getter
@Setter
@Qualifier("ProjectStructureServiceResponse")
public class ProjectStructureServiceResponse extends ComosCommonResponse {
    private ProjectStructureData data;
}
