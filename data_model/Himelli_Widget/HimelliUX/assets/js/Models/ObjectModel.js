define("VALCON/HimelliUX/ObjectModel", ['UWA/Core', 'UWA/Utils', 'DS/Foundation2/FoundationV2Data', 'DS/Foundation2/Models/FoundationBaseModel', 'DS/E6WCommonUI/UIHelper', 'DS/E6WCommonUI/Search', 'DS/E6WCommonUI/PlatformManager'], function(UWA, Utils, FoundationData, FoundationBaseModel, UIHelper, Search, PlatformManager) {
    'use strict';
    var PERSON_REGEX = /^iam:|^people:/;
    var i18n = function() {
        return FoundationData.getWidgetConstant.apply(this, arguments);
    };
    var ObjectModel = FoundationBaseModel.extend({
        _uwaClassName: 'HimelliUX-ObjectModel',
        getTransferObject: function() {
            var type = this.get('type') || this.get('busType');
            var transferObject;
            var id = this.get('physicalId');
            var serviceId = '3dspace';

            if (type === 'Person') {
                //in case of person DnD as if it was coming from swym if swym exists
                var swymService = PlatformManager.getServiceUrl('swym', true);
                if (swymService) {
                    type = 'pno:Person';
                    id = 'iam:' + this.get('name', {
                        noMapping: true
                    });
                    serviceId = '3DSwym';
                }
            }
            var lUIHelperOptions = {
                serviceId: serviceId,
                source: window.widget.getValue('appId') || 'X3DCSMA_AP', //appId may not have a value in non cloud installations
                envId: this.tenant || window.widget.getValue('x3dPlatformId'),
                objectId: id,
                objectType: type,
                objectType_Internal: type,
                displayName: this.get('name'),
                displayType: this.get('typeNLS') || UIHelper.translateType(type),
                image: this.get('image')
            };
            transferObject = UIHelper.getTransferObject(lUIHelperOptions);
            return transferObject;
        },
        /**
         * @override
         * read details from the server. Overridden for the specific case of people from swym
         * @method
         */
        syncRead: function(data, serviceName, options) {
            var that = this;
            if (this.serviceId === '3DSwym') { //case of a swym model
                var personMatch = this.id.match(PERSON_REGEX);
                if (personMatch) {
                    FoundationData.loadServiceData('PersonSearch?searchStr=' + this.id.substr(personMatch[0].length), function(resp) {
                        that.syncFromServer(resp);
                        options.onComplete && options.onComplete();
                    });
                }
            } else {
                this._parent.apply(this, arguments);
            }

        },
        /**
         * sync this object with the version of the same object coming from the server.
         * @override
         * in the case of person, make sure we get the correct one (the one where the name is the same as the id without the prefix).
         */
        syncFromServer: function(data) {
            if (this.serviceId === '3DSwym') { //case of a swym model
                var personMatch = this.id.match(PERSON_REGEX);
                var that = this;
                data.data = data.data.filter(function(iObject) { //keep only the correct result in case someone else has the login in his searchable fields
                    return iObject.dataelements.name === that.id.substr(personMatch[0].length);
                });
                if (data.data.length >= 1) {
                    this.set(this.parse(data.data[0]));
                } else {
                    UIHelper.displayError(i18n('emxCollaborativeTasks.Error.FailedToAssignUserAccessRightsIssue'));
                    //that.dispatchEvent('onNotFoundOnServer');
                    that.destroy({
                        localOnly: true
                    });
                }

            } else {
                this._parent.apply(this, arguments);
            }

        },
        /*
         * compute name differently for person in an artificial way
         */
        get: function(iAttrName, options) {
            if (iAttrName === 'name') {
                if (this.get('type') === 'Person') {
                    // No mapping incase of sending model.save request for an assignee, as assignee can be added with name too no need of id
                    if (options && options.noMapping) {
                        return this._parent.apply(this, arguments); //to still get the id when we need it
                    }
                    // send first+last in case of rendering person details,
                    // use display name incase first and last not present case when dropping person from  search
                    var firstName = this.get('firstname');
                    var lastName = this.get('lastname');
                    if (!firstName && !lastName) {
                        return this.get('displayName');
                    }
                    return this.get('firstname') + ' ' + this.get('lastname');
                }
                var lTitle = this.get('title');
                if (lTitle && lTitle.length) {
                    return lTitle;
                }


            } else if (iAttrName === 'image' && this.get('type') === 'Person') {
                // no mapping passed so we dont get full name instead we get he right id
                return UIHelper.userPictureURL(this.get('name', {
                    noMapping: true
                }));

            } else if (iAttrName === 'image') {
                var lM2Icon = this.get('dsM2:icon');
                if (lM2Icon && UWA.is(lM2Icon, 'object')) {
                    return lM2Icon;
                }
                // var lImage = this._parent.apply(this, arguments);
                // if (lImage) {
                // 	var parsedUrl = Utils.parseUrl(lImage);
                // }
            } else if (iAttrName === 'physicalId') {
                return this.id;
            } else if (iAttrName === 'dsaccess:accountName') {
                return this._parent('name');
            }

            return this._parent.apply(this, arguments);
        },
        toJSON: function(options) {
            var lOptions = UWA.clone(options || {}, false);
            if (!lOptions.hasOwnProperty('noMapping')) {
                lOptions.noMapping = !lOptions.dataForRendering;
            }
            var ret = this._parent.apply(this, arguments);
            // 	            if (ret.busType === 'Person') {
            // 	                if (!ret.image) {
            // 	                    ret.image = {};
            // 	                }
            // 	                ret.image.imageValue = FoundationData.userPictureURL(ret.name);

            // 	            }
            var lName = this.get('name', options);
            if (lName) {
                ret.name = lName;
            }
            var lImage = this.get('image', options);
            if (lImage) {
                ret.image = lImage;
            }
            return ret;
        },
        /*
         * while V2 and V1 are coexisting, need to have a physicalId
         * TODO remove this and change meta.html.handlebars when everything migrated
         */
        dataForRendering: function(options) {
            var lOptions = UWA.clone(options || {}, false);
            lOptions.dataForRendering = true;
            var ret = this.toJSON(lOptions);
            ret.physicalId = ret.id;
            return ret;
        }
    });
    // nma5: I dont think we are using below code, we need to remove it
    ObjectModel.typeAheadCallback = function(fieldConfig, term, callback, options) {
        //TODO filter out the people already part of the field
        var searchCriteria = '*';
        // depending on the source we need to change the query like for 3dplan it should be [ds6w:type]:
        var excludedSearchTypes = fieldConfig.excludeSearchTypes;
        if (excludedSearchTypes) {
            searchCriteria = '*  NOT ' + Search.buildSearchTypeQuery(excludedSearchTypes, {
                source: fieldConfig.searchSource
            });
        }
        var searchParams = 'searchStr=' + encodeURIComponent(term) + searchCriteria;
        var typeStr;
        if (fieldConfig && fieldConfig.searchTypes) {
            typeStr = encodeURIComponent(fieldConfig.searchTypes);
        } else {
            typeStr = ((fieldConfig.type === 'person') ? 'Person' : '*');
        }
        searchParams += '&typeStr=' + typeStr;
        return FoundationData.ajaxRequest({
            url: '/resources/v2/e6w/service/ObjectSearch?' + searchParams,
            type: 'get',
            dataType: 'json',
            callback: function(resp) {
                var callbackParam = [];
                if (resp.success && resp.data && resp.data.length) {
                    var lNbData = resp.data.length;
                    for (var lCurDataIdx = 0; lCurDataIdx < lNbData; lCurDataIdx++) {
                        var lCurRowObject = resp.data[lCurDataIdx];
                        if (lCurRowObject.dataelements) {
                            var lType = lCurRowObject.type;
                            var lName = lCurRowObject.dataelements.name || '';
                            var lRevision = lCurRowObject.dataelements.revision || '';
                            var lImage = lCurRowObject.dataelements.image || '';
                            callbackParam.push({
                                value: lCurRowObject.id,
                                label: lName,
                                revision: lRevision,
                                image: lImage,
                                sourceId: '3DSpace', //TODO check with Dave if this is correct, if not he needs to return the sourceId from the API
                                rowObject: lCurRowObject
                            });
                        }

                    }
                }
                callback(callbackParam, options);
            },
            headers: {
                'content-type': 'application/json'
            }
        });
    };
    Object.defineProperty(ObjectModel.prototype, 'serviceHosting', {
        get: function() {
            //  return '3DSwym'; //for launching properties
            if (this.get('type') === 'Person') {
                return 'fedsearch'; //for launching properties
            }
            return this.get('service') || '3DSpace';

        }
    });
    //person is only kind of a result model, it really is a proxy for a person in swym
    Object.defineProperty(ObjectModel.prototype, 'fedSearchIdQuery', {
        get: function() {
            //return this.id;
            if (this.get('type') === 'Person') {
                return 'resourceid:"iam:' + this.get('name', {
                    noMapping: true
                }) + '"'; //for launching properties
            }
            return 'physicalid:"' + this.id + '"';

        }
    });
    return ObjectModel;
});