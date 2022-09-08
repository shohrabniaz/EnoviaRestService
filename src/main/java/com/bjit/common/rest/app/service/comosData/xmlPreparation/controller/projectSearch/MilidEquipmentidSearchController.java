package com.bjit.common.rest.app.service.comosData.xmlPreparation.controller.projectSearch;

import com.bjit.common.rest.app.service.comosData.exceptions.MilEquipmentIdSearchException;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.model.ProjectSearchRequestData;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.model.projectSearch.ProjectSearchData;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.model.projectSearch.ProjectSearchServiceResponse;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.projectSearchProcessors.factoryImpls.ProjectSearchPreparation;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.projectStructureSearch.ComosStructureSearch;
import com.bjit.common.rest.app.service.payload.common_response.IResponse;
import com.bjit.common.rest.app.service.payload.common_response.Status;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.*;

@Log4j
@RestController
@Validated
@RequestMapping("/comos/v1")
public class MilidEquipmentidSearchController {

    @Autowired
    @Qualifier("CustomResponseBuilder")
    IResponse responseBuilder;

    @Autowired
    @Qualifier("ProjectSearchPreparation")
    ProjectSearchPreparation structurePreparation;

    @Autowired
    ComosStructureSearch comosStructureSearch;

    private final String reponseJsonString = "";

    @PostMapping("/import/milid-equipmentid-search")
    public String milidEquipmentidSearch(@Valid @RequestBody ProjectSearchRequestData requestData) throws IOException, MilEquipmentIdSearchException {

        List<Map> JsonMessages = new ArrayList<>();

        Map<String, String> responseData = new HashMap<String, String>();
        try {

            ProjectSearchServiceResponse serviceResponse = structurePreparation.prepareStructure(requestData);

            List<ProjectSearchData> projectSearchData = serviceResponse.getData();

            projectSearchData.forEach((ProjectSearchData element) -> {
                responseData.put("CompassId", element.getCompassId());
                responseData.put("millId", element.getMillId());
                responseData.put("equipmentId", element.getEquipmentId());
                JsonMessages.add(responseData);

            });

            String buildResponse = responseBuilder.setData(JsonMessages).setStatus(Status.OK).buildResponse();
            return buildResponse;

        } catch (Exception ex) {

            Pattern pattern = Pattern.compile(".*MilEquipmentIdSearchException: (.*).*");

            Matcher matcher = pattern.matcher(ex.getMessage());
            String errorMessage="";
            while (matcher.find()) {
                errorMessage=matcher.group(1);
            }

            throw new MilEquipmentIdSearchException(errorMessage, requestData);
        }
    }

}
