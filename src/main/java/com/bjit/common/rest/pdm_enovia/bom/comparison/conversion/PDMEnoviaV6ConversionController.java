package com.bjit.common.rest.pdm_enovia.bom.comparison.conversion;

import com.bjit.common.rest.pdm_enovia.bom.comparison.constant.Constant;
import com.bjit.common.rest.pdm_enovia.bom.comparison.data.model.pdm.PDMResponse;
import com.bjit.common.rest.pdm_enovia.bom.comparison.data.model.request.HTTPRequestObjects;
import com.bjit.common.rest.pdm_enovia.bom.comparison.service.conversion.Conversion;
import com.bjit.common.rest.pdm_enovia.bom.comparison.service.parse.ParsingInformation;
import com.bjit.common.rest.pdm_enovia.bom.comparison.service.parse.ParsingService;
import com.bjit.common.rest.pdm_enovia.bom.comparison.service.parse.SourceType;
import com.bjit.common.rest.pdm_enovia.bom.comparison.service.parse.Type;
import com.bjit.common.rest.pdm_enovia.bom.comparison.utility.PDMEnoviaV6Util;
import com.bjit.common.rest.pdm_enovia.bom.comparison.utility.PropertyUtil;
import com.bjit.ewc18x.utils.PropertyReader;
//import com.bjit.common.rest.pdm_enovia.bom.comparison.utility.PropertyUtil;

/**
 * @author Ashikur Rahman / BJIT
 */
@ParsingInformation(type = Type.JSON,
        sourceType = SourceType.ENDPOINT,
        sourceDataModel = PDMResponse.class)
public class PDMEnoviaV6ConversionController implements ParsingService, Conversion<Object> {

    public static Object pdmJSONDataParse() throws Exception {
        return (Object) new PDMEnoviaV6ConversionController().parse();
    }

    @Override
    public Object conversion(Object pdmRequestBody) throws Exception {
        if (PropertyUtil.getProperties() == null || PropertyUtil.getProperties().size() < 1) {
            PropertyUtil propertyUtil = new PropertyUtil();
            propertyUtil.loadPropertiesFile(this.getClass().getClassLoader().getResource(Constant.BOM_COMPARISON_PROPERTIES).toURI());
           // propertyUtil.loadEnvironment();
        }
        HTTPRequestObjects.setRequestBody(pdmRequestBody);
        HTTPRequestObjects.setRequestHeaders(PropertyUtil.getProperties());
        return PDMEnoviaV6Util.getEnoviaV6JSONAsString();
    }
}
