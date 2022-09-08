package com.bjit.common.rest.app.service.controller.export.himelli;

import com.bjit.common.code.utility.mail.builders.MailModelBuilder;
import com.bjit.common.code.utility.mail.constants.MailContentType;
import com.bjit.common.code.utility.mail.impls.Mail;
import com.bjit.common.code.utility.mail.models.MailModel;
import com.bjit.common.code.utility.mail.services.IMail;
import com.bjit.common.code.utility.mail.services.IMailModelBuilder;
import com.bjit.common.rest.app.service.model.himelli.HimelliModel;
import com.bjit.common.rest.app.service.utilities.NullOrEmptyChecker;
import com.bjit.ewc18x.utils.PropertyReader;
import com.bjit.mapper.mapproject.expand.ObjectTypesAndRelations;
import com.matrixone.apps.domain.util.FrameworkException;
import com.matrixone.apps.domain.util.MqlUtil;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;

import javax.mail.MessagingException;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.StringJoiner;

@Component
public class HimelliReportUtility {

    private static final org.apache.log4j.Logger LOGGER = org.apache.log4j.Logger.getLogger(HimelliReportUtility.class);

    private static final String TITLE = "Title";
    private static final String TYPE = "Type";
    private static final String VCOL = "vCOL RowTag";
    private static final String TECHNICALDESIGNATION = "Technical Designation";
    private static final String LEVEL = "Level";
    private static final String UNIT_BOM = "UnitBom";
    private static final String UNIT_SALES = "UnitSales";
    private static final String OTHER_TYPE_CODE = "T";
    private static final String SEQUENCE_ROW = "SeqRow";
    private static final String HIMELLI_MAPPER_FILE = "himelli.mapper.file";
    private static final String NO_PROCESS = "NA";
    private static final String DATE_FORMAT = "yyyy-MM-dd_HH-mm-ss";
    private static Map<String, String> typeMap;
    private static HashMap<String, Attributes> attributes;
    private static HashMap<String, String> DIRECTORY_MAP;
    @Value("${himelli.mail.subject}")
    String mailSubject;
    private int seqRow = 1;
    private OptionalRowGenarator optionalRowGenarator;
    private CellMasters cellMasters;
    @Value("${himelli.report.file.generation.location}")
    private String userHome;


    private Attributes getHimelliFields() {
        String mapperFile = PropertyReader.getProperty(HIMELLI_MAPPER_FILE);
        LOGGER.info("Going to read himelli attribute mapping file.");
        File file;
        Attributes himelliFields = null;
        try {
            file = ResourceUtils.getFile("classpath:" + mapperFile);
            JAXBContext jaxbContext = JAXBContext.newInstance(Attributes.class);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            himelliFields = (Attributes) jaxbUnmarshaller.unmarshal(file);
        } catch (FileNotFoundException | JAXBException e) {
            LOGGER.error(e);
        }
        return himelliFields;
    }

    private int getSeqRow() {
        return this.seqRow++;
    }

    private Map<String, String> getTypeMap() {
        LOGGER.info("Creating type mapping for himelli.");
        typeMap = new HashMap<>();
        typeMap.put("CreateAssembly", "a");
        typeMap.put("CreateMaterial", "a");
        typeMap.put("Provide", "p");
        typeMap.put("ProcessContinuousProvide", "p");
        LOGGER.info("Finished type mapping for himelli.");
        return typeMap;
    }

