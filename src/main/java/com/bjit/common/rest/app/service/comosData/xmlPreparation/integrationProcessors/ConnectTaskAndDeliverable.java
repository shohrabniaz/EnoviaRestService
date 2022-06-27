package com.bjit.common.rest.app.service.comosData.xmlPreparation.integrationProcessors;

import com.bjit.common.rest.app.service.comosData.xmlPreparation.model.SessionModel;
import com.bjit.common.rest.app.service.model.tnr.TNR;
import com.bjit.common.rest.app.service.utilities.CommonSearch;
import matrix.db.BusinessObject;
import matrix.db.Relationship;
import matrix.db.RelationshipType;
import matrix.util.MatrixException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.RequestScope;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Service
@RequestScope
public class ConnectTaskAndDeliverable implements IConnectTaskAndDeliverable {
    @Autowired
    CommonSearch commonSearch;

    @Autowired
    SessionModel sessionModel;

    @Autowired
    BeanFactory beanFactory;

    @Override
    public String searchItem(String type, String name) throws Exception {

        TNR tnr = getTnr(type, name);
        List<HashMap<String, String>> searchedItem = commonSearch.searchItem(sessionModel.getContext(), tnr);

        return searchedItem
                .stream()
                .filter(size -> !size.isEmpty())
                .findFirst()
                .get()
                .get("id");
    }

    private TNR getTnr(String type, String name) {
        TNR tnr = beanFactory.getBean(TNR.class);
        tnr.setType(type);
        tnr.setName(name);
        return tnr;
    }

    @Override
    public Boolean connectItem(String taskId, String logicalItemId) throws MatrixException {
        BusinessObject businessObject = new BusinessObject(taskId);
        RelationshipType task_deliverable = new RelationshipType("Task Deliverable");
        Relationship connect = businessObject.connect(sessionModel.getContext(), task_deliverable, Boolean.TRUE, new BusinessObject(logicalItemId));
        return Optional.ofNullable(connect.getPhysicalId(sessionModel.getContext())).isPresent();
    }
}
