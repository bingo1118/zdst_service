var width = $(window).width() - 150;
var height = $(window).height() - 100;
$("#menu_dy1, #menu_dl1, #menu_ldl1, #menu_wd1").css("width", width);
$("#menu_dy1, #menu_dl1, #menu_ldl1, #menu_wd1").css("height", height);
$(".menu").css("height", height - 2);
$(window).resize(function() {
	var width = $(window).width() - 150;
	var height = $(window).height() - 100;
	$("#menu_dy1, #menu_dl1, #menu_ldl1, #menu_wd1").css("width", width);
	$("#menu_dy1, #menu_dl1, #menu_ldl1, #menu_wd1").css("height", height);
	$(".menu").css("height", height - 30);
});

// 电压折线图
function dianya(mac) {
	new HighchartsPager('menu_dy', 20, {
		credits : {
			enabled : false
		},
		chart : {
			type : 'line'
		},
		title : {
			text : '电压折线图('+mac+')'
		},
		xAxis : {
			categories : voltageTime
		},
		yAxis : {
			title : {
				text : '电压值 (V)'
			},
			plotLines : [ {
				value : 0,
				width : 1,
				color : '#808080'
			} ]
		},
		tooltip : {
			valueSuffix : 'V'
		},
		legend : {
			layout : 'vertical',
			align : 'right',
			verticalAlign : 'middle',
			borderWidth : 0
		},
		series : [ {
			name : '电压1',
			data : voltageDataOne,
			color : '#90ed7d'
		}, {
			name : '电压2',
			data : voltageDataTwo,
			color : '#434348'
		}, {
			name : '电压3',
			data : voltageDataThree,
			color : '#7cb5ec'
		} ]
	});
}

// 电流折线图
function dianliu(mac) {
	new HighchartsPager('menu_dl', 20, {
		credits : {
			enabled : false
		},
		chart : {
			type : 'line'
		},
		title : {
			text : '电流折线图('+mac+')'
		},
		xAxis : {
			categories : currentTime
		},
		yAxis : {
			title : {
				text : '电流值 (A)'
			},
			plotLines : [ {
				value : 0,
				width : 1,
				color : '#808080'
			} ]
		},
		tooltip : {
			valueSuffix : 'A'
		},
		legend : {
			layout : 'vertical',
			align : 'right',
			verticalAlign : 'middle',
			borderWidth : 0
		},
		series : [ {
			name : '电流1',
			data : currentDataOne,
			color : '#90ed7d'
		}, {
			name : '电流2',
			data : currentDataTwo,
			color : '#434348'
		}, {
			name : '电流3',
			data : currentDataThree,
			color : '#7cb5ec'
		}, {
			name : '电流4',
			data : currentDataFour,
			color : '#7cb5dc'
		} ]
	});

}

// 漏电流折线图
function loudianliu(mac) {
	new HighchartsPager('menu_ldl', 20, {
		credits : {
			enabled : false
		},
		chart : {
			type : 'line'
		},
		title : {
			text : '漏电流折线图('+mac+')'
		},
		xAxis : {
			categories : leakageCurrentTime
		},
		yAxis : {
			title : {
				text : '漏电流值 (mA)'
			},
			plotLines : [ {
				value : 0,
				width : 1,
				color : '#808080'
			} ]
		},
		tooltip : {
			valueSuffix : 'mA'
		},
		legend : {
			layout : 'vertical',
			align : 'right',
			verticalAlign : 'middle',
			borderWidth : 0
		},
		series : [ {
			name : '漏电流1',
			data : leakageCurrentDataOne,
			color : '#90ed7d'
		}, {
			name : '漏电流2',
			data : leakageCurrentDataTwo,
			color : '#434348'
		}, {
			name : '漏电流3',
			data : leakageCurrentDataThree,
			color : '#7cb5ec'
		} ]
	});
}

