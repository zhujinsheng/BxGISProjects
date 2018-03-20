
var bxmap = bxmap || {};

/**
 * 地图工具组件
 * @param options
 * 		bmap - {bxmap.Map}
 */
bxmap.MapTool = function(options){
	var opt_options = options || {};
	this.bmap = opt_options.bmap;
	//用户自定义数据字典
	this._userData  = {};
	
	//地图交互工具
	this.interactions = null;
	this.initializeInteraction();
	this.initializeLayers();
	
	//底图切换/三维切换工具
	this.switherToolbar = new ToolBar({className:"switcherToolBar", showClose:false});
	this.initializeSwitherToolbar();
	
	//图层编辑窗口
	this.layerEditToolbar= null;
	this.initializeLayerEditToolbar();
	
	//常用地图工具
	this.layerSwitcherWindow = new FixWindow({width:270});//常用工具中的图层管理器工具窗体
	this.commonToolbar = new ToolBar({className:"commonToolBar", showClose:false});
	this.initializeCommonToolbar();
	//标绘工具窗
	this.plotDrawWindow = new FixWindow({width:254});
	this.initializePlotDrawWindow();
	//图片标注工具窗
	this.plotImageWindow = new FixWindow();
	this.initializePlotImageWindow();
	//文本标绘工具
	this.plotTextTool = null;
	this.initializePlotTextTool();
	
	//初始化工具箱
	this.initializeToolbox();
	//绑定工具箱自定义数据
	this.setToolboxUserData();
}

/**
 * 设置用户数据
 * @param key {String} 字符串类型
 * @param value {Object} 任意对象
 */
bxmap.MapTool.prototype.setUserData = function(key, value){
	if(key == null) return;
	if(typeof key !== "string"){
		key = key.toString();
	}
	this._userData[key] = value;
}

/**
 * 获取用户数据
 * @param key {String} 字符串类型
 * @returns {Object}
 */
bxmap.MapTool.prototype.getUserData = function(key){
	if(key == null) return null;
	if(typeof key !== "string"){
		key = key.toString();
	}
	return this._userData[key];
}

/**
 * 初始化地图交互工具
 */
bxmap.MapTool.prototype.initializeInteraction = function(){
	var bmap = this.bmap;
	// 添加默认交互工具
	var defInteractions = this.interactions = new bxmap.interaction.Defaults();
	var interaction;
	for(var i = 0; i < defInteractions.interactions.length; i++){
		interaction = defInteractions.interactions[i];
		if(interaction){
			bmap.addMutexInteraction(interaction);
		}
	}

    //量测注册为清空对象
    var dataClears = bmap.dataClear;
	if(!dataClears){
        dataClears = bmap.dataClear = new bxmap.DataClear();
	}
    var measure_area = defInteractions.get("measure_area");
    dataClears.register(measure_area);
    var measure_distance = defInteractions.get("measure_distance");
    dataClears.register(measure_distance);
}

/**
 * @description 初始化地图默认图层
 */
bxmap.MapTool.prototype.initializeLayers = function(){
	var defLayers = new bxmap.layer.Defaults();
	var layer;
	for(var i = 0; i < defLayers.layers.length; i++){
		layer = defLayers.layers[i];
		this.bmap.addIndexLayer(layer);	
	}
}

/**
 * 初始化切换工具条
 */
bxmap.MapTool.prototype.initializeSwitherToolbar = function(){
	var bmap = this.bmap;
	var layerconfig = bmap.getLayerContainer();
	var _this = this;
	
	//底图切换配置
	var baseLayerGroup = {
		"yx": "GDImage_map",//影像group_id
		"ht": "SeaMap_map"//地图group_id
	};
	
	var toolbar = this.switherToolbar;
	toolbar.addTool(new ToolboxTool({
			className:"toolgroupbar imagemap",
			title:"广东影像",
			onClick:function(evt){
				layerconfig.setCurrentBaseLayerGroup(baseLayerGroup.yx);
				bmap.updateBaseLayerGroup();
			}
		})
	);
	toolbar.addTool(new ToolboxTool({
			className:"toolgroupbar vmap",
			title:"广东海图",
			onClick:function(evt){
				layerconfig.setCurrentBaseLayerGroup(baseLayerGroup.ht);
				bmap.updateBaseLayerGroup();
			}
		})
	);
	//切换三维工具
	var d3dTool;
	toolbar.addTool(new ToolboxTool({
			className:"toolgroupbar d3dmap",
			title:"三维",
			onClick:function(evt){
				d3dTool = _this.getUserData("switcher_d3d_tool");
				d3dTool.setMode("right");
			}
		})
	);
	toolbar.redraw();
}

