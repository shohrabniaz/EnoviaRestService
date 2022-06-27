package com.bjit.common.rest.pdm_enovia.bom.comparison.utility;

import java.io.File;
import java.io.IOException;
import java.nio.charset.UnsupportedCharsetException;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import com.bjit.common.rest.pdm_enovia.bom.comparison.data.model.request.HTTPRequestObjects;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
/**
 * @author Ashikur Rahman / BJIT
 */
public class JSONUtil {
	private static ObjectMapper objectMapper = getDefaultObjectMapper();
	
	private static ObjectMapper getDefaultObjectMapper() {
		ObjectMapper defaultObjectMapper = new ObjectMapper();
		defaultObjectMapper.registerModule(new JavaTimeModule());
		defaultObjectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		return defaultObjectMapper;
	}
	
	/**
	 * @param src
	 * @return
	 * @throws IOException
	 */
	public static JsonNode parse(String src) throws IOException {
		return objectMapper.readTree(src);
	}
	
	/**
	 * @param jsonSrcFile
	 * @return
	 * @throws IOException
	 */
	public static JsonNode parseJSONFile(File jsonSrcFile) throws IOException {
		return objectMapper.readTree(jsonSrcFile);
	}
	
	/**
	 * @param node
	 * @param clazz
	 * @return
	 * @throws JsonProcessingException
	 */
	public static <A> A fromJson(JsonNode node, Class<A> clazz) throws JsonProcessingException {
		return objectMapper.treeToValue(node, clazz);
	}
	
	/**
	 * @param object
	 * @return
	 */
	public static JsonNode toJson(Object object) {
		return objectMapper.valueToTree(object);
	}
	
	/**
	 * @param node
	 * @return
	 * @throws JsonProcessingException
	 */
	public static String stringify(JsonNode node) throws JsonProcessingException {
		return generateString(node, false);
	}
	
	/**
	 * @param node
	 * @return
	 * @throws JsonProcessingException
	 */
	public static String prettyPrint(JsonNode node) throws JsonProcessingException {
		return generateString(node, true);
	}
	
	/**
	 * @param node
	 * @param isPretty
	 * @return
	 * @throws JsonProcessingException
	 */
	public static String generateString(JsonNode node, boolean isPretty) throws JsonProcessingException {
		ObjectWriter objWriter = objectMapper.writer();
		if(isPretty)
			objWriter = objWriter.with(SerializationFeature.INDENT_OUTPUT);
		return objWriter.writeValueAsString(node);
	}
	
	/**
	 * @param url
	 * @return
	 * @throws UnsupportedCharsetException
	 * @throws IOException
	 */
	public static String consumeHTTPResponse(String url) throws UnsupportedCharsetException, IOException {
		String consumedData = "";
		CloseableHttpClient client = HttpClients.createDefault();
		HttpPost httpPost = new HttpPost(url);
		Object requestBody = HTTPRequestObjects.getRequestBody();
		Map<String, String> headers = HTTPRequestObjects.getRequestHeaders();
		CloseableHttpResponse response = null;
		HttpEntity entity = null;
		if (requestBody != null || requestBody != "") {
			StringEntity stringEntity = new StringEntity(prettyPrint(toJson(requestBody)), ContentType.APPLICATION_FORM_URLENCODED);
			httpPost.setEntity(stringEntity);
		}
		for (Map.Entry<String,String> header : headers.entrySet())
			httpPost.addHeader(header.getKey(), header.getValue());
		response = client.execute(httpPost);
		entity = response.getEntity();
		if(entity != null) {
			consumedData = EntityUtils.toString(entity);
		}
		client.close();
		return consumedData;
	}
}