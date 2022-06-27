package com.bjit.common.rest.app.service.comosData.xmlPreparation.model.equipment;

import com.bjit.common.rest.app.service.comosData.xmlPreparation.model.common.CommonChild;
import com.google.gson.annotations.SerializedName;
import lombok.*;

import java.util.HashMap;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@ToString
@Getter
@Setter
public class EquipmentChild extends CommonChild {
    @SerializedName("3DxObjectType")
    private String threeDxObjectType;
    private String level;
    private HashMap<String, String> attributes;
    private List<EquipmentChild> childs;

    private Long sequence;
    private Boolean isAParentItem;
}
