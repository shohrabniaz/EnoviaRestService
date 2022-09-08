define("VALCON/HimelliUX/PreferenceConfiguration", [], function() {
    var preferenceConfiguration = {
        "RP_PREF_NAME_REPORT_FORMAT": "ReportFormat",
        "RP_PREF_NAME_TEMPLATE_LANGUAGE": "TemplateLanguage",
        "RP_PREF_NAME_BOM_LEVEL": "BOMLevel",
        "RP_PREF_NAME_PRINT_DRAWING": "printDrawingInfo",
        "RP_PREF_NAME_PRINT_SUMMARY": "printSummaryReport",
        "RP_PREF_NAME_PRINT_DELIVERY": "printDeliveryProjectInfo",
        "RP_PREF_NAME_REPORT_TYPE": "ReportType",
        "RP_PREF_NAME_PRIMARY_LANGUAGE": "PrimaryLanguage",
        "RP_PREF_NAME_SECONDARY_LANGUAGE": "SecondaryLanguage",
        "RP_PREF_NAME_SELECTED_ATTRIBUTE": "SelectedAttribute",
        "RP_PREF_NAME_BOM_VIEW": "BOMViewType",

        "RP_PREF_LABEL_REPORT_FORMAT": "Report Format",
        "RP_PREF_LABEL_TEMPLATE_LANGUAGE": "Template Language",
        "RP_PREF_LABEL_BOM_LEVEL": "BOM Level",
        "RP_PREF_LABEL_PRINT_DRAWING": "Print Drawing Info",
        "RP_PREF_LABEL_PRINT_SUMMARY": "Print Summary Report",
        "RP_PREF_LABEL_PRINT_DELIVERY": "Print Delivery Project Info",
        "RP_PREF_LABEL_REPORT_TYPE": "Report Type",
        "RP_PREF_LABEL_PRIMARY_LANGUAGE": "Primary Language",
        "RP_PREF_LABEL_SECONDARY_LANGUAGE": "Secondary Language",
        "RP_PREF_LABEL_SELECTED_ATTRIBUTE": "Selected Attribute",
        "RP_PREF_LABEL_BOM_VIEW": "BOM View",

        "RP_PREF_REPORT_FORMAT_PREFERRED_VALUE": "txt",
        "RP_PREF_TEMPLATE_LANGUAGE_PREFERRED_VALUE": "en",
        "RP_PREF_BOM_LEVEL_PREFERRED_VALUE": "99",
        "RP_PREF_PRINT_DRAWING_PREFERRED_VALUE": true,
        "RP_PREF_PRINT_SUMMARY_PREFERRED_VALUE": true,
        "RP_PREF_PRINT_DELIVERY_PREFERRED_VALUE": "true",
        "RP_PREF_REPORT_TYPE_PREFERRED_VALUE": "mbom",
        "RP_PREF_PRIMARY_LANGUAGE_PREFERRED_VALUE": "en",
        "RP_PREF_SECONDARY_LANGUAGE_PREFERRED_VALUE": "",
        "RP_PREF_SELECTED_ATTRIBUTE_PREFERRED_VALUE": "",
        "RP_PREF_BOM_VIEW_PREFERRED_VALUE": "multi",

        "RP_PREF_NAME_REPORT_FORMAT_OPTIONS": [ {
            value: "txt",
            label: "TXT"
        }],
        "RP_PREF_NAME_TEMPLATE_LANGUAGE_OPTIONS": [{
            value: "zh",
            label: "Chinese"
        }, {
            value: "en",
            label: "English"
        }, {
            value: "fn",
            label: "Finnish"
        }, {
            value: "fr",
            label: "French"
        }, {
            value: "de",
            label: "German"
        }, {
            value: "sv",
            label: "Swedish"
        }],
        "RP_PREF_NAME_PRIMARY_LANGUAGE_OPTIONS": [{
            value: "in",
            label: "Bahasa Indonesia"
        }, {
            value: "in",
            label: "Bahasa Indonesia"
        }, {
            value: "br",
            label: "Brazilian Portuguese"
        }, {
            value: "bg",
            label: "Bulgarian"
        }, {
            value: "zh",
            label: "Chinese"
        }, {
            value: "cs",
            label: "Czech"
        }, {
            value: "nl",
            label: "Dutch"
        }, {
            value: "en",
            label: "English"
        }, {
            value: "fi",
            label: "Finnish"
        }, {
            value: "fr",
            label: "French"
        }, {
            value: "de",
            label: "German"
        }, {
            value: "hu",
            label: "Hungarian"
        }, {
            value: "it",
            label: "Italian"
        }, {
            value: "ja",
            label: "Japanese"
        }, {
            value: "ko",
            label: "Korean"
        }, {
            value: "no",
            label: "Norwegian"
        }, {
            value: "sa",
            label: "SAvonkieli"
        }, {
            value: "es",
            label: "Spanish"
        }, {
            value: "pl",
            label: "Polish"
        }, {
            value: "pt",
            label: "Portuguese"
        }, {
            value: "sv",
            label: "Swedish"
        }, {
            value: "th",
            label: "Thailand"
        }, {
            value: "tr",
            label: "Turkish"
        }],
        "RP_PREF_NAME_SECONDARY_LANGUAGE_OPTIONS": [{
            value: "",
            label: "-- Select --"
        }, {
            value: "in",
            label: "Bahasa Indonesia"
        }, {
            value: "in",
            label: "Bahasa Indonesia"
        }, {
            value: "br",
            label: "Brazilian Portuguese"
        }, {
            value: "bg",
            label: "Bulgarian"
        }, {
            value: "zh",
            label: "Chinese"
        }, {
            value: "cs",
            label: "Czech"
        }, {
            value: "nl",
            label: "Dutch"
        }, {
            value: "en",
            label: "English"
        }, {
            value: "fi",
            label: "Finnish"
        }, {
            value: "fr",
            label: "French"
        }, {
            value: "de",
            label: "German"
        }, {
            value: "hu",
            label: "Hungarian"
        }, {
            value: "it",
            label: "Italian"
        }, {
            value: "ja",
            label: "Japanese"
        }, {
            value: "ko",
            label: "Korean"
        }, {
            value: "no",
            label: "Norwegian"
        }, {
            value: "sa",
            label: "SAvonkieli"
        }, {
            value: "es",
            label: "Spanish"
        }, {
            value: "pl",
            label: "Polish"
        }, {
            value: "pt",
            label: "Portuguese"
        }, {
            value: "sv",
            label: "Swedish"
        }, {
            value: "th",
            label: "Thailand"
        }, {
            value: "tr",
            label: "Turkish"
        }],
        "RP_PREF_NAME_BOM_LEVEL_MIN": "0",
        "RP_PREF_NAME_BOM_LEVEL_MAX": "99",
        "RP_PREF_NAME_BOM_LEVEL_STEP": 1,
        "MBOM_REPORT_DRAWING_TYPES" : [{
            label: "Production",
            value: "Production",
        }, {
            label: "Production And Customer",
            value: "ProductionAndCustomer",
        },{
            label: "Customer",
            value: "Customer",
        }],
        "EBOM_REPORT_DRAWING_TYPES" : [{
            label: "Production",
            value: "Production",
        }, {
            label: "Production And Customer",
            value: "ProductionAndCustomer",
        },{
            label: "Customer",
            value: "Customer",
        }, {
            label: "Engineering",
            value: "Engineering"
        }]

    };
    return preferenceConfiguration;
});