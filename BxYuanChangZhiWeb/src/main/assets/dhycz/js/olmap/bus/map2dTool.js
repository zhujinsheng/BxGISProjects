var BX = BX || {};
BX.map2dTool = {
		map: null,
		isLegend:false,//判断图例控件标识
		isFullScreen:false,//是否全屏
		is_initialize:null,//判断地图分屏初始化标识
		/**
		 *根据用户权限,读取工具栏的配置文件
		 *读取工具栏的配置文件,动态加载工具栏菜单
		 */  	    
		LoadToolbarHtml:function(){
			var toolbarHtml = "<div class='alright_top_rt'><ul>";
			//"viewQuery"://快速定位
			toolbarHtml += "<li class='view' id='viewQuery'><a href='javascript:void(0)' class='viewbg'><span class='viewlabel'></span>快速定位</a></li>";
			toolbarHtml += "<li class='publine'></li>";
			//"bPlot"://应急标绘
			//toolbarHtml += "<li class='PlotlTool' id='bPlot'><a href='javascript:void(0);' class='Pointbg'><span class='Plotlabel'></span>应急标绘</a></li>";
			toolbarHtml += "<li class='PlotlTool' id='bPlot' data-toggle='collapse' data-target='#Plotdemo'><a href='javascript:void(0);' class='Pointbg'><span class='Plotlabel'></span>应急标绘</a></li>";
			toolbarHtml += "<li class='publine'></li>";
			//"legend"图例
			toolbarHtml += "<li class='legend' id='legend'><a href='javascript:void(0);' class='legendbg'><span class='legendlabel'></span>图例</a></li>";
			toolbarHtml += "<li class='publine'></li>";
			//"zoomOut"://放大
			toolbarHtml += "<li class=zoomOut id='zoomOut'><a href='javascript:void(0)' class='zoomOutbg'><span class='zoomOutlabel'></span>放大</a></li>";
			toolbarHtml += "<li class='publine'></li>";
			//"zoomIn"://缩小
			toolbarHtml += "<li class='zoomIn' id='zoomIn'><a href='javascript:void(0)' class='zoomInbg'><span class='zoomInlabel'></span>缩小</a></li>";
			toolbarHtml += "<li class='publine'></li>";
			//"panMove"://漫游
			toolbarHtml += "<li class='panMove' id='panMove'><a href='javascript:void(0)' class='panMovebg'><span class='panMovelabel'></span>漫游</a></li>";
			toolbarHtml += "<li class='publine'></li>";
			//"mapCompare"://地图对比
			toolbarHtml += "<li class='mapcompare' id='mapCompare'>" +
			"<a href='javascript:void(0)' class='mapcomparebg' id='mapcompareType'><span class='mapcomparelabel'></span><span id='mtext'>二维地图</span></a><span class='raang_more' id='toolCur'></span>" +
			"<ul style='display: none;' id='mapcompareDiv'>" +
			"<li id='m2d' style='display:none;'><a href='javascript:void(0)'><span class='mapcomparelabel'></span>二维地图</a></li>" +
			"<li id='m3d'><a href='javascript:void(0)'><span class='mapcomparelabel'></span>三维地图</a></li>" +
			"<li id='m23d'><a href='javascript:void(0)'><span class='mapcomparelabel'></span>二三维地图</a></li>" +
			"</ul>" +
			"</li>";
			toolbarHtml += "<li class='publine'></li>";
			//"tLi"://工具
			toolbarHtml += "<li class='tool' id='tLi'>" +
			"<a href='javascript:void(0)' class='toolbg' id='toolType'><span class='toollabel'></span>工具</a><span class='raang_more' id='toolCur'></span>" +
			"<ul style='display: none;' id='toolDiv'>" +
			"<li id='bMeasureLine'><a href='javascript:void(0)'><span class='toolcjlabel'></span>测距</a></li>" +
			"<li id='bMeasureArea'><a href='javascript:void(0)'><span class='toolcmlabel'></span>测面</a></li>" +
			"<li id='bPrint'><a href='javascript:void(0)'><span class='tooldylabel'></span>打印</a></li>" +
			"</ul>" +
			"</li>";
			toolbarHtml += "<li class='publine'></li>";
			//"bClear"://清空
			toolbarHtml += "<li class='delete' id='bClear'><a href='javascript:void(0);' class='deletebg'><span class='dellabel'></span>清空</a></li>";
			toolbarHtml += "<li class='publine'></li>";
			//"fullScreen"://全屏
			toolbarHtml += "<li class='screen' id='fullScreen'><a href='javascript:void(0);' class='screenbg'><span class='scrlabel'></span>全屏</a></li>";
			toolbarHtml += "</ul></div>";
			
			//应急标绘按钮
			toolbarHtml += '<div id="Plotdemo" class="panel-collapse collapse">';
			toolbarHtml += '<div class="btn-group" style="z-index:1000;">';
			toolbarHtml += '<button class="btn btn-default" type="button" plottype="arc" des="activePlot(P.PlotTypes.ARC)">弧线</button>';
			toolbarHtml += '<button class="btn btn-default" type="button" plottype="curve" des="activePlot(P.PlotTypes.CURVE)">曲线</button>';
			toolbarHtml += '<button class="btn btn-default" type="button" plottype="polyline" des="activePlot(P.PlotTypes.POLYLINE)">折线</button>';
			toolbarHtml += '<button class="btn btn-default" type="button" plottype="freehandline" des="activePlot(P.PlotTypes.FREEHAND_LINE)">自由线</button>';
			toolbarHtml += '<button class="btn btn-default" type="button" plottype="circle" des="activePlot(P.PlotTypes.CIRCLE)">圆</button>';
			toolbarHtml += '<button class="btn btn-default" type="button" plottype="ellipse" des="activePlot(P.PlotTypes.ELLIPSE)">椭圆</button>';
			toolbarHtml += '<button class="btn btn-default" type="button" plottype="closedcurve" des="activePlot(P.PlotTypes.CLOSED_CURVE)">曲线面</button>';
			toolbarHtml += '<button class="btn btn-default" type="button" plottype="polygon" des="activePlot(P.PlotTypes.POLYGON)">多边形</button>';
			toolbarHtml += '<button class="btn btn-default" type="button" plottype="rectangle" des="activePlot(P.PlotTypes.RECTANGLE)">矩形</button>';
			toolbarHtml += '<button class="btn btn-default" type="button" plottype="freehandpolygon" des="activePlot(P.PlotTypes.FREEHAND_POLYGON)">自由面</button>';
			toolbarHtml += '<button class="btn btn-default" type="button" plottype="gatheringplace" des="activePlot(P.PlotTypes.GATHERING_PLACE)">聚集地</button>';
			toolbarHtml += '<button class="btn btn-default" type="button" plottype="doublearrow" des="activePlot(P.PlotTypes.DOUBLE_ARROW)">钳击双箭头</button>';
			toolbarHtml += '<button class="btn btn-default" type="button" plottype="straightarrow" des="activePlot(P.PlotTypes.STRAIGHT_ARROW)">直线箭头</button>';
			toolbarHtml += '<button class="btn btn-default" type="button" plottype="finearrow" des="activePlot(P.PlotTypes.FINE_ARROW)">细直箭头</button>';
			toolbarHtml += '<button class="btn btn-default" type="button" plottype="assaultdirection" des="activePlot(P.PlotTypes.ASSAULT_DIRECTION)">直箭头</button>';
			toolbarHtml += '<button class="btn btn-default" type="button" plottype="attackarrow" des="activePlot(P.PlotTypes.ATTACK_ARROW)">进攻箭头</button>';
			toolbarHtml += '<button class="btn btn-default" type="button" plottype="tailedattackarrow" des="activePlot(P.PlotTypes.TAILED_ATTACK_ARROW)">燕尾进攻箭头</button>';
			toolbarHtml += '<button class="btn btn-default" type="button" plottype="squadcombat" des="activePlot(P.PlotTypes.SQUAD_COMBAT)">斜箭头</button>';
			toolbarHtml += '<button class="btn btn-default" type="button" plottype="tailedsquadcombat" des="activePlot(P.PlotTypes.TAILED_SQUAD_COMBAT)">燕尾斜箭头</button>';
			toolbarHtml += '</div>';
			toolbarHtml += '</div>';
			return toolbarHtml;
		}, 
		InitTool: function (map, splitViewer) {
			var T = this;
			this.map = map;
			this.splitViewer = splitViewer;
			this.is_initialize = false;
			//加载工具栏
			var child = $("#tool_container").children();
			if (child.length > 0) {
				child.remove();
			}
			//读取配置文件，动态加载工具栏菜单
			$("#tool_container").empty();
			$("#tool_container").append(BX.map2dTool.LoadToolbarHtml());

			$("#tLi").bind("mouseover", function () { $("#toolDiv").show(); });
			$("#tLi").bind("mouseout", function () { $("#toolDiv").hide(); });
			
			// 添加默认交互工具
			var tools = new bxmap.interaction.Defaults();
			var toolsObj = tools.getProperties();
			var tool;
			for(var key in toolsObj){
				tool = toolsObj[key];
				if(tool){
					this.map.addMutexInteraction(tool);
				}
			}
			
			//测距
			$("#bMeasureLine").click(function () {
				tool = tools.get("measure_distance");
				T.map.setCurrentMutexInteraction(tool);
			});

			//测面积
			$("#bMeasureArea").click(function () {
				tool = tools.get("measure_area");
				T.map.setCurrentMutexInteraction(tool);
			});

			//快速定位
			$("#viewQuery").click(function () {});

			//打印
			$("#bPrint").click(function () {});

			//清除
			$("#bClear").click(function () {});

			//全屏
			$("#fullScreen").click(function () {});	
			
			//放大缩小       
			$("#zoomOut").click(function () {
				tool = tools.get("zoom_in");
				T.map.setCurrentMutexInteraction(tool);
			});
			$("#zoomIn").click(function () {
				tool = tools.get("zoom_out");
				T.map.setCurrentMutexInteraction(tool);
			});
			//漫游
			$("#panMove").click(function () {
				tool = tools.get("pan");
				T.map.setCurrentMutexInteraction(tool);
			}); 
			//图例
			$("#legend").click(function () {});
			
			//地图对比
			$("#mapCompare").bind("mouseenter", function () {
				$("#mapcompareDiv").show();
			});
			$("#mapCompare").bind("mouseleave", function () { 
				$("#mapcompareDiv").hide(); 
			});
			//二维
			$("#mapCompare #m2d").click(function(){
//				$("#cesiumContainer").hide();
//				$("#map").show();
//				$("#ol3cesium").hide();
//				
				$("#mapCompare #mtext").text("二维地图");
				$("#mapCompare #m2d").css("display","none");
				$("#mapCompare #m3d").css("display","block");
				$("#mapCompare #m23d").css("display","block");
				T.splitViewer.setMode("left");
			});
			//三维
			$("#mapCompare #m3d").click(function(){
//				$("#cesiumContainer").show();
//				$("#map").hide();
//				$("#ol3cesium").hide();
//		    	if(!mapPage.isload3DMap){
//		    		mapPage.load3DMap();
//		    	}
//		    	
				$("#mapCompare #mtext").text("三维地图");
				$("#mapCompare #m2d").css("display","block");
				$("#mapCompare #m3d").css("display","none");
				$("#mapCompare #m23d").css("display","block");
				T.splitViewer.setMode("right");
			});
			//二三维
			$("#mapCompare #m23d").click(function(){
				//document.getElementById("ol3_cesium").contentWindow.loadOl3Cesium(map.getMap());
//				$("#cesiumContainer").hide();
//				$("#map").hide();
//				$("#ol3cesium").show();
//				
				$("#mapCompare #mtext").text("二三维地图");
				$("#mapCompare #m2d").css("display","block");
				$("#mapCompare #m3d").css("display","block");
				$("#mapCompare #m23d").css("display","none");
				T.splitViewer.setMode("left-right");
			});
			
			//应急标绘
			$("#Plotdemo button").click(function () {
				var plottype = $(this).attr("plottype");
				tool = tools.get("plot");
				T.map.setCurrentMutexInteraction(tool);
				tool.setDrawType(plottype);
			});

			//地图控件控制显示或者隐藏
			$("#mapcontrol").bind("click", function () { 
				$("#map_controltree").show(); }
			);
			$("#map_controltree").bind("mouseleave", function () {
				$("#map_controltree").hide(); 
			});

			//地图图层控制显示或者隐藏
			$("#maplayer").bind("click", function () { 
				$("#map_layertree").show(); }
			);
			$("#map_layertree").bind("mouseleave", function () {
				$("#map_layertree").hide(); 
			});
			/**浏览器窗口缩放自适应iframe*/
			$(parent.window).resize(function () {
				var isFull=BX.map2dTool.isFullScreen;
				var height=parent.document.documentElement.scrollHeight;
				var width=parent.document.documentElement.scrollWidth;
				if(!isFull){
					height=height-129;
					$(parent.document).find('iframe').css({'height': height});
				}else{
					$(parent.document).find('iframe').css({'height': '100%'});
				}
				
			});
		}
}
