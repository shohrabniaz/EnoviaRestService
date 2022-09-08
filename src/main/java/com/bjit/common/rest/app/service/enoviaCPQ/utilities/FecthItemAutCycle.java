/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.app.service.enoviaCPQ.utilities;

import com.bjit.common.rest.app.service.utilities.NullOrEmptyChecker;
import com.matrixone.apps.domain.util.FrameworkException;
import com.matrixone.apps.domain.util.MqlUtil;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import matrix.db.Context;

/**
 *
 * @author BJIT
 */
public class FecthItemAutCycle {

    public String getRelationshipIdByItem(Context ctx, String type, String name, String rev) throws FrameworkException {

        String physical = "";

        // pri bus CreateAssembly mass-EPS1-00017778 1.1 select from[DELFmiFunctionIdentifiedInstance].id dump |
        StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append("print bus")
                .append(" ").append(type)
                .append(" ").append(name)
                .append(" ").append(rev)
                .append(" select physicalid dump |");
        String mqlQuery = queryBuilder.toString();
        System.out.println(" Relationship Id Info " + mqlQuery);
        String mqlResult = MqlUtil.mqlCommand(ctx, mqlQuery);

        physical = mqlResult;
        return physical;
    }

    public List<String> getTypeRelationshipId(Context ctx, String id) throws FrameworkException {

        List<String> mftitemname = new ArrayList<>();
        StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append("print bus")
                .append(" ").append(id)
                .append(" select type name revision attribute[AUT Lifecycle Status] attribute[Aton Version] dump |");
        String mqlQuery = queryBuilder.toString();

        System.out.println(" Item Info by Relationship " + mqlQuery);
        String mqlResult = MqlUtil.mqlCommand(ctx, mqlQuery);
        if (!NullOrEmptyChecker.isNullOrEmpty(mqlResult)) {
            String[] tempSingleList = mqlResult.split("\\|");

            for (int k = 0; k < tempSingleList.length; k++) {
                mftitemname.add(tempSingleList[k]);
            }
        }
        return mftitemname;
    }

    public boolean getAUTType(Context ctx, String type, String name, String rev) throws FrameworkException {

        boolean isPilot = false;

        StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append("print bus")
                .append(" ").append(type)
                .append(" ").append(name)
                .append(" ").append(rev)
                .append(" select attribute[AUT Lifecycle Status] dump |");
        String mqlQuery = queryBuilder.toString();
        System.out.println(" AUT Type check " + mqlQuery);

        String mqlResult = MqlUtil.mqlCommand(ctx, mqlQuery);
        if (!NullOrEmptyChecker.isNullOrEmpty(mqlResult)) {
            if (mqlResult.equalsIgnoreCase("Pilot")) {
                isPilot = true;
            }
        }
        return isPilot;
    }

    public List<String> getTypeRelationshipToChildId(Context ctx, String id) throws FrameworkException {

        List<String> mftitemname = new ArrayList<>();
        StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append("expand bus")
                .append(" ").append(id)
                .append(" type AUT_ContextItem to rel DELFmiFunctionIdentifiedInstance dump |");
        String mqlQuery = queryBuilder.toString();

        System.out.println(" Parent Item Info by Relationship " + mqlQuery);
        String mqlResult = MqlUtil.mqlCommand(ctx, mqlQuery);
        if (!NullOrEmptyChecker.isNullOrEmpty(mqlResult)) {
            String[] tempList = mqlResult.split("[\\r\\n]+");
            if (tempList.length == 1) {
                String[] tempSingleList = mqlResult.split("\\|");

                for (int k = 0; k < tempSingleList.length; k++) {
                    mftitemname.add(tempSingleList[k]);
                }
            }
        }
        return mftitemname;
    }

    public boolean getConnectedRelationshipToChildId(Context ctx, String id, String name, String rev) throws FrameworkException {

        boolean isMatch = false;

        List<String> mftitemname = new ArrayList<>();
        StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append("print connection")
                .append(" ").append(id)
                .append(" select to.name to.revision dump |");
        String mqlQuery = queryBuilder.toString();

        System.out.println(" Parent Item Info by Relationship " + mqlQuery);
        String mqlResult = MqlUtil.mqlCommand(ctx, mqlQuery);
        if (!NullOrEmptyChecker.isNullOrEmpty(mqlResult)) {
            String[] tempSingleList = mqlResult.split("\\|");

            if (name.equalsIgnoreCase(tempSingleList[0]) && rev.equalsIgnoreCase(tempSingleList[1])) {

                isMatch = true;
            }
        }
        return isMatch;
    }

//    public List<String> getConnectedInfoToChildId(Context ctx, String id) throws FrameworkException {
//
//        List<String> modelinfo = new ArrayList<>();
//
//        StringBuilder queryBuilder = new StringBuilder();
//        queryBuilder.append("print connection")
//                .append(" ").append(id)
//                .append(" select to.type to.name to.revision dump |");
//        String mqlQuery = queryBuilder.toString();
//
//        System.out.println(" Parent Item Info by Relationship " + mqlQuery);
//        String mqlResult = MqlUtil.mqlCommand(ctx, mqlQuery);
//        if (!NullOrEmptyChecker.isNullOrEmpty(mqlResult)) {
//            String[] tempSingleList = mqlResult.split("\\|");
//
//            for (int k = 0; k < tempSingleList.length; k++) {
//                modelinfo.add(tempSingleList[k]);
//            }
//        }
//        return modelinfo;
//    }
}
