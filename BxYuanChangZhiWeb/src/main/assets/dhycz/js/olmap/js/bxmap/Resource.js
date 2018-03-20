/**
 * @require Base.js
 */

/**
 * @namespace bxmap.Resource
 * @description 地图资源命名空间
 * @type {{}}
 */
bxmap.Resource = bxmap.Resource || {};

/**
 * @description 设置地图资源（图片/css）目录，例如"./olmap/res/"。
 * 在向地图中添加默认控件（由bxmap.control.Defaults对象创建的控件）之前应先执行该方法，默认控件需要资源目录位置
 * @memberof bxmap.Resource
 * @function
 * @param path {String} 路径
 * @static
 */
bxmap.Resource.setBasePath = function(path){
    bxmap.Resource.BaseURL = path;

    bxmap.Resource.ResourcePath = bxmap.Resource.BaseURL + "res/images/bmap/";
    //地图分屏时，副屏鼠标样式
    bxmap.Resource.LinkedMousePng = bxmap.Resource.ResourcePath + "ponter.png";
    //图层控制器图标
    bxmap.Resource.LayerSwitcherMapPng = bxmap.Resource.ResourcePath + "map_16.png";
    bxmap.Resource.LayerSwitcherGroupPng = bxmap.Resource.ResourcePath + "layers_16.png";
    bxmap.Resource.LayerSwitcherLayerPng = bxmap.Resource.ResourcePath + "layer_16.png";

    //鼠标样式
    bxmap.Resource.ZoomInCursor = bxmap.Resource.ResourcePath + "cursor/zoomin.cur";
    bxmap.Resource.ZoomOutCursor = bxmap.Resource.ResourcePath + "cursor/zoomout.cur";
    bxmap.Resource.DragPanCursor = bxmap.Resource.ResourcePath + "cursor/openhand.cur";
}