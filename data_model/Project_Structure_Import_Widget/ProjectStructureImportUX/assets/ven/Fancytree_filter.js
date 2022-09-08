var lFancytreePath = require.toUrl('DS/ProjectStructureImportUX/assets/ven/Fancytree/plugins/v2.9.0/');
// Remove any query strings
var lIndexOfQuestionMark = lFancytreePath.indexOf('?');
if (lIndexOfQuestionMark > -1) { //remove ? and what follows from the url
    lFancytreePath = lFancytreePath.substring(0, lIndexOfQuestionMark);
}

require.config({
    paths: {
        'DS/ProjectStructureImportUX/Fancytree_filter': lFancytreePath + 'jquery.fancytree.filter'
    },
    shim: {
        'DS/ProjectStructureImportUX/Fancytree_filter': {
            deps: ['DS/ProjectStructureImportUX/assets/ven/Fancytree'],
            exports: 'jQuery.ui.fancytree'
        }
    }
});

define('DS/ProjectStructureImportUX/assets/ven/Fancytree_filter', ['DS/ProjectStructureImportUX/Fancytree_filter'], function(fltr) {
    'use strict';
    return fltr;
});