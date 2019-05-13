<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
    <%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt"  prefix="fmt"%>
<!DOCTYPE html>
<html>
<head>
<title>用户重置</title>
<script type="text/javascript" src="js/jquery-2.0.3.min.js"></script>
<link type="text/css" rel="stylesheet" href="css/style1.css">
</head>
<body>
	<div class="contain_tj">
        <div class="conten_tj">
        	<div class="conten_tj_tit">重置用户信息</div>
            <div class="conten_txt">
            	<form id="setUserId" name="setUserId" action="setUserId" method="post">
                	<label>账号：<input type="text" name="userId" id="userId"><span><b style="color:#e90000;">*</b> 请填写正确的手机号</span></label>
                    <div style="text-align:left; margin-top:90px;"><input type="reset" value="取消" id="reset"/><input type="submit" value="提交" id="sub"/></div>
                </form>
            </div>
        </div>
    </div>
</body>
</html>



