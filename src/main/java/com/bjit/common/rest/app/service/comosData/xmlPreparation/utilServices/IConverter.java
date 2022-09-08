package com.bjit.common.rest.app.service.comosData.xmlPreparation.utilServices;

import java.io.IOException;
import javax.xml.bind.JAXBException;


public interface IConverter<T> {
    String serializeData(T model) throws JAXBException, IOException;
    T deSerializeData(String data) throws JAXBException, IOException;
    T deSerializeData() throws JAXBException, IOException;
}