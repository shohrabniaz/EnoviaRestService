package com.bjit.common.rest.app.service.controller.export.himelli;

import java.util.ArrayList;
import java.util.List;
import org.json.simple.JSONObject;

/**
 *
 * @author Horidas Roy
 */
public class GenerateOptionalRow implements OptionalRowIterator {

    private String KEY;
    private String vColCellTag;
    private JSONObject bomLine;
    private CellMasters cellMasters;
    private final String COLUMNNAME1 = "vCOL RowTag";
    private final String COLUMNNAME2 = "ItemDsc1";

    public GenerateOptionalRow(String Key, String VcolRow) {
        this.KEY = Key;
        this.vColCellTag = VcolRow;
    }

    @Override
    public void setData(JSONObject bomLine) {
        this.bomLine = bomLine;
    }

    //will return a cell
    //Column name in which value need to be added and value as parameter.
    public Cell addDataToRequiredColumn(String columnName, String value) {
        Cell vColCell = this.cellMasters.getCell(columnName);
        this.cellMasters.updateContent(vColCell, value);

        return vColCell;
    }

    @Override
    public List<Cell> getRow() {
        List<Cell> row = new ArrayList<>();
        if (this.bomLine.containsKey(KEY)) {
            String valueOfOptionalRow = (String) this.bomLine.get(KEY);
            if (valueOfOptionalRow != null && !valueOfOptionalRow.equals("")) {

                //send column name(in which column value need to be added) and value as parameter
                row.add(addDataToRequiredColumn(COLUMNNAME1, vColCellTag));
                row.add(addDataToRequiredColumn(COLUMNNAME2, valueOfOptionalRow));

            }
        }
        return row;
    }

    @Override
    public void setCellMasters(CellMasters cellMasters) {
        this.cellMasters = cellMasters;
    }

}
