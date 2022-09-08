define("VALCON/HimelliUX/DetailsDisplayView", ["UWA/Core", "UWA/Utils", "UWA/Event", "UWA/Class/Model", "UWA/Class/Events", "UWA/Controls/Drag", "DS/Handlebars/Handlebars4", "DS/ENO6WPlugins/jQuery_3.3.1", "DS/E6WCommonUI/Views/FoundationBaseView", "DS/E6WCommonUI/Views/TextView", "DS/E6WCommonUI/Views/TextAreaView", "DS/E6WCommonUI/Views/SelectView", "DS/E6WCommonUI/Views/CheckBoxView", "DS/E6WCommonUI/Views/DateView", "DS/E6WCommonUI/Views/NumberView", "VALCON/HimelliUX/ObjectView", "DS/E6WCommonUI/Views/PersonView", "DS/E6WCommonUI/Views/RangeView", "DS/E6WCommonUI/Views/StartStopView", "DS/E6WCommonUI/Views/UomView", "DS/E6WCommonUI/Views/E6WHandlebarsHelpers", "DS/E6WCommonUI/UIHelper", "DS/E6WCommonUI/Views/DetailsDisplaySharedView", "text!DS/E6WCommonUI/assets/templates/meta.html.handlebars", "i18n!DS/E6WCommonUI/assets/nls/E6WCommonUINLS", "css!DS/E6WCommonUI/E6WCommonUI", "css!DS/UIKIT/UIKIT"], function(e, t, i, n, a, o, r, s, l, c, d, u, h, m, p, g, f, v, E, _, b, C, y, w, D) {
    "use strict";
    return l.extend({
        _uwaClassName: "DetailsDisplayView",
        getHtmlFromTemplate: r.compile(w),
        className: "ds-dialog ds-detail-display ddv-view",
        id: function() {
            return "detailDisplayView" + this.cid
        },
        domEvents: {
            "click .form-section-title": "_showGroupProperties"
        },
        i18n: function(e, t) {
            return t || e
        },
        _displayMessage: function(e) {
            if (this._options.displayMessage) this._options.displayMessage.apply(this, arguments);
            else {
                var t = {
                    message: e.msg,
                    className: e.msgType
                };
                C.displayMessage(t) || (C.initializeAlert(this.container), C.displayMessage(t))
            }
        },
        setup: function(t) {
            var i = e.clone(t || {}, !1);
            this._options = i, this._parent.apply(this, arguments);
            var n = this;
            s("body").on("click.closeListInputFields", function(t) {
                if (n._config)
                    for (var i = e.Event.getElement(t), a = n._getConfigFromElement(i), o = n._config.length, r = 0; r < o; r++) {
                        var s = n._config[r];
                        switch (s.type) {
                            case "object":
                            case "person":
                                n._getChildViewBasedOnConfig(s).checkDialogDeactivation(s, a)
                        }
                    }
            })
        },
        getChildViewBasedOnConfig: function(e) {
            for (var t, i = this._childrenViews, n = 0; n < i.length; n++) {
                var a = i[n];
                if (a._config && a._config.type === e.type && a._config.name === e.name) {
                    t = a;
                    break
                }
            }
            return t
        },
        _getChildViewBasedOnConfig: function(e) {
            return this._configMap.get(e)
        },
        _resizeHandler: function() {
            var e = this;
            ! function() {
                var t = e.container.clientWidth,
                    i = e._config.length,
                    n = 0 !== t && t !== e.previousResizeWidth;
                n && (e.previousResizeWidth = t);
                for (var a = 0; a < i; a++) {
                    var o = e._config[a];
                    switch (o.type) {
                        case "text":
                            n && "true" === o.readWrite && y.prototype.normalizedGet(e.model, "canEdit") && e._getChildViewBasedOnConfig(o)._postTreatmentTextArea();
                            break;
                        case "textarea":
                            n && e._getChildViewBasedOnConfig(o)._postTreatmentTextArea()
                    }
                }
            }()
        },
        _dataForRenderingWithWhiteListAttributes: function() {
            var t = [],
                i = {};
            this._childrenViews.forEach(function(e) {
                var i = e.getDataOptions(),
                    n = t[i.level];
                n || (n = t[i.level] = {}), i.attributes.forEach(function(e) {
                    n[e] = !0
                })
            });
            for (var n = 0; n < t.length; n++) {
                var a = t[n];
                a && e.extend(i, C.normalizedToJson(this.model, {
                    level: n,
                    attributes: Object.keys(a)
                }))
            }
            return i
        },
        _setChildViews: function(t) {
            var i = this._childrenViews;
            if (i)
                for (var n = this._dataForRenderingWithWhiteListAttributes(), a = 0; a < i.length; a++) {
                    var o = i[a],
                        r = o._config;
                    o.model || (o.model = this.model);
                    var s = {
                        data: n
                    };
                    e.merge(s, t), o.render(s), this.injectFieldViewForConfig(r, o)
                }
        },
        injectFieldViewForConfig: function(e, t) {
            var i = e.group ? e.group.actualValue || e.group : void 0;
            i ? t.inject(this.container.getElement("#_" + this.cid + this.groupMap[i].actualValue + " .form-section-body")) : this.container.getElement(".form-section") ? t.inject(this.container.getElement(".form-section"), "before") : t.inject(this.container.getElement(".form.form-vertical"))
        },
        _template: function(e) {
            for (var t = 0; t < this._config.length; t++) {
                if (this._config[t].required) {
                    e.required = !0, e.requiredLegend = D.get("emxFoundation.Label.RequiredFieldLegend");
                    break
                }
            }
            return Object.keys(this.groupMap).length > 0 && (e.groupNames = this.groupMap, e.viewid = "_" + this.cid), this.getHtmlFromTemplate(e)
        },
        _filterConfigIntoGroups: function() {
            for (var e = this._config, t = {}, i = 0; i < e.length; i++) {
                var n = e[i],
                    a = n.group ? n.group.actualValue || n.group : void 0;
                if (a) {
                    var o = t[a];
                    o ? a && o.push(n) : (t[a] = [n], t[a].displayValue = a, t[a].actualValue = "G" + i)
                }
            }
            return t
        },
        render: function(e) {
            var t;
            if (!this._config) return this;
            t = this.getElements(".accordion.expanded").map(function(e) {
                return e.id
            });
            var i = {};
            return i.openedGroups = t, this.container.setHTML(this._template(i)), this._setChildViews(e), this.dispatchEvent("onFinishRender"), delete this.previousResizeWidth, this.container.isInjected() ? this._resizeHandler() : this.listenToOnce(this, "onPostInject", this._resizeHandler), this._toggleGroups(t, !0), this
        },
        setConfig: function(t) {
            this._config = t.map(function(t) {
                return e.clone(t, !1)
            }), this.groupMap = this._filterConfigIntoGroups(), delete t.options, this.cleanUpChildren();
            for (var i = 0; i < this._config.length; i++) {
                var n = this._config[i];
                n.index = i;
                var a = e.clone(this._options || {}, !1);
                e.extend(a, {
                    config: n,
                    model: this.model
                }), this._createAndAddChildView(n, a)
            }
        },
        cleanUpChildren: function() {
            return this._configMap = new WeakMap, this._parent.apply(this, arguments)
        },
        _createAndAddChildView: function(t, i) {
            var n, a = e.clone(i || {}, !1);
            if (a.detailDisplayViewId = "#" + (e.is(this.id, "function") ? this.id() : this.id), t.customView) n = new t.customView(a);
            else switch (t.type) {
                case "text":
                    n = new c(a);
                    break;
                case "textarea":
                    n = new d(a);
                    break;
                case "select":
                    n = new u(a);
                    break;
                case "checkbox":
                    n = new h(a);
                    break;
                case "date":
                    n = new m(a);
                    break;
                case "number":
                    n = new p(a);
                    break;
                case "object":
                    n = new g(a);
                    break;
                case "person":
                    n = new f(a);
                    break;
                case "range":
                    n = new v(a);
                    break;
                case "startStop":
                    n = new E(a);
                    break;
                case "uom":
                    n = new _(a)
            }
            n && (this._configMap.set(t, n), this.addChildView(n))
        },
        resetModel: function(t, i, n) {
            var a = e.clone(n || {}, !1);
            if (t !== this.model) {
                var o = this.getElements(".accordion.expanded").map(function(e) {
                    return e.id
                });
                this._toggleGroups(o, i);
                var r = this._childrenViews;
                if (r)
                    for (var s = 0; s < r.length; s++) r[s].resetModel(t);
                this.model = t, !a.noRender && this.render()
            }
        },
        _toggleGroups: function(e, t) {
            var i = this;
            e && e.forEach(function(e) {
                e && e.length > 0 && i.getElement("#" + e).toggleClassName("expanded", t)
            })
        },
        destroy: function() {
            delete this._configMap, this._parent(), s("body").off("click.closeListInputFields")
        },
        _showGroupProperties: function(t) {
            var i = e.Event.getElement(t);
            e.extendElement(i.parentElement).toggleClassName("expanded")
        },
        _getConfigFromElement: function(e) {
            var t, i = e.getClosest(".ddv-field");
            if (i) {
                var n = i.getAttribute("data-field-index");
                n = parseInt(n, 10), t = this._config[n]
            }
            return t
        },
        isModelValid: function() {
            for (var e = this._config, t = 0; t < e.length; t++)
                if (e[t].required && !y.prototype.normalizedGet(this.model, e[t].name)) return !1;
            return !0
        },
        getFieldViewByName: function(e) {
            var t;
            return this._childrenViews.some(function(i) {
                return i._config.name === e && (t = i, !0)
            }), t
        },
        dispatchScrollToElementEvent: function(e) {
            this.dispatchEvent("scrollToElement", e)
        }
    })
});