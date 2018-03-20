/**
 * @require ol.js
 */

/**
 * @namespace bxmap.control
 */
bxmap.control = bxmap.control || {};

/*----------默认交互工具类{bxmap.control.Defaults}---------*/
/**
 * @constructor
 * @classdesc 默认控件类，从默认配置bxmap.config.ToolConfig中读取控件信息，设置控件id和visible
 * @extends {ol.Object}
 * @example <caption> 创建默认控件 </caption>
 * var controlCreator = new bxmap.control.Defaults();
 * //地图滑动卷帘控件
 * controlCreator.createSwipeControl(bmap);
 * //创建地图导航控件
 * controlCreator.createNavigation(bmap);
 * //显示鹰眼
 * controlCreator.createOverviewMap(bmap);
 * //创建底部背景条
 * controlCreator.createBottomBackgroudControl(bmap);
 * //显示地图比例尺
 * controlCreator.createScaleLine(bmap);
 * //显示当前坐标
 * controlCreator.createMousePosition(bmap);
 * //显示Toolbox工具箱
 * controlCreator.createToolboxControl(bmap);
 */
bxmap.control.Defaults = function (){
	this.reader = new bxmap.reader.ToolConfigReader(bxmap.config.ToolConfig);
	ol.Object.call(this);
}
ol.inherits(bxmap.control.Defaults, ol.Object);

/**
 * @description 根据默认配置更新可见状态，visible默认为true
 * @param {bxmap.control.Control} ctl 当控件类型为{ol.control.Control}时，必须扩展setVisible方法
 */
bxmap.control.Defaults.prototype.updateVisibility = function(ctl){
	var controlID = ctl.get(bxmap.INDEX_CONTROL_ID);
	var values = this.reader.keyValuePair;
	
	var visible = true;
	if(controlID != null){
		var data = values[controlID];
		visible = data["visible"] == null ? true : data["visible"];
	}
	ctl.setVisible(visible);
}

/**
 * @description 创建DOM控件，并将控件嵌入到地图中
 * @param bmap {bxmap.Map} 地图对象
 * @param element {String|Element|undefined} 嵌入到Map中的DOM元素
 * @returns {bxmap.control.Control}
 * @example <caption> 创建嵌入到地图的DOM控件 </caption>
 * //创建嵌入到地图的3d切换控件element
 * var ctl = bxmap.control.Defaults.createElementControl(bmap);
 * var element = ctl.element;
 * element.className = "map_3D_switch";
 * //将数据添加至目标element
 * var tool = new BaseLayerSwitcherToolBar({
 *		target: element,
 *		data: data
 * });
 *
 * tool.onItemClick = function(itemData,index,element){
 *		if(onclick){
 *			onclick(itemData,index,element);
 *		}
 *	};
 */
bxmap.control.Defaults.createElementControl = function(bmap, element){
	var elem = element ? element : document.createElement("div");
	if(typeof elem === 'string'){
		elem = document.getElementById(elem)
	}
	var ctl = new bxmap.control.Control({element: elem});
	bmap.addControl(ctl);
	return ctl;
}

/**
 * @description 创建鹰眼控件
 * @param {bxmap.Map} bmap 地图对象
 * @returns {ol.control.OverviewMap}
 */
bxmap.control.Defaults.prototype.createOverviewMap = function(bmap){
	//var layerconfig = bmap.getLayerContainer();
	var v_layers = bmap.getBaseLayerGroup().getLayers();
	var mapView = bmap.getMap().getView();
	var view = new ol.View({
		"projection" : mapView.getProjection(), 
		"maxZoom" : mapView.get("max_zoom"),
		"minZoom" : mapView.get("min_zoom") - 1
	});
	var ctl = new ol.control.OverviewMap({
	 	"className": "ol-overviewmap ol-custom-overviewmap",
		"layers" : v_layers,
		"tipLabel": "鹰眼视图",
		"collapseLabel": "\u00BB",
		"label": "\u00AB",
		"collapsed": true,
		"view": view
	});
	bmap.addControl(ctl);
	//设置控件id
	ctl.set(bxmap.INDEX_CONTROL_ID, 4);
	this.updateVisibility(ctl);
	
	return ctl;
}

/**
 * @description 创建比例尺控件
 * @param {bxmap.Map} bmap 地图对象
 * @returns {ol.control.ScaleLine}
 */
bxmap.control.Defaults.prototype.createScaleLine = function(bmap){
	var ctl = new ol.control.ScaleLine({
		"className":"ol-custom-scaleline"
	});
	bmap.addControl(ctl);
	//设置控件id
	ctl.set(bxmap.INDEX_CONTROL_ID, 2);
	this.updateVisibility(ctl);
	
	return ctl;
}

/**
 * @description 创建鼠标位置控件
 * @param {bxmap.Map} bmap 地图对象
 * @returns {bxmap.control.MousePosition}
 */
bxmap.control.Defaults.prototype.createMousePosition = function(bmap){
    var ctl = new bxmap.control.MousePosition();
    bmap.addControl(ctl);
    //设置控件id
	ctl.set(bxmap.INDEX_CONTROL_ID, 3);
	this.updateVisibility(ctl);
	
	return ctl;
}

/**
 * @description 创建图层控制器按钮控件，悬浮在地图上
 * @param {bxmap.Map} bmap 地图对象
 * @returns {bxmap.control.LayerSwitcher}
 */
