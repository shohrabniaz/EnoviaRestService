package com.bjit.common.rest.pdm_enovia.bom.comparison.conversion;

import java.util.ArrayList;
import java.util.List;
import com.bjit.common.rest.pdm_enovia.bom.comparison.data.model.request.Item;
import com.bjit.common.rest.pdm_enovia.bom.comparison.data.model.request.PDMRequestBody;

/**
 * @author Ashikur Rahman / BJIT
 */
public class PDMEnoviaV6Conversion {
	/**
	 * @param itemName
	 * @return
	 * @throws Exception
	 */
	public String conversion(String itemName) throws Exception {
		return PDMEnoviaV6BOMConversion(itemName);
	}
	
	/**
	 * @param itemName
	 * @param revision
	 * @return
	 * @throws Exception
	 */
	public String conversion(String itemName, String revision) throws Exception {
		return PDMEnoviaV6BOMConversion(itemName, revision);
	}

	
	/**
	 * @param type
	 * @param itemName
	 * @param revision
	 * @return
	 * @throws Exception
	 */
	public String conversion(String type, String itemName, String revision) throws Exception {
		return PDMEnoviaV6BOMConversion(type, itemName, revision);
	}
	
	/**
	 * @param itemName
	 * @param revision
	 * @param relationship
	 * @param expansionLevel
	 * @return
	 * @throws Exception
	 */
	public String conversion(String itemName, String revision, String relationship, String expansionLevel) throws Exception {
		return PDMEnoviaV6BOMConversion(itemName, revision, relationship, expansionLevel);
	}
	
	/**
	 * @param type
	 * @param itemName
	 * @param revision
	 * @param relationship
	 * @param expansionLevel
	 * @return
	 * @throws Exception
	 */
	public String conversion(String type, String itemName, String revision, String relationship, String expansionLevel) throws Exception {
		return PDMEnoviaV6BOMConversion(type, itemName, revision, relationship, expansionLevel);
	}
	
	/**
	 * @param items
	 * @param relationshipName
	 * @param objectAttributeList
	 * @param relationshipAttrList
	 * @param expansionLevel
	 * @return
	 * @throws Exception
	 */
	public String conversion(List<Item> items, String relationshipName, List<String> objectAttributeList,
			List<String> relationshipAttrList, String expansionLevel) throws Exception {
		return PDMEnoviaV6BOMConversion(items, relationshipName,
				objectAttributeList, relationshipAttrList, expansionLevel);
	}
	
	/**
	 * @param items
	 * @param relationshipName
	 * @param objectAttributeList
	 * @param relationshipAttrList
	 * @param expansionLevel
	 * @return
	 * @throws Exception
	 */
	private String PDMEnoviaV6BOMConversion(List<Item> items,
										   String relationshipName,
										   List<String> objectAttributeList,
										   List<String> relationshipAttrList,
										   String expansionLevel) throws Exception {
		PDMRequestBody pdmRequestBody = new PDMRequestBody();
		pdmRequestBody.setItems(items);
		pdmRequestBody.setRelationship(relationshipName);
		pdmRequestBody.setObjAttrList(objectAttributeList);
		pdmRequestBody.setRelAttrList(relationshipAttrList);
		pdmRequestBody.setLevel(expansionLevel);
		return (String) new PDMEnoviaV6ConversionController().conversion(pdmRequestBody);
	}
	
	
	/**
	 * @param itemName
	 * @param revision
	 * @param relationship
	 * @param expansionLevel
	 * @return
	 * @throws Exception
	 */
	private String PDMEnoviaV6BOMConversion(String itemName, String revision, String relationship, String expansionLevel) throws Exception {
		PDMRequestBody pdmRequestBody = new PDMRequestBody();
		List<Item> items = new ArrayList<Item>();
		Item item = new Item();
		item.setName(itemName);
		item.setRevision(revision);
		items.add(item);
		pdmRequestBody.setItems(items);
		pdmRequestBody.setRelationship(relationship);
		pdmRequestBody.setLevel(expansionLevel);
		return (String) new PDMEnoviaV6ConversionController().conversion(pdmRequestBody);
	}
	
	
	/**
	 * @param type
	 * @param itemName
	 * @param revision
	 * @param relationship
	 * @param expansionLevel
	 * @return
	 * @throws Exception
	 */
	private String PDMEnoviaV6BOMConversion(String type, String itemName, String revision, String relationship, String expansionLevel) throws Exception {
		PDMRequestBody pdmRequestBody = new PDMRequestBody();
		List<Item> items = new ArrayList<Item>();
		Item item = new Item();
		item.setType(type);
		item.setName(itemName);
		item.setRevision(revision);
		items.add(item);
		pdmRequestBody.setItems(items);
		pdmRequestBody.setRelationship(relationship);
		pdmRequestBody.setLevel(expansionLevel);
		return (String) new PDMEnoviaV6ConversionController().conversion(pdmRequestBody);
	}
	
	/**
	 * @param type
	 * @param itemName
	 * @param revision
	 * @return
	 * @throws Exception
	 */
	private String PDMEnoviaV6BOMConversion(String type, String itemName, String revision) throws Exception {
		PDMRequestBody pdmRequestBody = new PDMRequestBody();
		List<Item> items = new ArrayList<Item>();
		Item item = new Item();
		item.setType(type);
		item.setName(itemName);
		item.setRevision(revision);
		items.add(item);
		pdmRequestBody.setItems(items);
		return (String) new PDMEnoviaV6ConversionController().conversion(pdmRequestBody);
	}
	
	/**
	 * @param itemName
	 * @param revision
	 * @return
	 * @throws Exception
	 */
	private String PDMEnoviaV6BOMConversion(String itemName, String revision) throws Exception {
		PDMRequestBody pdmRequestBody = new PDMRequestBody();
		List<Item> items = new ArrayList<Item>();
		Item item = new Item();
		item.setName(itemName);
		item.setRevision(revision);
		items.add(item);
		pdmRequestBody.setItems(items);
		return (String) new PDMEnoviaV6ConversionController().conversion(pdmRequestBody);
	}
	
	/**
	 * @param itemName
	 * @return
	 * @throws Exception
	 */
	private String PDMEnoviaV6BOMConversion(String itemName) throws Exception {
		PDMRequestBody pdmRequestBody = new PDMRequestBody();
		List<Item> items = new ArrayList<Item>();
		Item item = new Item();
		item.setName(itemName);
		items.add(item);
		pdmRequestBody.setItems(items);
		return (String) new PDMEnoviaV6ConversionController().conversion(pdmRequestBody);
	}
}