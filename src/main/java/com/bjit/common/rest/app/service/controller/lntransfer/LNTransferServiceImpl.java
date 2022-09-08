/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.app.service.controller.lntransfer;

import com.bjit.common.rest.app.service.context.CreateContext;
import com.bjit.common.rest.app.service.controller.lntransfer.Util.LNRequestUtil;
import com.bjit.common.rest.app.service.controller.lntransfer.Util.LNResponseMessageUtil;
import static com.bjit.common.rest.app.service.controller.lntransfer.Util.LNResponseMessageUtil.FAILED_ITEM_LIST;
import com.bjit.common.rest.app.service.controller.lntransfer.Util.LNResponseServiceImpl;
import com.bjit.common.rest.app.service.model.itemTransfer.LNTransferRequestModel;
import com.bjit.common.rest.app.service.model.tnr.TNR;
import com.bjit.common.rest.app.service.utilities.NullOrEmptyChecker;
import com.bjit.common.rest.item_bom_import.utility.BusinessObjectUtil;
import com.bjit.common.rest.item_bom_import.xml_mapping_model.ResponseMessageFormaterBean;
import com.bjit.ex.integration.model.webservice.Item;
import com.bjit.ex.integration.transfer.actions.LNTransferAction;
import com.bjit.ex.integration.transfer.actions.factory.TransferActionType;
import java.net.ConnectException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import matrix.db.Context;
import com.bjit.common.rest.app.service.controller.lntransfer.Util.LNResponseService;
import com.bjit.common.rest.app.service.lntransfer.LNResponseMessageFormater;
import java.util.HashSet;
import java.util.Iterator;

/**
 *
 * @author BJIT
 */
public class LNTransferServiceImpl implements LNTransferService {

    private static final org.apache.log4j.Logger LN_TRANSFER_SERVICE_IMPL = org.apache.log4j.Logger.getLogger(LNTransferServiceImpl.class);
    private BusinessObjectUtil businessObjectUtil = new BusinessObjectUtil();
    public static final Map<String, String> ITEM_RESULT_LIST_TYPE_MAP = new HashMap();

    public static final String SUCCESSFUL_ITEM_LIST = "SuccessfulItemList";
    public static final String FAILED_ITEM_LIST = "FailedItemList";

    public static final String SUCCESSFUL_BOM_LIST = "SuccessfulBomList";
    public static final String FAILED_BOM_LIST = "FailedBomList";
    LNRequestUtil lNRequestUtil = new LNRequestUtil();
    public boolean isService = true;
    LNResponseMessageUtil lNResponseMessageUtil = new LNResponseMessageUtil();
    LNTransferAction lnTransferAction = new LNTransferAction();

