/**
 * jQuery时间插件
 * 匿名函数
 */
(function($){
	/** $ jQuery核心函数 */
	/** 为jQuery函数添加对象方法 */
	$.prototype.aa = function(){
		alert("aa");
	};
	/** 为jQuery函数批量添加对象方法 */
	$.fn.extend({
		/** 显示系统运行时间 */
		runtime : function(){
			// 2016年11月01日 星期二 10:10:10
			/** 创建日期对象 */
			var d = new Date();
			/** 定义数组 */
			var arr = new Array();
			/** 获取年 */
			arr.push(d.getFullYear() + "年");
			/** 获取月 0-11 */
			arr.push($.calc(d.getMonth() + 1) + "月");
			/** 获取日 */
			arr.push($.calc(d.getDate()) + "日");
			
			/** 获取星期几 0(星期日)-6(星期六) */
			arr.push("&nbsp;" + $.weeks[d.getDay()] + "&nbsp;");
			
			/** 获取小时 */
			arr.push($.calc(d.getHours()) + ":");
			/** 获取分钟 */
			arr.push($.calc(d.getMinutes()) + ":");
			/** 获取秒 */
			arr.push($.calc(d.getSeconds()));
			
			/** join方法把数组元素用逗号分隔开，返回一个字符串 */
			this.html(arr.join(""));
			/** 声明一个局部变量 */
			var obj = this;
			/** 延迟的定时器
			 * 第一个参数：函数调用字符串|回调函数
			 * 第二个参数：毫秒数
			 *  */
			setTimeout(function(){
				obj.runtime();
			}, 1000);
			
		},
		bb : function(){
			alert("bb");
		},
		
		/** 验证码倒计时方法 */
		downcount : function(msg, seconds){
			if (seconds > 1){
				/** 自减 */
				seconds--;
				/** 替换消息中的占位符 */
				var msgText = msg.replace("{0}", seconds);
				/** 禁用按钮 */
				$(this).attr("disabled", true);
				/** 设置按钮的value属性值 */
				$(this).val(msgText);
				
				var t = this;
				/** 开启延迟的定时器 */
				setTimeout(function(){
					t.downcount(msg, seconds);
				}, 1000);
			}else{
				/** 启用按钮 */
				$(this).attr("disabled", false)
				       .val("获取验证码");
			}
		}

		
	});
	
	/** 为jQuery添加静态方法 */
	$.each5 = function(arr, callback){
		for (var i = 0; i < arr.length; i++){
			// call函数调用 
			// 第一参数为主调
			callback.call(arr[i], i, arr[i]);
		}
	};
	
	
	
	/** 为jQuery批量添加静态方法 */
	$.extend({
		weeks : ["星期日","星期一","星期二","星期三","星期四","星期五","星期六"],
		calc : function(num){
			return num > 9 ? num : "0" + num;
		}
	});
})(jQuery);