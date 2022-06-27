/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.bjit.common.rest.app.service.controller.modelVersion;

import com.bjit.common.rest.app.service.controller.modelVersion.processor.MVImportProcessor;
import com.bjit.common.rest.app.service.controller.modelVersion.validator.MVImportValidator;
import com.bjit.common.rest.app.service.model.modelVersion.MVCreateUpdateResponseFormatter;
import com.bjit.common.rest.app.service.model.modelVersion.MVDataTree;
import com.bjit.common.rest.app.service.model.modelVersion.MVItemsImportDataBean;
import com.bjit.common.rest.app.service.model.modelVersion.MVObjectDataBean;
import com.bjit.common.rest.app.service.model.tnr.TNR;
import com.bjit.common.rest.app.service.payload.common_response.CustomResponseBuilder;
import com.bjit.common.rest.app.service.payload.common_response.IResponse;
import com.bjit.common.rest.app.service.payload.common_response.Status;
import com.bjit.common.rest.app.service.service.modelVersionService.MVImportService;
import com.bjit.common.rest.app.service.utilities.DsServiceCall;
import com.bjit.common.rest.app.service.utilities.NullOrEmptyChecker;
import com.bjit.ewc18x.utils.PropertyReader;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import matrix.db.Context;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**

 @author Ahmad R Farabi [PLM Team, BJIT] <ahmad.farabi@bjitgroup.com>
 */
@RestController
public class ModelVersionImportController
{

    private static final org.apache.log4j.Logger MODEL_VERSION_CREATION_CONTROLLER_LOGGER = org.apache.log4j.Logger.getLogger(ModelVersionImportController.class);

    @Autowired
    MVImportService mvService;

    private List<HashMap<String, String>> intList;
    private List<HashMap<String, String>> stateList;
    private List<HashMap<String, String>> itemList;
    private List<HashMap<String, String>> bomList;
    private List<HashMap<String, String>> evoList;
    private List<HashMap<String, String>> childItemList;
    private List<HashMap<String, String>> childBomList;
    private HashMap<String, HashMap<String, String>> latestRevisionMap;

    @ResponseBody

