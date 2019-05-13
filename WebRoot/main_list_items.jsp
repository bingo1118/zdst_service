<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8">
<title>设备绑定</title>
<link rel="shortcut icon" href="images/fire.ico" />
<link type="text/css" rel="stylesheet" href="css/style.css">
<link type="text/css" rel="stylesheet" href="css/layui.css">
<script type="text/javascript" src="js/jquery-2.0.3.min.js"></script>
<script type="text/javascript" src="js/add.device.js"></script>
</head>
<script>
  $(window).load(function(){
  	$("#loader").hide();
  });
</script> 

<body style="width:100%;background-color:#fff;min-width:100%;overflow-y: scroll;">
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
	<div id="loader"></div>
	<div class="title_tj">
 <a class="layui-btn layui-btn-small" style="background:#3b66cc;" href="queryItems_test.do">返回</a> 
</div>
<div class="contain_tj">	
		<form id="query" name="query" method="get">
            <div class="groupForm" style="padding-left:18px;">
            	<label>终端编号：</label>
                <input style="border: 1px solid #999;" class="it" id="TerminalMac" name="TerminalMac" type="text" placeholder="请输入" value=""/>
            </div>
	        <div class="groupForm" style="margin-left: 30px;">
                <input class="ser" id="mainlistsubmit" type="button" ></input>
            </div>
        </form>   
   
	<div class="content_table" style="border-bottom: 0px solid #ced5e7;">
		<table class="deviceTable" id="deviceTable">
			<tr>
				<th colspan="2">报警设备</th>
				<th colspan="2">响应设备</th>
			</tr>
			<tr>
				<td class="deviceCall deviceCallList clearfix">
										
				</td>
				<td style="width:60px;">
					<input type="checkbox" class="deviceCallAll"/>全选
				</td>
				<td class="deviceResponse deviceResponseList clearfix">
					
				</td>
				<td style="width:60px;">
					<input type="checkbox" class="deviceResponseAll"/>全选
				</td>
			</tr>		
			<tr>
				<td class="deviceCallChecked deviceCallCheckedList clearfix" id="deviceCallChecked" colspan="2">
					<h2>已选择设备：</h2>					
				</td>
				<td class="deviceResponseChecked deviceResponseCheckedList clearfix" id="deviceResponseChecked" colspan="2">
					<h2>已选择设备：</h2>				
				</td>
			</tr>			
		</table>
		<input type="button" class="deviceOk" value="确认"/>
		
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
	</div>
	<div class="popup_mask" id="popup_mask"></div>
<div class="popup_mask" id="popup_mask1" style="background:none ;filter:alpha(opacity=100);/*IE滤镜，透明度50%*/-moz-opacity:1; /*Firefox私有，透明度50%*/opacity:1;/*其他，透明度50%*/">
	<div class="popup popup_plan" style="width:955px;height:600px;" id="popup_1">
    	<div class="close_1">X</div>
    	<h1>修改联动设备</h1>
	    <div class="content_table" style="border-bottom: 0px solid #ced5e7;">
			<table class="deviceTable deviceTablePopup" id="deviceTablePopup">
				<tr>
					<th>报警设备</th>
					<th>响应设备</th>
				</tr>
				<tr>
					<td class="deviceCall deviceCallListPopup clearfix">
						
					</td>
					<td class="deviceResponse deviceResponseListPopup clearfix">
					
					</td>	
				</tr>		
				<tr>
					<td class="deviceCallChecked clearfix" id="deviceCallCheckedpopup">
						<h2>已选择设备：</h2>					
					</td>
					<td class="deviceResponseChecked clearfix" id="deviceResponseCheckedpopup">
						<h2>已选择设备：</h2>				
					</td>
				</tr>
				<tr>			
			</table>
			<input type="button" class="deviceOk" value="确认"/>
		</div>
	</div> </div>
</div></div>	
	<script src="js/soud.popup.js"></script>
	<script src="js/layui.js"></script>
</body>
</html>