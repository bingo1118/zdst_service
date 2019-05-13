/**
 * jQuery工具插件
 * @param $
 */
(function($){
	/** 添加jQuery对象方法(把表单中的参数转化成JSON对象) */
	$.fn.form2JSON = function(){
		/** 把表单中的input元素序化成数组 [{name="user.name", value="admin"}]*/
		var paramArray = this.serializeArray();
		/** 定义数组 */
		var jsonArr = new Array();
		/** 迭代数组 */
		$.each(paramArray, function(){
			/** this: {name="user.name", value="admin"} */
			jsonArr.push('"'+ this.name +'"' + ":" + '"' + this.value + '"');
		});
		/** 把json格式的字符串转化成json对象 */
		return $.parseJSON("{" + jsonArr.join(",") + "}");
	};
})(jQuery);