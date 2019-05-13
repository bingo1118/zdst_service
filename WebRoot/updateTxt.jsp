<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
    <%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt"  prefix="fmt"%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <title>短信通知功能</title>
	<script type="text/javascript" src="js/jquery-2.0.3.min.js"></script>
	<link type="text/css" rel="stylesheet" href="css/style1.css">
  </head>
  
  <body>
    <div class="contain_tj">
        <div class="conten_tj">
            <div class="conten_tj_tit">短信通知功能</div>
            <div class="conten_txt">
                <form id="updateTxt" name="updateTxt" action="updateTxt" method="post">
                    <label>账号：<input type="text" name="userid" id="useid"><span><b style="color:#e90000;">*</b> 请填写正确的手机号</span></label>
                    <label>开通短信通知：<input type="radio"  name="txtState" value="1"/>开<input type="radio" name="txtState" value="0"/>关
                    </label>
                    <div style="text-align:left; margin-top:90px;"><input type="submit" value="提交" id="sub"/></div>
                </form>
            </div>
        </div>
    </div>
  </body>
</html>