    @PostMapping(path = "/valmet/enovia/api/v1/model-version/create-update", consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> mvCreateUpdate(HttpServletRequest httpRequest,
                                            @RequestBody final MVObjectDataBean mvRequest)
    {
        Instant startServiceTime = Instant.now();
        IResponse responseBuilder = new CustomResponseBuilder();
        String response;
        final Context context = (Context) httpRequest.getAttribute("context");
        List<MVCreateUpdateResponseFormatter> result = new ArrayList<>();
        List<String> errors = new ArrayList<>();

        try {
            if (!MVImportValidator.validateSource(mvRequest.getSource())) {
                errors = new ArrayList<>();
                errors.add("Source is not allowed.");
                response = responseBuilder.addErrorMessage(errors).setStatus(Status.FAILED).buildResponse();
                return new ResponseEntity<>(response, HttpStatus.OK);
            }

            MODEL_VERSION_CREATION_CONTROLLER_LOGGER.info("Aton service calling========");
            MVImportProcessor mvCreationProcessor = new MVImportProcessor();
            DsServiceCall dsCall = new DsServiceCall(true, PropertyReader.getProperty("aton.security.context.dslc"));

            MODEL_VERSION_CREATION_CONTROLLER_LOGGER.info("Creating Model version========");
            //result = mvCreationProcessor.mvCreation(mvRequest.geDataTree(), mvService, dsCall);
            result = mvCreationProcessor.mvCreation(mvRequest.geDataTree(), mvService, dsCall, context, mvRequest.getSource());

            if (!NullOrEmptyChecker.isNullOrEmpty(result) && !NullOrEmptyChecker.isNullOrEmpty(errors)) {
                response = responseBuilder.setData(result).addErrorMessage(errors).setStatus(Status.OK).buildResponse();
            } else {
                if (!NullOrEmptyChecker.isNullOrEmpty(result) && NullOrEmptyChecker.isNullOrEmpty(errors)) {
                    response = responseBuilder.setData(result).setStatus(Status.OK).buildResponse();
                } else {
                    response = responseBuilder.addErrorMessage(errors).setStatus(Status.FAILED).buildResponse();
                }
            }
            return new ResponseEntity<>(response, HttpStatus.OK);

        }
        catch (Exception e) {
            errors.add(e.getMessage());
            response = responseBuilder.addErrorMessage(errors).setStatus(Status.FAILED).buildResponse();
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        finally {
            Instant endServiceTime = Instant.now();
            Duration timeTakenbyService = Duration.between(startServiceTime, endServiceTime);
            MODEL_VERSION_CREATION_CONTROLLER_LOGGER.info("Time taken by Service :" + timeTakenbyService.toMillis());
        }
    }

    @ResponseBody
    @PostMapping(path = "/valmet/enovia/api/v1/aton/model/create-update", consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> atonMVCreateUpdate(HttpServletRequest httpRequest,
                                                @RequestBody final MVItemsImportDataBean mvSourceRequest)
    {
        Instant startServiceTime = Instant.now();
        IResponse responseBuilder = new CustomResponseBuilder();
        String response;
        final Context context = (Context) httpRequest.getAttribute("context");
        try {
            List<MVCreateUpdateResponseFormatter> resultAll = new ArrayList<>();
            List<String> errors = new ArrayList<>();
            
            //source validations
            if (!MVImportValidator.validateSource(mvSourceRequest.getSource())) {
                errors = new ArrayList<>();
                errors.add("Source is not allowed.");
                response = responseBuilder.addErrorMessage(errors).setStatus(Status.FAILED).buildResponse();
                return new ResponseEntity<>(response, HttpStatus.OK);
            }

            //params validations
            if (!MVImportValidator.validateParams(mvSourceRequest)) {
                errors = new ArrayList<>();
                errors.add("Invalid or missing parameters.");
                response = responseBuilder.addErrorMessage(errors).setStatus(Status.FAILED).buildResponse();
                return new ResponseEntity<>(response, HttpStatus.OK);
            }

            //sort based on request attribute
            MVItemsImportDataBean mvRequest = MVImportValidator.sortRequest(mvSourceRequest);

            //set version attribute if not set
            setVersionInClassIfNot(mvRequest);

            //Aton service calling
            MODEL_VERSION_CREATION_CONTROLLER_LOGGER.info("Aton service calling========");
            MVImportProcessor mvCreationProcessor = new MVImportProcessor();

            //set source and CS in service
            if (mvRequest.getSource().equalsIgnoreCase(PropertyReader.getProperty("aton.integration.source"))) {
                mvService.setSourceAndCS(PropertyReader.getProperty("aton.security.context.dslc"), mvRequest.getSource(), null);
            } else {
//                String token = httpRequest.getHeader("token");
//                WebToken webToken = new WebToken();
//                String newToken = webToken.VerifyToken(token);
//                AuthenticationUserModel model = webToken.getUserCredentials(newToken);
//                System.out.println("User = " + model.getUserId());
                mvService.setSourceAndCS("ctx::" + mvRequest.getSecurityContext(), mvRequest.getSource(), mvRequest.getOwner());
            }

            DsServiceCall dsCall;
            if (mvSourceRequest.getSource().equalsIgnoreCase(PropertyReader.getProperty("aton.integration.source"))) {
                dsCall = new DsServiceCall(true, PropertyReader.getProperty("aton.security.context.dslc"));
            } else {
                dsCall = new DsServiceCall(true, "ctx::" + mvRequest.getSecurityContext());
            }

            //process XML
            List<MVDataTree> reqTree = mvCreationProcessor.xml_process(context, mvRequest.geDataTree());
            mvRequest.geDataTree().clear();
            mvRequest.setDataTree(reqTree);

            for (MVDataTree requestTree : mvRequest.geDataTree()) {
                List<MVDataTree> requestListSingle = new ArrayList();
                requestListSingle.add(requestTree);

                List<MVDataTree> createList = new ArrayList();
                List<MVDataTree> updateList = new ArrayList();
                List<MVDataTree> reviseList = new ArrayList();

                latestRevisionMap = new HashMap();

                //Search for existing and make process decision
                Instant startSearchTime = Instant.now();
                HashMap<String, List<MVDataTree>> reqMap = new HashMap();
                try {
                    reqMap = mvCreationProcessor.searchForRevisionSingle(mvService, context, requestListSingle, dsCall, mvRequest.getSource());
                    createList = reqMap.get("createList");
                    reviseList = reqMap.get("reviseList");
                    updateList = reqMap.get("updateList");
                }
                catch (Exception exception) {
                    MVCreateUpdateResponseFormatter errorResp = new MVCreateUpdateResponseFormatter();
                    TNR tnr = new TNR(null, requestListSingle.get(0).getItem().getTnr().getName(), null);
                    errorResp.setTnr(tnr);
                    errorResp.setSourceRevision(requestListSingle.get(0).getItem().getTnr().getRevision());
                    errorResp.setFailedMessage("Item not found. Root cause= " + exception.getMessage());
                    resultAll.add(errorResp);
                    continue;
                }
                Instant endSearchTime = Instant.now();

                //Create, Revise and Update/Reimport processes
                Instant startCreateTime = Instant.now();
                if (!NullOrEmptyChecker.isNullOrEmpty(createList)) {

                    //run create process
                    List<MVCreateUpdateResponseFormatter> resultCreateAll = this.createProcess(context, dsCall, createList, errors, mvRequest.getSource());
                    resultAll.addAll(resultCreateAll);
                    MODEL_VERSION_CREATION_CONTROLLER_LOGGER.info("Time taken by search :" + Duration.between(startSearchTime, endSearchTime).toMillis());
                }
                Instant endCreateTime = Instant.now();

                Instant startReviseTime = Instant.now();
                if (!NullOrEmptyChecker.isNullOrEmpty(reviseList)) {

                    //run Revision process
                    List<MVCreateUpdateResponseFormatter> resultReviseAll = this.revisionProcess(context, dsCall, reviseList, errors, mvRequest.getSource());
                    resultAll.addAll(resultReviseAll);

                }
                Instant endReviseTime = Instant.now();

                Instant startUpdateTime = Instant.now();
                if (!NullOrEmptyChecker.isNullOrEmpty(updateList)) {

                    //run update and re-import process
                    List<MVCreateUpdateResponseFormatter> resultUpdateAll = this.updateProcess(context, dsCall, updateList, resultAll, errors, mvRequest.getSource());
                    resultAll.addAll(resultUpdateAll);
                }

                Instant endUpdateTime = Instant.now();

                MODEL_VERSION_CREATION_CONTROLLER_LOGGER.info("Time taken by create :" + Duration.between(startCreateTime, endCreateTime).toMillis());
                MODEL_VERSION_CREATION_CONTROLLER_LOGGER.info("Time taken by revise :" + Duration.between(startReviseTime, endReviseTime).toMillis());
                MODEL_VERSION_CREATION_CONTROLLER_LOGGER.info("Time taken by update :" + Duration.between(startUpdateTime, endUpdateTime).toMillis());
            }

            dsCall.closeClients(dsCall.getCSRFTokenService());
            context.close();

            response = responseBuilder.setData(resultAll).setStatus(Status.OK).buildResponse();
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        catch (Exception e) {
            List<String> errors = new ArrayList<>();
            errors.add("No item has been imported. Please try again. Root cause= " + e.getMessage());
            response = responseBuilder.addErrorMessage(errors).setStatus(Status.FAILED).buildResponse();
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        finally {
            Instant endServiceTime = Instant.now();
            Duration timeTakenbyService = Duration.between(startServiceTime, endServiceTime);
            MODEL_VERSION_CREATION_CONTROLLER_LOGGER.info("Time taken by Service :" + timeTakenbyService.toMillis());
        }
    }

    private List<MVCreateUpdateResponseFormatter> createProcess(
            Context context,
            DsServiceCall dsCall,
            List<MVDataTree> createList,
            List<String> errors,
            String source) throws Exception
    {
        Instant startMVTime = Instant.now();
        //Create MV
        MVImportProcessor mvCreationProcessor = new MVImportProcessor();
        List<MVCreateUpdateResponseFormatter> resultCreateAll = new ArrayList();

        for (MVDataTree create : createList) {

            List<MVCreateUpdateResponseFormatter> resultCreate = new ArrayList();
            List<MVDataTree> createListSingle = new ArrayList();
            createListSingle.add(create);

            Instant endMVTime = null;
            Instant startInterfaceTime = null;
            Instant endInterfaceTime = null;
            Instant startItemTime = null;
            Instant endItemTime = null;
            Instant startEvoTime = null;
            try {
                //params validations
                if (!MVImportValidator.validateAttributes(create)) {
                    MVCreateUpdateResponseFormatter errorResp = new MVCreateUpdateResponseFormatter();
                    TNR tnr = new TNR(null, createListSingle.get(0).getItem().getTnr().getName(), null);
                    errorResp.setTnr(tnr);
                    errorResp.setSourceRevision(createListSingle.get(0).getItem().getTnr().getRevision());
                    errorResp.setFailedMessage("Parameter/s not valid.");
                    resultCreate.add(errorResp);
                    resultCreateAll.addAll(resultCreate);
                    continue;
                }

                MODEL_VERSION_CREATION_CONTROLLER_LOGGER.info("Creating Model version========" + createListSingle.get(0).getItem().getTnr().getName() + " for Revision=" + createListSingle.get(0).getItem().getTnr().getRevision());

                try {
                    resultCreate = mvCreationProcessor.mvCreation(createListSingle, mvService, dsCall, context, source);
                    if (!NullOrEmptyChecker.isNullOrEmpty(resultCreate)) {
                        int i = 0;
                        for (MVCreateUpdateResponseFormatter res : resultCreate) {
                            if (createListSingle.get(i).getItem().getTnr().getName().equalsIgnoreCase(res.getTnr().getName())) {
                                res.setSourceRevision(createListSingle.get(i).getItem().getTnr().getRevision());
                            }
                            i++;
                        }
                    } else {
                        MVCreateUpdateResponseFormatter errorResp = new MVCreateUpdateResponseFormatter();
                        TNR tnr = new TNR(null, createListSingle.get(0).getItem().getTnr().getName(), null);
                        errorResp.setTnr(tnr);
                        errorResp.setSourceRevision(createListSingle.get(0).getItem().getTnr().getRevision());
                        errorResp.setFailedMessage("An error occurred while creating Model");
                        resultCreate.add(errorResp);
                        resultCreateAll.addAll(resultCreate);
                        continue;
                        //response = responseBuilder.addErrorMessage(errors).setStatus(Status.FAILED).buildResponse();
                        //return new ResponseEntity<>(response, HttpStatus.OK);
                    }

                    //change ownership of model
                    if (!source.equalsIgnoreCase(PropertyReader.getProperty("aton.integration.source"))) {
                        if (mvService.updateOwnershipOfModel(context, resultCreate.get(0).getModelPhysicalId())) {
                            MODEL_VERSION_CREATION_CONTROLLER_LOGGER.info("Model owner updated");
                        }
                    }
                }
                catch (Exception e) {
                    MVCreateUpdateResponseFormatter errorResp = new MVCreateUpdateResponseFormatter();
                    TNR tnr = new TNR(null, createListSingle.get(0).getItem().getTnr().getName(), null);
                    errorResp.setTnr(tnr);
                    errorResp.setSourceRevision(createListSingle.get(0).getItem().getTnr().getRevision());
                    errorResp.setFailedMessage("An error occurred while creating Model. Root cause= " + e.getMessage());
                    resultCreate.add(errorResp);
                    resultCreateAll.addAll(resultCreate);
                    continue;
                }
                endMVTime = Instant.now();

                //put results for future latest revision finding
                for (MVCreateUpdateResponseFormatter res : resultCreate) {
                    HashMap<String, String> map = new HashMap();
                    map.put("latestRevision", res.getSourceRevision());
                    map.put("latestRevisionPhysicalId", res.getMvPhysicalId());
                    latestRevisionMap.put(res.getTnr().getName(), map);
                }

                //classification attr update
                if (source.equalsIgnoreCase(PropertyReader.getProperty("aton.integration.source"))) {
                    errors = this.UpdateClassificationMVWithCheck(mvCreationProcessor, dsCall, context, resultCreate, createListSingle);
                    if (!NullOrEmptyChecker.isNullOrEmpty(errors)) {
                        resultCreate.get(0).setFailedMessage("An error occurred while updating class. Root cause= " + errors.get(0));
                        resultCreateAll.addAll(resultCreate);
                        continue;
                    }
                }

                startInterfaceTime = Instant.now();
                //add interface
                errors = this.addInterface(mvCreationProcessor, context, createListSingle, source, dsCall, resultCreate);
                if (!NullOrEmptyChecker.isNullOrEmpty(errors)) {
                    resultCreate.get(0).setFailedMessage("An error occurred while adding interface. Root cause= " + errors.get(0));
                    resultCreateAll.addAll(resultCreate);
                    continue;
                }
                endInterfaceTime = Instant.now();

                startItemTime = Instant.now();
                //top and child item creation
                errors = this.createItems(mvCreationProcessor, createListSingle, source, resultCreate, context, dsCall);
                if (!NullOrEmptyChecker.isNullOrEmpty(errors)) {
                    resultCreate.get(0).setFailedMessage("An error occurred while creating items. Root cause= " + errors.get(0));
                    resultCreateAll.addAll(resultCreate);
                    continue;
                }

                //top and child item bom creation
                errors = this.createItemInstances(mvCreationProcessor, itemList, context, dsCall);
                if (!NullOrEmptyChecker.isNullOrEmpty(errors)) {
                    resultCreate.get(0).setFailedMessage("An error occurred while creating BOM. Root cause= " + errors.get(0));
                    resultCreateAll.addAll(resultCreate);
                    continue;
                }
                endItemTime = Instant.now();

                startEvoTime = Instant.now();
                //set evolution and effectivity
                errors = this.setEvolution(mvCreationProcessor, dsCall, resultCreate, itemList, bomList);
                if (!NullOrEmptyChecker.isNullOrEmpty(errors)) {
                    resultCreate.get(0).setFailedMessage("An error occurred while setting effectivity. Root cause= " + errors.get(0));
                    resultCreateAll.addAll(resultCreate);
                    continue;
                }
            }
            catch (Exception exception) {
                if (!NullOrEmptyChecker.isNullOrEmpty(resultCreate)) {
                    resultCreate.get(0).setFailedMessage("An error occurred while creating item. Root cause= " + exception);
                    resultCreateAll.addAll(resultCreate);
                    continue;
                } else {
                    MVCreateUpdateResponseFormatter errorResp = new MVCreateUpdateResponseFormatter();
                    TNR tnr = new TNR(null, createListSingle.get(0).getItem().getTnr().getName(), null);
                    errorResp.setTnr(tnr);
                    errorResp.setSourceRevision(createListSingle.get(0).getItem().getTnr().getRevision());
                    errorResp.setFailedMessage("An error occurred while creating Model. Root cause= " + exception);
                    resultCreate.add(errorResp);
                    resultCreateAll.addAll(resultCreate);
                    continue;
                }
            }

            resultCreateAll.addAll(resultCreate);

            Instant endEvoTime = Instant.now();

            MODEL_VERSION_CREATION_CONTROLLER_LOGGER.info("Time taken by MV :" + Duration.between(startMVTime, endMVTime).toMillis());
            MODEL_VERSION_CREATION_CONTROLLER_LOGGER.info("Time taken by Interface :" + Duration.between(startInterfaceTime, endInterfaceTime).toMillis());
            MODEL_VERSION_CREATION_CONTROLLER_LOGGER.info("Time taken by ItemBom :" + Duration.between(startItemTime, endItemTime).toMillis());
            MODEL_VERSION_CREATION_CONTROLLER_LOGGER.info("Time taken by Evo :" + Duration.between(startEvoTime, endEvoTime).toMillis());

        }
        return resultCreateAll;
    }

    private List<MVCreateUpdateResponseFormatter> revisionProcess(
            Context context,
            DsServiceCall dsCall,
            List<MVDataTree> reviseList,
            List<String> errors,
            String source) throws Exception
    {
        MVImportProcessor mvCreationProcessor = new MVImportProcessor();
        List<MVCreateUpdateResponseFormatter> resultReviseAll = new ArrayList();

        int index = 0;
        for (MVDataTree revise : reviseList) {
            List<MVDataTree> reviseListSingle = new ArrayList();
            reviseListSingle.add(revise);
            MODEL_VERSION_CREATION_CONTROLLER_LOGGER.info("Revising Model version========" + reviseListSingle.get(0).getItem().getTnr().getName() + " for Revision=" + reviseListSingle.get(0).getItem().getTnr().getRevision());

            List<MVCreateUpdateResponseFormatter> resultRevise = new ArrayList();
            try {
                HashMap<String, List<MVDataTree>> reviseCheckMap = new HashMap();
                reviseCheckMap = mvCreationProcessor.getLatestRevision(mvService, reviseListSingle, latestRevisionMap);
                reviseListSingle = reviseCheckMap.get("reviseList");

                if (NullOrEmptyChecker.isNullOrEmpty(reviseListSingle)) {
                    MVCreateUpdateResponseFormatter errorResp = new MVCreateUpdateResponseFormatter();
                    TNR tnr = new TNR(null, revise.getItem().getTnr().getName(), null);
                    errorResp.setTnr(tnr);
                    errorResp.setSourceRevision(revise.getItem().getTnr().getRevision());
                    errorResp.setFailedMessage("An error occurred while updating Model. Root cause= Item not found/data missing.");
                    resultRevise.add(errorResp);
                    resultReviseAll.addAll(resultRevise);
                    continue;
                }

                //params validations
                if (!MVImportValidator.validateAttributes(revise)) {
                    MVCreateUpdateResponseFormatter errorResp = new MVCreateUpdateResponseFormatter();
                    TNR tnr = new TNR(null, reviseListSingle.get(0).getItem().getTnr().getName(), null);
                    errorResp.setTnr(tnr);
                    errorResp.setSourceRevision(reviseListSingle.get(0).getItem().getTnr().getRevision());
                    errorResp.setFailedMessage("Parameter/s not valid.");
                    resultRevise.add(errorResp);
                    resultReviseAll.addAll(resultRevise);
                    continue;
                }

                try {
                    resultRevise = mvCreationProcessor.reviseMV(reviseListSingle, source, mvService, dsCall, context);

                    if (!NullOrEmptyChecker.isNullOrEmpty(resultRevise)) {
                        int c = 0;
                        for (MVDataTree rev : reviseListSingle) {
                            if (rev.getItem().getTnr().getName().equalsIgnoreCase(resultRevise.get(c).getTnr().getName())) {
                                rev.getItem().getAttributes().put("mvPhysicalId", resultRevise.get(c).getMvPhysicalId());
                            }
                            c++;
                        }
                        c = 0;
                        for (MVCreateUpdateResponseFormatter res : resultRevise) {
                            if (reviseListSingle.get(c).getItem().getTnr().getName().equalsIgnoreCase(res.getTnr().getName())) {
                                res.setSourceRevision(reviseListSingle.get(c).getItem().getTnr().getRevision());
                            }
                            c++;
                        }

                        //update MV attributes after revision creation
                        if (!mvCreationProcessor.mvUpdateForRevise(reviseListSingle, mvService, context)) {
                            MVCreateUpdateResponseFormatter errorResp = new MVCreateUpdateResponseFormatter();
                            TNR tnr = new TNR(null, reviseListSingle.get(0).getItem().getTnr().getName(), null);
                            errorResp.setTnr(tnr);
                            errorResp.setSourceRevision(reviseListSingle.get(0).getItem().getTnr().getRevision());
                            errorResp.setFailedMessage("An error occurred while revising attributes. Root cause= " + errors.get(0));
                            resultRevise.add(errorResp);
                            resultReviseAll.addAll(resultRevise);
                            continue;
                        }

                    } else {
                        MVCreateUpdateResponseFormatter errorResp = new MVCreateUpdateResponseFormatter();
                        TNR tnr = new TNR(null, reviseListSingle.get(0).getItem().getTnr().getName(), null);
                        errorResp.setTnr(tnr);
                        errorResp.setSourceRevision(reviseListSingle.get(0).getItem().getTnr().getRevision());
                        errorResp.setFailedMessage("An error occurred while revising Model. Root cause= " + errors.get(0));
                        resultRevise.add(errorResp);
                        resultReviseAll.addAll(resultRevise);
                        continue;
                    }
                }
                catch (Exception e) {
                    MVCreateUpdateResponseFormatter errorResp = new MVCreateUpdateResponseFormatter();
                    TNR tnr = new TNR(null, reviseListSingle.get(0).getItem().getTnr().getName(), null);
                    errorResp.setTnr(tnr);
                    errorResp.setSourceRevision(reviseListSingle.get(0).getItem().getTnr().getRevision());
                    errorResp.setFailedMessage("An error occurred while revising Model. Root cause= " + e.getMessage());
                    resultRevise.add(errorResp);
                    resultReviseAll.addAll(resultRevise);
                    continue;
                }
                reviseList.set(index, reviseListSingle.get(0));
                index++;

                //put results for future latest revision finding
                for (MVCreateUpdateResponseFormatter res : resultRevise) {
                    HashMap<String, String> map = new HashMap();
                    map.put("latestRevision", res.getSourceRevision());
                    map.put("latestRevisionPhysicalId", res.getMvPhysicalId());
                    if (latestRevisionMap.containsKey(res.getTnr().getName())) {
                        map.put("topItemPhysicalId", latestRevisionMap.get(res.getTnr().getName()).get("topItemPhysicalId"));
                    } else {
                        map.put("topItemPhysicalId", mvCreationProcessor.getTopMfgItem(res.getTnr(), context));
                    }
                    latestRevisionMap.put(res.getTnr().getName(), map);
                }

                if (source.equalsIgnoreCase(PropertyReader.getProperty("aton.integration.source"))) {
                    //classification attr update
                    errors = this.UpdateClassificationMVWithCheck(mvCreationProcessor, dsCall, context, resultRevise, reviseListSingle);
                    if (!NullOrEmptyChecker.isNullOrEmpty(errors)) {
                        resultRevise.get(0).setFailedMessage("An error occurred while updating class. Root cause= " + errors.get(0));
                        resultReviseAll.addAll(resultRevise);
                        continue;
                    }
                }

                //child item create
                errors = this.createChildItemForRevision(mvCreationProcessor, reviseListSingle, source, resultRevise, context, dsCall);
                if (!NullOrEmptyChecker.isNullOrEmpty(errors)) {
                    resultRevise.get(0).setFailedMessage("An error occurred while creating items. Root cause= " + errors.get(0));
                    resultReviseAll.addAll(resultRevise);
                    continue;
                }

                //child item instance create
                errors = this.createItemInstanceForRevision(mvCreationProcessor, reviseListSingle, resultRevise, childItemList, dsCall, context);
                if (!NullOrEmptyChecker.isNullOrEmpty(errors)) {
                    resultRevise.get(0).setFailedMessage("An error occurred while creating item instance. Root cause= " + errors.get(0));
                    resultReviseAll.addAll(resultRevise);
                    continue;
                }

                //set evolution and effectivity
                errors = this.setEvolutionForRevise(mvCreationProcessor, dsCall, resultRevise, childItemList, childBomList);
                if (!NullOrEmptyChecker.isNullOrEmpty(errors)) {
                    resultRevise.get(0).setFailedMessage("An error occurred while setting effectivity. Root cause= " + errors.get(0));
                    resultReviseAll.addAll(resultRevise);
                    continue;
                }
            }
            catch (Exception exception) {
                if (!NullOrEmptyChecker.isNullOrEmpty(resultRevise)) {
                    resultRevise.get(0).setFailedMessage("An error occurred while revising item. Root cause= " + exception);
                    resultReviseAll.addAll(resultRevise);
                    continue;
                } else {
                    MVCreateUpdateResponseFormatter errorResp = new MVCreateUpdateResponseFormatter();
                    TNR tnr = new TNR(null, reviseListSingle.get(0).getItem().getTnr().getName(), null);
                    errorResp.setTnr(tnr);
                    errorResp.setSourceRevision(reviseListSingle.get(0).getItem().getTnr().getRevision());
                    errorResp.setFailedMessage("An error occurred while revising item. Root cause= " + exception);
                    resultRevise.add(errorResp);
                    resultReviseAll.addAll(resultRevise);
                    continue;
                }
            }
            resultReviseAll.addAll(resultRevise);
        }
        return resultReviseAll;
    }

    //update process
    private List<MVCreateUpdateResponseFormatter> updateProcess(Context context,
                                                                DsServiceCall dsCall,
                                                                List<MVDataTree> updateList,
                                                                List<MVCreateUpdateResponseFormatter> resultAll,
                                                                List<String> errors,
                                                                String source) throws Exception
    {
        MVImportProcessor mvCreationProcessor = new MVImportProcessor();
        List<MVCreateUpdateResponseFormatter> resultUpdateAll = new ArrayList();
        //Update MV
        for (MVDataTree update : updateList) {
            List<MVDataTree> updateListSingle = new ArrayList();
            updateListSingle.add(update);
            MODEL_VERSION_CREATION_CONTROLLER_LOGGER.info("Updating Model version========" + updateListSingle.get(0).getItem().getTnr().getName() + " for Revision=" + updateListSingle.get(0).getItem().getTnr().getRevision());

            List<MVCreateUpdateResponseFormatter> resultUpdate = new ArrayList();
            try {
                HashMap<String, List<MVDataTree>> updateCheckMap = new HashMap();
                updateCheckMap = mvCreationProcessor.checkRevisionForUpdate(mvService, context, updateListSingle, resultAll, dsCall, source);
                updateListSingle = updateCheckMap.get("updateList");

                if (NullOrEmptyChecker.isNullOrEmpty(updateListSingle)) {
                    MVCreateUpdateResponseFormatter errorResp = new MVCreateUpdateResponseFormatter();
                    TNR tnr = new TNR(null, update.getItem().getTnr().getName(), null);
                    errorResp.setTnr(tnr);
                    errorResp.setSourceRevision(update.getItem().getTnr().getRevision());
                    errorResp.setFailedMessage("An error occurred while updating Model. Root cause= Item not found/data missing.");
                    resultUpdate.add(errorResp);
                    resultUpdateAll.addAll(resultUpdate);
                    continue;
                }

                //params validations
                if (!MVImportValidator.validateAttributes(update)) {
                    MVCreateUpdateResponseFormatter errorResp = new MVCreateUpdateResponseFormatter();
                    TNR tnr = new TNR(null, updateListSingle.get(0).getItem().getTnr().getName(), null);
                    errorResp.setTnr(tnr);
                    errorResp.setSourceRevision(updateListSingle.get(0).getItem().getTnr().getRevision());
                    errorResp.setFailedMessage("Parameter/s not valid.");
                    resultUpdate.add(errorResp);
                    resultUpdateAll.addAll(resultUpdate);
                    continue;
                }

                try {
                    resultUpdate = mvCreationProcessor.mvUpdate(updateListSingle, mvService, dsCall, context);
                    if (!NullOrEmptyChecker.isNullOrEmpty(resultUpdate)) {
                        int i = 0;
                        for (MVCreateUpdateResponseFormatter res : resultUpdate) {
                            if (updateListSingle.get(i).getItem().getTnr().getName().equalsIgnoreCase(res.getTnr().getName())) {
                                res.setSourceRevision(updateListSingle.get(i).getItem().getTnr().getRevision());
                            }
                            i++;
                        }
                    } else {
                        MVCreateUpdateResponseFormatter errorResp = new MVCreateUpdateResponseFormatter();
                        TNR tnr = new TNR(null, updateListSingle.get(0).getItem().getTnr().getName(), null);
                        errorResp.setTnr(tnr);
                        errorResp.setSourceRevision(updateListSingle.get(0).getItem().getTnr().getRevision());
                        errorResp.setFailedMessage("An error occurred while updating Model.");
                        resultUpdate.add(errorResp);
                        resultUpdateAll.addAll(resultUpdate);
                        continue;
                    }
                }
                catch (Exception e) {
                    MVCreateUpdateResponseFormatter errorResp = new MVCreateUpdateResponseFormatter();
                    TNR tnr = new TNR(null, updateListSingle.get(0).getItem().getTnr().getName(), null);
                    errorResp.setTnr(tnr);
                    errorResp.setSourceRevision(updateListSingle.get(0).getItem().getTnr().getRevision());
                    errorResp.setFailedMessage("An error occurred while updating Model. Root cause= " + e.getMessage());
                    resultUpdate.add(errorResp);
                    resultUpdateAll.addAll(resultUpdate);
                    continue;
                }

                if (source.equalsIgnoreCase(PropertyReader.getProperty("aton.integration.source"))) {
                    //classification attr update
                    errors = this.UpdateClassificationMVWithCheck(mvCreationProcessor, dsCall, context, resultUpdate, updateListSingle);
                    if (!NullOrEmptyChecker.isNullOrEmpty(errors)) {
                        resultUpdate.get(0).setFailedMessage("An error occurred while updating class. Root cause= " + errors.get(0));
                        resultUpdateAll.addAll(resultUpdate);
                        continue;
                    }
                }

                //add interface
                errors = this.addInterface(mvCreationProcessor, context, updateListSingle, source, dsCall, resultUpdate);
                if (!NullOrEmptyChecker.isNullOrEmpty(errors)) {
                    resultUpdate.get(0).setFailedMessage("An error occurred while adding interface. Root cause= " + errors.get(0));
                    resultUpdateAll.addAll(resultUpdate);
                    continue;
                }

                if (updateListSingle.get(0).getItem().getAttributes().get("isRevision").equalsIgnoreCase("false")) {
                    //top and child item creation
                    errors = this.createItems(mvCreationProcessor, updateListSingle, source, resultUpdate, context, dsCall);
                    if (!NullOrEmptyChecker.isNullOrEmpty(errors)) {
                        resultUpdate.get(0).setFailedMessage("An error occurred while creating items. Root cause= " + errors.get(0));
                        resultUpdateAll.addAll(resultUpdate);
                        continue;
                    }
                    //top and child item bom creation
                    errors = this.createItemInstances(mvCreationProcessor, itemList, context, dsCall);
                    if (!NullOrEmptyChecker.isNullOrEmpty(errors)) {
                        resultUpdate.get(0).setFailedMessage("An error occurred while creating BOM. Root cause= " + errors.get(0));
                        resultUpdateAll.addAll(resultUpdate);
                        continue;
                    }

                    //set evolution and effectivity
                    errors = this.setEvolution(mvCreationProcessor, dsCall, resultUpdate, itemList, bomList);
                    if (!NullOrEmptyChecker.isNullOrEmpty(errors)) {
                        resultUpdate.get(0).setFailedMessage("An error occurred while setting effectivity. Root cause= " + errors.get(0));
                        resultUpdateAll.addAll(resultUpdate);
                        continue;
                    }
                } else {
                    //child item create
                    errors = this.createChildItemForRevision(mvCreationProcessor, updateListSingle, source, resultUpdate, context, dsCall);
                    if (!NullOrEmptyChecker.isNullOrEmpty(errors)) {
                        resultUpdate.get(0).setFailedMessage("An error occurred while creating items. Root cause= " + errors.get(0));
                        resultUpdateAll.addAll(resultUpdate);
                        continue;
                    }

                    //child item instance create
                    errors = this.createItemInstanceForRevision(mvCreationProcessor, updateListSingle, resultUpdate, childItemList, dsCall, context);
                    if (!NullOrEmptyChecker.isNullOrEmpty(errors)) {
                        resultUpdate.get(0).setFailedMessage("An error occurred while creating item instance. Root cause= " + errors.get(0));
                        resultUpdateAll.addAll(resultUpdate);
                        continue;
                    }

                    //set evolution and effectivity
                    errors = this.setEvolutionForRevise(mvCreationProcessor, dsCall, resultUpdate, childItemList, childBomList);
                    if (!NullOrEmptyChecker.isNullOrEmpty(errors)) {
                        resultUpdate.get(0).setFailedMessage("An error occurred while setting effectivity. Root cause= " + errors.get(0));
                        resultUpdateAll.addAll(resultUpdate);
                        continue;
                    }
                }
            }
            catch (Exception exception) {
                if (!NullOrEmptyChecker.isNullOrEmpty(resultUpdate)) {
                    resultUpdate.get(0).setFailedMessage("An error occurred while updating item. Root cause= " + exception);
                    resultUpdateAll.addAll(resultUpdate);
                    continue;
                } else {
                    MVCreateUpdateResponseFormatter errorResp = new MVCreateUpdateResponseFormatter();
                    TNR tnr = new TNR(null, updateListSingle.get(0).getItem().getTnr().getName(), null);
                    errorResp.setTnr(tnr);
                    errorResp.setSourceRevision(updateListSingle.get(0).getItem().getTnr().getRevision());
                    errorResp.setFailedMessage("An error occurred while updating item. Root cause= " + exception);
                    resultUpdate.add(errorResp);
                    resultUpdateAll.addAll(resultUpdate);
                    continue;
                }
            }

            resultUpdateAll.addAll(resultUpdate);
        }
        return resultUpdateAll;
    }

    private List<String> addInterface(MVImportProcessor mvCreationProcessor,
                                      Context context,
                                      List<MVDataTree> mvRequest, String source,
                                      DsServiceCall dsCall,
                                      List<MVCreateUpdateResponseFormatter> result) throws Exception
    {
        intList = new ArrayList<>();
        List<String> errors = new ArrayList<>();
        try {
            MODEL_VERSION_CREATION_CONTROLLER_LOGGER.info("Adding interfaces========");
            intList = mvCreationProcessor.addInterface(mvService, context, mvRequest, source, result, dsCall);
            if (!NullOrEmptyChecker.isNullOrEmpty(intList) && !intList.isEmpty()) {
                for (HashMap<String, String> interfaces : intList) {
                    if (!NullOrEmptyChecker.isNullOrEmpty(interfaces.get("error"))) {
                        errors.add(interfaces.get("error"));
                    }
                }
            }
        }
        catch (Exception e) {
            errors = new ArrayList<>();
            errors.add(e.getMessage());
        }
        if (!errors.isEmpty()) {
            return errors;
        }
        return null;
    }

    private List<String> createItems(
            MVImportProcessor mvCreationProcessor, List<MVDataTree> mvRequest,
            String source,
            List<MVCreateUpdateResponseFormatter> result,
            Context context, DsServiceCall dsCall) throws Exception
    {

        itemList = new ArrayList<>();
        bomList = new ArrayList<>();
        List<String> errors = new ArrayList<>();

        Instant startItemTime = Instant.now();
        try {
            MODEL_VERSION_CREATION_CONTROLLER_LOGGER.info("Creating top/child items========");
            itemList = mvCreationProcessor.itemsCreation(mvService, mvRequest, source, dsCall, context);
            if (!NullOrEmptyChecker.isNullOrEmpty(itemList) && !itemList.isEmpty()) {
                setMfgItemInfo(result.get(0), itemList);
                for (HashMap<String, String> item : itemList) {
                    if (!NullOrEmptyChecker.isNullOrEmpty(item.get("error"))) {
                        errors.add(item.get("error"));
                    }
                }
            }
        }
        catch (Exception e) {
            errors = new ArrayList<>();
            errors.add(e.getMessage());
        }
        Instant endItemTime = Instant.now();
        MODEL_VERSION_CREATION_CONTROLLER_LOGGER.info("Time taken by Item :" + Duration.between(startItemTime, endItemTime).toMillis());
        if (!errors.isEmpty()) {
            return errors;
        }
        return null;
    }

    private List<String> createItemInstances(
            MVImportProcessor mvCreationProcessor,
            List<HashMap<String, String>> itemList,
            Context context, DsServiceCall dsCall)
    {
        Instant startBomTime = Instant.now();
        //instance creation between items
        List<String> errors = new ArrayList<>();
        try {
            MODEL_VERSION_CREATION_CONTROLLER_LOGGER.info("Creating item instances========");
            //bomList = mvCreationProcessor.bomCreation(context, mvService, itemList);
            bomList = mvCreationProcessor.createItemInstance(mvService, itemList, dsCall, context);
            if (!NullOrEmptyChecker.isNullOrEmpty(bomList) && !bomList.isEmpty()) {
                for (HashMap<String, String> bom : bomList) {
                    if (!NullOrEmptyChecker.isNullOrEmpty(bom.get("error"))) {
                        errors.add(bom.get("error"));
                    }
                }
            }
        }
        catch (Exception e) {
            errors = new ArrayList<>();
            errors.add(e.getMessage());
        }
        Instant endBomTime = Instant.now();

        MODEL_VERSION_CREATION_CONTROLLER_LOGGER.info("Time taken by only Bom :" + Duration.between(startBomTime, endBomTime).toMillis());

        if (!errors.isEmpty()) {
            return errors;
        }

        return null;
    }

    private List<String> setEvolution(MVImportProcessor mvCreationProcessor,
                                      DsServiceCall dsCall,
                                      List<MVCreateUpdateResponseFormatter> result,
                                      List<HashMap<String, String>> itemList,
                                      List<HashMap<String, String>> bomList) throws Exception
    {
        evoList = new ArrayList<>();
        List<String> errors = new ArrayList<>();
        try {
            MODEL_VERSION_CREATION_CONTROLLER_LOGGER.info("Initiating Set evolution========");
            evoList = mvCreationProcessor.setEvolution(mvService, result, itemList, bomList, dsCall);
            if (!NullOrEmptyChecker.isNullOrEmpty(evoList) && !evoList.isEmpty()) {
                for (HashMap<String, String> evo : evoList) {
                    System.out.println("evo =================");
                    evo.entrySet().forEach(entry -> {
                        System.out.println(entry.getKey() + " " + entry.getValue());
                    });
                    //put results for future latest revision finding
                    if (latestRevisionMap.containsKey(evo.get("modelName"))) {
                        latestRevisionMap.get(evo.get("modelName")).put("topItemPhysicalId", evo.get("topItemPhysicalId"));
                    }

                    if (!NullOrEmptyChecker.isNullOrEmpty(evo.get("error"))) {
                        errors.add(evo.get("error"));
                    }
                }
            }
        }
        catch (Exception e) {
            errors = new ArrayList<>();
            errors.add(e.getMessage());
        }
        if (!errors.isEmpty()) {
            return errors;
        }

        return null;
    }

    private List<String> createChildItemForRevision(
            MVImportProcessor mvCreationProcessor, List<MVDataTree> mvRequest,
            String source,
            List<MVCreateUpdateResponseFormatter> result,
            Context context, DsServiceCall dsCall) throws Exception
    {

        childItemList = new ArrayList<>();
        List<String> errors = new ArrayList<>();
        try {
            MODEL_VERSION_CREATION_CONTROLLER_LOGGER.info("Creating top/child items========");
            childItemList = mvCreationProcessor.childItemsCreationForRevision(context, mvService, mvRequest, source, dsCall);
            if (!NullOrEmptyChecker.isNullOrEmpty(childItemList) && !childItemList.isEmpty()) {
                setMfgItemInfo(result.get(0), childItemList);
                childItemList.removeIf(x
                        -> x.get("type").equalsIgnoreCase(PropertyReader.getProperty("aton.topmfgitem.type")));
                for (HashMap<String, String> item : childItemList) {
                    if (!NullOrEmptyChecker.isNullOrEmpty(item.get("error"))) {
                        errors.add(item.get("error"));
                    }
                }
            }

        }
        catch (Exception e) {
            errors = new ArrayList<>();
            errors.add(e.getMessage());
        }
        if (!errors.isEmpty()) {
            return errors;
        }

        return null;
    }

    private List<String> createItemInstanceForRevision(
            MVImportProcessor mvCreationProcessor, List<MVDataTree> mvRequest,
            List<MVCreateUpdateResponseFormatter> result,
            List<HashMap<String, String>> childItemList, DsServiceCall dsCall,
            Context context) throws Exception
    {

        childBomList = new ArrayList<>();

        List<String> errors = new ArrayList<>();

        //instance creation between items
        try {
            MODEL_VERSION_CREATION_CONTROLLER_LOGGER.info("Creating instance between items========");
            childBomList = mvCreationProcessor.createItemInstanceForRevision(mvService, mvRequest, result, childItemList, dsCall, context);
            if (!NullOrEmptyChecker.isNullOrEmpty(childBomList) && !childBomList.isEmpty()) {
                for (HashMap<String, String> bom : childBomList) {
                    if (!NullOrEmptyChecker.isNullOrEmpty(bom.get("error"))) {
                        errors.add(bom.get("error"));
                    }
                }
            }
        }
        catch (Exception e) {
            errors = new ArrayList<>();
            errors.add(e.getMessage());
        }
        if (!errors.isEmpty()) {
            return errors;
        }

        return null;
    }

    private List<String> setEvolutionForRevise(
            MVImportProcessor mvCreationProcessor, DsServiceCall dsCall,
            List<MVCreateUpdateResponseFormatter> result,
            List<HashMap<String, String>> itemList,
            List<HashMap<String, String>> bomList) throws Exception
    {
        evoList = new ArrayList<>();
        List<String> errors = new ArrayList<>();
        try {
            MODEL_VERSION_CREATION_CONTROLLER_LOGGER.info("Initiating Set evolution========");
            evoList = mvCreationProcessor.setEvolutionForRevise(mvService, result, itemList, bomList, dsCall);
            if (!NullOrEmptyChecker.isNullOrEmpty(evoList) && !evoList.isEmpty()) {
                for (HashMap<String, String> evo : evoList) {
                    if (!NullOrEmptyChecker.isNullOrEmpty(evo.get("error"))) {
                        errors.add(evo.get("error"));
                    }
                }
            }
        }
        catch (Exception e) {
            errors = new ArrayList<>();
            errors.add(e.getMessage());
        }
        if (!errors.isEmpty()) {
            return errors;
        }

        return null;
    }

    private List<String> changeMaturityState(
            MVImportProcessor mvCreationProcessor, List<MVDataTree> mvRequest,
            DsServiceCall dsCall, List<MVCreateUpdateResponseFormatter> result) throws Exception
    {
        stateList = new ArrayList<>();
        List<String> errors = new ArrayList<>();
        try {
            MODEL_VERSION_CREATION_CONTROLLER_LOGGER.info("Changing maturity state========");
            stateList = mvCreationProcessor.changeMaturityState(mvService, mvRequest, result, dsCall);
            if (!NullOrEmptyChecker.isNullOrEmpty(stateList) && !stateList.isEmpty()) {
                for (HashMap<String, String> state : stateList) {
                    if (!NullOrEmptyChecker.isNullOrEmpty(state.get("error"))) {
                        errors.add(state.get("error"));
                    }
                }
            }
        }
        catch (Exception e) {
            errors = new ArrayList<>();
            errors.add(e.getMessage());
        }
        if (!errors.isEmpty()) {
            return errors;
        }
        return null;
    }

    private List<String> classifyMV(MVImportProcessor mvCreationProcessor,
                                    DsServiceCall dsCall,
                                    List<MVCreateUpdateResponseFormatter> result,
                                    List<MVDataTree> mvRequest) throws Exception
    {
        List<String> errors = new ArrayList<>();
        try {
            MODEL_VERSION_CREATION_CONTROLLER_LOGGER.info("Initiating classification========");
            List<HashMap<String, String>> classList = mvCreationProcessor.classifyMV(mvService, result, mvRequest, dsCall);
            if (!NullOrEmptyChecker.isNullOrEmpty(classList) && !classList.isEmpty()) {
                for (HashMap<String, String> cls : classList) {
                    System.out.println("cls =================");
                    cls.entrySet().forEach(entry -> {
                        System.out.println(entry.getKey() + " " + entry.getValue());
                    });

                    if (!NullOrEmptyChecker.isNullOrEmpty(cls.get("error"))) {
                        errors.add(cls.get("error"));
                    }
                }
            }
        }
        catch (Exception e) {
            errors = new ArrayList<>();
            errors.add(e.getMessage());
        }
        if (!errors.isEmpty()) {
            return errors;
        }

        return null;
    }

    private List<String> UpdateClassificationMV(
            MVImportProcessor mvCreationProcessor,
            DsServiceCall dsCall, Context context,
            List<MVCreateUpdateResponseFormatter> result,
            List<MVDataTree> mvRequest) throws Exception
    {
        HashMap<String, String> classMap = new HashMap();
        List<String> errors = new ArrayList<>();
        try {
            MODEL_VERSION_CREATION_CONTROLLER_LOGGER.info("Initiating classification========");
            List<HashMap<String, String>> classList = mvCreationProcessor.UpdateClassifcationMV(mvService, result, mvRequest, dsCall, context);
            if (!NullOrEmptyChecker.isNullOrEmpty(classList) && !classList.isEmpty()) {
                for (HashMap<String, String> cls : classList) {
                    System.out.println("cls =================");
                    cls.entrySet().forEach(entry -> {
                        System.out.println(entry.getKey() + " " + entry.getValue());
                    });

                    if (!NullOrEmptyChecker.isNullOrEmpty(cls.get("error"))) {
                        errors.add(cls.get("error"));
                    }
                }
            }
        }
        catch (Exception e) {
            errors = new ArrayList<>();
            errors.add(e.getMessage());
        }
        if (!errors.isEmpty()) {
            return errors;
        }

        return null;
    }

    private List<String> UpdateClassificationMVWithCheck(
            MVImportProcessor mvCreationProcessor,
            DsServiceCall dsCall, Context context,
            List<MVCreateUpdateResponseFormatter> result,
            List<MVDataTree> mvRequest) throws Exception
    {
        List<String> errors = new ArrayList<>();
        try {
            MODEL_VERSION_CREATION_CONTROLLER_LOGGER.info("Initiating classification========");
            List<HashMap<String, String>> classList = mvCreationProcessor.UpdateClassifcationMVWithCheck(mvService, result, mvRequest, dsCall, context);
            if (!NullOrEmptyChecker.isNullOrEmpty(classList) && !classList.isEmpty()) {
                for (HashMap<String, String> cls : classList) {
                    System.out.println("cls =================");
                    cls.entrySet().forEach(entry -> {
                        System.out.println(entry.getKey() + " " + entry.getValue());
                    });

                    if (!NullOrEmptyChecker.isNullOrEmpty(cls.get("error"))) {
                        errors.add(cls.get("error"));
                    }
                }
            }
        }
        catch (Exception e) {
            errors = new ArrayList<>();
            errors.add(e.getMessage());
        }
        if (!errors.isEmpty()) {
            return errors;
        }

        return null;
    }

    private void setVersionInClassIfNot(MVItemsImportDataBean mvRequest)
    {
        for (MVDataTree req : mvRequest.geDataTree()) {
            TNR tnr = req.getItem().getTnr();
            HashMap<String, String> attrMap = req.getItem().getAttributes();
            if (NullOrEmptyChecker.isNullOrEmpty(attrMap.get("version")) || NullOrEmptyChecker.isNullOrEmpty(attrMap.get("version"))) {
                attrMap.put("version", tnr.getRevision());
            }
        }
    }

    private void setMfgItemInfo(MVCreateUpdateResponseFormatter result,
                                List<HashMap<String, String>> itemList) throws Exception
    {
        //add mfgItemInfo with response for create
        for (HashMap<String, String> mfgItem : itemList) {
            if (mfgItem.get("type").equalsIgnoreCase(PropertyReader.getProperty("aton.childmfgitem.type"))) {
                result.setMfgItemCode(mfgItem.get("name") + "_" + mfgItem.get("revision"));
            } else {
                if (mfgItem.get("type").equalsIgnoreCase(PropertyReader.getProperty("aton.topmfgitem.type"))) {
                    result.setCtxMfgItemCode(mfgItem.get("name") + "_" + mfgItem.get("revision"));
                }
            }
        }

    }

}
