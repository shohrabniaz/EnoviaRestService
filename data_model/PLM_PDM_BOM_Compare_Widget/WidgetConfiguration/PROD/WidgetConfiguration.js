define("VALMET/PLMPDMBOMCompareUX/WidgetConfiguration", [], function() {
    var widgetConfiguration = {
        "BC_SEPARATE_INTEGRATION_SERVER_3DSPACE_URL": "https://dsprspace.plm.valmet.com:8180/3dspace",
        "BC_ENOVIA_REST_SERVICE_URL": "https://dsprspace.plm.valmet.com/EnoviaRestService",
        "BC_BOM_COMPARISON_SERVICE_URL": "/compareBOM/EnoviaPDMBOMComparison?",
        "BC_ALLOWED_TOP_ITEM_TYPES": "CreateAssembly"
    };
    return widgetConfiguration;
});