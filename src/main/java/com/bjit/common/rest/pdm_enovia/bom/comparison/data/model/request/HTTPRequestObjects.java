package com.bjit.common.rest.pdm_enovia.bom.comparison.data.model.request;

import java.util.Map;
/**
 * @author Ashikur Rahman / BJIT
 */
public class HTTPRequestObjects {
	
	private static Object requestBody;
	private static Map<String, String> requestHeaders;
	private static Map<String, String> requestParams;
	
	public static Object getRequestBody() {
		return requestBody;
	}
	public static void setRequestBody(Object requestBody) {
		HTTPRequestObjects.requestBody = requestBody;
	}
	public static Map<String, String> getRequestHeaders() {
		return requestHeaders;
	}
	public static void setRequestHeaders(Map<String, String> requestHeaders) {
		HTTPRequestObjects.requestHeaders = requestHeaders;
	}
	public static Map<String, String> getRequestParams() {
		return requestParams;
	}
	public static void setRequestParams(Map<String, String> requestParams) {
		HTTPRequestObjects.requestParams = requestParams;
	}
}