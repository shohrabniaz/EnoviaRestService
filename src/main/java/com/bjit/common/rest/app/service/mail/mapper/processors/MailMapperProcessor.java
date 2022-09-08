/**
 *
 */
package com.bjit.common.rest.app.service.mail.mapper.processors;

import com.bjit.common.rest.app.service.mail.mapper.interfaces.IMapperProcessor;
import com.bjit.common.rest.app.service.mail.mapper.models.Application;
import com.bjit.common.rest.app.service.mail.mapper.models.MailTemplateMapper;
import com.bjit.common.rest.app.service.mail.mapper.models.Templates;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import javax.xml.bind.JAXBException;
import java.io.FileNotFoundException;
import java.util.HashMap;
import com.bjit.common.rest.app.service.mail.mapper.interfaces.IMailMapCacheManager;

/**
 * @author BJIT
 *
 */
@Component
@Scope(value = "prototype")
public class MailMapperProcessor implements IMapperProcessor {

    @Override
    public HashMap<String, Templates> processAttributeXMLMapper() throws Exception {

        try {
            IMailMapCacheManager mapperCacheManager = new MapperCacheMgr();
            MailTemplateMapper mapper = mapperCacheManager.getMapper();

            HashMap<String, Templates> appTemplateMap = getAppTemplateMap(mapper);

            return appTemplateMap;
        } catch (JAXBException | FileNotFoundException | RuntimeException exp) {
            throw exp;
        } catch (Exception exp) {
            throw exp;
        }
    }

    private HashMap<String, Templates> getAppTemplateMap(MailTemplateMapper mailTemplateMapper) {
        HashMap<String, Templates> appTemplateMap = new HashMap<>();
        mailTemplateMapper.getApplications().getApplicationList().stream().forEach((Application application) -> {

            appTemplateMap.put(application.getType(), application.getTemplates());
        });

        return appTemplateMap;
    }

//    @Override
//    public IMapperElementMemento getMapperElementMemento() {
//        return mapperElementMemento;
//    }
}
