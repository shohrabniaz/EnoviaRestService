/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.app.service.enovia_pdm.processors;

import com.bjit.common.rest.app.service.enovia_pdm.models.Item;
import com.bjit.common.rest.app.service.enovia_pdm.models.*;
import com.bjit.common.rest.app.service.enovia_pdm.processors.interfaces.IRequestDataBuilder;
import org.apache.log4j.Logger;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

/**
 * @author BJIT
 */
@Component
//@Scope(value = "prototype", proxyMode = ScopedProxyMode.TARGET_CLASS)
@Scope(value = "prototype")
public class MasterShipModelBuilder implements IRequestDataBuilder {

    static final Logger MASTER_SHIP_MODEL_BUILDER_LOGGER = Logger.getLogger(MasterShipModelBuilder.class);

    @Override
    public HashMap<String, PdmBom> prepareRequestData(HashMap<String, List<ParentChildModel>> parentChildRelationMap) {
        HashMap<String, PdmBom> pdmBOMMap = new HashMap<>();
        parentChildRelationMap.forEach((parentItemName, parentChildModelList) -> {

            ChildData parentData = parentChildModelList.get(0).getParentData();
            Item parentItem = new Item(parentData.getTnr(), parentData.getOwner(), parentData.getAttributeMap());
            List<ChildItem> childList = new ArrayList<>();
            PdmBom pdmBom = new PdmBom(parentItem, childList);
            pdmBOMMap.put(parentItemName, pdmBom);
            parentChildModelList.stream().forEach((parentChildModel) -> {
                ChildData childData = parentChildModel.getChildData();
                Optional.ofNullable(childData).ifPresent((ChildData child) -> {
                    ChildItem childItem = new ChildItem(childData.getTnr(), childData.getOwner(), childData.getAttributeMap(), new RelationData("BOM", parentChildModel.getItemRelationMap()));
                    childList.add(childItem);
                });
            });
        });

        return pdmBOMMap;
    }
}
