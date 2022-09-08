
import com.matrixone.apps.domain.util.FrameworkException;
import matrix.db.BusinessInterface;
import matrix.db.BusinessInterfaceList;
import matrix.db.BusinessObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.function.*;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.matrixone.apps.domain.util.MqlUtil;
import java.util.logging.Level;
import java.util.logging.Logger;

import matrix.db.Context;
import matrix.db.MQLCommand;
import matrix.util.MatrixException;

public class emxCustomTriggerProcess_mxJPO {

    private final Function<MasterShipChangeModel, MasterShipChangeModel> setMastershipXMLData = model -> {
        model.fileData = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n"
                + "<item>\n"
                + "   <type>" + model.type + "</type>\n"
                + "    <name>" + model.name + "</name>\n"
                + "    <revision>" + model.revision + "</revision>\n"
                + "    <id>" + model.objectId + "</id>\n"
                + "    <currentState>" + model.currentState + "</currentState>\n"
                + "    <nextState>" + model.nextState + "</nextState>\n"
                + "    <event>Promote</event>\n"
                + "</item>";
        return model;
    };
    Consumer<MasterShipChangeModel> generateXMLFile = (MasterShipChangeModel model) -> {
        try {
            PrintWriter writer = new PrintWriter(model.fileName, StandardCharsets.UTF_8);
            writer.println(model.fileData);
            writer.close();
        } catch (Exception exp) {
            exp.printStackTrace();
        }
    };
    Supplier<String> getDate = () -> {
        DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmssSS");
        Date date = new Date();
        return dateFormat.format(date);
    };
    private final Function<MasterShipChangeModel, MasterShipChangeModel> setFileDirectory = model -> {
        if (model.mastership.equalsIgnoreCase("3DX") && model.hasInterface && model.owner.equalsIgnoreCase("coexusr1")) {
            model.fileName = getDirectory("pdm-mastership") + File.separator + model.type + "_" + model.name + "_" + this.getDate.get() + ".xml";
        } else //set condition here
        {
            model.fileName = getDirectory("LNTransfer") + File.separator + model.type + "_" + model.name + "_" + this.getDate.get() + ".xml";
        }
        return model;
    };
    BiFunction<Context, MasterShipChangeModel, MasterShipChangeModel> setItemTypeAndMastership = (ctx, masterShipChangeModel) -> {
        try {
            BusinessObject businessObject = new BusinessObject(masterShipChangeModel.objectId);
            businessObject.open(ctx);
            masterShipChangeModel.itemType = businessObject.getAttributeValues(ctx, "MBOM_MBOMReference.MBOM_Type").getValue();
            masterShipChangeModel.mastership = businessObject.getAttributeValues(ctx, "MBOM_MBOMPDM.MBOM_Mastership").getValue();
            masterShipChangeModel.owner = businessObject.getOwner().getName();

            return masterShipChangeModel;
        } catch (MatrixException e) {
            throw new RuntimeException(e);
        }
    };
    Function<String[], MasterShipChangeModel> getMastershipChangeModel
            = args -> new MasterShipChangeModel(args[5], args[3], args[4], args[0], args[1], args[2]);
    private final String typeList = "CreateAssembly,ElementaryEndItem,Provide,CreateMaterial,ProcessContinuousCreateMaterial,ProcessContinuousProvide";
    BiConsumer<Context, MasterShipChangeModel> generateXMLFileForDifferentApp = (context, masterShipChangeModel) -> {
        System.out.println(masterShipChangeModel.mastership.equalsIgnoreCase("3DX"));

        if (masterShipChangeModel.mastership.equalsIgnoreCase("3DX") && masterShipChangeModel.hasInterface && masterShipChangeModel.owner.equalsIgnoreCase("coexusr1")) {
            Optional.of(masterShipChangeModel)
                    .map(setFileDirectory)
                    .map(setMastershipXMLData)
                    .ifPresent(generateXMLFile);
        } else {
            try {
                lnXMLFileGenerationProcess(context, masterShipChangeModel);
            } catch (MatrixException e) {
                throw new RuntimeException(e);
            }
        }
    };

    public emxCustomTriggerProcess_mxJPO() {
    }

