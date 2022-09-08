/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.app.service.mail.mastershipChange;

import com.bjit.common.rest.app.service.enovia_pdm.models.Message;
import com.bjit.common.rest.app.service.enovia_pdm.models.ResponseModel;
import com.bjit.common.rest.app.service.model.rnp.RnPModel;
import com.bjit.common.rest.app.service.model.tnr.TNR;
import com.bjit.ewc18x.utils.PropertyReader;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;

/**
 *
 * @author BJIT
 */
//@Lazy
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class MCMailParamerterBuilder {

    private static final Logger MAIL_PARAMETER_BUILDER_LOGGER = Logger.getLogger(MCMailParamerterBuilder.class);

//    @Lazy
    @Autowired
    MCMailParameters mcMailParameters;

    public MCMailParameters prepareMailParameter(HashMap<String, List<ResponseModel>> pdmResponse) throws MalformedURLException {
        String environmentName = getEnvironmentName();
        mcMailParameters.setEnvName(environmentName);
        mcMailParameters.setMailSubject(PropertyReader.getProperty("mastership.change.mail.subject"));
        mcMailParameters.setErrorMessageFromPdm(prepareErrorData(pdmResponse));
        return mcMailParameters;
    }

    private String getEnvironmentName() {
        String environmentName = PropertyReader.getEnvironmentName();
        String[] envNameParts = environmentName.split("_");
        return envNameParts.length > 1 ? envNameParts[1] : envNameParts[0];
    }

    private String prepareErrorData(HashMap<String, List<ResponseModel>> pdmResponses) {
        List<ResponseModel> listOfUnsuccessfulResponses = pdmResponses.get("unsuccessful");
        if (!listOfUnsuccessfulResponses.isEmpty()) {

            StringBuilder tableBuilder = new StringBuilder();
            listOfUnsuccessfulResponses.forEach((ResponseModel responseModel) -> {
                List<Message> messages = responseModel.getMessages();
                messages.forEach((Message message) -> {
                    TNR tnr = message.getTnr();
                    String errorMessage = message.getErrorMessage();

                    tableBuilder
                            .append("<tr>")
                            .append("<td>")
                            .append(tnr.getName())
                            .append("</td>");
                    tableBuilder
                            .append("<td>")
                            .append(tnr.getRevision())
                            .append("</td>");
                    tableBuilder
                            .append("<td>")
                            .append(errorMessage)
                            .append("</td>")
                            .append("</tr>");
                });
            });

            String htmlTableRows = tableBuilder.toString();

            return htmlTableRows;
        } else {
            MAIL_PARAMETER_BUILDER_LOGGER.info("No mail has sent as pdm sent successful status for all items");
            return null;
        }
    }
}
