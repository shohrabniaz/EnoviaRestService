/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.app.service.controller.export.himelli;

import com.bjit.common.rest.app.service.utilities.NullOrEmptyChecker;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

/**
 *
 * @author Tohidul-571
 */
public class HimelliTest {

    public static void main(String[] args) {
        try {
            JSONParser parser = new JSONParser();
            Object obj = parser.parse(new FileReader("E:\\Task\\2021\\8_August\\26_item_common_text_supporting\\DemoJSONItemCommonTextFixing.json"));
            JSONObject jsonObject = (JSONObject) obj;
            //System.out.println(jsonObject.toJSONString());
            System.out.println("*******************++++++++++++++++++++");
            MultiLevelBOMStructureUtils multiLevelBOMStructureUtilsObj = new MultiLevelBOMStructureUtils(jsonObject, true);
            JSONObject json = multiLevelBOMStructureUtilsObj.modifyDataForHimeliExport();

            HimelliReportProcessor himelliReportProcessing = new HimelliReportProcessor(json);
            try {
                byte[] himelliReport = himelliReportProcessing.himelliDataProcessing();
            } catch (Exception ex) {
                Logger.getLogger(HimelliTest.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (Exception ex) {
            Logger.getLogger(HimelliTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void mainB(String[] args) {
        try {
            JSONParser parser = new JSONParser();
            Object obj = parser.parse(new FileReader("E:\\Task\\2021\\8_August\\26_item_common_text_supporting\\DemoJSONItemCommonTextFixing.json"));
            JSONObject jsonObject = (JSONObject) obj;
            //System.out.println(jsonObject.toJSONString());
            System.out.println("*******************++++++++++++++++++++");
            MultiLevelBOMStructureUtils multiLevelBOMStructureUtilsObj = new MultiLevelBOMStructureUtils(jsonObject, true);
            JSONObject json = multiLevelBOMStructureUtilsObj.modifyDataForHimeliExport();

        } catch (Exception ex) {
            Logger.getLogger(HimelliTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