// 温度折线图
function temperature(mac) {
	new HighchartsPager('menu_wd', 20, {
		credits : {
			enabled : false
		},
		chart : {
			type : 'line'
		},
		title : {
			text : '温度折线图('+mac+')'
		},
		xAxis : {
			categories : temperatureTime
		},
		yAxis : {
			title : {
				text : '温度 (℃)'
			},
			plotLines : [ {
				value : 0,
				width : 1,
				color : '#808080'
			} ]
		},
		tooltip : {
			valueSuffix : '℃'
		},
		legend : {
			layout : 'vertical',
			align : 'right',
			verticalAlign : 'middle',
			borderWidth : 0
		},
		series : [ {
			name : '温度1',
			data : temperatureDataOne,
			color : '#90ed7d'
		}, {
			name : '温度2',
			data : temperatureDataTwo,
			color : '#434348'
		}, {
			name : '温度3',
			data : temperatureDataThree,
			color : '#7cb5ec'
		}, {
			name : '温度4',
			data : temperatureDataFour,
			color : '#7cb5ed'
		} ]
	});
}

var mac;// mac的数值
/** 6代表电压 * */
var json6;
var voltageTime = [];// 电压时间
var voltageDataOne = [], voltageDataTwo = [], voltageDataThree = [];// 电压数值
/** 7代表电流 * */
var json7;
var currentTime = [];// 电流时间
var currentDataOne = [], currentDataTwo = [], currentDataThree = [], currentDataFour = [];// 电流数值
/** 8代表漏电流 * */
var json8;
var leakageCurrentTime = [];// 漏电流时间
var leakageCurrentDataOne = [], leakageCurrentDataTwo = [], leakageCurrentDataThree = [];// 漏电流数值
/** 9代表温度 * */
var json9;
var temperatureTime = [];// 温度时间
var temperatureDataOne = [], temperatureDataTwo = [], temperatureDataThree = [], temperatureDataFour = [];// 温度数值
$(function() {
	$(".myData6").click(function() {
		$(".myData6").attr('disabled',"true");
		$('#load2').css('display',"block");
				mac = $(this).attr("name");
				var tempTime, tempData;
				voltageDataOne.length = 0;
				voltageDataTwo.length = 0;
				voltageDataThree.length = 0;
				voltageTime.length = 0;
				$.ajax({
					type : "GET",
					url : "electricValue.do?type=6&mac=" + mac,
					async : true,
					dataType : "json",
					success : function(data) {
						$(".myData6").removeAttr("disabled"); 
						$('#load2').hide();
						// alert("成功返回来的数据" + data);
						if(data==null||data==""){
							alert("无数据");
							return false;
						}
						showDiv($('#menu_dy1'));showDiv($('#popup_mask'));
						$("#menu_dy").empty();
						json6 = eval(data);
						for ( var i = 0; i < json6.length; i++) {
							if (json6[i].list1[0] == "") {
								voltageDataOne.push(json6[i].list1[0]);
							} else {
								voltageDataOne
										.push(parseFloat(json6[i].list1[0]));
							}
							if (json6[i].list1[1] == "") {
								voltageDataTwo.push(json6[i].list1[1]);
							} else {
								voltageDataTwo
										.push(parseFloat(json6[i].list1[1]));
							}
							if (json6[i].list1[2] == "") {
								voltageDataThree.push(json6[i].list1[2]);
							} else {
								voltageDataThree
										.push(parseFloat(json6[i].list1[2]));
							}
							voltageTime.push(json6[i].list1[4]);
						}
						dianya(mac);
						pageM();
					},
					error : function() {
						$(".myData6").removeAttr("disabled"); 
						$('#load2').hide();
						// alert("查询不到"+mac+"电压的数据！");
						$("#menu_dy").empty();
						$("#menu_dy").html(
								"<span class='erroricon'>查询不到" + mac
										+ "电压的数据！</span>");
						
					}
				});

			});

	$(".myData7").click(
			function() {
				$(this).attr('disabled',"true");
				$('#load2').css('display',"block");
				mac = $(this).attr("name");
				currentDataOne.length = 0;
				currentDataTwo.length = 0;
				currentDataThree.length = 0;
				currentDataFour.length = 0;
				currentTime.length = 0;
				$.ajax({
					type : "GET",
					url : "electricValue.do?type=7&mac=" + mac,
					async : true,
					dataType : "json",
					success : function(data) {
						$(".myData7s").removeAttr("disabled"); 
						$('#load2').hide();
						// alert("成功返回来的电压数据：" + data);
						if(data==null||data==""){
							alert("无数据");
							return false;
						}
						showDiv($('#menu_dl1'));showDiv($('#popup_mask'));
						$("#menu_dl").empty();
						json7 = eval(data);
						for ( var i = 0; i < json7.length; i++) {
							if (json7[i].list1[0] == "") {
								currentDataOne.push(json7[i].list1[0]);
							} else {
								currentDataOne
										.push(parseFloat(json7[i].list1[0]));
							}
							if (json7[i].list1[1] == "") {
								currentDataTwo.push(json7[i].list1[1]);
							} else {
								currentDataTwo
										.push(parseFloat(json7[i].list1[1]));
							}
							if (json7[i].list1[2] == "") {
								currentDataThree.push(json7[i].list1[2]);
							} else {
								currentDataThree
										.push(parseFloat(json7[i].list1[2]));
							}
							if (json7[i].list1[3] == "") {
								currentDataFour.push(json7[i].list1[3]);
							} else {
								currentDataFour
										.push(parseFloat(json7[i].list1[3]));
							}
							currentTime.push(json7[i].list1[4]);
						}
						dianliu(mac);
						pageM();
					},
					error : function() {
						$(".myData7").removeAttr("disabled"); 
						$('#load2').hide();
						// alert("查询不到"+mac+"电流的数据");
						$("#menu_dl").empty();
						$("#menu_dl").html(
								"<span class='erroricon'>查询不到" + mac
										+ "电流的数据！</span>");
					}
				});

			});

	$(".myData8")
			.click(
					function() {
						$(this).attr('disabled',"true");
						$('#load2').css('display',"block");
						mac = $(this).attr("name");
						leakageCurrentDataOne.length = 0;
						leakageCurrentDataTwo.length = 0;
						leakageCurrentDataThree.length = 0;
						leakageCurrentTime.length = 0;
						$.ajax({
									type : "GET",
									url : "electricValue.do?type=8&mac=" + mac,
									async : true,
									dataType : "json",
									success : function(data) {
										$(".myData8").removeAttr("disabled"); 
										$('#load2').hide();
										if(data==null||data==""){
											alert("无数据");
											return false;
										}
										showDiv($('#menu_ldl1'));showDiv($('#popup_mask'));
										json8 = eval(data);
								
										$("#menu_ldl").empty();
										for ( var i = 0; i < json8.length; i++) {
											if (json8[i].list1[0] == "") {
												leakageCurrentDataOne
														.push(json8[i].list1[0]);
											} else {
												leakageCurrentDataOne
														.push(parseFloat(json8[i].list1[0]));
											}
											if (json8[i].list1[1] == "") {
												leakageCurrentDataTwo
														.push(json8[i].list1[1]);
											} else {
												leakageCurrentDataTwo
														.push(parseFloat(json8[i].list1[1]));
											}
											if (json8[i].list1[2] == "") {
												leakageCurrentDataThree
														.push(json8[i].list1[2]);
											} else {
												leakageCurrentDataThree
														.push(parseFloat(json8[i].list1[2]));
											}
											leakageCurrentTime
													.push(json8[i].list1[4]);
										}
										loudianliu(mac);
										pageM();

									},
									error : function() {
										$(".myData8").removeAttr("disabled"); 
										$('#load2').hide();
										// alert("提示信息:"+mac+"的设备没有漏电流数据！");
										$("#menu_ldl").empty();
										$("#menu_ldl").html(
												"<span class='erroricon'>查询不到"
														+ mac
														+ "漏电流的数据！</span>");
									}
								});

					});

	$(".myData9").click(
			function() {
				$(this).attr('disabled',"true");
				$('#load2').css('display',"block");
				mac = $(this).attr("name");
				temperatureDataOne.length = 0;
				temperatureDataTwo.length = 0;
				temperatureDataThree.length = 0;
				temperatureDataFour.length = 0;
				temperatureTime.length = 0;
				$.ajax({
					type : "GET",
					url : "electricValue.do?type=9&mac=" + mac,
					async : true,
					dataType : "json",
					success : function(data) {
						$(".myData9").removeAttr("disabled"); 
						$('#load2').hide();
						if(data==null||data==""){
							alert("无数据");
							return false;
						}
						showDiv($('#menu_wd1'));showDiv($('#popup_mask'));
						$("#menu_wd").empty();
						json9 = eval(data);

						for ( var i = 0; i < json9.length; i++) {
							if (json9[i].list1[0] == "") {
								temperatureDataOne.push(json9[i].list1[0]);
							} else {
								temperatureDataOne
										.push(parseFloat(json9[i].list1[0]));
							}
							if (json9[i].list1[1] == "") {
								temperatureDataTwo.push(json9[i].list1[1]);
							} else {
								temperatureDataTwo
										.push(parseFloat(json9[i].list1[1]));
							}
							if (json9[i].list1[2] == "") {
								temperatureDataThree.push(json9[i].list1[2]);
							} else {
								temperatureDataThree
										.push(parseFloat(json9[i].list1[2]));
							}
							if (json9[i].list1[3] == "") {
								temperatureDataFour.push(json9[i].list1[3]);
							} else {
								temperatureDataFour
										.push(parseFloat(json9[i].list1[3]));
							}
							temperatureTime.push(json9[i].list1[4]);
						}
						temperature(mac);
						pageM();
					},
					error : function() {
						$(".myData9").removeAttr("disabled"); 
						$('#load2').hide();
						// alert("提示信息:查询不到"+mac+"设备的温度数据！");
						$("#menu_wd").empty();
						$("#menu_wd").html(
								"<span class='erroricon'>查询不到" + mac
										+ "设备的温度数据！</span>");
					}
				});

			});

});

