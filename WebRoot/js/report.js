var nowDate=new Date();
var ToDay=nowDate;
//设置7天报日期范围
function day_n(n) {
	"use strict";
	var today=ToDay;
	var day_n_ago=new Date(today);
	var day_1_ago=new Date(today);
	day_n_ago.setDate(today.getDate()-n);
	day_1_ago.setDate(today.getDate()-1);
	
	$('#StartDate').datebox('setValue',day_n_ago.Format('yyyy-MM-dd'));
    $('#EndDate').datebox('setValue',day_1_ago.Format('yyyy-MM-dd'));
	
}


function myPrint(obj){
    "use strict";
	$('#DetectorSelect').hide();
	$('#DetectorSelected').show();
	var style="<style>@media print{.NextPage{page-break-after:always}} </style>";
	
	obj=$('#print');
	var newWindow=window.open("打印窗口","_blank");//打印窗口要换成页面的url
	var docStr = style+obj.html();
	$('#DetectorSelect').show();
	$('#DetectorSelected').hide();
	newWindow.document.write(docStr);
    newWindow.document.close();
    newWindow.print();
    //newWindow.close();
	
}

Date.prototype.Format = function (fmt) { //javascript时间日期函数
    "use strict";
	var o = {
        "M+": this.getMonth() + 1, //月份 
        "d+": this.getDate(), //日 
        "h+": this.getHours(), //小时 
        "m+": this.getMinutes(), //分 
        "s+": this.getSeconds(), //秒 
        "q+": Math.floor((this.getMonth() + 3) / 3), //季度 
        "S": this.getMilliseconds() //毫秒 
    };
    if (/(y+)/.test(fmt)) {
		fmt = fmt.replace(RegExp.$1, (this.getFullYear() + "").substr(4 - RegExp.$1.length));
	}
    for (var k in o) {
    	if (new RegExp("(" + k + ")").test(fmt)) {
			fmt = fmt.replace(RegExp.$1, (RegExp.$1.length == 1) ? (o[k]) : (("00" + o[k]).substr(("" + o[k]).length)));
		}
	}
    return fmt;
};