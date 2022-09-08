package com.bjit.common.rest.app.service.controller.modelVersion.aton.classattributes.updateconfig;

import com.bjit.common.code.utility.aton.automation.AtributeMapping;
import com.bjit.common.code.utility.aton.automation.ClassAttributeSet;
import com.bjit.common.code.utility.aton.automation.ExtensionAttributeController;
import com.bjit.common.code.utility.aton.automation.LockedClearedWrite;
import com.bjit.common.code.utility.aton.automation.PersistClassificationMap;
import com.bjit.common.code.utility.aton.automation.PersistInJSONFile;
import com.bjit.common.code.utility.aton.automation.WriteInFile;
import com.bjit.ewc18x.utils.PropertyReader;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import com.bjit.common.code.utility.security.ContextPasswordSecurity;
import org.apache.log4j.Logger;

/**
 *
 * @author Touhidul Islam
 */
@RestController
@RequestMapping(path = "/aton")
public class ClassAtributesFetchController {

    private static final Logger LOGGER = Logger.getLogger(ClassAtributesFetchController.class);

    @ResponseBody
    @RequestMapping(value = "/classification/attributes", method = RequestMethod.GET, produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity generateClassificationAttriubutes(HttpServletRequest httpRequest, HttpServletResponse httpResponse) throws Exception {
        LOGGER.info(" +++++++++++++ generateClassificationAttriubutes ++++++++++++");
        String filePath = PropertyReader.getProperty("aton.automation.classattributes.path");
        String source = PropertyReader.getProperty("ds.service.base.url.3dspace").concat("3dspace");
        ContextPasswordSecurity contextPasswordSecurity = new ContextPasswordSecurity();
        String userId = contextPasswordSecurity.decryptPassword(PropertyReader.getProperty("context.name"));
        String password = contextPasswordSecurity.decryptPassword(PropertyReader.getProperty("context.pass"));

        LOGGER.info(source);
        LOGGER.info((userId == null || userId.isEmpty()) ? "context user name is empty." : "");
        LOGGER.info((password == null || password.isEmpty()) ? "context password is empty." : "");

        ExtensionAttributeController obj = new ExtensionAttributeController();
        AtributeMapping attributeMapping = null;
        try {
            //part1: fetching and writing the classes to file system
            Set<ClassAttributeSet> classAttributes = obj.forceAutomation(source, userId, password);
            attributeMapping = new AtributeMapping();
            attributeMapping.toMap(classAttributes);
            if (filePath != null) {
                WriteInFile writer = new LockedClearedWrite();
                PersistClassificationMap persistClassificationMap = new PersistInJSONFile(filePath, writer);
                boolean isPersistent = persistClassificationMap.persist(attributeMapping);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(PropertyReader.getProperty("aton.automation.classattributes.success"), HttpStatus.OK);
    }
}