/**
 * 初始化常用工具条
 */
bxmap.MapTool.prototype.initializeCommonToolbar = function(){
	//地图交互工具
	var bmap = this.bmap;
	var tools = this.interactions;
	
	var toolbar = this.commonToolbar;	
	toolbar.addTool(new ToolboxTool({
			className:"toolgroupbar reposition",
			title:"复位",
			onClick:function(evt){
				bmap.zoomInitialPosition();
			}
		})
	);
	toolbar.addTool(new ToolboxTool({
			className:"toolgroupbar drag",
			title:"平移",
			onClick:function(evt){
				tool = tools.get("pan");
				bmap.setCurrentMutexInteraction(tool);
			}
		})
	);
	toolbar.addTool(new ToolboxTool({
			className:"toolgroupbar lager",
			title:"放大",
			onClick:function(evt){
				tool = tools.get("zoom_in");
				bmap.setCurrentMutexInteraction(tool);
			}
		})
	);
	toolbar.addTool(new ToolboxTool({
			className:"toolgroupbar smaller",
			title:"缩小",
			onClick:function(evt){
				tool = tools.get("zoom_out");
				bmap.setCurrentMutexInteraction(tool);
			}
		})
	);
	toolbar.addTool(new ToolboxTool({
			className:"toolgroupbar cej_icon",
			title:"测距离",
			onClick:function(evt){
				tool = tools.get("measure_distance");
				bmap.setCurrentMutexInteraction(tool);
			}
		})
	);
	toolbar.addTool(new ToolboxTool({
			className:"toolgroupbar cem_icon",
			title:"测面积",
			onClick:function(evt){
				tool = tools.get("measure_area");
				bmap.setCurrentMutexInteraction(tool);
			}
		})
	);
	//图层管理
	var layerSwitcherCtrl = new bxmap.control.LayerSwitcher({
		className:"ol-layer-switcher-nobutton",
		shownButton:false
	});
	layerSwitcherCtrl.showPanel();
	layerSwitcherCtrl.setMap(bmap.getMap());
	var layerSwitcherWindow = this.layerSwitcherWindow;
	layerSwitcherWindow.itemContainerElement.appendChild(layerSwitcherCtrl.panel);
	var _this = this;
	toolbar.addTool(new ToolboxTool({
			className:"toolgroupbar layertool",
			title:"图层管理器",
			onClick:function(evt){
				_this.plotDrawWindow.hide();
				_this.plotImageWindow.hide();
				layerSwitcherWindow.show();
			}
		})
	);
	var layerEditToolbar=this.layerEditToolbar;
	toolbar.addTool(new ToolboxTool({
			className:"toolgroupbar wfslayereditor",
			title:"图层编辑器",
			onClick:function(evt){
				layerEditToolbar.setVisible(true);
			}
		})
	);
	toolbar.addTool(new ToolboxTool({
			className:"toolgroupbar swipec",
			title:"卷帘效果",
			onClick:function(evt){
				var ctrl = bmap.getIndexControl(9);
				var visible = ctrl.getVisible();
				ctrl.setVisible(!visible);
				ctrl.changed();
			}
		})
	);
	toolbar.redraw();
}

/**
 * 初始化图层编辑器工具条
 */
bxmap.MapTool.prototype.initializeLayerEditToolbar=function(){
	//var toolbar = this.layerEditToolbar;
	
	//加载wfs图层数据
	var mapConfig = this.bmap.getMapConfig();
	var wfslayers = mapConfig.wfsLayers;
	var wfsLayerItems = [];
	for(var i = 0; i < wfslayers.length; i++){
		var wfslayer = wfslayers[i];
		//图层是否添加到WFS编辑工具条中
		if(!wfslayer.get("displayInEditor")) continue;
		wfsLayerItems.push({
			"id": wfslayer.get(bxmap.INDEX_LAYER_ID),
			"name": wfslayer.get("name"),
			"visible":wfslayer. getVisible(),
			"data": wfslayer
		});
		this.bmap.addIndexLayer(wfslayer);
	}
	
	//初始化wfs图层编辑工具条
	var wfsEditor = new bxmap.WFSEditor({
		bmap: this.bmap,
		wfsLayerItems: wfsLayerItems
	});
	this.setUserData("bxmap_WFSEditor", wfsEditor);
	this.layerEditToolbar = wfsEditor.wfsLayerEditorToolbar;
}

