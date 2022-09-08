package com.bjit.common.rest.app.service.controller.ln.dw;

import com.bjit.common.rest.app.service.payload.common_response.CustomResponseBuilder;
import com.bjit.common.rest.app.service.payload.common_response.IResponse;
import com.bjit.common.rest.app.service.payload.common_response.Status;
import com.bjit.common.rest.app.service.utilities.DateTimeUtils;
import com.bjit.common.rest.app.service.utilities.NullOrEmptyChecker;
import com.bjit.dw.enovia.action.DWDataFetchAction;
import com.bjit.dw.enovia.utility.WeightUtil;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author BJIT/Mashuk
 */
@RestController
public class LNDWController {
    private static final Logger DW_LOGGER = Logger.getLogger(LNDWController.class);
    
    @ResponseBody
    @GetMapping(value = "/ln/dw/v1/api/data/{itemNameList}", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> getSelectedItemInformationFromDW(HttpServletRequest httpRequest, HttpServletResponse httpResponse, @PathVariable String[] itemNameList) {
        DW_LOGGER.info("---------------------- ||| DW DATA FETCH START ||| ----------------------");
        DW_LOGGER.info("##########################################################################");
        
        Instant dwFetchStartTime = Instant.now();
        IResponse responseBuilder = new CustomResponseBuilder();
        String buildResponse;
        
        try {
            String lastExecutionDateTime = httpRequest.getHeader("lastExecutionDateTime");
        
            if(NullOrEmptyChecker.isNullOrEmpty(lastExecutionDateTime)) {
                buildResponse = responseBuilder.setStatus(Status.FAILED).addErrorMessage("lastExecutionDateTime can't be empty or null").buildResponse();
                return new ResponseEntity(buildResponse, HttpStatus.NOT_ACCEPTABLE);
            }
            
            if(!NullOrEmptyChecker.isNullOrEmpty(itemNameList)) {
                List<String> itemList = Arrays.asList(itemNameList);
                DWDataFetchAction dataFetchAction = new DWDataFetchAction();
                
                try {
                    DW_LOGGER.info(":: Service Request ::");
                    DW_LOGGER.info("Item List : " + itemList + " lastExecutionDateTime: " + lastExecutionDateTime);
                    List<Map<String, String>> itemResultList = dataFetchAction.fetchDWData(itemList, lastExecutionDateTime);
                    buildResponse = responseBuilder.setStatus(Status.OK).setData(itemResultList).buildResponse();
                    return new ResponseEntity(buildResponse, HttpStatus.OK);
                } catch (RuntimeException | SQLException ex) {
                    DW_LOGGER.error(ex.getMessage());
                    buildResponse = responseBuilder.setStatus(Status.FAILED).addErrorMessage(ex.getMessage()).buildResponse();
                    return new ResponseEntity(buildResponse, HttpStatus.NOT_ACCEPTABLE);
                }
            } else {
                buildResponse = responseBuilder.setStatus(Status.FAILED).addErrorMessage("Item List can't be empty or null").buildResponse();
                return new ResponseEntity(buildResponse, HttpStatus.NOT_ACCEPTABLE);
            }
        } catch (Exception ex) {
            DW_LOGGER.error("Error occured during dw data fetch : " + ex.getMessage());
            buildResponse = responseBuilder.addErrorMessage(ex.getMessage()).setStatus(Status.FAILED).buildResponse();
            return new ResponseEntity(buildResponse, HttpStatus.NOT_ACCEPTABLE);
        } finally {
            Instant dwFetchEndTime = Instant.now();
            long duration = DateTimeUtils.getDuration(dwFetchStartTime, dwFetchEndTime);
            
            DW_LOGGER.info("DW Data Fetch Process has taken : '" + duration + "' milli-seconds");
            DW_LOGGER.info("########################################################################");
            DW_LOGGER.info("---------------------- ||| DW DATA FETCH END ||| ----------------------");
        }
    }
    
    @ResponseBody
    @GetMapping(value = "/ln/dw/v1/api/data/all", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> getAllItemInformationFromDW(HttpServletRequest httpRequest, HttpServletResponse httpResponse) {
        DW_LOGGER.info("---------------------- ||| DW DATA FETCH START ||| ----------------------");
        DW_LOGGER.info("##########################################################################");
        
        Instant dwFetchStartTime = Instant.now();
        IResponse responseBuilder = new CustomResponseBuilder();
        String buildResponse;
        
        try {
            String lastExecutionDateTime = httpRequest.getHeader("lastExecutionDateTime");

            if(NullOrEmptyChecker.isNullOrEmpty(lastExecutionDateTime)) {
                buildResponse = responseBuilder.setStatus(Status.FAILED).addErrorMessage("lastExecutionDateTime can't be empty or null").buildResponse();
                return new ResponseEntity(buildResponse, HttpStatus.NOT_ACCEPTABLE);
            }

            DWDataFetchAction dataFetchAction = new DWDataFetchAction();

            try {
                List<Map<String, String>> itemResultList = dataFetchAction.fetchDWData(lastExecutionDateTime);
                buildResponse = responseBuilder.setStatus(Status.OK).setData(itemResultList).buildResponse();
                return new ResponseEntity(buildResponse, HttpStatus.OK);
            } catch (RuntimeException | SQLException ex) {
                DW_LOGGER.error(ex.getMessage());
                buildResponse = responseBuilder.setStatus(Status.FAILED).addErrorMessage(ex.getMessage()).buildResponse();
                return new ResponseEntity(buildResponse, HttpStatus.NOT_ACCEPTABLE);
            }
        } catch (Exception ex) {
            DW_LOGGER.error("Error occured during dw data fetch : " + ex.getMessage());
            buildResponse = responseBuilder.addErrorMessage(ex.getMessage()).setStatus(Status.FAILED).buildResponse();
            return new ResponseEntity(buildResponse, HttpStatus.NOT_ACCEPTABLE);
        } finally {
            Instant dwFetchEndTime = Instant.now();
            long duration = DateTimeUtils.getDuration(dwFetchStartTime, dwFetchEndTime);
            
            DW_LOGGER.info("DW Data Fetch Process has taken : '" + duration + "' milli-seconds");
            DW_LOGGER.info("########################################################################");
            DW_LOGGER.info("---------------------- ||| DW DATA FETCH END ||| ----------------------");
        }
    }
    
    @ResponseBody
    @GetMapping(value = "/v1/api/ln/weight/data", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> getWeightInformationFromLN(HttpServletRequest httpRequest, HttpServletResponse httpResponse) {
        DW_LOGGER.info("---------------------- ||| WEIGHT DATA FETCH START ||| ----------------------");
        DW_LOGGER.info("##########################################################################");
        
        Instant dataFetchStartTime = Instant.now();
        IResponse responseBuilder = new CustomResponseBuilder();
        String buildResponse;
        
        try {
            String startDateTime = httpRequest.getHeader("startDateTime");
            String endDateTime = httpRequest.getHeader("endDateTime");

            if(NullOrEmptyChecker.isNullOrEmpty(startDateTime)) {
                buildResponse = responseBuilder.setStatus(Status.FAILED).addErrorMessage("startDateTime can't be empty or null").buildResponse();
                return new ResponseEntity(buildResponse, HttpStatus.NOT_ACCEPTABLE);
            }
            if(NullOrEmptyChecker.isNullOrEmpty(endDateTime)) {
                buildResponse = responseBuilder.setStatus(Status.FAILED).addErrorMessage("endDateTime can't be empty or null").buildResponse();
                return new ResponseEntity(buildResponse, HttpStatus.NOT_ACCEPTABLE);
            }
            
            DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            try {
                sdf.parse(startDateTime);
            } catch (ParseException e) {
                buildResponse = responseBuilder.setStatus(Status.FAILED).addErrorMessage("System couldn't recognize 'startDateTime' format. Correct format: yyyy-MM-dd HH:mm:ss").buildResponse();
                return new ResponseEntity(buildResponse, HttpStatus.NOT_ACCEPTABLE);
            }

            try {
                sdf.parse(endDateTime);
            } catch (ParseException e) {
                buildResponse = responseBuilder.setStatus(Status.FAILED).addErrorMessage("System couldn't recognize 'endDateTime' format. Correct format: yyyy-MM-dd HH:mm:ss").buildResponse();
                return new ResponseEntity(buildResponse, HttpStatus.NOT_ACCEPTABLE);
            }

            WeightUtil weightUtil = new WeightUtil();
            try {
                List<HashMap<String, String>> itemResultList = weightUtil.getResult(startDateTime, endDateTime);
                DW_LOGGER.error("Response: "+itemResultList.toString());
                if(itemResultList.size() < 1) {
                    buildResponse = responseBuilder.setStatus(Status.FAILED).addErrorMessage("No data found").buildResponse();
                    return new ResponseEntity(buildResponse, HttpStatus.NOT_FOUND);
                } else {
                    buildResponse = responseBuilder.setStatus(Status.OK).setData(itemResultList).buildResponse();
                    return new ResponseEntity(buildResponse, HttpStatus.OK);
                }
            } catch (RuntimeException | SQLException ex) {
                DW_LOGGER.error(ex.getMessage());
                buildResponse = responseBuilder.setStatus(Status.FAILED).addErrorMessage(ex.getMessage()).buildResponse();
                return new ResponseEntity(buildResponse, HttpStatus.NOT_ACCEPTABLE);
            }
        } catch (Exception ex) {
            DW_LOGGER.error("Error occured during weight data fetch : " + ex.getMessage());
            buildResponse = responseBuilder.addErrorMessage(ex.getMessage()).setStatus(Status.FAILED).buildResponse();
            return new ResponseEntity(buildResponse, HttpStatus.NOT_ACCEPTABLE);
        } finally {
            Instant dataFetchEndTime = Instant.now();
            long duration = DateTimeUtils.getDuration(dataFetchStartTime, dataFetchEndTime);
            
            DW_LOGGER.info("Weight Data Fetch Process has taken : '" + duration + "' milli-seconds");
            DW_LOGGER.info("########################################################################");
            DW_LOGGER.info("---------------------- ||| DW DATA FETCH END ||| ----------------------");
        }
    }
}