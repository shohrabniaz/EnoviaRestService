package com.bjit.common.rest.pdm_enovia.bom.comparison.data.model.request;

import java.util.ArrayList;
import java.util.List;
import com.bjit.common.rest.pdm_enovia.bom.comparison.constant.Constant;
import com.sun.mail.iap.ParsingException;

/**
 * @author Ashikur Rahman / BJIT
 */
public class PDMRequestBody {
	
	private List<Item> items;
	private String relationship;
	private List<String> objAttrList;
	private List<String> relAttrList;
	private String level;
	
	public List<Item> getItems() throws ParsingException {
		if (items == null || items.size() < 1) {
			throw new ParsingException(Constant.ITEM_CANNOT_BE_NULL_EXCEPTION);
		}
		return items;
	}
	public void setItems(List<Item> items) {
		this.items = items;
	}
	public String getRelationship() {
		if (relationship == null || relationship.trim().equalsIgnoreCase(""))
			relationship = Constant.DEFAULT_RELATIONSHIP;
		return relationship;
	}
	public void setRelationship(String relationship) {
		this.relationship = relationship;
	}
	public List<String> getObjAttrList() {
		if (objAttrList == null || objAttrList.size() < 1) {
			objAttrList = new ArrayList<>();
			objAttrList.add(Constant.BOM_ATTRIBUTE_DRAWING_NUMBER);
                        objAttrList.add(Constant.PDM_ATTR_TRANSFERRED_TO_ERP);
			objAttrList.add(Constant.BOM_ATTRIBUTE_CUSTOMER_BOM_TYPE);
                        objAttrList.add(Constant.BOM_ATTRIBUTE_TITLE);
		}
		return objAttrList;
	}
	public void setObjAttrList(List<String> objAttrList) {
		this.objAttrList = objAttrList;
	}
	public List<String> getRelAttrList() {
		if (relAttrList == null || relAttrList.size() < 1) {
			relAttrList = new ArrayList<>();
			relAttrList.add(Constant.RELATIONSHIP_ATTR_WIDTH);
			relAttrList.add(Constant.RELATIONSHIP_ATTR_LENGTH);
			relAttrList.add(Constant.RELATIONSHIP_ATTR_POSITION);
			relAttrList.add(Constant.RELATIONSHIP_ATTR_LEVEL);
                        relAttrList.add(Constant.RELATIONSHIP_ATTR_QUANTITY);
		}
		return relAttrList;
	}
	
	public void setRelAttrList(List<String> relAttrList) {
		this.relAttrList = relAttrList;
	}
	public String getLevel() {
		if (level == null || level.trim().equalsIgnoreCase(""))
			level = Constant.DEFAULT_LEVEL;
		return level;
	}
	public void setLevel(String level) {
		this.level = level;
	}
	
	@Override
	public String toString() {
		return "PDMRequestBody [items=" + items + ", relationship=" + relationship + ", objAttrList=" + objAttrList
				+ ", relAttrList=" + relAttrList + ", level=" + level + "]";
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((items == null) ? 0 : items.hashCode());
		result = prime * result + ((level == null) ? 0 : level.hashCode());
		result = prime * result + ((objAttrList == null) ? 0 : objAttrList.hashCode());
		result = prime * result + ((relAttrList == null) ? 0 : relAttrList.hashCode());
		result = prime * result + ((relationship == null) ? 0 : relationship.hashCode());
		return result;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof PDMRequestBody))
			return false;
		PDMRequestBody other = (PDMRequestBody) obj;
		if (items == null) {
			if (other.items != null)
				return false;
		} else if (!items.equals(other.items))
			return false;
		if (level == null) {
			if (other.level != null)
				return false;
		} else if (!level.equals(other.level))
			return false;
		if (objAttrList == null) {
			if (other.objAttrList != null)
				return false;
		} else if (!objAttrList.equals(other.objAttrList))
			return false;
		if (relAttrList == null) {
			if (other.relAttrList != null)
				return false;
		} else if (!relAttrList.equals(other.relAttrList))
			return false;
		if (relationship == null) {
			if (other.relationship != null)
				return false;
		} else if (!relationship.equals(other.relationship))
			return false;
		return true;
	}
}
