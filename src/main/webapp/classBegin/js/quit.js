  $(function(){
         $('.quit').click(function() {

         	if(confirm("确认要退出登录吗")){
         var userId = $.cookie("userId",'');
         var userName = $.cookie("userName",'');  
          window.location.href="login.html";    
          }                  
      });
 })

