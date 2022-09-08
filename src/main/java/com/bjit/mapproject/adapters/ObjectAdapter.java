/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.mapproject.adapters;

import com.bjit.mapproject.xml_mapping_model.Mapping;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This adapter class receives a sourceObject and a destinationObject or
 * destinationReference. Creates a map of methods of sourceObject and another
 * one for destination object and a mapper object from the mapper project. it
 * then adds "get" and capitilize the first letter of maps source attribute and
 * adds "set" and capitilize the first letter of maps destination attribute.
 * Then it invokes all the setter method of the destination object and passes
 * all the getter methods of the source object for assigning the values in
 * destination object. As a technology it uses java reflection api.
 *
 * @author BJIT / Md.Omour Faruq
 * @param <SourceType>
 * @param <DestinationType>
 */
public final class ObjectAdapter<SourceType, DestinationType> {

    private SourceType sourceObject;
    private DestinationType destinationObject;
    private Mapping mapper;
    HashMap<String, Class<?>[]> sourceMethodParameterMap;
    HashMap<String, Class<?>[]> destinationMethodParameterMap;
    static final Logger OBJECT_ADAPTER_LOGGER = Logger.getLogger(ObjectAdapter.class.getName());

    /**
     * Here sourceObject is the object from where the data will be received.
     *
     * destinationReference is the reference of the destination class. Here from
     * the destinationReference class it creates an destinationObject classes
     * instance.
     *
     * Mapper is the map on basis of which the data assignment occurs from the
     * sourceObject and the distanceObject.
     *
     *
     * @param sourceObject
     * @param destinationReference
     * @param mapper
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    public ObjectAdapter(SourceType sourceObject, Class<DestinationType> destinationReference, Mapping mapper) throws InstantiationException, IllegalAccessException {
        try {
            OBJECT_ADAPTER_LOGGER.log(Level.INFO, "Initializing ObjectAdapter");
            OBJECT_ADAPTER_LOGGER.log(Level.INFO, "Source object is : {0}", sourceObject.getClass().getCanonicalName());
            this.setSourceObject(sourceObject);
            OBJECT_ADAPTER_LOGGER.log(Level.INFO, "Destination reference is : {0}", destinationReference.getClass().getCanonicalName());
            this.setDestinationObject(initializeDestinationObject(destinationReference));
            OBJECT_ADAPTER_LOGGER.log(Level.INFO, "Setting xml Mapper to the Adapter");
            this.setMapper(mapper);
            OBJECT_ADAPTER_LOGGER.log(Level.INFO, "Initialization ObjectAdapter has been successfully completed");
        } catch (InstantiationException | IllegalAccessException exp) {
            exp.printStackTrace(System.out);
            OBJECT_ADAPTER_LOGGER.log(Level.SEVERE, "Initialization ObjectAdapter has been failed");
            throw exp;
        }

    }

    /**
     * Here sourceObject is the object from where the data will be received.
     *
     * destinationObject is object whose properties will be assigned from the
     * sourceObjects property.
     *
     * Mapper is the map on basis of which the data assignment occurs from the
     * sourceObject and the distanceObject.
     *
     * @param sourceObject
     * @param destinationObject
     * @param mapper
     */
    public ObjectAdapter(SourceType sourceObject, DestinationType destinationObject, Mapping mapper) {
        OBJECT_ADAPTER_LOGGER.log(Level.INFO, "Initializing ObjectAdapter");
        OBJECT_ADAPTER_LOGGER.log(Level.INFO, "Source object is : {0}", sourceObject.getClass().getCanonicalName());
        this.setSourceObject(sourceObject);
        OBJECT_ADAPTER_LOGGER.log(Level.INFO, "Destination object is : {0}", destinationObject.getClass().getCanonicalName());
        this.setDestinationObject(destinationObject);
        OBJECT_ADAPTER_LOGGER.log(Level.INFO, "Setting xml Mapper to the Adapter");
        this.setMapper(mapper);
        OBJECT_ADAPTER_LOGGER.log(Level.INFO, "Initialization ObjectAdapter has been successfully completed");
    }

    private DestinationType initializeDestinationObject(Class<DestinationType> destinationReference) throws InstantiationException, IllegalAccessException {
        try {
            OBJECT_ADAPTER_LOGGER.log(Level.INFO, "Initializing destinationObject. Destination object is {0}", destinationReference.getCanonicalName());
            DestinationType newInstance = destinationReference.newInstance();
            OBJECT_ADAPTER_LOGGER.log(Level.INFO, "Initialization destinationObject has been successfully completed");
            return newInstance;
        } catch (InstantiationException | IllegalAccessException exp) {
            exp.printStackTrace(System.out);
            OBJECT_ADAPTER_LOGGER.log(Level.SEVERE, "Initialization destinationObject has been failed. {0}", exp.getMessage());
            throw exp;
        }
    }

    /**
     * Converts the source and destination object into two different maps where
     * the keys are the names of the methods of the source and destination
     * object and the values are also the names of the methods of corresponding
     * objects. After that it calls the process of assigning data into
     * destination object.
     *
     * @return DestinationType
     */
    public DestinationType getPopulatedDestinationObject() {
        try {
            ConvertAClassWithMethodsNameToAMap convertAClassWithMethodsNameToAMap = new ConvertAClassWithMethodsNameToAMap();
            this.setSourceMethodParameterMap(convertAClassWithMethodsNameToAMap.createMap(this.getSourceObject()));
            this.setDestinationMethodParameterMap(convertAClassWithMethodsNameToAMap.createMap(this.getDestinationObject()));

            populateDestinationObject();
        } catch (NullPointerException exp) {
            exp.printStackTrace(System.out);
        }
        return this.getDestinationObject();
    }

