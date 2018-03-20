
/**
 * 工具箱 
 * options
 * 		target - {Element|String|undefined} DOM 目标dom
 */
Toolbox = function(options) {
	var opt_options = options ? options : {};
	
	var target = this.target = opt_options.target;
	if(target){
		if(typeof target === 'string'){
			this.target = document.getElementById(target);
		}
	}else{
		this.target = document.createElement("div");
		this.target.className = "tool_next";
	}
	
	//toolbox DOM
	this.element = document.createElement("div");
	this.element.className = "navMenu";
	this.target.appendChild(this.element);
	
	//toolgroup 数组
	this.toolGroups = [];
	//toolGroups 容器dom
	this.toolGroupContainerElement = null;
	//toolgroups 是否可见，默认可见
	this._toolGroupsVisibility = true;
	//toolgroups 可见性变化监听
	this.toolGroupsVisibilityListeners = [];
	
	this._initialize();
}

/**
 * 初始化
 */
Toolbox.prototype._initialize = function(){
	var text = document.createElement("span");
	text.innerHTML = "工具箱";
	this.element.appendChild(text);
	
	//toolGroup 容器
	var ul = this.toolGroupContainerElement = document.createElement("ul");
	ul.className = "toolCenter";
	this.element.appendChild(ul);
	
	var _this = this;
	//绑定toolbox点击事件
	this.element.addEventListener("click",function(){
		var visibility = !_this._toolGroupsVisibility;
		_this.setToolGroupsVisible(visibility);
	});
}

/**
 * 设置toolbox是否可见
 * @param visible {Boolean} true,可见；false,不可见
 */
Toolbox.prototype.setVisible = function(visible){
	this.element.style.display = visible ? "block":"none";
}

/**
 * 获取toolgroups是否可见
 * @returns {Boolean} true,可见；false,不可见
 */
Toolbox.prototype.getToolGroupsVisible = function(){
	return this._toolGroupsVisibility;
}

/**
 * 设置toolgroups是否可见
 * @param visible {Boolean} true,可见；false,不可见
 */
Toolbox.prototype.setToolGroupsVisible = function(visible){
	if(visible === this._toolGroupsVisibility) return;
	this._toolGroupsVisibility = visible;
	
	//是否可见
	var ulElement = this.toolGroupContainerElement;
	if(this._toolGroupsVisibility){
		ulElement.style.display = "block";
	}else{
		ulElement.style.display = "none";
	}
	
	//执行绑定函数
	var func;
	for(var i = 0; i < this.toolGroupsVisibilityListeners.length; i++){
		func = this.toolGroupsVisibilityListeners[i];
		func(this._toolGroupsVisibility);
	}
}

/**
 * 重绘工具箱 DOM
 */
Toolbox.prototype.redraw = function(){
	//清空html
	var ul = this.toolGroupContainerElement;
	ul.innerHTML= "";
	
	var toolgroup,li;
	for(var i = 0;i < this.toolGroups.length;i++){
		toolgroup = this.toolGroups[i];
		li = document.createElement("li");
		if(toolgroup.element == null) continue;
		
		li.appendChild(toolgroup.element);
		ul.appendChild(li);
	}
	//last li 样式
	if(li){
		li.className = "borderNo";
	}
}

/**
 * 添加工具组
 * @param toolgroup {ToolGroup} 对象
 */
Toolbox.prototype.addToolGroup = function(toolgroup){
	if(toolgroup){
		this.toolGroups.push(toolgroup);
	}
}

/**
 * 移除工具组
 * @param toolgroup {ToolGroup} 对象
 */
Toolbox.prototype.removeToolGroup = function(toolgroup){
	if(toolgroup == null || this.toolGroups == null) return;
	var arr = this.toolGroups;
	var i = arr.indexOf(toolgroup);
	var found = i > -1;
	if (found) {
		arr.splice(i, 1);
	}
}

/**
 * 增加监听方法
 * @param func function(visibility)方法，visibility-可见性
 */
Toolbox.prototype.addToolGroupsVisibilityListener = function(func){
	if(func){
		this.toolGroupsVisibilityListeners.push(func);
	}
}

/**
 * 移除监听方法
 * @param func function(visibility)方法，visibility-可见性
 */
Toolbox.prototype.removeToolGroupsVisibilityListener = function(func){
	if(func == null || this.toolGroupsVisibilityListeners == null) return;
	var arr = this.toolGroupsVisibilityListeners;
	var i = arr.indexOf(func);
	var found = i > -1;
	if (found) {
		arr.splice(i, 1);
	}
}

