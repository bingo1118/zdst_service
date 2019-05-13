var showdate;
var indate=[];
var intime=[];
var l_value=h_value=0;
$(function () {	
		$.ajax({
			url:"waterLeveInfoAnalysis.do",
			type:"get",
			dataType:"json",
			success:function(data){
	 			var json = eval(data);
	 			/*console.log(json[1]);*/
	 			var i=0;
	 		for(i=0;i<json.length;i++){
	 			showdate  = {
	 			        name: null,
	 			        data: [],
	 			        visible:false,
	 			    };		
	 			showdate.name=json[i].waterName;
	 			console.log(showdate.name);
	 			for(y=0;y<json[i].waterList.length;y++){
	 				showdate.data[y]=parseFloat(json[i].waterList[y].value);
	 				intime[y]=json[i].waterList[y].waterTime;
	 			}
	 			indate.push(showdate);
	 			
//	 			showdate.data=getdata;
	 			
	 		}	
	 		console.log(indate[0]);
	 		if(indate[0]!=undefined){
	 		indate[0].visible=true;
	 		}
	 		foldline1();
			},
			error:function(){
				console.log("请求失败");
			}	
	});
//点击查询
$('#theSecondButtonSearch').click(function(){
	showdate  = {
	        name: null,
	        data: [],
	        visible:false,
	    };
	$('.no_search').hide();
	indate=[];
	intime=[];
	var StartDate = $("#J_xl_1").val();
	var EndDate  = $("#J_xl_2").val();
	var areaId = $('#areaId').val();
	var mac= $('#watermac').val();
	if(StartDate==null&&EndDate==null){
		alert("请选择时间");
		return null;
	}
	$.ajax({
		url:"waterLeveInfoAnalysis.do",
		type:"get",
		data:{waterMac:mac, areaid: areaId,start_Time:StartDate,end_Time:EndDate},
		dataType:"json",
		success:function(data){
        if(data==""){
        	indate=[];
        	intime=[];
          $('.no_data').show();
        	foldline1();
        }	
        else{
        	$('.no_data').hide();
 			var json = eval(data);
 			console.log(json);
 		for(i=0;i<json.length;i++){
 			showdate  = {
 			        name: null,
 			        data: [],
 			        visible:false,
 			    };
 			showdate.name=json[i].waterName;
 			for(y=0;y<json[i].waterList.length;y++){
 				showdate.data[y]=parseFloat(json[i].waterList[y].value);
 				intime[y]=json[i].waterList[y].waterTime;
 			}
 			l_value=json[0].L_value;
 			indate.push(showdate);
 		}	
 		
 		if(indate[0]!=undefined){
	 		indate[0].visible=true;
	 		}
 		console.log(showdate);
 		foldline1();
        }
        },
		error:function(){
			console.log("请求失败");
		}	
});
});
});
//连一条直线
function foldline1() {
    $('#container').highcharts({
    	credits: {
			 enabled: false
		},
        chart: {
            type: 'line'
        },
        title: {
            text: '无线水位探测器'
        },        
        xAxis: {
            categories:intime,
            
        },
        yAxis: {
            title: {
                text: '水位值(m)'
            },
            labels : {
                formatter : function () {
                    var strVal = this.value + '';
                    if (strVal.indexOf('.') == -1) {
                        return strVal + '.00';
                    } else {
                        var arr = strVal.split('.');
                        if (arr[1].length == 2) {
                            return strVal;
                        } else {
                            return strVal + '0';
                        }
                    }
                }
            },
            plotLines:[{
                color:'red',           //线的颜色，定义为红色
                dashStyle:'solid',     //默认值，这里定义为实线
                value:h_value,               //定义在那个值上显示标示线，这里是在x轴上刻度为3的值处垂直化一条线
                width:2,              //标示线的宽度，2px
                label:{
                    text:'高水位',     //标签的内容
                    align:'left',                //标签的水平位置，水平居左,默认是水平居中center
                    x:10                         //标签相对于被定位的位置水平偏移的像素，重新定位，水平居左10px
                }
            },
           
            {
                color:'blue',           //线的颜色，定义为红色
                dashStyle:'solid',     //默认值，这里定义为实线
                value:l_value,               //定义在那个值上显示标示线，这里是在x轴上刻度为3的值处垂直化一条线
                width:2,                //标示线的宽度，2px
                label:{
                    text:'低水位',     //标签的内容
                    align:'left',                //标签的水平位置，水平居左,默认是水平居中center
                    x:10                         //标签相对于被定位的位置水平偏移的像素，重新定位，水平居左10px
                }
            },
            ]
        },
        plotOptions: {
            line: {
                dataLabels: {
                    enabled: true          // 开启数据标签
                },
                enableMouseTracking: true // 鼠标跟踪，对应的提示框、点击事件会失效
            }
        },
        series: indate
    });

};   


    