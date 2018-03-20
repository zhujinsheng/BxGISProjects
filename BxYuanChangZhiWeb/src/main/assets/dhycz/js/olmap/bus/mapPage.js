var mapPage = {
		map:null,
		init: function () {
            bxmap.Resource.setBasePath("http://localhost:18080/systems/common-bx-gis/models/olmap/");

			$("#map").children().remove();
			map = bxmap.Map.createDefaultMap("map","map_1"); //创建一个地图实例
            var map2 = bxmap.Map.createDefaultMap("map2","map_1"); //创建一个地图实例
			//联动设置
            map.linkPosition(map2);

			var controlCreator = new bxmap.control.Defaults();
			//创建底图切换控件
			var mapLabelArray = [
			    {"id":1,"label":"影像","className":"imgType","data":"GDImage_map"},
			    {"id":2,"label":"海图","className":"vecType","data":"SeaMap_map"}
 			];
			controlCreator.createBaseLayerControl(map, mapLabelArray);
			//创建地图导航控件
			controlCreator.createNavigation(map);
			//显示鹰眼
			controlCreator.createOverviewMap(map);
			//显示地图比例尺
			controlCreator.createScaleLine(map);
			//显示地图版权信息  type:0为国家数据,1为省级数据,2为本市数据
			//map.showCopyRight(2);
			//显示当前坐标
			controlCreator.createMousePosition(map);
			//显示地图工具栏
			BX.map2dTool.InitTool(map);
			//添加地图图例
		}		
};
