/**
 * @require ol.js
 */

/**
 * @namespace bxmap.overlay
 */
bxmap.overlay = bxmap.overlay || {};

/*----------------------默认覆盖层{bxmap.overlay.Defaults}-------------------*/
/**
 * @classdesc 默认覆盖层
 * @constructor
 * @extends {ol.Object}
 *
 * @example <caption> 获取Overlay </caption>
 * var overlays = new bxmap.overlay.Defaults();
 * var popup = overlays.get("popup");
 */
bxmap.overlay.Defaults = function() {
	ol.Object.call(this);
	// popup
	var popup = new bxmap.overlay.Popup({
		id: "10001",
		panToCenter: true,
		showClose: true,
		positioning: "bottom-center",
		offset: [0, -20]
	});
	this.set("popup", popup);
}
ol.inherits(bxmap.overlay.Defaults, ol.Object);

/*----------------------弹框Popup类{bxmap.overlay.Popup}---------------------*/
/**
 * @classdesc 弹框Popup类
 * @constructor
 * @extends {ol.Overlay}
 * @param options
 * @param options.id {number | string | undefined}  可以通过ol.Map#getOverlayById方法获取
 * @param options.offset {Array.<number> | undefined} 偏移多少像素，格式为屏幕坐标[x,y],默认[0,0]
 * @param options.positioning {String}  'auto'|'bottom-auto'|'bottom-left'|'bottom-center'|'bottom-right'|'top-auto'|'top-left'|'top-center'|'top-right'|'center-auto'|'center-left'|'center-right'
 * @param options.popupClass {String} 默认为'default'，'default orange'|'black'|'tips'|'tips orange'|'warning'|'tooltips'
 * @param options.showTitle {Boolean} 默认为true，是否显示标题
 * @param options.showClose {Boolean} 默认为true，是否显示关闭按钮
 * @param options.panToCenter {Boolean} 默认为true，是否定位至中心
 * @api stable
 * @fires 'closed'
 *
 * @example <caption> 初始化popup </caption>
 * var popup = new bxmap.overlay.Popup({
 *	showClose: true,
 *	positioning: "auto",
 *	autoPan: true,
 *	onclose: function(){ console.log("You close the box"); }
 * });
 * map.addOverlay(popup);
 * //显示弹框
 * popup.show(coordinate, "Hello!");
 * //关闭弹框
 * popup.close();
 */
bxmap.overlay.Popup = function(options) {
	var opt_options = options || {};
	
	/**
	 * @readonly
	 * @description 弹框时，位置居中，默认为true
	 */
	this.panToCenter = opt_options.panToCenter == null ? true : opt_options.panToCenter;
	/**
	 * @readonly
	 * @description 是否显示标题，默认为true
	 */
	this.shownTitle = opt_options.showTitle == null ? true : opt_options.showTitle;
	/**
	 * @readonly
	 * @description 是否显示关闭按钮，默认为true
	 */
	this.shownClose = opt_options.showClose == null ? true : opt_options.showClose;
	
	//element
	var self = this;
	var elt = $("<div>");

    /**
     * @readonly
     * @description element
     */
	this.popupElement = opt_options.element = elt.get(0);

    /**
     * @readonly
     * @description title
     */
	this.title = $("<h3>").addClass("popup-title").appendTo(elt).get(0);
	
	// Closebox
	$("<button>").addClass("closeBox").appendTo(elt).click(function() {
		self.close();
		return false;
	});
	
	// Anchor div
	$("<div>").addClass("anchor").appendTo(elt);
	//var d = $("<div>").addClass('ol-overlaycontainer-stopevent').appendTo(elt);
	
	var d = elt;
	// Content
	this.content = $("<div>").addClass("content").appendTo(d).get(0);

	// Stop event
	opt_options.stopEvent = true; //禁止弹框上拖动时，地图随之移动
//	d.on("mousedown touchstart", function(e) {
//		e.stopPropagation();
//	})
	//弹框超出范围，自动调整
	opt_options.autoPan = true;
	opt_options.autoPanAnimation = { duration: 100 };

	ol.Overlay.call(this, opt_options);
	
	this.setPopupClass(opt_options.popupClass);
	this.setPositioning(opt_options.positioning);
	this.showClose(this.shownClose);
	this.showTitle(this.shownTitle);
}
ol.inherits(bxmap.overlay.Popup, ol.Overlay);

/**
 * @private
 * @description 根据Positioning获取弹出窗口的CSS类
 */
bxmap.overlay.Popup.prototype.getClassPositioning = function() {
	var c = "";
	var pos = this.getPositioning();
	if (/bottom/.test(pos)) c += "ol-popup-bottom ";
	if (/top/.test(pos)) c += "ol-popup-top ";
	if (/left/.test(pos)) c += "ol-popup-left ";
	if (/right/.test(pos)) c += "ol-popup-right ";
	if (/^center/.test(pos)) c += "ol-popup-middle ";
	if (/center$/.test(pos)) c += "ol-popup-center ";
	return c;
}

