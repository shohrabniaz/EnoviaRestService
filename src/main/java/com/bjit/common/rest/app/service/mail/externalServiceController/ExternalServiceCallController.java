package com.bjit.common.rest.app.service.mail.externalServiceController;
import com.bjit.ex.integration.serviceactions.DeleteLockFileAction;
import com.bjit.ex.integration.transfer.actions.TransferAction;
import java.net.MalformedURLException;
import matrix.util.MatrixException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 *
 * @author BJIT
 */
@Controller
public class ExternalServiceCallController {
    private static final org.apache.log4j.Logger LN_SERVICE_CONTROLLER = org.apache.log4j.Logger.getLogger(ExternalServiceCallController.class);  
    @RequestMapping(value = "/bomExportToLN", method = RequestMethod.GET)
    @ResponseStatus(value = HttpStatus.OK)
    public void transferToLN() throws MalformedURLException, MatrixException {
        DeleteLockFileAction deleteLockFileAction = new DeleteLockFileAction();
        try {
            LN_SERVICE_CONTROLLER.info("LN Transfer process Started\n\n");
            TransferAction transferAction = new TransferAction();
            LN_SERVICE_CONTROLLER.info("LN Transfer process completed successfully\n\n");
        } catch (Exception exp) {
            LN_SERVICE_CONTROLLER.error(exp);
        }
        finally {
            LN_SERVICE_CONTROLLER.info("Deleting .Lock file");
            try {
                deleteLockFileAction.deleteFile();
            } catch (Exception e) {
                LN_SERVICE_CONTROLLER.error("ERROR while deleting .Lock file!!");
                LN_SERVICE_CONTROLLER.error(e);
            }
        }
    }
    
    @RequestMapping(value = "/deleteLockFile", method = RequestMethod.DELETE)
    public ResponseEntity<Object> deleteLockFile(){
        DeleteLockFileAction deleteLockFileAction = new DeleteLockFileAction();
        try {
            LN_SERVICE_CONTROLLER.info("Initiating .Lock file delete service...");
            deleteLockFileAction.deleteFile();
            return new ResponseEntity<>("Successfully Deleted .Lock file", HttpStatus.OK);
        } catch (Exception e) {
            LN_SERVICE_CONTROLLER.error("ERROR while deleting .Lock file!!");
            LN_SERVICE_CONTROLLER.error(e);
            return new ResponseEntity<>("Exception occured while deleting .Lock file", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @RequestMapping(value = "/delete_scheduler_lock_file", method = RequestMethod.GET)
    public ResponseEntity<Object> deleteSchedulerLockFile(){
        DeleteLockFileAction deleteLockFileAction = new DeleteLockFileAction();
        try {
            LN_SERVICE_CONTROLLER.info("Initiating .Lock file delete service...");
            deleteLockFileAction.deleteFile();
            return new ResponseEntity<>("Successfully Deleted .Lock file", HttpStatus.OK);
        } catch (Exception e) {
            LN_SERVICE_CONTROLLER.error("ERROR while deleting .Lock file!!");
            LN_SERVICE_CONTROLLER.error(e);
            return new ResponseEntity<>("Exception occured while deleting .Lock file", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
