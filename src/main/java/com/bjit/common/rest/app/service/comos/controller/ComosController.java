/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.app.service.comos.controller;

import com.bjit.common.rest.app.service.aspects.annotations.LogExecutionTime;
import com.bjit.common.rest.app.service.constants.ItemImportEnvironments;
import com.bjit.common.rest.app.service.payload.common_response.CustomResponseBuilder;
import com.bjit.common.rest.app.service.payload.common_response.IResponse;
import com.bjit.common.rest.app.service.payload.common_response.Status;
import com.bjit.common.rest.app.service.utilities.CommonPropertyReader;
import com.bjit.common.rest.app.service.utilities.DateTimeUtils;
import com.bjit.common.rest.app.service.utilities.NullOrEmptyChecker;
import com.bjit.common.rest.app.service.comos.models.ComosObjectDataBean;
import com.bjit.common.rest.app.service.model.createBOM.BOMStructure;
import com.bjit.common.rest.app.service.model.createBOM.CreateBOMBean;
import com.bjit.common.rest.app.service.model.itemImport.DataTree;
import com.bjit.common.rest.app.service.utilities.JSON;
import com.bjit.common.rest.item_bom_import.factories.ItemOrBOMImportFactoryProducer;
import com.bjit.common.rest.item_bom_import.factories.ItemOrBOMAbstractFactory;
import com.bjit.common.rest.item_bom_import.import_interfaces.ItemOrBOMImport;
import com.bjit.common.rest.item_bom_import.utility.bomResponseMessageFormatter.ParentInfo;
import com.bjit.common.rest.item_bom_import.xml_mapping_model.ResponseMessageFormaterBean;
import com.matrixone.apps.domain.util.FrameworkException;
import com.matrixone.apps.domain.util.MqlUtil;
import java.time.Instant;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import matrix.db.Context;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.apache.log4j.Logger;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author BJIT
 */
//@RestController
//@RequestMapping(path = "/comos")
public class ComosController {

    private static final Logger COMOS_CONTROLLER_LOGGER = Logger.getLogger(ComosController.class);
    @Autowired
    CommonPropertyReader commonPropertyReader;