/*-------------------------------------------------------*/
/*                       工具组  {ToolGroup}                */
/*-------------------------------------------------------*/
/**
 * 工具组
 * options
 *		className - {String} DOM Element className值
 *		tip - {String|undefined} 鼠标悬停时提示信息
 *		onMouseOver - function(evt){} 鼠标悬停事件
 *		onMouseOut - function(evt){} 鼠标移除事件
 *		onClick - function(evt){} 点击事件
 */
ToolGroup = function(options){
	var opt_options = options ? options : {};
	
	var className = this._className = opt_options.className;
	
	var element = this.element = document.createElement("div");
	
	var imageElement  = this._imageElement = document.createElement("div");
	imageElement.className = this._className;
	element.appendChild(imageElement);
	
	//提示信息
	var tip = opt_options.tip;
	if(tip){
		element.title = tip;
	}
	
	this.onMouseOver = opt_options.onMouseOver;
	this.onMouseOut = opt_options.onMouseOut;
	this.onClick = opt_options.onClick;
	
	this._toolbars = [];
	
	//绑定事件
	var _this = this;
	element.addEventListener("mouseover",function(evt){
		//鼠标悬停样式
		imageElement.className = className + "_hover";
		//绑定事件
		if(_this.onMouseOver){
			_this.onMouseOver(evt);
		}
		//设置toolbar可见
		for(var i = 0; i < _this._toolbars.length;i++){
			_this._toolbars[i].setVisible(true);
		}
	});
	element.addEventListener("mouseout",function(evt){
		//鼠标悬停样式
		imageElement.className = className;
		//绑定事件
		if(_this.onMouseOut){
			_this.onMouseOut(evt);
		}
		//设置toolbar不可见
		for(var i = 0; i < _this._toolbars.length;i++){
			_this._toolbars[i].setVisible(false);
		}
	});
	element.addEventListener("click",function(evt){
		if(_this.onClick){
			_this.onClick(evt);
		}
	});
}

/**
 * 添加toolbar
 * @param toolbar
 */
ToolGroup.prototype.addToolBar = function(toolbar){
	if(toolbar){
		this._toolbars.push(toolbar);
	}
}

/**
 * 重绘toolgroup DOM
 */
ToolGroup.prototype.redraw = function(){
	//清空
	var element = this.element;
	element.innerHTML = "";
	
	element.appendChild(this._imageElement);
	
	var toolbar;
	for(var i = 0; i < this._toolbars.length; i++){
		toolbar = this._toolbars[i];
		element.appendChild(toolbar.element);
	}
}

/*-------------------------------------------------------*/
/*                       工具条  {ToolBar}                  */
/*-------------------------------------------------------*/
/**
 * 工具条
 * options
 *		target - {String|Element|undefined} DOM Element
 *		className - {String} DOM Element className值
 *		css - {String} DOM Element style值
 *		showClose - {Boolean} 是否显示关闭按钮,默认为true
 *		onVisibilityChanged - {Function(visibility){}}监听工具条可见状态
 */
ToolBar = function(options){
	var opt_options = options ? options : {};
	
	var target = this.target = opt_options.target;
	if(target){
		if(typeof target === 'string'){
			this.target = document.getElementById(target);
		}
	}
	
	/**是否显示关闭按钮*/
	this._showClose = options.showClose == null ? true : options.showClose;
	/**工具条默认不显示*/
	this._visibility = false;
	/**监听工具条可见状态*/
	this.onVisibilityChanged = options.onVisibilityChanged;
		
	var element = this.element = document.createElement("div");
	element.className = opt_options.className ? opt_options.className : "";
	if(opt_options.css){
		$(element).css(options.css);
	}
	element.style.display = "none";//默认不显示
	
	this._ulElement = document.createElement("ul");
	this.element.appendChild(this._ulElement);

	//ul列表
	if(target){
		this.target.appendChild(element);
	}
	
	if(this._showClose){
		//close 按钮
		var closeElement = this._closeElement = document.createElement("a");
		closeElement.className = "closetoolbar";
		element.appendChild(closeElement);
		var span = document.createElement("span");
		span.className = "glyphicon glyphicon-remove";
		closeElement.appendChild(span);
		
		var _this = this;
		closeElement.onclick = function(){
			_this.setVisible(false);
		}
	}
	
	this.tools = [];
}

