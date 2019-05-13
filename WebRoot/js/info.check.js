$(function(){ 
//文本框失去焦点后
$('.container form input').blur(function(){
	var $parent = $(this).parent();
    $parent.find(".formtips").remove();
     //验证单位名称
     if( $(this).attr('id')=='comanyName' ){
            if( this.value=="" ){
                var errorMsg = '请输入单位名称.';
                $parent.append('<span class="formtips onError">'+errorMsg+'</span>');
            }else{
                var okMsg = '输入正确.';
                $parent.append('<span class="formtips onSuccess">'+okMsg+'</span>');
            }
     }
     if( $(this).attr('id')=='telephone' ){
         if( this.value=="" ){
             var errorMsg = '请输入单位电话.';
             $parent.append('<span class="formtips onError">'+errorMsg+'</span>');
         }
         else if(checkPhone(this.value)==false){
        	 var errorMsg = '请输入正确的电话.';
             $parent.append('<span class="formtips onError">'+errorMsg+'</span>');
         }
         else{
             var okMsg = '输入正确.';
             $parent.append('<span class="formtips onSuccess">'+okMsg+'</span>');
         }
     }
     if( $(this).attr('id')=='email' ){
         if( this.value=="" ){
             var errorMsg = '请输入邮箱.';
             $parent.append('<span class="formtips onError">'+errorMsg+'</span>');
         }
         else if(checkEmail(this.value)==false){
        	 var errorMsg = '格式为“888@qq.com”.';
             $parent.append('<span class="formtips onError">'+errorMsg+'</span>');
         }
         else{
             var okMsg = '输入正确.';
             $parent.append('<span class="formtips onSuccess">'+okMsg+'</span>');
         }
     }
     if( $(this).attr('id')=='workers' ){
         if( this.value=="" ){
             var errorMsg = '请输入职工人数.';
             $parent.append('<span class="formtips onError">'+errorMsg+'</span>');
         }
         else if(checkPInt(this.value)==false){
        	 var errorMsg = '请输入正确的格式.';
             $parent.append('<span class="formtips onError">'+errorMsg+'</span>');
         }
         else{
             var okMsg = '输入正确.';
             $parent.append('<span class="formtips onSuccess">'+okMsg+'</span>');
         }
     }
     if( $(this).attr('id')=='floorArea' ){
         if( this.value=="" ){
             var errorMsg = '请输入占地面积.';
             $parent.append('<span class="formtips onError">'+errorMsg+'</span>');
         }
         else if(checkPInt(this.value)==false){
        	 var errorMsg = '请输入正确的格式.';
             $parent.append('<span class="formtips onError">'+errorMsg+'</span>');
         }
         else{
             var okMsg = '输入正确.';
             $parent.append('<span class="formtips onSuccess">'+okMsg+'</span>');
         }
     }
     if( $(this).attr('id')=='buildingArea' ){
         if( this.value=="" ){
             var errorMsg = '请输入建筑面积.';
             $parent.append('<span class="formtips onError">'+errorMsg+'</span>');
         }
         else if(checkPInt(this.value)==false){
        	 var errorMsg = '请输入正确的格式.';
             $parent.append('<span class="formtips onError">'+errorMsg+'</span>');
         }
         else{
             var okMsg = '输入正确.';
             $parent.append('<span class="formtips onSuccess">'+okMsg+'</span>');
         }
     }
     if( $(this).attr('id')=='storageArea' ){
         if( this.value=="" ){
             var errorMsg = '请输入仓库面积.';
             $parent.append('<span class="formtips onError">'+errorMsg+'</span>');
         }
         else if(checkPInt(this.value)==false){
        	 var errorMsg = '请输入正确的格式.';
             $parent.append('<span class="formtips onError">'+errorMsg+'</span>');
         }
         else{
             var okMsg = '输入正确.';
             $parent.append('<span class="formtips onSuccess">'+okMsg+'</span>');
         }
	  }
	  if( $(this).attr('id')=='foundTime' ){
	      if( this.value=="" ){
	          var errorMsg = '请输入成立时间.';
	          $parent.append('<span class="formtips onError">'+errorMsg+'</span>');
	      }else{
	             var okMsg = '输入正确.';
	             $parent.append('<span class="formtips onSuccess">'+okMsg+'</span>');
	      }
	  }
	  if( $(this).attr('id')=='result_lang' ){
	      if( this.value=="" ){
	          var errorMsg = '请输入经度.';
	          $parent.append('<span class="formtips onError">'+errorMsg+'</span>');
	      }else{
	             var okMsg = '输入正确.';
	             $parent.append('<span class="formtips onSuccess">'+okMsg+'</span>');
	      }
	  }
	  if( $(this).attr('id')=='result_lat' ){
	      if( this.value=="" ){
	          var errorMsg = '请输入纬度.';
	             $parent.append('<span class="formtips onError">'+errorMsg+'</span>');
	         }else{
	             var okMsg = '输入正确.';
	             $parent.append('<span class="formtips onSuccess">'+okMsg+'</span>');
	     }
	  }
	  
	  if( $(this).attr('id')=='adress' ){
	      if( this.value=="" ){
	          var errorMsg = '请输入单位地址.';
	             $parent.append('<span class="formtips onError" style="left: 490px;">'+errorMsg+'</span>');
	         }else{
	             var okMsg = '输入正确.';
	             $parent.append('<span class="formtips onSuccess" style="left: 490px;">'+okMsg+'</span>');
	     }
	  }
	  if( $(this).attr('id')=='dangerous' ){
	      if( this.value=="" ){
	          var errorMsg = '请输入危险化学品名称.';
	             $parent.append('<span class="formtips onError">'+errorMsg+'</span>');
	         }	      
	      else{
	             var okMsg = '输入正确.';
	             $parent.append('<span class="formtips onSuccess">'+okMsg+'</span>');
	      }
	  }
	  if( $(this).attr('id')=='passageCount' ){
	      if( this.value=="" ){
	          var errorMsg = '请输入消防车道数量.';
	             $parent.append('<span class="formtips onError">'+errorMsg+'</span>');
	         }
	      else if(checkPInt(this.value)==false){
	        	 var errorMsg = '请输入正确的格式.';
	             $parent.append('<span class="formtips onError">'+errorMsg+'</span>');
	         }
	      else{
	             var okMsg = '输入正确.';
	             $parent.append('<span class="formtips onSuccess">'+okMsg+'</span>');
	      }
	  }
	  if( $(this).attr('id')=='exitCount' ){
	      if( this.value=="" ){
	          var errorMsg = '请输入安全出口数.';
	             $parent.append('<span class="formtips onError">'+errorMsg+'</span>');
	         }
	      else if(checkPInt(this.value)==false){
	        	 var errorMsg = '请输入正确的格式.';
	             $parent.append('<span class="formtips onError">'+errorMsg+'</span>');
	         }
	      else{
	             var okMsg = '输入正确.';
	             $parent.append('<span class="formtips onSuccess">'+okMsg+'</span>');
	      }
	  }
	  if( $(this).attr('id')=='extinguisherNum2' ){
	      if( this.value=="" ){
	          var errorMsg = '请输入灭火器数量.';
	             $parent.append('<span class="formtips onError">'+errorMsg+'</span>');
	         }
	      else if(checkPInt(this.value)==false){
	        	 var errorMsg = '请输入正确的格式.';
	             $parent.append('<span class="formtips onError">'+errorMsg+'</span>');
	         }
	      else{
	             var okMsg = '输入正确.';
	             $parent.append('<span class="formtips onSuccess">'+okMsg+'</span>');
	      }
	  }
	  if( $(this).attr('id')=='fireliftCount' ){
	      if( this.value=="" ){
	          var errorMsg = '请输入消防电梯数量.';
	             $parent.append('<span class="formtips onError">'+errorMsg+'</span>');
	         }
	      else if(checkPInt(this.value)==false){
	        	 var errorMsg = '请输入正确的格式.';
	             $parent.append('<span class="formtips onError">'+errorMsg+'</span>');
	         }
	      else{
	             var okMsg = '输入正确.';
	             $parent.append('<span class="formtips onSuccess">'+okMsg+'</span>');
	      }
	  }
	  if( $(this).attr('id')=='stairwayCount' ){
	      if( this.value=="" ){
	          var errorMsg = '请输入疏散楼梯数量.';
	             $parent.append('<span class="formtips onError">'+errorMsg+'</span>');
	         }
	      else if(checkPInt(this.value)==false){
	        	 var errorMsg = '请输入正确的格式.';
	             $parent.append('<span class="formtips onError">'+errorMsg+'</span>');
	         }
	      else{
	             var okMsg = '输入正确.';
	             $parent.append('<span class="formtips onSuccess">'+okMsg+'</span>');
	      }
	  }
	  if( $(this).attr('id')=='staff_name_1' ){
	      if( this.value=="" ){
	          var errorMsg = '请输入姓名.';
	             $parent.append('<span class="formtips onError" style="left:130px; top:15px;">'+errorMsg+'</span>');
	         }	     
	      else{
	             var okMsg = '输入正确.';
	             $parent.append('<span class="formtips onSuccess" style="left:130px; top:15px;">'+okMsg+'</span>');
	      }
	  }
	  if( $(this).attr('id')=='staff_idcard_1' ){
	      if( this.value=="" ){
	          var errorMsg = '请输入身份证.';
	             $parent.append('<span class="formtips onError" style="left:220px; top:15px;">'+errorMsg+'</span>');
	         }
	      else if(checkIdcard(this.value)==false){
	        	 var errorMsg = '请输入正确的格式.';
	             $parent.append('<span class="formtips onError" style="left:220px; top:15px;">'+errorMsg+'</span>');
	         }
	      else{
	             var okMsg = '输入正确.';
	             $parent.append('<span class="formtips onSuccess" style="left:220px; top:15px;">'+okMsg+'</span>');
	      }
	  }
	  if( $(this).attr('id')=='staff_phone_1' ){
	      if( this.value=="" ){
	          var errorMsg = '请输入电话.';
	             $parent.append('<span class="formtips onError" style="left:160px; top:15px;">'+errorMsg+'</span>');
	         }
	      else if(checkPhone(this.value)==false){
	    	  alert(checkMobile(this.value)==false)
	        	 var errorMsg = '请输入正确的格式.';
	             $parent.append('<span class="formtips onError" style="left:160px; top:15px;">'+errorMsg+'</span>');
	         }
	      else{
	             var okMsg = '输入正确.';
	             $parent.append('<span class="formtips onSuccess" style="left:160px; top:15px;">'+okMsg+'</span>');
	      }
	  }
     
     
}).keyup(function(){
   $(this).triggerHandler("blur");
}).focus(function(){
     $(this).triggerHandler("blur");
});//end blur


//提交，最终验证。
 $('#submitInfo').click(function(){
	 $(".container form input").trigger('blur');
	 var numError = $('form .onError').length;
     if(numError){
         return false;
     } 
 });
});