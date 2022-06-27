package com.bjit.common.rest.app.service.comosData.xmlPreparation.projectStructureSearch;

import com.bjit.common.rest.app.service.comosData.xmlPreparation.model.projectStructure.search.Project;
import com.bjit.common.rest.app.service.context.CreateContext;
import com.bjit.common.rest.app.service.utilities.NullOrEmptyChecker;
import com.bjit.mapper.mapproject.util.Constants;
import matrix.db.Context;
import matrix.util.MatrixException;

public abstract class AStructureSearch {
    private static final org.apache.log4j.Logger LOGGER = org.apache.log4j.Logger.getLogger(AStructureSearch.class);
    Context context;

    //Connecting Context
    public Context connectContext(String user, String pass) {
        if (NullOrEmptyChecker.isNullOrEmpty(user) || NullOrEmptyChecker.isNullOrEmpty(pass)) {
            try {
                CreateContext createContext = new CreateContext();
                context = createContext.getAdminContext();
                if (!context.isConnected()) {
                    LOGGER.error(Constants.CONTEXT_EXCEPTION);
                    throw new Exception(Constants.CONTEXT_EXCEPTION);
                }
            } catch (Exception exp) {
                LOGGER.error(Constants.CONTEXT_EXCEPTION);
            }
        } else {
            try {
                CreateContext createContext = new CreateContext();
                context = createContext.getContext(user, pass);
                if (!context.isConnected()) {
                    LOGGER.error(Constants.CONTEXT_EXCEPTION);
                    throw new Exception(Constants.CONTEXT_EXCEPTION);
                }
            } catch (Exception exp) {
                LOGGER.error(Constants.CONTEXT_EXCEPTION);
            }
        }
        return context;
    }

    //Get objectId by compassId
    abstract String getObjectId(Context context, String compassId) throws MatrixException;

    //Get project structure by objectId
    abstract Project getStructure(Context context, String objectId);
}

