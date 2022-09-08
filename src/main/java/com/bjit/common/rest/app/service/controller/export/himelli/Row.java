package com.bjit.common.rest.app.service.controller.export.himelli;

import java.util.LinkedList;
import java.util.List;

public class Row {

    private List<Cell> row = new LinkedList<>();

    public Row(List<Cell> row) {
        super();
        this.row = row;
    }

    public List<Cell> getRowCells() {
        return row;
    }

    /**
     * checking each row cell value
     *
     * @param cells
     * @return
     * @author Tohidul Islam
     */
    public static boolean isEmptyRow(List<Cell> cells) {
        boolean isEmpty = true;
        for (Cell cell : cells) {
            if (cell != null && !cell.getValue().equals("")) {
                isEmpty = false;
                break;
            }
        }
        return isEmpty;
    }

    /**
     * checking each row cell value
     *
     * @param cells
     * @return
     * @author Tohidul Islam
     */
    public static boolean isEmptyRow(Row row) {
        boolean isEmpty = true;
        for (Cell cell : row.getRowCells()) {
            if (cell != null && !cell.getValue().equals("")) {
                isEmpty = false;
                break;
            }
        }
        return isEmpty;
    }
}