bxmap.control.Defaults.prototype.createLayerSwitcher = function(bmap){
	var ctl = new bxmap.control.LayerSwitcher();
	bmap.addControl(ctl);
	//设置控件id
	ctl.set(bxmap.INDEX_CONTROL_ID, 5);
	this.updateVisibility(ctl);
	
	return ctl;
}

/**
 * @description 创建全屏按钮控件
 * @param {bxmap.Map} bmap 地图对象
 * @param {String} target DOM指定承载全屏按钮的Element id
 * @returns {ol.control.FullScreen}
 */
bxmap.control.Defaults.prototype.createFullScreen = function(bmap, target){
	var ctl = new ol.control.FullScreen({
		tipLabel: "全屏",
		source: target
	});
	bmap.addControl(ctl);
	//设置控件id
	ctl.set(bxmap.INDEX_CONTROL_ID, 8);
	this.updateVisibility(ctl);
	
	return ctl;
}

/**
 * @description 创建导航器控件
 * @param {bxmap.Map} bmap 地图对象
 * @param {String} target DOM指定承载全屏按钮的Element id
 * @returns {bxmap.control.Navigation}
 */
bxmap.control.Defaults.prototype.createNavigation = function(bmap, target){
	var ctl = new bxmap.control.Navigation({
		map: bmap,
		target: target
	});
	//设置控件id
	ctl.set(bxmap.INDEX_CONTROL_ID, 1);
	this.updateVisibility(ctl);
	
	return ctl;
}

/**
 * @description 创建基础底图图层切换按钮控件，停靠在地图上
 * @param {bxmap.Map} bmap 地图对象
 * @param data {Array<Object>} 数据格式见example
 * @returns {bxmap.control.BaseLayerControl}
 * @example <caption> 创建地图底图切换控件 </caption>
 * //数组中data对应图层配置Config.js中maps.layer_groups数据项中的group_id值
 * var mapLabelArray = [
 * 	  {"id":1,"label":"影像","className":"imgType","data":"GDImage_map"},
 * 	  {"id":2,"label":"海图","className":"vecType","data":"SeaMap_map"}
 * 	];
 * controlCreator.createBaseLayerControl(bxMap, mapLabelArray);
 */
bxmap.control.Defaults.prototype.createBaseLayerControl = function(bmap , data){
	var ctl = new bxmap.control.BaseLayerControl({
	 	data: data
	});
	bmap.addControl(ctl);
	//设置控件id
	ctl.set(bxmap.INDEX_CONTROL_ID, 6);
	this.updateVisibility(ctl);
	
	return ctl;
}

/**
 * @description 创建基础三维切换按钮控件，悬浮在地图上
 * @param options
 * @param {bxmap.Map} options.bmap
 * @param {Array<Object>} options.data 示例[{"id":1,"label":"三维","className":"glbType","data":null}]
 * @param {Function(itemData,index,element)} options.onclick
 * @returns {bxmap.control.Control}
 * @example <caption> 创建三维切换按钮控件 </caption>
 * var data=[{"id":1,"label":"三维","className":"glbType","data":null}];
 * controlCreator.createD3DSwitcherControl({
 * 		bmap: bmap,
 * 		data: data,
 *  	onclick: function(itemData,index,element){
 *  		//执行三维切换...
 *		}
 *	});
 */
bxmap.control.Defaults.prototype.createD3DSwitcherControl = function(options){
	var bmap = options.bmap;
	var data = options.data;
	var onclick = options.onclick;
	
	//创建嵌入到地图的3d切换控件element
	var ctl = bxmap.control.Defaults.createElementControl(bmap);
	var element = ctl.element;
	element.className = "map_3D_switch";
	//将数据添加至目标element
	var tool = new BaseLayerSwitcherToolBar({
		target: element,
		data: data
	});
	
	tool.onItemClick = function(itemData,index,element){
		if(onclick){
			onclick(itemData,index,element);
		}
	};
	
	//设置控件id
	ctl.set(bxmap.INDEX_CONTROL_ID, 7);
	this.updateVisibility(ctl);
	
	return ctl;
}

/**
 * @description 创建标绘控件，废弃的控件
 * @param options
 * 		bmap {bxmap.Map}
 * 		toolbar_config {Object} 参考bxmap.config.ToolConfig
 * @returns {bxmap.control.Control}
 */
bxmap.control.Defaults.prototype.createPlotControl = function(options){
	var bmap = options.bmap;
	var toolbarConfig = options.toolbar_config;
	var imagePath = bxmap.Resource.BaseURL + "js/map/olmap/res/images/bmap/plot/";
	var data = [
	    {
			id:"1",
			image:imagePath + "icon_d.png",
			//data:"",onclick:function(element,data){},
			items:[
			 	{
			 		 title:"绘制点标",
			         items:[
			             {id:"101",title:"绘制点",data: "marker",onclick:function(element,data){alert(data);}}
			         ]
			    }
			]
		},
		{
			id:"2",
			image:imagePath + "icon_mb.png",
			//data:"",onclick:function(element,data){},
			items:[
			 	{
			 		 title:"绘制线标",
			         items:[
			             {id:"201",title:"绘制圆",data: "circle",onclick:function(element,data){
			            	 console.log("执行绘制圆操作");
			             }},
			             {id:"202",title:"绘制椭圆",data: "ele",onclick:function(element,data){
			            	 console.log("执行绘制椭圆操作");
			             }}
			         ]
			    }
			]
		}
	];
	

	//创建嵌入到地图的控件element
	var ctl = bxmap.control.Defaults.createElementControl(bmap);
	var element = ctl.element;
	element.className = "plot";
	//将数据添加至目标element
	var tool = new PlotToolbar({
		target: element
	});
	
	tool.setData(data);
	
	return ctl;
}

