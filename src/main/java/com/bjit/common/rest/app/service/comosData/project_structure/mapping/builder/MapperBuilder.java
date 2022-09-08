package com.bjit.common.rest.app.service.comosData.project_structure.mapping.builder;

//import com.bjit.project_structure.mapping.factory.MapperFactory;
//import com.bjit.project_structure.mapping.mapping_model.Mapping;
//import com.bjit.project_structure.mapping.processors.IMapper;
//import com.bjit.project_structure.mapping.processors.MappingXML;
import com.bjit.common.rest.app.service.comosData.project_structure.mapping.factory.MapperFactory;
import com.bjit.common.rest.app.service.comosData.project_structure.mapping.mapping_model.Mapping;
import com.bjit.common.rest.app.service.comosData.project_structure.mapping.processors.IMapper;
import com.bjit.common.rest.app.service.comosData.project_structure.mapping.processors.MappingXML;

import javax.xml.bind.JAXBException;

/**
 * Builds the mapper instance by accepting the the type of map. It can be an
 * <b>XML</b> or a <b>JSON</b> or a <b>PROP</b> type
 *
 * @author BJIT / Md.Omour Faruq
 * @param <T>
 */
public class MapperBuilder<T> {

    static final org.apache.log4j.Logger MAPPER_BUILDER_LOGGER = org.apache.log4j.Logger.getLogger(MapperBuilder.class.getName());
    private static MapperBuilder mappingBuilder;

    private IMapper<T> mapper, xmlMapper;

    public static final String XML = "XML";

    /*private MapperBuilder() {

    }*/
    /**
     * This method creates an instance of the <b>XML</b> or <b>JSON</b> or
     * <b>Property</b> mapper
     *
     * @throws Exception
     */
    private void getXmlMapperInstance() throws Exception {
        MAPPER_BUILDER_LOGGER.info("Initializing XML mapper");
        if (this.xmlMapper == null) {
            try {
                this.xmlMapper = this.getXmlMapper((Class<T>) Mapping.class).mapper;
                MAPPER_BUILDER_LOGGER.info("XML mapper initialized successfully");
            } catch (Exception exp) {
//                exp.printStackTrace(System.out);
                MAPPER_BUILDER_LOGGER.error("Directory name is " + exp.getMessage());
                throw exp;
            }
        }
        this.mapper = this.xmlMapper;
    }
    
        /**
     * This method creates an instance of the <b>XML</b> or <b>JSON</b> or
     * <b>Property</b> mapper
     *
     * @throws Exception
     */
    private void getXmlMapperInstance(String fileDirectory) throws Exception {
        MAPPER_BUILDER_LOGGER.info("Initializing XML mapper");
        if (this.xmlMapper == null) {
            try {
                this.xmlMapper = this.getXmlMapper((Class<T>) Mapping.class, fileDirectory).mapper;
                MAPPER_BUILDER_LOGGER.info("XML mapper initialized successfully");
            } catch (Exception exp) {
//                exp.printStackTrace(System.out);
                MAPPER_BUILDER_LOGGER.error("Directory name is " + exp.getMessage());
                throw exp;
            }
        }
        this.mapper = this.xmlMapper;
    }

    /**
     * Here type is the mapper type type = <code><b>"XML"</b></code> type =
     * <code><b>"JSON"</b></code> type = <code><b>"PROP"</b></code>
     *
     * @param type
     * @return
     * @throws Exception
     */
    public Mapping getMapper(String type) throws Exception {
        MAPPER_BUILDER_LOGGER.info("The mapper is getting ready for " + type);
        if (type != null || !"".equals(type)) {
            if (XML.equalsIgnoreCase(type)) {
                getXmlMapperInstance();
                MAPPER_BUILDER_LOGGER.info(type.toLowerCase() + " Mapper has been readied");
                return (Mapping) this.getData();
            }
            throw new Exception("Wrong type name");
        } else {
            throw new NullPointerException("Type name can not be empty or null");
        }
    }

    /**
     * Here type is the mapper type type = <code><b>"XML"</b></code> type =
     * <code><b>"JSON"</b></code> type = <code><b>"PROP"</b></code>
     *
     * @param type
     * @param fileDirectory
     * @return
     * @throws Exception
     */
    public Mapping getMapper(String type, String fileDirectory) throws Exception {
        MAPPER_BUILDER_LOGGER.info("The mapper is getting ready for " + type);
        if (type != null || !"".equals(type)) {
            if (XML.equalsIgnoreCase(type)) {
                getXmlMapperInstance(fileDirectory);
                MAPPER_BUILDER_LOGGER.info(type.toLowerCase() + " Mapper has been readied");
                return (Mapping) this.getData();
            }
            throw new Exception("Wrong type name");
        } else {
            throw new NullPointerException("Type name can not be empty or null");
        }
    }

    /**
     * Here type is the mapper type type = <code><b>"XML"</b></code> type =
     * <code><b>"JSON"</b></code> type = <code><b>"PROP"</b></code>
     *
     * and isRootElement is
     *
     * isRootElement = <code><b>true</b></code> to add properties as the last
     * element isRootElement = <code><b>false</b></code> to add properties as
     * normal element
     *
     * @param type
     * @param isRootElement
     * @return
     * @throws Exception
     */
    public Mapping getMapper(String type, Boolean isRootElement) throws Exception {
        MAPPER_BUILDER_LOGGER.info("The mapper is getting ready for " + type);
        if (isRootElement) {
            if (type != null || !"".equals(type)) {
                if (XML.equalsIgnoreCase(type)) {
                    getXmlMapperInstance();
                    MAPPER_BUILDER_LOGGER.info(type.toLowerCase() + " Mapper has been readied");
                    return (Mapping) this.getData();
                }
                throw new Exception("Wrong type name");
            } else {
                throw new NullPointerException("Type name can not be empty or null");
            }
        } else {
            return getMapper(type);
        }
    }

