/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.bjit.common.rest.app.service.model.itemHistoryLine;

import com.bjit.common.rest.app.service.model.tnr.TNR;
import java.util.List;

/**
 *
 * @author BJIT
 */

public class ItemHistoryBean {

    private String itemId;
    private String descId;
    private TNR tnr;
    private List<HistoryLineDetatils> historyLines;
    private String error;

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getDescId() {
        return descId;
    }

    public void setDescId(String descId) {
        this.descId = descId;
    }

    public TNR getTnr() {
        return tnr;
    }

    public void setTnr(TNR tnr) {
        this.tnr = tnr;
    }

    public List<HistoryLineDetatils> getHistoryLines() {
        return historyLines;
    }

    public void setHistoryLines(List<HistoryLineDetatils> historyLines) {
        this.historyLines = historyLines;
    }

    @Override
    public String toString() {
        return "ItemHistoryBean{" + "itemId=" + itemId + ", descId=" + descId + ", tnr=" + tnr + ", historyLines=" + historyLines + ", error=" + error + '}';
    }

}
