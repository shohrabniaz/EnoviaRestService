package com.bjit.common.rest.app.service.controller.enovia_cpq;

import com.bjit.common.rest.app.service.enoviaCPQ.model.Item;
import com.bjit.common.rest.app.service.enoviaCPQ.service.CPQTransferService;
import com.bjit.common.rest.app.service.enoviaCPQ.utilities.EmailSendUtil;
import com.bjit.common.rest.app.service.enoviaCPQ.utilities.FileProcessorUtils;

import com.bjit.ewc18x.utils.PropertyReader;
import com.bjit.ex.integration.serviceactions.DeleteLockFileAction;
import com.bjit.ex.integration.transfer.actions.TransferAction;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import matrix.util.MatrixException;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
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

    @RequestMapping(value = "/bomExportToCPQ", method = RequestMethod.GET)
    @ResponseStatus(value = HttpStatus.OK)
    public void transferToCPQ(HttpServletRequest httpRequest, HttpServletResponse httpServletResponse) throws MalformedURLException, MatrixException {
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
                for (Map.Entry<String, ResponseEntity> entry : bomExportjson.entrySet()) {
                    String cpqTransfer = "";
                    try {
                        filename = entry.getKey();

                        cpqTransfer = cpqTransferService.cpqTransfer(bomExportjson.get(filename));
                        CPQ_SERVICE_CONTROLLER.info("CPQ Response Body : " + cpqTransfer);
                        //  cpqTransferResValue = cpqTransfer;

                        processXMLFiles = fileProcessorUtils.processXMLFiles();

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
}