    @LogExecutionTime
    @ResponseBody
    @RequestMapping(value = "/logi/v1/item/create", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
    public ResponseEntity createItem(HttpServletRequest httpRequest, @RequestBody final ComosObjectDataBean objectDataBeanList) {
        Date controllerStartTime = DateTimeUtils.getTime(new Date());
        Instant itemImportStartTime = Instant.now();
        COMOS_CONTROLLER_LOGGER.info("---------------------------------------- ||| IMPORT COMOS ITEM CONTROLLER BEGIN ||| ----------------------------------------");
        COMOS_CONTROLLER_LOGGER.info("#############################################################################################################################\n");

        IResponse responseBuilder = new CustomResponseBuilder();
        String buildResponse;
        final Context context = (Context) httpRequest.getAttribute("context");
        try {
            ItemOrBOMAbstractFactory itemOrBOMCreateFactory = ItemOrBOMImportFactoryProducer.getFactory(ItemImportEnvironments.COMOS);
            ItemOrBOMImport commonItemImportFactory = itemOrBOMCreateFactory.getImportType(ItemImportEnvironments.COMOS);

            HashMap<String, List<ResponseMessageFormaterBean>> tnrListMap = commonItemImportFactory.doImport(context, objectDataBeanList);

            List<ResponseMessageFormaterBean> successFulItemList = tnrListMap.get("successFullList");
            List<ResponseMessageFormaterBean> errorItemList = tnrListMap.get("errorList");
            Boolean hasSuccessfulList = !NullOrEmptyChecker.isNullOrEmpty(successFulItemList);
            Boolean hasErrorList = !NullOrEmptyChecker.isNullOrEmpty(errorItemList);

            if (hasSuccessfulList && hasErrorList) {
                buildResponse = responseBuilder
                        .setStatus(Status.FAILED)
                        .setData(successFulItemList)
                        .addErrorMessage(errorItemList)
                        .buildResponse();
            } else if (hasSuccessfulList && !hasErrorList) {
                buildResponse = responseBuilder
                        .setStatus(Status.OK)
                        .setData(successFulItemList)
                        .buildResponse();
            } else if (!hasSuccessfulList && hasErrorList) {
                buildResponse = responseBuilder
                        .setStatus(Status.FAILED)
                        .addErrorMessage(errorItemList)
                        .buildResponse();
            } else {
                COMOS_CONTROLLER_LOGGER.fatal("Unknown excepiton occurred");
                throw new RuntimeException("Unknown excepiton occurred");
            }

            COMOS_CONTROLLER_LOGGER.debug(buildResponse);
            return new ResponseEntity<>(buildResponse, HttpStatus.OK);
        } catch (NullPointerException exp) {
            buildResponse = responseBuilder.addErrorMessage(exp.getMessage()).setStatus(Status.FAILED).buildResponse();
            COMOS_CONTROLLER_LOGGER.debug(buildResponse);
            return new ResponseEntity<>(buildResponse, HttpStatus.NOT_ACCEPTABLE);
        } catch (RuntimeException exp) {
            buildResponse = responseBuilder.addErrorMessage(exp.getMessage()).setStatus(Status.FAILED).buildResponse();
            COMOS_CONTROLLER_LOGGER.debug(buildResponse);
            return new ResponseEntity<>(buildResponse, HttpStatus.NOT_ACCEPTABLE);
        } catch (Exception exp) {
            buildResponse = responseBuilder.addErrorMessage(exp.getMessage()).setStatus(Status.FAILED).buildResponse();
            COMOS_CONTROLLER_LOGGER.debug(buildResponse);
            return new ResponseEntity<>(buildResponse, HttpStatus.NOT_ACCEPTABLE);
        } finally {
            Date controllerEndTime = DateTimeUtils.getTime(new Date());
            COMOS_CONTROLLER_LOGGER.debug("Time elapsed for the object type 'createManItem' service is : " + DateTimeUtils.elapsedTime(controllerStartTime, controllerEndTime, null, null));

            Instant itemImportEndTime = Instant.now();
            long duration = DateTimeUtils.getDuration(itemImportStartTime, itemImportEndTime);

            COMOS_CONTROLLER_LOGGER.info("ODI Item Import Process has taken : '" + duration + "' milli-seconds");
            COMOS_CONTROLLER_LOGGER.info("---------------------------------------- ||| IMPORT MOPAZ ITEM CONTROLLER END ||| ----------------------------------------");
            COMOS_CONTROLLER_LOGGER.info("###########################################################################################################################\n");
        }
    }

    @PostMapping("/logi/v1/bom/create")
    public ResponseEntity createBOM(HttpServletRequest httpRequest, @RequestBody final List<CreateBOMBean> createBOMBeanList) {
        Instant itemImportStartTime = Instant.now();
        COMOS_CONTROLLER_LOGGER.debug("---------------------- ||| BOM IMPORT ||| ----------------------");
        COMOS_CONTROLLER_LOGGER.debug("####################################################################");
        IResponse responseBuilder = new CustomResponseBuilder();
        String buildResponse = "";
        final Context context = (Context) httpRequest.getAttribute("context");
        final String source = "comos";
        BOMStructure bomStructureModel = new BOMStructure();
        bomStructureModel.setSource(source);
        bomStructureModel.setCreateBomBeanList(createBOMBeanList);

        try {
            ItemOrBOMAbstractFactory bomFactory = ItemOrBOMImportFactoryProducer.getFactory(source);
            ItemOrBOMImport comosBomImport = bomFactory.getImportType(source + "-bom");

            HashMap<String, List<ParentInfo>> responseMsgMap = comosBomImport.doImport(context, bomStructureModel);

            List<ParentInfo> successFulItemList = responseMsgMap.get("Successful");
            List<ParentInfo> errorItemList = responseMsgMap.get("Error");

            if (errorItemList != null && errorItemList.size() > 0) {
                buildResponse = responseBuilder.addErrorMessage(errorItemList).setStatus(Status.FAILED).buildResponse();
            } else if (successFulItemList != null && successFulItemList.size() > 0) {
                buildResponse = responseBuilder.setData(successFulItemList).setStatus(Status.OK).buildResponse();
            }

            COMOS_CONTROLLER_LOGGER.debug(buildResponse);
            return new ResponseEntity<>(buildResponse, HttpStatus.OK);
        } catch (Exception exp) {
            COMOS_CONTROLLER_LOGGER.error(exp);
            buildResponse = responseBuilder.addErrorMessage(exp.getMessage()).setStatus(Status.FAILED).buildResponse();
            COMOS_CONTROLLER_LOGGER.debug(buildResponse);
            return new ResponseEntity<>(buildResponse, HttpStatus.OK);
        } finally {
            Instant itemImportEndTime = Instant.now();
            long duration = DateTimeUtils.getDuration(itemImportStartTime, itemImportEndTime);
            COMOS_CONTROLLER_LOGGER.info(" | Process Time | Total BOM Import | " + duration);
        }
    }

    @RequestMapping(value = "/logi/v1/items/create", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
    public ResponseEntity createItems(HttpServletRequest httpRequest, @RequestBody final ComosObjectDataBean objectDataBeanList) {
        Date controllerStartTime = DateTimeUtils.getTime(new Date());
        Instant itemImportStartTime = Instant.now();
        COMOS_CONTROLLER_LOGGER.info("---------------------------------------- ||| IMPORT COMOS ITEM CONTROLLER BEGIN ||| ----------------------------------------");
        COMOS_CONTROLLER_LOGGER.info("#############################################################################################################################\n");

        JSON json = new JSON();
        String serialized = json.serialize(objectDataBeanList);
        ComosObjectDataBean newBackupItem = json.deserialize(serialized, ComosObjectDataBean.class);
        List<DataTree> geDataTree = objectDataBeanList.geDataTree();
        geDataTree.subList(1, geDataTree.size()).clear();

        IResponse responseBuilder = new CustomResponseBuilder();
        String buildResponse;
        final Context context = (Context) httpRequest.getAttribute("context");
        try {
            ItemOrBOMAbstractFactory itemOrBOMCreateFactory = ItemOrBOMImportFactoryProducer.getFactory(ItemImportEnvironments.COMOS);
            ItemOrBOMImport commonItemImportFactory = itemOrBOMCreateFactory.getImportType(ItemImportEnvironments.COMOS);

            HashMap<String, List<ResponseMessageFormaterBean>> tnrListMap = commonItemImportFactory.doImport(context, objectDataBeanList);

            List<ResponseMessageFormaterBean> successFulItemList = tnrListMap.get("successFullList");
            
            updateNames(context, successFulItemList, newBackupItem);
            
            
            List<ResponseMessageFormaterBean> errorItemList = tnrListMap.get("errorList");
            Boolean hasSuccessfulList = !NullOrEmptyChecker.isNullOrEmpty(successFulItemList);
            Boolean hasErrorList = !NullOrEmptyChecker.isNullOrEmpty(errorItemList);

            if (hasSuccessfulList && hasErrorList) {
                buildResponse = responseBuilder
                        .setStatus(Status.FAILED)
                        .setData(successFulItemList)
                        .addErrorMessage(errorItemList)
                        .buildResponse();
            } else if (hasSuccessfulList && !hasErrorList) {
                buildResponse = responseBuilder
                        .setStatus(Status.OK)
                        .setData(successFulItemList)
                        .buildResponse();
            } else if (!hasSuccessfulList && hasErrorList) {
                buildResponse = responseBuilder
                        .setStatus(Status.FAILED)
                        .addErrorMessage(errorItemList)
                        .buildResponse();
            } else {
                COMOS_CONTROLLER_LOGGER.fatal("Unknown excepiton occurred");
                throw new RuntimeException("Unknown excepiton occurred");
            }

            COMOS_CONTROLLER_LOGGER.debug(buildResponse);
            return new ResponseEntity<>(buildResponse, HttpStatus.OK);
        } catch (NullPointerException exp) {
            buildResponse = responseBuilder.addErrorMessage(exp.getMessage()).setStatus(Status.FAILED).buildResponse();
            COMOS_CONTROLLER_LOGGER.debug(buildResponse);
            return new ResponseEntity<>(buildResponse, HttpStatus.NOT_ACCEPTABLE);
        } catch (RuntimeException exp) {
            buildResponse = responseBuilder.addErrorMessage(exp.getMessage()).setStatus(Status.FAILED).buildResponse();
            COMOS_CONTROLLER_LOGGER.debug(buildResponse);
            return new ResponseEntity<>(buildResponse, HttpStatus.NOT_ACCEPTABLE);
        } catch (Exception exp) {
            buildResponse = responseBuilder.addErrorMessage(exp.getMessage()).setStatus(Status.FAILED).buildResponse();
            COMOS_CONTROLLER_LOGGER.debug(buildResponse);
            return new ResponseEntity<>(buildResponse, HttpStatus.NOT_ACCEPTABLE);
        } finally {
            Date controllerEndTime = DateTimeUtils.getTime(new Date());
            COMOS_CONTROLLER_LOGGER.debug("Time elapsed for the object type 'createManItem' service is : " + DateTimeUtils.elapsedTime(controllerStartTime, controllerEndTime, null, null));

            Instant itemImportEndTime = Instant.now();
            long duration = DateTimeUtils.getDuration(itemImportStartTime, itemImportEndTime);

            COMOS_CONTROLLER_LOGGER.info("ODI Item Import Process has taken : '" + duration + "' milli-seconds");
            COMOS_CONTROLLER_LOGGER.info("---------------------------------------- ||| IMPORT MOPAZ ITEM CONTROLLER END ||| ----------------------------------------");
            COMOS_CONTROLLER_LOGGER.info("###########################################################################################################################\n");
        }
    }

    void updateNames(Context context, List<ResponseMessageFormaterBean> successFulItemList, ComosObjectDataBean objectDataBeanList) throws FrameworkException {
        ResponseMessageFormaterBean responseBean = successFulItemList.get(0);
        String objectId = responseBean.getObjectId();

        String mql = "expand bus " + objectId + " from rel * withroots recurs to all select bus id dump |";
        String mqlCommand = MqlUtil.mqlCommand(context, mql);
        List<String> itemsList = Arrays.asList(mqlCommand.split("\n"));
        
        List<DataTree> geDataTree = objectDataBeanList.geDataTree();
        
        String unit = itemsList.get(1); // unit
        String subUnit = itemsList.get(2); // sub unit
        String equipment = itemsList.get(4); // equipment
        String[] unitSplited = unit.split("\\|");
        String[] subUnitSplited = subUnit.split("\\|");
        String[] equipmentSplited = equipment.split("\\|");
        String unitId = unitSplited[unitSplited.length - 1];
        String subUnitId = subUnitSplited[subUnitSplited.length - 1];
        String equipmentId = equipmentSplited[equipmentSplited.length - 1];
        
        String unitNameSearching = geDataTree.stream().filter(unitName -> unitName.getItem().getTnr().getType().equalsIgnoreCase("unit")).map(unitName -> unitName.getItem().getTnr().getName()).findFirst().get();
        String unitNameModMQL = "mod bus " + unitId + " PLMEntity.V_Name " + unitNameSearching + "";
        String updateUnitName = MqlUtil.mqlCommand(context, unitNameModMQL);
        
        String subunitNameSearching = geDataTree.stream().filter(subunitName -> subunitName.getItem().getTnr().getType().equalsIgnoreCase("Sub Unit")).map(subunitName -> subunitName.getItem().getTnr().getName()).findFirst().get();
        String subunitNameModMQL = "mod bus " + subUnitId + " PLMEntity.V_Name " + subunitNameSearching + "";
        String updatesubUnitName = MqlUtil.mqlCommand(context, subunitNameModMQL);
        
        String equipmentNameSearching = geDataTree.stream().filter(unitName -> unitName.getItem().getTnr().getType().equalsIgnoreCase("Device Position")).map(unitName -> unitName.getItem().getTnr().getName()).findFirst().get();
        String equipmentNameModMQL = "mod bus " + equipmentId + " PLMEntity.V_Name " + equipmentNameSearching + "";
        String updateequipmentName = MqlUtil.mqlCommand(context, equipmentNameModMQL);

    }
}
