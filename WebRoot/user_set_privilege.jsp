<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
    <%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt"  prefix="fmt"%>
<!DOCTYPE html>
<html>
<head>
<title>用户权限设置</title>
<script type="text/javascript" src="js/jquery-2.0.3.min.js"></script>
<link type="text/css" rel="stylesheet" href="css/style1.css">
<link type="text/css" rel="stylesheet" href="css/layui.css">
<script src="js/layui.js"></script>
</head>
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
<div class="title_tj">
 <a class="layui-btn layui-btn-small" style="background:#3b66cc;" href="queryItems_test.do">返回</a> 
</div>
	<div class="contain_tj">
        <div class="conten_tj">
        	<div class="conten_tj_tit">权限设置</div>
            <div class="conten_txt">
            	<form id="setUserId" name="setUserId" action="resetUserPrivilege.do" method="post">
                	<label>账号：<input type="text" name="userId" id="userId"><span><b style="color:#e90000;">*</b> 请填写正确的手机号</span></label>
                	<label>权限：
                		<select id="privi" name="privi">
                			<option value="1" selected="selected">1</option>
                			<option value="2">2</option>
                			<option value="3">3</option>
                			<option value="6">6</option>
                			<option value="7">7</option>
                			<!-- <option value="4">4</option> -->
                		</select>
                		<span><b style="color:#e90000;">*</b> 1、代表个体商户。<b style="color:#e90000;">*</b>2、预留权限。<b style="color:#e90000;">*</b>3、管理权限。</span></label>
                	</label>
                	<label>
                		<span><b style="color: red;">${message }</b></span>
                	</label>
                    <div style="text-align:left; margin-top:90px;"><input type="reset" value="取消" id="reset"/><input type="submit" value="提交" id="sub"/></div>
                </form>
            </div>
        </div>
    </div>
</body>
</html>



