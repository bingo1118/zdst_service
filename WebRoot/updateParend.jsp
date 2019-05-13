<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
    <%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt"  prefix="fmt"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta charset="utf-8">
<title>添加区域</title>
<link type="text/css" rel="stylesheet" href="css/style.css">
<link type="text/css" rel="stylesheet" href="css/layui.css">
<script src="js/layui.js"></script>
<script type="text/javascript" src="js/jquery-2.0.3.min.js"></script>
<script type="text/javascript" src="dwr/engine.js"></script>
<script type="text/javascript" src="dwr/util.js"></script>
<script type="text/javascript" src="dwr/interface/pushMessageCompont.js"></script>
<script type="text/javascript">
	
	$(function(){
		$("#sub").click(function(){
			var province2 = $("#province2").val();
			var province1 = $("#province1").val();
			//alert(province1+":"+province2);
			dwr.engine.setAsync(false);
			pushMessageCompont.updateParent(province2,province1,function(data){
				if(data==1){
					alert("保存成功");
				}
			});
			dwr.engine.setAsync(true);
		});
	});
</script>
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
        <div class="conten_tj">
        	<div class="conten_tj_tit">修改联动区域</div>
            <div class="conten_txt">
            	<form>
            		<label>二级区域：
            			<select name="province2" id="province2" style="width:155px">
							<option >请选择</option>
							<c:forEach items="${areaIds }" var="area" >								
								<option value="${area.areaId }">${area.areaName }</option>
							</c:forEach>
						</select>
            		</label>
            		<label>所属一级区域：
            			<select name="province1" id="province1" style="width:155px">
							<option >请选择</option>
							<c:forEach items="${parentIds }" var="parent" >								
								<option value="${parent.key }">${parent.value }</option>
							</c:forEach>
						</select>
            		</label>
             		
                    <div style="text-align:left; margin-top:90px;">
                    	<input type="reset" value="取消" id="reset"/>
                    	<input type="submit" value="设置" id="sub"/>
                    </div>
                </form>
            </div>
        </div>
    </div>
</body>
</html>
