/**
 * @author Faruq / Ashikur 
 */
import com.dassault_systemes.VPLMJDocumentServices.VPLMJDocumentServices;

import java.util.HashMap;
import matrix.db.Context;
import matrix.db.JPO;
import com.matrixone.apps.common.Document;

import matrix.db.BusinessObject;
import matrix.util.MatrixException;

import com.matrixone.apps.configuration.ProductConfiguration;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.DomainRelationship;
import com.matrixone.apps.framework.ui.UIUtil;
import com.matrixone.apps.domain.util.*;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.io.*;

public class CustomProdConfigImport_mxJPO {

	private File jpoLog = new File("");

	public CustomProdConfigImport_mxJPO(Context context, String[] args) throws Exception {

	}

	public String createProductConfig(Context context, String[] args) throws Exception {
		try {
			HashMap programMap = (HashMap) JPO.unpackArgs(args);

			com.dassault_systemes.enovia.configuration.modeler.ProductConfiguration productConfiguration = new com.dassault_systemes.enovia.configuration.modeler.ProductConfiguration();
			String productConfiguration1 = productConfiguration.createProductConfiguration(context,
					programMap.get("productConfigType").toString(), programMap.get("productConfigName").toString(),
					null, null, null, programMap.get("hardwareProductMarketingName").toString(),
					programMap.get("productConfigParentName").toString(), null, null);

			System.out.println(productConfiguration1);

		} catch (Exception exp) {
			return exp.getMessage();
		}
		return "Return error occurered";
	}

	public String utilityData(Context context, String[] args) throws Exception {
		// return PersonUtil.getUserCompanyId(context);

		// return PropertyUtil.getSchemaProperty(context,
		// "relationship_ProductConfiguration") + ", " +
		// PropertyUtil.getSchemaProperty(context,
		// "relationship_FeatureProductConfiguration");
		DomainObject domainObject = new DomainObject("42848.60610.20580.25776");
		return PropertyUtil.getSchemaProperty(context, "relationship_SelectedOptions");
	}

	public String connectProductConfig(Context context, String[] args) {
		StringBuilder msgBuilder = new StringBuilder("");
		String returnResult = "";
		try {

			HashMap programMap = (HashMap) JPO.unpackArgs(args);

			String userName = programMap.get("username").toString();
			String childObjectId = programMap.get("childObjectId").toString();
			String marketingName = programMap.get("marketingName").toString();
			String parentObjectId = programMap.get("parentObjectId").toString();
			MapList selectedOptionMap = (MapList) programMap.get("selectedOptionMap");
			String derivedFrom = programMap.get("derivedFrom").toString();

			//ContextUtil.startTransaction(context, true);

			if (UIUtil.isNullOrEmpty(userName)) {
				userName = context.getUser();
			}

			DomainObject childDomainObject = DomainObject.newInstance(context, childObjectId);

			HashMap objectsAttributeValueMap = new HashMap();
			if (!UIUtil.isNullOrEmpty(userName)) {
				objectsAttributeValueMap.put(PropertyUtil.getSchemaProperty(context, "attribute_Originator"), userName);
			}

			objectsAttributeValueMap.put(PropertyUtil.getSchemaProperty(context, "attribute_MarketingName"),
					marketingName);
			objectsAttributeValueMap.put(
					PropertyUtil.getSchemaProperty(context, "attribute_ProductConfigurationPurpose"), "Evaluation");
			childDomainObject.setAttributeValues(context, objectsAttributeValueMap);

			connectToOrganization(context, userName, "Standard Configuration", childDomainObject);
			connectToParent(context, parentObjectId, childDomainObject);

			if (selectedOptionMap.size() > 0) {
				setSelectedOptions(context, selectedOptionMap, childObjectId);
			} else if (derivedFrom != null && !"".equals(derivedFrom)) {
				setDerivedFrom(context, derivedFrom, childDomainObject);
			}

			//ContextUtil.commitTransaction(context);
//			context.commit();
			returnResult = childObjectId;
			msgBuilder.append("JPO Info: ").append("Product Configuration Create Operation Successful! ").append(returnResult);
		} catch (Exception exp) {
//			ContextUtil.abortTransaction(context);
			returnResult = exp.getMessage();
			msgBuilder.append("JPO Error: ").append(returnResult);
		}
		return msgBuilder.toString();
	}

	private void connectToOrganization(Context context, String contextUserName, String standardConfiguration,
			DomainObject childDomainObject) throws FrameworkException, Exception {
		if (!UIUtil.isNullOrEmpty(contextUserName) && !UIUtil.isNullOrEmpty(standardConfiguration)) {
			try {
				String relationshipItem = null;
				if (standardConfiguration.equals("Custom Configuration")) {
					relationshipItem = PropertyUtil.getSchemaProperty(context, "relationship_CustomItem");
				} else if (standardConfiguration.equals("Standard Configuration")) {
					relationshipItem = PropertyUtil.getSchemaProperty(context, "relationship_StandardItem");
				}

				if (!UIUtil.isNullOrEmpty(relationshipItem)) {
					DomainObject companyDomainObject = DomainObject.newInstance(context,
							PersonUtil.getUserCompanyId(context));
					DomainRelationship.connect(context, companyDomainObject, relationshipItem, childDomainObject);
				}

			} catch (FrameworkException exp) {
				throw new FrameworkException(exp);
			} catch (Exception exp) {
				throw new Exception(exp);
			}
		}
	}

