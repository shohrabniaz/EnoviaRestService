package com.bjit.common.rest.app.service.export.product;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import com.bjit.ewc18x.utils.CustomException;
import java.util.List;
import java.util.Map;
import matrix.db.Context;

/**
 *
 * @author Suvonkar Kundu
 */
public interface ProductExportService {

    public String getJsonOutput(String type, int limit, String startDate, String endDate, Context context) throws CustomException;

    public String getJsonOutput(String type, int limit, String startDate, String endDate, Context context, List<String> attributeList, List<String> propertityList, List<String> requestAttributeList) throws CustomException;

    public String getJsonOutput(String type, String name, String revision, String startDate, String endDate, Context context) throws CustomException;

    public String getJsonOutput(String type, String name, String revision, String startDate, String endDate, Context context, List<String> attributeList, List<String> propertityList, List<String> requestAttributeList) throws CustomException;

    public boolean hasValueInMap(Map<String, String> allItemMap, String listItem);

}
