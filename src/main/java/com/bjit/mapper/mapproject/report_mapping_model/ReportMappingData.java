/**
 *
 */
package com.bjit.mapper.mapproject.report_mapping_model;


import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.log4j.Logger;

/**
 * @author TAREQ SEFATI
 *
 */
public class ReportMappingData {

    private static final Logger Log = Logger.getLogger(ReportMappingData.class);
    private ReportMapper reportMapper = null;

    public ReportMappingData() {
        getData();
    }

    private void getData() {
        try {
            JAXBContext context = JAXBContext.newInstance(ReportMapper.class);
            Unmarshaller um = context.createUnmarshaller();
            reportMapper = (ReportMapper) um.unmarshal(getClass().getClassLoader().getResourceAsStream("mapper_files/report_param.xml"));
        } catch (JAXBException e) {
            Log.debug("JAXBException : " + e.getMessage());
            e.printStackTrace();
        }
    }

    public String getLevel() {
        Log.debug("Expand level: "+reportMapper.getLevel());
        return reportMapper.getLevel();
    }

    public String getTemplateLang() {
        Log.debug("Template Language: "+reportMapper.getTemplateLang());
        return reportMapper.getTemplateLang();
    }

    public String getReportFormat() {
        Log.debug("Report format: "+reportMapper.getFormat());
        return reportMapper.getFormat();
    }

    public boolean isPrintStartEndPage() {
        Log.debug("Print start & end page: "+reportMapper.isPrintStartEndPage());
        return reportMapper.isPrintStartEndPage();
    }
}
