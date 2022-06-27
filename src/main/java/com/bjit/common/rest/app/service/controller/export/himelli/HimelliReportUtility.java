package com.bjit.common.rest.app.service.controller.export.himelli;

import com.bjit.common.rest.app.service.utilities.NullOrEmptyChecker;
import java.io.File;
import java.io.FileNotFoundException;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.util.ResourceUtils;

import com.bjit.ewc18x.utils.PropertyReader;
import java.util.Set;

public class HimelliReportUtility {

    private static final String TITLE = "Title";
    private static final String TYPE = "Type";
    private static final String VCOL = "vCOL RowTag";
    private static final String TECHNICALDESIGNATION ="Technical Designation";
    private static final String LEVEL = "Level";
    private static final String UNIT_BOM = "UnitBom";
    private static final String UNIT_SALES = "UnitSales";
    private static final String OTHER_TYPE_CODE = "T";
    private static final String SEQUENCE_ROW = "SeqRow";
    private static final String HIMELLI_MAPPER_FILE = "himelli.mapper.file";
    private static final String NO_PROCESS = "NA";
    private static Map<String, String> typeMap;
    private int seqRow = 1;
    private static final String DATE_FORMAT = "yyyy-MM-dd_HH-mm-ss";
    private static HashMap<String, Attributes> attributes;
    private OptionalRowGenarator optionalRowGenarator;
    private CellMasters cellMasters;

    static {
        attributes = new HashMap<>();
        try {
            attributes.put(HIMELLI_MAPPER_FILE,
                    HimelliReportUtility.getHimelliFields(PropertyReader.getProperty(HIMELLI_MAPPER_FILE)));
        } catch (FileNotFoundException | URISyntaxException | JAXBException e) {
            e.printStackTrace();
            HimelliLogger.getInstance().printLog(e.getMessage(), LogType.ERROR);
            throw new RuntimeException(e.getMessage());
        }
    }

    private int getSeqRow() {
        return this.seqRow++;
    }

    @PostConstruct
    private Map<String, String> getTypeMap() {
        HimelliLogger.getInstance().printLog("Creating type mapping for himelli.", LogType.INFO);
        typeMap = new HashMap<>();
        typeMap.put("CreateAssembly", "a");
        typeMap.put("CreateMaterial", "a");
        typeMap.put("Provide", "p");
        typeMap.put("ProcessContinuousProvide", "p");
        HimelliLogger.getInstance().printLog("Finished type mapping for himelli.", LogType.INFO);
        return typeMap;
    }

    /**
     * this method builds the himelli report bodys, table header and table body
     *
     * @param json
     * @param notIncludedAttributes
     * @return
     * @throws URISyntaxException
     * @throws JAXBException
     * @throws FileNotFoundException
     */
    public String prepareReportTable(Object json, Set<Attribute> notIncludedAttributes) throws URISyntaxException, JAXBException, FileNotFoundException {
        HimelliLogger.getInstance().printLog("notIncludedAttributes: " + notIncludedAttributes.toString(), LogType.INFO);
        Attributes himelliFields = this.filterAttributes(attributes.get(HIMELLI_MAPPER_FILE), notIncludedAttributes);
        this.optionalRowGenarator = new OptionalRowGenarator(himelliFields);
        this.cellMasters = new CellMasters(himelliFields);
        this.optionalRowGenarator.setCellMasters(this.cellMasters);
        List<String> headers = this.prepareReportHeaders(himelliFields);
        List<List<String>> rowListForTable = this.prepareRowData(himelliFields, json);
        TableGenerator tableGenerator = new TableGenerator();
        String himelliReportBody = tableGenerator.generateTable(headers, rowListForTable);
        return himelliReportBody;
    }

    public static Attributes getHimelliFields(String mapperFile) throws URISyntaxException, JAXBException, FileNotFoundException {
        HimelliLogger.getInstance().printLog("Going to read himelli attribute mapping file.", LogType.INFO);
        File file = ResourceUtils.getFile("classpath:" + mapperFile);
        JAXBContext jaxbContext = JAXBContext.newInstance(Attributes.class);
        Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
        Attributes himelliFields = (Attributes) jaxbUnmarshaller.unmarshal(file);
        return himelliFields;
    }

    private List<List<String>> prepareRowData(Attributes himelliFields, Object json) {
        JSONObject jsonObject = (JSONObject) json;
        JSONObject dataObject = (JSONObject) jsonObject.get("data");
        JSONArray results = (JSONArray) dataObject.get("results");
        List<Row> dataList = new ArrayList<>();
        List<Row> rowsList = readAllChildren(results, himelliFields, dataList, getTypeMap());
        List<List<String>> rowListForTable = new ArrayList<>();
        rowsList.stream().filter(row -> row.getRowCells().size() > 0).forEach(row -> {
            List<String> rows = new LinkedList<>();
            row.getRowCells().stream().forEach(cell -> {
                rows.add(cell.getValue());
            });
            rowListForTable.add(rows);
        });
        return rowListForTable;
    }

