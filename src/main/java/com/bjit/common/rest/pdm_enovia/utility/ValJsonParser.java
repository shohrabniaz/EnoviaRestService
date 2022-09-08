package com.bjit.common.rest.pdm_enovia.utility;

import com.bjit.common.rest.pdm_enovia.result.ResultUtil;
import com.bjit.common.rest.app.service.model.createobject.CreateObjectBean;
import com.bjit.common.rest.app.service.model.itemImport.DataTree;
import com.bjit.common.rest.app.service.model.itemImport.Document;
import com.bjit.common.rest.app.service.model.itemImport.Substitute;
import com.bjit.common.rest.app.service.model.tnr.TNR;
import com.bjit.common.rest.app.service.utilities.NullOrEmptyChecker;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.apache.log4j.Logger;

/**
 *
 * @author Mashuk/BJIT
 */
public class ValJsonParser {

    private static final Logger VAL_JSON_PARSER = Logger.getLogger(ValJsonParser.class);
    
    private Map<String, CreateObjectBean> itemObjectMap;
    private Map<String, List<Document>> documentTreeListMap;
    private Map<String, List<String>> substituteMap;
    private String sourceENV;
    private Integer errorItemCounter;
    private Integer errorSubstituteCounter;
    private Integer errorDocumentCOunter;
    
    public ValJsonParser() {
        this.itemObjectMap = new HashMap<>();
        this.documentTreeListMap = new HashMap<>();
        this.substituteMap = new HashMap<>();
        this.errorItemCounter = 0;
        this.errorSubstituteCounter = 0;
        this.errorDocumentCOunter = 0;
    }
    
    public void parse(DataTree itemTree, String sourceENVName, ResultUtil resultUtil) throws CloneNotSupportedException, Exception {
        
        try {
            /*---------------------------------------- ||| Start Json Input Parse ||| ----------------------------------------*/
            VAL_JSON_PARSER.debug("------------- ||| Parsing Process Started ||| -------------");
            sourceENV = sourceENVName;
            String itemName = prepareItemAttributeMap(itemTree, resultUtil);
            if (!NullOrEmptyChecker.isNullOrEmpty(itemName)) {
                VAL_JSON_PARSER.info("FINISHED PARSING ONE DATA TREEE: " + itemName + "(rootObject)");
            }
        } catch (CloneNotSupportedException e) {
            VAL_JSON_PARSER.error(">>>>> Error: " + e);
            throw e;
        }
        finally {
            VAL_JSON_PARSER.debug("------------- ||| Parsing Process Completed ||| -------------\n\n");
        }
    }
    