/**
 * 初始化标绘工具窗
 */
bxmap.MapTool.prototype.initializePlotDrawWindow = function(){
	var html = '<span class="plot_freehandline" plottype="freehandline"></span>';
	html += '<span class="plot_line" plottype="polyline"></span>';
	html += '<span class="emergency_hx" plottype="arc"></span>';
	html += '<span class="emergency_qx" plottype="curve"></span>';
	html += '<span class="emergency_freehand" plottype="freehandpolygon"></span>';
	html += '<span class="emergency_polygon" plottype="polygon"></span>';
	html += '<span class="emergency_rect" plottype="rectangle"></span>';
	html += '<span class="emergency_circle" plottype="circle"></span>';
	html += '<span class="emergency_straight_arrow" plottype="assaultdirection"></span>';
	html += '<span class="emergency_simple_arrow" plottype="squadcombat"></span>';
	html += '<span class="emergency_tailed_arrow" plottype="tailedsquadcombat"></span>';
	html += '<span class="emergency_assembly" plottype="gatheringplace"></span>';
	
	//地图交互工具
	var bmap = this.bmap;
	var tools = this.interactions;
	
	var plotDrawWindow = this.plotDrawWindow;
	plotDrawWindow.itemContainerElement.innerHTML = html;
	plotDrawWindow.addItemEvent({
		events:"click",
		selector:"span",
		handler: function(evt){
			var plottype = $(this).attr("plottype");
			tool = tools.get("plot");
			bmap.setCurrentMutexInteraction(tool);
			tool.setDrawType(plottype);
		}
	});
}

/**
 * 初始化图片标记
 */
