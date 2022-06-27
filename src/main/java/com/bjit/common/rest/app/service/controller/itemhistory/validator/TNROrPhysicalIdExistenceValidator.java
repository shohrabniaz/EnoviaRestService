package com.bjit.common.rest.app.service.controller.itemhistory.validator;

import com.bjit.common.rest.app.service.controller.itemhistory.model.Data;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TNROrPhysicalIdExistenceValidator extends Validator {
    private final String NOT_EXIST = "No Physical id or TNR is provided. DescId: {0}";

    @Override
    protected List<String> _validate(List<Data> dataList) {
        List<String> errors = new ArrayList<>();
        Map<Integer, Integer> counts = new HashMap<>();

        for (Data data : dataList) {
            boolean isPIDEmpty = data.getItemId() == null || data.getItemId().isEmpty();
            boolean isTNREmpty = data.getTNR() == null;
            if (isPIDEmpty && isTNREmpty) {
                errors.add(MessageFormat.format(NOT_EXIST, data.getDescId()));
                continue;
            }
        }
        return errors;
    }
}
