/**
 * 系统判断js文件
**/
 window.onload = function hrefData(){
	var browser = {
		versions: function() {
			var u = navigator.userAgent,
			app = navigator.appVersion;

			return {
				android: u.indexOf('Android') > -1 || u.indexOf('Linux') > -1,
				iPhone: u.indexOf('iPhone') > -1 ,
				iPad: u.indexOf('iPad') > -1,
				iPod: u.indexOf('iPod') > -1,
				MicroMessenger: u.indexOf('MicroMessenger') > -1,
			};

		} (),

		language: (navigator.browserLanguage || navigator.language).toLowerCase()
	}

	if (browser.versions.iPhone||browser.versions.iPad||browser.versions.iPod)
	{
		//如果是ios系統，直接跳轉至appstore該應用首頁，傳遞参數为該應用在appstroe的id號
		//window.location.href="https://itunes.apple.com/cn/app/quan-qiu-ti-yu/id1079328958?mt=8";
		//window.location.href="https://itunes.apple.com/cn/app/quan-qiu-ti-yu/id1144065751?mt=8";
		//window.location.href="https://www.baidu.com/";
		var loadDateTime = new Date();
		window.setTimeout(function() {
		 var timeOutDateTime = new Date();
		 if (timeOutDateTime - loadDateTime < 500) {
		  window.location = "http://www.skbpt.com/share/share.html";
		 } else {
		  window.close();
		 }
		},
		25);
		window.location = "skb://skbapp?type='"+type+"'&id='"+id+"'";
	}
 

	if (browser.versions.MicroMessenger)
	{
		//如果是微信系統
		//window.location.href="http://a.app.qq.com/o/simple.jsp?pkgname=cn.qqw.app";
		window.location.href="http://www.skbpt.com/share/share.html";
	}
	else if(browser.versions.android) 
	{
		//如果是安卓系統
		//window.location.href="http://a.app.qq.com/o/simple.jsp?pkgname=cn.qqw.app";
		//window.location.href="http://www.hzlongke.com/";
		var state = null;
		try {
		 state = window.open("skb://skbapp?type='"+type+"'&id='"+id+"'", '_blank');
		} catch(e) {}
		if (state) {
		 window.close();
		} else {
		 window.location = "http://www.skbpt.com/share/share.html";
		}
	};
}
