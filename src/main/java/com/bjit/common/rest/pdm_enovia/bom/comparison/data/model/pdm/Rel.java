package com.bjit.common.rest.pdm_enovia.bom.comparison.data.model.pdm;

import com.bjit.common.rest.pdm_enovia.bom.comparison.constant.Constant;
import com.fasterxml.jackson.annotation.JsonProperty;
/**
 * @author Ashikur Rahman / BJIT
 */
public class Rel {
	
	private String type;
	
	@JsonProperty(Constant.PDM_ATTR_NODE)
	private RelAttr relAttr;
	
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public RelAttr getRelAttr() {
		return relAttr;
	}
	public void setRelAttr(RelAttr relAttr) {
		this.relAttr = relAttr;
	}
}
