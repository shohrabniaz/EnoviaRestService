package com.bjit.common.rest.app.service.comosData.xmlPreparation.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/import/jaxb")
public class XLSReaderController {
//    static int counter = 0;
//
//    @GetMapping("/xlsx/read")
////    public List<HashMap<String, String>> createJAXBModel() {
////    public List<RFLP> createJAXBModel() {
////        List<String> headerNames = null;
////        HashMap<String, String> xlsName = null;
////        List<HashMap<String, String>> listOfItems = new ArrayList<>();
////
////        try {
////            File file = new File("C:\\Users\\BJIT\\Desktop\\jaxb\\DevicePositionStructure.xlsx");
////            FileInputStream fis = new FileInputStream(file);
////            XSSFWorkbook myWorkBook = new XSSFWorkbook(fis);
////            XSSFSheet mySheet = myWorkBook.getSheetAt(0);
////            Iterator<Row> rowIterator = mySheet.iterator();
////
////            while (rowIterator.hasNext()) {
////                Row row = rowIterator.next();
////                int rowNum = row.getRowNum();
////                System.out.println(rowNum);
////
////                if (rowNum == 0) {
////                    headerNames = new ArrayList<>();
////                } else {
////                    xlsName = new HashMap<>();
////                    listOfItems.add(xlsName);
////                }
////
////                Iterator<Cell> cellIterator = row.cellIterator();
////
////                while (cellIterator.hasNext()) {
////                    Cell cell = cellIterator.next();
////                    CellType cellType = cell.getCellType();
////                    String objectType = cellType.toString();
////                    int columnIndex = cell.getColumnIndex();
////
////                    if (rowNum == 0) {
////                        setHeaderNames(headerNames, cell, objectType);
////                    } else {
////                        setColumnValue(headerNames, cell, objectType, xlsName, columnIndex);
////                    }
////
////                    System.out.println();
////                }
////            }
////        } catch (Exception exp) {
////            System.out.println(exp.getMessage());
////        }
////        final List<RFLP> rflpList = generateXMLFile(listOfItems);
//////        return listOfItems;
////        return rflpList;
//        return null;
//    }
//
//    private List<RFLP> generateXMLFile(List<HashMap<String, String>> listOfItems){
//        System.out.println("#############################");
//        Gson gson = new Gson();
//        String json = gson.toJson(listOfItems);
//        System.out.println(json);
//        System.out.println("#############################");
//
//
////        System.out.println(listOfItems);
//
//        List<RFLP> rflpList = new ArrayList<>();
//
//        listOfItems.forEach((HashMap<String, String> items) -> {
//            HashMap<String, HashMap<String, String>> mapping = getMapping();
//
//
//            List<RFLVPMItem> rflvpmItemList = new ArrayList<>();
//            LogicalReference logicalReference = new LogicalReference();
//            logicalReference.setId(rflvpmItemList);
//
//            RFLP rflp = new RFLP();
//            rflp.setLogicalReference(logicalReference);
//
//            rflpList.add(rflp);
//
//            mapping.forEach((String key, HashMap<String, String> itemMap) -> {
//                RFLVPMItem rflvpmItem = new RFLVPMItem();
//                rflvpmItemList.add(rflvpmItem);
//
//                rflvpmItem.setvName(prepareAttribute(key));
//
//                itemMap.forEach((String itemKey, String itemValue) -> {
//                    if(itemKey.equalsIgnoreCase("comos_uid")
//                            || itemKey.equalsIgnoreCase("plant_uid")
//                            || itemKey.equalsIgnoreCase("unit_uid")
//                            || itemKey.equalsIgnoreCase("subunit_uid")){
//                        Mandatory mandatory = new Mandatory();
//                        String attributeValue = items.get(itemKey);
//                        mandatory.setPlmExternalId(prepareAttribute(attributeValue));
//                        rflvpmItem.setMandatory(mandatory);
//                    }
//                    else if(itemKey.equalsIgnoreCase("device_position")
//                            || itemKey.equalsIgnoreCase("plant")
//                            || itemKey.equalsIgnoreCase("unit_code")
//                            || itemKey.equalsIgnoreCase("subunit_code")){
//                        String attributeValue = items.get(itemKey);
//                        rflvpmItem.setvName(prepareAttribute(attributeValue));
//                    }
////                    else if(itemKey.equalsIgnoreCase("description_in_english")
////                            || itemKey.equalsIgnoreCase("unit_description")
////                            || itemKey.equalsIgnoreCase("subunit_description")){
////                        rflvpmItem.setvd(prepareAttribute(items.get(itemValue)));
////                    }
//                });
//            });
//        });
//
//        rflpList.forEach(this::writeToFile);
//
//        return rflpList;
//    }
//
//    private void writeToFile(RFLP rflp) {
//        try {
//            JAXBContext jaxbContext = JAXBContext.newInstance(RFLP.class);
//            Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
//            StringWriter sw = new StringWriter();
//            jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
//            jaxbMarshaller.marshal(rflp, sw);
//            String xmlString = sw.toString();
//            System.out.println("printed");
//
//            try (PrintWriter out = new PrintWriter("C:/Users/BJIT/Desktop/comos file/" + ++counter + ".txt")) {
//                out.println(xmlString);
//            }
//
////            PrintWriter out = new PrintWriter("C:/Users/BJIT/Desktop/comos file/" + ++counter + ".txt");
////            out.println(xmlString);
//        }catch(Exception exp){
//            throw new RuntimeException(exp);
//        }
//    }
//
//    private Attribute prepareAttribute(String attributeValue){
//        Attribute attribute = new Attribute();
//        attribute.setType("String");
//        attribute.setValue(attributeValue);
//        return attribute;
//    }
//
//    private HashMap<String, HashMap<String, String>> getMapping(){
//        HashMap<String, HashMap<String, String>> totalMap = new HashMap<>();
//
//        HashMap<String, String> equipmentAttributeMap = new HashMap<>();
//        equipmentAttributeMap.put("comos_uid", "PLM_ExternalId");
//        equipmentAttributeMap.put("device_position", "V_Name");
//        equipmentAttributeMap.put("description_in_english", "V_Description");
//        totalMap.put("Equipment", equipmentAttributeMap);
//
//        HashMap<String, String> plantAttributeMap = new HashMap<>();
//        plantAttributeMap.put("plant_uid", "PLM_ExternalId");
//        plantAttributeMap.put("plant", "V_Name");
//        totalMap.put("Plant", plantAttributeMap);
//
//        HashMap<String, String> unitAttributeMap = new HashMap<>();
//        unitAttributeMap.put("unit_uid", "PLM_ExternalId");
//        unitAttributeMap.put("unit_code", "V_Name");
//        unitAttributeMap.put("unit_description", "V_Description");
//        totalMap.put("Unit", unitAttributeMap);
//
//        HashMap<String, String> subunitAttributeMap = new HashMap<>();
//        subunitAttributeMap.put("subunit_uid", "PLM_ExternalId");
//        subunitAttributeMap.put("subunit_code", "V_Name");
//        subunitAttributeMap.put("subunit_description", "V_Description");
//        totalMap.put("Subunit", subunitAttributeMap);
//
//        return totalMap;
//
//    }
//
//    private void setColumnValue(List<String> headerNames, Cell cell, String objectType, HashMap<String, String> xlsName, int columnIndex) {
//        switch (objectType) {
//            case "_NONE":
//                String cellValue = cell.getStringCellValue();
//                xlsName.put(headerNames.get(columnIndex), cellValue);
//                break;
//            case "NUMERIC":
//                cellValue = Double.toString(cell.getNumericCellValue());
//                xlsName.put(headerNames.get(columnIndex), cellValue);
//                break;
//            case "STRING":
//                cellValue = cell.getStringCellValue();
//                xlsName.put(headerNames.get(columnIndex), cellValue);
//                break;
//            case "FORMULA":
//                cellValue = cell.getCellFormula();
//                xlsName.put(headerNames.get(columnIndex), cellValue);
//                break;
//            case "BLANK":
//                cellValue = cell.getStringCellValue();
//                xlsName.put(headerNames.get(columnIndex), cellValue);
//                break;
//            case "BOOLEAN":
//                cellValue = Boolean.toString(cell.getBooleanCellValue());
//                xlsName.put(headerNames.get(columnIndex), cellValue);
//                break;
//            case "ERROR":
//                cellValue = Byte.toString(cell.getErrorCellValue());
//                xlsName.put(headerNames.get(columnIndex), cellValue);
//                break;
//            default:
//        }
//    }
//
//    private void setHeaderNames(List<String> headerNames, Cell cell, String objectType) {
//        switch (objectType) {
//            case "_NONE":
//                String cellValue = cell.getStringCellValue();
//                headerNames.add(cellValue.toLowerCase());
//                break;
//            case "NUMERIC":
//                cellValue = Double.toString(cell.getNumericCellValue());
//                headerNames.add(cellValue.toLowerCase());
//                break;
//            case "STRING":
//                cellValue = cell.getStringCellValue();
//                headerNames.add(cellValue.toLowerCase());
//                break;
//            case "FORMULA":
//                cellValue = cell.getCellFormula();
//                headerNames.add(cellValue.toLowerCase());
//                break;
//            case "BLANK":
//                cellValue = cell.getStringCellValue();
//                headerNames.add(cellValue.toLowerCase());
//                break;
//            case "BOOLEAN":
//                cellValue = Boolean.toString(cell.getBooleanCellValue());
//                headerNames.add(cellValue.toLowerCase());
//                break;
//            case "ERROR":
//                cellValue = Byte.toString(cell.getErrorCellValue());
//                headerNames.add(cellValue.toLowerCase());
//                break;
//            default:
//        }
//    }
}
