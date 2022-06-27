package com.bjit.common.rest.app.service.controller.modelVersion.atoncontext;

import java.util.List;
import java.util.Map;

import com.bjit.common.code.utility.dsapi.portfolio.model.ResponseModel;

public interface ProductsItemCodeService {
    public abstract Map<String, Map<String,Boolean>> execute(ResponseModel response,  Map<String, List<String>> quesList, String source);
}
