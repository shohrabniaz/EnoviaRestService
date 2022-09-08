package com.bjit.common.rest.app.service.enovia_pdm.utilities;

import com.bjit.common.rest.app.service.enovia_pdm.models.ParentChildModel;
import matrix.db.Context;
import matrix.util.SelectList;

import java.util.HashMap;
import java.util.List;

public interface IExpand {
//    HashMap<String, Map> getExpandedData(Context context, BusinessObject parentBO, List<String> busPatternList, List<String> relPatternList, String businessObjectWhereExpression, String relationshipWhereExpression, Short expandLevel, SelectList selectBusinessAttributeList, SelectList selectRelationAttributeList) throws Exception;
//    HashMap<String, Map> getExpandedData(Context context, BusinessObject parentBO, List<String> busPatternList, List<String> relPatternList, String businessObjectWhereExpression, String relationshipWhereExpression, Short expandLevel) throws Exception;

    HashMap<String, List<ParentChildModel>> expand(Context context, String rootObjectId, List<String> busPatternList, List<String> relPatternList, String businessObjectWhereExpression, String relationshipWhereExpression, Short expandLevel, SelectList selectBusinessAttributeList, SelectList selectRelationAttributeList) throws Exception;
//    HashMap<String, ChildData> getListMapOfAllItems ();
    ParentChildModel getRootItemInfo();
}