    public String prepareItemAttributeMap(DataTree itemTree, ResultUtil resultUtil) throws CloneNotSupportedException, Exception {
        /*---------------------------------------- ||| Check for Item's Substitute ||| ----------------------------------------*/
        List<String> substituteItemNameList = null;
        if (!NullOrEmptyChecker.isNullOrEmpty(itemTree.getSubstitutes())) {
            substituteItemNameList = new ArrayList<>();
            for (Substitute substituteTree : itemTree.getSubstitutes()) {
                String substituteItemName = prepareSubstituteAttributeMap(substituteTree, resultUtil);
                if (!NullOrEmptyChecker.isNullOrEmpty(substituteItemName)) {
                    substituteItemNameList.add(substituteItemName);
                }
            }
        }
        
        /*---------------------------------------- ||| Parse Item Attribute ||| ----------------------------------------*/
        VAL_JSON_PARSER.debug("Starting attribute parsing of item");
        
        if(NullOrEmptyChecker.isNull(itemTree.getItem())) {
            TNR errorTNR = new TNR();
            errorTNR.setType("");
            errorTNR.setName("Unknown Item");
            errorTNR.setRevision("");
            resultUtil.addErrorResult("Item "+(++this.errorItemCounter)+": Missing Item Tag", errorTNR, "Error: 'item' Tag was missing");
            VAL_JSON_PARSER.error(">>>>> Error: Error: 'item' Tag was missing");
            return "";
        }
        CreateObjectBean itemObject = CommonUtil.checkCreateObjectBean(itemTree.getItem());
        
        if(NullOrEmptyChecker.isNull(CommonUtil.validateTNR(itemObject.getTnr()))) {
            TNR errorTNR = new TNR();
            errorTNR.setType("");
            errorTNR.setName("Unknown Item");
            errorTNR.setRevision("");
            resultUtil.addErrorResult("Item "+(++this.errorItemCounter)+": Invalid TNR", errorTNR, "Error: Please Provide 'Type' and 'Name' properly");
            VAL_JSON_PARSER.debug(">>>>> Error: Please Provide 'Type' and 'Name' properly");
            return "";
        }
        TNR itemTNR = CommonUtil.validateTNR(itemObject.getTnr());
        
        if(com.bjit.common.rest.app.service.utilities.NullOrEmptyChecker.isNullOrEmpty(itemObject.getAttributes())) {
            resultUtil.addErrorResult(itemTNR.getName(), itemTNR, "Error: Item attribute list was empty");
            VAL_JSON_PARSER.error(">>>>> Error: Item attribute list was empty");
            return "";
        }
        HashMap<String, String> itemAttributes = itemObject.getAttributes();
        
        if (NullOrEmptyChecker.isNullOrEmpty(itemTNR.getName())) {
            resultUtil.addErrorResult("Item "+(++this.errorItemCounter)+": Unknown", itemTNR, "Error: Please Provide 'Name' properly");
            VAL_JSON_PARSER.error(">>>>> Error: Please Provide 'Name' properly");
            return "";
        }
        if (!itemAttributes.containsKey("status") || NullOrEmptyChecker.isNullOrEmpty(itemAttributes.get("status"))) {
            resultUtil.addErrorResult(itemTNR.getName(), itemTNR, "Error: Please Provide 'Status' properly");
            VAL_JSON_PARSER.error(">>>>> Error: Please Provide 'Status' properly");
            return "";
        }
        
        VAL_JSON_PARSER.debug("Ended attribute parsing of item: " + itemTNR.getName());
        itemObject.setSource(sourceENV);
        itemObjectMap.put(itemTNR.getName(), itemObject);
        resultUtil.addItemTNR(itemTNR.getName(), (TNR) itemTNR.clone());
        
        /*---------------------------------------- ||| Add Substitute Item in MAP ||| ----------------------------------------*/
        if (!NullOrEmptyChecker.isNullOrEmpty(substituteItemNameList)) {
            substituteMap.put(itemTNR.getName(), substituteItemNameList);
        }
        /*---------------------------------------- ||| Add Document in MAP ||| ----------------------------------------*/
        List<Document> itemDocumentList = itemTree.getDocuments();
        if (!NullOrEmptyChecker.isNullOrEmpty(itemDocumentList)) {
            List<Document> itemListOfDocumentMap = prepareDocumentMap(itemDocumentList, itemTNR.getName(), resultUtil);
            if (!NullOrEmptyChecker.isNullOrEmpty(itemListOfDocumentMap)) {
                documentTreeListMap.put(itemTNR.getName(), itemListOfDocumentMap);
            }
        }
        
        return itemTNR.getName();
    }
    
