/*!
 * jquery.fancytree.filter.js
 *
 * Remove or highlight tree nodes, based on a filter.
 * (Extension module for jquery.fancytree.js: https://github.com/mar10/fancytree/)
 *
 * Copyright (c) 2008-2015, Martin Wendt (http://wwWendt.de)
 *
 * Released under the MIT license
 * https://github.com/mar10/fancytree/wiki/LicenseInfo
 *
 * @version 2.9.0
 * @date 2015-04-19T13:41
 */
(function ($, window, document, undefined) {

	"use strict";

	/*******************************************************************************
	 * Private functions and variables
	 */
	var KeyNoData = "__not_found__";

	function _escapeRegex(str) {
		/*jshint regexdash:true */
		return (str + "").replace(/([.?*+\^\$\[\]\\(){}|-])/g, "\\$1");
	}

	$.ui.fancytree._FancytreeClass.prototype._applyFilterImpl = function (filter, branchMode, opts, filterProperties) {
		var leavesOnly, match, re,
			count = 0,
			hideMode = this.options.filter.mode === "hide";
		var hideNode = false;

		opts = opts || {};
		leavesOnly = !!opts.leavesOnly && !branchMode;

		//  Depricated : Default to 'match title substring (not case sensitive)' <- not used anymore
		// Modified default title match; property is made dynamic for filter; function argument 'filterProperty'
		if (typeof filter === "string") {
			if (filter === "") {
				this.warn(
					"Fancytree passing an empty string as a filter is handled as clearFilter()."
				);
				this.clearFilter();
				return;
			}

			match = _escapeRegex(filter); // make sure a '.' is treated literally
			re = new RegExp(".*" + match + ".*", "i");
			filter = function (node) {
				var isFound = false;
				for (let index = 0; index < filterProperties.length; index++) {
					const filterProperty = filterProperties[index];
					if (filterProperty === "title") {
						isFound = !!re.exec(node[filterProperty]);
					} else {
						isFound = !!re.exec(node.data[filterProperty]);
					}
					if (isFound) break;
				}
				return isFound;
			};
		}

		this.enableFilter = true;
		this.lastFilterArgs = arguments;

		this.$div.addClass("fancytree-ext-filter");
		if (hideMode) {
			this.$div.addClass("fancytree-ext-filter-hide");
		} else {
			this.$div.addClass("fancytree-ext-filter-dimm");
		}
		// Reset current filter
		this.visit(function (node) {
			delete node.match;
			delete node.subMatch;
		});
		var statusNode = this.getRootNode()._findDirectChild(KeyNoData);
		if (statusNode) {
			statusNode.remove();
		}
		// Adjust node.hide, .match, .subMatch flags
		this.visit(function (node) {
			if (filter(node)) {
				count++;
				node.match = true;
				// visit to parents and set submatch true , further it will add 'disabled' css class
				node.visitParents(function (p) {
					p.subMatch = true;
					if (opts.autoExpand && !p.expanded) {
						p.setExpanded(true, { noAnimation: true, noEvents: true, scrollIntoView: false });
						p._filterAutoExpanded = true;
					}
				});

				// visit to child
				/*
					if child node matches the keyword
					then set child.match true,
					otherwise check if the child node is folder
					then set child.match false and collapse the node
				*/

				if (branchMode) {
					hideNode = true;
					node.visit(function (childNode) {
						if (filter(childNode)) {
							childNode.match = true;

						} else {
							childNode.match = false;
							if (childNode.folder) {
								childNode.expanded = false;
							}
						}
					});
					return "skip";
				}
			}

			else if (node.children == null && !filter(node)) {
				count++;
				node.match = hideNode;
				// visit to parents and set submatch true , further it will add 'disabled' css class
				node.visitParents(function (p) {
					p.subMatch = hideNode;
					if (opts.autoExpand && !p.expanded) {
						p.setExpanded(true, { noAnimation: true, noEvents: true, scrollIntoView: false });
						p._filterAutoExpanded = true;
					}
				});

				// visit to child
				/*
					if child node matches the keyword
					then set child.match true,
					otherwise check if the child node is folder
					then set child.match false and collapse the node
				*/

				if (branchMode) {
					if (filter(node)) {
						node.match = true;
						node.visitParents(function (p) {
							p.subMatch = true;
							if (opts.autoExpand && !p.expanded) {
								p.setExpanded(true, { noAnimation: true, noEvents: true, scrollIntoView: false });
								p._filterAutoExpanded = true;
							}
						});
					} else {
						node.match = false;
					}
					return "skip";
				}
			}

		});

		if (count === 0 && this.options.filter.nodata && hideMode) {
			var statusNode = this.options.filter.nodata;
			if ($.isFunction(statusNode)) {
				statusNode = statusNode();
			}
			if (statusNode === true) {
				statusNode = {};
			} else if (typeof statusNode === "string") {
				statusNode = { title: statusNode };
			}
			statusNode = $.extend({
				statusNodeType: "nodata",
				key: KeyNoData,
				title: this.options.strings.noData,
			},
				statusNode
			);
			this.getRootNode().addNode(statusNode).match = true;
		}
		// Redraw
		this.render();
		return count;
	};

	/**
	 * [ext-filter] Dimm or hide nodes.
	 *
	 * @param {function | string} filter
	 * @param {boolean} [opts={autoExpand: false, leavesOnly: false}]
	 * @returns {integer} count
	 * @alias Fancytree#filterNodes
	 * @requires jquery.fancytree.filter.js
	 */
	$.ui.fancytree._FancytreeClass.prototype.filterNodes = function (filter, opts, filterProperty) {
		if (typeof opts === "boolean") {
			opts = { leavesOnly: opts };
			this.warn("Fancytree.filterNodes() leavesOnly option is deprecated since 2015-04-20.");
		}
		return this._applyFilterImpl(filter, false, opts, filterProperty);
	};

	/**
	 * @deprecated
	 */
	$.ui.fancytree._FancytreeClass.prototype.applyFilter = function (filter) {
		this.warn("Fancytree.applyFilter() is deprecated since 2014-05-10. Use .filterNodes() instead.");
		return this.filterNodes.apply(this, arguments);
	};

	/**
	 * [ext-filter] Dimm or hide whole branches.
	 *
	 * @param {function | string} filter
	 * @param {boolean} [opts={autoExpand: false}]
	 * @returns {integer} count
	 * @alias Fancytree#filterBranches
	 * @requires jquery.fancytree.filter.js
	 */
	$.ui.fancytree._FancytreeClass.prototype.filterBranches = function (filter, opts, filterProperty) {
		return this._applyFilterImpl(filter, true, opts, filterProperty);
	};

	/**
	 * [ext-filter] Reset the filter.
	 *
	 * @alias Fancytree#clearFilter
	 * @requires jquery.fancytree.filter.js
	 */
	$.ui.fancytree._FancytreeClass.prototype.clearFilter = function () {
		var statusNode = this.getRootNode()._findDirectChild(KeyNoData);
		if (statusNode) {
			statusNode.remove();
		}

		this.visit(function (node) {
			delete node.match;
			delete node.subMatch;
			if (node._filterAutoExpanded && node.expanded) {
				node.setExpanded(false, { noAnimation: true, noEvents: true, scrollIntoView: false });
			}

			delete node._filterAutoExpanded;
		});
		this.enableFilter = false;
		this.lastFilterArgs = null;
		this.$div.removeClass("fancytree-ext-filter fancytree-ext-filter-dimm fancytree-ext-filter-hide");
		this.render();
	};

	/*******************************************************************************
	 * Extension code
	 */
	$.ui.fancytree.registerExtension({
		name: "filter",
		version: "0.4.0",
		// Default options for this extension.
		options: {
			autoApply: true, // re-apply last filter if lazy data is loaded
			mode: "hide"
		},
		// treeInit: function(ctx){
		// 	this._superApply(arguments);
		// },
		nodeLoadChildren: function (ctx, source) {
			return this._superApply(arguments).done(function () {
				if (ctx.tree.enableFilter && ctx.tree.lastFilterArgs && ctx.options.filter.autoApply) {
					ctx.tree._applyFilterImpl.apply(ctx.tree, ctx.tree.lastFilterArgs);
				}
			});
		},
		nodeSetExpanded: function (ctx, flag, callOpts) {
			delete ctx.node._filterAutoExpanded;
			return this._superApply(arguments);
		},
		nodeRenderStatus: function (ctx) {
			// Set classes for current status
			var res,
				node = ctx.node,
				tree = ctx.tree,
				$span = $(node[tree.statusClassPropName]);

			res = this._superApply(arguments);
			// nothing to do, if node was not yet rendered
			if (!$span.length || !tree.enableFilter) {
				return res;
			}
			$span.toggleClass("fancytree-match", !!node.match)
				.toggleClass("fancytree-submatch", !!node.subMatch)
				.toggleClass("fancytree-hide", !(node.match || node.subMatch));
			return res;
		}
	});
}(jQuery, window, document));