package com.bjit.common.rest.app.service.controller.enovia_cpq;

import com.bjit.common.rest.app.service.context.CreateContext;
import com.bjit.common.rest.app.service.controller.enovia_cpq.validator.MVRequestValidator;
import com.bjit.common.rest.app.service.enoviaCPQ.model.Item;
import com.bjit.common.rest.app.service.enoviaCPQ.service.CPQTransferService;
import com.bjit.common.rest.app.service.enoviaCPQ.service.MVEnoviaItemService;
import com.bjit.common.rest.app.service.enoviaCPQ.utilities.EmailSendUtil;
import com.bjit.common.rest.app.service.enoviaCPQ.utilities.FileProcessorUtils;
import com.bjit.common.rest.app.service.model.mvEnoviaItem.ItemInfo;
import com.bjit.common.rest.app.service.model.mvEnoviaItem.Items;
import com.bjit.common.rest.app.service.model.mvEnoviaItem.MVInfo;
import com.bjit.common.rest.app.service.model.mvEnoviaItem.MVResponseModel;
import com.bjit.common.rest.app.service.model.tnr.TNR;
import com.bjit.common.rest.app.service.payload.common_response.CustomResponseBuilder;
import com.bjit.common.rest.app.service.payload.common_response.IResponse;
import com.bjit.common.rest.app.service.payload.common_response.Status;
import com.bjit.common.rest.app.service.utilities.NullOrEmptyChecker;
import com.bjit.ewc18x.utils.PropertyReader;
import com.bjit.mapper.mapproject.util.Constants;
import java.net.MalformedURLException;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import matrix.db.Context;
import matrix.util.MatrixException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import org.springframework.web.bind.annotation.RequestParam;

import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author BJIT
 */
@RestController
public class EnoviaCPQController {

    private static final org.apache.log4j.Logger CPQ_SERVICE_CONTROLLER = org.apache.log4j.Logger.getLogger(EnoviaCPQController.class);

    @Autowired
    private CPQTransferService cpqTransferService;

    @Autowired
    private MVEnoviaItemService mvEnoviaItemService;