/**
 * @description 创建工具箱toolbox控件
 * @param {bxmap.Map} bmap 地图对象
 * @returns {bxmap.control.Control}
 */
bxmap.control.Defaults.prototype.createToolboxControl = function(bmap){
	//var bmap = options.bmap;
	//var mapTool = options.mapTool;
	
	//创建工具箱Element
	var toolboxCtrl = bxmap.control.Defaults.createElementControl(bmap);
	//设置控件id
	toolboxCtrl.set(bxmap.INDEX_CONTROL_ID, 999);
	this.updateVisibility(toolboxCtrl);
	
//	var element = toolboxCtrl.element;
//	element.className = "tool_next";
//	var toolbox = new Toolbox({
//		target: element
//	});
//	
//	//上次弹出的工具窗
//	var lastPopupWindow = null;
//	//底图切换/3D切换ToolGroup
//	var switcherToolGroup = new ToolGroup({ className:"toolgroup switchertool",tip:"切换工具" });
//	switcherToolGroup.addToolBar(mapTool.switherToolbar);
//	switcherToolGroup.redraw();
//	toolbox.addToolGroup(switcherToolGroup);
//	//常用工具ToolGroup
//	var commonToolGroup = new ToolGroup({ className:"toolgroup common",tip:"常用工具" });
//	commonToolGroup.addToolBar(mapTool.commonToolbar);
//	var layerSwitcherWindow = mapTool.layerSwitcherWindow;//图层管理器窗体
//	toolboxCtrl.element.appendChild(layerSwitcherWindow.element);
//	var layerEditToolbar = mapTool.layerEditToolbar;//WFS图层编辑器工具条
//	toolboxCtrl.element.appendChild(layerEditToolbar.element);
//	
//	commonToolGroup.redraw();
//	toolbox.addToolGroup(commonToolGroup);
//	//标绘工具ToolGroup
//	var plotDrawToolGroup = new ToolGroup({ className:"toolgroup plotdraw",tip:"标绘" });
//	var plotDrawWindow = mapTool.plotDrawWindow;//标绘工具窗
//	toolboxCtrl.element.appendChild(plotDrawWindow.element);
//	plotDrawToolGroup.onClick = function(evt){
//		if(lastPopupWindow){
//			lastPopupWindow.hide();
//		}
//		plotDrawWindow.show();
//		lastPopupWindow = plotDrawWindow;
//	}
//	toolbox.addToolGroup(plotDrawToolGroup);
//	//文本标注ToolGroup
//	var labelDrawToolGroup = new ToolGroup({ className:"toolgroup labeldraw",tip:"文本标注" });
//	toolbox.addToolGroup(labelDrawToolGroup);
//	labelDrawToolGroup.onClick = function(){
//		bmap.setCurrentMutexInteraction(mapTool.plotTextTool);
//	}
//	//图标标注ToolGroup
//	var plotImageToolGroup = new ToolGroup({ className:"toolgroup imageplot",tip:"图片标注" });
//	var plotImageWindow = mapTool.plotImageWindow;//图片标注工具窗
//	toolboxCtrl.element.appendChild(plotImageWindow.element);
//	plotImageToolGroup.onClick = function(evt){
//		if(lastPopupWindow){
//			lastPopupWindow.hide();
//		}
//		plotImageWindow.show();
//		lastPopupWindow = plotImageWindow;
//	}
//	toolbox.addToolGroup(plotImageToolGroup);
//	
//	//toolbox toolGroupsVisibilityListeners事件
//	toolbox.addToolGroupsVisibilityListener(function(visibility){
//		if(visibility && lastPopupWindow){
//			lastPopupWindow.hide();//隐藏上个窗体
//		}
//		if(visibility){
//			layerSwitcherWindow.hide();//隐藏图层管理器
//			bmap.setCurrentMutexInteraction(null);//设置当前工具为null
//		}
//		
//	});
//	
//	//绘制toolbox
//	toolbox.redraw();
}

/**
 * @description 创建地图底部黑色透明背景控件，主要用于修饰比例尺控件和鼠标位置控件
 * @param {bxmap.Map} bmap 地图对象
 * @returns {bxmap.control.Control}
 */
bxmap.control.Defaults.prototype.createBottomBackgroudControl = function(bmap){
	//创建工具箱Element
	var footCtrl = bxmap.control.Defaults.createElementControl(bmap);
	var element = footCtrl.element;
	element.className = "mapfootBottom";
	element.onmousemove = function(evt){
		evt = evt || window.event;
		evt.stopPropagation();
		evt.preventDefault();
	}
	return footCtrl;
}

/**
 * @description 创建滑块卷帘控件
 * @param {bxmap.Map} bmap 地图对象
 * @returns {bxmap.control.Control}
 */
