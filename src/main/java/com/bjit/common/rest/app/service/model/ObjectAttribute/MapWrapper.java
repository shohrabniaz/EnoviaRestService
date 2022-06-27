/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.app.service.model.ObjectAttribute;

import org.w3c.dom.Element;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.namespace.QName;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * <dl>
 * <dt>References:
 * </dt>
 * <dd>
 *  <ul>
 *      <li><a href="http://stackoverflow.com/questions/21382202/use-jaxb-xmlanyelement-type-of-style-to-return-dynamic-element-names">Dynamic element names in JAXB</a></li>
 *      <li><a href="http://stackoverflow.com/questions/3941479/jaxb-how-to-marshall-map-into-keyvalue-key">Marshal Map into key-value pairs</a></li>
 *      <li><a href="http://stackoverflow.com/questions/3293493/dynamic-tag-names-with-jaxb">Dynamic tag names with JAXB</a></li>
 *  </ul>
 * </dd>
 * </dl>
 * @author MEC
 *
 */
@XmlType
public class MapWrapper{
    private List<JAXBElement<String>> properties = new ArrayList<>();
    public MapWrapper(){
    }
    /**
     * <p>
     * Funny fact: due to type erasure, this method may return
     * List<Element> instead of List<JAXBElement<String>> in the end;
     * </p>
     * <h4>WARNING: do not use this method in your programme</h4>
     * <p>
     * Thus to retrieve map entries you've stored in this MapWrapper, it's
     * recommended to use {@link #toMap()} instead.
     * </p>
     * @return
     */
    @XmlAnyElement
    public List<JAXBElement<String>> getProperties() {
        return properties;
    }
    public void setProperties(List<JAXBElement<String>> properties) {
        this.properties = properties;
    }
    /**
     * <p>
     * Only use {@link #addEntry(JAXBElement)} and {{@link #addEntry(String, String)}
     * when this <code>MapWrapper</code> instance is created by yourself
     * (instead of through unmarshalling).
     * </p>
     * @param key map key
     * @param value map value
     */
    public void addEntry(String key, String value){
        JAXBElement<String> prop = new JAXBElement<String>(new QName(key), String.class, value);
        addEntry(prop);
    }
    public void addEntry(JAXBElement<String> prop){
        properties.add(prop);
    }
    @Override
    public String toString() {
        return "MapWrapper [properties=" + toMap() + "]";
    }
    /**
     * <p>
     * To Read-Only Map
     * </p>
     *
     * @return
     */
    public Map<String, String> toMap(){
        //Note: Due to type erasure, you cannot use properties.stream() directly when unmashalling is used..
        List<?> props = properties;
        return props.stream().collect(Collectors.toMap(MapWrapper::extractLocalName, MapWrapper::extractTextContent));
    }
    /**
     * <p>
     * Extract local name from <code>obj</code>, whether it's javax.xml.bind.JAXBElement or org.w3c.dom.Element;
     * </p>
     * @param obj
     * @return
     */
    @SuppressWarnings("unchecked")
    private static String extractLocalName(Object obj){
        Map<Class<?>, Function<? super Object, String>> strFuncs = new HashMap<>();
        strFuncs.put(JAXBElement.class, (jaxb) -> ((JAXBElement<String>)jaxb).getName().getLocalPart());
        strFuncs.put(Element.class, ele -> ((Element) ele).getLocalName());
        return extractPart(obj, strFuncs).orElse("");
    }
    /**
     * <p>
     * Extract text content from <code>obj</code>, whether it's javax.xml.bind.JAXBElement or org.w3c.dom.Element;
     * </p>
     * @param obj
     * @return
     */
    @SuppressWarnings("unchecked")
    private static String extractTextContent(Object obj){
        Map<Class<?>, Function<? super Object, String>> strFuncs = new HashMap<>();
        strFuncs.put(JAXBElement.class, (jaxb) -> ((JAXBElement<String>)jaxb).getValue());
        strFuncs.put(Element.class, ele -> ((Element) ele).getTextContent());
        return extractPart(obj, strFuncs).orElse("");
    }
    /**
     * Check class type of <code>obj</code> according to types listed in <code>strFuncs</code> keys,
     * then extract some string part from it according to the extract function specified in <code>strFuncs</code>
     * values.
     * @param obj
     * @param strFuncs
     * @return
     */
    private static <ObjType, T> Optional<T> extractPart(ObjType obj, Map<Class<?>, Function<? super ObjType, T>> strFuncs){
        for(Class<?> clazz : strFuncs.keySet()){
            if(clazz.isInstance(obj)){
                return Optional.of(strFuncs.get(clazz).apply(obj));
            }
        }
        return Optional.empty();
    }
}