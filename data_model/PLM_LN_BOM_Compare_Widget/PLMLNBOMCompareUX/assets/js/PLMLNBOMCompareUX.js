define("VALMET/PLMLNBOMCompareUX/PLMLNBOMCompareUX", ["DS/ENO6WPlugins/jQuery", "DS/CompareWidgetUtils/Fancytree", "DS/CompareWidgetUtils/Fancytree_table", "UWA/Core", "DS/LifecycleServices/LifecycleServicesSettings", "DS/LifecycleServices/LifecycleServices", "DS/PlatformAPI/PlatformAPI", "DS/i3DXCompassPlatformServices/i3DXCompassPlatformServices", "UWA/Class/Promise", "VALMET/PLMLNBOMCompareUX/DnD", "DS/Foundation2/FoundationV2Data", "DS/Foundation/WidgetUwaUtils", "DS/Foundation/WidgetAPIs", "DS/UIKIT/Mask", "VALMET/PLMLNBOMCompareUX/PreferenceUtil", "DS/ENOFloatingPanel/ENOFloatingPanel", "VALMET/PLMLNBOMCompareUX/FilterColumns", "VALMET/PLMLNBOMCompareUX/WidgetConfiguration", "VALMET/PLMLNBOMCompareUX/BOMAttributeComparator", "VALMET/PLMLNBOMCompareUX/Export/CompareDataExporter", "css!DS/UIKIT/UIKIT.css", "css!DS/VENCompareWidgetUtils/plugins/fancytree/v2.9.0/fancytree.css"], function(jQuery, Tree, Table, UWA, LifecycleServicesSettings, LifecycleServices, PlatformAPI, i3DXCompassPlatformServices, UWApromise, DnD, FoundationV2Data, WidgetUwaUtils, WidgetAPIs, Mask, PreferenceUtil, ENOFloatingPanel, FilterColumns, WidgetConfiguration, BOMAttributeComparator, CompareDataExporter) {
    var PLMLNBOMCompareUX = {
        init: function() {
            window.BCWidget = {
                securitycontext: null,
                PlatformAPI: PlatformAPI,
                i3DXCompassPlatformServices: i3DXCompassPlatformServices,
                BOMTree0Source: "ENOVIA",
                BOMTree1Source: "LN",
                missingType: "dummy_type",
                missingName: "dummy_obj",
                collabStorage: "collabStorage",
                fTree_left: null,
                fTree_right: null,
                max_expansion_level: 1,
                CompareStructure: {
                    left: [],
                    right: []
                },
                nodeCount: 0,
                compareStatus: {
                    enoviaOnly: false,
                    lnOnly: false,
                    attributeDifference: false
                },
                columnAttributes: {}
            };
            WidgetUwaUtils.setupEnoviaServer();
            window.isIFWE = true;
            window.enoviaServer.showSpace = "true";
            WidgetUwaUtils.onAfterLoad = jQuery.noop;
            window.enoviaServer.widgetId = widget.id;

            var collabStorage = widget.getValue(BCWidget.collabstorage);
            WidgetAPIs.getCollaborativeStorages(function(k) {
                WidgetUwaUtils.processStorages(k, collabStorage);
                WidgetUwaUtils.setStoragesPrefs(widget, k, collabStorage, "true");
                WidgetUwaUtils.processStorageChange.call(PLMLNBOMCompareUX, collabStorage);
            });
            BCWidget.columnAttributes = PLMLNBOMCompareUX.getDefaultColumnAttributes();
            PLMLNBOMCompareUX.setupPreferences();
            LifecycleServicesSettings.app_initialization(function() {
                var SCPromise = LifecycleServices.getSecurityContextPromise(widget.getValue("x3dPlatformId"));
                SCPromise.then(function(SC) {
                    BCWidget.securitycontext = SC;
                    PLMLNBOMCompareUX.initUI();
                })
            });
        },
        initUI: function() {
            widget.setTitle("");

            var widgetBase = UWA.createElement("div", {
                id: "widget-content",
                html: {
                    tag: "div",
                    id: "compare-content",
                    html: [{
                        tag: "div",
                        styles: {
                            width: "100%",
                            height: "100%",
                            position: "relative"
                        },
                        html: [{
                            tag: "div",
                            id: "compare-region-left",
                            styles: {
                                float: "left",
                                height: "100%",
                                width: "100%"
                            },
                            html: [{
                                    tag: "div",
                                    id: "compare-region-header",
                                    html: [{
                                        tag: "div",
                                        id: "compare-legend",
                                        html: []
                                    }]
                                },
                                {
                                    tag: "div",
                                    id: "compare-region-upperleft",
                                    html: [{
                                        tag: "div",
                                        id: "compare-tree",
                                        html: [{
                                            tag: "div",
                                            id: "model-color-window",
                                            html: [{
                                                tag: "div",
                                                id: "model-color-panel",
                                                display: "flex",
                                                "flex-direction": "row",
                                                html: [{
                                                    tag: "div",
                                                    id: "model-color-left",
                                                    html: []
                                                }, {
                                                    tag: "div",
                                                    id: "model-color-spacer",
                                                    html: []
                                                }, {
                                                    tag: "div",
                                                    id: "model-color-right",
                                                    html: []
                                                }, {
                                                    tag: "div",
                                                    id: "model-color-spacer2",
                                                    html: []
                                                }, ]
                                            }, ]
                                        }, {
                                            tag: "div",
                                            id: "compare-trees",
                                            html: [{
                                                tag: "div",
                                                id: "compare-structure-tree",
                                                html: []
                                            }]
                                        }]
                                    }]
                                }
                            ]
                        }]
                    }]
                }
            });
            widgetBase.inject(widget.body.empty());
            PLMLNBOMCompareUX.createActionBar(true);
            if (widget.getValue("objPhysicalID") === undefined) {
                PLMLNBOMCompareUX.populateDropControl();
            } else {
                if (widget.getValue("column_attributes") !== undefined) {
                    BCWidget.columnAttributes = widget.getValue("column_attributes");
                }
                PLMLNBOMCompareUX.getCachedComapareStructure();
            }
            if (widget.getValue("showDiff") !== undefined) {
                if (widget.getValue("showDiff")) {
                    jQuery('*[data-name="ShowDiff"]').addClass("wux-ui-state-active");
                } else {
                    jQuery('*[data-name="ShowDiff"]').removeClass("wux-ui-state-active");
                }
            } else {
                widget.setValue("showDiff", false);
                jQuery('*[data-name="ShowDiff"]').removeClass("wux-ui-state-active");
            }
        },
        resize: function() {
            jQuery("#widget-content").height(window.innerHeight);
        },
        refresh: function() {
            PLMLNBOMCompareUX.initUI();
        },
        setupPreferences: function() {
            var bomExpandLevelPref = {
                type: "range",
                name: "bomExpandLevel",
                label: "BOM Expand Level",
                defaultValue: widget.getValue("bomExpandLevel") || 5,
                min: 1,
                max: 99,
                step: 1
            };
            widget.addPreference(bomExpandLevelPref);
            var UITreeExpandLevelPref = {
                type: "range",
                name: "UITreeExpandLevel",
                label: "UI Tree Expand Level",
                defaultValue: widget.getValue("UITreeExpandLevel") || 1,
                min: 1,
                max: 99,
                step: 1
            };
            widget.addPreference(UITreeExpandLevelPref);
            var y = {
                name: "attributes_not_shown",
                type: "hidden",
                defaultValue: "",
            };
            widget.addPreference(y);
            var x = {
                name: "column_attributes",
                type: "hidden",
                defaultValue: widget.getValue("column_attributes") || BCWidget.columnAttributes
            };
            widget.addPreference(x);
            var z = {
                name: "compare_status",
                type: "hidden",
                defaultValue: widget.getValue("compare_status") || PLMLNBOMCompareUX.getDefaultCompareStatus()
            };
            widget.addPreference(z);
        },
        onAfterLoad: function() {},
        getBOMComparisonDatafromServicePromise: function(tnr, showDiff) {
            var ServicePromise = new UWApromise(function(resolveFunction, rejectFunction) {
                if (WidgetConfiguration.BC_ENOVIA_REST_SERVICE_URL === "") {
                    alert("Enovia Rest Service URL not defined in Configuration file");
                    rejectFunction("Enovia Rest Service URL not defined in Configuration file");
                } else {
                    var url = WidgetConfiguration.BC_ENOVIA_REST_SERVICE_URL + WidgetConfiguration.BC_BOM_COMPARISON_SERVICE_URL;
                    url += "expandLevel=" + widget.getValue("bomExpandLevel");
                    if (showDiff) {
                        url += "&mode=diffonly";
                    }
                    if (WidgetConfiguration.BC_BOM_COMPARISON_SERVICE_ATTRIBUTES !== null &
                        WidgetConfiguration.BC_BOM_COMPARISON_SERVICE_ATTRIBUTES !== undefined) {
                        url += "&attrs=" + WidgetConfiguration.BC_BOM_COMPARISON_SERVICE_ATTRIBUTES;
                    }
                    if (WidgetConfiguration.BC_BOM_COMPARISON_SERVICE_DRAWING_TYPE !== null &
                        WidgetConfiguration.BC_BOM_COMPARISON_SERVICE_DRAWING_TYPE !== undefined) {
                        url += "&drawingType=" + WidgetConfiguration.BC_BOM_COMPARISON_SERVICE_DRAWING_TYPE;
                    }

                    var xhr = new XMLHttpRequest();
                    xhr.onreadystatechange = function() {
                        if (this.readyState == 4 && this.status == 200) {
                            var responseObj = JSON.parse(this.responseText);
                            resolveFunction(responseObj);
                        } else if (this.readyState == 4 && this.status > 200) {
                            rejectFunction(JSON.parse(this.responseText));
                        }
                    };
                    xhr.open("POST", url);
                    xhr.setRequestHeader("Content-Type", "application/json");
                    xhr.send(JSON.stringify(tnr));
                }
            });
            return ServicePromise;
        },
        getComapareStructure: function(BOMComparisonData) {
            if (BOMComparisonData == null || BOMComparisonData == undefined || BOMComparisonData.bom == null || BOMComparisonData.bom == undefined) {
                throw "Invalid Compare BOM Data. Can not build Compare Structure!";
            }
            PLMLNBOMCompareUX.checkIfBOMTransferredToLN(BOMComparisonData);
            var BOMStructureList = BOMComparisonData.bom;
            if (BOMStructureList.length == 2) {
                if (BOMStructureList[0].source === BCWidget.BOMTree0Source && BOMStructureList[1].source === BCWidget.BOMTree1Source) {
                    PLMLNBOMCompareUX.prepareCompareTree([BOMStructureList[0].structure], [BOMStructureList[1].structure], BCWidget.CompareStructure.left, BCWidget.CompareStructure.right, 0);
                } else if (BOMStructureList[1].source === BCWidget.BOMTree0Source && BOMStructureList[0].source === BCWidget.BOMTree1Source) {
                    PLMLNBOMCompareUX.prepareCompareTree([BOMStructureList[1].structure], [BOMStructureList[0].structure], BCWidget.CompareStructure.left, BCWidget.CompareStructure.right, 0);
                } else {
                    console.log('BOM Structure Source is invalid.');
                }
            } else {
                console.log('BOM Structure is invalid.');
            }
        },
        getCachedComapareStructure: function() {
            Mask.mask(widget.getElement("#compare-content"));
            var fData_cached = widget.getValue("fData_cached");
            if (fData_cached !== undefined) {
                PLMLNBOMCompareUX.populateTreeControl();
                PLMLNBOMCompareUX.getCurrentCompareStattusFromPreference();
                BCWidget.CompareStructure = fData_cached;
                BCWidget.fTree_left = PLMLNBOMCompareUX.createFancyTree("#structure-treetable-left", BCWidget.CompareStructure.left, 0, PLMLNBOMCompareUX.renderColumn).fancytree("getTree");
                BCWidget.fTree_right = PLMLNBOMCompareUX.createFancyTree("#structure-treetable-right", BCWidget.CompareStructure.right, 1, PLMLNBOMCompareUX.renderColumn).fancytree("getTree");
                PLMLNBOMCompareUX.getLegend();
                PLMLNBOMCompareUX.expandCompareTree();
            } else {
                alert("Unable to get cached bom comapare data");
                PLMLNBOMCompareUX.populateDropControl();
            }
            Mask.unmask(widget.getElement("#compare-content"));
        },
        prepareCompareTree: function(BOMStr0, BOMStr1, CMPTree0, CMPTree1) {
            for (var i = 0; i < BOMStr0.length; i++) {
                var node0 = PLMLNBOMCompareUX.getLeftCompareTreeNode(BOMStr0[i]);
                var node1 = PLMLNBOMCompareUX.getRightCompareTreeNode(BOMStr1[i]);
                node1.isRightSideData = true;
                PLMLNBOMCompareUX.compareNode(node0, node1);
                CMPTree0.push(node0);
                CMPTree1.push(node1);

                if (BOMStr0[i].bomLines.length) {
                    PLMLNBOMCompareUX.makeParentNode(node0);
                    PLMLNBOMCompareUX.makeParentNode(node1);
                    PLMLNBOMCompareUX.prepareCompareTree(BOMStr0[i].bomLines, BOMStr1[i].bomLines, node0.children, node1.children);
                }
            }
        },
        getCompareTreeNode: function(BOMNode) {
            if (BOMNode == undefined || BOMNode == null) {
                BOMNode = { "type": BCWidget.missingType, "name": BCWidget.missingName };
            }
            var node = {};
            node.type = BOMNode.type || "";
            node.name = node.title = BOMNode.name || "";
            node.revision = BOMNode.revision || "";
            node.position = BOMNode.position || "";
            node.quantity = BOMNode.qty || "";
            node.key = "";
            node.icon = null;
            node.folder = false;
            node.expanded = false;
            node.matchResult = "";
            node.difference = "";
            node.children = [];
            node.isRightSideData = false;
            node.enoviaOnly = false;
            node.lnONly = false;
            node.attributeDifference = false;
            return node;
        },
        getLeftCompareTreeNode: function(BOMNode) {
            if (BOMNode == undefined || BOMNode == null) {
                BOMNode = { "type": BCWidget.missingType, "name": BCWidget.missingName };
            }
            var node = {};
            node.type = BOMNode.type || "";
            node.name = BOMNode.name || "";
            node.title = BOMNode.name || "";
            node.itemTitle = BOMNode.title || "";
            node.mastership = BOMNode.mastership || "";
            node.revision = BOMNode.revision || "";
            node.drawingNumber = BOMNode.drawingNumber || "";
            if (WidgetConfiguration.BC_ALLOWED_PDM_ITEM_TYPES.indexOf(node.type) > -1) {
                if (node.mastership !== "" && node.mastership !== undefined) {
                    if (node.mastership === "PDM") {
                        node.revision = BOMNode.pdmRevision || "";
                        node.drawingNumber = BOMAttributeComparator.filterRevFromDrawingNumber(node.drawingNumber);
                    }
                }
            }
            node.position = BOMNode.position || "";
            node.quantity = BOMNode.qty || "";
            node.itemType = BOMNode.itemType || "";
            node.releasePurpose = BOMNode.releasePurpose || "";

            node.itemLevel = BOMNode.level || "";
            node.physicalid = BOMNode.physicalid || "";
            node.shortName = BOMNode.shortName || "";
            node.weight = BOMNode.weight || "";
            node.size = BOMNode.size || "";
            node.technicalDesignation = BOMNode.technicalDesignation || "";
            node.material = BOMNode.material || "";
            node.unit = BOMNode.unit || "";
            node.standard = BOMNode.standard || "";
            node.distributionList = BOMNode.distributionList || "";
            node.length = BOMNode.length || "";
            node.width = BOMNode.width || "";
            node.status = BOMNode.status || "";
            node.sourceItem = BOMNode.sourceItem || "";
            node.transferToERP = BOMNode.transferToERP || "";
            node.itemCommonText = BOMNode.itemCommonText || "";
            node.itemPurchasingText = BOMNode.itemPurchasingText || "";
            node.bomCommonText = BOMNode.bomCommonText || "";
            node.bomPurchasingText = BOMNode.bomPurchasingText || "";
            node.bomManufacturingText = BOMNode.bomManufacturingText || "";

            node.key = "";
            node.icon = null;
            node.folder = false;
            node.expanded = false;
            node.matchResult = "";
            node.difference = "";
            node.children = [];
            node.isRightSideData = false;
            node.enoviaOnly = false;
            node.lnONly = false;
            node.attributeDifference = false;
            return node;
        },
        getRightCompareTreeNode: function(BOMNode) {
            if (BOMNode == undefined || BOMNode == null) {
                BOMNode = { "type": BCWidget.missingType, "name": BCWidget.missingName };
            }
            var node = {};
            node.type = BOMNode.type || "";
            node.name = node.title = BOMNode.name || "";
            node.revision = BOMNode.revision || "";
            node.position = BOMNode.position || "";
            node.quantity = BOMNode.qty || "";
            node.itemType = BOMNode.itemType || "";
            node.signalCode = BOMNode.signalCode || "";
            node.drawingNumber = BOMNode.drawingNumber || "";
            node.key = "";
            node.icon = null;
            node.folder = false;
            node.expanded = false;
            node.matchResult = "";
            node.difference = "";
            node.children = [];
            node.isRightSideData = false;
            node.enoviaOnly = false;
            node.lnONly = false;
            node.attributeDifference = false;
            return node;
        },
        compareNode: function(node0, node1) {
            BCWidget.nodeCount++;
            node0.key = node1.key = "index_" + BCWidget.nodeCount;
            var missingStatus = PLMLNBOMCompareUX.checkIfItemMissing(node0, node1);
            if (missingStatus === "r") {
                node0.matchResult = "MATCH_MISSING_RIGHT";
                node1.matchResult = "MATCH_MISSING_RIGHT";
                if (!BCWidget.compareStatus.enoviaOnly) {
                    PLMLNBOMCompareUX.updateEnoviaOnlyCompareStatusFromPreference();
                }
            } else if (missingStatus === "l") {
                node0.matchResult = "MATCH_MISSING_LEFT";
                node1.matchResult = "MATCH_MISSING_LEFT";
                if (!BCWidget.compareStatus.lnOnly) {
                    PLMLNBOMCompareUX.updateLNOnlyCompareStatusFromPreference();
                }
            } else {
                var attributeDifferenceList = "";

                if (BOMAttributeComparator.hasNameDifference(node0.name, node1.name)) {
                    attributeDifferenceList = BOMAttributeComparator.addAttributeToDifferenceList("name", attributeDifferenceList);
                    PLMLNBOMCompareUX.updateAttributeDifferenceCompareStatus();
                }

                if (WidgetConfiguration.BC_EXCLUDE_ITEM_TYPES_FROM_REVISION_COMPARE.indexOf(node0.type) === -1) {
                    if (BOMAttributeComparator.hasStringDifference(node0.revision, node1.revision)) {
                        attributeDifferenceList = BOMAttributeComparator.addAttributeToDifferenceList("revision", attributeDifferenceList);
                        if (BCWidget.columnAttributes.revision.visible) {
                            PLMLNBOMCompareUX.updateAttributeDifferenceCompareStatus();
                        }
                    }
                }

                if (BOMAttributeComparator.hasQuantityDifference(node0.quantity, node1.quantity)) {
                    attributeDifferenceList = BOMAttributeComparator.addAttributeToDifferenceList("qty", attributeDifferenceList);
                    if (BCWidget.columnAttributes.qty.visible) {
                        PLMLNBOMCompareUX.updateAttributeDifferenceCompareStatus();
                    }
                }

                if (BOMAttributeComparator.hasItemTypeDifference(node0.itemType, node1.itemType)) {
                    attributeDifferenceList = BOMAttributeComparator.addAttributeToDifferenceList("itemType", attributeDifferenceList);
                    if (BCWidget.columnAttributes.itemType.visible) {
                        PLMLNBOMCompareUX.updateAttributeDifferenceCompareStatus();
                    }
                }

                if (BOMAttributeComparator.hasReleasePurposeSignalCodeDifference(node0.releasePurpose, node1.signalCode)) {
                    attributeDifferenceList = BOMAttributeComparator.addAttributeToDifferenceList("releasePurpose", attributeDifferenceList);
                    attributeDifferenceList = BOMAttributeComparator.addAttributeToDifferenceList("signalCode", attributeDifferenceList);
                    if (BCWidget.columnAttributes.releasePurposeSignalCode.visible) {
                        PLMLNBOMCompareUX.updateAttributeDifferenceCompareStatus();
                    }
                }

                if (BOMAttributeComparator.hasDrawingNumberDifference(node0.drawingNumber, node1.drawingNumber)) {
                    attributeDifferenceList = BOMAttributeComparator.addAttributeToDifferenceList("drawingNumber", attributeDifferenceList);
                    if (BCWidget.columnAttributes.drawingNumber.visible) {
                        PLMLNBOMCompareUX.updateAttributeDifferenceCompareStatus();
                    }
                }

                if (!attributeDifferenceList.length) {
                    node0.matchResult = node1.matchResult = "MATCH_OK";
                } else {
                    node0.matchResult = node1.matchResult = "MATCH_DIFFERENCE";
                    node0.difference = node1.difference = attributeDifferenceList;
                }
            }
        },
        compareName: function(name1, name2) {
            return typeof name1 === 'string' && typeof name2 === 'string' ?
                name1.localeCompare(name2, undefined, { sensitivity: 'accent' }) !== 0 :
                name1 !== name2;
        },
        checkIfItemMissing: function(node0, node1) {
            var missingStatus = "";
            if (node0.type === BCWidget.missingType && node0.name === BCWidget.missingName) {
                missingStatus = "l";
                PLMLNBOMCompareUX.clearNodeProperties(node0);
            } else if (node1.type === BCWidget.missingType && node1.name === BCWidget.missingName) {
                missingStatus = "r";
                PLMLNBOMCompareUX.clearNodeProperties(node1);
            }
            return missingStatus;
        },
        clearNodeProperties: function(node) {
            node.type = node.name = node.title = node.revision = node.position = node.quantity = "";
        },
        makeParentNode: function(node) {
            node.folder = true;
        },
        checkIfBOMTransferredToLN: function(BOMComparisonData) {
            var LNStructureIsValid = true;
            if (BOMComparisonData.bom[1].source == BCWidget.BOMTree1Source) {
                BOMComparisonData.bom[1].structure == null ? LNStructureIsValid = false : LNStructureIsValid = true;
            } else if (BOMComparisonData.bom[0].source == BCWidget.BOMTree1Source) {
                BOMComparisonData.bom[0].structure == null ? LNStructureIsValid = false : LNStructureIsValid = true;
            }
            if (!LNStructureIsValid) {
                throw "BOM Not Available in LN!";
            }
        },
        expandCompareTree: function() {
			var maxBOMExpandLevel = parseInt(PreferenceUtil.get("bomExpandLevel"));
            var maxNodeExpandLevel = parseInt(PreferenceUtil.get("UITreeExpandLevel"));
			var expandLevel = maxNodeExpandLevel > maxBOMExpandLevel ? maxBOMExpandLevel : maxNodeExpandLevel;
            jQuery("#structure-treetable-left").fancytree("getRootNode").visit(function(node) {
                if (node.getLevel() <= expandLevel) {
                    node.setExpanded(true);
                }
            });
            jQuery("#structure-treetable-right").fancytree("getRootNode").visit(function(node) {
                if (node.getLevel() <= expandLevel) {
                    node.setExpanded(true);
                }
            });
        },
        isNullOrEmpty: function(property) {
            if (property === null || property === undefined || property === "") {
                return true;
            } else {
                return false;
            }
        },
        createFancyTree: function(treeID, source, nodeColumnIdx, renderColumnFunction) {
            return jQuery(treeID).fancytree({
                extensions: ["table"],
                checkbox: false,
                table: {
                    indentation: 20,
                    nodeColumnIdx: nodeColumnIdx
                },
                imagePath: null,
                source: source,
                renderColumns: renderColumnFunction,
                activate: PLMLNBOMCompareUX.onActivate,
                expand: PLMLNBOMCompareUX.onExpand,
                collapse: PLMLNBOMCompareUX.onCollapse,
            });
        },
        onActivate: function(event, fNode) {
            var Q = null;
            var R = null;
            var T = PLMLNBOMCompareUX.getOtherTree(fNode.tree);
            var O = T.getNodeByKey(fNode.node.key);
            var N = null;
            jQuery(fNode.node.tr).addClass("info");
            if (fNode.tree._id === BCWidget.fTree_left._id) {
                jQuery(fNode.node.tr).addClass("leftActive")
            } else {
                jQuery(fNode.node.tr).addClass("rightActive")
            }
            O.setActive();
            if (fNode.tree._id === BCWidget.fTree_left._id) {
                jQuery(BCWidget.fTree_left.tbody).find("tr:not(.fancytree-active) > td").css("padding-bottom", "");
                jQuery(BCWidget.fTree_right.tbody).find("tr:not(.fancytree-active) > td").css("padding-bottom", "");
                Q = jQuery(fNode.node.tr);
                var P = Q.find(".title-field").height();
                N = jQuery("#compare-structure-tree").scrollTop() + Q.position().top + P;
            }
        },
        setExpanded: function(fNode, setExpended) {
            var S, P;
            if (fNode.originalEvent != undefined) {
                S = PLMLNBOMCompareUX.getOtherTree(fNode.tree);
                P = S.getNodeByKey(fNode.node.key);
                P.setExpanded(setExpended);
            }
            var Q = jQuery("#structure-treetable-right").height();
            var O = jQuery("#structure-treetable-left").height();
            if (Q < O) {
                Q = O
            }
        },
        onExpand: function(event, fNode) {
            PLMLNBOMCompareUX.setExpanded(fNode, true);
        },
        onCollapse: function(event, fNode) {
            PLMLNBOMCompareUX.setExpanded(fNode, false);
        },
        renderColumn: function(event, TreeNode) {
            var columnAttributeVisibility = BCWidget.columnAttributes;
            var node = TreeNode.node;
            var data = node.data;
            var tableRow = node.tr;
            var tableColumn = jQuery(tableRow).find(">td");
            var columnIdx = 0;
            if (data.isRightSideData) {
                var differenceStatusIdx = columnIdx++;
            }
            var nameIdx = columnIdx++;
            if (!data.isRightSideData) {
                var titleIdx = columnIdx++;
                jQuery(tableRow).attr("bomLevel", data.itemLevel);
            }
            if (columnAttributeVisibility.revision.visible) {
                var revisionIdx = columnIdx++;
            }
            if (columnAttributeVisibility.position.visible) {
                var positionIdx = columnIdx++;
            }
            if (columnAttributeVisibility.qty.visible) {
                var quantityIdx = columnIdx++;
            }
            if (columnAttributeVisibility.releasePurposeSignalCode.visible) {
                var releasePurposeSignalCodeIdx = columnIdx++;
            }
            if (columnAttributeVisibility.itemType.visible) {
                var itemTypeIdx = columnIdx++;
            }
            if (columnAttributeVisibility.drawingNumber.visible) {
                var drawingNumberIdx = columnIdx++;
            }

            if (columnAttributeVisibility.transferToERP && columnAttributeVisibility.transferToERP.visible) {
                var transferToERPIdx = columnIdx++;
            }
            if (columnAttributeVisibility.distributionList && columnAttributeVisibility.distributionList.visible) {
                var distributionListIdx = columnIdx++;
            }
            if (columnAttributeVisibility.weight && columnAttributeVisibility.weight.visible) {
                var weightdx = columnIdx++;
            }
            if (columnAttributeVisibility.size && columnAttributeVisibility.size.visible) {
                var sizeIdx = columnIdx++;
            }
            if (columnAttributeVisibility.length && columnAttributeVisibility.length.visible) {
                var lengthIdx = columnIdx++;
            }
            if (columnAttributeVisibility.width && columnAttributeVisibility.width.visible) {
                var widthIdx = columnIdx++;
            }

            if (data.isRightSideData) {
                jQuery(tableColumn.eq(differenceStatusIdx)).addClass("change-field");
                var differenceStatus = "no-diff";
                if (data.matchResult === "MATCH_MISSING_RIGHT") {
                    differenceStatus = "left-only";
                } else if (data.matchResult === "MATCH_MISSING_LEFT") {
                    differenceStatus = "right-only";
                }
                jQuery(tableColumn.eq(differenceStatusIdx)).html('<span class="diff-type ' + differenceStatus + '"></span>');
            }

            jQuery(tableColumn.eq(nameIdx)).addClass("title-field");
            jQuery(tableColumn.eq(nameIdx)).find(".fancytree-icon").remove();
            if (data.type === "" && data.name === "" && data.revision === "") {
                jQuery(tableColumn.eq(nameIdx)).find(".fancytree-expander").remove();
            }

            if (!data.isRightSideData) {
                jQuery(tableColumn.eq(titleIdx)).addClass("itemTitle-field text-center");
                jQuery(tableColumn.eq(titleIdx)).html(data.shortName);
            }

            if (columnAttributeVisibility.revision.visible) {
                jQuery(tableColumn.eq(revisionIdx)).addClass("revision-field text-center");
                jQuery(tableColumn.eq(revisionIdx)).html(data.revision);
            }

            if (columnAttributeVisibility.position.visible) {
                jQuery(tableColumn.eq(positionIdx)).addClass("position-field text-center");
                jQuery(tableColumn.eq(positionIdx)).html(data.position);
            }

            if (columnAttributeVisibility.qty.visible) {
                jQuery(tableColumn.eq(quantityIdx)).addClass("quantity-field text-center");
                jQuery(tableColumn.eq(quantityIdx)).html(data.quantity);
            }

            if (columnAttributeVisibility.releasePurposeSignalCode.visible) {
                jQuery(tableColumn.eq(releasePurposeSignalCodeIdx)).addClass("releasePurposeSignalCode-field text-center");
                if (data.isRightSideData) {
                    jQuery(tableColumn.eq(releasePurposeSignalCodeIdx)).html(data.signalCode);
                } else {
                    jQuery(tableColumn.eq(releasePurposeSignalCodeIdx)).html(data.releasePurpose);
                }
            }

            if (columnAttributeVisibility.itemType.visible) {
                jQuery(tableColumn.eq(itemTypeIdx)).addClass("itemType-field text-center");
                jQuery(tableColumn.eq(itemTypeIdx)).html(data.itemType);
            }

            if (columnAttributeVisibility.drawingNumber.visible) {
                jQuery(tableColumn.eq(drawingNumberIdx)).addClass("drawingNumber-field text-center");
                jQuery(tableColumn.eq(drawingNumberIdx)).html(data.drawingNumber);
            }

            if (!data.isRightSideData) {

                if (columnAttributeVisibility.transferToERP && columnAttributeVisibility.transferToERP.visible) {
                    jQuery(tableColumn.eq(transferToERPIdx)).addClass("transferToERP-field text-center");
                    jQuery(tableColumn.eq(transferToERPIdx)).html(data.transferToERP);
                }

                if (columnAttributeVisibility.distributionList && columnAttributeVisibility.distributionList.visible) {
                    jQuery(tableColumn.eq(distributionListIdx)).addClass("distributionList-field text-center");
                    jQuery(tableColumn.eq(distributionListIdx)).html(data.distributionList);
                }

                if (columnAttributeVisibility.weight && columnAttributeVisibility.weight.visible) {
                    jQuery(tableColumn.eq(weightdx)).addClass("weight-field text-center");
                    jQuery(tableColumn.eq(weightdx)).html(data.weight);
                }

                if (columnAttributeVisibility.size && columnAttributeVisibility.size.visible) {
                    jQuery(tableColumn.eq(sizeIdx)).addClass("size-field text-center");
                    jQuery(tableColumn.eq(sizeIdx)).html(data.size);
                }

                if (columnAttributeVisibility.length && columnAttributeVisibility.length.visible) {
                    jQuery(tableColumn.eq(lengthIdx)).addClass("length-field text-center");
                    jQuery(tableColumn.eq(lengthIdx)).html(data.length);
                }

                if (columnAttributeVisibility.width && columnAttributeVisibility.width.visible) {
                    jQuery(tableColumn.eq(widthIdx)).addClass("width-field text-center");
                    jQuery(tableColumn.eq(widthIdx)).html(data.width);
                }
            }

            if (data.matchResult === "MATCH_DIFFERENCE") {
                var hasDifference = false;

                if (data.difference.indexOf("name") > -1) {
                    jQuery(tableColumn.eq(nameIdx)).addClass("field-difference");
                    hasDifference = true;
                }

                if (columnAttributeVisibility.revision.visible) {
                    if (data.difference.indexOf("revision") > -1) {
                        jQuery(tableColumn.eq(revisionIdx)).addClass("field-difference");
                        hasDifference = true;
                    }
                }

                if (columnAttributeVisibility.qty.visible) {
                    if (data.difference.indexOf("qty") > -1) {
                        jQuery(tableColumn.eq(quantityIdx)).addClass("field-difference");
                        hasDifference = true;
                    }
                }

                if (columnAttributeVisibility.itemType.visible) {
                    if (data.difference.indexOf("itemType") > -1) {
                        jQuery(tableColumn.eq(itemTypeIdx)).addClass("field-difference");
                        hasDifference = true;
                    }
                }

                if (columnAttributeVisibility.releasePurposeSignalCode.visible) {
                    if (data.difference.indexOf("signalCode") > -1 && data.difference.indexOf("releasePurpose") > -1) {
                        jQuery(tableColumn.eq(releasePurposeSignalCodeIdx)).addClass("field-difference");
                        hasDifference = true;
                    }
                }

                if (columnAttributeVisibility.drawingNumber.visible) {
                    if (data.difference.indexOf("drawingNumber") > -1) {
                        jQuery(tableColumn.eq(drawingNumberIdx)).addClass("field-difference");
                        hasDifference = true;
                    }
                }

                if (hasDifference) {
                    var differenceStatus = "attr-diff";
                    jQuery(tableColumn.eq(differenceStatusIdx)).html('<span class="diff-type ' + differenceStatus + '"></span>');
                }
            }
        },
        getCurrentCompareStattusFromPreference: function() {
            BCWidget.compareStatus = widget.getValue("compare_status");
        },
        updateEnoviaOnlyCompareStatusFromPreference: function() {
            BCWidget.compareStatus.enoviaOnly = true;
        },
        updateLNOnlyCompareStatusFromPreference: function() {
            BCWidget.compareStatus.lnOnly = true;
        },
        updateAttributeDifferenceCompareStatus: function() {
            BCWidget.compareStatus.attributeDifference = true;
        },
        clearCompareStatus: function() {
            BCWidget.compareStatus = PLMLNBOMCompareUX.getDefaultCompareStatus();
        },
        getDefaultCompareStatus: function() {
            return { enoviaOnly: false, lnOnly: false, attributeDifference: false };
        },
        createActionBar: function(activeStatus) {
            var getIconURL = WidgetConfiguration.BC_SEPARATE_INTEGRATION_SERVER_3DSPACE_URL + "/webapps/PLMLNBOMCompareUX/assets/img/32/";
            var getTenent = "tenent=" + "OnPremise";
            var opacity = activeStatus ? ' style="opacity: 1; pointer-events: auto;">' : ' style="opacity: 0.2; pointer-events: none;">';
            var O = 'style="width: 32px; height: 32px; line-height: 32px; display: inline-block; vertical-align: top; background-size: cover; ';
            var actionBar = '<ul class="Sections">';
            actionBar += '';
            actionBar += '';
            actionBar += '  <div style="display: inline-block;">';
            actionBar += '      <ul class="wux-afr-actionbar-sections-tabs">';
            actionBar += '          <li class="wux-actionbar-section-tab wux-ui-state-active" data-sectionname="Compare" data-sectionid="0">' + "BOM Comparison" + "</li>";
            actionBar += "      </ul>";
            actionBar += "  </div>";
            actionBar += '  <div class="wux-ui-chevron" style="display: inline-block;">';
            actionBar += '      <span class="wux-ui-icon-chevron"  line-height: 20px; vertical-align: middle; height: 20px;"></span>';
            actionBar += "  </div>";
            actionBar += "  <div></div>";
            actionBar += '  <div class="wux-afr wux-afr-view wux-afr-state-nolabels" style="display: inline-block; min-width: 179px;">';
            actionBar += '      <div class="wux-afr-border-container">';
            actionBar += '          <div class="wux-afr-cmdWorkbench" style="display: inline-block;">';
            actionBar += '              <div class="wux-afr-cmdContainer" data-name="Compare" data-representationtype="AfrActionBarSection" style="transform: translate3d(0px, 0px, 0px);">';

            actionBar += '                  <div class="wux-afr-cmdstarter" data-name="ShowDiff" data-command="ShowDiffHdr" data-title=' + '"Show Difference Only"' + ' title=' + '"Show Difference Only"' + ' style="opacity: 1; pointer-events: auto;">';
            actionBar += '                      <div class=""       id="DiffButton"   ' + O + " background-image: url(" + getIconURL + "I_Difference.png?" + getTenent + '); "></div>';
            actionBar += '                      <div class="wux-afr-label">' + "Show Difference Only" + "</div>";
            actionBar += "                  </div>";

            actionBar += '                  <div class="wux-afr-cmdSeparator" style="display: inline-block;"></div>';
            actionBar += '                  <div class="wux-afr-cmdstarter" data-name="ToggleAttr" data-command="ToggleAttrHdr" data-title=' + '"Toggle Attribute"' + ' title=' + '"Toggle Attribute"' + ' style="opacity: 0.2; pointer-events: auto;">';
            actionBar += '                      <div class=""       id="AttrButton"   ' + O + " background-image: url(" + getIconURL + "I_Properties.png?" + getTenent + '); "></div>';
            actionBar += '                      <div class="wux-afr-label">' + "Toggle Attribute" + "</div>";
            actionBar += "                  </div>";

            actionBar += '                  <div class="wux-afr-cmdstarter" data-name="ToggleLegend" data-command="ToggleLegendHdr" data-title=' + '"Toggle Legend"' + ' title=' + '"Toggle Legend"' + ' style="opacity: 0.2; pointer-events: auto;">';
            actionBar += '                      <div class=""       id="LegendButton"   ' + O + " background-image: url(" + getIconURL + "I_Legend.png?" + getTenent + '); "></div>';
            actionBar += '                      <div class="wux-afr-label">' + "Toggle Legend" + "</div>";
            actionBar += "                  </div>";

            actionBar += '                  <div class="wux-afr-cmdSeparator" style="display: inline-block;"></div>';
            actionBar += '                  <div class="wux-afr-cmdstarter" data-name="CustomizeColumns" data-command="CustomizeColumnsHdr" data-title=' + '"Customize Columns"' + ' title=' + '"Customize Columns"' + ' style="opacity: 1; pointer-events: auto;">';
            actionBar += "                      <div " + O + " background-image: url(" + getIconURL + "I_CustomizeTable.png?" + getTenent + '); "></div>';
            actionBar += '                      <div class="wux-afr-label">' + "Customize Columns" + "</div>";
            actionBar += "                  </div>";

            actionBar += '                  <div class="wux-afr-cmdSeparator" style="display: inline-block;"></div>';
            actionBar += '                  <div class="wux-afr-cmdstarter" data-name="DataExport" data-command="DataExportHdr" data-title=' + '"Comparison Data Export"' + ' title=' + '"Comparison Data Export"' + ' style="opacity: 1; pointer-events: auto;">';
            actionBar += "                      <div " + O + " background-image: url(" + getIconURL + "Compare_export.png?" + getTenent + '); "></div>';
            actionBar += '                      <div class="wux-afr-label">' + "Comparison Data Export" + "</div>";
            actionBar += "                  </div>";

            actionBar += '                  <div class="wux-afr-cmdstarter" data-name="Refresh" data-command="RefreshHdr" data-title=' + '"Refresh"' + ' title=' + '"Get Latest BOM Changes"' + opacity;
            actionBar += "                      <div " + O + " background-image: url(" + getIconURL + "I_Refresh.png?" + getTenent + '); "></div>';
            actionBar += '                      <div class="wux-afr-label">' + "Get Latest BOM Changes" + "</div>";
            actionBar += "                  </div>";

            actionBar += '                  <div class="wux-afr-cmdSeparator" style="display: inline-block;"></div>';
            actionBar += '                  <div class="wux-afr-cmdstarter" data-name="RemoveAll" data-command="RemoveAllHdr" data-title=' + '"Remove"' + ' title=' + '"Remove"' + opacity;
            actionBar += "                      <div " + O + " background-image: url(" + getIconURL + "I_RemoveAll.png?" + getTenent + '); "></div>';
            actionBar += '                      <div class="wux-afr-label">' + "Remove" + "</div>";
            actionBar += "                  </div>";

            actionBar += "              </div>";
            actionBar += "          </div>";
            actionBar += "      </div>";
            actionBar += "  </div>";
            actionBar += "</ul>";

            var actionControl = UWA.createElement("div", {
                id: "compare-actionBar",
                "class": "AfrActionBar wux-afr wux-afr-actionbar wux-afr-actionbar-top-section wux-ui-is-rendered",
                "data-open": "true",
                style: "width: 50%",
                html: actionBar
            }).inject(widget.getElement("#compare-content"));

            jQuery("#compare-actionBar").ready(function() {
                var diffButton = jQuery('*[data-name="ShowDiff"]');
                jQuery(diffButton).click(function() {
                    PLMLNBOMCompareUX.showDiffOnly();
                });

                var attributeButton = jQuery('*[data-name="ToggleAttr"]');
                jQuery(attributeButton).click(function() {
                    PLMLNBOMCompareUX.doToggleAttr();
                });

                var legendButton = jQuery('*[data-name="ToggleLegend"]');
                jQuery(legendButton).click(function() {
                    PLMLNBOMCompareUX.doToggleLegend();
                });


                var customizeColumnsButton = jQuery('*[data-name="CustomizeColumns"]');
                jQuery(customizeColumnsButton).click(function() {
                    PLMLNBOMCompareUX.filterColumnAttributes();
                });

                var dataExportButton = jQuery('*[data-name="DataExport"]');
                jQuery(dataExportButton).click(function() {
                    CompareDataExporter.exportUI();
                });

                var refreshButton = jQuery('*[data-name="Refresh"]');
                jQuery(refreshButton).click(function() {
                    PLMLNBOMCompareUX.doRefresh()
                });

                var removeButton = jQuery('*[data-name="RemoveAll"]');
                jQuery(removeButton).click(function() {
                    PLMLNBOMCompareUX.removeAll();
                });

                var T = jQuery("#compare-actionBar .wux-ui-chevron");
                jQuery(T).click(function() {
                    var ae = jQuery("#compare-actionBar");
                    if ((ae.length == 1) && (ae[0].hasAttribute("data-open"))) {
                        if (ae[0].getAttribute("data-open") == "true") {
                            var af = jQuery("#compare-actionBar .wux-afr-state-nolabels");
                            af.css("display", "none");
                            var ad = jQuery("#compare-actionBar .wux-afr-actionbar-sections-tabs");
                            ad.css("display", "none");
                            ae[0].setAttribute("data-open", "false")
                        } else {
                            var af = jQuery("#compare-actionBar .wux-afr-state-nolabels");
                            af.css("display", "inline-block");
                            var ad = jQuery("#compare-actionBar .wux-afr-actionbar-sections-tabs");
                            ad.css("display", "");
                            ae[0].setAttribute("data-open", "true")
                        }
                    }
                })
            });
        },
        showDiffOnly: function() {
            var tnr = widget.getValue("objTNR");
            if (tnr !== undefined) {
                if (widget.getValue("showDiff") !== undefined) {
                    if (widget.getValue("showDiff")) {
                        widget.setValue("showDiff", false);
                        jQuery('*[data-name="ShowDiff"]').removeClass("wux-ui-state-active");
                    } else {
                        widget.setValue("showDiff", true);
                        jQuery('*[data-name="ShowDiff"]').addClass("wux-ui-state-active");
                    }
                } else {
                    widget.setValue("showDiff", true);
                    jQuery('*[data-name="ShowDiff"]').addClass("wux-ui-state-active");
                }
                PLMLNBOMCompareUX.doRefresh();
            }
        },
        doToggleAttr: function() {},
        doToggleLegend: function() {},
        filterColumnAttributes: function() {
            if (PLMLNBOMCompareUX.filterColumnAttributesControl == undefined) {
                var modalBody = document.createElement("div");
                modalBody.className = "filter-columns-popup";
                modalBody.innerHTML = "Select Compare Column Visibility";

                var modalFooter = document.createElement("div");

                var columnSelectionModal = new ENOFloatingPanel({
                    className: "filter-column-modal",
                    title: "Filter Columns",
                    body: modalBody,
                    footer: modalFooter,
                    overlay: false,
                    closable: true,
                    animate: true,
                    resizable: true,
                    autocenter: true,
                    visible: true,
                    events: {
                        onResizeFloating: function() {
                            if (PLMLNBOMCompareUX.filterColumnAttributesControl != null && PLMLNBOMCompareUX.filterColumnAttributesControl.columnSelectionModal != null && PLMLNBOMCompareUX.filterColumnAttributesControl.columnSelectionModal.elements != null) {
                                PLMLNBOMCompareUX.filterColumnAttributesControl.columnSelectionModal.centerIt();
                                PLMLNBOMCompareUX.filterColumnAttributesControl.resizeForScrolling();
                            }
                        },
                        onShow: function() {
                            PLMLNBOMCompareUX.updateActionBarFilterColumnButtonState(false);
                        },
                        onHide: function() {
                            PLMLNBOMCompareUX.updateActionBarFilterColumnButtonState(true);
                        },
                        onClose: function() {
                            PLMLNBOMCompareUX.doCloseCustomizeColumns();
                        }
                    }
                });
                var columnAttributeList = PLMLNBOMCompareUX.getAttributeListFromTable();

                PLMLNBOMCompareUX.filterColumnAttributesControl = new FilterColumns(modalBody, modalFooter, columnAttributeList, {
                    context: PLMLNBOMCompareUX.context,
                    getAttributeDisplayOption: PLMLNBOMCompareUX.getAttributeDisplayOption,
                    getDefaultShowAttributes: PLMLNBOMCompareUX.getDefaultShowAttributes,
                    getDontShowAttributes: PLMLNBOMCompareUX.getDontShowAttributes,
                    setAttributeDisplayOptions: PLMLNBOMCompareUX.setAttributeDisplayOptions,
                    events: {
                        onReady: function(L) {
                            columnSelectionModal.inject(document.body);
                            var modalOpions = {
                                x: 5
                            };
                            columnSelectionModal.setNewSizeWithDiff(modalOpions);
                        },
                        onModification: function(L) {
                            PLMLNBOMCompareUX.updateTableRowVisibility();
                        },
                        onClose: function(L) {
                            PLMLNBOMCompareUX.doCloseCustomizeColumns();
                        }
                    }
                }).inject(modalBody);
                PLMLNBOMCompareUX.filterColumnAttributesControl.columnSelectionModal = columnSelectionModal;
            } else {
                PLMLNBOMCompareUX.filterColumnAttributesControl.columnSelectionModal.show();
            }
        },
        getAttributeListFromTable: function() {
            var o = WidgetConfiguration.BC_SELECTION_BOX_VISLIBITY_LIST;
            return o;
        },
        updateActionBarFilterColumnButtonState: function(isVisible) {
            var H = jQuery('*[data-name="CustomizeColumns"]');
            if (isVisible) {
                H.css("opacity", "1.0");
                H.css("pointer-events", "auto")
            } else {
                H.css("opacity", "0.2");
                H.css("pointer-events", "none")
            }
        },
        updateTableRowVisibility: function() {
            PLMLNBOMCompareUX.getColumnAttributes();
            PLMLNBOMCompareUX.doRefresh();
        },
        getColumnAttributes: function() {
            var displayOption = widget.getValue("attributes_display_option");
            if ((displayOption === undefined) || (displayOption === null) || (displayOption === "")) {
                BCWidget.columnAttributes = PLMLNBOMCompareUX.getDefaultColumnAttributes();
            } else if (displayOption == "default") {
                BCWidget.columnAttributes = PLMLNBOMCompareUX.getDefaultColumnAttributes();
            } else if (displayOption == "all") {
                BCWidget.columnAttributes = PLMLNBOMCompareUX.getAllColumnAttributes();
            } else if (displayOption == "selected") {
                BCWidget.columnAttributes = PLMLNBOMCompareUX.getSelectedColumnAttributes();
            }
            widget.setValue("column_attributes", BCWidget.columnAttributes);
        },
        getDefaultColumnAttributes: function() {
            var attrs = WidgetConfiguration.BC_SELECTION_BOX_VISLIBITY_LIST;
            var defaultAttrs = WidgetConfiguration.BC_SELECTION_BOX_DEFAULT_LIST;
            var columnAttributes = {};
            for (var i = 0; i < attrs.length; i++) {
                columnAttributes[attrs[i].name] = {};
                if (defaultAttrs.indexOf(attrs[i].name) > -1) {
                    columnAttributes[attrs[i].name].visible = true;
                } else {
                    columnAttributes[attrs[i].name].visible = false;
                }
            }
            return columnAttributes;
        },
        getAllColumnAttributes: function() {
            var attrs = WidgetConfiguration.BC_SELECTION_BOX_VISLIBITY_LIST;
            var columnAttributes = {};
            for (var i = 0; i < attrs.length; i++) {
                columnAttributes[attrs[i].name] = {};
                columnAttributes[attrs[i].name].visible = true;
            }
            return columnAttributes;
        },
        getSelectedColumnAttributes: function() {
            var attrs = WidgetConfiguration.BC_SELECTION_BOX_VISLIBITY_LIST;
            var columnAttributes = PLMLNBOMCompareUX.getAllColumnAttributes();
            var removedAttributes = widget.getValue("attributes_not_shown");
            if (removedAttributes == "") {
                return columnAttributes;
            } else {
                for (var i = 0; i < attrs.length; i++) {
                    if (removedAttributes.indexOf(attrs[i].name) > -1) {
                        columnAttributes[attrs[i].name].visible = false;
                    }
                }
                return columnAttributes;
            }
        },
        doCloseCustomizeColumns: function() {
            if ((PLMLNBOMCompareUX.filterColumnAttributesControl.columnSelectionModal !== undefined) && (PLMLNBOMCompareUX.filterColumnAttributesControl.columnSelectionModal !== null)) {
                PLMLNBOMCompareUX.filterColumnAttributesControl.columnSelectionModal.destroy();
                PLMLNBOMCompareUX.updateActionBarFilterColumnButtonState(true);
                delete PLMLNBOMCompareUX.filterColumnAttributesControl
            }
        },
        getAttributeDisplayOption: function() {
            var o = widget.getValue("attributes_display_option");
            if ((o === undefined) || (o === null) || (o === "")) {
                var p = widget.getValue("attributes_not_shown");
                if (p === "__showall__") {
                    return "all"
                } else {
                    if (p === "") {
                        return "default"
                    } else {
                        return "selected"
                    }
                }
            } else {
                return o
            }
        },
        getDefaultShowAttributes: function() {
            var o = WidgetConfiguration.BC_SELECTION_BOX_DEFAULT_LIST;
            return o
        },
        getDontShowAttributes: function() {
            var o = widget.getValue("attributes_not_shown");
            var p = [];
            if (o !== "__showall__") {
                p = o.split("|")
            }
            return p
        },
        setAttributeDisplayOptions: function(o, p) {
            widget.setValue("attributes_display_option", o);
            if (o === "selected") {
                widget.setValue("attributes_not_shown", p)
            }
        },
        doRefresh: function() {
            var tnr = widget.getValue("objTNR");
            if (tnr !== undefined) {
                Mask.mask(widget.getElement("#compare-content"));
                if (widget.getValue("showDiff") !== undefined) {
                    if (widget.getValue("showDiff")) {
                        PLMLNBOMCompareUX.getBOMComparisonDatafromServicePromise(tnr, true).then(function(resolveData) {
                            console.log("%cCalled BOM Comparison Service", "color: #f3e257");
                            console.log("%c Response:", "color: #6ef5ee");
                            console.log(resolveData);
                            PLMLNBOMCompareUX.populateTreeControl();
                            BCWidget.CompareStructure.left = [];
                            BCWidget.CompareStructure.right = [];
                            PLMLNBOMCompareUX.clearCompareStatus();
                            PLMLNBOMCompareUX.getComapareStructure(resolveData);
                            BCWidget.fTree_left = PLMLNBOMCompareUX.createFancyTree("#structure-treetable-left", BCWidget.CompareStructure.left, 0, PLMLNBOMCompareUX.renderColumn).fancytree("getTree");
                            BCWidget.fTree_right = PLMLNBOMCompareUX.createFancyTree("#structure-treetable-right", BCWidget.CompareStructure.right, 1, PLMLNBOMCompareUX.renderColumn).fancytree("getTree");
                            PLMLNBOMCompareUX.getLegend();
                            PLMLNBOMCompareUX.expandCompareTree();
                            Mask.unmask(widget.getElement("#compare-content"));
                            widget.setValue("fData_cached", BCWidget.CompareStructure);
                            widget.setValue("compare_status", BCWidget.compareStatus);
                        }, function(rejectData) {
                            console.log(rejectData);
                        });
                    } else {
                        PLMLNBOMCompareUX.getBOMComparisonDatafromServicePromise(tnr).then(function(resolveData) {
                            console.log("%cCalled BOM Comparison Service", "color: #f3e257");
                            console.log("%c Response:", "color: #6ef5ee");
                            console.log(resolveData);
                            PLMLNBOMCompareUX.populateTreeControl();
                            BCWidget.CompareStructure.left = [];
                            BCWidget.CompareStructure.right = [];
                            PLMLNBOMCompareUX.clearCompareStatus();
                            PLMLNBOMCompareUX.getComapareStructure(resolveData);
                            BCWidget.fTree_left = PLMLNBOMCompareUX.createFancyTree("#structure-treetable-left", BCWidget.CompareStructure.left, 0, PLMLNBOMCompareUX.renderColumn).fancytree("getTree");
                            BCWidget.fTree_right = PLMLNBOMCompareUX.createFancyTree("#structure-treetable-right", BCWidget.CompareStructure.right, 1, PLMLNBOMCompareUX.renderColumn).fancytree("getTree");
                            PLMLNBOMCompareUX.getLegend();
                            PLMLNBOMCompareUX.expandCompareTree();
                            Mask.unmask(widget.getElement("#compare-content"));
                            widget.setValue("fData_cached", BCWidget.CompareStructure);
                            widget.setValue("compare_status", BCWidget.compareStatus);
                        }, function(rejectData) {
                            console.log(rejectData);
                        });
                    }
                }
            }
        },
        removeAll: function() {
            Mask.unmask(widget.getElement("#compare-content"));
            PLMLNBOMCompareUX.populateDropControl();
            widget.setValue("objPhysicalID", undefined);
            widget.setValue("objTNR", undefined);
            widget.setValue("fData_cached", undefined);
            widget.setValue("showDiff", false);
            jQuery('*[data-name="ShowDiff"]').removeClass("wux-ui-state-active");
        },
        getLegend: function() {
            var sourceHTML = '<div style="padding-left:10px; padding-right:10px;"><b>' + "Source" + "</b></div>";
            sourceHTML += '<div class="status-legend-item">';
            sourceHTML += '<div class="diff-type-legend diff-type-' + "Enovia" + '"></div>';
            sourceHTML += '<div class="status-legend-itemtext">' + "ENOVIA" + "</div>";
            sourceHTML += "</div>";
            sourceHTML += '<div class="status-legend-item">';
            sourceHTML += '<div class="diff-type-legend diff-type-' + "LN" + '"></div>';
            sourceHTML += '<div class="status-legend-itemtext">' + "LN" + "</div>";
            sourceHTML += "</div>";
            var compareStatusHTML = "";
            if (BCWidget.compareStatus.enoviaOnly || BCWidget.compareStatus.lnOnly || BCWidget.compareStatus.attributeDifference) {
                compareStatusHTML = '<div style="padding-left:10px; padding-right:10px;"><b>' + "Compare Status" + "</b></div>";
                if (BCWidget.compareStatus.enoviaOnly) {
                    compareStatusHTML += '<div class="status-legend-item">';
                    compareStatusHTML += '<div class="diff-type-legend' + " diff-type left-only" + '"></div>';
                    compareStatusHTML += '<div class="status-legend-itemtext">' + "ENOVIA-only" + "</div>";
                    compareStatusHTML += "</div>";
                }
                if (BCWidget.compareStatus.lnOnly) {
                    compareStatusHTML += '<div class="status-legend-item">';
                    compareStatusHTML += '<div class="diff-type-legend' + " diff-type right-only" + '"></div>';
                    compareStatusHTML += '<div class="status-legend-itemtext">' + "LN-only" + "</div>";
                    compareStatusHTML += "</div>";
                }
                if (BCWidget.compareStatus.attributeDifference) {
                    compareStatusHTML += '<div class="status-legend-item">';
                    compareStatusHTML += '<div class="diff-type-legend' + " diff-type attr-diff" + '"></div>';
                    compareStatusHTML += '<div class="status-legend-itemtext">' + "Attribute-difference" + "</div>";
                    compareStatusHTML += "</div>";
                }
            }
            widget.getElement("#compare-legend").empty().setContent(sourceHTML + compareStatusHTML);
        },
        drophandler: function(data) {
            if (data.length > 1) {
                alert("Drop single item only!");
            } else {
                Mask.mask(widget.getElement("#compare-content"));
                var obj = data[0];
                console.log("%cDropped Item Data:", "color: #f3e257");
                console.log("%c Physical ID: " + obj.objectId, "color: #6ef5ee");
                console.log(data);

                if (PLMLNBOMCompareUX.isItemTypeAllowedAsTopItem(obj.objectType)) {
                    PLMLNBOMCompareUX.getObjectInfo(obj.objectId).then(function(resolveData) {
                        widget.setValue("objTNR", resolveData);
                        PLMLNBOMCompareUX.getBOMComparisonDatafromServicePromise(resolveData).then(function(resolveData) {
                            console.log("%cCalled BOM Comparison Service", "color: #f3e257");
                            console.log("%c Response:", "color: #6ef5ee");
                            console.log(resolveData);
                            PLMLNBOMCompareUX.populateTreeControl();
                            BCWidget.CompareStructure.left = [];
                            BCWidget.CompareStructure.right = [];
                            widget.setValue("showDiff", false);
                            jQuery('*[data-name="ShowDiff"]').removeClass("wux-ui-state-active");
                            try {
                                PLMLNBOMCompareUX.clearCompareStatus();
                                PLMLNBOMCompareUX.getComapareStructure(resolveData);
                                PLMLNBOMCompareUX.populateLeftTreePromise().then(function(resolveData) {
                                    BCWidget.fTree_left = resolveData;
                                    PLMLNBOMCompareUX.populateRightTreePromise().then(function(resolveData) {
                                        BCWidget.fTree_right = resolveData;
                                        PLMLNBOMCompareUX.populateLegendPromise().then(function() {
                                            PLMLNBOMCompareUX.expandTreePromise().then(function() {
                                                Mask.unmask(widget.getElement("#compare-content"));
                                                widget.setValue("objPhysicalID", obj.objectId);
                                                widget.setValue("fData_cached", BCWidget.CompareStructure);
                                                widget.setValue("compare_status", BCWidget.compareStatus);
                                            });
                                        });
                                    });
                                });
                            } catch (error) {
                                if (error === "BOM Not Available in LN!") {
                                    alert(error);
                                }
                                console.error(error);
                                PLMLNBOMCompareUX.populateDropControl();
                                widget.setValue("objPhysicalID", undefined);
                                widget.setValue("fData_cached", undefined);
                                widget.setValue("objTNR", undefined);
                                Mask.unmask(widget.getElement("#compare-content"));
                            }
                        }, function(rejectData) {
                            console.error(rejectData);
                            console.error("Invalid Compare BOM Data. Can not build Compare Structure!");
                            PLMLNBOMCompareUX.populateDropControl();
                            widget.setValue("objPhysicalID", undefined);
                            widget.setValue("fData_cached", undefined);
                            widget.setValue("objTNR", undefined);
                            Mask.unmask(widget.getElement("#compare-content"));
                            if (rejectData.status !== undefined) {
                                if (rejectData.status === "FAILED") {
                                    if (rejectData.systemErrors !== undefined) {
                                        if (rejectData.systemErrors.length) {
                                            if (rejectData.systemErrors[0] === "LN service returned null.") {
                                                console.log(rejectData.systemErrors[0]);
                                                alert("BOM is not transferred to LN");
                                            }else if(rejectData.systemErrors[0] === "Object Not Found in Enovia."){
                                                console.log(rejectData.systemErrors[0]);
                                                alert("Object Not Found in Enovia");
                                            }                   
                                        }
                                    }
                                }
                            } else {
                                console.log("System can not get proper response from Service");
                                alert("System doesn't getting proper Data from Service.. Please try again");
                            }
                        });
                    }, function(rejectData) {
                        console.log(rejectData);
                    });
                } else {
                    alert("Item of this type is not allowed!");
                    Mask.unmask(widget.getElement("#compare-content"));
                }
            }
        },
        isItemTypeAllowedAsTopItem: function(itemType) {
            var allowedTopItemTypes = WidgetConfiguration.BC_ALLOWED_TOP_ITEM_TYPES;
            if (allowedTopItemTypes.indexOf(itemType) > -1) {
                return true;
            }
            return false;
        },
        populateLeftTreePromise: function() {
            var treePopulationPromise = new UWApromise(function(reseolveFunction, rejectFunction) {
                var treeData = PLMLNBOMCompareUX.createFancyTree("#structure-treetable-left", BCWidget.CompareStructure.left, 0, PLMLNBOMCompareUX.renderColumn).fancytree("getTree");
                reseolveFunction(treeData);
                rejectFunction("");
            });
            return treePopulationPromise;
        },
        populateRightTreePromise: function() {
            var treePopulationPromise = new UWApromise(function(reseolveFunction, rejectFunction) {
                var treeData = PLMLNBOMCompareUX.createFancyTree("#structure-treetable-right", BCWidget.CompareStructure.right, 1, PLMLNBOMCompareUX.renderColumn).fancytree("getTree");
                reseolveFunction(treeData);
                rejectFunction("");
            });
            return treePopulationPromise;
        },
        populateLegendPromise: function() {
            var legendPopulationPromise = new UWApromise(function(reseolveFunction, rejectFunction) {
                PLMLNBOMCompareUX.getLegend();
                reseolveFunction("");
                rejectFunction("");
            });
            return legendPopulationPromise;
        },
        expandTreePromise: function() {
            var expandTreePromise = new UWApromise(function(reseolveFunction, rejectFunction) {
                PLMLNBOMCompareUX.expandCompareTree();
                reseolveFunction("");
                rejectFunction("");
            });
            return expandTreePromise;
        },
        populateDropControl: function() {
            jQuery("#model-color-left").addClass("ft-drop-cue-on");
            jQuery("#model-color-right").addClass("ft-drop-cue-on");
            var legendControl = widget.getElement("#compare-legend");
            legendControl.empty();
            var ComapareBaseControl = widget.getElement("#compare-structure-tree");
            ComapareBaseControl.setContent(jQuery('<div id="initial-drop"><div class="drop-label"><span></span></div><div id="drop-targets"><div id="left-container"><div id="left-rev-list" class="rev-list" hidden="true"></div><div id="drop-target-left" class="drop-target"><div id="drop-target-left-iconlabel" class="drop-icon-label"><span id="drop-target-left-icon" class="fonticon fonticon-2x fonticon-download"></span><br>Drop content here</div></div></div></div></div>'));
            DnD.setProcessingCB(this, this.drophandler);
            DnD.attachObject("drop-target-left", this, this.dragEnterHighlight, this.dragLeaveDeHighlight);
        },
        populateTreeControl: function() {
            jQuery("#model-color-left").removeClass("ft-drop-cue-on");
            jQuery("#model-color-right").removeClass("ft-drop-cue-on");
            var ComapareBaseControl = widget.getElement("#compare-structure-tree");
            ComapareBaseControl.setContent(jQuery('<div id="compare-tree-left">' +
                '<table class="table table-striped table-condensed" id="structure-treetable-left">' +
                '<thead>' +
                '<tr>' +
                '<th>Name</th>' +
                '<th class="itemTitle-header-left text-center">Title</th>' +
                '<th class="revision-header-left text-center">Rev.</th>' +
                '<th class="position-header-left text-center">Pos.</th>' +
                '<th class="qty-header-left text-center">Qty</th>' +
                '<th class="releasePurpose-header-left text-center">Rel. Purpose</th>' +
                '<th class="itemType-header-left text-center">Item Type</th>' +
                '<th class="drawingNumber-header-left text-center">Drw. Nr.</th>' +
                '<th class="transferToERP-header-left text-center">Trans. to ERP</th>' +
                '<th class="distributionList-header-left text-center">Dist. List</th>' +
                '<th class="weight-header-left text-center">Weight</th>' +
                '<th class="size-header-left text-center">Size</th>' +
                '<th class="length-header-left text-center">Length</th>' +
                '<th class="width-header-left text-center">Width</th>' +
                '</tr>' +
                '</thead>' +
                '<tbody>' +
                '<tr><td></td></tr>' +
                '</tbody>' +
                '</table>' +
                '</div>' +
                '<div id="compare-tree-column-separator"></div>' +
                '<div id="compare-tree-right">' +
                '<table class="table table-condensed table-striped" id="structure-treetable-right" >' +
                '<thead>' +
                '<tr>' +
                '<th class="diff-status-header"></th>' +
                '<th>Name</th>' +
                '<th class="revision-header-right text-center">Rev.</th>' +
                '<th class="position-header-right text-center">Pos.</th>' +
                '<th class="qty-header-right text-center">Qty</th>' +
                '<th class="itemSignal-header-right text-center">Item Signal</th>' +
                '<th class="itemType-header-right text-center">Item Type</th>' +
                '<th class="drawingNumber-header-right text-center">Drw. Nr.</th>' +
                '</tr>' +
                '</thead>' +
                '<tbody>' +
                '<tr><td></td></tr>' +
                '</tbody>' +
                '</table>' +
                '</div>'));
            DnD.setProcessingCB(this, this.drophandler);
            DnD.attachObject("compare-tree-left", this, function() {
                jQuery("#structure-treetable-left").addClass("ft-drop-cue-on");
                jQuery("#model-color-left").addClass("ft-drop-cue-on");
                jQuery("#structure-treetable-right").addClass("ft-drop-cue-on");
                jQuery("#model-color-right").addClass("ft-drop-cue-on")
            }, function() {
                jQuery("#structure-treetable-left").removeClass("ft-drop-cue-on");
                jQuery("#model-color-left").removeClass("ft-drop-cue-on");
                jQuery("#structure-treetable-right").removeClass("ft-drop-cue-on");
                jQuery("#model-color-right").removeClass("ft-drop-cue-on")
            });
            DnD.attachObject("compare-tree-right", this, function() {
                jQuery("#structure-treetable-left").addClass("ft-drop-cue-on");
                jQuery("#model-color-left").addClass("ft-drop-cue-on");
                jQuery("#structure-treetable-right").addClass("ft-drop-cue-on");
                jQuery("#model-color-right").addClass("ft-drop-cue-on")
            }, function() {
                jQuery("#structure-treetable-left").removeClass("ft-drop-cue-on");
                jQuery("#model-color-left").removeClass("ft-drop-cue-on");
                jQuery("#structure-treetable-right").removeClass("ft-drop-cue-on");
                jQuery("#model-color-right").removeClass("ft-drop-cue-on")
            });
            PLMLNBOMCompareUX.removeColumnBasedOnAttribute();
        },
        removeColumnBasedOnAttribute: function() {
            var columnAttributeVisibility = widget.getValue("column_attributes");
            if (!columnAttributeVisibility.revision.visible) {
                jQuery(".revision-header-right").remove();
                jQuery(".revision-header-left").remove();
            }
            if (!columnAttributeVisibility.position.visible) {
                jQuery(".position-header-right").remove();
                jQuery(".position-header-left").remove();
            }
            if (!columnAttributeVisibility.qty.visible) {
                jQuery(".qty-header-right").remove();
                jQuery(".qty-header-left").remove();
            }
            if (!columnAttributeVisibility.releasePurposeSignalCode.visible) {
                jQuery(".releasePurpose-header-left").remove();
                jQuery(".itemSignal-header-right").remove();
            }
            if (!columnAttributeVisibility.itemType.visible) {
                jQuery(".itemType-header-right").remove();
                jQuery(".itemType-header-left").remove();
            }
            if (!columnAttributeVisibility.drawingNumber.visible) {
                jQuery(".drawingNumber-header-right").remove();
                jQuery(".drawingNumber-header-left").remove();
            }
            if (columnAttributeVisibility.transferToERP === undefined || !columnAttributeVisibility.transferToERP.visible) {
                jQuery(".transferToERP-header-left").remove();
            }
            if (columnAttributeVisibility.distributionList === undefined || !columnAttributeVisibility.distributionList.visible) {
                jQuery(".distributionList-header-left").remove();
            }
            if (columnAttributeVisibility.weight === undefined || !columnAttributeVisibility.weight.visible) {
                jQuery(".weight-header-left").remove();
            }
            if (columnAttributeVisibility.size === undefined || !columnAttributeVisibility.size.visible) {
                jQuery(".size-header-left").remove();
            }
            if (columnAttributeVisibility.length === undefined || !columnAttributeVisibility.length.visible) {
                jQuery(".length-header-left").remove();
            }
            if (columnAttributeVisibility.width === undefined || !columnAttributeVisibility.width.visible) {
                jQuery(".width-header-left").remove();
            }
        },
        dragEnterHighlight: function(H) {
            var I = UWA.Event.getElement(H).id;
            if (I.startsWith("drop-target-left")) {
                jQuery("#drop-target-left").addClass("drop-cue-on")
            }
        },
        dragLeaveDeHighlight: function(H) {
            if (H == null) {} else {
                var I = UWA.Event.getElement(H).id;
                if (I.startsWith("drop-target-left")) {
                    jQuery("#drop-target-left").removeClass("drop-cue-on")
                }
            }
        },
        getOtherTree: function(fTree) {
            return (fTree._id === BCWidget.fTree_left._id) ? BCWidget.fTree_right : BCWidget.fTree_left;
        },
        getObjectInfo: function(ObjectID) {
            var objectInfoPromise = new UWApromise(function(reseolveFunction, rejectFunction) {
                FoundationV2Data.ajaxRequest({
                    url: "/resources/v2/e6w/service/ObjectInfo/" + ObjectID + "?$fields=objectId",
                    callback: function(E) {
                        var D = UWA.is(E, "object") ? E : JSON.parse(E);
                        if(D.error) {                    
                            console.log("Object Not Found in Enovia");
                            alert("Object Not Found in Enovia");
                            Mask.unmask(widget.getElement("#compare-content"));
                    }  
                    else{
                    if (D.data[0].dataelements.objectId) {
                        console.log("%cCalled Item Info Service. Fetching Item Properties.", "color: #f3e257");
                        console.info("%cSelected Object Properties: ", "color: #6ef5ee");
                        console.info("%cType : " + D.data[0].type, "color: #6ef5ee");
                        console.info("%cName : " + D.data[0].dataelements.name, "color: #6ef5ee");
                        console.info("%cRevision : " + D.data[0].dataelements.revision, "color: #6ef5ee");
                        var tnr = { "type": D.data[0].type, "name": D.data[0].dataelements.name, "revision": D.data[0].dataelements.revision };
                        reseolveFunction(tnr);
                    } else {
                        rejectFunction("Object Info service error not found");
                    }
                        }
                    }
                });
            });
            return objectInfoPromise;
        }
    };
    return PLMLNBOMCompareUX;
});
define("VALMET/PLMLNBOMCompareUX/DnD", ["DS/ENO6WPlugins/jQuery"], function(b) {
    this._processingCB = null;
    this._processingCBThis = null;
    var a = function(j, e, c, k, h) {
        var f = this;
        f._dragInVizCB = c;
        f._dragOutVizCB = k;
        f._dragOverVizCB = h;
        f._dragCBThis = e;
        f._dragInCount = 0;
        f.getDropSide = function(m) {
            var n = m.currentTarget.id;
            var l = null;
            if (n === "drop-target-right" || n === "drop-target-right-iconlabel" || n === "drop-target-right-icon" || n === "compare-tree-to-1") {
                l = "drop-right"
            } else {
                if ((n === "drop-target-left") || (n === "drop-target-left-iconlabel") || (n === "drop-target-left-icon" || (n === "compare-tree-base"))) {
                    l = "drop-left"
                }
            }
            return l
        };
        f.getObjs = function(l) {
            var o = [];
            var s = null;
            var t = null;
            var u = navigator.userAgent.toLowerCase().indexOf("chrome") > -1;
            if (u) {
                var y = (l.originalEvent.dataTransfer.types.indexOf("text/searchitems") >= 0);
                if (y) {
                    t = l.originalEvent.dataTransfer.getData("text/searchitems")
                } else {
                    t = l.originalEvent.dataTransfer.getData("text/plain")
                }
            } else {
                var p = UWA.is(l.originalEvent.dataTransfer.types.indexOf, "function") ? "indexOf" : "contains";
                var n = "";
                var x = ["text/searchitems", "text/plain", "Text"];
                for (var w = 0; w < x.length && n === ""; w++) {
                    var v = l.originalEvent.dataTransfer.types[p](x[w]);
                    if ((UWA.is(v, "number") && v >= 0) || (UWA.is(v, "boolean") && v === true)) {
                        n = x[w]
                    }
                }
                if (n != "") {
                    t = l.originalEvent.dataTransfer.getData(n)
                }
            }
            if ((t) && (t != "")) {
                var m = JSON.parse(t).data;
                for (var r = 0; r < m.items.length; r++) {
                    var q = m.items[r];
                    o.push(q)
                }
            }
            return (o)
        };
        f.handleDragEnter = function(l) {
            l.preventDefault();
            f._dragInCount++;
            if (f._dragInCount == 1) {
                f._dragInVizCB.call(f._dragCBThis, l)
            }
            return true
        };
        f.handleDragOver = function(l) {
            l.preventDefault();
            if (f._dragOverVizCB != null) {
                f._dragOverVizCB(l)
            }
        };
        f.handleDragLeave = function(l) {
            l.preventDefault();
            f._dragInCount--;
            if (f._dragInCount == 0) {
                f._dragOutVizCB.call(f._dragCBThis, l)
            }
            return true
        };
        f.handleDragDropped = function(n) {
            f._dragInCount = 0;
            n.preventDefault();
            f._dragOutVizCB.call(f._dragCBThis, n);
            var m = f.getObjs(n);
            if (m.length > 0) {
                var l = f.getDropSide(n);
                if (_processingCB != null) {
                    _processingCB.call(_processingCBThis, m, l)
                }
            }
            return true
        };
        var d = {
            dragenter: f.handleDragEnter,
            dragover: f.handleDragOver,
            dragleave: f.handleDragLeave,
            drop: f.handleDragDropped,
            dragdrop: f.handleDragDropped
        };
        var g = b("#" + j);
        g.bind(d)
    };
    return {
        _attachedObjects: [],
        setProcessingCB: function(d, c) {
            _processingCBThis = d;
            _processingCB = c
        },
        attachObject: function(f, c, d, g, e) {
            this._attachedObjects.push(new a(f, c, d, g, e))
        },
    }
});
define("VALMET/PLMLNBOMCompareUX/FilterColumns", ["UWA/Controls/Abstract", "DS/UIKIT/Input/Button", "DS/Controls/Toggle", "DS/UIKIT/Input/Select", "i18n!DS/CompareWidget/assets/nls/CompareWidget"], function(Abstract, Button, Toggle, Select, d) {
    var FilterColumns = Abstract.extend({
        callback: null,
        context: null,
        attributesList: "",
        attributeDisplayOption: "default",
        defaultShowAttributes: "",
        dontShowAttributes: "",
        displayOptionCombo: null,
        attributeCheckboxes: [],
        columnSelectionModal: null,
        init: function(modalBody, modalFooter, columnAttributeList, options) {
            var k = this;
            this._parent(options);
            this.elements.container = UWA.createElement("div");
            this.elements.container.className = "filter_attributes_container";
            modalBody.appendChild(this.elements.container);
            this.elements.footer = modalFooter;
            this.attributesList = columnAttributeList;
            if (typeof(options) !== "undefined" && options !== null) {
                if (typeof(options.callback) !== "undefined" && options.callback !== null) {
                    k.callback = options.callback
                }
                if (typeof(options.context) !== "undefined" && options.context != null) {
                    k.context = options.context
                }
                if (typeof(options.getAttributeDisplayOption) !== "undefined" && options.getAttributeDisplayOption !== null) {
                    k.attributeDisplayOption = options.getAttributeDisplayOption();
                }
                if (typeof(options.getDefaultShowAttributes) !== "undefined" && options.getDefaultShowAttributes !== null) {
                    k.defaultShowAttributes = options.getDefaultShowAttributes();
                }
                if (typeof(options.getDontShowAttributes) !== "undefined" && options.getDontShowAttributes !== null) {
                    k.dontShowAttributes = options.getDontShowAttributes();
                }
                k.setAttributeDisplayOptions = options.setAttributeDisplayOptions
            }
            this.initUI();
        },
        initUI: function() {
            var p = this;
            var dropDownContainer = UWA.createElement("div");
            dropDownContainer.className = "attribute-displayoptions";
            var dropDownOptions = [];
            dropDownOptions[0] = {
                label: d.displayAll,
                value: "all"
            };
            dropDownOptions[2] = {
                label: d.displaySelected,
                value: "selected"
            };
            dropDownOptions[3] = {
                label: d.displayDefault,
                value: "default"
            };
            this.displayOptionCombo = new Select({
                placeholder: false,
                value: p.attributeDisplayOption,
                options: dropDownOptions,
                events: {
                    onChange: function() {
                        var A = p.attributeDisplayOption;
                        var z = this.getValue();
                        var C = z[0];
                        if (C !== A) {
                            p.attributeDisplayOption = C;
                            if (n) {
                                n.setDisabled(false)
                            }
                            if (A === "selected") {
                                var x = 0;
                                p.dontShowAttributes = [];
                                if (p.attributesList.length === p.attributeCheckboxes.length) {
                                    for (var y = 0; y < p.attributesList.length; ++y) {
                                        if (p.attributeCheckboxes[y].checkFlag === false) {
                                            p.dontShowAttributes[x] = p.attributesList[y].name;
                                            ++x
                                        }
                                    }
                                }
                            }
                            for (var y = 0; y < p.attributeCheckboxes.length; ++y) {
                                var w = p.attributesList[y].name;
                                if (p.attributeDisplayOption === "all") {
                                    p.attributeCheckboxes[y].checkFlag = true;
                                    p.attributeCheckboxes[y].disabled = true
                                } else {
                                    if (p.attributeDisplayOption === "default") {
                                        var B = true;
                                        for (var x = 0; x < p.defaultShowAttributes.length; ++x) {
                                            if (p.defaultShowAttributes[x] === w) {
                                                B = false
                                            }
                                        }
                                        p.attributeCheckboxes[y].checkFlag = !B;
                                        p.attributeCheckboxes[y].disabled = true
                                    } else {
                                        var B = false;
                                        for (var x = 0; x < p.dontShowAttributes.length; ++x) {
                                            if (p.dontShowAttributes[x] === w) {
                                                B = true
                                            }
                                        }
                                        p.attributeCheckboxes[y].checkFlag = !B;
                                        p.attributeCheckboxes[y].disabled = false
                                    }
                                }
                            }
                        }
                    }
                }
            }).inject(dropDownContainer);
            this.elements.container.appendChild(dropDownContainer);
            var s = UWA.createElement("div");
            s.className = "attributes-scroll-container";
            this.elements.container.appendChild(s);
            var r = UWA.createElement("div");
            r.className = "attribute-list-container";
            s.appendChild(r);
            var u = (p.attributeDisplayOption !== "selected");

            function k(x) {
                var y = true;
                if (p.attributeDisplayOption === "all") {
                    y = false
                } else {
                    if (p.attributeDisplayOption === "default") {
                        y = true;
                        for (var w = 0; w < p.defaultShowAttributes.length; ++w) {
                            if (p.defaultShowAttributes[w] === x) {
                                y = false
                            }
                        }
                    } else {
                        y = false;
                        for (var w = 0; w < p.dontShowAttributes.length; ++w) {
                            if (p.dontShowAttributes[w] === x) {
                                y = true
                            }
                        }
                    }
                }
                return y ? false : true
            }
            for (var m = 0; m < this.attributesList.length; ++m) {
                var l = UWA.createElement("div");
                l.className = "attribute-onestring";
                var o = k(this.attributesList[m].name);
                this.attributeCheckboxes[m] = new Toggle({
                    label: this.attributesList[m].displayName,
                    value: this.attributesList[m].name,
                    checkFlag: o,
                    disabled: u,
                    visibleFlag: true,
                });
                this.attributeCheckboxes[m].inject(l);
                this.attributeCheckboxes[m].addEventListener("buttonclick", function(w) {
                    if (n) {
                        n.setDisabled(false)
                    }
                });
                r.appendChild(l)
            }
            var t = UWA.createElement("div");
            t.className = "filterattributes-OkCancel";
            var n = new Button({
                value: d.save,
                className: "primary",
                visibleFlag: true,
                disabled: true,
            });
            var j = new Button({
                value: d.cancel,
                className: "default",
                visibleFlag: true,
            });
            n.inject(t);
            j.inject(t);
            this.elements.footer.appendChild(t);
            n.addEvent("onClick", function() {
                var w = "";
                if (p.attributeDisplayOption === "selected") {
                    for (var x = 0; x < p.attributesList.length; ++x) {
                        if (p.attributeCheckboxes[x].checkFlag === false) {
                            if (w !== "") {
                                w += "|"
                            }
                            w += p.attributesList[x].name
                        }
                    }
                }
                if ((p.setAttributeDisplayOptions !== null) && typeof(p.setAttributeDisplayOptions) === "function") {
                    p.setAttributeDisplayOptions(p.attributeDisplayOption, w)
                }
                n.disabled = true;
                n.setDisabled(true);
                j.setDisabled(true);
                var y = {};
                p.dispatchEvent("onModification", y);
                var y = {};
                p.dispatchEvent("onClose", y)
            });
            j.addEvent("onClick", function() {
                var w = {};
                p.dispatchEvent("onClose", w)
            });
            var q = {};
            this.dispatchEvent("onReady", q)
        },
        resizeForScrolling: function() {
            if (this.columnSelectionModal !== null) {
                var j = this.columnSelectionModal.elements.container.getDimensions();
                var m = j.height;
                var p = j.maxHeight;
                if (p === -1) {
                    this.columnSelectionModal._fixMaxWidthAndHeight();
                    j = this.columnSelectionModal.elements.container.getDimensions();
                    m = j.height
                }
                var n = this.columnSelectionModal.elements.footer.getDimensions().height;
                var q = jQuery(".attribute-displayoptions");
                var k = q[0].offsetTop;
                var l = q[0].offsetHeight;
                var o = jQuery(".attributes-scroll-container");
                var h = m - (k + l) - n;
                if (h > 0) {
                    o[0].style.height = h.toString() + "px"
                }
            }
        },
    });
    return FilterColumns
});
define("VALMET/PLMLNBOMCompareUX/BOMAttributeComparator", [], function() {
    var BOMAttrComaparator = {
        hasStringDifference: function(str1, str2) {
            if (str1 !== str2) {
                return true;
            } else {
                return false;
            }
        },
        hasNameDifference: function(name1, name2) {
            return typeof name1 === 'string' && typeof name2 === 'string' ?
                name1.localeCompare(name2, undefined, { sensitivity: 'accent' }) !== 0 :
                name1 !== name2;
        },
        hasQuantityDifference: function(quantity0, quantity1) {
            if (BOMAttrComaparator.isNullOrEmpty(quantity0) && BOMAttrComaparator.isNullOrEmpty(quantity1)) {
                return false;
            } else if (!BOMAttrComaparator.isNullOrEmpty(quantity0) && !BOMAttrComaparator.isNullOrEmpty(quantity1)) {
                var qty0 = parseFloat(quantity0);
                var qty1 = parseFloat(quantity1);
                if (qty0 != qty1) {
                    return true;
                } else if (qty0 == qty1) {
                    return false;
                }
            } else {
                return true;
            }
        },
        hasItemTypeDifference: function(itemType0, itemType1) {
            if (itemType0 === "" && itemType1 === "") {
                return false
            } else if (itemType0 === "manufacture" && itemType1 === "Manufactured") {
                return false;
            } else if (itemType0 === "manufacture" && itemType1 !== "Manufactured") {
                return true;
            } else if (itemType0 === "purchase" && itemType1 === "Purchased") {
                return false;
            } else if (itemType0 === "purchase" && itemType1 !== "Purchased") {
                return true;
            } else {
                return true;
            }
        },
        hasReleasePurposeSignalCodeDifference: function(releasePurpose, signalCode) {
            if (releasePurpose === "" && signalCode === "") {
                return false
            } else if (releasePurpose === "Planning" && signalCode === "DRA") {
                return false;
            } else if (releasePurpose === "Planning" && signalCode !== "DRA") {
                return true;
            } else if (releasePurpose === "Production" && signalCode === "NEW") {
                return false;
            } else if (releasePurpose === "Production" && signalCode !== "NEW") {
                return true;
            } else {
                return true;
            }
        },
        hasDrawingNumberDifference: function(drawingNumber0, drawingNumber1) {
            drawingNumber1 = drawingNumber1.replace(/ /g, "_");
            return this.hasStringDifference(drawingNumber0, drawingNumber1);
        },
        isNullOrEmpty: function(property) {
            if (property === null || property === undefined || property === "") {
                return true;
            } else {
                return false;
            }
        },
        addAttributeToDifferenceList: function(attr, list) {
            if (list.length) {
                list += "," + attr;
            } else {
                list += attr;
            }
            return list;
        },
        filterRevFromDrawingNumber: function(drwNum) {
            var index = drwNum.indexOf("_");
            if (index > -1) {
                drwNum = drwNum.substring(0, index);
            }
            return drwNum;
        }
    };
    return BOMAttrComaparator;
});
//VSIX-4747 Export PLM LN comparison result as report.
define("VALMET/PLMLNBOMCompareUX/Export/CompareDataExporter", ["VALMET/PLMLNBOMCompareUX/WidgetConfiguration"], function(WidgetConfiguration) {
    var CompareDataExporter = {
        exportUI: function() {
            console.log("++++++++export ui++++++++++");
            var date = new Date();
            var formattedDate = date.getFullYear() + "_" + (date.getMonth() + 1) + "_" + date.getDate() + "_" + date.getHours() + "_" + date.getMinutes() + "_" + date.getSeconds();
            var newTable = CompareDataExporter.prepareTableData();
            var fileName = WidgetConfiguration.BC_WIDGET_NAME + formattedDate + ".xls";
            var html = newTable;
            var url = 'data:application/vnd.ms-excel,' + escape(html); // Set html table into url
            var elem = document.createElement('a');
            elem.setAttribute("href", url);
            elem.setAttribute("download", fileName);
            elem.click();
            //just in case, prevent default behaviour
            elem.preventDefault();
        },
        prepareTableData: function() {
            var leftTable = document.getElementById('structure-treetable-left');
            var rightTable = document.getElementById('structure-treetable-right');
            var leftHeaderData = leftTable.firstChild.innerText;
            leftHeaderData = leftHeaderData.replace('\n', '').split('\t');
            var rightHeaderData = rightTable.firstChild.innerText;
            rightHeaderData = rightHeaderData.replace('\n', '').split('\t');
            var allHeadersData = leftHeaderData.concat(rightHeaderData);
            var leftTableLength = leftHeaderData.length;
            var newThead = '<thead><tr>';
            newThead += '<th style="background-color:#f39c12";>Level</th>';
            for (var i = 0; i < allHeadersData.length; i++) {
                if (i < leftTableLength) {
                    newThead += '<th style="background-color:#f39c12";>' + allHeadersData[i] + '</th>';
                } else if (i === leftTableLength) {
                    newThead += '<th style="background-color:#fffbc3">Compared Status </th>';
                    newThead += '<th style="background-color:#fffbc3">Compared Attributes</th>';
                } else if (i > leftTableLength && i < allHeadersData.length) {
                    newThead += '<th style="background-color:#009ddb";>' + allHeadersData[i] + '</th>';
                }
            }
            newThead += '</tr></thead>';
            var leftBody = leftTable.lastChild;
            var rightBody = rightTable.lastChild;
            var length = leftBody.childNodes.length;
            var rowLength = leftBody.childNodes[0].cells.length + rightBody.childNodes[0].cells.length + 1;
            var allBodyData = [];
            var start = leftHeaderData.length + 1;
            var rowIndex = 0;
            var leftDataCount = 0;
            var rightDataCount = 0;
            for (var j = 0; j < length; j++) {
                var itemLevel = "0" + "Attr:";
                if (j > 0) {
                    itemLevel = leftBody.childNodes[j].attributes.bomlevel.nodeValue + "Attr:";
                }
                allBodyData.push(itemLevel);
                for (var k = 0; k < leftHeaderData.length; k++) {
                    var className = leftBody.childNodes[j].childNodes[k].className;
                    var attrName = "Attr:";
                    if (className.contains("field-difference")) {
                        if (className.startsWith("title-field")) {
                            attrName += "Name";
                        } else if (className.startsWith("revision-field")) {
                            attrName += "Rev";
                        } else if (className.startsWith("position-field")) {
                            attrName += "Pos.";
                        } else if (className.startsWith("quantity-field")) {
                            attrName += "Qty";
                        } else if (className.startsWith("releasePurposeSignalCode-field")) {
                            attrName += "Rel. Purpose";
                        } else if (className.startsWith("itemType-field")) {
                            attrName += "Item Type";
                        } else if (className.startsWith("drawingNumber-field")) {
                            attrName += "Drw. Nr.";
                        }
                    }
                    var data = leftBody.childNodes[j].childNodes[k].innerText;
                    var columnValue = data + attrName;
                    allBodyData.push(columnValue);
                }
                if (leftBody.childNodes[j].childNodes[0].innerText.trim().length > 0) {
                    ++leftDataCount;
                }
                if (rightBody.childNodes[j].childNodes[1].innerText.trim().length > 0) {
                    ++rightDataCount;
                }
                for (var k = 0; k < rightHeaderData.length; k++) {
                    var diffStatus = "";
                    if (k % rightHeaderData.length === 0) {
                        var clsName = rightBody.childNodes[j].childNodes[k].childNodes[0].className;
                        if (clsName.contains("no-diff")) {
                            diffStatus = "Identical";
                        } else if (clsName.contains("attr-diff")) {
                            diffStatus = "Different";
                        } else if (clsName.contains("left-only")) {
                            diffStatus = "Enovia Only";
                        } else if (clsName.contains("right-only")) {
                            diffStatus = "LN Only";
                        }
                        allBodyData.push(diffStatus);
                    } else {
                        var className = rightBody.childNodes[j].childNodes[k].className;
                        var attrName = "Attr:";
                        if (className.contains("field-difference")) {
                            if (className.startsWith("title-field")) {
                                attrName += "Name";
                            } else if (className.startsWith("revision-field")) {
                                attrName += "Rev";
                            } else if (className.startsWith("position-field")) {
                                attrName += "Pos.";
                            } else if (className.startsWith("quantity-field")) {
                                attrName += "Qty";
                            } else if (className.startsWith("releasePurposeSignalCode-field")) {
                                attrName += "Item Signal";
                            } else if (className.startsWith("itemType-field")) {
                                attrName += "Item Type";
                            } else if (className.startsWith("drawingNumber-field")) {
                                attrName += "Drw. Nr.";
                            }
                        }
                        var data = rightBody.childNodes[j].childNodes[k].innerText;
                        var columnValue = data + attrName;
                        allBodyData.push(columnValue);
                    }
                }
            }
            var date = new Date();
            var currentDate = date.toLocaleString();
            var valmetText = '<tr><td/><td style="font-weight:bold; font-size:1.2em;">Valmet</td></tr>';
            var reportName = '<tr><td/><td style="font-weight:bold">Report Name : </td><td style="width:135px;white-space: nowrap;">PLM LN BOM Comparison Report</td></tr>';
            var reportDate = '<tr><td/><td style="font-weight:bold">Report Date : </td><td style="white-space: nowrap">' + currentDate + '</td></tr>';
            var colorDiv = '<tr><td/><td style="font-weight:700">PLM Data Marking :</td><td style="background-color:#f39c12"></td></tr><tr><td/><td style="font-weight:700">LN Data Marking :</td><td style="background-color:#009ddb"></td></tr><tr><td/><td style="font-weight:700">Comparison Status Marking :</td><td style="background-color:#fffbc3"></td></tr>';
            var plmDataCount = '<tr><td/><td style="font-weight:bold">PLM Record Count : </td><td style="">&nbsp;' + leftDataCount + '</td></tr>';
            var lnDataCount = '<tr><td/><td style="font-weight:bold">LN Record Count : </td><td style="">&nbsp;' + rightDataCount + '</td></tr>';
            var emptyCell = '<tr></tr>';
            var newTbody = '<tbody><tr>';
            var attributeList = "";
            for (var i = 0; i < allBodyData.length; i++) {
                if (i % rowLength == 0 && i !== 0) {
                    newTbody += '</tr><tr>';
                    rowIndex++;
                    attributeList = "";
                }
                var dataValue = allBodyData[i].split("Attr:")[0];
                var attrValue = allBodyData[i].split("Attr:")[1];
                if (attrValue) {
                    if (attributeList.length > 0) {
                        attributeList += "," + attrValue;
                    } else {
                        attributeList += attrValue;
                    }
                }
                if ((start) === i) {
                    if (rowIndex % 2 !== 0) {
                        newTbody += '<td style="border: 1px solid #d6dbdf ; background-color:#eaeded";>&nbsp;' + dataValue + '</td>';
                        newTbody += '<td style="border: 1px solid #d6dbdf ; background-color:#eaeded"";>' + attributeList + '</td>';
                    } else {
                        newTbody += '<td style="border: 1px solid #d6dbdf ";>&nbsp;' + dataValue + '</td>';
                        newTbody += '<td style="border: 1px solid #d6dbdf";>' + attributeList + '</td>';
                    }
                    start += rowLength;
                } else if (attrValue.trim().length > 0) {
                    newTbody += '<td style="border: 1px solid #d6dbdf; background-color: #FFFBC3";>&nbsp;' + dataValue + '</td>';
                } else if (rowIndex % 2 !== 0) {
                    newTbody += '<td style="border: 1px solid #d6dbdf ; background-color:#eaeded";>&nbsp;' + dataValue + '</td>';
                } else {
                    newTbody += '<td style="border: 1px solid #d6dbdf ";>&nbsp;' + dataValue + '</td>';
                }
            }
            newTbody += '</tr></tbody>';
            var newTable = '<table id="comparison_table">';
            var cellArray = [valmetText, reportName, reportDate, colorDiv, plmDataCount, lnDataCount, emptyCell, newThead, newTbody];
            for (var j = 0; j < cellArray.length; j++) {
                newTable = newTable.concat(cellArray[j]);
            }
            newTable += '</table>';
            return newTable;
        },
    }
    return CompareDataExporter;
});