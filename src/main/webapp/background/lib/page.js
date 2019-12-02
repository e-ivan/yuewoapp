// 点击添加商品属性
$(function(){
  var userId = $.cookie("userId");
  var roleType = $.cookie("roleType");
  var username = $.cookie("username");
  if(userId==""||userId==null){
    parent.location.href="login.html"
    return false;
  }
})
function pubAjax(page){
    var id=$.cookie("userId");
    var accessToken=$.cookie("accessToken");
    $.ajax({
       url: '/qnyy/crew/queryCrewList',
       type: "post",
       dataType: "json",
       headers:{tokenUserId:id,accessToken:accessToken},
       data:{page:page,limit:limit},
          beforeSend: function () {
               $("#load").css("display","block");
          },
          success: function (data) {
           console.log(data);
           if (data.errcode == 0) {
              $("#mail-list").children().remove();
              var dataa = eval(data.data.iData);
              $("#num11").html(data.data.totalCount)
              list(data)
              var myobj=eval(data); 
              pageList(myobj);
              clickEvents(myobj);
           }else{
              return false;
           }
          
         },
        complete: function () {
            $("#load").css("display","none");
        },              
    });  
} 
function list(data){
       var tableDom = $('#mail-list');
       var html = "<tr class='text-c'><th width='50'>序号</th><th style='text-align: left'>剧组名称</th><th width='100'>类型</th><th width='80'>时间</th><th width='120'>地点</th><th width='75'>上映</th><th width='100'>开机时间</th><th width='100'>招募开始</th><th width='80'>招募结束</th><th width='150'>操作</th></tr>";  
       var dataa = eval(data.data.iData);                 
        for(var i=0;i<dataa.length;i++){
              html += "<tr class='text-c .table-hover' id='"+dataa[i].id+"'>"+
              "<td>"+ parseInt(i+1) +"</td>"+
              "<td class='text-l'>"+dataa[i].name+"</td>"+
              "<td>"+dataa[i].type+"</td>"+
              "<td>"+dataa[i].shootingCycle+"</td>"+
              "<td>"+dataa[i].shootingLocation+"</td>"+
              "<td>"+dataa[i].publishPlatform+"</td>"+
              "<td>"+dataa[i].openTime+"</td>"+
              "<td>"+dataa[i].startRecruitTime+"</td>"+
              "<td>"+dataa[i].endRecruitTime+"</td>"+
              "<td class='f-14 td-manage'>"+
              "<a style='text-decoration:none' class='ml-5' title='添加招聘' href='store-add.html?id="+dataa[i].id+"' href='javascript:;'><i class='Hui-iconfont'> &#xe603;</i></a>"+
              "<a style='text-decoration:none' class='ml-5' title='招聘列表' href='recruit.html?id="+dataa[i].id+"' href='javascript:;'><i class='Hui-iconfont'> &#xe667;</i></a>"+
              "<a style='text-decoration:none' class='ml-5' title='删除' "+dataa[i].id+" onclick='del(\""+dataa[i].id+"\")'><i class='Hui-iconfont'> &#xe609;</i></a>"+
              "<a style='text-decoration:none' class='ml-5' title='剧组详情' href='mailstore-detail.html?id="+dataa[i].id+"' href='javascript:;'><i class='Hui-iconfont'> &#xe636;</i></a>"+
            "</tr>";
        }
       $("#mail-list").append(html);
    }
function pageList(myobj){ 
   var totalCount = myobj.data.totalCount;
   var pagelist=$('.pagelist');
   var limit = myobj.data.limit;                  
   var page = myobj.data.page;
   var pageNum = Math.ceil(totalCount/limit) ;
   var pages = '';
   for (var i = 1; i < pageNum+1; i++) {
      pages +=  "<a>" + i + "</a>";
   }      
   pagelist.html(pages);
   var emptyPage = pagelist.html();
   if(emptyPage==''|| emptyPage==null){
       $('.prepage,.nextpage').css({display: 'none'});
   }
   else{
    $('.prepage,.nextpage').css({display: 'inline-block'});
   }
   pagelist.children().eq(page).addClass('cur').siblings().removeClass('cur');
}

function clickEvents(myobj){
    var totalCount = myobj.data.totalCount;
    var limit = myobj.data.limit;         
    var page = myobj.data.page;
    var pageNum = Math.ceil(totalCount/limit) ;
    $(".nextpage,.prepage,.pagelist a").unbind("click");                   
    $('.nextpage').click(function() {
         if (page<= pageNum-2) {
           page++;
           pubAjax(page); 
         } 
         else{
            return false;
         } 
     });
    $('.prepage').click(function() {
     if (page>= 1) {
         page--;
         pubAjax(page);  
       }
       else{
         return false;
       }
    });
   $('.pagelist a').click(function() {
     page =$(this).html()-1;
     pubAjax(page);
   }); 
}
function del(cid){
    var id=$.cookie("userId");
    var accessToken=$.cookie("accessToken");
    $.ajax({
        url:getRootPath_dc() + "/crew/deleteCrewById",
        type:"post",
        dataType:"json",
        headers:{tokenUserId:id,accessToken:accessToken},
        data:{crewId:cid},
        success:function(data){
            if (confirm("您确定要删除吗")) {
                $("tr#"+cid).remove();
            }
        }
    })
}
// json时间转化
function dateFormatUtil(longTypeDate){  
     var dateTypeDate = "";  
     var date = new Date(longTypeDate);  
     dateTypeDate += date.getFullYear();   //年  
     dateTypeDate += "-" + (date.getMonth()+1); //月   
     dateTypeDate += "-" + date.getDate();  //日  
     dateTypeDate += '&nbsp;'
     dateTypeDate += date.getHours();
     dateTypeDate += ":"+date.getMinutes();
     dateTypeDate += ":"+date.getSeconds();
     return dateTypeDate;  
}    