$(function(){
     $('.aside-nav dt').click(function(){
	      $(this).toggleClass('jian').parent().siblings().find('dt').removeClass('jian');
	      $(this).next("dd").toggle(300);
          //$(this).next("dd").children().find("li:first").addClass("select").siblings().removeClass("select");
	      $(this).parent().siblings().find('dd').hide(300);
     })
     $(".aside-nav dd li").click(function(){
     	$(this).addClass("select").siblings().removeClass("select");
     	$(this).parent().parent().parent().siblings().find("li").removeClass("select");
     })
})
  //左侧栏js
