package com.bjit.common.rest.app.service.controller.dataSecurity;

import java.util.HashMap;
import java.util.Map;

public interface IEncryptData {
    Map<String, String> encryptData(HashMap<String, String> toBeEncryptedMap);
}
