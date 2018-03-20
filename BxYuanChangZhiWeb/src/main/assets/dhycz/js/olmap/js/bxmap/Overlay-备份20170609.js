/**
 * @require ol.js
 */
bxmap.overlay = bxmap.overlay || {};

/*----------------------默认覆盖层{bxmap.overlay.Defaults}-------------------*/
/**
 * 默认覆盖层
 */
bxmap.overlay.Defaults = function () {
	ol.Object.call(this);
	// popup
	var popup = new bxmap.overlay.Popup();
	this.set("popup", popup);
	// popover
 	var popover = new bxmap.overlay.Popover({
		title_text:"提示",
		show_close: true,
		placement:"top"
	});
 	this.set("popover", popover);
}
ol.inherits(bxmap.overlay.Defaults, ol.Object);

/*----------------------弹框Popup类{bxmap.overlay.Popup}---------------------*/
/**
 * 弹框Popup类
 * options
 * 		class_name - {String}样式，默认为ol-popup
 * 		title_text - {String}文本
 * 		pan_center - {Boolean} 弹窗位置是否平移至中心，默认为true
 * @fires "closed"
 */
bxmap.overlay.Popup = function(options){
	var opt_options = options ? options:{};
	this.panCenter = opt_options.pan_center == null ? true : pan_center;
	
    this.container = document.createElement('div');
    this.container.className = 'ol-popup';

    this.closer = document.createElement('a');
    this.closer.className = 'ol-popup-closer';
    this.container.appendChild(this.closer);

    var that = this;
    this.closer.addEventListener('click', function(evt) {
        that.close();
        evt.preventDefault();
    }, false);

	this.titleText = opt_options.title_text ? opt_options.title_text : '';
    this.title = document.createElement('div');
    this.title.className = 'popup-title';
    this.title.innerHTML = this.titleText;
    this.container.appendChild(this.title);
    
    this.content = document.createElement('div');
    this.content.className = 'ol-popup-content';
    this.container.appendChild(this.content);
    
    var overlay_opt = {
    	element: this.container,
    	insertFirst: true
    };
    if(!this.panCenter){
    	//超出边界时自动调整
    	overlay_opt.autoPan = true;
    	overlay_opt.autoPanAnimation = {duration: 350};
    }
    var new_pot=$.extend({},opt_options,overlay_opt);
    ol.Overlay.call(this, new_pot);	
}

ol.inherits(bxmap.overlay.Popup, ol.Overlay);

/**
 * 显示弹框
 * @param coordinate [x,y]坐标位置
 * @param html	{String}内容html
 * @param title {String}title
 */
bxmap.overlay.Popup.prototype.show = function(coordinate, html, title){
	//内容赋值
	this.content.innerHTML = html ? html : "";
	
	//title赋值
	var titleText = title ? title : this.titleText;
    this.title.innerHTML = titleText;

	this.setPosition(coordinate);
	if(this.panCenter){
		var map = this.getMap();
		//平移至中心
		bxmap.common.panAnimationToCenter(map,coordinate);
	}
}

/**
 * 关闭弹框
 */
bxmap.overlay.Popup.prototype.close = function(){
	this.setPosition(undefined);
	this.closer.blur();
	this.dispatchEvent({ type:'closed'});
}


/*----------------------弹框Popover类{bxmap.overlay.Popover}---------------------*/
/**
 * 弹框Popover类
 * options
 * 		placement - {String} 弹框位置top|bottom|left|right|auto，默认为top
 * 		title_text - {String} title显示内容
 * 		show_close - {Boolean} 是否显示关闭按钮，默认为true
 * 		pan_center - {Boolean} 弹窗位置是否平移至中心，默认为true
 * @fires "closed"
 */
bxmap.overlay.Popover = function(options){
	var opt_options = options ? options : {};
	this.titleText = opt_options.title_text ? opt_options.title_text : '';
	this.placement = opt_options.placement ? opt_options.placement : 'top';
	this.showClose = opt_options.show_close == null ? true : opt_options.show_close;
	this.panCenter = opt_options.pan_center == null ? true : opt_options.pan_center;
	
    var container = this.container = document.createElement('div');
	var element = this.element = document.createElement('div');
	this.container.appendChild(this.element);
    
	ol.Overlay.call(this, {
		element: this.container
    });
	
	var _this = this;
	$(container).on("click", ".popover .close" , function(){
		_this.close();
    });
}
ol.inherits(bxmap.overlay.Popover, ol.Overlay);

/**
 * 设置弹框放置位置
 * @param placement {String} 弹框位置top|bottom|left|right|auto
 */
bxmap.overlay.Popover.prototype.setPlacement = function(placement){
	if(placement){
		this.placement = placement;	
	}
}

/**
 * 显示
 * @param coordinate [x,y]坐标位置
 * @param html {String}内容html
 * @param title {String}title
 */
bxmap.overlay.Popover.prototype.show = function(coordinate, html, title){
	this.close();
	
	var titleText = title ? title : this.titleText;
	var contentText = html ? html : "";
	//关闭按钮
	if(this.showClose){
		titleText += ' <a class="close" data-dismiss="alert">×</a>';
	}
	
	var element = this.element;
	this.setPosition(coordinate);
	$(element).popover({
        'placement': this.placement,
        'animation': false,
        'html': true,
        'title' : titleText,
        'content': contentText
      });
	$(element).popover('show');
	
	if(this.panCenter){
		var map = this.getMap();
		//平移至中心
		bxmap.common.panAnimationToCenter(map,coordinate);
	}
}

/**
 * 关闭
 */
bxmap.overlay.Popover.prototype.close = function(){
	var element = this.element;
	$(element).popover('destroy');
	this.dispatchEvent({ type:'closed'});
}