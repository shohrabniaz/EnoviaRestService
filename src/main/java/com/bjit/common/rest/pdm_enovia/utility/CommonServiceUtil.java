package com.bjit.common.rest.pdm_enovia.utility;

import com.bjit.common.rest.pdm_enovia.model.CheckinBean;
import com.bjit.common.rest.pdm_enovia.model.DocumentInfoBean;
import com.bjit.common.rest.app.service.model.createobject.CreateObjectBean;
import com.bjit.common.rest.app.service.model.tnr.TNR;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Mashuk/BJIT
 */
public class CommonServiceUtil {

    public static CheckinBean getCheckinBean(String objectId, TNR tnr, List<DocumentInfoBean> listOfDocumentInfoBean, List<Map<String, String>> docIdList) {
        CheckinBean checkInBean = new CheckinBean();
        checkInBean.setBaseObjectId(objectId);
        checkInBean.setTnr(tnr);
        checkInBean.setDocumentInfoList(listOfDocumentInfoBean);
        checkInBean.setDocumentIds(docIdList);
        return checkInBean;
    }

    public static CreateObjectBean getCreateObjectBean(String skeletonObjectId, Boolean isAutoName, HashMap attributeMap, TNR tnr, String folderId, String source) {
        CreateObjectBean createObjectBean = new CreateObjectBean();
        createObjectBean.setIsAutoName(isAutoName);
        createObjectBean.setTnr(tnr);
        createObjectBean.setAttributes(attributeMap);
        createObjectBean.setTemplateBusinessObjectId(skeletonObjectId);
        createObjectBean.setAttributeGlobalRead(false);
        createObjectBean.setFolderId(folderId);
        createObjectBean.setSource(source);
        return createObjectBean;
    }

    public static TNR getTNR(String type, String name, String revision) {
        TNR tnr = new TNR();
        tnr.setName(name);
        tnr.setType(type);
        tnr.setRevision(revision);
        return tnr;
    }

    public static DocumentInfoBean getDocumentInfoBean(CreateObjectBean createObjectBean, List<String> fileNameList) {
        DocumentInfoBean documentInfoBean = new DocumentInfoBean();
        documentInfoBean.setCreateObjectBean(createObjectBean);
        documentInfoBean.setFiles(fileNameList);
        return documentInfoBean;
    }
}
