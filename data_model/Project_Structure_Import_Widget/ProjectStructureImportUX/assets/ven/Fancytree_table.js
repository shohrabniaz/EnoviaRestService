var lFancytreePath = require.toUrl('DS/ProjectStructureImportUX/assets/ven/Fancytree/plugins/v2.9.0/');
// Remove any query strings
var lIndexOfQuestionMark = lFancytreePath.indexOf('?');
if (lIndexOfQuestionMark > -1) { //remove ? and what follows from the url
    lFancytreePath = lFancytreePath.substring(0, lIndexOfQuestionMark);
}

require.config({
    paths: {
        'DS/ProjectStructureImportUX/Fancytree_table': lFancytreePath + 'jquery.fancytree.table'
    },
    shim: {
        'DS/ProjectStructureImportUX/Fancytree_table': {
            deps: ['DS/ProjectStructureImportUX/assets/ven/Fancytree'],
            exports: 'jQuery.ui.fancytree'
        }
    }
});

define('DS/ProjectStructureImportUX/assets/ven/Fancytree_table', ['DS/ProjectStructureImportUX/Fancytree_table'], function(tbl) {
    'use strict';
    return tbl;
});