    @Override
    public Map<LNResponseMessageFormater, String> itemTransfer(LNTransferRequestModel lnTransferRequestModel, String type, String level) throws Exception, ConnectException {
        LN_TRANSFER_SERVICE_IMPL.info("Transfer Calling from Service: " + isService);
        Map<String, ResponseMessageFormaterBean> transferResultMap = new HashMap<>();
        Map<LNResponseMessageFormater, String> listTransferResultMap = new HashMap<>();
        LNResponseMessageFormater listSuccessTransferResultMap = new LNResponseMessageFormater();
        LNResponseMessageFormater listErrorTransferResultMap = new LNResponseMessageFormater();
        Set<ResponseMessageFormaterBean> listSuccessItemTransferResultMap = new HashSet<>();
        Set<ResponseMessageFormaterBean> listErrorItemTransferResultMap = new HashSet<>();
        LNResponseServiceImpl responseService = new LNResponseServiceImpl();
        Map<String, String> itemResultMap = new HashMap<>();
        CreateContext createContext = new CreateContext();
        Context context = null;
        LNTransferAction lnTransferAction = new LNTransferAction();
        Item rootitem = new Item();
        boolean hasBOM = false;
        try {
            context = createContext.getAdminContext();
            for (Item baseitem : lnTransferRequestModel.getItems()) {
                if (!level.equalsIgnoreCase("1")) {
                    rootitem = baseitem;
                    Map<String, List<Item>> expandedData = lNRequestUtil.getExpandedItem(baseitem, context, type, level);
                    List<Item> expandedItem = expandedData.get("ITEM");
                    if (expandedItem.size() > 0) {
                        for (Item item : expandedItem) {
                            try {
                                ResponseMessageFormaterBean validationErrorMessageFormatter = LNRequestUtil.validateRequestedItem(context, item);
                                if (validationErrorMessageFormatter != null) {
                                    //  errorItemList.add(validationErrorMessageFormatter);
                                    continue;
                                }
                                itemResultMap = lnTransferAction.initiateTransferToLN(context, item, TransferActionType.ITEMS, isService, hasBOM);
                                transferResultMap = lNResponseMessageUtil.getResponseMessageFormatter(item, itemResultMap, type);

                            } catch (Exception ex) {
                                // messageFormaterBean =
                                //     transferResultMap = lNResponseMessageUtil.getResponseMessageFormatter(item, ex);
                                // errorItemList.add(messageFormaterBean);
                                LN_TRANSFER_SERVICE_IMPL.trace(ex);
                                LN_TRANSFER_SERVICE_IMPL.error(ex);
                            }

                            if (!NullOrEmptyChecker.isNull(transferResultMap.get(SUCCESSFUL_ITEM_LIST))) {
                                listSuccessItemTransferResultMap.add(transferResultMap.get(SUCCESSFUL_ITEM_LIST));
                                transferResultMap.remove(transferResultMap.get(SUCCESSFUL_ITEM_LIST));
                            }
                            if (!NullOrEmptyChecker.isNull(transferResultMap.get(FAILED_ITEM_LIST))) {
                                listErrorItemTransferResultMap.add(transferResultMap.get(FAILED_ITEM_LIST));
                                transferResultMap.remove(transferResultMap.get(FAILED_ITEM_LIST));
                            }
                        }
                    } else {
                        transferResultMap = lNResponseMessageUtil.getResponseMessageFormatter(baseitem, itemResultMap, "item");
                        listErrorItemTransferResultMap.add(transferResultMap.get(FAILED_ITEM_LIST));
                    }
                } else {

                    try {
                        ResponseMessageFormaterBean validationErrorMessageFormatter = LNRequestUtil.validateRequestedItem(context, baseitem);
                        if (validationErrorMessageFormatter != null) {
                            //  errorItemList.add(validationErrorMessageFormatter);
                            continue;
                        }
                        itemResultMap = lnTransferAction.initiateTransferToLN(context, baseitem, TransferActionType.ITEMS, isService, hasBOM);
                        transferResultMap = lNResponseMessageUtil.getResponseMessageFormatter(baseitem, itemResultMap, type);

                    } catch (Exception ex) {
                        // messageFormaterBean =
                        transferResultMap = lNResponseMessageUtil.getResponseMessageFormatter(baseitem, type, ex);
                        // errorItemList.add(messageFormaterBean);
                        if (!NullOrEmptyChecker.isNull(transferResultMap.get(FAILED_ITEM_LIST))) {
                            listErrorItemTransferResultMap.add(transferResultMap.get(FAILED_ITEM_LIST));
                            transferResultMap.remove(transferResultMap.get(FAILED_ITEM_LIST));
                        }
                        LN_TRANSFER_SERVICE_IMPL.trace(ex);
                        LN_TRANSFER_SERVICE_IMPL.error(ex);
                    }
                    if (!NullOrEmptyChecker.isNull(transferResultMap.get(SUCCESSFUL_ITEM_LIST))) {
                        listSuccessItemTransferResultMap.add(transferResultMap.get(SUCCESSFUL_ITEM_LIST));
                        transferResultMap.remove(transferResultMap.get(SUCCESSFUL_ITEM_LIST));
                    }
                    if (!NullOrEmptyChecker.isNull(transferResultMap.get(FAILED_ITEM_LIST))) {
                        listErrorItemTransferResultMap.add(transferResultMap.get(FAILED_ITEM_LIST));
                        transferResultMap.remove(transferResultMap.get(FAILED_ITEM_LIST));
                    }

                    //   listTransferResultMap = responseService.finalResponseListMap(transferResultMap);
                }
            }
        } catch (ConnectException ex) {
            LN_TRANSFER_SERVICE_IMPL.error(ex);
            ex.printStackTrace();
            throw ex;
        } catch (Exception ex) {
            LN_TRANSFER_SERVICE_IMPL.error(ex);
            transferResultMap = lNResponseMessageUtil.getResponseMessageFormatter(rootitem, type, ex);
            if (!NullOrEmptyChecker.isNull(transferResultMap.get(FAILED_ITEM_LIST))) {
                listErrorItemTransferResultMap.add(transferResultMap.get(FAILED_ITEM_LIST));
                transferResultMap.remove(LNTransferServiceImpl.FAILED_ITEM_LIST);
            }
            ex.printStackTrace();
            //  throw ex;
        } finally {
            if (context != null) {
                context.close();
            }
        }

        if (listSuccessItemTransferResultMap.size() > 0) {
            listSuccessTransferResultMap.setItem(listSuccessItemTransferResultMap);
            listTransferResultMap.put(listSuccessTransferResultMap, SUCCESSFUL_ITEM_LIST);

        }

        if (listErrorItemTransferResultMap.size() > 0) {
            listErrorTransferResultMap.setItem(listErrorItemTransferResultMap);
            listTransferResultMap.put(listErrorTransferResultMap, FAILED_ITEM_LIST);

        }

        return listTransferResultMap;
    }

