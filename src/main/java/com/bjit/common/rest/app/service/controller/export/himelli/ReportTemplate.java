package com.bjit.common.rest.app.service.controller.export.himelli;

import java.util.Set;

public interface ReportTemplate {

    public String addHeader() throws Exception;

    public String addBody() throws Exception;

    public String addFooter() throws Exception;

    public void setNotIncludedAttributes(Set<Attribute> notIncludedAttributes);
}
