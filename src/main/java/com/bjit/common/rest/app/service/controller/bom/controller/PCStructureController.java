/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.app.service.controller.bom.controller;

import com.bjit.common.rest.app.service.controller.item.controller.ImportItemController;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.StringJoiner;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.bjit.common.rest.app.service.controller.bom.processor.HPStructureProcessor;
import com.bjit.common.rest.app.service.controller.bom.model.ParentChildRelationshipModel;
import com.bjit.common.rest.app.service.controller.bom.model.ParentRelationshipModel;
import com.bjit.common.rest.app.service.controller.bom.model.ChildItem;
import com.bjit.common.rest.app.service.controller.bom.model.ExpandedModel;
import com.bjit.common.rest.app.service.controller.bom.model.RootItem;
import com.bjit.common.rest.app.service.controller.bom.model.StructureModel;
import com.bjit.common.rest.app.service.model.createobject.CreateObjectBean;
import com.bjit.common.rest.app.service.model.itemImport.DataTree;
import com.bjit.common.rest.app.service.model.itemImport.ObjectDataBean;
import com.bjit.common.rest.app.service.model.tnr.TNR;
import com.bjit.common.rest.app.service.payload.common_response.CustomResponseBuilder;
import com.bjit.common.rest.app.service.payload.common_response.IResponse;
import com.bjit.common.rest.app.service.payload.common_response.Status;
import com.bjit.common.rest.item_bom_import.import_threads.ItemImportProcess;
import com.bjit.common.rest.item_bom_import.utility.BusinessObjectUtil;
import com.bjit.common.rest.item_bom_import.xml_mapping_model.ResponseMessageFormaterBean;
import com.matrixone.apps.domain.util.ContextUtil;
import com.matrixone.apps.domain.util.FrameworkException;
import com.matrixone.apps.domain.util.MqlUtil;

import matrix.db.BusinessObject;
import matrix.db.Context;
import matrix.util.MatrixException;

/**
 *
 * @author BJIT
 */
