/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.app.service.controller.iteminfo;

import com.bjit.common.rest.app.service.model.iteminfo.ItemInfoRequestBean;
import com.bjit.common.rest.app.service.model.iteminfo.ItemInfoResponseBean;
import com.bjit.common.rest.app.service.model.tnr.TNR;
import com.bjit.common.rest.app.service.payload.common_response.CustomResponseBuilder;
import com.bjit.common.rest.app.service.payload.common_response.IResponse;
import com.bjit.common.rest.app.service.payload.common_response.Status;
import com.matrixone.apps.domain.util.FrameworkException;
import com.matrixone.apps.domain.util.MqlUtil;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import javax.servlet.http.HttpServletRequest;
import matrix.db.BusinessObject;
import matrix.db.BusinessObjectWithSelect;
import matrix.db.Context;
import matrix.util.MatrixException;
import matrix.util.SelectList;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 *
 * @author BJIT
 */



@Controller
@RequestMapping(path = "/getItemInfo")
public class GetItemInfoController {

    private static final org.apache.log4j.Logger CREATE_AND_CHECKING_CONTROLLER_LOGGER = org.apache.log4j.Logger.getLogger(GetItemInfoController.class);
    private Map <String,String> intemInfoMap;
    @RequestMapping(value = "/Item", method = RequestMethod.GET, consumes = "application/json", produces = "application/json")
    @ResponseBody
    public ResponseEntity getItemInfo(HttpServletRequest httpRequest, @RequestBody final ItemInfoRequestBean itemInfoRequestBean) throws MatrixException {
        String buildResponse;
        intemInfoMap = new HashMap<>();
        CREATE_AND_CHECKING_CONTROLLER_LOGGER.debug("WITH GET ITEM INFO ContROLLER");
        ItemInfoResponseBean itemInfoResponseBean = new ItemInfoResponseBean();
        IResponse responseBuilder = new CustomResponseBuilder();
        BusinessObject itemBus; 
        final Context context = (Context) httpRequest.getAttribute("context");
        String item_Id = itemInfoRequestBean.getItem_Id();
        if (item_Id!=null || "".equals(item_Id)) {
            itemBus = new BusinessObject(item_Id);
        } else {
            TNR tnr = itemInfoRequestBean.getType_name_revision();
            itemBus = new BusinessObject(tnr.getType(),tnr.getName(),tnr.getRevision(),"");
        }
        itemBus.open(context);
        getAttributeValues(context,itemBus,itemInfoRequestBean.getAttribute_List());
        getPropertyValues(context, item_Id, itemInfoRequestBean.getProperty_List());
        itemInfoResponseBean.setItem_Id(item_Id);
        itemInfoResponseBean.setRequested_information(intemInfoMap);
        buildResponse = responseBuilder.setData(itemInfoResponseBean).setStatus(Status.OK).buildResponse();
        return new ResponseEntity<>(buildResponse, HttpStatus.OK);
    }
    private void getAttributeValues(Context context,BusinessObject itemBus,List<String> attributeList) throws MatrixException {
        SelectList sList = new SelectList();
        for (int i=0;i<attributeList.size();i++) {
            sList.addAttribute(attributeList.get(i));
        }
        BusinessObjectWithSelect busWithSelect = itemBus.select(context, sList);
        for (int i=0;i<attributeList.size();i++) {
           String selectData = busWithSelect.getSelectData((String) sList.get(i));
           intemInfoMap.put(attributeList.get(i), selectData);
        }
    }
    private void getPropertyValues(Context context,String item_Id, List<String>propertyNameList) throws FrameworkException{
        String printQuery = "print bus " + item_Id+" select";
        for (int i=0;i<propertyNameList.size();i++) {
            printQuery = printQuery +" \"" + propertyNameList.get(i)+"\"";
        }
        printQuery = printQuery+ " dump |";
        CREATE_AND_CHECKING_CONTROLLER_LOGGER.info("print Query : " + printQuery);
        
        String mqlCommand = MqlUtil.mqlCommand(context, printQuery);
        String[] propertyValues = mqlCommand.split(Pattern.quote("|"));
        for (int i=0;i<propertyNameList.size();i++) {
            intemInfoMap.put(propertyNameList.get(i), propertyValues[i]);
        }
    }
}
