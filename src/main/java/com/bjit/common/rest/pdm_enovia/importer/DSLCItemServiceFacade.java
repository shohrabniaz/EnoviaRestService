package com.bjit.common.rest.pdm_enovia.importer;

import java.util.List;
import java.util.Map;

import com.bjit.common.code.utility.dslc.model.ItemCreationSkeleton;
import com.bjit.common.code.utility.dslc.service.duplicate.item.BasicContextLoadingService;
import com.bjit.common.code.utility.dslc.service.duplicate.item.DSLCItemService;
import com.bjit.common.code.utility.dslc.service.duplicate.item.ItemCreationService;
import com.bjit.common.code.utility.dslc.service.duplicate.item.SecurityContextLoadingService;
import com.bjit.common.code.utility.http.client.CSRFTokenGenerationService;
import org.apache.http.util.Args;

/**
 * decoupling client regarding client call details
 *
 * @author Tohidul-571
 *
 */
class DSLCItemServiceFacade {

    private String hostUrl;
    private String passportRootUrl;
    private String username;
    private String password;
    private String securityContext;

    /**
     * @param hostUrl
     * @param passportRootUrl
     * @param username
     * @param password
     */
    public DSLCItemServiceFacade(String hostUrl, String passportRootUrl, String username, String password,
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
    }

    public Map<String, List<String>> call(String objectId, String objectType, String SOURCE) {
        
        /*
         * CSRFTokenGenerationService csrftg = new CSRFTokenGenerationService(
         * "https://dsd2v21xspace.plm.valmet.com/3dspace",
         * "https://dsd2v21xpassport.plm.valmet.com/3dpassport", "coe----", "------");
         */
        CSRFTokenGenerationService csrftg = new CSRFTokenGenerationService(this.hostUrl, this.passportRootUrl,
                this.username, this.password);
        DSLCItemService dslcItemService = new DSLCItemService(csrftg);
        /*
         * we can also set which Security context will be used if we do not want to use
         * default one provided by commoncode service. using default one could be
         * errornous. it is recomended to use basic one. following is the way to do it.
         */
        SecurityContextLoadingService scls = new BasicContextLoadingService(this.securityContext);
        dslcItemService.setSecurityContextLoadingService(scls);

        ItemCreationSkeleton itemCreationSkeleton = new ItemCreationSkeleton();
        itemCreationSkeleton.setSingleSkeletonData(objectId, objectType, SOURCE);
        ItemCreationService itemCreationService = new ItemCreationService(csrftg);
        itemCreationService.setItemCreationSkeleton(itemCreationSkeleton);

        dslcItemService.setItemService(itemCreationService);

        System.out.println("+++++++++++++++++ Going to Execute Service++++++++++++");
        Map<String, List<String>> responseMap = dslcItemService.execute();
        return responseMap;

    }

    /**
     * @return the hostUrl
     */
    public String getHostUrl() {
        return hostUrl;
    }

    /**
     * @return the passportRootUrl
     */
    public String getPassportRootUrl() {
        return passportRootUrl;
    }

    /**
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * @return the securityContext
     */
    public String getSecurityContext() {
        return securityContext;
    }

    /**
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * @param hostUrl the hostUrl to set
     */
    public void setHostUrl(String hostUrl) {
        this.hostUrl = hostUrl;
    }

    /**
     * @param passportRootUrl the passportRootUrl to set
     */
    public void setPassportRootUrl(String passportRootUrl) {
        this.passportRootUrl = passportRootUrl;
    }

    /**
     * @param password the password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * @param securityContext the securityContext to set
     */
    public void setSecurityContext(String securityContext) {
        this.securityContext = securityContext;
    }

    /**
     * @param username the username to set
     */
    public void setUsername(String username) {
        this.username = username;
    }
}
