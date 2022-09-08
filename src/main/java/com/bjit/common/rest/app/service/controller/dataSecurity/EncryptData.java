package com.bjit.common.rest.app.service.controller.dataSecurity;

import com.bjit.common.code.utility.security.ContextPasswordSecurity;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Log4j
@Service
@Qualifier("EncryptData")
public class EncryptData implements IEncryptData
{
    @Override
    public Map<String, String> encryptData(HashMap<String, String> toBeEncryptedMap){
        ContextPasswordSecurity contextPasswordSecurity = new ContextPasswordSecurity();
//                Map<String, String> encryptedMap = toBeEncryptedList
//                        .stream()
//                        .collect(Collectors.toMap(Function.identity(), data -> {
//                            try {
//                                return contextPasswordSecurity.encryptPassword(data);
//                            } catch (Exception e) {
//                                AUTHENTICATION_CONTROLLER_LOGGER.error(e);
//                                throw new RuntimeException(e);
//                            }
//                        }));

        return toBeEncryptedMap.keySet().stream().collect(Collectors.toMap(key -> key, key -> {
            try {
                return contextPasswordSecurity.encryptPassword(toBeEncryptedMap.get(key));
            } catch (Exception e) {
                log.error(e);
                throw new RuntimeException(e);
            }
        }));
    }
}
