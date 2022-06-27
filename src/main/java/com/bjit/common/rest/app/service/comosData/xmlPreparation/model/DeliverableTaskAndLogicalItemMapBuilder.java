package com.bjit.common.rest.app.service.comosData.xmlPreparation.model;

import com.bjit.common.rest.app.service.comosData.xmlPreparation.model.comosEnovia.Deliverables;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Log4j
@Service
public class DeliverableTaskAndLogicalItemMapBuilder {
    @Autowired
    BeanFactory beanFactory;

    public DeliverableTaskAndLogicalItemMap getDeliverableTaskAndLogicalItemMap(String serviceName, String email, Object requestData, List<Deliverables> deliverablesOfTaskAndLogicalItem){
        DeliverableTaskAndLogicalItemMap bean = beanFactory.getBean(DeliverableTaskAndLogicalItemMap.class);
        bean.setServiceName(serviceName);
        bean.setEmail(email);
        bean.setRequestData(requestData);
        bean.setDeliverablesOfTaskAndLogicalItem(deliverablesOfTaskAndLogicalItem);

        return bean;
    }
}