    public String prepareSubstituteAttributeMap(Substitute substituteTree, ResultUtil resultUtil) throws CloneNotSupportedException, Exception {
        
        /*---------------------------------------- ||| Parse Substitute Item Attribute ||| ----------------------------------------*/
        VAL_JSON_PARSER.debug("Starting attribute parsing of substitute");
        
        if(NullOrEmptyChecker.isNull(substituteTree.getSubstituteItem())) {
            TNR errorTNR = new TNR();
            errorTNR.setType("");
            errorTNR.setName("Unknown Susbtitute Item");
            errorTNR.setRevision("");
            resultUtil.addErrorResult("Susbtitute Item "+(++this.errorSubstituteCounter)+": Missing Susbtitute Item Tag", errorTNR, "Error: 'substituteItem' Tag was missing");
            VAL_JSON_PARSER.error(">>>>> Error: 'substituteItem' Tag was missing");
            return "";
        }
        CreateObjectBean substituteObject = CommonUtil.checkCreateObjectBean(substituteTree.getSubstituteItem());
        
        if(NullOrEmptyChecker.isNull(CommonUtil.validateTNR(substituteObject.getTnr()))) {
            TNR errorTNR = new TNR();
            errorTNR.setType("");
            errorTNR.setName("Unknown Susbtitute Item");
            errorTNR.setRevision("");
            resultUtil.addErrorResult("Susbtitute Item "+(++this.errorSubstituteCounter)+": Invalid TNR", errorTNR, "Error: Please Provide 'Type' and 'Name' properly");
            VAL_JSON_PARSER.error(">>>>> Error: Please Provide 'Type' and 'Name' properly");
            return "";
        }
        TNR substituteTNR = substituteObject.getTnr();
        
        if (NullOrEmptyChecker.isNullOrEmpty(substituteTNR.getName())) {
            resultUtil.addErrorResult("Susbtitute Item "+(++this.errorSubstituteCounter)+": Unknown", substituteTNR, "Error: Please Provide 'Name' properly");
            VAL_JSON_PARSER.error(">>>>> Error: Please Provide 'Name' properly");
            return "";
        }
        VAL_JSON_PARSER.info("Found Substitute : " + substituteTNR.getName() + "!");
        
        if(com.bjit.common.rest.app.service.utilities.NullOrEmptyChecker.isNullOrEmpty(substituteObject.getAttributes())) {
            resultUtil.addErrorResult(substituteTNR.getName(), substituteTNR, "Error: Substitute Item attribute list was empty");
            VAL_JSON_PARSER.error(">>>>> Error: Substitute Item attribute list was empty");
            return "";
        }
        HashMap<String, String> substituteAttributes = substituteObject.getAttributes();
        
        
        if (!substituteAttributes.containsKey("status") || NullOrEmptyChecker.isNullOrEmpty(substituteAttributes.get("status"))) {
            resultUtil.addErrorResult(substituteTNR.getName(), substituteTNR, "Error: Please Provide 'Status' properly");
            VAL_JSON_PARSER.error(">>>>> Error: Please Provide 'Status' properly");
            return "";
        }
        
        VAL_JSON_PARSER.debug("Ended attribute parsing of substitute : " + substituteTNR.getName());
        substituteObject.setSource(sourceENV);
        itemObjectMap.put(substituteTNR.getName(), substituteObject);
        resultUtil.addItemTNR(substituteTNR.getName(), (TNR) substituteTNR.clone());
        
        /*---------------------------------------- ||| Add Document in MAP ||| ----------------------------------------*/
        List<Document> substituteDocumentList = substituteTree.getDocuments();
        if (!NullOrEmptyChecker.isNullOrEmpty(substituteDocumentList)) {
            List<Document> substituteListOfDocumentMap = prepareDocumentMap(substituteDocumentList, substituteTNR.getName(), resultUtil);
            if (!NullOrEmptyChecker.isNullOrEmpty(substituteListOfDocumentMap)) {
                documentTreeListMap.put(substituteTNR.getName(), substituteListOfDocumentMap);
            }
        }
        return substituteTNR.getName();
    }
    
