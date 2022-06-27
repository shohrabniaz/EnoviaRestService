package com.bjit.common.rest.app.service.dsservice.serviceclient;

import okhttp3.*;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.*;
import com.bjit.common.rest.app.service.dsservice.stores.Cookie;
import com.bjit.common.rest.app.service.dsservice.stores.CookieStore;
import com.bjit.common.rest.app.service.dsservice.consumers.IConsumer;
import com.bjit.ewc18x.utils.PropertyReader;
import java.util.concurrent.TimeUnit;

public class HttpClientBuilder {

    private static final org.apache.log4j.Logger LOGGER = org.apache.log4j.Logger.getLogger(HttpClientBuilder.class);

    OkHttpClient client;
    MediaType mediaType;
    HttpClientModel httpClientModel;
    MultipartBody.Builder multipartBodyBuilder;
    CookieStore cookieStore;
    String bodyData;

    public HttpClientBuilder() {
        this.client = new OkHttpClient()
                .newBuilder()
                .readTimeout(Integer.parseInt(PropertyReader.getProperty("ds.service.request.timeout.in.milliseconds")), TimeUnit.MILLISECONDS) 
                .build();
        httpClientModel = new HttpClientModel();
    }

    public CookieStore getCookieStore() {
        return this.cookieStore;
    }

    public HttpClientBuilder setCookieStore(CookieStore cookieStore) {
        this.cookieStore = cookieStore;
        return this;
    }

    public HttpClientBuilder setUrl(String url) {
        this.httpClientModel.url = url;
        return this;
    }

    public String getBodyData() {
        return bodyData;
    }

    public HttpClientBuilder setBodyData(String bodyData) {
        this.bodyData = bodyData;
        return this;
    }

    public HttpClientBuilder setFormData(HashMap<String, String> formField) {
        this.httpClientModel.formField = formField;
        return this;
    }

    public HttpClientBuilder setFormData(String name, String value) {
        this.httpClientModel.formField = Optional.ofNullable(this.httpClientModel.formField).filter(formField -> !formField.isEmpty()).orElse(new HashMap<>());
        this.httpClientModel.formField.put(name, value);
        return this;
    }

    public HttpClientBuilder fileUpload(HashMap<String, List<File>> filesMap) {
        Optional.ofNullable(this.httpClientModel.fileList).filter(existingFiles -> !existingFiles.isEmpty())
                .ifPresentOrElse(
                        (HashMap<String, List<File>> existingFiles) -> {
                            existingFiles.putAll(filesMap);
                        },
                        () -> this.httpClientModel.fileList = filesMap);

        return this;
    }

    public HttpClientBuilder fileUpload(String parameterName, List<File> fileList) {
        Optional.ofNullable(this.httpClientModel.fileList).filter(existingFileList -> !existingFileList.isEmpty())
                .ifPresentOrElse(
                        ((HashMap<String, List<File>> existingFileList) -> {
                            if (existingFileList.containsKey(parameterName)) {
                                existingFileList.get(parameterName).addAll(fileList);
                            } else {
                                existingFileList.put(parameterName, fileList);
                            }
                        }),
                        () -> {
                            this.httpClientModel.fileList = new HashMap<>();
                            this.httpClientModel.fileList.put(parameterName, fileList);
                        });
        return this;
    }

    public HttpClientBuilder fileUpload(String filedName, File file) {
        this.httpClientModel.fileList = Optional.ofNullable(this.httpClientModel.fileList).orElseGet(() -> new HashMap<>());
        Optional.of(this.httpClientModel.fileList).filter(uploadableFile -> !uploadableFile.isEmpty()).ifPresentOrElse((uploadableFileMap) -> {
            List<File> fileList = uploadableFileMap.get(filedName);
            Optional.ofNullable(fileList).filter(files -> !files.isEmpty()).ifPresentOrElse((files) -> files.add(file), () -> {
                List<File> listOfFiles = new ArrayList<>();
                fileList.add(file);
                this.httpClientModel.fileList.put(filedName, listOfFiles);
            });
        }, () -> {
            List<File> fileList = new ArrayList<>();
            fileList.add(file);
            this.httpClientModel.fileList.put(filedName, fileList);
        });
        return this;
    }

    public HttpClientBuilder readFromForm(Boolean isReadFromForm) {
        this.httpClientModel.isReadFromForm = isReadFromForm;
        return this;
    }

    public HttpClientBuilder setMediaType(String mediaType) {
        this.httpClientModel.mediaType = mediaType;
        return this;
    }

    public HttpClientBuilder setFieldData(HashMap<String, String> fieldData) {
        this.httpClientModel.fieldData = fieldData;
        return this;
    }

    public HttpClientBuilder setFieldData(String name, String value) {
        this.httpClientModel.fieldData = Optional.ofNullable(this.httpClientModel.fieldData).filter(fieldData -> !fieldData.isEmpty()).orElse(new HashMap<>());
        this.httpClientModel.fieldData.put(name, value);
        return this;
    }

    public HttpClientBuilder setHeaders(HashMap<String, String> headers) {
        this.httpClientModel.headers = headers;
        return this;
    }

    public HttpClientBuilder setHeaders(String name, String value) {
        this.httpClientModel.headers = Optional.ofNullable(this.httpClientModel.headers).filter(headers -> !headers.isEmpty()).orElse(new HashMap<>());
        this.httpClientModel.headers.put(name, value);
        return this;
    }

    public HttpClientBuilder setMethod(String methodType) {
        this.httpClientModel.methodType = methodType;
        return this;
    }

