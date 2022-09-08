/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.app.service.controller.export;

/**
 *
 * @author BJIT
 */
public class ReportUtilities {

    private static final org.apache.log4j.Logger REPORT_UTILITIES_CONTROLLER = org.apache.log4j.Logger.getLogger(ReportUtilities.class);

    public static void enableGraphicsSupport() {
        try {

            String GRAPHICS_PROPERTY = "java.awt.headless";
            String GRAPHICS_SUPPORT_ENABLED = "true";

            String isHeadless = System.getProperty(GRAPHICS_PROPERTY);
            REPORT_UTILITIES_CONTROLLER.debug("Graphics property is headless : " + isHeadless);
            if (!isHeadless.equalsIgnoreCase(GRAPHICS_SUPPORT_ENABLED)) {
                System.setProperty(GRAPHICS_PROPERTY, GRAPHICS_SUPPORT_ENABLED);
            }
        } catch (Exception exp) {
            REPORT_UTILITIES_CONTROLLER.error(exp);
            throw exp;
        }
    }
}
