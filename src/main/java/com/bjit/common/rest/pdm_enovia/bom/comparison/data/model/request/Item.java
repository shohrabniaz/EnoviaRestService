package com.bjit.common.rest.pdm_enovia.bom.comparison.data.model.request;

import com.bjit.common.rest.pdm_enovia.bom.comparison.constant.Constant;

/**
 * @author Ashikur Rahman / BJIT
 */
public class Item {
	
	private String type;
	private String name;
	private String revision;
        private String id;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
        public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getRevision() {
		if (revision == null || revision.isEmpty())
			revision = Constant.DEFAULT_REVISION;
		return revision;
	}
	public void setRevision(String revision) {
		this.revision = revision;
	}
	public String getType() {
		if (type == null || type.isEmpty())
			type = Constant.DEFAULT_TYPE;
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	@Override
	public String toString() {
		return "Item [type=" + type + ", name=" + name + ", revision=" + revision + "]";
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((revision == null) ? 0 : revision.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof Item))
			return false;
		Item other = (Item) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (revision == null) {
			if (other.revision != null)
				return false;
		} else if (!revision.equals(other.revision))
			return false;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		return true;
	}
}
