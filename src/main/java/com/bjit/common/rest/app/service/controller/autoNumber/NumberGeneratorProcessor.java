package com.bjit.common.rest.app.service.controller.autoNumber;

import com.bjit.common.rest.app.service.payload.common_response.Status;
import matrix.db.Context;
import matrix.db.JPO;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class NumberGeneratorProcessor implements INumberGenerator {

    @Autowired
    BeanFactory beanFactory;
    @Value("#{${symbolicType}}")
    private Map<String, String> symbolicType;
    @Value("#{${symbolicPolicy}}")
    private Map<String, String> symbolicPolicy;


    public NumberGeneratorResponse generateAutonumber(NumberGenerationModel numberGenerationModel) {
        String[] initargs = {};
        HashMap jpoParameters = new HashMap();
        jpoParameters.put("symbolicTypeName", symbolicType.get(numberGenerationModel.getType()));
        jpoParameters.put("symbolicPolicyName", symbolicPolicy.get(numberGenerationModel.getType()));

        List<String> generatedNumberList = null;
        List<String> errorList = null;

        for (Integer i = 0; i < numberGenerationModel.getObjectCount(); i++) {
            try {
                String autoName = JPO.invoke(numberGenerationModel.getContext(), "CloneObjectUtil", initargs, "getAutoNameBySymbolicTypePolicyJpo", JPO.packArgs(jpoParameters), String.class);

                generatedNumberList = Optional.ofNullable(generatedNumberList).orElse(new ArrayList<>());
                generatedNumberList.add(autoName);
            } catch (Exception exp) {
                errorList = Optional.ofNullable(errorList).orElse(new ArrayList<>());
                errorList.add(exp.getMessage());
            }
        }

        NumberGeneratorResponse bean = getNumberGeneratorResponse(numberGenerationModel.getType(), generatedNumberList, errorList);
        return bean;
    }

    private NumberGeneratorResponse getNumberGeneratorResponse(String type, List<String> generatedNumberList, List<String> errorList) {
        NumberGeneratorResponse bean = beanFactory.getBean(NumberGeneratorResponse.class);
        bean.setType(type);
        bean.setStatus(Optional.ofNullable(errorList).isEmpty() ? Status.OK : Status.FAILED);
        bean.setName(generatedNumberList);
        bean.setError(errorList);
        return bean;
    }
}
