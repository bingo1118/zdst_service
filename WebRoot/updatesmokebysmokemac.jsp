<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta charset="utf-8">
<title>无标题文档</title>
<link type="text/css" rel="stylesheet" href="css/layui.css">
<script type="text/javascript" src="js/jquery-2.0.3.min.js"></script>
<script type="text/javascript">
	$(function() {

		$("#button_update").click(function() {
			if ($("#smokeName").val() == "" || $("#smokeName").val() == null) {
				alert("设备名称不能为空");
				return false;
			}
			if ($("#address").val() == "" || $("#address").val() == null) {
				alert("地址不能为空");
				return false;
			}

			$("#updateSmoke").submit();

		});

	});
</script>
<style type="text/css">
.content_table {
	width: 500px;
	margin-left: auto;
	margin-right: auto;
}

.change_title {
	margin: 15px 0px;
	text-align: center;
	font-size: 20px;
	font-weight: bold;
}
</style>

</head>

<body style="width:100%;background-color:#fff;min-width:100%;">
	<div class="content_table">
		<h2 class="change_title">更新设备信息</h2>
		<form class="layui-form" id="updateSmoke"
			action="${pageContext.request.contextPath }/updateSmokeByMac.do"
			method="post">
			<div class="layui-form-item">
				<label class="layui-form-label">设备mac：</label>
				<div class="layui-input-block">
					<input type="text" class="layui-input" readonly="readonly"
						id="smokeMac" name="smokeMac" value="${entity.smokeMac}"
						style="background: #f1eaea;" /> <span><b
						style="color:green;"><%=request.getAttribute("message") == null ? "" : request
					.getAttribute("message")%></b> </span>
				</div>
			</div>
			<div class="layui-form-item">
				<label class="layui-form-label">设备名称：</label>
				<div class="layui-input-block">
					<input class="layui-input" id="smokeName" name="smokeName"
						value="${entity.smokeName}" />
				</div>
			</div>
			<div class="layui-form-item">
				<label class="layui-form-label">设备状态：</label>
				<div class="layui-input-block">
				<c:if test="${entity.lineState==1 }">
					<input type="text" class="layui-input" readonly="readonly" id="lineState" name="lineState" value="在线"
									style="background: #f1eaea;" />
				</c:if>
				<c:if test="${entity.lineState==0 }">
					<input type="text" class="layui-input" readonly="readonly" id="lineState" name="lineState" value="离线"
									style="background: #f1eaea;" />
				</c:if>
					<%-- <c:forEach items="${deviceNetState }" var="deviceNetState">
						<c:choose>
							<c:when test="${entity.lineState == deviceNetState.netState}">
								<input type="text" class="layui-input" readonly="readonly"
									id="lineState" name="lineState"
									value="${deviceNetState.netStateName}"
									style="background: #f1eaea;" />
							</c:when>
						</c:choose>
					</c:forEach> --%>
				</div>
			</div>
			<div class="layui-form-item">
				<label class="layui-form-label">添加时间：</label>
				<div class="layui-input-block">
					<input class="layui-input" type="text" readonly="readonly"
						id="addTime" name="addTime" value="${entity.addTime}"
						style="background: #f1eaea;" />
				</div>
			</div>
			<div class="layui-form-item">
				<label class="layui-form-label">地址：</label>
				<div class="layui-input-block">
					<input class="layui-input" id="address" name="address"
						value="${entity.address}" />
				</div>
			</div>
			<div class="layui-form-item">
				<label class="layui-form-label">负责人1：</label>
				<div class="layui-input-block">
					<input class="layui-input" id="principal1" name=principal1
						value="${entity.principal1}" />
				</div>
			</div>
			<div class="layui-form-item">
				<label class="layui-form-label">联系方式1：</label>
				<div class="layui-input-block">
					<input class="layui-input" id="principal1Phone"
						name="principal1Phone" value="${entity.principal1Phone}" />
				</div>
			</div>
			<div class="layui-form-item">
				<label class="layui-form-label">联动范围：</label>
				<div class="layui-input-block">
					<input class="layui-input" id="linkageDistance"
						name="linkageDistance" value="${entity.linkageDistance}" />
				</div>
			</div>
			<div class="layui-form-item">
				<label class="layui-form-label">所属区域：</label>
				<div class="layui-input-block">
					<select id="areaName" name="areaName">
						<option value="" check="checked">--请选择--</option>
						<c:forEach items="${areaAndRepeaters }" var="areaAndRepeaters">
							<c:if test="${entity.areaName ==areaAndRepeaters.areaId  }">
								<option value="${areaAndRepeaters.areaId }" selected="selected">${areaAndRepeaters.areaName}</option>
							</c:if>
							<c:if test="${entity.areaName !=areaAndRepeaters.areaId }">
								<option value="${areaAndRepeaters.areaId }">${areaAndRepeaters.areaName}</option>
							</c:if>
						</c:forEach>
					</select>
				</div>
			</div>
			<div class="layui-form-item">
				<div class="layui-input-block">
					<button type="submit" class="layui-btn" lay-filter="demo1">提交</button>
					<button type="button" class="layui-btn layui-btn-primary"
						onclick="close_window();">关闭</button>
				</div>
			</div>
		</form>
	</div>



</body>
</html>
<script src="js/layui.js"></script>
<script>
layui.use(['form', 'layedit', 'laydate'], function(){});
</script>
<script>
if(<%=request.getAttribute("message")==null %>){}
else{}
	var index = parent.layer.getFrameIndex(window.name);
	function close_window() {
		parent.layer.close(index);
	}
</script>