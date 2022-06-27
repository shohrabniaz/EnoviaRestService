package com.bjit.common.rest.app.service.controller.export.report.single_level.provider;

import com.bjit.common.rest.app.service.context.CreateContext;
import com.bjit.common.rest.app.service.utilities.NullOrEmptyChecker;
import com.bjit.ewc18x.utils.PropertyReader;
import matrix.db.Context;
import org.apache.log4j.Logger;

/**
 *
 * @author BJIT
 */
public class ContextProvider {
    private static final Logger CONTEXT_PROVIDER_LOGGER = Logger.getLogger(ContextProvider.class);
    
    public static synchronized Context provideContext() throws Exception {
        try {
            Context context = null;
            CreateContext createContext = new CreateContext();
            context = createContext.getAdminContext();
            
            if (!context.isConnected()) {
                throw new Exception(PropertyReader.getProperty("report.root.context.connect.error"));
            }
            if(NullOrEmptyChecker.isNull(context)) {
                CONTEXT_PROVIDER_LOGGER.info(PropertyReader.getProperty("report.root.context.create.error"));
                throw new Exception(PropertyReader.getProperty("report.root.context.create.error"));
            }
            return context;
        } catch (Exception e) {
            CONTEXT_PROVIDER_LOGGER.info(PropertyReader.getProperty("report.root.context.create.error"));
            throw e;
        }
    }
}
