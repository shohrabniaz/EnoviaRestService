package com.bjit.common.rest.app.service.comosData.xmlPreparation.utilServiceImpls;

import com.bjit.common.rest.app.service.comosData.xmlPreparation.model.comosxml.RFLP;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.utilServices.IConverter;
import java.io.IOException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.StringWriter;

@Component
@Qualifier("ComosConverter")
public class ComosConverter implements IConverter<RFLP>{
    @Override
    public String serializeData(RFLP model) throws JAXBException {
        JAXBContext jaxbContext = null;
        jaxbContext = JAXBContext.newInstance(RFLP.class);
        Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
        StringWriter sw = new StringWriter();
        jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        jaxbMarshaller.marshal(model, sw);
        String xmlString = sw.toString();
        return xmlString;
    }

    @Override
    public RFLP deSerializeData(String data) {
        return null;
    }

    @Override
    public RFLP deSerializeData() throws JAXBException, IOException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
