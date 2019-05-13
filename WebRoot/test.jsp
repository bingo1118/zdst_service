<%@page import="com.cloudfire.dwr.push.PushMessageUtil"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="../WEB-INF/page/common/common_header.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<title>建筑单位-资料添加</title>
<link href="css/bootstrap.min.css" rel="stylesheet">
<link type="text/css" rel="stylesheet" href="css/style.css">
<script type="text/javascript" src="js/jquery-2.0.3.min.js"></script>
<link href="css/fileinput.css" media="all" rel="stylesheet" type="text/css" />
<script src="js/fileinput.js" type="text/javascript"></script>
<script src="js/bootstrap.min.js" type="text/javascript"></script>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link href="${base}/resource/css/pushmsg.css" rel="stylesheet" type="text/css" />
<script type="text/javascript" src="${base}/resource/js/jquery.js"></script>
<script type="text/javascript" src="${base}/resource/js/openMsgDialog.js"></script>
<script type="text/javascript" src="${base}/dwr/engine.js"></script>
<script type="text/javascript" src="${base}/dwr/util.js"></script>
<script type="text/javascript" src="${base}/dwr/interface/TestPush.js"></script>
</head>

<body style=" overflow:auto" onload="onPageLoad();dwr.engine.setActiveReverseAjax(true);dwr.engine.setNotifyServerOnPageUnload(true,true);">
<div class="header">
	<div class="top clearfix">
    	<div class="logo fl">
        	<img src="images/logo.png" title="智能云消防大数据管理平台">
        </div>
        <div class="user fr">
        	<span>你好！张三</span>
            <a href="" class="grzx">个人中心</a>
            <a href="" class="xgmm">修改密码</a>
            <a href="login.html" class="zc">注销</a>
        </div>
    </div>
</div>
<div class="container">
    <div class="company">
    	<div class="company_jibenjiliao">
        	<h2>基本资料</h2>
            <div class="company_jibenjiliao_div clearfix">
            	<div class="col-zdy-tb">
                	<span class="zdy-label">单位名称：</span>
                    <div class="zdy-c1">
                    	<input type="text"/>
                    </div>
                </div>
                <div class="col-zdy-tb">
                	<span class="zdy-label">单位性质：</span>
                    <div class="zdy-c1">
                    	<input type="text"/>
                    </div>
                </div>
            </div>
            <div class="company_jibenjiliao_div clearfix">
            	<div class="col-zdy-tb">
                	<span class="zdy-label">单位电话：</span>
                    <div class="zdy-c1">
                    	<input type="text"/>
                    </div>
                </div>
                <div class="col-zdy-tb">
                	<span class="zdy-label">邮箱：</span>
                    <div class="zdy-c1">
                    	<input type="text"/>
                    </div>
                </div>
            </div>
            <div class="company_jibenjiliao_div clearfix">
            	<div class="col-zdy-tb">
                	<span class="zdy-label">营业执照注册号：</span>
                    <div class="zdy-c1">
                    	<input type="text"/>
                    </div>
                </div>
                <div class="col-zdy-tb">
                	<span class="zdy-label">职工人数：</span>
                    <div class="zdy-c1">
                    	<input type="text"/>
                    </div>
                </div>
            </div>
            <div class="company_jibenjiliao_div clearfix">
            	<div class="col-zdy-tb">
                	<span class="zdy-label">占地面积：</span>
                    <div class="zdy-c1">
                    	<input type="text"/>
                    </div>
                </div>
                <div class="col-zdy-tb">
                	<span class="zdy-label">建筑面积：</span>
                    <div class="zdy-c1">
                    	<input type="text"/>
                    </div>
                </div>
            </div>
            <div class="company_jibenjiliao_div clearfix">
            	<div class="col-zdy-tb">
                	<span class="zdy-label">仓库面积：</span>
                    <div class="zdy-c1">
                    	<input type="text"/>
                    </div>
                </div>
                <div class="col-zdy-tb">
                	<span class="zdy-label">成立时间：</span>
                    <div class="zdy-c1">
                    	<input type="text"/>
                    </div>
                </div>
            </div>
            <div class="company_jibenjiliao_div clearfix">
            	<div class="col-zdy-tb">
                	<span class="zdy-label">经度：</span>
                    <div class="zdy-c1">
                    	<input type="text"/>
                    </div>
                </div>
                <div class="col-zdy-tb">
                	<span class="zdy-label">维度：</span>
                    <div class="zdy-c1">
                    	<input type="text"/>
                    </div>
                </div>
            </div>
        </div>
    	<div class="company_jibenjiliao clearfix" style="margin-top:15px;">
        	<h2>单位简介</h2>
        	<div class="abt1">
            	<span>单位正门：</span>
                <div class="img">
                	<form enctype="multipart/form-data">
                        <div class="form-group">
                            <input id="file-1" type="file" multiple class="file" data-overwrite-initial="false" data-min-file-count="2">
                        </div>
                    </form>
                </div>
            </div>
            <div class="abt1">
            	<span>安全出口及消火栓位置平面图：</span>
                <div class="img">
                	<form enctype="multipart/form-data">
                        <div class="form-group">
                            <input id="file-1" type="file" multiple class="file" data-overwrite-initial="false" data-min-file-count="2">
                        </div>
                    </form>
                </div>
            </div>
            <div class="abt1">
            	<span>消防安全重点部位照片：</span>
                <div class="img">
                	<form enctype="multipart/form-data">
                        <div class="form-group">
                            <input id="file-1" type="file" multiple class="file" data-overwrite-initial="false" data-min-file-count="2">
                        </div>
                    </form>
                </div>
            </div>
            <div class="abt1">
            	<span>火警疏散平面图：</span>
                <div class="img">
                	<form enctype="multipart/form-data">
                        <div class="form-group">
                            <input id="file-1" type="file" multiple class="file" data-overwrite-initial="false" data-min-file-count="2">
                        </div>
                    </form>
                </div>
            </div>
        </div>
        <div class="company_jibenjiliao" style="margin-top:20px;">
        	<textarea id="description" class="form-control" rows="3" placeholder="在这里填写单位详情" style="margin:20px auto; padding:15px; width:1100px;"></textarea>
        </div>
        <div class="company_btn">
        	<button>提交</button>
            <button>取消</button>
        </div>
    </div>
