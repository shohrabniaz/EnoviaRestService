package com.bjit.common.rest.pdm_enovia.bom.comparison.utility;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import com.bjit.common.rest.pdm_enovia.bom.comparison.conversion.PDMEnoviaV6ConversionController;
import com.bjit.common.rest.pdm_enovia.bom.comparison.data.model.common.Message;
import com.bjit.common.rest.pdm_enovia.bom.comparison.data.model.enovia.v6.EnoviaV6Attrs;
import com.bjit.common.rest.pdm_enovia.bom.comparison.data.model.enovia.v6.EnoviaV6BOM;
import com.bjit.common.rest.pdm_enovia.bom.comparison.data.model.enovia.v6.EnoviaV6BOMs;
import com.bjit.common.rest.pdm_enovia.bom.comparison.data.model.pdm.Attr;
import com.bjit.common.rest.pdm_enovia.bom.comparison.data.model.pdm.ChildObj;
import com.bjit.common.rest.pdm_enovia.bom.comparison.data.model.pdm.PDMResponse;
import com.bjit.common.rest.pdm_enovia.bom.comparison.data.model.pdm.Rel;
import com.bjit.common.rest.pdm_enovia.bom.comparison.data.model.pdm.RelAttr;
import static com.bjit.common.rest.pdm_enovia.bom.comparison.utility.JSONUtil.prettyPrint;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;

/**
 * @author Ashikur Rahman / BJIT
 */
public class PDMEnoviaV6Util extends JSONUtil {
	
	/**
	 * @param childObj
	 * @param childNodeList
	 * @return
	 * @throws JsonProcessingException
	 */
	public static List<EnoviaV6Attrs> conversionToEnoviaV6ExportJSON(ChildObj childObj, List<EnoviaV6Attrs> childNodeList) throws JsonProcessingException {
		List<EnoviaV6Attrs> getBOMLines = getEnoviaV6BOMLines(childObj);
		if (getBOMLines != null) {
			EnoviaV6Attrs enoviaBOMNode = getEnoviaV6BOMNode(childObj, getBOMLines);
			childNodeList.add(enoviaBOMNode);
			List<ChildObj> obj = childObj.getObj();
			if (obj != null) {
				for (ChildObj object : obj) {
					conversionToEnoviaV6ExportJSON(object, childNodeList);
				}
			}
		}
		return childNodeList;
	}
	
	/**
	 * @param childObj
	 * @param parentNode
	 * @return
	 * @throws JsonProcessingException
	 */
	public static EnoviaV6Attrs conversionToEnoviaV6ComparisonJSON(ChildObj childObj, EnoviaV6Attrs parentNode) throws JsonProcessingException {
		 List<ChildObj> childObjs = childObj.getObj();
		 if (childObjs != null) {
			 sortingBOMbyPosition(childObjs, true);
			 for (ChildObj childObject : childObjs) {
		         EnoviaV6Attrs bomNode = getEnoviaV6BomAttributes(childObject);
		         parentNode.getBomLines().add(bomNode);
		         conversionToEnoviaV6ComparisonJSON(childObject, bomNode);
			 }
		 }
		 return parentNode;
	}
	
	/**
	 * @param childObj
	 * @param bomLines
	 * @return
	 * @throws JsonProcessingException
	 */
	public static EnoviaV6Attrs getEnoviaV6BOMNode(ChildObj childObj, List<EnoviaV6Attrs> bomLines) throws JsonProcessingException {
		EnoviaV6Attrs enoviaAttrs = getEnoviaV6BomAttributes(childObj);
		enoviaAttrs.setBomLines(bomLines);
		return enoviaAttrs;
	}
	
	/**
	 * @param childObj
	 * @return
	 * @throws JsonProcessingException
	 */
	public static List<EnoviaV6Attrs> getEnoviaV6BOMLines(ChildObj childObj) throws JsonProcessingException {
		List<EnoviaV6Attrs> bomLines = new ArrayList<>();
		List<ChildObj> obj = childObj.getObj();
		if (obj != null) {
			for (ChildObj object : obj) {
				EnoviaV6Attrs enoviaAttrs = getEnoviaV6BomAttributes(object);
				bomLines.add(enoviaAttrs);
			}
		}
		return bomLines;
	}
	
