/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.ewc18x.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFPicture;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.OfficeXmlFileException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Drawing;
import org.apache.poi.ss.usermodel.Picture;
import org.apache.poi.ss.usermodel.PictureData;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;

/**
 *
 * @author Tomal
 */
public class EnoviaWebserviceClient {

    private static final Logger logger = Logger.getLogger(EnoviaWebserviceClient.class);

    /**
     * maps the value of input xls to attribute List
     *
     * @param input xls file
     * @param list of not-Updatable properties
     * @return List of objects with attributes
     * @throws IOException if I/O operation fails
     */
    public List< HashMap<String, String>> getMappedValuesFromXls(File file, List<String> notUpdatableProperties) throws CustomException {

        checkXlsValidity(file);

        List<HashMap<String, String>> mappedValues = new ArrayList< HashMap<String, String>>();
        //List<String> modifiableAttrbutes = getModifiableAttributes();
        //List<String> invalidAttributeList = new ArrayList<>();
        String isClassificationPath = "false";
        try {
            FileInputStream inputStream = new FileInputStream(file);
            Workbook workbook = new HSSFWorkbook(inputStream);
            Sheet firstSheet = workbook.getSheetAt(0);
            Iterator<Row> iterator = firstSheet.iterator();
            //skipping xls header template(if any)
            Row nextRow = skipHeader(iterator);

            Iterator<Cell> cellIterator = nextRow.cellIterator();
            List<String> listOfKeys = new ArrayList<>();
            String key;
            //reading xls file for table headers/attributes
            while (cellIterator.hasNext()) {
                Cell cell = cellIterator.next();
                key = cell.getStringCellValue().trim();
                 if(key.equalsIgnoreCase("status") || key.equalsIgnoreCase("message")) {
                    continue;
                }
                if (key.equalsIgnoreCase("state")) {
                    key = "current";
                }
                if(key.equalsIgnoreCase("Classification Path")) {
                    isClassificationPath = "true";
                }
                listOfKeys.add(key);
//                if (key.equalsIgnoreCase("id") || key.equalsIgnoreCase("name") || key.equalsIgnoreCase("type")
//                        || key.equalsIgnoreCase("revision") || key.equalsIgnoreCase("depth")) {
//                    continue;
//                }
                //check if the attribute is modifiable
//                if (!modifiableAttrbutes.contains(key)) {
//                    invalidAttributeList.add(key);
//                }
            }
            //this code is for testing if the xls only contains columns with not-updatable properties
            List<String> temp1 = listOfKeys;
            List<String> temp2 = notUpdatableProperties;
            Collection<String> temp3 = CollectionUtils.subtract(temp1, temp2);
            if(temp3.isEmpty() == true) {
                return mappedValues;
            }
            //----checking not-updatable properties-- end
            //logger.debug("Invalid attributes : " + invalidAttributeList.toString());
            //reading xls file for values
            while (iterator.hasNext()) {
                nextRow = iterator.next();
                cellIterator = nextRow.cellIterator();
                HashMap<String, String> temp = new HashMap<String, String>();
                Cell cell = cellIterator.next();
                for (int j = 0; j < listOfKeys.size(); j++) {
                    if (j == cell.getAddress().getColumn()) {
                        //if (!invalidAttributeList.contains(listOfKeys.get(j))) {
                            cell.setCellType(Cell.CELL_TYPE_STRING);
                            temp.put(listOfKeys.get(j), cell.getStringCellValue().trim());
                        //}
                        if (cellIterator.hasNext()) {
                            cell = cellIterator.next();
                        }
                    } else /*if (!invalidAttributeList.contains(listOfKeys.get(j)))*/ {
                        temp.put(listOfKeys.get(j), "");
                    }
                }
                mappedValues.add(temp);
            }
            workbook.close();
            inputStream.close();

        } catch (IOException e) {
            e.printStackTrace();
            throw new CustomException("Exception in mapping xls File. " + e.getMessage());
        } catch (OfficeXmlFileException e) {
            e.printStackTrace();
            logger.debug("service=util;msg=" + e.getMessage());
            throw new CustomException("Input file contains incompatible data.");
        }
//        if (invalidAttributeList.size() > 0) {
//            HashMap<String, String> temp = new HashMap<String, String>();
//            temp.put("invalidAttr", invalidAttributeList.toString());
//            mappedValues.add(temp);
//        }
        HashMap<String, String> isClassPath = new HashMap<String, String>();
        isClassPath.put("isClassificationPath", isClassificationPath);
        mappedValues.add(isClassPath);
        return mappedValues;
    }

