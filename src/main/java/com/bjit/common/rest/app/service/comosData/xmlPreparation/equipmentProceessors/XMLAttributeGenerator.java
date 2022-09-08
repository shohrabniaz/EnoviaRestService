package com.bjit.common.rest.app.service.comosData.xmlPreparation.equipmentProceessors;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.model.comosxml.Attribute;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.model.comosxml.Mandatory;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.model.comosxml.TextGraphicProperties;
import java.util.ArrayList;
import java.util.List;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.model.comosxml.RFLVPMItem;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.equipmentProceessors.constants.DefaultConstants;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.model.comosxml.BoundingBox;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.model.comosxml.Relation;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.model.comosxml.RFLP;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.model.comosxml.LogicalReference;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.model.comosxml.LogicalInstance;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.model.comosxml.LogicalPort;

@Component
public class XMLAttributeGenerator {
    @Autowired
    BeanFactory beanFactory;

    public Attribute getXmlAttribute(String value) {
        Attribute attribute = beanFactory.getBean(Attribute.class);
        attribute.setType("String");
        attribute.setValue(value);
        return attribute;
    }

    public Attribute getXmlAttribute(String id, String value) {
        Attribute attribute = getXmlAttribute(value);
        attribute.setId(id);
        return attribute;
    }

    public Attribute getAttribute(String type, String value) {
        Attribute attribute = beanFactory.getBean(Attribute.class);
        attribute.setType(type);
        attribute.setValue(value);
        return attribute;
    }

    public TextGraphicProperties getTextGraphicProperties(String displayName) {
        TextGraphicProperties textGraphicProperties = new TextGraphicProperties();
        textGraphicProperties.setDisplayName(displayName);
        return textGraphicProperties;
    }

    public RFLVPMItem getCommonAttributes(RFLVPMItem rflvpmItem) {
//        rflvpmItem.setOwner(getXmlAttribute(DefaultConstants.OWNER));
//        rflvpmItem.setRevision(getXmlAttribute("2", DefaultConstants.REVISION));
//        rflvpmItem.setPolicy(getXmlAttribute(DefaultConstants.POLICY));
//        rflvpmItem.setCurrent(getXmlAttribute(DefaultConstants.CURRENT));
//        rflvpmItem.setTextGraphicProperties(getTextGraphicProperties(DefaultConstants.DISPLAY_NAME));
        
        rflvpmItem.setProject(getXmlAttribute("String", DefaultConstants.PROJECT));
        rflvpmItem.setOrganization(getXmlAttribute("String", DefaultConstants.ORGANIZATION));

        return rflvpmItem;
    }

    public Mandatory setReferenceItemsMandatoryAttribute(String externalId) {
        Attribute plmExternalId = beanFactory.getBean(Attribute.class);
        plmExternalId.setType("String");
        plmExternalId.setValue(externalId);

//        BoundingBox boundingBox = beanFactory.getBean(BoundingBox.class);
//        boundingBox.setXMax(DefaultConstants.REFERENCE_X_MAX);
//        boundingBox.setXMin(DefaultConstants.REFERENCE_X_MIN);
//        boundingBox.setYMax(DefaultConstants.REFERENCE_Y_MAX);
//        boundingBox.setYMin(DefaultConstants.REFERENCE_Y_MIN);

        Mandatory mandatory = beanFactory.getBean(Mandatory.class);
        mandatory.setPlmExternalId(plmExternalId);
//        mandatory.setBoundingBox(boundingBox);

        return mandatory;
    }

    public Mandatory setInstanceItemsMandatoryAttribute(String externalId, String ownerReference, String reference) {
        Attribute plmExternalId = beanFactory.getBean(Attribute.class);
        plmExternalId.setType("String");
        plmExternalId.setValue(externalId);

//        BoundingBox boundingBox = beanFactory.getBean(BoundingBox.class);
//        boundingBox.setXMax(DefaultConstants.INSTANCE_X_MAX);
//        boundingBox.setXMin(DefaultConstants.INSTANCE_X_MIN);
//        boundingBox.setYMax(DefaultConstants.INSTANCE_Y_MAX);
//        boundingBox.setYMin(DefaultConstants.INSTANCE_Y_MIN);

        Mandatory mandatory = beanFactory.getBean(Mandatory.class);
        mandatory.setPlmExternalId(plmExternalId);
//        mandatory.setBoundingBox(boundingBox);

        Relation relation = beanFactory.getBean(Relation.class);
        relation.setOwnerReference(ownerReference);
        relation.setReference(reference);
        mandatory.setRelation(relation);

        return mandatory;
    }

    public RFLP getRflp() {
        List<RFLVPMItem> logicalReferencesList = new ArrayList<>();
        List<RFLVPMItem> logicalInstanceList = new ArrayList<>();
        List<RFLVPMItem> logicalPortList = new ArrayList<>();

        LogicalReference logicalReference = new LogicalReference();
        logicalReference.setId(logicalReferencesList);

        LogicalInstance logicalInstance = new LogicalInstance();
        logicalInstance.setId(logicalInstanceList);

        LogicalPort logicalPort = new LogicalPort();
        logicalPort.setId(logicalPortList);

        RFLP rflp = beanFactory.getBean(RFLP.class);
        rflp.setLogicalReference(logicalReference);
        rflp.setLogicalInstance(logicalInstance);
        rflp.setLogicalPort(logicalPort);
        return rflp;
    }

    public RFLP getRflp(RFLP parentRFLP, RFLP childRFLP) {
        List<RFLVPMItem> parentLogicalId = parentRFLP.getLogicalReference().getId();
        List<RFLVPMItem> parentInstanceId = parentRFLP.getLogicalInstance().getId();
        List<RFLVPMItem> parentPortId = parentRFLP.getLogicalPort().getId();

        List<RFLVPMItem> childLogicalId = childRFLP.getLogicalReference().getId();
        List<RFLVPMItem> childInstanceId = childRFLP.getLogicalInstance().getId();
        List<RFLVPMItem> childPortId = childRFLP.getLogicalPort().getId();

        parentLogicalId.addAll(childLogicalId);
        parentInstanceId.addAll(childInstanceId);
        parentPortId.addAll(childPortId);

        return parentRFLP;
    }
}