    /**
     * this method builds the himelli report body, table header and table body
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
        Attributes himelliFields = getHimelliFields();
        this.optionalRowGenarator = new OptionalRowGenarator(himelliFields);
        this.cellMasters = new CellMasters(himelliFields);
        this.optionalRowGenarator.setCellMasters(this.cellMasters);
        List<String> headers = this.prepareReportHeaders(himelliFields);
        List<List<String>> rowListForTable = this.prepareRowData(himelliFields, json);
        TableGenerator tableGenerator = new TableGenerator();
        String himelliReportBody = tableGenerator.generateTable(headers, rowListForTable);
        return himelliReportBody;
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
                } else if (himelliHeader.equals("Type")) {
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

    public String generateHimelliReportName(String type, String name, String rev) {
        StringBuilder fileNameBuilder = new StringBuilder();
        fileNameBuilder.append(type).append("_").append(name).append("_").append(rev).append("_").append(formattedDateGenerate()).append("_")
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

    public Boolean isBackgroundProcess(HimelliModel himelliModel) throws NumberFormatException, FrameworkException {
        try {
            Long numberOfChildren = getStructureCount(himelliModel);
            LOGGER.info("Himelli number of child in structure : " + numberOfChildren);
            himelliModel.setNumberOfChildInTheStructure(numberOfChildren);
            return numberOfChildren > Long.parseLong(PropertyReader.getProperty("himelli.background.process.large.structure.max.items.count"));
        } catch (Exception exp) {
            LOGGER.error(exp.getMessage());
            throw exp;
        }
    }

    private Long getStructureCount(HimelliModel himelliModel) throws NumberFormatException, FrameworkException {
        try {
            if (NullOrEmptyChecker.isNullOrEmpty(DIRECTORY_MAP)) {
                DIRECTORY_MAP = PropertyReader.getProperties("bom.export.type.map.directory", true);
            }
            String mapsAbsoluteDirectory = DIRECTORY_MAP.get(himelliModel.getType());

            ObjectTypesAndRelations objectTypesAndRelations = new ObjectTypesAndRelations(mapsAbsoluteDirectory);
            List<String> relationshipNames = objectTypesAndRelations.getRelationshipNames();
            StringJoiner relJoiner = new StringJoiner(",");
            relationshipNames.forEach(relJoiner::add);
            List<String> typeNames = objectTypesAndRelations.getTypeNames();
            StringJoiner typeJoiner = new StringJoiner(",");
            typeNames.forEach(typeJoiner::add);

            String himelliCountQuery;
            if (!NullOrEmptyChecker.isNullOrEmpty(himelliModel.getObjectId())) {
                himelliCountQuery = "eval expression 'count TRUE' on expand bus '" + himelliModel.getObjectId() + "' from rel '" + relJoiner + "' type '" + typeJoiner + "' recurse to all";
            } else {
                himelliCountQuery = "eval expression 'count TRUE' on expand bus '" + himelliModel.getType() + "' '" + himelliModel.getName() + "' '" + himelliModel.getRev() + "' from rel '" + relJoiner + "' type '" + typeJoiner + "' recurse to all";
            }
            LOGGER.info("Himelli number of child in structure count query : " + himelliCountQuery);
            return Long.parseLong(MqlUtil.mqlCommand(himelliModel.getContext(), himelliCountQuery));
        } catch (FrameworkException exp) {
            LOGGER.error(exp.getMessage());
            throw exp;
        }
    }

    public void createAndSaveFile(HimelliModel himelliModel, byte[] himelliReport) {
        try {
            String outputFilePath = createOutputDirectory();
            File myObj = new File(outputFilePath + himelliModel.getRptFileName());
            if (myObj.createNewFile()) {
                Path path = Paths.get(outputFilePath + himelliModel.getRptFileName());
                Files.write(path, himelliReport);
            } else {
                LOGGER.info("File already exists.");
            }
        } catch (IOException e) {
            LOGGER.error(e);
        }
    }

    public String createOutputDirectory() {
        String outputFilePath = userHome + File.separator + "generated_reports" + File.separator;
        File files = new File(outputFilePath);
        if (!files.exists()) {
            if (files.mkdirs()) {
                LOGGER.info("Directories are created.");
                return outputFilePath;
            } else {
                LOGGER.info("Failed to create directories.");
                return "";
            }
        } else {
            LOGGER.info("Directories already created.");
            return outputFilePath;
        }
    }

    public void removeAttr(String attributeString, HimelliReportProcessor himelliReportProcessing) {
        List<String> completeAttrList = new ArrayList<>(List.of("Technical Designation", "Type", "name", "Level", "Status", "Title", "Drawing Number", "Weight", "revision", "Material", "Size", "Unit", "item common text", "item purchasing text", "PDM revision", "Term_ID", "DistributionList", "Release purpose", "Width", "Position", "Unique Key", "Standard", "Mastership", "Length", "id", "Unique Key"));
        String[] attrParam = attributeString.split(",");
        for (String s : attrParam) {
            completeAttrList.remove(s);
        }

        for (String s : completeAttrList) {
            himelliReportProcessing.removeAttr(s);
        }
    }

    public void prepareToSendMail(String sender, HimelliModel himelliModel) {
        IMail mail = new Mail();
        IMailModelBuilder mailModelBuilder = new MailModelBuilder();

        //Prepared subject
        String subject = "[" + getEnvironmentName() + "] [" + himelliModel.getName() + "] " + mailSubject;

        //Building mail object
        MailModel mailObj = mailModelBuilder.setTo(sender).setSubject(subject).setData(getMailTemplate(subject, prepareReportDownloadLink(himelliModel))).setMailContentType(MailContentType.HTML).build();

        try {
            //Sending mail with mail object
            mail.sendMail(mailObj);
        } catch (MessagingException e) {
            LOGGER.error(e);
        }
    }

    private String getMailTemplate(String subject, String downloadLink) {
        return "<!DOCTYPE html>\n" +
                "<html lang=\"en\">\n" +
                "    <head>\n" +
                "        <title>" + subject + "</title>\n" +
                "        <style>\n" +
                "            .footer {\n" +
                "                position: fixed;\n" +
                "                left: 0;\n" +
                "                bottom: 0;\n" +
                "                width: 100%;\n" +
                "                font-weight: bold;\n" +
                "                font-size: 10px;\n" +
                "            }\n" +
                "\n" +
                "            .footer mark {\n" +
                "                font-size: 12px;\n" +
                "            }\n" +
                "        </style>\n" +
                "    </head>\n" +
                "    <body>\n" +
                "        <div class=\"container\">\n" +
                "            Hello,\n" +
                "            <br />\n" +
                "            Background Process for large report has been completed for this item.<br />\n" +
                "            <b>Report download link : " + downloadLink + "</b><br />\n" +
                "            Thanks.<br/>\n" +
                "        </div>\n" +
                "        <div class=\"footer\">\n" +
                "            This email may contain confidential and/or legally privileged information. For any mismatch , please inform concern person immediately\n" +
                "        </div>\n" +
                "    </body>\n" +
                "</html>\n";
    }

    private String getEnvironmentName() {
        String environmentName = PropertyReader.getEnvironmentName();
        String[] envNameParts = environmentName.split("_");
        return envNameParts.length > 1 ? envNameParts[1] : envNameParts[0];
    }

    private String prepareReportDownloadLink(HimelliModel himelliModel) {
        String fileName = himelliModel.getRptFileName();
        String reportDownloadUrl = null;
        reportDownloadUrl = getBaseUrl(himelliModel) + PropertyReader.getProperty("himelli.report.download.service") + fileName;
        reportDownloadUrl = "<a href='" + reportDownloadUrl + "'>" + himelliModel.getName() + "</a>";
        LOGGER.info(reportDownloadUrl);
        return reportDownloadUrl;
    }

    private static String getBaseUrl(HimelliModel himelliModel) {
        String devMachine = System.getenv("dev_machine");
        devMachine = Optional.ofNullable(devMachine).filter(devPc -> !devPc.isEmpty()).orElse("");
        LOGGER.info("Machine : " + devMachine);
        if ((devMachine.equalsIgnoreCase("local_code_dev_machine")) && (himelliModel.getBaseUrl().contains("localhost") || himelliModel.getBaseUrl().contains("127.0.0.1"))) {
            return himelliModel.getBaseUrl();
        } else {
            return PropertyReader.getProperty("enovia.rest.service.url");
        }
    }
}
