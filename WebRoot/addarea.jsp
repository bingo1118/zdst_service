<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<!DOCTYPE html>
<html>
<head>
<title>添加区域</title>
<style type="text/css">
.title_tj {
    padding-left: 10px;
    margin-top: 10px;
}
</style>
<link type="text/css" rel="stylesheet" href="css/style1.css">
<link type="text/css" rel="stylesheet" href="css/layui.css">
<script type="text/javascript" src="js/jquery.js"></script>


</head>

<body onload="getAreaIds();">
 <div class="header">
	<div class="top clearfix">
    	<div class="logo fl">
        	<img src="images/logo.png" title="智慧云消防大数据管理平台">
        </div>
        <div class="user fr">
        	<span> </span>
        	<span id="timer"> </span>
            <a href="http://www.cloudlinks.cn/view/ReSetPwd.htm" target="_blank" class="xgmm">修改密码</a>
            <a href="loginOut.do" class="zc">注销</a>
        </div>
    </div>
</div>
<div class="title_tj">
  <a class="layui-btn layui-btn-small" style="background:#3b66cc; color:#fff;" href="queryItems_test.do">返回</a> 
</div>
<div class="contain_tj">
		<div class="conten_tj">
			<div class="conten_tj_tit">创建区域</div>
			<div class="conten_txt">
<form class="layui-form" id="add" action="">
					<div class="layui-form-item">
    <label class="layui-form-label">上级区域</label>
    <div class="layui-input-inline">
      <select name="interest" lay-filter="aihao">
        <option value=""></option>
        <option value="0">写作</option>
        <option value="1" selected="">阅读</option>
        <option value="2">游戏</option> 
        <option value="3">音乐</option>
        <option value="4">旅行</option>
      </select>
    </div>
  </div>
   <div class="layui-form-item">
    <label class="layui-form-label">新区域名称</label>
    <div class="layui-input-inline">
      <input type="text" name="username" lay-verify="required|title" placeholder="请输入" autocomplete="off" class="layui-input">
    </div>
    <span style="color:#009688;line-height: 35px;">*名称在1--60个字符之间</span>
  </div>
  <div class="layui-form-item">
    <div class="layui-input-block">
      <button class="layui-btn" lay-submit lay-filter="formDemo">立即提交</button>
      <button type="reset" class="layui-btn layui-btn-primary">重置</button>
    </div>
  </div>
				</form>
			</div>
		</div>
	</div>
	</div>
</body>
</html>

<script type="text/javascript" src="js/jquery1.42.min.js"></script>
<script type="text/javascript" src="js/check.js"></script>
<script src="js/layui.js" charset="utf-8"></script>
<!-- 注意：如果你直接复制所有代码到本地，上述js路径需要改成你本地的 -->
<script>
layui.use(['form', 'layedit', 'laydate'], function(){
  var form = layui.form
  ,layer = layui.layer
  ,layedit = layui.layedit
  ,laydate = layui.laydate;

  
 
  //自定义验证规则
  form.verify({
    title: function(value){
      if(value.length>60){
        return '区域名称不能多于60个字符';
      }
    }
    ,content: function(value){
      layedit.sync(editIndex);
    }
  });

  
  //监听提交
  form.on('submit(demo1)', function(data){
    layer.alert(JSON.stringify(data.field), {
      title: '最终的提交信息'
    })
    return false;
  });
  
  
});
</script>

