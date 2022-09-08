package com.bjit.common.rest.app.service.dsservice.consumers;

import okhttp3.Response;
import com.bjit.common.rest.app.service.dsservice.models.csrf.SecurityContextResponseModel;
import java.net.MalformedURLException;
import java.util.HashMap;
import com.bjit.common.rest.app.service.dsservice.serviceclient.Method;
import com.bjit.common.rest.app.service.dsservice.models.sercuritycontext.PreferredCredentialsModel;
import com.bjit.common.rest.app.service.dsservice.stores.PropertyStore;
import com.bjit.ewc18x.utils.PropertyReader;

public class SecurityContextConsumer extends ConsumerModel<SecurityContextResponseModel> {

    @Override
    public SecurityContextResponseModel consume() throws Exception {
        Response response = getClientBuilder()
                .setUrl(getURL())
                .setMethod(Method.GET)
                .setParameters("current", "true")
                .setParameters("select", "preferredcredentials")
                .build();

        SecurityContextResponseModel csrfResponseModel = getResponseModel(response, SecurityContextResponseModel.class);;
        managePropertyStore(csrfResponseModel);

        return csrfResponseModel;
    }

    private void managePropertyStore(SecurityContextResponseModel securityContext) throws MalformedURLException {
        PreferredCredentialsModel preferredCredentials = securityContext.getPreferredcredentials();
        String cs = "ctx::" + preferredCredentials.getRole().getName() + "." + preferredCredentials.getOrganization().getName() + "." + preferredCredentials.getCollabspace().getName();

        HashMap<String, String> properties = new HashMap<>();
        properties.put("securityContext", cs);

        PropertyStore propertyStore = setProperties(properties);
        setPropertyStore(propertyStore);
    }

    @Override
    public String getURL() {
//        "https://dsd2v21xspace.plm.valmet.com/3dspace/resources/modeler/pno/person"
        String threedSpace = PropertyReader.getProperty("ds.service.base.url.3dspace");
        String csUrl = PropertyReader.getProperty("ds.service.url.security.context");
        String securityContextUrl = threedSpace + csUrl;
        return securityContextUrl;
    }
}
