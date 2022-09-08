package com.bjit.common.rest.app.service.controller.export.himelli;

// knows all cells details
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * {@link CellMasters} knows to details Column Cell specifications
 *
 * @author Tohidul Islam
 */
public class CellMasters {

    public static final String CELL_DEFAULT_VALUE = "";
    public static final String SEQUENCE_ROW = "SeqRow";
    private Map<String, Cell> defaultCells;

    public CellMasters(Attributes himelliFields) {
        HimelliLogger.getInstance().printLog(" ++++++ CellMasters", LogType.DEBUG);
        this.defaultCells = new HashMap<>();
        List<Attribute> attributes = himelliFields.getAttributes();

        for (int i = 0; i < attributes.size(); i++) {
            Attribute attr = attributes.get(i);
            String himelliHeader = attr.getSource();
            this.defaultCells.put(himelliHeader, new Cell(himelliHeader, CELL_DEFAULT_VALUE, i));
        }

        System.out.println("this.defaultCells:" + this.defaultCells.toString());
        HimelliLogger.getInstance().printLog(" ------ CellMasters", LogType.DEBUG);
    }

    public Cell getCell(String th) {
        Cell c = null;
        if (this.defaultCells.containsKey(th)) {
            c = (Cell) (this.defaultCells.get(th));
            return this.createCell(th, c.getValue(), c.getCellIndex());
        }
        return null;
    }

    public Cell createCell(String th, String cellContent, int cellIndexInRow) {
        return new Cell(th, cellContent, cellIndexInRow);
    }

    public static Cell updateContent(Cell cell, int content) {
        cell.setValue(content);
        return cell;
    }

    public static Cell updateContent(Cell cell, String content) {
        cell.setValue(content);
        return cell;
    }

    /**
     * header name and index exist but not cell content
     *
     * @param himelliFields
     * @return
     */
    public ArrayList<Cell> getEmptyHimelliRow(Attributes himelliFields) {
        List<Attribute> attributes = himelliFields.getAttributes();
        ArrayList<Cell> row = new ArrayList<>();
        for (int i = 0; i < attributes.size(); i++) {
            Attribute attr = attributes.get(i);
            String himelliHeader = attr.getSource();
            row.add(new Cell(himelliHeader, CELL_DEFAULT_VALUE, i));
        }
        return row;
    }

    public List<Cell> mergeCell(List<Cell> row, Cell cell) {
        if (cell != null) {
            // get the respective Cell
            Cell c1 = row.get(cell.getCellIndex());
            // setting just the cell value
            c1.setValue(cell.getValue());// row2 will get updated, as changes being made in same place
        }
        return row;
    }

    /**
     *
     * @param row  the himelli {@link Row}
     * @param cell the himelli {@link Cell}
     * @return an updated himelli {@code Row}
     */
    public Row mergeCell(Row row, Cell cell) {
        return new Row(this.mergeCell(row.getRowCells(), cell));
    }
}
