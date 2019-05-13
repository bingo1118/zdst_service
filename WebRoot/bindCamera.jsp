<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
    <%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt"  prefix="fmt"%>
<!DOCTYPE html>
<html>
<head>
<title>添加用户</title>
<script type="text/javascript" src="js/jquery-2.0.3.min.js"></script>
<link type="text/css" rel="stylesheet" href="css/style.css">
<link type="text/css" rel="stylesheet" href="css/layui.css">
<script type="text/javascript" src="dwr/engine.js"></script>
<script type="text/javascript" src="dwr/util.js"></script>
<script type="text/javascript" src="dwr/interface/pushMessageCompont.js"></script>
<script type="text/javascript">
$(function(){
var smokeArr = [];
	$("#new").click(function(){
		var smoke = $("#smoke").val();
		if(smoke == ""){
			alert("摄像头MAC编号错误！");
			return false;
		}else{
			$(".smoke").append(smoke+"&nbsp;&nbsp;&nbsp;&nbsp;");
			smokeArr.push(smoke);
			$("#smoke").val("");
		}	
	});
	$("#add").submit(function () {
			var username = $("#username");
			var smoke = $("#smoke");
				if(username.val()==""){
					alert("账号不能为空");
					return false;
				}
				if(smokeArr.length == 0){
					alert("烟感MAC编号不能为空");
					return false;
				}			
				$.ajax({
					type: "post",
					url: "bindCameraByUserId.do",
					async: false,
					traditional :true, 
					dataType: "json",
					data: { 
						username:username.val(),
						smokeArr: smokeArr		
					},
					success: function (data) {
						//alert(data);
						//username.val("");
						smoke.val("");
						alert("绑定成功");
						smokeArr.length = 0;
						$(".smoke").remove();
						$(".deviceTableRecord .deviceTableRecordli").empty();
						var json = eval(data);
						var deviceTableRecordStr = "";
						for(var i=0;i<json.length;i++){
							deviceTableRecordStr += "<tr class='deviceTableRecordli' onmouseover='this.style.backgroundColor='#ffff66';' onmouseout='this.style.backgroundColor='#fff';'>"+
							"<td style='width:30px;text-align: center;'>"+(i+1)+"</td>"+
							"<td style='padding:0 10px;'><div>"+json[i]+"</div></td>"+
							"<td style='width:75px;text-align: center;' devMac='"+json[i]+"'><a class='delete' href='javascript:void(0);'>解绑</a></td>"+
							"</tr>";
						}
						$(".deviceTableRecord tr#title").after(deviceTableRecordStr);
						return false;
					},
					error: function () {  alert("绑定失败");return false;}		
				});
			 return false;
		});
});	
</script>
</head>
<body>
<div class="header">
	<div class="top clearfix">
    	<div class="logo fl">
        	<img src="images/logo.png" title="智慧云消防大数据管理平台">
        </div>
        <div class="user fr">
        	<span>你好！欢迎 ${userId} 登录</span>
        	<span id="timer"> </span>
           <!--  <a href="http://www.cloudlinks.cn/view/ReSetPwd.htm" target="_blank" class="xgmm">修改密码</a> -->
            <a href="loginOutWelcome.do" class="zc">注销</a>
        </div>
    </div>
</div>
<div class="title_tj">
 <a class="layui-btn layui-btn-small" style="background:#3b66cc;" href="queryItems_test.do">返回</a> 
</div>
	<div class="contain_tj">
        <div class="conten_tj">
        	<div class="conten_tj_tit">用户绑定摄像头设备</div>
            <div class="conten_txt">
            	<form id="add">
                	<label>账号：<input type="text" name="username" id="username" onblur="selectCamera()">
                	<!-- <input type="button" value="查询" > -->
                	<span><b style="color:#e90000;">*</b> 请填写正确的手机号</span></label>
                    <label>烟感MAC编号：<input type="text" name="smoke" id="smoke"><input type="button" value="添加" id="new" style="width: 50px; margin-left: 15px;cursor:pointer;"/><span><b style="color:#e90000;">*</b> 请填写正确的烟感MAC编号</span></label>
                    <p class="smoke">已绑定的摄像头：</p>
                    <div style="text-align:center; margin-top:90px;"><input type="reset" value="取消" id="reset"/><input type="submit" value="提交" id="sub"/></div>
                
                	<h2 style="margin-top:100px; margin-bottom:30px;font-size:16px;">已添加设备记录:</h2>
					<table class="deviceTableRecord">
						<tr id="title">
							<th style="width:30px;text-align: center;">
								序号
							</th>
							<th>
								已添加设备
							</th>
							<th style="width:75px;text-align: center;">
								操作
							</th>
						</tr>
					</table>
                </form>
            </div>
            
        </div>
    </div>
</body>
</html>
<script type="text/javascript">
function selectCamera(){
var username = $("#username").val();
	dwr.engine.setAsync(false);
   		pushMessageCompont.getCamera(username, function(data) {
		if(data!==null){
			$(".deviceTableRecord .deviceTableRecordli").empty();
				var json = eval(data);
				var deviceTableRecordStr = "";
				for(var i=0;i<json.length;i++){
					deviceTableRecordStr += "<tr class='deviceTableRecordli' onmouseover='this.style.backgroundColor='#ffff66';' onmouseout='this.style.backgroundColor='#fff';'>"+
					"<td style='width:30px;text-align: center;'>"+(i+1)+"</td>"+
					"<td style='padding:0 10px;'><div>"+json[i]+"</div></td>"+
					"<td style='width:75px;text-align: center;' devMac='"+json[i]+"'><a class='delete' href='javascript:void(0);'>解绑</a></td>"+
					"</tr>";
				}
				
			$(".deviceTableRecord tr#title").after(deviceTableRecordStr);
		}
	});
	dwr.engine.setAsync(true);
}


$(".deviceTableRecord").on("click","a.delete",function(){
	var devMac = $(this).parent("td").attr("devMac");
	var username = $("#username").val();
	dwr.engine.setAsync(false);
   		pushMessageCompont.delCamera(username,devMac, function(data) {
		 if(data!==null){
		 alert("解绑成功");
			$(".deviceTableRecord .deviceTableRecordli").empty();
				var json = eval(data);
				var deviceTableRecordStr = "";
				for(var i=0;i<json.length;i++){
					deviceTableRecordStr += "<tr class='deviceTableRecordli' onmouseover='this.style.backgroundColor='#ffff66';' onmouseout='this.style.backgroundColor='#fff';'>"+
					"<td style='width:30px;text-align: center;'>"+(i+1)+"</td>"+
					"<td style='padding:0 10px;'><div>"+json[i]+"</div></td>"+
					"<td style='width:75px;text-align: center;' devMac='"+json[i]+"'><a class='delete' href='javascript:void(0);'>解绑</a></td>"+
					"</tr>";
				}
			$(".deviceTableRecord tr#title").after(deviceTableRecordStr);
		} 
	});
	dwr.engine.setAsync(true);
});
</script>

