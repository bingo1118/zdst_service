$(function(){  
	 function checkSmokeMac(user){
		  var smokeMac = document.getElementsByName("smokeMac");
	      for(var i=0;i<smokeMac.length;i++){
	     	 smokeMac[i].checked = user.checked;
	      }
	   }
		
		//����ɾ��
		function deleteAll(opFlag){
		
	   		var smokeMacs = new Array();
	   		$(".smokeMac").each(function(i){
	   			if(this.checked){
	   				//smokeMacs[i] = $(this).val();
	   				smokeMacs.push($(this).val());
	   			}
	   		});
	   		//flag Ϊ1ʱ�ǻָ� 0��ɾ��
		 var smokeMac = document.getElementsByName("smokeMac");
		 var flag = false;
	     for(var i=0;i<smokeMac.length;i++){
	     	if(smokeMac[i].checked){
	     		flag = true;
	     	} 
	     }
	     if(!flag){
	     	alert("û��ѡ��ִ�в���������ִ�иò���");
	     	return false;
	     }
	     else{
	   	    var msg="";
	     	if(opFlag==0){
	     		msg="��ȷ��Ҫ��������ɾ����mac:"
	     	}else{
	     		msg="��ȷ��Ҫ���������ָ���"
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
	 	    				alert("ɾ���ɹ�");
	 	    				window.location.reload();
	 	    			},
	 	    			error:function(){
	 	    				alert("ɾ��ʧ��");
	 	    				window.location.reload();
	 	    			}
	 	    		});
	     		}
	     		return true;
	     	}
	     }
	   }	
			


	
	
});