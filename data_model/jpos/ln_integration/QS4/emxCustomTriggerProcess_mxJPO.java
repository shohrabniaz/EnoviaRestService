/*
 * This JPO is to create xml file on server for following types :
 * CreateAssembly, ElementaryEndItem, Provide, CreateMaterial, ProcessContinuousCreateMaterial, ProcessContinuousProvide
 * The xml file contains Item information like, id, name, rev, current state and next state. 
 */
import matrix.db.*;
import java.io.*;
import java.net.*;
import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;


public class emxCustomTriggerProcess_mxJPO {

    public emxCustomTriggerProcess_mxJPO() {
    }

    public int mxMain(Context ctx, String[] args) {
        return 1;
    }

    //VPLM_SMB_Definition Policy
    public void CommonStateChangeFileGeneration(Context ctx, String[] args) throws Exception, UnknownHostException, IOException, ClassNotFoundException, InterruptedException {

        System.out.println("Trigger Function called successffully!!!!");
        String objectId = args[0];
        String currentState = args[1];
        String nextState = args[2];
        String name = args[3];
        String revision = args[4];
        String type = args[5];
        DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmssSS");
        Date date = new Date();
        String dateAndTime = dateFormat.format(date);
        boolean isModelItem = false;
        boolean isV6Item = false;
        boolean isSupportedMaturity = false;
        boolean isSupportedType = false;
        String itemType = "";
        String mastership = "";

        BusinessObject bo = new BusinessObject(objectId);

        itemType = bo.getAttributeValues(ctx, "MBOM_MBOMReference.MBOM_Type").getValue();
        mastership = bo.getAttributeValues(ctx, "MBOM_MBOMPDM.MBOM_Mastership").getValue();

        if (itemType.equalsIgnoreCase("Product_Model")) {// Product_Model type item shouldn't be able to send to LN.
            isModelItem = true;
        }
        if (mastership.equalsIgnoreCase("") || mastership.equalsIgnoreCase("3DX")) { // LN transfer is allowed only V6 Owned item.
            isV6Item = true;
        }
        if (nextState.equals("RELEASED") || nextState.equals("OBSOLETE")) // if Maturity released or Obsolete need to send the information to LN
        {
            isSupportedMaturity = true;
        }

        switch (type) {
            case "CreateAssembly":
                isSupportedType = true;
                break;
            case "ElementaryEndItem":
                isSupportedType = true;
                break;
            case "Provide":
                isSupportedType = true;
                break;
            case "CreateMaterial":
                isSupportedType = true;
                break;
            case "ProcessContinuousCreateMaterial":
                isSupportedType = true;
                break;
            case "ProcessContinuousProvide":
                isSupportedType = true;
                break;
            default:
                isSupportedType = false;
        }
		
		String dirInitial = "";
		String dir1 = "D:" + File.separator + "Integrations" + File.separator + "LNTransfer";
		String dir2 = "G:" + File.separator + "Integrations" + File.separator + "LNTransfer";
		String dir3 = "H:" + File.separator + "Integrations" + File.separator + "LNTransfer";
        File file1 = new File(dir1);
		File file2 = new File(dir2);
		File file3 = new File(dir3);
		
        if(file1.exists()){
			dirInitial = dir1;
        } else if(file2.exists()){
			dirInitial = dir2;
        } else {
			dirInitial = dir3;
		}

        if (!isModelItem && isV6Item && isSupportedType && isSupportedMaturity) { //Generate Item & BOM transfer
            String dir = dirInitial + File.separator + "LNTransfer_" + name + "_" + dateAndTime + ".xml";
            createXmlFileInSpecificDirectory(objectId, currentState, nextState, name, revision, dir);
        } else if (isSupportedMaturity && type.equals("DELI_VALDeliverable")){
            String dir = dirInitial + File.separator + "Deliverable_" + name + "_" + dateAndTime + ".xml";
            createXmlFileInSpecificDirectory(objectId, currentState, nextState, name, revision, dir);
        }
        /*else if (nextState.equals("RELEASED") && type.equals("Drawing") ) {
            String dir = "D:"+File.separator+"Drawing"+File.separator+"Drawing_"+dateAndTime+".xml";
            createXmlFileInSpecificDirectory(objectId,currentState,nextState,name,revision,dir);
        }*/
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

}