    /**
     * Here type is type = <code><b>"XML"</b></code> type =
     * <code><b>"JSON"</b></code> type = <code><b>"PROP"</b></code>
     *
     * data is naturally a map form of data from external source. It may be a
     * map object of representation of <code><b>xml</b></code> or
     * <code><b>json</b></code> or <code><b>prop</b></code>. It then converts
     * the given map into targeted type object. For example if data is a map of
     * an XML model object like our mapper model and the type is JSON then the
     * XML model map object will be converted into a JSON model map object.
     *
     * @param type
     * @param data
     * @return
     * @throws Exception
     */
    public Mapping setMapper(String type, T data) throws Exception {
        MAPPER_BUILDER_LOGGER.info("The mapper is getting ready for " + type);
        if (type != null || !"".equals(type)) {
            if (XML.equalsIgnoreCase(type)) {
                getXmlMapperInstance();
                this.setData(data);
                MAPPER_BUILDER_LOGGER.info(type.toLowerCase() + " Mapper has been readied");
                return null;
            }
            throw new Exception("Wrong type name");
        } else {
            throw new NullPointerException("Type name can not be empty or null");
        }
    }

    /**
     * Here type is type = <code><b>"XML"</b></code> type =
     * <code><b>"JSON"</b></code> type = <code><b>"PROP"</b></code>
     *
     * data is naturally a map form of data from external source. It may be a
     * map object of representation of <code><b>xml</b></code> or
     * <code><b>json</b></code> or <code><b>prop</b></code>. It then converts
     * the given map into targeted type object. For example if data is a map of
     * an XML model object like our mapper model and the type is JSON then the
     * XML model map object will be converted into a JSON model map object.
     *
     * isRootElement = <code><b>true</b></code> to add properties as the last
     * element isRootElement = <code><b>false</b></code> to add properties as
     * normal element
     *
     * @param type
     * @param data
     * @param isRootElement
     * @return
     * @throws Exception
     */
    public Mapping setMapper(String type, T data, Boolean isRootElement) throws Exception {
        MAPPER_BUILDER_LOGGER.info("The mapper is getting ready for " + type);
        if (isRootElement) {
            if (type != null || !"".equals(type)) {
                if (XML.equalsIgnoreCase(type)) {
                    getXmlMapperInstance();
                    this.setData(data);
                    MAPPER_BUILDER_LOGGER.info(type.toLowerCase() + " Mapper has been readied");
                    return null;
                }
                throw new Exception("Wrong type name");
            } else {
                throw new NullPointerException("Type name can not be empty or null");
            }
        } else {
            return setMapper(type, data);
        }
    }

    /**
     * Returns map with XML capabilites on the basis of JAXB parser
     *
     * @param classReference
     * @return
     * @throws Exception
     */
    public MapperBuilder getXmlMapper(Class<T> classReference) throws Exception {
        setMapper(MapperFactory.getInstnace(MappingXML.class, classReference));
        return this;
    }
    
        /**
     * Returns map with XML capabilites on the basis of JAXB parser
     *
     * @param classReference
     * @param fileDirectory
     * @return
     * @throws Exception
     */
    public MapperBuilder getXmlMapper(Class<T> classReference, String fileDirectory) throws Exception {
        setMapper(MapperFactory.getInstnace(MappingXML.class, classReference, fileDirectory));
        return this;
    }

    //endregion
    //region Data Getter
    /**
     * Returns a map object which may be a JSON or an XML or a Property model
     * map
     *
     * @return T
     * @throws Exception
     */
    public T getData() throws Exception {
        try {
            final T object = mapper.getObjectFromString();
            return object;
        } catch (JAXBException | NullPointerException jaxExp) {
//            jaxExp.printStackTrace(System.out);

            try {
                final T object = mapper.getObject();
                return object;
            } catch (Exception exp) {
//                exp.printStackTrace(System.out);
                throw exp;
            }
        }
    }

    //endregion
    //region Data Setters
    /**
     * data is a model map object.
     *
     * @param data
     * @return
     * @throws Exception
     */
    public MapperBuilder setData(T data) throws Exception {
        try {
            mapper.setObject(data);
            return this;
        } catch (Exception exp) {
//            exp.printStackTrace(System.out);
            throw exp;
        }
    }

    public MapperBuilder setData() throws Exception {
        MAPPER_BUILDER_LOGGER.error("Data setting with empty parametere is not working");
        throw new Exception("Not Implemented");
    }
    //endregion

    //region Builders Instance
    /**
     * Returns the existing mapBuilder. If there is no mapBuilder then it
     * creates one and returns that. It uses singleton pattern to create and
     * return object.
     *
     * @return MapperBuilder
     */
    public static MapperBuilder getInstance() {
        if (mappingBuilder == null) {
            setMappingBuilder(new MapperBuilder());
        }
        return getMappingBuilder();
    }
    //endregion

    //region Getters Setters
    public static MapperBuilder getMappingBuilder() {
        return mappingBuilder;
    }

    public static void setMappingBuilder(MapperBuilder mappingBuilder) {
        MapperBuilder.mappingBuilder = mappingBuilder;
    }

    public IMapper<T> getJsonMapper() {
        return this.mapper;
    }

    public void setMapper(IMapper<T> mapper) {
        this.mapper = mapper;
    }

    public void setMapper(IMapper<T> mapper, String fileDirectory) {
        this.mapper = mapper;
    }
    //endregion
}
