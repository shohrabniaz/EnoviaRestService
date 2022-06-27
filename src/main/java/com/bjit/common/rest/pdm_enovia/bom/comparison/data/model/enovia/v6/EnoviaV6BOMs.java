package com.bjit.common.rest.pdm_enovia.bom.comparison.data.model.enovia.v6;

import java.util.LinkedList;
import java.util.List;

import com.bjit.common.rest.pdm_enovia.bom.comparison.data.model.common.Message;
/**
 * @author Ashikur Rahman / BJIT
 */
public class EnoviaV6BOMs {
	
	private List<EnoviaV6BOM> boms = new LinkedList<>();
	private List<Message> messages = new LinkedList<>();
	
	public List<EnoviaV6BOM> getBoms() {
		return boms;
	}
	public void setBoms(List<EnoviaV6BOM> boms) {
		this.boms = boms;
	}
	public List<Message> getMessages() {
		return messages;
	}
	public void setMessages(List<Message> messages) {
		this.messages = messages;
	}
}
