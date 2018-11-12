 $(function (){
      $('.save').click(function(event) {
        var id=$.cookie("userId");
	    var accessToken=$.cookie("accessToken");
        var content = $('.froala-element').html();
        var neirong = $('.froala-element p').html();
      if (neirong==''||neirong=='<br>') {
           alert('请添加内容');
        }
        else{
          jQuery.ajax({
            url: '/qnyy/user/addAgreement',
            type: "post",
            dataType: "json",
            headers:{tokenUserId:id,accessToken:accessToken},
            data:{content:content},
            success: function (data) {
              if (data.errcode==0) {
                 alert(data.msg);
                 window.location.href='xy-list.html'
              }
              else{
                alert(data.msg);
              }
            }              
           });
          function addpic(){
             var $list=$("#thelist"); 
             var $btn =$("#ctlBtn");  
             var thumbnailWidth = 80;    
             var thumbnailHeight = 80;  
             var fkPurposeId = $('#fkPurposeId').val();
             var uploader = WebUploader.create({  
                 auto: false,  
                 swf: 'uploadify/Uploader.swf',  
                 server:prefixion+ '/qnyy/file/uploadFile',  
                 pick:'#filePicker',
                 fileVal:'uploadFile', 
                 fileNumLimit:1,
                 formData: {'fileType':'image','filePurpose':'imageBannerPhotoHead','fkPurposeId':fkPurposeId},
                 accept: {  
                     title: 'Images',  
                     extensions: 'gif,jpg,jpeg,png',  
                     mimeTypes: 'image/jpg,image/jpggifimage/jpeg,image/png,'  
                 },  
                 method:'POST',  
             });
             // 当有文件添加进来的时候  
             uploader.on( 'fileQueued', function( file ) { 
                var $li = $(  
                         '<span id="' + file.id + '" class="file-item thumbnail" style="float:left; width: 80px; position: relative; margin-right: 10px;">' +  
                             '<img>' +  
                         '</span>'  
                         ),  
                  $del = $('<div class="file-panel"><span class="cancel"><img src=images/chu.png style="position:absolute; right:-5px; top:-8px; height:14px; width:14px; cursor:pointer;"/></span></div>')
                 $img = $li.find('img');  
                 $list.html( $li );  
                 $del.appendTo($li);
                 uploader.makeThumb( file, function( error, src ) {   //webuploader方法  
                     if ( error ) {  
                         $img.replaceWith('<span>不能预览</span>');  
                         return;  
                     }  
            
                     $img.attr( 'src', src );  
                 }, thumbnailWidth, thumbnailHeight );  

                 $del.on('click', 'span', function() {
                 removeFile(file);
                  });
             });

            function removeFile(file) {
                var $li = $('#' + file.id);
                uploader.removeFile(file, true);
                $li.off().find('.file-panel').off().end().remove();
                }
             // 文件上传成功，给item添加成功class, 用样式标记上传成功。  
             uploader.on( 'uploadSuccess', function( file ) {  
                 alert('图片上传成功');
                 window.location.href='advlist.html';
             });
             // 文件上传失败，显示上传出错。  
             uploader.on( 'uploadError', function( file ) {  
                 var $li = $( '#'+file.id ),  
                     $error = $li.find('div.error');  
                 // 避免重复创建  
                 if ( !$error.length ) {  
                     $error = $('<div class="error"></div>').appendTo( $li );  
                 }  
            
                 $error.text('上传失败');  
             });  
           
                $btn.on( 'click', function() {  
                  uploader.upload(); 
                });   
             }
            
          }
      });  
}); 