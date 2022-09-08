package com.bjit.common.rest.app.service.comosData.xmlPreparation.controller.projectStructure;

import com.bjit.common.rest.app.service.aspects.annotations.LogExecutionTime;
import com.bjit.common.rest.app.service.comosData.exceptions.ProjectStructureException;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.model.ProjectStructureRequestData;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.model.projectStructure.ProjectStructureRequestEnvelope;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.model.projectStructure.ProjectStructureServiceResponse;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.model.projectStructure.search.AppResponse;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.projectStructureSearch.ComosStructureSearch;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.utilServices.IStructurePreparation;
import com.bjit.common.rest.app.service.payload.common_response.IResponse;
import com.bjit.common.rest.app.service.payload.common_response.Status;
import com.bjit.common.rest.app.service.utilities.NullOrEmptyChecker;
import com.bjit.common.rest.item_bom_import.xml_mapping_model.ResponseMessageFormaterBean;
import matrix.db.Context;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
@Validated
@RequestMapping("/comos/v1")
public class ProjectStructureController {

    private static final Logger PROJECT_STRUCTURE_CONTROLLER_LOGGER = Logger.getLogger(ProjectStructureController.class);
    private static final String NOT_FOUND = "Not Found";
    public static final String USER = "user";
    public static final String PASS = "pass";
    Context context;

    @Autowired
    @Qualifier("CustomResponseBuilder")
    IResponse responseBuilder;

    @Autowired
    @Qualifier("ProjectStructurePreparation")
    IStructurePreparation<ResponseMessageFormaterBean, ProjectStructureServiceResponse, ProjectStructureRequestData> structurePreparation;

    @Autowired
    ComosStructureSearch comosStructureSearch;

    @LogExecutionTime
    @PostMapping("/")
    public String index() throws IOException {
        return "Comos Project Structure Import Greetings Service";
    }

    @LogExecutionTime
    @PostMapping("/import/project-structure")
    public String projectStructureImport(@Valid @RequestBody ProjectStructureRequestEnvelope requestData) throws IOException {
//        IResponse responseBuilder = new CustomResponseBuilder();
        try {
            ResponseMessageFormaterBean responseMessageFormaterBean = structurePreparation.prepareStructure(requestData.getProjectStructureRequestData());

            String buildResponse = responseBuilder.setData(responseMessageFormaterBean).setStatus(Status.OK).buildResponse();
            return buildResponse;
        } catch (Exception ex) {
            Pattern pattern = Pattern.compile(".*ProjectStructureException: (.*).*");

            Matcher matcher = pattern.matcher(ex.getMessage());
            String errorMessage="";
            while (matcher.find()) {
                errorMessage=matcher.group(1);
            }
            throw new ProjectStructureException(errorMessage, requestData.getProjectStructureRequestData());
        }
    }

    @LogExecutionTime
    @GetMapping("/search/project-structure")
    public ResponseEntity<?> getProject(HttpServletRequest httpRequest, @RequestParam("compassId") String compassId) throws Exception {
        PROJECT_STRUCTURE_CONTROLLER_LOGGER.info("COMOS Project Structure Search API Execution Start ------------- ");
        String buildResponse;
        try {
            //Connecting Context
            String user = httpRequest.getHeader(USER);
            String pass = httpRequest.getHeader(PASS);
            context = comosStructureSearch.connectContext(user, pass);

            //Getting objectId by compassId
            String objectId = comosStructureSearch.getObjectId(context, compassId);

            //Getting project structure by objectId
            if (!NullOrEmptyChecker.isNullOrEmpty(objectId)) {
                return new ResponseEntity<>(new AppResponse(Status.OK, comosStructureSearch.getStructure(context, objectId)), HttpStatus.OK);
            } else {
                buildResponse = responseBuilder.setStatus(Status.FAILED).addErrorMessage(NOT_FOUND).setStatus(Status.FAILED).buildResponse();
                PROJECT_STRUCTURE_CONTROLLER_LOGGER.error(buildResponse);
                return new ResponseEntity<>(buildResponse, HttpStatus.OK);
            }
        } catch (Exception e) {
            buildResponse = responseBuilder.setStatus(Status.FAILED).addErrorMessage(e.getMessage()).setStatus(Status.FAILED).buildResponse();
            return new ResponseEntity<>(buildResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        } finally {
            if (context != null) {
                context.close();
                context = null;
            }
        }
    }
}
