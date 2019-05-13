<%@page import="org.dom4j.Document"%>

<%@page import="org.apache.struts2.components.Include"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
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
	<meta http-equiv=refresh content="1;url=http://119.29.224.28:8088">
	<link rel="shortcut icon" href="images/fire.ico" />
	<link type="text/css" rel="stylesheet" href="css/page.css">
	<script src="${pageContext.request.contextPath }/js/jquery-1.8.2.js"></script>
	<script type="text/javascript" src="${pageContext.request.contextPath }/js/jquery-timer-1.0.js"></script>
</head>
<body  onload="onPageLoad()">
</body>
</html>

<script type="text/javascript">
	function getCookie(cookie_name)
		{
		    var allcookies = document.cookie;
		    var cookie_pos = allcookies.indexOf(cookie_name);   //索引的长度
		  
		    // 如果找到了索引，就代表cookie存在，
		    // 反之，就说明不存在。
		    if (cookie_pos != -1)
		    {
		        // 把cookie_pos放在值的开始，只要给值加1即可。
	        cookie_pos += cookie_name.length + 1;      //这里容易出问题，所以请大家参考的时候自己好好研究一下
	        var cookie_end = allcookies.indexOf(";", cookie_pos);
	  
	        if (cookie_end == -1)
	        {
	            cookie_end = allcookies.length;
	        }
	  
	        var value = unescape(allcookies.substring(cookie_pos, cookie_end));         //这里就可以得到你想要的cookie的值了。。。
			    }
		    return value;
		}
	 function onPageLoad(){
		 // 调用函数
		var cookie_val = getCookie("hrsst_userid");
	 }
</script>






