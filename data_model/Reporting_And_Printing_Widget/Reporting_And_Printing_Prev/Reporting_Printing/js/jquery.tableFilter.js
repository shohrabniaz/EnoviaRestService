(function($){
    "use strict";
    $.fn.tableFilter = function(options) {
	
		//default settings
	    var settings = $.extend({
			tableID: '#filter-table',
			filterID: '#filter',
			filterCell: '.filter-cell',
			autoFocus: false,
			caseSensitive: false,
			noResults: 'no results found',
			columns: null
		}, options);
		
		//auto focus on filter element if autofocs set to true
		if(settings.autoFocus) {
			$(settings.filterID).focus();
		}
		
		//get table rows
		var rowCount = $(settings.filterCell).parent().length;
		
		//get tablecolumns by counting td's in forst row unless passed as option
		if(settings.columns === null) {
			settings.columns = $(settings.tableID + ' > tbody > tr:first >td').length;
		}
		
		//use case-sensitive matching unless changed by settings (default)
		var contains = ':contains';
		
		if(!settings.caseSensitive) {
			//create custom "icontains" selector for case insensitive search
			$.expr[':'].icontains = $.expr.createPseudo(function(text) {
			    return function(e) {
			        return $(e).text().toUpperCase().indexOf(text.toUpperCase()) >= 0;
			    };
			});
			contains = ':icontains';
		}
		
		//bind eventListener to filter element
		return this.find(settings.filterID).on("change paste keyup", function() {
			//get value of input
			var filterString = $(this).val();
		
			//for each student name compare versus filter input
			$(settings.filterCell).each(function(i){ //pass i as iterator
				if($(this).is(contains + '(' + filterString + ')')) {
					//check hidden rows for backspace operation
					if($(this).is(':hidden')) {
						$(this).parent().removeClass('filter-hidden').show();
					}
				} else {
					$(this).parent().addClass('filter-hidden').hide();
				}
				//check if .each() iterations complete
				if(rowCount === (i + 1)) {
					//find rows with 'hidden' class and compare to row count if equal then display 'no results found' message
					var hidden = $(settings.tableID).find('.filter-hidden').length;
					if (hidden === rowCount) {
						if ($('#noResults').is(':visible')) {
							return; //do not display multiple "no results" messages
						}
						var newRow = $('<tr id="noResults"><td colspan="' + settings.columns +'"><em>' + settings.noResults + '</em></td></tr>').hide(); //row can be styled with CSS
						$(settings.tableID).append(newRow);
						newRow.show();
					} else if ($('#noResults').is(':visible')) { 
						$('#noResults').remove();
					} 
				}
			}); 
		});
	};
}(jQuery));