bxmap.MapTool.prototype.initializePlotImageWindow = function(){
	
	var resourcePath = bxmap.Resource.ResourcePath + "plot/";
	var html = '<select name="fenlei" class="fenlei">';
	html += '<option value="car" selected="true">车辆类</option>';
	html += '<option value="person">消防力量</option>';
	html += '<option value="material">物质资源</option>';
	html += '</select>';
	html += '<div class="imgplottls"><ul><li class="tlselect"><a href="javascript:void(0)"><span class="tlselectlabel"></span>框选</a></li><li class="tlclear"><a href="javascript:void(0);"><span class="tlclearlabel"></span>清空</a></li></ul></div>';
	html += '<div class="imgList">';
	html += '    <div class="contish">';
	html += '            <ul class="car" style="display:block;">';
	html += '                <li><img src="' + resourcePath + 'car/c6.gif"></li>';
	html += '                <li><img src="' + resourcePath + 'car/c6-1.gif"></li>';
	html += '                <li><img src="' + resourcePath + 'car/c4-1.gif"></li>';
	html += '                <li><img src="' + resourcePath + 'car/c0.gif"></li>';
	html += '                <li><img src="' + resourcePath + 'car/c5.gif"></li>';
	html += '                <li><img src="' + resourcePath + 'car/c1.gif"></li>';
	html += '                <li><img src="' + resourcePath + 'car/c8.gif"></li>';
	html += '                <li><img src="' + resourcePath + 'car/c8-1.gif"></li>';
	html += '                <li><img src="' + resourcePath + 'car/c7-1.gif"></li>';
	html += '                <li><img src="' + resourcePath + 'car/bingzuo.png"></li>';
	html += '                <li><img src="' + resourcePath + 'car/huoche.png"></li>';
	html += '                <li><img src="' + resourcePath + 'car/c2.gif"></li>';
	html += '            </ul>';
	html += '            <ul class="person" style="display:none;">';
	html += '                <li><img src="' + resourcePath + 'person/p1.gif"></li>';
	html += '                <li><img src="' + resourcePath + 'person/p2.gif"></li>';
	html += '                <li><img src="' + resourcePath + 'person/p2-1.gif"></li>';
	html += '                <li><img src="' + resourcePath + 'person/p3.gif"></li>';
	html += '                <li><img src="' + resourcePath + 'person/p3-1.gif"></li>';
	html += '                <li><img src="' + resourcePath + 'person/p4.gif"></li>';
	html += '                <li><img src="' + resourcePath + 'person/p5.gif"></li>';
	html += '                <li><img src="' + resourcePath + 'person/p5-1.gif"></li>';
	html += '                <li><img src="' + resourcePath + 'person/p6.gif"></li>';
	html += '                <li><img src="' + resourcePath + 'person/p6-1.gif"></li>';
	html += '                <li><img src="' + resourcePath + 'person/p7.gif"></li>';
	html += '                <li><img src="' + resourcePath + 'person/p7-1.gif"></li>';
	html += '            </ul>';
	html += '             <ul class="material" style="display:none;">';
	html += '                <li><img src="' + resourcePath + 'material/m0.gif"></li>';
	html += '                <li><img src="' + resourcePath + 'material/m0-1.gif"></li>';
	html += '                <li><img src="' + resourcePath + 'material/m0-2.gif"></li>';
	html += '                <li><img src="' + resourcePath + 'material/m1.gif"></li>';
	html += '                <li><img src="' + resourcePath + 'material/m1-1.gif"></li>';
	html += '                <li><img src="' + resourcePath + 'material/m2.gif"></li>';
	html += '                <li><img src="' + resourcePath + 'material/m3.gif"></li>';
	html += '                <li><img src="' + resourcePath + 'material/m4.gif"></li>';
	html += '                <li><img src="' + resourcePath + 'material/m4-1.gif"></li>';
	html += '                <li><img src="' + resourcePath + 'material/m5.gif"></li>';
	html += '                <li><img src="' + resourcePath + 'material/m5-1.gif"></li>';
	html += '                <li><img src="' + resourcePath + 'material/m2-1.gif"></li>';
	html += '            </ul>';
	html += '    </div>';
	html += '</div>';
	
	var plotImageWindow = this.plotImageWindow;
	plotImageWindow.itemContainerElement.innerHTML = html;
	
	//select切换图片资源
	var lastSelected = "car",currSelected,
		resItem;
	plotImageWindow.addItemEvent({
		events:"change",
		selector:"select",
		handler: function(evt){
			if(lastSelected === evt.target.value) return;
			currSelected = evt.target.value;
			//隐藏上一个选项
			var $item = $(plotImageWindow.itemContainerElement);
			resItem = $item.find("." + lastSelected)[0];
			resItem.style.display = "none";
			//显示当前选项
			resItem = $item.find("." + currSelected)[0];
			resItem.style.display = "block";
			
			lastSelected = currSelected;
		}
	});
	
	//创建图片标注图层
	var bmap = this.bmap;
	var imageLabelLayer = bmap.getIndexLayer(bxmap.DEFAULT_IMG_LABEL_LAYER_ID);
	//用于绘制图片标注
	var tool = new bxmap.interaction.Draw({
		source : imageLabelLayer.getSource(),
		type: 'Point'
	});
	bmap.addMutexInteraction(tool);
	
	var img;
	//加载图片
	function loadImg (url) {
	    var defer = $.Deferred();

	    img = new Image();
	    img.src = url;
	    img.onload = function () {
	        defer.resolve()  ;
	    };
	    img.onerror = function () {
	        defer.reject()  ;
	    };

	    return defer.promise();
	};
	//点击图片事件绘制图片标注
	var srcImagePath, feature;
	function imageLabelDrawend(evt){
		feature = evt.feature;
		//图片加载完成后设置feature样式
		$.when(loadImg(srcImagePath)).then(function () {
			var opt = bxmap.common.getFeatureIconParams(feature);
			opt.img = img;
			feature.changed();
		});
	}
	//添加图片点击事件绘制图片标注
	plotImageWindow.addItemEvent({
		events:"click",
		selector:"img",
		handler: function(evt){
			tool.un("drawend",imageLabelDrawend);
			srcImagePath = evt.target.currentSrc;
			bmap.setCurrentMutexInteraction(tool);
			tool.on("drawend",imageLabelDrawend);
		}
	});
	
	//框选工具临时图层
	var source = new ol.source.Vector({wrapX: false});
	source.on("addfeature",function(){
		source.clear();
	});
    var vector = new ol.layer.Vector({
       source: source
    });
    bmap.getMap().addLayer(vector);
    //编辑工具
    var transform = new bxmap.interaction.Transform();
	bmap.addMutexInteraction(transform);
    //框选工具
	var boxselect = new bxmap.interaction.Draw({
		source: source,
		type:"Box"
	});
	bmap.addMutexInteraction(boxselect);
	boxselect.on("drawend",function(evt){
		var extent = evt.feature.getGeometry().getExtent();
		var features = imageLabelLayer.getSource().getFeaturesInExtent(extent);
		if(features.length != 0){
			bmap.setCurrentMutexInteraction(transform);
		}
		transform.select(features);
	})
	//框选选择编辑图片
	plotImageWindow.addItemEvent({
		events:"click",
		selector:".tlselect",
		handler: function(evt){
			bmap.setCurrentMutexInteraction(boxselect);
		}
	});
	//清空标绘图片
	plotImageWindow.addItemEvent({
		events:"click",
		selector:".tlclear",
		handler: function(evt){
			bmap.setCurrentMutexInteraction(null);
			//清空图片数据
			imageLabelLayer.getSource().clear();
		}
	});
}

