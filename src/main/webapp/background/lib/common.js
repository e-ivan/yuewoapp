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

//获取项目路径
function getMarketImgPath_dc() {
    return "http://www.myyuncang.com";
}

/**
 * 获取get请求参数
 * @param key
 */
function getQueryString(key) {
    var reg = new RegExp("(^|&)" + key + "=([^&]*)(&|$)", "i");
    var r = window.location.search.substr(1).match(reg);
    if (r != null) return unescape(r[2]);
    return null;
}

function encodeText(str) {
    return encodeURI(encodeURI(str).replace(/'/g, '&apos;'));
}

var tokenUserId = $.cookie("userId");
var roleType = $.cookie("roleType");
var username = $.cookie("username");
var accessToken = $.cookie("accessToken");

//测试登录
$(function () {
    var pathName = window.location.pathname.substring(1);
    var s = pathName.substring(pathName.lastIndexOf('/'));
    if (s.indexOf('login') >= 0) {
        return false;
    }
    $.ajax({
        url: getRootPath_dc() + "/common/checkLogin",
        type: "get",
        headers: {tokenUserId: tokenUserId, accessToken: accessToken},
        success: function (data) {
            if (data.errcode == 600) {
                layer.alert("登录失效，请重新登录！", function () {
                    window.location.href = "login.html";
                });
                return false;
            }
        }
    });
});


/**
 * 分页数据
 * @param data
 */
function pageList(data) {
    var pagelist = $('.pagelist');
    var page = data.page + 1;
    var pageNum = data.totalPage;
    pagelist.html(buildPages(page, pageNum));
    var emptyPage = pagelist.html();
    if (emptyPage == '' || emptyPage == null) {
        $('.prepage,.nextpage').css({display: 'none'});
    }
    else {
        var pageEle = pagelist.closest(".page");
        var limitEle = $(pageEle).find(".limit");
        limitEle.remove();
        $(pageEle).prepend(buildLimit(data.limit));
        $('.prepage,.nextpage').css({display: 'inline-block'});
    }
    pagelist.children().map(function (index, item) {
        item = $(item);
        item.removeClass('cur');
        if (item.html() == page.toString()) {
            item.addClass('cur');
        }
    });
}

function buildLimit(limit) {
    var html = "<select class='select-box inline limit'>" +
        "<option value='5'>5条/页</option>" +
        "<option value='10'>10条/页</option>" +
        "<option value='20'>20条/页</option>" +
        "<option value='50'>50条/页</option>" +
        "<option value='100'>100条/页</option>" +
        "</select>";
    var ele = $(html);
    ele.find("[value='" + limit + "']").attr("selected", "selected");
    return ele;
}
/**
 * 创建分页
 * @param page  当前页，0开始
 * @param pageNum   总页数
 * @returns {string}
 */
function buildPages(page, pageNum) {
    var pages = '';
    var showPageNum = 7;
    var spaceNum = parseInt((showPageNum - 3) / 2);
    if (pageNum <= showPageNum) {
        for (var i = 1; i < pageNum + 1; i++) {
            pages += "<a>" + i + "</a>";
        }
    } else if (page < showPageNum - 1) {
        for (var i = 1; i < showPageNum; i++) {
            pages += "<a>" + i + "</a>";
        }
        pages = pages + '...' + '<a>' + pageNum + '</a>';
    } else if (page >= pageNum - (showPageNum - 3)) {
        for (var i = pageNum - (showPageNum - 2); i <= pageNum; i++) {
            pages += "<a>" + i + "</a>";
        }
        pages = '<a>1</a>' + '...' + pages;
    } else {
        for (var i = page - spaceNum; i < page + 1 + spaceNum; i++) {
            pages += "<a>" + i + "</a>";
        }
        pages = '<a>1</a>' + '...' + pages + '...' + '<a>' + pageNum + '</a>';
    }
    return pages;
}
/**
 * 分页点击事件
 * @param data  分页数据
 * @param pubAjax   数据方法
 */
function clickEvents(data, pubAjax) {
    var page = data.page;
    var pageNum = data.totalPage;
    $(".nextpage,.prepage").removeClass("btn disabled");
    if (page == pageNum - 1) {
        $(".nextpage").addClass("btn disabled");
    }
    if (page == 0) {
        $(".prepage").addClass("btn disabled");
    }
    $(".nextpage,.prepage,.pagelist a").unbind("click");
    $('.nextpage').click(function () {
        if (page <= pageNum - 2) {
            page++;
            pubAjax(page);
        } else {
            return false;
        }
    });
    $('.prepage').click(function () {
        if (page >= 1) {
            page--;
            pubAjax(page);
        }
        else {
            return false;
        }
    });
    $('.pagelist a').click(function () {
        page = $(this).html() - 1;
        pubAjax(page);
    });
    $(".limit").change(function () {
        pubAjax(0);
    })
}

//设置订单自动评价
function setOrderEvaluate(orderId) {
    layer.confirm('是否该订单为<span style="color: red">默认评价</span>？', function (index) {
        $.ajax({
            type: 'post',
            url: getRootPath_dc() + '/evaluate/setOrderEvaluate',
            headers: {tokenUserId: tokenUserId, accessToken: accessToken},
            data: {orderId: orderId, auto: true},
            success: function (data) {
                layer.alert(data.msg, function (index) {
                    if (data.errcode == 0) {
                        $("#refresh").click();
                        var refresh = parent.$("#refresh");
                        $(refresh).click();
                    }
                    layer.close(index);
                });
            }
        });
        layer.close(index);
    });
}

//读取图片文件，并将图片的缓存放到目标
function readFileSrc(file, target) {
    //创建用来读取此文件的对象
    var reader = new FileReader();
    //使用该对象读取file文件
    reader.readAsDataURL(file);
    //读取文件成功后执行的方法函数
    reader.onload = function (e) {
        //读取成功后返回的一个参数e，整个的一个进度事件
        //选择所要显示图片的img，要赋值给img的src就是e中target下result里面
        //的base64编码格式的地址
        target.src = e.target.result;
    };
}

//高亮点击行
function clickLine(event, style) {
    event = $(event);
    event.parent().children().removeClass(style);
    event.addClass(style);
}
//截取文本显示
function substringText(str, len) {
    if (isNull(str)) return '';
    if (isNull(len)) len = 20;
    var content = str.replace(/\r\n/g, "</br>");
    content = content.replace(/\n/g, "</br>");
    var reg = /^([hH][tT]{2}[pP]:\/\/|[hH][tT]{2}[pP][sS]:\/\/).*$/;
    var aAttribute;
    if (reg.test(str.trim())) {//网页
        aAttribute = ' href="' + str + '" target="_blank" title="打开链接" ';
    }else {
        aAttribute = 'onclick="layer.open({type: 1,skin: \'layui-layer-lan\',title:\'内容\',closeBtn: 0,anim: 2,shadeClose: true, content: \'' + content + '\'})"';
    }
    return str != null && str.length > len ?
    '<a style="display: block" ' + aAttribute + ' >'
    + str.substring(0, len) + ' ...</a>' :
        str;
}

function addTableNoDateHtml(length) {
    if (length <= 0) {
        return "<tr class='text-c .table-hover'><td colspan='20' style='text-align: center;color: grey'>暂无数据</td></tr>";
    }
}


function unixTodateWithSecond(timestamp) {
    return dateFormatUtil(timestamp * 1000);
}

function dateFormatUtil(timestamp, onlyDay) {
    if (typeof timestamp == 'string') {
        timestamp = parseInt(timestamp);
    }
    var date = new Date(timestamp);    //根据时间戳生成的时间对象
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
    var dateTypeDate = fullYear + seperator1 + month + seperator1 + strDate;
    if (!onlyDay) {
        dateTypeDate += " " + hours + seperator2 + (minutes <= 9 ? "0" + minutes : minutes)
            + seperator2 + (seconds <= 9 ? "0" + seconds : seconds);
    }
    return dateTypeDate;
}

//字符串转Date
function convertDateFromString(dateString) {
    if (dateString) {
        var date = new Date(dateString.replace(/-/, "/"));
        return date;
    }
}

/**
 * 向tr添加审核样式
 */
function addTdAuditHtml(data) {
    var html = "";
    if (data.auditorId) {
        html += "<td><a href='javascript:layer_show(\"用户信息\",\"user-info-detail.html?userId=" + data.auditorId + "\")'>" + data.auditorId + "</a></td>" +
            "<td>" + substringText(data.remark, 15) + "</td>";
    } else {
        html += "<td></td><td></td>"
    }
    if (data.status == 10) {
        html += "<td class='td-status'><span class='label radius label-primary'>待审核</span></td>";
    } else if (data.status == 1000) {
        html += "<td class='td-status'><span class='label radius label-danger'>拒绝</span></td>";
    } else {
        html += "<td class='td-status'><span class='label radius label-success'>通过</span></td>";
    }
    return html;
}

/**
 * 解析支付方式
 */
function parsePayType(value) {
    var html = "";
    switch (value) {
        case 'alipay' :
            html += "<span class='label radius label-primary'>支付宝</span>";
            break;
        case 'wxpay' :
            html += "<span class='label radius label-success'>微信</span>";
            break;
        case 'balance' :
            html += "<span class='label radius label-secondary'>余额</span>";
            break;
        case 'coupon' :
            html += "<span class='label radius label-danger'>优惠券</span>";
            break;
        case 'sale' :
            html += "<span class='label radius label-warning'>活动</span>";
            break;
        default :
            html += "<span class='label radius label-default'>未知</span>";
            break;
    }
    return html;
}

//判断字符串是否为空
function isNull(str) {
    if (str == null || str == "" || str == 'null') return true;
    var regu = "^[ ]+$";
    var re = new RegExp(regu);
    return re.test(str);
}

function viewerOption(addOption) {
    var option = {
        url: function () {
            var src = $(this).attr("src");
            var lastIndexOf = src.lastIndexOf("_");
            if (lastIndexOf >= 0) {
                var point = src.lastIndexOf(".");
                //获取图片后缀
                var suffix = src.substring(point);
                var pre = src.substring(0, lastIndexOf);
                return pre + suffix;
            }
        }
    };
    for (var i in addOption) {
        option[i] = addOption[i];
    }
    return option;
}