	private void connectToParent(Context context, String parentId, DomainObject childDomainObject)
			throws FrameworkException, Exception {
		if (!UIUtil.isNullOrEmpty(parentId)) {
			try {
				DomainObject parentDomainObject = DomainObject.newInstance(context, parentId);
				DomainRelationship.connect(context, parentDomainObject,
						PropertyUtil.getSchemaProperty(context, "relationship_ProductConfiguration"),
						childDomainObject);
				DomainRelationship.connect(context, parentDomainObject,
						PropertyUtil.getSchemaProperty(context, "relationship_FeatureProductConfiguration"),
						childDomainObject);
			} catch (FrameworkException exp) {
				throw new FrameworkException(exp);
			} catch (Exception exp) {
				throw new Exception(exp);
			}
		}
	}

	private void setSelectedOptions(Context context, MapList selectedOptionValueMap, String childObjectId)
			throws FrameworkException, Exception {
		try {
			ContextUtil.startTransaction(context, true);
			String childBusinessObjectId = childObjectId;
			int mapSize = 0;

			while (true) {
				if (mapSize >= selectedOptionValueMap.size()) {
					PropertyUtil.setGlobalRPEValue(context, "UpdatePCFilterBinary", "TRUE");
					ContextUtil.commitTransaction(context);
					break;
				}

				Map map = (Map) selectedOptionValueMap.get(mapSize);

				Set keySet = map.keySet();
				Iterator iterator = keySet.iterator();

				while (iterator.hasNext()) {
					String relationshipId = (String) iterator.next();
					Map itemMap = (Map) map.get(relationshipId);

					String mqlQuery = "add connection $1 from $2 torel $3 select $4 dump";
					String[] relationshipSelectedOption = new String[] {
							PropertyUtil.getSchemaProperty(context, "relationship_SelectedOptions"),
							childBusinessObjectId, relationshipId, "id[connection]" };
					String newRelationshipId = MqlUtil.mqlCommand(context, mqlQuery, relationshipSelectedOption);

					DomainRelationship domainRelationship = DomainRelationship.newInstance(context, newRelationshipId);
					domainRelationship.setAttributeValues(context, itemMap);
				}

				++mapSize;
			}
		} catch (FrameworkException exp) {
			ContextUtil.abortTransaction(context);
			throw new FrameworkException(exp);
		} catch (Exception exp) {
			ContextUtil.abortTransaction(context);
			throw new Exception(exp);
		}
	}

	private String setDerivedFrom(Context context, String derivedFrom, DomainObject childDomainObject)
			throws FrameworkException, Exception {
		String domainRelationshipName = "";
		try {
			DomainObject domainObject = DomainObject.newInstance(context, derivedFrom);
			DomainRelationship domainRelationship = DomainRelationship.connect(context, domainObject,
					PropertyUtil.getSchemaProperty(context, "relationship_DerivedFrom"), childDomainObject);
			domainRelationshipName = domainRelationship.getName();
		} catch (FrameworkException exp) {
			throw new FrameworkException(exp);
		} catch (Exception exp) {
			throw new Exception(exp);
		}

		return domainRelationshipName;
	}

	public String updateProductConfig(Context context, String[] args) {
		StringBuilder msgBuilder = new StringBuilder("");
		String returnResult = "";
		try {
			HashMap programMap = (HashMap) JPO.unpackArgs(args);
			String userName = programMap.get("username").toString();
			String childObjectId = programMap.get("childObjectId").toString();
			String marketingName = programMap.get("marketingName").toString();
			String parentObjectId = programMap.get("parentObjectId").toString();
			MapList selectedOptionMap = (MapList) programMap.get("selectedOptionMap");
			String derivedFrom = programMap.get("derivedFrom").toString();
			//ContextUtil.startTransaction(context, true);

			if (UIUtil.isNullOrEmpty(userName)) {
				userName = context.getUser();
			}
			DomainObject childDomainObject = DomainObject.newInstance(context, childObjectId);
			HashMap objectsAttributeValueMap = new HashMap();
			if (!UIUtil.isNullOrEmpty(userName)) {
				objectsAttributeValueMap.put(PropertyUtil.getSchemaProperty(context, "attribute_Originator"), userName);
			}
			objectsAttributeValueMap.put(PropertyUtil.getSchemaProperty(context, "attribute_MarketingName"),
					marketingName);
			objectsAttributeValueMap.put(
					PropertyUtil.getSchemaProperty(context, "attribute_ProductConfigurationPurpose"), "Evaluation");
			childDomainObject.setAttributeValues(context, objectsAttributeValueMap);
			if (selectedOptionMap.size() > 0) {
				setSelectedOptions(context, selectedOptionMap, childObjectId);
			} else if (derivedFrom != null && !derivedFrom.isEmpty()) {
				setDerivedFrom(context, derivedFrom, childDomainObject);
			}
			//ContextUtil.commitTransaction(context);
			context.commit();
			returnResult = childObjectId;
			msgBuilder.append("JPO Info: ").append("Product Configuration Update Operation Successful! ").append(returnResult);
		} catch (Exception exp) {
			ContextUtil.abortTransaction(context);
			returnResult = exp.getLocalizedMessage();
			msgBuilder.append("JPO Error: ").append(returnResult);
		}
		return msgBuilder.toString();
	}

}


