
/**
 * 弹出窗体 
 * options
 * 		className - {String} DOM className
 * 		items - {Array.<Element>}
 * 		width - {Number|undefined} 宽度
 * 		height - {Number|undefined} 高度
 * 		top - {Number|undefined} position.top
 * 		left - {Number|undefined} position.left
 * 		right - {Number|undefined} position.right
 * 		bottom - {Number|undefined} position.bottom
 * 		onClose - {Function} function(){}
 */
FixWindow = function(options) {
	var opt_options = options ? options : {};

	var element = this.element = document.createElement("div");
	element.className = opt_options.className || "popfixwindow";
	element.style.width = opt_options.width ? (opt_options.width + "px") : "";
	element.style.height = opt_options.height ? (opt_options.height + "px") : "";
	element.style.top = opt_options.top ? (opt_options.top + "px") : "";
	element.style.left = opt_options.left ? (opt_options.left + "px") : "";
	element.style.right = opt_options.right ? (opt_options.right + "px") : "";
	element.style.bottom = opt_options.bottom ? (opt_options.bottom + "px") : "";
	
	//close 按钮
	var closeElement = document.createElement("a");
	closeElement.className = "closewindow";
	element.appendChild(closeElement);
	var span = document.createElement("span");
	span.className = "glyphicon glyphicon-remove";
	closeElement.appendChild(span);
	
	this.onClose = opt_options.onClose;
	var _this = this;
	closeElement.onclick = function(){
		_this.hide();
	}
	
	//items
	this.itemContainerElement = document.createElement("div");
	element.appendChild(this.itemContainerElement);
	this.items = opt_options.items || [];
	this.redraw();
}

/**
 * 重绘fixwindow DOM
 */
FixWindow.prototype.redraw = function(){
	var containerElement = this.itemContainerElement;
	containerElement.innerHTML = "";
	
	var item;
	for(var i = 0; i < this.items.length; i++){
		item = this.items[i];
		containerElement.appendChild(item);
	}
}

/**
 * 显示
 */
FixWindow.prototype.show = function(){
	this.element.style.display = "block";
}

/**
 * 隐藏
 */
FixWindow.prototype.hide = function(){
	this.element.style.display = "none";
	if(this.onClose){
		this.onClose();
	}
}

/**
 * 添加item Element事件
 * @param options
 * 		selector {String} Element选择器，id使用"#id"，class使用".class",符合jquery规则
 * 		events {String} 一个或多个用空格分隔的事件类型和可选的命名空间，例如"click"、"focus click"、"keydown.myPlugin"。
 * 		handler function(evt){}
 */
FixWindow.prototype.addItemEvent = function(options){
	var selector = options.selector;
	var events = options.events;
	var handler = options.handler;
	
	var $item = $(this.itemContainerElement);
	$item.on(events,selector,handler);
}

/**
 * 移除item Element事件
 * @param options
 * 		selector {String} Element选择器，id使用"#id"，class使用".class",符合jquery规则
 * 		events {String} 一个或多个用空格分隔的事件类型和可选的命名空间，例如"click"、"focus click"、"keydown.myPlugin"。
 * 		handler function(evt){}
 */
FixWindow.prototype.removeItemEvent = function(options){
	var selector = options.selector;
	var events = options.events;
	var handler = options.handler;
	
	var $item = $(this.itemContainerElement);
	$item.off(events,selector,handler);
}