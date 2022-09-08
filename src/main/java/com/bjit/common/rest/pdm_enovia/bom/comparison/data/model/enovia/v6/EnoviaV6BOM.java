package com.bjit.common.rest.pdm_enovia.bom.comparison.data.model.enovia.v6;

import java.util.LinkedList;
import java.util.List;
import com.bjit.common.rest.pdm_enovia.bom.comparison.data.model.common.Structure;

/**
 * @author Ashikur Rahman / BJIT
 */
public class EnoviaV6BOM extends Structure {
	
	private List<EnoviaV6Attrs> bom = new LinkedList<>();
	
	public List<EnoviaV6Attrs> getBom() {
		return bom;
	}

	public void setBom(List<EnoviaV6Attrs> bom) {
		this.bom = bom;
	}
}