</div>
</body>
</html>
<div id="tip_alarm">
	<div class="tip_alarm clearfix">
    	<div class="close"></div>
    	<div class="tip_alarm_l fl">
            1
        </div>
        <div class="tip_alarm_r fr" >
            <table border="0" cellpadding="0" cellspacing="0">
            	<tr>
                	<th>设备名称</th>
                    <th>报警时间</th>
                    <th>报警类型</th>
                    <th>报警地点</th>
                    <th>警情描述</th>
                </tr>
            </table>
        </div>
    </div>
</div>

<div class="popup_mask" id="popup_mask"></div>
<div class="popup" id="popup_chuli" style=" width:750px;">
    	<h1 style="background-color:#ecebeb;">查看报警</h1>
        <div id="stepBar" class="ui-stepBar-wrap">
            <div class="ui-stepBar">
                <div class="ui-stepProcess"></div>
            </div>
            <div class="ui-stepInfo-wrap">
                <table class="ui-stepLayout" border="0" cellpadding="0" cellspacing="0">
                    <tr>
                        <td class="ui-stepInfo">
                            <a class="ui-stepSequence">1</a>
                            <p class="ui-stepName">报警已接受<br/>正在查证<br/><span>2017-01-13  13：54：21 </span></p>
                        </td>
                        <td class="ui-stepInfo">
                            <a class="ui-stepSequence">2</a>
                            <p class="ui-stepName">查证结果为火警<br/>地址：天河区农信大厦<br/><span>2017-01-13  13：54：21 </span></p>
                        </td>
                        <td class="ui-stepInfo">
                            <a class="ui-stepSequence">3</a>
                            <p class="ui-stepName">正在出警<br/><span>2017-01-13  13：54：21 </span></p>
                        </td>
                        <td class="ui-stepInfo">
                            <a class="ui-stepSequence">4</a>
                            <p class="ui-stepName">火警已处理<br/><span>2017-01-13  13：54：21 </span></p>
                        </td>
                        <td class="ui-stepInfo">
                            <a class="ui-stepSequence">5</a>
                            <p class="ui-stepName">确认处理</p>
                        </td>
                    </tr>
                </table>
            </div>
        </div>
        <div class="popop_btn">
        	<a href="javascript:void(0);" class="guanbi">取消</a>
            <a href="javascript:void(0);" class="qd">确定</a>
        </div>
    </div>
<script src="js/popup.js"></script>
<script type="text/javascript" src="js/jquery.easing.1.3.js"></script>
<script type="text/javascript" src="js/stepBar.js"></script>
<script>
function handle(){
		showDiv($('#popup_chuli'));
		showDiv($('#popup_mask'));
		$("#popup").fadeOut();
		stepBar.init("stepBar", {
			step :4,
			change : true,
			animation : true
		});
	}
//报警实况的提示的隐藏和显示
$(function(){
	$(".close").click(function(){
		$("#tip_alarm").animate({right:"-512px"});
	});
	$(".tip_alarm_l").click(function(){
		$("#tip_alarm").animate({right:"-2px"});
	});
});
</script>
<script>
    $("#file-1").fileinput({
        allowedFileExtensions : ['jpg', 'png','gif'],
	});
</script>

<script type="text/javascript">
	/** 首先加载 绑定UserId*/
	function onPageLoad() {
		TestPush.onPageLoad("${userId }");
	}
	function clickEvent() {
		//alert("success!!!");
	}
	function showMessage(sendMessages, clickEvent) {
		//alert(sendMessages);
	}
</script>
<script src="js/ajax.js"></script>