/**
 * 添加元素
 * @param element DOM 
 */
ToolBar.prototype.addElement = function(element){
	if(element){
		this.tools.push(element);	
	}
}

/**
 * 添加工具
 * @param tool {Tool} Tool对象
 */
ToolBar.prototype.addTool = function(tool){
	if(tool == null) return;
	this.addElement(tool.element);
}

/**
 * 绘制toolbar DOM
 */
ToolBar.prototype.redraw = function(){
	//清空元素
	var ul = this._ulElement;
	ul.innerHTML = "";
	
	var tool,li;
	for(var i = 0;i < this.tools.length;i++){
		tool = this.tools[i];
		li = document.createElement("li");		
		li.appendChild(tool);
		ul.appendChild(li);
	}
}

/**
 * 设置toolbar是否可见
 * @param visible {Boolean} true可见，false不可见
 */
ToolBar.prototype.setVisible = function(visible){
	this.element.style.display = visible === true ? "block":"none";
	
	if(visible === this._visibility) return;
	this._visibility = visible;
	if(this.onVisibilityChanged){
		this.onVisibilityChanged(visible);
	}
}

/**
 * 获取toolbar是否可见
 * @returns {Boolean}
 */
ToolBar.prototype.getVisible = function(){
	return this._visibility;
}

/*-------------------------------------------------------*/
/*                       工具接口  {Tool}                    */
/*-------------------------------------------------------*/
/**
 * 工具接口，不要使用该接口实例化，使用子类实例化
 * @param options
 * 		visibility - {Boolean} 是否可见，默认可见
 * 		enabled - {Boolean} 是否可用，默认可用
 * 		hover - {Boolean} 是否为鼠标盘旋状态，默认为false
 * 		active - {Boolean} 是否为激活状态，默认为true
 */
Tool = function(options){
	/**DOM*/
	this.element = null;
	var opt_options = options || {};
	/**是否可见*/
	this._visibility = opt_options.visibility == null ? true : opt_options.visibility;
	/**是否可用*/
	this._enabled = opt_options.enabled == null ? true : opt_options.enabled;
	/**是否为鼠标盘旋状态*/
	this._isHover = opt_options.hover == null ? false : opt_options.hover;
	/**是否为激活状态*/
	this._active = opt_options.active == null ? false : opt_options.active;
	
	/**监听器*/
	this._eventListeners = {
		"visibilityChanged":[],//监听可见状态发生改变
		"enabledChanged":[],//监听可用状态发生改变
		"hoverChanged":[],//监听鼠标盘旋状态发生改变
		"activeChanged":[]//监听激活状态发生改变
	};
}

/**Tool 监听事件类型*/
Tool.EventTypes = {
	"visibilityChanged": "visibilityChanged",
	"enabledChanged": "enabledChanged",
	"hoverChanged": "hoverChanged",
	"activeChanged": "activeChanged"
};

/**
 * 设置/获取title
 * @param title {String}
 */
Tool.prototype.title = function(title){
	//获取title
	if(title === undefined) {
		return this.element ? this.element.title : "";
	}
	//设置title
	if(this.element){
		this.element.title = title || "";
	}
}

/**
 * 设置/获取 element.innerHTML
 * @param title {String}
 */
Tool.prototype.innerHTML = function(html){
	//获取innerHTML
	if(html === undefined) {
		return this.element ? this.element.innerHTML : "";
	}
	//设置innerHTML
	if(this.element){
		this.element.innerHTML = html || "";
	}
}

/**
 * 设置是否可见
 * @param visible {Boolean} true可见，false不可见
 */
Tool.prototype.setVisible = function(visible){
	if(visible === this._visibility) return;
	
	this.element.style.display = visible ? "block":"none";
	this._visibility = visible;
	
	//执行绑定监听
	var listeners = this._eventListeners[Tool.EventTypes.visibilityChanged];
	var func;
	for(var i = 0; i < listeners.length; i++){
		func = listeners[i];
		if(func){
			func(this._visibility);
		}
	}
}

/**
 * 获取是否可见
 * @returns {Boolean}
 */
Tool.prototype.getVisible = function(){
	return this._visibility;
}

/**
 * 设置是否可用
 * @param visible {Boolean} true可用，false不可用
 */
