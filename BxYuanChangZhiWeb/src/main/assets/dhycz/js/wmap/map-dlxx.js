$(function(){
    var global = window.G = window.G || {};
    global.maxResolution = 0.0000858306884765625; //点击海湾详情/显示文本的最大精度

    var bmap = global.bmap = bxmap.Map.createDefaultMap('olmap','vecMap'); //创建一个地图实例

    //添加图层
    var layerOp = new wmap.wlayers();
    layerOp.checkAddLayersToMap(bmap);

    //追加海湾信息
    appendHWFeatures();
    //追加海岸线信息
    appendAXFeatures();
    //详细信息气泡
    appendPopup();

    //点击选中要素
    bmap.getMap().on("click", featureClickEvent, bmap);

    //定位
    $("#map-w-location").click(function(){
        bmap.zoomInitialPosition();
    });

    //切换底图
    $("#map-grouplayer-switcher").click(function(){
        var layerContainer = bmap.getLayerContainer();
        var group = layerContainer.getNextIndexBaseLayerGroup();
        layerContainer.setCurrentBaseLayerGroup(group);
        bmap.updateBaseLayerGroup(layerContainer);
    });
});

//追加海湾要素
function appendHWFeatures() {
    var data = [{
        id: "5aab7df49397fad6d0447873",
        name:"诏安湾",
        geo_wkt: "POLYGON ((117.06687184366069 23.619823570301605, 117.07311510637714 23.618751706126375, 117.07437411227556 23.61861896709172, 117.07749501878016 23.6194959877497, 117.07827221109369 23.619217812152385, 117.07905555746811 23.619649945388744, 117.07920739270708 23.620084353010554, 117.07919152866623 23.621520103569992, 117.07965562560673 23.62223459784849, 117.08025767585116 23.624395643544176, 117.08103175291308 23.625833442759244, 117.08117632342862 23.627269142956607, 117.08085458916798 23.627981555304586, 117.08006408688811 23.628696283406782, 117.07849858933844 23.629541460869746, 117.07771019147219 23.630258355438798, 117.07739497639716 23.630970779477934, 117.07801319195528 23.631696174438957, 117.07894845001101 23.632128242924125, 117.0797188551411 23.632427852063756, 117.07987950643417 23.632708045739776, 117.0795576651542 23.633285831376043, 117.07689819539542 23.633989990645603, 117.076102564282 23.636275073535842, 117.0767048069813 23.638290681769604, 117.07732411701443 23.63886918866217, 117.07732415658461 23.639582141502672, 117.07542606905793 23.6424478851618, 117.07322350795857 23.644441929451546, 117.07259792785214 23.644721932471271, 117.07165103976376 23.646151564043521, 117.06959627864535 23.648143514711478, 117.06786309170843 23.650150833893917, 117.06629260472005 23.65143010511531, 117.06550397482863 23.651564395480534, 117.06473276840256 23.651270511425025, 117.06425488035848 23.651415868847323, 117.06332356403129 23.65155500565902, 117.06269846146483 23.651407435904503, 117.06207594265072 23.650825449534921, 117.06066721581942 23.650671477506705, 117.05956793041571 23.652387534157413, 117.05893823950817 23.652674377420624, 117.05783508072443 23.653234903268242, 117.05689744576057 23.653378405389162, 117.05518764029739 23.6520711472657, 117.05441081940387 23.65178238934584, 117.05362729406443 23.65177165233996, 117.05206609437482 23.652338796298466, 117.05049000011343 23.653764447471303, 117.05063547354837 23.655205325965085, 117.05108919770839 23.656930619552497, 117.05077267142167 23.657500395826673, 117.04951918106542 23.6576299017986, 117.04873442455414 23.658926081173774, 117.04652465269066 23.661055821469063, 117.04574183781563 23.661202197823229, 117.04497222858549 23.66076630272272, 117.04419160355974 23.66061932292348, 117.04356462051089 23.660902437597485, 117.04373376050467 23.658746471272707, 117.04311991834959 23.657308270959959, 117.04172315340088 23.655860013528866, 117.03752215572297 23.65383214213972, 117.05116760060434 23.638482613282008, 117.0605994689904 23.630881462409604, 117.06687184366069 23.619823570301605))"
    },{
        id: "5aab7e829397fad6d0447893",
        name:"乌礁湾",
        geo_wkt: "POLYGON ((116.66752145085434 23.187214838993668, 116.668468825476 23.179365990548092, 116.65817493982172 23.16410011392918, 116.6810053370267 23.145785917785929, 116.71147205219347 23.131801141609458, 116.72833138178794 23.128720612866061, 116.74734370634701 23.126763477652446, 116.74934731222879 23.129955706690566, 116.765012733351 23.163859275485208, 116.68961559288539 23.209474021908648, 116.68829903037602 23.20687255122732, 116.67647718307558 23.190579412310058, 116.66752145085434 23.187214838993668))"
    }];

    var bmap = window.G.bmap;
    var layerOp = new wmap.wlayers();
    var layer = layerOp.getHWLayer(bmap);

    var geowkt, feautres = [];
    for(var i = 0; i < data.length; i++){
        var info = data[i];
        geowkt = info.geo_wkt;
        if(!geowkt) continue;

        var feature = new ol.format.WKT().readFeature(geowkt);
        feature.setProperties(info);
        feautres.push(feature);
    }
    layer.getSource().addFeatures(feautres);
}

