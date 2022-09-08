package com.bjit.common.rest.app.service.dsservice.consumers;

import com.bjit.common.rest.app.service.dsservice.models.csrf.CSRFTokenResponseModel;
import com.bjit.common.rest.app.service.dsservice.models.csrf.SecurityContextResponseModel;


public class ConsumerContainers {
    private IConsumer<SecurityContextResponseModel> securityContextConsumer;
    private IConsumer<CSRFTokenResponseModel> csrfTokenResponseModelIConsumer;

    /**
     * @return the securityContextConsumer
     */
    public IConsumer<SecurityContextResponseModel> getSecurityContextConsumer() {
        return securityContextConsumer;
    }

    /**
     * @param securityContextConsumer the securityContextConsumer to set
     */
    public void setSecurityContextConsumer(IConsumer<SecurityContextResponseModel> securityContextConsumer) {
        this.securityContextConsumer = securityContextConsumer;
    }

    /**
     * @return the csrfTokenResponseModelIConsumer
     */
    public IConsumer<CSRFTokenResponseModel> getCsrfTokenResponseModelIConsumer() {
        return csrfTokenResponseModelIConsumer;
    }

    /**
     * @param csrfTokenResponseModelIConsumer the csrfTokenResponseModelIConsumer to set
     */
    public void setCsrfTokenResponseModelIConsumer(IConsumer<CSRFTokenResponseModel> csrfTokenResponseModelIConsumer) {
        this.csrfTokenResponseModelIConsumer = csrfTokenResponseModelIConsumer;
    }
}
