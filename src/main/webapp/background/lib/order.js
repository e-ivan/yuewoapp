
function pubAjax(page,status){
    var id=$.cookie("userId");
    $.ajax({
       url: '/skl/order/queryOrderListByStoreId',
       type: "post",
       dataType: "json",
       data:{page:page,limit:limit,storeId:id,status:status},
          beforeSend: function () {
               $("#load").css("display","block");
          },
          success: function (data) {
           console.log(data);
            $("#mail-list").children().remove();
            var dataa = eval(data.data.iData);
            $("#num11").html(data.data.totalCount)
                for(var i=0;i<dataa.length;i++){
                  var baseid=dataa[i].storeGoodBaseAttrs
                       var html = "<tr class='text-c .table-hover' id='"+dataa[i].id+"'>"+
                      "<td>"+ parseInt(i+1) +"</td>"+
                      "<td class='text-l'><u style='cursor:pointer' class='text-primary'>"+ dataa[i].receiveName +"</u></td>"+
                      "<td>"+dataa[i].receivePhone+"</td>"+
                      "<td>"+dateFormatUtil(dataa[i].createTime)+"</td>"+
                      "<td>"+dataa[i].payType+"</td>"+
                      "<td >"+dataa[i].payMoney+"</td>"+
                      "<td >"+dataa[i].receiveAddress+"</td>"+
                      "<td class='f-14 td-manage'>"+
                      //"<a style='text-decoration:none' class='ml-5' title='删除'  href='javascript:;' onclick='dela(\""+dataa[i].id+"\")'><i class='Hui-iconfont'> &#xe6e2;</i></a>"+
                      "<a style='text-decoration:none' class='ml-5' title='详情' href='order-detail.html?id="+dataa[i].id+"' href='javascript:;'><i class='Hui-iconfont'> &#xe637;</i></a>"+
                    "</tr>";
                     $("#mail-list").append(html);
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
function dela(id){
   if(confirm("您确定删除订单？")){
      $.ajax({
         url:"/skl/order/deleteOrder",
         type:"post",
         dataType:"json",
         data:{orderId:id},
         success:function(data){
            $("tr#"+id).remove();
            location.reload();
         }
      })
   }
}
