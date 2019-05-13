
var width = $(window).width()-150;
var height = $(window).height()-100;
$("#menu_dy1, #menu_dl1, #menu_ldl1, #menu_wd1").css("width",width);
$("#menu_dy1, #menu_dl1, #menu_ldl1, #menu_wd1").css("height",height);
$(".menu").css("height",height-2);
$(window).resize(function() {
	var width = $(window).width()-150;
	var height = $(window).height()-100;
	$("#menu_dy1, #menu_dl1, #menu_ldl1, #menu_wd1").css("width",width);
	$("#menu_dy1, #menu_dl1, #menu_ldl1, #menu_wd1").css("height",height);
	$(".menu").css("height",height-30);
});

//电压折线图
function dianya(){
	new HighchartsPager('menu_dy',20, {
		credits: {
		    enabled: false
		},
        chart: {
            type: 'line'
        },
        title: {
            text: '电压折线图'
        },
        xAxis: {
        	categories: voltageTime
        },        
        yAxis: {
            title: {
                text: '电压值 (V)'
            },
            plotLines: [{
                value: 0,
                width: 1,
                color: '#808080'
            }]
        },
        tooltip: {
            valueSuffix: 'V'
        },
        legend: {
            layout: 'vertical',
            align: 'right',
            verticalAlign: 'middle',
            borderWidth: 0
        },
        series: [{
            name: '电压1',
            data: voltageDataOne
        },{
            name: '电压2',
            data: voltageDataTwo
        },{
            name: '电压3',
            data: voltageDataThree
        }]
    });
}


//电流折线图
function dianliu(){
new HighchartsPager('menu_dl',20,{
	credits: {
	    enabled: false
	},
        chart: {
            type: 'line'
        },
        title: {
            text: '电流折线图'
        },
        xAxis: {
            categories: currentTime
        },
        yAxis: {
            title: {
                text: '电流值 (mA)'
            },
            plotLines: [{
                value: 0,
                width: 1,
                color: '#808080'
            }]
        },
        tooltip: {
            valueSuffix: 'mA'
        },
        legend: {
            layout: 'vertical',
            align: 'right',
            verticalAlign: 'middle',
            borderWidth: 0
        },
        series: [{
            name: '电流1',
            data: currentDataOne
        },{
            name: '电流2',
            data: currentDataTwo
        },{
            name: '电流3',
            data: currentDataThree
        }]
    });

}
	
	//漏电流折线图
function loudianliu(){
new HighchartsPager('menu_ldl',20,{
	credits: {
	    enabled: false
	},
        chart: {
            type: 'line'
        },
        title: {
            text: '漏电流折线图'
        },       
        xAxis: {
            categories: leakageCurrentTime
        },
        yAxis: {
            title: {
                text: '漏电流值 (mA)'
            },
            plotLines: [{
                value: 0,
                width: 1,
                color: '#808080'
            }]
        },
        tooltip: {
            valueSuffix: 'mA'
        },
        legend: {
            layout: 'vertical',
            align: 'right',
            verticalAlign: 'middle',
            borderWidth: 0
        },
        series: [{
            name: '漏电流1',
            data: leakageCurrentDataOne
        }/*, {
            name: '漏电流2',
            data: leakageCurrentDataTwo
        }, {
            name: '漏电流3',
            data: leakageCurrentDataThree
        }*/]
    });
}	
	
	//温度折线图
function temperature(){
new HighchartsPager('menu_wd',20,{
	credits: {
	    enabled: false
	},
        chart: {
            type: 'line'
        },
        title: {
            text: '温度折线图'
        },
        xAxis: {
            categories: temperatureTime
        },
        yAxis: {
            title: {
                text: '温度 (℃)'
            },
            plotLines: [{
                value: 0,
                width: 1,
                color: '#808080'
            }]
        },
        tooltip: {
            valueSuffix: '℃'
        },
        legend: {
            layout: 'vertical',
            align: 'right',
            verticalAlign: 'middle',
            borderWidth: 0
        },
        series: [{
            name: '温度1',
            data: temperatureDataOne
        }, {
            name: '温度2',
            data: temperatureDataTwo
        }, {
            name: '温度3',
            data: temperatureDataThree
        },{
            name: '温度4',
            data: temperatureDataFour
        }]
    });
}

