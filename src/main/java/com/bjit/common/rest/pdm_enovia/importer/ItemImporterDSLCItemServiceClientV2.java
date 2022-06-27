package com.bjit.common.rest.pdm_enovia.importer;

import com.bjit.common.code.utility.dslc.model.ItemCreationSkeleton;
import com.bjit.common.code.utility.dslc.service.duplicate.item.BasicContextLoadingService;
import com.bjit.common.code.utility.dslc.service.duplicate.item.DSLCItemService;
import com.bjit.common.code.utility.dslc.service.duplicate.item.ItemCreationService;
import com.bjit.common.code.utility.dslc.service.duplicate.item.SecurityContextLoadingService;
import com.bjit.common.code.utility.http.client.CSRFTokenGenerationService;
import java.util.List;
import java.util.Map;
import org.apache.http.util.Args;
import org.apache.log4j.Logger;

/**
 *
 * @author Tohidul Islam
 */
public class ItemImporterDSLCItemServiceClientV2 {
    private static final Logger LOGGER = Logger.getLogger(ItemImporterDSLCItemServiceClientV2.class);

    private static String SOURCE = "3DSpace";
    private DSLCItemServiceResponseParser resultParser;
    //private String objectType;
    private String hostUrl;
    private String passportRootUrl;
    private String username;
    private String password;
    private String securityContext;
    private CSRFTokenGenerationService csrftg;
    private DSLCItemService dslcItemService;

    public ItemImporterDSLCItemServiceClientV2(String hostUrl, String passportRootUrl, String username, String password,
            String securityContext) {
        Args.notNull(hostUrl, "hostUrl can not be null");
        Args.notNull(passportRootUrl, "passportRootUrl can not be null");
        Args.notNull(username, "username can not be null");
        Args.notNull(password, "password can not be null");
        this.hostUrl = hostUrl;
        this.passportRootUrl = passportRootUrl;
        this.username = username;
        this.password = password;
        this.securityContext = securityContext;

        /* 
         * eager initialization, cause this initialized object will be passed to the 
         * next call stack and will be reused,
         */
        this.csrftg = new CSRFTokenGenerationService(this.hostUrl, this.passportRootUrl,
                this.username, this.password);//now authenticated, http client contains CSRF token
        this.dslcItemService = new DSLCItemService(this.csrftg);
        SecurityContextLoadingService scls = new BasicContextLoadingService(this.securityContext);
        this.dslcItemService.setSecurityContextLoadingService(scls);

        this.resultParser = new SimpleDSLCItemServiceResponseParserForItemImport();
    }

    public String call(String objectType, String objectId) throws Exception {
        Args.notBlank(objectType, "Object Type string");
        Args.notBlank(objectType, "Object PhysicalId string");
        LOGGER.info(objectType + " type object going to be created from skeleton(id)" + objectId);
        ItemCreationSkeleton itemCreationSkeleton = new ItemCreationSkeleton();
        itemCreationSkeleton.setSingleSkeletonData(objectId, objectType, SOURCE);
        ItemCreationService itemCreationService = new ItemCreationService(this.csrftg);
        itemCreationService.setItemCreationSkeleton(itemCreationSkeleton);

        dslcItemService.setItemService(itemCreationService);
        Map<String, List<String>> responseMap = dslcItemService.execute();
        LOGGER.info("DSLC response is:" + responseMap);
        String createdItemPhysicalId = this.resultParser.parse(responseMap);
        System.out.println("Newly Created Item details- Type:" + objectType + ",id:" + createdItemPhysicalId);
        return createdItemPhysicalId;
    }

//    public void closeClient() {
//        this.dslcItemService.closeClient();
//    }
}
