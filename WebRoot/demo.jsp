<%@page import="org.dom4j.Document"%>

<%@page import="org.apache.struts2.components.Include"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="com.cloudfire.dwr.push.PushMessageUtil"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>

<!doctype html>
<html>
<head>
	<title>建筑单位-视频监控</title>
	<style>
        html,body{margin:0; padding: 0;text-align: center;}
        video{max-width: 400px;width: 50%;}
    </style>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
	<meta http-equiv="Pragma" content="no-cache" />
	<link rel="shortcut icon" href="images/fire.ico" />
	<link type="text/css" rel="stylesheet" href="css/page.css">
	<link type="text/css" rel="stylesheet" href="css/style.jianzhu.css">
	<script src="${pageContext.request.contextPath }/js/jquery-1.8.2.js"></script>
	<script type="text/javascript" src="js/jquery-timer-1.0.js"></script>
	<script type="text/javascript" src="http://static.runoob.com/assets/vue/1.0.11/vue.min.js"></script>
	<script>
		document.write("<link type='text/css' href='${pageContext.request.contextPath }/css/demo.css?version=" + new Date().getTime() + "' rel='stylesheet' />");
	</script>
	<script type="text/javascript" src="${pageContext.request.contextPath }/js/ezuikit.js"></script>
<%-- <%@include file="../common/top.jsp" %> --%>
<script type="text/javascript">
	/** 文档加载完 */
			$(function(){
				/** 获取timer对应的jquery对象 */
				$("#timer").runtime();
			});
</script>
</head>
<body>
<div class="header">
	<div class="top clearfix">
    	<div class="logo fl">
        	<img src="images/logo.png" title="智能云消防大数据管理平台">
        </div>
        <div class="user fr">
        	<span>你好！${userId}</span>
            <span id="timer"> </span>
            <a href="http://www.cloudlinks.cn/view/ReSetPwd.htm" target="_blank" class="xgmm">修改密码</a>
            <a href="loginOut.do" class="zc">注销</a>
        </div>
    </div>
   <div class="nav">
    	<ul>
        	<li>
            	<a href="mydevices.do"><i class="icon02"></i><span>我的设备</span></a>
            </li>
            <li>
            	<a href="contractcount.do"><i class="icon04"></i><span>统计分析</span></a>
            </li>
            <li>
            	<a href="contractvoid.do"  class="active"><i class="icon05"></i><span>监控视频</span></a>
            </li>
            <li>
            	<a href="contractinfoweb.do"><i class="icon01"></i><span>基本资料</span></a>
            </li>
        </ul>
    </div>
</div>
<div id="app">
	<p>{{message}}</p>
	<input v-model="message">
</div>
</body>
</html>
<script type="text/javascript">
new Vue({
	el:'#app',
	data:{
		message:'hello'
	}
});


</script>






