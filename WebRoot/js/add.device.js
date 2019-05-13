$(function(){
	//搜索区域管理终端设备下的设备方法
	$("#mainlistsubmit").bind("click",function(){
		var TerminalMac = $("#TerminalMac").val();
		if(TerminalMac.length == 0){
			alert("终端编号不能为空！");
			return false;
		}
		$.ajax({
			type:"GET",
			url:"selectDeviceByRepeater.do?TerminalMac="+TerminalMac,
			async:true,				
			dataType:"json",
			success : function(data){
				
				$(".deviceResponseList").empty();
				$(".deviceCallList").empty();
				$(".deviceTableRecord .deviceTableRecordli").empty();
				var json = eval(data);
				var deviceCallStr = "";
				var deviceResponseStr = "";
				var deviceTableRecordStr = "";
				var soundMacStr = "";
				for(var i=0;i<json[0].smokeList.length;i++){
					if(json[0].smokeList[i].deviceType == 7){
						deviceResponseStr += "<div class='deviceChoose'><input type='checkbox' name='soundMac' value='"+json[0].smokeList[i].mac+"' title='"+json[0].smokeList[i].name+"("+json[0].smokeList[i].mac+")'>"+json[0].smokeList[i].name+"("+json[0].smokeList[i].mac+")</div>";
					}else{
						deviceCallStr += "<div class='deviceChoose'><input type='checkbox' name='vehicle' value='"+json[0].smokeList[i].deviceType+json[0].smokeList[i].mac+"' title='"+json[0].smokeList[i].name+"("+json[0].smokeList[i].mac+")'>"+json[0].smokeList[i].name+"("+json[0].smokeList[i].mac+")</div>";
					}
				}
				for(var i=0;i<json[0].ackList.length;i++){
					var soundMac = "";
					for(soundMac in json[0].ackList[i].ackMap){
						for(var k=0;k<json[0].ackList[i].ackMap[soundMac].length;k++){
							
							soundMacStr += json[0].ackList[i].ackMap[soundMac][k].deviceType+"("+json[0].ackList[i].ackMap[soundMac][k].deviceMac+")、";
						
						}
						
						soundMacStr = soundMacStr+"声光报警器("+soundMac+")";
						
					
					}
					deviceTableRecordStr += "<tr class='deviceTableRecordli' onmouseover='this.style.backgroundColor='#ffff66';' onmouseout='this.style.backgroundColor='#fff';'>"+
					"<td style='width:30px;text-align: center;'>"+(i+1)+"</td>"+
					"<td style='padding:0 10px;'><div>"+soundMacStr+"</div></td>"+
					"<td style='width:75px;text-align: center;' soundMac='"+soundMac+"'><a class='modify' href='javascript:void(0);show()'>修改</a>/<a class='delete' href='javascript:void(0);'>删除</a></td>"+
					"</tr>";
					soundMacStr = "";
				}
					
				$(".deviceResponseList").append(deviceResponseStr);
				$(".deviceCallList").append(deviceCallStr);
				$(".deviceTableRecord tr#title").after(deviceTableRecordStr);
			},
			error : function(){
				alert("查询失败");
			}
		});
		 return false;
	});
	
	//添加联动设备的方法
	var deviceCallArray = [],deviceResponseArray = [];
	$(".deviceOk").click(function(){
		var TerminalMac = $("#TerminalMac").val();
		deviceCallArray = [];
		deviceResponseArray = []; 
		var deviceCallArraylength = $(".deviceCall input:checkbox:checked").length;
		var deviceResponseArraylength = $(".deviceResponse input:checkbox:checked").length;
		if(deviceCallArraylength == 0 || deviceResponseArraylength == 0){
			alert("报警设备或响应设备不能为空！");
			return false;
		}
		for(var i=0;i<deviceCallArraylength;i++){
			deviceCallArray.push($(".deviceCall input:checkbox:checked").eq(i).val());
		}
		for(var i=0;i<deviceResponseArraylength;i++){
			deviceResponseArray.push($(".deviceResponse input:checkbox:checked").eq(i).val());
		}
		$.ajax({
			type:"GET",
			url:"saveDeviceByRepeater.do?deviceCallArray="+deviceCallArray+"&deviceResponseArray="+deviceResponseArray+"&TerminalMac="+TerminalMac,
			async:true,				
			dataType:"json",
			success : function(data){
				$("#popup_mask,#popup_1").fadeOut();
				alert("添加成功！");
				$(".deviceTableRecord .deviceTableRecordli").empty();
				var json = eval(data);
				var deviceTableRecordStr = "";
				var soundMacStr = "";
				for(var i=0;i<json.length;i++){
					var soundMac = "";
					for(soundMac in json[i].ackMap){
						for(var k=0;k<json[i].ackMap[soundMac].length;k++){
							
							soundMacStr += json[i].ackMap[soundMac][k].deviceType+"("+json[i].ackMap[soundMac][k].deviceMac+")、";
						
						}
						
						soundMacStr = soundMacStr+"声光报警器("+soundMac+")";
										
					}
					deviceTableRecordStr += "<tr class='deviceTableRecordli' onmouseover='this.style.backgroundColor='#ffff66';' onmouseout='this.style.backgroundColor='#fff';'>"+
					"<td style='width:30px;text-align: center;'>"+(i+1)+"</td>"+
					"<td style='padding:0 10px;'><div>"+soundMacStr+"</div></td>"+
					"<td style='width:75px;text-align: center;' soundMac='"+soundMac+"'><a class='modify' href='javascript:void(0);show()'>修改</a>/<a class='delete' href='javascript:void(0);'>删除</a></td>"+
					"</tr>";
					soundMacStr = "";
				}					
				$(".deviceTableRecord tr#title").after(deviceTableRecordStr);
				$("input:checkbox:checked").removeAttr("checked");  
				$("#deviceCallChecked span").remove();				
				$("#deviceResponseChecked span").remove();			
			},
			error : function(){
				alert("添加失败！");
			}
		});
	});
	
	//修改一条添加设备的方法
	$(".deviceTableRecord").on("click","a.modify",function(){
		var TerminalMac = $("#TerminalMac").val();
		var soundMac = $(this).parent("td").attr("soundMac");
		$(".deviceResponseListPopup").empty();
		$(".deviceCallListPopup").empty();
		$("#deviceCallCheckedpopup span").remove();				
		$("#deviceResponseCheckedpopup span").remove();
		$.ajax({
			type:"GET",
			url:"updateDeviceByRepeater.do?TerminalMac="+TerminalMac+"&soundMac="+soundMac,
			async:true,				
			dataType:"json",
			success : function(data){
				console.log("成功！");
				var json = eval(data);
				var deviceCallStr = "";
				var deviceResponseStr = "";

				for(var i=0;i<json[0].smokeList.length;i++){
					if(json[0].smokeList[i].deviceType == 7){
						deviceResponseStr += "<div class='deviceChoose'><input type='checkbox' name='soundMac' value='"+json[0].smokeList[i].mac+"' title='"+json[0].smokeList[i].name+"("+json[0].smokeList[i].mac+")'>"+json[0].smokeList[i].name+"("+json[0].smokeList[i].mac+")</div>";
					}else{
						deviceCallStr += "<div class='deviceChoose'><input type='checkbox' name='vehicle' value='"+json[0].smokeList[i].deviceType+json[0].smokeList[i].mac+"' title='"+json[0].smokeList[i].name+"("+json[0].smokeList[i].mac+")'>"+json[0].smokeList[i].name+"("+json[0].smokeList[i].mac+")</div>";
					}
				}									
				$(".deviceResponseListPopup").append(deviceResponseStr);
				$(".deviceCallListPopup").append(deviceCallStr);
				for(var i=0;i<json[0].smokeList.length;i++){
	
					if(soundMac == json[0].smokeList[i].mac){
						$(".deviceResponseListPopup").find("input[value='"+json[0].smokeList[i].mac+"']").prop("checked","checked");  
					}
					for(var j=0;j<json[0].ackList.length;j++){		
						//alert(json[0].ackList[j].deviceMac == json[0].smokeList[i].mac)
						if(json[0].ackList[j].deviceMac == json[0].smokeList[i].mac){
							//alert(json[0].ackList[j].deviceMac == json[0].smokeList[i].mac);
							$(".deviceCallListPopup").find("input[value='"+json[0].smokeList[i].deviceType+json[0].smokeList[i].mac+"']").prop("checked","checked");  
						}
					}
				}
				$(".deviceCallListPopup input[name='vehicle']").each(function(){
					if($(this).is(":checked")){
						var deviceVal = $(this).attr("title");
						var	deviceCheckedChild = $("#deviceCallCheckedpopup h2").last();						
						var deviceChecked = "<span title='"+deviceVal+"'>"+deviceVal+"<a class='delete' href='javascript:void(0);'>删除</a></span>";
						deviceCheckedChild.after(deviceChecked);
					}
				});
				$(".deviceResponseListPopup input[name='soundMac']").each(function(){
					if($(this).is(":checked")){
						var deviceVal = $(this).attr("title");
						var	deviceCheckedChild = $("#deviceResponseCheckedpopup h2").last();						
						var deviceChecked = "<span title='"+deviceVal+"'>"+deviceVal+"<a class='delete' href='javascript:void(0);'>删除</a></span>";
						deviceCheckedChild.after(deviceChecked);
					}
				});
				
			},
			error : function(){
				console.log("失败！");
			}
		});
	});
	//删除一条添加设备的方法
	$(".deviceTableRecord").on("click","a.delete",function(){
		var soundMac = $(this).parent("td").attr("soundMac");
		var TerminalMac = $("#TerminalMac").val();
		$.ajax({
			type:"GET",
			url:"deleteDeviceByRepeater.do?soundMac="+soundMac+"&TerminalMac="+TerminalMac,
			async:true,				
			dataType:"json",
			success : function(data){
				alert("删除成功！");
				$(".deviceTableRecord .deviceTableRecordli").empty();
				var json = eval(data);
				var deviceTableRecordStr = "";
				var soundMacStr = "";
				for(var i=0;i<json.length;i++){
					var soundMac = "";
					for(soundMac in json[i].ackMap){
						for(var k=0;k<json[i].ackMap[soundMac].length;k++){
							
							soundMacStr += json[i].ackMap[soundMac][k].deviceType+"("+json[i].ackMap[soundMac][k].deviceMac+")、";
						
						}
						
						soundMacStr = soundMacStr+"声光报警器("+soundMac+")";
										
					}
					deviceTableRecordStr += "<tr class='deviceTableRecordli' onmouseover='this.style.backgroundColor='#ffff66';' onmouseout='this.style.backgroundColor='#fff';'>"+
					"<td style='width:30px;text-align: center;'>"+(i+1)+"</td>"+
					"<td style='padding:0 10px;'><div>"+soundMacStr+"</div></td>"+
					"<td style='width:75px;text-align: center;' soundMac='"+soundMac+"'><a class='modify' href='javascript:void(0);show()'>修改</a>/<a class='delete' href='javascript:void(0);'>删除</a></td>"+
					"</tr>";
					soundMacStr = "";
				}					
				$(".deviceTableRecord tr#title").after(deviceTableRecordStr);
			},
			error : function(){
				alert("删除失败！");
			}
		});
	});
	
	//全选设备的方法
	//报警设备全选方法
	$(".deviceCallAll").click(function(){ 	
		var deviceCheckedInputNum = $(".deviceCallList").find("input[name='vehicle']").length;
		var deviceCheckedNum = $(".deviceCallCheckedList").find("span").length;
		var deviceChecked = "";
		$(".deviceCallCheckedList").find("span").remove();
		if(deviceCheckedNum == deviceCheckedInputNum){ 	
			$(".deviceCallList").find("input[name='vehicle']").removeAttr("checked");   
		}else{
			for(var i=0;i<deviceCheckedInputNum;i++){
				var deviceVal = $(".deviceCallList input").eq(i).attr("title");
				deviceChecked += "<span title='"+deviceVal+"'>"+deviceVal+"<a class='delete' href='javascript:void(0);'>删除</a></span>"; 
				$(".deviceCallList").find("input[name='vehicle']").eq(i).prop("checked","checked");  
			}
			deviceCheckedChild = $(".deviceCallCheckedList h2").last();		
			deviceCheckedChild.after(deviceChecked);
		}
	});
	//响应设备全选方法
	$(".deviceResponseAll").click(function(){ 	
		var deviceCheckedInputNum = $(".deviceResponseList").find("input[name='soundMac']").length;
		var deviceCheckedNum = $(".deviceResponseCheckedList").find("span").length;
		var deviceChecked = "";
		$(".deviceResponseCheckedList").find("span").remove();
		if(deviceCheckedNum == deviceCheckedInputNum){ 	
			$(".deviceResponseList").find("input[name='soundMac']").removeAttr("checked");   
		}else{
			for(var i=0;i<deviceCheckedInputNum;i++){
				var deviceVal = $(".deviceResponseList input").eq(i).attr("title");
				deviceChecked += "<span title='"+deviceVal+"'>"+deviceVal+"<a class='delete' href='javascript:void(0);'>删除</a></span>"; 
				$(".deviceResponseList").find("input[name='soundMac']").eq(i).prop("checked","checked");  
			}
			deviceCheckedChild = $(".deviceResponseCheckedList h2").last();		
			deviceCheckedChild.after(deviceChecked);
		}
	});
	
	//选择报警设备的方法
	var deviceCheckedChild;
	$("#deviceTable .deviceCall").on("click","input",function(){
		var deviceCheckedNum = $("#deviceCallChecked span").length;
		var deviceVal = $(this).attr("title");
		if($(this).is(":checked")){
			if(deviceCheckedNum<=0){
				deviceCheckedChild = $("#deviceCallChecked h2").last();
			}else{
				deviceCheckedChild = $("#deviceCallChecked span").last();
			}
			var deviceChecked = "<span title='"+deviceVal+"'>"+deviceVal+"<a class='delete' href='javascript:void(0);'>删除</a></span>";
			deviceCheckedChild.after(deviceChecked);
		}else{
			$("#deviceTable .deviceCallAll").removeAttr("checked");   
			for(var i=0;i<deviceCheckedNum;i++){
				var deviceValP = $("#deviceCallChecked span").eq(i).attr("title");
				var deviceCheck;
				if(deviceVal == deviceValP){
					$("#deviceCallChecked span").eq(i).remove();	
					
				}
			}
		}
	});	
	
	$("#deviceTable .deviceResponse").on("click","input",function(){
		var deviceCheckedNum = $("#deviceResponseChecked span").length;
		var deviceVal = $(this).attr("title");
		if($(this).is(":checked")){
			if(deviceCheckedNum<=0){
				deviceCheckedChild = $("#deviceResponseChecked h2").last();
			}else{
				deviceCheckedChild = $("#deviceResponseChecked span").last();
			}
			var deviceChecked = "<span title='"+deviceVal+"'>"+deviceVal+"<a href='javascript:void(0);'>删除</a></span>";
			deviceCheckedChild.after(deviceChecked);
		}else{
			$("#deviceTable .deviceResponseAll").removeAttr("checked");   
			for(var i=0;i<deviceCheckedNum;i++){
				var deviceValP = $("#deviceResponseChecked span").eq(i).attr("title");
				var deviceCheck;
				if(deviceVal == deviceValP){
					$("#deviceResponseChecked span").eq(i).remove();	
					
				}
			}
		}
	});
	
	$("#deviceTablePopup .deviceCall").on("click","input",function(){
		var deviceCheckedNum = $("#deviceCallCheckedpopup span").length;
		var deviceVal = $(this).attr("title");
		if($(this).is(":checked")){
			if(deviceCheckedNum<=0){
				deviceCheckedChild = $("#deviceCallCheckedpopup h2").last();
			}else{
				deviceCheckedChild = $("#deviceCallCheckedpopup span").last();
			}
			var deviceChecked = "<span title='"+deviceVal+"'>"+deviceVal+"<a class='delete' href='javascript:void(0);'>删除</a></span>";
			deviceCheckedChild.after(deviceChecked);
		}else{
			for(var i=0;i<deviceCheckedNum;i++){
				var deviceValP = $("#deviceCallCheckedpopup span").eq(i).attr("title");
				var deviceCheck;
				if(deviceVal == deviceValP){
					$("#deviceCallCheckedpopup span").eq(i).remove();	
					
				}
			}
		}
	});	
	
	$("#deviceTablePopup .deviceResponse").on("click","input",function(){
		var deviceCheckedNum = $("#deviceResponseCheckedpopup span").length;
		var deviceVal = $(this).attr("title");
		if($(this).is(":checked")){
			if(deviceCheckedNum<=0){
				deviceCheckedChild = $("#deviceResponseCheckedpopup h2").last();
			}else{
				deviceCheckedChild = $("#deviceResponseCheckedpopup span").last();
			}
			var deviceChecked = "<span title='"+deviceVal+"'>"+deviceVal+"<a href='javascript:void(0);'>删除</a></span>";
			deviceCheckedChild.after(deviceChecked);
		}else{
			for(var i=0;i<deviceCheckedNum;i++){
				var deviceValP = $("#deviceResponseCheckedpopup span").eq(i).attr("title");
				var deviceCheck;
				if(deviceVal == deviceValP){
					$("#deviceResponseCheckedpopup span").eq(i).remove();	
					
				}
			}
		}
	});
	
	$(".deviceCallChecked").on("click","a",function(){
		var spanTitle =  $(this).parent("span").attr("title");
		$(this).parent("span").remove();
		var deviceCheckedNum = $(".deviceCall input:checkbox:checked").length;
		for(var i=0;i<deviceCheckedNum;i++){
			$("#deviceTable .deviceCallAll").removeAttr("checked");   
			var inputTitle = $(".deviceCall input:checkbox:checked").eq(i).attr("title");
			if(spanTitle == inputTitle){
				$(".deviceCall input:checkbox:checked").eq(i).removeAttr("checked");
			}
		}
	});
	$(".deviceResponseChecked").on("click","a",function(){
		var spanTitle =  $(this).parent("span").attr("title");
		$(this).parent("span").remove();
		var deviceCheckedNum = $(".deviceResponse input:checkbox:checked").length;
		for(var i=0;i<deviceCheckedNum;i++){
			$("#deviceTable .deviceResponseAll").removeAttr("checked");   
			var inputTitle = $(".deviceResponse input:checkbox:checked").eq(i).attr("title");
			if(spanTitle == inputTitle){
				$(".deviceResponse input:checkbox:checked").eq(i).removeAttr("checked");
			}
		}
	});
	
});