package com.bjit.common.rest.app.service.controller.export.himelli;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * MultiLevelBOMStructureUtils is a utility class to convert multiLevelBomExport
 * json output to hierarchical tree-structured json. Also modify data regarding
 * to himelli export data.
 *
 * @author Touhidul Islam
 * @version 1.0
 * @Since 2021-04-02
 */
public class MultiLevelBOMStructureUtils {

    private JSONObject json = null;
    private JSONObject treeStructuredJson = null;
    private Map<String, JSONObject> bomLineMap = new HashMap<>();
    private boolean isDataInTreeStructured;
    private JSONObject updatedBOMLinesForHimelli;
    private boolean isBOMLinesForHimelliUpdated;

    /**
     * @return the isBOMLinesForHimelliUpdated
     */
    public boolean isBOMLinesForHimelliUpdated() {
        return isBOMLinesForHimelliUpdated;
    }

    /**
     * @param isBOMLinesForHimelliUpdated the isBOMLinesForHimelliUpdated to set
     */
    private void setIsBOMLinesForHimelliUpdated(boolean isBOMLinesForHimelliUpdated) {
        this.isBOMLinesForHimelliUpdated = isBOMLinesForHimelliUpdated;
    }

    /**
     * this EnoviaObject Inner class properties used to fetch type name revision
     * info from json to make unique key
     *
     * @author Touhidul Islam
     * @version 1.0
     * @Since 2021-04-02
     */
    private static class EnoviaObject {

        public static String type = "Type";
        public static String name = "name";
        public static String revision = "revision";
    }

    /**
     * this BOMLineAttribute Inner class properties used to define the json keys
     *
     * @author Touhidul Islam
     * @version 1.0
     * @Since 2021-04-02
     */
    private static class BOMLineAttribute {

        public static String TITLE = "Title";
        public static String NAME = "name";
    }

    public MultiLevelBOMStructureUtils() {

    }