    @Override
    public Map<LNResponseMessageFormater, String> bomTransfer(LNTransferRequestModel lnTransferRequestModel, String type, String level) throws Exception, ConnectException {
        LN_TRANSFER_SERVICE_IMPL.info("Transfer Calling from Service: " + isService);
        Map<String, ResponseMessageFormaterBean> transferResultMap = new HashMap<>();
        Map<LNResponseMessageFormater, String> listTransferResultMap = new HashMap<>();
        LNResponseMessageFormater listSuccessTransferResultMap = new LNResponseMessageFormater();
        LNResponseMessageFormater listErrorTransferResultMap = new LNResponseMessageFormater();
        Set<ResponseMessageFormaterBean> listSuccessItemTransferResultMap = new HashSet<>();
        Set<ResponseMessageFormaterBean> listErrorItemTransferResultMap = new HashSet<>();
        Set<ResponseMessageFormaterBean> listSuccessBomTransferResultMap = new HashSet<>();
        Set<ResponseMessageFormaterBean> listErrorBomTransferResultMap = new HashSet<>();
        Map<String, String> itemResultMap = new HashMap<>();
        CreateContext createContext = new CreateContext();
        Context context = null;
        Item rootitem = new Item();
        Set<ResponseMessageFormaterBean> errItemList = new HashSet<>();
        // LNTransferAction lnTransferAction = new LNTransferAction();
        LNResponseMessageFormater lnResponseMessageFormater = new LNResponseMessageFormater();
        // LNResponseService responseService = new LNResponseServiceImpl();
        boolean hasBOM = true;
        try {
            context = createContext.getAdminContext();
            for (Item baseitem : lnTransferRequestModel.getItems()) {
                rootitem = baseitem;
                LNResponseMessageFormater errorItemList = new LNResponseMessageFormater();
                LNResponseMessageFormater successItemList = new LNResponseMessageFormater();

                Map<String, List<Item>> expandedData = lNRequestUtil.getExpandedItem(baseitem, context, type, level);
                List<Item> expandedItem = expandedData.get("ITEM");
                List<Item> expandedBOM = expandedData.get("BOM");
                if (expandedBOM.size() < 1) {
                    hasBOM = false;
                }
                if (expandedItem.size() > 0) {
                    for (Iterator<Item> iterator = expandedItem.iterator(); iterator.hasNext();) {
                        Item item = iterator.next();
                        try {
                            ResponseMessageFormaterBean validationErrorMessageFormatter = LNRequestUtil.validateRequestedItem(context, item);
                            if (validationErrorMessageFormatter != null) {
                                //   errorItemList.add(validationErrorMessageFormatter);
                                continue;
                            }

                            itemResultMap = lnTransferAction.initiateTransferToLN(context, item, TransferActionType.ITEM_BOM, isService, hasBOM);
                            LN_TRANSFER_SERVICE_IMPL.info("Transfer Calling from Service: " + itemResultMap.size() + item);
                            transferResultMap = ResponseList(context, item, itemResultMap, iterator, expandedBOM);

                            if (!NullOrEmptyChecker.isNull(transferResultMap.get(SUCCESSFUL_ITEM_LIST))) {
                                listSuccessItemTransferResultMap.add(transferResultMap.get(SUCCESSFUL_ITEM_LIST));
                                transferResultMap.remove(transferResultMap.get(SUCCESSFUL_ITEM_LIST));
                            }
                            if (!NullOrEmptyChecker.isNull(transferResultMap.get(FAILED_ITEM_LIST))) {
                                listErrorItemTransferResultMap.add(transferResultMap.get(FAILED_ITEM_LIST));
                                transferResultMap.remove(transferResultMap.get(FAILED_ITEM_LIST));
                            }
                            if (!NullOrEmptyChecker.isNull(transferResultMap.get(SUCCESSFUL_BOM_LIST))) {
                                listSuccessBomTransferResultMap.add(transferResultMap.get(SUCCESSFUL_BOM_LIST));
                                transferResultMap.remove(transferResultMap.get(SUCCESSFUL_BOM_LIST));
                            }
                            if (!NullOrEmptyChecker.isNull(transferResultMap.get(FAILED_BOM_LIST))) {
                                listErrorBomTransferResultMap.add(transferResultMap.get(FAILED_BOM_LIST));
                                transferResultMap.remove(transferResultMap.get(FAILED_BOM_LIST));
                            }

                        } catch (Exception ex) {

                            transferResultMap = lNResponseMessageUtil.getResponseMessageFormatter(item, type, ex);
                            if (!NullOrEmptyChecker.isNull(transferResultMap.get(FAILED_BOM_LIST))) {
                                listErrorBomTransferResultMap.add(transferResultMap.get(FAILED_BOM_LIST));
                                transferResultMap.remove(transferResultMap.get(FAILED_BOM_LIST));
                            }
                            LN_TRANSFER_SERVICE_IMPL.trace(ex);
                            LN_TRANSFER_SERVICE_IMPL.error(ex);
                        }

                    }
                    //     listTransferResultMap.put(errorItemList, type)

                } else {
                    ResponseMessageFormaterBean responseMessageFormaterBean = new ResponseMessageFormaterBean();

                    if (NullOrEmptyChecker.isNullOrEmpty(baseitem.getTnr().getName())) {
                        responseMessageFormaterBean.setObjectId(baseitem.getId());
                    } else {
                        responseMessageFormaterBean.setTnr(new TNR(
                                baseitem.getTnr().getType(),
                                baseitem.getTnr().getName(),
                                baseitem.getTnr().getRevision()));
                    }

                    listErrorBomTransferResultMap.add(responseMessageFormaterBean);

                }
            }
        } catch (ConnectException ex) {
            LN_TRANSFER_SERVICE_IMPL.error(ex);
            ex.printStackTrace();

            throw ex;
        } catch (Exception ex) {
            LN_TRANSFER_SERVICE_IMPL.error(ex);
            transferResultMap = lNResponseMessageUtil.getResponseMessageFormatter(rootitem, type, ex);
            if (!NullOrEmptyChecker.isNull(transferResultMap.get(FAILED_BOM_LIST))) {
                listErrorBomTransferResultMap.add(transferResultMap.get(FAILED_BOM_LIST));
                transferResultMap.remove(transferResultMap.get(FAILED_BOM_LIST));
            }
            ex.printStackTrace();
            //  throw ex;
        } finally {
            if (context != null) {
                context.close();
            }
        }

        if (listSuccessItemTransferResultMap.size() > 0) {

            listSuccessTransferResultMap.setItem(listSuccessItemTransferResultMap);
            listTransferResultMap.put(listSuccessTransferResultMap, SUCCESSFUL_ITEM_LIST);

        }

        if (listErrorItemTransferResultMap.size() > 0) {

            listErrorTransferResultMap.setItem(listErrorItemTransferResultMap);
            listTransferResultMap.put(listErrorTransferResultMap, FAILED_ITEM_LIST);

        }
        if (listSuccessBomTransferResultMap.size() > 0) {

            listSuccessTransferResultMap.setBom(listSuccessBomTransferResultMap);
            listTransferResultMap.put(listSuccessTransferResultMap, SUCCESSFUL_BOM_LIST);

        }

        if (listErrorBomTransferResultMap.size() > 0) {

            listErrorTransferResultMap.setBom(listErrorBomTransferResultMap);
            listTransferResultMap.put(listErrorTransferResultMap, FAILED_BOM_LIST);

        }

        return listTransferResultMap;
    }