    private List<String> prepareReportHeaders(Attributes himelliFields) {
        List<String> headers = new LinkedList<>();
        for (Attribute attr : himelliFields.getAttributes()) {
            headers.add(attr.getSource());
        }
        return headers;
    }

    private List<Row> readAllChildren(JSONArray childArray, Attributes himelliFields, List<Row> rowsList, Map<String, String> typeMap) {
        prepareRowList(childArray, himelliFields, rowsList, typeMap);
        return rowsList;
    }

    private void prepareRowList(JSONArray childArray, Attributes himelliFields, List<Row> rowsList, Map<String, String> typeMap) {
        for (int i = 0; i < childArray.size(); i++) {
            List<Cell> row = new ArrayList<>();
            JSONObject rowObject = (JSONObject) childArray.get(i);
            String enoviaTypeField = (String) rowObject.get(TYPE);
            String mappedTypeValue = typeMap.get(enoviaTypeField);
            if (mappedTypeValue == null) {
                mappedTypeValue = OTHER_TYPE_CODE;
            }
            this.populateRow(himelliFields, row, rowObject, mappedTypeValue);
            rowsList.add(new Row(row));
            /*
            //new row for current row, adding regarding Item_Common_text or others
            List<Cell> optionalRow = this.getOptionalRows(himelliFields, rowObject);
            rowsList.add(new Row(optionalRow));
             */
            List<Row> optionalRows = this.getOptionalRows(himelliFields, rowObject);
            rowsList.addAll(optionalRows);

            JSONArray bomLines = (JSONArray) rowObject.get("bomLines");
            if (bomLines == null) {
                continue;
            }
            if (bomLines.size() > 0) {
                readAllChildren(bomLines, himelliFields, rowsList, typeMap);
            }
        }
    }

    /**
     * this method responsible for prepare each data row of hemilli report
     *
     * @param himelliFields - attribute mapper
     * @param level - current level of object
     * @param row -
     * @param rowObject - bom json object
     * @param mappedTypeValue - enovia object type mapping
     * @author Tohidul Islam
     */
    private void populateRow(Attributes himelliFields, List<Cell> row, JSONObject rowObject, String mappedTypeValue) {
        for (Attribute attr : himelliFields.getAttributes()) {
            String himelliHeader = attr.getSource();
            String bomObjKey = attr.getDest();
            Cell cell = this.cellMasters.getCell(himelliHeader);
            String cellContent = "";

            // conditional duplication is intentional
            if (!bomObjKey.equals(NO_PROCESS)) {
                if (himelliHeader.equals(VCOL)) {
                    cellContent = this.getVColRowTag();
                } else if (himelliHeader.equals(LEVEL)) {
                    cellContent = (String) rowObject.get(LEVEL);
                } else if (himelliHeader.equals("Pos")) {
                    cellContent = (String) rowObject.get(bomObjKey);
                } else if (himelliHeader.equals("AssyDsc1")) {
                    cellContent = this.getLanguageTitle((String) rowObject.get("parentTitle"), true);
                } else if (himelliHeader.equals("AssyDsc2")) {
                    cellContent = this.getLanguageTitle((String) rowObject.get("parentTitle"), false);
                } else if (himelliHeader.endsWith("Dwg")) {
                    cellContent = (String) rowObject.get(bomObjKey);
                } else if (himelliHeader.equals("AssyID")) {
                    cellContent = (String) rowObject.get("parentName");
                } else if (himelliHeader.equals("ItemDsc1")) {
                    String compositeValue = (String) rowObject.get(bomObjKey);
                    String Title = this.getLanguageTitle(compositeValue, true);
                    cellContent = this.mergeTechnicalDesignationWithTitle(Title, rowObject);
                } else if (himelliHeader.equals("ItemDsc2")) {
                    String compositeValue = (String) rowObject.get(bomObjKey);
                    cellContent = this.getLanguageTitle(compositeValue, false);
                } else if (himelliHeader.equals("Material")) {
                    cellContent = (String) rowObject.get(bomObjKey);
                } else if (himelliHeader.equals("Size")) {
                    cellContent = (String) rowObject.get(bomObjKey);
                } else if (himelliHeader.equals("Standard")) {
                    cellContent = (String) rowObject.get(bomObjKey);
                } else if (himelliHeader.equals("ItemId")) {
                    cellContent = (String) rowObject.get(bomObjKey);
                } else if (himelliHeader.equals("QtyLine")) {
                    cellContent = (String) rowObject.get(bomObjKey);
                } else if (himelliHeader.equals("QtyRec")) {
                    cellContent = "0";
                } else if (himelliHeader.equals(UNIT_BOM) || himelliHeader.equals(UNIT_SALES)) {
                    String compositeValue = (String) rowObject.get(bomObjKey);
                    if (compositeValue != null && !compositeValue.equals("")) {
                        cellContent = PropertyReader.getProperty(compositeValue);
                        //HimelliLogger.getInstance().printLog("Enovia unit: " + compositeValue + "; himelli unit value: " + cellContent, LogType.INFO);
                    }
                } else if (himelliHeader.equals(SEQUENCE_ROW)) {
                    int seqNum = this.getSeqRow();
                    cellContent = String.valueOf(seqNum);
                }
                if (cellContent == null) {
                    cellContent = "";
                }
                //HimelliLogger.getInstance().printLog("Header: " + himelliHeader + "; cellContent value: " + cellContent, LogType.INFO);
            }
            cell.setValue(cellContent);
            row.add(cell);
        }
    }
    