/**
 * 初始化文本标注工具
 */
bxmap.MapTool.prototype.initializePlotTextTool = function(){
	this.plotTextTool = new bxmap.interaction.TextLabel();
	var bmap = this.bmap;
	bmap.addMutexInteraction(this.plotTextTool);
}

/**
 * 初始化工具箱
 */
bxmap.MapTool.prototype.initializeToolbox = function(){
	var toolboxCtrl = this.bmap.getIndexControl(999);
	var mapTool = this;
	var bmap = this.bmap;
	
	var element = toolboxCtrl.element;
	element.className = "tool_next";
	var toolbox = new Toolbox({
		target: element
	});
	
	//上次弹出的工具窗
	var lastPopupWindow = null;
	//底图切换/3D切换ToolGroup
	var switcherToolGroup = new ToolGroup({ className:"toolgroup switchertool",tip:"切换工具" });
	switcherToolGroup.addToolBar(mapTool.switherToolbar);
	switcherToolGroup.redraw();
	toolbox.addToolGroup(switcherToolGroup);
	//常用工具ToolGroup
	var commonToolGroup = new ToolGroup({ className:"toolgroup common",tip:"常用工具" });
	commonToolGroup.addToolBar(mapTool.commonToolbar);
	var layerSwitcherWindow = mapTool.layerSwitcherWindow;//图层管理器窗体
	toolboxCtrl.element.appendChild(layerSwitcherWindow.element);
	var layerEditToolbar = mapTool.layerEditToolbar;//WFS图层编辑器工具条
	toolboxCtrl.element.appendChild(layerEditToolbar.element);
	
	commonToolGroup.redraw();
	toolbox.addToolGroup(commonToolGroup);
	//标绘工具ToolGroup
	var plotDrawToolGroup = new ToolGroup({ className:"toolgroup plotdraw",tip:"标绘" });
	var plotDrawWindow = mapTool.plotDrawWindow;//标绘工具窗
	toolboxCtrl.element.appendChild(plotDrawWindow.element);
	plotDrawToolGroup.onClick = function(evt){
		if(lastPopupWindow){
			lastPopupWindow.hide();
		}
		plotDrawWindow.show();
		lastPopupWindow = plotDrawWindow;
	}
	toolbox.addToolGroup(plotDrawToolGroup);
	//文本标注ToolGroup
	var labelDrawToolGroup = new ToolGroup({ className:"toolgroup labeldraw",tip:"文本标注" });
	toolbox.addToolGroup(labelDrawToolGroup);
	labelDrawToolGroup.onClick = function(){
		bmap.setCurrentMutexInteraction(mapTool.plotTextTool);
	}
	//图标标注ToolGroup
	var plotImageToolGroup = new ToolGroup({ className:"toolgroup imageplot",tip:"图片标注" });
	var plotImageWindow = mapTool.plotImageWindow;//图片标注工具窗
	toolboxCtrl.element.appendChild(plotImageWindow.element);
	plotImageToolGroup.onClick = function(evt){
		if(lastPopupWindow){
			lastPopupWindow.hide();
		}
		plotImageWindow.show();
		lastPopupWindow = plotImageWindow;
	}
	toolbox.addToolGroup(plotImageToolGroup);
	
	//toolbox toolGroupsVisibilityListeners事件
	toolbox.addToolGroupsVisibilityListener(function(visibility){
		if(visibility && lastPopupWindow){
			lastPopupWindow.hide();//隐藏上个窗体
		}
		if(visibility){
			layerSwitcherWindow.hide();//隐藏图层管理器
			bmap.setCurrentMutexInteraction(null);//设置当前工具为null
		}
		
	});
	
	//绘制toolbox
	toolbox.redraw();
}

/**
 * 绑定工具箱自定义数据
 */
bxmap.MapTool.prototype.setToolboxUserData = function(){
	var ctrl = this.bmap.getIndexControl(999);
	if(ctrl){
		ctrl.set("bxmap_WFSEditor", this.getUserData("bxmap_WFSEditor"));//value {bxmap.WFSEditor}
	}
}