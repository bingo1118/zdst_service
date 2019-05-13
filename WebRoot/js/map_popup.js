$(function () {

	var macNum = document.getElementById('macNum').value  ;
	var macNum = parseFloat(macNum);
	var netStaterNum = document.getElementById('netStaterNum').value  ;
	var netStaterNum = parseFloat(netStaterNum);
	var ifDealNum = document.getElementById('ifDealNum').value  ;
	var ifDealNum = parseFloat(ifDealNum);
	var noNetStater = document.getElementById('noNetStater').value  ;
	var noNetStater = parseFloat(noNetStater);
	var alarmTypeNum = document.getElementById('alarmTypeNum').value  ;
	var alarmTypeNum = parseFloat(alarmTypeNum);
	var alarmTruthNum = document.getElementById('alarmTruthNum').value  ;
	var alarmTruthNum = parseFloat(alarmTruthNum);
    $('#map_tab').highcharts({
		credits: {
			 enabled: false
		},
        chart: {
            plotBackgroundColor: null,
            plotBorderWidth: null,
            plotShadow: false
        },
        title: {
            text: '设备状态统计图'
        },
        tooltip: {
            pointFormat: '{series.name}: <b>{point.percentage:.1f}%</b>'
        },
        plotOptions: {
            pie: {
                allowPointSelect: true,
                cursor: 'pointer',
                dataLabels: {
                    enabled: false
                },
				showInLegend: true
            }
        },
        series: [{
            type: 'pie',
            name: '数量百分比',
            data: [
                ['正常',     netStaterNum],
                ['火警',     alarmTypeNum],
                /*['误报',    alarmTruthNum],*/
                ['报警',        ifDealNum],
                ['故障',      noNetStater]
            ]
        }]
    });
});