var mac ;//mac的数值
/**6代表电压 **/
var json6;
var voltageTime = [];//电压时间
var voltageDataOne = [],voltageDataTwo = [],voltageDataThree = [];//电压数值
/**7代表电流 **/
var json7;
var currentTime = [];//电流时间
var currentDataOne = [],currentDataTwo = [],currentDataThree = [];//电流数值
/**8代表漏电流 **/
var json8;
var leakageCurrentTime = [];//漏电流时间
var leakageCurrentDataOne = [],leakageCurrentDataTwo = [],leakageCurrentDataThree = [];//漏电流数值
/**9代表温度 **/
var json9;
var temperatureTime = [];//温度时间
var temperatureDataOne = [],temperatureDataTwo = [],temperatureDataThree = [],temperatureDataFour = [];//温度数值
$(function(){
	 $(".myData6").click(function(){
		 mac = $(this).attr("name");
		 var tempTime,tempData;
		$.ajax({
			type:"GET",
			url:"bqEletricData.do?type=6&deviceId="+mac,
			async:true,				
			dataType:"json",
			success : function(data){
						json6=eval(data);
						voltageDataOne.length=0;
						voltageDataTwo.length=0;
						voltageDataThree.length=0;
						voltageTime.length=0;
						//var datas=JSON.parse(json6);*/
						//console.log("成功返回来的数据" + JSON.stringify(data));
						for(var i=0;i<json6.length;i++){	
							if(json6[i]["voltage1"]==""||json6[i]["voltage1"]=="0.0"){
								voltageDataOne.push(0);
							}else{
								voltageDataOne.push(parseInt(json6[i]["voltage1"]));
							}
							if(json6[i]["voltage2"]==""||json6[i]["voltage2"]=="0.0"){
								voltageDataTwo.push(0);
							}else{
								voltageDataTwo.push(parseInt(json6[i]["voltage2"]));
							}
							if(json6[i]["voltage3"]==""||json6[i]["voltage3"]=="0.0"){
								voltageDataThree.push(0);
							}else{
								voltageDataThree.push(parseInt(json6["voltage3"]));
							}
							voltageTime.push(json6[i]["createTimeString"]);
						}
						dianya();
						pageM();
					},
					error : function(){
						alert("查询不到"+mac+"电压的数据！");
					}
		});
	
	}); 
	 
	 $(".myData7").click(function(){
		 mac = $(this).attr("name");
		 //alert (mac);
		$.ajax({
			type:"GET",
			url:"bqEletricData.do?type=7&deviceId="+mac,
			async:true,				
			dataType:"json",
			success : function(data){						
						//alert("成功返回来的电压数据：" + data);
				
				json7 = eval(data);	
				currentDataOne.length=0;
				currentDataTwo.length=0;
				currentDataThree.length=0;
				currentTime.length=0;
				for(var i=0;i<json7.length;i++){					
					if(json7[i]["eleCurrent1"]==""||json7[i]["eleCurrent1"]=="0.0"){
						currentDataOne.push(0);
					}else{
						currentDataOne.push(parseInt(json7[i]["eleCurrent1"]));
					}
					if(json7[i]["eleCurrent2"]==""||json7[i]["eleCurrent2"]=="0.0"){
						currentDataTwo.push(0);
					}else{
						currentDataTwo.push(parseInt(json7[i]["eleCurrent2"]));
					}
					if(json7[i]["eleCurrent3"]==""||json7[i]["eleCurrent3"]=="0.0"){
						currentDataThree.push(0);
					}else{
						currentDataThree.push(parseInt(json7[i]["eleCurrent3"]));
					}
					currentTime.push(json7[i]["createTimeString"]);
				}					
				dianliu();
			},
					error : function(){
						alert("查询不到"+mac+"电流的数据");
					}
		});
	
	}); 
	 

	 $(".myData8").click(function(){
		 mac = $(this).attr("name");
		$.ajax({
			type:"GET",
			url:"bqEletricData.do?type=8&deviceId="+mac,
			async:true,				
			dataType:"json",
			success : function(data){
				json8=eval(data);
				leakageCurrentDataOne.length=0;
				leakageCurrentTime.length=0;
				for(var i=0;i<json8.length;i++){					
					if(json8[i]["leakEleCurrent1"]==""||json8[i]["leakEleCurrent1"]=="0.0"){
						leakageCurrentDataOne.push(0);
					}else{
						leakageCurrentDataOne.push(parseInt(json8[i]["leakEleCurrent1"]));
					}
					/*if(json8[i]["eleCurrent3"]==""||json8[i]["eleCurrent3"]=="0.0"){
						leakageCurrentDataTwo.push(json8[i].list1[1]);
					}else{
						leakageCurrentDataTwo.push(parseInt(json8[i].list1[1]));
					}
					if(json8[i]["eleCurrent3"]==""||json8[i]["eleCurrent3"]=="0.0"){
						leakageCurrentDataThree.push(json8[i].list1[2]);
					}else{
						leakageCurrentDataThree.push(parseInt(json8[i].list1[2]));
					}*/
					leakageCurrentTime.push(json8[i]["createTimeString"]);
				}	
				loudianliu();
				
			},
					error : function(){
						alert("提示信息:查询不到"+mac+"的设备漏电流数据！");
					}
		});
	
	}); 
	 

	 $(".myData9").click(function(){
		 mac = $(this).attr("name");
		$.ajax({
			type:"GET",
			url:"bqEletricData.do?type=9&deviceId="+mac,
			async:true,				
			dataType:"json",
			success : function(data){
				console.log("成功返回来的数据" + JSON.stringify(data));
				json9=eval(data);
				temperatureDataOne.length=0;
				temperatureDataTwo.length=0;
				temperatureDataThree.length=0;
				temperatureDataFour.length=0;
				temperatureTime.length=0;
				for(var i=0;i<json9.length;i++){					
					if(json9[i]["temperature1"]==""||json9[i]["temperature1"]=="0.0"){
						temperatureDataOne.push(0);
					}else{
						temperatureDataOne.push(parseInt(json9[i]["temperature1"]));
					}
					if(json9[i]["temperature2"]==""||json9[i]["temperature2"]=="0.0"){
						temperatureDataTwo.push(0);
					}else{
						temperatureDataTwo.push(parseInt(json9[i]["temperature2"]));
					}
					if(json9[i]["temperature3"]==""||json9[i]["temperature3"]=="0.0"){
						temperatureDataThree.push(0);
					}else{
						temperatureDataThree.push(parseInt(json9[i]["temperature3"]));
					}if(json9[i]["temperature4"]==""||json9[i]["temperature4"]=="0.0"){
						temperatureDataFour.push(0);
					}else{
						temperatureDataFour.push(parseInt(json9[i]["temperature4"]));
					}
					temperatureTime.push(json9[i]["createTimeString"]);
				}	
				temperature();
			},
			error : function(){
						alert("提示信息:查询不到"+mac+"设备的温度信息！");
					}
		});
	
	}); 
	
}); 

