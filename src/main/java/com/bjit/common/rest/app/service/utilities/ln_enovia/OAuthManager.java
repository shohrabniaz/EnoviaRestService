package com.bjit.common.rest.app.service.utilities.ln_enovia;

import com.bjit.ewc18x.utils.PropertyReader;
import com.microsoft.aad.msal4j.ClientCredentialFactory;
import com.microsoft.aad.msal4j.ClientCredentialParameters;
import com.microsoft.aad.msal4j.ConfidentialClientApplication;
import com.microsoft.aad.msal4j.IAuthenticationResult;
import java.net.MalformedURLException;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 *
 * @author BJIT
 */
public class OAuthManager {
    private final ConfidentialClientApplication app;
    
    public OAuthManager() throws MalformedURLException {
        this.app = configureApp();
    }
    
    private ConfidentialClientApplication configureApp() throws MalformedURLException {
        return ConfidentialClientApplication.builder(
                        PropertyReader.getProperty("ln.cost.service.client.id"),
                        ClientCredentialFactory.createFromSecret(PropertyReader.getProperty("ln.cost.service.client.secret")))
                        .authority(PropertyReader.getProperty("ln.cost.service.token.url"))
                        .build();
    }
    
    private CompletableFuture<IAuthenticationResult> getTokenProvider() {
        ClientCredentialParameters clientCredentialParameters = ClientCredentialParameters.builder(
                Collections.singleton(PropertyReader.getProperty("ln.cost.service.scope")))
                .build();
        
        return this.app.acquireToken(clientCredentialParameters);
    }
    
    public String getAccessToken() throws ExecutionException, InterruptedException {
        CompletableFuture<IAuthenticationResult> tokenProvider = getTokenProvider();
        String token = tokenProvider.get().accessToken();
        return token;
    }
}
