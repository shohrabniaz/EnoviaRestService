package com.bjit.common.rest.app.service.dsservice.serviceclient;

import java.io.File;
import java.util.HashMap;
import java.util.List;

public class HttpClientModel {
    String url;
    String mediaType;
    HashMap<String, String> formField;
    HashMap<String, List<File>> fileList;
    HashMap<String, String> fieldData;
    Boolean isReadFromForm;
    HashMap<String, String> headers;
    String methodType;
    HashMap<String, String> parameters;
}
