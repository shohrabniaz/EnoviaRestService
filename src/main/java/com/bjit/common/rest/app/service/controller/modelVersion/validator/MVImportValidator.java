/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.bjit.common.rest.app.service.controller.modelVersion.validator;

import com.bjit.common.rest.app.service.model.modelVersion.MVDataTree;
import com.bjit.common.rest.app.service.model.modelVersion.MVItemsImportDataBean;
import com.bjit.common.rest.app.service.model.tnr.TNR;
import com.bjit.common.rest.app.service.utilities.NullOrEmptyChecker;
import com.bjit.ewc18x.utils.PropertyReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author Ahmad R Farabi [PLM Team, BJIT] <ahmad.farabi@bjitgroup.com>
 */
public class MVImportValidator
{

    private static final String SOURCE_ATON = PropertyReader.getProperty("aton.integration.source");
    private static final String SOURCE_TOOL = PropertyReader.getProperty("tool.integration.source");

    public static boolean validateSource(String source)
    {
        if (source.equalsIgnoreCase(SOURCE_ATON)) {
            return true;
        } else {
            if (source.equalsIgnoreCase(SOURCE_TOOL)) {
                return true;
            }
        }
        return false;
    }

    public static boolean validateParams(MVItemsImportDataBean mvRequest)
    {
        if (NullOrEmptyChecker.isNullOrEmpty(mvRequest.geDataTree())) {
            return false;
        }
        for (MVDataTree req : mvRequest.geDataTree()) {
            if (NullOrEmptyChecker.isNull(req.getItem())) {
                return false;
            }
            if (NullOrEmptyChecker.isNull(req.getItem().getTnr())) {
                return false;
            }
            TNR tnr = req.getItem().getTnr();
            if (NullOrEmptyChecker.isNullOrEmpty(tnr.getName()) || NullOrEmptyChecker.isNullOrEmpty(tnr.getRevision())) {
                return false;
            }

            HashMap<String, String> attrMap = req.getItem().getAttributes();
            if (NullOrEmptyChecker.isNull(attrMap)) {
                return false;
            }

            if (mvRequest.getSource().equalsIgnoreCase(SOURCE_ATON) && NullOrEmptyChecker.isNullOrEmpty(attrMap.get("id"))) {
                return false;
            }

            if (!mvRequest.getSource().equalsIgnoreCase(SOURCE_ATON) && NullOrEmptyChecker.isNullOrEmpty(mvRequest.getSecurityContext())) {
                return false;
            }

            if (!mvRequest.getSource().equalsIgnoreCase(SOURCE_ATON) && NullOrEmptyChecker.isNullOrEmpty(mvRequest.getOwner())) {
                return false;
            }
        }

        return true;
    }

    public static boolean validateAttributes(MVDataTree req)
    {
        HashMap<String, String> attrMap = req.getItem().getAttributes();
        if (NullOrEmptyChecker.isNull(attrMap)) {
            return false;
        }

        if (NullOrEmptyChecker.isNullOrEmpty(attrMap.get("title")) || NullOrEmptyChecker.isNullOrEmpty(attrMap.get("status"))) {
            return false;
        }

        return true;
    }

    public static MVItemsImportDataBean sortRequest(MVItemsImportDataBean mvRequest)
    {
        if (mvRequest.getSource().equalsIgnoreCase(SOURCE_ATON)) {
            MVItemsImportDataBean sortedRequest = new MVItemsImportDataBean();
            sortedRequest.setSource(mvRequest.getSource());
            List<MVDataTree> sortedList = new ArrayList();
            int topData = 0;
            List<MVDataTree> requestList = mvRequest.geDataTree();
            for (int i = 0; i < requestList.size(); i++) {
                MVDataTree req1 = requestList.get(i);
                topData = i;
                for (int j = i; j < requestList.size(); j++) {
                    MVDataTree req2 = requestList.get(j);
                    if (req1.getItem().getTnr().getName().equalsIgnoreCase(req2.getItem().getTnr().getName())) {
//                        if (Double.parseDouble(req2.getItem().getTnr().getRevision()) < Double.parseDouble(requestList.get(topData).getItem().getTnr().getRevision())) {
                        if (Long.parseLong(req2.getItem().getAttributes().get("id")) < Long.parseLong(requestList.get(topData).getItem().getAttributes().get("id"))) {
                            topData = j;
                        }
                    }
                }
                sortedList.add(requestList.get(topData));
                Collections.swap(requestList, i, topData);
            }
            sortedRequest.setDataTree(sortedList);
            if (sortedRequest.geDataTree().size() == mvRequest.geDataTree().size()) {
                return sortedRequest;
            }
        } else {
            return mvRequest;
        }
        return null;
    }
}