// 分页插件
function HighchartsPager(id, pageSize, options) {
	this.id = id;
	options.chart = options.chart || {};
	options.chart.renderTo = id;
	this._options = $.extend({}, options);
	this._xAxis = options.xAxis;
	this._series = options.series;
	this._total = 0;
	if (this._xAxis.categories) {
		this._total = this._xAxis.categories.length;
	} else {
		this._total = this._series[0].data.length;
	}
	this.toPage(this._total, pageSize);
	this.chart = null;
	// this.chart = new Highcharts.Chart(options);

	this.showPage(1);
	$(".popPrev").click(function() {
		pagePrev(id);
	});
	$(".popNext").click(function() {
		pageNext(id);
	});
	return this.chart;
}

HighchartsPager.prototype.showPageBar = function(pageTotal) {
	var the = this;
	var arr = [];
	var suffixStr = '-pagebar-div';
	for ( var i = 0; i < pageTotal; i++) {
		arr
				.push('<a style="margin: 0 5px;text-decoration : underline; cursor: pointer; font-size: 11px;">'
						+ (i + 1) + '</a>');
	}
	$('#' + this.id)
			.append(
					'<center><span class="popPrev" style="padding:0 10px;cursor: pointer;">《</span><div style="display:inline-block;border:0px red solid; height: 30px;" id="'
							+ this.id
							+ suffixStr
							+ '">'
							+ arr.join('')
							+ '</div><span class="popNext" style="padding:0 10px;cursor: pointer;">》</span></center>');

	the._current_pageNum = -1;

	$('#' + this.id + suffixStr).children().each(function(index) {
		$(this).click(function() {
			the.showPage(index + 1);
		});
	});
}
HighchartsPager.prototype.showPage = function(pageNum) {
	var the = this;
	var suffixStr = '-pagebar-div';
	if (pageNum == the._current_pageNum) {
		return;
	}
	var data = the.pageData(pageNum);
	if (the.chart == null) {
		var options = $.extend({}, the._options);
		options.xAxis = data.xAxis;
		options.series = data.series;
		the.chart = new Highcharts.Chart(options);
		the.showPageBar(the._page.pageTotal);

		the._current_pageNum = 1;
		$($('#' + the.id + suffixStr).children()[0]).css('text-decoration',
				'none').css('font-size', '13px').css('font-weight', 'bold');

	} else {
		the.removeData();
		the.chart.addAxis(data.xAxis, true, true);
		for ( var i = 0; i < data.series.length; i++) {
			the.chart.addSeries(data.series[i], true);
		}
		if (the._current_pageNum != -1) {
			$($('#' + the.id + suffixStr).children()[the._current_pageNum - 1])
					.css('text-decoration', 'underline').css('font-size',
							'11px').css('font-weight', 'normal');
		}
		the._current_pageNum = pageNum;
		$($('#' + the.id + suffixStr).children()[pageNum - 1]).css(
				'text-decoration', 'none').css('font-size', '13px').css(
				'font-weight', 'bold');
	}

}
HighchartsPager.prototype.toPage = function(total, pageSize) {
	this._page = {
		pageSize : pageSize,
		pageTotal : (total - total % pageSize) / pageSize
				+ (total % pageSize != 0 ? 1 : 0),
		total : total
	};
}

