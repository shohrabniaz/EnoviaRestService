package com.bjit.common.rest.app.service.comosData.xmlPreparation.equipmentProceessors;

import com.bjit.common.rest.app.service.comosData.xmlPreparation.equipmentProceessors.factoryServices.IComosDataProcessor;
import org.springframework.stereotype.Component;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.model.comosxml.RFLP;

@Component
public class ComosProcessor implements IComosDataProcessor {
    @Override    
    public RFLP processData(String jsonData) {
        return null;
    }

//    private ComosServiceResponse getServiceResponseModel(String jsonString) {
//        Gson gson = new Gson();
//        ComosServiceResponse serviceResponse = gson.fromJson(jsonString, ComosServiceResponse.class);
//
//        Child comosParent = serviceResponse.getData().getComosModel();
//        comosParent.setSequence(sequencedData);
//        setSequence(comosParent);
//
//        return serviceResponse;
//    }
}