Tool.prototype.setEnabled = function(enabled){
	if(enabled === this._enabled) return;
	this._enabled = enabled;
	
	//执行绑定监听
	var listeners = this._eventListeners[Tool.EventTypes.enabledChanged];
	var func;
	for(var i = 0; i < listeners.length; i++){
		func = listeners[i];
		if(func){
			func(this._enabled);
		}
	}
}

/**
 * 获取是否可见
 * @returns {Boolean}
 */
Tool.prototype.getEnabled = function(){
	return this._enabled;
}

/**
 * 设置是否为鼠标盘旋状态
 * @param isHover {Boolean} true是，false否
 */
Tool.prototype.setHover = function(isHover){
	if(isHover === this._isHover) return;
	this._isHover = isHover;
	
	//执行绑定监听
	var listeners = this._eventListeners[Tool.EventTypes.hoverChanged];
	var func;
	for(var i = 0; i < listeners.length; i++){
		func = listeners[i];
		if(func){
			func(this._isHover);
		}
	}
}

/**
 * 获取是否为鼠标盘旋状态
 * @returns {Boolean}
 */
Tool.prototype.getHover = function(){
	return this._isHover;
}

/**
 * 设置是否为激活状态
 * @param active {Boolean} true是，false否
 */
Tool.prototype.setActive = function(active){
	if(active === this._active) return;
	this._active = active;
	
	//执行绑定监听
	var listeners = this._eventListeners[Tool.EventTypes.activeChanged];
	var func;
	for(var i = 0; i < listeners.length; i++){
		func = listeners[i];
		if(func){
			func(this._active);
		}
	}
}

/**
 * 获取是否为激活状态
 * @returns {Boolean}
 */
Tool.prototype.getActive = function(){
	return this._active;
}

/**
 * 为element添加事件
 * @param options
 * 		selector {String} (可选)Element选择器，id使用"#id"，class使用".class",符合jquery规则
 * 		events {String} 一个或多个用空格分隔的事件类型和可选的命名空间，例如"click"、"focus click"、"keydown.myPlugin"。
 * 		handler function(evt){}
 */
Tool.prototype.addEvent = function(options){
	var selector = options.selector;
	var events = options.events;
	var handler = options.handler;
	if(this.element){
		var $item = $(this.element);
		if(selector){
			$item.on(events,selector,handler);
		}
		else{
			$item.on(events,handler);
		}	
	}
}

/**
 * 为element移除事件
 * @param options
 * 		selector {String} (可选)Element选择器，id使用"#id"，class使用".class",符合jquery规则
 * 		events {String} 一个或多个用空格分隔的事件类型和可选的命名空间，例如"click"、"focus click"、"keydown.myPlugin"。
 * 		handler function(evt){}
 */
Tool.prototype.removeEvent = function(options){
	var selector = options.selector;
	var events = options.events;
	var handler = options.handler;
	if(this.element){
		var $item = $(this.element);
		if(selector){
			$item.off(events,selector,handler);
		}
		else{
			$item.off(events,handler);
		}	
	}
}

/**
 * 增加监听方法
 * @param event {String} 有效值参考Tool.EventTypes
 * @param func function(evt)
 */
Tool.prototype.addEventListener = function(event, func){
	if(func && this._eventListeners[event]){
		this._eventListeners[event].push(func);
	}
}

/**
 * 移除监听方法
 * @param func function(visibility)方法，visibility-可见性
 */
Tool.prototype.removeEventListener = function(event, func){
	if(func == null || this._eventListeners[event] == null) return;
	var arr = this._eventListeners[event];
	var i = arr.indexOf(func);
	var found = i > -1;
	if (found) {
		arr.splice(i, 1);
	}
}


/*-------------------------------------------------------*/
/*                工具箱工具对象  {ToolboxTool}                */
/*-------------------------------------------------------*/
/**
 * 工具箱工具对象 仅有click事件
 * @param options
 * 		className - {String} DOM 样式className
 * 		title - {String|undefined} 鼠标悬停时提示信息
 * 		onClick - function(evt) 鼠标点击事件
 */
