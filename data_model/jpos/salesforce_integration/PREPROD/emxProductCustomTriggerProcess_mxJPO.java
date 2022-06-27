/*
 * This JPO is to create xml file on server for following types :
 * ModelVersion
 * The xml file contains Item information like, id, name, rev, current state and next state. 
 */
import matrix.db.*;
import java.io.*;
import java.net.*;
import com.matrixone.apps.domain.util.MqlUtil;
import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;
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

public class emxProductCustomTriggerProcess_mxJPO {

    public emxProductCustomTriggerProcess_mxJPO() {
    }

    public int mxMain(Context ctx, String[] args) {
        return 1;
    }

    public void CommonStateChangeFileGeneration(Context ctx, String[] args) throws Exception, UnknownHostException, IOException, ClassNotFoundException, InterruptedException {

        System.out.println("Model Version Release Trigger Function called successffully!!!!");
        String objectId = args[0];
        String currentState = args[1];
        String nextState = args[2];
        String name = args[3];
        String revision = args[4];
        String type = args[5];
        DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmssSS");
        Date date = new Date();
        String dateAndTime = dateFormat.format(date);
		
		String dirInitial = "";
		String dir1 = "D:" + File.separator + "Integrations" + File.separator + "EnoviaToSalesforce";
		String dir2 = "G:" + File.separator + "Integrations" + File.separator + "EnoviaToSalesforce";
		String dir3 = "H:" + File.separator + "Integrations" + File.separator + "EnoviaToSalesforce";
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
		
		String dir = dirInitial + File.separator + "ModelVersion_" + name + "_" + dateAndTime + ".xml";
		if(checkAUTLifecycleStatus(ctx,objectId,"Active")&&isMaturityStatusCheck(ctx,objectId,"Release")) {
		  createXmlFileInSpecificDirectory(objectId, currentState, nextState, name, revision, dir, " ");
		} else {
			if((checkAUTLifecycleStatus(ctx,objectId,"Phase out")||checkAUTLifecycleStatus(ctx,objectId,"End of Life"))&&isMaturityStatusCheck(ctx,objectId,"Release")) {
				if(isAnyModelVersionActive(ctx,name)) {
					 createXmlFileInSpecificDirectory(objectId, currentState, nextState, name, revision, dir, "other active version is available");
					
				} else {
					
					 createXmlFileInSpecificDirectory(objectId, currentState, nextState, name, revision, dir, " ");
				}
				
			} else {
				
				if(checkAUTLifecycleStatus(ctx,objectId,"Pilot")&&isMaturityStatusCheck(ctx,objectId,"Review")) {
					
					if(isAnyModelVersionActive(ctx,name)) {
					    createXmlFileInSpecificDirectory(objectId, currentState, nextState, name, revision, dir, "other active version is available");
					
				    } else {
					
					    createXmlFileInSpecificDirectory(objectId, currentState, nextState, name, revision, dir, " ");
				    }
					
				} else {
					
					if(checkAUTLifecycleStatus(ctx,objectId,"Discontinued")&&isMaturityStatusCheck(ctx,objectId,"Obsolete")) {
					
				     if(isAnyModelVersionActive(ctx,name)) {
					    createXmlFileInSpecificDirectory(objectId, currentState, nextState, name, revision, dir, "other active version is available");
					
				    } else {
					
					    createXmlFileInSpecificDirectory(objectId, currentState, nextState, name, revision, dir, " ");
				    }
				   
				   }
				
			}
			
		}
    }
}
    public void createXmlFileInSpecificDirectory(String objectId, String currentState, String nextState, String name, String revision, String dir,String message) {
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
			
			Element messageError = doc.createElement("message");
            messageError.appendChild(doc.createTextNode(message));
            rootElement.appendChild(messageError);

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
	
	 public  boolean checkAUTLifecycleStatus(Context ctx,String objectId, String status) {
		boolean isStatus= false;
		MQLCommand objMQL = new MQLCommand();
		String sMQLStatement = "pri bus "+ objectId +" select attribute[AUT Lifecycle Status].value dump |"; 
        if (objectId != null) {
			try {
            String result = MqlUtil.mqlCommand(ctx, objMQL, sMQLStatement);
			if(result.equals(status)){
				isStatus = true;
			}
        } catch (Exception ex) {
     
        }
        }
        
        return isStatus;
    }
	
	/*public  boolean isAUTLifeStatusPilot(Context ctx,String objectId) {
		boolean isActive= false;
		MQLCommand objMQL = new MQLCommand();
		String sMQLStatement = "pri bus "+ objectId +" select attribute[AUT Lifecycle Status].value dump |"; 
        if (objectId != null) {
			try {
            String result = MqlUtil.mqlCommand(ctx, objMQL, sMQLStatement);
			if(result.equals("Pilot")){
				isActive = true;
			}
        } catch (Exception ex) {
			
        }
        }
        return isActive;
    }*/
	
	public  boolean isMaturityStatusCheck(Context ctx,String objectId,String maturity) {
		boolean isMaturityStatus = false;
		MQLCommand objMQL = new MQLCommand();
		String sMQLStatement = "pri bus "+ objectId +" select current dump |"; 
        if (objectId != null) {
			try {
            String result = MqlUtil.mqlCommand(ctx, objMQL, sMQLStatement);
			if(result.equals(maturity)){
				isMaturityStatus = true;
			}
        } catch (Exception ex) {
			
        }
        }
        return isMaturityStatus;
    }
	
	public  boolean isAnyModelVersionActive(Context ctx,String objectName) {
		boolean isActive =false;
		try {
			MQLCommand objMQL = new MQLCommand();
			String sMQLStatement = "temp query bus Products "+ objectName +" * select attribute[AUT Lifecycle Status].value current dump <>";
			String result = MqlUtil.mqlCommand(ctx, objMQL, sMQLStatement);
			Scanner scLine = new Scanner(result);
			String lineobject;
			while (scLine.hasNext()) {
				lineobject = scLine.nextLine();
				String[] objectValue = lineobject.split("<>");
                                if(objectValue.length == 5){
                                    if(objectValue[3].equals("Active") && !objectValue[4].equals("Release")){
                                            return true;
                                    }
                                }
			}
		} catch (Exception ex) {
			
        }
       return isActive;		
    }
}