bxmap.control.Defaults.prototype.createSwipeControl = function(bmap){
	//初始化时 layers 应该为空
	var layers = []; //bmap.getBaseLayers();
	var ctl = new bxmap.control.Swipe({
		layers: layers,
		position: 0.5,
		orientation: "vertical"
	});
	bmap.addControl(ctl);
	//设置控件id
	ctl.set(bxmap.INDEX_CONTROL_ID, 9);
	this.updateVisibility(ctl);
	
	//注册属性监听事件
	ctl.on("change",function(){
		if(this.getVisible()){
            layers = bmap.getBaseLayers();
			this.addLayer(layers);
		}else{
			this.removeAllLayers();
		}
	},ctl);
	//注册地图底图切换事件
	var updateBaseLayer = bmap.getBaseLayerGroup();
    updateBaseLayer.on("change:layers", function(){
    	this.removeAllLayers();
		//reloadLayers();
		if(this.getVisible()){
            layers = bmap.getBaseLayers();
			this.addLayer(layers);
		}
    }, ctl);
	
	return ctl;
}

/*--------------------------------------------------------------*/
/*                 基础控件类{bxmap.control.Control}            */
/*--------------------------------------------------------------*/

/**
 * @classdesc 控件基础类，自定义控件需要继承该类
 * @param options
 * @constructor
 * @extends {ol.control.Control}
 */
bxmap.control.Control = function(options){
	ol.control.Control.call(this, options);
}
ol.inherits(bxmap.control.Control, ol.control.Control);

/**
 * @description 设置控件是否可见
 * @param {Boolean} visible true可见，false，不可见
 */
bxmap.control.Control.prototype.setVisible = function(visible){
	var display = visible ? "block" : "none";
	this.element.style.display = display;
}

/**
 * @description 获取控件是否可见
 * @returns {Boolean} true可见，false，不可见
 */
bxmap.control.Control.prototype.getVisible = function(){
	var display = this.element.style.display == "none";
	return !display;
}

/*----------导航工具条{bxmap.control.Navigation}---------*/
/**
 * @classdesc Element控件。导航工具条控件，整合NavigationToolbar.js
 * @constructor
 * @param options
 * @param options.map {bxmap.Map}
 * @param options.target {String} Element id
 * @extends {ol.Object}
 */
bxmap.control.Navigation = function(options){
	var bmap = options.map,
		target = options.target,
		map = bmap.getMap(),
		view = map.getView();

	this.element = null;
	if(target == null){
		var elem = this.element = document.createElement("DIV");
		target = elem.id =  "navigationToolbar";
		bmap.getMap().getViewport().appendChild(elem);
	}else{
		this.element = document.getElementById(target);
	}
	
	var config = {
	    targetId: target,
	    minValue: view.get("min_zoom"),     //0
	    maxValue: view.get("max_zoom"),    //19
	    startValue: view.getZoom(),  //0
	    toolbarCss: ["toolBar", "toolBar_button", "toolBar_slider", "toolBar_mark"],
	    marksShow: {
	        countryLevel: null,
	        provinceLevel: null,
	        cityLevel: null,
	        streetLevel: null
	    }
	};
	var toolBar = new MapNavigationToolbar(config);
	/* 地图上移 */
    toolBar.onMoveUp = function () { bmap.pan("up"); };
    /* 地图下移 */
    toolBar.onMoveDown = function () { bmap.pan("down");};
    /* 地图左移 */
    toolBar.onMoveLeft = function () { bmap.pan("left"); };
    /* 地图右移 */
    toolBar.onMoveRight = function () { bmap.pan("right"); };
    /* 地图全图 */
    toolBar.onFullMap = function () { bmap.zoomFullExtent();};
    /* 地图放大 */
    toolBar.onZoomIn = function () { view.setZoom(toolBar.getValue()); };
    /* 地图缩小 */
    toolBar.onZoomOut = function () { view.setZoom(toolBar.getValue()); };
    /* 滑动条滑动结束 */
    toolBar.onSliderEnd = function () { view.setZoom(toolBar.getValue()); };
    /* 地图级别标记-街道 */
    toolBar.onMark_Street = function () { view.setZoom(config.marksShow.streetLevel); };
    /* 地图级别标记-城市 */
    toolBar.onMark_City = function () { view.setZoom(config.marksShow.cityLevel); };
    /* 地图级别标记-省级 */
    toolBar.onMark_Province = function () { view.setZoom(config.marksShow.provinceLevel); };
    /* 地图级别标记-国家 */
    toolBar.onMark_Country = function () { view.setZoom(config.marksShow.countryLevel); };
	toolBar.create();
	toolBar.setValue(config.startValue);
	view.on("change:resolution",function(){
		var level = view.getZoom();
		toolBar.setValue(level);
	});
	ol.Object.call(this);
}
ol.inherits(bxmap.control.Navigation, ol.Object);

/**
 * @description 设置控件是否可见
 * @param visible {Boolean} true可见，false，不可见
 */
bxmap.control.Navigation.prototype.setVisible = function(visible){
	var display = visible ? "block" : "none";
	this.element.style.display = display;
}

/*----------基础底图切换控件{bxmap.control.BaseLayerControl}---------*/
/**
 * @classdesc 基础底图切换按钮控件，整合BaseLayerSwitcherToolBar.js
 * @constructor
 * @extends {bxmap.control.Control}
 * @param options
 * @param options.data {Array<Object>}
 * 示例[{"id":1,"label":"地图","className":"vecType","data":"v_map"}]，
 * 其中数组中data对应图层配置Config.js中maps.layer_groups数据项中的group_id值
 */