ToolboxTool = function(options){
	var opt_params = {
		canListenEnabled: false
	};
	Tool.call(this, opt_params);
	var opt_options = options || {};
	var div = this.element = document.createElement("div");
	var className = div.className = opt_options.className || "";
	div.title = opt_options.title || "";
	
	var _this = this;
	//绑定点击事件
	if(options.onClick){
		this.addEvent({
			events: "click",
			handler: function(evt){
				options.onClick(evt);
				_this.setActive(true);
			}
		});
	}
	
	//绑定鼠标mouseover/mouseout事件
	this.addEvent({
		events: "mouseover",
		handler: function(evt){ 
			div.className = className + "_hover"; 
			_this.setHover(true);
		}
	});
	this.addEvent({
		events: "mouseout",
		handler: function(evt){ 
			div.className = className; 
			_this.setHover(false);
		}
	});
}
/**继承Tool*/
ToolboxTool.prototype = Object.create(Tool.prototype);
ToolboxTool.prototype.constructor = ToolboxTool;

/*-------------------------------------------------------*/
/*        WFS图层编辑工具条按钮类工具  {WFSLayerButtonTool}       */
/*-------------------------------------------------------*/
/**
 * WFS图层编辑工具条按钮类工具
 * iconElement属性已公开，如果有需要可对该div进行操作，例如设置className，title等
 * @param options
 * 		className - {String} 工具element 样式className
 * 		title - {String|undefined} 鼠标悬停时提示信息
 *		enabled - {Boolean} 是否可用，默认为false
 *		clickToggleActive - {Boolean} 点击是否切换按钮激活状态,默认为true
 * @fires "enabledChanged","activeChanged";使用addEventListener()方法监听;
 * "click"事件使用addEvent()方法监听
 * 结构：
	<div class="wfsboxshadow currentNav" title="新增面"> //element 容器
		<div class="wfsstartedit">	//图标样式
			<div class="wfstooldisable">//遮罩层
			</div>
		</div>
	</div>
 */
WFSLayerButtonTool = function(options){
	var opt_options = options || {};
	var opt_params = {
		enabled : opt_options.enabled == null ? false : opt_options.enabled,
		active: false
	};
	Tool.call(this, opt_params);
	/**点击是否切换按钮激活状态，可以获取该属性值，设置只能在初始化时设置*/
	this.clickToggleActive = opt_options.clickToggleActive == null ? true : 　opt_options.clickToggleActive;
	
	//创建div容器
	this._defaultClassName = "wfsboxshadow";
	this._currentActiveClassName = this._defaultClassName + " wfsactivetool";//工具激活状态样式
	var element = this.element = document.createElement("div");
	var className = element.className = this._defaultClassName;

	//iconElement
	var iconElement = this.iconElement = document.createElement("div");
	if(opt_options.className){
		iconElement.className = opt_options.className;
	}
	iconElement.title = opt_options.title;
	iconElement.style.cursor = this._enabled ? "pointer":"default";
	element.appendChild(iconElement);
	
	//添加遮罩层
	var wfsenableClassName = "wfstoolenable";//工具可用样式
	var wfsdisableClassName = "wfstooldisable";//工具不可用样式
	var taskElement = this._elem_task = document.createElement("div");
	taskElement.className = this._enabled ? wfsenableClassName: wfsdisableClassName;
	element.appendChild(taskElement);
	
	var _this = this;
	//添加监听事件
	this.addEventListener("enabledChanged", function(enabled){
		taskElement.className = enabled ? wfsenableClassName : wfsdisableClassName;
		iconElement.style.cursor = enabled ? "pointer":"default";
		if(enabled){
			element.className = _this._defaultClassName;
		}
	});
	//点击切换激活状态
	if(this.clickToggleActive){
		//绑定点击事件
		this.addEvent({
			events: "click",
			handler: function(evt){
				_this.setActive(!_this._active);
			}
		});	
	}
}
/**继承Tool*/
WFSLayerButtonTool.prototype = Object.create(Tool.prototype);
WFSLayerButtonTool.prototype.constructor = WFSLayerButtonTool;

/**
 * 设置/获取title，获取调用方法title()
 * 重写title方法
 * @param title {String|undefined}
 */
WFSLayerButtonTool.prototype.title = function(title){
	if(title === undefined) {
		//获取title
		return this.iconElement.title;
	}else{
		//设置title
		this.iconElement.title = title || "";
	}
}

/**
 * 重写setActive方法
 */
WFSLayerButtonTool.prototype.setActive = function(active){
	Tool.prototype.setActive.call(this, active);
	this.setActiveState(active);
}

/**
 * 设置激活/注销激活按钮状态，并不触发activeChanged
 */
WFSLayerButtonTool.prototype.setActiveState = function(active){
	this.element.className = active ?　 this._currentActiveClassName: this._defaultClassName;
	this._active = active;
}

