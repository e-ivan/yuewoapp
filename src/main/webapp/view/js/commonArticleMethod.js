/**
 * Created by hh on 2017.8.5 0005.
 */
//获取url的请求参数
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
//获取项目路径
function getRootPath_dc() {
    var pathName = window.location.pathname.substring(1);
    var webName = pathName == '' ? '' : pathName.substring(0, pathName.indexOf('/'));
    if (webName == "") {
        return window.location.protocol + '//' + window.location.host;
    }
    else {
        return window.location.protocol + '//' + window.location.host + '/' + webName;
    }
}
//日期处理
function formatDate(date) {
    var seperator1 = "-";
    var seperator2 = ":";
    var month = date.getMonth() + 1;
    var strDate = date.getDate();
    if (month >= 1 && month <= 9) {
        month = "0" + month;
    }
    if (strDate >= 0 && strDate <= 9) {
        strDate = "0" + strDate;
    }
    var fullYear = date.getFullYear();
    var hours = date.getHours();
    var minutes = date.getMinutes();
    var seconds = date.getSeconds();
    var currentdate = fullYear + seperator1 + month + seperator1 + strDate
        + " " + hours + seperator2 + (minutes <= 9 ? "0" + minutes : minutes)
        + seperator2 + (seconds <= 9 ? "0" + seconds : seconds);
    return currentdate;
}
//评论处理
function handleComments(comment,showReply) {
    var commentId = comment["id"];
    console.log("生成评论");
    var li_list = '';
    li_list += '<li class="clearfix">';
    li_list += '		<div class="list-left">';
    li_list += '			<div class="active">';
    li_list += '				<img src="' + (comment["userPhotoHead"] ? comment["userPhotoHead"] : "img/me.png") + '"/>';
    li_list += '			</div>';
    li_list += '		</div>';
    li_list += '		<div class="list-right">';
    li_list += '			<div class="name">' + comment["author"] + '</div>';
    li_list += '			<div class="id" hidden>' + commentId + '</div>';
    li_list += '			<div class="details">' + comment["text"] + '</div>';
    li_list += '			<div class="time">' + formatDate(new Date(comment["created"])) + '</div>';
    li_list += '			<div class="fun">';
    if (showReply){
        li_list += '				<span class="reply">回复</span>';
        li_list += '				<span class="like">喜欢</span>';
    }
    li_list += '			</div>';
    li_list += '		</div>';
    if (showReply && comment["commentCount"] > 0) {//有人评论
        li_list += '<div class="reply-message clearfix">';
        li_list += '	<b class="top"><i class="top-arrow1"></i><i class="top-arrow2"></i></b>';
        li_list += '	<ul class="reply-message-ul">';
        $.ajax({
            url: '/qnyy/article/queryCommentsInComment',
            type: "post",
            async: false,
            dataType: "json",
            data: {commentId: commentId, orderBy: 'timeDown', page: 0, limit: 3},
            success: function (data) {
                if (data.errcode == 0) {
                    var comments = data.data.iData;
                    //显示留言
                    $.each(comments, function (index, replyComment) {
                        li_list += handleReplyComments(replyComment);
                    });
                }
            }
        });
        li_list += '</ul>';
        li_list += '</div>';
    }
    li_list += '	</li>';
    return li_list;
}
function handleReplyComments(replyComment) {
    var li_list = '';
    li_list += '<li class="reply-message-li">';
    li_list += '	<div class="reply-message-top clearfix">';
    li_list += '		<div>' + replyComment["author"] + '</div>';
    li_list += '		<div class="time">' + formatDate(new Date(replyComment["created"])) + '</div>';
    li_list += '	</div>';
    li_list += '    <div class="reply-message-content">' + replyComment["text"] + '</div>';
    li_list += '</li>';
    return li_list;
}