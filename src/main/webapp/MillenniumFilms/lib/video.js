function pubAjax(page){
    //var id=$.cookie("userId")
    var id=$.cookie("userId");
    console.log(id)
    $.ajax({
       url: '/skl/videoCourse/queryVideoCourseForVideoStore',
       type: "post",
       dataType: "json",
       data:{page:page,limit:limit,videoStoreId:id},
          beforeSend: function () {
               $("#load").css("display","block");
          },
          success: function (data) {
           console.log(data);
            $("#video-list").children().remove();
            var dataa = eval(data.data.iData);
            $("#num11").html(data.data.totalCount)
                for(var i=0;i<dataa.length;i++){
                  var baseid=dataa[i].storeGoodBaseAttrs
                       var html = "<tr class='text-c .table-hover' id='"+dataa[i].id+"'>"+
                      "<td>"+ parseInt(i+1) +"</td>"+
                      "<td class='text-l'><u style='cursor:pointer' class='text-primary'>"+ dataa[i].name +"</u></td>"+
                      "<td>"+dataa[i].intro+"</td>"+
                      "<td>"+dataa[i].fitPeople+"</td>"+
                      "<td>"+dataa[i].orgName+"</td>"+
                      "<td>"+dataa[i].orgIntro+"</td>"+
                      "<td>"+dataa[i].teacherName+"</td>"+
                      "<td>"+dataa[i].teacherIntro+"</td>"+
                      "<td>"+dataa[i].price+"</td>"+
                      "<td>"+dataa[i].sellCount+"</td>"+
                      "<td>"+dataa[i].avgScore+"</td>"+
                      "<td>"+dateFormatUtil(dataa[i].createTime)+"</td>"+
                      //"<td class='td-status'><span class='label label-success radius fabu'>上架</span><em></em></td>"+
                      "<td class='f-14 td-manage'>"+
                      "<a style='text-decoration:none' class='ml-5' title='订单' href='member-record-browse.html?id="+dataa[i].id+"' href='javascript:;'><i class='Hui-iconfont'> &#xe627;</i></a>"+
                      "<a style='text-decoration:none' class='ml-5' title='详情' href='member-del-detail.html?id="+dataa[i].id+"' href='javascript:;'><i class='Hui-iconfont'> &#xe685;</i></a></td>"+
                    "</tr>";
                     $("#video-list").append(html);
                }
          var myobj=eval(data); 
          pageList(myobj);
          clickEvents(myobj);
         },
        complete: function () {
            $("#load").css("display","none");
        },              
    });  
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
function getQueryString(id) { 
    var reg = new RegExp("(^|&)" + id + "=([^&]*)(&|$)", "i"); 
    var r = window.location.search.substr(1).match(reg); 
    if (r != null) return unescape(r[2]); return null; 
} 