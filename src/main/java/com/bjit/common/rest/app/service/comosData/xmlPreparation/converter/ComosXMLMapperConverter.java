/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.bjit.common.rest.app.service.comosData.xmlPreparation.converter;

import com.bjit.common.rest.app.service.comosData.xmlPreparation.model.mapper.ComosMapper;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.utilServices.IConverter;
import java.io.IOException;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

/**
 *
 * @author Omour Faruq
 */

@Log4j
@Component
//@Scope("prototype")
@Qualifier("ComosXMLMapperConverter")
public class ComosXMLMapperConverter implements IConverter<ComosMapper> {

    @Value("classpath:mapper_files/comos/Comos.xml")
    Resource resourceFile;

    @Override
    public String serializeData(ComosMapper model) throws JAXBException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ComosMapper deSerializeData(String data) throws JAXBException, IOException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ComosMapper deSerializeData() throws JAXBException, IOException {
        try {
            JAXBContext context = JAXBContext.newInstance(ComosMapper.class);
            return (ComosMapper) context.createUnmarshaller()
                    .unmarshal(resourceFile.getFile());
        } catch (JAXBException | IOException ex) {
            log.error(ex);
            throw ex;
        }
    }
}