//@RestController
//@RequestMapping(path = "/importStructure")
public class PCStructureController {
//	
//	String rootHardwareItemId;
//	private static boolean IsImportItemStructure = false;
//	
//    private static final org.apache.log4j.Logger IMPORT_PRODUCT_CONFIGURATION_STRUCTURE_CONTROLLER_LOGGER = org.apache.log4j.Logger.getLogger(PCStructureController.class);
//    
//    @RequestMapping(value = "/importPCStructure", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
//    @ResponseBody
//    public ResponseEntity<?> importProductConfigurationStructure(HttpServletRequest httpRequest, @RequestBody final StructureModel structureModel) {
//    	this.setIsImportItemStructure(true);
//        IResponse responseBuilder = new CustomResponseBuilder();
//        String buildResponse = "";
//        rootHardwareItemId = null;
//        HPStructureProcessor importPCStructureProcessor = new HPStructureProcessor();
//        //Boolean createProductConfigurationIfNotExits = Boolean.parseBoolean(createPCIfNotExits.orElse("false"));
//        BusinessObjectUtil businessObjectUtil = new BusinessObjectUtil();
//        HashMap<String, ExpandedModel> expandedMap = new HashMap<>();
//        try {
//            final Context context = (Context) httpRequest.getAttribute("context");
//            List<RootItem> rootItemList = structureModel.getItem();
//            importLineData(httpRequest, structureModel, businessObjectUtil, context, rootItemList);
//        	
//        	try {
//
//                /*---------------------------------------- ||| Start Transaction for Importing Product Configuration Structure ||| ----------------------------------------*/
//                IMPORT_PRODUCT_CONFIGURATION_STRUCTURE_CONTROLLER_LOGGER.debug("Starting Transaction");
//                ContextUtil.startTransaction(context, true);
//                ParentChildRelationshipModel parentChildRelationshipModel = importPCStructureProcessor.prepareHardwareProductRelationshipMap();
//                HashMap<String, List<ParentRelationshipModel>> childParentRelationship = parentChildRelationshipModel.getParentChildRelationshipModel();
//
//                StringJoiner parentRelationshipJoiner = new StringJoiner(",");
//                StringJoiner childTypeJoiner = new StringJoiner(",");
//                //StringJoiner selectedOptionTypeJoiner = new StringJoiner(",");
//                childParentRelationship
//                        .entrySet()
//                        .stream()
//                        .forEach(entry -> childParentRelationship.get(entry.getKey())
//                        .stream()
//                        .forEach(relationship -> parentRelationshipJoiner.add(relationship.getRelationName())));
//
//                HashMap<String, String> productConfigurationIdList = new HashMap();
//
//                rootItemList.stream().forEach((RootItem rootItem) -> {
//                    TNR parentItem = rootItem.getTnr();
//                    ArrayList<BusinessObject> searchRootItem;
//                    try {
//                        searchRootItem = businessObjectUtil.findBO(context, parentItem);
//                    } catch (MatrixException exp) {
//                        IMPORT_PRODUCT_CONFIGURATION_STRUCTURE_CONTROLLER_LOGGER.error(exp);
//                        throw new RuntimeException(exp);
//                    }
//
//                    if (!searchRootItem.isEmpty()) {
//                        BusinessObject rootBusinessObject = searchRootItem.get(0);
//                        String rootId = rootBusinessObject.getObjectId();
//                        rootItem.setId(rootId);
//
//                        if (parentItem.getType().equalsIgnoreCase("Hardware Product")) {
//                            rootHardwareItemId = rootId;
//                        }
//                        
//                        List<ChildItem> lines = rootItem.getLines();
//                        
//                        lineItemExistenceValidation(structureModel, context, childParentRelationship, childTypeJoiner, rootItem, lines);
//                        
//                        List<ExpandedModel> expandedList;
//                        try {
//                        	IMPORT_PRODUCT_CONFIGURATION_STRUCTURE_CONTROLLER_LOGGER.debug("@@@@@ parentRelationshipJoiner: " + parentRelationshipJoiner.toString() + "ChildTypeJoiner:  " + childTypeJoiner.toString());
//                            expandedList = importPCStructureProcessor.expandItem(context, rootId, parentRelationshipJoiner.toString(), childTypeJoiner.toString(), (short) 2);
//                        } catch (Exception exp) {
//                            IMPORT_PRODUCT_CONFIGURATION_STRUCTURE_CONTROLLER_LOGGER.error(exp);
//                            throw new RuntimeException(exp);
//                        }
//                        expandedList.stream().forEach(expandedIdtem -> {
//                        	IMPORT_PRODUCT_CONFIGURATION_STRUCTURE_CONTROLLER_LOGGER.debug("<<<====>>> Expanded item: " + expandedIdtem.getItemTnr().getName() + " <<<====>>>");
//                        	expandedMap.put(expandedIdtem.getItemId(), expandedIdtem);
//                        });
//                        HashMap<String, ChildItem> childItemMap = new HashMap<>();
//                        lines.stream().forEach((ChildItem lineItem) -> {
//                            childItemMap.put(lineItem.getId(), lineItem);
//                            Optional.ofNullable(lineItem.getSelected()).ifPresent(selectedOption -> childItemMap.put(selectedOption.getId(), selectedOption));
//                        });
//                   
//                        lines.stream().forEach((ChildItem childItem) -> {
//                        	IMPORT_PRODUCT_CONFIGURATION_STRUCTURE_CONTROLLER_LOGGER.info("Going to execute product configuration creation for item : " + childItem.getTnr().getName());
//                            try {
//                                checkChildItemsInExpandedList(context, childItem, rootId, expandedMap, productConfigurationIdList, importPCStructureProcessor);
//                            } catch (FrameworkException exp) {
//                                IMPORT_PRODUCT_CONFIGURATION_STRUCTURE_CONTROLLER_LOGGER.error(exp);
//                                throw new RuntimeException(exp);
//                            }
//                            Optional.ofNullable(childItem.getSelected()).ifPresent((ChildItem selectedOption) -> {
//                                try {
//                                    checkChildItemsInExpandedList(context, selectedOption, childItem.getId(), expandedMap, productConfigurationIdList, importPCStructureProcessor);
//                                } catch (FrameworkException exp) {
//                                    IMPORT_PRODUCT_CONFIGURATION_STRUCTURE_CONTROLLER_LOGGER.error(exp);
//                                    throw new RuntimeException(exp);
//                                }
//                            });
//                        });
//                    } else {
//                        IMPORT_PRODUCT_CONFIGURATION_STRUCTURE_CONTROLLER_LOGGER.error("Item not found");
//                        throw new RuntimeException("Item not found");
//                    }
//
//                    Optional.ofNullable(rootHardwareItemId).orElseThrow(() -> new NullPointerException("Root Hardware Item not found"));
//                    Optional.ofNullable(productConfigurationIdList).orElseThrow(() -> new NullPointerException("No Configuration Feature or Configuration Options found for Product Configuration"));
//
//                    productConfigurationIdList.keySet().stream().parallel().forEach((String productConfigurationId) -> {
//                        try {
//                        	if (expandedMap.containsKey(productConfigurationId)) {
//                        		importPCStructureProcessor.updateProductConfigJPOMethod(context, rootHardwareItemId, productConfigurationId, Optional.ofNullable(productConfigurationIdList.get(productConfigurationId)).orElse("Default Marketing Name"));
//                        	} else
//                        		importPCStructureProcessor.callProductConfigJPOMethod(context, rootHardwareItemId, productConfigurationId, Optional.ofNullable(productConfigurationIdList.get(productConfigurationId)).orElse("Default Marketing Name"));
//                            /*---------------------------------------- ||| Commit Transaction Clone Business Object ||| ----------------------------------------*/
//                            //IMPORT_PRODUCT_CONFIGURATION_STRUCTURE_CONTROLLER_LOGGER.info("Committing Transaction");
//                            //ContextUtil.commitTransaction(context);
//                        } catch (MatrixException exp) {
//                            IMPORT_PRODUCT_CONFIGURATION_STRUCTURE_CONTROLLER_LOGGER.error(exp);
//                            throw new RuntimeException(exp);
//                        } catch (Exception e) {
//							// TODO Auto-generated catch block
//							e.printStackTrace();
//						}
//                    });
//                });
//
//                buildResponse = responseBuilder.setData("Structure Imported Successfully").setStatus(Status.OK).buildResponse();
//
//            } catch (FrameworkException exp) {
//                /*---------------------------------------- ||| Aborting Transaction Clone Business Object ||| ----------------------------------------*/
//                IMPORT_PRODUCT_CONFIGURATION_STRUCTURE_CONTROLLER_LOGGER.error("Aborting for Transaction");
//                ContextUtil.abortTransaction(context);
//
//                IMPORT_PRODUCT_CONFIGURATION_STRUCTURE_CONTROLLER_LOGGER.error(exp);
//                buildResponse = responseBuilder.addErrorMessage(exp.getMessage()).setStatus(Status.FAILED).buildResponse();
//                IMPORT_PRODUCT_CONFIGURATION_STRUCTURE_CONTROLLER_LOGGER.debug(buildResponse);
//                return new ResponseEntity<>(buildResponse, HttpStatus.OK);
//            } catch (Exception exp) {
//                /*---------------------------------------- ||| Aborting Transaction Clone Business Object ||| ----------------------------------------*/
//                IMPORT_PRODUCT_CONFIGURATION_STRUCTURE_CONTROLLER_LOGGER.error("Aborting for Transaction");
//                ContextUtil.abortTransaction(context);
//
//                IMPORT_PRODUCT_CONFIGURATION_STRUCTURE_CONTROLLER_LOGGER.error(exp);
//                buildResponse = responseBuilder.addErrorMessage(exp.getMessage()).setStatus(Status.FAILED).buildResponse();
//                IMPORT_PRODUCT_CONFIGURATION_STRUCTURE_CONTROLLER_LOGGER.debug(buildResponse);
//                return new ResponseEntity<>(buildResponse, HttpStatus.OK);
//            }
//        } catch (Exception exp) {
//            /*---------------------------------------- ||| Aborting Transaction Clone Business Object ||| ----------------------------------------*/
//            IMPORT_PRODUCT_CONFIGURATION_STRUCTURE_CONTROLLER_LOGGER.error(exp);
//            buildResponse = responseBuilder.addErrorMessage(exp.getMessage()).setStatus(Status.FAILED).buildResponse();
//            IMPORT_PRODUCT_CONFIGURATION_STRUCTURE_CONTROLLER_LOGGER.debug(buildResponse);
//            return new ResponseEntity<>(buildResponse, HttpStatus.OK);
//        } finally {
//        	this.setIsImportItemStructure(false);
//        }
//
//        return new ResponseEntity<>(buildResponse, HttpStatus.OK);
//    }
//
//	private void lineItemExistenceValidation(final StructureModel structureModel, final Context context,
//			HashMap<String, List<ParentRelationshipModel>> childParentRelationship, StringJoiner childTypeJoiner,
//			RootItem rootItem, List<ChildItem> lines) {
//		lines.stream().forEach((ChildItem childItem) -> {
//		    TNR childTnr = childItem.getTnr();
//		    Boolean itemExistsInTheSystem = validateChildItemsExistenceAndRelationship(context, rootItem.getTnr(), childItem, childParentRelationship, childTypeJoiner);
//		    if (!itemExistsInTheSystem) {
//		    	IMPORT_PRODUCT_CONFIGURATION_STRUCTURE_CONTROLLER_LOGGER.info("[[######### " + childTnr.getName() + " NOT FOUND IN THE SYSTEM. GOING TO EXECUTE IMPORT #########]]");
//		        try {
//					importItems(context, childItem, rootItem.getTnr(), structureModel, childParentRelationship, childTypeJoiner);
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//		    } else {
//		    	 IMPORT_PRODUCT_CONFIGURATION_STRUCTURE_CONTROLLER_LOGGER.info("[[######### " + childTnr.getName() + " FOUND IN THE SYSTEM.#########]]");
//		    }
//		    Optional.ofNullable(childItem.getSelected()).ifPresent((ChildItem selectedOption) -> {
//		        Boolean selectedItemExistsInTheSystem = validateChildItemsExistenceAndRelationship(context, childItem.getTnr(), selectedOption, childParentRelationship, childTypeJoiner);
//		        if (!selectedItemExistsInTheSystem) {
//		        	IMPORT_PRODUCT_CONFIGURATION_STRUCTURE_CONTROLLER_LOGGER.info("[[######### " + selectedOption.getTnr().getName() + " NOT FOUND IN THE SYSTEM. GOING TO EXECUTE IMPORT #########]]");
//		            try {
//						importItems(context, selectedOption, childTnr, structureModel, childParentRelationship, childTypeJoiner);
//					} catch (Exception e) {
//						e.printStackTrace();
//					}
//		        } else {
//		        	 IMPORT_PRODUCT_CONFIGURATION_STRUCTURE_CONTROLLER_LOGGER.info("[[######### " + selectedOption.getTnr().getName() + " FOUND IN THE SYSTEM.#########]]");                          
//		        }
//		    });                         
//		});
//	}
//
//	private void importLineData(HttpServletRequest httpRequest, final StructureModel structureModel,
//			BusinessObjectUtil businessObjectUtil, final Context context, List<RootItem> rootItemList)
//			throws Exception {
//		rootItemList = structureModel.getItem();
//		rootItemList.parallelStream().forEach(rootItem -> {
//			TNR parentItem = rootItem.getTnr();
//			ArrayList<BusinessObject> searchRootItem;
//			try {
//				searchRootItem = businessObjectUtil.findBO(context, parentItem);
//			} catch (MatrixException exp) {
//				IMPORT_PRODUCT_CONFIGURATION_STRUCTURE_CONTROLLER_LOGGER.error(exp);
//				throw new RuntimeException(exp);
//			}
//			if (searchRootItem.size() > 0) {
//				IMPORT_PRODUCT_CONFIGURATION_STRUCTURE_CONTROLLER_LOGGER
//						.error("ROOT item type: " + parentItem.getType());
//				if (!parentItem.getType().equals("Hardware Product")) {
//					IMPORT_PRODUCT_CONFIGURATION_STRUCTURE_CONTROLLER_LOGGER
//							.error("ROOT item must be of type Hardware Product");
//					throw new RuntimeException("ROOT item must be of type Hardware Product");
//				} else {
//					BusinessObject hpRoot = searchRootItem.get(0);
//					try {
//						if (hpRoot.isLocked(context)) {
//							IMPORT_PRODUCT_CONFIGURATION_STRUCTURE_CONTROLLER_LOGGER
//									.info(hpRoot.getName() + " is locked, going to unlock.");
//							hpRoot.unlock(context);
//							IMPORT_PRODUCT_CONFIGURATION_STRUCTURE_CONTROLLER_LOGGER
//									.info(hpRoot.getName() + " is unlocked successfully.");
//						}
//					} catch (MatrixException e) {
//						IMPORT_PRODUCT_CONFIGURATION_STRUCTURE_CONTROLLER_LOGGER
//								.info(hpRoot.getName() + " " + e.getMessage());
//						e.printStackTrace();
//					}
//				}
//			} else {
//				IMPORT_PRODUCT_CONFIGURATION_STRUCTURE_CONTROLLER_LOGGER.error("Item not found");
//				throw new RuntimeException("Item not found");
//			}
//
//			List<ChildItem> lines = rootItem.getLines();
//			lines.stream().forEach(childItem -> {
//				TNR childTnr = childItem.getTnr();
//				try {
//					importChildItem(httpRequest, context, parentItem, childItem, structureModel.getSource());
//				} catch (Exception e1) {
//					IMPORT_PRODUCT_CONFIGURATION_STRUCTURE_CONTROLLER_LOGGER.error(e1.getMessage());
//					e1.printStackTrace();
//					throw new RuntimeException("Line Item creation or update is failed.");
//				}
//			});
//		});
//		return;
//	}
//
//	private void importChildItem(HttpServletRequest httpRequest, Context context,
//			TNR parentItem, ChildItem childItem, String source) throws Exception {
//		TNR childTnr = childItem.getTnr();
//		DataTree singleDataTree = new DataTree();
//		CreateObjectBean createObjectBean = new CreateObjectBean();
//		HashMap<String, String> attributes = childItem.getAttributes();
//		attributes = (attributes == null) ? new HashMap<>() : attributes;
//		ObjectDataBean objectDataBean = new ObjectDataBean();
//		List<DataTree> dataTree = new ArrayList<>();
//		ChildItem selectedItem = childItem.getSelected();
//		if (childTnr.getType().equalsIgnoreCase("Product Configuration")) {
//			String marketingName = attributes.get("marketingName");
//			attributes.remove("marketingName");
//			attributes.put("Marketing Name", marketingName);
//		}
//		createObjectBean.setTnr(childItem.getTnr());
//		createObjectBean.setAttributes(attributes);
//		createObjectBean.setIsAutoName(false);
//		createObjectBean.setAllowNewRevision(false);
//		createObjectBean.setNextVersion("");
//		createObjectBean.setSource(source);
//		createObjectBean.setAttributeGlobalRead(false);
//		
//		singleDataTree.setItem(createObjectBean);
//		singleDataTree.setDocuments(new ArrayList<>());
//		singleDataTree.setSubstitutes(new ArrayList<>());
//		dataTree.add(singleDataTree);
//		
//		objectDataBean.setSource(source);
//		objectDataBean.setDataTree(dataTree);
//		
//		ImportItemController importItemController = new ImportItemController();
//		ResponseEntity<?> response  = null;
//		if (childTnr.getType().equalsIgnoreCase("Product Configuration")) {
//			response = importItemController.createProductConfiguration(httpRequest, objectDataBean);
//		} else {
//			response = importItemController.createConfigurationFeature(httpRequest, objectDataBean);
//		}
//		IMPORT_PRODUCT_CONFIGURATION_STRUCTURE_CONTROLLER_LOGGER.info("[[[[[[[[[[[[[[ " + response.getBody() + " ]]]]]]]]]]]]");
//		IMPORT_PRODUCT_CONFIGURATION_STRUCTURE_CONTROLLER_LOGGER.info(childTnr.getName() + " item creation or udpate successful.");
//		if(selectedItem != null) {
//			try {
//				importChildItem(httpRequest, context, childItem.getTnr(), selectedItem, source);
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//		}
//		return;
//	}
//
//
//    private void importItems(Context context, ChildItem childItem, TNR rootTnr, StructureModel structureModel, HashMap<String, List<ParentRelationshipModel>> childParentRelationship, StringJoiner childTypeJoiner) throws Exception {
//        TNR childTnr = childItem.getTnr(); 
//        if (childTnr.getType().equalsIgnoreCase("Product Configuration")) {
//            createProductConfiguration(context, rootTnr, childItem, structureModel, childParentRelationship, childTypeJoiner);
//        } else {
//            IMPORT_PRODUCT_CONFIGURATION_STRUCTURE_CONTROLLER_LOGGER.error("Item not found");
//            throw new RuntimeException("Item not found");
//        }
//    }
//
//    private Boolean validateChildItemsExistenceAndRelationship(Context context, TNR parentTnr, ChildItem childItem, HashMap<String, List<ParentRelationshipModel>> childParentRelationship, StringJoiner childTypeJoiner) throws RuntimeException {
//        TNR childTnr = childItem.getTnr();
//        ArrayList<BusinessObject> childItemBusinessObjectList;
//        BusinessObjectUtil businessObjectUtil = new BusinessObjectUtil();
//        try {
//            childItemBusinessObjectList = businessObjectUtil.findBO(context, childTnr);
//        } catch (MatrixException exp) {
//            IMPORT_PRODUCT_CONFIGURATION_STRUCTURE_CONTROLLER_LOGGER.error(exp);
//            throw new RuntimeException(exp);
//        }
//
//        if (!childItemBusinessObjectList.isEmpty()) {
//
//            BusinessObject childBusinessObject = childItemBusinessObjectList.get(0);
//            String childId = childBusinessObject.getObjectId();
//            childItem.setId(childId);
//
//            List<ParentRelationshipModel> parentChildRelationshipModel = null;
//
//            if (childParentRelationship.containsKey(childTnr.getType())) {
//                String parentItemType = parentTnr.getType();
//                String childItemType = childTnr.getType();
//                childTypeJoiner.add(childItemType);
//                parentChildRelationshipModel = childParentRelationship.get(childItemType);
//                if (!parentChildRelationshipModel.stream().anyMatch(parentChildRelationship -> parentChildRelationship.getParentType().equalsIgnoreCase(parentItemType))) {
//                    throw new RuntimeException("Making relationship between '" + parentItemType + "' and '" + childItemType + "' is not possible");
//                }
//            }
//            return true;
//        }
//
//        return false;
//    }
//
//    private void createProductConfiguration(Context context, TNR rootTnr, ChildItem childItem, StructureModel structureModel, HashMap<String, List<ParentRelationshipModel>> childParentRelationship, StringJoiner childTypeJoiner) throws Exception {
//        TNR childTnr = childItem.getTnr();
//        IMPORT_PRODUCT_CONFIGURATION_STRUCTURE_CONTROLLER_LOGGER.debug(">>>>>>>>>>>>>>>>>>>>>>>>>> CREATE PRODUCT CONFIGURATION  <<<<<<<<<<<<<<<<<");
//        HashMap<String, String> attributes = new HashMap<>();
//        attributes.put("Configuration Selection Type", "Single");
//        attributes.put("Display Name", "Automatically Generated");
//        attributes.put("project", "GLOBAL_COMPONENTS_INTERNAL");
//        attributes.put("organization", "VALMET_INTERNAL");
//
//        CreateObjectBean pcItemCreateModel = new CreateObjectBean(childTnr, attributes, structureModel.getSource());
//        ResponseMessageFormaterBean responseMessageFormaterBean = new ResponseMessageFormaterBean();
//        ItemImportProcess itemImportProcess = new ItemImportProcess(context, pcItemCreateModel, responseMessageFormaterBean, structureModel.getSource());
//        try {
//            String newPCObjectId = itemImportProcess.processItem(context, pcItemCreateModel, responseMessageFormaterBean);
//            childItem.setId(newPCObjectId);
//            IMPORT_PRODUCT_CONFIGURATION_STRUCTURE_CONTROLLER_LOGGER.debug("!!!!!!!!!!!!!!!!! new product configuration object id: " + newPCObjectId);
//            List<ParentRelationshipModel> parentRelationList = null;
//
//            if (childParentRelationship.containsKey(childItem.getTnr().getType())) {
//
//                String rootItemType = rootTnr.getType();
//                String childType = childItem.getTnr().getType();
//                childTypeJoiner.add(childType);
//
//                parentRelationList = childParentRelationship.get(childType);
//
//                if (!parentRelationList.stream().anyMatch(parentRelation -> parentRelation.getParentType().equalsIgnoreCase(rootItemType))) {
//                    throw new RuntimeException("Making relationship between '" + rootItemType + "' and '" + childType + "' is not possible");
//                }
//            }
//
//        } catch (RuntimeException exp) {
//            IMPORT_PRODUCT_CONFIGURATION_STRUCTURE_CONTROLLER_LOGGER.error(exp);
//            throw new RuntimeException(exp);
//        } catch (Exception exp) {
//            IMPORT_PRODUCT_CONFIGURATION_STRUCTURE_CONTROLLER_LOGGER.error(exp);
//            throw new RuntimeException(exp);
//        }
//    }
//   
//
//    private HashMap<String, String> checkChildItemsInExpandedList(Context context, ChildItem childItem, String parentId, HashMap<String, ExpandedModel> expandedMap, HashMap<String, String> productConfigurationIdList, HPStructureProcessor importPCStructureProcessor) throws FrameworkException {
//    	if (childItem.getTnr().getType().equalsIgnoreCase("Product Configuration")) {
//            productConfigurationIdList.put(childItem.getId(), Optional.ofNullable(childItem.getAttributes().get("Marketing Name")).orElseThrow(() -> new NullPointerException("Marketing Name should not be null")));
//        }
//    	if (!expandedMap.containsKey(childItem.getId())) {
//    		String mqlQuery = "";
//            List<ExpandedModel> expandedList;
//            if (childItem.getTnr().getType().equalsIgnoreCase("Configuration Feature")) {
//            	try {
//                    expandedList = importPCStructureProcessor.expandItem(context, parentId, "Configuration Features", "Configuration Feature", (short) 1);
//                    expandedList.stream().forEach(expandedIdtem -> {
//                    	IMPORT_PRODUCT_CONFIGURATION_STRUCTURE_CONTROLLER_LOGGER.info("!!!!!!====>>> Expanded item: " + expandedIdtem.getItemTnr().getName());
//                    	expandedMap.put(expandedIdtem.getItemId(), expandedIdtem);
//                    });
//                } catch (Exception exp) {
//                    IMPORT_PRODUCT_CONFIGURATION_STRUCTURE_CONTROLLER_LOGGER.error(exp);
//                    throw new RuntimeException(exp);
//                }
//                mqlQuery = "add connection 'Configuration Features' from " + parentId + " to " + childItem.getId() + " select id dump";
//            } else if (childItem.getTnr().getType().equalsIgnoreCase("Configuration Option")) {              
//                try {
//                    expandedList = importPCStructureProcessor.expandItem(context, parentId, "Configuration Options", "Configuration Option", (short) 1);
//                    expandedList.stream().forEach(expandedIdtem -> {
//                    	IMPORT_PRODUCT_CONFIGURATION_STRUCTURE_CONTROLLER_LOGGER.debug("!!!!!!====>>> Expanded item: " + expandedIdtem.getItemTnr().getName());
//                    	expandedMap.put(expandedIdtem.getItemId(), expandedIdtem);
//                    });
//                } catch (Exception exp) {
//                    IMPORT_PRODUCT_CONFIGURATION_STRUCTURE_CONTROLLER_LOGGER.error(exp);
//                    throw new RuntimeException(exp);
//                }
//                mqlQuery = "add connection 'Configuration Options' from " + parentId + " to " + childItem.getId() + " select id dump";
//            }
//            try {
//                if (!expandedMap.containsKey(childItem.getId())) {
//                    IMPORT_PRODUCT_CONFIGURATION_STRUCTURE_CONTROLLER_LOGGER.info("######## Connection query : " + mqlQuery);
//                    String queryResult = MqlUtil.mqlCommand(context, mqlQuery);
//
//                    IMPORT_PRODUCT_CONFIGURATION_STRUCTURE_CONTROLLER_LOGGER.info("Result returned from server : " + queryResult);
//                    importPCStructureProcessor.prepareProductConfigurationJPOMap(queryResult, childItem.getAttributes());
//                } else {
//                    importPCStructureProcessor.prepareProductConfigurationJPOMap(expandedMap.get(childItem.getId()).getRelationshipId(), childItem.getAttributes());
//                }
//            } catch (FrameworkException exp) {
//                IMPORT_PRODUCT_CONFIGURATION_STRUCTURE_CONTROLLER_LOGGER.error(exp);
//                throw exp;
//            } catch (Exception exp) {
//                IMPORT_PRODUCT_CONFIGURATION_STRUCTURE_CONTROLLER_LOGGER.error(exp);
//                throw exp;
//            }
//        } else {
//            if (childItem.getTnr().getType().equalsIgnoreCase("Configuration Feature") || childItem.getTnr().getType().equalsIgnoreCase("Configuration Option")) {
//            	IMPORT_PRODUCT_CONFIGURATION_STRUCTURE_CONTROLLER_LOGGER.debug("<<< ####### Going to create product configuration JPO Map ####### >>>");
//                importPCStructureProcessor.prepareProductConfigurationJPOMap(expandedMap.get(childItem.getId()).getRelationshipId(), childItem.getAttributes());
//            }
//        }
//
//        return productConfigurationIdList;
//    }
//
//	public boolean isIsImportItemStructure() {
//		return IsImportItemStructure;
//	}
//
//	public void setIsImportItemStructure(boolean isImportItemStructure) {
//		IsImportItemStructure = isImportItemStructure;
//	}
//    
//    
}
