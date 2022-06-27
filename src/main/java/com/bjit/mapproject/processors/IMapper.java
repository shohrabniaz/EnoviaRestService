package com.bjit.mapproject.processors;

import javax.xml.bind.JAXBException;

public interface IMapper<T> {
    T getObject() throws Exception;
    T getObjectFromString() throws JAXBException;
    void setObject() throws Exception;
    void setObject(T object) throws Exception;
    void __init__(Class<T> classReference) throws Exception;
}