/**
 * @description 设置是否显示close按钮
 * @param {Boolean} shown true，显示；false，不显示
 */
bxmap.overlay.Popup.prototype.showClose = function(shown) {
	this.shownClose = shown;
	if (shown){
		$(this.popupElement).addClass("hasclosebox");
	} else{
		$(this.popupElement).removeClass("hasclosebox");
	}
}

/**
 * @description 设置是否显示标题
 * @param {Boolean} shown true，显示；false，不显示
 */
bxmap.overlay.Popup.prototype.showTitle = function(shown) {
	this.shownTitle = shown;
	if (shown){
		$(this.popupElement).addClass("haspopuptitle");
	} else{
		$(this.popupElement).removeClass("haspopuptitle");
	}
}

/**
 * @description 设置Popup CSS样式
 * @param {String} c CSS样式
 */
bxmap.overlay.Popup.prototype.setPopupClass = function(c) {
	$(this.popupElement).removeClass().addClass("ol-popup " + (c || "default") + " " + this.getClassPositioning() + (this.shownClose ? " hasclosebox" : "") + (this.shownTitle ? " haspopuptitle" : ""));
}

/**
 * @description 添加 CSS样式
 * @param {String} c CSS样式
 */
bxmap.overlay.Popup.prototype.addPopupClass = function(c) {
	$(this.popupElement).addClass(c);
}

/**
 * @description 移除 CSS样式
 * @param {String} c CSS样式
 * @api stable
 */
bxmap.overlay.Popup.prototype.removePopupClass = function(c) {
	$(this.popupElement).removeClass(c);
}

/**
 * @description 设置弹框位置
 * @param {String} pos 有效值参考类参数positioning属性
 */
bxmap.overlay.Popup.prototype.setPositioning = function(pos) {
	//自动
	if (/auto/.test(pos)) {
		this.autoPositioning = pos.split('-');
		if (this.autoPositioning.length == 1){
			this.autoPositioning[1] = "auto";
		}
	} else{
		this.autoPositioning = false;
	}
		
	pos = pos.replace(/auto/g, "center");
	if (pos == "center"){
		pos = "bottom-center";
	}
	this.setPositioning_(pos);
}
/**
 * @private
 * @description 设置
 * @param {String} pos
 */
bxmap.overlay.Popup.prototype.setPositioning_ = function(pos) {
	ol.Overlay.prototype.setPositioning.call(this, pos);
	$(this.popupElement).removeClass("ol-popup-top ol-popup-bottom ol-popup-left ol-popup-right ol-popup-center ol-popup-middle");
	$(this.popupElement).addClass(this.getClassPositioning());
}

/**
 * @description 显示弹框
 * @param {ol.Coordinate|string} coordinate [x,y]坐标位置
 * @param {string|undefined} html html内容，如果为null，则为上次弹出的内容
 * @param {string|undefined} title 标题，如果为null，则为上次弹出的标题
 */
bxmap.overlay.Popup.prototype.show = function(coordinate, html , title) {
	if (!html && typeof (coordinate) == 'string') {
		html = coordinate;
		coordinate = null;
	}

	var self = this;
	var map = this.getMap();
	if (!map) return;

	if (html && html !== this._prevHTML) {
		this._prevHTML = html;
		$(this.content).html("").append(html);
		// 刷新
		$("*", this.content).load(function() {
			map.renderSync();
		})
	}
	if(title && title !== this._prevTitle){
		this._prevTitle = title;
		$(this.content).html("").append(title);
	}

	if (coordinate) { 
		// Auto
		if (this.autoPositioning) {
			var p = map.getPixelFromCoordinate(coordinate);
			var s = map.getSize();
			var pos = [];
			if (this.autoPositioning[0] == 'auto') {
				pos[0] = (p[1] < s[1] / 3) ? "top" : "bottom";
			} else{
				pos[0] = this.autoPositioning[0];
			}
			pos[1] = (p[0] < 2 * s[0] / 3) ? "left" : "right";
			this.setPositioning_(pos[0] + "-" + pos[1]);
		}
		
		this.setPosition(coordinate);
		// Set visible class (wait to compute the size/position first)
		$(this.popupElement).parent().show();
		this._tout = setTimeout(function() {
			$(self.popupElement).addClass("visible");
		}, 0);
		
		//是否居中
		if(this.panToCenter){
			//bxmap.common.panAnimationToCenter(map, coordinate);
            map.getView().setCenter(coordinate);
		}
	}
}

/**
 * @description 关闭弹框
 */
bxmap.overlay.Popup.prototype.close = function() {
	this.setPosition(undefined);
	if (this._tout){
		clearTimeout(this._tout)
	}
	$(this.popupElement).removeClass("visible");
	
	this.dispatchEvent({ type:'closed'});
}