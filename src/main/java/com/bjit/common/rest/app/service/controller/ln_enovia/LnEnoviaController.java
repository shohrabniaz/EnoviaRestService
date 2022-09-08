package com.bjit.common.rest.app.service.controller.ln_enovia;

import com.bjit.common.rest.app.service.controller.ln_enovia.utilities.LnDataFetchUtil;
import com.bjit.common.rest.app.service.model.ln_enovia.LNCostData;
import com.bjit.common.rest.app.service.payload.common_response.CustomResponseBuilder;
import com.bjit.common.rest.app.service.payload.common_response.IResponse;
import com.bjit.common.rest.app.service.payload.common_response.Status;
import com.bjit.common.rest.app.service.service.ln_enovia.LNCostService;
import com.bjit.common.rest.app.service.utilities.ln_enovia.LNCommonUtil;
import com.bjit.ewc18x.utils.PropertyReader;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author BJIT
 */
@RequestMapping(value = "/valmet/enovia/api/items")
@RestController
public class LnEnoviaController {

    private static final Logger LN_ENOVIA_CONTROLLER = Logger.getLogger(LnEnoviaController.class);

    @Autowired
    private LNCostService lnCostService;

    @GetMapping(path = "/v1/ln/cost/data", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity getCostDataFromLN(HttpServletRequest httpRequest) {
        IResponse responseBuilder = new CustomResponseBuilder();
        String buildResponse;

        try {
            String startAtDateTime = httpRequest.getHeader("startAt");
            LnDataFetchUtil.validateCostBeforeDateTime(startAtDateTime);

            List<LNCostData> costData = lnCostService.getCostData(httpRequest.getHeader("startAt"), "", "");

            buildResponse = responseBuilder.setStatus(Status.OK).setData(costData).buildResponse();
            return new ResponseEntity<>(buildResponse, HttpStatus.OK);
        } catch (Exception e) {
            LN_ENOVIA_CONTROLLER.error(e);
            buildResponse = responseBuilder.setStatus(Status.FAILED).addErrorMessage(e.getMessage()).buildResponse();
            return new ResponseEntity<>(buildResponse, HttpStatus.OK);
        }
    }

    @GetMapping(path = "/nightly/cost-data")
    public ResponseEntity nightlyUpdateCostData() throws InterruptedException, Exception {
        IResponse responseBuilder = new CustomResponseBuilder();
        String buildResponse;
        List<String> response;
        StringBuilder items = new StringBuilder();
        try {
            String startDate = LNCommonUtil.getCostDataUpdateStartDate();
            LN_ENOVIA_CONTROLLER.info("Cost Data Update Start Time: " + startDate);

            List<LNCostData> costData = lnCostService.getCostData(startDate, "", "");
            if (costData.size() > 0) {
                response = lnCostService.updateCostData(costData);
                if (response.size() < 1) {
                    LN_ENOVIA_CONTROLLER.info("No Cost Data Found");
                    buildResponse = responseBuilder.setStatus(Status.OK).setData("No Cost Data Found").buildResponse();
                    return new ResponseEntity<>(buildResponse, HttpStatus.OK);
                } else {
                    response.forEach(item -> {
                        items.append(item);
                        items.append(",");
                    });
                    items.deleteCharAt(items.lastIndexOf(","));
                }
            } else {
                LN_ENOVIA_CONTROLLER.info("No Cost Data Found");
                buildResponse = responseBuilder.setStatus(Status.OK).setData("No Cost Data Found").buildResponse();
                return new ResponseEntity<>(buildResponse, HttpStatus.OK);
            }

            LN_ENOVIA_CONTROLLER.info("Failed to Updated Cost Data to PLM");
            LNCommonUtil.updateCostUpdateDate(PropertyReader.getProperty("ln.cost.data.sucessful.update.date.key"));
            buildResponse = responseBuilder.setStatus(Status.OK).setData(items).buildResponse();
            return new ResponseEntity<>(buildResponse, HttpStatus.OK);

        } catch (Exception e) {
            LN_ENOVIA_CONTROLLER.error(e);
            if (e.getMessage().equalsIgnoreCase(PropertyReader.getProperty("cost.data.update.error.message"))) {
                LN_ENOVIA_CONTROLLER.info("No Cost Data Found");
                buildResponse = responseBuilder.setStatus(Status.OK).setData("No Cost Data Found").buildResponse();
                return new ResponseEntity<>(buildResponse, HttpStatus.OK);
            } else {
                LN_ENOVIA_CONTROLLER.info("Failed to Updated Cost Data to PLM");
                LNCommonUtil.updateCostUpdateDate(PropertyReader.getProperty("ln.cost.data.failed.update.date.key"));
                buildResponse = responseBuilder.setStatus(Status.FAILED).addErrorMessage(e.getMessage()).buildResponse();
                return new ResponseEntity<>(buildResponse, HttpStatus.OK);
            }
        }
    }

    @GetMapping(path = "/updateCostDataForItem")
    public ResponseEntity getCostDataByItem(@RequestParam(value = "Itemid", required = true) String itemId) throws InterruptedException, Exception {
        IResponse responseBuilder = new CustomResponseBuilder();
        String buildResponse;
        List<String> response;
        StringBuilder items = new StringBuilder();
        try {
            List<LNCostData> costData = lnCostService.getCostData("", itemId, "byItem");
            if (costData.size() > 0) {
                response = lnCostService.updateCostData(costData);
                if (response.size() < 1) {
                    LN_ENOVIA_CONTROLLER.info("No Cost Data Found");
                    buildResponse = responseBuilder.setStatus(Status.OK).setData("No Cost Data Found").buildResponse();
                    return new ResponseEntity<>(buildResponse, HttpStatus.OK);
                } else {
                    response.forEach(item -> {
                        items.append(item);
                        items.append(",");
                    });
                    items.deleteCharAt(items.lastIndexOf(","));
                }
            } else {
                LN_ENOVIA_CONTROLLER.info("No Cost Data Found");
                buildResponse = responseBuilder.setStatus(Status.OK).setData("No Cost Data Found").buildResponse();
                return new ResponseEntity<>(buildResponse, HttpStatus.OK);
            }

            LN_ENOVIA_CONTROLLER.info("Successfully Updated Cost Data to PLM");
            buildResponse = responseBuilder.setStatus(Status.OK).setData(items).buildResponse();
            return new ResponseEntity<>(buildResponse, HttpStatus.OK);

        } catch (Exception e) {
            LN_ENOVIA_CONTROLLER.error(e);
            if (e.getMessage().equalsIgnoreCase(PropertyReader.getProperty("cost.data.update.error.message"))) {
                LN_ENOVIA_CONTROLLER.info("No Cost Data Found");
                buildResponse = responseBuilder.setStatus(Status.OK).setData("No Cost Data Found").buildResponse();
                return new ResponseEntity<>(buildResponse, HttpStatus.OK);
            } else {
                LN_ENOVIA_CONTROLLER.info("Failed to Updated Cost Data to PLM");
                LNCommonUtil.updateCostUpdateDate(PropertyReader.getProperty("ln.cost.data.failed.update.date.key"));
                buildResponse = responseBuilder.setStatus(Status.FAILED).addErrorMessage(e.getMessage()).buildResponse();
                return new ResponseEntity<>(buildResponse, HttpStatus.OK);
            }
        }
    }
}
