$(function () {


    var _arrToTree = function (arrData, pid) {
        var result = [], temp;
        for (var i = 0; i < arrData.length; i++) {
            if (arrData[i].pid == pid) {
                var obj = {
                    "name": arrData[i].name,
                    "id": arrData[i].id,
                    "wbCnt": arrData[i].wbCnt,
                    "ybCnt": arrData[i].ybCnt
                };
                temp = _arrToTree(arrData, arrData[i].id);
                if (temp.length > 0) {
                    obj.children = temp;
                }
                result.push(obj);
            }
        }
        return result;
    };


    var _renderHtml = function (treeDatas) {
        var _html = "";
        for (var i = 0; i < treeDatas.length; i++) {
            var treeObj = treeDatas[i];
            _html += '<li>' + treeObj.name;
            if (treeObj.children && treeObj.children.length > 0) {//存在子节点
                _html += '<ul class="d-firstDrop s-firstDrop">' + _renderHtml(treeObj.children) + '</ul> ';
            }
            _html += "</li>";
        }
        return _html;
    };


    var $body = $(document.body);
    $body.on("change", ".area-select", function (e) {
        var selVal = $(this).val();
        var treeNodesUrl = "",
            cntUrl = "";
        if (selVal === "1") {//区域
            treeNodesUrl = GLOBAL.domainRest + "/sys/mgr/org/all";
            cntUrl = GLOBAL.domainRest + "/wzt/event/tjByqy";
        } else {//湾区
            treeNodesUrl = GLOBAL.domainRest + "/manage/bay/all";
            cntUrl = GLOBAL.domainRest + "/wzt/event/tjBywq";
        }
        //获取
        new Promise(function (resolve, reject) {//查出所有区域/湾区
            $.post(treeNodesUrl, {}, function (data) {
                resolve(data);
            });
        }).then(function (nodes) {//获取区域/湾区的统计数据
            return new Promise(function (resolve, reject) {
                $.post(cntUrl, {}, function (cntData) {
                    console.log(cntData);
                    for (var i = 0; i < nodes.length; i++) {//设置统计属性，供展示
                        var _node = nodes[i];
                        var _id = _node.id;
                        var cntObj = cntData[_id];
                        if (cntObj) {
                            _node.wbCnt = cntObj.wbCnt ? cntObj.wbCnt : 0;//未办
                            _node.ybCnt = cntObj.ybCnt ? cntObj.ybCnt : 0;//已办
                        } else {
                            _node.wbCnt = 0;//未办
                            _node.ybCnt = 0;//已办
                        }
                    }
                    resolve(nodes);
                });
            });
        }).then(function (nodesWithCnt) {//根据统计的树数据生成页面
            var treeDatas = _arrToTree(nodesWithCnt, "root");
            // var treeObj = treeDatas[0];
            // console.log(treeObj);
            var _html = _renderHtml(treeDatas);
            console.log(_html);
            //todo 等待页面支持5级


        });

    });

    //初始化触发
    $body.find(".area-select").change();


});