bxmap.control.BaseLayerControl = function(options){
	var opt_options = options ? options : {};
	var data = opt_options.data ? opt_options.data : [];
	this.map = null;
	
	//初始化control
	var tool = new BaseLayerSwitcherToolBar({
		data: data
	});
	//绑定点击事件
	var _this = this;
	tool.onItemClick = function(itemData,index,element){
		var bmap = _this.map;
		if(bmap == null) return;
		var layerconfig = bmap.getLayerContainer();
		layerconfig.setCurrentBaseLayerGroup(itemData.data);
		bmap.updateBaseLayerGroup();
	}
	
	var element = tool.target;
	bxmap.control.Control.call(this, {
        "element": element
    });
}
ol.inherits(bxmap.control.BaseLayerControl, bxmap.control.Control);

/**
 * @inheritdoc
 * @description 设置地图，addcontrol时会执行
 * @param {ol.CanvasMap} map 地图对象
 */
bxmap.control.BaseLayerControl.prototype.setMap = function (map) {
    bxmap.control.Control.prototype.setMap.call(this, map);
    if(map == null){
    	this.map = null;
    }else{
    	//获取bxmap.Map对象
    	this.map = map.get("wrap");
	}
};


/*----------坐标位置显示控件{bxmap.control.MousePosition}---------*/
/**
 * @classdesc 坐标位置显示控件，用于鼠标在地图上移动时，显示坐标位置
 * @extends {bxmap.control.Control}
 * @constructor
 * @param options
 * @param options.class_name {String} 默认为ol-custom-mouseposition
 * @param options.target {Element} DOM element 默认为null
 * @param options.coordinate_format {function(coordinate)} 格式化方法，参数coordinate为[x,y]
 */
bxmap.control.MousePosition = function (options) {

    var opt_options = options ? options : {};
    var element = this.element = opt_options.target ? opt_options.target : document.createElement('DIV');
    element.className = opt_options.class_name !== undefined ? opt_options.class_name : 'ol-custom-mouseposition';
    element.style.display = "none";
    
    //mousemove禁止事件向父级传递
    element.addEventListener("mousemove",function(evt){
		evt = evt || window.event;
		evt.stopPropagation();
		evt.preventDefault();
	});
    
    bxmap.control.Control.call(this, {
        "element": element
    });

    if (opt_options.coordinate_format) {
        this.setCoordinateFormat(opt_options.coordinate_format);
    } else {
        this.setCoordinateFormat(function (coord) {
            var template = 'X:{x} Y:{y}';
            return ol.coordinate.format(coord, template, 3);
        });
    }
}
ol.inherits(bxmap.control.MousePosition, bxmap.control.Control);

/**
 * @inheritdoc
 * @description 设置地图，addcontrol时会执行
 * @param {ol.CanvasMap} map 地图对象
 */
bxmap.control.MousePosition.prototype.setMap = function (map) {
	bxmap.control.Control.prototype.setMap.call(this, map);
    if (map) {
    	map.on("pointermove", this.handleMouseMove, this);
        // var viewport = map.getViewport();
        // ol.events.listen(viewport, "mousemove", this.handleMouseMove, this);
    }
};

/**
 * @private
 * @description 绑定鼠标移动事件
 */
bxmap.control.MousePosition.prototype.handleMouseMove = function (evt) {
    // var map = this.getMap();
    // var pixel = map.getEventPixel(evt);
    // var coordinate = map.getCoordinateFromPixel(pixel);
    var coordinate = evt.coordinate;
    var html = "";
    if (coordinate) {
        var coordinateFormat = this.getCoordinateFormat();
        if (coordinateFormat) {
            html = coordinateFormat(coordinate);
        } else {
            html = coordinate.join(",");
        }
    }
    this.element.innerHTML = html;
}

/**
 * @private
 * @description 设置坐标显示格式化的函数
 * @param {Funciton(coordinate)} format 方法
 */
bxmap.control.MousePosition.prototype.setCoordinateFormat = function (format) {
    this.set("coordinateFormat", format);
};
/**
 * @private
 * @description 获取坐标显示格式化的函数
 * @returns {Funciton(coordinate)}
 */
bxmap.control.MousePosition.prototype.getCoordinateFormat = function () {
    return (this.get("coordinateFormat"));
};


/*----------图层控制器{bxmap.control.LayerSwitcher}---------*/
/**
 * @classdesc 图层控制器控件，依赖ztree.js
 * @constructor
 * @extends {bxmap.control.Control}
 * @param options
 * @param options.shownButton {Boolean} 是否显示button，默认为true，由button控制图层管理器
 * @param options.className {String|undefined} DOM element.className，默认为ol-layer-switcher
 */
bxmap.control.LayerSwitcher = function (options) {
	var opt_options = options || {};
	
    //创建图层控制器dom
    var element = this.panel = document.createElement("div");
    this.hiddenClassName = element.className = opt_options.className || "ol-layer-switcher";
    this.shownClassName = this.hiddenClassName + " shown";
    
    var shownButton = opt_options.shownButton == null ? true : opt_options.shownButton;
    if(shownButton){
    	var button = document.createElement("button");
        button.setAttribute("title", "图层控制");
        element.appendChild(button);
    }
    
    var treeElement = this._treeElement = document.createElement("ul");
    treeElement.id = "ol-layer-switcher-ztree";
    treeElement.className="ztree";
    element.appendChild(treeElement);
    
    if(shownButton){
	    var _this = this;
	    button.onclick = function(e) {
	        e = e || window.event;
	        _this.showPanel();
	        e.preventDefault();
	    };
	    
	    treeElement.onmouseout = function(e) {
	        e = e || window.event;
	        if (!treeElement.contains(e.toElement || e.relatedTarget)) {
	        	_this.hidePanel();
	        }
	    };
    }
    
	this.tree = null;//图层控制树
	this.setting = null;
	this._initTreeSetting();
	
	bxmap.control.Control.call(this, {
        element: element
    });
}
ol.inherits(bxmap.control.LayerSwitcher, bxmap.control.Control);

