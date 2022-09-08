package com.bjit.common.rest.pdm_enovia.result.xls;

import com.bjit.common.rest.app.service.utilities.NullOrEmptyChecker;
import com.bjit.common.rest.pdm_enovia.utility.CommonUtil;
import com.bjit.ewc18x.utils.PropertyReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.log4j.Logger;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 *
 * @author Mashuk
 */
public class XLSFileUtil {
    
    private static final Logger XLS_UTIL_LOGGER = Logger.getLogger(XLSFileUtil.class);
    
    private String currentFileName;
    private Map<String, List<String>> writableItemListMap;
    private List<String> writableValItemList;
    
    public XLSFileUtil(Map<String, List<String>> writableItemListMap) {
        this.writableItemListMap = writableItemListMap;
    }
    
    public List<String> readFileAndFetchItemList() throws Exception {
        List<String> fetchedItemListFromXLS = new ArrayList<>();
        FileInputStream inputStream = null;
        XSSFWorkbook workbook = null;
        try {
            inputStream = new FileInputStream(getXLSFile());
            workbook = new XSSFWorkbook(inputStream);
            String sheetName = PropertyReader.getProperty("xls.sheet.name");
            if(NullOrEmptyChecker.isNullOrEmpty(sheetName)) {
                throw new Exception("Error getting xls.sheet.name value from properties");
            }
            XSSFSheet spreadsheet = workbook.getSheet(sheetName);
            Iterator<Row> rowIterator = spreadsheet.iterator();
            ignoreXLSSheetColumnHeader(rowIterator);
            rowIterator.forEachRemaining((row) -> {
                Iterator<Cell> cellIterator = row.cellIterator();
                if(cellIterator.hasNext()) {
                    Cell cell = cellIterator.next();
                    String fetchedItemName = cell.getStringCellValue();
                    fetchedItemListFromXLS.add(fetchedItemName);
                }
            });
        } catch (Exception e) {
            XLS_UTIL_LOGGER.error("Error occured while fetching VAL Item list from XLS file: " + e.getMessage());
        } finally {
            try {
                if(!NullOrEmptyChecker.isNull(workbook)) {
                    workbook.close();
                }
            } catch (IOException e) {
                XLS_UTIL_LOGGER.error("Error occured while closing XLS file content retrive operation: " + e.getMessage());
            }
            try {
                if(!NullOrEmptyChecker.isNull(inputStream)) {
                    inputStream.close();
                }
            } catch (IOException e) {
                XLS_UTIL_LOGGER.error("Error occured while closing XLS file stream retrive operation: " + e.getMessage());
            }
        }
        return fetchedItemListFromXLS;
    }
    
    public void ignoreXLSSheetColumnHeader(Iterator<Row> rowIterator) {
        if(rowIterator.hasNext()) {
            rowIterator.next();
        }
    }
    
    public List<String> removeAlreadyAddedItemsFromList(List<String> addableItemList) {
        List<String> filteredAddableItemlist = new ArrayList<>();
        try {
            if(!NullOrEmptyChecker.isNullOrEmpty(addableItemList)) {
                List<String> alreadyAddedItemList = readFileAndFetchItemList();
                if(!NullOrEmptyChecker.isNullOrEmpty(alreadyAddedItemList)) {
                    filteredAddableItemlist = addableItemList.stream()
                        .filter(itemName -> !alreadyAddedItemList.contains(itemName))
                        .collect(Collectors.toList());
                } else {
                    filteredAddableItemlist = addableItemList;
                }
            }
        } catch (Exception e) {
            XLS_UTIL_LOGGER.error("Error occured while filtering already added VAL Item list: " + e.getMessage());
        }
        return filteredAddableItemlist;
    }

    public void writeFile() throws IOException, InvalidFormatException, Exception {
        prepareWritableVALItemList();
        if(!NullOrEmptyChecker.isNullOrEmpty(writableValItemList)) {
            FileOutputStream outputStream = null;
            XSSFWorkbook workbook = null;
            try {
                File file = getXLSFile();
                workbook = buildWorkbook(writableValItemList, file);
                file.setWritable(Boolean.TRUE);
                outputStream = new FileOutputStream(file);
                file.setReadOnly();
                workbook.write(outputStream);
                outputStream.close();
                workbook.close();
            } catch (Exception e) {
                XLS_UTIL_LOGGER.error("Error occured while Writing XLS File: " + e.getMessage());
            } finally {
                try {
                    if(!NullOrEmptyChecker.isNull(outputStream)) {
                        outputStream.close();
                    }
                } catch (IOException e) {
                    XLS_UTIL_LOGGER.error("Error occured while closing XLS file stream writing operation: " + e.getMessage());
                }
                try {
                    if(!NullOrEmptyChecker.isNull(workbook)) {
                        workbook.close();
                    }
                } catch (IOException e) {
                    XLS_UTIL_LOGGER.error("Error occured while closing XLS file content writing operation: " + e.getMessage());
                }
            }
        }
    }
    
    private XSSFWorkbook buildWorkbook(List<String> data, File file) throws IOException, InvalidFormatException, Exception {
        
        XSSFWorkbook workbook;
        if(!file.exists() && !file.isDirectory()) {
            workbook = new XSSFWorkbook();
        }
        else {
            try(InputStream is = new FileInputStream(file)) {
                workbook = new XSSFWorkbook(is);
            }
        }
        String sheetName = PropertyReader.getProperty("xls.sheet.name");
        if(NullOrEmptyChecker.isNullOrEmpty(sheetName)) {
            throw new Exception("Error getting xls.sheet.name value from properties");
        }
        XSSFSheet spreadsheet = workbook.getSheet(sheetName);
        if (workbook.getNumberOfSheets() != 0) {
            for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
               if (workbook.getSheetName(i).equals(sheetName)) {
                    spreadsheet = workbook.getSheet(sheetName);
                } else spreadsheet = workbook.createSheet(sheetName);
            }
        }
        else {
            spreadsheet = workbook.createSheet(sheetName);
        }
        
