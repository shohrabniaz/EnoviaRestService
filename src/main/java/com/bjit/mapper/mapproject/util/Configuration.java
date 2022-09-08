/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.mapper.mapproject.util;

import com.bjit.ewc18x.utils.PropertyReader;

/**
 *
 * @author BJIT
 */
public class Configuration {
    public static String MAPPING_XML_FILE_PATH = "/mapper_files/attribute_and_property_mapper.xml";
    public static String MAPPING_XML_FILE_DIRECTORY = PropertyReader.getProperty("xml.att.mapping.file.dir");
}
