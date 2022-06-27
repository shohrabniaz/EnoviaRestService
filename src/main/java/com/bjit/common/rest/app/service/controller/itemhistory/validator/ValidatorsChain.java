package com.bjit.common.rest.app.service.controller.itemhistory.validator;

import matrix.db.Context;

public class ValidatorsChain {
    public static Validator getChainOfValidators(Context context) {
        //instantiate validators
        Validator descIdValidator = new DescIdValidator();

        ValidateId validateId = new ValidateIdByMQL(context);
        Validator phyIdValidator = new PhysicalIdValidator(validateId);

        //chained validators
        descIdValidator.setNextValidator(phyIdValidator);

        return descIdValidator;
    }
}
