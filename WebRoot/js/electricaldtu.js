function dianya(){
    new HighchartsPager('container1',20, {
        chart: {
            type: 'area'
        },
        title: {
            text: '电气火灾探测器实时数据电压值'
        },        
        xAxis: {
        	categories: voltageTime
        }, 

        yAxis: {
            title: {
                text: '电压值（V）'
            },
            labels: {
                formatter: function () {
                    return this.value + 'V';
                }
            }
        },
        tooltip: {
            split: true,
            valueSuffix: ' V'
        },
        plotOptions: {
            area: {
            	marker: {
                    enabled: false,
                    symbol: 'circle',
                    radius: 2,
                    states: {
                        hover: {
                            enabled: true
                        }
                    }
                }
            }
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

        },{
            name: '电压4',
            data: voltageDataFour

        }
        
        ]
    });
}  

function dianliu(){
   new HighchartsPager('container2',20,{
        chart: {
            type: 'area'
        },
        title: {
            text: '电气火灾探测器实时数据电流值'
        },        
        xAxis: {
        	categories: voltageTime

        },
        yAxis: {
            title: {
                text: '电流值（A）'
            },
            labels: {
                formatter: function () {
                    return this.value+ 'A';
                }
            }
        },
        tooltip: {
            split: true,
            valueSuffix: 'A'
        },
        plotOptions: {
            area: {
                marker: {

                    enabled: false,
                    symbol: 'circle',
                    radius: 2,
                    states: {
                        hover: {
                            enabled: true
                        }
                    }
                }
            }
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
        },{
            name: '电流4',
            data: currentDataFour
        }]
    });
}

function loudianliu(){
    new HighchartsPager('container3',20,{
        chart: {
            type: 'area'
        },
        title: {
            text: '电气火灾探测器实时数据漏电流值'
        },
        xAxis: {
            categories: leakageCurrentTime
        },
        yAxis: {
            title: {
                text: '漏电流值（mA）'
            },
            labels: {
                formatter: function () {
                    return this.value + 'mA';
                }
            }
        },
        tooltip: {
            split: true,
            valueSuffix: 'mA'
        },
        plotOptions: {
            area: {
            	marker: {
                    enabled: false,
                    symbol: 'circle',
                    radius: 2,
                    states: {
                        hover: {
                            enabled: true
                        }
                    }
                }
            }
        },
        series: [{
            name: '漏电流1',
            data: leakageCurrentDataOne
        },{
            name: '漏电流2',
            data: leakageCurrentDataTwo
        },{
            name: '漏电流3',
            data: leakageCurrentDataThree
        },{
            name: '漏电流4',
            data: leakageCurrentDataFour
        }]
    });
}
    