/*-------------------------------------------------------*/
/*       WFS图层编辑工具条图层下拉列表工具 {WFSLayerListTool}       */
/*-------------------------------------------------------*/
/**
 * WFS图层编辑工具条下拉选择列表工具
 * @param options
 * 		className - {String} 工具element 样式className
 * 		comboxClassName - {String} 下拉列表样式className
 * 		items - [{id:"1",name:"贮罐区图层","visible":true,data:任意值},{id:"2",name:"贮罐图层","visible":true,data:任意值}]
 * 		key - {id:"id",name:"name"}
 * 		onItemChanged - {function(index, item)} 列表项改变事件
 * 		onItemComboxEnabledChanged - {function(enabled)} 下拉列表可用性改变事件
 * 		onItemVisibilityChanged - {function(index, item, visible)} 可见状态改变事件
 * @remark
 * 	items为下拉列表
 * 	key为当自定义键时使用，默认items key为id,name
 */
WFSLayerListTool = function(options){
	Tool.call(this);
	/**div容器*/
	this.element = null;
	/**图层列表element*/
	this._elem_items = null;
	/**图层列表遮罩层element*/
	this._elem_itemTask = null;
	/**可见控制element*/
	this._elem_layereye = null;
	this._elem_eyeVisibleState = null;
	
	var opt_options = options || {};
	/**key设置*/
	this.key = {"id":"id","name":"name"};
	if(opt_options.key){
		key["id"] = opt_options.key["id"] || "id";
		key["name"] = opt_options.key["name"] || "name";
	}
	/**图层数组*/
	this._items = opt_options.items || [];
	/**下拉列表可用性*/
	this._comboxEnabled = true;
	/**列表项改变事件*/
	this.onItemChanged = opt_options.onItemChanged;
	this.onItemComboxEnabledChanged = opt_options.onItemEnabledChanged;
	this.onItemVisibilityChanged = opt_options.onItemVisibilityChanged;
	
	this._initializeDOM(opt_options);
	this._bindingEvent();
}
/**继承Tool*/
WFSLayerListTool.prototype = Object.create(Tool.prototype);
WFSLayerListTool.prototype.constructor = WFSLayerListTool;

/**
 * 初始化DOM
 *  结构：
 *  <div> //容器
		<span>图层：</span>
		<div> 容器用于添加遮罩层
		    <select> 图层列表
				<option value="2">图层2</option>
		    	<option value="3">图层3</option>
			</select>
		</div>
		<div> 容器用于添加遮罩层
			<div></div> 图层可见控制
		</div>
    </div>
 */
WFSLayerListTool.prototype._initializeDOM = function(opt_options){
	//创建div容器
	var element = this.element = document.createElement("div");
	element.className = opt_options.className;
	
	//图层
	var span = document.createElement("span");
	$(span).css({"float": "left","line-height": "22px"});
	span.innerHTML = "图层:";
	element.appendChild(span);
	
	var selectContainer = document.createElement("div");
	$(selectContainer).css({"float": "left","margin-left": "5px","width":"100px","height":"22px"});
	element.appendChild(selectContainer);
	//图层列表select
	var select = this._elem_items = document.createElement("select");
	select.className = opt_options.comboxClassName;
	select.style.width = "100px";
	selectContainer.appendChild(select);
	//图层列表遮罩层
	var selectTask = this._elem_itemTask = document.createElement("div");
	selectContainer.appendChild(selectTask);
	
	var eyeContainer = document.createElement("div");
	$(eyeContainer).css({"float":"right"});
	element.appendChild(eyeContainer);
	//可见控制
	var eye = this._elem_layereye =document.createElement("div");
	eye.className = "wfslayereye";
	eye.title = "可见";
	eyeContainer.appendChild(eye);
	var eyeshown = this._elem_eyeVisibleState = document.createElement("div");
	eyeshown.className = "wfslayereyeshown";
	eye.appendChild(eyeshown);
	
	this.redraw();
}

/**
 * 绑定事件
 */
