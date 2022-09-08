
package com.bjit.common.rest.app.service.comosData.xmlPreparation.model.projectStructure;

/**
 *
 * @author Toufiqul Khan-17
 */

import com.bjit.common.rest.app.service.comosData.xmlPreparation.model.common.CommonChild;
import lombok.*;

import java.util.HashMap;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@ToString
//@Builder
@Getter
@Setter
public class ProjectStructureChild extends CommonChild {
    private String activityLevel;
    private HashMap<String, String> properties;
    private List<ProjectStructureChild> childs;
}
