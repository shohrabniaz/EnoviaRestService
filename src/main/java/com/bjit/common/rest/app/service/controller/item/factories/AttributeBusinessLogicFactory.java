/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.app.service.controller.item.factories;

import com.bjit.common.rest.app.service.constants.ItemImportEnvironments;
import com.bjit.common.rest.app.service.controller.item.attr_biz_logic.CommonAttributeBusinessLogic;
import com.bjit.common.rest.app.service.controller.item.attr_biz_logic.HP_AttributeBusinessLogic;
import com.bjit.common.rest.app.service.comos.attrBizLogic.ComosAttributeBusinessLogic;
import com.bjit.common.rest.app.service.controller.item.attr_biz_logic.MD_AttributeBusinessLogic;
import com.bjit.common.rest.app.service.controller.item.attr_biz_logic.MV_AttributeBusinessLogic;
import com.bjit.common.rest.app.service.controller.item.attr_biz_logic.ProductAttributeBusinessLogic;
import com.bjit.common.rest.app.service.controller.item.attr_biz_logic.SP_AttributeBusinessLogic;
import com.bjit.common.rest.app.service.controller.item.attr_biz_logic.SW_AttributeBusinessLogic;
import com.bjit.common.rest.item_bom_import.item_import.AttributeBusinessLogic;

/**
 *
 * @author BJIT
 */
public class AttributeBusinessLogicFactory {

    public AttributeBusinessLogic getAttributeBusinessLogic(String businessLogicType) {
        AttributeBusinessLogic attributeBusinessLogic;
        switch (businessLogicType) {
            case ItemImportEnvironments.COMMON:
                attributeBusinessLogic = new CommonAttributeBusinessLogic();
                break;
            case ItemImportEnvironments.MASS_HW_IMPORT:
                attributeBusinessLogic = new HP_AttributeBusinessLogic();
                break;
            case ItemImportEnvironments.MASS_SWP_IMPORT:
                attributeBusinessLogic = new SW_AttributeBusinessLogic();
                break;
            case ItemImportEnvironments.MASS_MV_IMPORT:
                attributeBusinessLogic = new MV_AttributeBusinessLogic();
                break;
            case ItemImportEnvironments.MASS_SP_IMPORT:
                attributeBusinessLogic = new SP_AttributeBusinessLogic();
                break;
            case ItemImportEnvironments.MASS_MDP_IMPORT:
                attributeBusinessLogic = new MD_AttributeBusinessLogic();
                break;
            case ItemImportEnvironments.PRDCONFIG:
                attributeBusinessLogic = new ProductAttributeBusinessLogic();
                break;
            case ItemImportEnvironments.MOPAZ:
                attributeBusinessLogic = new CommonAttributeBusinessLogic();
                break;
            case ItemImportEnvironments.COMOS:
                attributeBusinessLogic = new ComosAttributeBusinessLogic();
                break;
            default:
                attributeBusinessLogic = new CommonAttributeBusinessLogic();
                break;
        }
        return attributeBusinessLogic;
    }
}
