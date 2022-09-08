package com.bjit.common.rest.pdm_enovia.bom.comparison.data.model.pdm;

import java.util.ArrayList;
import java.util.List;

import com.bjit.common.rest.pdm_enovia.bom.comparison.constant.Constant;
import com.bjit.common.rest.pdm_enovia.bom.comparison.data.model.common.Message;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
/**
 * @author Ashikur Rahman / BJIT
 */
public class PDMResponse {
	
	@JsonIgnore
	private String status;
	
	@JsonIgnore
	private String systemErrors;
	
	@JsonProperty(Constant.PDM_OBJECT_TREE_NODE)
	private List<ChildObj> objectTree = new ArrayList<>();
	
	private List<Message> messages= new ArrayList<>();

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getSystemErrors() {
		return systemErrors;
	}

	public void setSystemErrors(String systemErrors) {
		this.systemErrors = systemErrors;
	}

	public List<ChildObj> getObjectTree() {
		return objectTree;
	}

	public void setObjectTree(List<ChildObj> objectTree) {
		this.objectTree = objectTree;
	}

	public List<Message> getMessages() {
		return messages;
	}

	public void setMessages(List<Message> messages) {
		this.messages = messages;
	}
}
