package com.bjit.common.rest.pdm_enovia.bom.comparison.service.parse;

import java.lang.annotation.Annotation;
import java.nio.file.Paths;

import com.bjit.common.rest.pdm_enovia.bom.comparison.constant.Constant;
import com.bjit.common.rest.pdm_enovia.bom.comparison.exception.ParsingException;
import com.bjit.common.rest.pdm_enovia.bom.comparison.utility.JSONUtil;
import com.bjit.common.rest.pdm_enovia.bom.comparison.utility.PropertyUtil;
import com.fasterxml.jackson.databind.JsonNode;
/**
 * @author Ashikur Rahman / BJIT
 */
@ParsingInformation(type=Type.JSON,
sourceType=SourceType.FILE,
sourceInput="",
sourceDataModel=DataModel.class)
public interface ParsingService extends Parse<Object> {
	@Override
	public default Object parse() throws Exception {
		Class<?> clazz = this.getClass();
		Annotation annotation = clazz.getAnnotation(ParsingInformation.class);
		ParsingInformation parsingInformation = (ParsingInformation) annotation;
		Type type = parsingInformation.type();
		SourceType sourceType = parsingInformation.sourceType();
		String sourceInput = parsingInformation.sourceInput();
		Class<?> dataModel = parsingInformation.sourceDataModel();
		Object deserializedObject = null;
		JsonNode node = null;
		if (type == Type.JSON) {
			if (sourceType == SourceType.FILE) {
				node = JSONUtil.parseJSONFile(Paths.get(sourceInput).toFile());
				deserializedObject = JSONUtil.fromJson(node, dataModel);
			} else if (sourceType == SourceType.STRING) {
				node = JSONUtil.parse(sourceInput);
				deserializedObject = JSONUtil.fromJson(node, dataModel);
			} else if (sourceType == SourceType.ENDPOINT) {
				if (sourceInput.equalsIgnoreCase("")) {
					sourceInput = PropertyUtil.getPropertyValue(Constant.BOM_EXPORT_URL);
					if (sourceInput == null)
						throw new ParsingException(Constant.ENDPOINT_EXCEPTION_MESSAGE);
				}
				node = JSONUtil.parse(JSONUtil.consumeHTTPResponse(sourceInput));
				deserializedObject = JSONUtil.fromJson(node, dataModel);
			}
		}
		return deserializedObject;
	}
}