package com.bjit.common.rest.app.service.controller.export.himelli;

import java.util.ArrayList;
import java.util.List;
import org.json.simple.JSONObject;

/**
 * {@link BomCommonTextOptionalIterator} knows to create bom common text new row
 *
 * @author Tohidul
 */
@Deprecated
public class BomCommonTextOptionalIterator implements OptionalRowIterator {

    private final String KEY = "bom common text";
    private final String vColCellTag = "b";
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
            String bomCommonText = (String) this.bomLine.get(KEY);
            if (bomCommonText != null && !bomCommonText.equals("")) {
                Cell vColCell = this.cellMasters.getCell("vCOL RowTag");
                this.cellMasters.updateContent(vColCell, vColCellTag);
                row.add(vColCell);

                Cell cell = this.cellMasters.getCell("ItemDsc1");
                this.cellMasters.updateContent(cell, bomCommonText);
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
