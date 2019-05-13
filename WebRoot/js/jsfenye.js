// JavaScript Document
/**
 * 分页函数
 * pno--页数
 * psize--每页显示记录数
 * 分页部分是从真实数据行开始，因而存在加减某个常数，以确定真正的记录数
 * 纯js分页实质是数据行全部加载，通过是否显示属性完成分页功能goPage(1,10)
 **/
 var currentPage;
 var ye = 0;
 var totalPage = 0;//总页数
 var num;
function goPage(pno,psize){
    var itable = document.getElementById("idData");
    num = itable.rows.length-1;//表格所有行数(所有记录数)
    console.log("所有记录数:"+num);
    var pageSize = psize;//每页显示行数
    //总共分几页 
    if(num/pageSize > parseInt(num/pageSize)){   
            totalPage=parseInt(num/pageSize)+1;   
       }else{   
           totalPage=parseInt(num/pageSize);   
       }   
    currentPage = pno;//当前页数
    var startRow = (currentPage - 1) * pageSize+1;//开始显示的行  31 
       var endRow = currentPage * pageSize;//结束显示的行   40
       endRow = (endRow > num)? num : endRow;    40
       console.log("结束显示的行:"+endRow);
       //遍历显示数据实现分页
    for(var i=1;i<num+1;i++){    
        var irow = itable.rows[i];
        if(i>=startRow && i<=endRow){
            irow.style.display = "";    
        }else{
            irow.style.display = "none";
        }
    }
    var pageEnd = document.getElementById("pageEnd");
	var record = document.getElementById("record");
    record.innerHTML = "<li>共"+num+"条记录，每页10条，当前第"+currentPage+"/"+totalPage+"页</li>";//<li>共 10 条记录，每页 10 条，当前第 1 / 1 页</li>
}
$(function(){
    goPage(1,10);
	    if(num<1){
	    	$(".ul_pagenav").empty();
	    	$(".ul_pagenav").text("没有数据");
	    }else{
	    $('.ul_pagenav .pageTest').page({
	      leng: totalPage,//分页总数
	      activeClass: 'activP' , //active 类样式定义
	    });
	    }
	});