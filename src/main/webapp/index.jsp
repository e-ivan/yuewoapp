<html>
<head>
    <script type="text/javascript" src="jquery-2.2.3.min.js"></script>
</head>
<title>ni hao</title>
<body id="bodybody">

</body>
</html>

<script type="text/javascript">


    $(function (){
        jQuery.ajax({
            url: '/qnyy/filmorder/queryMyOrderList',
            type: "post",
            dataType: "json",
            data:{orderId:"149673652903795936",status:"1",page:"0",limit:"2"},
            success: function (data) {
                var myobj=eval(data);
                if(myobj.errcode== 0){
                    $("#bodybody").append(myobj.msg);
                }
            }

        });
    });


</script>
