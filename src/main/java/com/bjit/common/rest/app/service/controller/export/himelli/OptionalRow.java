package com.bjit.common.rest.app.service.controller.export.himelli;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.json.simple.JSONObject;

/**
 * {@link OptionalRow} create new row.
 *
 * <p>
 * In himelli new row business logic exists for item common text or bom common
 * text
 * </p>
 *
 * @author Touhidul Islam
 * @version 1.0
 * @Since 2021-09-03
 */
public class OptionalRow {

    private JSONObject bomLine;
    private Attributes himelliFields;
    private CellMasters cellMasters;
    private final String ITEM_COMMON_TEXT = "item common text";
    private final String BOM_COMMON_TEXT = "bom common text";
    private final String ITEM_PURCHASING_TEXT = "item purchasing text";
    private final String BOM_PURCHASING_TEXT = "bom purchasing text";
    private final String MANUFACTURING_TEXT = "Description";
    private final String BOM_MANUFACTURING_TEXT = "bom manufacturing text";
    private final String VCOLROW_I = "i";
    private final String VCOLROW_B = "b";
    private final String VCOLROW_E = "e";

    public OptionalRow(Attributes himelliFields, CellMasters cellMasters) {
        this.himelliFields = himelliFields;
        this.cellMasters = cellMasters;
        if (this.cellMasters == null) {
            this.cellMasters = new CellMasters(himelliFields);
        }
    }

    public OptionalRow(Attributes himelliFields) {
        this(himelliFields, null);
    }

    /**
     * @return the bomLine
     */
    public JSONObject getBomLine() {
        return bomLine;
    }

    /**
     * @param bomLine the bomLine to set
     */
    public void setBomLine(JSONObject bomLine) {
        this.bomLine = bomLine;
    }

    /**
     * this method combine multiple optional row into single one which is not
     * correct.
     *
     * @see #getRows()
     */
    @Deprecated
    public List<Cell> getRow() {
        List<List<Cell>> rows = new ArrayList<>();
        // List<OptionalRowIterator> iterators = this.configurations.getIterators();
        List<OptionalRowIterator> iterators = this.getIterators();
        Cell[] row = new Cell[this.himelliFields.getAttributes().size()];

        // n numbers of iterator prepares n number of rows
        for (OptionalRowIterator iterator : iterators) {
            iterator.setData(this.bomLine);
            iterator.setCellMasters(this.cellMasters);
            List<Cell> r = iterator.getRow();
            rows.add(r);
        }

        // combine n number of rows into single row
        for (List<Cell> r : rows) {
            for (Cell c : r) {
                row[c.getCellIndex()] = c;
            }
        }
        List<Cell> list = Arrays.asList(row);
        System.out.println(list.toString());
        return list;
    }

    /**
     * this method prepare uniue key of an object regarding its type name
     * revision
     *
     * @return {@code List<Row>} containing optional row. NB it is not a
     * complete optional himelli row but himelli optional row can be generated
     * from here.
     *
     * @author Touhidul Islam
     * @version 1.1
     * @Since 2021-11-03
     */
    public List<Row> getRows() {
        List<Row> rows = new ArrayList<>();
        List<OptionalRowIterator> iterators = this.getIterators();
        Cell[] row = new Cell[this.himelliFields.getAttributes().size()];

        // n numbers of iterator prepares n number of rows
        for (OptionalRowIterator iterator : iterators) {
            iterator.setData(this.bomLine);
            iterator.setCellMasters(this.cellMasters);
            List<Cell> r = iterator.getRow();
            rows.add(new Row(r));
        }
        return rows;
    }

    /**
     * there could be multiple iterator
     *
     * @return
     */
    public List<OptionalRowIterator> getIterators() {
        List<OptionalRowIterator> iterators = new ArrayList<>();
        iterators.add(new GenerateOptionalRow(ITEM_COMMON_TEXT, VCOLROW_I));
        iterators.add(new GenerateOptionalRow(BOM_COMMON_TEXT, VCOLROW_B));
        iterators.add(new GenerateOptionalRow(MANUFACTURING_TEXT, VCOLROW_E));
        iterators.add(new GenerateOptionalRow(BOM_MANUFACTURING_TEXT, VCOLROW_E));
        iterators.add(new GenerateOptionalRow(ITEM_PURCHASING_TEXT, VCOLROW_E));
        iterators.add(new GenerateOptionalRow(BOM_PURCHASING_TEXT, VCOLROW_E));
        return iterators;
    }
}