    /**
     * In this section the system iterates through the mapper. For all mapper
     * attributes and relationships it calls a method with source name (getter
     * method) and the destination name (setter method)
     */
    private void populateDestinationObject() {
        OBJECT_ADAPTER_LOGGER.log(Level.INFO, "Attributes are searching for mapper attribute");
        this.getMapper().getXmlMapElementObjects().getXmlMapElementObject().forEach(object -> {
            if (isNullOrEmptyOrEqual(object.getType(), "Object Type")) {
                object.getXmlMapElementAttributes().getXmlMapElementAttribute().forEach(attribute -> {
                    try {
                        populate(attribute.getSourceName(), attribute.getDestinationName());
                    } catch (NoSuchMethodException | IllegalAccessException | IllegalArgumentException | InvocationTargetException exp) {
                        exp.printStackTrace(System.out);
                        OBJECT_ADAPTER_LOGGER.log(Level.SEVERE, "Data assignment has been failed for source {0} and destiniation {1}", new Object[]{attribute.getSourceName(), attribute.getDestinationName()});
                    }
                });
            }
        });

        OBJECT_ADAPTER_LOGGER.log(Level.INFO, "Relationships are searching for mapper relationships");
        this.getMapper().getXmlMapElementBOMRelationships().getXmlMapElementBOMRelationship().forEach(relationship -> {
            if (isNullOrEmptyOrEqual(relationship.getName(), "Relationship Name")) {
                relationship.getXmlMapElementAttributes().getXmlMapElementAttribute().forEach(attribute -> {
                    try {
                        populate(attribute.getSourceName(), attribute.getDestinationName());
                    } catch (NoSuchMethodException | IllegalAccessException | IllegalArgumentException | InvocationTargetException exp) {
                        exp.printStackTrace(System.out);
                        OBJECT_ADAPTER_LOGGER.log(Level.SEVERE, "Data assignment has been failed for source {0} and destiniation {1}", new Object [] {attribute.getSourceName(), attribute.getDestinationName()});
                    }
                });
            }
        });
    }

    private Boolean isNullOrEmptyOrEqual(String typeOrName, String message) throws NullPointerException {
        if (typeOrName == null || typeOrName.equals("")) {
            throw new NullPointerException(message + " can not be null or empty");
        }

        return this.sourceObject.getClass().getSimpleName().equals(typeOrName);
    }

    /**
     * Adds get before source and capitalize the first character and adds set
     * before destination and capitalize the first character. Then invokes the
     * destinationObjects setter method and sourceObjects getter method. Getter
     * methods is used as the parameter of setter method. Here this operation
     * operates Java reflection API.
     *
     * @param source
     * @param destination
     * @throws NoSuchMethodException
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     * @throws InvocationTargetException
     */
    private void populate(String source, String destination) throws NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        try {
            OBJECT_ADAPTER_LOGGER.log(Level.INFO, "Adding set and get before destination method and source method");
            destination = "set" + this.capitalizeFirstLetter(destination);
            source = "get" + this.capitalizeFirstLetter(source);

            OBJECT_ADAPTER_LOGGER.log(Level.INFO, "Invoking source method");
            Method sourceMethod = this.getSourceObject().getClass().getDeclaredMethod(source, this.getSourceMethodParameterMap().get(source));
            sourceMethod.setAccessible(true);

            OBJECT_ADAPTER_LOGGER.log(Level.INFO, "Invoking destination method");
            Method destinationMethod = this.getDestinationObject().getClass().getDeclaredMethod(destination, this.getDestinationMethodParameterMap().get(destination));
            destinationMethod.setAccessible(true);

            OBJECT_ADAPTER_LOGGER.log(Level.INFO, "Assigning source value to destnation property");
            destinationMethod.invoke(this.getDestinationObject(), sourceMethod.invoke(this.getSourceObject()));
        } catch (NoSuchMethodException | IllegalAccessException | IllegalArgumentException | InvocationTargetException exp) {
            exp.printStackTrace(System.out);
            OBJECT_ADAPTER_LOGGER.log(Level.SEVERE, "Value of {0} which is source, couldn't be assigned to {1} which is destination", new Object [] {source, destination});
            throw exp;
        }
    }

    private String capitalizeFirstLetter(String value) {
        return value.substring(0, 1).toUpperCase() + value.substring(1);
    }

    //region getters and setters
    private SourceType getSourceObject() {
        return sourceObject;
    }

    private void setSourceObject(SourceType sourceObject) {
        this.sourceObject = sourceObject;
    }

    private DestinationType getDestinationObject() {
        return destinationObject;
    }

    private void setDestinationObject(DestinationType destinationObject) {
        this.destinationObject = destinationObject;
    }

    private Mapping getMapper() {
        return mapper;
    }

    private void setMapper(Mapping mapper) {
        this.mapper = mapper;
    }

    private HashMap<String, Class<?>[]> getSourceMethodParameterMap() {
        return sourceMethodParameterMap;
    }

    private void setSourceMethodParameterMap(HashMap<String, Class<?>[]> sourceMethodParameterMap) {
        this.sourceMethodParameterMap = sourceMethodParameterMap;
    }

    private HashMap<String, Class<?>[]> getDestinationMethodParameterMap() {
        return destinationMethodParameterMap;
    }

    private void setDestinationMethodParameterMap(HashMap<String, Class<?>[]> destinationMethodParameterMap) {
        this.destinationMethodParameterMap = destinationMethodParameterMap;
    }
    //endregion
}
