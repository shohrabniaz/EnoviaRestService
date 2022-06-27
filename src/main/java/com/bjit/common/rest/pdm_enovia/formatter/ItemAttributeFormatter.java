package com.bjit.common.rest.pdm_enovia.formatter;

import com.bjit.common.rest.app.service.model.createobject.CreateObjectBean;
import com.bjit.common.rest.app.service.model.tnr.TNR;
import com.bjit.common.rest.app.service.utilities.NullOrEmptyChecker;
import com.bjit.common.rest.item_bom_import.item_import.AttributeBusinessLogic;
import com.bjit.common.rest.pdm_enovia.mapper.xml_mapping_model.ItemImportMapping;
import com.bjit.common.rest.item_bom_import.xml_mapping_model.ResponseMessageFormaterBean;
import com.bjit.common.rest.pdm_enovia.mapper.ItemMapper;
import com.bjit.common.rest.pdm_enovia.result.ResultUtil;
import com.bjit.common.rest.pdm_enovia.utility.CommonUtil;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import matrix.util.MatrixException;
import org.apache.log4j.Logger;

/**
 *
 * @author Mashuk/BJIT
 */
public class ItemAttributeFormatter extends AttributeFormatter {
    
    private static final Logger ITEM_ATTIRBUTE_LOGGER = Logger.getLogger(ItemAttributeFormatter.class);
    
    List<ResponseMessageFormaterBean> tnrSuccessfullList = new ArrayList<>();
    List<ResponseMessageFormaterBean> tnrErrorList = new ArrayList<>();
    HashMap<String, List<ResponseMessageFormaterBean>> tnrListMap = new HashMap<>();

    public ItemAttributeFormatter(CreateObjectBean createObjectBean, HashMap<String, String> propertyMap, List<String> substituteItemList) {
        super(createObjectBean, propertyMap, substituteItemList);
    }

    @Override
    public CreateObjectBean getFormattedObjectBean(ResultUtil resultUtil, AttributeBusinessLogic attributeBusinessLogic) throws IOException {
        try {
            TNR itemTNR = createObjectBean.getTnr();
            String itemType = itemTNR.getType();
            itemTNR.setType(propertyMap.get("Type"));
            String itemRevision = NullOrEmptyChecker.isNullOrEmpty(itemTNR.getRevision()) ? "00" : itemTNR.getRevision();
            itemTNR.setRevision(itemRevision);
            createObjectBean.setTnr(itemTNR);
            createObjectBean.setTemplateBusinessObjectId("");
            createObjectBean.setIsAutoName(Boolean.FALSE);
            createObjectBean.setFolderId("");
            
            String mapsAbsoluteDirectory = CommonUtil.populateMapDirectoryFromObject(createObjectBean);
            if (NullOrEmptyChecker.isNullOrEmpty(mapsAbsoluteDirectory)) {
                String errorMessage = "System could not recognize the type : '" + itemType + "'";
                ITEM_ATTIRBUTE_LOGGER.error(errorMessage);
                throw new NullPointerException(errorMessage);
            }
            addMissingPDMAttributes(resultUtil);
            //updateAttributeValues();
                
            ItemImportMapping mapper = null;
            ItemMapper ObjectTypesAndRelations = new ItemMapper(mapsAbsoluteDirectory, ItemImportMapping.class,  createObjectBean);
        } catch (NullPointerException ex) {
            ITEM_ATTIRBUTE_LOGGER.error(">>>>>>>>> Error: " + ex.getMessage());
            throw ex;
        }
        ITEM_ATTIRBUTE_LOGGER.debug(">>>>>>>>> Formatted Attribute Map of " + createObjectBean.getTnr().getName() + ": " + createObjectBean.getAttributes());
        return createObjectBean;
    }
    
    private void addMissingPDMAttributes(ResultUtil resultUtil) {
        TNR itemTNR = resultUtil.getItemTNR(createObjectBean.getTnr().getName());
        if (!NullOrEmptyChecker.isNullOrEmpty(itemTNR.getType())) {
            createObjectBean.getAttributes().put("Commercial Item Family", itemTNR.getType());
        }
        if (!NullOrEmptyChecker.isNullOrEmpty(itemTNR.getName())) {
            createObjectBean.getAttributes().put("External Id", itemTNR.getName());
        }
        if(!NullOrEmptyChecker.isNullOrEmpty(substituteItemList)) {
            if(substituteItemList.size() > 1) {
                StringBuilder sb = new StringBuilder();
                for(int i = 0; i<substituteItemList.size(); i++ ) {
                    sb.append(substituteItemList.get(i));
                    if(i<=substituteItemList.size()-1 && i > 0) {
                        sb.append(",");
                    }
                }
                createObjectBean.getAttributes().put("Substitute Item", sb.toString());
            }
            else {
                createObjectBean.getAttributes().put("Substitute Item", substituteItemList.get(0));
            }
        }
    }
    
    private void updateAttributeValues() {
        try {
            if (createObjectBean.getAttributes().containsKey("Translation name")) {
                if (!NullOrEmptyChecker.isNullOrEmpty(createObjectBean.getAttributes().get("Translation name"))) {
                    String translationName = createObjectBean.getAttributes().get("Translation name");
                    createObjectBean.getAttributes().put("Translation name", AttributeFormatter.getBundleIdFromTranslationName(translationName));
                }
            }
        } catch (MatrixException e) {
            String errorMessage = "System could not process attribute 'Translation Name' due to : " + e.getMessage();
            ITEM_ATTIRBUTE_LOGGER.error(errorMessage);
        }
    }
}
