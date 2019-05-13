
	$(function(){
		//关闭报警
		$(".guanbiWired").click(function(){
			$("#popupWired,#popup_mask").hide();
		});
		//去处理报警
		$(".chuliWired").click(function(){
			$("#popupWired").hide();
			$("#chuliBaojing").show();
		});
		//确认处理报警
		var faultCode,repeaterMac,alarmTime;
		$(".dealWired").click(function(){
			faultCode = $(this).parent("td").siblings("td.faultCode").text();
			repeaterMac =  $(this).parent("td").siblings("td.repeaterMac").text();
			alarmTime =  $(this).parent("td").siblings("td.alarmTime").text();
		});
		$(".querenWired").click(function(){			
			var dealUser = $("#handlePerson").val();
			var dealText = $("#treatmentReasons").val();
			//alert(faultCode+"="+repeaterMac+"="+alarmTime+"="+dealUser+"="+dealText);
			if(dealUser==""||dealText==""){
				alert("处理内容不能为空！");
				return false;
			}else{
			$("#chuliBaojing,#popup_mask").hide();
			$.ajax({
				type: "GET",
				url: "faultAlarm.do?faultCode="+faultCode+"&repeaterMac="+repeaterMac+"&alarmTime="+alarmTime+"&dealUser="+dealUser+"&dealText="+dealText,
				dataType: "json",		
				async : false, 
				success: function (data) {				
					console.log("处理成功");						
				},
				error: function() {
					console.log("处理失败");
					return false;
				}
			});
			}
			window.location.reload();
			return false;
		});
		//关闭报警处理
		$(".guanbiChuWired").click(function(){
			$("#chuliBaojing,#popup_mask").hide();
		});
		
		//判断是否已处理
		var tdTotal = $(".scroll_table tr").length-1;
		for(var i=0;i<tdTotal;i++){
			//alert($(".scroll_table td.dealTime").eq(i).text().length);
			if($(".scroll_table td.dealTime").eq(i).text().length>0){
				$(".scroll_table td.dealTd").eq(i).text("已处理");
				$(".scroll_table td.faultType").eq(i).text("已处理");
			}
		}
		
	});