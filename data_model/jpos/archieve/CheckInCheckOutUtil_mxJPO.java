/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 * @author Faruq
 */

import java.util.HashMap;
import com.matrixone.apps.common.Document;
import com.dassault_systemes.VPLMJDocumentServices.VPLMJDocumentServices;
import matrix.db.Context;
import matrix.db.JPO;
import com.matrixone.apps.domain.util.MapList;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import matrix.db.BusinessObject;
import matrix.util.MatrixException;
import org.apache.commons.fileupload.disk.DiskFileItem;

public class CheckInCheckOutUtil_mxJPO {

    String role = "ctx::VPLMAdmin.Company Name.Default";
    public CheckInCheckOutUtil_mxJPO(Context context, String[] args) throws Exception {

    }

    public String createDocument(Context context, String[] args) throws Exception {
        Document document = null;
        try {
            HashMap programMap = (HashMap) JPO.unpackArgs(args);
            //context.setRole("ctx::VPLMViewer.VALMET_INTERNAL.GLOBAL_COMPONENTS_INTERNAL");
            context.setVault("eService Production");

            document = new Document();
            
            String documentName = programMap.get("DocumentName").toString();

            String type = "Document";
            String policy = "Document Release";
            String name = documentName;
            String description = documentName;
            String tilte = documentName;
            String language = "English";
            Map <String,String> m = new HashMap<>();
//            m.put("File Type","TEXT");
//            m.put("Checkin Reason","TEsting");
            document.createAndConnect(context, type, name, (String) null, policy, description, (String) null, tilte, language, null, null, null, m, (String) null);
            if (document == null) {
                throw new NullPointerException("The document creation failed");
            }
            document.setState(context, "IN_WORK");
        } catch (Exception exp) {
            throw exp;
        }
        return document.getId();
    }

    public String attachDocument(Context context, String[] args) throws Exception {
        HashMap programMap = (HashMap) JPO.unpackArgs(args);

        try {
            //context.setRole("ctx::VPLMProjectLeader.Company Name.Common Space");
           // context.setVault("eService Production");
           // context.setApplication("VPLM");
            VPLMJDocumentServices vplmDocumentServicesJ = VPLMJDocumentServices.getInstance();
            
            HashMap attachDocumentProperties = new HashMap();
           // context.setSessionId((String)programMap.get("sessionId"));
//            attachDocumentProperties.put("role", "ctx::VPLMProjectLeader.Company Name.Common Space");
//            attachDocumentProperties.put("vault", "eService Production");
//            attachDocumentProperties.put("application", "VPLM");
           // context.setSessionId(programMap.get("sessionId").toString());
            attachDocumentProperties.put("objectId", programMap.get("objectId").toString());
            String[] documentIds = (String[]) programMap.get("documentIds");
            attachDocumentProperties.put("documentIds", documentIds);
            vplmDocumentServicesJ.attachDocuments(context, attachDocumentProperties);

            return "Document Attached";
        } catch (Exception exp) {
            throw exp;
        }
    }

    public MapList fileCheckOut(Context context, String[] args) throws Exception {
        //context.setRole("ctx::VPLMProjectLeader.Company Name.Common Space");
        MapList mapList = new MapList();
        try {
            HashMap programMap = (HashMap) JPO.unpackArgs(args);
            HashMap<String, String> hp = (HashMap) programMap.get("objectIdMap");
            VPLMJDocumentServices vp = VPLMJDocumentServices.getInstance();
            mapList = vp.getDocuments(context, hp);
            System.out.println(mapList.toString());
        } catch (Exception ex) {
            throw ex;
        }
        return mapList;
    }
}
