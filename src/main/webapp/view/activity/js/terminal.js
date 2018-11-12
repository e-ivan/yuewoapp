/**
 * Created by hh on 2017.8.23 0023.
 */
function terminalCheck(androidObj,iPhoneObj,iPadObj) {
    // 获取终端的相关信息
    var Terminal = {
        // 辨别移动终端类型
        platform: function () {
            var u = navigator.userAgent, app = navigator.appVersion;
            return {
                // android终端或者uc浏览器
                android: u.indexOf('Android') > -1 || u.indexOf('Linux') > -1,
                // 是否为iPhone或者QQHD浏览器
                iPhone: u.indexOf('iPhone') > -1,
                // 是否iPad
                iPad: u.indexOf('iPad') > -1
            };
        }(),
        // 辨别移动终端的语言：zh-cn、en-us、ko-kr、ja-jp...
        language: (navigator.browserLanguage || navigator.language).toLowerCase()
    };
    // 根据不同的终端，跳转到不同的地址
    var theUrl = "";
    if (Terminal.platform.android) {
        theUrl = androidObj;
    } else if (Terminal.platform.iPhone) {
        theUrl = iPhoneObj;//向IOS输出参数
    } else if (Terminal.platform.iPad) {
        theUrl = iPadObj;//向IOS输出参数
    }
    return theUrl;
}

function getRequest() {
    var url = location.search; //获取url中"?"符后的字串
    var theRequest = new Object();
    if (url.indexOf("?") != -1) {
        var str = url.substr(1);
        strs = str.split("&");
        for (var i = 0; i < strs.length; i++) {
            theRequest[strs[i].split("=")[0]] = unescape(strs[i].split("=")[1]);
        }
    }
    return theRequest;
}
function postStr(movieId){
    var request = getRequest();
    var share = request['share'];
    var url;
    if (share){
        url = terminalCheck('http://a.app.qq.com/o/simple.jsp?pkgname=com.qnyy.yueme&from=singlemessage',
            'https://itunes.apple.com/cn/app/%E7%BA%A6%E6%88%91%E7%9C%8B%E7%94%B5%E5%BD%B1/id1256243849?mt=8',
            'https://itunes.apple.com/cn/app/%E7%BA%A6%E6%88%91%E7%9C%8B%E7%94%B5%E5%BD%B1/id1256243849?mt=8')
    }else {
        url = "rrcc://clickEvent?type=";
        if (movieId && movieId != 'null') {
            url += "0&movieId=" + movieId;
        }else {
            url += "1";
        }
    }
    location.href = url;
}
