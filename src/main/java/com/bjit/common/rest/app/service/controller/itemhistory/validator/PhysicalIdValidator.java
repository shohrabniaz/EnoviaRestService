package com.bjit.common.rest.app.service.controller.itemhistory.validator;

import com.bjit.common.rest.app.service.controller.itemhistory.model.Data;
import org.apache.log4j.Logger;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

public class PhysicalIdValidator extends Validator {
    private static final Logger LOGGER = Logger.getLogger(PhysicalIdValidator.class);
    private ValidateId validation;

    public PhysicalIdValidator(ValidateId validation) {
        this.validation = validation;
    }

    @Override
    protected List<String> _validate(List<Data> dataList) {
        LOGGER.debug("+++++++++++++  PhysicalIdValidator:_validate() +++++++++++++++");
        List<String> errors = new ArrayList<>();
        String errorMessageFormat = "{0} is not valid.";

        for (Data data : dataList) {
            LOGGER.debug("data:" + data);
            String id = data.getItemId();
            LOGGER.debug("item id to validate:" + id);
            if (id != null && !id.isEmpty()) {
                try {
                    boolean isValid = this.validation.isValid(id);
                    LOGGER.debug("IS item id valid:" + isValid);
                    if (!isValid) {
                        errors.add(MessageFormat.format(errorMessageFormat, id));
                    }
                } catch (Exception exception) {
                    LOGGER.debug(id + "is not a valid item id");
                    LOGGER.error(exception.getMessage());
                    errors.add(MessageFormat.format(errorMessageFormat, id));
                }
            }
        }
        LOGGER.debug(errors.toString());
        return errors;
    }
}