    /**
     * checks if the input xls is valid(similar to export)
     *
     * @param input xls file
     * @throws CustomException if xls is invalid
     */
    private void checkXlsValidity(File file) throws CustomException {
        logger.debug("service=util;msg=in checkXlsValidity method.");
        try {
            FileInputStream imageStream = new FileInputStream(getClass().getResource("/img/ValmetLogo.jpg").getFile());
            byte[] actualPicture = IOUtils.toByteArray(imageStream);
            FileInputStream inputStream = new FileInputStream(file);
            Workbook workbook = new HSSFWorkbook(inputStream);
            Sheet spreadSheet = workbook.getSheetAt(0);
            /*
            check if the valmet logo is present
            * first- get all pictures in xls
            * if xls does not contain any image , throw exception
            * if xls contains an image check if it is valmet's logo(used by export)
            * if not throw exception
             */
            logger.debug("service=util;msg=Checking Logo.");
            List<PictureData> picList = (List<PictureData>) workbook.getAllPictures();

            if (picList.size() <= 0 || picList.size() > 1) {
                throw new CustomException("Invalid Template.");
            }

            byte[] xlsPictureData = picList.get(0).getData();

            boolean isEqual = Arrays.equals(actualPicture, xlsPictureData);
            if (!isEqual) {
                logger.debug("service=util;msg=Logo is not same.");
                throw new CustomException("Invalid Template.");
            } else {
                logger.debug("service=util;msg=Logo Matched.");
            }
            /*
             * Checking valmet logo ends here
             */

            /*
             * check if the valmet export template header is present
             * if not, throw exception
             */
            logger.debug("service=util;msg=Checking Template.");
            HSSFRow dataRow = (HSSFRow) spreadSheet.getRow(0);
            HSSFCell cell;
            String poweredBy = "", applicationName = "", type = "", name = "", revision = "";
            try {
                cell = (HSSFCell) spreadSheet.getRow(0).getCell(2);
                poweredBy = cell.getStringCellValue();
                cell = (HSSFCell) spreadSheet.getRow(1).getCell(2);
                applicationName = cell.getStringCellValue();
                cell = (HSSFCell) spreadSheet.getRow(0).getCell(3);
                type = cell.getStringCellValue();
                cell = (HSSFCell) spreadSheet.getRow(1).getCell(3);
                name = cell.getStringCellValue();
                cell = (HSSFCell) spreadSheet.getRow(2).getCell(3);
                revision = cell.getStringCellValue();
            } catch (Exception e) {
            }
            if (poweredBy.compareTo("Powered By : Valmet") == 0 && applicationName.compareTo("Application Name : VSIX") == 0
                    && type.split("Type : ").length > 1 && name.split("Name : ").length > 1 && revision.split("Revision :").length > 1) {
                cell = (HSSFCell) spreadSheet.getRow(2).getCell(2);
                String date = cell.getStringCellValue();
                String[] dates = date.split("Date :");
                new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").parse(dates[1].trim());
                logger.debug("service=util;msg=Template Matched!");
            } else {
                logger.debug("service=util;msg=Template did not Match!");
                throw new CustomException("Invalid Template.");
            }
            /*
             * Checking valmet template ends here
             */

            /*
             * check if the values start from 11-th row
             * if not, throw exception
             */
            logger.debug("service=util;msg=Checking if the values start from 11-th row.");
            dataRow = (HSSFRow) spreadSheet.getRow(10);
            List<String> headers = new ArrayList<>();
            String checkValue = null;
            try {
                checkValue = dataRow.getCell(0).getStringCellValue();
            } catch (Exception e) {
                logger.debug("service=util;msg=" + e.getMessage());
            }
            if (checkValue == null || checkValue == "") {
                logger.debug("service=util;msg=Could not find data row.");
                throw new CustomException("Invalid Template.");
            }

            Iterator<Cell> cellIterator = dataRow.cellIterator();
            while (cellIterator.hasNext()) {
                cell = (HSSFCell) cellIterator.next();
                headers.add(cell.getStringCellValue());
            }
            //if (headers.contains("depth")) {
                if (headers.contains("id") || (headers.contains("type") || headers.contains("name") || headers.contains("revision"))) {
                    logger.debug("service=util;msg=" + "All necessary headers are present.");
                } else {
                    logger.debug("service=util;msg=" + "Data Row is not in proper position.");
                    throw new CustomException("Invalid XLS.");
                }
            //} else {
            //    throw new CustomException("Invalid XLS.");
            //}

            /*
             * Checking if the values start from 11-th row
             */
        } catch (FileNotFoundException ex) {
            logger.debug("service=util;msg=" + ex.getMessage());
            throw new CustomException("Error reading input file.");
        } catch (ParseException | IllegalStateException ex) {
            logger.debug("service=util;msg=" + ex.getMessage());
            throw new CustomException("Invalid Template.");
        } catch (IOException ex) {
            logger.debug("service=util;msg=" + ex.getMessage());
            throw new CustomException("Error reading input file.");
        }
        logger.debug("service=util;msg=" + "returning from checkXlsValidity method.");
    }
    /**
     * skips the header template (if any) of input xls file
     * @param row iterator
     * @return Row
     */
    public Row skipHeader(Iterator iterator) {
        Row nextRow = (Row) iterator.next();
            while (iterator.hasNext()) {
                Cell cell = nextRow.getCell(0);
                String s = null;
                try {
                    s = cell.getStringCellValue();
                } catch (Exception e) {
                    logger.debug("service=util;msg=" + e.getMessage());
                }
                if (s != null && !s.isEmpty()) {
                    break;
                } else {
                    nextRow = (Row) iterator.next();
                }
            }
            return nextRow;
    }