HighchartsPager.prototype.pageData = function(pageNum) {
	var xAxis = $.extend({}, this._xAxis);
	if (xAxis.categories) {
		xAxis.categories = [];
		for ( var i = (pageNum - 1) * this._page.pageSize; i < Math.min(
				this._total, pageNum * this._page.pageSize); i++) {
			xAxis.categories.push(this._xAxis.categories[i]);
		}
	}

	var series = [];
	var series_child = null;
	for ( var j = 0; j < this._series.length; j++) {
		series_child = $.extend({}, this._series[j]);
		series_child.data = [];

		for ( var i = (pageNum - 1) * this._page.pageSize; i < Math.min(
				this._series[j].data.length, pageNum * this._page.pageSize); i++) {
			series_child.data.push(this._series[j].data[i]);
		}
		series.push(series_child);
	}
	return {
		xAxis : xAxis,
		series : series
	};
}

HighchartsPager.prototype.removeData = function() {
	if (this.chart == null) {
		return;
	}
	for ( var i = 0; i < this.chart.xAxis.length; i++) {
		// this.chart.xAxis[i].remove();
	}

	// for(var i=0; i<this.chart.series.length; i++){
	// this.chart.series[i].remove(true);
	// }

	this.chart.xAxis[0].remove(true);

}