/**
 * @description 显示控件
 */
bxmap.control.LayerSwitcher.prototype.showPanel = function() {
	this.panel.className = this.shownClassName;
};

/**
 * @description 隐藏控件
 */
bxmap.control.LayerSwitcher.prototype.hidePanel = function() {
	this.panel.className = this.hiddenClassName;
};

/**
 * @description 获取显示状态
 * @returns {Boolean} true-可见；false-不可见
 */
bxmap.control.LayerSwitcher.prototype.getVisible = function() {
	var visible = this.panel.className == this.shownClassName? true:false;
	return visible;
};

/**
 * @inheritdoc
 * @description 设置控件关联的地图
 * @param {ol.CanvasMap} map 地图对象
 */
bxmap.control.LayerSwitcher.prototype.setMap = function (map) {
	if(this.getMap()){
		var bmap = this.getMap().get("wrap");
		 //切换地图时，自动切换图层控制
        var updateBaseLayer = bmap.getBaseLayerGroup();
        updateBaseLayer.un("change:layers", this._handleInitializeMap, this)
	}
    bxmap.control.Control.prototype.setMap.call(this, map);
    if (map) {
        var bmap = map.get("wrap");
        this.initialize(bmap);
        //切换地图时，自动切换图层控制
        var updateBaseLayer = bmap.getBaseLayerGroup();
        updateBaseLayer.on("change:layers", this._handleInitializeMap, this);
    }
};

/**
 * @private
 * @description 初始化地图
 * @param evt
 */
bxmap.control.LayerSwitcher.prototype._handleInitializeMap = function(evt){
	var map = this.getMap();
	var bmap = map.get("wrap");
	this.initialize(bmap);
}

/**
 * @description 初始化图层控制
 * @param bxmap {bxmap.Map}
 */
bxmap.control.LayerSwitcher.prototype.initialize = function(bmap){
	var container = bmap.getLayerContainer();
	var layers = container.getCurrentLayers();
	var nodes = this._getNodesFromLayers(layers);
	var setting = this.setting;
	
	var _this = this;
  	$(document).ready(function() {
  		var tmp = _this.tree;
  		_this.tree = $.fn.zTree.init($(_this._treeElement), setting, nodes);
  	});
}

/**
 * @private
 * @description 初始化树节点设置
 */
bxmap.control.LayerSwitcher.prototype._initTreeSetting = function () {
	//ztree的设置
    var setting = this.setting = {
        check: {
            chkboxType: { "Y": "ps", "N": "ps" },
            enable: true,
            autoCheckTrigger: true
        },
        data: {
            simpleData: {
                enable: true,
                idKey: "id",
                pIdKey: "pid",
                rootPId: bxmap.LAYER_SWITCHER_ROOT//"root"
            },
            key: {
                name: "title",
                title: "tip"
            }
        },
        view: {
            selectedMulti: false
        },
        callback: {}
    };

    var _this = this;
    //勾选事件
    setting.callback.onCheck = function (event, treeid, treenode) {
        var bmap = _this.getMap().get("wrap");
        var container = bmap.getLayerContainer();
        var sellayer = _this.getBindingLayer(treenode.id);
        if (sellayer) {
            sellayer.setVisible(treenode.checked);
        }
    }
}

/**
 * @private
 * @description 根据图层获取树节点
 * @param layers
 * @returns {Array}
 */
bxmap.control.LayerSwitcher.prototype._getNodesFromLayers = function(layers){
	var nodes = [];
	//如果pid为"root",nocheck选项
	var rootId = bxmap.LAYER_SWITCHER_ROOT;
	var category_map = bxmap.LAYER_SWITCHER_CATEGORY_MAP;//图层控制器根节点地图
	var category_group = bxmap.LAYER_SWITCHER_CATEGORY_GROUP;//图层控制器图层组节点
	var category_layer = bxmap.LAYER_SWITCHER_CATEGORY_LAYER;//图层控制器图层节点
	var _this = this;
	layers.forEach(function(elem,index,array){
		var layerid = elem.get("id");
		if(layerid == null) return;
		
		_this.getBindingLayer(layerid);
		//是否显示在图层控制器中
		var displayInSwitcher = elem.get("inswitcher");
		if(!displayInSwitcher) return;

		//ztree node节点设置
		var node = {
			"id":elem.get("id"),
  			"pid":elem.get("pid"),
  			"title":elem.get("title"),
  			"tip":elem.get("tip"),
  			"open":true,
  			"icon":bxmap.Resource.LayerSwitcherLayerPng
		};
		
		var category = elem.get("bxmap.LAYER_SWITCHER_CATEGORY");
		switch(category){
			case category_map://地图节点
				node["nocheck"] = true;
				node["open"] = true;
				node["icon"] = bxmap.Resource.LayerSwitcherMapPng;
				break;
			case category_group://图层组节点
				node["icon"] = bxmap.Resource.LayerSwitcherGroupPng;
				node["checked"] = elem.get("visible");
				break;
			case category_layer://图层节点
				node["icon"] = bxmap.Resource.LayerSwitcherLayerPng;
				node["checked"] = elem.get("visible");
				break;
		}
		nodes.push(node);
  	});
	return nodes;
}

