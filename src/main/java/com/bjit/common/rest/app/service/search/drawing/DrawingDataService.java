/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.app.service.search.drawing;


import com.bjit.common.rest.app.service.model.drawing.response.Info;
import com.bjit.mapper.mapproject.jsonOutput.Items;
import matrix.db.Context;
import matrix.util.MatrixException;

import java.util.List;

/**
 *
 * @author BJIT
 */
public interface DrawingDataService {
    Items getItemByObjectId(Context context, String objectId);
    List<Info> getProjectAndTaskInfo(Context context, String itemId);
}
