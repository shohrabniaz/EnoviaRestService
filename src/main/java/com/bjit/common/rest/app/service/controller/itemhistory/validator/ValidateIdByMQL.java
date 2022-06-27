package com.bjit.common.rest.app.service.controller.itemhistory.validator;

import com.matrixone.apps.domain.util.MqlUtil;
import matrix.db.Context;
import matrix.db.MQLCommand;
import matrix.util.MatrixException;
import org.apache.log4j.Logger;

import java.text.MessageFormat;

public class ValidateIdByMQL implements ValidateId {
    private static final Logger LOGGER = Logger.getLogger(ValidateIdByMQL.class);

    private Context context;
    private String mqlFormat = "print bus {0}  select exists dump |";

    public ValidateIdByMQL(Context context) {
        this.context = context;
    }

    @Override
    public boolean isValid(String id) throws Exception {
        String query = MessageFormat.format(mqlFormat, id);
        String queryResult = getQueryResult(query, this.context);
        queryResult = queryResult.toLowerCase();
        return Boolean.parseBoolean(queryResult);
    }

    private static String getQueryResult(String query, Context context) throws Exception {
        MQLCommand objMQL = new MQLCommand();
        try {
            objMQL.open(context);
            String result = MqlUtil.mqlCommand(context, objMQL, query);
            LOGGER.info("Query: " + query);
            LOGGER.info("Result: " + result);
            // objMQL.close(context);
            return result;
        } catch (MatrixException e) {
            LOGGER.error("Matrix Exception occured at during query execution: " + e.getMessage());
            LOGGER.error("Query String : " + query);
            objMQL.close(context);
            throw e;
        }
    }
}
