<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
    <%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt"  prefix="fmt"%>
<!DOCTYPE html>
<html>
<head>
<title>更换联系人</title>
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
        	<div class="conten_tj_tit">更换联系人</div>
            <div class="conten_txt">
            	<form id="setUserId" name="setUserId" action="updateUserInfo.do" method="post">
                	<label>
                		主&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;机&nbsp;&nbsp;&nbsp;&nbsp;号：<input type="text" name="repeater" id="repeater">
                		<span>
                			<b style="color:#e90000;">*</b>根据主机号(终端号)来批量修改联系人信息
                		</span>
                	</label>
                	<label>
                		联&nbsp;&nbsp;系&nbsp;&nbsp;人&nbsp;&nbsp;&nbsp;1：
                		<input type="text" name="principal1" id="principal1">
                		<span>
                			<b style="color:#e90000;">*</b>不填则不做修改
                		</span>
                	</label>
                	<label>
                		联系人1电话：<input type="text" name="principal1Phone" id="principal1Phone">
                		<span>
                			<b style="color:#e90000;">*</b>不填则不做修改
                		</span>
                	</label>
                	<label>
                		联&nbsp;&nbsp;系&nbsp;&nbsp;人&nbsp;&nbsp;&nbsp;2：
                		<input type="text" name="principal2" id="principal2">
                		<span>
                			<b style="color:#e90000;">*</b>不填则不做修改
                		</span>
                	</label>
                	<label>
                		联系人2电话：<input type="text" name="principal2Phone" id="principal2Phone">
                		<span>
                			<b style="color:#e90000;">*</b>不填则不做修改
                		</span>
                	</label>
                	<label>
                		<span>
                			<b style="color:#e90000;">*</b>${message }
                		</span>
                	</label>
                    <div style="text-align:left; margin-top:90px;">
                    	<input type="reset" value="取消" id="reset"/>
                    	<input type="submit" value="提交" id="sub"/>
                    </div>
                </form>
            </div>
        </div>
    </div>
</body>
</html>



