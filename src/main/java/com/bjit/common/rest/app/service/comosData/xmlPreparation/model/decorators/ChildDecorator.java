package com.bjit.common.rest.app.service.comosData.xmlPreparation.model.decorators;

import com.bjit.common.rest.app.service.comosData.xmlPreparation.model.equipment.EquipmentChild;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@ToString
//@Builder
@Getter
@Setter
public class ChildDecorator {
    private EquipmentChild item;
    private Long sequence;
    private Boolean isParentItem;
    private List<ChildDecorator> childDecorator;
}