/**
 * @private
 * @description 根据图层id获取基础底图图层
 * @param {String} layerid 图层id
 * @returns {ol.layer.Layer}
 */
bxmap.control.LayerSwitcher.prototype.getBindingLayer = function (layerid) {
    var bmap = this.getMap().get("wrap");
    var container = bmap.getLayerContainer();

    //地图hash
    if (this[container.mapId] == null) {
        this[container.mapId] = {};
    }

    //图层组hash
    var mapHash = this[container.mapId];
    var groupId = container.getCurrentBaseLayerGroup().get(bxmap.CONFIG_GROUP_ID);
    if (mapHash[groupId] == null) {
        mapHash[groupId] = {};
    }

    //图层hash
    var groupHash = mapHash[groupId];
    if (groupHash[layerid] == null) {
        var layer = this.getCurrentLayerById(layerid);
        //控制图层是否显示在控制器中，如果根图层不显示在控制器中，则改图层不显示
        var rootLayer = this.getRootLayer(layer);
        if(rootLayer){
        	var displayInSwitcher = rootLayer.get("inswitcher");
            layer.set("inswitcher", displayInSwitcher);
        }
        
        groupHash[layerid] = layer;
    }
    return groupHash[layerid];
}

/**
 * @description 获取图层所属的根图层，根图层是指root节点下的图层
 * @param layer {ol.layer.Layer} 图层
 * @returns {ol.layer.Layer}
 */
bxmap.control.LayerSwitcher.prototype.getRootLayer = function(layer){
	//pid == root则该图层为root节点
	var pid = layer.get("pid");
	if(pid == bxmap.LAYER_SWITCHER_ROOT){
		return null;
	}
	//若layer的pid -> pid == root，则该图层为根图层
    var pLayer = this.getCurrentLayerById(pid);
    pid = pLayer.get("pid");
    if(pid == bxmap.LAYER_SWITCHER_ROOT){
    	return layer;
    }
    return this.getRootLayer(pLayer);
}

/**
 * @description 从当前基础底图中获取指定图层id的图层
 * @param {String} layerid 图层id
 * @returns {ol.layer.Layer}
 */
bxmap.control.LayerSwitcher.prototype.getCurrentLayerById = function (layerid) {
    var output = null;
    var bmap = this.getMap().get("wrap");
    var container = bmap.getLayerContainer();
    var currentGroup = container.getCurrentBaseLayerGroup();
    var layersArray = currentGroup.getLayers().getArray();
    var layer;
    for (var i = 0, length = layersArray.length; i < length; i++) {
        layer = layersArray[i];
        if (layer.get("id") === layerid) {
            output = layer;
            break;
        }
    }
    return output;
}

/*--------------------------------------------------------*/
/*            滑动卷帘控件{bxmap.control.Swipe}               */
/*--------------------------------------------------------*/
/**
 * @classdesc 滑动卷帘控件
 * @constructor
 * @extends {bxmap.control.Control}
 * @param options
 * @param options.className {String} css样式
 * @param options.layers {ol.layer.Layer|Array<ol.layer.Layer>} 显示在滑块左/右，上/下的图层
 * @param options.position {Number} 滑块位置，默认为0.5
 * @param options.orientation {String} 'vertical'|'horizontal'，默认为'vertical'，垂直分割为左右两部分，目前上下结构还不支持
 */
bxmap.control.Swipe = function(options) {
	var opt_options = options || {};

	//滑块控制按钮
	var button = document.createElement('button');

	var element = document.createElement('div');
    element.className = (opt_options.className || "ol-swipe") + " ol-unselectable ol-control";
    element.appendChild(button);

	$(element).on ("mousedown touchstart", this, this._handleMove );
    
	bxmap.control.Control.call(this, {element: element});
	
	this.layers = [];
	//left layers
	if (opt_options.layers){
		this.addLayer(opt_options.layers);
	} 

	//注册属性改变事件
	this.on('propertychange', function() {
		if (this.getMap()){
			this.getMap().renderSync();
		}
		if (this.get('orientation') === "horizontal"){
			$(this.element).css("top", this.get('position')*100+"%")
			$(this.element).css("left", "");
		}else {
			if (this.get('orientation') !== "vertical") this.set('orientation', "vertical");
			$(this.element).css("left", this.get('position')*100+"%")
			$(this.element).css("top", "");
		}
		$(this.element).removeClass("horizontal vertical")
		$(this.element).addClass(this.get('orientation'))
	}, this);
	
	this.set('position', opt_options.position || 0.5);
	this.set('orientation', opt_options.orientation || 'vertical');
};
ol.inherits(bxmap.control.Swipe, bxmap.control.Control);

/**
 * @inheritdoc
 * @param map {ol.CanvasMap}
 */
bxmap.control.Swipe.prototype.setMap = function(map) {
	//取消绑定事件
	if (this.getMap()){
		for (var layer, i = 0; i < this.layers.length; i++){
			this._unbindCompose(this.layers[i]);
		}
		this.getMap().renderSync();
	}

	ol.control.Control.prototype.setMap.call(this, map);

	//绑定事件
	if (map){
		for (var i = 0; i < this.layers.length; i++){
			this._bindCompose(this.layers[i]);
		}
		map.renderSync();
	}
};

/**
 * @description 添加图层
 * @param layers {ol.layer.Layer|Array<ol.layer.Layer>} 图层或图层数组
 */
