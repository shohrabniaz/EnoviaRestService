package com.bjit.common.rest.app.service.controller.export.himelli;

import java.util.List;
import org.json.simple.JSONObject;

/**
 * 
 * @author Tohidul Islam
 */
public interface OptionalRowIterator {

    public abstract List<Cell> getRow();

    public abstract void setData(JSONObject bomLine);

    public void setCellMasters(CellMasters cellMasters);
}
