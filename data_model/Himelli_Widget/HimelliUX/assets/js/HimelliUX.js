define("VALCON/HimelliUX/HimelliUX", ["DS/Foundation/WidgetUwaUtils", "DS/Foundation/WidgetAPIs", "DS/ENO6WPlugins/jQuery_3.3.1", "VALCON/HimelliUX/PreferenceUtil", "VALCON/HimelliUX/ObjectSearchView", "VALCON/HimelliUX/ObjectModel", "DS/UIKIT/Input/Select", "DS/UIKIT/Input/Text", "DS/UIKIT/Input/Toggle", "DS/UIKIT/Tooltip", "DS/UIKIT/Alert", "VALCON/HimelliUX/WidgetConfiguration", "VALCON/HimelliUX/PreferenceConfiguration", "DS/UIKIT/Mask", "css!DS/HimelliUX/assets/css/HimelliUX", "i18n!DS/HimelliUX/assets/nls/HimelliUX", "DS/WAFData/WAFData", "UWA/Class/Promise", "DS/i3DXCompassPlatformServices/i3DXCompassPlatformServices"], function(WidgetUwaUtils, WidgetAPIs, Jquery, PreferenceUtil, ObjectSearchView, ObjectModel, Select, Text, Toggle, Tooltip, Alert, WidgetConfiguration, PreferenceConfiguration, Mask, css, i18n, WAFData, UWAPromise, i3DXCompassPlatformServices) {
    'use strict';
    var HimelliUX = {
        mailAddress: "",
        onLoad: function() {
            WidgetUwaUtils.setupEnoviaServer();
            window.isIFWE = true;
            window.enoviaServer.showSpace = "true";
            WidgetUwaUtils.onAfterLoad = Jquery.noop;
            window.enoviaServer.widgetId = widget.id;

            HimelliUX.init();
        },
        init: function() {
            window.Himelli = {
                "savedType": "savedType",
                "savedID": "savedID",
                "savedName": "savedName",
                "savedRevision": "savedRevision",
                "objectType": "objectType",
                "collabStorage": "collabStorage",
                populateSearchConfig: function() {
                    return [{
                        readWrite: "true",
                        name: "item",
                        revision: "revision",
                        attributeToDisplay: "name",
                        imageAttribute: "image",
                        singleObjectOnly: true,
                        searchTypes: Himelli.objectType,
                        validTypes: Himelli.objectType,
                        searchTitle: "Item Search",
                        typeAheadCallback: ObjectModel.typeAheadCallback,
                        type: "object"
                    }];
                },
                controls: {},
                helpers: {},
                fileReader: new FileReader(),
                baseURL: "",
                get3DSpaceBaseURL: function() {
                    i3DXCompassPlatformServices.getServiceUrl({
                        serviceName: "3DSpace",
                        platformId: "OnPremise",
                        onComplete: function(url) {
                            Himelli.baseURL = url;
                            HimelliUX.getMail(Himelli.baseURL + WidgetConfiguration.RP_GET_MAIL_URL).then(function(resolveData) {
                                HimelliUX.mailAddress = resolveData.email;
                            }, function(rejectData) {
                                console.log("Error while getting user mail:");
                                console.log(rejectData);
                            });
                        },
                        onFailure: function(t) {
                            console.log("Error while getting OnPremise 3DSpace baseURL:");
                            console.log(t);
                        }
                    });
                }
            };
            Himelli.get3DSpaceBaseURL();
            HimelliUX.getWidgetPreferences();
            HimelliUX.getWidgetBase();
        },
        getWidgetPreferences: function() {
            var reportFormatPref = {
                type: "list",
                name: PreferenceConfiguration.RP_PREF_NAME_REPORT_FORMAT,
                label: PreferenceConfiguration.RP_PREF_LABEL_REPORT_FORMAT,
                options: PreferenceConfiguration.RP_PREF_NAME_REPORT_FORMAT_OPTIONS,
                defaultValue: widget.getValue(PreferenceConfiguration.RP_PREF_NAME_REPORT_FORMAT) || PreferenceConfiguration.RP_PREF_REPORT_FORMAT_PREFERRED_VALUE
            };
            widget.addPreference(reportFormatPref);
            var templateLanguagePref = {
                type: "list",
                name: PreferenceConfiguration.RP_PREF_NAME_TEMPLATE_LANGUAGE,
                label: PreferenceConfiguration.RP_PREF_LABEL_TEMPLATE_LANGUAGE,
                options: PreferenceConfiguration.RP_PREF_NAME_TEMPLATE_LANGUAGE_OPTIONS,
                defaultValue: widget.getValue(PreferenceConfiguration.RP_PREF_NAME_TEMPLATE_LANGUAGE) || PreferenceConfiguration.RP_PREF_TEMPLATE_LANGUAGE_PREFERRED_VALUE
            };
            widget.addPreference(templateLanguagePref);
            var bomLevelPref = {
                type: "range",
                name: PreferenceConfiguration.RP_PREF_NAME_BOM_LEVEL,
                label: PreferenceConfiguration.RP_PREF_LABEL_BOM_LEVEL,
                defaultValue: widget.getValue(PreferenceConfiguration.RP_PREF_NAME_BOM_LEVEL) || PreferenceConfiguration.RP_PREF_BOM_LEVEL_PREFERRED_VALUE,
                min: PreferenceConfiguration.RP_PREF_NAME_BOM_LEVEL_MIN,
                max: PreferenceConfiguration.RP_PREF_NAME_BOM_LEVEL_MAX,
                step: PreferenceConfiguration.RP_PREF_NAME_BOM_LEVEL_STEP
            };
            widget.addPreference(bomLevelPref);
            var printDrawingInfoPref = {
                type: "boolean",
                name: PreferenceConfiguration.RP_PREF_NAME_PRINT_DRAWING,
                label: PreferenceConfiguration.RP_PREF_LABEL_PRINT_DRAWING,
                defaultValue: widget.getValue(PreferenceConfiguration.RP_PREF_NAME_PRINT_DRAWING) || PreferenceConfiguration.RP_PREF_PRINT_DRAWING_PREFERRED_VALUE
            };
            widget.addPreference(printDrawingInfoPref);
            var printSummaryReportPref = {
                type: "boolean",
                name: PreferenceConfiguration.RP_PREF_NAME_PRINT_SUMMARY,
                label: PreferenceConfiguration.RP_PREF_LABEL_PRINT_SUMMARY,
                defaultValue: widget.getValue(PreferenceConfiguration.RP_PREF_NAME_PRINT_SUMMARY) || PreferenceConfiguration.RP_PREF_PRINT_SUMMARY_PREFERRED_VALUE
            };
            widget.addPreference(printSummaryReportPref);
            var primaryLanguage = {
                type: "list",
                name: PreferenceConfiguration.RP_PREF_NAME_PRIMARY_LANGUAGE,
                label: PreferenceConfiguration.RP_PREF_LABEL_PRIMARY_LANGUAGE,
                options: PreferenceConfiguration.RP_PREF_NAME_PRIMARY_LANGUAGE_OPTIONS,
                defaultValue: widget.getValue(PreferenceConfiguration.RP_PREF_NAME_PRIMARY_LANGUAGE) || PreferenceConfiguration.RP_PREF_PRIMARY_LANGUAGE_PREFERRED_VALUE
            };
            widget.addPreference(primaryLanguage);
            var secondaryLanguage = {
                type: "list",
                name: PreferenceConfiguration.RP_PREF_NAME_SECONDARY_LANGUAGE,
                label: PreferenceConfiguration.RP_PREF_LABEL_SECONDARY_LANGUAGE,
                options: PreferenceConfiguration.RP_PREF_NAME_SECONDARY_LANGUAGE_OPTIONS,
                defaultValue: widget.getValue(PreferenceConfiguration.RP_PREF_NAME_SECONDARY_LANGUAGE) || PreferenceConfiguration.RP_PREF_SECONDARY_LANGUAGE_PREFERRED_VALUE
            };
            widget.addPreference(secondaryLanguage);
            var printDeliveryProjectInfo = {
                type: "hidden",
                name: PreferenceConfiguration.RP_PREF_NAME_PRINT_DELIVERY,
                label: PreferenceConfiguration.RP_PREF_LABEL_PRINT_DELIVERY,
                defaultValue: PreferenceConfiguration.RP_PREF_PRINT_DELIVERY_PREFERRED_VALUE
            };
            widget.addPreference(printDeliveryProjectInfo);
            var reportTypePref = {
                type: "hidden",
                name: PreferenceConfiguration.RP_PREF_NAME_REPORT_TYPE,
                label: PreferenceConfiguration.RP_PREF_LABEL_REPORT_TYPE,
                defaultValue: widget.getValue(PreferenceConfiguration.RP_PREF_NAME_REPORT_TYPE) || PreferenceConfiguration.RP_PREF_REPORT_TYPE_PREFERRED_VALUE
            };
            widget.addPreference(reportTypePref);
            var selectedAttribute = {
                type: "hidden",
                name: PreferenceConfiguration.RP_PREF_NAME_SELECTED_ATTRIBUTE,
                label: PreferenceConfiguration.RP_PREF_LABEL_SELECTED_ATTRIBUTE,
                defaultValue: widget.getValue(PreferenceConfiguration.RP_PREF_NAME_SELECTED_ATTRIBUTE) || PreferenceConfiguration.RP_PREF_SELECTED_ATTRIBUTE_PREFERRED_VALUE
            };
            widget.addPreference(selectedAttribute);
            var bomView = {
                type: "hidden",
                name: PreferenceConfiguration.RP_PREF_NAME_BOM_VIEW,
                label: PreferenceConfiguration.RP_PREF_LABEL_BOM_VIEW,
                defaultValue: widget.getValue(PreferenceConfiguration.RP_PREF_NAME_BOM_VIEW) || PreferenceConfiguration.RP_PREF_BOM_VIEW_PREFERRED_VALUE
            };
            widget.addPreference(bomView);
            var collabStorage = widget.getValue(Himelli.collabstorage);
            WidgetAPIs.getCollaborativeStorages(function(k) {
                WidgetUwaUtils.processStorages(k, collabStorage);
                WidgetUwaUtils.setStoragesPrefs(widget, k, collabStorage, "true");
                WidgetUwaUtils.processStorageChange.call(HimelliUX, collabStorage);
            });
        },
        submitReportParameters: function() {
            if (!widget.body.getElements(".invalid-input").length) {
                var validBOMLevel = widget.getValue(PreferenceConfiguration.RP_PREF_NAME_BOM_LEVEL).length;
                if (PreferenceUtil.get(Himelli.savedType) && PreferenceUtil.get(Himelli.savedID)) {
                    if (validBOMLevel) {
                        if (widget.getValue(PreferenceConfiguration.RP_PREF_NAME_PRIMARY_LANGUAGE) === widget.getValue(PreferenceConfiguration.RP_PREF_NAME_SECONDARY_LANGUAGE)) {
                            HimelliUX.warn(i18n.RP_ALERT_PRIMARY_SECONDARY_LANGUAGE_SAME);
                        } else {
                            var seletedreportName = jQuery('#reportType').find('option:selected').val();
                            var seletedreportLang = widget.getValue(PreferenceConfiguration.RP_PREF_NAME_TEMPLATE_LANGUAGE);
                            var seletedreportBasic = jQuery('#selectbasic').find('option:selected').val();
                            var docType = jQuery('#docType').val();
                            var seletedreportFormat = widget.getValue(PreferenceConfiguration.RP_PREF_NAME_REPORT_FORMAT);
                            var bomLevel = widget.getValue(PreferenceConfiguration.RP_PREF_NAME_BOM_LEVEL);
                            var printDrawing = widget.getValue(PreferenceConfiguration.RP_PREF_NAME_PRINT_DRAWING);
                            var printSummary = widget.getValue(PreferenceConfiguration.RP_PREF_NAME_PRINT_SUMMARY);
                            var printDelivery = widget.getValue(PreferenceConfiguration.RP_PREF_NAME_PRINT_DELIVERY);                         
                            var primaryLanguage = widget.getValue(PreferenceConfiguration.RP_PREF_NAME_PRIMARY_LANGUAGE);
                            var secondaryLanguage = widget.getValue(PreferenceConfiguration.RP_PREF_NAME_SECONDARY_LANGUAGE);

                            var reportAttributes = "";
                            var attributeList = jQuery('.list-group')[1].children;
                            for (var index = 0; index < attributeList.length; index++) {
                                if (reportAttributes.length > 0) {
                                    reportAttributes += ",";
                                }
                                reportAttributes += attributeList[index].getAttribute('value');
                            }                           
								
                            var isLatest = jQuery('#latestBom').is(':checked');
                            HimelliUX.generateHimelliReport(bomLevel,reportAttributes,seletedreportFormat,seletedreportLang,primaryLanguage, secondaryLanguage,docType,isLatest);								
							
                        }
                    } else {
                        HimelliUX.warn(i18n.RP_ALERT_BOM_LEVEL);
                    }
                } else {
                    if (!validBOMLevel) {
                        HimelliUX.warn(i18n.RP_ALERT_ITEM_NAME_BOM_LEVEL);
                    } else {
                        HimelliUX.warn(i18n.RP_ALERT_ITEM_NAME);
                    }
                }
            } else {
                HimelliUX.warn(i18n.RP_ALERT_INVALID_INPUT);
            }
        },
        generateHimelliReport: function(bomLevel,reportAttributes,seletedreportFormat,seletedreportLang,primaryLanguage, secondaryLanguage, docType,isLatest) {
            var itemType = PreferenceUtil.get(Himelli.savedType);
            var itemName = PreferenceUtil.get(Himelli.savedName);
            var itemRev = PreferenceUtil.get(Himelli.savedRevision);
            var submitURL = "";
            var format = seletedreportFormat;
            var requester = "himelli";
                submitURL = WidgetConfiguration.RP_ENOVIA_REST_SERVICE_URL + WidgetConfiguration.RP_SPR_REPORT_GENERATE_SERVICE_URL;
				submitURL = submitURL + "type=" + itemType;
                submitURL = submitURL + "&name=" + itemName;
                submitURL = submitURL + "&format=" + seletedreportFormat;
                submitURL = submitURL + "&lang=" + seletedreportLang;
               // submitURL = submitURL + "&printDrawing=" + printDrawing;
                submitURL = submitURL + "&rev=" + itemRev;
                submitURL = submitURL + "&download=" + true;				
                submitURL = submitURL + "&isLatest=" + isLatest;
                submitURL = submitURL + "&primaryLang=" + primaryLanguage;
                submitURL = submitURL + "&secondaryLang=" + secondaryLanguage;
                submitURL = submitURL + "&expandLevel=" + bomLevel; 
                submitURL = submitURL + "&requester=" + requester; 				
                submitURL = submitURL + "&drawingType=" + docType;
			
            if (reportAttributes.length > 0) {
                var timestampNow = new Date().getTime();
                submitURL = submitURL + "&attrs=" + reportAttributes;
               // submitURL = submitURL + "&v=" + timestampNow;
                var fileName = itemType + "_" + itemName + "_" + timestampNow + "." + format;
                submitURL = submitURL + "&rptFileName=" + encodeURIComponent(fileName);               
                 if (window.navigator.msSaveBlob) {
                    try {
                        var request = new XMLHttpRequest();
                        request.open("GET", submitURL, true);
                        request.responseType = "blob";
                        request.onload = function() {
                            if (request.readyState == 4 && request.status === 200) {
                                if (this.response.type === "application/json") {
                                    Himelli.fileReader.readAsText(this.response);
                                    Himelli.fileReader.onload = function(e) {
                                        var serviceResponse = Himelli.fileReader.result;
                                        try {
                                            serviceResponse = JSON.parse(serviceResponse);
                                            if (serviceResponse.status !== null || serviceResponse.status !== undefined) {
                                                if (serviceResponse.status === "OK") {
                                                    console.log(serviceResponse);
                                                    Mask.unmask(widget.getElement(".container"));
                                                    if (serviceResponse.data !== null || serviceResponse.data !== undefined) {
                                                        HimelliUX.success(serviceResponse.data);
                                                    } else {
                                                        HimelliUX.success(i18n.RP_REPORT_GENERATION_OK);
                                                    }
                                                } else if (serviceResponse.status === "FAILED") {
                                                    if (serviceResponse.systemErrors !== null || serviceResponse.systemErrors !== undefined) {
                                                        console.log(serviceResponse.systemErrors[0]);
                                                    } else {
                                                        console.log(serviceResponse);
                                                    }
                                                    Mask.unmask(widget.getElement(".container"));
                                                    HimelliUX.error(i18n.RP_REPORT_GENERATION_FAILED);
                                                }
                                            } else {
                                                console.log(serviceResponse);
                                                Mask.unmask(widget.getElement(".container"));
                                                HimelliUX.error(i18n.RP_REPORT_GENERATION_FAILED);
                                            }
                                        } catch (error) {
                                            console.log(serviceResponse);
                                            Mask.unmask(widget.getElement(".container"));
                                            HimelliUX.error(i18n.RP_REPORT_GENERATION_FAILED);
                                        }
                                    };
                                } else {
                                    var type = request.getResponseHeader('Content-Type');
                                    var blob = new Blob([this.response], {
                                        type: type
                                    });
                                    window.navigator.msSaveBlob(blob, fileName);
                                    Mask.unmask(widget.getElement(".container"));
                                    HimelliUX.success(i18n.RP_REPORT_GENERATION_OK);
                                }
                            } else if (this.status > 200) {
                                console.log("File Download Error " + request.status);
                                if (this.status === 406) {
                                    Himelli.fileReader.readAsText(this.response);
                                    Himelli.fileReader.onload = function(e) {
                                        console.log(Himelli.fileReader.result);
                                    };
                                }
                                Mask.unmask(widget.getElement(".container"));
                                HimelliUX.error(i18n.RP_REPORT_GENERATION_FAILED);
                            }
                        };
                        request.onerror = function() {
                            Mask.unmask(widget.getElement(".container"));
                            HimelliUX.error(i18n.RP_REPORT_GENERATION_FAILED);
                        };
                        request.onabort = function() {
                            Mask.unmask(widget.getElement(".container"));
                            HimelliUX.error(i18n.RP_REPORT_GENERATION_FAILED);
                        };
                        Mask.mask(widget.getElement(".container"), i18n.RP_REPORT_GENERATION_ON_GOING);
                        request.send();
                    } catch (error) {
                        Mask.unmask(widget.getElement(".container"));
                        HimelliUX.error(i18n.RP_REPORT_GENERATION_FAILED);
                        console.log("Report Generation Service call error: " + error);
                    }
                } else {
                    try {
                        var request = new XMLHttpRequest();
                        request.open("GET", submitURL, true);
                        request.responseType = "blob";
                        request.onload = function() {
                            if (request.readyState == 4 && request.status === 200) {
                                if (this.response.type === "application/json") {
                                    Himelli.fileReader.readAsText(this.response);
                                    Himelli.fileReader.onload = function(e) {
                                        var serviceResponse = Himelli.fileReader.result;
                                        try {
                                            serviceResponse = JSON.parse(serviceResponse);
                                            if (serviceResponse.status !== null || serviceResponse.status !== undefined) {
                                                if (serviceResponse.status === "OK") {
                                                    console.log(serviceResponse);
                                                    Mask.unmask(widget.getElement(".container"));
                                                    if (serviceResponse.data !== null || serviceResponse.data !== undefined) {
                                                        HimelliUX.success(serviceResponse.data);
                                                    } else {
                                                        HimelliUX.success(i18n.RP_REPORT_GENERATION_OK);
                                                    }
                                                } else if (serviceResponse.status === "FAILED") {
                                                    if (serviceResponse.systemErrors !== null || serviceResponse.systemErrors !== undefined) {
                                                        console.log(serviceResponse.systemErrors[0]);
                                                    } else {
                                                        console.log(serviceResponse);
                                                    }
                                                    Mask.unmask(widget.getElement(".container"));
                                                    HimelliUX.error(i18n.RP_REPORT_GENERATION_FAILED);
                                                }
                                            } else {
                                                console.log(serviceResponse);
                                                Mask.unmask(widget.getElement(".container"));
                                                HimelliUX.error(i18n.RP_REPORT_GENERATION_FAILED);
                                            }
                                        } catch (error) {
                                            console.log(serviceResponse);
                                            Mask.unmask(widget.getElement(".container"));
                                            HimelliUX.error(i18n.RP_REPORT_GENERATION_FAILED);
                                        }
                                    };
                                } else {
                                    var type = request.getResponseHeader('Content-Type');
                                    var blob = new Blob([this.response], {
                                        type: type
                                    });
                                    var downloadUrl = URL.createObjectURL(blob);
                                    var a = document.createElement("a");
                                    a.href = downloadUrl;
                                    a.download = fileName;
                                    document.body.appendChild(a);
                                    a.click();
                                    Mask.unmask(widget.getElement(".container"));
                                    HimelliUX.success(i18n.RP_REPORT_GENERATION_OK);
                                    setTimeout(function() {
                                        URL.revokeObjectURL(downloadUrl);
                                    }, 100);
                                    a.remove();
                                }
                            } else if (request.status > 200) {
                                console.log("File Download Error " + request.status);
                                if (this.status === 406) {
                                    Himelli.fileReader.readAsText(this.response);
                                    Himelli.fileReader.onload = function(e) {
                                        console.log(Himelli.fileReader.result);
                                    };
                                }
                                Mask.unmask(widget.getElement(".container"));
                                HimelliUX.error(i18n.RP_REPORT_GENERATION_FAILED);
                            }
                        };
                        request.onerror = function() {
                            Mask.unmask(widget.getElement(".container"));
                            HimelliUX.error(i18n.RP_REPORT_GENERATION_FAILED);
                        };
                        request.onabort = function() {
                            Mask.unmask(widget.getElement(".container"));
                            HimelliUX.error(i18n.RP_REPORT_GENERATION_FAILED);
                        };
                        Mask.mask(widget.getElement(".container"), i18n.RP_REPORT_GENERATION_ON_GOING);
                        request.send();
                    } catch (error) {
                        Mask.unmask(widget.getElement(".container"));
                        HimelliUX.error(i18n.RP_REPORT_GENERATION_FAILED);
                        console.log("Report Generation Service call error: " + error);
                    }
                }
            } else {
                HimelliUX.warn(i18n.RP_ALERT_ATTRIBUTE_LIST);
            }
        },
       
        getMail: function(url, method) {
            var getMailPromise = new UWAPromise(function(reseolveFunction, rejectFunction) {
                WAFData.authenticatedRequest(url, {
                    method: null == method ? "GET" : "POST",
                    type: "json",
                    proxy: "passport",
                    timeout: 3e4,
                    headers: {
                        Accept: "application/json",
                        "Content-Type": "application/json",
                        "Accept-Language": widget.lang
                    },
                    onComplete: function(data) {
                        reseolveFunction(data);
                    },
                    onFailure: function(error) {
                        rejectFunction(error);
                    }
                });
            });
            return getMailPromise;
        },
        onRefresh: function() {
            Himelli.controls.reportType.setValue(widget.getValue(PreferenceConfiguration.RP_PREF_NAME_REPORT_TYPE) || PreferenceConfiguration.RP_PREF_REPORT_TYPE_PREFERRED_VALUE);
            var objectType = "";
            if (widget.getValue(PreferenceConfiguration.RP_PREF_NAME_REPORT_TYPE)) {
                var reportType = widget.getValue(PreferenceConfiguration.RP_PREF_NAME_REPORT_TYPE);
                if (reportType === PreferenceConfiguration.RP_PREF_REPORT_TYPE_PREFERRED_VALUE || reportType === "spr") {
                    objectType = WidgetConfiguration.RP_ITEM_TYPE_DEFAULT;
                    var objectTypeArray = objectType.split(',');
                    objectType = objectTypeArray[0];

                } else if (reportType !== PreferenceConfiguration.RP_PREF_REPORT_TYPE_PREFERRED_VALUE) {
                    objectType = WidgetConfiguration.RP_ITEM_TYPE_SELECTABLE;
                }
                Himelli.objectType = widget.getValue(PreferenceConfiguration.RP_PREF_NAME_REPORT_TYPE) === PreferenceConfiguration.RP_PREF_REPORT_TYPE_PREFERRED_VALUE ? WidgetConfiguration.RP_ITEM_TYPE_DEFAULT : WidgetConfiguration.RP_ITEM_TYPE_SELECTABLE;
            } else {
                Himelli.objectType = WidgetConfiguration.RP_ITEM_TYPE_DEFAULT;
                objectType = WidgetConfiguration.RP_ITEM_TYPE_DEFAULT;
                var objectTypeArray = objectType.split(',');
                objectType = objectTypeArray[0];
            }
            HimelliUX.populateReportableAttributes(objectType);
            widget.body.getElement(".autocomplete-input").value = "";         
            HimelliUX.resetSelectedItemValue();
        },
        onAfterLoad: function() {
            HimelliUX.resetSelectedItemValue();
            HimelliUX.configureSearchView();
            var reportType = widget.getValue(PreferenceConfiguration.RP_PREF_NAME_REPORT_TYPE);
            var objectType = "";
            if (reportType === PreferenceConfiguration.RP_PREF_REPORT_TYPE_PREFERRED_VALUE || reportType === "spr" ) {
                objectType = WidgetConfiguration.RP_ITEM_TYPE_DEFAULT;
                var objectTypeArray = objectType.split(',');
                objectType = objectTypeArray[0];
            } else if (reportType !== PreferenceConfiguration.RP_PREF_REPORT_TYPE_PREFERRED_VALUE) {
                objectType = WidgetConfiguration.RP_ITEM_TYPE_SELECTABLE;
            }
            HimelliUX.populateReportableAttributes(objectType);
            HimelliUX.setSearchButtonType();
        },
        configureSearchView: function() {
            Himelli.view = new ObjectSearchView();

            var searchContainer = widget.body.getElement("#itemNameContainer");
            Himelli.view.render().inject(searchContainer);

            var reportType = widget.getElement("#reportType");
            reportType.addEvent("change", HimelliUX.handleReportTypeChange);
        },
        populateReportableAttributes: function(itemType) {
            var selectedAttributePreferred = [];
            var selectedAttributeValue = widget.getValue(PreferenceConfiguration.RP_PREF_NAME_SELECTED_ATTRIBUTE);
            if (selectedAttributeValue.length) {
                selectedAttributePreferred = widget.getValue(PreferenceConfiguration.RP_PREF_NAME_SELECTED_ATTRIBUTE).split("\|");
            }
           
		   var requestUrl =  WidgetConfiguration.RP_ENOVIA_REST_SERVICE_URL + WidgetConfiguration.SPR_ALL_SELECTABLE_ATTRIBUTES_URL+ itemType + "&requester=himelli";
            var httpRequest = new XMLHttpRequest();
            httpRequest.onreadystatechange = function() {
                if (this.readyState == 4 && this.status == 200) {
                    var reportAttributeListLeft = jQuery('.list-group')[0];
                    var reportAttributeListRight = jQuery('.list-group')[1];
                    jQuery('.list-group')[0].empty();
                    jQuery('.list-group')[1].empty();
                    var responseObj = JSON.parse(this.responseText);
                    var attributesAndValues = responseObj.data.report_attributes;
                    var requiredAttributes = responseObj.data.required_attributes;
                    for (var key in attributesAndValues) {
                        if (attributesAndValues.hasOwnProperty(key)) {
                            var liItem = document.createElement('li');
                            liItem.appendChild(document.createTextNode(key));
                            liItem.className = 'list-group-item active';
                            liItem.setAttribute('value', attributesAndValues[key]);

                            if (selectedAttributePreferred.length) {
                                if (selectedAttributePreferred.indexOf(attributesAndValues[key]) !== -1) {
                                    reportAttributeListRight.appendChild(liItem);
                                } else {
                                    reportAttributeListLeft.appendChild(liItem);
                                }
                            } else {
                                if (requiredAttributes.hasOwnProperty(key))
                                    reportAttributeListRight.appendChild(liItem);
                                else
                                    reportAttributeListLeft.appendChild(liItem);
                            }
                        }
                    }
                    HimelliUX.addSelectedAttributeinPreference();
                }
            };
            httpRequest.open("GET", requestUrl);
            httpRequest.send();
        },
        handleReportTypeChange: function(event) {
            var selectedReportName = event.target.options[event.target.selectedIndex].value;
            widget.setValue("ReportType", selectedReportName);
            var objectType = "";
            jQuery('#docTypeContainer').html('');
            if(selectedReportName === "spr"){
                document.getElementById("reportSingleBomType").disabled=true;
                Himelli.objectType = WidgetConfiguration.RP_ITEM_TYPE_DEFAULT;
                objectType = WidgetConfiguration.RP_ITEM_TYPE_DEFAULT;
                var objectTypeArray = objectType.split(',');
                objectType = objectTypeArray[0];
				HimelliUX.injectDocTypeSelectComponent(PreferenceConfiguration.MBOM_REPORT_DRAWING_TYPES);
            }
            Himelli.view._searchBar.setConfig(Himelli.populateSearchConfig());
            Himelli.view.render();
            HimelliUX.setSearchButtonType();
            if( selectedReportName === "spr"){
                Himelli.view.setPlaceHolderText(i18n.RP_TEXT_MBOM_ITEM_SEARCH);
            }
            widget.setValue(PreferenceConfiguration.RP_PREF_NAME_SELECTED_ATTRIBUTE, "");
            HimelliUX.populateReportableAttributes(objectType);
            HimelliUX.resetSelectedItemValue();
            HimelliUX.addSelectedAttributeinPreference();			
        },
        resetSelectedItemValue: function() {
            PreferenceUtil.set(Himelli.savedID, "");
            PreferenceUtil.set(Himelli.savedType, "");
            PreferenceUtil.set(Himelli.savedName, "");
        },
        addSelectedAttributeinPreference: function() {
            var selectedAttributes = "";
            var attributeList = jQuery('.list-group')[1].children;
            for (var index = 0; index < attributeList.length; index++) {
                if (selectedAttributes.length > 0) {
                    selectedAttributes += "|";
                }
                selectedAttributes += attributeList[index].getAttribute('value');
            }
            widget.setValue(PreferenceConfiguration.RP_PREF_NAME_SELECTED_ATTRIBUTE, selectedAttributes);
        },
        getWidgetBase: function() {
            var dummayFrame = {
                tag: 'iframe',
                id: 'dummyframe',
                attributes: {
                    height: "0",
                    width: "0",
                    border: "0",
                    name: "dummyframe"
                }
            };
            var widgeContentContainer = {
                tag: "div",
                class: "container",
                html: [{
                    tag: "form",
                    class: "form-horizontal",
                    id: "widgetMain",
                    attributes: {
                        action: 'submitscript.jsp',
                        target: 'dummyframe'
                    },
                    html: [{
                        tag: 'fieldset',
                        html: [{
                            tag: 'legend',
                            text: i18n.RP_LEGEND_HEADER
                        }, {
                            tag: "div",
                            class: "form-group",
                            html: [{
                                tag: "label",
                                class: "col-md-2 control-label",
                                for: "reportType",
                                text: i18n.RP_LABEL_REPORT_TYPE
                            }, {
                                tag: "div",
                                class: "col-md-6",
                                id: "reportNameContainer",
                            }, {
                                tag: "div",
                                class: "col-md-2",
                                id: "reportMultiBomTypeContainer",
                            }, {
                                tag: "div",
                                class: "col-md-2",
                                id: "reportSingleBomTypeContainer",
                            }]
                        }, {
                            tag: "div",
                            class: "form-group",
                            html: [{
                                tag: "label",
                                class: "col-md-2 control-label",
                                for: "itemName",
                                html: [i18n.RP_LABEL_ITEM_NAME, {
                                    tag: "span",
                                    class: "label-field-required",
                                    text: " *"
                                }]
                            }, {
                                tag: "div",
                                class: "col-md-8",
                                id: "itemNameContainer"
                            }, {
                                tag: "label",
                                class: "col-md-1 control-label",
                                for: "revision",
                                text: i18n.RP_LABEL_REVISION
                            }, {
                                tag: "div",
                                class: "col-md-1",
                                id: "revisionContainer",
                            }]
                        }, {
                            tag: 'div',
                            class: 'form-group',
                            html: [{
                                    tag: 'label',
                                    class: 'col-md-2 control-label',
                                    text: i18n.RP_LABEL_ATTRIBUTES,
                                    attributes: {
                                        for: 'attr'
                                    }
                                },
                                {
                                    tag: 'div',
                                    class: 'dual-list list-left col-md-4',
                                    html: [{
                                        tag: 'div',
                                        class: 'well text-left',
                                        html: [{
                                                tag: 'div',
                                                class: 'row',
                                                html: [{
                                                        tag: 'div',
                                                        class: 'col-xs-2',
                                                        html: [{
                                                            tag: 'div',
                                                            class: 'btn-group',
                                                            html: [{
                                                                tag: 'a',
                                                                id: 'checkBoxLeftParent',
                                                                class: 'btn btn-default dual-list-buttons selector selected',
                                                                html: [{
                                                                    tag: 'i',
                                                                    id: 'checkBoxLeft',
                                                                    class: 'fonticon fonticon-select-all'
                                                                }]
                                                            }]
                                                        }]
                                                    },
                                                    {
                                                        tag: 'div',
                                                        class: 'col-xs-10',
                                                        html: [{
                                                            tag: 'div',
                                                            class: 'input-group',
                                                            html: [{
                                                                    tag: 'input',
                                                                    class: 'form-control',
                                                                    attributes: {
                                                                        name: 'SearchDualList',
                                                                        placeholder: i18n.RP_TEXT_ATTRIBUTE_SEARCH,
                                                                        type: 'text'
                                                                    }
                                                                },
                                                                {
                                                                    tag: 'span',
                                                                    class: 'input-group-addon fonticon fonticon-search'
                                                                }
                                                            ]
                                                        }]
                                                    }
                                                ]
                                            },
                                            {
                                                tag: 'ul',
                                                class: 'list-group'
                                            }
                                        ]
                                    }]
                                },
                                {
                                    tag: 'div',
                                    class: 'list-arrows col-md-2 text-center',
                                    html: [{
                                            tag: 'button',
                                            class: 'btn  dual-list-buttons btn-default move-left',
                                            attributes: {
                                                type: 'button'
                                            },
                                            html: [{
                                                tag: 'span',
                                                class: 'fonticon fonticon-fast-backward'
                                            }]
                                        },
                                        {
                                            tag: 'button',
                                            class: 'btn btn-default dual-list-buttons move-right',
                                            attributes: {
                                                type: 'button'
                                            },
                                            html: [{
                                                tag: 'span',
                                                class: 'fonticon fonticon-fast-forward'
                                            }]
                                        }
                                    ]
                                },
                                {
                                    tag: 'div',
                                    class: 'dual-list list-right col-md-4',
                                    html: [{
                                        tag: 'div',
                                        class: 'well',
                                        html: [{
                                                tag: 'div',
                                                class: 'row',
                                                html: [{
                                                        tag: 'div',
                                                        class: 'col-xs-2',
                                                        html: [{
                                                            tag: 'div',
                                                            class: 'btn-group',
                                                            html: [{
                                                                tag: 'a',
                                                                id: 'checkBoxRightParent',
                                                                class: 'btn btn-default dual-list-buttons selector selected',
                                                                html: [{
                                                                    tag: 'i',
                                                                    id: 'checkBoxRight',
                                                                    class: 'fonticon fonticon-select-all'
                                                                }]
                                                            }]
                                                        }]
                                                    },
                                                    {
                                                        tag: 'div',
                                                        class: 'col-xs-10',
                                                        html: [{
                                                            tag: 'div',
                                                            class: 'input-group',
                                                            html: [{
                                                                    tag: 'input',
                                                                    class: 'form-control',
                                                                    attributes: {
                                                                        name: 'SearchDualList',
                                                                        placeholder: i18n.RP_TEXT_ATTRIBUTE_SEARCH,
                                                                        type: 'text'
                                                                    }
                                                                },
                                                                {
                                                                    tag: 'span',
                                                                    class: 'input-group-addon fonticon fonticon-search'
                                                                }
                                                            ]
                                                        }]
                                                    }
                                                ]
                                            },
                                            {
                                                tag: 'ul',
                                                class: 'list-group'
                                            }
                                        ]
                                    }]
                                }
                            ]
                        }, {
                            tag: "div",
                            class: "form-group",
                            html: [{
                                tag: "label",
                                class: "col-md-2 control-label",
                                for: "docTypeTitle",
                                text: i18n.RP_LABEL_DOCTYPE
                            }, {
                                tag: "div",
                                class: "col-md-10",
                                id: "docTypeContainer",
                            }]
                        }, {
                            tag: 'div',
                            class: 'form-group',
                            html: [{
                                tag: 'div',
                                class: 'col-md-6 col-md-offset-6 col-xs-offset-5',
                                html: [{
                                    tag: 'button',
                                    id: 'submit',
                                    name: 'submit',
                                    class: 'btn btn-primary',
                                    attributes: {
                                        type: 'button'
                                    },
                                    text: i18n.RP_LABEL_SUBMIT
                                }]
                            }]
                        }]
                    }]
                }]
            };

            widget.setBody([dummayFrame, widgeContentContainer]);
            widget.body.setStyle("overflow", "auto");
            HimelliUX.populateUIKITControls();
            HimelliUX.handleDualListEvent();
            HimelliUX.populateToolTips();
            HimelliUX.submitButtonClickHandler();
        },
        submitButtonClickHandler: function() {
            var submitButton = widget.getElement("#submit");
            submitButton.addEvent("click", HimelliUX.submitReportParameters);
        },
        handleDualListEvent: function() {
            jQuery('body').on('click', '.list-group .list-group-item', function() {
                jQuery(this).toggleClass('active');
                if (!jQuery(this).hasClass('active')) {
                    HimelliUX.toggleAttributeCheckboxState(jQuery(this), 'fonticon-select-all', 'fonticon-select-none');
                    return;
                }
                var uncheckCheckBox = true;
                jQuery(this).siblings().each(function() {
                    if (!jQuery(this).hasClass('active')) {
                        HimelliUX.toggleAttributeCheckboxState(jQuery(this), 'fonticon-select-all', 'fonticon-select-none');
                        uncheckCheckBox = false;
                        return;
                    }
                });
                if (uncheckCheckBox) {
                    HimelliUX.toggleAttributeCheckboxState(jQuery(this), 'fonticon-select-none', 'fonticon-select-all');
                }
            });
            jQuery('.list-arrows button').click(function() {
                var jQuerybutton = jQuery(this),
                    actives = '';
                if (jQuerybutton.hasClass('move-left')) {
                    actives = jQuery('.list-right ul li.active');
                    actives.clone().appendTo('.list-left ul');
                    actives.remove();
                } else if (jQuerybutton.hasClass('move-right')) {
                    actives = jQuery('.list-left ul li.active');
                    actives.clone().appendTo('.list-right ul');
                    actives.remove();
                }
                HimelliUX.addSelectedAttributeinPreference();
            });
            jQuery('.dual-list .selector').click(function() {
                var jQuerycheckBox = jQuery(this);
                if (!jQuerycheckBox.hasClass('selected')) {
                    jQuerycheckBox.addClass('selected').closest('.well').find('ul li:not(.active)').addClass('active');
                    jQuerycheckBox.children('i').removeClass('fonticon-select-none').addClass('fonticon-select-all');
                } else {
                    jQuerycheckBox.removeClass('selected').closest('.well').find('ul li.active').removeClass('active');
                    jQuerycheckBox.children('i').removeClass('fonticon-select-all').addClass('fonticon-select-none');
                }
            });
            jQuery('[name="SearchDualList"]').keyup(function(e) {
                var code = e.keyCode || e.which;
                if (code == '9')
                    return;
                if (code == '27')
                    jQuery(this).val(null);
                var jQueryrows = jQuery(this).closest('.dual-list').find('.list-group li');
                var val = jQuery.trim(jQuery(this).val()).replace(/ +/g, ' ').toLowerCase();
                jQueryrows.show().filter(function() {
                    var text = jQuery(this).text().replace(/\s+/g, ' ').toLowerCase();
                    return !~text.indexOf(val);
                }).hide();
            });
        },
        injectDocTypeSelectComponent: function(options) {
            Himelli.controls.docType = new Select({
                name: "docType",
                id: "docType",
                placeholder: false,
                multiple: true,
                options: options,
                events: []
            }).inject(widget.getElement("#docTypeContainer"));
        },
        populateUIKITControls: function() {
            var reportType = Himelli.controls.reportType = new Select({
                name: "reportType",
                id: "reportType",
                placeholder: false,
				disabled: true,
                options: [ {
                    label: "SPR Report",
                    value: "spr"
                }],
                events: []
            }).inject(widget.getElement("#reportNameContainer"));
            reportType.setValue(widget.getValue(PreferenceConfiguration.RP_PREF_NAME_REPORT_TYPE) || PreferenceConfiguration.RP_PREF_REPORT_TYPE_PREFERRED_VALUE);
            var BOMViewType = widget.getValue(PreferenceConfiguration.RP_PREF_NAME_BOM_VIEW) || PreferenceConfiguration.RP_PREF_LABEL_BOM_VIEW;
           
            var latestBom = Himelli.controls.latestBom = new Toggle({
                name: "latestBom",
                id: "latestBom",
                type: "checkbox",
                label: "Latest Bom",
                value: "latestBom",
                checked: BOMViewType === "latestBom" ? true : false,
                events: {
                    onChange: function() {
                        widget.setValue(PreferenceConfiguration.RP_PREF_NAME_BOM_VIEW, "latestBom");
                    }
                }
            }).inject(widget.getElement("#reportSingleBomTypeContainer"));

            HimelliUX.injectDocTypeSelectComponent(PreferenceConfiguration.MBOM_REPORT_DRAWING_TYPES);        
            var revision = Himelli.controls.revision = new Text({
                name: "revision",
                id: "revision",
                type: "text",
                class: "form-control",
                disabled: true,
            }).inject(widget.getElement("#revisionContainer"));
          
           
        },       
        addControlValidation: function(event) {
            var parentContainer = event.target.parentElement;
            var textControl = parentContainer.getElement(".form-control");
            var textControlID = textControl.id;
            var textHelperID = parentContainer.getElement(".form-control-helper-text").id;
            var value = textControl.value;
            var valueLength = value.length;
            if (valueLength > WidgetConfiguration.RP_TEXT_MAX_LENGTH) {
                widget.body.getElement("#" + textControlID).addClassName("input-error");
                widget.body.getElement("#" + textHelperID).addClassName("invalid-input");
                widget.body.getElement("#" + textHelperID).setText(UWA.String.format(i18n.RP_ERROR_MESSAGE_MAX_LENGTH, WidgetConfiguration.RP_TEXT_MAX_LENGTH));
            } else {
                widget.body.getElement("#" + textControlID).removeClassName("input-error");
                widget.body.getElement("#" + textHelperID).removeClassName("invalid-input");
                widget.body.getElement("#" + textHelperID).setText("");
            }
            if (valueLength <= WidgetConfiguration.RP_TEXT_MAX_LENGTH) {
                var validated = value.test(WidgetConfiguration.RP_TEXT_REGEX);
                if (!validated) {
                    widget.body.getElement("#" + textControlID).addClassName("input-error");
                    widget.body.getElement("#" + textHelperID).addClassName("invalid-input");
                    widget.body.getElement("#" + textHelperID).setText(i18n.RP_ERROR_MESSAGE_SPECIAL_CHARACTER);
                } else {
                    widget.body.getElement("#" + textControlID).removeClassName("input-error");
                    widget.body.getElement("#" + textHelperID).removeClassName("invalid-input");
                    widget.body.getElement("#" + textHelperID).setText("");
                }
            }
        },
        populateToolTips: function() {
            var checkBoxLeftToolTip = new Tooltip({
                position: "top",
                target: widget.getElement("#checkBoxLeftParent"),
                body: "Select All"
            });
            var checkBoxRightToolTip = new Tooltip({
                position: "top",
                target: widget.getElement("#checkBoxRightParent"),
                body: "Select All"
            });
            var selectAttributes = new Tooltip({
                position: "top",
                target: widget.getElement(".dual-list-buttons.move-right"),
                body: i18n.RP_SELECT_ATTRIBUTES
            });
            var deselectAttributes = new Tooltip({
                position: "top",
                target: widget.getElement(".dual-list-buttons.move-left"),
                body: i18n.RP_DESELECT_ATTRIBUTES
            });
        },
        toggleAttributeCheckboxState: function(element, state1, state2) {
            if (element.parent().parent().hasClass('text-left')) {
                jQuery('#checkBoxLeft').removeClass(state1).addClass(state2);
            } else {
                jQuery('#checkBoxRight').removeClass(state1).addClass(state2);
            }
        },
        message: function(message, className) {
            HimelliUX.alert = Himelli.alert || null;
            if (HimelliUX.alert != null) {
                HimelliUX.alert.destroy();
            }
            var notErrorMessage = true;
            if (className == "error") {
                notErrorMessage = false;
            }
            HimelliUX.alert = new Alert({
                closable: true,
                visible: true,
                autoHide: notErrorMessage,
                hideDelay: 3500,
                className: "wp-alert"
            }).inject(document.body, "top");
            HimelliUX.alert.add({
                className: className,
                message: message
            });
            Himelli.alert = HimelliUX.alert;
        },
        success: function(message) {
            HimelliUX.message(message, "success");
        },
        warn: function(message) {
            HimelliUX.message(message, "warning")
        },
        info: function(message) {
            HimelliUX.message(message, "primary")
        },
        error: function(message) {
            HimelliUX.message(message, "error")
        },
        setSearchButtonType: function() {
            widget.getElement(".ds-group-add .btn").setAttributes({
                type: 'button'
            });
        }
    };
    return HimelliUX;
});