    public MultiLevelBOMStructureUtils(String jsonStr) {
        JSONParser parser = new JSONParser();
        try {
            this.json = (JSONObject) parser.parse(jsonStr);
        } catch (ParseException ex) {
            Logger.getLogger(MultiLevelBOMStructureUtils.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public MultiLevelBOMStructureUtils(JSONObject json) {
        this.json = json;
    }

    public MultiLevelBOMStructureUtils(JSONObject json, boolean isDataInTreeStructured) {
        this.isDataInTreeStructured = isDataInTreeStructured;
        if (isDataInTreeStructured) {
            this.treeStructuredJson = json;
        }
        this.json = json;
    }

    /**
     * @return the json
     */
    public JSONObject getJson() {
        return json;
    }

    /**
     * @param json the json to set
     */
    public void setJson(JSONObject json) {
        this.json = json;
    }

    /**
     * Get hierarchical tree-structured data derived from multi Level Bom Export
     * data
     *
     * @return the treeStructuredJson
     *
     * @author Touhidul Islam
     * @version 1.0
     * @Since 2021-04-02
     */
    public JSONObject getTreeStructuredJson() {
        if (this.json == null) {
            return this.treeStructuredJson;
        }
        this.bomLineMap = this.buildSingleBomLinesMap(this.json);
        // worked only results json array
        JSONObject obj = this.buildTreeStructuredJSONObject();
        // compose the status with tree-structured data
        return composeData(obj);
    }

    /**
     * this method responsible to build map of bom lines
     *
     * @param {JSONObject} json - json data from multiLevelBomExport rest call
     * @return singleBomLinesMap - key is objects type,name,revision combination and
     *         value is hierarchical JSON object.
     *
     * @author Touhidul Islam
     * @version 1.0
     * @Since 2021-04-02
     */
    private Map<String, JSONObject> buildSingleBomLinesMap(JSONObject json) {
        Map<String, JSONObject> singleBomLinesMap = new HashMap<>();
        JSONObject dataObject = (JSONObject) this.json.get("data");
        JSONArray results = (JSONArray) dataObject.get("results");

        for (int i = 1; i < results.size(); i++) {
            JSONObject rowObject = (JSONObject) results.get(i);
            String objectUniqueKey = this.getObjectUniqueKey(rowObject);
            singleBomLinesMap.put(objectUniqueKey, rowObject);
        }
        return singleBomLinesMap;
    }

    /**
     * this method prepare uniue key of an object regarding its type name revision
     *
     * @param {String} type - object type
     * @param {String} name - object name
     * @param {String} revision - object revision
     * @return {String} key
     *
     * @author Touhidul Islam
     * @version 1.1
     * @Since 2021-04-02
     */
    @Deprecated
    private String getObjectUniqueKey(String type, String name, String revision) {
        String key = type.replaceAll(" ", "") + ":" + name.replaceAll(" ", "") + ":";
        if (!revision.isEmpty()) {
            key += revision.replaceAll(" ", "");
        }
        return key;
    }

    /**
     * this method fetch unique key of an object.
     *
     * For himelli report purpose multilevel bom export report set a
     * unique key for each bomline
     *
     * @param {JSONObject} obj - bom line object
     * @return {String} key
     *
     * @author Touhidul Islam
     * @version 1.0
     * @Since 2021-11-03
     */
    private String getObjectUniqueKey(JSONObject obj) {
        String objUniqueKey = (String) obj.getOrDefault("Unique Key", "#");
        if (objUniqueKey == null || objUniqueKey.equals("")) {
            objUniqueKey = (String) obj.get("id");
        }
        return objUniqueKey;
    }

    /**
     * gateway method to convert multi Level Bom json data to tree structured data
     *
     * @param {String} type - object type
     * @param {String} name - object name
     * @param {String} revision - object revision
     * @return {String} key
     *
     * @author Touhidul Islam
     * @version 1.0
     * @Since 2021-04-02
     */
    private JSONObject buildTreeStructuredJSONObject() {
        JSONObject dataObject = (JSONObject) this.json.get("data");
        JSONArray results = (JSONArray) dataObject.get("results");
        JSONObject rootObject = (JSONObject) results.get(0);
        return this.getTreeStructuredBOMLines(rootObject);
    }

    /**
     * make parent object bomlines tree-structured
     *
     * @param {JSONObject} parentObj
     * @return {String} key
     *
     * @author Touhidul Islam
     * @version 1.0
     * @Since 2021-04-02
     */
    private JSONObject getTreeStructuredBOMLines(JSONObject parentObj) {
        JSONArray bomLines = (JSONArray) parentObj.get("bomLines");
        if (bomLines.size() > 0) {
            bomLines = this.iterateBomLines(bomLines, this.bomLineMap);
            parentObj.put("bomLines", bomLines);
        }
        return parentObj;
    }

    /**
     * iterate each bom lines and make bom lines tree-structured
     *
     * @param {JSONArray} bomLines
     * @param {Map}       objectMap - mapping of enovia object to bomlines JSON
     *                    object
     * @return {String} key
     *
     * @author Touhidul Islam
     * @version 1.0
     * @Since 2021-04-02
     */
    private JSONArray iterateBomLines(JSONArray bomLines, Map<String, JSONObject> objectMap) {
        for (int i = 0; i < bomLines.size(); i++) {
            JSONObject rowObject = (JSONObject) bomLines.get(i);
            String type = (String) rowObject.get(EnoviaObject.type);
            String name = (String) rowObject.get(EnoviaObject.name);
            String revision = (String) rowObject.get(EnoviaObject.revision);
            String objectUniqueKey = this.getObjectUniqueKey(rowObject);

            if (objectMap.containsKey(objectUniqueKey)) {
                JSONObject hierarchicalObject = objectMap.get(objectUniqueKey);
                hierarchicalObject = this.getTreeStructuredBOMLines(hierarchicalObject);
                bomLines.set(i, hierarchicalObject);
            }
        }
        return bomLines;
    }

    private JSONObject composeData(JSONObject obj) {
        this.treeStructuredJson = this.json;
        JSONArray results = new JSONArray();
        results.add(obj);

        JSONObject data = (JSONObject) this.treeStructuredJson.get("data");
        data.put("results", results);
        return this.treeStructuredJson;
    }

    /**
     * iterate each bom lines and add level, parent title
     *
     * @return {JSONObject} updatedBOMLinesForHimelli
     *
     * @author Touhidul Islam
     * @version 1.0
     * @Since 2021-04-05
     */
    public JSONObject modifyDataForHimeliExport() {
        if (!this.isDataInTreeStructured) {
            this.getTreeStructuredJson();
        }
        JSONObject updated = this.updateBOMLinesForHimelli(this.getUpdatedRootObject());
        JSONArray results = new JSONArray();
        results.add(updated);

        JSONObject clonedTreeStructuredObj = this.getClonedObject(this.treeStructuredJson);
        JSONObject data = (JSONObject) clonedTreeStructuredObj.get("data");
        data.put("results", results);
        this.updatedBOMLinesForHimelli = clonedTreeStructuredObj;
        this.setIsBOMLinesForHimelliUpdated(true);
        return this.updatedBOMLinesForHimelli;
    }

    /**
     * get a copy of updated root object level,parentTitle updated also this object
     * can be safely modified by caller
     *
     * @return {JSONObject}
     *
     * @author Touhidul Islam
     * @version 1.0
     * @Since 2021-04-05
     */
    private JSONObject getUpdatedRootObject() {
        JSONObject clonedObj = this.getClonedObject(this.treeStructuredJson);
        JSONObject data = (JSONObject) clonedObj.get("data");
        JSONArray results = (JSONArray) data.get("results");
        JSONObject rootObject = (JSONObject) results.get(0);
        rootObject.put("parentTitle", "");
        rootObject.put("level", 0);
        return rootObject;
    }

    /**
     * updating each bomline regarding its level and its parent title info
     *
     * @return {JSONObject}
     *
     * @author Touhidul Islam
     * @version 1.0
     * @Since 2021-04-05
     */
    private JSONObject updateBOMLinesForHimelli(JSONObject parentObj) {
        if (!parentObj.containsKey("bomLines")) {
            return parentObj;
        }
        JSONArray bomLines = (JSONArray) parentObj.get("bomLines");
        for (int i = 0; i < bomLines.size(); i++) {
            JSONObject bomLineJsonObj = (JSONObject) bomLines.get(i);
            String parentTitle = (String) parentObj.get(BOMLineAttribute.TITLE);
            int level = (int) parentObj.get("level") + 1;
            String parentName = (String) parentObj.get(BOMLineAttribute.NAME);
            bomLineJsonObj.put("parentTitle", parentTitle);
            bomLineJsonObj.put("level", level);
            bomLineJsonObj.put("parentName", parentName);
            bomLines.set(i, this.updateBOMLinesForHimelli(bomLineJsonObj));
        }
        parentObj.put("bomLines", bomLines);
        return parentObj;
    }

    /**
     * get a copy of passed JSONObject which can be safely modified by caller
     *
     * @return {JSONObject}
     *
     * @author Touhidul Islam
     * @version 1.0
     * @Since 2021-04-05
     */
    private JSONObject getClonedObject(JSONObject obj) {
        JSONParser parser = new JSONParser();
        try {
            return (JSONObject) parser.parse(obj.toString());
        } catch (ParseException ex) {
            Logger.getLogger(MultiLevelBOMStructureUtils.class.getName()).log(Level.SEVERE, null, ex);
        }
        return obj;
    }
}