WFSLayerListTool.prototype._bindingEvent = function(){
	var _this = this;
	//绑定选择事件
	this.addEvent({
		selector:"select",
		events:"change",
		handler:function(evt){
			var index = _this._elem_items.selectedIndex;
			if(_this.onItemChanged){
				var item = _this._items[index];
				_this.onItemChanged(index, item);
			}
		}
	});
	//绑定可见事件
	this.addEvent({
		selector:".wfslayereye",
		events:"click",
		handler:function(evt){
			//设置item可见状态
			var index = _this._elem_items.selectedIndex;
			var item = _this._items[index];
			if(item){
				var visible = !item["visible"];
				_this.setEyeVisibleState(visible);
				_this.setItemVisible(index, visible);
			}
		}
	});
}

/**
 * 获取或设置选择的index
 * 获取使用selectIndex();
 * @param index {Number|undefined} 索引
 */
WFSLayerListTool.prototype.selectIndex = function(index){
	//返回索引
	if(index === undefined) {
		return this._elem_items.selectedIndex;
	}
	//设置索引
	if(typeof index == 'number' && index >= 0 && index < this._items.length){
		this._elem_items.selectedIndex = index;
		var item = this._items[index];
		//设置可见控制按钮状态
		this.setEyeVisibleState(item["visible"]);
		if(this.onItemChanged){
			this.onItemChanged(index, item);
		}
	}
}

/**
 * 设置选择项
 * @param id {String} 选项id
 */
WFSLayerListTool.prototype.selectItemById = function(id){
	if(id == null) return;
	
	var index, item;
	for(var i = 0 ; i < this._items.length; i++){
		item = this._items[i];
		if(item == null) continue;
		
		if(item[this.key["id"]] == id){
			index = i;
			break;
		}
	}
	if(index != null){
		this.selectIndex(index);
	}
}

/**
 * 添加item ,item结构要和options结构一致
 */
WFSLayerListTool.prototype.addItem = function(item){
	if(item != null){
		//设置item可见性
		item["visible"] = item["visible"] == null ? true : item["visible"];
		this._items.push(item);
	}
}

/**
 * 移除item
 * @params id {String}
 */
WFSLayerListTool.prototype.removeItemById = function(id){
	if(id == null) return;
	var item = null;
	for(var i = this._items.length -1 ; i > 0; i--){
		item = this._items[i];
		if(item[this.key["id"]] === id){
			this._items.splice(i, 1);
		}
	}
}

/**
 * 重绘dom
 */
WFSLayerListTool.prototype.redraw = function(){
	var item, id, name;
	for(var i = 0; i < this._items.length; i++){
		item = this._items[i] || {};
		id = item[this.key["id"]] || "";
		name = item[this.key["name"]] || "未命名";
		//设置item可见性
		item["visible"] = item["visible"] == null ? true : item["visible"];
		this._elem_items.options.add(new Option(name,id));
	}
}

/**
 * 设置下拉列表Combox可用性
 * @param {Boolean}
 */
WFSLayerListTool.prototype.setItemComboxEnabled = function(enabled){
	if(enabled === this._comboxEnabled) return;
	this._comboxEnabled = enabled;
	this._elem_itemTask.className = enabled ?　"wfstoolenable" : "wfstooldisable";
	if(this.onItemComboxEnabledChanged){
		this.onItemComboxEnabledChanged(enabled);
	}
}

/**
 * 设置item可见状态
 * @param {Boolean}
 */
WFSLayerListTool.prototype.setItemVisible = function(index, visible){
	var item = this._items[index];
	if(item == null || item["visible"] === visible) return;
	item["visible"] = visible;
	if(this.onItemVisibilityChanged){
		this.onItemVisibilityChanged(index, item, visible);
	}
}

/**
 * 设置可见按钮状态，不触发onItemVisibilityChanged事件
 */
WFSLayerListTool.prototype.setEyeVisibleState = function(visible){
	if(visible){
		this._elem_layereye.title = "可见";
		this._elem_eyeVisibleState.style.display = "";
	}else{
		this._elem_layereye.title = "不可见";
		this._elem_eyeVisibleState.style.display = "none";
	}
}

/*-------------------------------------------------------*/
/*       WFS图层编辑工具条自定义下拉框 {WFSLayerComboboxTool}     */
/*-------------------------------------------------------*/
/**
 * WFS图层编辑工具条自定义下拉框
 * @param options
 * 			items - [{className:"wfsselectfeatureEnvelope",title:"矩形选择",data:null},...]
 * 			onItemChanged - {function(index, item)} 列表项改变事件
 */
