
import matrix.db.BusinessInterface;
import matrix.db.BusinessInterfaceList;
import matrix.db.BusinessObject;
import matrix.db.Context;
import matrix.util.MatrixException;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.function.*;

public class emxCustomRevisionTrigger_mxJPO {
    private final Function<MasterShipChangeModel, MasterShipChangeModel> setMastershipXMLData = model -> {
        model.fileData = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
                "<item>\n" +
                "   <type>" + model.type + "</type>\n" +
                "    <name>" + model.name + "</name>\n" +
                "    <revision>" + model.revision + "</revision>\n" +
                "    <id>" + model.objectId + "</id>\n" +
                "    <currentState>" + model.currentState + "</currentState>\n" +
                "    <nextState>" + model.nextState + "</nextState>\n" +
                "    <username>" + model.username + "</username>\n" +
                "    <event>Promote</event>\n" +
                "</item>";
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

    private final Function<MasterShipChangeModel, MasterShipChangeModel> setMastershipChangeFileDirectory = model -> {
        model.fileName = getDirectory("pdm-mastership") + File.separator + model.type + "_" + model.name + "_revision"+ "_" + this.getDate.get() + ".xml";
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

    Function<String[], MasterShipChangeModel> getMastershipChangeModel =
            args -> new MasterShipChangeModel(args[5], args[3], args[4], args[0], args[1], args[2]);

    private final String typeList = "CreateAssembly,ElementaryEndItem,Provide,CreateMaterial,ProcessContinuousCreateMaterial,ProcessContinuousProvide";

    public emxCustomRevisionTrigger_mxJPO() {
    }

    private void writeDataToALogFileMastershipChangeRevision(Context ctx,String[] args) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter("G:\\Integrations\\pdm-mastership\\requestDataFromMastershipItemRevision.txt"));

        Arrays.asList(args).forEach(requestData -> {
            try {
                writer.write(requestData + "\n");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        writer.write(ctx.getUser());
        writer.close();
    }

    String mastershipChangeModelMastershipAttribute = "3DX";

    public void mastershipChangeFileGeneration(Context ctx, String[] args) throws Exception {
        System.out.println("Mastership Change Trigger Function called successffully!!!!");

        writeDataToALogFileMastershipChangeRevision(ctx,args);

        MasterShipChangeModel masterShipChangeModel = getMastershipChangeModel.apply(args);
        masterShipChangeModel.username = ctx.getUser();
        masterShipChangeModel = setItemTypeAndMastership.apply(ctx, masterShipChangeModel);
        masterShipChangeModel.hasInterface = hasTheInterface(ctx, masterShipChangeModel.objectId, "MBOM_MBOMPDM");

        if (masterShipChangeModel.mastership.equalsIgnoreCase(mastershipChangeModelMastershipAttribute) && masterShipChangeModel.hasInterface ) {

            Optional.of(masterShipChangeModel)
                    .map(setMastershipChangeFileDirectory)
                    .map(setMastershipXMLData)
                    .ifPresent(generateXMLFile);
        }
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
        public String username;

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
