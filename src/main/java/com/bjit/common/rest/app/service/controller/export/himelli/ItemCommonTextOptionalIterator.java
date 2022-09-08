package com.bjit.common.rest.app.service.controller.export.himelli;

import java.util.ArrayList;
import java.util.List;
import org.json.simple.JSONObject;

/**
 * {@link ItemCommonTextOptionalIterator} knows to create item common text new
 * row
 *
 * @author Tohidul Islam
 */
@Deprecated
public class ItemCommonTextOptionalIterator implements OptionalRowIterator {

    private final String KEY = "item common text";
    private final String vColCellTag = "i";
    private JSONObject bomLine;
    private CellMasters cellMasters;

    @Override
    public void setData(JSONObject bomLine) {
        this.bomLine = bomLine;
    }

    @Override
    public List<Cell> getRow() {
        List<Cell> row = new ArrayList<>();
        if (this.bomLine.containsKey(KEY)) {
            String itemCommonText = (String) this.bomLine.get(KEY);
            if (itemCommonText != null && !itemCommonText.equals("")) {
                Cell vColCell = this.cellMasters.getCell("vCOL RowTag");
                this.cellMasters.updateContent(vColCell, vColCellTag);
                row.add(vColCell);

                Cell cell = this.cellMasters.getCell("ItemDsc1");
                this.cellMasters.updateContent(cell, itemCommonText);
                row.add(cell);
            }
        }
        return row;
    }

    @Override
    public void setCellMasters(CellMasters cellMasters) {
        this.cellMasters = cellMasters;
    }

}