//追加岸线要素
function appendAXFeatures(){
    var data = [{
        id: "5aab7d9397fad6d0447873",
        name:"诏安湾线",
        geo_wkt: "LINESTRING (116.98927257556898 23.633762965788264, 116.98999855059265 23.633887336631346, 116.99075559989103 23.633837546565587)"
    },{
        id: "5aab7e829397f6d0447893",
        name:"乌礁湾线",
        geo_wkt: "LINESTRING (116.96851922827977 23.577246860554226, 116.96902160576258 23.577528048880424, 116.96931340518961 23.577690738037177, 116.96970346184537 23.577677376809561, 116.97019479025744 23.57762213415424, 116.97124262184957 23.577462697845306, 116.97137399391477 23.577750270458694, 116.97200068378459 23.577592129173468, 116.97280685225451 23.577288109758513)"
    }];

    var bmap = window.G.bmap;
    var layerOp = new wmap.wlayers();
    var layer = layerOp.getAXLayer(bmap);

    var geowkt, feautres = [];
    for(var i = 0; i < data.length; i++){
        var info = data[i];
        geowkt = info.geo_wkt;
        if(!geowkt) continue;

        var feature = new ol.format.WKT().readFeature(geowkt);
        feature.setProperties(info);
        feautres.push(feature);
    }
    layer.getSource().addFeatures(feautres);
}

//添加弹出框
function appendPopup() {
    var map = window.G.bmap.getMap();
    var popup = map.getOverlayById("wzt_dlxx_1000");
    if(!popup){
        popup = new bxmap.overlay.Popup ({
            id: "wzt_dlxx_1000",
            showTitle: false,
            showClose: false,
            positioning: "bottom-center",
            panToCenter: false
        });
        map.addOverlay(popup);
    }
}

//鼠标点击要素
function featureClickEvent(evt) {
    var map = evt.map;
    if(map.getView().getResolution() > window.G.maxResolution) return;

    var bmap = this;
    var layerOp = new wmap.wlayers();
    var currentFeature = null, type = "";
    map.forEachFeatureAtPixel(evt.pixel, function(feature, layer){
        currentFeature = feature;

        if(layer == layerOp.getHWLayer(bmap)){
            type = "HW_Highlight";
        }else if(layer == layerOp.getAXLayer(bmap)){
            type = "AX_Highlight";
        }
    }, {
        layerFilter: function(layer){
            return layer == layerOp.getHWLayer(bmap) || layer == layerOp.getAXLayer(bmap);
        },
        hitTolerance: 4 //1像素，仅在canvas render中有效
    });

    if(currentFeature) {
        layerOp.highlightCopyFeature(bmap, type, currentFeature);
    }

    evt.originalEvent.stopImmediatePropagation();
    //显示气泡
    showPopup(currentFeature);
}

//显示气泡
function showPopup(feature) {
    if(!feature) return;

    var map = window.G.bmap.getMap();

    var geom = feature.getGeometry();
    var geoType = geom.getType();
    var coordinate = null;
    if("LineString" == geoType){
        coordinate = geom.getCoordinateAt(0.5);
    }else if("Polygon" == geoType){
        coordinate = geom.getInteriorPoint().getCoordinates();
    }

    var popup = map.getOverlayById("wzt_dlxx_1000");

    function detailClick() {
        var fid = popup.Fid;
        console.log(fid);
        window.location.href = "./hlxx.html";
    }

    //气泡弹框
    if(coordinate){
        //关联要素id
        popup.Fid = feature.get("id");
        popup.show(coordinate, "查看详细信息");

        var element = popup.popupElement.lastChild;
        element.removeEventListener("click", detailClick, false);
        element.addEventListener("click", detailClick, false);
    }
}