    public List<Document> prepareDocumentMap(List<Document> documentList, String itemName, ResultUtil resultUtil) throws CloneNotSupportedException, Exception {
        VAL_JSON_PARSER.debug("\n\n");
        List<Document> validDocumentList = new ArrayList<>();
        if (NullOrEmptyChecker.isNullOrEmpty(documentList)) {
            VAL_JSON_PARSER.info("No document/s present with the item".toUpperCase(Locale.ENGLISH));
            return validDocumentList;
        }
        VAL_JSON_PARSER.info("Found " + documentList.size() + " document!");
        
        VAL_JSON_PARSER.debug("Starting attribute parsing of document");
        for(Document document : documentList) {
            if(NullOrEmptyChecker.isNull(document.getDocumentItem())) {
                TNR errorTNR = new TNR();
                errorTNR.setType("");
                errorTNR.setName("Unknown Document");
                errorTNR.setRevision("");
                resultUtil.addErrorResult("Document Item "+(++this.errorDocumentCOunter)+": Missing Document Item Tag", errorTNR, "Error: 'documentItem' Tag was missing");
                VAL_JSON_PARSER.error(">>>>> Error: 'documentItem' Tag was missing");
                CommonUtil.moveFileToErrorLocation(errorTNR, document.getFiles(), resultUtil);
                continue;
            }
            CreateObjectBean documentObject = CommonUtil.checkCreateObjectBean(document.getDocumentItem());
            
            if(NullOrEmptyChecker.isNull(CommonUtil.validateTNR(documentObject.getTnr()))) {
                TNR errorTNR = new TNR();
                errorTNR.setType("");
                errorTNR.setName("Unknown Document");
                errorTNR.setRevision("");
                resultUtil.addErrorResult("Document Item "+(++this.errorDocumentCOunter)+": Invalid TNR", errorTNR, "Error: Please Provide 'Type' and 'Name' properly");
                VAL_JSON_PARSER.error(">>>>> Error: Please Provide 'Type' and 'Name' properly");
                CommonUtil.moveFileToErrorLocation(errorTNR, document.getFiles(), resultUtil);
                continue;
            }
            TNR documentTNR = CommonUtil.validateTNR(documentObject.getTnr());
            
            if (NullOrEmptyChecker.isNullOrEmpty(documentTNR.getType())) {
                resultUtil.addErrorResult("Document Item "+(++this.errorDocumentCOunter)+": Unknown Document", documentTNR, "Error: Please Provide 'Type' properly");
                VAL_JSON_PARSER.error(">>>>> Error: Please Provide 'Type' properly");
                CommonUtil.moveFileToErrorLocation(documentTNR, document.getFiles(), resultUtil);
                continue;
            }
            
            if (NullOrEmptyChecker.isNullOrEmpty(documentTNR.getName())) {
                resultUtil.addErrorResult("Document Item "+(++this.errorDocumentCOunter)+": Unknown Document", documentTNR, "Error: Please Provide 'Name' properly");
                VAL_JSON_PARSER.error(">>>>> Error: Please Provide 'Name' properly");
                CommonUtil.moveFileToErrorLocation(documentTNR, document.getFiles(), resultUtil);
                continue;
            }
            
//            if(!documentTNR.getType().equalsIgnoreCase("Brochure")) {
//                ResultUtil.addErrorResult(documentTNR.getName(), documentTNR, "Error: Expected 'Type' was not found! Found: '" + documentTNR.getType() + "'");
//                VAL_JSON_PARSER.error(">>>>> Error: Expected 'Type' was not found! Found: " + documentTNR.getType());
//                CommonUtil.moveFileToErrorLocation(documentTNR, document.getFiles());
//                continue;
//            }

            if(CommonUtil.checkAllowedTypeListForCreateScopeLink(sourceENV.toLowerCase(), documentTNR.getType()))
                resultUtil.itemListWith3dModels.add(itemName);
            
            if(CommonUtil.checkAllowedCheckInFileTypeList(sourceENV.toLowerCase(), documentTNR.getType())) {
                if(NullOrEmptyChecker.isNullOrEmpty(documentObject.getAttributes())) {
                    resultUtil.addErrorResult(documentTNR.getName(), documentTNR, "Error: Document attribute list was empty");
                    VAL_JSON_PARSER.error(">>>>> Error: Document attribute list was empty");
                    CommonUtil.moveFileToErrorLocation(documentTNR, document.getFiles(), resultUtil);
                    continue;
                }

//                if(NullOrEmptyChecker.isNull(document.getFiles())) {
//                    ResultUtil.addErrorResult(documentTNR.getName(), documentTNR, "Error: Document 'files' tag was missing");
//                    VAL_JSON_PARSER.error(">>>>> Error: Document 'files' tag was missing");
//                    CommonUtil.moveFileToErrorLocation(documentTNR, document.getFiles());
//                    continue;
//                }
//
//                if(document.getFiles().isEmpty()) {
//                    ResultUtil.addErrorResult(documentTNR.getName(), documentTNR, "Error: Document file list was missing");
//                    VAL_JSON_PARSER.error(">>>>> Error: Document file list was missing");
//                    CommonUtil.moveFileToErrorLocation(documentTNR, document.getFiles());
//                    continue;
//                }

                documentObject.setSource(sourceENV);
                validDocumentList.add(document);
                resultUtil.addItemTNR(documentTNR.getName(), (TNR) documentTNR.clone());
            }
        }
        
        VAL_JSON_PARSER.debug("Ended attribute parsing of " + validDocumentList.size() + " document/s");
        return validDocumentList;
    }
    
    public Map<String, List<String>> getSubstituteMap() {
        return substituteMap;
    }
    
    public Map<String, CreateObjectBean> getItemObjectMap() {
        return itemObjectMap;
    }

    public Map<String, List<Document>> getDocumentTreeListMap() {
        return documentTreeListMap;
    }
}