    /**
     * obtains the lists of attributes from header of input xls file
     *
     * @param cell Iterator
     * @return list (of headers/attribute names)
     */
    public List getHeaders(Iterator<Cell> cellIterator) {
        List<String> listOfKeys = new ArrayList<>();
        String key;
        //reading xls file for table headers/attributes
        while (cellIterator.hasNext()) {
            Cell cell = cellIterator.next();
            key = cell.getStringCellValue();//.replace("_", " ");
            if (key.equalsIgnoreCase("state")) {
                key = "current";
            }

            listOfKeys.add(key);

        }
        return listOfKeys;
    }

    /**
     * copy row from one sheet to another sheet within the same workbook
     *
     * @param HSSFWorkbook
     * @return sourceRow index
     */
    public void copyRow(HSSFWorkbook workbook, int sourceRowNum) {
        logger.debug("service=util;msg=in copyRow method");
        // retriving/creating row
        HSSFSheet worksheet = workbook.getSheetAt(0);
        HSSFSheet templateSheet = workbook.getSheet("templateSheet");
        HSSFRow newRow = templateSheet.createRow(sourceRowNum);
        HSSFRow sourceRow = worksheet.getRow(sourceRowNum);
        if(sourceRow == null) {
            return;
        }

        // If the row exist in destination, push down all rows by 1 else create a new row
        if (newRow != null) {
            templateSheet.shiftRows(sourceRowNum, worksheet.getLastRowNum(), 1);
        } else {
            newRow = templateSheet.createRow(sourceRowNum);
        }

        // Loop through source columns to add to new row
        for (int i = 0; i < sourceRow.getLastCellNum(); i++) {
            // Grab a copy of the old/new cell
            HSSFCell oldCell = sourceRow.getCell(i);
            HSSFCell newCell = newRow.createCell(i);
            templateSheet.setColumnWidth(i, worksheet.getColumnWidth(i));
            // If the old cell is null jump to next cell
            if (oldCell == null) {
                newCell = null;
                continue;
            }

            // Copy style from old cell and apply to new cell
            HSSFCellStyle newCellStyle = workbook.createCellStyle();
            newCellStyle.cloneStyleFrom(oldCell.getCellStyle());
            ;
            newCell.setCellStyle(newCellStyle);

            // If there is a cell comment, copy
            if (oldCell.getCellComment() != null) {
                newCell.setCellComment(oldCell.getCellComment());
            }

            // If there is a cell hyperlink, copy
            if (oldCell.getHyperlink() != null) {
                newCell.setHyperlink(oldCell.getHyperlink());
            }

            // Set the cell data type
            newCell.setCellType(oldCell.getCellType());

            // Set the cell data value
            switch (oldCell.getCellType()) {
                case Cell.CELL_TYPE_BLANK:
                    newCell.setCellValue(oldCell.getStringCellValue());
                    break;
                case Cell.CELL_TYPE_BOOLEAN:
                    newCell.setCellValue(oldCell.getBooleanCellValue());
                    break;
                case Cell.CELL_TYPE_ERROR:
                    newCell.setCellErrorValue(oldCell.getErrorCellValue());
                    break;
                case Cell.CELL_TYPE_FORMULA:
                    newCell.setCellFormula(oldCell.getCellFormula());
                    break;
                case Cell.CELL_TYPE_NUMERIC:
                    newCell.setCellValue(oldCell.getNumericCellValue());
                    break;
                case Cell.CELL_TYPE_STRING:
                    newCell.setCellValue(oldCell.getRichStringCellValue());
                    break;
            }
        }

        // If there are are any merged regions in the source row, copy to new row
        for (int i = 0; i < worksheet.getNumMergedRegions(); i++) {
            CellRangeAddress cellRangeAddress = worksheet.getMergedRegion(i);
            if (cellRangeAddress.getFirstRow() == sourceRow.getRowNum()) {
                CellRangeAddress newCellRangeAddress = new CellRangeAddress(newRow.getRowNum(),
                        (newRow.getRowNum() +
                                (cellRangeAddress.getLastRow() - cellRangeAddress.getFirstRow()
                                        )),
                        cellRangeAddress.getFirstColumn(),
                        cellRangeAddress.getLastColumn());
                worksheet.addMergedRegion(newCellRangeAddress);
            }
        }
    }

//    public List getModifiableAttributes() throws FileNotFoundException {
//
//        File attributeFile = new File(getClass().getResource("/ExpandObjectUpdateAttributes.txt").getFile());
//        Scanner input = new Scanner(attributeFile);
//        List<String> modifiableAttrbutes = new ArrayList<String>();
//        while (input.hasNextLine()) {
//            String line = input.nextLine();
//            modifiableAttrbutes.add(line);
//        }
//        input.close();
//        logger.info("Availabe Attributes to update size : " + modifiableAttrbutes.size() + " \n values \n" + modifiableAttrbutes.toString());
//
//        return modifiableAttrbutes;
//    }

    /**
     * reads Extension attribute from file and adds necessary configurations to
     * the lists
     *
     * @return
     * @throws com.bjit.ewc.utils.CustomException
     * @throws ArrayIndexOutOfBoundsException if configuration file does not
     * follow the configuration format
     */
    public List<String> getAllowedTypesForImport() throws CustomException {
        logger.debug("service=util;msg=" + "In getAllowedTypesForImport() method.");
        try {
            List<String> allowedTypes = new ArrayList();
            logger.debug("service=util;msg=" + "Reading File for type list.");
            File file = new File(getClass().getResource("/update_object_types.txt").getFile());
            Scanner input = new Scanner(file);
            while (input.hasNextLine()) {
                String line = input.nextLine();
                if (line.charAt(0) == '#') {
                    continue;
                }
                allowedTypes.add(line);
            }
            input.close();
            return allowedTypes;
        } catch (FileNotFoundException ex) {
            logger.debug("service=util;msg=" + ex.getMessage());
            throw new CustomException("Type configuration is not available.");
        } catch (ArrayIndexOutOfBoundsException ex) {
            logger.debug("service=util;msg=" + ex.getMessage());
            throw new CustomException("Could not read Type configuration.");
        }
    }
}
