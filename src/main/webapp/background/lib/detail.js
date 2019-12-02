$(function(){
	// 商品详情
	var crewId=getQueryString("id")
	var id=$.cookie("userId");
    var accessToken=$.cookie("accessToken");
	$.ajax({
	   url: '/qnyy/crew/queryCrewById',
       type: "post",
       dataType: "json",
       headers:{tokenUserId:id,accessToken:accessToken},
       data:{crewId:crewId},
       success:function(data){
       		console.log(data)
       		var status=data.data.status;
       		$("#sstatus1").val(status);
       		$("#d_name").val(data.data.type);
       		$("#d_fl").val(data.data.name);
       		$("#d_type").val(data.data.genre);
       		$("#d_move").val(data.data.publishPlatform);
       		$("#d_time").val(data.data.openTime);
       		$("#d_location").val(data.data.shootingLocation);
       		$("#d_long").val(data.data.shootingCycle);
       		$("#d_start").val(data.data.startRecruitTime);
       		$("#d_end").val(data.data.endRecruitTime);
       		if (data.data.backPhoto==null) {
       			return false;
       		}else{
       			$("#lunbo").html("<img src='/qnyy/"+data.data.backPhoto+"'>")
       		}
       }
	})
})
function getQueryString(id) { 
    var reg = new RegExp("(^|&)" + id + "=([^&]*)(&|$)", "i"); 
    var r = window.location.search.substr(1).match(reg); 
    if (r != null) return unescape(r[2]); return null; 
} 
// jsonÊ±¼ä×ª»¯
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