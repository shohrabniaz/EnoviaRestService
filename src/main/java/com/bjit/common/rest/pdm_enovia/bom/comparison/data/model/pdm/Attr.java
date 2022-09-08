package com.bjit.common.rest.pdm_enovia.bom.comparison.data.model.pdm;

import com.bjit.common.rest.pdm_enovia.bom.comparison.constant.Constant;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author Ashikur Rahman / BJIT
 */
public class Attr {

    private String owner = "";
    private String name = "";
    private String modified = "";
    private String id = "";
    private String state = "";
    private String policy = "";
    private String originated = "";
    private String revision = "";
    @JsonProperty(Constant.PDM_ATTR_DRAWING_NUMBER)
    private String drawingNumber = "";
    @JsonProperty(Constant.PDM_ATTR_TRANSFERRED_TO_ERP)
    private String transferToERP = "";
    @JsonProperty(Constant.PDM_ATTR_TITLE)
    private String title = "";

    public String getRevision() {
        return revision;
    }

    public void setRevision(String revision) {
        this.revision = revision;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getModified() {
        return modified;
    }

    public void setModified(String modified) {
        this.modified = modified;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getPolicy() {
        return policy;
    }

    public void setPolicy(String policy) {
        this.policy = policy;
    }

    public String getOriginated() {
        return originated;
    }

    public void setOriginated(String originated) {
        this.originated = originated;
    }

    public String getDrawingNumber() {
        return drawingNumber;
    }

    public void setDrawingNumber(String drawingNumber) {
        this.drawingNumber = drawingNumber;
    }

    public String getTransferToERP() {
        return transferToERP;
    }

    public void setTransferToERP(String transferToERP) {
        this.transferToERP = transferToERP;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String toString() {
        return "Attr [owner=" + owner + ", name=" + name + ", modified=" + modified + ", id=" + id + ", state=" + state
                + ", policy=" + policy + ", originated=" + originated + ", revision=" + revision + ", drawingNumber="
                + drawingNumber + ",transferToERP="
                + transferToERP + ", title="
                + title + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((drawingNumber == null) ? 0 : drawingNumber.hashCode());
        result = prime * result + ((transferToERP == null) ? 0 : transferToERP.hashCode());
        result = prime * result + ((title == null) ? 0 : title.hashCode());
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((modified == null) ? 0 : modified.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((originated == null) ? 0 : originated.hashCode());
        result = prime * result + ((owner == null) ? 0 : owner.hashCode());
        result = prime * result + ((policy == null) ? 0 : policy.hashCode());
        result = prime * result + ((revision == null) ? 0 : revision.hashCode());
        result = prime * result + ((state == null) ? 0 : state.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof Attr)) {
            return false;
        }
        Attr other = (Attr) obj;
        if (drawingNumber == null) {
            if (other.drawingNumber != null) {
                return false;
            }
        } else if (!drawingNumber.equals(other.drawingNumber)) {
            return false;
        }
        if (transferToERP == null) {
            if (other.transferToERP != null) {
                return false;
            }
        } else if (!transferToERP.equals(other.transferToERP)) {
            return false;
        }
        if (title == null) {
            if (other.title != null) {
                return false;
            }
        } else if (!title.equals(other.title)) {
            return false;
        }
        if (id == null) {
            if (other.id != null) {
                return false;
            }
        } else if (!id.equals(other.id)) {
            return false;
        }
        if (modified == null) {
            if (other.modified != null) {
                return false;
            }
        } else if (!modified.equals(other.modified)) {
            return false;
        }
        if (name == null) {
            if (other.name != null) {
                return false;
            }
        } else if (!name.equals(other.name)) {
            return false;
        }
        if (originated == null) {
            if (other.originated != null) {
                return false;
            }
        } else if (!originated.equals(other.originated)) {
            return false;
        }
        if (owner == null) {
            if (other.owner != null) {
                return false;
            }
        } else if (!owner.equals(other.owner)) {
            return false;
        }
        if (policy == null) {
            if (other.policy != null) {
                return false;
            }
        } else if (!policy.equals(other.policy)) {
            return false;
        }
        if (revision == null) {
            if (other.revision != null) {
                return false;
            }
        } else if (!revision.equals(other.revision)) {
            return false;
        }
        if (state == null) {
            if (other.state != null) {
                return false;
            }
        } else if (!state.equals(other.state)) {
            return false;
        }
        return true;
    }
}
