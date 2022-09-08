define("DS/i3DXCompass/Tools", ["UWA/Core", "UWA/Element", "UWA/Utils"], function(d, a, b) {
    var c = {
        os: null,
        browser: null,
        detectOs: function() {
            var e = c.getUserAgent().toLowerCase();
            if (e.indexOf("windows") > -1) {
                c.os = (e.indexOf("windows nt 10") > -1) ? "windows10" : "windows";
                return
            }
            if (e.match(/(ipad|iphone|ipod)/g)) {
                c.os = "ios";
                return
            }
            if (e.indexOf("mac") > -1) {
                c.os = "macos";
                return
            }
            if (e.indexOf("android") > -1) {
                c.os = "android";
                return
            }
            if (e.indexOf("linux") > -1) {
                c.os = "linux";
                return
            }
            c.os = "unknown"
        },
        _detectBrowser: function() {
            var e = b.Client.Engine;
            if (window.navigator.userAgent.indexOf("Edge") > -1) {
                c.browser = "edge"
            } else {
                if (e.ie) {
                    c.browser = "internet explorer"
                } else {
                    c.browser = e.name
                }
            }
        },
        getOs: function() {
            if (!c.os) {
                c.detectOs()
            }
            return c.os
        },
        getBrowser: function() {
            if (!c.browser) {
                c._detectBrowser()
            }
            return c.browser
        },
        getMajorIosVersion: function() {
            if (!c.iosVersion) {
                if (/iP(hone|od|ad)/.test(navigator.platform)) {
                    var e = (navigator.appVersion).match(/OS (\d+)_(\d+)_?(\d+)?/);
                    c.iosVersion = [parseInt(e[1], 10), parseInt(e[2], 10), parseInt(e[3] || 0, 10)];
                    d.log("iOS version = " + c.iosVersion[0] + "." + c.iosVersion[1] + "." + c.iosVersion[2])
                }
            }
            return c.iosVersion && c.iosVersion[0]
        },
        getUserAgent: function() {
            return navigator.userAgent
        },
        arrayFind: function(f, h, g) {
            var e;
            f.some(function(i) {
                if (h.call(g, i)) {
                    e = i;
                    return true
                }
            });
            return e
        },
        forEachOwnProperty: function(h, g, e) {
            var f = Object.keys(h);
            f.forEach(function(i) {
                g.call(e, h[i], i)
            })
        },
        pushUnique: function(e, f) {
            if (e.indexOf(f) === -1) {
                e.push(f)
            }
        },
        unshiftUnique: function(e, f) {
            if (e.indexOf(f) === -1) {
                e.unshift(f)
            }
        },
        moveToFirst: function(f, g) {
            var e = f.indexOf(g);
            if (e !== -1) {
                f.unshift(f.splice(e, 1)[0])
            }
        },
        includes: function(f, e) {
            return e.every(function(g) {
                return f.indexOf(g) !== -1
            })
        },
        removeDoublons: function(f) {
            var e = [];
            f.forEach(function(g) {
                c.pushUnique(e, g)
            });
            return e
        },
        customLaunchWebApp: function() {
            return false
        },
        enableWidgetDrag: function() {},
        instanciateWidget: function() {},
        onRoleChange: function() {},
        setCustomFunction: function(e, f) {
            if (["customLaunchWebApp", "enableWidgetDrag", "instanciateWidget", "onRoleChange"].indexOf(e) > -1) {
                c[e] = f
            }
        },
        disableSelect: function() {
            a.setStyles.call(document.body, {
                "-webkit-touch-callout": "none",
                "-webkit-tap-highlight-color": "rgba(0, 0, 0, 0)",
                "-webkit-user-select": "none",
                "-moz-user-select": "none",
                "-ms-user-select": "none",
                "user-select": "none"
            })
        },
        enableSelect: function() {
            a.setStyles.call(document.body, {
                "-webkit-touch-callout": "",
                "-webkit-tap-highlight-color": "",
                "-webkit-user-select": "",
                "-moz-user-select": "",
                "-ms-user-select": "",
                "user-select": ""
            })
        },
        isOverElement: function(f, g, e) {
            return f.x > g.x && f.x < g.x + e.width && f.y > g.y && f.y < g.y + e.height
        },
        isSameApp: function(f, e) {
            if (b.matchUrl(f, e)) {
                return (b.parseUrl(f).directoryPath || "/").split("/")[1] === (b.parseUrl(e).directoryPath || "/").split("/")[1]
            }
            return false
        },
        isBase64: function(f) {
            var e = new RegExp("^(?:[A-Za-z0-9+/]{4})*(?:[A-Za-z0-9+/]{2}==|[A-Za-z0-9+/]{3}=|[A-Za-z0-9+/]{4})$");
            return e.test(f)
        },
        isBase64Image: function(e) {
            if (e.indexOf("data:image") > -1 && e.indexOf(";base64,") > -1) {
                e = e.split(";base64,")[1]
            }
            return this.isBase64(e)
        },
        htmlEntityDecode: function(e) {
            return d.createElement("div", {
                html: e
            }).getText()
        },
        now: Date.now || function() {
            return new Date().getTime()
        },
        debounce: function(g, i, f) {
            var l, k, e, j, m;
            var h = function() {
                var n = c.now() - j;
                if (n < i && n >= 0) {
                    l = setTimeout(h, i - n)
                } else {
                    l = null;
                    if (!f) {
                        m = g.apply(e, k);
                        if (!l) {
                            e = k = null
                        }
                    }
                }
            };
            return function() {
                e = this;
                k = arguments;
                j = c.now();
                var n = f && !l;
                if (!l) {
                    l = setTimeout(h, i)
                }
                if (n) {
                    m = g.apply(e, k);
                    e = k = null
                }
                return m
            }
        },
        compareVersion: function(g, e) {
            var h, f, j;
            g = g.split(".");
            e = e.split(".");
            f = Math.min(g.length, e.length);
            for (h = 0; h < f; h++) {
                j = parseInt(g[h], 10) - parseInt(e[h], 10);
                if (j !== 0) {
                    return j
                }
            }
            return g.length - e.length
        },
        i18n: function(e) {
            var f = d.i18n(e);
            if (f) {
                f = f.replace("3DEXPERIENCE", "<b>3D</b>EXPERIENCE")
            }
            return f
        },
        isSmartphone: function() {
            return b.Client.getSize().width < 959 && b.Client.getSize().height < 570
        },
        isValidMarketAppUrl: function(e) {
            return /^[a-z]*:\/\/\?-.*/.test(e)
        }
    };
    return c
});
define("DS/i3DXCompass/Model/Install", ["UWA/Core", "UWA/Class/Model", "UWA/Class/Collection"], function(f, e, c) {
    var a = e.extend({
            idAttribute: "appsId"
        }),
        d = c.extend({
            model: a
        }),
        b = e.extend({
            isCompatible: function(g) {
                var h = this.get("GAlevel").substr(1, 3);
                return (g.length === 0 || g.indexOf(h) > -1)
            },
            setup: function() {
                this._apps = new d(this.get("apps"));
                this.addEvent("onChange:apps", function(h, g) {
                    this._apps.set(g)
                })
            }
        });
    return b
});
define("DS/i3DXCompass/Controls/Search", ["UWA/Core", "UWA/Controls/Abstract", "DS/UIKIT/Input/Text"], function(e, d, b) {
    var c = e.i18n,
        a = d.extend({
            className: "compass-search",
            defaultOptions: {
                typeDelay: 500
            },
            isActive: false,
            currentText: "",
            typeTimeout: null,
            init: function(f) {
                this._parent(f);
                this.buildSkeleton()
            },
            buildSkeleton: function() {
                var g = this,
                    f = this.container = e.createElement("div", {
                        "class": this.className
                    }),
                    h = this.elements = {};
                h.button = e.createElement("div", {
                    "class": this.className + "-button fonticon fonticon-search",
                    events: {
                        click: function() {
                            g.dispatchEvent("onToggle")
                        }
                    }
                }).inject(f);
                h.reset = e.createElement("button", {
                    "class": this.className + "-text-reset close fonticon fonticon-clear",
                    styles: {
                        "font-size": "18px"
                    },
                    attributes: {
                        type: "button",
                        "aria-hidden": true
                    },
                    events: {
                        click: function() {
                            g.dispatchEvent("onReset")
                        }
                    }
                });
                h.text = new b({
                    className: this.className + "-text",
                    placeholder: c("searchapps"),
                    attributes: {
                        events: {
                            keyup: function() {
                                g.dispatchEvent("onKeyUp")
                            }
                        }
                    }
                });
                h.textWrapper = e.createElement("div", {
                    "class": this.className + "-text-wrapper",
                    html: [h.reset, h.text]
                }).inject(f);
                f.addClassName(this.className)
            },
            onToggle: function() {
                var f = !this.isActive;
                this.container.toggleClassName("active", f);
                if (f) {
                    this.elements.text.setFocus()
                } else {
                    this.dispatchEvent("onReset")
                }
                this.isActive = f
            },
            onReset: function() {
                var f = this.elements.text;
                if (this.currentText !== "") {
                    window.clearTimeout(this.typeTimeout);
                    f.setValue("");
                    this.currentText = "";
                    this.dispatchEvent("onSearch", "")
                }
                f.setFocus()
            },
            onKeyUp: function() {
                var f = this;
                var g = this.elements.text.getValue().toLowerCase();
                if (g !== this.currentText) {
                    this.currentText = g;
                    window.clearTimeout(this.typeTimeout);
                    this.typeTimeout = window.setTimeout(function() {
                        f.dispatchEvent("onSearch", g)
                    }, this.options.typeDelay)
                }
            },
            hide: function() {
                this.container.addClassName("transparent")
            },
            show: function() {
                this.container.removeClassName("transparent")
            },
            blur: function() {
                this.elements.text.setFocus(false)
            }
        });
    return a
});
define("DS/i3DXCompass/Types", ["UWA/Core"], function(c) {
    var a = {},
        b = null;
    a.get = function() {
        c.log("get Types");
        c.log("Types = " + JSON.stringify(b));
        return b
    };
    a.set = function(d) {
        c.log("Set Types");
        b = d
    };
    a.reset = function() {
        b = null
    };
    return a
});
define("DS/i3DXCompass/Model/Product", ["UWA/Core", "UWA/Class/Model"], function(c, b) {
    var a = b.extend({});
    return a
});
define("DS/i3DXCompass/UsageTracker", ["UWA/Core", "DS/Usage/TrackerAPI"], function(d, c) {
    function b(g, f, h, e) {
        if (!e && !h) {
            return g + ":" + f
        } else {
            if (!e) {
                h = h.replace(/ /g, "");
                return g + ":" + h + ":" + f
            } else {
                h = h.replace(/ /g, "");
                return g + ":" + h + ":" + f + ":" + e
            }
        }
    }
    var a = {
        _IDS: {
            compass: {
                quadrant: {
                    north: "compass.quadrant.north",
                    east: "compass.quadrant.east",
                    south: "compass.quadrant.south",
                    west: "compass.quadrant.west",
                    play: "compass.quadrant.play"
                },
                cross: "compass.cross",
                role: "compass.role",
                roleFavorite: "compass.role.favorite",
                app: "compass.app",
                appFavorite: "compass.app.favorite",
                section: "compass.section"
            }
        },
        inc: function(g, f, h, e) {
            c.getCounter(b(g, f, h, e)).inc()
        }
    };
    return a
});
define("DS/i3DXCompass/Controls/IframeLoader", ["UWA/Core", "UWA/Controls/Abstract", "DS/UIKIT/Mask"], function(e, d, b) {
    var c = "compass-iframe-loader-failed";
    var a = d.extend({
        defaultOptions: {
            src: "",
            text: "",
            timeout: 15000,
            frameId: "3DCommerceRoleDescription"
        },
        className: "compass-iframe-loader",
        init: function(f) {
            this._parent(f);
            this.buildSkeleton()
        },
        buildSkeleton: function() {
            var f = this.options,
                g = this.elements = [];
            g.container = e.createElement("div", {
                "class": this.className
            });
            if (f.src && f.src !== "" && !this._isUrl(f.src)) {
                this.buildText(f.src)
            } else {
                if (f.src && f.src !== "" && this._isUrl(f.src) && !this.getFailed()) {
                    b.mask(g.container);
                    this.launchTimeout();
                    g.iframe = e.createElement("iframe", {
                        src: f.src,
                        allowfullscreen: true
                    }).inject(g.container)
                } else {
                    if (f.src && f.src !== "" && this._isUrl(f.src) && this.getFailed()) {
                        this.buildText();
                        this.launchTimeout();
                        g.iframe = e.createElement("iframe", {
                            src: f.src,
                            "class": "hidden",
                            allowfullscreen: true
                        }).inject(g.container)
                    } else {
                        this.buildText()
                    }
                }
            }
        },
        buildText: function(f) {
            this.elements.container.setContent(e.createElement("div", {
                "class": f ? this.className + "-text-description" : this.className + "-text",
                html: f || this.options.text
            }))
        },
        _isUrl: function(g) {
            var f = new RegExp("^(https?:\\/\\/)((([a-z\\d]([a-z\\d-]*[a-z\\d])*)\\.)+[a-z]{2,}|((\\d{1,3}\\.){3}\\d{1,3}))(\\:\\d+)?(\\/[\\(\\)-a-z\\d%_.~+]*)*(\\?[\\(\\);&a-z,\\d%_.~+=-]*)?(\\#[-a-z\\d_]*)?$", "i");
            return (f.test(g))
        },
        launchTimeout: function() {
            var h = this,
                i = this.elements,
                f = function() {
                    removeEventListener("message", g);
                    clearTimeout(j)
                },
                g = function(l) {
                    var k = l.data;
                    if (k.id === h.options.frameId) {
                        f();
                        if (k.code === 0) {
                            i.iframe.addClassName("visible");
                            i.iframe.removeClassName("hidden");
                            if (e.typeOf(k.height) === "number") {
                                i.iframe.setStyle("height", k.height)
                            }
                            if (i.container.getElement("." + h.className + "-text")) {
                                i.container.getElement("." + h.className + "-text").addClassName("hidden")
                            }
                        } else {
                            h.buildText()
                        }
                        b.unmask(i.container);
                        h.setFailed(false)
                    }
                },
                j = setTimeout(function() {
                    f();
                    b.unmask(i.container);
                    h.buildText();
                    h.setFailed(true)
                }, this.options.timeout);
            addEventListener("message", g)
        },
        getFailed: function() {
            var g;
            try {
                g = sessionStorage.getItem(c);
                return g === "true"
            } catch (f) {
                e.log(f);
                return false
            }
        },
        setFailed: function(f) {
            var h = f ? "true" : "false";
            try {
                sessionStorage.setItem(c, h)
            } catch (g) {
                e.log(g)
            }
        }
    });
    return a
});
define("DS/i3DXCompass/Controls/Message", ["UWA/Core", "UWA/Controls/Abstract", "DS/UIKIT/Spinner"], function(d, c, b) {
    var a = c.extend({
        options: {
            className: "compass-overlay"
        },
        init: function() {
            var e = this.elements = {
                text: d.createElement("div", {
                    "class": this.options.className + "-text"
                }),
                spinner: new b({
                    className: this.options.className + "-spinner compass-spinner",
                    animate: false
                })
            };
            e.container = d.createElement("div", {
                "class": this.options.className,
                styles: {
                    display: "none"
                },
                html: [e.text, e.spinner]
            })
        },
        show: function(g, f) {
            var e = this.elements;
            window.clearTimeout(this.timeoutVar);
            e.text.setContent(g || "");
            if (f) {
                e.spinner.show()
            } else {
                e.spinner.hide()
            }
            e.container.show()
        },
        hide: function(e) {
            if (!e) {
                this.elements.container.hide()
            } else {
                this.timeoutVar = window.setTimeout(this.hide.bind(this), e)
            }
        }
    });
    return a
});
define("DS/i3DXCompass/EventManager", ["UWA/Core", "UWA/Class", "UWA/Class/Events"], function(d, b, a) {
    var c = b.extend(a);
    return new c()
});
define("DS/i3DXCompass/Model/Suggestion", ["UWA/Core", "UWA/Utils", "UWA/Class/Model"], function(d, a, c) {
    var b = {
        idAttribute: "productId",
        setup: function() {}
    };
    return c.extend(b)
});
define("DS/i3DXCompass/X3DContent", ["UWA/Core"], function(c) {
    var a = {},
        b = {};
    a.getX3DContent = function() {
        c.log("get X3DContent");
        c.log("Content = " + JSON.stringify(b));
        return b
    };
    a.setX3DContent = function(d) {
        c.log("set X3DContent");
        b = d
    };
    return a
});
define("i3DXCompass/X3DContent", ["DS/i3DXCompass/X3DContent"], function(a) {
    return a
});
define("DS/i3DXCompass/CacheManager", ["UWA/Core", "UWA/Class", "UWA/Utils", "UWA/Storage", "UWA/Storage/Adapter/Dom"], function(c, b, g, i) {
    var d, a, h = "420.1.0",
        f = 24 * 3600000,
        e = new i({
            adapter: "Dom",
            database: "3DCompass"
        });
    return b.singleton({
        setup: function(j, k) {
            d = j;
            if (!j) {
                e = null
            }
            a = k
        },
        setRequestCache: function(k, j) {
            this.setCache(k.url + JSON.stringify(k.data), j)
        },
        getRequestCache: function(j) {
            return this.getCache(j.url + JSON.stringify(j.data))
        },
        setCache: function(j, k) {
            if (e) {
                try {
                    e.set(this._getKeyUID(j), k)
                } catch (l) {
                    e = null
                }
            }
        },
        getCache: function(k, m) {
            var j = null;
            if (!m) {
                m = f
            }
            if (e) {
                try {
                    j = e.get(this._getKeyUID(k), m)
                } catch (l) {
                    e = null
                }
            }
            return j
        },
        _getKeyUID: function(j) {
            return g.getCheckSum(j + a + d + h)
        },
        _getUser: function() {
            return d
        }
    })
});
define("DS/i3DXCompass/TopBarManager", ["UWA/Core"], function(c) {
    var b = c.i18n,
        a = {
            topBarProxy: null,
            menu: null,
            events: {},
            initialized: false,
            MenuItem: null,
            _roles: null,
            init: function(d) {
                require(["DS/TopBarProxy/TopBarProxy", "DS/TopBarProxy/Menu", "DS/TopBarProxy/MenuItem"], function(e, g, f) {
                    a.menu = new g();
                    a.topBarProxy = new e({
                        id: d.topBarId
                    });
                    a.initialized = true;
                    a.topBarProxy.addContent({
                        profile: [new f({
                            label: b("myroles"),
                            submenu: a.menu,
                            multiSelect: true
                        })]
                    });
                    a.events.onRoleChange = d.onRoleChange;
                    a.MenuItem = f;
                    if (a._roles) {
                        a.setRoles(a._roles)
                    }
                })
            },
            setRoles: function(e) {
                var f = a.MenuItem,
                    d;
                if (!a.initialized) {
                    a._roles = e
                } else {
                    if (a._roles) {
                        a._roles = null
                    }
                    d = e.map(function(g) {
                        return new f({
                            roleId: g.get("id"),
                            label: g.get("title"),
                            selected: g.get("active"),
                            selectable: true,
                            onExecute: function(h) {
                                a.events.onRoleChange(h.get("roleId"), !h.get("selected"))
                            }
                        })
                    });
                    if (e.length > 1) {
                        a.topBarProxy.setContent({
                            profile: [new f({
                                label: b("myroles"),
                                submenu: a.menu,
                                multiSelect: true
                            })]
                        });
                        a.menu.reset();
                        a.menu.add(d)
                    } else {
                        a.topBarProxy.empty();
                        a.menu.reset()
                    }
                }
            },
            removeRole: function(e) {
                var d = a.getRoleItem(e);
                if (d) {
                    a.menu.remove(d);
                    if (a.menu.length <= 1) {
                        a.topBarProxy.empty()
                    }
                }
            },
            setChecked: function(e) {
                var d = a.getRoleItem(e);
                if (d) {
                    d.set("selected", e.get("active"))
                }
            },
            containsRole: function(d) {
                return !!a.getRoleItem(d)
            },
            getRoleItem: function(d) {
                return a.menu.findWhere({
                    roleId: d.id
                })
            }
        };
    return a
});
define("DS/i3DXCompass/View/SuggestionListView", ["UWA/Core", "UWA/Class/View", "DS/UIKIT/Spinner"], function(d, c, b) {
    var a = {
        className: "suggestion-section",
        suggestionsRendered: false,
        attachEvents: function() {
            var e = this.collection;
            e.addEvent("onRemove", this.removeSug.bind(this));
            e.addEvent("onMultipleAdd", this.renderSuggestions.bind(this))
        },
        render: function() {
            var e = this.elements.loader = d.createElement("div", {
                "class": "compass-loading",
                html: new b({
                    visible: true,
                    className: "compass-spinner"
                })
            });
            if (this.suggestionsRendered) {
                e.hide()
            }
            this.container.addContent(e);
            return this
        },
        renderSuggestions: function() {
            var f = this.elements,
                g = this.collection.map(function(h) {
                    return {
                        tag: "li",
                        "class": "suggestion-item",
                        html: [{
                            tag: "img",
                            "class": "suggestion-item-icon",
                            src: h.get("thumbnailUrl")
                        }, {
                            "class": "suggestion-item-description",
                            html: [{
                                "class": "suggestion-item-title",
                                text: h.get("title")
                            }, {
                                "class": "suggestion-item-baseline",
                                text: h.get("baseline")
                            }]
                        }],
                        "data-id": h.get("productId")
                    }
                }),
                e = f.list;
            if (!e) {
                e = f.list = d.createElement("ul", {
                    "class": "suggestion-list"
                }).inject(this.container)
            }
            e.setContent(g);
            this.unmask();
            this.attachEvents();
            this.suggestionsRendered = true;
            return this
        },
        refresh: function() {
            var e = this;
            if (this.suggestionsRendered) {
                this.mask();
                this.collection.fetch({
                    onComplete: function() {
                        e.unmask()
                    },
                    onFailure: function() {
                        e.unmask()
                    }
                })
            }
        },
        removeSug: function(e) {
            var f = this.getSugElmt(e);
            if (f) {
                f.destroy()
            }
        },
        getSugElmt: function(e) {
            return this.getElement(d.String.format('[data-id="{0}"]', e.id))
        },
        mask: function() {
            var e = this.elements.loader;
            if (e) {
                e.show()
            }
        },
        unmask: function() {
            var e = this.elements.loader;
            if (e) {
                e.hide()
            }
        }
    };
    return c.extend(a)
});
define("DS/i3DXCompass/InterCom", ["UWA/Utils/InterCom", "DS/i3DXCompass/Tools"], function(c, b) {
    var a = {
        initialize: function(d, e) {
            var f = a.eventServer = new c.Server(d, {
                isPublic: true
            });
            b.forEachOwnProperty(e, function(h, g) {
                f.addListener(g, h)
            })
        },
        fireEvent: function(d, e) {
            if (a.eventServer) {
                a.eventServer.dispatchEvent(d, e)
            }
        }
    };
    return a
});
define("DS/i3DXCompass/Data", ["UWA/Core", "UWA/Utils", "UWA/Ajax", "UWA/Data", "DS/i3DXCompass/InterCom", "DS/i3DXCompass/EventManager", "DS/WebappsUtils/WebappsUtils", "DS/WAFData/WAFData", "DS/i3DXCompass/CacheManager", "DS/i3DXCompassPlatformServices/i3DXCompassPlatformServices", "DS/i3DXCompass/Tools", "UWA/Storage", "UWA/Storage/Adapter/Object"], function(g, i, b, m, j, f, d, k, h, a, e, p) {
    var o, c, n = g.i18n,
        l = {
            root: null,
            saveSectionTimeout: 0,
            initialize: function(s, q) {
                s.myAppsBaseUrl = s.myAppsBaseUrl.replace("/resources/AppsMngt", "");
                var r = s.myAppsBaseUrl.replace(/\/$/, "") + "/resources/AppsMngt/";
                this.myAppsBaseUrl = r;
                this.getAppsUrl = r + "apps/compass";
                if (s.userId) {
                    this.getAppsUrl = r + "apps/compass"
                } else {
                    this.getAppsUrl = r + "apps/public/compass"
                }
                this.addinMode = s.addinMode;
                h.setup(s.userId, s.lang);
                this.setPreferencesUrl = r + "user/setPreferences";
                this.getPreferencesUrl = r + "user/getPreferences";
                this.setFavoriteUrl = r + "user/setFavorite";
                this.setSectionUrl = r + "apps/compass/section";
                this.getProcessDetailsUrl = r + "apps/licenses";
                this.requestRoleUrl = r + "role/request";
                this.tryRoleUrl = r + "role/try";
                this.encryptUrl = r + "security/encrypt";
                this.decryptUrl = r + "security/decrypt";
                this.getSuggestionsUrl = r + "role/suggestions";
                this.getAppInfoUrl = r + "apps";
                if (s.passportUrl) {
                    this.passportUrl = s.passportUrl
                }
                this.onlineInstallUrl = null;
                this.buildTransactionId = null;
                o = g.extend({}, s);
                this.root = q;
                f.dispatchEvent("onInitialized")
            },
            request: function(q) {
                var u = {
                        Accept: q.Accept || "application/json",
                        "Accept-Language": o ? o.lang : null
                    },
                    t, r, s = q.proxy;
                if ((q.method === "POST" || q.method === "PUT") && q.contentType) {
                    u["Content-Type"] = q.contentType
                }
                if (q.headers) {
                    u = g.merge(u, q.headers)
                }
                t = {
                    timeout: q.timeout !== undefined ? q.timeout : 15000,
                    cache: q.cache !== undefined ? q.cache : -1,
                    method: q.method || "GET",
                    type: q.type || "json",
                    headers: u,
                    proxy: (s === "none" || !h._getUser()) ? null : (s || "passport"),
                    data: q.postData,
                    onComplete: q.onComplete,
                    onFailure: q.onFailure
                };
                r = q.url + (q.urlParams ? "?" + i.toQueryString(q.urlParams) : "");
                if (l._isCorsAvailable(q) || (q.urlParams && q.urlParams.cors)) {
                    return t.proxy === "passport" ? k.authenticatedRequest(r, t) : k.request(r, t)
                } else {
                    return m.request(r, t)
                }
            },
            get3DDashboardUrl: function(q) {
                var r = this;
                if (this.dashboardUrl) {
                    if (q) {
                        q(this.dashboardUrl)
                    }
                } else {
                    a.getServiceUrl({
                        serviceName: "3DDashboard",
                        onComplete: function(t) {
                            var s;
                            if (t.length > 0) {
                                s = t.detect(function(u) {
                                    return u.url
                                });
                                if (s) {
                                    r.dashboardUrl = s.url
                                }
                            }
                            if (q) {
                                q(r.dashboardUrl)
                            }
                        }
                    })
                }
            },
            setPreferences: function(r) {
                var q = {
                    url: l.setPreferencesUrl,
                    urlParams: r,
                    onComplete: function(s) {
                        if (s.code === 0) {
                            f.dispatchEvent("onShowMessage", [s.message, 5000])
                        } else {
                            f.dispatchEvent("onShowMessage", [s.message || n("errorsavingpreferences"), 5000])
                        }
                    },
                    onFailure: function(s) {
                        f.dispatchEvent("onShowMessage", [(s && s.message) || n("errorsavingpreferences"), 5000])
                    }
                };
                l.request(q)
            },
            saveSectionStatus: function(q) {
                clearTimeout(l.saveSectionTimeout);
                l.saveSectionTimeout = setTimeout(function() {
                    l.request({
                        method: "PUT",
                        url: l.setSectionUrl,
                        contentType: "application/x-www-form-urlencoded",
                        postData: {
                            name: "value",
                            value: q
                        },
                        cache: null
                    })
                }, 1100)
            },
            setPreference: function(r) {
                var q = {
                    url: l.setPreferencesUrl,
                    urlParams: {
                        name: r.name,
                        value: r.value
                    },
                    cache: null,
                    onComplete: function(s) {
                        if (s && s.code === 0) {
                            if (r && r.onComplete) {
                                r.onComplete()
                            }
                        } else {
                            if (r && r.onFailure) {
                                r.onFailure()
                            }
                        }
                    },
                    onFailure: function() {
                        if (r && r.onFailure) {
                            r.onFailure()
                        }
                    }
                };
                l.request(q)
            },
            getCasTgc: function(q) {
                var u = this,
                    x = (e.getOs() === "ios"),
                    r = {
                        timeout: 15000,
                        method: "GET",
                        type: "json",
                        onFailure: function() {
                            q.onComplete()
                        }
                    },
                    s, z = null,
                    v = 5000,
                    y = function(G) {
                        var F = i.encodeUrl(i.buildUrl(window.location, d.getWebappsBaseUrl() + "i3DXCompass/gettgc.html")),
                            A = o.passportUrl.replace(/\/$/, "") + "/",
                            D = i.buildUrl(A, "cas/getcastgc?userid=" + o.userId + "&service=V6&ticket=" + G + "&callback=" + F),
                            C, B = new RegExp("castgc=(.*-cas)");
                        r.type = "text";
                        r.onComplete = function(I) {
                            if (I.data) {
                                I = JSON.parse(I.data);
                                if (I.target === "com.ds.compass") {
                                    s.destroy();
                                    window.removeEventListener("message", r.onComplete);
                                    if (I.castgc === "notgc") {
                                        q.onComplete()
                                    } else {
                                        if (x) {
                                            c = I.castgc
                                        } else {
                                            u.storage.set("casTGC", I.castgc)
                                        }
                                        q.onComplete(I.castgc)
                                    }
                                }
                            } else {
                                if (B.test(I)) {
                                    g.log("CORS request succeeded");
                                    var H = B.exec(I);
                                    if (x) {
                                        c = H[1]
                                    } else {
                                        u.storage.set("casTGC", H[1])
                                    }
                                    q.onComplete(H[1])
                                } else {
                                    q.onComplete()
                                }
                            }
                        };
                        try {
                            k.request(D, r)
                        } catch (E) {
                            g.log("CORS request failed");
                            window.addEventListener("message", function(H) {
                                u.storage.set("casTGC", H);
                                r.onComplete(H)
                            });
                            s = g.createElement("iframe", {
                                src: D,
                                "class": "tgc-frame",
                                styles: {
                                    border: "none",
                                    width: "0px",
                                    height: "0px"
                                }
                            }).inject(l.root)
                        }
                    },
                    t = function() {
                        r.onComplete = function(A) {
                            if (A.result) {
                                y(A.result)
                            } else {
                                q.onComplete()
                            }
                        };
                        k.request(o.proxyTicketUrl + (new Date().getTime()), r)
                    };
                if (!this.storage) {
                    this.storage = new p({
                        adapter: "Object",
                        database: "compass"
                    })
                }
                if (x) {
                    if (c) {
                        q.onComplete(c)
                    } else {
                        try {
                            c = u.storage.get("casTGC", v)
                        } catch (w) {}
                        t()
                    }
                } else {
                    try {
                        z = u.storage.get("casTGC", v)
                    } catch (w) {}
                    if (z) {
                        q.onComplete(z)
                    } else {
                        t()
                    }
                }
            },
            getCasTransientUrl: function(s) {
                var v = {
                        timeout: 15000,
                        method: "GET",
                        type: "json",
                        onFailure: function() {
                            s.onComplete()
                        }
                    },
                    r, q = o.passportUrl.replace(/\/$/, "") + "/",
                    t = i.buildUrl(q, "/api/authenticated/cas/transient");
                v.type = "json";
                v.onComplete = function(x) {
                    var w = "";
                    if (x) {
                        x = JSON.parse(x);
                        if (x.x3ds_reauth_url) {
                            w = x.x3ds_reauth_url
                        }
                    }
                    s.onComplete(w)
                };
                v.onFailure = function() {
                    s.onComplete("")
                };
                try {
                    r = b.getRequest(t, v);
                    r.withCredentials = true;
                    r.send()
                } catch (u) {
                    g.log("CORS FAIL: getTransientUrl");
                    s.onComplete("")
                }
            },
            getCasTgcAndCasTransientUrl: function(r) {
                var u, s, q = 2,
                    t = function() {
                        q--;
                        if (q === 0) {
                            r.onComplete(u, s)
                        }
                    };
                this.getCasTgc({
                    onComplete: function(v) {
                        u = v;
                        t()
                    }
                });
                this.getCasTransientUrl({
                    onComplete: function(v) {
                        s = v;
                        t()
                    }
                })
            },
            _isCorsAvailable: function(q) {
                return q.url.indexOf(l.myAppsBaseUrl) > -1
            }
        };
    return l
});
define("DS/i3DXCompass/DaemonManager", ["UWA/Core", "UWA/Utils", "UWA/Json", "DS/i3DXCompass/EventManager", "DS/i3DXCompass/Data", "DS/i3DXCompass/Tools", "DS/UIKIT/Alert", "DS/UIKIT/Modal", "DS/UIKIT/Input/Text", "DS/UIKIT/Input/Button", "DS/UIKIT/Spinner", "DS/UIKIT/Popover"], function(k, f, d, z, s, e, o, c, w, r, n, x) {
    var l = 103,
        m = 104,
        u = "3DEXPERIENCELauncher.msi",
        a = "isDaemonBusy",
        g = "discoverAllInstall",
        j = "startV6install",
        i = "createShortcutFromV6Install",
        b = "installMedia",
        h = "selfUpdate",
        q = "installCnt",
        B = [g, j, i, b, h],
        p = e.i18n,
        A = null,
        v = "-file",
        y = null,
        t = {
            version: "1.0",
            msi: "0",
            frameId: "16086506-8109-4ac5-82d6-3e2f550fd401",
            ports: {
                local: [20300, 33200, 40600],
                myApps: undefined
            },
            daemonBase: "https://dslauncher.3ds.com",
            defaultFramePort: 4444,
            framePort: null,
            framePath: "{0}:{1}/iframe?version={2}&id={3}#origin:{4}",
            frameOrigin: "{0}:{1}",
            frame: null,
            catEnvConfig: null,
            functionPath: "{0}:{1}/{2}?version={3}&castgc={4}&cryptws={5}&arg={6}",
            functionPathNoCrypt: "{0}:{1}/{2}?version={3}&arg={4}",
            requestPath: "{0}:{1}/{2}",
            origin: window.location.protocol + "//" + window.location.hostname + (window.location.port ? ":" + window.location.port : ""),
            timeout: 5000,
            storageId: "compass-daemon-port",
            dpInfo: null,
            installing: false,
            installed: false,
            init: function(D, E) {
                var C = parseInt(localStorage.getItem(t.storageId), 10),
                    F = t.ports.local;
                if (k.is(C, "number")) {
                    if (F.indexOf(C) === -1) {
                        e.unshiftUnique(F, C)
                    } else {
                        e.moveToFirst(F, C)
                    }
                }
                t.root = D;
                t.cloud = E
            },
            getFramePath: function(C) {
                return k.String.format(t.framePath, t.daemonBase, C, t.version, t.frameId, encodeURIComponent(t.origin))
            },
            getFrameOrigin: function() {
                return k.String.format(t.frameOrigin, t.daemonBase, t.framePort)
            },
            parseMessage: function(C) {
                return C
            },
            cryptArg: function(C) {
                s.request({
                    url: s.encryptUrl,
                    method: "POST",
                    type: "text",
                    Accept: "text/plain",
                    contentType: "application/x-www-form-urlencoded",
                    postData: {
                        value: C.arg
                    },
                    timeout: 600000,
                    onComplete: function(D) {
                        s.getCasTgc({
                            onComplete: function(E) {
                                C.onComplete({
                                    encrypted: D,
                                    tgc: E || ""
                                })
                            }
                        })
                    },
                    onFailure: function(D) {
                        k.log(D);
                        C.onFailure()
                    }
                })
            },
            buildMessage: function(E) {
                var D, G = E.data,
                    F = G.arg.functionName,
                    C;
                if (F === a) {
                    D = k.String.format(t.requestPath, t.daemonBase, t.framePort, G.arg.functionName);
                    E.onComplete({
                        id: G.id,
                        data: D
                    })
                } else {
                    C = d.encode(G.arg);
                    if (B.indexOf(F) === -1) {
                        D = k.String.format(t.functionPathNoCrypt, t.daemonBase, t.framePort, "function", t.version, encodeURIComponent(C));
                        E.onComplete({
                            id: G.id,
                            data: D
                        })
                    } else {
                        t.cryptArg({
                            arg: C,
                            onComplete: function(H) {
                                D = k.String.format(t.functionPath, t.daemonBase, t.framePort, "function", t.version, encodeURIComponent(H.tgc), encodeURIComponent(s.decryptUrl), encodeURIComponent(H.encrypted));
                                E.onComplete({
                                    id: G.id,
                                    data: D
                                })
                            },
                            onFailure: E.onFailure
                        })
                    }
                }
            },
            request: function(D) {
                var H, I, G, E = D.onFailure || function() {},
                    C = function() {
                        if (I) {
                            window.removeEventListener("message", I)
                        }
                    },
                    F = function() {
                        G = f.getUUID();
                        I = function(K) {
                            var J = t.parseMessage(K.data),
                                M = J.id,
                                L = J.data;
                            if (M === G) {
                                C();
                                if (L.returnCode === 0) {
                                    D.onComplete(L.content)
                                } else {
                                    if (L.returnCode === l || L.returnCode === m) {
                                        t.showUpgradeModal({
                                            onComplete: function() {
                                                t.request(D)
                                            },
                                            onFailure: E
                                        })
                                    } else {
                                        E({
                                            returnCode: L.returnCode,
                                            commentary: L.commentary
                                        })
                                    }
                                }
                            }
                        };
                        window.addEventListener("message", I);
                        H = {
                            id: G,
                            arg: {
                                functionName: D.functionName,
                                params: D.params || {}
                            }
                        };
                        t.buildMessage({
                            data: H,
                            onComplete: function(J) {
                                k.log(J);
                                t.frame.contentWindow.postMessage(J, t.getFrameOrigin())
                            },
                            onFailure: E
                        });
                        k.log(H)
                    };
                if (!t.installing) {
                    if (t.installed) {
                        F()
                    } else {
                        t.installDaemon({
                            onComplete: function() {
                                F()
                            },
                            onFailure: E,
                            silent: D.silent
                        })
                    }
                }
            },
            installDaemon: function(E) {
                k.log("install daemon");
                var C = 0,
                    G = t.ports.local,
                    D = G[C],
                    F = function() {
                        if (E.silent !== true) {
                            t.getDpInfo({
                                onComplete: function(I) {
                                    t.showModal(E, I.url)
                                }
                            })
                        } else {
                            k.log("launchApp event: silent mode is activated");
                            t.installing = false
                        }
                    },
                    H = {
                        onComplete: E.onComplete,
                        onFailure: E.onFailure,
                        onPortFailure: function() {
                            var I = t.ports.myApps;
                            C++;
                            if (C < G.length) {
                                D = G[C];
                                t.installDaemonOnPort(H, D)
                            } else {
                                if (C === G.length) {
                                    if (k.is(I, "number")) {
                                        t.installDaemonOnPort(H, I)
                                    } else {
                                        if (I === null) {
                                            F()
                                        } else {
                                            s.request({
                                                url: s.getPreferencesUrl,
                                                type: "text",
                                                urlParams: {
                                                    name: t.storageId
                                                },
                                                onComplete: function(K) {
                                                    var J = parseInt(K, 10);
                                                    if (k.is(J, "number") && G.indexOf(J) === -1) {
                                                        I = t.ports.myApps = J;
                                                        t.installDaemonOnPort(H, I)
                                                    } else {
                                                        t.ports.myApps = null;
                                                        F()
                                                    }
                                                },
                                                onFailure: function() {
                                                    F()
                                                }
                                            })
                                        }
                                    }
                                } else {
                                    F()
                                }
                            }
                        }
                    };
                t.installDaemonOnPort(H, D)
            },
            installDaemonOnPort: function(E, D, C) {
                var H, G, F;
                t.installing = true;
                F = function(J) {
                    var K = t.parseMessage(J.data),
                        L, I;
                    if (K.id === t.frameId) {
                        k.log("iframe loaded");
                        window.removeEventListener("message", F);
                        window.clearTimeout(G);
                        if (J.origin) {
                            L = J.origin.split(":");
                            if (L.length >= 2) {
                                I = parseInt(L[2], 10)
                            }
                            if (I !== D) {
                                t.frame.src = t.getFramePath(I);
                                D = I
                            }
                        }
                        t.framePort = D;
                        localStorage.setItem(t.storageId, t.framePort);
                        if (C) {
                            s.request({
                                url: s.setPreferencesUrl,
                                urlParams: {
                                    name: t.storageId,
                                    value: t.framePort
                                }
                            })
                        }
                        t.installing = false;
                        t.installed = true;
                        k.log("Daemon MSI version: " + K.data.msi);
                        k.log("Daemon version: " + K.data.daemon);
                        t.msi = K.data.msi || t.msi;
                        t.version = K.data.daemon || t.version;
                        t.getDpInfo({
                            onComplete: function(N) {
                                var M = N.version,
                                    O = N.options ? (N.options && N.options.forceUpdate !== true) : true;
                                if (M === null || e.compareVersion(t.msi, M) >= 0) {
                                    E.onComplete()
                                } else {
                                    if (E.onOutdated) {
                                        E.onOutdated();
                                        E.onOutdated = undefined
                                    }
                                    if (K.data.daemon && e.compareVersion(t.version, "2.0") >= 0) {
                                        t.showSelfUpdateModal(E, O)
                                    } else {
                                        t.showUpgradeModal(E, true)
                                    }
                                }
                                k.log("DP version: " + M)
                            }
                        })
                    }
                };
                window.addEventListener("message", F);
                G = window.setTimeout(function() {
                    k.log("frame not loaded");
                    window.removeEventListener("message", F);
                    H.destroy();
                    E.onPortFailure()
                }, t.timeout);
                H = t.frame = k.createElement("iframe", {
                    src: t.getFramePath(D),
                    "class": "daemon-frame",
                    styles: {
                        border: "none",
                        width: "0px",
                        height: "0px"
                    }
                }).inject(t.root)
            },
            uninstallDaemon: function() {
                t.frame.destroy();
                t.installed = false
            },
            showModal: function(E, G) {
                var O = E.onComplete,
                    P = E.onFailure,
                    K = new r({
                        value: p("continue"),
                        disabled: true,
                        events: {
                            onClick: function() {
                                var U = parseInt(T.getValue(), 10);
                                if (k.is(U, "number")) {
                                    L.show();
                                    E.onComplete = function() {
                                        M.hide();
                                        O()
                                    };
                                    E.onPortFailure = function() {
                                        M.getContent().getElement(".modal-content").addClassName("failure");
                                        F.setStyle("height", N.getSize().height + 16);
                                        L.hide()
                                    };
                                    E.onOutdated = function() {
                                        M.hide()
                                    };
                                    t.installDaemonOnPort(E, U, true)
                                } else {
                                    E.onComplete = O;
                                    E.onFailure = P;
                                    E.onOutdated = undefined;
                                    z.dispatchEvent("onShowMessage", ["", 30000, true]);
                                    M.hide();
                                    t.installDaemon(E)
                                }
                            }
                        }
                    }),
                    T = new w({
                        value: t.framePort,
                        className: "daemon-port-input",
                        events: {
                            onChange: function() {
                                K.enable();
                                K.setClassName("primary")
                            },
                            onClick: function() {
                                K.enable();
                                K.setClassName("primary")
                            },
                            onKeyDown: function() {
                                K.enable();
                                K.setClassName("primary")
                            }
                        }
                    }),
                    L = k.createElement("div", {
                        "class": "compass-loading",
                        styles: {
                            display: "none"
                        },
                        html: new n({
                            visible: true,
                            className: "compass-spinner"
                        })
                    }),
                    J = k.createElement("div", {
                        tag: "span",
                        "class": "fonticon fonticon-info-circled"
                    }),
                    C, R = k.createElement("div", {
                        tag: "span",
                        "class": "fonticon fonticon-info-circled"
                    }),
                    H, Q = "large block download-daemon-btn",
                    I = G ? k.createElement("div", {
                        "class": "download-wrapper",
                        html: [{
                            tag: "div",
                            "class": "compass-java-desc",
                            html: p("launcherfirstvisit")
                        }, {
                            tag: "ul",
                            html: [{
                                tag: "li",
                                "class": "compass-java-desc",
                                text: p("downloadlauncherclicking")
                            }, new r({
                                value: e.htmlEntityDecode(p("downloadlauncher")),
                                className: "primary " + Q,
                                events: {
                                    onClick: function() {
                                        window.open(G);
                                        this.disable();
                                        this.setClassName("default " + Q);
                                        K.enable();
                                        K.setClassName("primary")
                                    }
                                }
                            }), {
                                tag: "li",
                                "class": "compass-java-desc",
                                text: k.String.format(p("installlauncherrunning"), u)
                            }, {
                                tag: "li",
                                "class": "compass-java-desc",
                                html: [k.String.format(p("clickcontinue"), "<b>", "</b>"), {
                                    text: p("youcaninstallrun")
                                }]
                            }]
                        }]
                    }) : k.createElement("div", {
                        "class": "compass-java-desc",
                        html: p("downloadlauncheradmin")
                    }),
                    S = k.createElement("div", {
                        "class": "more-wrapper",
                        html: [{
                            tag: "div",
                            "class": "compass-java-desc",
                            html: p("alreadyinstalledlauncher")
                        }, {
                            tag: "ul",
                            html: [{
                                tag: "li",
                                "class": "compass-java-desc",
                                html: [p("launchernotstarted"), J]
                            }, {
                                tag: "li",
                                "class": "compass-java-desc",
                                html: [p("portchanged"), {
                                    "class": "change-port",
                                    html: [{
                                        tag: "span",
                                        text: p("changeport")
                                    }, T, R]
                                }]
                            }]
                        }]
                    }),
                    D = k.createElement("div", {
                        "class": "more-container",
                        styles: {
                            height: 0
                        },
                        html: S
                    }),
                    N = k.createElement("div", {
                        "class": "compass-java-desc alert-message alert-error",
                        html: [p("portunreachable")]
                    }),
                    F = k.createElement("div", {
                        "class": "alert-wrapper",
                        html: N
                    }),
                    M = new c({
                        className: "compass-modal daemon-wrapper-modal",
                        closable: true,
                        visible: true,
                        events: {
                            onHide: function() {
                                t.installing = false;
                                M.destroy()
                            }
                        },
                        header: "<h4>" + p("welcomelauncher") + "</h4>",
                        body: k.createElement("div", {
                            "class": "daemon-modal",
                            html: [F, I, {
                                "class": "divider"
                            }, new r({
                                value: p("troubleshootbtn"),
                                icon: "right-dir",
                                className: "link",
                                events: {
                                    onClick: function() {
                                        this.setFocus(false);
                                        D.toggleClassName("open");
                                        if (D.hasClassName("open")) {
                                            this.setIcon("down-dir");
                                            D.setStyle("height", S.getSize().height + 16)
                                        } else {
                                            this.setIcon("right-dir");
                                            D.setStyle("height", "")
                                        }
                                    }
                                }
                            }), D, L]
                        }),
                        footer: [K, {
                            tag: "button",
                            "class": "btn btn-default",
                            text: p("cancel"),
                            events: {
                                click: function() {
                                    M.hide()
                                }
                            }
                        }]
                    }).inject(document.body);
                C = new x({
                    target: J,
                    className: "compass-popover list-style-type-disc",
                    position: "right",
                    trigger: "hover",
                    body: [{
                        tag: "li",
                        html: k.String.format(p("checksystrayicon"), '<span class="compass-systray"></span>')
                    }, {
                        tag: "li",
                        html: p("dontseelaunchermenu")
                    }]
                });
                H = new x({
                    target: R,
                    className: "compass-popover list-style-type-decimal",
                    position: "right",
                    trigger: "hover",
                    body: {
                        tag: "ul",
                        html: [{
                            tag: "li",
                            html: k.String.format(p("clicksystrayicon"), '<span class="compass-systray"></span>')
                        }, {
                            tag: "li",
                            text: p("copyportnumber")
                        }, {
                            tag: "li",
                            text: p("pasteportnumber")
                        }, {
                            tag: "li",
                            html: k.String.format(p("clickcontinue"), "<b>", "</b>")
                        }]
                    }
                });
                z.dispatchEvent("onHideMessage")
            },
            showUpgradeModal: function(G, H) {
                var E = false,
                    I = false,
                    D = t.dpInfo ? t.dpInfo.url : null,
                    F = "large block download-daemon-btn",
                    C = new r({
                        value: p("continue"),
                        disabled: true,
                        events: {
                            onClick: function() {
                                E = true;
                                z.dispatchEvent("onShowMessage", ["", 30000, true]);
                                J.hide()
                            }
                        }
                    }),
                    J = new c({
                        className: "compass-modal",
                        closable: true,
                        visible: true,
                        events: {
                            onHide: function() {
                                J.destroy();
                                if (E) {
                                    if (I) {
                                        G.onComplete()
                                    } else {
                                        t.uninstallDaemon();
                                        t.installDaemon(G)
                                    }
                                } else {
                                    t.uninstallDaemon()
                                }
                            }
                        },
                        header: "<h4>" + p("launcherupdate") + "</h4>",
                        body: k.createElement("div", {
                            "class": "daemon-modal",
                            html: D ? [{
                                tag: "div",
                                "class": "compass-java-desc",
                                html: k.String.format(p("updateinfos"), "", "", t.dpInfo.version, (t.dpInfo.size / 1000000).toFixed(2))
                            }, {
                                tag: "div",
                                "class": "compass-java-desc",
                                text: "1. " + p("downloadupdateclicking")
                            }, new r({
                                value: e.htmlEntityDecode(p("downloadlauncher")),
                                className: "primary " + F,
                                events: {
                                    onClick: function() {
                                        window.open(D);
                                        this.disable();
                                        this.setClassName("default " + F);
                                        C.enable();
                                        C.setClassName("primary")
                                    }
                                }
                            }), {
                                tag: "div",
                                "class": "compass-java-desc",
                                text: k.String.format("2. " + p("installupdaterunning"), u)
                            }, {
                                tag: "div",
                                "class": "compass-java-desc",
                                html: k.String.format("3. " + p("clickcontinue"), "<b>", "</b>")
                            }] : {
                                tag: "div",
                                "class": "compass-java-desc",
                                text: p("updatelauncheradmin")
                            }
                        }),
                        footer: D ? [C, H ? new r({
                            value: p("proceedanyway"),
                            events: {
                                onClick: function() {
                                    E = true;
                                    I = true;
                                    z.dispatchEvent("onShowMessage", ["", 30000, true]);
                                    J.hide()
                                }
                            }
                        }) : null] : null
                    }).inject(document.body);
                z.dispatchEvent("onHideMessage")
            },
            showSelfUpdateModal: function(K, D) {
                var C = false,
                    H = false,
                    I = t.dpInfo ? t.dpInfo.url : null,
                    F = "large block download-daemon-btn",
                    G = new r({
                        value: p("continue"),
                        disabled: true,
                        events: {
                            onClick: function() {
                                C = true;
                                z.dispatchEvent("onShowMessage", ["", 30000, true]);
                                J.hide()
                            }
                        }
                    }),
                    E = new r({
                        value: p("proceedanyway"),
                        events: {
                            onClick: function() {
                                C = true;
                                H = true;
                                z.dispatchEvent("onShowMessage", ["", 30000, true]);
                                J.hide()
                            }
                        }
                    }),
                    J = new c({
                        className: "compass-modal",
                        closable: true,
                        visible: true,
                        events: {
                            onHide: function() {
                                J.destroy();
                                if (C) {
                                    if (H) {
                                        K.onComplete()
                                    } else {
                                        t.uninstallDaemon();
                                        t.installDaemon(K)
                                    }
                                } else {
                                    t.uninstallDaemon()
                                }
                            }
                        },
                        header: "<h4>" + p("launcherupdate") + "</h4>",
                        body: k.createElement("div", {
                            "class": "daemon-modal",
                            html: I ? [{
                                tag: "div",
                                "class": "compass-java-desc",
                                html: k.String.format(p("updateinfos"), '<a href="' + t.dpInfo.url + '">', "</a>", t.dpInfo.version, (t.dpInfo.size / 1000000).toFixed(2))
                            }, {
                                tag: "div",
                                "class": "compass-java-desc",
                                text: "1. " + p("startselfupdateclicking")
                            }, new r({
                                value: p("startselfupdate"),
                                className: "primary " + F,
                                events: {
                                    onClick: function() {
                                        var L = this;
                                        E.disable();
                                        t.selfUpdate({
                                            onComplete: function() {
                                                G.enable();
                                                G.setClassName("primary")
                                            },
                                            onFailure: function(M) {
                                                k.log("Self update failed with code " + M.returnCode + " (" + M.commentary + ")");
                                                new o({
                                                    visible: true
                                                }).inject(J.getBody(), "top").add({
                                                    className: "error",
                                                    message: p("selfupdateerror")
                                                });
                                                L.enable();
                                                L.setClassName("primary " + F)
                                            }
                                        });
                                        this.disable();
                                        this.setClassName("default " + F)
                                    }
                                }
                            }), {
                                tag: "div",
                                "class": "compass-java-desc",
                                text: "2. " + p("selfupdaterunning")
                            }, {
                                tag: "div",
                                "class": "compass-java-desc",
                                html: k.String.format("3. " + p("clickcontinue"), "<b>", "</b>")
                            }] : {
                                tag: "div",
                                "class": "compass-java-desc",
                                text: p("updatelauncheradmin")
                            }
                        }),
                        footer: I ? [G, D ? E : null] : null
                    }).inject(document.body);
                z.dispatchEvent("onHideMessage")
            },
            selfUpdate: function(C) {
                C.functionName = h;
                if (!C.params) {
                    C.params = {}
                }
                C.params.url = t.dpInfo.url;
                C.params.hash = t.dpInfo.md5;
                C.params.size = t.dpInfo.size;
                t.request(C)
            },
            discoverAllInstall: function(C) {
                C.functionName = g;
                t.request(C)
            },
            startV6install: function(C) {
                C.functionName = j;
                if (!C.params) {
                    C.params = {}
                }
                C.params.fileArgStrings = [];
                if (A) {
                    C.params.fileArgStrings.push({
                        name: v,
                        content: A
                    });
                    this._addOpenFromWebArg(C)
                }
                if (C.file && C.file.name && C.file.content) {
                    C.params.fileArgStrings.push({
                        name: C.file.name,
                        content: C.file.content
                    });
                    this._addOpenFromWebArg(C)
                }
                k.log(C.params.fileArgStrings);
                k.merge(C.params, t._getCatEnvInformation(C.params));
                t.request(C)
            },
            _getCatEnvInformation: function(F) {
                var C = {};
                if (t.catEnvConfig) {
                    var D = F.argString.match(/-tenant=([^ ]+)/),
                        E = null;
                    if (D && D.length > 1) {
                        E = D[1];
                        C = t.catEnvConfig.detect(function(G) {
                            return G.id === E
                        })
                    } else {
                        if (t.catEnvConfig.length > 0) {
                            C = t.catEnvConfig[0]
                        }
                    }
                    if (C) {
                        C = {
                            dirEnv: C.direnv,
                            catEnvFileName: C.env
                        }
                    }
                }
                return C
            },
            _addOpenFromWebArg: function(C) {
                if (C && C.params && C.params.argString) {
                    C.params.argString += " -e CATUNILoadFromWebCmdHdr Open"
                }
            },
            createV6Shortcut: function(G, I) {
                var E = I.get("icon"),
                    J = e.isBase64Image(E) ? E : E.substring(E.lastIndexOf("/") + 1, E.lastIndexOf(".")),
                    D = " - " + G.get("name"),
                    C, H = e.htmlEntityDecode(I.get("title")),
                    F = I.get("tooltip") ? e.htmlEntityDecode(I.get("tooltip")) : H;
                z.dispatchEvent("onShowMessage", k.String.format(p("creatingshortcut"), F));
                C = {
                    functionName: i,
                    params: {
                        instID: G.get("daemonId"),
                        exeName: I.get("exeName"),
                        argString: I.getShortcutLaunchInfos(),
                        scName: H + D,
                        iconRelPath: J,
                        scDesc: F + D,
                        nativeLauncherName: I.get("launcher")
                    },
                    onComplete: function() {
                        z.dispatchEvent("onShowMessage", p("shortcutcreated"));
                        z.dispatchEvent("onHideMessage", 5000)
                    },
                    onFailure: function(K) {
                        k.log(K);
                        z.dispatchEvent("onShowMessage", p("shortcutcreatingerror"));
                        z.dispatchEvent("onHideMessage", 5000)
                    }
                };
                k.merge(C.params, t._getCatEnvInformation(C.params));
                t.request(C)
            },
            installMedia: function(C) {
                C.functionName = b;
                t.request(C)
            },
            isDaemonBusy: function(C) {
                C.functionName = a;
                t.request(C)
            },
            getDpInfo: function(C) {
                if (!t.cloud) {
                    C.onComplete({
                        version: null
                    })
                } else {
                    if (t.dpInfo) {
                        C.onComplete(t.dpInfo)
                    } else {
                        s.request({
                            url: s.onlineInstallUrl,
                            proxy: "ajax",
                            method: "POST",
                            contentType: "application/json",
                            postData: JSON.stringify({
                                id: (new Date().getTime()),
                                method: "ResourcesV2::check",
                                params: {
                                    type: u,
                                    refine: s.buildTransactionId
                                }
                            }),
                            onComplete: function(D) {
                                t.dpInfo = D.result;
                                C.onComplete(t.dpInfo)
                            },
                            onFailure: function() {
                                C.onComplete({
                                    version: null
                                })
                            }
                        })
                    }
                }
            },
            setStructure: function(C) {
                A = C
            },
            resetStructure: function() {
                A = null
            },
            setLauncherConfig: function(D) {
                var E = D.split(":"),
                    C = parseInt(E[E.length - 1], 10);
                if (C) {
                    localStorage.setItem(t.storageId, C);
                    D = D.replace(":" + C, "")
                }
                t.daemonBase = D
            },
            setCatEnvConfig: function(C) {
                t.catEnvConfig = C
            }
        };
    return t
});
define("DS/i3DXCompass/Controls/Compass", ["UWA/Core", "UWA/Event", "UWA/Utils/Client", "UWA/Controls/Abstract", "DS/i3DXCompass/Tools", "DS/UIKIT/Tooltip", "DS/i3DXCompass/UsageTracker", "DS/i3DXCompass/Data"], function(c, i, a, d, b, e, h, f) {
    var g = {
        defaultOptions: {
            defaultQuadrant: "north",
            useQuadrantsOnTouch: true
        },
        slideTimeout: 0,
        quadrantNames: {
            north: "",
            east: "",
            south: "",
            west: "",
            play: ""
        },
        offsets: {
            north: {
                x: 32,
                y: 20
            },
            east: {
                x: 64,
                y: 32
            },
            south: {
                x: 32,
                y: 64
            },
            west: {
                x: 20,
                y: 32
            },
            play: {
                x: 40,
                y: 40
            }
        },
        tooltipsOffsets: {
            north: [{
                x: 14,
                y: -23
            }, {
                x: -12,
                y: -20
            }],
            east: [{
                x: 30,
                y: 0
            }, {
                x: 15,
                y: 15
            }],
            south: [{
                x: 14,
                y: 23
            }, {
                x: -12,
                y: 32
            }],
            west: [{
                x: -20,
                y: 0
            }, {
                x: -37,
                y: 17
            }],
            play: [{
                x: 14,
                y: 0
            }, {
                x: 0,
                y: 5
            }]
        },
        quadrantAction: "Click",
        currentQuadrant: null,
        highlightedQuadrant: null,
        open: false,
        closable: true,
        typeCompassView: "compassBase",
        init: function(j) {
            this._parent(j);
            if (c.is(j.closable)) {
                this.closable = j.closable
            }
            this.buildSkeleton()
        },
        buildSkeleton: function() {
            var k = this.elements = {},
                j = this;
            k.compassSmallOver = c.createElement("div", {
                "class": "compass-small-over"
            });
            k.compassSmall = c.createElement("div", {
                "class": "compass-small",
                html: [k.compassSmallOver],
                events: {
                    click: function(l) {
                        j.dispatchEvent("onCompassSmallClick", [l, a.Features.touchEvents])
                    },
                    mousemove: function(l) {
                        j.dispatchEvent("onCompassSmallOver", [l])
                    },
                    mouseout: function() {
                        j.dispatchEvent("onCompassSmallOut")
                    }
                }
            });
            k.compassOver = c.createElement("div", {
                "class": "compass-over"
            });
            k.compassOn = c.createElement("div", {
                "class": "compass-on"
            });
            k.compassBase = c.createElement("div", {
                "class": "compass-base",
                html: [k.compassOver, k.compassOn],
                events: {
                    click: function(l) {
                        j.dispatchEvent("onCompassBaseClick", [l])
                    },
                    mousemove: function(l) {
                        j.dispatchEvent("onCompassBaseOver", [l])
                    },
                    mouseout: function() {
                        j.dispatchEvent("onCompassBaseOut")
                    },
                    transitionEnd: j.updateOpenState.bind(j)
                }
            });
            k.tooltip = new e({
                target: k.compassSmall,
                position: "right",
                className: "compass-small-tooltip"
            });
            this.container = k.container = c.createElement("div", {
                "class": "compass-ct",
                html: [k.compassSmall, k.compassBase]
            })
        },
        getQuadrant: function(r, q, k, j, n, s) {
            var o = r - q > 0,
                m = r + q - s > 0,
                l = s / 2,
                p = (r - l) * (r - l) + (q - l) * (q - l);
            if (p < j) {
                if (p < k) {
                    return "play"
                }
                return null
            }
            if (p > n) {
                return null
            }
            if (o) {
                if (m) {
                    return "east"
                }
                return "north"
            }
            if (m) {
                return "south"
            }
            return "west"
        },
        onCompassSmallClick: function(m, n) {
            var j, l, k;
            if (b.isSmartphone()) {
                this.switchCompassView(false)
            } else {
                if (n && !this.options.useQuadrantsOnTouch) {
                    this.clickQuadrant(this.currentQuadrant || this.options.defaultQuadrant)
                } else {
                    c.log("click compass: clientX " + m.clientX + "; clientY " + m.clientY);
                    j = i.getPosition(m);
                    l = this.elements.compassSmall.getOffsets();
                    k = this.getQuadrant(j.x - l.x, j.y - l.y, 256, 256, 1024, 64);
                    if (k) {
                        this.quadrantAction = "Open";
                        if (k === "play") {
                            this.clickQuadrant(this.currentQuadrant || this.options.defaultQuadrant)
                        } else {
                            this.clickQuadrant(k)
                        }
                    }
                }
            }
        },
        onCompassBaseClick: function(m) {
            var j, l, k;
            j = i.getPosition(m);
            l = this.elements.compassBase.getOffsets();
            k = k = this.getQuadrant(j.x - l.x, j.y - l.y, 0, 1378, 3844, 124);
            if (k) {
                this.quadrantAction = "Click";
                this.clickQuadrant(k)
            }
        },
        onCompassSmallOver: function(q) {
            if (!b.isSmartphone()) {
                var j, l, k, o = this.elements,
                    n = this.elements.tooltip,
                    m = o.compassSmallOver,
                    p = o.compassSmall;
                j = i.getPosition(q);
                l = this.elements.compassSmall.getOffsets();
                k = k = this.getQuadrant(j.x - l.x, j.y - l.y, 256, 256, 1024, 64);
                if (k !== this.highlightedQuadrant) {
                    this.highlightedQuadrant = k;
                    m.className = "compass-small-over";
                    if (k) {
                        n.hide();
                        this.showTooltip(k);
                        m.addClassName(k);
                        p.setStyle("cursor", "")
                    } else {
                        p.setStyle("cursor", "default")
                    }
                }
            }
        },
        onCompassSmallOut: function() {
            if (!b.isSmartphone()) {
                this.elements.compassSmallOver.className = "compass-small-over";
                this.highlightedQuadrant = null
            }
        },
        onCompassBaseOver: function(p) {
            var j, n, k, o = this.elements,
                m = o.compassOver,
                l = o.compassBase;
            j = i.getPosition(p);
            n = this.elements.compassBase.getOffsets();
            k = k = this.getQuadrant(j.x - n.x, j.y - n.y, 0, 1378, 3844, 124);
            if (k !== this.highlightedQuadrant) {
                this.highlightedQuadrant = k;
                m.className = "compass-over";
                if (k) {
                    m.addClassName(k);
                    l.setStyle("cursor", "")
                } else {
                    l.setStyle("cursor", "default")
                }
            }
        },
        onCompassBaseOut: function() {
            this.elements.compassOver.className = "compass-over";
            this.highlightedQuadrant = null
        },
        clickQuadrant: function(j) {
            if (j === this.currentQuadrant && this.open && this.closable) {
                if (c.is(f.addinMode) === false) {
                    this.dispatchEvent("onClose");
                    this.quadrantAction = "Close"
                }
            } else {
                this.currentQuadrant = j;
                this.dispatchEvent("onOpenQuadrant", j)
            }
            h.inc(h._IDS.compass["quadrant"][j], this.quadrantAction)
        },
        onClose: function() {
            this.open = false;
            this.elements.compassBase.removeClassName("animate");
            if ((b.isSmartphone() && document.body.getElement(".my-apps-panel").hasClassName("onSmartphone"))) {
                this.typeCompassView = "compassSmall"
            } else {
                this.typeCompassView = "compassBase"
            }
        },
        onOpenQuadrant: function(j) {
            var k = this,
                l = b.getOs();
            if (!this.open) {
                this.open = true;
                if (b.isSmartphone()) {
                    if (this.typeCompassView === "compassSmall") {
                        setTimeout(function() {
                            k.switchCompassView(true)
                        }, 300)
                    } else {
                        setTimeout(function() {
                            k.switchCompassView(false)
                        }, 300)
                    }
                } else {
                    this.container.addClassName("open");
                    window.setTimeout(function() {
                        k.elements.compassBase.addClassName("animate")
                    }, 50)
                }
            }
            this.elements.compassOn.className = "compass-on " + j
        },
        updateOpenState: function() {
            if (!this.open) {
                this.container.removeClassName("open")
            }
        },
        setQuadrantNames: function(j) {
            var k = this.quadrantNames;
            k.north = j.north;
            k.east = j.east;
            k.south = j.south;
            k.west = j.west;
            k.play = j.play
        },
        _getQuadrantRelatedOffset: function(m, n, l, k) {
            var j = this.tooltipsOffsets && this.tooltipsOffsets[m][k];
            if (j) {
                return {
                    x: j.x + n,
                    y: j.y + l
                }
            }
        },
        showTooltip: function(j) {
            var r, q, p, l, o, m, k, n;
            if (this.quadrantNames[j]) {
                q = this.elements.compassSmall.getSize();
                k = q.height / 2;
                r = this.elements.tooltip;
                r.setBody(this.quadrantNames[j]);
                n = r.elements.container.getSize().height / 25;
                p = n > 1 && ((j !== "east" && j !== "west") || n >= 2) ? 1 : 0;
                l = p === 1 ? "bottom bottom-left" : "right";
                o = p === 1 ? k : -k;
                m = p === 1 ? -k : 0;
                r.options.offset = this._getQuadrantRelatedOffset(j, o, m, p);
                if (this.quadrantNames[j] !== "") {
                    r.show();
                    r.updatePosition(l)
                }
            }
        },
        switchCompassView: function(j) {
            var m = this,
                k = document.body.getElement(".my-apps-panel"),
                l = this.getContent();
            clearTimeout(m.slideTimeout);
            if (j) {
                k.removeClassName("onSmartphoneCompassOpen");
                k.addClassName("onSmartphone");
                m.elements.compassSmallOver.className = "compass-small-over " + (m.currentQuadrant || m.defaultOptions.defaultQuadrant);
                m.slideTimeout = setTimeout(function() {
                    l.removeClassName("open");
                    l.addClassName("openOnSmartphone")
                }, 150);
                k.removeEvent("click", m.switchCompassView)
            } else {
                k.addEvent("click", m.switchCompassView.bind(m));
                k.removeClassName("onSmartphone");
                k.addClassName("onSmartphoneCompassOpen");
                m.slideTimeout = setTimeout(function() {
                    l.removeClassName("openOnSmartphone");
                    if (!m.elements.compassBase.hasClassName("animate")) {
                        m.elements.compassBase.addClassName("animate")
                    }
                    l.addClassName("open")
                }, 150);
                this.dispatchEvent("onUnScroll")
            }
        }
    };
    return d.extend(g)
});
define("DS/i3DXCompass/Collection/SuggestionList", ["UWA/Core", "UWA/Class/Collection", "DS/i3DXCompass/Data", "DS/i3DXCompass/Model/Suggestion"], function(e, a, c, d) {
    var b = {
        model: d,
        setup: function() {
            this.added = []
        },
        sync: function(i, h, f) {
            var g;
            switch (i) {
                case "read":
                    g = c.request({
                        url: c.getSuggestionsUrl,
                        onComplete: function(j) {
                            if (j && j.code === 0) {
                                if (f && f.onComplete) {
                                    f.onComplete(j)
                                }
                            } else {
                                if (f && f.onFailure) {
                                    f.onFailure(j)
                                }
                            }
                        },
                        onFailure: function(j) {
                            if (f && f.onFailure) {
                                f.onFailure(j)
                            }
                        }
                    });
                    break
            }
            return g
        },
        parse: function(f) {
            return f.suggestions
        },
        set: function() {
            this.added.length = 0;
            this._parent.apply(this, arguments);
            if (this.added.length > 0) {
                this.dispatchEvent("onMultipleAdd", [this.added])
            }
        },
        onAdd: function(f) {
            this.added.push(f)
        }
    };
    return a.extend(b)
});
define("DS/i3DXCompass/Collection/InstallList", ["UWA/Core", "UWA/Class/Collection", "DS/i3DXCompass/Model/Install", "DS/i3DXCompass/DaemonManager", "DS/i3DXCompass/EventManager", "DS/i3DXCompass/Data"], function(h, d, c, a, b, g) {
    var f = h.i18n,
        e = d.extend({
            model: c,
            parse: function(k) {
                var j = this,
                    i = [];
                k.forEach(function(l) {
                    var m;
                    if (l.lastBuildId && l.type === "CODE" && j.isAvailableForCurrentEnvironment(l)) {
                        l.processes = (l.installedProcesses || []).map(function(n) {
                            return n.split(".")[0]
                        });
                        m = l.processes[0];
                        l.dpId = l.lastBuildId + "+" + m;
                        l.dpIdObj = {
                            ID: l.lastBuildId,
                            process: m,
                            golden: (l.SPlevel || "").indexOf(".") === -1
                        };
                        l.name = (l.longName || l.GAlevel) + (l.registry_Identifier && (l.registry_Identifier !== "!") ? " - " + l.registry_Identifier : "");
                        l.daemonId = l.id;
                        l.id = l.GAlevel + l.installationType + (l.registry_Identifier || "");
                        i.push(l)
                    }
                });
                return i
            },
            isAvailableForCurrentEnvironment: function(l) {
                var k = true,
                    i = g.buildTransactionId && g.buildTransactionId.split("-")[0],
                    j = l.installationType.indexOf("_Cloud") > -1;
                if ((i >= "R421" || i === "") && l.installationType.toLowerCase().indexOf("desktop") > -1) {
                    k = g.cloud ? j : !j
                }
                return k
            },
            sync: function(k, j, i) {
                switch (k) {
                    case "read":
                        a.discoverAllInstall({
                            onComplete: function(l) {
                                if (i && i.onComplete) {
                                    i.onComplete(l)
                                }
                            },
                            onFailure: function() {
                                b.dispatchEvent("onShowMessage", [f("unableinstalldata"), 5000]);
                                if (i && i.onFailure) {
                                    i.onFailure()
                                }
                            },
                            silent: i._silent
                        });
                        break
                }
            }
        });
    return e
});
define("DS/i3DXCompass/ServiceApi/UserServiceApi", ["UWA/Core", "UWA/Utils", "DS/i3DXCompass/Data"], function(e, a, i) {
    var c = 3600000,
        f = "api/pull/self",
        d, b = false,
        h = false,
        g, p, r = [],
        k = function() {
            return i.myAppsBaseUrl ? i.myAppsBaseUrl + f : null
        },
        l = function(s) {
            r.forEach(function(t) {
                if (t[s]) {
                    setTimeout(t[s], 0, d)
                }
            });
            r.length = 0
        },
        j = function() {
            h = true;
            i.request({
                url: k(),
                headers: {
                    "Last-Modified": p
                },
                onComplete: function(s, t) {
                    d = s;
                    b = true;
                    if (t["Last-Modified"]) {}
                    l("onComplete");
                    h = false
                },
                onFailure: function() {
                    if (d) {
                        l("onComplete")
                    } else {
                        l("onFailure")
                    }
                    h = false
                }
            })
        },
        o = function() {
            var s;
            if (!h) {
                if (k()) {
                    j()
                } else {
                    s = setInterval(function() {
                        if (k()) {
                            clearInterval(s);
                            j()
                        } else {
                            e.log("wait for myapps url")
                        }
                    }, 100)
                }
            }
        },
        m = function(t) {
            var s = Date.now();
            if (b && (!g || (s - g < c))) {
                t.onComplete(d)
            } else {
                g = s;
                r.push({
                    onComplete: t.onComplete,
                    onFailure: t.onFailure
                });
                o()
            }
        },
        n = function() {
            d = undefined;
            b = false;
            h = false
        };
    var q = {
        getUser: function(s) {
            if (!s || e.typeOf(s.onComplete) !== "function") {
                return
            }
            m({
                onComplete: function(t) {
                    var u = s.platformId;
                    t = e.clone(t);
                    if (s.platformId) {
                        t.platform = e.Array.detect(t.platforms, function(v) {
                            return v.id === u
                        });
                        delete t.platforms;
                        if (t.platform) {
                            t.admin = t.platform.role === "admin"
                        } else {
                            t.admin = false;
                            if (s.onFailure) {
                                return s.onFailure()
                            }
                        }
                    }
                    s.onComplete(t)
                },
                onFailure: s.onFailure
            })
        },
        _private: {
            callbacks: r,
            fireCallbacks: l,
            reset: n
        }
    };
    return q
});
define("DS/i3DXCompass/View/ServicesView", ["UWA/Core", "UWA/Utils", "UWA/Class/View", "DS/UIKIT/Tooltip", "DS/i3DXCompass/Tools", "DS/i3DXCompass/Data", "DS/i3DXCompass/X3DContent"], function(e, f, b, g, d, h, c) {
    var a = "service";
    var i = {
        className: a + "-section",
        _onClick: function(k) {
            var j = k.get("url");
            if (false && k.get("id") === "X3DPSLY_AP" && e.is(h.addinMode) === true) {
                j = this._manageAddinUrl(j)
            }
            if (d.isSameApp(j, window.location)) {
                window.open(j, "_self")
            } else {
                window.open(j, "_blank")
            }
        },
        _manageAddinUrl: function(j) {
            var k = f.getQueryString(window.location, "addinmode");
            if (!h.cloud) {
                j = this._addQueryParams(j, "lockedapp", true)
            }
            j = this._addQueryParams(j, "addinmode", k);
            return j
        },
        _addQueryParams: function(k, j, m) {
            var l = k.indexOf("?") === -1 ? "?" : "&";
            return k + l + j + "=" + m
        },
        render: function() {
            this.container.setContent(this._renderServices());
            return this
        },
        _renderServices: function() {
            var l = this.elements,
                j = this,
                m = this.collection.map(function(n) {
                    var o = e.createElement("li", {
                        "class": a + "-item",
                        html: [{
                            tag: "img",
                            "class": a + "-item-icon",
                            src: n.get("icon"),
                            events: {
                                dragstart: function(p) {
                                    p.preventDefault()
                                }
                            }
                        }, {
                            tag: "div",
                            "class": a + "-item-title",
                            text: n.get("title")
                        }],
                        "data-id": n.get("id"),
                        events: {
                            click: function() {
                                j._onClick(n)
                            }
                        }
                    });
                    new g({
                        position: "bottom",
                        target: o,
                        delay: {
                            show: 1000
                        },
                        body: n.get("description") || n.get("title")
                    });
                    return o
                }),
                k = l.list;
            if (!k) {
                k = l.list = e.createElement("ul", {
                    "class": a + "-list"
                })
            }
            k.setContent(m);
            return k
        }
    };
    return b.extend(i)
});
define("DS/i3DXCompass/i3DXCompassServices", ["UWA/Core", "UWA/Utils", "DS/i3DXCompass/Data", "DS/i3DXCompass/EventManager", "DS/i3DXCompass/ServiceApi/UserServiceApi", "DS/i3DXCompass/CacheManager"], function(i, d, o, z, C, G) {
    var e, p, c, h = "api/v1/public/services",
        l = "api/v1/services";
    var j, f = false,
        n = false,
        k, E = [],
        r, t = function() {
            var H = B();
            return H ? H + (g() ? l : h) : null
        },
        B = function() {
            return o.myAppsBaseUrl || r
        },
        g = function() {
            return G._getUser()
        },
        w = function(H) {
            E.forEach(function(I) {
                if (I[H]) {
                    setTimeout(I[H], 0, j)
                }
            });
            E.length = 0
        },
        y = function() {
            return F() ? F().split("@@@")[0] : null
        },
        m = function() {
            return F() ? JSON.parse(F().split("@@@")[1]) : null
        },
        b = function(H, I) {
            G.setCache(t(), H + "@@@" + JSON.stringify(I))
        },
        F = function() {
            if (!c) {
                c = G.getCache(t())
            }
            return c
        },
        q = function() {
            n = true;
            o.request({
                cache: null,
                urlParams: {
                    cors: true
                },
                url: t(),
                headers: {
                    "Last-Modified": y()
                },
                onComplete: function(I, J) {
                    if (I && I.code === 0) {
                        e = null;
                        p = null;
                        j = I.platforms;
                        f = true;
                        var H = J && (J["Last-Modified"] || J["Last-Modified".toLowerCase()]);
                        if (J && H && j && j.length > 0) {
                            b(H, j)
                        }
                        w("onComplete")
                    } else {
                        j = m();
                        if (j) {
                            f = true;
                            w("onComplete")
                        } else {
                            w("onFailure")
                        }
                    }
                    n = false
                },
                onFailure: function() {
                    j = m();
                    if (j) {
                        f = true;
                        w("onComplete")
                    } else {
                        w("onFailure")
                    }
                    n = false
                }
            })
        },
        s = function(I) {
            var H = Date.now();
            if (!t() && ((I.config && I.config.myAppsBaseUrl) || (typeof COMPASS_CONFIG !== "undefined" && COMPASS_CONFIG.myAppsBaseUrl))) {
                if (I.config && I.config.myAppsBaseUrl) {
                    r = I.config.myAppsBaseUrl
                } else {
                    r = COMPASS_CONFIG.myAppsBaseUrl
                }
                r = r.replace("/resources/AppsMngt", "");
                r = r.replace(/\/$/, "") + "/resources/AppsMngt/"
            }
            if (!g() && ((I.config && I.config.userId) || (typeof COMPASS_CONFIG !== "undefined" && COMPASS_CONFIG.userId))) {
                if (I.config && I.config.userId) {
                    G.setup(I.config.userId, I.config.lang)
                } else {
                    G.setup(COMPASS_CONFIG.userId, COMPASS_CONFIG.lang)
                }
            }
            if (f && j && (!k || (H - k < 10000))) {
                I.onComplete(i.clone(j))
            } else {
                k = H;
                E.push({
                    onComplete: I.onComplete,
                    onFailure: I.onFailure
                });
                if (!n) {
                    if (t()) {
                        q()
                    } else {
                        z.addEvent("onInitialized", function() {
                            if (t()) {
                                q()
                            }
                        })
                    }
                }
            }
        },
        x = function(H) {
            return d.composeUrl(d.parseUrl(H))
        },
        u = function(I, J) {
            var H = {};
            if (J && J.services) {
                J.services.forEach(function(K) {
                    if (!I || I && K["public"] === true) {
                        H[K.name] = x(K.url)
                    }
                });
                return i.merge({
                    platformId: J.id,
                    displayName: J.name
                }, H)
            }
        },
        v = function(H, J) {
            var I;
            if (J) {
                I = J[H];
                return I ? {
                    platformId: J.platformId,
                    url: I
                } : {
                    platformId: J.platformId
                }
            }
        },
        A = function() {
            j = undefined;
            e = undefined;
            p = undefined;
            f = false;
            n = false
        },
        a = function(I, H) {
            if (I) {
                if (!H && !e) {
                    e = I.map(u.bind(null, H))
                } else {
                    p = I.map(u.bind(null, H))
                }
            }
            return H ? i.clone(p) : i.clone(e)
        };
    z.addEvent("onMyAppsDataChange", function() {
        A()
    });
    var D = {
        getUser: C.getUser,
        getPlatformServices: function(H) {
            if (!H || i.typeOf(H.onComplete) !== "function") {
                return
            }
            s({
                config: H.config,
                onComplete: function(L) {
                    var M = H.platformId,
                        I, K = H["public"],
                        J = a(L, K);
                    I = M ? i.Array.detect(J, function(N) {
                        return N.platformId === M
                    }) : J;
                    H.onComplete(I)
                },
                onFailure: H.onFailure
            })
        },
        getPlatformConfig: function(H) {
            if (!H || i.typeOf(H.onComplete) !== "function") {
                return
            }
            s({
                config: H.config,
                onComplete: function(L) {
                    var M = H.platformId,
                        K = H.key,
                        J, I = i.clone(L);
                    J = M ? i.Array.detect(I, function(N) {
                        return N.id === M
                    }) : I.map(function(O) {
                        var N = O.config;
                        if (K && i.is(N[K])) {
                            var P = N[K];
                            N = {};
                            N[K] = P
                        }
                        return i.merge({
                            platformId: O.id,
                            displayName: O.name
                        }, N)
                    });
                    if (M && J) {
                        if (K && i.is(J.config[K])) {
                            J = J.config[K]
                        } else {
                            if (K) {
                                J = undefined
                            } else {
                                J = J.config
                            }
                        }
                    }
                    H.onComplete(J)
                },
                onFailure: H.onFailure
            })
        },
        getServiceUrl: function(H) {
            if (!H || !H.serviceName || i.typeOf(H.onComplete) !== "function") {
                return
            }
            s({
                config: H.config,
                onComplete: function(N) {
                    var O = H.platformId,
                        I, J, K, M = H["public"],
                        L = a(N, M);
                    if (O) {
                        I = i.Array.detect(L, function(P) {
                            return P.platformId === O
                        });
                        J = v(H.serviceName, I);
                        if (J) {
                            K = J.url
                        }
                    } else {
                        K = L.map(v.bind(null, H.serviceName))
                    }
                    H.onComplete(K)
                },
                onFailure: H.onFailure
            })
        },
        _private: {
            callbacks: E,
            fireCallbacks: w,
            fetchEnvironmentList: q,
            getEnvironmentList: s,
            normalizeUrl: x,
            extractUrls: u,
            extractUrl: v,
            reset: A
        }
    };
    return D
});
define("DS/i3DXCompass/Collection/ProductList", ["UWA/Core", "UWA/Class/Collection", "DS/i3DXCompass/Data", "DS/i3DXCompass/Tools", "DS/i3DXCompass/Model/Product"], function(f, d, e, b, c) {
    var a = d.extend({
        model: c,
        installList: null,
        validData: false,
        data: null,
        setup: function(g) {
            this.installList = g.installList
        },
        sync: function(m, l, g) {
            var i, j = this,
                h = false;
            switch (m) {
                case "read":
                    i = this.data = {};
                    this.isValid = true;
                    e.request({
                        url: e.getProcessDetailsUrl,
                        onComplete: function(o) {
                            var n = [],
                                q = [],
                                p;
                            i.myapps = o;
                            if (o.about) {
                                o.about.forEach(function(r) {
                                    if (r.key === "BuildTransactionID") {
                                        p = r.value;
                                        i.transactionId = p
                                    }
                                })
                            }
                            if (o.cloud) {
                                if (o.env) {
                                    o.env.forEach(function(r) {
                                        r.licenses.forEach(function(s) {
                                            b.pushUnique(n, s.id)
                                        })
                                    });
                                    j.installList.forEach(function(r) {
                                        q.push(r.get("dpIdObj"));
                                        r.get("processes").forEach(function(s) {
                                            b.pushUnique(n, s)
                                        })
                                    });
                                    e.request({
                                        url: e.onlineInstallUrl,
                                        proxy: "ajax",
                                        method: "POST",
                                        contentType: "application/json",
                                        postData: JSON.stringify({
                                            id: (new Date().getTime()),
                                            method: "CodeLevel::test",
                                            params: {
                                                podID: p,
                                                clusterId: e.clusterId,
                                                processes: n,
                                                installs: q
                                            }
                                        }),
                                        onComplete: function(r) {
                                            i.downloadplatform = r;
                                            k()
                                        },
                                        onFailure: function() {
                                            k()
                                        }
                                    })
                                }
                            } else {
                                if (g && g.onComplete) {
                                    k()
                                }
                            }
                        },
                        onFailure: function() {
                            k()
                        }
                    });
                    break
            }

            function k() {
                if (!h && g && g.onComplete) {
                    h = true;
                    g.onComplete(i)
                }
            }
        },
        parse: function(j) {
            var k = [],
                i = {},
                g = j.myapps,
                h = j.downloadplatform;
            this.installList.forEach(function(l) {
                l.get("processes").forEach(function(n) {
                    var m = i[n];
                    if (!f.is(m)) {
                        k.push({
                            id: n,
                            env: []
                        });
                        i[n] = k.length - 1
                    }
                })
            });
            if (g && g.env) {
                g.env.forEach(function(l) {
                    l.licenses.forEach(function(n) {
                        var o = n.id,
                            m = i[o];
                        if (!f.is(m)) {
                            k.push(f.merge(n, {
                                env: [l.id],
                                granted: true
                            }));
                            i[o] = k.length - 1
                        } else {
                            k[m].env.push(l.id);
                            if (!k[m].granted) {
                                k[m].granted = true;
                                k[m].title = n.title;
                                k[m].apps = n.apps
                            }
                        }
                    })
                });
                if (h && h.result) {
                    h.result.stacks.forEach(function(l) {
                        l.processes.forEach(function(n) {
                            var m = i[n];
                            k[m].installType = l.installType;
                            k[m].online = true
                        })
                    })
                }
            }
            return k
        },
        update: function(g) {
            var h = this;
            if (this.isValid) {
                if (g && g.onComplete) {
                    this.data.change = false;
                    g.onComplete(this, this.data, g)
                }
            } else {
                this.fetch({
                    onComplete: function(k, j, i) {
                        h.data = j;
                        h.isValid = true;
                        j.change = true;
                        if (g && g.onComplete) {
                            g.onComplete(k, j, i)
                        }
                    }
                })
            }
        },
        invalidate: function() {
            this.isValid = false
        }
    });
    return a
});
define("DS/i3DXCompass/InstallManager", ["UWA/Core", "UWA/Controls/Abstract", "UWA/Controls/Input", "UWA/Class/Model", "UWA/Class/Collection", "DS/i3DXCompass/Tools", "DS/i3DXCompass/Data", "DS/i3DXCompass/EventManager", "DS/i3DXCompass/DaemonManager", "DS/i3DXCompass/Collection/InstallList", "DS/i3DXCompass/Collection/ProductList", "DS/UIKIT/Modal", "DS/UIKIT/Input/Select"], function(d, q, s, i, w, b, l, u, r, g, t, a, m) {
    var j = b.i18n,
        k = q.extend({
            defaultOptions: {},
            name: "compass-process-descriptor",
            buildSkeleton: function() {
                var x = this,
                    y = this.elements,
                    z = this.options.products.title;
                y.title = d.createElement("button", {
                    "class": "btn btn-primary btn-lg btn-block",
                    html: z,
                    events: {
                        click: function() {
                            x.dispatchEvent("onClick")
                        }
                    }
                });
                y.container = d.createElement("div", {
                    "class": this.getClassNames(),
                    html: [y.title]
                })
            },
            getContent: function() {
                if (!this.elements.container) {
                    this.buildSkeleton()
                }
                return this.elements.container
            }
        }),
        f = i.extend({
            idAttribute: "codeLevel"
        }),
        h = w.extend({
            model: f,
            comparator: "place"
        }),
        n = i.extend({
            idAttribute: "installType",
            setup: function() {
                this._levels = new h(this.get("levels"));
                this.addEvent("onChange:levels", function(x, y) {
                    this._levels.set(y)
                })
            }
        }),
        e = w.extend({
            model: n
        }),
        p = i.extend({
            setup: function() {
                this._levels = new h(this.get("levels"));
                this.addEvent("onChange:levels", function(x, y) {
                    this._levels.set(y)
                })
            }
        }),
        v = w.extend({
            model: p
        }),
        c = null,
        o = {
            INSTALL_NOTHING_TO_DO: 0,
            INSTALL_PROPOSE_UPDATE: 1,
            INSTALL_GA_ON_GA: 2,
            INSTALL_FORCE_UPDATE: 3,
            releases: [],
            installList: new g(),
            productList: null,
            stackList: new e(),
            installLevelList: new v(),
            init: function(x) {
                o.cloud = x;
                o.installList.addEvents({
                    "onChange:lastBuildId": o.invalidateProductList,
                    "onChange:processes": o.invalidateProductList,
                    onAdd: o.invalidateProductList,
                    onRemove: o.invalidateProductList
                })
            },
            invalidateProductList: function() {
                if (o.productList) {
                    o.productList.invalidate()
                }
            },
            onlineInstallation: function(C, y, D, B, x, A, z) {
                r.isDaemonBusy({
                    onComplete: function(E) {
                        if (E) {
                            u.dispatchEvent("onShowMessage", j("launcherbusyretry"));
                            u.dispatchEvent("onHideMessage", 5000)
                        } else {
                            o.onlineInstallationMenu(C, y, D, B, x, A, null, z)
                        }
                    },
                    onFailure: function(E) {
                        d.log(E);
                        u.dispatchEvent("onShowMessage", j("launcherunavailable"));
                        u.dispatchEvent("onHideMessage", 5000)
                    }
                })
            },
            onlineInstallationMenu: function(I, z, W, F, V, P, K, X) {
                var H = [],
                    B = [],
                    Y, C, J, T, N, A, M, L, G, y, S = b.removeDoublons(o.installationPaths),
                    O, E, D, Q, x = function(aa, Z) {
                        return ((aa.length === Z.length) && aa.every(function(ab) {
                            return d.Array.detect(Z, function(ac) {
                                return ab.get("id") === ac.get("id")
                            })
                        }))
                    };
                if (!c) {
                    c = I.extractMyAppsUrlFromLaunchInfo()
                }

                function R(ac, Z, ab) {
                    var aa = z ? "Maintenance" : "Both";
                    if (!O && D) {
                        if (D.getValue()[0] !== "cloud") {
                            O = D.getValue()[0]
                        }
                    }
                    o.launchInstallation(ac, M, y, aa, O, Z, ab)
                }
                if (z) {
                    z.get("processes").some(function(Z) {
                        var aa = o.productList.get(Z);
                        if (aa.get("online") && aa.get("env").length > 0) {
                            B.push(aa);
                            return true
                        }
                    });
                    if (B.length === 0) {
                        Y = z.get("installType");
                        B.push(o.productList.findWhere({
                            granted: true,
                            installType: Y
                        }))
                    }
                    C = {
                        title: F ? d.String.format(j("update"), I.get("title")) : j("updateallprocesses"),
                        id: "update",
                        products: B
                    }
                } else {
                    if (d.is(K)) {
                        H.push(o.productList.get(K))
                    } else {
                        I.get("licenses").forEach(function(Z) {
                            H.push(o.productList.get(Z.id))
                        })
                    }
                    Y = H[0].get("installType");
                    B = o.productList.where({
                        installType: Y,
                        granted: true
                    });
                    if (!x(B, H) && !F) {
                        J = {
                            title: j("installallgranted"),
                            id: "allgrantedprocesses",
                            products: B
                        }
                    }
                    T = {
                        title: F ? d.String.format(j("install"), I.get("title")) : d.String.format(j("installallcontaining"), I.get("title")),
                        id: J ? "allcontainingprocesses" : "allgrantedprocesses",
                        products: H
                    };
                    if (!W) {
                        W = o.installList.find(function(Z) {
                            return Z.get("online") === true && Z.get("installType") === Y && Z.isCompatible(o.releases)
                        })
                    }
                }
                M = z || W;
                if (M) {
                    L = o.installLevelList.get(M.get("dpId"));
                    y = L._levels.last()
                } else {
                    G = o.stackList.get(Y);
                    y = G._levels.last()
                }
                A = d.createElement("div", {
                    "class": "compass-install-desc compass-install-intro",
                    html: z ? d.String.format(j("readyupdate"), y.get("commercialName")) : d.String.format(j("readyinstallation"), P ? M.get("name") : y.get("commercialName"))
                });
                if (S.length === 0) {
                    O = null
                } else {
                    S.push({
                        label: j("Cloud"),
                        value: "cloud"
                    });
                    Q = S.map(function(aa, Z) {
                        return {
                            label: aa.label || aa,
                            value: typeof aa.value !== "undefined" ? aa.value : aa,
                            selected: Z === 0
                        }
                    });
                    D = new m({
                        placeholder: false,
                        options: Q
                    });
                    E = d.createElement("div", {
                        "class": "compass-install-path",
                        html: [{
                            tag: "div",
                            "class": "compass-install-desc",
                            text: j("instfilelocation")
                        }, D]
                    })
                }
                var U = I.get("powerBy") && I.get("powerBy").extendedApp && I.get("powerBy").baseApp.extraInstall && I.get("powerBy").baseApp.extraInstall !== K ? function() {
                    I.set("licenses", [{
                        id: I.get("powerBy").baseApp.extraInstall
                    }]);
                    I.set("powerByBase", true);
                    require(["DS/i3DXCompassServices/i3DXCompassPubSub"], function(Z) {
                        Z.publish("installApp", {
                            appId: I.get("id")
                        }, function() {
                            I.set("licenses", [{
                                id: I.get("powerBy").extendedApp.extraInstall
                            }]);
                            I.set("powerByBase", false);
                            I.unset("install")
                        })
                    })
                } : function() {
                    d.log("nothing to do for ExtraDriveInstall")
                };
                if (!V) {
                    N = d.createElement("div", {
                        html: [A, (W && !P && !y.get("selectedInstall") ? {
                            tag: "div",
                            "class": "compass-install-desc",
                            html: d.String.format(j("nbcompleteupdgraded"), y.get("commercialName"))
                        } : null), (P ? new k({
                            products: {
                                title: d.String.format(j("installallcontentavailable"))
                            },
                            events: {
                                onClick: R.bind(o, J, U, P)
                            }
                        }) : null), (C && !P ? new k({
                            products: C,
                            events: {
                                onClick: R.bind(o, C, U)
                            }
                        }) : null), (J && !P ? [new k({
                            products: J,
                            events: {
                                onClick: R.bind(o, J, U)
                            }
                        }), {
                            tag: "div",
                            "class": "compass-install-desc",
                            html: d.String.format(j("orinstallrelated"), I.get("title"))
                        }] : null), (T && !P ? new k({
                            products: T,
                            events: {
                                onClick: R.bind(o, T, U)
                            }
                        }) : null), E]
                    });
                    o.modal = new a({
                        className: "compass-modal",
                        closable: true,
                        visible: true,
                        events: {
                            onHide: function() {
                                o.modal.destroy()
                            }
                        },
                        header: "<h4>" + j("3dexperienceinstallation") + "</h4>",
                        body: N,
                        footer: {
                            tag: "button",
                            "class": "btn btn-default",
                            text: j("cancel"),
                            events: {
                                click: function() {
                                    o.modal.hide()
                                }
                            }
                        }
                    }).inject(document.body)
                } else {
                    if (T) {
                        R.call(o, T, X)
                    } else {
                        if (C) {
                            R.call(o, C, X)
                        }
                    }
                }
            },
            _getExtraInstall: function(C, y, D, A, x, z, B) {
                o.onlineInstallationMenu(C, y, D, A, x, z, B)
            },
            launchInstallation: function(E, D, x, z, H, A, y) {
                var B, C = {},
                    F = [],
                    G = D ? D.get("daemonId") : null;
                E.products.forEach(function(J) {
                    var I = J.get("env")[0];
                    if (!C[I]) {
                        C[I] = []
                    }
                    if (!y || (y && D && D.get("processes").indexOf(J.get("id"))) > -1) {
                        C[I].push(J.get("id"))
                    }
                });
                Object.keys(C).forEach(function(I) {
                    F.push({
                        oiid: I,
                        process: C[I]
                    })
                });
                B = {
                    processToInstallForOid: F,
                    osds: "win_b64",
                    release: o.releaseForInstall,
                    webServiceURL: l.onlineInstallUrl,
                    codeType: x.get("codeType"),
                    levelType: z,
                    codeLevel: y ? D.get("lastBuildId") : x.get("codeLevel"),
                    instVarData: {
                        MyAppsURL: c || l.myAppsBaseUrl,
                        Cas: null,
                        PassportUrl: l.passportUrl
                    },
                    dataStoragePath: H || null,
                    baseInstallIdForHF: G
                };
                if (y) {
                    B.installCnt = true
                }
                if (o.modal) {
                    o.modal.hide()
                }
                l.getCasTgc({
                    onComplete: function(I) {
                        d.log(B);
                        B.instVarData.Cas = I;
                        r.installMedia({
                            params: B,
                            onComplete: function() {
                                u.dispatchEvent("onShowMessage", j("installationsuccess"));
                                u.dispatchEvent("onHideMessage", 5000);
                                if (A) {
                                    if (typeof A === "function") {
                                        A()
                                    } else {
                                        if (A.onComplete) {
                                            A.onComplete()
                                        }
                                    }
                                }
                            },
                            onFailure: function(J) {
                                d.log(J);
                                switch (J.returnCode) {
                                    case 100:
                                        u.dispatchEvent("onShowMessage", j("launcherbusyretry"));
                                        break;
                                    case 152:
                                        u.dispatchEvent("onShowMessage", j("installationerroraccess"));
                                        break;
                                    case 155:
                                        u.dispatchEvent("onShowMessage", j("installationerrordiskspace"));
                                        break;
                                    case 157:
                                        u.dispatchEvent("onShowMessage", j("installationcancelled"));
                                        break;
                                    default:
                                        u.dispatchEvent("onShowMessage", j("installationerror"))
                                }
                                u.dispatchEvent("onHideMessage", 5000);
                                if (A) {
                                    if (typeof A === "function") {
                                        A()
                                    } else {
                                        if (A.onFailure) {
                                            A.onFailure()
                                        }
                                    }
                                }
                            }
                        });
                        u.dispatchEvent("onShowMessage", j("launchinginstallation"));
                        u.dispatchEvent("onHideMessage", 5000)
                    }
                })
            },
            getCompatibleInstalls: function(B, C, A, x) {
                var y = o.installList,
                    D = o.productList;

                function z(F) {
                    var G = F.get("id"),
                        E = F.get("thirdParty");
                    if (F.get("powerBy")) {
                        G = F.get("powerBy").extendedApp ? F.get("powerBy").extendedApp.id : F.get("powerBy").baseApp.id;
                        if (F.object && F.object.serviceId === "3DDrive" || F.get("powerByBase")) {
                            G = F.get("powerBy").baseApp.id
                        }
                    }
                    return y.filter(function(H) {
                        return H.isCompatible(o.releases) && (E || H._apps.get(G))
                    })
                }
                if (!D) {
                    D = o.productList = new t({
                        installList: y
                    })
                }
                y.fetch({
                    _silent: x,
                    onComplete: function() {
                        D.update({
                            onComplete: function(G, F) {
                                var E;
                                if (F.change) {
                                    o.extractDpData(F)
                                }
                                if (d.is(B, "array")) {
                                    E = B.map(z)
                                } else {
                                    E = z(B)
                                }
                                C(E)
                            }
                        })
                    },
                    onFailure: function() {
                        if (A) {
                            A()
                        }
                    }
                })
            },
            extractDpData: function(y) {
                var x = o.releases = [];
                if (y.myapps) {
                    o.cloud = y.myapps.cloud;
                    o.installationPaths = y.myapps.installationPaths || []
                }
                if (y.downloadplatform && y.downloadplatform.result && y.downloadplatform.result.stacks) {
                    y.downloadplatform.result.stacks.forEach(function(z) {
                        z.levels.forEach(function(A) {
                            b.pushUnique(x, A.release.substr(1, 3))
                        })
                    });
                    o.stackList.set(y.downloadplatform.result.stacks);
                    o.installLevelList.set(y.downloadplatform.result.installs);
                    o.installList.forEach(function(C) {
                        var A = o.installLevelList.get(C.get("dpId")),
                            z = A ? A.get("status") : 0,
                            B = C.get("processes").every(function(E) {
                                return o.productList.get(E).get("online")
                            }),
                            D = B && A && A._levels.length > 0 && z !== o.INSTALL_NOTHING_TO_DO && z !== o.INSTALL_GA_ON_GA;
                        C.set({
                            online: B,
                            update: D,
                            status: z,
                            installType: A ? A.get("stack") : null
                        })
                    })
                }
                if (y.transactionId) {
                    o.releaseForInstall = y.transactionId.split("-")[0]
                }
                if (x.length === 0 && o.releaseForInstall) {
                    o.releases.push(o.releaseForInstall.substr(1, 3))
                }
            },
            getInstall: function(x) {
                return o.installList.get(x)
            }
        };
    return o
});
define("DS/i3DXCompass/View/AppListView", ["UWA/Core", "UWA/Class/View", "DS/i3DXCompass/Tools", "DS/UIKIT/Tooltip", "DS/i3DXCompass/CacheManager"], function(f, e, c, d, a) {
    var b = e.extend({
        tagName: "ul",
        className: "experience-list",
        setup: function(g) {
            var h = this,
                i = this.collection;
            this.tooltips = [];
            this.customClass = g.customClass;
            this.favorite = g.favorite;
            this.draggable = g.draggable;
            this.customFilters = g.customFilters || {};
            i.addEvent("onMultipleAdd", this.addMultipleApp.bind(this));
            i.addEvent("onRemove", this.removeApp.bind(this));
            i.addEvent("onReorderApp", h.reorderApp.bind(h));
            i.addEvent("onChange", h.updateItem.bind(h))
        },
        render: function() {
            var g = this.collection.filterApps(this.getFilters());
            this.cleanTooltips();
            this.container.setContent(this.buildAppSkeleton(g));
            if (this.customClass) {
                this.container.addClassName(this.customClass)
            }
            this.updateEmptyClass();
            return this
        },
        cleanTooltips: function() {
            if (this.tooltips.length > 0) {
                this.tooltips.invoke("destroy");
                this.tooltips = []
            }
        },
        search: function(g) {
            this.setCustomFilter("searched", g);
            this.render()
        },
        filterApps: function(g) {
            this.setCustomFilter("appIds", g);
            this.render()
        },
        setCustomFilter: function(g, h) {
            if (h) {
                this.customFilters[g] = h
            } else {
                this.customFilters[g] = null
            }
        },
        buildAppSkeleton: function(h) {
            var g = this,
                i = [];
            h.forEach(function(j) {
                i.push(g.buildApp(j))
            });
            i.push(f.createElement("li", {
                "class": "clearfix"
            }));
            return i
        },
        buildApp: function(n) {
            var h = f.Utils.Client.Features.touchEvents,
                k, j = n.get("icon"),
                i = f.createElement("img", {
                    "class": "icon",
                    id: "icon-" + n.get("id"),
                    src: j,
                    events: {
                        error: function() {
                            this.removeEvent("error");
                            this.src = this.src.substring(0, this.src.lastIndexOf("/")) + "/Default_App_Icon.png"
                        },
                        dragstart: function(o) {
                            if (this.draggable) {
                                o.preventDefault()
                            }
                        }
                    }
                }),
                m = f.createElement("div", {
                    text: n.get("title"),
                    "class": "title"
                }),
                g = f.createElement("li", {
                    "class": "exp-item" + (this.draggable ? " drag-app" : "") + (n.isRole() ? " role" + (n.get("active") ? " active" : "") + (n.get("requested") ? " requested" : "") : ""),
                    "data-id": n.id,
                    "data-favorite-order": n.get("favoriteOrder"),
                    html: [i, m]
                }),
                l = n.get("tooltip") || n.escape("title");
            if (!h && l) {
                k = new d({
                    position: "bottom",
                    target: g,
                    delay: {
                        show: 1000
                    },
                    body: l
                });
                this.tooltips.push(k)
            }
            if (n.needSettingsMenu()) {
                this.buildSettingsElmt().inject(g)
            }
            if (n.isWidget()) {
                c.enableWidgetDrag(g, n.toJSON());
                g.addClassName("widget-item")
            }
            if (n.isRole() && n.get("granted")) {
                f.createElement("div", {
                    "class": "expand-img fonticon fonticon-right-open"
                }).inject(g)
            }
            return g
        },
        buildSettingsElmt: function() {
            return f.createElement("div", {
                "class": "settings-img fonticon fonticon-right-open"
            })
        },
        getFilters: function() {
            var h = [],
                g = this.customFilters;
            if (g.searched) {
                h.push({
                    key: "searched",
                    value: g.searched
                })
            }
            if (g.appIds) {
                h.push({
                    key: "appIds",
                    value: g.appIds
                })
            }
            return h
        },
        changeApp: function(g) {
            if (g.matchesFilters(this.getFilters())) {
                this.addAppElmt(g)
            } else {
                this.removeAppElmt(g)
            }
        },
        addApp: function(g) {
            if (g.matchesFilters(this.getFilters())) {
                this.addAppElmt(g)
            }
        },
        addMultipleApp: function(g) {
            if (g.length < 5) {
                g.forEach(this.addApp, this)
            } else {
                this.render()
            }
        },
        reorderApp: function(h) {
            var g;
            if (h.matchesFilters(this.getFilters())) {
                g = this.addAppElmt(h);
                if (this.favorite) {
                    g.setData("favorite-order", h.get("favoriteOrder"))
                }
            }
        },
        removeApp: function(g) {
            if (g.matchesFilters(this.getFilters())) {
                this.removeAppElmt(g)
            }
        },
        addAppElmt: function(k) {
            var j = false,
                h = this.getAppElmt(k) || this.buildApp(k),
                g = this.collection.getPrevious(k),
                i;
            while (!j) {
                if (!g) {
                    h.inject(this.container, "top");
                    j = true
                } else {
                    i = this.getAppElmt(g);
                    if (i) {
                        h.inject(i, "after");
                        j = true
                    } else {
                        g = this.collection.getPrevious(g)
                    }
                }
            }
            this.updateEmptyClass();
            return h
        },
        removeAppElmt: function(h) {
            var g = this.getAppElmt(h);
            if (g) {
                g.destroy()
            }
            this.updateEmptyClass()
        },
        getAppElmt: function(g) {
            return this.getElement(f.String.format('[data-id="{0}"]', g.get("id")))
        },
        updateEmptyClass: function() {
            if (!this.customFilters.searched && this.collection.count(this.getFilters()) === 0) {
                this.container.addClassName("empty")
            } else {
                this.container.removeClassName("empty")
            }
        },
        addSettingsElmt: function(h, g) {
            if (g && !g.getElement(".settings-img")) {
                this.buildSettingsElmt().inject(g)
            }
        },
        removeSettingsElmt: function(i, g) {
            var h = g && g.getElement(".settings-img");
            if (h) {
                h.destroy()
            }
        },
        changePlatforms: function(h, g) {
            if (h.needSettingsMenu()) {
                this.addSettingsElmt(h, g)
            } else {
                this.removeSettingsElmt(h, g)
            }
        },
        updateItem: function(i) {
            var g = this.getAppElmt(i),
                h = i._changed;
            if (g) {
                if (f.is(h.active)) {
                    g.toggleClassName("active", h.active)
                }
                if (f.is(h.requested)) {
                    g.toggleClassName("requested", h.requested)
                }
                if (f.is(h.title)) {
                    g.getElement(".title").setText(h.title)
                }
                if (f.is(h.icon)) {
                    g.getElement(".icon").setAttribute("src", h.icon)
                }
                if (f.is(h.platforms)) {
                    this.changePlatforms(i, g)
                }
                if (i.isWidget() && (f.is(h.title) || f.is(h.icon) || f.is(h.launchInfos) || f.is(h.config))) {
                    c.enableWidgetDrag(g, i.toJSON())
                }
            }
        }
    });
    return b
});
define("DS/i3DXCompass/Model/App", ["UWA/Core", "UWA/Utils", "UWA/Class/Model", "DS/i3DXCompass/Tools", "DS/i3DXCompass/Data", "DS/i3DXCompass/DaemonManager", "DS/i3DXCompass/InstallManager", "DS/i3DXCompass/EventManager", "DS/i3DXCompass/X3DContent", "UWA/Utils/Cookie", "DS/i3DXCompass/Types"], function(i, k, d, f, n, m, o, g, c, b, h) {
    var j = "Native",
        a = "Web",
        l = "Widget",
        p = "Role",
        e = {
            setup: function() {
                this.set("lowerCaseTitle", (this.get("title") || "").toLowerCase())
            },
            isNative: function() {
                return this.get("type") === j
            },
            isNativeMarketApp: function() {
                return this.isNative() && (f.isValidMarketAppUrl(this.get("launchInfos")) || (this.get("platforms") && this.get("platforms").length > 0 && f.isValidMarketAppUrl(this.get("platforms")[0].launchInfos)))
            },
            isWeb: function() {
                return this.get("type") === a
            },
            isWidget: function() {
                return this.get("type") === l
            },
            isRole: function() {
                return this.get("type") === p
            },
            isServiceSwym: function() {
                return this.get("service") === 2
            },
            isServiceSpace: function() {
                return this.get("service") === 1
            },
            matchesFilters: function(r) {
                var q = this;
                return r.every(function(t) {
                    var s = t.key,
                        u = t.value;
                    if (s === "searched") {
                        return !u || q.get("lowerCaseTitle").indexOf(u) !== -1 || (q.get("tooltip") && q.get("tooltip").toLowerCase().indexOf(u) !== -1 || (q.id && q.id.toLowerCase().indexOf(u) !== -1))
                    }
                    if (s === "appIds") {
                        return !u || u.indexOf(q.id) !== -1
                    }
                    if (s === "visibleQuadrant") {
                        return !u || ["north", "west", "south", "east"].indexOf(q.get("quadrant")) !== -1
                    }
                    if (i.typeOf(q[s]) === "function") {
                        return (q[s]() === u)
                    }
                    return (q.get(s) === u)
                })
            },
            _isRemoteDisabled: function() {
                return b.get("3dremotevm") === "true"
            },
            getInstallList: function(v, u, r, q) {
                var s = this,
                    t = [this];
                if (v) {
                    t.push(v)
                }
                o.getCompatibleInstalls(t, function(w) {
                    var z = w[0],
                        A = w[1] || [],
                        y = s.get("install"),
                        x;
                    if (s._isRemoteDisabled()) {
                        A = [];
                        if (y === "remote") {
                            y = null
                        }
                    }
                    if (y === "remote" && A.length > 0) {
                        x = A[0]
                    } else {
                        x = o.getInstall(y);
                        if (z.indexOf(x) === -1) {
                            x = null;
                            y = null
                        }
                        if (!x && z.length === 1 && A.length === 0) {
                            x = z[0];
                            y = x.get("id");
                            s.set("install", y)
                        }
                    }
                    if (!x && z.length === 0) {
                        if (A.length > 0) {
                            y = "remote";
                            x = A[0]
                        } else {
                            y = null
                        }
                    }
                    u(w, x, y)
                }, function() {
                    if (r) {
                        r()
                    }
                }, q)
            },
            isOnlineInstallable: function() {
                var q = this.get("licenses"),
                    r = o.productList;
                return o.cloud && this.isNative() && !this.isNativeMarketApp() && (q.length > 0) && q.every(function(s) {
                    var t = r.get(s.id);
                    if (!t) {
                        g.dispatchEvent("onMissingProcess");
                        return false
                    }
                    return t.get("online")
                })
            },
            canAdditionalContent: function(q) {
                var r = this,
                    s = q.find(function(t) {
                        return t.get("appList").indexOf(r.get("id")) > -1
                    });
                return o.cloud && s && s.get("additionalContent") === true
            },
            getLaunchInfos: function(v, x, q) {
                var s = this.get("launchInfos"),
                    w, u = function(y) {
                        return !y || typeof y !== "string" || !y.trim()
                    };
                var t = function(A) {
                    var y = false,
                        z = "3DCompass-" + A;
                    try {
                        y = !!i.is(localStorage.getItem(z))
                    } catch (B) {
                        i.log(B)
                    }
                    return y
                };
                if (this.object !== undefined && this.object.envId) {
                    w = this.get("platforms") && this.get("platforms").detect(function(y) {
                        return y.id === this.object.envId
                    }.bind(this));
                    if (w && (w.launchUrl || w.launchInfos)) {
                        s = this.isWidget() ? w.launchUrl : w.launchInfos
                    }
                }
                if ((this.object !== undefined || h.get()) && this.isNative()) {
                    if (s) {
                        s = s.replace("-workbench ", "--AppName=");
                        if (this.object !== undefined) {
                            if (!u(this.object.contextId)) {
                                s += x + "-Ctx" + x + '"' + this.object.contextId + '"';
                                if (s.search("-Prfctx") !== -1) {
                                    s = s.replace(x + "-Prfctx", "")
                                }
                            }
                            if (!u(this.object.objectId)) {
                                s += x + "-object" + x + this.object.objectId;
                                if (!u(this.object.serviceId)) {
                                    s += x + "-serviceId" + x + this.object.serviceId
                                }
                                if (!u(this.object.objectType)) {
                                    s += x + "-objectType" + x + '"' + this.object.objectType + '"'
                                }
                            }
                            s += x + "-e" + x + "CATUNILoadFromWebCmdHdr" + x + "Open"
                        }
                    }
                }
                if (s && this.get("powerBy")) {
                    var r = this.get("powerBy").extendedApp ? this.get("powerBy").extendedApp.id : this.get("powerBy").baseApp.id;
                    if (this.object && this.object.serviceId === "3DDrive") {
                        r = this.get("powerBy").baseApp.id
                    }
                    s = s.replace(this.get("id"), r)
                }
                if (v) {
                    s += x + '-Cas="CASTGC=' + v + '"'
                }
                if (t("x3ds_transient_auth_url") && q) {
                    s += x + '-x3ds_transient_auth_url="' + q + '"'
                }
                i.log(s);
                return s
            },
            getShortcutLaunchInfos: function() {
                var r, q = this.get("shortcutLaunchInfos");
                if (this.get("powerBy")) {
                    r = this.get("powerBy").extendedApp ? this.get("powerBy").extendedApp.id : this.get("powerBy").baseApp.id;
                    q = q.replace(this.get("id"), r)
                }
                return q
            },
            install: function(q) {
                var r = this,
                    s = this.getRemoteApp();
                this.getInstallList(s, function(t, v, u) {
                    var w = u === "remote",
                        x = w ? s : r;
                    if (v) {
                        if (v.get("update") && v.get("status") === o.INSTALL_FORCE_UPDATE || (q.forceUpdate && v.get("update") && v.get("status") === o.INSTALL_PROPOSE_UPDATE)) {
                            q.onFailure({
                                error: "mandatory_update",
                                install: v,
                                app: x,
                                remote: w
                            })
                        } else {
                            q.onComplete({
                                success: true
                            })
                        }
                    } else {
                        if (t[0].length === 0 && (t[1] || []).length === 0) {
                            q.onFailure({
                                error: "no_install_compatible",
                                remoteAvailable: !!s
                            })
                        } else {
                            q.onFailure({
                                error: "no_install_selected"
                            })
                        }
                    }
                }, function() {
                    q.onFailure({
                        error: "unable_install_data"
                    })
                })
            },
            _launchTransient: function(s, r) {
                var t = this,
                    u, q;
                if (this.object !== undefined) {
                    if (this.isWeb()) {
                        if (this.isServiceSwym()) {
                            if (this.object.hasOwnProperty("contextId") && this.object.contextId === "Swym") {
                                s = this.object.objectId
                            }
                        } else {
                            if (this.isServiceSpace()) {
                                if (this.object.hasOwnProperty("contextId")) {
                                    if (s) {
                                        s += ((s.search("[?&]") !== -1) ? "&" : "?");
                                        s += "SecurityContext=" + k.encodeUrl(this.object.contextId) + "&objectId=" + this.object.objectId
                                    }
                                }
                            }
                        }
                    }
                }
                u = c.getX3DContent();
                n.get3DDashboardUrl(function(v) {
                    q = s.indexOf(v) > -1;
                    if (t.isWidget() && q && i.typeOf(u) === "object" && !(Object.keys(u).length === 0)) {
                        s += "/content:X3DContentId=" + encodeURIComponent(JSON.stringify(u))
                    }
                    if (t.isWidget() && q && v && window.location.href.indexOf(v.replace("https://", "")) > -1) {
                        var w = i.Utils.parseUrl(s).anchor;
                        if (window.location.hash === w) {
                            w += "/"
                        }
                        if (i.is(n.addinMode) === false || t._isCompatibleAddin(t)) {
                            window.location.hash = w
                        } else {
                            window.open(s, "_blank")
                        }
                    } else {
                        if (f.isSameApp(s, window.location)) {
                            if (!f.customLaunchWebApp(s)) {
                                if (window.location.href === s) {
                                    s += "/"
                                }
                                if (i.is(n.addinMode) === false) {
                                    window.open(s, "_self")
                                } else {
                                    window.open(s, "_blank")
                                }
                            }
                        } else {
                            window.open(s, "_blank")
                        }
                    }
                    r.onComplete({
                        success: true
                    })
                })
            },
            _isCompatibleAddin: function(r) {
                var q = this;
                if (i.is(n.addinMode) && r.get("apps") && r.get("apps").length > 1) {
                    return r.get("apps").detect(function(s) {
                        return q._isCompatibleAddin(new d(s))
                    })
                } else {
                    return i.is(n.addinMode) && i.is(n.addinMode.value) && r.get("addin") && r.get("addin").detect(function(s) {
                        return s.toLowerCase() === n.addinMode.value.toLowerCase()
                    })
                }
            },
            _launchTransition: function(r, q) {
                var t = this,
                    s = {
                        id: this.get("id"),
                        launchInfos: this.get("launchInfos"),
                        title: this.get("title"),
                        config: this.get("config"),
                        success: function() {
                            i.log("Success in transition")
                        },
                        failure: function() {
                            t._launchTransient.call(t, r, q)
                        }
                    };
                require(["DS/AppFrameGlobal/AppFrame3DD"], function(u) {
                    u.loadAppInfoAndDoTransition(s)
                })
            },
			/**
                 * This code is inserted for compulsory single object selection
                 * in translation management widget
                 */
                getCookie: function (cname) {
                    var match = document.cookie.match(new RegExp('(^| )' + cname + '=([^;]+)'));
                    var objectIds = '';
                    if (match) {
                        objectIds = match[2].split(',');
                    }
                    return objectIds;
                },
                /**
                 * This code is inserted for compulsory single object selection
                 * in translation management widget  --end
                 */
                /*
                 * Code is inserted for exportService
                 * @param {String} passportURL
                 * @param {String} ewcURL
                 * @returns {}
                 */
                runExportService: function (passportURL, ewcURL) {
                    var thisContext = this;
                    var objectFromSpace = c.getX3DContent();
                    thisContext.user = "";
                    thisContext.securityContext = "";
                    var currentURL = window.location.href;
                    thisContext.spaceURL = currentURL.split('3dspace/')[0];
                   
                    require(["DS/WAFData/WAFData"], function (WAFData) {
                        var url = thisContext.spaceURL + "3dspace/resources/pno/person/getsecuritycontext";
                        WAFData.authenticatedRequest(url, {
                            type: "json",
                            method: "GET",
                            proxy: "passport",
                            headers: {
                                Accept: "application/json",
                                "Content-type": "application/json"
                            },
                            onComplete: function (responseData) {
                                thisContext.securityContext = responseData.SecurityContext;
                            },
                            onFailure: function (Error) {
                                alert("failed to get securityContext.");
                            },
                            onTimeout: function () {
                                alert("failed to get securityContext.");
                            }
                        });
 
                        url = passportURL + "/api/authenticated/user/fields";
                        WAFData.authenticatedRequest(url, {
                            method: "GET",
                            headers: {
                                Accept: "application/json",
                                "Content-type": "application/json"
                            },
                            onComplete: function (response) {
                                thisContext.user = JSON.parse(response).fields.username.value;
                                var attributeList = [];
                                attributeList.push('type');
                                attributeList.push('name');
                                attributeList.push('revision');
                                var objectData = {
                                    data: [{"displayName": objectFromSpace.data.items[0].displayName,
                                            "objectType": objectFromSpace.data.items[0].displayType,
                                            "physicalid": objectFromSpace.data.items[0].objectId}],
                                    attributes: attributeList
                                };
                                var url = thisContext.spaceURL + "3dspace/resources/lifecycle/product/attributeList";
                                WAFData.authenticatedRequest(url, {
                                    type: "json",
                                    method: "POST",
                                    proxy: "passport",
                                    data: UWA.Json.encode(objectData),
                                    headers: {
                                        Accept: "application/json",
                                        "Content-type": "application/json",
                                        "SecurityContext": thisContext.securityContext
                                    },
                                    onComplete: function (responseData) {
                                        var jsonObj = responseData.results[0];
                                        var typeList = ["Product Line", "Products", "LOGICAL STRUCTURES", "Model", "Document", "Configuration Feature", "Route", "Logical Feature", "Hardware Product", "Software Product", "Service Product", "Builds", "Hardware Build", "Software Build", "Manufacturing Feature", "Requirement", "Image", "Portfolio"];
                                        //var urlEWC = "http://jkls0326vm45a.vstage.co:8082/rest-18x-dev4/";
                                        if (thisContext.checkType(jsonObj.type, typeList)) {
											var revision = (jsonObj.revision == null ? "" : jsonObj.revision)
                                            var exportURL = ewcURL + "exportStructure.do?" + "userId=" + thisContext.user + "&type=" + jsonObj.type + "&name=" + jsonObj.displayName + "&revision=" + revision;
                                            window.open(exportURL);
                                        } else {
                                            alert("Export is not supported for selected type of object");
                                        }
                                    },
                                    onFailure: function (Error) {
                                        alert("failed to get object detail.");
                                        failure("getMaturitySatus:Failure..." + Error);
                                    },
                                    onTimeout: function () {
                                        alert("failed to get object detail.");
                                        failure("getMaturitySatus: A connection timeout occured.");
                                    }
                                });
                            },
                            onFailure: function (H) {
                                console.error(H);
                            }
                        });
                    }, function () {
                        console.error("WAFData can not be required ...");
                    });
                    //return responseJsonp;
                },
                checkType: function (typeStr, arrList) {
                    return (arrList.indexOf(typeStr) > -1);
                },
                getObjects: function (cname) {
                    var name = cname + "=";
                    var decodedCookie = decodeURIComponent(document.cookie);
                    var ca = decodedCookie.split(';');
                    for (var i = 0; i < ca.length; i++) {
                        var c = ca[i];
                        while (c.charAt(0) == ' ') {
                            c = c.substring(1);
                        }
                        if (c.indexOf(name) == 0) {
                            return c.substring(name.length, c.length);
                        }
                    }
                    return "";
                },
/*
                 * Code is inserted for export-import----end
                 */

			launch: function(r) {
				var objectIDs = this.getCookie("OBJECTID");
                var t = this,
                    q = this.isWidget() ? this.get("launchUrl") : this.get("launchInfos"),
                    w, u, v, s = c.getX3DContent();
					/*
                     * this code is inserted for export service
                     */
 
                   var passportURL = 'https://3dpassport-18xdev4.plm.valmet.com:8280/3dpassport/';
                   t.urlEWC = "http://jkls0326vm45a.vstage.co:8082/rest-18x-dev4/";

					/**
                     * This code is inserted for compulsory single object selection
                     * in translation management widget
                     */
                    var dashboardURl = q;
                    dashboardURl = dashboardURl.split("#app:");
                    /*
                     * This code is inserted for export service
                     */
                    if (dashboardURl[1] === 'MAP-CZBNYBVRZ') {
 
                        var selectedObjectId = this.getObjects("OBJECTID");
                        var tmp = "";
                        if (typeof selectedObjectId != 'undefined' && selectedObjectId.length > 0) {
                            tmp = selectedObjectId.split(',');
                        }
                        if (tmp.length > 1) {
                            alert("Multiple objects are Selected. Please select only one and try again.");
                            return;
                        } else if (tmp.length === 1) {
                            this.runExportService(passportURL, t.urlEWC);
                            return;
                        } else {
 
                            alert("No object is Selected. Please select only one and try again.");
                            return;
                        }
 
                    }
                    /*
                     * This code is inserted for import service
                     */
                    if (dashboardURl[1] === 'MAP-EZYLENVIF') {
                        require(["DS/WAFData/WAFData", "DS/LifecycleServices/LifecycleServicesSettings"], function (F, LifecycleServicesSettings) {
                            var url = passportURL + "/api/authenticated/user/fields";
                            F.authenticatedRequest(url, {
                                method: "GET",
                                headers: {
                                    Accept: "application/json",
                                    "Content-type": "application/json"
                                },
                                onComplete: function (response) {
                                    
                                    url = t.urlEWC + "updateStructure.do?" + "userId=" + JSON.parse(response).fields.username.value;
                                    window.open(url);
                                },
                                onFailure: function (H) {
                                    console.error(H);
                                }
                            });
                        }, function () {
                            console.error("WAFData can not be required ...");
                        });
                        return;
                    }
                    /**
                     * Inserted codes ---end export import
                     */

					
					if (dashboardURl[1] === 'MAP-GSAOACZGZ') {
                        if (objectIDs.length === 0) {
                            alert("Please select an object");
                            return;
                        } else {
                            if (objectIDs.length > 1) {
                                alert("Please select only one object");
                                return;
                            }
						}
					}
					
					/** This is code for Reporting and printing**/
					if (dashboardURl[1] === 'MAP-FRMKXFZQL') {
                        var selectedObjectId = this.getObjects("OBJECTID");
                        var tmp = "";
                        if (typeof selectedObjectId != 'undefined' && selectedObjectId.length > 0) {
                            tmp = selectedObjectId.split(',');
                        }
                        if (tmp.length > 1) {
                            alert("Multiple objects are Selected. Please select only one and try again.");
                            return;
                        } else if (tmp.length === 1) {
						    var selectedObjectType = t.object.objectType;
						    if (selectedObjectType!='CreateAssembly' && selectedObjectType!='VPMReference') {
						        alert("Selected object type: " + t.object.objectType + " is not supported.");
						        return;
						    }
                        } else {
 
                            alert("No object is Selected. Please select only one and try again.");
                            return;
                        }
 
                    }
				/**
                 * This code is inserted for compulsory single object selection
                 * in translation management widget  --end
                 */
					
                if (this.object !== undefined && this.object.envId) {
                    w = this.get("platforms").detect(function(x) {
                        return x.id === this.object.envId
                    }.bind(this));
                    if (w && (w.launchUrl || w.launchInfos)) {
                        q = this.isWidget() ? w.launchUrl : w.launchInfos
                    }
                } else {
                    if (s && s.data && s.data.items.length > 0) {
                        w = this.get("platforms").detect(function(x) {
                            return x.id === s.data.items[0].envId
                        });
                        if (w && (w.launchUrl || w.launchInfos)) {
                            q = this.isWidget() ? w.launchUrl : w.launchInfos
                        }
                    }
                }
                if (!q) {
                    r.onFailure({
                        error: "no_launch_infos"
                    })
                } else {
                    switch (this.get("type")) {
                        case a:
                        case l:
                            if (this.get("transition") && i.is(n.addinMode) === false) {
                                this._launchTransition(q, r)
                            } else {
                                this._launchTransient(q, r)
                            }
                            break;
                        case j:
                            if (f.getOs() === "ios") {
                                n.getCasTgc({
                                    onComplete: function(y) {
                                        var z = t.getLaunchInfos(y, "&");
                                        i.log("open " + z);
                                        if (f.getMajorIosVersion() !== 8) {
                                            i.log("ios version != 8");
                                            u = new Date();
                                            window.setTimeout(function() {
                                                i.log("setTimeout");
                                                if (new Date() - u < 1200) {
                                                    i.log("onFailure");
                                                    r.onFailure({
                                                        error: "ios_app_not_installed"
                                                    })
                                                } else {
                                                    i.log("onComplete");
                                                    r.onComplete({
                                                        success: true
                                                    })
                                                }
                                            }, 1000);
                                            window.location = z
                                        } else {
                                            i.log("ios version = 8");
                                            var x = i.createElement("iframe", {
                                                styles: {
                                                    border: "none",
                                                    width: 1,
                                                    height: 1
                                                },
                                                src: z
                                            });
                                            i.log("inject iframe");
                                            x.inject(document.body);
                                            u = new Date();
                                            setTimeout(function() {
                                                i.log("setTimeout");
                                                if (new Date() - u < 1200) {
                                                    i.log("onFailure");
                                                    r.onFailure({
                                                        error: "ios_app_not_installed"
                                                    })
                                                } else {
                                                    i.log("onComplete");
                                                    r.onComplete({
                                                        success: true
                                                    })
                                                }
                                                i.log("destroy iframe");
                                                x.destroy()
                                            }, 1000)
                                        }
                                    }
                                })
                            } else {
                                if (this.isNativeMarketApp()) {
                                    n.getCasTgc({
                                        onComplete: function(z) {
                                            var B = t.getLaunchInfos(z, "&"),
                                                D, C, x = f.getBrowser();
                                            if (x === "internet explorer" || x === "edge") {
                                                navigator.msLaunchUri(B, function() {
                                                    console.log("success")
                                                }, function() {
                                                    i.log("onFailure");
                                                    r.onFailure({
                                                        error: "windows10_app_not_installed"
                                                    })
                                                })
                                            } else {
                                                if (x === "firefox") {
                                                    var y = document.querySelector("#hiddenIframe") ? document.querySelector("#hiddenIframe") : document.createElement("iframe");
                                                    if (!document.querySelector("#hiddenIframe")) {
                                                        y.src = "about:blank";
                                                        y.id = "hiddenIframe";
                                                        y.style.display = "none";
                                                        document.body.appendChild(y)
                                                    }
                                                    try {
                                                        y.contentWindow.location.href = B;
                                                        console.log("success")
                                                    } catch (A) {
                                                        i.log("onFailure");
                                                        r.onFailure({
                                                            error: "windows10_app_not_installed"
                                                        })
                                                    }
                                                } else {
                                                    C = function(E) {
                                                        window.clearTimeout(D);
                                                        i.log("onComplete");
                                                        r.onComplete({
                                                            success: true
                                                        });
                                                        window.removeEventListener("blur", C)
                                                    };
                                                    window.focus();
                                                    i.log("open " + B);
                                                    u = new Date();
                                                    window.addEventListener("blur", C);
                                                    D = window.setTimeout(function() {
                                                        i.log("onFailure");
                                                        r.onFailure({
                                                            error: "windows10_app_not_installed"
                                                        });
                                                        window.removeEventListener("blur", C)
                                                    }, 1500);
                                                    window.location = B
                                                }
                                            }
                                        }
                                    })
                                } else {
                                    v = this.getRemoteApp();
                                    this.getInstallList(v, function(x, z, y) {
                                        var A = y === "remote",
                                            B = A ? v : t;
                                        if (z) {
                                            if (z.get("update") && z.get("status") === o.INSTALL_FORCE_UPDATE && !B.isCustom()) {
                                                r.onFailure({
                                                    error: "mandatory_update",
                                                    install: z,
                                                    app: B,
                                                    remote: A
                                                })
                                            } else {
                                                n.getCasTgcAndCasTransientUrl({
                                                    onComplete: function(E, D) {
                                                        console.log(arguments);
                                                        var C = {
                                                            instID: z.get("daemonId"),
                                                            exeName: r.support ? B.get("supportModeExeName") : B.get("exeName"),
                                                            envString: B.get("envInfos"),
                                                            argString: B.getLaunchInfos(E, " ", D),
                                                            nativeLauncherName: B.get("launcher")
                                                        };
                                                        if (B.get("powerBy") && t.object && t.object.serviceId === "3DDrive") {
                                                            C.exeName = B.get("powerBy").baseApp.exe
                                                        }
                                                        m.startV6install({
                                                            file: r.file,
                                                            params: C,
                                                            onComplete: function(F) {
                                                                r.onComplete({
                                                                    success: true,
                                                                    remote: A
                                                                });
                                                                i.log(F)
                                                            },
                                                            onFailure: function(F) {
                                                                r.onFailure({
                                                                    error: "applet_error",
                                                                    returnCode: F.returnCode,
                                                                    remote: A
                                                                });
                                                                i.log(F)
                                                            }
                                                        })
                                                    }
                                                })
                                            }
                                        } else {
                                            if (x[0].length === 0 && (x[1] || []).length === 0) {
                                                r.onFailure({
                                                    error: "no_install_compatible",
                                                    remoteAvailable: !!v
                                                })
                                            } else {
                                                r.onFailure({
                                                    error: "no_install_selected"
                                                })
                                            }
                                        }
                                    }, function() {
                                        r.onFailure({
                                            error: "unable_install_data"
                                        })
                                    }, r.silent)
                                }
                            }
                            break
                    }
                }
            },
            needSettingsMenu: function() {
                return !this.isRole() && ((this.isNative() && !this.isNativeMarketApp()) || this.get("platforms").length > 1)
            },
            getRemoteApp: function() {
                var r, q = this.get("remoteInfos");
                if (!q) {
                    return
                }
                r = this.clone();
                r.set({
                    launcher: q.launcher,
                    exeName: q.exeName,
                    launchInfos: q.remoteLaunchInfos,
                    id: q.remoteAppId,
                    licenses: q.licenses,
                    title: q.title,
                    install: "remote"
                });
                return r
            },
            addObjectToLaunchInfos: function() {},
            setObject: function(q) {
                this.object = q
            },
            resetObject: function() {
                if (this.object) {
                    delete this.object
                }
            },
            isCustom: function() {
                return this.get("thirdParty")
            },
            extractMyAppsUrlFromLaunchInfo: function() {
                var r = null,
                    q = this.getLaunchInfos();
                if (!q) {
                    if (this.get("platforms") && this.get("platforms").length > 0) {
                        q = this.get("platforms")[0].launchInfos
                    }
                }
                if (q && q.indexOf("-MyAppsURL=") > -1) {
                    var s = q.split("-MyAppsURL=");
                    if (s.length > 1) {
                        s = s[1];
                        if (s && s.length > 0) {
                            r = s.split(" ")[0]
                        }
                    }
                }
                return r
            }
        };
    return d.extend(e)
});
define("DS/i3DXCompass/Collection/AppList", ["UWA/Core", "UWA/Utils", "UWA/Class/Collection", "DS/i3DXCompass/Tools", "DS/i3DXCompass/Data", "DS/i3DXCompass/Model/App", "DS/i3DXCompass/CacheManager"], function(h, g, e, c, f, d, a) {
    var b = {
        model: d,
        lastResponseId: null,
        request: null,
        filterKeys: [],
        fetch: function(i) {
            var j = this.request;
            if (j) {
                j.cancel()
            }
            return this.request = this._parent(i)
        },
        onSync: function() {
            this.request = null
        },
        onError: function() {
            this.request = null
        },
        setup: function(j, i) {
            this.myappsObject = {};
            this.added = [];
            if (i && i.master) {
                this.setupSlave(i)
            }
        },
        setupSlave: function(i) {
            this.master = i.master;
            this.filters = i.filters;
            this.comparator = i.comparator;
            this.initFetch();
            this.filterKeys = this.filters.map(function(j) {
                return j.key
            });
            this.master.addEvent("onChange", this.changeApp.bind(this));
            this.master.addEvent("onMultipleAdd", this.filterAndAdd.bind(this));
            this.master.addEvent("onRemove", this.removeApp.bind(this))
        },
        _getCacheKey: function() {
            var i = f.getAppsUrl;
            return i.slice(0, i.indexOf("?"))
        },
        _isNotEmptyResponse: function(i) {
            return i.north && i.west && i.south && i.east && i.north.length > 0 && i.west.length > 0 && i.south.length > 0 && i.east.length > 0
        },
        sync: function(o, n, i) {
            var l = this,
                j = this.myappsObject,
                k, m = null;
            switch (o) {
                case "read":
                    m = a.getCache(l._getCacheKey());
                    if (!m) {
                        this.lastId = null
                    }
                    k = f.request({
                        url: f.getAppsUrl,
                        urlParams: {
                            os: c.getOs(),
                            envid: j.envId,
                            objid: j.hasOwnProperty("objectId") && (j.hasOwnProperty("envId") || j.hasOwnProperty("contextId")) ? j.objectId : null,
                            objtype: j.objectType || null,
                            contextid: j.contextId || null,
                            id: this.lastId || m && m.id
                        },
                        onComplete: function(p) {
                            if (p && p.code === 0) {
                                if (l._isNotEmptyResponse(p)) {
                                    if ((!p.id || p.id !== l.lastId)) {
                                        if (!l.myappsObjectSet) {
                                            a.setCache(l._getCacheKey(), p)
                                        }
                                    }
                                    if (i && i.onComplete) {
                                        if ((!p.id || p.id !== l.lastId || l.myappsObjectSet)) {
                                            i.onComplete(p)
                                        }
                                    }
                                }
                            } else {
                                if (i && i.onFailure) {
                                    i.onFailure(p)
                                }
                            }
                        },
                        onFailure: function(p) {
                            if (i && i.onFailure) {
                                i.onFailure(p)
                            }
                        }
                    });
                    if (m) {
                        if (m && m.code === 0) {
                            if (i && i.onComplete) {
                                setTimeout(function() {
                                    i.onComplete(m)
                                }, 5)
                            }
                        } else {
                            if (i && i.onFailure) {
                                i.onFailure(m)
                            }
                        }
                    }
                    break
            }
            return k
        },
        parse: function(m, k) {
            var j = ["north", "west", "south", "east", "other"],
                i = [],
                l = [];
            h.log("Compass refresh: parsing apps");
            if ((!m.id || m.id !== this.lastId)) {
                if (!this.myappsObjectSet) {
                    this.lastId = m.id
                }
                if (m.favorite && m.favorite.apps) {
                    i = m.favorite.apps.split(",")
                }
                j.forEach(function(n) {
                    var o = m[n];
                    if (o) {
                        o.forEach(function(p, q) {
                            var r = (q === 1);
                            p.apps.forEach(function(s) {
                                s.favoriteOrder = i.indexOf(s.id);
                                s.isFavorite = s.favoriteOrder !== -1;
                                s.lma = r;
                                s.quadrant = n;
                                if (s.remoteInfos === undefined) {
                                    s.remoteInfos = undefined
                                }
                                l.push(s)
                            })
                        })
                    }
                });
                if (m.roles) {
                    m.roles.role.forEach(function(n) {
                        n.favoriteOrder = -1;
                        n.isFavorite = false;
                        n.lma = false;
                        l.push(n)
                    })
                }
                m.changed = true
            } else {
                k.remove = false;
                m.changed = false
            }
            return l
        },
        set: function() {
            this.added.length = 0;
            this._parent.apply(this, arguments);
            if (this.added.length > 0) {
                this.dispatchEvent("onMultipleAdd", [this.added])
            }
        },
        onAdd: function(i) {
            this.added.push(i)
        },
        initFetch: function() {
            if (this.master) {
                this.filterAndAdd(this.master)
            }
        },
        removeApp: function(i) {
            if (this.get(i)) {
                this.remove(i)
            }
        },
        filterAndAdd: function(j) {
            var i = j.filter(function(k) {
                return k.matchesFilters(this.filters)
            }, this);
            if (i.length > 0) {
                this.add(i)
            }
        },
        changeApp: function(k) {
            var j = k._changed,
                i = this.filterKeys.some(function(l) {
                    return h.is(j[l])
                });
            if (i) {
                if (k.matchesFilters(this.filters)) {
                    this.add(k);
                    this.dispatchEvent("onMultipleAdd", [
                        [k]
                    ])
                } else {
                    this.removeApp(k)
                }
            }
            if (this.get(k) && h.is(j[this.comparator])) {
                this.sort();
                this.dispatchEvent("onReorderApp", k)
            }
        },
        filterApps: function(i) {
            return this.filter(function(j) {
                return j.matchesFilters(i)
            })
        },
        getPrevious: function(j) {
            var i = this.indexOf(j) - 1;
            if (i < 0) {
                return null
            }
            return this.at(i)
        },
        count: function(i) {
            return this.reduce(function(j, k) {
                return k.matchesFilters(i) ? j + 1 : j
            }, 0)
        },
        setObject: function(i) {
            this.myappsObjectSet = i.hasOwnProperty("objectId");
            if (this.myappsObjectSet) {
                this.forEach(function(j) {
                    j.setObject(i)
                })
            } else {
                h.log("Object is not valid for setObject")
            }
        },
        resetObject: function() {
            this.forEach(function(i) {
                i.resetObject()
            })
        },
        saveFavorites: function() {
            var i = this.pluck("id").join(",");
            f.request({
                url: f.setFavoriteUrl,
                method: "POST",
                contentType: "application/x-www-form-urlencoded",
                postData: {
                    value: i
                },
                onComplete: function() {},
                onFailure: function() {}
            })
        },
        saveRoles: function(j) {
            var i = this.where({
                    active: true
                }),
                k = i.map(function(l) {
                    return l.get("process")
                }).join(",");
            f.setPreference({
                name: "active_roles",
                value: k,
                onComplete: function() {
                    if (j && j.onComplete) {
                        j.onComplete()
                    }
                }
            })
        },
        getSelectedRoleApps: function() {
            var i = [],
                j = this.where({
                    active: true
                });
            if (this.length === j.length) {
                return null
            }
            j.forEach(function(k) {
                k.get("appList").forEach(function(l) {
                    c.pushUnique(i, l)
                })
            });
            return (i)
        }
    };
    return e.extend(b)
});
define("DS/i3DXCompass/Controls/Settings", ["UWA/Core", "DS/i3DXCompass/Tools", "DS/i3DXCompass/InstallManager", "DS/UIKIT/DropdownMenu"], function(f, c, b, a) {
    var e = f.i18n,
        d = a.extend({
            customClass: "compass-app-settings",
            iconClass: "fonticon fonticon-check",
            clickInstallId: "clickinstall",
            clickInstallRemoteId: "clickremote",
            init: function(t) {
                this.app = t.app;
                var m = this,
                    k = t.data,
                    q = k.installs,
                    s = k.remoteInstall,
                    l = k.platforms,
                    r = t.preferredInstallId,
                    o = t.processes,
                    p = t.onlineInstallable,
                    h = q ? q.map(function(u) {
                        return this.getInstallItem(u, r, o, p, false, t.shortcutEnabled, t.additionalContent)
                    }, this) : null,
                    g = h && h.length === 0,
                    j = (l && (!g || s)) ? l.map(this.getPlatformItem, this) : null,
                    n = j || [],
                    i;
                if (g) {
                    h.push({
                        className: "header no-install",
                        html: k.error ? k.error : f.String.format(e("noinstall"), t.appTitle)
                    });
                    if (p) {
                        h.push({
                            className: "download-link",
                            name: this.clickInstallId,
                            text: e("clickinstall")
                        })
                    }
                }
                if (h && t.remoteEnabled) {
                    h.push({
                        className: "divider"
                    });
                    if (s) {
                        h.push(this.getInstallItem(s, r, [], p, true, false))
                    } else {
                        if (p) {
                            h.push({
                                className: "download-link",
                                name: this.clickInstallRemoteId,
                                text: f.String.format(e("install"), t.remoteAppTitle)
                            })
                        }
                    }
                }
                if (j && h) {
                    n.push({
                        className: "divider"
                    })
                }
                n = n.concat(h || []);
                i = {
                    items: n,
                    altPosition: function() {
                        var C = m.elements,
                            A = C.container.offsetWidth,
                            v = C.container.offsetHeight,
                            z = window.innerWidth,
                            w = window.innerHeight,
                            B = 20,
                            u = t.position.x,
                            D = t.position.y;
                        if (u + A + B > z) {
                            u = z - A - B
                        }
                        if (D + v + B > w) {
                            D = w - v - B
                        }
                        return {
                            x: u,
                            y: D
                        }
                    },
                    renderTo: document.body,
                    target: t.target,
                    events: t.events,
                    shortcutEnabled: t.shortcutEnabled
                };
                this._parent(i);
                this.trigger = t.trigger
            },
            buildSkeleton: function() {
                var g;
                this._parent();
                if (this.menus) {
                    g = this.menus[0];
                    this.items = g.items;
                    this.items.forEach(this.editItem, this)
                }
                this.elements.container.addClassName(this.customClass)
            },
            editItem: function(i) {
                var h = this,
                    g = i.elements.container;
                if (i.html) {
                    g.setContent(i.html)
                }
                if (i.type === "install") {
                    if (i.shortcut) {
                        g.addClassName("has-shortcut");
                        f.createElement("div", {
                            "class": "action shortcut",
                            title: e("createshortcut"),
                            events: {
                                click: function() {
                                    h.dispatchEvent("onClickShortcut", i.name)
                                }
                            }
                        }).inject(g)
                    }
                    if (i.update || i.newProc) {
                        g.addClassName("has-update");
                        f.createElement("div", {
                            "class": "action update" + (i.update ? (i.mand ? " mand" : "") : (i.newProc ? " new" : "")),
                            title: i.update ? (i.mand ? e("updateneeded") : e("updateavailable")) : (i.newProc ? e("newprocessesavailable") : ""),
                            events: {
                                click: function() {
                                    h.dispatchEvent("onClickUpdate", [i.name, i.update]);
                                    h.hideSettings()
                                }
                            }
                        }).inject(g)
                    }
                    if (i.additionalContent && !i.mand) {
                        g.addClassName("has-additional");
                        f.createElement("div", {
                            "class": "fonticon fonticon-install",
                            title: e("additionalcontentavailable"),
                            events: {
                                click: function() {
                                    h.dispatchEvent("onClickAdditional", i.name);
                                    h.hideSettings()
                                }
                            }
                        }).inject(g)
                    }
                }
            },
            getPlatformItem: function(g) {
                return {
                    text: g.name,
                    name: g.id,
                    type: "platform",
                    className: "platform-item",
                    icon: g.preferred ? this.iconClass : null,
                    preferred: g.preferred
                }
            },
            getInstallItem: function(l, g, i, h, k, n, j) {
                var m = k ? g === "remote" : l.get("id") === g;
                return {
                    text: k ? e("remoteit") : l.get("name"),
                    name: k ? "remote" : l.get("id"),
                    type: "install",
                    className: "install-item",
                    icon: m ? this.iconClass : null,
                    additionalContent: j && l.get("isContentInstalled") !== true,
                    preferred: m,
                    mand: (l.get("status") === b.INSTALL_FORCE_UPDATE) && !this.app.isCustom(),
                    update: l.get("update") && !this.app.isCustom(),
                    shortcut: n,
                    newProc: h && l.get("online") && l.get("status") !== b.INSTALL_GA_ON_GA && !c.includes(l.get("processes"), i) && !this.app.isCustom()
                }
            },
            onClick: function(k, i) {
                var h = this,
                    g = i.type,
                    j = {};
                if (i.name === this.clickInstallId) {
                    this.dispatchEvent("onClickInstall");
                    this.hideSettings();
                    return
                }
                if (i.name === this.clickInstallRemoteId) {
                    this.dispatchEvent("onClickInstallRemote");
                    this.hideSettings();
                    return
                }
                this.items.forEach(function(l) {
                    if (l.type === g && l !== i && l.preferred) {
                        l.icon = null;
                        l.preferred = false;
                        h.updateItem(l)
                    }
                });
                i.icon = this.iconClass;
                i.preferred = true;
                h.updateItem(i);
                j[i.type] = i.name;
                this.dispatchEvent("onPlatformChange", j)
            },
            onClickOutside: function(h) {
                var g = h.target;
                if (g && g === this.trigger) {
                    h.stopPropagation()
                }
                this.hideSettings()
            },
            hideSettings: function() {
                var g = {};
                this.items.forEach(function(h) {
                    if (h.preferred) {
                        g[h.type] = h.name
                    }
                });
                this.dispatchEvent("onHideSettings", g);
                this.hide();
                this.destroy()
            },
            updateItem: function(g) {
                if (g.icon) {
                    if (g.elements.icon) {
                        g.elements.icon.className = g.icon
                    } else {
                        g.elements.icon = f.createElement("span", {
                            "class": g.icon
                        }).inject(g.elements.container, "top")
                    }
                } else {
                    if (g.elements.icon) {
                        g.elements.icon.destroy();
                        delete g.elements.icon
                    }
                }
            }
        });
    return d
});
define("DS/i3DXCompass/Controls/ScrollAccordion", ["UWA/Core", "UWA/Utils/Client", "UWA/Event", "UWA/Element", "UWA/Controls/Abstract", "DS/i3DXCompass/Tools", "DS/i3DXCompass/UsageTracker", "DS/UIKIT/Tooltip", "DS/UIKIT/SuperModal"], function(f, c, j, b, g, e, i, h, a) {
    var k = e.i18n,
        d = {
            defaultOptions: {
                className: "",
                sections: 1,
                scrollable: false,
                scrollSize: 40
            },
            className: "compass-scroll-accordion",
            scrolled: 0,
            sumHeight: 0,
            containerHeight: 0,
            scrollbarHeight: 0,
            scrollMove: 0,
            moved: false,
            scrolling: false,
            titleHeight: 52,
            adjustDelay: 300,
            adjustTimeoutId: null,
            fromAdjusScrolls: 0,
            init: function(l) {
                this._parent(l);
                if (!l.data) {
                    var m = l.sections - 1,
                        n = [];
                    for (; m >= 0; m--) {
                        n.push({
                            title: "",
                            html: null,
                            open: true,
                            visible: true
                        })
                    }
                    this.options.data = n
                } else {
                    if (l.sections !== l.data.length) {
                        this.options.sections = this.options.data.length
                    }
                }
                this.sectionsByName = {};
                this.buildSkeleton()
            },
            buildSkeleton: function() {
                var w = this,
                    m = this.elements = {
                        container: f.createElement("div", {
                            "class": this.className + " smooth-scroll " + this.options.className
                        }),
                        wrapper: f.createElement("div", {
                            "class": this.className + "-wrapper" + (e.getOs() === "ios" ? " iosVersion" : "")
                        }),
                        sections: [],
                        sectionTitles: [],
                        sectionContents: [],
                        sectionContentWrappers: []
                    },
                    s = 0,
                    n = this.options.sections,
                    r, B, p, y, C, v, t, x, u, q, z, o, A = this.styles = [];
                z = f.createElement("div", {
                    "class": "delete-favorite",
                    html: {
                        tag: "span",
                        "class": "fonticon fonticon-trash"
                    },
                    events: {
                        click: function(l) {
                            f.Event.stop(l);
                            w._confirmModal()
                        }
                    }
                });
                new h({
                    position: "right",
                    target: z,
                    body: k("clickRemoveFavorite")
                });
                for (; s < n; s++) {
                    r = this.options.data[s];
                    o = r.parent ? this.getParentElm(r.parent).elm : null;
                    y = f.createElement("span", {
                        "class": this.className + "-section-title-icon " + ("fonticon fonticon-" + r.icon || "")
                    });
                    C = f.createElement("span", {
                        "class": this.className + "-section-title-text",
                        text: r.title
                    });
                    p = f.createElement("div", {
                        "class": this.className + "-section-title-wrapper" + (r.parent ? " child" : ""),
                        html: [y, C, {
                            tag: "span",
                            "class": this.className + "-section-title-arrow fonticon fonticon-right-dir"
                        }, {
                            tag: "span",
                            "class": this.className + "-section-title-arrow fonticon fonticon-down-dir"
                        }, (r.id === "favorite") ? (r.html[0].collection.length > 0 ? z : z.addClassName("hide")) : ""]
                    });
                    u = m.sectionTitles.length;
                    q = r.open ? 0 : this.marginTopClosedSection(r);
                    B = f.createElement("div", {
                        "class": this.className + "-section-title" + (r.icon ? " has-icon" : ""),
                        html: p,
                        "data-sectionnum": u
                    });
                    if (r.parent) {
                        B.setAttribute("data-parent", r.parent)
                    }
                    v = f.createElement("div", {
                        "class": this.className + "-section-content",
                        "data-sectionnum": u,
                        html: r.html
                    });
                    t = f.createElement("div", {
                        "class": this.className + "-section-content-wrapper",
                        html: v,
                        styles: {
                            "margin-top": q
                        }
                    });
                    m.sectionTitles.push({
                        container: B,
                        icon: y,
                        text: C
                    });
                    m.sectionContents.push(v);
                    m.sectionContentWrappers.push(t);
                    x = f.createElement("div", {
                        "class": this.className + "-section" + (r.open ? "" : " closed") + ((o && !o.open) ? " hidden" : "") + (r.name ? (" " + r.name) : "") + (r.visible === false ? " not-visible" : ""),
                        attributes: {
                            "data-id": r.id
                        },
                        html: [B, {
                            "class": this.className + "-section-content-container",
                            html: t
                        }]
                    });
                    m.sections.push(x);
                    x.inject(m.wrapper);
                    A.push({
                        margin: q,
                        height: 0,
                        closed: !r.open,
                        scrollable: !!r.open,
                        visible: r.visible !== false
                    });
                    if (r.name) {
                        this.sectionsByName[r.name] = u
                    }
                }
                m.wrapper.inject(m.container);
                if (this.options.scroller) {
                    m.scrollbar = f.createElement("div", {
                        "class": this.className + "-scrollbar"
                    });
                    m.scrollbarWrapper = f.createElement("div", {
                        "class": this.className + "-scrollbar-wrapper",
                        html: m.scrollbar
                    }).inject(m.container)
                }
                this.initEvents()
            },
            marginTopClosedSection: function(o) {
                var l = e.isSmartphone() ? 2 : 4,
                    n = -10000,
                    p = 53;
                for (var m = 0; m < o.html.length; m++) {
                    if (o.html[m].collection) {
                        n = Math.min(-(o.html[m].collection.length * (88 + p) / l), n)
                    }
                }
                return n
            },
            setContent: function(n, l) {
                var o = this.getSectionNum(n),
                    m = this.elements.sectionContents[o];
                if (m) {
                    m.setContent(l)
                }
            },
            setTitle: function(p, s, o) {
                var r = this.getSectionNum(p),
                    n, l, m, q;
                if (r) {
                    n = this.elements.sectionTitles[r];
                    if (n) {
                        l = n.container;
                        m = n.text;
                        q = n.icon;
                        m.setText(s);
                        if (o) {
                            l.addClassName("has-icon");
                            q.className = this.className + "-section-title-icon";
                            q.addClassName(o)
                        } else {
                            l.removeClassName("has-icon")
                        }
                    }
                }
            },
            initEvents: function() {
                var n = this.eventHandler = this.handleEvent.bind(this),
                    l = this.scrollbarEventHandler = this.handleScrollbarEvent.bind(this),
                    m = this.titleEventHandler = this.handleTitleEvent.bind(this),
                    o = this.eventNames = {
                        start: "touchstart",
                        move: "touchmove",
                        stop: "touchend",
                        cancel: "touchcancel"
                    };
                if (!c.Features.touchEvents) {
                    if (window.PointerEvent) {
                        o = this.eventNames = {
                            start: "pointerdown",
                            move: "pointermove",
                            stop: "pointerup",
                            cancel: "pointerup"
                        };
                        this.elements.wrapper.setStyle("touch-action", "none")
                    } else {
                        if (window.MSPointerEvent) {
                            o = this.eventNames = {
                                start: "MSPointerDown",
                                move: "MSPointerMove",
                                stop: "MSPointerUp",
                                cancel: "MSPointerUp"
                            };
                            this.elements.wrapper.setStyle("touch-action", "none")
                        }
                    }
                }
                if (this.options.scroller) {
                    this.elements.container.addEvent("mousewheel", n);
                    this.elements.sectionContents.invoke("addEvent", "transitionEnd", n);
                    this.elements.container.addEvent(o.start, n);
                    this.elements.container.addEvent("click", n, false, 0, true);
                    this.elements.scrollbar.addEvent("mousedown", l);
                    this.elements.scrollbarWrapper.addEvent("transitionEnd", l)
                }
                this.elements.sectionContents.invoke("addEvent", "resize", n);
                this.elements.container.addEvent("resize", n);
                this.elements.sectionTitles.forEach(function(p) {
                    p.container.addEvent("click", m)
                })
            },
            handleEvent: function(m) {
                var l, n = this.eventNames;
                f.log("event:" + m.type);
                if (m.pointerType === undefined || m.pointerType !== "mouse") {
                    switch (m.type) {
                        case n.start:
                            l = this.dispatchEvent("onScrollInit", [m]);
                            break;
                        case n.move:
                            l = this.dispatchEvent("onScrollMove", [m]);
                            break;
                        case n.cancel:
                        case n.stop:
                            l = this.dispatchEvent("onScrollStop", [m]);
                            break;
                        case "DOMMouseScroll":
                        case "onmousewheel":
                        case "mousewheel":
                            l = this.dispatchEvent("onMouseWheel", [m]);
                            break;
                        case "onclick":
                        case "click":
                            l = this.dispatchEvent("onClick", [m]);
                            break;
                        case "transitionEnd":
                        case "otransitionend":
                        case "MSTransitionEnd":
                        case "transitionend":
                        case "webkitTransitionEnd":
                            l = this.dispatchEvent("onScrollEnd", [m]);
                            break;
                        case "resize":
                            if (m.target === this.elements.container) {
                                l = this.dispatchEvent("onContainerResize", [m])
                            } else {
                                l = this.dispatchEvent("onContentResize", [m])
                            }
                            break
                    }
                }
                return l
            },
            handleScrollbarEvent: function(m) {
                var l;
                switch (m.type) {
                    case "mousedown":
                        l = this.dispatchEvent("onScrollbarInit", [m]);
                        break;
                    case "mousemove":
                        l = this.dispatchEvent("onScrollbarMove", [m]);
                        break;
                    case "mouseup":
                        l = this.dispatchEvent("onScrollbarStop", [m]);
                        break;
                    case "transitionEnd":
                    case "otransitionend":
                    case "MSTransitionEnd":
                    case "transitionend":
                    case "webkitTransitionEnd":
                        l = this.dispatchEvent("onScrollbarToggle", [m]);
                        break
                }
                return l
            },
            handleTitleEvent: function(m) {
                var l;
                switch (m.type) {
                    case "onclick":
                    case "click":
                        l = this.dispatchEvent("onTitleClick", [m]);
                        break
                }
                return l
            },
            onTitleClick: function(m) {
                var l = this,
                    o = m.currentTarget.getData("sectionnum"),
                    n = this.elements.container.getElements("[data-parent=" + m.currentTarget.getParent().getAttribute("data-id") + "]");
                this.toggleSection(o);
                n.forEach(function(q) {
                    var p = l.elements.sections[o];
                    if (p.hasClassName("closed")) {
                        l.closeSection(q.getData("sectionnum"));
                        if (!p.hasClassName("hidden")) {
                            q.addClassName("hidden")
                        }
                    } else {
                        l.openSection(q.getData("sectionnum"));
                        q.removeClassName("hidden");
                        if (q.getParent().hasClassName("hidden")) {
                            q.getParent().removeClassName("hidden")
                        }
                    }
                });
                if (n.length) {
                    setTimeout(function() {
                        l.dispatchEvent("onToggleSection")
                    }, 800)
                }
            },
            onClick: function(l) {
                if (this.moved) {
                    l.stopPropagation()
                }
            },
            onMouseWheel: function(l) {
                var m = j.wheelDelta(l) * this.options.scrollSize;
                f.log(j.wheelDelta(l));
                f.log(j.wheelDelta(this.options.scrollSize));
                f.log(m);
                this.scrolling = true;
                this.scroll(m);
                l.preventDefault()
            },
            onScrollInit: function(n) {
                var m = j.getPosition(n).y,
                    o = this.eventNames,
                    l = this.eventHandler;
                this.scrollMove = m;
                this.refMove = m;
                this.refTime = Date.now();
                this.firstMove = m;
                this.moved = false;
                b.addEvent.call(window, o.move, l);
                b.addEvent.call(window, o.stop, l);
                b.addEvent.call(window, o.cancel, l);
                this.elements.container.removeClassName("smooth-scroll");
                this.scrolling = true
            },
            onScrollMove: function(m) {
                var l = j.getPosition(m).y,
                    o = l - this.scrollMove,
                    n = Date.now();
                this.scrollMove = l;
                this.scroll(o);
                if (n - this.refTime > 300) {
                    this.refMove = l;
                    this.refTime = n
                }
                if (!this.moved && Math.abs(l - this.firstMove) > 10) {
                    this.moved = true
                }
                m.preventDefault()
            },
            onScrollStop: function(p) {
                var r = this.eventNames,
                    m = this.eventHandler,
                    o, q, l, n;
                b.removeEvent.call(window, r.move, m);
                b.removeEvent.call(window, r.stop, m);
                b.removeEvent.call(window, r.cancel, m);
                this.elements.container.addClassName("smooth-scroll");
                if (p) {
                    o = j.getPosition(p).y;
                    q = Date.now() - this.refTime;
                    l = o - this.refMove;
                    n = (l / q) * 300;
                    if (Math.abs(l) > 10 && q <= 300) {
                        this.scroll(n)
                    } else {
                        this.scrolling = false
                    }
                }
            },
            onScrollbarInit: function(n) {
                var m = j.getPosition(n).y,
                    l = this.scrollbarEventHandler;
                if (j.whichButton(n) === 0) {
                    this.scrollMove = m;
                    b.addEvent.call(window, "mousemove", l);
                    b.addEvent.call(window, "mouseup", l);
                    this.elements.container.removeClassName("smooth-scroll");
                    this.elements.scrollbar.addClassName("grabbing")
                }
                n.stopPropagation()
            },
            onScrollbarMove: function(n) {
                var m = j.getPosition(n).y,
                    l = this.scrollableHeight,
                    o = (m - this.scrollMove) * (l - this.sumHeight) / (this.scrollbarWrapperHeight - this.scrollbarHeight);
                this.scrollMove = m;
                this.scroll(o);
                n.preventDefault()
            },
            onScrollbarStop: function() {
                var l = this.scrollbarEventHandler;
                b.removeEvent.call(window, "mousemove", l);
                b.removeEvent.call(window, "mouseup", l);
                this.elements.container.addClassName("smooth-scroll");
                this.elements.scrollbar.removeClassName("grabbing");
                this.scrolling = false
            },
            onScrollbarToggle: function() {
                var m = this.elements.scrollbarWrapper,
                    l = m.getStyle("opacity");
                if (l === 0) {
                    m.hide()
                }
            },
            onScrollEnd: function() {
                this.scrolling = false
            },
            onContainerResize: function() {
                var o = parseInt(this.elements.wrapper.getStyle("height"), 10),
                    n = this.styles,
                    l = 0,
                    m;
                if (o !== this.containerHeight && this.options.scroller) {
                    this.containerHeight = o;
                    this.scrollbarWrapperHeight = o - 12;
                    for (m = this.styles.length - 1; m >= 0; m--) {
                        if (n[m].visible) {
                            l++
                        }
                    }
                    f.log("containerHeight:" + o);
                    f.log("visibleSections" + l);
                    f.log("this.titleHeight" + this.titleHeight);
                    this.scrollableHeight = o - l * this.titleHeight;
                    this.dispatchEvent("onAdjustScrolls")
                }
            },
            onContentResize: function(q) {
                var n = q.target,
                    p = n.getData("sectionnum"),
                    m = this.elements.sectionContentWrappers[p],
                    o = this.styles[p],
                    r = o.margin,
                    s, l = parseInt(n.getStyle("height"), 10);
                if (l.toString() === "NaN") {
                    l = o.height
                }
                if (l !== o.height) {
                    o.height = l;
                    m.setStyle("height", l);
                    if (!o.scrollable || r < -l) {
                        m.setStyle("margin-top", -l);
                        s = o.margin = -l
                    }
                    if (this.options.scroller) {
                        this.sumHeight = this.styles.reduce(function(t, u) {
                            return t + ((u.closed && !u.scrollable && u.visible) ? 0 : u.height)
                        }, 0);
                        if (r < -l && o.scrollable) {
                            this.scrolled += r - o.margin
                        }
                        this.dispatchEvent("onAdjustScrolls")
                    }
                }
            },
            scroll: function(w) {
                var t = w > 0,
                    q = this.elements.sectionContentWrappers,
                    v, u = this.scrollableHeight,
                    z = this.sumHeight,
                    l = this.scrolled,
                    n, x, p, y, s, r, o = t ? q.length - 1 : 0,
                    m = t ? -1 : q.length;
                if (typeof u === "undefined") {
                    this.onContainerResize();
                    u = this.scrollableHeight
                }
                for (; o !== m; t ? o-- : o++) {
                    y = this.styles[o];
                    v = q[o];
                    s = u - (z - l);
                    if (w === 0 || (w < 0 && s >= 0)) {
                        break
                    }
                    if (y.scrollable && y.visible) {
                        n = y.margin;
                        x = y.height;
                        s = w < 0 ? Math.max(w, s) : w;
                        p = Math.max(Math.min(n + s, 0), -x);
                        v.setStyle("margin-top", p);
                        y.margin = p;
                        r = (p - n);
                        w = w - r;
                        l -= r
                    }
                }
                if (this.scrolled !== l) {
                    this.dispatchEvent("onScroll", l - this.scrolled)
                }
                this.scrolled = l;
                if (this.fromAdjusScrolls !== 1) {
                    if (w >= 0 && l < 1) {
                        this.dispatchEvent("onScrollTop")
                    } else {
                        this.dispatchEvent("onScrollDown")
                    }
                }
                if (this.fromAdjusScrolls === 1) {
                    this.fromAdjusScrolls = 0
                }
                this.updateScrollbarPosition();
                this.updateSectionStyles()
            },
            onAdjustScrolls: function() {
                if (!this.adjustTimeoutId) {
                    this.adjustTimeoutId = setTimeout(this.adjustScrolls.bind(this), this.adjustDelay)
                }
            },
            adjustScrolls: function() {
                var l = this.scrollableHeight - (this.sumHeight - this.scrolled);
                this.adjustTimeoutId = null;
                if (l > 0) {
                    this.fromAdjusScrolls = 1;
                    this.scroll(l)
                }
                this.updateScrollbar();
                this.updateScrollbarPosition();
                this.updateSectionStyles()
            },
            unScroll: function(p) {
                var o = this.styles,
                    n, l = o.length - 1,
                    m = 0;
                for (; l >= p; l--) {
                    n = o[l];
                    if (n.scrollable && n.visible) {
                        m -= n.margin
                    }
                }
                this.scroll(m)
            },
            updateScrollbar: function() {
                var m = this.scrollableHeight,
                    o = this.sumHeight,
                    p, n = this.elements,
                    l = n.scrollbarWrapper,
                    q = n.scrollbar;
                if (m >= o) {
                    l.setStyle("opacity", 0);
                    n.container.removeClassName("has-scroller")
                } else {
                    p = m * this.scrollbarWrapperHeight / o;
                    q.setStyle("height", p);
                    l.show();
                    l.setStyle("opacity", 1);
                    this.scrollbarHeight = p;
                    n.container.addClassName("has-scroller")
                }
            },
            updateScrollbarPosition: function() {
                var l = this.scrollableHeight,
                    n = this.scrollbarHeight,
                    m = (n - this.scrollbarWrapperHeight) * (this.scrolled) / (l - this.sumHeight);
                this.elements.scrollbar.setStyle("top", m)
            },
            updateSectionStyles: function() {
                var z = this.styles,
                    m, v, n, y = this.elements.sections,
                    x, p = z.length,
                    s = p - 1,
                    q, u, r, o, w;
                for (; s >= 0; s--) {
                    m = z[s];
                    v = null;
                    x = y[s];
                    n = m.height + m.margin < 1;
                    w = !(this.options.data && !this.options.data[s].parent);
                    r = (this.options.data && !this.options.data[s].parent && !w) ? this.getChildrenIndex(s) : ((this.options.data && this.options.data[s].parent) ? this.getChildrenIndex(this.getParentElm(this.options.data[s].parent).index) : []);
                    u = true;
                    o = true;
                    for (var t = 0; t < r.length; t++) {
                        if ((z[r[t]].height + z[r[t]].margin < 1) === false) {
                            if (w) {
                                o = false
                            } else {
                                u = false
                            }
                        }
                    }
                    if (m.scrollable) {
                        if (n && u) {
                            x.addClassName("closed");
                            m.closed = true
                        } else {
                            x.removeClassName("closed");
                            m.closed = false
                        }
                    }
                    if (n) {
                        for (q = s + 1; q < p; q++) {
                            if (z[q].scrollable) {
                                v = z[q];
                                break
                            }
                        }
                        if ((x.getChildren().length && x.getChildren()[0].getAttribute("data-parent") && x.hasClassName("closed")) || (w && o)) {
                            if (x.getChildren().length) {
                                x.getChildren()[0].addClassName("hidden")
                            }
                        }
                        if (v && v.margin <= -1) {
                            if (x.getAttribute("data-parent")) {
                                x.addClassName("scrolled")
                            }
                        } else {
                            if (x.getAttribute("data-parent")) {
                                x.removeClassName("scrolled")
                            }
                            if (x.getChildren().length && x.getChildren()[0].getAttribute("data-parent") && !this.elements.container.getElement("[data-id=" + x.getChildren()[0].getAttribute("data-parent") + "]").hasClassName("closed")) {
                                x.getChildren()[0].removeClassName("hidden")
                            }
                        }
                    } else {
                        if (x.getAttribute("data-parent")) {
                            x.removeClassName("scrolled")
                        }
                        if (x.getChildren().length && x.getChildren()[0].getAttribute("data-parent") && !this.elements.container.getElement("[data-id=" + x.getChildren()[0].getAttribute("data-parent") + "]").hasClassName("closed")) {
                            x.getChildren()[0].removeClassName("hidden")
                        }
                    }
                }
            },
            toggleSection: function(s) {
                var q = this.elements.sectionContentWrappers[s],
                    u = this.elements.sections[s],
                    o = this.styles[s],
                    n = o.closed,
                    v = o.scrollable,
                    r = this.options.scroller,
                    p = n ? 0 : -o.height;
                o.closed = !n;
                o.scrollable = n;
                if (r && n) {
                    if (!v) {
                        this.sumHeight += o.height;
                        this.scrolled -= o.margin
                    }
                    this.updateScrollbar();
                    this.unScroll(s)
                } else {
                    u.toggleClassName("closed");
                    q.setStyle("margin-top", p);
                    if (r) {
                        if (n) {
                            this.sumHeight += o.height
                        } else {
                            this.sumHeight -= o.height;
                            this.scrolled += o.margin
                        }
                        o.margin = p
                    }
                    this.dispatchEvent("onAdjustScrolls")
                }
                var m = u.getAttribute("data-id"),
                    t = i._IDS.compass["section"];
                if (n) {
                    var l = "Open"
                } else {
                    var l = "Close"
                }
                i.inc(t, l, m);
                this.dispatchEvent("onToggleSection")
            },
            showSection: function(m) {
                var o = this.getSectionNum(m),
                    n = this.elements.sections[o],
                    l = this.styles[o];
                f.log("show section: " + m);
                if (!l.visible) {
                    l.visible = true;
                    this.scrollableHeight -= this.titleHeight;
                    if (l.scrollable) {
                        this.sumHeight += l.height;
                        this.scrolled -= l.margin
                    }
                    n.removeClassName("not-visible");
                    this.dispatchEvent("onAdjustScrolls")
                }
            },
            hideSection: function(m) {
                var o = this.getSectionNum(m),
                    n = this.elements.sections[o],
                    l = this.styles[o];
                f.log("hide section: " + m);
                if (l.visible) {
                    l.visible = false;
                    this.scrollableHeight += this.titleHeight;
                    if (l.scrollable) {
                        this.sumHeight -= l.height;
                        this.scrolled += l.margin
                    }
                    n.addClassName("not-visible");
                    this.dispatchEvent("onAdjustScrolls")
                }
            },
            getSectionNum: function(l) {
                if (f.typeOf(l) === "string") {
                    return this.sectionsByName[l]
                }
                return l
            },
            closeSection: function(l) {
                var m = this.getSectionNum(l);
                if (!this.styles[m].closed) {
                    this.toggleSection(m)
                }
            },
            openSection: function(l) {
                var m = this.getSectionNum(l);
                if (this.styles[m].closed) {
                    this.toggleSection(m)
                }
            },
            getVisibleHeight: function(m) {
                var n = this.getSectionNum(m),
                    l;
                if (n >= this.options.sections) {
                    return
                }
                l = this.styles[n];
                return l.height + l.margin
            },
            showTempHeight: function(n, l) {
                var p = this.getSectionNum(n),
                    o = this.getVisibleHeight(p),
                    m = this.styles[p];
                if (l > o) {
                    this.elements.sectionContentWrappers[p].setStyle("margin-top", (l - m.height))
                }
            },
            hideTempHeight: function(m) {
                var n = this.getSectionNum(m),
                    l = this.styles[n];
                this.elements.sectionContentWrappers[n].setStyle("margin-top", l.margin)
            },
            isScrolling: function() {
                return this.scrolling
            },
            _confirmModal: function() {
                var m = this,
                    l = new a({
                        closable: false
                    });
                l.confirm(k("confirmRemoveFavorite"), k("confirmation"), function(n) {
                    if (n) {
                        m.dispatchEvent("onRemoveAllFavoriteApp")
                    }
                })
            },
            hideDeleteZone: function() {
                f.log("hideDeleteZone");
                this.deleteZone.hide()
            },
            hideDeleteZone1: function() {
                f.log("hideDeleteZone1");
                this.deleteZone.hide()
            },
            getPositionSection: function(m) {
                var l;
                this.options.data.forEach(function(n, o) {
                    if (n.id === m) {
                        l = o
                    }
                });
                return l
            },
            getParentElm: function(m) {
                var n = this.options.data;
                for (var l = 0; l < n.length; l++) {
                    if (n[l].id === m) {
                        return {
                            index: l,
                            elm: n[l]
                        }
                    }
                }
                return null
            },
            getChildrenIndex: function(m) {
                var o = this.options.data,
                    l = [],
                    p = m;
                for (var n = m + 1; n < o.length; n++) {
                    if (o[m].id === o[n].parent) {
                        l.push(n)
                    } else {
                        return l
                    }
                }
                return l
            }
        };
    return g.extend(d)
});
define("DS/i3DXCompass/View/MyApps", ["UWA/Core", "UWA/Utils/Client", "UWA/Class/View", "UWA/Controls/Accordion", "UWA/Controls/Scroller", "UWA/Class/Collection", "DS/i3DXCompass/Tools", "DS/i3DXCompass/EventManager", "DS/i3DXCompass/Data", "DS/i3DXCompass/DaemonManager", "DS/i3DXCompass/InstallManager", "DS/i3DXCompass/TopBarManager", "DS/i3DXCompass/Collection/AppList", "DS/i3DXCompass/View/AppListView", "DS/i3DXCompass/View/SuggestionListView", "DS/i3DXCompass/Controls/Message", "DS/i3DXCompass/Controls/Settings", "DS/i3DXCompass/Controls/ScrollAccordion", "DS/i3DXCompass/Controls/Search", "DS/i3DXCompass/Controls/IframeLoader", "DS/UIKIT/Modal", "DS/UIKIT/Spinner", "DS/UIKIT/Input/Button", "DS/i3DXCompass/View/ServicesView", "DS/i3DXCompass/CacheManager", "DS/i3DXCompassServices/i3DXCompassPubSub", "DS/i3DXCompass/Types", "UWA/Utils/Cookie", "DS/i3DXCompass/Controls/Compass", "DS/i3DXCompass/UsageTracker"], function(j, t, y, f, A, E, c, C, r, x, z, d, h, v, g, a, B, o, m, D, b, n, q, k, F, e, w, l, s, u) {
    var p = j.i18n,
        i = {
            tagName: "div",
            className: "my-apps-panel",
            domEvents: {
                "click .compass-close": "close",
                "click .exp-item": "clickApp",
                "click .suggestion-item": "clickSuggestion",
                "click .settings-img": "clickSettings",
                "click .expand-img": "clickExpand",
                "click .all-roles-btn": "toggleAllRoles",
                "click .expanded-role-close, .expanded-role-title": "hideRoleDetail",
                "click .expanded-role-info": "clickRoleInfo"
            },
            currentQuadrant: "",
            itemsPerLine: 0,
            setup: function(G) {
                this.options = G;
                C.addEvents({
                    onShowMessage: this.showMessage.bind(this),
                    onHideMessage: this.hideMessage.bind(this)
                });
                this.collections = {
                    grantedRoles: new h(null, {
                        master: this.collection,
                        comparator: "title",
                        filters: [{
                            key: "isRole",
                            value: true
                        }, {
                            key: "granted",
                            value: true
                        }]
                    }),
                    visibleRoles: new h(null, {
                        master: this.collection,
                        comparator: "title",
                        filters: [{
                            key: "isRole",
                            value: true
                        }, {
                            key: "granted",
                            value: true
                        }, {
                            key: "visible",
                            value: true
                        }]
                    })
                };
                this.views = {};
                this.sugCollection = G.sugCollection
            },
            render: function() {
                var H = this.container,
                    G = this.maskElmt = j.createElement("div", {
                        "class": "compass-loading",
                        html: new n({
                            visible: true,
                            className: "compass-spinner"
                        })
                    });
                G.inject(H);
                if (this.options.closable) {
                    H.appendChild(j.createElement("button", {
                        "class": "close compass-close fonticon fonticon-cancel",
                        attributes: {
                            type: "button",
                            "aria-hidden": true
                        }
                    }))
                }
                H.hide();
                return this
            },
            renderAppsView: function(L) {
                var J = this,
                    G = this.elements,
                    H = this.container,
                    P, K, O = new a(),
                    I = this.collections,
                    N, M = this.views;
                I.favorite = new h(null, {
                    master: this.collection,
                    comparator: "favoriteOrder",
                    filters: [{
                        key: "isFavorite",
                        value: true
                    }, {
                        key: "visibleQuadrant",
                        value: true
                    }]
                });
                I.platformRoles = new h(null, {
                    master: this.collection,
                    comparator: "title",
                    filters: [{
                        key: "isRole",
                        value: true
                    }, {
                        key: "granted",
                        value: false
                    }, {
                        key: "visible",
                        value: true
                    }]
                });
                I.expandedRole = new h(null, {
                    master: this.collection,
                    comparator: "title",
                    filters: [{
                        key: "isRole",
                        value: false
                    }, {
                        key: "lma",
                        value: false
                    }, {
                        key: "visibleQuadrant",
                        value: true
                    }]
                });
                I.apps = {};
                I.lma = {};
                N = I.grantedRoles.getSelectedRoleApps();
                M.favorite = new v({
                    collection: I.favorite,
                    customFilters: {
                        appIds: N
                    },
                    customClass: "favorite",
                    favorite: true,
                    draggable: true
                });
                M.visibleRoles = new v({
                    collection: I.visibleRoles,
                    customClass: "roles",
                    draggable: true
                });
                M.platformRoles = new v({
                    collection: I.platformRoles,
                    customClass: "roles not-available-roles",
                    draggable: false
                });
                M.expandedRole = new v({
                    collection: I.expandedRole,
                    customFilters: {
                        appIds: []
                    },
                    draggable: true
                });
                M.apps = {};
                M.lma = {};
                this.options.sectionData = L;
                if (G.error) {
                    G.error.destroy();
                    delete G.error
                }
                this.forEachQuadrant(function(Q) {
                    I.apps[Q] = new h(null, {
                        master: J.collection,
                        comparator: "title",
                        filters: [{
                            key: "quadrant",
                            value: Q
                        }, {
                            key: "lma",
                            value: false
                        }]
                    });
                    I.lma[Q] = new h(null, {
                        master: J.collection,
                        comparator: "title",
                        filters: [{
                            key: "quadrant",
                            value: Q
                        }, {
                            key: "lma",
                            value: true
                        }]
                    });
                    M.apps[Q] = new v({
                        collection: I.apps[Q],
                        customFilters: {
                            appIds: N
                        },
                        customClass: Q,
                        draggable: true
                    });
                    M.lma[Q] = new v({
                        collection: I.lma[Q],
                        customClass: Q
                    })
                });
                if (L.eservices && L.eservices.data && L.eservices.data.length > 0) {
                    I.eservices = new E(L.eservices.data);
                    M.eservices = new k({
                        collection: I.eservices
                    })
                }
                H.addEvent("resize", function(Q) {
                    J.resizeAccordionData(Q)
                });
                P = new m({
                    events: {
                        onSearch: function(Q) {
                            J.searchInViews(Q)
                        }
                    }
                });
                if (!j.is(F._getUser())) {
                    P.container.setStyle("visibility", "hidden")
                }
                P.inject(H);
                G.search = P;
                K = this.buildAccordion();
                j.createElement("div", {
                    "class": "compass-accordion-wrapper",
                    html: K
                }).inject(H);
                O.inject(H);
                this.appsAccordion = K;
                this.message = O;
                this.resizeAccordionData();
                I.grantedRoles.addEvent("onChange:active", this.changeRoles.bind(this));
                I.grantedRoles.addEvent("onChange:appList", this.changeRoles.bind(this));
                I.grantedRoles.addEvent("onRemove", this.changeRoles.bind(this));
                I.grantedRoles.addEvent("onMultipleAdd", this.changeRoles.bind(this))
            },
            renderSuggestionsView: function() {
                this.getSuggestionsView().renderSuggestions();
                if (this.appsAccordion && this.getSuggestionsView().collection.length > 0) {
                    this.appsAccordion.showSection("suggestions-section")
                }
            },
            getSuggestionsView: function() {
                var G = this.views;
                if (!G.suggestions) {
                    G.suggestions = new g({
                        collection: this.sugCollection
                    })
                }
                return G.suggestions
            },
            renderErrorView: function(G) {
                var H = this.elements;
                if (!H.error) {
                    H.error = j.createElement("div", {
                        "class": "compass-error",
                        text: G
                    }).inject(this.container)
                }
            },
            _needToShowMyRoles: function() {
                return this.collections.visibleRoles.length > 1 || this.collections.platformRoles.length > 0
            },
            buildAccordion: function() {
                var L, K = this,
                    N = this.options.sectionData,
                    P = false,
                    Q = [],
                    R = this.views,
                    G = this.elements,
                    H = [],
                    O = [],
                    J = G.expandedRole = j.createElement("div", {
                        tag: "div",
                        "class": "expanded-role",
                        html: [{
                            "class": "expanded-role-title"
                        }, {
                            tag: "span",
                            "class": "fonticon fonticon-left-open expanded-role-close"
                        }, {
                            tag: "span",
                            "class": "fonticon fonticon-info-circled expanded-role-info"
                        }, this.views.expandedRole.render()]
                    }),
                    M = G.allRolesBtn = j.createElement("span", {
                        "class": "fonticon fonticon-down-dir fonticon-interact all-roles-btn"
                    }),
                    I = this.getSuggestionsView(),
                    S = R.eservices;
                this.forEachQuadrant(function(T) {
                    H.push(R.apps[T].render());
                    if (P === false && R.apps[T].collection.size() > 0) {
                        P = true
                    }
                    H.push(j.createElement("div", {
                        "class": "alert-message alert-default noapps " + T,
                        text: N[T].noapps
                    }));
                    O.push(R.lma[T].render())
                });
                if (N.services && N.services.data && N.services.data.length > 0 || S) {
                    Q.push({
                        id: "wrapperMP",
                        title: p("3DEXPERIENCEMarketplace") || N.wrapperMP && N.wrapperMP.title,
                        html: S ? j.createElement("div", {
                            styles: {
                                height: 10
                            }
                        }) : new k({
                            collection: new E(N.services && N.services.data || [])
                        }).render(),
                        open: !N.wrapperMP.closed,
                        visible: true,
                        name: S ? "learn-more-about-section" : "services-section"
                    })
                }
                if (S) {
                    Q.push({
                        id: "eservices",
                        parent: "wrapperMP",
                        icon: "component",
                        title: N.eservices && N.eservices.title,
                        html: R.eservices.render(),
                        open: !N.eservices.closed,
                        visible: true,
                        name: "services-section"
                    })
                }
                if (S && N.services && N.services.data && N.services.data.length > 0) {
                    Q.push({
                        id: "services",
                        parent: "wrapperMP",
                        icon: "globe",
                        title: N.services && N.services.title || p("MyCommunityServices"),
                        html: new k({
                            collection: new E(N.services && N.services.data || [])
                        }).render(),
                        open: !N.services.closed,
                        visible: true,
                        name: "services-section"
                    })
                }
                Q.push({
                    id: "wrapperRole",
                    title: N.wrapperRole.title || p("wrapperRole"),
                    html: j.createElement("div", {
                        styles: {
                            height: 10
                        }
                    }),
                    open: !N.wrapperRole.closed,
                    visible: j.is(F._getUser()),
                    name: "learn-more-about-section"
                });
                Q.push({
                    id: "roles",
                    parent: "wrapperRole",
                    title: N.roles.title,
                    icon: "role roles",
                    html: [R.visibleRoles.render(), j.createElement("div", {
                        "class": "alert-message alert-default noapps roles",
                        text: N.roles.noapps
                    }), R.platformRoles.render(), M, J],
                    open: !N.roles.closed,
                    visible: this._needToShowMyRoles(),
                    name: "my-roles-section"
                });
                if (j.is(F._getUser())) {
                    Q.push({
                        id: "favorite",
                        parent: "wrapperRole",
                        title: N.favorite.title,
                        icon: "star-empty favorite",
                        html: [R.favorite.render(), j.createElement("div", {
                            "class": "alert-message alert-default noapps favorite",
                            text: p("dropfavoriteappshere")
                        })],
                        open: !N.favorite.closed,
                        visible: true,
                        name: "my-favorite-apps-section"
                    })
                }
                if (P) {
                    Q.push({
                        id: "apps",
                        parent: "wrapperRole",
                        title: "",
                        html: H,
                        open: !N.north.closed,
                        visible: true,
                        name: "my-apps-section"
                    })
                }
                if (j.is(j.Array.detect(O, function(T) {
                        return !T.getContent().hasClassName("empty")
                    }))) {
                    Q.push({
                        id: "lma",
                        title: N.lma.title,
                        html: O,
                        open: !N.lma.closed,
                        visible: true,
                        name: "learn-more-about-section"
                    })
                }
                Q.push({
                    id: "suggestions",
                    title: N.suggestions.title,
                    html: I.render(),
                    open: !N.suggestions.closed,
                    visible: I.suggestionsRendered,
                    name: "suggestions-section"
                });
                L = new o({
                    className: "apps-accordion",
                    sections: 4,
                    scroller: true,
                    data: Q,
                    events: {
                        onToggleSection: this.saveSections.bind(this),
                        onScrollTop: function() {
                            K.dispatchEvent("onScrollTop")
                        },
                        onScrollDown: function() {
                            K.dispatchEvent("onScrollDown")
                        },
                        onRemoveAllFavoriteApp: function() {
                            K.removeAllFavoriteApp()
                        }
                    }
                });
                return L
            },
            resizeAccordionData: function() {
                var G = this.container.getSize().width + 1 - 34,
                    H, I = this.itemsPerLine;
                H = Math.floor(G / 88);
                if (H !== I) {
                    this.container.removeClassName("items-per-line-" + I);
                    this.container.addClassName("items-per-line-" + H);
                    this.itemsPerLine = H
                }
            },
            saveSections: function() {
                var G = {};
                this.container.getElements(".compass-accordion-wrapper .compass-scroll-accordion-section").forEach(function(H) {
                    G[H.getAttribute("data-id")] = H.hasClassName("closed")
                });
                if (j.is(F._getUser())) {
                    r.saveSectionStatus(JSON.stringify(G))
                }
            },
            manageRoleSectionDisplay: function() {
                if (this.appsAccordion) {
                    if (this._needToShowMyRoles()) {
                        this.appsAccordion.showSection("my-roles-section")
                    } else {
                        this.appsAccordion.hideSection("my-roles-section");
                        this.collections.visibleRoles.forEach(function(G) {
                            this.toggleRole(G, true)
                        }, this)
                    }
                }
            },
            initTopBar: function() {
                var G = this.collections.visibleRoles;
                d.setRoles(G);
                G.addEvent("onChange:active", function(H) {
                    d.setChecked(H)
                });
                G.addEvent("onMultipleAdd", function() {
                    d.setRoles(G)
                });
                G.addEvent("onRemove", function(H) {
                    if (H.get("granted") === true) {
                        d.removeRole(H)
                    }
                })
            },
            searchInViews: function(G) {
                this.filterViews("search", G)
            },
            filterAppsInViews: function(G) {
                this.filterViews("filterApps", G)
            },
            filterViews: function(L, J) {
                var H = this.views,
                    K = (j.is(J, "string") && J.match(/^\s*role(s|)\s*:/g)),
                    G = K ? J.replace(/^\s*role(s|)\s*:/g, "").trim() : "",
                    I = K ? "" : J;
                H.favorite[L](I);
                H.platformRoles[L](G);
                H.visibleRoles[L](G);
                this.forEachQuadrant(function(M) {
                    H.apps[M][L](I)
                })
            },
            clickApp: function(I) {
                var H = I.target,
                    G, J;
                if (!this._handleAppClick) {
                    this._handleAppClick = c.debounce(this.handleAppClick, 500, true).bind(this)
                }
                if (H.hasClassName("exp-item")) {
                    G = H
                } else {
                    if (H.hasClassName("icon") || H.hasClassName("title")) {
                        G = H.getParent()
                    }
                }
                if (G) {
                    J = this.collection.get(G.getAttribute("data-id"));
                    this._handleAppClick(J, G)
                }
            },
            handleAppClick: function(I, P) {
                var K = this,
                    N = I.get("tooltip") || I.get("title"),
                    H = I.isNative(),
                    M = u._IDS.compass["app"],
                    L = I.isRole() ? I.get("process") : I.get("id"),
                    J = "Click",
                    G = {
                        onComplete: function(Q) {
                            if (K.options.closeOnLaunch) {
                                K.dispatchEvent("onClose")
                            }
                            if (H) {
                                if (G.support) {
                                    K.showMessage(p("supportactivated"), 5000)
                                } else {
                                    if (Q.remote) {
                                        K.showMessage(j.String.format(p("remoting"), N), 5000)
                                    } else {
                                        K.showMessage(j.String.format(p("launching"), N), 5000)
                                    }
                                }
                                if (w.get()) {
                                    e.publish("setTypesCallback", j.merge({
                                        appId: I.get("id")
                                    }, w.get()))
                                }
                            }
                        },
                        onFailure: function(R) {
                            var Q = R.error;
                            switch (Q) {
                                case "no_launch_infos":
                                case "no_install_selected":
                                    K.hideMessage();
                                    K.openSettings(I, P.getElement(".settings-img"));
                                    break;
                                case "no_install_compatible":
                                    K.hideMessage();
                                    if (I.isOnlineInstallable() && !R.remoteAvailable) {
                                        z.onlineInstallation(I, null, null)
                                    } else {
                                        K.openSettings(I, P.getElement(".settings-img"))
                                    }
                                    break;
                                case "mandatory_update":
                                    K.hideMessage();
                                    z.onlineInstallation(R.app, R.install, null, R.remote);
                                    break;
                                case "windows10_app_not_installed":
                                    K.hideMessage();
                                    window.location = "https://www.microsoft.com/store/apps/9wzdncrdjzv3";
                                    break;
                                case "ios_app_not_installed":
                                    K.hideMessage();
                                    window.location = "https://itunes.apple.com/us/app/3dplay/id825988585&mt=8";
                                    break;
                                case "applet_error":
                                    if (R.returnCode === 100) {
                                        K.showMessage(p("launcherbusyretry"), 5000)
                                    } else {
                                        if (R.remote) {
                                            K.showMessage(j.String.format(p("remotingerror"), N), 5000)
                                        } else {
                                            K.showMessage(j.String.format(p("launchingerror"), N), 5000)
                                        }
                                    }
                                    break
                            }
                        }
                    };
                if (H) {
                    if (I.get("supportModeExeName")) {
                        var O = new b({
                            className: "compass-modal compass-modal-support",
                            closable: true,
                            visible: true,
                            events: {
                                onHide: function() {
                                    O.destroy()
                                }
                            },
                            header: "<h4>" + p("supportmode") + "</h4>",
                            body: p("supportactivated") + "<br>" + p("wantsupport"),
                            footer: [j.createElement("button", {
                                "class": "btn btn-primary",
                                text: p("yes"),
                                events: {
                                    click: function() {
                                        G.support = true;
                                        K.showMessage("", 20000, true);
                                        I.launch(G);
                                        O.hide()
                                    }
                                }
                            }), j.createElement("button", {
                                "class": "btn btn-default",
                                text: p("no"),
                                events: {
                                    click: function() {
                                        K.showMessage("", 20000, true);
                                        I.launch(G);
                                        O.hide()
                                    }
                                }
                            }), j.createElement("button", {
                                "class": "btn btn-default",
                                text: p("cancel"),
                                events: {
                                    click: function() {
                                        O.hide()
                                    }
                                }
                            })]
                        }).inject(document.body);
                        u.inc(M, J, L);
                        return
                    }
                    this.showMessage("", 20000, true)
                } else {
                    if (I.isRole()) {
                        M = u._IDS.compass["role"];
                        if (I.get("granted")) {
                            if (I.get("active")) {
                                J = "Deactive"
                            } else {
                                J = "Active"
                            }
                            this.toggleRole(I)
                        } else {
                            J = "OpenRequestModal";
                            this.showRequestModal(I)
                        }
                        u.inc(M, J, L);
                        return
                    }
                }
                u.inc(M, J, L);
                if (H && w.get()) {
                    G.onComplete({})
                } else {
                    I.launch(G)
                }
            },
            handleInstallAppEvent: function(H, I) {
                var K, J = H.appId,
                    G;
                if (J) {
                    K = this.collection.get(J);
                    G = K.get("tooltip") || K.get("title");
                    if (K.isNative()) {
                        K.install({
                            forceUpdate: true,
                            onComplete: function() {
                                I.onComplete()
                            },
                            onFailure: function(M) {
                                var L = M.error;
                                switch (L) {
                                    case "no_install_compatible":
                                        if (K.isOnlineInstallable() && !M.remoteAvailable) {
                                            z.onlineInstallation(K, null, null, null, true, null, I)
                                        } else {
                                            I.onFailure({
                                                error: "no_install_compatible_no_online_installation"
                                            })
                                        }
                                        break;
                                    case "mandatory_update":
                                        z.onlineInstallation(M.app, M.install, null, M.remote, true);
                                        break;
                                    case "applet_error":
                                        if (M.returnCode === 100) {
                                            I.onFailure({
                                                error: L,
                                                message: p("launcherbusyretry")
                                            })
                                        } else {
                                            if (M.remote) {
                                                I.onFailure({
                                                    error: L,
                                                    message: p("launcherbusyretry")
                                                })
                                            } else {
                                                I.onFailure({
                                                    error: L,
                                                    message: j.String.format(p("launchingerror"), G)
                                                })
                                            }
                                        }
                                        break
                                }
                            }
                        })
                    } else {
                        I.onFailure({
                            error: "not_native_app"
                        })
                    }
                } else {
                    I.onFailure({
                        error: "no_app_id"
                    })
                }
            },
            handleLaunchAppEvent: function(J, H) {
                if (j.is(J.appId) && J.appId !== "") {
                    var I = this.collection.get(J.appId);
                    if (I) {
                        j.log(I);
                        var K = I.isNative(),
                            G = {
                                silent: J.silent,
                                onComplete: function() {
                                    H.onComplete({
                                        success: true
                                    })
                                },
                                onFailure: function(M) {
                                    var L = M.error;
                                    switch (L) {
                                        case "no_launch_infos":
                                            H.onFailure({
                                                error: "no_launch_infos"
                                            });
                                            break;
                                        case "no_install_selected":
                                            H.onFailure({
                                                error: "no_install_selected"
                                            });
                                            break;
                                        case "no_install_compatible":
                                            if (I.isOnlineInstallable() && !M.remoteAvailable) {
                                                H.onFailure({
                                                    error: "no_install_compatible_online_installation"
                                                })
                                            } else {
                                                H.onFailure({
                                                    error: "no_install_compatible_no_online_installation"
                                                })
                                            }
                                            break;
                                        case "mandatory_update":
                                            H.onFailure({
                                                error: "mandatory_update_online_installation"
                                            });
                                            break;
                                        case "ios_app_not_installed":
                                            H.onFailure({
                                                error: "ios_app_not_installed"
                                            });
                                            break;
                                        case "applet_error":
                                            H.onFailure({
                                                error: "applet_error"
                                            });
                                            break;
                                        case "unable_install_data":
                                            H.onFailure({
                                                error: "unable_install_data"
                                            });
                                            break
                                    }
                                }
                            };
                        if (J.fileName && J.fileContent) {
                            G.file = {
                                name: J.fileName,
                                content: J.fileContent
                            }
                        }
                        if (K) {
                            if (I.get("supportModeExeName")) {
                                H.onFailure({
                                    error: "no_mode_selected"
                                });
                                return
                            }
                        } else {
                            if (I.isRole()) {
                                H.onFailure({
                                    error: "app_is_a_role"
                                });
                                return
                            }
                        }
                        I.launch(G)
                    } else {
                        H.onFailure({
                            error: "no_app"
                        })
                    }
                } else {
                    H.onFailure({
                        error: "no_app_id"
                    })
                }
            },
            clickSuggestion: function(J) {
                var I = J.target,
                    H, G;
                if (I.hasClassName("suggestion-item")) {
                    H = I
                } else {
                    H = I.getParent(".suggestion-item")
                }
                if (H) {
                    G = this.sugCollection.get(H.getAttribute("data-id"));
                    this.handleSuggestionClick(G, H)
                }
            },
            handleSuggestionClick: function(G) {
                this.showTryModal(G)
            },
            showRequestModal: function(I) {
                var H = {
                        description: I.get("description"),
                        platforms: [],
                        buttonDisabled: false,
                        buttonText: p("request"),
                        action: "sendRoleRequest"
                    },
                    G = I.get("platforms");
                if (G.length > 0) {
                    H.platforms = G.reduce(function(K, J) {
                        if (!J.requested) {
                            K.push({
                                name: J.id,
                                text: j.String.format(p("requeston"), J.name)
                            })
                        }
                        return K
                    }, [])
                } else {
                    if (I.get("requested") || I.get("granted")) {
                        H.buttonDisabled = true
                    }
                }
                this.showRoleModal(I, H)
            },
            showTryModal: function(I) {
                var H = {
                        description: I.get("productUrl"),
                        platforms: null,
                        buttonDisabled: false,
                        buttonText: p("tryit"),
                        action: "sendTryRequest"
                    },
                    G = I.get("checkResultsByPlatformId");
                if (G.length > 1) {
                    H.platforms = G.reduce(function(K, J) {
                        if (J.checkResult) {
                            K.push({
                                name: J.platformId,
                                text: j.String.format(p("tryiton"), J.displayName)
                            })
                        }
                        return K
                    }, [])
                } else {
                    H.platforms = []
                }
                if (H.platforms.length === 0 && !G[0].checkResult) {
                    H.buttonDisabled = true
                }
                this.showRoleModal(I, H)
            },
            showRoleModal: function(M, H) {
                var K = this,
                    J = H.description,
                    G = H.platforms,
                    L, I;
                if (G.length > 0) {
                    L = {
                        className: "compass-modal-dropdown",
                        items: G,
                        events: {
                            onClick: function(O, N) {
                                K[H.action](M, N.name);
                                I.hide()
                            }
                        }
                    }
                }
                I = new b({
                    className: "compass-modal compass-request-modal",
                    closable: true,
                    visible: true,
                    events: {
                        onHide: function() {
                            I.destroy()
                        }
                    },
                    header: "<h4>" + M.get("title") + "</h4>",
                    body: new D({
                        src: J,
                        text: p("noadditionalinfo")
                    }),
                    footer: [new q({
                        value: H.buttonText,
                        className: H.buttonDisabled ? "default" : "primary",
                        disabled: H.buttonDisabled,
                        events: {
                            onClick: function() {
                                if (!L) {
                                    K[H.action](M);
                                    I.hide()
                                }
                            }
                        },
                        dropdown: L
                    }), new q({
                        value: p("cancel"),
                        events: {
                            onClick: function() {
                                I.hide()
                            }
                        }
                    })]
                }).inject(document.body)
            },
            clickRoleInfo: function() {
                var G = this.elements.expandedRole.getAttribute("data-id");
                this.showRequestModal(this.collection.get(G))
            },
            sendRoleRequest: function(K, J) {
                var I = this,
                    G = K.get("platforms"),
                    H = function() {
                        I.showMessage(p("requestfailure"), 5000)
                    };
                I.showMessage("", 20000, true);
                r.request({
                    url: r.requestRoleUrl,
                    method: "POST",
                    contentType: "application/x-www-form-urlencoded",
                    postData: {
                        process: K.get("process"),
                        platform: J
                    },
                    onComplete: function(L) {
                        if (L && L.code === 0) {
                            K.set("requested", true);
                            if (J) {
                                G.some(function(M) {
                                    if (M.id === J) {
                                        M.requested = true;
                                        return true
                                    }
                                })
                            }
                            I.showMessage(L.message, 5000)
                        } else {
                            H(L)
                        }
                    },
                    onFailure: H
                });
                j.log("requested " + K.get("process") + ":" + J)
            },
            sendTryRequest: function(K, J) {
                var I = this,
                    G = K.get("checkResultsByPlatformId"),
                    H = function() {
                        I.showMessage(p("trialrequestfailure"), 5000)
                    };
                I.showMessage("", 20000, true);
                if (!J) {
                    J = G[0].platformId
                }
                r.request({
                    url: r.tryRoleUrl,
                    method: "POST",
                    contentType: "application/x-www-form-urlencoded",
                    postData: {
                        process: K.get("productId"),
                        platform: J
                    },
                    onComplete: function(L) {
                        if (L && L.code === 0) {
                            I.showMessage(L.message, 5000);
                            I.refreshSuggestions()
                        } else {
                            H(L)
                        }
                    },
                    onFailure: H
                });
                j.log("try " + K.get("productId") + ":" + J)
            },
            refreshSuggestions: function() {
                this.views.suggestions.refresh()
            },
            _isRemoteDisabled: function() {
                if (typeof this._removeDisabled === "undefined") {
                    this._removeDisabled = l.get("3dremotevm") === "true"
                }
                return this._removeDisabled
            },
            openSettings: function(I, H) {
                var L = this,
                    P = this._isRemoteDisabled(),
                    G = I.getRemoteApp(),
                    J = I.get("platforms"),
                    N, K = H.getOffsets(),
                    M = t.getScrolls(),
                    O = function(U, T, W) {
                        var X, Y, V = c.arrayFind(J, function(Z) {
                                return Z.preferred
                            }),
                            R = V ? V.id : null,
                            Q = R,
                            S = function() {
                                N.hideSettings()
                            };
                        if (document.body.getElement(".my-apps-panel").getStyle("display") !== "none") {
                            if (U) {
                                X = U[0];
                                Y = U[1] && U[1].length > 0 ? U[1][0] : null
                            }
                            H.addClassName("active");
                            L.appsAccordion.addEvent("onScroll", S);
                            N = new B({
                                data: {
                                    platforms: J.length > 1 ? J : null,
                                    installs: X,
                                    remoteInstall: Y,
                                    error: I.get("error")
                                },
                                preferredInstallId: W,
                                processes: I.get("licenses").map(function(Z) {
                                    return Z.id
                                }),
                                app: I,
                                container: L.container,
                                appTitle: I.get("tooltip") || I.get("title"),
                                remoteAppTitle: G ? G.get("title") : null,
                                onlineInstallable: I.isOnlineInstallable(),
                                trigger: H,
                                target: H,
                                position: {
                                    x: K.x - M.x + 12,
                                    y: K.y - M.y
                                },
                                shortcutEnabled: !!R || J.length === 1,
                                remoteEnabled: !P && !!(I.get("remoteInfos")),
                                additionalContent: I.canAdditionalContent(L.collections.grantedRoles),
                                events: {
                                    onShow: function() {
                                        L.hideMessage()
                                    },
                                    onPlatformChange: function(ab) {
                                        var Z = ab.platform && R !== ab.platform,
                                            aa = ab.install && W !== ab.install;
                                        if (Z) {
                                            J.forEach(function(ac) {
                                                var ad = ac.preferred = (ac.id === ab.platform);
                                                if (ad) {
                                                    R = ac.id;
                                                    I.set("launchInfos", ac.launchInfos);
                                                    if (I.isWidget()) {
                                                        I.set("launchUrl", ac.launchUrl || I.get("launchUrl"));
                                                        I.set("config", ac.config || I.get("config"));
                                                        I.set("checksum", ac.checksum || I.get("checksum"));
                                                        I.set("platformId", ac.id);
                                                        L.container.getElements(j.String.format('[data-id="{0}"]', I.get("id"))).forEach(function(ae) {
                                                            c.enableWidgetDrag(ae, I.toJSON())
                                                        })
                                                    }
                                                    if (I.isNative()) {
                                                        I.set("envInfos", ac.envInfos);
                                                        I.set("shortcutLaunchInfos", ac.shortcutLaunchInfos);
                                                        if (ac.remoteLaunchInfos && I.get("remoteInfos")) {
                                                            I.get("remoteInfos").remoteLaunchInfos = ac.remoteLaunchInfos
                                                        }
                                                    }
                                                }
                                            })
                                        }
                                        if (aa) {
                                            I.set("install", ab.install)
                                        }
                                    },
                                    onHideSettings: function(ab) {
                                        var Z = ab.platform && Q !== ab.platform,
                                            aa = ab.install && W !== ab.install;
                                        H.removeClassName("active");
                                        L.appsAccordion.removeEvent("onScroll", S);
                                        if (Z || aa) {
                                            r.setPreferences({
                                                appid: I.id,
                                                preferredPlatform: ab.platform,
                                                preferredInstall: ab.install
                                            });
                                            Q = ab.platform
                                        }
                                    },
                                    onClickShortcut: function(Z) {
                                        x.createV6Shortcut(z.getInstall(Z), I)
                                    },
                                    onClickUpdate: function(Z, ac) {
                                        var ab = Z === "remote",
                                            aa = ab ? Y : z.getInstall(Z);
                                        if (ac) {
                                            z.onlineInstallation(ab ? G : I, aa, null, ab)
                                        } else {
                                            z.onlineInstallation(I, null, aa)
                                        }
                                    },
                                    onClickInstall: function() {
                                        z.onlineInstallation(I, null, null)
                                    },
                                    onClickInstallRemote: function() {
                                        z.onlineInstallation(G, null, null, true)
                                    },
                                    onClickAdditional: function() {
                                        z.onlineInstallation(I, null, null, null, null, true)
                                    }
                                }
                            });
                            N.show()
                        } else {
                            L.hideMessage()
                        }
                    };
                this.elements.search.blur();
                this.showMessage("", 30000, true);
                if ((I.isNative()) && !I.isNativeMarketApp() && c.getOs() !== "ios") {
                    I.getInstallList(G, O)
                } else {
                    O()
                }
            },
            clickSettings: function(I) {
                var H = I.target,
                    G = H.getParent(".exp-item"),
                    K = G.getAttribute("data-id"),
                    J = this.collection.get(K);
                this.openSettings(J, H)
            },
            longClickApp: function(G) {
                var H = this.collection.get(G.getAttribute("data-id"));
                if (H.isRole()) {
                    this.expandRoleDetail(H)
                } else {
                    if (H.needSettingsMenu()) {
                        this.openSettings(H, G.getElement(".settings-img"))
                    }
                }
            },
            clickExpand: function(I) {
                var H = I.target,
                    G = H.getParent(".exp-item"),
                    K = G.getAttribute("data-id"),
                    J = this.collection.get(K);
                this.expandRoleDetail(J)
            },
            expandRoleDetail: function(I) {
                var H = this.elements.expandedRole,
                    G = this.views.expandedRole;
                G.filterApps(I.get("appList"));
                H.getElement(".expanded-role-title").setText(I.get("title"));
                H.setAttribute("data-id", I.id);
                this.container.addClassName("roles-expanded")
            },
            hideRoleDetail: function() {
                this.container.removeClassName("roles-expanded")
            },
            toggleAllRoles: function() {
                this.container.toggleClassName("all-roles");
                this.elements.allRolesBtn.toggleClassName("fonticon-down-dir");
                this.elements.allRolesBtn.toggleClassName("fonticon-up-dir")
            },
            close: function() {
                var H = u._IDS.compass["cross"],
                    I = null,
                    G = "Click";
                u.inc(H, G, I);
                this.dispatchEvent("onClose")
            },
            onClose: function() {
                this.container.hide()
            },
            open: function() {
                this.dispatchEvent("onOpen")
            },
            onOpen: function() {
                this.container.show();
                r.get3DDashboardUrl()
            },
            displayQuadrant: function(G) {
                var H = this.currentQuadrant;
                this.appsAccordion.setTitle("my-apps-section", this.options.sectionData[G].title, G);
                if (H !== G) {
                    this.container.removeClassName(H);
                    this.container.addClassName(G);
                    this.currentQuadrant = G
                }
            },
            forEachQuadrant: function(G) {
                var H = ["north", "west", "south", "east"],
                    I = H.length - 1;
                for (; I >= 0; I--) {
                    G(H[I])
                }
            },
            showMessage: function(J, G, I) {
                var H = this.message;
                if (H) {
                    H.show(J, I);
                    if (G) {
                        H.hide(G)
                    }
                }
            },
            hideMessage: function(G) {
                var H = this.message;
                if (H) {
                    H.hide(G)
                }
            },
            mask: function() {
                this.maskElmt.show()
            },
            unmask: function() {
                this.maskElmt.hide()
            },
            setFavoriteApps: function(H, G) {
                var I = G + H.length,
                    L = this.collections.favorite,
                    J = this.collection.filterApps([{
                        key: "isRole",
                        value: false
                    }, {
                        key: "lma",
                        value: false
                    }, {
                        key: "appIds",
                        value: H
                    }]),
                    K = L.filter(function(M) {
                        return M.get("favoriteOrder") >= G
                    });
                K.forEach(function(N, M) {
                    if (J.indexOf(N) === -1) {
                        N.set("favoriteOrder", I + M)
                    }
                });
                J.forEach(function(M) {
                    M.set("favoriteOrder", G);
                    M.set("isFavorite", true);
                    G++
                });
                if (this.getContent().getElement(".delete-favorite").hasClassName("hide")) {
                    this.getContent().getElement(".delete-favorite").removeClassName("hide")
                }
                if (this.getContent().getElement(".my-favorite-apps-section").hasClassName("closed")) {
                    this.getContent().getElement(".my-favorite-apps-section").removeClassName("closed")
                }
                L.saveFavorites()
            },
            reorderFavoriteApp: function(G) {
                var H = this.collections.favorite.get(G);
                if (H) {
                    this.views.favorite.reorderApp(H)
                }
            },
            removeFavoriteApp: function(G) {
                var I = this.collections.favorite,
                    H = I.get(G);
                if (H) {
                    H.set("isFavorite", false);
                    H.set("favoriteOrder", -1)
                }
                if (I.length < 1 && !this.getContent().getElement(".delete-favorite").hasClassName("hide")) {
                    this.getContent().getElement(".delete-favorite").addClassName("hide")
                }
                I.saveFavorites()
            },
            removeAllFavoriteApp: function() {
                var H = this.collections.favorite,
                    G = this.collection.filterApps([{
                        key: "isRole",
                        value: false
                    }, {
                        key: "lma",
                        value: false
                    }, {
                        key: "isFavorite",
                        value: true
                    }]);
                G.forEach(function(I) {
                    I.set("isFavorite", false);
                    I.set("favoriteOrder", -1)
                });
                if (!this.getContent().getElement(".delete-favorite").hasClassName("hide")) {
                    this.getContent().getElement(".delete-favorite").addClassName("hide")
                }
                H.saveFavorites()
            },
            toggleRole: function(H, G) {
                if (G === true || G === false) {
                    H.set("active", G)
                } else {
                    H.set("active", !H.get("active"))
                }
                if (H.hasChanged("active")) {
                    this.collections.grantedRoles.saveRoles({
                        onComplete: function() {
                            c.onRoleChange(H.get("process"), H.get("active"))
                        }
                    })
                }
            },
            getVisibleHeight: function(G) {
                return this.appsAccordion.getVisibleHeight(G)
            },
            showTempHeight: function(H, G) {
                this.appsAccordion.showTempHeight(H, G)
            },
            hideTempHeight: function(G) {
                this.appsAccordion.hideTempHeight(G)
            },
            closeSection: function(G) {
                this.appsAccordion.closeSection(G)
            },
            isScrolling: function() {
                return this.appsAccordion.isScrolling()
            },
            stopScrolling: function() {
                this.appsAccordion.onScrollStop()
            },
            showDeleteZone: function() {
                var G;
                G = this.getContent().getElement(".delete-favorite");
                G.addClassName("show");
                G.removeClassName("hide")
            },
            hideDeleteZone: function() {
                var G;
                G = this.getContent().getElement(".delete-favorite");
                G.removeClassName("active");
                G.removeClassName("show")
            },
            changeRoles: function() {
                var G = this.collections.grantedRoles.getSelectedRoleApps();
                this.filterAppsInViews(G)
            }
        };
    return y.extend(i)
});
define("DS/i3DXCompass/i3DXCompass", ["UWA/Core", "UWA/Controls/Abstract", "UWA/Controls/Accordion", "UWA/Controls/Scroller", "UWA/Controls/ToolTip", "UWA/Ajax", "UWA/Data", "UWA/Utils/InterCom", "UWA/Event", "UWA/Utils", "UWA/Utils/Client", "UWA/Json", "UWA/Fx", "UWA/Controls/Drag", "UWA/Controls/Input", "DS/i3DXCompass/Data", "DS/i3DXCompass/Tools", "DS/i3DXCompass/Collection/AppList", "DS/i3DXCompass/Collection/SuggestionList", "DS/i3DXCompass/View/MyApps", "DS/i3DXCompass/DaemonManager", "DS/i3DXCompass/EventManager", "DS/i3DXCompass/InstallManager", "DS/i3DXCompass/TopBarManager", "DS/i3DXCompass/InterCom", "DS/i3DXCompass/Controls/Compass", "DS/UIKIT/Modal", "DS/WebappsUtils/WebappsUtils", "DS/i3DXCompass/X3DContent", "DS/i3DXCompassServices/i3DXCompassPubSub", "DS/i3DXCompass/CacheManager", "DS/i3DXCompass/Types", "DS/i3DXCompass/UsageTracker", "DS/i3DXCompass/i3DXCompassServices"], function(b, ah, p, ad, o, H, ab, af, T, U, n, R, ag, aj, aa, x, m, L, Z, i, B, G, y, w, Q, u, f, W, D, C, e, J, I, V) {
    var E = {
            closable: false,
            closeOnLaunch: false,
            lang: "en",
            myAppsBaseUrl: "",
            proxyTicketUrl: U.buildUrl(U.composeUrl(b.extend(U.parseUrl(window.location), {
                anchor: ""
            })), "api/passport/ticket?url=V6")
        },
        q = "north",
        t = "",
        ae = {},
        ai = [],
        Y, v, P, g, K, j, M = function(al) {
            return j.getElement(al)
        },
        r, S = {},
        ak = b.i18n,
        N = {},
        ac = {},
        X = false,
        a = false,
        l = false,
        k = false,
        d = false,
        F = false,
        h, s = [],
        A = [],
        O, c = function(al) {
            var am = t || q;
            if (!d) {
                d = true;
                Y.onOpen();
                r.onOpenQuadrant(am);
                N.loadQuadrant(am)
            }
            if (al) {
                N.dispatchOpen()
            }
        },
        z = function(al) {
            return b.is(x.addinMode) && b.is(x.addinMode.value) && al.addin && al.addin.detect(function(am) {
                return am.toLowerCase() === x.addinMode.value.toLowerCase()
            })
        };
    ac.intercom = {
        serverId: "com.ds.compass"
    };
    N.initDrag = function() {
        var ap = "drag-app",
            aN, aH = ".experience-list.favorite",
            aL, aA = null,
            aq, aO, at = false,
            am, aE, aP, au = false,
            ax = false,
            ar = false,
            av = false,
            az = false,
            aK = true,
            al = false,
            ao, an = null,
            ay, aG, aJ, aw, aF, aM, aI = function(aQ) {
                var aR = aQ.currentDelta;
                if (!aR || (Math.abs(aR.x) < 20 && Math.abs(aR.y) < 20)) {
                    aD.stop();
                    Y.longClickApp(aN)
                }
            },
            aB = function() {
                clearTimeout(aM);
                aG = ay.getData("favorite-order");
                aA = b.createElement("li", {
                    "class": "exp-item drop-favorite",
                    html: {
                        tag: "div",
                        html: [{
                            tag: "div",
                            "class": "fonticon fonticon-plus-circled add"
                        }, {
                            tag: "div",
                            text: ak("dropapphere"),
                            "class": "text"
                        }]
                    },
                    "data-favorite-order": b.is(aG, "number") ? aG : '"none"'
                });
                aA.inject(ay, "before");
                if (az) {
                    aL.removeClassName("empty")
                }
                ao = window.setTimeout(function() {
                    Y.showTempHeight("my-favorite-apps-section", 99);
                    window.setTimeout(aD.refreshCache, 350)
                }, 700);
                if (aF) {
                    Y.closeSection("my-roles-section")
                }
                al = true
            },
            aC = function() {
                av = false;
                ax = false;
                ar = false;
                at = false;
                aq = false;
                aO = false;
                az = false;
                aK = true;
                al = false
            },
            aD = new aj.Move({
                centerHandles: true,
                snap: 20,
                delegate: "." + ap + ", ." + ap + " > *",
                zones: aH,
                handles: function(aQ) {
                    var aR = aQ.target,
                        aS;
                    aC();
                    aN = aR.hasClassName(ap) ? aR : aR.getParent();
                    aL = M(aH);
                    am = aN.getAttribute("data-id");
                    az = aL.hasClassName("empty");
                    if (aN.isInjected(aL)) {
                        aE = aL.getChildren().indexOf(aN);
                        aP = aN.getData("favorite-order");
                        an = b.createElement("div", {
                            styles: {
                                display: "none"
                            }
                        }).inject(document.body);
                        aN.inject(an);
                        ay = aL.getChildren()[aE];
                        Y.showDeleteZone();
                        au = true
                    } else {
                        aE = -1;
                        if (aL.getElement(b.String.format('[data-id="{0}"]', am))) {
                            aK = false
                        } else {
                            ay = aL.getElement(".clearfix");
                            aS = j.getOffsets();
                            aF = aN.getParent(".compass-scroll-accordion-section").hasClassName("my-roles-section");
                            if (aF) {
                                aJ = {
                                    x: aS.x,
                                    y: aN.getOffsets().y + 3 * aN.getSize().height / 2
                                };
                                aw = {
                                    width: j.getSize().width,
                                    height: 1000
                                }
                            } else {
                                aJ = {
                                    x: aS.x,
                                    y: aS.y
                                };
                                aw = {
                                    width: j.getSize().width,
                                    height: aN.getOffsets().y - aS.y - aN.getSize().height / 2
                                }
                            }
                        }
                        au = false
                    }
                    if (aK && aE >= 0) {
                        aB()
                    }
                    if (Y.isScrolling()) {
                        Y.stopScrolling()
                    }
                    m.disableSelect();
                    window.setTimeout(aD.refreshCache, 1000);
                    aM = setTimeout(aI.bind(null, aQ), 1000);
                    return b.createElement("img", {
                        "class": "compass-drag-icon",
                        src: aN.getElement(".icon").src
                    }).inject(aQ.root)
                },
                cancel: function() {
                    return true
                },
                stop: function(aQ) {
                    if (!ar) {
                        aQ.handles.destroy();
                        if (aA) {
                            aA.destroy()
                        }
                        if (!ax && au) {
                            if (aO) {
                                aN.remove();
                                Y.removeFavoriteApp(am);
                                I.inc(I._IDS.compass["appFavorite"], "Remove", am)
                            } else {
                                Y.reorderFavoriteApp(am);
                                av = true
                            }
                        }
                        if (!av && az) {
                            M(aH).addClassName("empty")
                        }
                        aA = null;
                        window.clearTimeout(ao);
                        window.setTimeout(function() {
                            Y.hideTempHeight(1)
                        }, 500);
                        if (an) {
                            an.destroy();
                            an = null
                        }
                        Y.hideDeleteZone();
                        m.enableSelect();
                        clearTimeout(aM);
                        ar = true
                    }
                },
                enter: function() {
                    at = true
                },
                leave: function() {
                    if (aA) {
                        aA.removeClassName("active")
                    }
                    aq = false;
                    at = false
                },
                drop: function() {
                    var aS;
                    if (!ax) {
                        var aR = aA ? aA.getData("favorite-order") : null,
                            aQ = b.is(aR, "number") ? aR : P.max("favoriteOrder").get("favoriteOrder") + 1,
                            aT;
                        if (!aO) {
                            if (aE >= 0) {
                                aN.inject(aA, "before");
                                if (aq) {
                                    Y.setFavoriteApps([am], aQ)
                                }
                                av = true
                            } else {
                                if (aq) {
                                    if (aN.hasClassName("role")) {
                                        aT = P.get(aN.getAttribute("data-id"));
                                        Y.setFavoriteApps(aT.get("appList"), aQ);
                                        Y.toggleRole(aT, true)
                                    } else {
                                        Y.setFavoriteApps([am], aQ)
                                    }
                                    av = true
                                }
                            }
                            ax = true
                        }
                        if (aN.getAttribute("data-favorite-order") === "-1") {
                            if (b.is(aT)) {
                                aS = I._IDS.compass["roleFavorite"]
                            } else {
                                aS = I._IDS.compass["appFavorite"]
                            }
                            I.inc(aS, "Add", am)
                        }
                        return false
                    }
                },
                move: function(aS) {
                    var aY = {
                        x: aS.startOrigin.x + aS.currentDelta.x,
                        y: aS.startOrigin.y + aS.currentDelta.y
                    };
                    if (aK && !al && m.isOverElement(aY, aJ, aw)) {
                        aB()
                    }
                    if (au) {
                        var a4 = Y.getContent().getElement(".delete-favorite"),
                            a0 = a4.getOffsets(),
                            a1 = a4.getSize();
                        if (m.isOverElement(aY, a0, a1)) {
                            a4.addClassName("active");
                            aO = true
                        } else {
                            a4.removeClassName("active");
                            aO = false
                        }
                    }
                    if (at) {
                        var aT = aS.placeholderCache,
                            aV = aT.length,
                            aW, a3, a5, aR, aZ, aX, aU;
                        if (aA) {
                            var a2 = aA.getOffsets(),
                                aQ = aA.getSize();
                            if (m.isOverElement(aY, a2, aQ)) {
                                aA.addClassName("active");
                                aq = true
                            } else {
                                aA.removeClassName("active");
                                aq = false
                            }
                        }
                        for (aW = 0; aW < aV; aW++) {
                            a3 = aS.placeholderCache[aW];
                            aR = a3.element;
                            if (!(aR.hasClassName("drop-favorite") || aR.hasClassName("clearfix")) && aA) {
                                a5 = aR.getSize();
                                aZ = a3.position;
                                if (m.isOverElement(aY, aZ, a5)) {
                                    if (aY.x > aZ.x + a5.width / 2) {
                                        aX = aR.nextSibling;
                                        aU = aX ? aX.getData("favorite-order") : null;
                                        aA.inject(aR, "after")
                                    } else {
                                        aU = aR.getData("favorite-order");
                                        aA.inject(aR, "before")
                                    }
                                    aA.setAttribute("data-favorite-order", b.is(aU, "number") ? R.encode(aU) : '"none"');
                                    aD.refreshPlaceholderCache();
                                    break
                                }
                            }
                        }
                    }
                }
            })
    };
    N.manageNewGrantedRole = function() {
        var al = [];
        if (O) {
            O.forEach(function(an) {
                var am = Y.container.getElement(".exp-item[data-id=" + an + "true]");
                if (am) {
                    al.push(am);
                    am.addClassName("grow");
                    am.setStyles("opacity", "0")
                }
            });
            setTimeout(function() {
                al.forEach(function(am) {
                    am.setStyle("opacity", 1).setStyle("transition", "all .5s ease-in-out").removeClassName("grow")
                })
            }, 100);
            setTimeout(function() {
                al.forEach(function(am) {
                    am.setStyle("transition", null)
                });
                O = null
            }, 500)
        }
    };
    N.initTranslate = function(an) {
        var ao = E.lang,
            al = W.getWebappsAssetUrl("i3DXCompass", b.String.format("lang/{0}.json", ao)),
            am = {
                timeout: 15000,
                method: "GET",
                type: "json",
                onComplete: function(ap) {
                    b.log("loaded " + ao + " translations");
                    ak(ap);
                    an()
                },
                onFailure: function(ap) {
                    if (!(ap instanceof TypeError)) {
                        b.log("Failed to load " + ao + " translations");
                        if (ao !== "en") {
                            ao = "en";
                            al = W.getWebappsAssetUrl("i3DXCompass", b.String.format("lang/{0}.json", ao));
                            ab.request(al, am)
                        } else {
                            an()
                        }
                    } else {
                        throw ap
                    }
                }
            };
        if (ao === "#_key") {
            ao = "en";
            ak = function() {}
        }
        ak({
            unableinstalldata: "Unable to retrieve install data",
            erroroccurred: "An error occurred",
            noinstall: "{0} is not installed.",
            createshortcut: "Create Desktop Shortcut",
            launching: "Launching {0}",
            remoting: "Remoting {0}",
            errorsavingpreferences: "An error occurred while saving the preferences",
            creatingshortcut: "Creating Desktop Shortcut for {0}",
            selectionupdated: "Your selection has been updated",
            shortcutcreated: "Shortcut Created",
            install: "Install {0}",
            shortcutcreatingerror: "Shortcut Creating Error",
            launchingerror: "{0} Launching Error",
            remotingerror: "{0} Remoting Error",
            installprocess: "Install {0}",
            installallgranted: "Install All Granted Roles",
            installallcontaining: "Install All Roles Containing {0}",
            updateallprocesses: "Update All Roles",
            update: "Update {0}",
            "3dexperienceinstallation": "3DEXPERIENCE Installation",
            installationerror: "Installation Error",
            installationerrordiskspace: "Installation Error: Not enough disk space for download",
            installationcancelled: "Installation Cancelled",
            installationsuccess: "Installation Success",
            launchinginstallation: "Launching Installation",
            clickinstall: "Install Now",
            remoteit: "Remote Mode",
            ok: "OK",
            readyinstallation: "{0} is ready to be installed",
            readyupdate: "{0} is ready to update your installation",
            updateavailable: "Update available",
            updateneeded: "Update needed",
            newprocessesavailable: "New roles available",
            orinstallrelated: "Or",
            nbcompleteupdgraded: "NB: the complete installation will be upgraded to {0}",
            instfilelocation: "Installation File Location",
            javainstallation: "Java Installation",
            javanotinstalled: "The required Java Runtime Environment is not installed on this computer.",
            pleaseinstalljava: "Please install it now, both 32bit and 64bit versions.",
            dropapphere: "Drop your app here",
            dropfavoriteappshere: "Drop your favorite apps here",
            missingprocesses: "Missing Roles",
            missingprocessesreloaded: "Some roles are missing. The Compass has been reloaded.",
            searchapps: "Search Apps",
            supportmode: "Support Mode",
            supportactivated: "Support Mode is activated for recording system traces.",
            wantsupport: "Do you want to start the app with support mode?",
            yes: "Yes",
            no: "No",
            cancel: "Cancel",
            removeapp: "Remove App",
            "continue": "Continue",
            proceedanyway: "Update later",
            welcomelauncher: "Welcome to the 3DEXPERIENCE Launcher",
            launcherfirstvisit: "If this is your first visit to the 3DEXPERIENCE platform, please download and install the 3DEXPERIENCE Launcher.",
            downloadlauncherclicking: "Click the button below:",
            downloadlauncher: "Download 3DEXPERIENCE Launcher",
            startselfupdate: "Start update now",
            installlauncherrunning: "Run the downloaded file ({0}) to start install.",
            youcaninstallrun: "You can now install new apps or run those already installed.",
            clickcontinue: "Click {0}Continue{1}.",
            downloadlauncheradmin: "If the 3DEXPERIENCE Launcher is not installed on your machine, please contact your administrator to install it.",
            updatelauncheradmin: "Please contact your administrator to install the update.",
            alreadyinstalledlauncher: "If you have already installed the 3DEXPERIENCE Launcher, one of the following issues may be preventing it from working:",
            launchernotstarted: "The 3DEXPERIENCE Launcher is not started.",
            portchanged: "The port number used by the 3DEXPERIENCE Launcher may have changed or a firewall or anti-virus software may be blocking the port.",
            changeport: "If required, enter the new one:",
            portunreachable: "The provided port is unreachable.",
            launcherupdate: "Update 3DEXPERIENCE Launcher",
            downloadupdateclicking: "Click the button bellow:",
            updateinfos: "A new update of 3DEXPERIENCE Launcher is available ({0}version {2} / {3}MB{1}).",
            startselfupdateclicking: "Start launcher update by clicking here:",
            installupdaterunning: "Install the update by running the downloaded file: {0}",
            selfupdaterunning: "Wait for the update to finish.",
            selfupdateerror: "Unable to update 3DEXPERIENCE Launcher. Please try again or download and install manually.",
            checksystrayicon: "Check that the 3DEXPERIENCE Launcher icon {0} is available in the Windows system tray.",
            dontseelaunchermenu: "If you do not see 3DEXPERIENCE Launcher on the menu, please contact your administrator.",
            clicksystrayicon: "In the system tray, right-click the 3DEXPERIENCE Launcher {0}",
            copyportnumber: "Copy the port number.",
            pasteportnumber: "Paste it in the field.",
            troubleshootbtn: "Troubleshooting",
            launcherbusyretry: "The 3DEXPERIENCE Launcher is currently performing an action . Please retry later.",
            launcherunavailable: "The 3DEXPERIENCE Launcher is currently unavailable. Please try again later.",
            canrequest: "You can request this role from your Administrator.",
            requeston: "Request on {0}",
            alreadyrequestedplatforms: "{0} has already been requested on all platforms.",
            alreadyrequested: "{0} has already been requested.",
            noadditionalinfo: "No additional information for this role",
            request: "Request",
            requestfailure: "Request Failure",
            trialrequestfailure: "Trial Request Failure",
            tryit: "Try it",
            tryiton: "Try it on {0}",
            myroles: "My Roles",
            installationerroraccess: "Installation Error: Unable to access the Installation File Location",
            youcaninstallandlaunch: "You can now install new apps or run those already installed.",
            installallcontentavailable: "Install all additional content",
            additionalcontentavailable: "Additional content available",
            clickRemoveFavorite: "Click to remove all your favorite apps. </br> Or drag & drop any favorite app to remove it.",
            confirmRemoveFavorite: "Are you sure you want to remove all your favorite apps?",
            confirmation: "Confirmation",
            "3DEXPERIENCEMarketplace": "3DEXPERIENCE Marketplace",
            wrapperRole: "3DEXPERIENCE Roles & Apps",
            DassaultSystemesCommunities: "Dassault Systemes Communities",
            MyCommunityServices: "My Community Services",
            MyEnterpriseServices: "My Enterprise Services",
            Cloud: "Cloud"
        });
        ab.request(al, am)
    };
    N.handleQuadrantClick = function(al) {
        Q.fireEvent("compassEvent", {
            quadrant: al
        });
        C.publish("compassEvent", {
            quadrant: al
        });
        if (al !== "play") {
            if (!d) {
                d = true;
                Y.onOpen();
                N.dispatchOpen()
            }
            N.loadQuadrant(al)
        }
    };
    N.loadQuadrant = function(al) {
        var am = function(an, ao) {
            if (d && !h) {
                h = window.setTimeout(N.loadQuadrant.bind(null, t), 300000)
            }
            if (an) {
                y.invalidateProductList()
            }
            if (ao) {
                Y.refreshSuggestions()
            }
            Y.manageRoleSectionDisplay();
            N.manageNewGrantedRole()
        };
        t = al;
        h = window.clearTimeout(h);
        if (!l) {
            P.fetch({
                onComplete: function(ao, an) {
                    if (!v) {
                        v = {
                            north: {
                                title: an.north[0].title,
                                closed: an.north[0].closed,
                                noapps: an.north[0].noapps,
                                icon: true
                            },
                            west: {
                                title: an.west[0].title,
                                closed: an.west[0].closed,
                                noapps: an.west[0].noapps,
                                icon: true
                            },
                            south: {
                                title: an.south[0].title,
                                closed: an.south[0].closed,
                                noapps: an.south[0].noapps,
                                icon: true
                            },
                            east: {
                                title: an.east[0].title,
                                closed: an.east[0].closed,
                                noapps: an.east[0].noapps,
                                icon: true
                            },
                            lma: {
                                title: an.north[1].title,
                                closed: an.north[1].closed,
                                noapps: "",
                                icon: false
                            },
                            favorite: {
                                title: an.favorite.title,
                                closed: an.favorite.closed,
                                noapps: "",
                                icon: true
                            },
                            roles: {
                                title: an.roles.title,
                                closed: an.roles.closed,
                                noapps: an.roles.noroles,
                                icon: true
                            },
                            suggestions: {
                                title: an.suggestions.title,
                                closed: an.suggestions.closed,
                                icon: false
                            },
                            services: an.services,
                            eservices: an.eservices,
                            wrapperMP: {
                                title: an.wrapperMP.title,
                                closed: an.wrapperMP.closed,
                                icon: false
                            },
                            wrapperRole: {
                                title: an.wrapperRole.title,
                                closed: an.wrapperRole.closed,
                                icon: false
                            }
                        };
                        b.log("onCompassLoaded");
                        G.dispatchEvent("onCompassLoaded")
                    } else {
                        v.north.closed = an.north[0].closed;
                        v.west.closed = an.west[0].closed;
                        v.south.closed = an.south[0].closed;
                        v.east.closed = an.east[0].closed;
                        v.lma.closed = an.north[1].closed;
                        v.favorite.closed = an.favorite.closed;
                        v.roles.closed = an.roles.closed;
                        v.suggestions.closed = an.suggestions.closed;
                        if (an.services) {
                            v.services.closed = an.services.closed
                        }
                        if (an.eservices) {
                            v.eservices.closed = an.eservices.closed
                        }
                        v.wrapperMP.closed = an.wrapperMP.closed;
                        v.wrapperRole.closed = an.wrapperRole.closed
                    }
                    if (an.config && an.config.launcherUrl && an.config.launcherUrl[0] && an.config.launcherUrl[0].url !== "") {
                        B.setLauncherConfig(an.config.launcherUrl[0].url)
                    }
                    if (an.config && an.config.catEnv && an.config.catEnv[0] && an.config.catEnv[0].dir !== "") {
                        B.setCatEnvConfig(an.config.catEnv)
                    }
                    if (d) {
                        if (!l) {
                            Y.renderAppsView(v)
                        }
                        l = true;
                        k = true;
                        Y.displayQuadrant(t);
                        Y.unmask()
                    }
                    if (!a) {
                        a = true;
                        r.setQuadrantNames({
                            north: an.north[0].title,
                            west: an.west[0].title,
                            south: an.south[0].title,
                            east: an.east[0].title,
                            play: an.play[0].title
                        });
                        x.onlineInstallUrl = an.install;
                        x.clusterId = an.clusterId;
                        x.buildTransactionId = an.build;
                        y.init(an.cloud);
                        x.cloud = an.cloud;
                        B.init(K, an.cloud);
                        s.forEach(function(ap) {
                            setTimeout(ap, 0)
                        });
                        s.length = 0;
                        A.forEach(function(ap) {
                            setTimeout(ap, 0)
                        });
                        A.length = 0;
                        if (E.topBarId) {
                            Y.initTopBar()
                        }
                    }
                    am(an.changed, false)
                },
                onFailure: function() {
                    Y.renderErrorView(ak("erroroccurred"));
                    Y.unmask();
                    am(false, false)
                }
            })
        } else {
            if (!k) {
                Y.mask();
                P.fetch({
                    onComplete: function(ao, an) {
                        k = true;
                        Y.displayQuadrant(t);
                        Y.unmask();
                        am(an.changed, an.changed)
                    },
                    onFailure: function() {
                        Y.showMessage(ak("erroroccurred"), 5000);
                        Y.unmask();
                        am(false, false)
                    }
                })
            } else {
                Y.displayQuadrant(t);
                P.fetch({
                    onComplete: function(ao, an) {
                        if (an.eservices && Y.views.eservices) {
                            Y.views.eservices.collection.set(an.eservices.data);
                            Y.views.eservices.render()
                        }
                        am(an.changed, an.changed)
                    },
                    onFailure: function() {
                        Y.showMessage(ak("erroroccurred"), 5000);
                        Y.unmask();
                        am(false, false)
                    }
                })
            }
        }
    };
    ac.initialize = function(al) {
        b.log("initializing Compass");
        E = b.merge(al, E);
        e.setup(E.userId, E.lang);
        Q.initialize(ac.intercom.serverId, {
            onSetObject: ac.setObject,
            onResetObject: ac.resetObject,
            onLaunchApp: ac.launchApp,
            onSetStructure: ac.setStructure,
            onResetStructure: ac.resetStructure,
            onShowAccordion: ac.showAccordion,
            onHideAccordion: ac.hideAccordion,
            onSetX3DContent: ac.setX3DContent,
            onResetX3DContent: ac.resetX3DContent
        });
        C.subscribe("setObject", ac.setObject);
        C.subscribe("resetObject", ac.resetObject);
        C.subscribe("launchApp", ac.launchApp);
        C.subscribe("installApp", ac.installApp);
        C.subscribe("setStructure", ac.setStructure);
        C.subscribe("resetStructure", ac.resetStructure);
        C.subscribe("setTypes", ac.setTypes);
        C.subscribe("resetTypes", ac.resetTypes);
        C.subscribe("setX3DContent", ac.setX3DContent);
        C.subscribe("resetX3DContent", ac.resetX3DContent);
        if (b.typeOf(E.launchOnSameDomain) === "function") {
            m.setCustomFunction("customLaunchWebApp", E.launchOnSameDomain);
            delete E.launchOnSameDomain
        }
        if (b.typeOf(E.enableWidgetDrag) === "function") {
            m.setCustomFunction("enableWidgetDrag", E.enableWidgetDrag);
            delete E.enableWidgetDrag
        }
        if (b.typeOf(E.instanciateWidget) === "function") {
            m.setCustomFunction("instanciateWidget", E.instanciateWidget);
            delete E.instanciateWidget
        }
        if (b.typeOf(E.onRoleChange) === "function") {
            m.setCustomFunction("onRoleChange", E.onRoleChange);
            delete E.onRoleChange
        }
        if (b.typeOf(E.onOpen) === "function") {
            N.onOpen = E.onOpen;
            delete E.onOpen
        }
        if (b.typeOf(E.onClose) === "function") {
            N.onClose = E.onClose;
            delete E.onClose
        }
        if (E.proxyTicketUrl.indexOf("?") === -1) {
            E.proxyTicketUrl += "?t="
        } else {
            E.proxyTicketUrl += "&t="
        }
        if (E.myAppsBaseURL) {
            E.myAppsBaseUrl = E.myAppsBaseURL;
            delete E.myAppsBaseURL;
            b.log("Compass: myAppsBaseURL init option is deprecated. Please use myAppsBaseUrl instead.")
        }
        d = false;
        if (E.compassTarget) {
            K = b.extendElement(document.getElementById(E.compassTarget));
            if (!K) {
                b.log("Compass: this compass target markup is not available!")
            }
        } else {
            b.log("Compass: you must provide a target for Compass (compassTarget)!")
        }
        r = new u({
            closable: E.closable,
            defaultQuadrant: q,
            events: {
                onOpenQuadrant: N.handleQuadrantClick,
                onClose: function() {
                    Y.onClose();
                    N.dispatchClose();
                    h = window.clearTimeout(h);
                    d = false
                }
            }
        }).inject(K);
        x.initialize(E, K);
        N.initTranslate(function() {
            if (E.topBarId) {
                w.init({
                    topBarId: E.topBarId,
                    onRoleChange: function(an, ao) {
                        var ap = P.get(an);
                        if (ap) {
                            Y.toggleRole(ap, ao)
                        }
                    }
                })
            }
            P = new L(null, {
                comparator: "title"
            });
            g = new Z();
            Y = new i({
                closable: E.closable,
                collection: P,
                sugCollection: g,
                closeOnLaunch: E.closeOnLaunch
            });
            Y.addEvent("onClose", function() {
                r.onClose();
                N.dispatchClose();
                h = window.clearTimeout(h);
                d = false
            });
            if (m.isSmartphone()) {
                Y.addEvent("onScrollTop", function() {
                    r.switchCompassView(false)
                });
                Y.addEvent("onScrollDown", function() {
                    r.switchCompassView(true)
                });
                r.addEvent("onUnScroll", function(an) {
                    if (Y.appsAccordion.scrolled !== 0) {
                        Y.appsAccordion.unScroll(0)
                    }
                })
            }
            j = b.extendElement(document.getElementById(E.appsTarget));
            if (j) {
                Y.render().inject(j)
            }
            G.addEvent("onMissingProcess", function() {
                if (!F) {
                    k = false;
                    N.loadQuadrant(t);
                    F = true;
                    var an = new f({
                        className: "compass-modal compass-modal-missing",
                        closable: true,
                        visible: true,
                        events: {
                            onHide: function() {
                                F = false;
                                an.destroy()
                            }
                        },
                        header: "<h4>" + ak("missingprocesses") + "</h4>",
                        body: ak("missingprocessesreloaded")
                    }).inject(document.body)
                }
            });
            N.initDrag();
            N.loadQuadrant(q);
            if (!E.closable) {
                c(q)
            }
            var am = "DS/PlatformAPI/PlatformAPI";
            require([am], function(an) {
                an.subscribe("com.ds.compass:onGrantedRole", function(ao) {
                    P.lastId = undefined;
                    k = false;
                    V._private.reset();
                    N.loadQuadrant(t)
                })
            });
            X = true
        })
    };
    ac.showAccordion = c;
    N.dispatchOpen = function() {
        Q.fireEvent("compassPanelOnShow", {});
        C.publish("compassPanelOnShow", {});
        if (N.onOpen) {
            N.onOpen()
        }
    };
    ac.hideAccordion = function(al) {
        if (d) {
            r.onClose();
            Y.onClose();
            h = window.clearTimeout(h);
            d = false
        }
        if (al) {
            N.dispatchClose()
        }
    };
    N.dispatchClose = function() {
        Q.fireEvent("compassPanelOnHide", {});
        C.publish("compassPanelOnHide", {});
        if (N.onClose) {
            N.onClose()
        }
    };
    ac.isInitialized = function() {
        return X
    };
    ac.setObject = function(am) {
        var al = function(an) {
            return !an || typeof an !== "string" || !an.trim()
        };
        b.log("Compass: set object");
        b.log(am);
		if(am.objectType === undefined) {
			document.cookie = "OBJECTID=;expires=-1;path=/";
		}
        if (b.typeOf(am) === "object") {
            if (JSON.stringify(am) !== JSON.stringify(S)) {
                Y.showMessage(ak("selectionupdated"), 5000)
            }
            S = am;
            if (S.hasOwnProperty("cstorage") && !S.hasOwnProperty("envId")) {
                S.envId = S.cstorage
            }
            if (S.hasOwnProperty("cspace") && !S.hasOwnProperty("contextId")) {
                S.contextId = S.cspace
            }
            if (al(S.serviceId)) {
                console.warn("setObject without key: serviceId is deprecated, please provide the source id of the object")
            }
        } else {
            S = {}
        }
        ac.resetStructure();
        ac.resetTypes();
        P.setObject(S)
    };
    ac.resetObject = function() {
        b.log("Compass: reset object");
        S = {};
        P.resetObject()
    };
    ac.setX3DContent = function(al) {
        b.log("Compass: set content");
        b.log(al);
        if (b.typeOf(al) === "object" && !(Object.keys(al).length === 0)) {
            D.setX3DContent(al);
            C.publish("setX3DContentCallback", al)
        }
    };
    ac.resetX3DContent = function() {
        b.log("Compass: reset content");
        var al = {};
        D.setX3DContent(al)
    };
    ac.installApp = function(al) {
        var am = {
            onComplete: function() {
                C.publish("installAppCallback", al)
            },
            onFailure: function(an) {
                al = b.merge(al, an);
                C.publish("installAppCallback", al)
            }
        };
        b.log("Compass: install App");
        b.log("appId = " + al.appId);
        b.log("widgetId = " + al.widgetId);
        Y.handleInstallAppEvent(al, am)
    };
    ac.launchApp = function(an) {
        b.log("Compass: launch App");
        b.log("appId = " + an.appId);
        b.log("widgetId = " + an.widgetId);
        b.log("silent = " + an.silent);
        b.log("fileName = " + an.fileName);
        b.log("fileContent = " + an.fileContent);
        var al = {
                widgetId: an.widgetId,
                appId: an.appId,
                response: ""
            },
            am = {
                onComplete: function() {
                    b.log("COMPLETE");
                    al.response = "COMPLETE";
                    Q.fireEvent("launchApp", al);
                    if (an._PUB_SUB_ID) {
                        al._PUB_SUB_ID = an._PUB_SUB_ID
                    }
                    C.publish("launchAppCallback", al)
                },
                onFailure: function(ap) {
                    var ao = ap.error;
                    b.log(ao);
                    al.response = ao;
                    Q.fireEvent("launchApp", al);
                    if (an._PUB_SUB_ID) {
                        al._PUB_SUB_ID = an._PUB_SUB_ID
                    }
                    C.publish("launchAppCallback", al)
                }
            };
        Y.handleLaunchAppEvent(an, am)
    };
    ac.setStructure = function(al) {
        b.log("Compass set structure");
        b.log(al);
        if (b.typeOf(al) === "object" && b.typeOf(al.structure) === "string") {
            ac.resetObject();
            ac.resetTypes();
            B.setStructure(al.structure)
        } else {
            B.resetStructure()
        }
    };
    ac.resetStructure = function() {
        b.log("reset structure");
        B.resetStructure()
    };
    ac.setTypes = function(al) {
        b.log("Compass set Types");
        b.log(al);
        if (b.typeOf(al) === "object") {
            J.set(al);
            ac.resetStructure();
            P.resetObject()
        } else {
            J.reset()
        }
    };
    ac.resetTypes = function() {
        b.log("reset Types");
        J.reset()
    };
    ac.getAppInfo = function(am) {
        var al;
        if (am && am.appId && b.typeOf(am.onComplete) === "function") {
            al = function() {
                var ap = ac._private._getAppList().get(am.appId),
                    an = ap ? ap.toJSON() : undefined;
                if (b.is(x.addinMode) === true && an && an.apps.length > 1) {
                    var ao = [];
                    an.apps.forEach(function(aq) {
                        if (z(aq)) {
                            ao.push(aq)
                        }
                    });
                    an.apps = ao
                }
                if (!an && ae[am.appId]) {
                    an = ae[am.appId]
                }
                if (an) {
                    am.onComplete(an)
                } else {
                    if (!ai[am.appId]) {
                        ai[am.appId] = []
                    }
                    ai[am.appId].push(am);
                    if (ai[am.appId].length === 1) {
                        x.request({
                            url: x.getAppInfoUrl,
                            urlParams: {
                                id: am.appId
                            },
                            onComplete: function(aq) {
                                if (aq) {
                                    ae[am.appId] = aq
                                }
                                ai[am.appId].forEach(function(ar) {
                                    ar.onComplete(aq)
                                });
                                ai[am.appId] = null
                            },
                            onFailure: function() {
                                ai[am.appId].forEach(function(aq) {
                                    aq.onComplete(an)
                                });
                                ai[am.appId] = null;
                                throw new Error("No config found for App:" + am.appId)
                            }
                        })
                    }
                }
            };
            if (ac._private._isAppsDataInitialized()) {
                al()
            } else {
                s.push(al)
            }
        } else {
            if (am && b.typeOf(am.onComplete) === "function") {
                am.onComplete(new Error("Mandatory params are missing"))
            }
        }
    };
    ac.getAppsInfo = function(am) {
        var al;
        if (am && am.data && b.typeOf(am.onComplete) === "function" && b.typeOf(am.data) === "array") {
            al = function() {
                var an = [];
                am.data.forEach(function(ar) {
                    var aq = ac._private._getAppList().get(ar),
                        ao = aq ? aq.toJSON() : undefined;
                    if (b.is(x.addinMode) === true && ao && ao.apps.length > 1) {
                        var ap = [];
                        ao.apps.forEach(function(at) {
                            if (z(at)) {
                                ap.push(at)
                            }
                        });
                        ao.apps = ap
                    }
                    if (ao) {
                        an.push(ao)
                    }
                });
                am.onComplete(an)
            };
            if (ac._private._isAppsDataInitialized()) {
                al()
            } else {
                A.push(al)
            }
        } else {
            if (am && b.typeOf(am.onComplete) === "function") {
                am.onComplete(new Error("Mandatory params are missing"))
            }
        }
    };
    ac.onRoleChange = function(al, am) {
        var an;
        if (al) {
            an = P.findWhere({
                process: al
            }, {
                granted: true
            });
            if (an) {
                Y.toggleRole(an, am)
            }
        } else {
            k = false;
            if (d) {
                N.loadQuadrant(t)
            }
        }
    };
    ac._private = {
        _setQuadrant: function(al) {
            t = al
        },
        _getMyAppsView: function() {
            return Y
        },
        _getAppList: function() {
            return P
        },
        _isAppsDataInitialized: function() {
            return a
        }
    };
    return ac
});
define("i3DXCompass/i3DXCompass", ["DS/i3DXCompass/i3DXCompass"], function(a) {
    return a
});
define("DS/i3DXCompass/ServiceApi/GrantedRolesApi", ["UWA/Core", "DS/i3DXCompass/i3DXCompass", "DS/i3DXCompass/EventManager", "DS/i3DXCompass/Data", "DS/i3DXCompass/Collection/AppList", "DS/i3DXCompass/CacheManager"], function(g, m, e, l, n, h) {
    var k = [],
        i, j = function(o) {
            k.forEach(function(p) {
                setTimeout(p, 0, o)
            });
            k.length = 0
        },
        d = function(o) {
            return o.isRole() && o.get("granted") === true && o.get("id") !== "CUSTOM"
        },
        b = function() {
            var p = [],
                o = i || m._private._getAppList();
            if (o) {
                o.forEach(function(q) {
                    if (d(q)) {
                        p.push({
                            id: q.get("process"),
                            name: q.get("title"),
                            platforms: q.get("grantedPlatforms")
                        })
                    }
                })
            }
            return p
        },
        a = function() {
            j(b());
            e.removeEvent(a)
        },
        c = function(p) {
            var o = b();
            if (o.length > 0) {
                p(o)
            } else {
                k.push(p);
                e.addEventOnce("onCompassLoaded", a);
                if (typeof COMPASS_CONFIG !== "undefined" && COMPASS_CONFIG.myAppsBaseUrl && !i) {
                    l.initialize(COMPASS_CONFIG);
                    h.setup(COMPASS_CONFIG.userId, COMPASS_CONFIG.lang);
                    i = new n();
                    i.fetch({
                        onComplete: function() {
                            e.dispatchEvent("onCompassLoaded", a)
                        },
                        onFailure: function() {
                            g.log("AppList: onFailure")
                        }
                    })
                }
            }
        };
    var f = {
        getGrantedRoles: function(o) {
            if (!o || g.typeOf(o) !== "function") {
                return
            }
            c(o)
        },
        _private: {
            callbacks: k,
            fireCallbacks: j
        }
    };
    return f
});