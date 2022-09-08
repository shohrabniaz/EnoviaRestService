package com.bjit.common.rest.app.service.controller.export.himelli;

/**
 * @author Ashikur / BJIT
 *
 */
public class Cell implements Cloneable {

    private int cellIndex = -1;
    private String himelliField;
    private String value;

    public Cell(String himelliField, String value) {
        super();
        this.himelliField = himelliField;
        this.value = value;
    }

    public Cell(String himelliField, String value, int cellIndex) {
        this(himelliField, value);
        this.cellIndex = cellIndex;
    }

    public String getHimelliField() {
        return himelliField;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "Cell [himelliField=" + himelliField + ", value=" + getValue() + ", index=" + getCellIndex() + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((himelliField == null) ? 0 : himelliField.hashCode());
        result = prime * result + ((getValue() == null) ? 0 : getValue().hashCode());
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
        if (getClass() != obj.getClass()) {
            return false;
        }
        Cell other = (Cell) obj;
        if (himelliField == null) {
            if (other.himelliField != null) {
                return false;
            }
        } else if (!himelliField.equals(other.himelliField)) {
            return false;
        }
        if (getValue() == null) {
            if (other.getValue() != null) {
                return false;
            }
        } else if (!value.equals(other.value)) {
            return false;
        }
        return true;
    }

    /**
     * @return the cellIndex
     */
    public int getCellIndex() {
        return cellIndex;
    }

    /**
     * @param cellIndex the cellIndex to set
     * @author Tohidul Islam
     */
    public void setCellIndex(int cellIndex) {
        this.cellIndex = cellIndex;
    }

    /**
     * @param value the value to set
     * @author Tohidul Islam
     */
    public void setValue(String value) {
        this.value = value;
    }

    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    /**
     *
     * @param value
     * @author Tohidul Islam
     */
    void setValue(int value) {
        this.setValue(String.valueOf(value));
    }
}
