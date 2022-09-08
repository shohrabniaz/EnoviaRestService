package com.bjit.mapproject.factory;


import com.bjit.mapproject.processors.IMapper;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author BJIT / Md.Omour Faruq
 */
public class MapperFactory {

    static final Logger MAPPER_FACTORY_LOGGER = Logger.getLogger(MapperFactory.class.getName());

    public static <T extends IMapper, K> T getInstnace(Class<T> type, Class<K> mapObject) throws Exception {
        try {
            try {
                MAPPER_FACTORY_LOGGER.log(Level.INFO, "Instantiating {0}", type.getCanonicalName());
                final T newInstance = type.newInstance();
                newInstance.__init__(mapObject);
                return newInstance;
            } catch (Exception exp) {
                exp.printStackTrace(System.out);
                MAPPER_FACTORY_LOGGER.log(Level.SEVERE, "Initialization of {0} has been failed", type.getCanonicalName());
                MAPPER_FACTORY_LOGGER.log(Level.SEVERE, "Error occured :{0}. ", exp.getMessage());
                throw exp;
            }
        } catch (IllegalAccessException | InstantiationException exp) {
            exp.printStackTrace(System.out);
            MAPPER_FACTORY_LOGGER.log(Level.SEVERE, "Initialization of {0} has been failed", type.getCanonicalName());
            MAPPER_FACTORY_LOGGER.log(Level.SEVERE, "Error occured :{0}. ", exp.getMessage());
            throw exp;
        }
    }
}
