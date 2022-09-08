/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.app.service.controller.document.checkout;

import com.bjit.common.rest.app.service.context.CreateContext;
import com.bjit.common.rest.app.service.model.checkout.CheckOutBean;
import com.matrixone.apps.domain.util.MapList;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import matrix.db.BusinessObject;
import matrix.db.File;
import matrix.db.FileList;
import matrix.db.JPO;
import matrix.util.MatrixException;
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
@RequestMapping(path = "/checkoutObject")
public class CheckoutController {
    private static final org.apache.log4j.Logger CHECKOUT_CONTROLLER = org.apache.log4j.Logger.getLogger(CheckoutController.class);
    
    @RequestMapping(value = "/checkout", method = RequestMethod.POST, produces = "application/json", consumes = "application/json")
    @ResponseBody
    public HashMap checkout(HttpServletRequest request, @RequestBody final CheckOutBean checkOutBean) throws MatrixException{
        matrix.db.Context context = null;
        HashMap hM = new HashMap();
        List<String> listOfFileName = new ArrayList();
        try {
            CreateContext createContext = new CreateContext();
            context = createContext.getUserCredentialsFromHeader(request);
            CHECKOUT_CONTROLLER.info("Created Context");
        } catch (Exception exp) {
            CHECKOUT_CONTROLLER.error(exp.getMessage());
            exp.printStackTrace(System.out);
        }
        
        if(context == null){
            CHECKOUT_CONTROLLER.error("Context is null");
            hM.put("Error", "Faild: Context is null");
            return hM;
        }
        
        System.out.println("Business Object Id : " + checkOutBean.getBusinessObjectId());
        CHECKOUT_CONTROLLER.info("Business Object Id : " + checkOutBean.getBusinessObjectId());
        BusinessObject bo = new BusinessObject(checkOutBean.getBusinessObjectId());
        bo.open(context);
        hM.put("item_name", bo.getName());
        System.out.println("Storage Location : " + checkOutBean.getStorageLocation());
        CHECKOUT_CONTROLLER.info("Storage Location : " + checkOutBean.getStorageLocation());
        
        try{
            listOfFileName = fileCheckOut(context, checkOutBean.getBusinessObjectId(), checkOutBean.getStorageLocation(), Boolean.FALSE);
            hM.put("files", listOfFileName);
            return hM;
        }
        catch(Exception exp){
            exp.printStackTrace(System.out);
            CHECKOUT_CONTROLLER.error(exp.getMessage());
            throw exp;
        }
    }
    
    private List<String> fileCheckOut(matrix.db.Context context, String objectId, String checkOutLocation, Boolean sameDirectory) throws MatrixException {
        String[] initargs = {};
        HashMap params = new HashMap();
        HashMap <String, String> hp = new HashMap<>();
        hp.put("objectId",objectId);
        params.put("objectIdMap",hp);
        MapList temp= new MapList();
        List<String> listOfFileName = new ArrayList<>();
        try {
             temp = JPO.invoke(context, "CheckInCheckOutUtil", initargs, "fileCheckOut", JPO.packArgs(params), MapList.class);
        } catch (MatrixException exp) {
            CHECKOUT_CONTROLLER.error(exp.getMessage());
             throw new MatrixException("Could not get the Document IDs.  Error:"+exp.getMessage());
        }
        System.out.println("CREATED:"+temp.toString());
        for (int i=0;i<temp.size();i++) {
            Set formatSet = new HashSet();
            HashMap  objt = (HashMap)temp.get(i);
            Iterator it = objt.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry)it.next();
                System.out.println(pair.getKey() + " = " + pair.getValue());
                try {
                    BusinessObject bo = new BusinessObject((String) pair.getValue());
                    bo.open(context);
                    FileList files = bo.getFiles(context);
                    for(int kk=0;kk<files.size();kk++) {
                        File file = files.get(kk);
                        listOfFileName.add(file.getName());
                        formatSet.add(file.getFormat());
                    }
                    if(sameDirectory){
                        Iterator formatSetIterator = formatSet.iterator(); 
                        while (formatSetIterator.hasNext()) {
                            bo.checkoutFiles(context, false,formatSetIterator.next().toString(), files, checkOutLocation);
                        }
                    }
                    else{
                        Iterator formatSetIterator = formatSet.iterator(); 
                        while (formatSetIterator.hasNext()) {
                            bo.checkoutFiles(context, false, formatSetIterator.next().toString(), files, checkOutLocation + bo.getName());
                        }
                    }
                    System.out.println("-----" + files.size());
                } catch (MatrixException mx) {
                    throw new MatrixException("Could not checkout Files from Document type Object. Error:"+mx.getMessage());
                }
                it.remove();
            }
        }
        return listOfFileName;
    }
}
