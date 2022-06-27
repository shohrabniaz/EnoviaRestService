package com.bjit.common.rest.pdm_enovia.bom.comparison.data.model.enovia.v6;

import java.util.LinkedList;
import java.util.List;

/**
 * @author Ashikur Rahman / BJIT
 */
public class EnoviaV6Attrs {

    private String name = "";
    private String revision = "";
    private String drawingNumber = "";
    private String inventoryUnit = "";
    private String group = "";
    private String type = "";
    private String description = "";
    private String signalCode = "";
    private String selectionCode = "";
    private String productType = "";
    private String level = "";
    private String position = "";
    private String qty = "";
    private String note = "";
    private double length = 0.0;
    private double width = 0.0;
    private String title = "";
    private String transferToERP = "";
    private List<EnoviaV6Attrs> bomLines = new LinkedList<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRevision() {
        return revision;
    }

    public void setRevision(String revision) {
        this.revision = revision;
    }

    public String getDrawingNumber() {
        return drawingNumber;
    }

    public void setDrawingNumber(String drawingNumber) {
        this.drawingNumber = drawingNumber;
    }

    public String getInventoryUnit() {
        return inventoryUnit;
    }

    public void setInventoryUnit(String inventoryUnit) {
        this.inventoryUnit = inventoryUnit;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSignalCode() {
        return signalCode;
    }

    public void setSignalCode(String signalCode) {
        this.signalCode = signalCode;
    }

    public String getSelectionCode() {
        return selectionCode;
    }

    public void setSelectionCode(String selectionCode) {
        this.selectionCode = selectionCode;
    }

    public String getProductType() {
        return productType;
    }

    public void setProductType(String productType) {
        this.productType = productType;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public List<EnoviaV6Attrs> getBomLines() {
        return bomLines;
    }

    public void setBomLines(List<EnoviaV6Attrs> bomLines) {
        this.bomLines = bomLines;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getQty() {
        return qty;
    }

    public void setQty(String qty) {
        this.qty = qty;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
     public String getTransferToERP() {
        return transferToERP;
    }

    public void setTransferToERP(String transferToERP) {
        this.transferToERP = transferToERP;
    }

    @Override
    public String toString() {
        return "V6Attrs [name=" + name + ", revision=" + revision + ", drawingNumber=" + drawingNumber
                + ", inventoryUnit=" + inventoryUnit + ", group=" + group + ", type=" + type + ", description="
                + description + ", signalCode=" + signalCode + ", selectionCode=" + selectionCode + ", productType="
                + productType + ", position=" + position + ", qty=" + qty + ", level=" + level + ", note=" + note + ", length=" + length
                + ", width=" + width + ",title=" + title + ",  transferToERP=" + transferToERP + ",bomLines=" + bomLines + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((bomLines == null) ? 0 : bomLines.hashCode());
        result = prime * result + ((description == null) ? 0 : description.hashCode());
        result = prime * result + ((drawingNumber == null) ? 0 : drawingNumber.hashCode());
        result = prime * result + ((transferToERP == null) ? 0 : transferToERP.hashCode());
        result = prime * result + ((title == null) ? 0 : title.hashCode());
        result = prime * result + ((group == null) ? 0 : group.hashCode());
        result = prime * result + ((inventoryUnit == null) ? 0 : inventoryUnit.hashCode());
        long temp;
        temp = Double.doubleToLongBits(length);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        result = prime * result + ((level == null) ? 0 : level.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((note == null) ? 0 : note.hashCode());
        result = prime * result + ((position == null) ? 0 : position.hashCode());
        result = prime * result + ((qty == null) ? 0 : qty.hashCode());
        result = prime * result + ((productType == null) ? 0 : productType.hashCode());
        result = prime * result + ((revision == null) ? 0 : revision.hashCode());
        result = prime * result + ((selectionCode == null) ? 0 : selectionCode.hashCode());
        result = prime * result + ((signalCode == null) ? 0 : signalCode.hashCode());
        result = prime * result + ((type == null) ? 0 : type.hashCode());
        temp = Double.doubleToLongBits(width);
        result = prime * result + (int) (temp ^ (temp >>> 32));
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
        if (!(obj instanceof EnoviaV6Attrs)) {
            return false;
        }
        EnoviaV6Attrs other = (EnoviaV6Attrs) obj;
        if (bomLines == null) {
            if (other.bomLines != null) {
                return false;
            }
        } else if (!bomLines.equals(other.bomLines)) {
            return false;
        }
        if (description == null) {
            if (other.description != null) {
                return false;
            }
        } else if (!description.equals(other.description)) {
            return false;
        }
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
        if (group == null) {
            if (other.group != null) {
                return false;
            }
        } else if (!group.equals(other.group)) {
            return false;
        }
        if (inventoryUnit == null) {
            if (other.inventoryUnit != null) {
                return false;
            }
        } else if (!inventoryUnit.equals(other.inventoryUnit)) {
            return false;
        }
        if (Double.doubleToLongBits(length) != Double.doubleToLongBits(other.length)) {
            return false;
        }
        if (level == null) {
            if (other.level != null) {
                return false;
            }
        } else if (!level.equals(other.level)) {
            return false;
        }
        if (name == null) {
            if (other.name != null) {
                return false;
            }
        } else if (!name.equals(other.name)) {
            return false;
        }
        if (note == null) {
            if (other.note != null) {
                return false;
            }
        } else if (!note.equals(other.note)) {
            return false;
        }
        if (position == null) {
            if (other.position != null) {
                return false;
            }
        } else if (!position.equals(other.position)) {
            return false;
        }
        if (qty == null) {
            if (other.qty != null) {
                return false;
            }
        } else if (!qty.equals(other.qty)) {
            return false;
        }
        if (productType == null) {
            if (other.productType != null) {
                return false;
            }
        } else if (!productType.equals(other.productType)) {
            return false;
        }
        if (revision == null) {
            if (other.revision != null) {
                return false;
            }
        } else if (!revision.equals(other.revision)) {
            return false;
        }
        if (selectionCode == null) {
            if (other.selectionCode != null) {
                return false;
            }
        } else if (!selectionCode.equals(other.selectionCode)) {
            return false;
        }
        if (signalCode == null) {
            if (other.signalCode != null) {
                return false;
            }
        } else if (!signalCode.equals(other.signalCode)) {
            return false;
        }
        if (type == null) {
            if (other.type != null) {
                return false;
            }
        } else if (!type.equals(other.type)) {
            return false;
        }
        if (Double.doubleToLongBits(width) != Double.doubleToLongBits(other.width)) {
            return false;
        }
        return true;
    }
}
