package com.bjit.common.rest.app.service;

import org.springframework.stereotype.Component;

/**
 *
 * @author BJIT
 */
@Component(value = "myappServiceWadl")
public final class MyAppServiceWadl {

 /*   private static final String APP_NAME = "myapp";

    @Autowired
    private RequestMappingHandlerMapping handlerMapping;

    public WadlApplication generateWadl(HttpServletRequest request) {
        WadlApplication result = new WadlApplication();
        WadlDoc doc = new WadlDoc();
        doc.setTitle("myapp Service WADL");
        result.getDoc().add(doc);
        WadlResources wadResources = new WadlResources();
        wadResources.setBase(getBaseUrl(request));

        Map<RequestMappingInfo, HandlerMethod> handletMethods = handlerMapping.getHandlerMethods();
        for (Map.Entry<RequestMappingInfo, HandlerMethod> entry : handletMethods.entrySet()) {

            HandlerMethod handlerMethod = entry.getValue();
            RequestMappingInfo mappingInfo = entry.getKey();

            Set<String> pattern = mappingInfo.getPatternsCondition().getPatterns();
            if (!pattern.contains("/home/application.wadl")) {
                
            }
            
                ) {

WadlResource wadlResource = new WadlResource();
                Set<RequestMethod> httpMethods = mappingInfo.getMethodsCondition().getMethods();
                ProducesRequestCondition producesRequestCondition = mappingInfo.getProducesCondition();
                Set<MediaType> mediaTypes = producesRequestCondition.getProducibleMediaTypes();

                for (RequestMethod httpMethod : httpMethods) {
                    WadlMethod wadlMethod = new WadlMethod();

                    for (String uri : pattern) {
                        wadlResource.setPath(uri);
                    }

                    wadlMethod.setName(httpMethod.name());
                    Method javaMethod = handlerMethod.getMethod();
                    wadlMethod.setId(javaMethod.getName());
                    WadlDoc wadlDocMethod = new WadlDoc();
                    wadlDocMethod.setTitle(javaMethod.getDeclaringClass().getName() + "." + javaMethod.getName());
                    wadlMethod.getDoc().add(wadlDocMethod);

                    // Request
                    WadlRequest wadlRequest = new WadlRequest();

                    Annotation[][] annotations = javaMethod.getParameterAnnotations();
                    for (Annotation[] annotation : annotations) {
                        for (Annotation annotation2 : annotation) {
                            if (annotation2 instanceof RequestParam) {
                                RequestParam param2 = (RequestParam) annotation2;
                                WadlParam waldParam = new WadlParam();
                                waldParam.setName(param2.value());
                                waldParam.setStyle(WadlParamStyle.QUERY);
                                waldParam.setRequired(param2.required());*/
                                /*
* String defaultValue =
* cleanDefault(param2.defaultValue()); if (
* !defaultValue.equals("") ) {
* waldParam.setDefault(defaultValue); }
                                 */
  /*                              wadlRequest.getParam().add(waldParam);
                            } else if (annotation2 instanceof PathVariable) {
                                PathVariable param2 = (PathVariable) annotation2;
                                WadlParam waldParam = new WadlParam();
                                waldParam.setName(param2.value());
                                waldParam.setStyle(WadlParamStyle.TEMPLATE);
                                waldParam.setRequired(true);
                                wadlRequest.getParam().add(waldParam);
                            }
                        }
                    }
                    if (!wadlRequest.getParam().isEmpty()) {
                        wadlMethod.setRequest(wadlRequest);
                    }

                    // Response
                    if (!mediaTypes.isEmpty()) {
                        WadlResponse wadlResponse = new WadlResponse();
                        wadlResponse.getStatus().add(200l);
                        for (MediaType mediaType : mediaTypes) {
                            WadlRepresentation wadlRepresentation = new WadlRepresentation();
                            wadlRepresentation.setMediaType(mediaType.toString());
                            wadlResponse.getRepresentation().add(wadlRepresentation);
                        }
                        wadlMethod.getResponse().add(wadlResponse);
                    }

                    wadlResource.getMethodOrResource().add(wadlMethod);

                }

                wadResources.getResource().add(wadlResource);
            }
        }
        result.getResources().add(wadResources);

        return result;
    }

    private String getBaseUrl(HttpServletRequest request) {
        String requestUri = request.getRequestURI();
        return request.getScheme() + "://" + request.getServerName() + ":"
                + request.getServerPort() + "/" + APP_NAME;
    }*/

    /*    private String cleanDefault(String value) {
value = value.replaceAll("\t", "");
value = value.replaceAll("\n", "");
value = value.replaceAll("?", "");
value = value.replaceAll("?", "");
value = value.replaceAll("?", "");
return value;
    }*/
}