// 左右滚动分页
function pageM() {
	var pageNum = $("#menu_dy-pagebar-div a,#menu_dl-pagebar-div a,#menu_ldl-pagebar-div a,#menu_wd-pagebar-div a").length;
	for ( var i = 15; i < pageNum; i++) {
		$(
				"#menu_dy-pagebar-div a:eq(" + i
						+ "),#menu_dl-pagebar-div a:eq(" + i
						+ "),#menu_ldl-pagebar-div a:eq(" + i
						+ "),#menu_wd-pagebar-div a:eq(" + i + ")").hide();
	}
}
function pagePrev(id) {
	var pageTotal = $("#" + id + " a").length;// 分页总数
	var pageNum = $("#" + id + " a:visible").length;// 可显示的页数长度
	var pageMin = $("#" + id + " a:visible:eq(0)").text() - 1;// 可显示最小的页码值
	var pageMax = $("#" + id + " a:visible:eq(" + (pageNum - 1) + ")").text() - 1;// 可显示最大的页码值
	var pageTemp = pageMax + 1 - pageMin;// 即将显示的页码的长度
	if (pageMin > 0) {
		if (pageMin == 15) {
			for ( var i = 0; i <= 15; i++) {
				if (0 < pageTemp < 15) {
					for ( var j = 0; j < pageTemp; j++) {
						$("#" + id + " a:eq(" + (pageMax - j) + ")").hide();
					}
				}
				$("#" + id + " a:eq(" + (pageMin - i) + ")").show();
			}
		} else {
			for ( var i = 1; i < 16; i++) {
				if (0 < pageTemp < 15) {
					for ( var j = 0; j < pageTemp; j++) {
						$("#" + id + " a:eq(" + (pageMax - j) + ")").hide();
					}
				} else {
					$("#" + id + " a:eq(" + (pageMax + 1 - i) + ")").hide();
				}
				$("#" + id + " a:eq(" + (pageMin - i) + ")").show();
			}
		}
	}
}
function pageNext(id) {
	var pageTotal = $("#" + id + " a").length;
	var pageNum = $("#" + id + " a:visible").length;
	var pageMin = $("#" + id + " a:visible:eq(0)").text() - 1;
	var pageMax = $("#" + id + " a:visible:eq(" + (pageNum - 1) + ")").text() - 1;
	var pageTemp = pageTotal - (pageMax + 1);
	if (pageTemp > 0) {
		if (pageTemp < 15) {
			for ( var i = 0; i < pageTemp; i++) {
				for ( var j = 0; j < 15; j++) {
					$("#" + id + " a:eq(" + (pageMin + j) + ")").hide();
				}
				$("#" + id + " a:eq(" + (pageMax + 1 + i) + ")").show();
			}
		} else {
			for ( var i = 0; i < 15; i++) {
				$("#" + id + " a:eq(" + (pageMin + i) + ")").hide();

				$("#" + id + " a:eq(" + (pageMax + 1 + i) + ")").show();
			}
		}
	}
}
