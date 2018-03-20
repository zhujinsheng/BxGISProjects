var userData;
$.ajax({
    url: "http://localhost:18082/sys/auth/login",
    type: "POST",
    async: false,
    dataType: "json",
    data: {'name': "admin", "password": "123456"},
    success: function (data) {
        console.log(data);
        userData = data;
    }
});
//统一授权
$.ajaxSetup({
    global: true,
    beforeSend: function (jqXHR, settings) {
        console.log(settings);
        if(settings.url && settings.url.indexOf("=")>0){
            settings.url = settings.url + "&_sso_token=" + userData.sso_token;
        }else{
            settings.url = settings.url + "?_sso_token=" + userData.sso_token;
        }
    }
});


$(function getRem() {

    //页面大小
    var width = screen.width;
    var remSize = (width * 100) / 750;
    $('html').css('font-size', remSize);
});
