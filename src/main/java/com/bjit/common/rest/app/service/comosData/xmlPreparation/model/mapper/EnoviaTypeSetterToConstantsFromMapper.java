/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.bjit.common.rest.app.service.comosData.xmlPreparation.model.mapper;

import com.bjit.common.rest.app.service.comosData.xmlPreparation.utilServices.IConverter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import javax.annotation.PostConstruct;
import javax.xml.bind.JAXBException;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 *
 * @author Omour Faruq
 */
@Log4j
@Component
@Qualifier("EnoviaTypeSetterToConstantsFromMapper")
public class EnoviaTypeSetterToConstantsFromMapper {

    @Autowired
    @Qualifier("ComosXMLMapperConverter")
    IConverter<ComosMapper> comosMapperConverter;

    ComosMapper comosMapper;

    HashMap<String, HashMap<String, HashMap<String, HashMap<String, HashMap<String, String>>>>> comosXMLMap;

    @PostConstruct
    public void setMapDataToConstants() {
        try {

            comosXMLMap = new HashMap<>();
            comosMapper = comosMapperConverter.deSerializeData();

            comosMapper.getItemList().forEach((ComosItems comosItems) -> {

                String parentType = comosItems.getType();
                List<Item> childItemList = comosItems.getItemList();

                HashMap<String, HashMap<String, HashMap<String, HashMap<String, String>>>> childWiseAttributeMap = new HashMap<>();
                comosXMLMap.put(parentType, childWiseAttributeMap);

                Optional.ofNullable(childItemList).orElse(new ArrayList<>()).forEach((Item childItem) -> {
                    String childType = childItem.getType();

                    ComosAttributeList comosAttributeList = Optional.ofNullable(childItem.getComosAttributeList()).orElse(new ComosAttributeList());

                    HashMap<String, HashMap<String, HashMap<String, String>>> attributeTypeWiseMap = new HashMap<>();
                    childWiseAttributeMap.put(childType, attributeTypeWiseMap);

                    ItemAttributeList itemAttributes = Optional.ofNullable(comosAttributeList.getItemAttributes()).orElse(new ItemAttributeList());

                    HashMap<String, HashMap<String, String>> typeWiseItemAttributeMap = new HashMap<>();
                    attributeTypeWiseMap.put("item", typeWiseItemAttributeMap);
                    Optional.ofNullable(itemAttributes.getItemAttributeList()).orElse(new ArrayList<>()).forEach((ItemAttribute itemAttribute) -> {
                        String itemType = itemAttribute.getType();
                        HashMap<String, String> itemAttributeMap = new HashMap<>();
                        itemAttributeMap.put(itemAttribute.getSourceType(), itemAttribute.getDestinationType());
                        typeWiseItemAttributeMap.put(itemType, itemAttributeMap);
                    });

                    RelationalAttributeList relationalAttributes = Optional.ofNullable(comosAttributeList.getRelationalAttributes()).orElse(new RelationalAttributeList());

                    HashMap<String, HashMap<String, String>> typeWiseRelationalAttributeMap = new HashMap<>();
                    attributeTypeWiseMap.put("relation", typeWiseRelationalAttributeMap);
                    Optional.ofNullable(relationalAttributes.getRelationalAttributeList()).orElse(new ArrayList<>()).forEach((RelationalAttribute relationalAttribute) -> {
                        String relationType = relationalAttribute.getType();
                        HashMap<String, String> itemAttributeMap = new HashMap<>();
                        itemAttributeMap.put(relationalAttribute.getSourceType(), relationalAttribute.getDestinationType());
                        typeWiseRelationalAttributeMap.put(relationType, itemAttributeMap);
                    });
                });
            });
        } catch (JAXBException | IOException ex) {
            log.error(ex);
        }
    }

    public HashMap<String, HashMap<String, HashMap<String, HashMap<String, HashMap<String, String>>>>> getConfigurableMap() {
        return comosXMLMap;
    }

    public ComosMapper getMapper() {
        return comosMapper;
    }
}
