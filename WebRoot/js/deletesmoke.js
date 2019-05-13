$(function(){  
	 function checkSmokeMac(user){
		  var smokeMac = document.getElementsByName("smokeMac");
	      for(var i=0;i<smokeMac.length;i++){
	     	 smokeMac[i].checked = user.checked;
	      }
	   }
		
		//批量删除
		function deleteAll(opFlag){
		
	   		var smokeMacs = new Array();
	   		$(".smokeMac").each(function(i){
	   			if(this.checked){
	   				//smokeMacs[i] = $(this).val();
	   				smokeMacs.push($(this).val());
	   			}
	   		});
	   		//flag 为1时是恢复 0是删除
		 var smokeMac = document.getElementsByName("smokeMac");
		 var flag = false;
	     for(var i=0;i<smokeMac.length;i++){
	     	if(smokeMac[i].checked){
	     		flag = true;
	     	} 
	     }
	     if(!flag){
	     	alert("没有选择执行操作！不能执行该操作");
	     	return false;
	     }
	     else{
	   	    var msg="";
	     	if(opFlag==0){
	     		msg="你确定要进行批量删除吗？mac:"
	     	}else{
	     		msg="你确定要进行批量恢复吗？"
	     	}
	     	var confirmflag = window.confirm(msg);
	     	if(!confirmflag){
	     		return false;
	     	}
	     	else{
	     		if(opFlag==0){
	     			alert(smokeMacs);
	 	    		$.ajax({
	 	    			type:"get",
	 	    			url:"deleteBuyerByIds.do?smokeMacs="+smokeMacs,
	 	    			dataType:"json",
	 	    			/* data:{
	 	    				smokeMacs:smokeMacs
	 	    			}, */
	 	    			success:function(){
	 	    				alert("删除成功");
	 	    				window.location.reload();
	 	    			},
	 	    			error:function(){
	 	    				alert("删除失败");
	 	    				window.location.reload();
	 	    			}
	 	    		});
	     		}
	     		return true;
	     	}
	     }
	   }	
			


	
	
});