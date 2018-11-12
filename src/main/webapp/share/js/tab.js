$(function(){
    $('.p-detail h2 a ').click(function(){
        $(this).addClass('checked').siblings().removeClass('checked');
        $('.d-list>div:eq('+$(this).index()+')').show().siblings().hide();
    })

})//商品详情切换




$(function(){
    $('.fenlei li ').click(function(){
        $(this).addClass('current').siblings().removeClass('current');

    })
})//商品属性切换

$(function(){
    $(".add").click(function(){
        var t=$(this).parent().find('.text_box');
        t.val(parseInt(t.val())+1);
        var kc=parseInt( $(".kucun").text());
        if(parseInt(t.val())> kc){
            t.val(kc);
            alert("购买数量不能大于库存");
        }
    })
    $(".reduce").click(function(){
        var t=$(this).parent().find('.text_box');
        t.val(parseInt(t.val())-1);
        if(parseInt(t.val())<1){
            t.val(1);
            alert("购买数量必须大于或等于1");
        }
    })
})//商品数量

$(function(){
    $('.buy a ').click(function(){
        $('.property').css( "display","block");
        $('#bg').css( "display","block");
    })
    $('.close ').click(function(){
        $('.property').css( "display","none");
        $('#bg').css( "display","none");
    })
})//立即购买

$(function () {
    $(".sure a").click(function () {
        $('.property').css( "display","none");
        $('#bg').css( "display","none");
        $('#w-bg').css( "display","block");
        $('.succeed').css({
            //设置弹出层距离左边的位置
            left: ($("body").width() -  $('.succeed').width()) / 2  + "px",
            //设置弹出层距离上面的位置
            top: ($(window).height() - $('.succeed').height()) / 3 + $(window).scrollTop() + "px",
            display: "block"

        });
        $('#w-bg').click(function(){
            $('#w-bg').css( "display","none");
            $('.succeed').css( "display","none");
        })
    });
});//添加购物车成功