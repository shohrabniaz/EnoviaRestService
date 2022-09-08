/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.bjit.common.rest.app.service.controller.itemHistoryDetatils;

import com.bjit.common.rest.app.service.context.CreateContext;
import com.bjit.common.rest.app.service.controller.itemHistoryDetatils.validator.ItemHistoryValidator;
import com.bjit.common.rest.app.service.itemHistoryLines.HistoryLinesService;
import com.bjit.common.rest.app.service.itemHistoryLines.HistoryLinesServiceImpl;
import com.bjit.common.rest.app.service.model.itemHistoryLine.ItemHistoryBean;
import com.bjit.common.rest.app.service.model.itemHistoryLine.ItemHistoryReqModel;
import com.bjit.common.rest.app.service.payload.common_response.CustomResponseBuilder;
import com.bjit.common.rest.app.service.payload.common_response.IResponse;
import com.bjit.common.rest.app.service.payload.common_response.Status;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import matrix.db.Context;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Fazley Rabbi-11372 Date: 27-06-2022
 */
@RestController
@RequestMapping(path = "/fetch/item-history")
public class ItemHistoryDetatilsController {

    private static final org.apache.log4j.Logger ITEM_HISTORY_DETAILS_CONTROLLER_LOGGER = org.apache.log4j.Logger.getLogger(ItemHistoryDetatilsController.class);

    @ResponseBody
    @PostMapping(path = "/details")
    public ResponseEntity<?> changeMaturityStatus(@RequestBody ItemHistoryReqModel reqModel) throws Exception {
        Instant startServiceTime = Instant.now();
        IResponse responseBuilder = new CustomResponseBuilder();
        String response;
        ItemHistoryBean itemHistoryBean = null;
        List<ItemHistoryBean> itemHistoryBeanList = new ArrayList<>();
        List<String> errors = new ArrayList<>();

        //Validate request data
        if (!ItemHistoryValidator.validateRequest(reqModel)) {
            errors.add("Invalid request data");
            response = responseBuilder.addErrorMessage(errors).setStatus(Status.FAILED).buildResponse();
            return new ResponseEntity<>(response, HttpStatus.OK);
        }

        ITEM_HISTORY_DETAILS_CONTROLLER_LOGGER.info("#################Item History Service Calling##################");

        String historyOrder = reqModel.getConstraint().getHistoryOrder();

        //Context creation
        CreateContext createContext = new CreateContext();
        Context context = null;
        try {
            context = createContext.getAdminContext();
        } catch (Exception e) {
            ITEM_HISTORY_DETAILS_CONTROLLER_LOGGER.error("Error Raised: " + e.getMessage());
        }

        HistoryLinesService fetch = new HistoryLinesServiceImpl(historyOrder, context);
        try {
            for (int i = 0; i < reqModel.getData().size(); i++) {
                //HistoryLinesImpl service calling
                itemHistoryBean = fetch.getItemHistoryLines(reqModel.getData().get(i).getItemId());

                itemHistoryBean.setDescId(reqModel.getData().get(i).getDescId());
                ITEM_HISTORY_DETAILS_CONTROLLER_LOGGER.info("History Data: " + itemHistoryBean);
                itemHistoryBeanList.add(itemHistoryBean);
            }

            ITEM_HISTORY_DETAILS_CONTROLLER_LOGGER.info("History Lines : " + itemHistoryBeanList);
            response = responseBuilder.setData(itemHistoryBeanList).setStatus(Status.OK).buildResponse();
            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (Exception e) {
            response = responseBuilder.addErrorMessage(e.getMessage()).setStatus(Status.FAILED).buildResponse();
            ITEM_HISTORY_DETAILS_CONTROLLER_LOGGER.error("Error Raised: " + e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.OK);

        } finally {
            context.close();
            Instant endServiceTime = Instant.now();
            ITEM_HISTORY_DETAILS_CONTROLLER_LOGGER.info("Time taken by service :" + Duration.between(startServiceTime, endServiceTime).toMillis());
        }
    }
}
