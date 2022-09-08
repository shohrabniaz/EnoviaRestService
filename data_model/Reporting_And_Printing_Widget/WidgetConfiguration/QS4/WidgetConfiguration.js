define("VALCON/ReportingPrintingUX/WidgetConfiguration", [], function() {
    var widgetConfiguration = {
        "RP_ENOVIA_REST_SERVICE_URL": "https://vm3dxintegration.plm.valmet.com/EnoviaRestService",
        "RP_GET_MAIL_URL": "/resources/modeler/pno/person?current=true&select=email",
        "RP_MULTI_REPORT_GENERATE_SERVICE_URL": "/export/multiLevelBomDataReport?",
        "RP_SINGLE_REPORT_GENERATE_SERVICE_URL": "/export/singleLevelBomDataReport?",
        "RP_ALL_SELECTABLE_ATTRIBUTES_URL": "/export/allSelectableAttributes?type=",
        "RP_SPR_REPORT_GENERATE_SERVICE_URL": "/export/himelli/bom?",
        "SPR_ALL_SELECTABLE_ATTRIBUTES_URL": "/export/allSelectableAttributes?type=",
        "RP_ITEM_TYPE": {
            "mbom": "CreateAssembly,ProcessContinuousCreateMaterial",
            "ebom": "VPMReference",
            "spr": "CreateAssembly,ProcessContinuousCreateMaterial"
        },
        "RP_Report_Type_DROPDOWN_OPTS": [{
            label: "MBOM Report",
            value: "mbom",
        }, {
            label: "EBOM Report",
            value: "ebom"
        }, {
            label: "SPR Report",
            value: "spr"
        }],
        "RP_TEXT_REGEX": /^([0-9A-Za-z\-\.\_][ ]?)*$/,
        "RP_TEXT_MAX_LENGTH": "30",
        "RP_TEXT_MAX_LENGTH_PSK": "16",
        "RP_TEXT_MAX_LENGTH_PRODUCT": "22"
    };
    return widgetConfiguration;
});