<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta charset="utf-8">
<title>登录</title>
<link type="text/css" rel="stylesheet" href="css/style.css">

</head>

<body>
	<div class="contain">
    	<div class="login">
        	<div class="login_user"></div>
            <form method="post" id="loginForm" >
                <div>手机号：
                	<input type="" placeholder="请输入手机号" id="userId" value="" size="20"/>
                    <span class="tip"></span>
                </div>
                <div>密码：
                	<input type="password" id="password" placeholder="请输入密码"/>
                    <span class="tip"></span>
                </div>
                <div>验证码：<br/>
                	<input type="" placeholder="" class="yzm" id="code" maxlength="4" value=""/>
                  <div class='yzma'   style="margin-top: 0;"><a onclick='createCode()'  id='discode' >ACDF</a></div>
                    <div style="clear: both;"></div>
                   <div class="tip" style="margin-top:5px;position:absolute;">密验证码不能为空！</div>
                    </div>
                <div>
                	<button class="active" type="submit" id="loginBtn">登录<tton>
                </div>
            </form>
        </div>
    </div>
</body>
</html>
<script type="text/javascript" src="js/jquery1.42.min.js"></script>
<script type="text/javascript" src="js/check.js"></script>

<script type="text/javascript">
$(document.body).ready(function () {
    //首次获取验证码
    $("#imgVerify").attr("src","getVerify.do?"+Math.random());
});
//获取验证码
function getVerify(obj){
    obj.src = "getVerify.do?"+Math.random();
}
    
</script>

