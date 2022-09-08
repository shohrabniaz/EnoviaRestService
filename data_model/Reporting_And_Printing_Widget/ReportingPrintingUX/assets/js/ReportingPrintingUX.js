define("VALCON/ReportingPrintingUX/ReportingPrintingUX", ["DS/Foundation/WidgetUwaUtils", "DS/Foundation/WidgetAPIs", "DS/ENO6WPlugins/jQuery_3.3.1", "VALCON/ReportingPrintingUX/PreferenceUtil", "VALCON/ReportingPrintingUX/ObjectSearchView", "VALCON/ReportingPrintingUX/ObjectModel", "DS/UIKIT/Input/Select", "DS/UIKIT/Input/Text", "DS/UIKIT/Input/Toggle", "DS/UIKIT/Tooltip", "DS/UIKIT/Alert", "VALCON/ReportingPrintingUX/WidgetConfiguration", "VALCON/ReportingPrintingUX/PreferenceConfiguration", "DS/UIKIT/Mask", "css!DS/ReportingPrintingUX/assets/css/ReportingPrintingUX", "i18n!DS/ReportingPrintingUX/assets/nls/ReportingPrintingUX", "DS/WAFData/WAFData", "UWA/Class/Promise", "DS/i3DXCompassPlatformServices/i3DXCompassPlatformServices"], function(WidgetUwaUtils, WidgetAPIs, Jquery, PreferenceUtil, ObjectSearchView, ObjectModel, Select, Text, Toggle, Tooltip, Alert, WidgetConfiguration, PreferenceConfiguration, Mask, css, i18n, WAFData, UWAPromise, i3DXCompassPlatformServices) {
    'use strict';
    var ReportingPrintingWidget = {
        mailAddress: "",
        onLoad: function() {
            WidgetUwaUtils.setupEnoviaServer();
            window.isIFWE = true;
            window.enoviaServer.showSpace = "true";
            WidgetUwaUtils.onAfterLoad = Jquery.noop;
            window.enoviaServer.widgetId = widget.id;

            ReportingPrintingWidget.init();
        },
        init: function() {
            window.RPWidget = {
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
                        searchTypes: RPWidget.objectType,
                        validTypes: RPWidget.objectType,
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
                            RPWidget.baseURL = url;
                            ReportingPrintingWidget.getMail(RPWidget.baseURL + WidgetConfiguration.RP_GET_MAIL_URL).then(function(resolveData) {
                                ReportingPrintingWidget.mailAddress = resolveData.email;
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
            RPWidget.get3DSpaceBaseURL();
            ReportingPrintingWidget.getWidgetPreferences();
            ReportingPrintingWidget.getWidgetBase();
            var reportType = widget.getValue(PreferenceConfiguration.RP_PREF_NAME_REPORT_TYPE);
            //alert(reportType);
            if (reportType === "spr") {
                jQuery(".EBOM-MBOM-HIDE").css("display", "none");
                jQuery(".SPR-HIDE").css("display", "block");
            } else {
                jQuery(".EBOM-MBOM-HIDE").css("display", "block");
                jQuery(".SPR-HIDE").css("display", "none");
            }
        },
        getWidgetPreferences: function() {
            var reportType = widget.getValue(PreferenceConfiguration.RP_PREF_NAME_REPORT_TYPE) || PreferenceConfiguration.RP_PREF_REPORT_TYPE_PREFERRED_VALUE;
            var reportFormatPref = {
                type: "list",
                name: PreferenceConfiguration.RP_PREF_NAME_REPORT_FORMAT,
                label: PreferenceConfiguration.RP_PREF_LABEL_REPORT_FORMAT,
                options: PreferenceConfiguration.RP_PREF_REPORT_FORMAT_OPTS[reportType].opts,
                defaultValue: widget.getValue(PreferenceConfiguration.RP_PREF_NAME_REPORT_FORMAT) || PreferenceConfiguration.RP_PREF_REPORT_FORMAT_OPTS[reportType].defaultValue
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
            var reportFormatPrefMBOMEBOM = {
                type: "hidden",
                name: PreferenceConfiguration.RP_PREF_NAME_REPORT_FORMAT_MBOM_EBOM,
                label: PreferenceConfiguration.RP_PREF_LABEL_REPORT_FORMAT_MBOM_EBOM,
                defaultValue: widget.getValue(PreferenceConfiguration.RP_PREF_NAME_REPORT_FORMAT_MBOM_EBOM) || PreferenceConfiguration.RP_PREF_REPORT_FORMAT_PREFERRED_VALUE
            };
            widget.addPreference(reportFormatPrefMBOMEBOM);
            var bomView = {
                type: "hidden",
                name: PreferenceConfiguration.RP_PREF_NAME_BOM_VIEW,
                label: PreferenceConfiguration.RP_PREF_LABEL_BOM_VIEW,
                defaultValue: widget.getValue(PreferenceConfiguration.RP_PREF_NAME_BOM_VIEW) || PreferenceConfiguration.RP_PREF_BOM_VIEW_PREFERRED_VALUE
            };
            widget.addPreference(bomView);
            var collabStorage = widget.getValue(RPWidget.collabstorage);
            WidgetAPIs.getCollaborativeStorages(function(k) {
                WidgetUwaUtils.processStorages(k, collabStorage);
                WidgetUwaUtils.setStoragesPrefs(widget, k, collabStorage, "true");
                WidgetUwaUtils.processStorageChange.call(ReportingPrintingWidget, collabStorage);
            });
        },
        submitReportParameters: function() {
            if (!widget.body.getElements(".invalid-input").length) {
                var validBOMLevel = widget.getValue(PreferenceConfiguration.RP_PREF_NAME_BOM_LEVEL).length;
                var reportType = widget.getValue(PreferenceConfiguration.RP_PREF_NAME_REPORT_TYPE);
                if (PreferenceUtil.get(RPWidget.savedType) && PreferenceUtil.get(RPWidget.savedID)) {
                    if (validBOMLevel) {
                        if (widget.getValue(PreferenceConfiguration.RP_PREF_NAME_PRIMARY_LANGUAGE) === widget.getValue(PreferenceConfiguration.RP_PREF_NAME_SECONDARY_LANGUAGE)) {
                            ReportingPrintingWidget.warn(i18n.RP_ALERT_PRIMARY_SECONDARY_LANGUAGE_SAME);
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
                            var mainProjTitle = jQuery('#mainProjectTitle').val();
                            var psk = jQuery('#psk').val();
                            var subTitle = jQuery('#subTitle').val();
                            var product = jQuery('#product').val();
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
                            if(printDrawing === true && docType === null) {
                                ReportingPrintingWidget.error(i18n.RP_ALERT_DOC_TYPE);
                            } else {
                                if (reportType === "spr") {
                                    var isLatest = jQuery('#latestBom').is(':checked');
                                    if (!(reportAttributes.includes("name") && reportAttributes.includes("Title"))) {
                                        ReportingPrintingWidget.error(i18n.SRP_MANDATORY_ATTRIBUTE_ERROR);
                                    } else {
                                        var drawingNumber = false;
                                        if ((reportAttributes.includes("Drawing Number"))) {
                                            drawingNumber = true;
                                        }
                                        ReportingPrintingWidget.generateHimelliReport(bomLevel, reportAttributes, seletedreportFormat, seletedreportLang, primaryLanguage, secondaryLanguage, docType, isLatest, drawingNumber);
                                    }
                                } else {
                                    ReportingPrintingWidget.generateReport(seletedreportName, seletedreportLang, seletedreportBasic, seletedreportFormat, bomLevel, printDrawing, printSummary, reportAttributes, printDelivery, mainProjTitle, psk, subTitle, product, primaryLanguage, secondaryLanguage, docType);
                                }
                            }
                        }
                    } else {
                        ReportingPrintingWidget.warn(i18n.RP_ALERT_BOM_LEVEL);
                    }
                } else {
                    if (!validBOMLevel) {
                        ReportingPrintingWidget.warn(i18n.RP_ALERT_ITEM_NAME_BOM_LEVEL);
                    } else {
                        ReportingPrintingWidget.warn(i18n.RP_ALERT_ITEM_NAME);
                    }
                }
            } else {
                ReportingPrintingWidget.warn(i18n.RP_ALERT_INVALID_INPUT);
            }
        },
        generateHimelliReport: function(bomLevel, reportAttributes, seletedreportFormat, seletedreportLang, primaryLanguage, secondaryLanguage, docType, isLatest,drawingNumber) {
            var itemType = PreferenceUtil.get(RPWidget.savedType);
            var itemName = PreferenceUtil.get(RPWidget.savedName);
            var itemRev = PreferenceUtil.get(RPWidget.savedRevision);
            var submitURL = "";
            var format = seletedreportFormat;

            if (seletedreportFormat != "txt") {
                ReportingPrintingWidget.warn(i18n.SPR_REPORT_FORMAT_ERROR);
            }

            var requester = "himelli";
            submitURL = WidgetConfiguration.RP_ENOVIA_REST_SERVICE_URL + WidgetConfiguration.RP_SPR_REPORT_GENERATE_SERVICE_URL;
            submitURL = submitURL + "type=" + itemType;
            submitURL = submitURL + "&name=" + itemName;
            // submitURL = submitURL + "&format=" + seletedreportFormat;
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
			submitURL = submitURL + "&drawingNumber=" + drawingNumber;
            submitURL = submitURL + "&receiverEmail=" + encodeURIComponent(ReportingPrintingWidget.mailAddress);

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
                                    RPWidget.fileReader.readAsText(this.response);
                                    RPWidget.fileReader.onload = function(e) {
                                        var serviceResponse = RPWidget.fileReader.result;
                                        try {
                                            serviceResponse = JSON.parse(serviceResponse);
                                            if (serviceResponse.status !== null || serviceResponse.status !== undefined) {
                                                if (serviceResponse.status === "OK") {
                                                    console.log(serviceResponse);
                                                    Mask.unmask(widget.getElement(".container"));
                                                    if (serviceResponse.data !== null || serviceResponse.data !== undefined) {
                                                        ReportingPrintingWidget.success(serviceResponse.data);
                                                    } else {
                                                        ReportingPrintingWidget.success(i18n.RP_REPORT_GENERATION_OK);
                                                    }
                                                } else if (serviceResponse.status === "FAILED") {
                                                    if (serviceResponse.systemErrors !== null || serviceResponse.systemErrors !== undefined) {
                                                        console.log(serviceResponse.systemErrors[0]);
                                                    } else {
                                                        console.log(serviceResponse);
                                                    }
                                                    Mask.unmask(widget.getElement(".container"));
                                                    ReportingPrintingWidget.error(i18n.RP_REPORT_GENERATION_FAILED);
                                                }
                                            } else {
                                                console.log(serviceResponse);
                                                Mask.unmask(widget.getElement(".container"));
                                                ReportingPrintingWidget.error(i18n.RP_REPORT_GENERATION_FAILED);
                                            }
                                        } catch (error) {
                                            console.log(serviceResponse);
                                            Mask.unmask(widget.getElement(".container"));
                                            ReportingPrintingWidget.error(i18n.RP_REPORT_GENERATION_FAILED);
                                        }
                                    };
                                } else {
                                    var type = request.getResponseHeader('Content-Type');
                                    var blob = new Blob([this.response], {
                                        type: type
                                    });
                                    window.navigator.msSaveBlob(blob, fileName);
                                    Mask.unmask(widget.getElement(".container"));
                                    ReportingPrintingWidget.success(i18n.RP_REPORT_GENERATION_OK);
                                }
                            } else if (this.status > 200) {
                                console.log("File Download Error " + request.status);
                                if (this.status === 406) {
                                    RPWidget.fileReader.readAsText(this.response);
                                    RPWidget.fileReader.onload = function(e) {
                                        console.log(RPWidget.fileReader.result);
                                    };
                                }
                                Mask.unmask(widget.getElement(".container"));
                                ReportingPrintingWidget.error(i18n.RP_REPORT_GENERATION_FAILED);
                            }
                        };
                        request.onerror = function() {
                            Mask.unmask(widget.getElement(".container"));
                            ReportingPrintingWidget.error(i18n.RP_REPORT_GENERATION_FAILED);
                        };
                        request.onabort = function() {
                            Mask.unmask(widget.getElement(".container"));
                            ReportingPrintingWidget.error(i18n.RP_REPORT_GENERATION_FAILED);
                        };
                        Mask.mask(widget.getElement(".container"), i18n.RP_REPORT_GENERATION_ON_GOING);
                        request.send();
                    } catch (error) {
                        Mask.unmask(widget.getElement(".container"));
                        ReportingPrintingWidget.error(i18n.RP_REPORT_GENERATION_FAILED);
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
                                    RPWidget.fileReader.readAsText(this.response);
                                    RPWidget.fileReader.onload = function(e) {
                                        var serviceResponse = RPWidget.fileReader.result;
                                        try {
                                            serviceResponse = JSON.parse(serviceResponse);
                                            if (serviceResponse.status !== null || serviceResponse.status !== undefined) {
                                                if (serviceResponse.status === "OK") {
                                                    console.log(serviceResponse);
                                                    Mask.unmask(widget.getElement(".container"));
                                                    if (serviceResponse.data !== null || serviceResponse.data !== undefined) {
                                                        ReportingPrintingWidget.success(serviceResponse.data);
                                                    } else {
                                                        ReportingPrintingWidget.success(i18n.RP_REPORT_GENERATION_OK);
                                                    }
                                                } else if (serviceResponse.status === "FAILED") {
                                                    if (serviceResponse.systemErrors !== null || serviceResponse.systemErrors !== undefined) {
                                                        console.log(serviceResponse.systemErrors[0]);
                                                    } else {
                                                        console.log(serviceResponse);
                                                    }
                                                    Mask.unmask(widget.getElement(".container"));
                                                    ReportingPrintingWidget.error(i18n.RP_REPORT_GENERATION_FAILED);
                                                }
                                            } else {
                                                console.log(serviceResponse);
                                                Mask.unmask(widget.getElement(".container"));
                                                ReportingPrintingWidget.error(i18n.RP_REPORT_GENERATION_FAILED);
                                            }
                                        } catch (error) {
                                            console.log(serviceResponse);
                                            Mask.unmask(widget.getElement(".container"));
                                            ReportingPrintingWidget.error(i18n.RP_REPORT_GENERATION_FAILED);
                                        }
                                    };
                                } else {
                                    if (this.response.size === 0){
                                        Mask.unmask(widget.getElement(".container"));
                                        ReportingPrintingWidget.info(i18n.RP_REPORT_GENERATION_BACKGROUND_MESSAGE);
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
                                        ReportingPrintingWidget.success(i18n.RP_REPORT_GENERATION_OK);
                                        setTimeout(function() {
                                            URL.revokeObjectURL(downloadUrl);
                                        }, 100);
                                        a.remove();
                                    }
                                }
                            } else if (request.status > 200) {
                                console.log("File Download Error " + request.status);
                                if (this.status === 406) {
                                    RPWidget.fileReader.readAsText(this.response);
                                    RPWidget.fileReader.onload = function(e) {
                                        console.log(RPWidget.fileReader.result);
                                    };
                                }
                                Mask.unmask(widget.getElement(".container"));
                                ReportingPrintingWidget.error(i18n.RP_REPORT_GENERATION_FAILED);
                            }
                        };
                        request.onerror = function() {
                            Mask.unmask(widget.getElement(".container"));
                            ReportingPrintingWidget.error(i18n.RP_REPORT_GENERATION_FAILED);
                        };
                        request.onabort = function() {
                            Mask.unmask(widget.getElement(".container"));
                            ReportingPrintingWidget.error(i18n.RP_REPORT_GENERATION_FAILED);
                        };
                        Mask.mask(widget.getElement(".container"), i18n.RP_REPORT_GENERATION_ON_GOING);
                        request.send();
                    } catch (error) {
                        Mask.unmask(widget.getElement(".container"));
                        ReportingPrintingWidget.error(i18n.RP_REPORT_GENERATION_FAILED);
                        console.log("Report Generation Service call error: " + error);
                    }
                }
            } else {
                ReportingPrintingWidget.warn(i18n.RP_ALERT_ATTRIBUTE_LIST);
            }
        },
        generateReport: function(name, lang, basic, format, level, printDrawing, printSummary, reportAttributes, printDelivery, mainProjTitle, psk, subTitle, product, primaryLanguage, secondaryLanguage, docType) {
            var itemType = PreferenceUtil.get(RPWidget.savedType);
            var itemName = PreferenceUtil.get(RPWidget.savedName);
            var itemObjectID = PreferenceUtil.get(RPWidget.savedID);
            var submitURL = "";
            if (format === "txt") {
                ReportingPrintingWidget.warn(i18n.RP_REPORT_FORMAT_ERROR);
            }
            if (RPWidget.controls.reportSingleBomType !== undefined) {
                if (RPWidget.controls.reportSingleBomType.isChecked()) {
                    submitURL = WidgetConfiguration.RP_ENOVIA_REST_SERVICE_URL + WidgetConfiguration.RP_SINGLE_REPORT_GENERATE_SERVICE_URL;
                } else {
                    submitURL = WidgetConfiguration.RP_ENOVIA_REST_SERVICE_URL + WidgetConfiguration.RP_MULTI_REPORT_GENERATE_SERVICE_URL;
                }
            } else if (reportType === "spr") {
                submitURL = WidgetConfiguration.RP_ENOVIA_REST_SERVICE_URL + WidgetConfiguration.RP_SPR_REPORT_GENERATE_SERVICE_URL;
            } else {
                submitURL = WidgetConfiguration.RP_ENOVIA_REST_SERVICE_URL + WidgetConfiguration.RP_MULTI_REPORT_GENERATE_SERVICE_URL;
            }
            submitURL = submitURL + "type=" + itemType;
            submitURL = submitURL + "&objectId=" + itemObjectID;
            submitURL = submitURL + "&format=" + format;
            submitURL = submitURL + "&lang=" + lang;
            submitURL = submitURL + "&printDrawing=" + printDrawing;
            submitURL = submitURL + "&printSummary=" + printSummary;
            submitURL = submitURL + "&printDelivery=" + printDelivery;
            submitURL = submitURL + "&mainProjTitle=" + mainProjTitle;
            submitURL = submitURL + "&psk=" + psk;
            submitURL = submitURL + "&subTitle=" + subTitle;
            submitURL = submitURL + "&product=" + product;
            submitURL = submitURL + "&primaryLang=" + primaryLanguage;
            submitURL = submitURL + "&secondaryLang=" + secondaryLanguage;
            submitURL = submitURL + "&expandLevel=" + level;
            submitURL = submitURL + "&download=" + true;
            submitURL = submitURL + "&receiverEmail=" + encodeURIComponent(ReportingPrintingWidget.mailAddress);
            submitURL = submitURL + "&docType=" + docType;


      
            if (reportAttributes.length > 0) {
                var timestampNow = new Date().getTime();
                submitURL = submitURL + "&attrs=" + reportAttributes;
                submitURL = submitURL + "&v=" + timestampNow;
                var fileName = itemType + "_" + itemName + "_" + timestampNow + "." + format;
                submitURL = submitURL + "&rptFileName=" + encodeURIComponent(fileName);
                if (window.navigator.msSaveBlob) {
                    try {
                        var request = new XMLHttpRequest();
                        request.open("GET", submitURL, true);
                        request.responseType = "blob";
                        request.onload = function() {
                            if (request.readyState == 4 && request.status === 200) {
                                if (this.response.type === "text/plain" || this.response.type === "application/json") {
                                    RPWidget.fileReader.readAsText(this.response);
                                    RPWidget.fileReader.onload = function(e) {
                                        var serviceResponse = RPWidget.fileReader.result;
                                        try {
                                            serviceResponse = JSON.parse(serviceResponse);
                                            if (serviceResponse.status !== null || serviceResponse.status !== undefined) {
                                                if (serviceResponse.status === "OK") {
                                                    console.log(serviceResponse);
                                                    Mask.unmask(widget.getElement(".container"));
                                                    if (serviceResponse.data !== null || serviceResponse.data !== undefined) {
                                                        ReportingPrintingWidget.success(serviceResponse.data);
                                                    } else {
                                                        ReportingPrintingWidget.success(i18n.RP_REPORT_GENERATION_OK);
                                                    }
                                                } else if (serviceResponse.status === "FAILED") {
                                                    if (serviceResponse.systemErrors !== null || serviceResponse.systemErrors !== undefined) {
                                                        console.log(serviceResponse.systemErrors[0]);
                                                    } else {
                                                        console.log(serviceResponse);
                                                    }
                                                    Mask.unmask(widget.getElement(".container"));
                                                    ReportingPrintingWidget.error(i18n.RP_REPORT_GENERATION_FAILED);
                                                }
                                            } else {
                                                console.log(serviceResponse);
                                                Mask.unmask(widget.getElement(".container"));
                                                ReportingPrintingWidget.error(i18n.RP_REPORT_GENERATION_FAILED);
                                            }
                                        } catch (error) {
                                            console.log(serviceResponse);
                                            Mask.unmask(widget.getElement(".container"));
                                            ReportingPrintingWidget.error(i18n.RP_REPORT_GENERATION_FAILED);
                                        }
                                    };
                                } else {
                                    var type = request.getResponseHeader('Content-Type');
                                    var blob = new Blob([this.response], {
                                        type: type
                                    });
                                    window.navigator.msSaveBlob(blob, fileName);
                                    Mask.unmask(widget.getElement(".container"));
                                    ReportingPrintingWidget.success(i18n.RP_REPORT_GENERATION_OK);
                                }
                            } else if (this.status > 200) {
                                console.log("File Download Error " + request.status);
                                if (this.status === 406) {
                                    RPWidget.fileReader.readAsText(this.response);
                                    RPWidget.fileReader.onload = function(e) {
                                        console.log(RPWidget.fileReader.result);
                                    };
                                }
                                Mask.unmask(widget.getElement(".container"));
                                ReportingPrintingWidget.error(i18n.RP_REPORT_GENERATION_FAILED);
                            }
                        };
                        request.onerror = function() {
                            Mask.unmask(widget.getElement(".container"));
                            ReportingPrintingWidget.error(i18n.RP_REPORT_GENERATION_FAILED);
                        };
                        request.onabort = function() {
                            Mask.unmask(widget.getElement(".container"));
                            ReportingPrintingWidget.error(i18n.RP_REPORT_GENERATION_FAILED);
                        };
                        Mask.mask(widget.getElement(".container"), i18n.RP_REPORT_GENERATION_ON_GOING);
                        request.send();
                    } catch (error) {
                        Mask.unmask(widget.getElement(".container"));
                        ReportingPrintingWidget.error(i18n.RP_REPORT_GENERATION_FAILED);
                        console.log("Report Generation Service call error: " + error);
                    }
                } else {
                    try {
                        var request = new XMLHttpRequest();
                        request.open("GET", submitURL, true);
                        request.responseType = "blob";
                        request.onload = function() {
                            if (request.readyState == 4 && request.status === 200) {
                                if (this.response.type === "text/plain" || this.response.type === "application/json") {
                                    RPWidget.fileReader.readAsText(this.response);
                                    RPWidget.fileReader.onload = function(e) {
                                        var serviceResponse = RPWidget.fileReader.result;
                                        try {
                                            serviceResponse = JSON.parse(serviceResponse);
                                            if (serviceResponse.status !== null || serviceResponse.status !== undefined) {
                                                if (serviceResponse.status === "OK") {
                                                    console.log(serviceResponse);
                                                    Mask.unmask(widget.getElement(".container"));
                                                    if (serviceResponse.data !== null || serviceResponse.data !== undefined) {
                                                        ReportingPrintingWidget.success(serviceResponse.data);
                                                    } else {
                                                        ReportingPrintingWidget.success(i18n.RP_REPORT_GENERATION_OK);
                                                    }
                                                } else if (serviceResponse.status === "FAILED") {
                                                    if (serviceResponse.systemErrors !== null || serviceResponse.systemErrors !== undefined) {
                                                        console.log(serviceResponse.systemErrors[0]);
                                                    } else {
                                                        console.log(serviceResponse);
                                                    }
                                                    Mask.unmask(widget.getElement(".container"));
                                                    ReportingPrintingWidget.error(i18n.RP_REPORT_GENERATION_FAILED);
                                                }
                                            } else {
                                                console.log(serviceResponse);
                                                Mask.unmask(widget.getElement(".container"));
                                                ReportingPrintingWidget.error(i18n.RP_REPORT_GENERATION_FAILED);
                                            }
                                        } catch (error) {
                                            console.log(serviceResponse);
                                            Mask.unmask(widget.getElement(".container"));
                                            ReportingPrintingWidget.error(i18n.RP_REPORT_GENERATION_FAILED);
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
                                    ReportingPrintingWidget.success(i18n.RP_REPORT_GENERATION_OK);
                                    setTimeout(function() {
                                        URL.revokeObjectURL(downloadUrl);
                                    }, 100);
                                    a.remove();
                                }
                            } else if (request.status > 200) {
                                console.log("File Download Error " + request.status);
                                if (this.status === 406) {
                                    RPWidget.fileReader.readAsText(this.response);
                                    RPWidget.fileReader.onload = function(e) {
                                        console.log(RPWidget.fileReader.result);
                                    };
                                }
                                Mask.unmask(widget.getElement(".container"));
                                ReportingPrintingWidget.error(i18n.RP_REPORT_GENERATION_FAILED);
                            }
                        };
                        request.onerror = function() {
                            Mask.unmask(widget.getElement(".container"));
                            ReportingPrintingWidget.error(i18n.RP_REPORT_GENERATION_FAILED);
                        };
                        request.onabort = function() {
                            Mask.unmask(widget.getElement(".container"));
                            ReportingPrintingWidget.error(i18n.RP_REPORT_GENERATION_FAILED);
                        };
                        Mask.mask(widget.getElement(".container"), i18n.RP_REPORT_GENERATION_ON_GOING);
                        request.send();
                    } catch (error) {
                        Mask.unmask(widget.getElement(".container"));
                        ReportingPrintingWidget.error(i18n.RP_REPORT_GENERATION_FAILED);
                        console.log("Report Generation Service call error: " + error);
                    }
                }
            } else {
                ReportingPrintingWidget.warn(i18n.RP_ALERT_ATTRIBUTE_LIST);
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
            RPWidget.controls.reportType.setValue(widget.getValue(PreferenceConfiguration.RP_PREF_NAME_REPORT_TYPE) || PreferenceConfiguration.RP_PREF_REPORT_TYPE_PREFERRED_VALUE);
            var reportFormat = widget.getValue(PreferenceConfiguration.RP_PREF_NAME_REPORT_FORMAT);
            if (PreferenceConfiguration.RP_PREF_REPORT_FORMAT_VALUE_CHECK.indexOf(reportFormat) !== -1) {
                widget.setValue(PreferenceConfiguration.RP_PREF_NAME_REPORT_FORMAT_MBOM_EBOM, reportFormat);
            }
            var objectType = "";
            if (widget.getValue(PreferenceConfiguration.RP_PREF_NAME_REPORT_TYPE)) {
                var reportType = widget.getValue(PreferenceConfiguration.RP_PREF_NAME_REPORT_TYPE);
                objectType = WidgetConfiguration.RP_ITEM_TYPE[reportType].split(",")[0];

                RPWidget.objectType = WidgetConfiguration.RP_ITEM_TYPE[reportType];
            } else {
                RPWidget.objectType = WidgetConfiguration.RP_ITEM_TYPE[PreferenceConfiguration.RP_PREF_REPORT_TYPE_PREFERRED_VALUE];
                objectType = WidgetConfiguration.RP_ITEM_TYPE[PreferenceConfiguration.RP_PREF_REPORT_TYPE_PREFERRED_VALUE].split(',')[0];
            }
            widget.setValue(PreferenceConfiguration.RP_PREF_NAME_SELECTED_ATTRIBUTE, "");
            ReportingPrintingWidget.populateReportableAttributes(objectType);
            widget.body.getElement(".autocomplete-input").value = "";
            RPWidget.controls.revision.setValue("");
            RPWidget.controls.mainProjTitle.setValue([""]);
            RPWidget.controls.psk.setValue([""]);
            RPWidget.controls.subTitle.setValue([""]);
            RPWidget.controls.product.setValue([""]);
            RPWidget.controls.docType.setValue([""]);
            RPWidget.controls.mainProjTitle.enable;
            RPWidget.controls.psk.enable;
            RPWidget.controls.subTitle.enable;
            RPWidget.controls.product.enable;
            ReportingPrintingWidget.resetSelectedItemValue();
        },
        onAfterLoad: function() {
            ReportingPrintingWidget.resetSelectedItemValue();
            ReportingPrintingWidget.configureSearchView();
            var reportType = widget.getValue(PreferenceConfiguration.RP_PREF_NAME_REPORT_TYPE);
            ReportingPrintingWidget.populateReportableAttributes(WidgetConfiguration.RP_ITEM_TYPE[reportType].split(",")[0]);
            ReportingPrintingWidget.setSearchButtonType();
        },
        configureSearchView: function() {
            RPWidget.view = new ObjectSearchView();

            var searchContainer = widget.body.getElement("#itemNameContainer");
            RPWidget.view.render().inject(searchContainer);

            var reportType = widget.getElement("#reportType");
            reportType.addEvent("change", ReportingPrintingWidget.handleReportTypeChange);
        },
        populateReportableAttributes: function(itemType) {
            var selectedAttributePreferred = [];
            var selectedAttributeValue = widget.getValue(PreferenceConfiguration.RP_PREF_NAME_SELECTED_ATTRIBUTE);
            if (selectedAttributeValue.length) {
                selectedAttributePreferred = widget.getValue(PreferenceConfiguration.RP_PREF_NAME_SELECTED_ATTRIBUTE).split("\|");
            }
            var reportType = widget.getValue(PreferenceConfiguration.RP_PREF_NAME_REPORT_TYPE);
            var printSummaryReportPref;
            if (reportType === "spr") {
                printSummaryReportPref = {
                    type: "hidden",
                    name: PreferenceConfiguration.RP_PREF_NAME_PRINT_SUMMARY,
                    label: PreferenceConfiguration.RP_PREF_LABEL_PRINT_SUMMARY,
                    defaultValue: false
                };
                var requestUrl = WidgetConfiguration.RP_ENOVIA_REST_SERVICE_URL + WidgetConfiguration.SPR_ALL_SELECTABLE_ATTRIBUTES_URL + itemType + "&requester=himelli";
                console.log(requestUrl);
            } else {
                printSummaryReportPref = {
                    type: "boolean",
                    name: PreferenceConfiguration.RP_PREF_NAME_PRINT_SUMMARY,
                    label: PreferenceConfiguration.RP_PREF_LABEL_PRINT_SUMMARY,
                    defaultValue: widget.getValue(PreferenceConfiguration.RP_PREF_NAME_PRINT_SUMMARY) || PreferenceConfiguration.RP_PREF_PRINT_SUMMARY_PREFERRED_VALUE
                };
                var requestUrl = WidgetConfiguration.RP_ENOVIA_REST_SERVICE_URL + WidgetConfiguration.RP_ALL_SELECTABLE_ATTRIBUTES_URL + itemType;
            }
            widget.addPreference(printSummaryReportPref);
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
                            if (key === 'Name' || key === 'Title' || key === 'Revision' || key === 'Position') {
                                liItem.className = 'list-group-item disabled';
                            } else {
                                liItem.className = 'list-group-item active';
                            }
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
                    ReportingPrintingWidget.addSelectedAttributeinPreference();
                }
            };
            httpRequest.open("GET", requestUrl);
            httpRequest.send();
        },
        handleReportTypeChange: function(event) {
            var selectedReportName = event.target.options[event.target.selectedIndex].value;
            widget.setValue("ReportType", selectedReportName);
            jQuery('#docTypeContainer').html('');
            if (selectedReportName === "mbom") {
                ReportingPrintingWidget.injectDocTypeSelectComponent(PreferenceConfiguration.MBOM_REPORT_DRAWING_TYPES);
                
                jQuery(".EBOM-MBOM-HIDE").css("display", "block");
                jQuery(".SPR-HIDE").css("display", "none");
            } else if (selectedReportName === "ebom") {
                ReportingPrintingWidget.injectDocTypeSelectComponent(PreferenceConfiguration.EBOM_REPORT_DRAWING_TYPES);
               
                jQuery(".EBOM-MBOM-HIDE").css("display", "block");
                jQuery(".SPR-HIDE").css("display", "none");
            } else if (selectedReportName === "spr") {
                
                ReportingPrintingWidget.injectDocTypeSelectComponent(PreferenceConfiguration.MBOM_REPORT_DRAWING_TYPES);
                RPWidget.controls.docType.setValue(PreferenceConfiguration.MBOM_REPORT_DRAWING_TYPES[0]);
                jQuery(".EBOM-MBOM-HIDE").css("display", "none");
                jQuery(".SPR-HIDE").css("display", "block");
            }
            RPWidget.objectType = WidgetConfiguration.RP_ITEM_TYPE[selectedReportName];
            RPWidget.view._searchBar.setConfig(RPWidget.populateSearchConfig());
            RPWidget.view.render();
            ReportingPrintingWidget.setSearchButtonType();
            RPWidget.view.setPlaceHolderText(i18n["RP_TEXT_" + selectedReportName.toUpperCase() + "_ITEM_SEARCH"]);
            widget.setValue(PreferenceConfiguration.RP_PREF_NAME_SELECTED_ATTRIBUTE, "");
            ReportingPrintingWidget.populateReportableAttributes(WidgetConfiguration.RP_ITEM_TYPE[selectedReportName].split(',')[0]);
            ReportingPrintingWidget.resetSelectedItemValue();
            ReportingPrintingWidget.addSelectedAttributeinPreference();
            RPWidget.controls.revision.setValue("");
            ReportingPrintingWidget.updatePreferenceBasedOnReportType(selectedReportName);
        },
        updatePreferenceBasedOnReportType: function(reportType) {
            var allPreferences = widget.getPreferences();
            allPreferences[0].options = PreferenceConfiguration.RP_PREF_REPORT_FORMAT_OPTS[reportType].opts;
            allPreferences[0].defaultValue = PreferenceConfiguration.RP_PREF_REPORT_FORMAT_OPTS[reportType].defaultValue;
            allPreferences[0].value = PreferenceConfiguration.RP_PREF_REPORT_FORMAT_OPTS[reportType].value;
            widget.setPreferences(allPreferences);

            if (PreferenceConfiguration.RP_PREF_REPORT_FORMAT_CHECK.indexOf(reportType) !== -1) {
                var reportFormatPrevious = widget.getValue(PreferenceConfiguration.RP_PREF_NAME_REPORT_FORMAT_MBOM_EBOM);
                var reportFormat = widget.getValue(PreferenceConfiguration.RP_PREF_NAME_REPORT_FORMAT);
                if (PreferenceConfiguration.RP_PREF_REPORT_FORMAT_VALUE_CHECK.indexOf(reportFormat) !== -1) {
                    widget.setValue(PreferenceConfiguration.RP_PREF_NAME_REPORT_FORMAT_MBOM_EBOM, reportFormat);
                }
                widget.setValue(PreferenceConfiguration.RP_PREF_NAME_REPORT_FORMAT, reportFormatPrevious);
            } else {
                widget.setValue(PreferenceConfiguration.RP_PREF_NAME_REPORT_FORMAT, PreferenceConfiguration.RP_PREF_REPORT_FORMAT_OPTS[reportType].value);
            }
        },
        resetSelectedItemValue: function() {
            PreferenceUtil.set(RPWidget.savedID, "");
            PreferenceUtil.set(RPWidget.savedType, "");
            PreferenceUtil.set(RPWidget.savedName, "");
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
                                class: "col-md-2 EBOM-MBOM-HIDE",
                                id: "reportMultiBomTypeContainer",
                            }, {
                                tag: "div",
                                class: "col-md-2 EBOM-MBOM-HIDE",
                                id: "reportSingleBomTypeContainer",
                            }, {
                                tag: "div",
                                class: "col-md-2 SPR-HIDE",
                                id: "reportIsLatestBomContainer",
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
                            tag: "div",
                            class: "form-group EBOM-MBOM-HIDE",
                            html: [{
                                tag: "label",
                                class: "col-md-2 control-label EBOM-MBOM-HIDE",
                                for: "selectbasic",
                                text: i18n.RP_LABEL_CONFIDENTIALITY
                            }, {
                                tag: "div",
                                class: "col-md-10 EBOM-MBOM-HIDE",
                                id: "confidentialityContainer",
                            }]
                        }, {
                            tag: "div",
                            class: "form-group EBOM-MBOM-HIDE",
                            html: [{
                                tag: "label",
                                class: "col-md-2 control-label EBOM-MBOM-HIDE",
                                for: "mainProjectTitle",
                                text: i18n.RP_LABEL_MAIN_PROJ_TITLE
                            }, {
                                tag: "div",
                                class: "col-md-4 EBOM-MBOM-HIDE",
                                id: "mainProjectTitleContainer",
                            }, {
                                tag: "label",
                                class: "col-md-2 control-label EBOM-MBOM-HIDE",
                                for: "projSearchKey",
                                text: i18n.RP_LABEL_PSK
                            }, {
                                tag: "div",
                                class: "col-md-4 EBOM-MBOM-HIDE",
                                id: "projSearchKeyContainer",
                            }]
                        }, {
                            tag: "div",
                            class: "form-group EBOM-MBOM-HIDE",
                            html: [{
                                tag: "label",
                                class: "col-md-2 control-label EBOM-MBOM-HIDE",
                                for: "subTitle",
                                text: i18n.RP_LABEL_SUB_TITLE
                            }, {
                                tag: "div",
                                class: "col-md-4 EBOM-MBOM-HIDE",
                                id: "subTitleContainer",
                            }, {
                                tag: "label",
                                class: "col-md-2 control-label EBOM-MBOM-HIDE",
                                for: "product",
                                text: i18n.RP_LABEL_PRODUCT
                            }, {
                                tag: "div",
                                class: "col-md-4 EBOM-MBOM-HIDE",
                                id: "productContainer",
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
            ReportingPrintingWidget.populateUIKITControls();
            ReportingPrintingWidget.handleDualListEvent();
            ReportingPrintingWidget.populateToolTips();
            ReportingPrintingWidget.submitButtonClickHandler();
        },
        submitButtonClickHandler: function() {
            var submitButton = widget.getElement("#submit");
            submitButton.addEvent("click", ReportingPrintingWidget.submitReportParameters);
        },
        handleDualListEvent: function() {
            jQuery('body').on('click', '.list-group .list-group-item', function() {
                jQuery(this).toggleClass('active');
                if (!jQuery(this).hasClass('active')) {
                    ReportingPrintingWidget.toggleAttributeCheckboxState(jQuery(this), 'fonticon-select-all', 'fonticon-select-none');
                    return;
                }
                var uncheckCheckBox = true;
                jQuery(this).siblings().each(function() {
                    if (!jQuery(this).hasClass('active')) {
                        ReportingPrintingWidget.toggleAttributeCheckboxState(jQuery(this), 'fonticon-select-all', 'fonticon-select-none');
                        uncheckCheckBox = false;
                        return;
                    }
                });
                if (uncheckCheckBox) {
                    ReportingPrintingWidget.toggleAttributeCheckboxState(jQuery(this), 'fonticon-select-none', 'fonticon-select-all');
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
                ReportingPrintingWidget.addSelectedAttributeinPreference();
            });
            jQuery('.dual-list .selector').click(function() {
                var jQuerycheckBox = jQuery(this);
                if (!jQuerycheckBox.hasClass('selected')) {
                    jQuerycheckBox.addClass('selected').closest('.well').find('ul li:not(.active)').addClass('active');
                    jQuerycheckBox.children('i').removeClass('fonticon-select-none').addClass('fonticon-select-all');
                    jQuerycheckBox.addClass('selected').closest('.well').find('ul li.disabled').removeClass('active');
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
            RPWidget.controls.docType = new Select({
                name: "docType",
                id: "docType",
                placeholder: false,
                multiple: true,
                options: options,
                events: []
            }).inject(widget.getElement("#docTypeContainer"));
        },
        populateUIKITControls: function() {
            var reportType = RPWidget.controls.reportType = new Select({
                name: "reportType",
                id: "reportType",
                placeholder: false,
                options: WidgetConfiguration.RP_Report_Type_DROPDOWN_OPTS,
                events: []
            }).inject(widget.getElement("#reportNameContainer"));
            reportType.setValue(widget.getValue(PreferenceConfiguration.RP_PREF_NAME_REPORT_TYPE) || PreferenceConfiguration.RP_PREF_REPORT_TYPE_PREFERRED_VALUE);
            var BOMViewType = widget.getValue(PreferenceConfiguration.RP_PREF_NAME_BOM_VIEW) || PreferenceConfiguration.RP_PREF_LABEL_BOM_VIEW;
            var reportMultiBomType = RPWidget.controls.reportMultiBomType = new Toggle({
                name: "reportBomType",
                id: "reportMultiBomType",
                type: "radio",
                label: "Multi Level",
                value: "multi",
                checked: BOMViewType === "multi" ? true : false,
                events: {
                    onChange: function() {
                        widget.setValue(PreferenceConfiguration.RP_PREF_NAME_BOM_VIEW, "multi");
                    }
                }
            }).inject(widget.getElement("#reportMultiBomTypeContainer"));
            var reportSingleBomType = RPWidget.controls.reportSingleBomType = new Toggle({
                name: "reportBomType",
                id: "reportSingleBomType",
                type: "radio",
                label: "Single Level",
                value: "single",
                checked: BOMViewType === "single" ? true : false,
                events: {
                    onChange: function() {
                        widget.setValue(PreferenceConfiguration.RP_PREF_NAME_BOM_VIEW, "single");
                    }
                }
            }).inject(widget.getElement("#reportSingleBomTypeContainer"));

            var latestBom = RPWidget.controls.latestBom = new Toggle({
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
            }).inject(widget.getElement("#reportIsLatestBomContainer"));
            ReportingPrintingWidget.injectDocTypeSelectComponent(PreferenceConfiguration.MBOM_REPORT_DRAWING_TYPES);
            var confidentiality = RPWidget.controls.confidentiality = new Select({
                name: "selectbasic",
                id: "selectbasic",
                placeholder: false,
                disabled: true,
                options: [{
                    label: "CONFIDENTIAL",
                    value: "confidential"
                }, {
                    label: "INTERNAL",
                    value: "internal"
                }, {
                    label: "PUBLIC",
                    value: "public",
                    selected: true
                }]
            }).inject(widget.getElement("#confidentialityContainer"));
            var mainProjTitle = RPWidget.controls.mainProjTitle = new Text({
                name: "mainProjectTitle",
                id: "mainProjectTitle",
                type: "text",
                class: "form-control",
                maxlength: WidgetConfiguration.RP_TEXT_MAX_LENGTH,
                placeholder: i18n.RP_TEXT_MAIN_PROJ_TITLE
            }).inject(widget.getElement("#mainProjectTitleContainer"));
            var revision = RPWidget.controls.revision = new Text({
                name: "revision",
                id: "revision",
                type: "text",
                class: "form-control",
                disabled: true,
            }).inject(widget.getElement("#revisionContainer"));
            var psk = RPWidget.controls.psk = new Text({
                name: "psk",
                id: "psk",
                type: "text",
                class: "form-control",
                maxlength: WidgetConfiguration.RP_TEXT_MAX_LENGTH_PSK,
                placeholder: i18n.RP_TEXT_PSK
            }).inject(widget.getElement("#projSearchKeyContainer"));
            var subTitle = RPWidget.controls.subTitle = new Text({
                name: "subTitle",
                id: "subTitle",
                type: "text",
                class: "form-control",
                maxlength: WidgetConfiguration.RP_TEXT_MAX_LENGTH,
                placeholder: i18n.RP_TEXT_SUB_TITLE
            }).inject(widget.getElement("#subTitleContainer"));
            var product = RPWidget.controls.product = new Text({
                name: "product",
                id: "product",
                type: "text",
                class: "form-control",
                maxlength: WidgetConfiguration.RP_TEXT_MAX_LENGTH_PRODUCT,
                placeholder: i18n.RP_TEXT_PRODUCT
            }).inject(widget.getElement("#productContainer"));

            ReportingPrintingWidget.populateUIKITControlHelpers();
            ReportingPrintingWidget.populateUIKITControlValidation();
        },
        populateUIKITControlHelpers: function() {
            var mainProjTitleHelper = RPWidget.helpers.mainProjTitleHelper = UWA.createElement("label", {
                id: "mainProjTitleHelper",
                class: "helper-error form-control-helper-text"
            }).inject(widget.getElement("#mainProjectTitleContainer"), "bottom");
            var pskHelper = RPWidget.helpers.pskHelper = UWA.createElement("label", {
                id: "pskHelper",
                class: "helper-error form-control-helper-text"
            }).inject(widget.getElement("#projSearchKeyContainer"), "bottom");
            var subTitleHelper = RPWidget.helpers.subTitleHelper = UWA.createElement("label", {
                id: "subTitleHelper",
                class: "helper-error form-control-helper-text"
            }).inject(widget.getElement("#subTitleContainer"), "bottom");
            var productHelper = RPWidget.helpers.productHelper = UWA.createElement("label", {
                id: "productHelper",
                class: "helper-error form-control-helper-text"
            }).inject(widget.getElement("#productContainer"), "bottom");
        },
        populateUIKITControlValidation: function() {
            RPWidget.controls.revision.addEvent("onChange", ReportingPrintingWidget.addControlValidation);
            RPWidget.controls.mainProjTitle.addEvent("onChange", ReportingPrintingWidget.addControlValidation);
            RPWidget.controls.psk.addEvent("onChange", ReportingPrintingWidget.addControlValidation);
            RPWidget.controls.subTitle.addEvent("onChange", ReportingPrintingWidget.addControlValidation);
            RPWidget.controls.product.addEvent("onChange", ReportingPrintingWidget.addControlValidation);
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
            ReportingPrintingWidget.alert = RPWidget.alert || null;
            if (ReportingPrintingWidget.alert != null) {
                ReportingPrintingWidget.alert.destroy();
            }
            var notErrorMessage = true;
            if (className == "error") {
                notErrorMessage = false;
            }
            ReportingPrintingWidget.alert = new Alert({
                closable: true,
                visible: true,
                autoHide: notErrorMessage,
                hideDelay: 3500,
                className: "wp-alert"
            }).inject(document.body, "top");
            ReportingPrintingWidget.alert.add({
                className: className,
                message: message
            });
            RPWidget.alert = ReportingPrintingWidget.alert;
        },
        success: function(message) {
            ReportingPrintingWidget.message(message, "success");
        },
        warn: function(message) {
            ReportingPrintingWidget.message(message, "warning")
        },
        info: function(message) {
            ReportingPrintingWidget.message(message, "primary")
        },
        error: function(message) {
            ReportingPrintingWidget.message(message, "error")
        },
        setSearchButtonType: function() {
            widget.getElement(".ds-group-add .btn").setAttributes({
                type: 'button'
            });
        }
    };
    return ReportingPrintingWidget;
});