    public HttpClientBuilder setParameters(HashMap<String, String> parameters) {
        this.httpClientModel.parameters = parameters;
        return this;
    }

    public HttpClientBuilder setParameters(String name, String value) {
        this.httpClientModel.parameters = Optional.ofNullable(this.httpClientModel.parameters).filter(parameters -> !parameters.isEmpty()).orElse(new HashMap<>());
        this.httpClientModel.parameters.put(name, value);
        return this;
    }

    public Response build() throws IOException {
        try {
            httpClientModel.mediaType = Optional.ofNullable(httpClientModel.mediaType).filter(mediaType -> !mediaType.isEmpty()).orElse(MethodTypes.JSON);
            setParameters();

            RequestBody requestBody
                    = (this.bodyData != null && !this.bodyData.isEmpty()) ? RequestBody.create(mediaType, this.bodyData)
                    : Optional.ofNullable(httpClientModel.isReadFromForm).orElse(false) ? setFormFields() : setFields();

            Request.Builder requestBuilder = new Request.Builder()
                    .url(httpClientModel.url)
                    .method(httpClientModel.methodType, requestBody);
            setCookies(requestBuilder);
            setHeaders(requestBuilder);
            Request request = requestBuilder.build();

            Response response = client.newCall(request).execute();

            return response;
        } catch (Exception exp) {
            LOGGER.error("Exception From : " + httpClientModel.url);
            LOGGER.error(exp);
            throw exp;
        }
    }

    private Request.Builder setHeaders(Request.Builder requestBuilder) {
        Optional.ofNullable(httpClientModel.headers).filter(headers -> !headers.isEmpty()).ifPresent((HashMap<String, String> requestHeaders) -> {
            requestHeaders.forEach((key, value) -> {
                requestBuilder.addHeader(key, value);
            });
        });

        return requestBuilder;
    }

    private Request.Builder setCookies(Request.Builder requestBuilder) throws MalformedURLException {

        Optional.ofNullable(this.cookieStore).ifPresent((cookieStore) -> {
            HashMap<String, List<Cookie>> allCookies = this.cookieStore.getCookies();

            Optional.ofNullable(allCookies).filter(cookies -> !cookies.isEmpty()).ifPresent((cookies) -> {
                try {
                    List<Cookie> specifiedSiteCookies = cookies.get(IConsumer.getBaseFromURL(httpClientModel.url));
                    Optional.ofNullable(specifiedSiteCookies).filter(siteCookies -> !siteCookies.isEmpty()).ifPresent((List<Cookie> cookieList) -> {
                        StringJoiner cookieJoiner = new StringJoiner("; ");
                        cookieList.forEach((Cookie cookie) -> {
                            cookieJoiner.add(cookie.getName() + "=" + cookie.getValue().split(";\\s", 2)[0]);
                        });

                        requestBuilder.addHeader("Cookie", cookieJoiner.toString());
                    });
                } catch (MalformedURLException exp) {
                    LOGGER.error(exp.getMessage());
                    throw new RuntimeException(exp);
                }
            });

        });

        return requestBuilder;
    }

    private void setParameters() {
        Optional.ofNullable(this.httpClientModel.parameters).filter(parameters -> !parameters.isEmpty()).ifPresent((parameters) -> {
            StringJoiner parameterJoiner = new StringJoiner("&");
            parameters.forEach((key, value) -> {
                parameterJoiner.add(key + "=" + value);
            });
            httpClientModel.url += "?" + parameterJoiner;
        });
    }

    private RequestBody setFields() {
        StringJoiner fieldDataJoiner = new StringJoiner("&");
        Optional.ofNullable(this.httpClientModel.fieldData).filter(fieldData -> !fieldData.isEmpty()).ifPresent((fieldData) -> {
            httpClientModel.mediaType = MethodTypes.URL_ENCODED;
            fieldData.forEach((key, value) -> {
                fieldDataJoiner.add(key + "=" + value);
            });
        });

        RequestBody body = fieldDataJoiner.length() > 0 ? RequestBody.create(mediaType, fieldDataJoiner.toString()) : (isItPostType() ? RequestBody.create(mediaType, "") : null);
        return body;
    }

    private Boolean isItPostType() {
        return this.httpClientModel.methodType.equalsIgnoreCase("put");
    }

    private RequestBody setFormFields() {
        Optional.ofNullable(this.httpClientModel.formField).filter(formFields -> !formFields.isEmpty()).ifPresent((HashMap<String, String> formFields) -> {
            multipartBodyBuilder = new MultipartBody.Builder().setType(MultipartBody.FORM);
            httpClientModel.mediaType = MethodTypes.JSON;

            formFields.forEach((key, value) -> {
                multipartBodyBuilder.addFormDataPart(key, value);
            });
        });

        Optional.ofNullable(this.httpClientModel.fileList).filter(fileList -> !fileList.isEmpty()).ifPresent((HashMap<String, List<File>> filesMap) -> {
            multipartBodyBuilder = Optional.ofNullable(multipartBodyBuilder).orElseGet(() -> new MultipartBody.Builder().setType(MultipartBody.FORM));
            httpClientModel.mediaType = MethodTypes.JSON;

            filesMap.forEach((key, fileList) -> {
                fileList.forEach((File uploadableFile) -> {
                    multipartBodyBuilder.addFormDataPart(key, uploadableFile.getName(), RequestBody.create(MediaType.parse(MethodTypes.OCTET_STREAM), uploadableFile));
                });
            });
        });

        RequestBody body = multipartBodyBuilder.build();
        return body;
    }
}
