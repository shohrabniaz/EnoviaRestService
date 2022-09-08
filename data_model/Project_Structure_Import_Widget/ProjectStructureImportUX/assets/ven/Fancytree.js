var lFancytreePath = require.toUrl('DS/ProjectStructureImportUX/assets/ven/Fancytree/plugins/v2.9.0/');
// Remove any query strings
var lIndexOfQuestionMark = lFancytreePath.indexOf('?');
if (lIndexOfQuestionMark > -1) { //remove ? and what follows from the url
    lFancytreePath = lFancytreePath.substring(0, lIndexOfQuestionMark);
}

require.config({
    paths: {
        'DS/ProjectStructureImportUX/Fancytree': lFancytreePath + 'jquery.fancytree'
    },
    shim: {
        'DS/ProjectStructureImportUX/Fancytree': {
            deps: ['DS/ENO6WPlugins/jQueryUI', 'DS/ENO6WPlugins/jQuery', 'css!DS/ProjectStructureImportUX/assets/ven/Fancytree/plugins/v2.9.0/fancytree'],
            exports: 'jQuery.ui.fancytree'
        }
    }
});

define('DS/ProjectStructureImportUX/assets/ven/Fancytree', ['DS/ProjectStructureImportUX/Fancytree'], function(fancytree) {
    'use strict';
    return fancytree;
});