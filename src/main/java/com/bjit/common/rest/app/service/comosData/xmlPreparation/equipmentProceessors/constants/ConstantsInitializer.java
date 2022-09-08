/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.bjit.common.rest.app.service.comosData.xmlPreparation.equipmentProceessors.constants;

import java.util.HashMap;
import java.util.List;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author Omour Faruq
 */
@Component
public class ConstantsInitializer {

    @Autowired
    private List<IConstantType> constantsList;
    
    private final HashMap<String, IConstantType> constantClassMap = new HashMap<>();
    
    @PostConstruct
    private void updateConstants(){
        constantsList.forEach((IConstantType constantData) -> {
            constantClassMap.put(constantData. getConstantType(), constantData);
        });
    }
    
    public IConstantType getConstantDataObject(String objectType){
        return constantClassMap.get(objectType);
    }
}
