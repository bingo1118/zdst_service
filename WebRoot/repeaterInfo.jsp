<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8">
<title>主机信息</title>
<link rel="shortcut icon" href="images/fire.ico" />
<link type="text/css" rel="stylesheet" href="css/page.css">
<link type="text/css" rel="stylesheet" href="css/style.css">
<link type="text/css" rel="stylesheet" href="css/laydate.css">
<link type="text/css" rel="stylesheet" href="css/laydate.min.css">
<link type="text/css" rel="stylesheet" href="css/layui.css">
<script src="js/layui.js"></script>
<script type="text/javascript" src="js/jquery-2.0.3.min.js"></script>

</head>
<script>
  $(window).load(function() {
  	$("#loader").hide();
  });
</script> 
<script type="text/javascript">
	$(function(){
		$("#mainlistsubmit").click(function(){
			var named = $("#named").val();
			var characterId = $("#characterId").val();
			var placetypeId = $("#placetypeId").val();
			var areaId = $("#areaId").val();
			$("#query").submit();
		});
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
<div class="title_tj">
 <a class="layui-btn layui-btn-small" style="background:#3b66cc;" href="queryItems_test.do">返回</a> 
</div>
<div id="loader"></div>
<div class="contain_tj">
	<div class="content_search">
		<form id="query" name="query" action="${pageContext.request.contextPath }/queryRepeaterInfo.do" method="get">
            <div class="groupForm">
            	<label>终端编号：</label>
                <input id="repeaterMac" name="repeaterMac" type="text" placeholder="请输入" value="${repeaterMac }"/>
            </div>
             <label>
                <input class="ser" id="mainlistsubmit" type="submit"/>
             </label>
          </form>   
      </div>
	<div class="content_table">
		<div class="dydc">
			<a href="javascript:void(0);" id="print">打印</a> | <a
				href="javascript:void(0);" id="daochu">导出</a>
		</div>
		<div class="scroll_table">
			<table id="idData" class="idData">
				<tr>
					<th>序号</th>
					<th>终端编号</th>
					<!-- <th>最后心跳时间</th> -->
					<th>状态</th>
					<th>主备电状态</th>
					<th>时间</th>
					<th>操作</th>
				</tr>
				<c:forEach items="${repeaterList}" var="vMap" varStatus="vs" step="1" >
					<tr class="tr">
						<td>${vs.index+1}</td>
						<td>${vMap.repeaterMac }</td>
						<td>${vMap.netStates }</td>
						<td>${vMap.hostStates }</td>
						<td>${vMap.repeaterTime }</td>
						<td>--<%-- <a href="ckxq.do?phone=${vMap.principal1Phone }" class="ckxq">查看详情</a> --%>
						</td>
					</tr>
				</c:forEach>				
			</table>
		</div>
		<%-- <div class="PageNum">
			<span>
				<span class="r inb_a page_b">
					<c:forEach items="${pagination.pageView }" var="pageView">
						${pageView }
					</c:forEach> 
				</span>			
			</span>
		</div> --%>
	</div></div>
</body>
</html>
<script language="javascript" src="js/jquery.jqprint.js"></script>
<script src="js/jquery-migrate-1.2.1.min.js"></script>
<script type="text/javascript">
	$(document).ready(function() {
		$("#print").click(function() {
			$("#idData").jqprint();
		});
	});
</script>
<script src="js/Blob.js"></script>
<script src="js/FileSaver.min.js"></script>
<script src="js/ExportUtil.js"></script>
<script type="text/javascript" src="js/laydate.dev.js"></script>