WFSLayerComboboxTool = function(options){
	Tool.call(this);
	var opt_options = options || {};
	/**下拉选项*/
	this._items = opt_options.items || [];
	/**当前item div*/
	this._elem_current_item = null;
	/**下拉列表项 ul*/
	this._elem_ul = null;
	/**获取当前选择index，设置请使用selectIndex方法*/
	this.selectedIndex = 0;
	/**列表项改变事件*/
	this.onItemChanged = opt_options.onItemChanged;
	
	var element = this.element = document.createElement("div");
	element.className = "dropdown";
	
	this._initializeCurrentItemDOM(this._items[0]);
	this._initializeDropDownListDOM(this._items);
	this._bindingEvent();
}
/**继承Tool*/
WFSLayerComboboxTool.prototype = Object.create(Tool.prototype);
WFSLayerComboboxTool.prototype.constructor = WFSLayerComboboxTool;

/***
 * 初始化当前选项
 */
WFSLayerComboboxTool.prototype._initializeCurrentItemDOM = function(item){
	//<div class="card-drop"><a class="toggle"><span><div class="wfsselectfeatureEnvelope"></div></span></a></div>
	var div = document.createElement("div");
	div.className = "card-drop";
	this.element.appendChild(div);
	
	var a = this._activeElement = document.createElement("a");
	a.className = "toggle";
	div.appendChild(a);
	
	var span = document.createElement("span");
	a.appendChild(span);
	
	var currentElement = this._elem_current_item = document.createElement("div");
	span.appendChild(currentElement);
	if(item != null){
		currentElement.className = item.className;
		currentElement.title = item.title;
	}
}

/**
 * 初始化下拉列表
 */
WFSLayerComboboxTool.prototype._initializeDropDownListDOM = function(items){
	//<ul><li type="rectangle"><a data-native-menu="1"><div class="wfsselectfeatureEnvelope"></div></a></li><li type="polygon"><a data-native-menu="2"><div class="wfsselectfeaturePolygon"></div></a></li><li type="circle"><a data-native-menu="3"><div class="wfsselectfeatureCircle"></div></a></li></ul>
	var ulElement = this._elem_ul = document.createElement("ul");
	this.element.appendChild(ulElement);
	var html = "", item;
	for(var i = 0; i < items.length; i++){
		item = items[i];
		//<li><div typeindex="0" class="wfsselectfeatureEnvelope" title="多边形选择"></div></li>
		html += '<li><div typeindex="'+ i +'" class="'+ item.className +'" title="' + item.title + '"></div></li>';
	}
	ulElement.innerHTML = html;
}

/**
 * 绑定事件
 */
WFSLayerComboboxTool.prototype._bindingEvent = function(){
	var _this = this;
	var $currentItemElement = $(this._elem_current_item);
	var $ulElement = $(this._elem_ul);
	//绑定当前项点击事件
	this.addEvent({
		events: "mouseover",
		handler: function(){
			$ulElement.show();
		}
	});
	this.addEvent({
		events: "mouseout",
		handler: function(){
			$ulElement.hide();
		}
	});
	this.addEvent({
		events: "click",
		selector: "a",
		handler: function(){
			_this.setActive(!_this._active);
		}
	});
	//绑定选项点击事件
	var $ulElement = $(this._elem_ul);
	$ulElement.on("click", "li", function(){
		var strIndex = $(this).find("div").attr("typeindex");
		var index = parseInt(strIndex);
		_this.selectIndex(index);
		$ulElement.toggle();
	});
}

/**
 * 设置当前选项索引
 */
WFSLayerComboboxTool.prototype.selectIndex = function(index){
	var item = this._items[index];
	
	var currentElement = this._elem_current_item;
	currentElement.className = item.className;
	currentElement.title = item.title;
	
	if(this.onItemChanged && this.selectedIndex !== index){
		this.selectedIndex = index;
		this.onItemChanged(index, item);
	}
}

/**
 * 获取当前选项
 */
WFSLayerComboboxTool.prototype.getCurrentItem = function(){
	return this._items[this.selectedIndex];
}

/**
 * 重写setActive方法
 */
WFSLayerComboboxTool.prototype.setActive = function(active){
	Tool.prototype.setActive.call(this, active);
	//设置激活状态
	this.setActiveState(active);
}

/**
 * 设置激活/注销激活按钮状态，并不触发activeChanged
 */
WFSLayerComboboxTool.prototype.setActiveState = function(active){
	var bgcolor = active ?　"#fafafa" : "#eee";
	$(this._activeElement).css({"background-color": bgcolor});
	this._active = active;
}