    @Override
    public void gtsNigtlyUpdateToLN() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public Map<String, ResponseMessageFormaterBean> ResponseList(Context context, Item item, Map<String, String> itemResultMap, Iterator<Item> iterator, List<Item> expandedBOM) {
        Set<ResponseMessageFormaterBean> errorItemList = new HashSet<>();
        Set<ResponseMessageFormaterBean> successItemList = new HashSet<>();
        Map<String, ResponseMessageFormaterBean> transferResultMap = new HashMap<>();
        //   LNTransferAction lnTransferAction = new LNTransferAction();
        String errorType = itemResultMap.get(item.getTnr().getName().toUpperCase());
        LN_TRANSFER_SERVICE_IMPL.info("BOM Type Service Call" + errorType);
        if (expandedBOM.isEmpty()) {

            try {
                lnTransferAction.initiateTransferToLN(context, item, TransferActionType.ITEMS, isService, false);
            } catch (Exception ex) {
                LN_TRANSFER_SERVICE_IMPL.trace(ex);
                LN_TRANSFER_SERVICE_IMPL.error(ex);
            }
            LN_TRANSFER_SERVICE_IMPL.info("Empty Level Item call for BOM Transfer Service .");
            transferResultMap = lNResponseMessageUtil.getResponseMessageFormatter(item, itemResultMap, "item");
            iterator.remove();
        } else if (expandedBOM.size() > 0 && expandedBOM.contains(item)) {
            LN_TRANSFER_SERVICE_IMPL.info("BOM Transfer Service Calling ....");
            try {
                ResponseMessageFormaterBean validationErrorMessageFormatter = LNRequestUtil.validateRequestedItem(context, item);
                if (validationErrorMessageFormatter != null) {
                    errorItemList.add(validationErrorMessageFormatter);
                    // continue;
                }
                itemResultMap = lnTransferAction.initiateTransferToLN(context, item, TransferActionType.BOM, isService, true);
                transferResultMap = lNResponseMessageUtil.getResponseMessageFormatter(item, itemResultMap, "bom");

            } catch (Exception ex) {
                transferResultMap = lNResponseMessageUtil.getResponseMessageFormatter(item, "bom", ex);
                LN_TRANSFER_SERVICE_IMPL.trace(ex);
                LN_TRANSFER_SERVICE_IMPL.error(ex);
            }
            iterator.remove();
        }

        return transferResultMap;
    }
}
