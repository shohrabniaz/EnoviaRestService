/**
 * This Module is used to get/set Preference Value of Widget
 */
define("VALMET/PLMPDMBOMCompareUX/PreferenceUtil", [], function() {
    var a = {
        get: function(b) {
            var c;
            if (window.isIFWE && widget) {
                c = widget.getValue(b)
            } else {
                if (typeof(Storage) !== "undefined") {
                    c = localStorage[b];
                    switch (c) {
                        case "true":
                            c = true;
                            break;
                        case "false":
                            c = false;
                            break;
                        case "null":
                            c = null;
                            break;
                        case "undefined":
                            c = void 0;
                            break;
                        default:
                    }
                }
            }
            return (c)
        },
        set: function(b, c) {
            if (window.isIFWE && widget) {
                widget.setValue(b, c)
            } else {
                if (typeof(Storage) !== "undefined") {
                    localStorage[b] = c
                }
            }
        },
        parse: function(b) {
            return (b ? JSON.parse(b) : b)
        }
    };
    return (a);
});