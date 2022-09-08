package com.bjit.common.rest.app.service.controller.dataSecurity;

import com.bjit.common.code.utility.security.ContextPasswordSecurity;
import com.bjit.common.rest.app.service.payload.common_response.CustomResponseBuilder;
import com.bjit.common.rest.app.service.payload.common_response.IResponse;
import com.bjit.common.rest.app.service.payload.common_response.Status;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Log4j
@RestController
@RequestMapping(path = "/secured")
public class SecuredData {

    @Autowired
    @Qualifier("EncryptData")
    IEncryptData encryptData;

    @Autowired
    @Qualifier("CustomResponseBuilder")
    IResponse responseBuilder;

    @RequestMapping(value = "/encrypt/data", method = RequestMethod.POST, produces = "application/json", consumes = "application/json")
    @ResponseBody
    public ResponseEntity encryptCredentials(@RequestBody HashMap<String, String> toBeEncryptedMap) {
        try {
            log.debug("--------------- ||| Encryption Process has been started ||| ---------------");
            log.debug("###########################################################################");
            String buildResponse = responseBuilder.setData(encryptData.encryptData(toBeEncryptedMap)).setStatus(Status.OK).buildResponse();
            return new ResponseEntity<>(buildResponse, HttpStatus.OK);
        } catch (Exception exp) {
            log.error("Error occurred due to : " + exp.getMessage());
            String buildResponse = responseBuilder.addErrorMessage("Couldn't encrypt the data").setStatus(Status.FAILED).buildResponse();
            return new ResponseEntity<>(buildResponse, HttpStatus.NON_AUTHORITATIVE_INFORMATION);
        } finally {
            log.info("--------------- ||| Encryption Process has been stopped ||| ---------------");
            log.info("###########################################################################");
        }
    }
}
