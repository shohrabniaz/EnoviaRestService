package com.bjit.common.rest.app.service.controller.modelVersion.atoncontext;

import com.bjit.common.rest.app.service.model.tnr.TNR;
import com.bjit.common.rest.app.service.utilities.CommonSearch;
import com.bjit.ewc18x.utils.PropertyReader;
import com.matrixone.apps.domain.util.FrameworkException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import matrix.db.Context;

/**
 *
 * @author Touhidul Islam
 */
public class SearchConfigServiceImpl implements SearchConfigService {

    private static final org.apache.log4j.Logger LOGGER = org.apache.log4j.Logger
            .getLogger(SearchConfigServiceImpl.class);

    public SearchConfigServiceImpl() {

    }

    @Override
    public AtonResponseModel execute(AtonRequestModel reqModel,
            String topItemType, Context context,String source) {
        CommonSearch commonSearch = new CommonSearch();
        AtonResponseModel atonResponseModel = new AtonResponseModel();
        List<String> messages = new ArrayList<>();
        List<Model> models = new ArrayList<>();

//        CommonUtilities commonUtilities = new CommonUtilities();
//        commonUtilities.doStartTransaction(context);
        List<TNR> modelList = reqModel.getModels();
        // name is unique for 'Model' duplication searching omitting
        List<String> selectDataList = new ArrayList<>();
        selectDataList.add("physicalid");
        // selectDataList.add("MOD_AutomationProductExt.ItemCode");

        for (TNR m : modelList) {
            Model resModel = new Model();
            resModel.setTnr(m);
            ModelInfo modelInfo = new ModelInfo();
            HashMap<String, String> whereMap = new HashMap();
            if (!m.getRevision().equalsIgnoreCase("*")) {
                if(source.equalsIgnoreCase(PropertyReader.getProperty("aton.integration.source"))){
                    whereMap.put("attribute[Aton Version]", m.getRevision());         
                }else{
                    whereMap.put("revision", m.getRevision());             
                }
                resModel.getTnr().setRevision("*");
            }

            try {
                List<HashMap<String, String>> resultList = commonSearch.searchItem(context, m, whereMap, selectDataList);
                // set model infos
                modelInfo.setPhysicalid(resultList.get(0).get("physicalid"));
                resModel.setModelExists(Boolean.TRUE);
                resModel.setModelInfo(modelInfo);

                // set mfg top item info
                try {
                    String mfgPhysicalId = this.getTopMfgItem(m, topItemType, context);
                    resModel.setManufacturingItemInfo(new ManufacturingItemInfo(mfgPhysicalId));
                }
                catch (Exception exception) {
                    LOGGER.info("Top context item not found.");
                }
            } catch (Exception ex) {
                //Logger.getLogger(SearchConfigServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
                resModel.setModelExists(Boolean.FALSE);
                messages.add("Model/Model version not found.");
            }
            models.add(resModel);
        }
        atonResponseModel.setMessages(messages);
        atonResponseModel.setModels(models);

        return atonResponseModel;
    }

    public String getTopMfgItem(TNR m, String topItemType, Context context) throws FrameworkException, Exception {
        CommonSearch commonSearch = new CommonSearch();
        List<String> mfgItemSelection = new ArrayList<>();
        mfgItemSelection.add("physicalid");

        HashMap<String, String> whereClause = new HashMap<String, String>();
        //whereClause.put("type", "AUT_ContextItem");
        whereClause.put("attribute[MBOM_MBOMATON.MBOM_ItemCode]", m.getName());

        TNR mv = new TNR(topItemType, "*", "*");
        List<HashMap<String, String>> mfgItemList = commonSearch.searchItem(context, mv, whereClause, mfgItemSelection);
        LOGGER.info(mfgItemList);
        return mfgItemList.get(0).get("physicalid");
    }

    @Override
    public List<HashMap<String, String>> getMfgItemInfo(TNR m,
                                                        String sourceRevision,
                                                        String topItemType,
                                                        String childItemType,
                                                        Context context) throws FrameworkException, Exception
    {
        CommonSearch commonSearch = new CommonSearch();
        List<String> mfgItemSelection = new ArrayList<>();
        mfgItemSelection.add("physicalid");
        mfgItemSelection.add("type");
        mfgItemSelection.add("name");
        mfgItemSelection.add("revision");

        List<HashMap<String, String>> itemList = new ArrayList();
        List<HashMap<String, String>> childItemList = new ArrayList();
        HashMap<String, String> whereClause = null;
        try {
            whereClause = new HashMap();
            whereClause.put("attribute[MBOM_MBOMATON.MBOM_ItemCode]", m.getName());
            TNR top = new TNR(topItemType, "*", "*");
            itemList = commonSearch.searchItem(context, top, whereClause, mfgItemSelection);
        
        }
        catch (NullPointerException exception) {
            itemList = new ArrayList();
        }
        try {
            whereClause = new HashMap();
            whereClause.put("type", childItemType);
            whereClause.put("attribute[MBOM_MBOMATON.MBOM_ItemCode]", m.getName());
            whereClause.put("attribute[MBOM_MBOMATON.MBOM_AtonVersion]", sourceRevision);
            TNR child = new TNR(childItemType, "*", "*");
            childItemList = commonSearch.searchItem(context, child, whereClause, mfgItemSelection);
        }
        catch (NullPointerException exception) {
        }
        if (!childItemList.isEmpty()) {
            itemList.addAll(childItemList);
        }
        if (!itemList.isEmpty()) {
            itemList.forEach((item) -> {
                item.put("physicalId", item.get("physicalid"));
            });
        }

        LOGGER.info(itemList);
        return itemList;
    }

}
