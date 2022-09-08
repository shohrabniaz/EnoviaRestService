package com.bjit.common.rest.pdm_enovia.importer;

import com.bjit.common.code.utility.security.ContextPasswordSecurity;
import com.bjit.ewc18x.utils.PropertyReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.http.util.Args;

/**
 * @author Tohidul-571
 *
 */
public class ItemImporterDSLCItemServiceClientV1 implements DSLCItemServiceClient {

    private static String SOURCE = "3DSpace";
    private DSLCItemServiceResponseParser resultParser;
    private String objectType;
    private String objectId;
    private Map<String, List<String>> responseMap;

    /**
     * @param objectType
     * @param objectId
     */
    public ItemImporterDSLCItemServiceClientV1(String objectType, String objectId) {
        Args.notBlank(objectType, "Object Type string");
        Args.notBlank(objectType, "Object PhysicalId string");
        this.objectType = objectType;
        this.objectId = objectId;
        // by default simple parsing is assigned, it can be reset by setter method
        this.resultParser = new SimpleDSLCItemServiceResponseParserForItemImport();
    }

    /**
     * @param resultParser the resultParser to set
     */
    public void setResultParser(DSLCItemServiceResponseParser resultParser) {
        this.resultParser = resultParser;
    }

    /**
     * @return the objectType
     */
    public String getObjectType() {
        return objectType;
    }

    /**
     * @param objectType the objectType to set
     */
    public void setObjectType(String objectType) {
        this.objectType = objectType;
    }

    /**
     * @return the objectId
     */
    public String getObjectId() {
        return objectId;
    }

    /**
     * @param objectId the objectId to set
     */
    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    /**
     * working as "facade driver"
     *
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    @Override
    public String call() throws Exception {
        ContextPasswordSecurity contextPasswordSecurity = new ContextPasswordSecurity();
        DSLCItemServiceFacade dslcItemServiceFacade = null;
        try {
            String userId = contextPasswordSecurity.decryptPassword(PropertyReader.getProperty("context.name"));
            String password = contextPasswordSecurity.decryptPassword(PropertyReader.getProperty("context.pass"));
            String host = PropertyReader.getProperty("matrix.context.cas.connection.host.dslc");
            // should not have port, as dslc item creation not working with integretion url
            String passportUrl = PropertyReader.getProperty("matrix.context.cas.connection.passport");
            String securityContext = PropertyReader.getProperty("preferred.security.context.dslc");
            dslcItemServiceFacade = new DSLCItemServiceFacade(host, passportUrl, userId, password, securityContext);
        } catch (Exception ex) {
            Logger.getLogger(DSLCItemServiceFacade.class.getName()).log(Level.SEVERE, null, ex);
        }
        this.responseMap = dslcItemServiceFacade.call(this.objectId, this.objectType, this.SOURCE);
        String createdItemPhysicalId = this.resultParser.parse(this.responseMap);
        System.out.println("Newly Created Item details- Type:" + this.objectType + ",id:" + createdItemPhysicalId);
        return createdItemPhysicalId;
    }

    private String deletePortFromURL(String u) {
        URL url = null;
        try {
            url = new URL(u);
            url = new URL(url.getProtocol(), url.getHost(), url.getFile());
        } catch (MalformedURLException ex) {
            Logger.getLogger(ItemImporterDSLCItemServiceClientV1.class.getName()).log(Level.SEVERE, null, ex);
        }
        String newURL = url.toString();
        return newURL;
    }
}
