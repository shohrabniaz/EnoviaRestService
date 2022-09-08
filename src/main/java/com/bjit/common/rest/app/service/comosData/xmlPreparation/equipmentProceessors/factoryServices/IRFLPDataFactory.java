/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.app.service.comosData.xmlPreparation.equipmentProceessors.factoryServices;

import com.bjit.common.rest.app.service.comosData.xmlPreparation.model.comosxml.RFLP;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.model.equipment.EquipmentChild;

/**
 *
 * @author BJIT
 */
public interface IRFLPDataFactory {

    String getType();

    RFLP getRFLPData(EquipmentChild parentItem, EquipmentChild childItem, Long sequence);
    RFLP getRFLPData(EquipmentChild parentItem, EquipmentChild childItem, Long sequence, String filename);
}