    //Merge Title with Technical Designation
    public String mergeTechnicalDesignationWithTitle(String title, JSONObject rowObject) {
        String combinedTitle = "";
        String technicalDesignation = (String) rowObject.get(TECHNICALDESIGNATION);
        
        if(!NullOrEmptyChecker.isNullOrEmpty(technicalDesignation)){
            combinedTitle = title + " " + technicalDesignation;
        } else {
            combinedTitle = title;
        }
        
        return combinedTitle;
    }

    /**
     *
     * @param himelliFields
     * @param rowObject
     * @return
     * @author Tohidul Islam
     */
    private List<Row> getOptionalRows(Attributes himelliFields, JSONObject rowObject) {
        List<Row> rows = this.optionalRowGenarator.getRows(rowObject);
        for (Row row : rows) {
            //checking whether row has any value in any cell or not
            if (!Row.isEmptyRow(row)) {
                Cell seqRowCell = this.cellMasters.getCell(CellMasters.SEQUENCE_ROW);
                if (seqRowCell != null) {
                    seqRowCell = CellMasters.updateContent(seqRowCell, this.getSeqRow());
                    row = this.cellMasters.mergeCell(row, seqRowCell);
                }
            }
        }

        return rows;
    }

    public String getLanguageTitle(String str, boolean isPrimary) {
        String title = "";
        if (!str.isEmpty()) {
            String[] langs = str.split("\n");
            if (isPrimary) {
                return langs[0].split(":")[1].trim();
            } else if (langs.length > 1) {
                return langs[1].split(":")[1].trim();
            }
        }
        return title;
    }

    public String generateHimelliReportName(String name, String rev) {
        StringBuilder fileNameBuilder = new StringBuilder();
        fileNameBuilder.append(name).append("_").append(rev).append("_").append(formattedDateGenerate()).append("_")
                .append("enovia").append(".txt");
        HimelliLogger.getInstance().printLog("#### himelli report name: " + fileNameBuilder.toString() + " ####",
                LogType.INFO);
        return fileNameBuilder.toString();
    }

    private String formattedDateGenerate() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern(DATE_FORMAT);
        LocalDateTime now = LocalDateTime.now();
        return dtf.format(now);
    }

    public static HashMap<String, Attributes> getAttributes() {
        return attributes;
    }

    /**
     * this method will remove attributes from himelli mapping attributes
     *
     * @param allHimelliFields - contains source-dest information of himelli
     * mapping
     * @param notIncludedAttributes - specified elements will be removed from
     * himelli mapping
     * @return filteredHimelliFields
     */
    private Attributes filterAttributes(Attributes allHimelliFields, Set<Attribute> notIncludedAttributes) {
        // copying to new list to avoid reference issue
        List<Attribute> allAttrs = new LinkedList<>();
        allAttrs.addAll(allHimelliFields.getAttributes());

        // removing attributes
        for (Attribute a : notIncludedAttributes) {
            allAttrs.removeIf(attr -> attr.getSource().equals(a.getSource()) && attr.getDest().equals(a.getDest()));
        }
        Attributes filteredHimelliFields = new Attributes();
        filteredHimelliFields.setAttributes(allAttrs);
        return filteredHimelliFields;
    }

    /**
     *
     * @return Row Tag
     * @author Tohidul Islam
     */
    private String getVColRowTag() {
        if (this.seqRow == 1) {
            return "a";
        }
        return "p";
    }

    private void If(boolean b) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
