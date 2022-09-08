
package com.bjit.common.rest.app.service.comosData.xmlPreparation.model.projectStructure;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.model.common.CommonData;
import lombok.*;
@NoArgsConstructor
@AllArgsConstructor
@ToString
//@Builder
@Getter
@Setter
public class ProjectStructureData extends CommonData {
    private String millHierarchyId;
    private String plantId;
    private String projectId;
    private String layerId;
    private ProjectStructureChild projectStructure;
}
