package com.bjit.common.rest.app.service.comosData.responseHandler;

import com.bjit.common.rest.app.service.comosData.xmlPreparation.model.ResponseModel;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.model.SessionModel;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.model.assembly.AssemblyRequestEnvelope;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.model.common.CommonRequestEnvelope;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.structureProcessor.IPrepareStructure;
import com.bjit.common.rest.app.service.payload.common_response.IResponse;
import com.bjit.common.rest.app.service.payload.common_response.Status;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

@Service
public class HandleResponse {

    @Autowired
    @Qualifier("CustomResponseBuilder")
    IResponse responseBuilder;

    @Autowired
    Environment env;

    @Autowired
    BeanFactory beanFactory;

    @Autowired
    IPrepareStructure prepareComosStructure;

    public String prepareResponse(Object requestData, CommonRequestEnvelope requestEnvelope, int numberOfFilesWritten) {
        String destinationDirectory = env.getProperty("comos.generated.file.directory");

        ResponseModel responseModel = beanFactory.getBean(ResponseModel.class);
        responseModel.setRequestData(requestData);

        String buildResponse;
        if(requestEnvelope.getConnectInStructure()){
            Boolean successfullyCompletedProcess = Boolean.parseBoolean(env.getProperty("deliverable.task.and.logical.item.connect.directly"))
                    ? prepareComosStructure.prepareComosStructure()
                    : prepareComosStructure.prepareDeliverableTaskAndLogicalItemMap(requestData, requestEnvelope.getEmail(), requestEnvelope.getServiceName());
            String responseMessage;
            if(successfullyCompletedProcess){
                responseMessage = "Process completed successfully";
                buildResponse = prepareResponse(responseModel, responseMessage, Status.OK);
            } else{
                responseMessage = "Something went wrong";
                buildResponse = prepareResponse(responseModel, responseMessage, Status.FAILED);
            }
        }
        else{
            String responseMessage = numberOfFilesWritten + " xml files has been generated in "  + destinationDirectory + " directory";
            buildResponse = prepareResponse(responseModel, responseMessage, Status.OK);
        }
        return buildResponse;
    }

    private String prepareResponse(ResponseModel responseModel, String responseMessage, Status status) {
        responseModel.setResponseMassage(responseMessage);
        return responseBuilder.setData(responseModel).setStatus(status).buildResponse();
    }
}
