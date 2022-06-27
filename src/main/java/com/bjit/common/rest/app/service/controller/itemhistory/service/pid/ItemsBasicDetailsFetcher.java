package com.bjit.common.rest.app.service.controller.itemhistory.service.pid;

import com.bjit.common.rest.app.service.controller.itemhistory.model.Data;
import matrix.db.Context;

import java.util.List;

public interface ItemsBasicDetailsFetcher {
    List<Data> fetch(List<Data> dataList, Context context) throws Exception;
}
