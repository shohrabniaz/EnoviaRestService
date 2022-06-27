package com.bjit.common.rest.app.service.controller.itemhistory.validator;

import com.bjit.common.rest.app.service.controller.itemhistory.model.Data;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DescIdValidator extends Validator {
    private final String DUPLICATE = "DescId {0} found multiple time. DescId must be unique and any Integer Number.";
    private final String NOT_EXIST = "No DescId id defined for Item: {0}";

    @Override
    protected List<String> _validate(List<Data> dataList) {
        List<String> errors = new ArrayList<>();
        Map<Integer, Integer> counts = new HashMap<>();

        for (Data data : dataList) {
            if (data.getDescId() == null) {
                if (data.getItemId() != null || !data.getItemId().isEmpty()) {
                    errors.add(MessageFormat.format(NOT_EXIST, data.getItemId()));
                } else if (data.getTNR() != null) {
                    errors.add(MessageFormat.format(NOT_EXIST, data.getTNR()));
                }
                continue;
            }
            int count = 1;
            if (counts.containsKey(data.getDescId())) {
                count += counts.get(data.getDescId());
            }
            counts.put(data.getDescId(), count);
        }

        for (Integer key : counts.keySet()) {
            if (counts.get(key) > 1) {
                errors.add(MessageFormat.format(DUPLICATE, key));
            }
        }

        return errors;
    }
}
