define(function (require, exports, module) {

    var view_list = require('./map.html');
    var Conf = require('./conf');//配置信息
    var $doc = MVC.loadView(view_list);//加载列表视图页面并返回

    //设置地图res目录
    bxmap.Resource.setBasePath(Conf.OLMAP_PATH);

    var bmap = bxmap.Map.createDefaultMap('map','map_1'); //创建一个地图实例
    var controlCreator = new bxmap.control.Defaults();
    //地图滑动卷帘控件
    controlCreator.createSwipeControl(bmap);
    //创建地图导航控件
    controlCreator.createNavigation(bmap);
    //显示鹰眼
    controlCreator.createOverviewMap(bmap);
    //创建底部背景条
    controlCreator.createBottomBackgroudControl(bmap);
    //显示地图比例尺
    controlCreator.createScaleLine(bmap);
    //显示当前坐标
    controlCreator.createMousePosition(bmap);

    controlCreator.createToolboxControl(bmap);

    var mapTool = new bxmap.MapTool({bmap:bmap});
    //设置当前交互工具为平移工具
    var pan = bmap.getIndexInteraction(bxmap.DEFAULT_INTER_DRAG_PAN_ID);
    bmap.setCurrentMutexInteraction(pan);
});