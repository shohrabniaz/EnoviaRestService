define("VALCON/HimelliUX/ObjectView", ["UWA/Core", "UWA/Class/Collection", "DS/RDFFoundation/RDFUtils", "DS/Handlebars/Handlebars4", "UWA/Controls/Drag", "UWA/Class/Promise", "DS/E6WCommonUI/Views/FoundationBaseView", "DS/E6WCommonUI/Views/DetailsDisplaySharedView", "DS/UIKIT/Spinner", "DS/E6WCommonUI/Search", "DS/E6WCommonUI/UIHelper", "DS/E6WCommonUI/DragUtil", "DS/E6WCommonUI/Views/CustomizedAutocomplete", "DS/E6WCommonUI/Models/DropValidator", "text!DS/E6WCommonUI/assets/templates/object.html.handlebars", "i18n!DS/E6WCommonUI/assets/nls/E6WCommonUINLS"], function(e, t, i, n, a, o, r, s, l, c, d, u, h, m, p, g) {
    "use strict";
    var f = n.compile(p),
        v = (d.isIE() || d.isEdge(), s.extend({
            _uwaClassName: "HimelliUX-ObjectView",
            tagName: "div",
            name: "RP-ObjectView",
            domEvents: {
                "dragenter .drop-target": "handleDragEnter",
                "dragover .drop-target": "handleDragOver",
                "dragleave .drop-target": "handleDragLeave",
                "drop .drop-target": "handleDrop",
                "dragend .drop-target": "handleDragEnd",
                "click .object-list a": "objectClick",
                "click ul": "_activateObjectField",
                "click button.ds-group-plus": "_activateObjectField",
                "click button.ds-group-upload": "_uploadToObjectList",
                "click .custom-button": "_onClickCustomButton",
                'keypress .ds-group-add input[type="text"]': "_keyUpInputAddObject",
                'input .ds-group-add input[type="text"]': "_enableDisableSearch",
                "click label": "_preventLabelDefault",
                "click input": "_onInputClicked",
                "keyup input": "_onKeyUp"
            },
            i18n: function(e, t) {
                return t || e
            },
            getDataOptions: function() {
                var e = this._parent.apply(this, arguments);
                e.level = 1;
                var t = e.attributes.indexOf("canEdit");
                return e.attributes.splice(t, 1, "physicalId", this._config.attributeToDisplay, this._config.imageAttribute, "busType", "type", "hasfiles"), e
            },
            _getData: function() {
                var e = this._parent.apply(this, arguments);
                return e.hasOwnProperty("canEdit") || (e.canEdit = this.normalizedGet(this.model, "canEdit")), e
            },
            setup: function(e) {
                this._parentContainerId = e.detailDisplayViewId, this._parent.apply(this, arguments);
                this._config.readWrite && (this.search = this._config.search || new c("", !1, "furtive")), this.context = e && e.context, this._createAutocomplete()
            },
            _getMinLengthBeforeSearch: function() {
                var e = this._config.minLengthBeforeAutocomplete;
                return void 0 === e && (e = 2), e
            },
            _createAutocomplete: function() {
                this.autoCompleteControl = new h({
                    disabled: !0,
                    cid: this.cid,
                    placeholder: this._config.placeHolder || "",
                    minLengthBeforeSearch: this._getMinLengthBeforeSearch(),
                    noResultsMessage: g.get("emxFoundation.Label.NoResultsFound"),
                    datasets: [{
                        configuration: {
                            templateEngine: this._addAutoCompleteItemToContainer.bind(this),
                            remoteSearchEngine: this._getAndSendAutoCompleteRequest.bind(this)
                        }
                    }]
                }), this.autoCompleteControl.elements.input.addClassName("form-control")
            },
            _preventLabelDefault: function(e) {
                return e.preventDefault(), !1
            },
            render: function(t) {
                var i = e.clone(t || {}, !1);
                this.removeAutoCompleteFromDOM(), this.autoCompleteControl.setBoundingElement(i.boundingElement);
                var n = this._parent.apply(this, arguments);
                return this._postTreatmentObjectList(), this._listenToSubModels(), this.setDefaultImages(), this.autoCompleteControl.enable(), n
            },
            _submodelCallback: function() {
                this.render()
            },
            _listenToSubModels: function() {
                this.stopListening(null, null, this._submodelCallback);
                var t = this.normalizedGet(this.model, this._config.name);
                e.is(t, "array") || (t = t ? [t] : []);
                for (var i = t.length, n = 0; n < i; n++) {
                    var a = t[n];
                    a && a.addEvent && (this.listenTo(a, "onChange", this._submodelCallback), this.listenTo(a, "onViewAccessChanged", this._submodelCallback))
                }
            },
            checkDialogDeactivation: function(t, i) {
                if (t !== i) {
                    if (this) {
                        var n = this.container.getElement("ul");
                        n && (n.toggleClassName("active", !1), e.is(t.activationCallback, "function") && t.activationCallback(t.name, !1))
                    }
                }
            },
            _uploadToObjectList: function() {
                var e = this,
                    t = this._config;
                d.openFileExplorer({
                    callback: function(t) {
                        e._addToModel(t)
                    },
                    accept: t.accept
                })
            },
            _addToModel: function(t, i) {
                var n = e.clone(i || this._options || {}, !1),
                    a = this._config,
                    o = this.normalizedGet(this.model, a.name);
                if (a.singleObjectOnly && o && e.is(o, "array") && o.length > 0 && e.is(this.model.remove, "function")) {
                    var r = n.singleObjectRemove;
                    n.singleObjectRemove = !0, this.model.remove(a.name, 0, n), n.singleObjectRemove = r
                }
                return this.modelAdd(a.name, t, n)
            },
            _onClickCustomButton: function(t) {
                t.preventDefault && t.preventDefault();
                var i = e.Event.getElement(t),
                    n = this._config,
                    a = i.getAttribute("data-custom-button-index");
                a = parseInt(a);
                var o = n.customButtons;
                o && o[a].callback.call(this, o)
            },
            handleDragEnter: function(t) {
                e.Event.getElement(t).addClassName("drag-over")
            },
            handleDragLeave: function(t) {
                e.Event.getElement(t).removeClassName("drag-over")
            },
            handleDragOver: function(t) {
                return t.preventDefault && t.preventDefault(), t.dataTransfer.dropEffect = "copy", e.Event.getElement(t).addClassName("drag-over"), !1
            },
            handleDrop: function(t) {
                this.getElement(".object-list") && this.getElement(".object-list").removeClassName("drag-over"), t.stopPropagation && t.stopPropagation(), t.preventDefault && t.preventDefault(), this.getElement("input") && this.getElement("input").removeClassName("drag-over");
                var i = this._config,
                    n = e.clone(this._options || {}, !1);
                if ((n.config = i, n.objectView = this, i.dropHandler) && (e.is(i.dropHandler, "function") ? i.dropHandler : this.model.dropHandler).call(this.model, t.dataTransfer, i, n)) return !1;
                return t.dataTransfer.files && t.dataTransfer.files.length ? this._handleDroppedFiles(t.dataTransfer.files) : !this.validateDroppedObjects(t.dataTransfer) && this.dropHandler(t.dataTransfer, i, n)
            },
            dropHandler: function(e) {
                var t = d.getDataTransferJSON(e);
                return t && this.processDropData(t), !1
            },
            _handleDroppedFiles: function(e) {
                var t = this._config;
                if (t.allowFileManagement && "true" === t.readWrite && this.normalizedGet(this.model, "canEdit")) {
                    if (t.accept) {
                        for (var i = t.accept.toLowerCase().split(","), n = e.length, a = [], o = 0; o < n; o++) {
                            var r = e[o],
                                s = r.name,
                                l = s.lastIndexOf(".");
                            if (-1 !== l) {
                                var c = s.substring(l); - 1 !== i.indexOf(c.toLowerCase()) && a.push(r)
                            }
                        }
                        if (e = a, !a.length) return !1
                    }
                    return this._addToModel(e), !1
                }
                return t.allowFileManagement || d.displayError(g.get("E6WCommonUI.Error.DroppingFilesNotSupported")), !0
            },
            validateDroppedObjects: function(e) {
                for (var t, i = d.getDataTransferJSON(e).data.items, n = 0; n < i.length && !t; n++) {
                    var a = i[n];
                    (t = this.validateDroppedObject(a)) && d.displayError(t)
                }
                return Boolean(t)
            },
            validateDroppedObject: function(e) {
                return m.validateDroppedObject(e, this._config)
            },
            processDropData: function(t) {
                for (var i = t.data.items, n = i.length, a = this._config, o = !1, r = 0; r < n; r++) {
                    var s = i[r],
                        l = s.displayName;
                    e.is(this.model.preprocessPersonObject, "function") && this.model.preprocessPersonObject(s);
                    var c = e.clone(this._options || {}, !1);
                    c.config = a, c.dropObj = s;
                    var d = this._addToModel(s.objectId, c);
                    if (d && d.length > 0) {
                        o = !0;
                        var u, h = d[0];
                        u = "person" === a.type ? "firstname" : this._config.attributeToDisplay, this.normalizedGet(h, u) || this.normalizedSet(h, {
                            name: u
                        }, l)
                    }
                }
                o && this.render()
            },
            objectClick: function(i, n) {
                var a = e.clone(n || {}, !1),
                    o = e.Event.getElement(i),
                    r = this._config;
                i.stopPropagation && i.stopPropagation(), i.preventDefault && i.preventDefault(), i.clickedOnObject = !0;
                var s, l = o.getClosest("li").getAttribute("data-id"),
                    c = r.searchSource,
                    u = this.normalizedGet(this.model, r.name);
                if (e.is(u, "array") || u instanceof t || (u = [u]), u && u.length > 0)
                    for (var h = u.length, m = 0; m < h; m++) {
                        var p = u.at ? u.at(m) : u[m],
                            f = d.normalizedToJson(p);
                        if (e.is(f, "array"))
                            for (var v = f.length, E = 0; E < v; E++)
                                if (f[E].physicalId === l) {
                                    f = f[E];
                                    break
                                }
                        if (f.physicalId === l) {
                            s = p;
                            break
                        }
                    }
                if (a.model = s, !r.linkCallback || !r.linkCallback.call(this, l, a)) {
                    var _ = g.get("emxFoundation.Label.ObjectDetails");
                    a = {
                        title: _ = _.replace("%NAME%", o.getText()),
                        source: c,
                        tenant: void 0,
                        role: this.context,
                        idcard_activated: !0,
                        select_result_max_idcard: !0,
                        default_search_criteria: "physicalid:" + l
                    }, this.search && this.search.activate(a)
                }
                return !0
            },
            _onKeyUp: function(e) {
                "Tab" === e.key && this._activateObjectField(e)
            },
            _activateObjectField: function(t) {
                if (t.preventDefault && t.preventDefault(), !t.clickedOnObject) {
                    var i = this._config,
                        n = this.getElement("ul");
                    if (("true" === i.readWrite || !0 === i.readWrite) && this.normalizedGet(this.model, "canEdit") && n) {
                        n.toggleClassName("active", !0), e.is(i.activationCallback, "function") && i.activationCallback(i.name, !0);
                        var a = n.getElement("input");
                        a && a.focus()
                    }
                }
            },
            handleDragEnd: function(t) {
                e.Event.getElement(t).removeClassName("drag-over")
            },
            _postTreatmentObjectList: function(e) {
                if (e || (e = this._config), e.typeAheadCallback && e.readWrite && this._postTreatmentObjectListAutocomplete(e), this.postTreatmentObjectListClickHandlers(e), ("object" === e.type || "person" === e.type) && this.shouldDisableSearch()) {
                    var t = this.getElement(".ds-group-add .btn");
                    t && (t.disabled = !0)
                }
            },
            _onAddButtonClicked: function(e) {
                e.stopPropagation && e.stopPropagation(), e.preventDefault && e.preventDefault();
                var t = this._config,
                    i = this.getElement("input"),
                    n = i.value.trim().split(",").map(function(e) {
                        return e.trim()
                    });
                n = this.getFederatedQueryCriteria(n);
                var a = t.searchSource,
                    o = a && t.searchSourceTypes || t.searchTypes,
                    r = t.excludeSearchTypes;
                o || (o = "person" === t.type ? "Person" : "");
                var s = t.searchTitle;
                s || (s = ""), i.value = "", this._toggleSearchButton(i);
                var l = {
                    title: s,
                    source: a,
                    role: this.context,
                    types: o,
                    multiSel: !0 !== t.singleObjectOnly,
                    default_search_criteria: n,
                    excludedSearchTypes: r,
                    callback: this._searchCallback.bind(this)
                };
                this.search && this.search.activate(l), this.autoCompleteControl.reset()
            },
            getFederatedQueryCriteria: function(e) {
                return e.length > 1 ? "(" + e.join(" OR ") + ")" : 0 === e.length || 0 === e[0].length ? "" : e[0] + "*"
            },
            postTreatmentObjectListClickHandlers: function(t) {
                var i = this;
                if (e.is(this.model.add, "function")) {
                    var n = this.getElement(".ds-group-add button");
                    n && n.addEvent("click", this._onAddButtonClicked.bind(this))
                }
                if (e.is(this.model.remove, "function"))
                    for (var a = function(n, a) {
                            a.stopPropagation();
                            var o = e.clone(i._options || {}, !1);
                            o.config = t;
                            var r = this.getAttribute("data-field-name");
                            i.model.remove(r, n, o)
                        }, o = this.getElements(".ds-group-remove"), r = o.length, s = 0; s < r; s++) {
                        var l = o[s];
                        l.getClosest("button").addEvent("click", a.bind(l, s))
                    }
                for (var c = this.getElements(".fonticon-download"), d = c.length, u = 0; u < d; u++) {
                    var h = c[u];
                    h.getClosest("button").addEvent("click", this.objectClickDownload.bind(this)), this._donwloadAddedCode(h)
                }
                this._makeDraggableAndDroppable()
            },
            _donwloadAddedCode: Function.prototype,
            _makeDraggableAndDroppable: function() {
                var e = this.container.getElements('[draggable="true"]');
                this._makeDraggableAndDroppableUWA();
                for (var t = 0; t < e.length; t++) {
                    var i = e[t];
                    i && i.addEvent("dragstart", this._dragHandler.bind(this, i.getParent()))
                }
            },
            _dragHandler: function(e, t) {
                var i = this.computeDragDataFromElement(e);
                t.dataTransfer.effectAllowed = "copy", u.setDragTransferData(t, i), d.extraDragForSwym(i)
            },
            getModelFromCard: function(e) {
                var t = e.getClosest("li").getAttribute("data-id"),
                    i = this,
                    n = this.normalizedGet(this.model, this._config.name),
                    a = n;
                return n.forEach && n.forEach(function(e) {
                    i._normalizedGetPhysicalId(e) === t && (a = e)
                }), a
            },
            _normalizedGetPhysicalId: function(e) {
                var t = this.normalizedGet(e, "physicalId");
                return t || (t = d.normalizedToJson(e).physicalId), t
            },
            computeDragDataFromModel: function(e) {
                var t;
                if (e.getTransferObject) t = e.getTransferObject();
                else {
                    var i = this.normalizedGet(e, "type") || this.normalizedGet(e, "busType");
                    t = d.getTransferObject({
                        source: window.widget.getValue("appId") || "X3DCSMA_AP",
                        envId: this.tenant || window.widget.getValue("x3dPlatformId"),
                        contextId: this.context,
                        objectId: this._normalizedGetPhysicalId(e),
                        objectType: i,
                        objectType_Internal: i,
                        displayName: this.normalizedGet(e, this._config.attributeToDisplay)
                    })
                }
                return t
            },
            computeDragDataFromElement: function(e) {
                var t = this.getModelFromCard(e);
                return this.computeDragDataFromModel(t)
            },
            _makeDraggableAndDroppableUWA: function() {
                this.dragControl && this.dragControl.destroy();
                var t = this;
                this.dragControl = new a.Move({
                    container: widget.body,
                    root: this.container,
                    zoneCss: ".drop-target",
                    zones: function() {
                        return widget.body.getElements(this.options.zoneCss)
                    },
                    delegate: ".draggable-item *",
                    centerHandles: !0,
                    handles: function(i) {
                        var n = i.target.getParent(".draggable-item");
                        i.elemToClone = n;
                        var a = e.Element.extend(n.cloneNode(!0));
                        a.setStyle("width", n.offsetWidth), a.setStyle("height", n.offsetHeight);
                        for (var o = a.getElements("button"), r = 0; r < o.length; r++) o[r].remove();
                        var s = e.createElement("ul", {
                            class: "object-list"
                        });
                        a.inject(s);
                        var l = e.createElement("div", {
                            styles: {
                                position: "absolute",
                                zIndex: 100
                            }
                        });
                        l.toggleClassName("draggable-helper", !0), s.inject(l);
                        var c = t._config.appendTo ? t._config.appendTo : "body",
                            d = n.getClosest(c);
                        return l.inject(d), l
                    },
                    start: function(e) {
                        if (e.currentEvent instanceof MouseEvent) return !1;
                        var i = e.target.getParent(".draggable-item");
                        i.toggleClassName("ui-being-dragged", !0);
                        var n = t.computeDragDataFromElement(i);
                        return i.setData(v.DRAG_DATA_TAG, n), !0
                    },
                    stop: function(e) {
                        var t = e.elemToClone;
                        t.toggleClassName("ui-being-dragged", !1), t.toggleClassName("drag", !1), e.handles.destroy()
                    },
                    enter: function(e) {
                        var t = e.zone;
                        e.accept() && (t.toggleClassName("drag-over", !0), t.dispatchEvent(e.getCustomEvent("dragenter")))
                    },
                    leave: function(e) {
                        e.zone.toggleClassName("drag-over", !1)
                    },
                    drop: function(e) {
                        var t = e.zone;
                        t.toggleClassName("drag-over", !1), e.accept() && t.dispatchEvent(e.getCustomEvent("drop"))
                    }
                }), this.dragControl.accept = function() {
                    return this.target.getClosest(this.options.zoneCss) !== this.zone
                }, this.dragControl.getCustomEvent = function(e) {
                    var t = this.target.getParent(".draggable-item").getData(v.DRAG_DATA_TAG);
                    t.getData = function() {
                        return this
                    };
                    var i = new window.Event(e, {
                        bubbles: !0
                    });
                    return i.dataTransfer = t, i
                }
            },
            objectClickDownload: function(t, i) {
                var n = e.Event.getElement(t);
                t.stopPropagation && t.stopPropagation(), t.preventDefault && t.preventDefault(), t.clickedOnObject = !0;
                var a = n.getClosest("li").getAttribute("data-id");
                return this.download({
                    pid: a,
                    tenant: i && this.normalizedGet(i, "dscom:Tenant"),
                    serviceId: i && this.normalizedGet(i, "dscom:ServiceHosting")
                }), !0
            },
            download: function(t) {
                var i;
                t = t || {};
                var n = this,
                    a = e.clone(t, !1);
                return a.autoDownload = !0, a.csrf = this.model.csrf, a.onFailure = function() {
                    n._parentView._displayMessage({
                        msg: g.get("emxFoundation.Alert.PopupBlocker"),
                        msgType: "error"
                    }), t.onFailure && t.onFailure()
                }, this._options.DocumentManagement && (i = this._options.DocumentManagement.downloadDocument(a.pid, void 0, Boolean(t.checkout), a)), i
            },
            processAutoCompleteData: function(e) {
                return e
            },
            _addAutoCompleteItemToContainer: function(t, i, n) {
                var a = this.processAutoCompleteData(n),
                    o = e.createElement("span", {
                        class: "thumbnail profile-image"
                    });
                a.image && e.createElement("img", {
                    draggable: "false",
                    src: a.image,
                    title: a.label,
                    "data-actualvalue": a.value,
                    events: {
                        contextmenu: function() {
                            return !1
                        }
                    }
                }).inject(o), o.inject(t), e.createElement("span", {
                    text: a.label + " (" + a.revision + ")"
                }).inject(t)
            },
            _getExistingIds: function() {
                var e = {},
                    t = this.getExistingObjs(this._config.name);
                return t && (Array.isArray(t) || (t = [t]), t.forEach(function(t) {
                    e[t.id] = !0
                })), e
            },
            _getAndSendAutoCompleteRequest: function(t, i) {
                var n = this;
                return new o(function(a) {
                    var o = v.filter.bind(void 0, function(i) {
                        var o = {};
                        o.dataset = t;
                        var r = [];
                        e.is(i, "array") && i.forEach(function(e) {
                            e.handler = n.onAutocompleteSelect.bind(n, n._config, e), r.push(e)
                        }), o.matchingItems = r, a(o)
                    }, n._getExistingIds());
                    n._previousTypeaheadRequest && n._previousTypeaheadRequest.cancel && n._previousTypeaheadRequest.cancel(), n._previousTypeaheadRequest = n._config.typeAheadCallback(n._config, i, o, {
                        model: n.model
                    })
                })
            },
            removeAutoCompleteFromDOM: function() {
                this.autoCompleteControl && this.autoCompleteControl.remove()
            },
            _postTreatmentObjectListAutocomplete: function() {
                if (this.normalizedGet(this.model, "canEdit") && this._config.readWrite) {
                    var e = this.getElement("input");
                    e && e.remove(), this.autoCompleteControl.inject(this.getElement("li.chooser"), "top")
                }
            },
            _searchCallback: function(t) {
                for (var i = [], n = t.length, a = 0; a < n; a++) {
                    var o = t[a];
                    o.id ? (d.enrichObjectFromSearch(o), i.push(o.id)) : i.push(o)
                }
                var r = i.join(","),
                    s = e.clone(this._options || {}, !1);
                s.detailedObjects = t, s.config = this._config;
                var l = this._config;
                if (l.singleObjectOnly && t && t.length > 0) return r = i[0], s.detailedObjects = [t[0]], void this._addToModel(r, s);
                this.modelAdd(l.name, r, s)
            },
            modelAdd: function() {
                if (e.is(this.model.add, "function")) return this.model.add.apply(this.model, arguments)
            },
            destroy: function() {
                this.search && this.search.destroy(), delete this.search, this.autoCompleteControl.disable(), document.removeEventListener("click", this.autoCompleteControl.events.document.click, !0), this.autoCompleteControl.destroy(), delete this.autoCompleteControl, this._parent()
            },
            _keyUpInputAddObject: function(t) {
                if ("Enter" === t.key) {
                    var i = e.Event.getElement(t).getClosest(".form-group").getElement(".ds-group-add .btn");
                    i.disabled || i.click()
                }
            },
            _enableDisableSearch: function(t) {
                var i = e.Event.getElement(t);
                this._toggleSearchButton(i)
            },
            _toggleSearchButton: function() {
                var e = this.getElement(".ds-group-add .btn");
                e && (this.getElement("input").value.length > 0 ? e.disabled = !1 : this.shouldDisableSearch() && (e.disabled = !0))
            },
            onAutocompleteSelect: function(t, i) {
                var n = e.clone(i, !1);
                n.id = i.value || i.id, delete n.value, this._searchCallback([n]), this.autoCompleteControl.reset()
            },
            getExistingObjs: function(e) {
                return this.normalizedGet(this.model, e)
            },
            showAutoComplete: function() {
                var e = this.getElement("input");
                this.autoCompleteControl && this.autoCompleteControl.getSuggestions(e.value)
            },
            _onInputClicked: function() {
                this.getElement("input").value.length >= this._getMinLengthBeforeSearch() && this.showAutoComplete()
            },
            shouldDisableSearch: function() {
                return !this._config.searchTypes && !this._config.searchSourceTypes
            },
            getCardById: function(e) {
                return this.container.getElement('li[data-id="' + e + '"]')
            }
        }));
    return v.filter = function(e, t, i) {
        e(i.filter(function(e) {
            return !t[e.value]
        }))
    }, v.prototype.compiledTemplate = f, v.DRAG_DATA_TAG = "dragDataTransfer", v.DROP_CLASS_NAME = "drag-over", v
});