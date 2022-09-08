/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.bjit.common.rest.app.service.itemHistoryLines;

import com.bjit.common.rest.app.service.model.itemHistoryLine.ItemHistoryBean;

/**
 *
 * @author Fazley Rabbi-11372
 * Date: 27-06-2022
 */
public interface HistoryLinesService {

    public ItemHistoryBean getItemHistoryLines(String objId) throws Exception;

}
