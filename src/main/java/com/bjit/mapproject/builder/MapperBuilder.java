package com.bjit.mapproject.builder;



import com.bjit.mapproject.factory.MapperFactory;
import com.bjit.mapproject.processors.IMapper;
import com.bjit.mapproject.processors.MappingXML;
import com.bjit.mapproject.xml_mapping_model.Mapping;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXBException;

/**
 * Builds the mapper instance by accepting the the type of map. It can be an
 * <b>XML</b> or a <b>JSON</b> or a <b>PROP</b> type
 *
 * @author BJIT / Md.Omour Faruq
 * @param <T>
 */
public class MapperBuilder<T> {

    static final Logger MAPPER_BUILDER_LOGGER = Logger.getLogger(MapperBuilder.class.getName());
    private static MapperBuilder mappingBuilder;

    private IMapper<T> mapper, xmlMapper;

    public static final String XML = "XML";
    public static final String JSON = "JSON";
    public static final String PROP = "PROP";

    /*private MapperBuilder() {

    }*/

    /**
     * This method creates an instance of the <b>XML</b> or <b>JSON</b> or
     * <b>Property</b> mapper
     *
     * @throws Exception
     */
    private void getXmlMapperInstance() throws Exception {
        MAPPER_BUILDER_LOGGER.log(Level.INFO, "Initializing XML mapper");
        if (this.xmlMapper == null) {
            try {
                this.xmlMapper = this.getXmlMapper((Class<T>) Mapping.class).mapper;
                MAPPER_BUILDER_LOGGER.log(Level.INFO, "XML mapper initialized successfully");
            } catch (Exception exp) {
                exp.printStackTrace(System.out);
                MAPPER_BUILDER_LOGGER.log(Level.SEVERE, "Directory name is {0}", exp.getMessage());
                throw exp;
            }
        }
        this.mapper = this.xmlMapper;
    }

    /**
     * This method creates an instance of the <b>JSON</b> mapper using
     * <b>GSON</b>
     *
     * @throws Exception
     */

    /**
     * This method creates an instance of the <b>JSON</b> mapper using
     * <b>Jackson</b> as <b>Jackson</b> made <b>JSON<b> string can keep the root
     * node.
     *
     * @throws Exception
     */

    /**
     * Here type is the mapper type type = <code><b>"XML"</b></code> type =
     * <code><b>"JSON"</b></code> type = <code><b>"PROP"</b></code>
     *
     * @param type
     * @return
     * @throws Exception
     */
    public Mapping getMapper(String type) throws Exception {
        MAPPER_BUILDER_LOGGER.log(Level.INFO, "The mapper is getting ready for {0}.", type);
        if (type != null || !"".equals(type)) {
            if (XML.equalsIgnoreCase(type)) {
                getXmlMapperInstance();
                MAPPER_BUILDER_LOGGER.log(Level.INFO, "{0}Mapper has been readied.", type.toLowerCase());
                return (Mapping) this.getData();
            }
            else if (PROP.equalsIgnoreCase(type)) {
                MAPPER_BUILDER_LOGGER.log(Level.SEVERE, "There is no parser has been declared for {0}", type);
                throw new Exception("Not Implemented");
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
        MAPPER_BUILDER_LOGGER.log(Level.INFO, "The mapper is getting ready for {0}.", type);
        if (isRootElement) {
            if (type != null || !"".equals(type)) {
                if (XML.equalsIgnoreCase(type)) {
                    getXmlMapperInstance();
                    MAPPER_BUILDER_LOGGER.log(Level.INFO, "{0}Mapper has been readied.", type.toLowerCase());
                    return (Mapping) this.getData();
                }
                else if (PROP.equalsIgnoreCase(type)) {
                    MAPPER_BUILDER_LOGGER.log(Level.SEVERE, "There is no parser has been declared for {0}", type);
                    throw new Exception("Not Implemented");
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
        MAPPER_BUILDER_LOGGER.log(Level.INFO, "The mapper is getting ready for {0}.", type);
        if (type != null || !"".equals(type)) {
            if (XML.equalsIgnoreCase(type)) {
                getXmlMapperInstance();
                this.setData(data);
                MAPPER_BUILDER_LOGGER.log(Level.INFO, "{0}Mapper has been readied.", type.toLowerCase());
                return null;
            }
            else if (PROP.equalsIgnoreCase(type)) {
                MAPPER_BUILDER_LOGGER.log(Level.SEVERE, "There is no parser has been declared for {0}", type);
                throw new Exception("Not Implemented");
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
        MAPPER_BUILDER_LOGGER.log(Level.INFO, "The mapper is getting ready for {0}.", type);
        if (isRootElement) {
            if (type != null || !"".equals(type)) {
                if (XML.equalsIgnoreCase(type)) {
                    getXmlMapperInstance();
                    this.setData(data);
                    MAPPER_BUILDER_LOGGER.log(Level.INFO, "{0}Mapper has been readied.", type.toLowerCase());
                    return null;
                }
                else if (PROP.equalsIgnoreCase(type)) {
                    MAPPER_BUILDER_LOGGER.log(Level.SEVERE, "There is no parser has been declared for {0}", type);
                    throw new Exception("Not Implemented");
                }
                throw new Exception("Wrong type name");
            } else {
                throw new NullPointerException("Type name can not be empty or null");
            }
        } else {
            return setMapper(type, data);
        }
    }

    //region Mapper Instantiator
    /**
     * Returns map with JSON capabilites on the basis of GSON parser
     *
     * @param classReference
     * @return
     * @throws Exception
     */

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
     * Returns map with JSON capabilites on the basis of Jackson parser. Here
     * the parser can keep the root element in JSON string which can not be
     * found in GSON parser.
     *
     * @param classReference
     * @return
     * @throws Exception
     */
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
            jaxExp.printStackTrace(System.out);

            try {
                final T object = mapper.getObject();
                return object;
            } catch (Exception exp) {
                exp.printStackTrace(System.out);
                throw exp;
            }
        }
    }
    /**
     * Will be deleted soon
     *
     * @return
     * @throws Exception
     */
    /*@Deprecated
    public T getData() throws Exception {
        try {
            final T object = mapper.getObject();
            return object;
        } catch (Exception exp) {
            exp.printStackTrace(System.out);
            throw exp;
        }
    }*/

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
            exp.printStackTrace(System.out);
            throw exp;
        }
    }

    public MapperBuilder setData() throws Exception {
        MAPPER_BUILDER_LOGGER.log(Level.SEVERE, "Data setting with empty parametere is not working");
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
    //endregion
}
