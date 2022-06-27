require.config({
    paths: {
        "jquery": "ProjectStructureImportUX/assets/ven/Jquery/jquery-3.4.1.min",
        "bootstrap": "ProjectStructureImportUX/assets/ven/DataTables/Bootstrap-3.3.7/js/bootstrap",

        "datatables.net": "ProjectStructureImportUX/assets/ven/DataTables/DataTables-1.10.22/js/jquery.dataTables.min",
        "datatables.net-bs": "ProjectStructureImportUX/assets/ven/DataTables/DataTables-1.10.22/js/dataTables.bootstrap.min",

        "datatables.net-buttons": "ProjectStructureImportUX/assets/ven/DataTables/Buttons-1.6.4/js/dataTables.buttons.min",
        "datatables.net-buttons-bs": "ProjectStructureImportUX/assets/ven/DataTables/Buttons-1.6.4/js/buttons.bootstrap.min",

        "datatables.net-fixedheader": "ProjectStructureImportUX/assets/ven/DataTables/FixedHeader-3.1.7/js/dataTables.fixedHeader.min",
        "datatables.net-fixedheader-bs": "ProjectStructureImportUX/assets/ven/DataTables/FixedHeader-3.1.7/js/dataTables.fixedHeader.min",

        "datatables.net-keytable": "ProjectStructureImportUX/assets/ven/DataTables/KeyTable-2.5.3/js/dataTables.keyTable.min",
        "datatables.net-keytable-bs": "ProjectStructureImportUX/assets/ven/DataTables/KeyTable-2.5.3/js/keyTable.bootstrap.min",

        "datatables.net-responsive": "ProjectStructureImportUX/assets/ven/DataTables/Responsive-2.2.6/js/dataTables.responsive.min",
        "datatables.net-responsive-bs": "ProjectStructureImportUX/assets/ven/DataTables/Responsive-2.2.6/js/responsive.bootstrap.min",

        "datatables.net-scroller": "ProjectStructureImportUX/assets/ven/DataTables/Scroller-2.0.3/js/dataTables.scroller.min",
        "datatables.net-scroller-bs": "ProjectStructureImportUX/assets/ven/DataTables/Scroller-2.0.3/js/scroller.bootstrap.min",

        "datatables.net-searchpanes": "ProjectStructureImportUX/assets/ven/DataTables/SearchPanes-1.2.0/js/dataTables.searchPanes.min",
        "datatables.net-searchpanes-bs": "ProjectStructureImportUX/assets/ven/DataTables/SearchPanes-1.2.0/js/searchPanes.bootstrap.min",

        "datatables.net-select": "ProjectStructureImportUX/assets/ven/DataTables/Select-1.3.1/js/dataTables.select.min",
        "datatables.net-select-bs": "ProjectStructureImportUX/assets/ven/DataTables/Select-1.3.1/js/select.bootstrap.min",

        "datatables": "ProjectStructureImportUX/assets/ven/DataTables/datatables.min",
    },
    shim: {
        "bootstrap": {
            "deps": ["jquery", "css!ProjectStructureImportUX/assets/ven/DataTables/Bootstrap-3.3.7/css/bootstrap"]
        },
        "datatables.net-bs": {
            "deps": ["css!ProjectStructureImportUX/assets/ven/DataTables/DataTables-1.10.22/css/dataTables.bootstrap.min"]
        },
        "datatables.net-buttons-bs": {
            "deps": ["jquery", "datatables.net-bs", "datatables.net-buttons", "css!ProjectStructureImportUX/assets/ven/DataTables/Buttons-1.6.4/css/buttons.bootstrap.min"]
        },
        "datatables.net-fixedheader-bs": {
            "deps": ["css!ProjectStructureImportUX/assets/ven/DataTables/FixedHeader-3.1.7/css/fixedHeader.bootstrap.min"]
        },
        "datatables.net-keytable-bs": {
            "deps": ["jquery", "datatables.net-bs", "datatables.net-keytable", "css!ProjectStructureImportUX/assets/ven/DataTables/KeyTable-2.5.3/css/keyTable.bootstrap.min"]
        },
        "datatables.net-responsive-bs": {
            "deps": ["jquery", "datatables.net-bs", "datatables.net-responsive", "css!ProjectStructureImportUX/assets/ven/DataTables/Responsive-2.2.6/css/responsive.bootstrap.min"]
        },
        "datatables.net-scroller-bs": {
            "deps": ["jquery", "datatables.net-bs", "datatables.net-scroller", "css!ProjectStructureImportUX/assets/ven/DataTables/Scroller-2.0.3/css/scroller.bootstrap.min"]
        },
        "datatables.net-searchpanes-bs": {
            "deps": ["jquery", "datatables.net-bs", "datatables.net-searchpanes", "css!ProjectStructureImportUX/assets/ven/DataTables/SearchPanes-1.2.0/css/searchPanes.bootstrap.min"]
        },
        "datatables.net-select-bs": {
            "deps": ["jquery", "datatables.net-bs", "datatables.net-select", "css!ProjectStructureImportUX/assets/ven/DataTables/Select-1.3.1/css/select.bootstrap.min"]
        },
    }
});