    @RequestMapping(value = "/bomExportToCPQ", method = RequestMethod.GET)
    @ResponseStatus(value = HttpStatus.OK)
    public void transferToCPQ(HttpServletRequest httpRequest, HttpServletResponse httpServletResponse) throws MalformedURLException, MatrixException, Exception {
        //  DeleteLockFileAction deleteLockFileAction = new DeleteLockFileAction();
        FileProcessorUtils fileProcessorUtils = new FileProcessorUtils();
        String triggerredFileDirectory = PropertyReader.getProperty("cpq.env.config.properties.dir");
        String filename = "";
        //  String cpqTransferResValue = "";
        HashMap<Item, String> transferItemResMap = new HashMap<>();
        HashMap<String, Item> processXMLFiles = new HashMap<>();
        try {
            CPQ_SERVICE_CONTROLLER.info("CPQ Transfer process Started\n\n");
            Map<String, ResponseEntity> bomExportjson = cpqTransferService.bomExportjson(httpRequest, httpServletResponse);
            if (!bomExportjson.isEmpty()) {
                processXMLFiles = fileProcessorUtils.processXMLFiles();
                for (Map.Entry<String, ResponseEntity> entry : bomExportjson.entrySet()) {
                    String cpqTransfer = "";
                    filename = entry.getKey();
                    if (filename.contains("=")) {
                        String fileNameStr[] = filename.split("=");
                        Item itemError = processXMLFiles.get(fileNameStr[0]);
                        itemError.setItemTransfer("error");
                        transferItemResMap.put(itemError, itemError.getMessage());
                        CPQ_SERVICE_CONTROLLER.info("Error Occured while transfering -- Error for msg in xml");
                        fileProcessorUtils.moveToFolder(triggerredFileDirectory + "/" + fileNameStr[0], "error");
                    } else {
                        try {

                            cpqTransfer = cpqTransferService.cpqTransfer(bomExportjson.get(filename));
                            CPQ_SERVICE_CONTROLLER.info("CPQ Response Body : " + cpqTransfer);
                            //  cpqTransferResValue = cpqTransfer;

                            if (!processXMLFiles.isEmpty()) {
                                Item item = processXMLFiles.get(filename);
                                item.setItemTransfer("success");
                                transferItemResMap.put(item, cpqTransfer);
                                fileProcessorUtils.moveToFolder(triggerredFileDirectory + "/" + filename, "old");
                                CPQ_SERVICE_CONTROLLER.info("CPQ Transfer process completed successfully\n\n");

                            }
                        } catch (Exception ex) {
                            Item item = processXMLFiles.get(filename);
                            item.setItemTransfer("error");
                            transferItemResMap.put(item, cpqTransfer);
                            CPQ_SERVICE_CONTROLLER.info("Error Occured while transfering -- " + ex.getMessage());
                            fileProcessorUtils.moveToFolder(triggerredFileDirectory + "/" + filename, "error");
                        }
                    }
                }
                EmailSendUtil emailSendUtil = new EmailSendUtil();
                emailSendUtil.emailSendingProcess(transferItemResMap);
            } else {
                CPQ_SERVICE_CONTROLLER.info("BomExport returns empty value \n");
            }

        } catch (Exception exp) {
            CPQ_SERVICE_CONTROLLER.error(exp);
            fileProcessorUtils.moveToFolder(triggerredFileDirectory + "/" + filename, "error");
            CPQ_SERVICE_CONTROLLER.info("File moved to error folder \n");

        } finally {

            try {
                //   deleteLockFileAction.deleteFile();
            } catch (Exception e) {
                CPQ_SERVICE_CONTROLLER.error("ERROR while deleting .Lock file!!");
                CPQ_SERVICE_CONTROLLER.error(e);
            }
        }
    }

//    @ResponseBody
//    @PostMapping(value = "/valmet/enovia/api/v1/getModelByEnoviaItem", produces = {MediaType.APPLICATION_JSON_VALUE}, consumes = {MediaType.APPLICATION_JSON_VALUE})
//    public ResponseEntity<Object> getMVByEnovia(@RequestBody Item item) {
//
//        String buildResponse = "";
//        
//        
//        
//        
//
//    }
//    @RequestMapping(value = "/deleteLockFile", method = RequestMethod.DELETE)
//    public ResponseEntity<Object> deleteLockFile() {
//        DeleteLockFileAction deleteLockFileAction = new DeleteLockFileAction();
//        try {
//            CPQ_SERVICE_CONTROLLER.info("Initiating .Lock file delete service...");
//            deleteLockFileAction.deleteFile();
//            return new ResponseEntity<>("Successfully Deleted .Lock file", HttpStatus.OK);
//        } catch (Exception e) {
//            CPQ_SERVICE_CONTROLLER.error("ERROR while deleting .Lock file!!");
//            CPQ_SERVICE_CONTROLLER.error(e);
//            return new ResponseEntity<>("Exception occured while deleting .Lock file", HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//    }
//
//    @RequestMapping(value = "/delete_scheduler_lock_file", method = RequestMethod.GET)
//    public ResponseEntity<Object> deleteSchedulerLockFile() {
//        DeleteLockFileAction deleteLockFileAction = new DeleteLockFileAction();
//        try {
//            CPQ_SERVICE_CONTROLLER.info("Initiating .Lock file delete service...");
//            deleteLockFileAction.deleteFile();
//            return new ResponseEntity<>("Successfully Deleted .Lock file", HttpStatus.OK);
//        } catch (Exception e) {
//            CPQ_SERVICE_CONTROLLER.error("ERROR while deleting .Lock file!!");
//            CPQ_SERVICE_CONTROLLER.error(e);
//            return new ResponseEntity<>("Exception occured while deleting .Lock file", HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//    }
    @ResponseBody
    @PostMapping(value = "/valmet/enovia/api/v1/getModelByEnoviaItem", produces = {MediaType.APPLICATION_JSON_VALUE}, consumes = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> getMVByEnoviaItem(@RequestBody Items items,
            @RequestParam(value = "match", required = false, defaultValue = "false") String match
    ) throws Exception {

        Instant startServiceTime = Instant.now();
        IResponse responseBuilder = new CustomResponseBuilder();
        String response = null;
        MVResponseModel mvResponseModel = new MVResponseModel();
        List<MVInfo> mvList = new ArrayList<>();
        List<ItemInfo> errorItemList = new ArrayList<>();
        Context context = null;

        try {
            CreateContext createContext = new CreateContext();
            context = createContext.getAdminContext();
            if (!context.isConnected()) {
                throw new Exception(Constants.CONTEXT_EXCEPTION);
            }
        } catch (Exception exp) {
            return new ResponseEntity<>(Constants.CONTEXT_EXCEPTION, HttpStatus.NOT_ACCEPTABLE);
        }
        try {
            //validate request data
            if (!MVRequestValidator.validateRequestData(items)) {
                response = responseBuilder.addErrorMessage("Invalid request data").setStatus(Status.FAILED).buildResponse();
                return new ResponseEntity<>(response, HttpStatus.OK);
            }
            List<ItemInfo> itemList = items.getItems();
            for (ItemInfo item : itemList) {
                //service calling
                Map<String, List<String>> modelByItem = mvEnoviaItemService.getModelByItem(context, item, match);
                CPQ_SERVICE_CONTROLLER.info("Model Information: " + modelByItem);

                if (modelByItem.size() == 0) {
                    ItemInfo itemInfo = new ItemInfo();

                    if (!NullOrEmptyChecker.isNull(item.getTnr()) && !item.getTnr().equals("")) {
                        itemInfo.setTnr(item.getTnr());
                        errorItemList.add(itemInfo);
                    } else {
                        itemInfo.setPhysicalid(item.getPhysicalid());
                        errorItemList.add(itemInfo);
                    }
                    continue;
                }
                for (Map.Entry<String, List<String>> entry : modelByItem.entrySet()) {
                    List<String> data = entry.getValue();

                    MVInfo mvInfo = new MVInfo();
                    mvInfo.setType(data.get(0));
                    mvInfo.setName(data.get(1));

                    if (data.size() > 2) {
                        mvInfo.setRevision(data.get(2));
                        mvInfo.setLife_cycle_status(data.get(3));
                        mvInfo.setAton_version(data.get(4));
                    }
                    mvList.add(mvInfo);
                }
            }
            if (mvList.size() > 0) {
                mvResponseModel.setItems(mvList);
            }
            if (!NullOrEmptyChecker.isNull(mvResponseModel.getItems())) {
                response = responseBuilder.setData(mvResponseModel).setStatus(Status.OK).buildResponse();
            }
            if (!NullOrEmptyChecker.isNullOrEmpty(errorItemList)) {
                response = responseBuilder.addErrorMessage(errorItemList).setStatus(Status.OK).buildResponse();
            }
            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (Exception ex) {
            CPQ_SERVICE_CONTROLLER.error(ex.getMessage());
            response = responseBuilder.addErrorMessage(ex.getMessage()).setStatus(Status.FAILED).buildResponse();
            return new ResponseEntity<>(response, HttpStatus.OK);
        } finally {
            context.close();
            Instant endServiceTime = Instant.now();
            CPQ_SERVICE_CONTROLLER.info("Time taken by service :" + Duration.between(startServiceTime, endServiceTime).toMillis());
        }
    }
}