    private void writeDataToAFile(String[] args) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter("G:\\Integrations\\LNTransfer\\requestDataFromLN.txt"));

        Arrays.asList(args).forEach(requestData -> {
            try {
                writer.write(requestData + "\n");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        writer.close();
    }

    public void CommonStateChangeFileGeneration(Context ctx, String[] args) throws Exception {
        System.out.println("Trigger Function called successffully!!!!");

        writeDataToAFile(args);

        MasterShipChangeModel masterShipChangeModel = getMastershipChangeModel.apply(args);
        masterShipChangeModel = setItemTypeAndMastership.apply(ctx, masterShipChangeModel);
        masterShipChangeModel.hasInterface = hasTheInterface(ctx, masterShipChangeModel.objectId, "MBOM_MBOMPDM");
        generateXMLFileForDifferentApp.accept(ctx, masterShipChangeModel);

    }

    public void customEnoviaCPQTransferAction(Context ctx, String[] args) throws Exception {
        System.out.println("CPQ Trigger Function called successffully!!!!");

        writeDataToAFile(args);

        MasterShipChangeModel masterShipChangeModel = getMastershipChangeModel.apply(args);
        masterShipChangeModel = setItemTypeAndMastership.apply(ctx, masterShipChangeModel);
        masterShipChangeModel.hasInterface = hasTheInterface(ctx, masterShipChangeModel.objectId, "MBOM_MBOMPDM");
        lnXMLFileGenerationProcessCPQ(ctx, masterShipChangeModel);

    }

    private void lnXMLFileGenerationProcess(Context ctx, MasterShipChangeModel model) throws MatrixException {
        String dateAndTime = getDate.get();

        BusinessObject bo = new BusinessObject(model.objectId);

        String itemType = bo.getAttributeValues(ctx, "MBOM_MBOMReference.MBOM_Type").getValue();
        String mastership = bo.getAttributeValues(ctx, "MBOM_MBOMPDM.MBOM_Mastership").getValue();
        String organization = bo.getOrganizationOwner(ctx).getName();

        Boolean isModelItem = itemType.equalsIgnoreCase("Product_Model");
        Boolean isV6Item = mastership.equalsIgnoreCase("") || mastership.equalsIgnoreCase("3DX");
        Boolean isAutValInternalOrg = organization.equalsIgnoreCase("AUTOMATION_VAL_INTERNAL");
        Boolean isSupportedMaturity = model.nextState.equals("RELEASED") || model.nextState.equals("OBSOLETE");

        Boolean isSupportedType = Arrays
                .asList(typeList.split(","))
                .stream()
                .filter(permittedItemType -> permittedItemType.equalsIgnoreCase(model.type))
                .findFirst()
                .isPresent();

        String dirInitial = getDirectory("LNTransfer");

        if (!isModelItem && isV6Item && isSupportedType && isSupportedMaturity && !isAutValInternalOrg) { //Generate Item & BOM transfer
            String dir = dirInitial + File.separator + "LNTransfer_" + model.name + "_" + dateAndTime + ".xml";
            createXmlFileInSpecificDirectory(model.objectId, model.currentState, model.nextState, model.name, model.revision, dir);
        } else if (isSupportedMaturity && model.type.equals("DELI_VALDeliverable")) {
            //Deliverable Sending
            String dir = dirInitial + File.separator + "Disassemble_" + model.name + "_" + dateAndTime + ".xml";
            createXmlFileInSpecificDirectory(model.objectId, model.currentState, model.nextState, model.name, model.revision, dir);
        }
        /*else if (nextState.equals("RELEASED") && type.equals("Drawing") ) {
            String dir = "D:"+File.separator+"Drawing"+File.separator+"Drawing_"+dateAndTime+".xml";
            createXmlFileInSpecificDirectory(objectId,currentState,nextState,name,revision,dir);
        }*/
    }

    private void lnXMLFileGenerationProcessCPQ(Context ctx, MasterShipChangeModel model) throws MatrixException {
        String dateAndTime = getDate.get();

        BusinessObject bo = new BusinessObject(model.objectId);

        String itemType = bo.getAttributeValues(ctx, "MBOM_MBOMReference.MBOM_Type").getValue();
        String mastership = bo.getAttributeValues(ctx, "MBOM_MBOMPDM.MBOM_Mastership").getValue();
        String organization = bo.getOrganizationOwner(ctx).getName();

        Boolean isAutValInternalOrg = organization.equalsIgnoreCase("AUTOMATION_VAL_INTERNAL");

        String itemCode = bo.getAttributeValues(ctx, "MBOM_MBOMATON.MBOM_ItemCode").getValue();
        Boolean isSupportedType = Arrays
                .asList(typeList.split(","))
                .stream()
                .filter(permittedItemType -> permittedItemType.equalsIgnoreCase(model.type))
                .findFirst()
                .isPresent();

        String dirForenCPQ = getDirectory("EnoviaToCPQ");
        if (isAutValInternalOrg && model.nextState.equals("RELEASED") && !itemCode.isEmpty()) {

            String dir = dirForenCPQ + File.separator + "CPQTransfer_" + model.name + "_" + dateAndTime + ".xml";
            createXmlFileInSpecificDirectory(model.objectId, model.currentState, model.nextState, model.name, model.revision, dir);
        } else if (isAutValInternalOrg && model.nextState.equals("FROZEN") && !itemCode.isEmpty()) {
            FetchEvolution fetchEvolution = new FetchEvolution();
            FecthItemAutCycle fecthItemAutCycle = new FecthItemAutCycle();
            List<String> relationshipIdByItem = fecthItemAutCycle.getRelationshipIdByItem(ctx, model.type, model.name, model.revision);
            for (int i = 0; i < relationshipIdByItem.size(); i++) {

                List<String> typeRelationshipInfo = fecthItemAutCycle.getTypeRelationshipId(ctx, relationshipIdByItem.get(i));
                if (typeRelationshipInfo.get(2).equalsIgnoreCase(model.revision)) {

                    try {
                        boolean hasEvolution = fetchEvolution.hasEvolution(relationshipIdByItem.get(i), ctx);
                        if (hasEvolution) {
                            Map<String, List<String>> mvRelid = fetchEvolution.getEvolution(relationshipIdByItem.get(i), ctx);

                            ///  List<String> mvrel = mvRelid.get(relationshipIdByItem.get(i));
                            // List<String> mvitemInfo = fecthItemAutCycle.getTypeRelationshipId(ctx, mvrel.get(0));
                            boolean autType = fecthItemAutCycle.getAUTType(ctx, typeRelationshipInfo.get(0), typeRelationshipInfo.get(1), typeRelationshipInfo.get(2));
                            if (autType) {
                                dirForenCPQ = getDirectory("EnoviaToCPQ");
                                String dir = dirForenCPQ + File.separator + "CPQTransfer_" + model.name + "_" + dateAndTime + ".xml";
                                createXmlFileInSpecificDirectory(model.objectId, model.currentState, model.nextState, model.name, model.revision, dir);
                            }

                        }
                    } catch (Exception ex) {
                        Logger.getLogger(emxCustomTriggerProcess_mxJPO.class.getName()).log(Level.SEVERE, null, ex);
                    }

                }

            }

        }
        /*else if (nextState.equals("RELEASED") && type.equals("Drawing") ) {
            String dir = "D:"+File.separator+"Drawing"+File.separator+"Drawing_"+dateAndTime+".xml";
            createXmlFileInSpecificDirectory(objectId,currentState,nextState,name,revision,dir);
        }*/
    }

    private String getDirectory(String env) {
        String dirInitial = "";
        String dir1 = "D:" + File.separator + "Integrations" + File.separator + env;
        String dir2 = "G:" + File.separator + "Integrations" + File.separator + env;
        String dir3 = "H:" + File.separator + "Integrations" + File.separator + env;
        File file1 = new File(dir1);
        File file2 = new File(dir2);
        File file3 = new File(dir3);

        if (file1.exists()) {
            dirInitial = dir1;
        } else if (file2.exists()) {
            dirInitial = dir2;
        } else {
            dirInitial = dir3;
        }
        return dirInitial;
    }

    public BusinessInterfaceList getInterfaceList(Context context, String id) throws MatrixException, ExecutionException, InterruptedException {
        BusinessObject businessObject = new BusinessObject(id);
        return businessObject.getBusinessInterfaces(context);
    }

    public Boolean hasTheInterface(Context context, String id, String interfaceName) throws MatrixException, ExecutionException, InterruptedException {
        return Optional.ofNullable(getInterfaceList(context, id))
                .map(itemInterfaceList -> getInterfaceName(itemInterfaceList, interfaceName))
                .map(foundInterface -> Optional.ofNullable(foundInterface)
                .filter(pdmInterface -> !pdmInterface.isEmpty())
                .isPresent())
                .orElse(Boolean.FALSE);
    }

    String getInterfaceName(BusinessInterfaceList interfaceNameList, String interfaceName) {
        BusinessInterface businessInterface = interfaceNameList.find(interfaceName);
        String name = Optional
                .ofNullable(businessInterface)
                .map(pdmItemInterface -> pdmItemInterface.getName())
                .orElse("");
        return name;
    }

    public int mxMain(Context ctx, String[] args) {
        return 1;
    }

    public void createXmlFileInSpecificDirectory(String objectId, String currentState, String nextState, String name, String revision, String dir) {
        try {

            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

            Document doc = docBuilder.newDocument();
            Element rootElement = doc.createElement("iteminfo");
            doc.appendChild(rootElement);

            Element staff = doc.createElement("id");
            staff.appendChild(doc.createTextNode(objectId));
            rootElement.appendChild(staff);

            Element itemName = doc.createElement("name");
            itemName.appendChild(doc.createTextNode(name));
            rootElement.appendChild(itemName);

            Element itemRev = doc.createElement("revision");
            itemRev.appendChild(doc.createTextNode(revision));
            rootElement.appendChild(itemRev);

            Element itemCurrentStatus = doc.createElement("currentState");
            itemCurrentStatus.appendChild(doc.createTextNode(currentState));
            rootElement.appendChild(itemCurrentStatus);

            Element itemNextState = doc.createElement("nextState");
            itemNextState.appendChild(doc.createTextNode(nextState));
            rootElement.appendChild(itemNextState);

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            transformer.setOutputProperty(OutputKeys.METHOD, "xml");
            transformer.setOutputProperty(OutputKeys.ENCODING, "ISO-8859-1");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            StreamResult result = new StreamResult(new File(dir));

            transformer.transform(source, result);
            System.out.println("File saved! Directory :" + dir);

        } catch (ParserConfigurationException pce) {
            pce.printStackTrace();
        } catch (TransformerException tfe) {
            tfe.printStackTrace();
        }
    }

    private class MasterShipChangeModel {

        public String fileName;
        public String fileData;
        public String type;
        public String objectId;
        public String currentState;
        public String nextState;
        public String name;
        public String revision;
        public String itemType;
        public String mastership;
        public Boolean hasInterface;
        public String owner;

        private MasterShipChangeModel() {
        }

        private MasterShipChangeModel(String type, String name, String revision, String objectId, String currentState, String nextState) {
            this.type = type;
            this.objectId = objectId;
            this.currentState = currentState;
            this.nextState = nextState;
            this.name = name;
            this.revision = revision;
        }
    }
}

class FetchEvolution {
    // private static final Logger LOGGER = Logger.getLogger(FetchEvolution.class.getName());

    public boolean hasEvolution(String pid, Context context) throws Exception {
        System.out.println(" ++++++++++++++ hasEvolution +++++++++++++++ ");
        String queryResult = this.evolutionQueryResult(pid, context);

        if (queryResult == null || queryResult.isEmpty()) {
            return false;
        }
        System.out.println(" -------------- hasEvolution --------------- ");
        return true;
    }

    private String evolutionQueryResult(String pid, Context context) throws Exception {
        String queryFormat = "expand bus {0} from relationship VPLMrel/PLMConnection/V_Owner type VPMCfgEffectivity select bus physicalid dump |";
        String query = MessageFormat.format(queryFormat, pid);
        return FetchEvolution.getQueryResult(query, context);
    }

    public Map<String, List<String>> getEvolution(String id, Context context) throws Exception {
        Map<String, List<String>> instanceEvolutionMap = new HashMap<>();
        if (!this.hasEvolution(id, context)) {
            return instanceEvolutionMap;
        }

        String resultVPMCfgEffectivity = evolutionQueryResult(id, context);

        List<String> idOfVPMCfgEffectivity = this.getVPMCfgEffectivityId(resultVPMCfgEffectivity);

        List<String> pathsInfo = this.getPathsInfoOfVPMCfgEffectivity(idOfVPMCfgEffectivity, context);

        instanceEvolutionMap = this.getEvolutionMap(pathsInfo);

        return instanceEvolutionMap;
    }

    private Map<String, List<String>> getEvolutionMap(List<String> pathsInfo) {
        System.out.println(" +++++++++++++++ getEvolutionMap ++++++++++++++ ");
        Map<String, List<String>> map = new HashMap<>();

        // each path has one bom-rel id
        for (String path : pathsInfo) {
            // System.out.println(path);
            String[] pathChunks = path.split("\\|");

            String relationshipPid = "";
            List<String> connectedMVs = new ArrayList<>();

            // this loop is for evolution of single rel id
            for (String pathChunk : pathChunks) {
                //System.out.println(pathChunk);

                if (pathChunk.contains("DELFmiFunctionIdentifiedInstance")) {
                    // getting relationship pid
                    relationshipPid = pathChunk.substring(pathChunk.indexOf("DELFmiFunctionIdentifiedInstance,"))
                            .split("\\,")[1];
                } else if (pathChunk.contains("Products")) {
                    connectedMVs.add(pathChunk.substring(pathChunk.indexOf("Products,")).split("\\,")[1]);
                }
            }
            map.put(relationshipPid, connectedMVs);
        }
        System.out.println(map.toString());
        System.out.println(" --------------- getEvolutionMap -------------- ");
        return map;
    }

    private List<String> getPathsInfoOfVPMCfgEffectivity(List<String> idListOfVPMCfgEffectivity, Context context)
            throws Exception {
        System.out.println(" ++++++++++++ getPathsInfoOfVPMCfgEffectivity ++++++++++++++ ");
        String pathsQueryFormat = "print bus {0} select paths.path dump |";

        List<String> paths = new ArrayList<>();
        for (String id : idListOfVPMCfgEffectivity) {
            String query = MessageFormat.format(pathsQueryFormat, id);
            System.out.println("Query: " + query);
            String queryResult = FetchEvolution.getQueryResult(query, context);
            paths.add(queryResult);
        }
        System.out.println(" ------------ getPathsInfoOfVPMCfgEffectivity -------------- ");
        return paths;
    }

    private List<String> getVPMCfgEffectivityId(String resultVPMCfgEffectivity) {
        List<String> idList = new ArrayList<>();

        String[] linesVPMCfgEffectivity = resultVPMCfgEffectivity.split("\\n");
        System.out.println(Arrays.toString(linesVPMCfgEffectivity));

        for (String e : linesVPMCfgEffectivity) {
            String[] chunks = e.split("\\|");
            String physicalId = chunks[chunks.length - 1];
            System.out.println("VPMCfgEffectivity physical id:" + physicalId);
            idList.add(physicalId);
        }

        return idList;
    }

    public static String getQueryResult(String query, Context context) throws Exception {
        MQLCommand objMQL = new MQLCommand();

        try {
            objMQL.open(context);
            String result = MqlUtil.mqlCommand(context, objMQL, query);

            // objMQL.close(context);
            return result;
        } catch (MatrixException e) {

            System.out.println("Query String : {0}" + query);

            throw e;
        }
    }
}

class FecthItemAutCycle {

    public List<String> getRelationshipIdByItem(Context ctx, String type, String name, String rev) throws FrameworkException {

        List<String> physical = new ArrayList<>();

        // pri bus CreateAssembly mass-EPS1-00017778 1.1 select from[DELFmiFunctionIdentifiedInstance].id dump |
        StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append("print bus")
                .append(" ").append(type)
                .append(" ").append(name)
                .append(" ").append(rev)
                .append(" select from[DELFmiFunctionIdentifiedInstance].id dump |");
        String mqlQuery = queryBuilder.toString();
        System.out.println(" Relationship Id Info " + mqlQuery);
        String mqlResult = MqlUtil.mqlCommand(ctx, mqlQuery);

        String[] tempSingleList = mqlResult.split("\\|");

        for (int k = 0; k < tempSingleList.length; k++) {
            physical.add(tempSingleList[k]);
        }

        return physical;
    }

    public List<String> getTypeRelationshipId(Context ctx, String id) throws FrameworkException {

        List<String> mftitemname = new ArrayList<>();
        StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append("print connection")
                .append(" ").append(id)
                .append(" select to.type to.name to.revision dump |");
        String mqlQuery = queryBuilder.toString();

        System.out.println(" Item Info by Relationship " + mqlQuery);
        String mqlResult = MqlUtil.mqlCommand(ctx, mqlQuery);

        String[] tempSingleList = mqlResult.split("\\|");

        for (int k = 0; k < tempSingleList.length; k++) {
            mftitemname.add(tempSingleList[k]);
        }

        return mftitemname;
    }

    public boolean getAUTType(Context ctx, String type, String name, String rev) throws FrameworkException {

        boolean isPilot = false;

        StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append("print bus")
                .append(" ").append(type)
                .append(" ").append(name)
                .append(" ").append(rev)
                .append(" select attribute[AUT Lifecycle Status] dump |");
        String mqlQuery = queryBuilder.toString();
        System.out.println(" AUT Type check " + mqlQuery);
        String mqlResult = MqlUtil.mqlCommand(ctx, mqlQuery);

        if (mqlResult.equalsIgnoreCase("Pilot")) {
            isPilot = true;
        }
        return isPilot;
    }
}
