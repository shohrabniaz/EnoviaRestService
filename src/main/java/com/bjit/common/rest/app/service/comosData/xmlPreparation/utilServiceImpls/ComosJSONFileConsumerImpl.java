package com.bjit.common.rest.app.service.comosData.xmlPreparation.utilServiceImpls;

import com.bjit.common.rest.app.service.comosData.utils.ComosUtilities;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.model.equipment.EquipmentServiceResponse;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.utilServices.IServiceConsumer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Qualifier("JSONFileConsumer")
public class ComosJSONFileConsumerImpl implements IServiceConsumer<EquipmentServiceResponse> {
    @Autowired
    Environment env;

    @Autowired
    ComosUtilities utilities;

    @Override
    public EquipmentServiceResponse getServiceResponse() throws IOException {
        String jsonFileDirectory = env.getProperty("comos.service.response.file.directory");
        String jsonFileExtension = env.getProperty("comos.response.file.extension");
        String jsonRespondedFile = utilities.getRespondedFiles(jsonFileDirectory, jsonFileExtension).get(0);
        return utilities.readFile(jsonRespondedFile);
    }
}
