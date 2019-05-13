<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt"  prefix="fmt"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8">
<title>无标题文档</title>
<link rel="shortcut icon" href="images/fire.ico" />
<link type="text/css" rel="stylesheet" href="css/page.css">
<link type="text/css" rel="stylesheet" href="css/style.css">
<link type="text/css" rel="stylesheet" href="css/laydate.css">
<link type="text/css" rel="stylesheet" href="css/laydate.min.css">
<script type="text/javascript" src="js/jquery-2.0.3.min.js"></script>
<script type="text/javascript" src="js/page.js"></script>
<script>
  $(window).load(function() {
  	$("#loader").hide();
  });
</script>
<script type="text/javascript">
	$(function(){
		$("#faultsumit").click(function(){
			var repeaterMac = $("#repeaterMac").val();
			var faultCode = $("faultCode").val();
			var J_xl_1 = $("#J_xl_1").val();
			var J_xl_2 = $("#J_xl_2").val();
			if($("#J_xl_1").val()!=""&&$("#J_xl_2").val()==""){
				alert("结束时间不能为空，请输入正确时间");
				return false;
			} if($("#J_xl_1").val()==""&&$("#J_xl_2").val()!=""){
				alert("开始时间不能为空，请输入正确时间");
				return false;
			}if($("#J_xl_1").val() > $("#J_xl_2").val()){
				alert("开始时间不能大于结束时间");
				return false;
			}
			
			$("#query").submit();
		});
	});
</script>
</head>

<body style="width:100%;background-color:#fff;min-width:100%;overflow-y: scroll;">
<div id="loader"></div>
<form id="query" name="query" action="${pageContext.request.contextPath }/faultinfo3.do" method="get">
	<div class="content_search">
	<div class="groupForm" style="width: 35%;">
         <label  class='data_label'>报警日期：</label> 
         	<input id="repeaterMac" type="hidden" name="repeaterMac" value="${repeaterMac}"/>
         	<input id="faultCode" type="hidden" name="faultCode" value="${faultCode}"/>
         	 <div class="input_data" style='width: 35%;'>
             <input type="text" readOnly="true" name="J_xl_1" id="J_xl_1" placeholder="起始日期" value="${J_xl_1}" />
             </div>
               <div class='fh'> &nbsp;-&nbsp;</div>
       <div class="input_data"  style='width: 35%;'>    
	             <input type="text" readOnly="true" name="J_xl_2" id="J_xl_2" placeholder="结束日期" value="${J_xl_2 }"/>
	   </div>
</div>
         <label style="position:relative;  top:-16px;"  id='search_btn'>
                <input id="faultsumit" class="ser" type="submit"/>
                    <!-- <button class="tianjia" onClick="showDiv($('#popup'));showDiv($('#popup_mask'))"></button> -->
         </label>
    </div>
</form>
<div class="content_table">
<div class="back"><!-- <a href="javascript:history.back();" id="back"> --><a href="faultinfo2.do?repeaterMac=${repeaterMac}" id="back">返回</a></div> <!-- edit by yfs @2017.11.10 -->
            	<div class="dydc">
                    <a href="javascript:void(0);" id="print">打印</a>
                    |
                    <a href="javascript:void(0);" id="daochu">导出</a>
           		</div>
           		<div class="scroll_table">
	            	<table id="idData">
	                	<tr>
	                    	<th>序号</th>
	                        <th>设备名称</th>
	                        <th>设备ID</th>
	                        <th>报警类型</th>
	                        <th>报警时间</th>
	                        <th>处理状态</th>
	                        <th>信息备注时间</th>
	                        <th>设备的故障原因</th>
	                        <th>处理人</th>
	                        <th>操作</th>
	                    </tr>
	                    <c:forEach items="${pagination.list}" var="item" varStatus="status">
		                    <tr>
		                    	<td>${item.row}</td>
		                        <td>${item.faultDevDesc}</td>
		                        <%-- <td class="faultCode">${item.faultCode}</td> --%>
		                        <td class="repeaterMac">${item.repeaterMac}</td>
		                        <td>${item.faultType}</td>
		                        <td class="alarmTime">${item.faultTime}</td>
		                        <td class="faultType">
		                        	<c:if test='${item.faultType.contains("消除")}'>已处理</c:if>
		                        	<c:if test='${item.faultType.contains("消除") == false}'>未处理</c:if>
		                        </td>
		                        <td  class="dealTime">${item.dealTime}</td>
		                        <td>${item.dealText}</td>
		                        <td>${item.dealUser}</td>
		                        <td class="dealTd">
		                        	<c:if test='${item.faultType.contains("消除")}'>已处理</c:if>
		                        	<c:if test='${item.faultType.contains("消除") == false}'><a href="javascript:lookWired();" class="dealWired">处理</a></c:if>
		                        </td>
		                    </tr>
	                     </c:forEach>
	                </table>
                </div>
                <div class="PageNum">
					<span>
						<span class="r inb_a page_b">
							<c:forEach items="${pagination.pageView }" var="pageView">
								${pageView }
							</c:forEach> 
						</span>			
					</span>
				</div>
            </div>
</body>
</html>
<!-- <script type="text/javascript" src="js/jsfenye.js"></script> -->
<script language="javascript" src="js/jquery.jqprint.js"></script>
<script src="js/jquery-migrate-1.2.1.min.js"></script>
<script type="text/javascript"> 
		$(document).ready(function() { 
			$("#print").click(function(){ 
				$(".content_table table").jqprint(); 
			}); 
		}); 
</script> 
<script src="js/Blob.js"></script>
<script src="js/FileSaver.min.js"></script>
<script src="js/ExportUtil.js"></script>
<script>
		$(function(){
			$(".content_table tr:even").css('background-color','#f1f2f9');
			$("#daochu").click(function(){
				ExportUtil.toExcel("idData");	
			});	
		});
		function lookWired(){
			showDiv($('#chuliBaojing'));
			showDiv($('#popup_mask'));
		}
</script>
<div class="popup_mask" id="popup_mask"></div>
<div class="popup popup_baojing" id="chuliBaojing" >
    	<h1>处理报警</h1>
        <div class="popop_content">
        	<form class="wired">
        		<p>
        			<label>处理人：</label>
        			<input name="name" type="text" id="handlePerson"/>
        		</p>
        		<p>
        			<label>报警原因：</label>
        			<textarea rows="5" cols="" id="treatmentReasons"></textarea>
        		</p>       	

        	</form>
        </div>
        <div class="popop_btn">
        	<a href="javascript:void(0);" class="guanbiChuWired">关闭</a>
            <a href="javascript:void(0);" class="querenWired">确认</a>
        </div>
</div>
<script src="js/popup.js"></script>
<script type="text/javascript" src="js/laydate.dev.js"></script>
<script type="text/javascript">
        laydate({
            elem: '#J_xl_1'
        });
        laydate({
            elem: '#J_xl_2'
        });
</script>
<script type="text/javascript" src="js/wired.push.js"></script>