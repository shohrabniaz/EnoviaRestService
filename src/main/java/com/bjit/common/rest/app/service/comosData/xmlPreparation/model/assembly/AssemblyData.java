
package com.bjit.common.rest.app.service.comosData.xmlPreparation.model.assembly;

import com.bjit.common.rest.app.service.comosData.xmlPreparation.model.equipment.EquipmentData;
import lombok.*;

/**
 *
 * @author Toufiqul Khan-17
 */
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Getter
@Setter
public class AssemblyData extends EquipmentData {
    private String millHierarchyId;
}
