package com.bjit.common.rest.pdm_enovia.bom.comparison.data.model.pdm;

import java.util.List;
/**
 * @author Ashikur Rahman / BJIT
 */
public class ChildObj {
	
	private String type;
	private Attr attr;
	private List<ChildObj> obj;
	private Rel rel;
	
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public Attr getAttr() {
		return attr;
	}
	public void setAttr(Attr attr) {
		this.attr = attr;
	}
	public List<ChildObj> getObj() {
		return obj;
	}
	public void setObj(List<ChildObj> obj) {
		this.obj = obj;
	}
	public Rel getRel() {
		return rel;
	}
	public void setRel(Rel rel) {
		this.rel = rel;
	}
}