define("VALMET/ProjectStructureImportUX/ProjectStructureImportUX", ["UWA/Core", "jquery", "DS/UIKIT/Mask", "DS/UIKIT/Alert", "VALMET/ProjectStructureImportUX/WidgetConfiguration", "i18n!DS/ProjectStructureImportUX/assets/nls/ProjectStructureImportUX", "bootstrap", "datatables.net-bs", "datatables.net-buttons-bs", "datatables.net-fixedheader-bs", "datatables.net-keytable-bs", "datatables.net-responsive-bs", "datatables.net-scroller-bs", "datatables.net-searchpanes-bs", "datatables.net-select-bs", "DS/ProjectStructureImportUX/assets/ven/Fancytree", "DS/ProjectStructureImportUX/assets/ven/Fancytree_table", "DS/ProjectStructureImportUX/assets/ven/Fancytree_filter", "DS/ENO6WPlugins/jQuery", "css!DS/UIKIT/UIKIT"], function(UWA, jquery, Mask, Alert, WidgetConfiguration, i18n) {
    var ProjectStructureImportUX = {
        init: function() {
            window.PSIWidget = {};
            PSIWidget.Mask = Mask;
            widget.setTitle("");
            ProjectStructureImportUX.loadContent();
            ProjectStructureImportUX.makeWidgetScrollable();
        },
        loadContent: function() {
            ProjectStructureImportUX.populateWidgetBase();
            ProjectStructureImportUX.populateProjectList();
        },
        populateWidgetBase: function() {
            var widgetContainer = UWA.createElement("div", {
                id: "DataTable-Widget",
                class: "container-fluid",
                html: [{
                    tag: "div",
                    class: "row no-gutters",
                    id: "Widget-Header",
                    html: [{
                        tag: "div",
                        class: "col-md-6",
                        html: [{
                            tag: "div",
                            id: "project-panel",
                            class: "panel panel-default",
                            html: [{
                                tag: "div",
                                class: "panel-heading clearfix",
                                html: [{
                                    tag: "h4",
                                    class: "panel-title pull-left",
                                    html: "Project List"
                                }]
                            }, {
                                tag: "div",
                                class: "panel-body",
                                html: [{
                                    tag: "div",
                                    class: "projectListGrid",
                                    html: [{
                                        tag: "table",
                                        id: "projectList",
                                        class: "responsive table table-sm table-bordered table-condensed",
                                        styles: {
                                            "width": "100%"
                                        },
                                        html: [{
                                            tag: "thead",
                                            class: "grid-header",
                                            html: [{
                                                tag: "tr",
                                                html: [{
                                                    tag: "th"
                                                }, {
                                                    tag: "th",
                                                    html: "Project"
                                                }, {
                                                    tag: "th",
                                                    html: "Description"
                                                }]
                                            }]
                                        }, {
                                            tag: "tbody",
                                            class: "grid-body"
                                        }]
                                    }]
                                }]
                            }]
                        }]
                    }, {
                        tag: "div",
                        class: "col-md-6",
                        html: [{
                            tag: "div",
                            class: "panel panel-default",
                            html: [{
                                tag: "div",
                                class: "panel-heading clearfix",
                                html: [{
                                    tag: "div",
                                    class: "panel-title pull-left",
                                    html: "Activity Tree"
                                }, {
                                    tag: "div",
                                    class: "btn-group pull-right",
                                    html: [{
                                        tag: "button",
                                        id: "importButton",
                                        class: "btn btn-primary",
                                        attributes: {
                                            type: "button"

                                        },
                                        html: "Import"
                                    }]
                                }, {
                                    tag: "div",
                                    class: "form-group pull-right",
                                    styles: {
                                        "margin-right": "10px"
                                    },
                                    html: [{
                                        tag: "input",
                                        id: "searchActivity",
                                        class: "form-control",
                                        type: "text",
                                        placeholder: "Search...",
                                        styles: {
                                            "margin-right": "10px"
                                        }
                                    }]
                                }]
                            }, {
                                tag: "div",
                                class: "panel-body",
                                id: "activity-container",
                                styles: {
                                    overflow: "auto"
                                }
                            }]
                        }]
                    }]
                }]
            });
            widgetContainer.inject(widget.body.empty());
            ProjectStructureImportUX.handleImportButtonClick();
            ProjectStructureImportUX.addTreeTemplate("activity-tree", "#activity-container");
            ProjectStructureImportUX.adjustTreeHeight("activity");
        },
        handleImportButtonClick: function() {
            jquery("#importButton").click(function() {
                ProjectStructureImportUX.importProjectAndActivitytoEnoviaFromLN();
            });
        },
        populateProjectList: function() {
            ProjectStructureImportUX.populateProjectListUI();
            ProjectStructureImportUX.fetchProjectListFromService();
        },
        fetchProjectListFromService: function() {
            Mask.mask(widget.getElement("#project-panel"), i18n.PSI_ALERT_PROCESS_PROJECT_LIST);
            var url = WidgetConfiguration.PSI_ENOVIA_REST_SERVICE_URL + WidgetConfiguration.PSI_GET_PROJECT_LIST_SERVICE_URL;
            console.log("Calling service: " + url);
            jQuery.ajax({
                type: "GET",
                url: url,
                contentType: "application/json",
                success: function(response) {
                    console.log("Service Response:");
                    console.log(response);
                    if (response.status && response.status === "OK") {
                        PSIWidget.DataTable.rows.add(response.data).draw();
                    } else {
                        ProjectStructureImportUX.error(i18n.PSI_ALERT_ERROR_LOAD_PROJECT);
                        console.error("Failed to load Project List.");
                        console.error(response);
                    }
                    Mask.unmask(widget.getElement("#project-panel"));
                },
                error: function(xhr, exception) {
                    ProjectStructureImportUX.error(i18n.PSI_ALERT_ERROR_LOAD_PROJECT);
                    console.log("Failed to load Project List. Error: " + xhr.status);
                    console.log(exception);
                    Mask.unmask(widget.getElement("#project-panel"));
                }
            });
        },
        populateProjectListUI: function() {
            PSIWidget.DataTable = jquery('#projectList').DataTable({
                "responsive": true,
                "processing": true,
                "paging": true,
                "pagingType": "full",
                "lengthChange": true,
                "lengthMenu": [
                    [10, 15, 20, 25, 50, 100, -1],
                    [10, 15, 20, 25, 50, 100, "All"]
                ],
                "dom": "<'row no-gutters justify-content-between' <'col-auto col-sm-auto col-md-4' l> <'col-auto col-sm-auto col-md-8' <'row no-gutters justify-content-end' <'col-auto col-sm-auto col-md-auto' f> > > >" +
                    "<'row' <'col-sm-12 col-md-12 col-12' tr> >" +
                    "<'row align-items-center'<'col-12 col-sm-12 col-md-6 mb-1'i><'col-sm-12 col-md-6'<'pagination-sm' p>>>",
                buttons: [],
                "data": [],
                "columns": [
                    { "data": null, "width": "30" },
                    { "data": "ProjectCode" },
                    { "data": "Description", "defaultContent": "" }
                ],
                "columnDefs": [{
                    targets: 0,
                    orderable: false,
                    className: 'select-checkbox',
                    defaultContent: '',
                    title: ''
                }],
                "select": {
                    "style": 'os',
                    "selector": 'td:first-child'
                },
                "order": [
                    [1, 'asc']
                ],
                "language": {
                    "infoEmpty": "No Projects to show",
                    "emptyTable": "No Projects available in table",
                    "zeroRecords": "No Projects to display",
                    "processing": '<i class="fa fa-spinner fa-pulse fa-2x"></i><span class="sr-only">Processing...</span>'
                }
            }).on('select', function(e, dt, type, indexes) {
                var rowData = PSIWidget.DataTable.rows(indexes).data().toArray();
                var projectSpace = rowData[0].ProjectCode;
                console.log("Selected Project: " + projectSpace);
                ProjectStructureImportUX.fetchActivityData(projectSpace);
                jquery("#searchActivity").val("");
            }).on('deselect', function(e, dt, type, indexes) {
                ProjectStructureImportUX.deselectActivityData();
            });
        },
        fetchActivityData: function(projectSpace) {
            Mask.mask(widget.getElement("#activity-tree"), i18n.PSI_ALERT_PROCESS_ACTIVITY_LIST);
            var encodedProjectSpace = encodeURIComponent(projectSpace || "");
            var url = WidgetConfiguration.PSI_ENOVIA_REST_SERVICE_URL + WidgetConfiguration.PSI_GET_ACTIVITY_LIST_SERVICE_URL_PREFIX + encodedProjectSpace + WidgetConfiguration.PSI_GET_ACTIVITY_LIST_SERVICE_URL_SUFFIX;
            console.log("Calling service: " + url);
            jQuery.ajax({
                type: 'GET',
                url: url,
                contentType: "application/json",
                success: function(response) {
                    console.log("Service Response:");
                    console.log(response);

                    if (response.status && response.status === "OK") {
                        PSIWidget.ActivityList = response.data || [];

                        if (!PSIWidget.ActivityList.length) {
                            ProjectStructureImportUX.addTreeTemplate("activity-tree", "#activity-container");
                            return;
                        }

                        var treeData = [];
                        var indexOfRootItems = [];
                        for (var index = 0; index < PSIWidget.ActivityList.length; index++) {
                            if (PSIWidget.ActivityList[index].ParentActivity == undefined) {
                                var treeNode = {
                                    "Project": PSIWidget.ActivityList[index].Project || "",
                                    "Activity": PSIWidget.ActivityList[index].Activity || "",
                                    "ParentActivity": "root",
                                    "ActivityType": PSIWidget.ActivityList[index].ActivityType || "",
                                    "ActivityDescription": PSIWidget.ActivityList[index].ActivityDescription || "",
                                    "ActivityStatus": PSIWidget.ActivityList[index].ActivityStatus || "",
                                    "AcutalEndDate": PSIWidget.ActivityList[index].AcutalEndDate || "",
                                    "NetQuantity": PSIWidget.ActivityList[index].NetQuantity || "",
                                    "Unit": PSIWidget.ActivityList[index].Unit || "",
                                    "ProductType": PSIWidget.ActivityList[index].ProductType || "",
                                    "ProductTypeDesc": PSIWidget.ActivityList[index].ProductTypeDesc || "",
                                    "Pos": PSIWidget.ActivityList[index].Pos || "",
                                    "ContractDeliverableNo": PSIWidget.ActivityList[index].ContractDeliverableNo || "",
                                    "Selectable": PSIWidget.ActivityList[index].Selectable || "",
                                    "title": PSIWidget.ActivityList[index].Activity || "",
                                    "folder": false,
                                    "expanded": false,
                                    "icon": "",
                                    children: []
                                };

                                treeData.push(treeNode);
                                indexOfRootItems.push(index);
                            }
                        }

                        for (var index = indexOfRootItems.length - 1; index >= 0; index--) {
                            PSIWidget.ActivityList.splice(indexOfRootItems[index], 1);
                        }

                        for (var index = 0; index < treeData.length; index++) {
                            ProjectStructureImportUX.addChildren(treeData[index], PSIWidget.ActivityList);
                        }

                        PSIWidget.ActivityData = treeData;
                        console.log("Activity Tree Data");
                        console.log(PSIWidget.ActivityData);
                        ProjectStructureImportUX.populateActivityTree();
                        ProjectStructureImportUX.adjustTreeHeight("activity");
                        Mask.unmask(widget.getElement("#activity-tree"));
                    } else {
                        ProjectStructureImportUX.error(i18n.PSI_ALERT_ERROR_LOAD_ACTIVITY);
                        ProjectStructureImportUX.addTreeTemplate("activity-tree", "#activity-container");
                        console.log("Failed to load Activity List.");
                        console.log(response);
                    }
                },
                error: function(xhr, exception, error) {
                    ProjectStructureImportUX.error(i18n.PSI_ALERT_ERROR_LOAD_ACTIVITY);
                    ProjectStructureImportUX.addTreeTemplate("activity-tree", "#activity-container");
                    console.log("Failed to load Activity List. Error:" + xhr.status);
                    console.log(exception);
                }
            });
        },
        populateActivityTree: function() {
            ProjectStructureImportUX.addTreeTemplate("activity-tree", "#activity-container");
            PSIWidget.ActivityTree = ProjectStructureImportUX.createFancyTree('#activity-tree', PSIWidget.ActivityData, 1, 0, ProjectStructureImportUX.renderer);
        },
        addTreeTemplate: function(treeId, containerId) {
            widget.body.getElement(containerId).empty();
            var ActivityTreeTemplate = UWA.createElement("table", {
                id: treeId,
                class: "table table-bordered table-condensed",
                styles: {
                    overflow: "visible !important"
                },
                html: [{
                    tag: "thead",
                    class: "thead-dark",
                    html: [{
                        tag: "tr",
                        html: [{
                            tag: "th",
                        }, {
                            tag: "th",
                            class: "text-center",
                            html: [{
                                tag: "span",
                                html: "Activity"
                            }]
                        }, {
                            tag: "th",
                            class: "text-center",
                            html: [{
                                tag: "span",
                                html: "Type",
                            }]
                        }, {
                            tag: "th",
                            class: "text-center",
                            html: [{
                                tag: "span",
                                html: "Description",
                            }]
                        }, {
                            tag: "th",
                            class: "text-center",
                            html: [{
                                tag: "span",
                                html: "Status",
                            }]
                        }, {
                            tag: "th",
                            class: "text-center",
                            html: [{
                                tag: "span",
                                html: "Selectable",
                            }]
                        }]
                    }]
                }, {
                    tag: "tbody",
                    html: [{
                        tag: "tr",
                        html: [{
                            tag: "td"
                        }, {
                            tag: "td",
                            html: "No activities found."
                        }, {
                            tag: "td"
                        }, {
                            tag: "td"
                        }, {
                            tag: "td"
                        }, {
                            tag: "td"
                        }]
                    }]
                }]
            });
            ActivityTreeTemplate.inject(widget.body.getElement(containerId));
            ProjectStructureImportUX.handleFilterSearchClick();
        },
        createFancyTree: function(elementId, data, nodeColumnIdx, checkboxColumnIdx, rendererFunction) {
            PSIWidget.ActivityTree = jQuery(elementId).fancytree({
                extensions: ["table", "filter"],
                checkbox: true,
                selectMode: 3,
                checkboxAutoHide: true,
                table: {
                    indentation: 22,
                    nodeColumnIdx: nodeColumnIdx,
                    checkboxColumnIdx: checkboxColumnIdx
                },
                imagePath: null,
                source: data,
                filter: {
                    autoApply: true, // Re-apply last filter if lazy data is loaded
                    autoExpand: true, // Expand all branches that contain matches while filtered
                    counter: true, // Show a badge with number of matching child nodes near parent icons
                    fuzzy: false, // Match single characters in order, e.g. 'fb' will match 'FooBar'
                    hideExpandedCounter: true, // Hide counter badge if parent is expanded
                    hideExpanders: true, // Hide expanders if all child nodes are hidden by filter
                    highlight: true, // Highlight matches by wrapping inside <mark> tags
                    leavesOnly: false, // Match end nodes only
                    nodata: true, // Display a 'no data' status node if result is empty
                    mode: "hide" // Grayout unmatched nodes (pass "hide" to remove unmatched node instead)
                },
                select: function() {
                    ProjectStructureImportUX.deselectProjectData();
                },
                renderColumns: rendererFunction,
                init: function() {
                    var elId = elementId.slice(1, elementId.length);
                    var tableElement = document.getElementById(elId);
                    ProjectStructureImportUX.resizableTreeColumn(tableElement);
                },
                expand: function() {
                    var elId = elementId.slice(1, elementId.length);
                    var tableElement = document.getElementById(elId);
                    ProjectStructureImportUX.resizableTreeColumn(tableElement);
                    ProjectStructureImportUX.adjustTreeHeight("activity");
                }
            });
            return PSIWidget.ActivityTree;
        },
        handleFilterSearchClick: function() {
            jquery("#searchActivity").keyup(function(e) {
                var n;
                var tree = PSIWidget.ActivityTree.fancytree('getTree');
                var args = "autoApply autoExpand fuzzy hideExpanders highlight leavesOnly nodata mode".split(" ");
                var opts = {};
                var filterFunc = tree.filterBranches;
                var match = jQuery(this).val();
                opts.autoExpand = true;
                opts.mode = "hide";
                var filterAttributes = ["Activity", "ActivityType", "ActivityDescription", "ActivityStatus", "Selectable"];
                n = filterFunc.call(tree, match, opts, filterAttributes);
            }).focus();
        },
        renderer: function(event, nodeData) {
            var node = nodeData.node;
            var data = node.data;
            var tableRow = node.tr;
            var tableColumn = jQuery(tableRow).find(">td");
            var columnIndex = 0;
            var checkboxindex = columnIndex++;
            var titleIndex = columnIndex++;
            var typeindex = columnIndex++;
            var descriptionIndex = columnIndex++;
            var statusIndex = columnIndex++;
            var selectIndex = columnIndex++;
            if (node.data.Selectable == 0 && node.data.ActivityDescription != "Milestones") {

                nodeData.node.unselectable = true;

                jQuery(tableColumn.eq(checkboxindex)).addClass("checkbox-field text-center");
            } else {

                jQuery(tableColumn.eq(checkboxindex)).addClass("checkbox-field text-center");

            }

            if (node.data.Selectable == 0) {

                var firstValue = "no";
                var secondValue = "yes";
            } else {

                var firstValue = "yes";
                var secondValue = "no";

            }

            jQuery(tableColumn.eq(titleIndex)).addClass("title-field");
            jQuery(tableColumn.eq(titleIndex)).find(".fancytree-icon").remove();
            jQuery(tableColumn.eq(typeindex)).addClass("type-field text-center");
            jQuery(tableColumn.eq(typeindex)).html("<span>" + data.ActivityType + "</span>");
            jQuery(tableColumn.eq(descriptionIndex)).addClass("type-field text-left");
            jQuery(tableColumn.eq(descriptionIndex)).html("<span>" + data.ActivityDescription + "</span>");
            jQuery(tableColumn.eq(statusIndex)).addClass("type-field text-center");
            jQuery(tableColumn.eq(statusIndex)).html("<span>" + data.ActivityStatus + "</span>");
            jQuery(tableColumn.eq(selectIndex)).addClass("selectable-field text-center");
            jQuery(tableColumn.eq(selectIndex)).html("<select disabled><option>" + firstValue + "</option><option>" + secondValue + "</option></select>");
        },
        refresh: function() {

        },
        resize: function() {
            ProjectStructureImportUX.adjustTreeHeight("activity");
        },
        makeWidgetScrollable: function() {
            widget.body.setStyle("overflow", "auto");
        },
        addChildren: function(parent, treeList) {
            var filterArray = [];
            for (var index = 0; index < treeList.length; index++) {
                if (parent.Activity == treeList[index].ParentActivity) {
                    var treeNode = {
                        "Project": treeList[index].Project || "",
                        "Activity": treeList[index].Activity || "",
                        "ParentActivity": treeList[index].ParentActivity || "",
                        "ActivityType": treeList[index].ActivityType || "",
                        "ActivityDescription": treeList[index].ActivityDescription || "",
                        "ActivityStatus": treeList[index].ActivityStatus || "",
                        "AcutalEndDate": treeList[index].AcutalEndDate || "",
                        "NetQuantity": treeList[index].NetQuantity || "",
                        "Unit": treeList[index].Unit || "",
                        "ProductType": treeList[index].ProductType || "",
                        "ProductTypeDesc": treeList[index].ProductTypeDesc || "",
                        "Pos": treeList[index].Pos || "",
                        "ContractDeliverableNo": treeList[index].ContractDeliverableNo || "",
                        "Selectable": treeList[index].Selectable || "",
                        "title": treeList[index].Activity || "",
                        "folder": false,
                        "expanded": false,
                        "icon": "",
                        children: []
                    };
                    if (treeNode.ActivityType !== "") {
                        parent.children.push(treeNode);
                    }
                    filterArray.push(index);
                }
            }

            for (var index = filterArray.length - 1; index >= 0; index--) {
                treeList.splice(filterArray[index], 1);
            }

            if (parent.children.length) {
                parent.folder = true;
                if (parent.Activity === "0") {
                    parent.expanded = true;
                }
                for (var index = 0; index < parent.children.length; index++) {
                    ProjectStructureImportUX.addChildren(parent.children[index], treeList);
                }
            }
        },
        resizableTreeColumn: function(e) {
            var t = e.getElementsByTagName("tr")[0],
                n = t ? t.children : void 0;
            if (n) {
                for (var i = e.offsetHeight, o = 0; o < n.length - 1; o++) {
                    var r = s(i);
                    n[o].appendChild(r), n[o].style.position = "relative", d(r)
                }
            }

            function d(e) {
                var t, n, i, o, r;
                e.addEventListener("mousedown", function(e) {
                        n = e.target.parentElement, i = n.nextElementSibling, t = e.pageX;
                        var d = function(e) {
                            if ("border-box" == l(e, "box-sizing")) return 0;
                            var t = l(e, "padding-left"),
                                n = l(e, "padding-right");
                            return parseInt(t) + parseInt(n)
                        }(n);
                        o = n.offsetWidth - d, i && (r = i.offsetWidth - d)
                    }),
                    e.addEventListener("mouseout", function(e) {
                        e.target.style.borderRight = ""
                    }),
                    document.addEventListener("mousemove", function(e) {
                        if (n) {
                            var d = e.pageX - t;
                            i && (i.style.width = r - d + "px"), n.style.width = o + d + "px"
                        }
                    }),
                    document.addEventListener("mouseup", function(e) {
                        n = void 0, i = void 0, t = void 0, r = void 0, o = void 0
                    })
            }

            function s(e) {
                var t = document.createElement("div");
                return t.style.top = 0,
                    t.style.right = 0,
                    t.style.width = "7px",
                    t.style.position = "absolute",
                    t.style.cursor = "col-resize",
                    t.style.userSelect = "none",
                    t.style.height = e + "px",
                    t
            }

            function l(e, t) {
                return window.getComputedStyle(e, null).getPropertyValue(t)
            }
        },
        adjustTreeHeight: function(activityType) {
            var treeHeight = document.getElementById(activityType + "-tree").offsetHeight;
            var widgetHeight = widget.body.clientHeight;
            if (treeHeight > widgetHeight) {
                widgetHeight = widgetHeight - 50;
                widget.body.getElement("#" + activityType + "-container").style.height = widgetHeight + "px";
                widget.body.getElement("#" + activityType + "-container").style.overflow = "auto";
            } else {
                widget.body.getElement("#" + activityType + "-container").style.overflow = "visible";
                widget.body.getElement("#" + activityType + "-container").style.height = "auto";
            }
        },
        importProjectAndActivitytoEnoviaFromLN: function() {
            PSIWidget.SelectedProject = PSIWidget.DataTable.rows('.selected').data().toArray()[0];
            if (PSIWidget.SelectedProject !== null && PSIWidget.SelectedProject !== undefined) {
                PSIWidget.ImportActivityData = [];
                var selectedTreeNodes = PSIWidget.ActivityTree.fancytree('getTree').getSelectedNodes();
                ProjectStructureImportUX.fetchImportInformationFromSelectedTreeNodes(selectedTreeNodes);
                if (PSIWidget.ImportActivityData.length > 0) {
                    PSIWidget.ImportData = {};
                    PSIWidget.ImportData.TableData = [];
                    PSIWidget.ImportData.TableData = PSIWidget.ImportActivityData;
                    PSIWidget.ImportData.Project = PSIWidget.SelectedProject.ProjectCode;
                    PSIWidget.ImportData.Description = PSIWidget.SelectedProject.Description;
                    ProjectStructureImportUX.sendImportRequest(PSIWidget.ImportData);
                } else {
                    ProjectStructureImportUX.warn("Select atleast one Activity!");
                }
            } else {
                ProjectStructureImportUX.warn("Select atleast one Project!");
            }
        },
        deselectActivityData: function() {
            if (PSIWidget.ActivityTree && PSIWidget.ActivityTree.fancytree) {
                var node = PSIWidget.ActivityTree.fancytree('getTree').getSelectedNodes();
                for (var i = 0; i < node.length; i++) {
                    console.log("Selected Node" + node[i].data.Activity);
                    node[i].unselectable = false;
                    node[i].setSelected(false);
                    if (node[i].data.Selectable == 0) {
                        node[i].unselectable = true;
                    }
                }
            }
        },
        deselectProjectData: function() {
            if (PSIWidget.ActivityTree && PSIWidget.ActivityTree.fancytree) {
                var node = PSIWidget.ActivityTree.fancytree('getTree').getSelectedNodes();
                for (var i = 0; i < node.length; i++) {
                    if (i == node.length - 1) {
                        if (node[i].data.Selectable == 0) {
                            node[i].unselectable = false;
                            node[i].setSelected(false);
                            node[i].unselectable = true;
                        }
                    }
                }
            }
        },
        fetchImportInformationFromSelectedTreeNodes: function(nodeList) {
            for (var i = 0; i < nodeList.length; i++) {
                var importActivityObject = {};
                importActivityObject = ProjectStructureImportUX.getLNImportActivityObject(nodeList[i]);
                PSIWidget.ImportActivityData.push(importActivityObject);
                ProjectStructureImportUX.fetchImportInformationOfParentNode(nodeList[i]);
            }
        },
        fetchImportInformationOfParentNode: function(childNode) {
            if (childNode.parent !== null) {
                var parentNode = childNode.parent;
                if (parentNode.title !== "root") {
                    var importActivityObject = {};
                    importActivityObject = ProjectStructureImportUX.getLNImportActivityObject(parentNode);
                    ProjectStructureImportUX.checkDuplicateEntryAndAddImportInformation(importActivityObject);
                    ProjectStructureImportUX.fetchImportInformationOfParentNode(parentNode);
                }
            }
        },
        checkDuplicateEntryAndAddImportInformation: function(importActivityObject) {
            var matched = false;
            for (var i = 0; i < PSIWidget.ImportActivityData.length; i++) {
                if (PSIWidget.ImportActivityData[i].Activity === importActivityObject.Activity) {
                    matched = true;
                }
            }
            if (!matched) {
                PSIWidget.ImportActivityData.push(importActivityObject);
            }
        },
        getLNImportActivityObject: function(treeNode) {
            var importActivityObject = {};
            importActivityObject.Project = treeNode.data.Project;
            importActivityObject.Activity = treeNode.data.Activity;
            importActivityObject.ParentActivity = treeNode.data.ParentActivity == "root" ? null : treeNode.data.ParentActivity;
            importActivityObject.ActivityType = treeNode.data.ActivityType;
            importActivityObject.ActivityDescription = treeNode.data.ActivityDescription;
            importActivityObject.ActivityStatus = treeNode.data.ActivityStatus;
            importActivityObject.AcutalEndDate = treeNode.data.AcutalEndDate;
            importActivityObject.NetQuantity = treeNode.data.NetQuantity;
            importActivityObject.Unit = treeNode.data.Unit;
            importActivityObject.ProductType = treeNode.data.ProductType;
            importActivityObject.ProductTypeDesc = treeNode.data.ProductTypeDesc;
            importActivityObject.Pos = treeNode.data.Pos;
            importActivityObject.Selectable = treeNode.data.Selectable;
            importActivityObject.ContractDeliverableNo = treeNode.data.ContractDeliverableNo;
            return importActivityObject;
        },
        sendImportRequest: function(requestData) {
            Mask.mask(widget.getElement("#Widget-Header"), i18n.PSI_ALERT_PROCESS_IMPORT);
            var serviceURL = WidgetConfiguration.PSI_ENOVIA_REST_SERVICE_URL + WidgetConfiguration.PSI_PROJECT_ACTIVITY_IMPORT_SERVICE_URL;
            console.log("Calling service: " + serviceURL);

            jquery.ajax({
                url: serviceURL,
                method: "POST",
                data: JSON.stringify(requestData),
                contentType: "application/json",
                cache: false,
                success: function(responseData) {
                    if (responseData.status && responseData.status === "OK") {
                        console.log("Response Data:");
                        console.log(responseData);
                        Mask.unmask(widget.getElement("#Widget-Header"));
                        ProjectStructureImportUX.success(i18n.PSI_ALERT_SUCCESS_IMPORT);
                    } else {
                        ProjectStructureImportUX.error(i18n.PSI_ALERT_ERROR_IMPORT);
                        Mask.unmask(widget.getElement("#Widget-Header"));
                        console.log("Failed to Import. Response Data:");
                        console.log(responseData);
                    }
                },
                error: function(xhr, exception, error) {
                    ProjectStructureImportUX.error(i18n.PSI_ALERT_ERROR_IMPORT);
                    Mask.unmask(widget.getElement("#Widget-Header"));
                    console.log("Failed to Import. Error: " + xhr.status);
                    console.log(exception);
                }
            });
        },
        message: function(message, className) {
            ProjectStructureImportUX.alert = PSIWidget.alert || null;
            if (ProjectStructureImportUX.alert != null) {
                ProjectStructureImportUX.alert.destroy();
            }
            var notErrorMessage = true;
            if (className == "error") {
                notErrorMessage = false;
            }
            ProjectStructureImportUX.alert = new Alert({
                closable: true,
                visible: true,
                autoHide: notErrorMessage,
                hideDelay: 3500,
                className: "wp-alert"
            }).inject(document.body, "top");
            ProjectStructureImportUX.alert.add({
                className: className,
                message: message
            });
            PSIWidget.alert = ProjectStructureImportUX.alert;
        },
        success: function(message) {
            ProjectStructureImportUX.message(message, "success");
        },
        warn: function(message) {
            ProjectStructureImportUX.message(message, "warning")
        },
        info: function(message) {
            ProjectStructureImportUX.message(message, "primary")
        },
        error: function(message) {
            ProjectStructureImportUX.message(message, "error")
        },
        setSearchButtonType: function() {
            widget.getElement(".ds-group-add .btn").setAttributes({
                type: 'button'
            });
        }
    };
    return ProjectStructureImportUX;
});