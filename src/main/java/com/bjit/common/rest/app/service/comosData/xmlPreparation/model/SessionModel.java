package com.bjit.common.rest.app.service.comosData.xmlPreparation.model;

import com.bjit.common.rest.app.service.comosData.xmlPreparation.model.comosxml.RFLP;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.utilServices.IComosFileWriter;
import com.bjit.common.rest.app.service.utilities.CommonUtilities;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j;
import matrix.db.Context;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Log4j
@Component
@RequestScope
public class SessionModel {
    @Autowired
    private CommonUtilities commonUtilities;

    @Autowired
    @Qualifier("ComosFileWriter")
    private IComosFileWriter fileWriter;

    @Setter
    private Context context;

    @Getter
    private String xmlMapFileName;

    @Getter
    private List<String> errorList;

    @Getter
    @Setter
    private Boolean doNotGenerateLogicalItemsXMLFile;

    @Getter
    @Setter
    private String projectSpace;

    public Context getContext(){
        return Optional.ofNullable(this.context).orElseGet(() -> {
            try {
                this.context = commonUtilities.generateContext();
                return this.context;
            } catch (Exception e) {
                log.error(e);
                throw new RuntimeException(e);
            }
        });
    }

    public void setXmlMapFileName(HashMap<String, RFLP> stringRFLPHashMap) {
        String xmlFileMapFileName = fileWriter.getXMLFileDirectory(stringRFLPHashMap
                .keySet()
                .stream()
                .findFirst()
                .get());
        this.xmlMapFileName = xmlFileMapFileName;
    }

    public void setErrorList(String error){
        Optional
                .ofNullable(errorList)
                .orElse(new ArrayList<>())
                .add(error);
    }

    public void setErrorList(List<String> errorList){
        Optional
                .ofNullable(errorList)
                .orElse(new ArrayList<>())
                .addAll(errorList);
    }
}
