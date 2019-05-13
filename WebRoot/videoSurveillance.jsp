<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@page import="com.cloudfire.dwr.push.PushMessageUtil"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>
<!DOCTYPE html>
<html>
<head>
<base href="<%=basePath%>">
<meta charset="utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=9"> 
<meta name="renderer" content="webkit"> 
<title>视频监控</title>
<link rel="shortcut icon" href="images/fire.ico" />
<link type="text/css" rel="stylesheet" href="css/style.css">
<script type="text/javascript" src="js/jquery-2.0.3.min.js"></script>
<script type="text/javascript" src="js/jquery-timer-1.0.js"></script>

<script type="text/javascript">
	/** 文档加载完 */
			$(function(){
				/** 获取timer对应的jquery对象 */
				$("#timer").runtime();
			});
			
		function govideo(){
			$.ajax({
			type : "POST",
			url : "http://127.0.0.1:8088/LogOn/doLogin/",
			data : GetDate(),
			success : function(result) {
				hideLoading();
				
				//result = JSON.parse(result);
				if (result.info == "success") {
					if ($("#hidBackUrl").val())
						window.location.href = $("#hidBackUrl").val();
					else
						window.location.href = '/LiveMonitor/index/';
				} 
				else if(result.data){
					ChangeVerifyCode();
					$("#btnLogin").attr("disabled", false);
					//MsgBoxHelper.Show("error", _L_("errorTip"), result.data, UserNameFocus);
					$("#ErrorMsg").html(result.data);
				}
				else{
					ChangeVerifyCode();
					$("#btnLogin").attr("disabled", false);
					//MsgBoxHelper.Show("error", _L_("errorTip"), _L_("loginErrorForServ"), UserNameFocus);
					$("#ErrorMsg").html(_L_("loginErrorForServ"));
				}
			},
			error:function(e){
				alert(e);
			}
			
		});
		}
		
		function GetDate() {
			var data = {
				//userName : $("#userName").val(),
				//password : $("#password").val(),
				userName : "admin",
				password : "admin",
				port : getPort()
			};
			return data;
		}
		function getPort()
		{
			alert(1);
			var port = 6003;
			$.ajax({
		        async: false,
		        type: "get",
		        url: "http://127.0.0.1:8088/Public/OCX/LocalParameters.xml",
		        dataType: "xml",
		        success: function(ResponseText) {
		        alert(ResponseText);
		        	port = $.trim($("LocalParameters > NetPort", $(data)).text());
		        }
		    });
		    alert(port);
			return port;
		}
</script>
</head>

<body style="overflow-y: scroll;" onload="govideo()">

</body>
<input type="hidden" id="userName" value="${userId}">
<input type="hidden" id="password" value="${userId}">
</html> 

