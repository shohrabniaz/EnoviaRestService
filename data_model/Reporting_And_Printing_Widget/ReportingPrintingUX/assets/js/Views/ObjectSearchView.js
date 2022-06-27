try {
    define("VALCON/ReportingPrintingUX/ObjectSearchView", ["VALCON/ReportingPrintingUX/ObjectModel", "DS/E6WCommonUI/Views/FoundationBaseView", "VALCON/ReportingPrintingUX/PreferenceUtil", "VALCON/ReportingPrintingUX/DetailsDisplayView", "DS/Foundation2/FoundationV2Data", "VALCON/ReportingPrintingUX/WidgetConfiguration", "VALCON/ReportingPrintingUX/PreferenceConfiguration", "i18n!DS/ReportingPrintingUX/assets/nls/ReportingPrintingUX"], function(ObjectModel, FoundationBaseView, PreferenceUtil, DetailsDisplayView, FoundationData, WidgetConfiguration, PreferenceConfiguration, i18n) {
        var savedObject = "savedObject";
        var savedType = "savedType";
        var savedID = "savedID";
        var savedName = "savedName";
        var savedRevision = "savedRevision";
        var savedTitle = "savedTitle";

        var objectModel = ObjectModel.extend({
            get: function(e) {
                if ("canEdit" === e) return !0, !0;
                this._parent.apply(this, arguments)
            },
            dataForRendering: function() {
                return {
                    canEdit: !0
                }
            },
            add: function(e, t) {
                this._fireRelationshipChangeEvents(e, t, !0)
            }
        });
        var ObjectSearchView = FoundationBaseView.extend({
            _uwaClassName: "ObjectView",
            className: "reporting-printing-view col-md-12",
            id: function() {
                return "reportingPrintingView"
            },
            setup: function(e) {
                this.container.inject(widget.body);

                this.model = new objectModel;
                this._searchBar = new DetailsDisplayView({
                    model: this.model,
                    id: "itemSearch"
                });
                this._searchBar.setConfig([{
                    readWrite: "true",
                    name: "item",
                    attributeToDisplay: "name",
                    imageAttribute: "image",
                    singleObjectOnly: !0,
                    searchTypes: WidgetConfiguration.RP_ITEM_TYPE[widget.getValue(PreferenceConfiguration.RP_PREF_NAME_REPORT_TYPE)],
                    validTypes: WidgetConfiguration.RP_ITEM_TYPE[widget.getValue(PreferenceConfiguration.RP_PREF_NAME_REPORT_TYPE)],
                    searchTitle: "Item Search",
                    typeAheadCallback: ObjectModel.typeAheadCallback,
                    type: "object"
                }]);

                this.listenTo(this.model, "onChange:item", this._onItemSelected);
            },
            prerender: function() {},
            changeAutoCompleteDisplaySettings: function() {
                this._searchBar.getElement("input");
                var e = this._searchBar.getElement(".chooser");
                this._searchBar._childrenViews[0].autoCompleteControl.setOption("position", {
                    my: "left-1.5 top",
                    at: "left bottom",
                    of: e,
                    collision: "fit",
                    within: this.container
                })
            },
            renderObjectSearch: function() {
                this._searchBar.render().inject(this.container);
                this._searchBar._childrenViews[0].container.toggleClassName("fluid-width", !0);
                this._searchBar._childrenViews[0].container.toggleClassName("visible", !0);
                this._searchBar._childrenViews[0].container.toggleClassName("ds-detail-display", !1);
                this.changeAutoCompleteDisplaySettings();
                this.refreshPlaceHolderText();
            },
            render: function(e) {
                var t = this;
                this.container.empty();
                this.renderObjectSearch();
                return t
            },
            _onItemSelected: function(e, t) {
                FoundationData.ajaxRequest({
                    url: "/resources/v2/e6w/service/ObjectInfo/" + t + "?$fields=objectId",
                    callback: function(e) {
                        var D = UWA.is(e, "object") ? e : JSON.parse(e);
                        if (D.data[0].dataelements.objectId) {
                            PreferenceUtil.set(savedObject, D.data[0].dataelements.objectId);
                            PreferenceUtil.set(savedTitle, D.data[0].dataelements.title);
                            PreferenceUtil.set(savedID, D.data[0].dataelements.objectId);
                            PreferenceUtil.set(savedType, D.data[0].type);
                            PreferenceUtil.set(savedName, D.data[0].dataelements.name);
                            PreferenceUtil.set(savedRevision, D.data[0].dataelements.revision);

                            console.info("%cSelected Object Properties: ", "color: #6ef5ee");
                            console.info("%cType : " + D.data[0].type, "color: #6ef5ee");
                            console.info("%cID : " + D.data[0].dataelements.objectId, "color: #6ef5ee");
                            console.info("%cName : " + D.data[0].dataelements.name, "color: #6ef5ee");
                            console.info("%cRevision : " + D.data[0].dataelements.revision, "color: #6ef5ee");
                            widget.getElement(".autocomplete-input").value = D.data[0].dataelements.name;
                            RPWidget.controls.revision.setValue(D.data[0].dataelements.revision);

                          
                        }
                    }
                });
                this.refreshPlaceHolderText();
            },
            refreshPlaceHolderText: function() {
                this.setPlaceHolderText(i18n["RP_TEXT_" + widget.getElement("#reportType").value.toUpperCase() + "_ITEM_SEARCH"]);
            },
            setPlaceHolderText: function(placeHolderText) {
                var A = this._searchBar.getElement("input[type=text]");
                if (!A) {
                    return
                }
                A.setAttribute("placeholder", placeHolderText);
            },
        });
        return ObjectSearchView;
    });
} catch (error) {
    console.log("==============================================Error:======" + error.Name);
}