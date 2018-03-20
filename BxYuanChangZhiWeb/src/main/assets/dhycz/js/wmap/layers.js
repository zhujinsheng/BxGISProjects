var wmap = wmap || {};
wmap.wlayers = function () {
    var hwLayerId = "wzt_hw_1000"; //海湾图层Id
    var axLayerId = "wzt_ax_1000"; //岸线图层Id
    var hiLayerId = "wzt_hi_1000"; //高亮图层Id

    //文本样式
    //always总是显示
    var createTextStyle = function(feature, resolution, always){
        var text = feature.get("name");

        var maxresolution = window.G.maxResolution; //显示文本的最大精度
        if(!always && maxresolution && resolution > maxresolution){
            text = "";
        }

        var sty = new ol.style.Text({
            font: "normal 12px Arial",
            scale: 1,
            text: text,
            textAlign: "center",
            textBaseline: "middle",
            fill: new ol.style.Fill({ color: "#fff" }),
            stroke: new ol.style.Stroke({
                color: "#98744E",
                width: 1
            })
        });
        return sty;
    }

    var red = [237, 28, 36, 1];
    var blue = [0, 153, 255, 1];
    var width = 2;
    //初始化样式
    var styles = {};
    //岸线默认样式
    styles["AX"] = function (feature, resolution) {
        return new ol.style.Style({
            stroke: new ol.style.Stroke({
                color: blue,
                width: width
            }),
            text : createTextStyle(feature, resolution)
        });
    }
    //岸线高亮样式
    styles["AX_Highlight"] = function (feature, resolution) {
        return new ol.style.Style({
            stroke: new ol.style.Stroke({
                color: red,
                width: width + 1
            })
        })
    }
    //海湾默认样式
    styles["HW"] = function (feature, resolution) {
        return new ol.style.Style({
            fill: new ol.style.Fill({
                color: [255, 255, 255, 0.5]
            }),
            stroke: new ol.style.Stroke({
                color: blue,
                width: width
            }),
            text : createTextStyle(feature, resolution)
        });
    }
    //海湾高亮样式
    styles["HW_Highlight"] = function (feature, resolution) {
        return new ol.style.Style({
            fill: new ol.style.Fill({
                color: [255, 255, 255, 0.5]
            }),
            stroke: new ol.style.Stroke({
                color: red,
                width: width + 1
            })
        });
    }
    //轨迹样式函数
    styles["Trace"] = [
        styles["AX_Highlight"],
        new ol.style.Style({
            image: new ol.style.Icon({
                size:[26,32],
                src: bxmap.Resource.ResourcePath + "trace/stp.png",
                anchor:[0.5,36],
                anchorOrigin:"top-left",
                anchorXUnits:"fraction",
                anchorYUnits:"pixels",
                offsetOrigin:"top-left",
                snapToPixel:true,
                rotateWithView:false
            }),
            geometry: function(feature) {
                // LineString
                var coordinates = feature.getGeometry().getFirstCoordinate();
                return new ol.geom.Point(coordinates);
            }
        }),
        new ol.style.Style({
            image: new ol.style.Icon({
                size:[26,32],
                src: bxmap.Resource.ResourcePath + "trace/endp.png",
                anchor:[0.5,36],
                anchorOrigin:"top-left",
                anchorXUnits:"fraction",
                anchorYUnits:"pixels",
                offsetOrigin:"top-left",
                snapToPixel:true,
                rotateWithView:false
            }),
            geometry: function(feature) {
                // LineString
                var coordinates = feature.getGeometry().getLastCoordinate();
                return new ol.geom.Point(coordinates);
            }
        })
    ];
    
    /**
     * 添加海湾、岸线图层到地图，如果地图已添加则不再执行添加操作
     * @param bmap
     * @returns {ol.layer.Layer}
     */
    function checkAddLayersToMap(bmap) {

        //海湾图层
        var addHwLayer = function () {
            var layer  = bmap.getIndexLayer(hwLayerId);
            if(!layer){
                layer = new ol.layer.Vector({
                    source : new ol.source.Vector(),
                    style : styles["HW"]
                });
                layer.set(bxmap.INDEX_LAYER_ID, hwLayerId);
                bmap.addIndexLayer(layer);
            }
        }

        //岸线图层
        var addAxLayer = function(){
            var layer  = bmap.getIndexLayer(axLayerId);
            if(!layer){
                layer = new ol.layer.Vector({
                    source : new ol.source.Vector(),
                    style :  styles["AX"]
                });
                layer.set(bxmap.INDEX_LAYER_ID, axLayerId);
                bmap.addIndexLayer(layer);
            }
        }

        //高亮图层
        var addHighlightLayer = function () {
            var layer  = bmap.getIndexLayer(hiLayerId);
            if(!layer){
                layer = new ol.layer.Vector({
                    source : new ol.source.Vector()
                });
                layer.set(bxmap.INDEX_LAYER_ID, hiLayerId);
                bmap.addIndexLayer(layer);
            }
        }

        //添加海湾、岸线图层、高亮图层
        addHwLayer();
        addAxLayer();
        addHighlightLayer();
    }

    /**
     * 高亮要素，会改变feature原始样式
     * @pharam type {ol.Feature}
     * @private
     */
    function highlightFeature(bmap, type, feature) {
        var style = styles[type];
        if(style){
            feature.setStyle(style);
        }

        var layer = bmap.getIndexLayer(hiLayerId);

        if(layer){
            //先清空
            var vecSource = layer.getSource();
            vecSource.clear();
            if(feature){
                //再添加
                vecSource.addFeature(feature);
            }
        }
    }

    /**
     * 高亮要素，不会改变feature原始样式
     * @param bmap
     * @param type
     * @param feature
     */
    function highlightCopyFeature(bmap, type, feature) {
        if(feature){
            var geometry = feature.getGeometry();
            var newFeature = new ol.Feature({
                geometry: geometry.clone()
            });
        }

        highlightFeature(bmap,type,newFeature);
        return newFeature;
    }

    /**
     * 高亮几何
     * @pharam type {String} 样式代码 "AX"|"AX_Highlight"|"HW"|"HW_Highlight"
     * @param wkt
     * @return {ol.Feature}
     */
    function highlightWKTGeometry(bmap, type, wkt) {
        var feature = new ol.format.WKT().readFeature(wkt);
        highlightFeature(bmap, type, feature);
        bmap.zoomToFeature(feature);

        return feature;
    }
    
    /**
     * 显示轨迹
     * @param bmap
     * @param wkt
     * @return {ol.Feature}
     */
    function traceWKTGeometry(bmap, wkt) {
        return highlightGeometry(bmap, "Trace", wkt);
    }

    /**
     * 获取海湾图层
     */
    function getHWLayer(bmap) {
        var layer  = bmap.getIndexLayer(hwLayerId);
        return layer;
    }
    /**
     * 获取海岸线图层
     */
    function getAXLayer(bmap) {
        var layer  = bmap.getIndexLayer(axLayerId);
        return layer;
    }

    return {
        checkAddLayersToMap: checkAddLayersToMap,
        highlightCopyFeature: highlightCopyFeature,
        highlightWKTGeometry: highlightWKTGeometry,
        traceWKTGeometry: traceWKTGeometry,
        getHWLayer: getHWLayer,
        getAXLayer: getAXLayer
    }
}