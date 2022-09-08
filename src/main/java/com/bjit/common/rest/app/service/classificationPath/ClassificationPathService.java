/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.app.service.classificationPath;

import com.bjit.common.rest.app.service.model.modelVersion.MVResponseMessageFormatterBean;
import java.util.List;
import matrix.db.Context;

/**
 *
 * @author Arifur Rahman
 */
public interface ClassificationPathService {

    public List<MVResponseMessageFormatterBean> addClassificationPath(Context context, List<MVResponseMessageFormatterBean> successFulItemList);

    public String addClassificationPath(Context context, String objectId, String classificationPath);
}
