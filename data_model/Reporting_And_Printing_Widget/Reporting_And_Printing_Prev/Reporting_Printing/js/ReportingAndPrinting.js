/*
 * module VALCON/ReportingAndPrinting/ReportingAndPrinting
 * using enovia's built in AMD modules(mentioned below)
 * @param {module} UWA/Controls/Abstract - module is needed for building the widget
 * @param {module} DS/WAFData/WAFData - module is needed for fetching data from 3dSpace
 * @param {module} DS/LifecycleServices/LifecycleServicesSettings - module is needed for initializing widget & getting 3dSpace env URL
 * @param {module} DS/LifecycleServices/LifecycleServices - needed for obtaining current security context
 * @returns {reportingAndPrintingWidget} reportingAndPrintingWidget
 */
define("VALCON/ReportingAndPrinting/ReportingAndPrinting", ["UWA/Controls/Abstract", "DS/WAFData/WAFData", "DS/LifecycleServices/LifecycleServicesSettings", "DS/LifecycleServices/LifecycleServices"], function (uwaControlAbstract, WAFData, LifecycleServicesSettings, LifecycleServices) {
    var reportingAndPrintingWidget = uwaControlAbstract.extend({
        name: "ReportingAndPrintingWidget-container",
        tenant: null,
        enovia3dspaceurl: null,
        collabsharing: false,
        /*
         * This method is called on Load
         * @param reportingAndPrintingWidget
         * @param reportingAndPrintingWidgetBody
         * @param reportingAndPrintingWidgetOptions
         */
		 
        init: function (reportingAndPrintingWidget, reportingAndPrintingWidgetBody, reportingAndPrintingWidgetOptions) {
            /*
             * declaring global variables
             */
            /*
             * widget variables
             */
            this.widget = reportingAndPrintingWidget;
            this.parentWidget = reportingAndPrintingWidgetBody;
            var thisWidget = this;
			var selectedObjectIDD = '';
			var selectedObjectName = '';
			var selectedObjectType = '';
            this.widgetType = "ReportingAndPrinting";
            if (window.location.href.indexOf("ENOLCMS_AP") > -1) {
                console.log("ReportingAndPrinting: In Collab Sharing App!!!");
                thisWidget.collabsharing = true;
            }
            this._parent(reportingAndPrintingWidgetOptions);
            /*
             * set widget title
             */
            this.setTitle('');
            /*
             * object selected in 3dSpace
             * @type {json}
             */
            /*
             * declaring global variables----end
             */
            /*
             * widget execution begins here
             * call method useCompassContent to get the selected object from 3dSpace
             */
            LifecycleServicesSettings.app_initialization(function () {
                console.log("Initializing app.");
                if ($('#dummyframe').parent().hasClass('moduleContent')){
                    $('#dummyframe').parent().css({'overflow':'auto'});
                }
                thisWidget.useCompassContent();
		document.getElementById("itemName").value = this.selectedObjectName;
		if (this.selectedObjectType==="VPMReference") {
                    document.getElementById("selectName").selectedIndex = 1;
		}
                thisWidget.populateReportableAttributes(this.selectedObjectType,thisWidget);
                document.getElementById("submit").addEventListener("click", function(){

                    var seletedreportName = $('#selectName').find('option:selected').val();
                    var seletedreportLang = $('#reportLang').find('option:selected').val();
                    var seletedreportBasic = $('#selectbasic').find('option:selected').val();
                    var seletedreportFromat = $('#reportFormat').find('option:selected').val();
                    var bomLevel = $('#bomLevel').val();
                    var reportAttributes = "";
                    var attributeList = $('.list-group')[1].children;
                    for (var index=0;index<attributeList.length;index++){
                        if (reportAttributes.length>0){
                                reportAttributes += ",";
                        }
                        reportAttributes += attributeList[index].getAttribute('value');
                    }
                    thisWidget.temp(seletedreportName,seletedreportLang,seletedreportBasic,seletedreportFromat,bomLevel,reportAttributes);
		});
            });
			buildSkeleton();
        },
		temp: function (name,lang,basic,format,level,reportAttributes) {
			var requestUrl="https://3dspace-18xmigr2.plm.valmet.com:443/EnoviaRestService/export/multiLevelBomDataReport?";
			requestUrl = requestUrl+ "type="+selectedObjectType;
			requestUrl = requestUrl+"&objectId="+selectedObjectIDD;
			requestUrl = requestUrl+"&format="+format;
			requestUrl = requestUrl+"&lang="+lang;
			requestUrl = requestUrl+"&expandLevel="+level;
			requestUrl = requestUrl+"&download="+true;
                        if (reportAttributes.length>0)
                            requestUrl = requestUrl+"&attrs="+reportAttributes;
                        console.log(requestUrl);
                        // Pattern for range 0 to 99
                        var bomLevelPattern = new RegExp("^[0-9]?[0-9]$");
			if (bomLevelPattern.test(level)) {
                var link = document.createElement('a');
                link.href = requestUrl;
                document.body.appendChild(link);
                link.click();
			}
		},
		buildSkeleton: function () {
            console.log('injecting widget container in body');
			alert ("Skeleton build");
            var mainContainer = this.elements.container = UWA.createElement("div", {
                "class": this.getClassNames()
            }).inject(widget.body);
		},
        jsonpFunc: function (data) {
            alert("callback: " + data);
        },
        callback: function (data) {
            alert("callback: " + data);
        },
        setTitle: function (title) {
            this.widget.setTitle(title);
        },
        refresh: function () {
            this.makeUserPreference();
        },
        updateConfiguration: function (id) {
            alert("updateConfiguration");
        },
		setTenant: function (tnt) {
            this.tenant = tnt;
            widget.setValue("x3dPlatformId", tnt);
        },

        /*
         * check if objects from 3dSpace can be obtained
         * @returns {Boolean} gotSavedObjects
         */
        gotSavedObjects: function () {
            if (widget.getValue("Custo_roots_pids")) {
                var savedObjects = JSON.parse(widget.getValue("Custo_roots_pids"));
                if (savedObjects !== "undefined" && savedObjects !== null && savedObjects.length > 0) {
                    return true;
                }
            }
            return false;
        },
        populateReportableAttributes : function (objectType,widget) {
                $('body').on('click', '.list-group .list-group-item', function () {
                    $(this).toggleClass('active');
                    if (!$(this).hasClass('active')){
                        widget.toggleAttributeCheckboxState($(this),'glyphicon-check','glyphicon-unchecked');
                        return;
                    }
                    var uncheckCheckBox = true;
                    $(this).siblings().each(function (){
                        if (!$(this).hasClass('active')){
                            widget.toggleAttributeCheckboxState($(this),'glyphicon-check','glyphicon-unchecked');
                            uncheckCheckBox = false;
                            return;
                        }
                    });
                    if (uncheckCheckBox){
                        widget.toggleAttributeCheckboxState($(this),'glyphicon-unchecked','glyphicon-check');
                    }
                });
                $('.list-arrows button').click(function () {
                    var $button = $(this), actives = '';
                    if ($button.hasClass('move-left')) {
                        actives = $('.list-right ul li.active');
                        actives.clone().appendTo('.list-left ul');
                        actives.remove();
                    } else if ($button.hasClass('move-right')) {
                        actives = $('.list-left ul li.active');
                        actives.clone().appendTo('.list-right ul');
                        actives.remove();
                    }
                });
                $('.dual-list .selector').click(function () {
                    var $checkBox = $(this);
                    if (!$checkBox.hasClass('selected')) {
                        $checkBox.addClass('selected').closest('.well').find('ul li:not(.active)').addClass('active');
                        $checkBox.children('i').removeClass('glyphicon-unchecked').addClass('glyphicon-check');
                    } else {
                        $checkBox.removeClass('selected').closest('.well').find('ul li.active').removeClass('active');
                        $checkBox.children('i').removeClass('glyphicon-check').addClass('glyphicon-unchecked');
                    }
                });
                $('[name="SearchDualList"]').keyup(function (e) {
                    var code = e.keyCode || e.which;
                    if (code == '9')
                        return;
                    if (code == '27')
                        $(this).val(null);
                    var $rows = $(this).closest('.dual-list').find('.list-group li');
                    var val = $.trim($(this).val()).replace(/ +/g, ' ').toLowerCase();
                    $rows.show().filter(function () {
                        var text = $(this).text().replace(/\s+/g, ' ').toLowerCase();
                        return !~text.indexOf(val);
                    }).hide();
                });
                var requestUrl = "https://3dspace-18xmigr2.plm.valmet.com:443/EnoviaRestService/export/allSelectableAttributes?type=" + objectType;
                var httpRequest = new XMLHttpRequest();
                httpRequest.onreadystatechange = function () {
                    if (this.readyState == 4 && this.status == 200) {
                        var reportAttributeListLeft = $('.list-group')[0];
                        var reportAttributeListRight = $('.list-group')[1];
                        var responseObj = JSON.parse(this.responseText);
                        var attributesAndValues = responseObj.data.report_attributes;
                        var requiredAttributes = responseObj.data.required_attributes;
                        for (var key in attributesAndValues) {
                            if (attributesAndValues.hasOwnProperty(key)) {
                                console.log(key + " -> " + attributesAndValues[key]);
                                var liItem = document.createElement('li');
                                liItem.appendChild(document.createTextNode(key));
                                liItem.className = 'list-group-item active';
                                liItem.setAttribute('value', attributesAndValues[key]);
                                if (requiredAttributes.hasOwnProperty(key))
                                    reportAttributeListRight.appendChild(liItem);
                                else
                                    reportAttributeListLeft.appendChild(liItem);
                            }
                        }
                    }
                };
                httpRequest.open("GET", requestUrl);
                httpRequest.send();
        },
        toggleAttributeCheckboxState: function (element, state1, state2) {
            if (element.parent().parent().hasClass('text-left')){
                $('#checkBoxLeft').removeClass(state1).addClass(state2);
            } else {
                $('#checkBoxRight').removeClass(state1).addClass(state2);
            }
        },
        /*
         * get the object sent from 3dspace
         * @param {}
         */
        useCompassContent: function () {
            console.log("fetching object from 3dSpace");
            if (!this.gotSavedObjects()) {
                /*
                 * check if object can be obtained
                 * @type jsonObject
                 */
                var data = (typeof (this.widget.getValue("X3DContentId")) !== "undefined") ? JSON.parse(widget.getValue("X3DContentId")) : null;
                if (data !== null) {
                    var savedObject = data.data;
                    if (savedObject !== null) {
                        var savedObjects = [];
                        if (savedObject.items.length > 0) {
                            var item = savedObject.items[0];
                            /*
                             * check if any value is null
                             */
                            if (item.objectId !== null && item.objectId !== undefined) {
                                this.setTenant(item.envId);
                                if (this.externalRefresh) {
                                    this.externalRefresh.setTenant(this.tenant);
                                }
                                /*
                                 * prepare data of selected object
                                 * @type json
                                 */
                                var busObject = {
                                    physicalid: item.objectId
                                };
                                if (item.displayName !== null && item.displayName !== undefined) {
                                    busObject.displayName = item.displayName;
                                }
                                if (item.objectType !== null && item.objectType !== undefined) {
                                    busObject.objectType = item.objectType;
                                }
                                if (item.displayIdentifier !== null && item.displayIdentifier !== undefined) {
                                    busObject.displayIdentifier = item.displayIdentifier;
                                }
								selectedObjectIDD=item.objectId;
								selectedObjectName= item.displayIdentifier;
								selectedObjectType = item.objectType;
								console.log(item);
								console.log(busObject);
								console.log(savedObjects);
                                console.log("fetched object from 3dSpace: " + JSON.stringify(savedObjects));
                                savedObjects.push(busObject);
                            }
                        }
                    }
                } else {
                    alert("Please select an object in 3dSpace.");
                    this.drawErrorArea("Please select an object in 3dSpace.");
                }
            }
        }
    });
    return reportingAndPrintingWidget;
});