function temperature(){
    new HighchartsPager('container4',20,{
        chart: {
            type: 'area'
        },
        title: {
            text: '电气火灾探测器实时数据温度值'
        },       
        xAxis: {
            categories: temperatureTime
        },
        yAxis: {
            title: {
                text: '温度值（℃）'
            },
            labels: {
                formatter: function () {
                    return this.value + '℃';
                }
            }
        },
        tooltip: {
            split: true,
            valueSuffix: '℃'
        },
        plotOptions: {
            area: {
            	marker: {
                    enabled: false,
                    symbol: 'circle',
                    radius: 2,
                    states: {
                        hover: {
                            enabled: true
                        }
                    }
                }
            }
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
var mac = $("#mac").val();
var type = $("#type").val();
$.ajax({
	type:"GET",
	url:"electric_Need_DTU_data.do?type=6&mac="+mac,
	async:true,				
	dataType:"json",
	success : function(data){
				json6=eval(data);
				voltageDataOne.length=0;
				voltageDataTwo.length=0;
				voltageDataThree.length=0;
				voltageDataFour.length=0;
				voltageTime.length=0;
				for(var i=0;i<json6.length;i++){	
					if(json6[i]["electricValue1"]==""||json6[i]["electricValue1"]=="0.0"){
						voltageDataOne.push(0);
					}else{
						voltageDataOne.push(parseFloat(json6[i]["electricValue1"]));
					}
					if(json6[i]["electricValue2"]==""||json6[i]["electricValue2"]=="0.0"){
						voltageDataTwo.push(0);
					}else{
						voltageDataTwo.push(parseFloat(json6[i]["electricValue2"]));
					}
					if(json6[i]["electricValue3"]==""||json6[i]["electricValue3"]=="0.0"){
						voltageDataThree.push(0);
					}else{
						voltageDataThree.push(parseFloat(json6["electricValue3"]));
					}
					if(json6[i]["electricValue4"]==""||json6[i]["electricValue4"]=="0.0"){
						voltageDataFour.push(0);
					}else{
						voltageDataFour.push(parseFloat(json6["electricValue4"]));
					}
					voltageTime.push(json6[i]["electricTime"]);
				}
				dianya();
				pageM();
			},
			error : function(){
				alert("提示信息:查询不到"+mac+"电压的数据！");
			}
});

/**6代表电压 **/
var json6;
var voltageTime = [];//电压时间
var voltageDataOne = [],voltageDataTwo = [],voltageDataThree = [],voltageDataFour = [];//电压数值
/**7代表电流 **/
var json7;
var currentTime = [];//电流时间
var currentDataOne = [],currentDataTwo = [],currentDataThree = [],currentDataFour = [];//电流数值
/**8代表漏电流 **/
var json8;
var leakageCurrentTime = [];//漏电流时间
var leakageCurrentDataOne = [],leakageCurrentDataTwo = [],leakageCurrentDataThree = [],leakageCurrentDataFour = [];//漏电流数值
/**9代表温度 **/
var json9;
var temperatureTime = [];//温度时间
var temperatureDataOne = [],temperatureDataTwo = [],temperatureDataThree = [],temperatureDataFour = [];//温度数值
$(function(){
	 $(".northqincontent li:eq(0)").click(function(){
		$.ajax({
			type:"GET",
			url:"electric_Need_DTU_data.do?type=6&mac="+mac,
			async:true,				
			dataType:"json",
			success : function(data){
						json6=eval(data);
						voltageDataOne.length=0;
						voltageDataTwo.length=0;
						voltageDataThree.length=0;
						voltageDataFour.length=0;
						voltageTime.length=0;
						for(var i=0;i<json6.length;i++){	
							if(json6[i]["electricValue1"]==""||json6[i]["electricValue1"]=="0.0"){
								voltageDataOne.push(0);
							}else{
								voltageDataOne.push(parseFloat(json6[i]["electricValue1"]));
							}
							if(json6[i]["electricValue2"]==""||json6[i]["electricValue2"]=="0.0"){
								voltageDataTwo.push(0);
							}else{
								voltageDataTwo.push(parseFloat(json6[i]["electricValue2"]));
							}
							if(json6[i]["electricValue3"]==""||json6[i]["electricValue3"]=="0.0"){
								voltageDataThree.push(0);
							}else{
								voltageDataThree.push(parseFloat(json6["electricValue3"]));
							}
							if(json6[i]["electricValue4"]==""||json6[i]["electricValue4"]=="0.0"){
								voltageDataFour.push(0);
							}else{
								voltageDataFour.push(parseFloat(json6["electricValue4"]));
							}
							voltageTime.push(json6[i]["electricTime"]);
						}
						dianya();
						pageM();
					},
					error : function(){
						alert("提示信息:查询不到"+mac+"电压的数据！");
					}
		});
	
	}); 
	 
	 $(".northqincontent li:eq(1)").click(function(){
		$.ajax({
			type:"GET",
			url:"electric_Need_DTU_data.do?type=7&mac="+mac,
			async:true,				
			dataType:"json",
			success : function(data){	
				json7 = eval(data);	
				currentDataOne.length=0;
				currentDataTwo.length=0;
				currentDataThree.length=0;
				currentDataFour.length=0;
				currentTime.length=0;
				for(var i=0;i<json7.length;i++){					
					if(json7[i]["electricValue1"]==""||json7[i]["electricValue1"]=="0.0"){
						currentDataOne.push(0);
					}else{
						currentDataOne.push(parseFloat(json7[i]["electricValue1"]));
					}
					if(json7[i]["electricValue2"]==""||json7[i]["electricValue2"]=="0.0"){
						currentDataTwo.push(0);
					}else{
						currentDataTwo.push(parseFloat(json7[i]["electricValue2"]));
					}
					if(json7[i]["electricValue3"]==""||json7[i]["electricValue3"]=="0.0"){
						currentDataThree.push(0);
					}else{
						currentDataThree.push(parseFloat(json7[i]["electricValue3"]));
					}
					if(json7[i]["electricValue4"]==""||json7[i]["electricValue4"]=="0.0"){
						currentDataFour.push(0);
					}else{
						currentDataFour.push(parseFloat(json7[i]["electricValue4"]));
					}
					currentTime.push(json7[i]["electricTime"]);
				}					
				dianliu();
				pageM();
			},
					error : function(){
						alert("提示信息:查询不到"+mac+"电流的数据");
					}
		});
	
	}); 
	 

	 $(".northqincontent li:eq(2)").click(function(){
		$.ajax({
			type:"GET",
			url:"electric_Need_DTU_data.do?type=8&mac="+mac,
			async:true,				
			dataType:"json",
			success : function(data){
				json8=eval(data);
				leakageCurrentDataOne.length=0;
				leakageCurrentDataTwo.length=0;
				leakageCurrentDataThree.length=0;
				leakageCurrentDataFour.length=0;
				leakageCurrentTime.length=0;
				for(var i=0;i<json8.length;i++){					
					if(json8[i]["electricValue1"]==""||json8[i]["electricValue1"]=="0.0"){
						leakageCurrentDataOne.push(0);
					}else{
						leakageCurrentDataOne.push(parseFloat(json8[i]["electricValue1"]));
					}
					if(json8[i]["electricValue2"]==""||json8[i]["electricValue2"]=="0.0"){
						leakageCurrentDataTwo.push(0);
					}else{
						leakageCurrentDataTwo.push(parseFloat(json8[i]["electricValue2"]));
					}
					if(json8[i]["electricValue3"]==""||json8[i]["electricValue3"]=="0.0"){
						leakageCurrentDataThree.push(0);
					}else{
						leakageCurrentDataThree.push(parseFloat(json8[i]["electricValue3"]));
					}
					if(json8[i]["electricValue4"]==""||json8[i]["electricValue4"]=="0.0"){
						leakageCurrentDataFour.push(0);
					}else{
						leakageCurrentDataFour.push(parseFloat(json8[i]["electricValue4"]));
					}
					leakageCurrentTime.push(json8[i]["electricTime"]);
				}	
				loudianliu();
				pageM();
			},
					error : function(){
						alert("提示信息:查询不到"+mac+"的设备漏电流数据！");
					}
		});
	
	}); 
	 

	 $(".northqincontent li:eq(3)").click(function(){
		$.ajax({
			type:"GET",
			url:"electric_Need_DTU_data.do?type=9&mac="+mac,
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
					if(json9[i]["electricValue1"]==""||json9[i]["electricValue1"]=="0.0"){
						temperatureDataOne.push(0);
					}else{
						temperatureDataOne.push(parseFloat(json9[i]["electricValue1"]));
					}
					if(json9[i]["electricValue2"]==""||json9[i]["electricValue2"]=="0.0"){
						temperatureDataTwo.push(0);
					}else{
						temperatureDataTwo.push(parseFloat(json9[i]["electricValue2"]));
					}
					if(json9[i]["electricValue3"]==""||json9[i]["electricValue3"]=="0.0"){
						temperatureDataThree.push(0);
					}else{
						temperatureDataThree.push(parseFloat(json9[i]["electricValue3"]));
					}if(json9[i]["electricValue4"]==""||json9[i]["electricValue4"]=="0.0"){
						temperatureDataFour.push(0);
					}else{
						temperatureDataFour.push(parseFloat(json9[i]["electricValue4"]));
					}
					temperatureTime.push(json9[i]["electricTime"]);
				}	
				temperature();
				pageM();
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