bxmap.control.Swipe.prototype.addLayer = function(layers) {
	var tmpLayers = layers;
	if (!(layers instanceof Array)) tmpLayers = [layers];
	for (var layer,i = 0; i < tmpLayers.length; i++){
		layer = tmpLayers[i];
		
		//this.layers中没有layer对象,添加至this.layers
		var swipetype = layer.get("swipetype");
		var isSwipeLayer = swipetype == "left" || swipetype == "right";
		if(!isSwipeLayer) continue;
		
		var found = this.layers.indexOf(layer) > -1;
		if(!found && swipetype){
			this.layers.push(layer);
		}
		
		if(!this.getMap()) continue;
		
		//绑定事件
		this._bindCompose(layer);
	}
	
	if(this.getMap()){
		this.getMap().renderSync();
	}
}

/**
 * @description 移除图层
 * @param layers {ol.layer.Layer|Array<ol.layer.Layer>} 图层或图层数组
 */
bxmap.control.Swipe.prototype.removeLayer = function(layers){
	var tmpLayers = layers;
	if (!(layers instanceof Array)) tmpLayers = [layers];
	for (var layer,i = 0; i < tmpLayers.length; i++){
		layer = tmpLayers[i];
		var k = this.layers.indexOf(layer);
		var found = k > -1;
		if (found && this.getMap()){
			//解绑事件
			this._unbindCompose(layer);
			
			this.layers.splice(k,1);
		}
	}
	if(this.getMap()){
		this.getMap().renderSync();
	}
}

/**
 * @description 移除所有图层
 */
bxmap.control.Swipe.prototype.removeAllLayers = function(){
	var copylayers = this.layers.concat([]);
	this.removeLayer(copylayers);
}

/**
 * @private
 * @description 绑定precompose/postcompose事件
 * @param layer {ol.layer.Layer}
 */
bxmap.control.Swipe.prototype._bindCompose = function(layer) {
	var swipetype = layer.get("swipetype");
	if (swipetype == "right"){
		layer.on('precompose', this._precomposeRight, this);
		layer.on('postcompose', this._postcompose, this);
	} else if(swipetype == "left"){
		layer.on('precompose', this._precomposeLeft, this);
		layer.on('postcompose', this._postcompose, this);
	}
}

/**
 * @private
 * @description 解绑precompose/postcompose事件
 * @param layer {ol.layer.Layer}
 */
bxmap.control.Swipe.prototype._unbindCompose = function(layer) {
	var swipetype = layer.get("swipetype");
	if (swipetype == "right") {
		layer.un('precompose', this._precomposeRight, this);
		layer.un('postcompose', this._postcompose, this);
	} else if(swipetype == "left"){
		layer.un('precompose', this._precomposeLeft, this);
		layer.un('postcompose', this._postcompose, this);
	}
}

/**
 * @private
 */
bxmap.control.Swipe.prototype._handleMove = function(e) {
	var self = e.data;
    var mapTargetElement = self.getMap().getTargetElement();
	switch (e.type){
		case 'touchcancel': 
		case 'touchend': 
		case 'mouseup': 
		{	
			self.isMoving = false;
			$(mapTargetElement).off ("mouseup mousemove touchend touchcancel touchmove", self._handleMove );
			break;
		}
		case 'mousedown': 
		case 'touchstart':
		{	
			self.isMoving = true;
			$(mapTargetElement).on ("mouseup mousemove touchend touchcancel touchmove", self, self._handleMove );
		}
		default: 
		{	
			if (self.isMoving){
				if (self.get('orientation') === "vertical"){
					//相对位置有问题 地图position 可能使用了padding/margin 导致left为0
					// var pageX = e.pageX || e.originalEvent.touches[0].pageX;
					// if (!pageX) break;
					//pageX -= $(self.getMap().getTargetElement()).position().left;

                    var pageX = e.originalEvent.clientX  || e.originalEvent.touches[0].clientX;
                    //获取地图相对屏幕的绝对位置
                    var left = self.getMap().getTargetElement().getBoundingClientRect().left;
                    pageX -= left;

					var l = self.getMap().getSize()[0];
					l = Math.min(Math.max(0, 1-(l-pageX)/l), 1);
					self.set('position', l);
				}else{
					var pageY = e.pageY || e.originalEvent.touches[0].pageY;
					if (!pageY) break;
					pageY -= $(self.getMap().getTargetElement()).position().top;
				
					var l = self.getMap().getSize()[1];
					l = Math.min(Math.max(0, 1-(l-pageY)/l), 1);
					self.set('position', l);
				}
			}
			break;
		}
	}
}

/**
 * @private
 */
bxmap.control.Swipe.prototype._precomposeLeft = function(e) {
	var ctx = e.context;
	var canvas = ctx.canvas;
	ctx.save();
	ctx.beginPath();
	if (this.get('orientation') === "vertical") ctx.rect (0,0, canvas.width*this.get('position'), canvas.height);
	else ctx.rect (0,0, canvas.width, canvas.height*this.get('position'));
	ctx.clip();
}

/**
 * @private
 */
bxmap.control.Swipe.prototype._precomposeRight = function(e) {
	var ctx = e.context;
	var canvas = ctx.canvas;
	ctx.save();
	ctx.beginPath();
	if (this.get('orientation') === "vertical") ctx.rect (canvas.width*this.get('position'), 0, canvas.width, canvas.height);
	else ctx.rect (0,canvas.height*this.get('position'), canvas.width, canvas.height);
	ctx.clip();
}

/**
 * @private
 */
bxmap.control.Swipe.prototype._postcompose = function(e) {
	ctx = e.context.restore();
}