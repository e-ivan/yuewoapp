//调用navDown时间
//注意引用style.css样式
function navDown() {
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
    var theUrl = '';
    if (Terminal.platform.android) {
        theUrl = 'http://a.app.qq.com/o/simple.jsp?pkgname=com.qnyy.yueme&from=singlemessage';
    } else if (Terminal.platform.iPhone) {
        theUrl = 'https://itunes.apple.com/cn/app/%E7%BA%A6%E6%88%91%E7%9C%8B%E7%94%B5%E5%BD%B1/id1256243849?mt=8';
    } else if (Terminal.platform.iPad) {
        theUrl = 'https://itunes.apple.com/cn/app/%E7%BA%A6%E6%88%91%E7%9C%8B%E7%94%B5%E5%BD%B1/id1256243849?mt=8';
    }


    var divHeight = document.createElement("div");
    divHeight.style.height = "50px";
    var divDown = document.createElement("div");
    divDown.className = "leaveMessage";
    divDown.id = "leaveMessage";
    var down = "";
    down += '	<div class="logo">';
    down += '		<div class="img"></div>';
    down += '	</div>';
    down += '	<div class="left">';
    down += '		<p>约我</p>';
    down += '		<p>不只是看电影！</p>';
    down += '	</div>';
    down += '	<div class="right">';
    down += '		<a href="' + theUrl + '" class="download">下载</a>';
    down += '	</div>';
    divDown.innerHTML = down;
    document.body.appendChild(divHeight);
    document.body.appendChild(divDown);
}