        spreadsheet.setColumnWidth((short) 0, (short) ((70* 8) / ((double) 1 / 20)));
        int lastDataRow = spreadsheet.getPhysicalNumberOfRows();
        
        if(lastDataRow < 1) {
            createRowHeader(workbook, sheetName);
        }
        
        lastDataRow = spreadsheet.getPhysicalNumberOfRows();
        int newDataRowPoint = lastDataRow++;
        int dataCellNum = 0;
        
        for(String valItemName : data) {
            Row dataRow = spreadsheet.createRow(newDataRowPoint);
            Cell dataCell = dataRow.createCell(dataCellNum);
            dataCell.setCellValue(valItemName);
            newDataRowPoint++;
        }
        return workbook;
    }
    
    private File getXLSFile() throws Exception {
        String fileNamePrefix = PropertyReader.getProperty("xls.file.prefix");
        if(NullOrEmptyChecker.isNullOrEmpty(fileNamePrefix)) {
            throw new Exception("Error getting xls.file.prefix value from properties");
        }
        String fileType = PropertyReader.getProperty("xls.file.type");
        if(NullOrEmptyChecker.isNullOrEmpty(fileNamePrefix)) {
            throw new Exception("Error getting xls.file.type value from properties");
        }
        String fileName = fileNamePrefix + CommonUtil.getCurrentSystemDate() + "." + fileType;
        currentFileName = fileName;
        
        String fileLocation = PropertyReader.getProperty("xls.file.location");
        if(NullOrEmptyChecker.isNullOrEmpty(fileLocation)) {
            throw new Exception("Error getting xls.file.location value from properties");
        }
        
        File file = new File(fileLocation + fileName);
        checkXLSLocation();
        moveOlderXLSFiles();
        return file;
    }
    
    private void checkXLSLocation() throws Exception {
        String fileLocation = PropertyReader.getProperty("xls.file.location");
        if(NullOrEmptyChecker.isNullOrEmpty(fileLocation)) {
            throw new Exception("Error getting xls.file.location value from properties");
        }
        CommonUtil.createDirectory(fileLocation);
    }
    
    private void moveOlderXLSFiles() throws Exception {
        String fileLocation = PropertyReader.getProperty("xls.file.location");
        if(NullOrEmptyChecker.isNullOrEmpty(fileLocation)) {
            throw new Exception("Error getting xls.file.location value from properties");
        }
        File directoryName = new File(fileLocation);
        
        String fileType = PropertyReader.getProperty("xls.file.type");
        if(NullOrEmptyChecker.isNullOrEmpty(fileLocation)) {
            throw new Exception("Error getting xls.file.type value from properties");
        }
        
        FileFilter fileFilter = CommonUtil.getFileFilter(directoryName, fileType);
        File[] listOfFiles = directoryName.listFiles(fileFilter);
        int fileMovedCounter = 0;
        if(listOfFiles.length > 0) {
            for(int fileIterator=0; fileIterator<listOfFiles.length; fileIterator++) {
                String fileName = listOfFiles[fileIterator].getName();
                if(!fileName.equals(currentFileName)) {
                    moveFileToHistory(listOfFiles[fileIterator]);
                    fileMovedCounter++;
                }
            }
            XLS_UTIL_LOGGER.debug("Number of old xls moved is " + fileMovedCounter);
        }
    }
    
    private void moveFileToHistory(File file) throws Exception {
        String fileHistory = PropertyReader.getProperty("xls.file.history");
        if(NullOrEmptyChecker.isNullOrEmpty(fileHistory)) {
            throw new Exception("Error getting xls.file.history value from properties");
        }
        
        CommonUtil.moveFile(file, fileHistory);
    }
    
    private void createRowHeader(XSSFWorkbook workbook, String sheetName) throws Exception {
        Font font = workbook.createFont();
        font.setFontHeightInPoints((short) 11);
        font.setColor((short) Font.COLOR_NORMAL);
        font.setBold(Boolean.TRUE);
        XSSFCellStyle cellStyle = (XSSFCellStyle) workbook.createCellStyle();
        cellStyle.setFont(font);
        cellStyle.setAlignment(HorizontalAlignment.CENTER);
        
        int rowNum = 0;
        int cellNum = 0;
        XSSFSheet spreadsheet = workbook.getSheet(sheetName);
        Row hRow = spreadsheet.createRow(rowNum);
        Cell hCell = hRow.createCell(cellNum);
        String headerName = PropertyReader.getProperty("xls.sheet.header.name");
        if(NullOrEmptyChecker.isNullOrEmpty(headerName)) {
            throw new Exception("Error getting xls.sheet.header.name value from properties");
        }
        
        hCell.setCellValue(headerName);
        hCell.setCellStyle(cellStyle);
    }
    
    private void prepareWritableVALItemList() {
        writableValItemList = new ArrayList<>();
        if(!NullOrEmptyChecker.isNullOrEmpty(writableItemListMap.get("created"))) {
            writableValItemList.addAll(writableItemListMap.get("created"));
        }
        List<String> addableUpdateItemList = removeAlreadyAddedItemsFromList(writableItemListMap.get("updated"));
        if(!NullOrEmptyChecker.isNullOrEmpty(addableUpdateItemList)) {
            writableValItemList.addAll(addableUpdateItemList);
        }
    }
}
