package com.bjit.common.rest.pdm_enovia.bom.comparison.data.model.pdm;

import com.bjit.common.rest.pdm_enovia.bom.comparison.constant.Constant;
import com.fasterxml.jackson.annotation.JsonProperty;
/**
 * @author Ashikur Rahman / BJIT
 */
public class RelAttr {
	
	@JsonProperty(Constant.RELATIONSHIP_ATTR_LENGTH)
	private double length = 0.0;
	
	@JsonProperty(Constant.RELATIONSHIP_ATTR_WIDTH)
	private double width = 0.0;
	
	@JsonProperty(Constant.RELATIONSHIP_ATTR_POSITION)
	private String position = "";
	
	@JsonProperty(Constant.RELATIONSHIP_ATTR_LEVEL)
	private String level = "";
        
        @JsonProperty(Constant.RELATIONSHIP_ATTR_QUANTITY)
	private String qty = "";
	
	public double getLength() {
		return length;
	}

	public void setLength(double length) {
		this.length = length;
	}

	public double getWidth() {
		return width;
	}

	public void setWidth(double width) {
		this.width = width;
	}

	public String getPosition() {
		return position;
	}

	public void setPosition(String position) {
		this.position = position;
	}

	public String getLevel() {
		return level;
	}

	public void setLevel(String level) {
		this.level = level;
	}
        
        public String getQty() {
		return qty;
	}

	public void setQty(String qty) {
		this.qty = qty;
	}
}