//分页插件
function HighchartsPager(id, pageSize, options) {
    this.id = id;
    options.chart = options.chart || {};
    options.chart.renderTo = id;
    this._options = $.extend({}, options);
    this._xAxis = options.xAxis;
    this._series = options.series;     
    this._total = 0;
    if(this._xAxis.categories){
        this._total = this._xAxis.categories.length;
    }else{
        this._total = this._series[0].data.length;
    }
    this.toPage(this._total, pageSize);
    this.chart = null;
    //this.chart = new Highcharts.Chart(options);
     
    this.showPage(1); 
     
    return this.chart;
}
 
HighchartsPager.prototype.showPageBar = function(pageTotal) {
    var the = this;
    var arr = [];
    var suffixStr = '-pagebar-div';
    for ( var i = 0; i < pageTotal; i++) {
        arr.push('<a style="margin: 0 5px;text-decoration : underline; cursor: pointer; font-size: 11px;">'
                        + (i + 1) + '</a>');
    }
    $('#' + this.id).append(
            '<center><span id="popPrev" style="padding:0 10px;cursor: pointer;">《</span><div style="display:inline-block;border:0px red solid; height: 30px;" id="'
                    + this.id + suffixStr + '">' + arr.join('')
                    + '</div><span id="popNext" style="padding:0 10px;cursor: pointer;">》</span></center>');
 
    the._current_pageNum = -1;
 
    $('#' + this.id + suffixStr).children().each(function(index) {
        $(this).click(function() {
            the.showPage(index + 1);
        });
    });
    $("#popPrev").click(function(){
    	pagePrev();
    });
    $("#popNext").click(function(){
    	pageNext();
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
                'none').css(
                        'font-size', '13px').css(
                                'font-weight', 'bold');
 
    } else {
        the.removeData();
        the.chart.addAxis(data.xAxis, true, true);
        for ( var i = 0; i < data.series.length; i++) {
            the.chart.addSeries(data.series[i], true);
        }
        if (the._current_pageNum != -1) {
            $($('#' + the.id + suffixStr).children()[the._current_pageNum - 1])
                    .css('text-decoration', 'underline').css(
                            'font-size', '11px').css(
                                    'font-weight', 'normal');
        }
        the._current_pageNum = pageNum;
        $($('#' + the.id + suffixStr).children()[pageNum - 1]).css(
                'text-decoration', 'none').css(
                        'font-size', '13px').css(
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
    if(xAxis.categories){
        xAxis.categories = [];
        for ( var i = (pageNum - 1) * this._page.pageSize; i < Math.min(this._total, pageNum * this._page.pageSize); i++) {
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
        //this.chart.xAxis[i].remove();
    }
 
    //for(var i=0; i<this.chart.series.length; i++){
    //this.chart.series[i].remove(true);
    //}
 
    this.chart.xAxis[0].remove(true);
 
}

//左右滚动分页
function pageM(){
	var pageNum = $("#menu_dy-pagebar-div a").length;
	for(var i=15;i<pageNum;i++){
		$("#menu_dy-pagebar-div a:eq("+i+")").hide();
	}
}
function pagePrev(){
	var pageTotal  = $("#menu_dy-pagebar-div a").length;
	var pageNum = $("#menu_dy-pagebar-div a:visible").length;
	var pageMin = $("#menu_dy-pagebar-div a:visible:eq(0)").text()-1;
	var pageMax = $("#menu_dy-pagebar-div a:visible:eq("+(pageNum-1)+")").text()-1;
	var pageTemp = pageMax+1-pageMin;
	if(pageMin > 0 ){
		if(pageMin==15){
			for(var i=0;i<=15;i++){
				if(0<pageTemp<15){
					for(var j=0;j<pageTemp;j++){
						$("#menu_dy-pagebar-div a:eq("+(pageMax-j)+")").hide();
					}
				}
				$("#menu_dy-pagebar-div a:eq("+(pageMin-i)+")").show();
			}
		}else{
			for(var i=0;i<15;i++){
				$("#menu_dy-pagebar-div a:eq("+(pageMax-i)+")").hide();
				$("#menu_dy-pagebar-div a:eq("+(pageMin-i)+")").show();
			}
		}
	}
}
function pageNext(){
	var pageTotal  = $("#menu_dy-pagebar-div a").length;
	var pageNum = $("#menu_dy-pagebar-div a:visible").length;
	var pageMin = $("#menu_dy-pagebar-div a:visible:eq(0)").text()-1;
	var pageMax = $("#menu_dy-pagebar-div a:visible:eq("+(pageNum-1)+")").text()-1;
	var pageTemp = pageTotal - (pageMax+1);
	if(pageTemp > 0){
		if(pageTemp < 15){
			for(var i=1;i<pageTemp;i++){
				for(var j=0;j<15;j++){
					$("#menu_dy-pagebar-div a:eq("+(pageMin+j)+")").hide();
				}
				$("#menu_dy-pagebar-div a:eq("+(pageMax+i)+")").show();
			}
		}else{
			for(var i=0;i<15;i++){
				$("#menu_dy-pagebar-div a:eq("+(pageMin+i)+")").hide();
			}
		}
	}
}
