
(function($, undefined){
    // Default Settings
    var _defaults = {
        title               : "Popup Window",
        modal               : true,
        autoOpen            : true,
        animationTime       : 300,

        buttons             : {
            close               : true,
        },
        buttonsTexts        : {
            close               : "Close",
        },

        top                 : "auto",
        left                : "auto",

        height              : 200,
        width               : 400,
        maxHeight           : undefined,
        maxWidth            : undefined,
        minHeight           : 100,
        minWidth            : 200,
    };

    // Required CSS
    var _css = {
        overlay : {
        	"display"			:"flex",
        },
    };

    // Icons
    var _icons = {
        close           : '<img src="/images/layout/popup_w2.png"  alt=""/>',
    };

    // Main Container
    var _mainContainer;

    // Minimized Area
    var _minimizedArea = {
        position    : "bottom left",
        direction   : "horizontal"
    };


    // **************************************************
    //  METHODS
    // **************************************************
    $.PopupWindowSetup = function(options){
        $.extend(true, _defaults, options);
    };

    $.PopupWindowMinimizedArea = function(options){
        if (options === undefined) return $.extend({}, _minimizedArea);
        if (options.position) _minimizedArea.position = ((options.position.toLowerCase().indexOf("b") >= 0) ? "bottom" : "top") + " " + ((options.position.toLowerCase().indexOf("l") >= 0) ? "left" : "right");
        if (options.direction) _minimizedArea.direction = (options.direction.toLowerCase().indexOf("h") >= 0) ? "horizontal" : "vertical";
    };

    $.fn.PopupWindow = function(opt1, opt2){
        if (typeof opt1 == "string") {
            switch (opt1.toLowerCase()) {
                case "init":
                    return this.each(function(){
                        _Init($(this), opt2);
                    });
                case "open":
                    return this.each(function(){
                        _Open($(this).closest(".popup_box"));
                    });
                case "close":
                    return this.each(function(){
                        _Close($(this).closest(".popup_box"));
                    });
            }
        } else {
            return this.each(function(){
                _Init($(this), opt1);
            });
        }
    };


    // **************************************************
    //  FUNCTIONS
    // **************************************************
    function _Init(originalTarget, options){
        if (originalTarget.closest(".popup_box").length) {
            _Warning("jQuery PopupWindow is already initialized on this element");
            return;
        }
        var settings = $.extend(true, {}, _defaults, options);
        settings.animationTime  = parseInt(settings.animationTime, 10);
        settings.height         = parseInt(settings.height, 10);
        settings.width          = parseInt(settings.width, 10);
        settings.maxHeight      = parseInt(settings.maxHeight, 10) || 0;
        settings.maxWidth       = parseInt(settings.maxWidth, 10) || 0;
        settings.minHeight      = parseInt(settings.minHeight, 10) || 0;
        settings.minWidth       = parseInt(settings.minWidth, 10) || 0;

        // Overlay
        var overlay = $("<section>", {
            class   : "popup_dim"
        })
        .css(_css.overlay)
        .appendTo(_mainContainer);
        if (settings.modal) overlay.css("pointer-events", "auto");

        // Popup Window
        var position    = {
            left    : (settings.left == "auto") ? ((overlay.width() - settings.width) / 2) : parseInt(settings.left, 10),
            top     : (settings.top == "auto") ? ((overlay.height() - settings.height) / 2) : parseInt(settings.top, 10)
        };

        var popupWindow = $("<section>", {
            class   : "popup_box"
        })
        .css({
        	"width":settings.width
        })
        .data({
            originalTarget      : originalTarget,
            originalParent      : originalTarget.parent(),
            overlay             : overlay,
            settings            : settings,
            opened              : false,
            currentPosition     : position
        })
        .appendTo(overlay);

        // Titlebar
        var titlebar = $("<div>", {
            class   : "pop_header"
        })
        .appendTo(popupWindow);

        // Text
        $("<h1>", {
        	class   : "pop_header__title",
            text    : settings.title
        })
        .appendTo(titlebar);

        // Buttons
        if (settings.buttons.close) {
        	var closeButton = $("<a>", {
                class   : "pop_header__close"
            })
            .attr("title", settings.buttonsTexts.close)
            .attr("href", "javascript:void(0);")
            .on("click", _ButtonClose_Click)
            
        	var closeText = $("<span>", {
        		text    : "닫기"
            })
            .attr("hidden","")
            .appendTo(closeButton);
            
        	closeButton.appendTo(titlebar);
        }

        // Content
        originalTarget.show().appendTo(popupWindow);

        // Final Settings
        if (!settings.modal) overlay.width(0).height(0);
        overlay.hide();
        if (settings.autoOpen) _Open(popupWindow);
    }

    function _Open(popupWindow){
        if (!_CheckPopupWindow(popupWindow) || popupWindow.data("opened")) return;
        popupWindow.data("overlay").show();
        popupWindow.data("opened", true);
        _TriggerEvent(popupWindow, "open");
    }
    function _Close(popupWindow){
        if (!_CheckPopupWindow(popupWindow) || !popupWindow.data("opened")) return;
        popupWindow.data("overlay").hide();
        popupWindow.data("opened", false);
        _TriggerEvent(popupWindow, "close");
    }

    function _TriggerEvent(popupWindow, eventName){
        var eventData;
        popupWindow.data("originalTarget").trigger(eventName + ".popup_box", eventData);
    }

    function _CheckPopupWindow(popupWindow){
        if (popupWindow.length) return true;
        _Warning("jQuery PopupWindow is not initialized on this element");
        return false;
    }

    function _Warning(message){
        message = "jQuery PopupWindow Warning: " + message;
        if (window.console.warn) {
            console.warn(message);
        } else if (window.console.log) {
            console.log(message);
        }
    }


    // **************************************************
    //  EVENT HANDLERS
    // **************************************************
    function _ButtonClose_Click(event){
        _Close($(event.currentTarget).closest(".popup_box"));
    }


    $(function(){
        _mainContainer = $("<div>", {
            class   : "popupwindow_container"
        })
        .appendTo("body");
    });

}(jQuery));