	/**
	 * @param object
	 * @return
	 */
	public static EnoviaV6Attrs getEnoviaV6BomAttributes(ChildObj object) {
		String type = object.getType();
		Attr attr = object.getAttr();
		Rel rel = object.getRel();
		RelAttr relAttr = null;
		EnoviaV6Attrs v6Attrs = new EnoviaV6Attrs();
		v6Attrs.setName(attr.getName());
		v6Attrs.setRevision(attr.getRevision());
		v6Attrs.setDrawingNumber(attr.getDrawingNumber());
                v6Attrs.setTitle(attr.getTitle());
		v6Attrs.setTransferToERP(attr.getTransferToERP());
		if (rel != null) {
			relAttr = rel.getRelAttr();
			v6Attrs.setLength(relAttr.getLength());
			v6Attrs.setWidth(relAttr.getWidth());
			v6Attrs.setPosition(relAttr.getPosition());
			v6Attrs.setLevel(relAttr.getLevel());
                        v6Attrs.setQty(relAttr.getQty());
		}
		return v6Attrs;
	}
	
	public static JsonNode getEnoviaV6JSON() throws Exception {
		PDMResponse pdmResponse = (PDMResponse) PDMEnoviaV6ConversionController.pdmJSONDataParse();
		List<ChildObj> childObjList = pdmResponse.getObjectTree();
        List<Message> messages = pdmResponse.getMessages();
        List<EnoviaV6BOM> v6Structures = new LinkedList<>();
        EnoviaV6BOMs finalStructure = new EnoviaV6BOMs();
        JsonNode jsonNode = null;
        if (childObjList != null) {
        	for(ChildObj obj : childObjList) {
        		List<EnoviaV6Attrs> childNodeList = new LinkedList<>();
        		EnoviaV6BOM v6Structure = new EnoviaV6BOM();
        		EnoviaV6Attrs bomNode = PDMEnoviaV6Util.getEnoviaV6BomAttributes(obj);
	            EnoviaV6Attrs parentNode = PDMEnoviaV6Util.conversionToEnoviaV6ComparisonJSON(obj, bomNode);
	            childNodeList.add(parentNode);                  
	            v6Structure.setBom(childNodeList);
	            v6Structures.add(v6Structure);
	        }
        	finalStructure.setBoms(v6Structures);
        }
        finalStructure.setMessages(messages);
        jsonNode = JSONUtil.toJson(finalStructure);
        return jsonNode;
	}
	
	/**
	 * @return
	 * @throws Exception
	 */
	public static String getEnoviaV6JSONAsString() throws Exception {
		return prettyPrint(getEnoviaV6JSON());
	}
	
	/**
	 * @return
	 * @throws Exception
	 */
	public static JsonNode getEnoviaV6BOMExportJSON() throws Exception {
		PDMResponse pdmResponse = (PDMResponse) PDMEnoviaV6ConversionController.pdmJSONDataParse();
        List<EnoviaV6BOM> enoviaBOMStructures = new LinkedList<>();
        EnoviaV6BOMs finalStructure = new EnoviaV6BOMs();
        List<ChildObj> childObjList = pdmResponse.getObjectTree();
        List<Message> messages = pdmResponse.getMessages();
        JsonNode jsonNode = null;
        if (childObjList != null) {
        	for(ChildObj obj : childObjList) {
            	List<EnoviaV6Attrs> getBOMLines = new ArrayList<>();
            	List<EnoviaV6Attrs> childNodeList = new ArrayList<>();
            	EnoviaV6BOM enoviaBOMStructure = new EnoviaV6BOM();
    			getBOMLines = PDMEnoviaV6Util.conversionToEnoviaV6ExportJSON(obj, childNodeList);
                enoviaBOMStructure.setBom(getBOMLines);
                enoviaBOMStructures.add(enoviaBOMStructure);
            }
            finalStructure.setBoms(enoviaBOMStructures);
        }
        finalStructure.setMessages(messages);
        jsonNode = JSONUtil.toJson(finalStructure);
        return jsonNode;
	}
	
	/**
	 * @return
	 * @throws Exception
	 */
	public static String getEnoviaV6BOMExportJSONAsString() throws Exception {
		return prettyPrint(getEnoviaV6BOMExportJSON());
	}
	
	/**
	 * @param childObjs
	 * @param isAscending
	 */
	private static void sortingBOMbyPosition(List<ChildObj> childObjs, boolean isAscending) {
		Collections.sort(childObjs, new Comparator<ChildObj>() {
			  @Override
			  public int compare(ChildObj u1, ChildObj u2) {
				Rel childObj1Rel = u1.getRel();
				Rel childObj2Rel = u2.getRel();
				Integer childObj1Position = 0;
				Integer childObj2Position = 0;
				if (childObj1Rel != null && childObj2Rel != null) {
					RelAttr childObj1RelAttr = childObj1Rel.getRelAttr();
					RelAttr childObj2RelAttr = childObj2Rel.getRelAttr();
					childObj1Position = Integer.parseInt(childObj1RelAttr.getPosition());
					childObj2Position = Integer.parseInt(childObj2RelAttr.getPosition());
				}
				if (isAscending)
					return childObj1Position.compareTo(childObj2Position);
				else
					return childObj2Position.compareTo(childObj1Position);
			  }
		 });
	}
}
