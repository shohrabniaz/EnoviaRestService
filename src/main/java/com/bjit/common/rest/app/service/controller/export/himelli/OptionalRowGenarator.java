package com.bjit.common.rest.app.service.controller.export.himelli;

import java.util.ArrayList;
import java.util.List;
import org.json.simple.JSONObject;

/**
 *
 * @author Touhidul Islam
 * @version 1.0
 * @Since 2021-09-03
 */
public class OptionalRowGenarator {

    private OptionalRow optionalRow;
    private Attributes himelliFields;
    private CellMasters cellMasters;

    public OptionalRowGenarator(Attributes himelliFields) {
        this.himelliFields = himelliFields;
        this.optionalRow = new OptionalRow(himelliFields);
    }

    @Deprecated
    public List<Cell> getRow(JSONObject bomLine) {
        this.optionalRow.setBomLine(bomLine);
        List<Cell> row1 = this.optionalRow.getRow();
        List<Cell> finalRow = new ArrayList<>();

        finalRow = this.cellMasters.getEmptyHimelliRow(himelliFields);

        // each cell in optional row cotains its column position
        for (Cell c : row1) {
            if (c != null) {
                // get the respective Cell, Cell value is empty
                Cell finalCell = finalRow.get(c.getCellIndex());
                // setting just the cell value
                finalCell.setValue(c.getValue());// finalRow will get updated, as changes being made in same place
            }
        }
        return finalRow;
    }

    public List<Row> getRows(JSONObject bomLine) {
        this.optionalRow.setBomLine(bomLine);
        List<Row> rows = this.optionalRow.getRows();
        List<Row> finalRows = new ArrayList<>();
        for (Row row : rows) {
            /*
             * we may get empty row. ie. empty row for bom common text. So just drop it.
             * Otherwise make it standalone new row
             */
            if (Row.isEmptyRow(row)) {
                continue;
            }
            // each cell in optional row cotains its column position
            List<Cell> finalRowCells = this.cellMasters.getEmptyHimelliRow(himelliFields);
            for (Cell c : row.getRowCells()) {
                if (c != null) {
                    // get the respective Cell, Cell value is empty
                    Cell finalCell = finalRowCells.get(c.getCellIndex());
                    // setting just the cell value
                    /*
                     * finalRowCells will get updated, as changes being made in same place
                     */
                    finalCell.setValue(c.getValue());
                }
            }

            finalRows.add(new Row(finalRowCells));
        }

        return finalRows;
    }

    public void setCellMasters(CellMasters cellMasters) {
        this.cellMasters = cellMasters;
    }
}
