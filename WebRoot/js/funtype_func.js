var fun_flag_arr = new Array(); // 已选中数组
var son_arr = new Array(); // 已选中子类数组
var par_arr = new Array(); // 已选中父类数组
// var fun_flag_arr = new Array('0100','2400','2402');
//判断是否在数组
function contains(arr, obj) {
	  var i = arr.length;
	  while (i--) {
	    if (arr[i] === obj) {
	      return true;
	    }
	  }
	  return false;
	}
var Funtype = {
	// 职位列表
	init : function() {
		var _str = '', _id = '';
		if (fun_flag_arr.length > 0) {
			for ( var i in fun_flag_arr) {
				_str += ',' + fun_a[fun_flag_arr[i]];
				_id += ',' + fun_flag_arr[i];
			}
			$('#btn_FuntypeID').val(_str.substring(1));
			$('#FuntypeID').val(_id.substring(1));
		}
	},
	Show : function() {
		var output = '', flag, output2 = '';
		for ( var i = 0; i < all_parent.length; i++) {
			try {
	if (typeof (all_parent[i].areaName) != "undefined") {
	output += '<li class="list_item" onclick="Funtype.SubLayer(\'' + i + '\',this)">'
							+ all_parent[i].areaName + '</li>';
				}
				;
			} catch (err) {
			/*	console.log(err);*/
			}

		}
		$('#FuntypeList').html('<ul>' + output + '</ul>');
		$('#FuntypeSelected dd').html(output2);

		// 鼠标悬停变色
		$('#FuntypeAlpha li').hover(function() {
			$(this).addClass('over');
		}, function() {
			$(this).removeClass('over');
		});
		// 点击弹出子菜单
		$('#FuntypeList li').click(function(e) {
			$(this).addClass('abc');
			var fag=$(this);
			$("#sublist").css({
				top : e.pageY -2,
				left : e.pageX - 20
			}).hover(function() {
				$(this).show()
			}, function() {
				$(this).hide();
				fag.removeClass('abc');
			})
		})
	},
	// 子职位 悬浮菜单
	SubLayer : function(id,obj) {
		var output = '', width, flag;
		// var myid = id.substr(0, 2);
		var len = 0;
	//父类未选
		var father_a=false;
		if(!contains(par_arr,all_parent[id].areaId)){
			output += '<h4 onclick="Funtype.Chk(\'f' +all_parent[id].areaId
			+ '\')"><a href="javascript:"id="'+all_parent[id].areaId +'" class="f'
			+ all_parent[id].areaId + '">' + all_parent[id].areaName
			+ '</a></h4>';
			father_a=false;
		}
		//父类已选
		else{
		output += '<h4 onclick="Funtype.del(\'f' +all_parent[id].areaId
				+ '\')"><a href="javascript:"id="'+all_parent[id].areaId +'" class="chkON f'
				+ all_parent[id].areaId + '">' + all_parent[id].areaName
				+ '</a></h4>';
		father_a=true;
		}
		if (all_parent[id].parent == undefined) {
			if(father_a){for ( var i = 0; i < children.length; i++) {
				if (children[i].parentId == all_parent[id].areaId) {
					//子类已选
					output += '<li><a href="javascript:"id="'+children[i].areaId +'" class="chkON s'
						+ children[i].areaId + '" onclick="Funtype.Chk(\'s'
						+ children[i].areaId
						+ '\')">' + children[i].areaName + '</a></li>';}
				
				len++;}
			}
			else{
			for ( var i = 0; i < children.length; i++) {
				if (children[i].parentId == all_parent[id].areaId) {
					//子类未选
					if(!contains(son_arr,children[i].areaId)){
					output += '<li><a href="javascript:"id="'+children[i].areaId +'" class="s'
							+ children[i].areaId + '" onclick="Funtype.Chk(\'s'
							+ children[i].areaId
							+ '\')">' + children[i].areaName + '</a></li>';
					}
					//子类已选
					else{output += '<li><a href="javascript:"id="'+children[i].areaId +'" class="chkON s'
						+ children[i].areaId + '" onclick="Funtype.Chk(\'s'
						+ children[i].areaId
						+ '\')">' + children[i].areaName + '</a></li>';}
				}
				len++;
			}
			}
		}
		width = len > 10 ? 440 : 220;
		output = '<div id="sub_funtype"><ul style="width:' + width + 'px">'
				+ output + '</ul></div>';
		$("#sublist").html(output).show();
	},
	Chk : function(id) { 	
		if (id.substr(0,1) == 'f') { // 选择父类
            $('.'+id).toggleClass('chkON');
//            $('.'+id).parent().parent().find('li').find('a').toggleClass('chkON');
            //判断是否选中
   if($('.'+id).hasClass('chkON')){
	   $('.'+id).parent().parent().find('li').find('a').addClass('chkON');
	   var num;
for(var x in all_parent){
		if(all_parent[x].areaId==id.substr(1)) {num=x;} 
	}
   if (all_parent[num].parent == undefined) { 
           for(var i in children){
        	   if(children[i].parentId==id.substr(1)){
        if(!contains(son_arr,children[i].areaId)){son_arr.push(children[i].areaId);}
        	   }
           }      
            }   
   else{
	   if(!contains(son_arr,id.substr(1))){son_arr.push(id.substr(1));}
   }
        }
        else{  
        	$('.'+id).parent().parent().find('li').find('a').removeClass('chkON');
        	for (var i in son_arr){
    			if(son_arr[i]==id.substr(1)) son_arr.splice(i,1);	
    		}	
       for(var i in children){
          	   if(children[i].parentId==id.substr(1)){
          for (var x in son_arr){
  			if(son_arr[x]==children[i].areaId) son_arr.splice(x,1);	
  		}
          	   }
             }   
        }
        }
	else { //选择子类
		$('.'+id).toggleClass('chkON');
		$('.'+id).parent().prevAll('h4').find('a').removeClass('chkON');
		parenid=$('.'+id).parent().prevAll('h4').find('a').attr('id');
		for (var i in par_arr){
			if(par_arr[i]==parenid) par_arr.splice(i,1);	
		}
		$('.'+id).parent().parent().find('li').find('a').each(function(){
			 if($(this).hasClass('chkON')){
				 
				 if(!contains(son_arr,$(this).attr('id'))){son_arr.push($(this).attr('id'));}
			 }
			 else{
				 for (var i in son_arr){
		    			if(son_arr[i]==$(this).attr('id'))son_arr.splice(i,1);
		    		}
			 }
		});

	}
		$('#FuntypeSelected li').hover(function(){$(this).addClass('over')},function(){$(this).removeClass('over')});
		showson();
	},
	// 确定
	del : function(id) {
		$('.'+id).removeClass('chkON');
		$('.'+id).parent().parent().find('li').find('a').removeClass('chkON');
		for (var i in par_arr){
			if(par_arr[i]==id.substr(1)) par_arr.splice(i,1);	
		}
		showson();
	},
	confirm : function() {
		var funStr = '', fun_Id = '';
		for ( var i in fun_flag_arr) {
			funStr += ',' + fun_a[fun_flag_arr[i]];
		}
		funStr = funStr.substring(1) ? funStr.substring(1) : '请选择职能类别';
		$('#btn_FuntypeID').val(funStr);
		$('#FuntypeID').val(fun_flag_arr);
		boxAlpha();
	},
	/* ****************************** 单选 ********************************* */
	// 单选输出
	Show2 : function() {
		var output = '', flag, output2 = '';
		for ( var i in fun_a) {
			if (i.substring(2) == '00') {
				output += '<li onclick="Funtype.SubLayer2(\'' + i + '\')">'
						+ fun_a[i] + '</li>';
			}
		}
		$('#drag').width('670px');
		$('#FuntypeList').html('<ul>' + output + '</ul>');
		// 鼠标悬停变色
		$('#FuntypeAlpha li').hover(function() {
			$(this).addClass('over')
		}, function() {
			$(this).removeClass('over')
		});
		// 点击弹出子菜单
		$('#FuntypeList li').click(function(e) {
			$("#sublist").css({
				top : e.pageY - 4,
				left : e.pageX - 4
			}).hover(function() {
				$(this).show()
			}, function() {
				$(this).hide();
			})
		})
	},
	// 子职位 悬浮菜单
	SubLayer2 : function(id) {
		var output = '', width;
		var myid = id.substr(0, 2);
		var len = 0;
		for ( var i in fun_a) {
			if (i.substr(0, 2) == myid) {
				if (i.substr(2) == '00') {
					output += '<h4 onclick="Funtype.Chk2(\'' + id
							+ '\')"><a href="javascript:">'
							+ all_parent[id].areaName + '</a></h4>';
				} else {
					output += '<li><a href="javascript:" onclick="Funtype.Chk2(\''
							+ i + '\')">' + fun_a[i] + '</a></li>';
					len++;
				}
			}
		}
		width = len > 10 ? 440 : 220;
		output = '<div id="sub_funtype" class="radio"><ul style="width:'
				+ width + 'px">123456' + output + '</ul></div>';
		$("#sublist").html(output).show();
	},
	Chk2 : function(id) {
		$('#btn_FuntypeID_2').val(fun_a[id]);
		$('#FuntypeID_2').val(id);
		$("#sublist").empty().hide();
		boxAlpha();
	}
}
//子类选中后展示
function showson(){
$('#FuntypeSelected dd').html("");
var html="";
var name_son="";
var parent_f="";
var parent_son;
	for(var x in son_arr){
		for(var i in children){
			if(children[i].areaId==son_arr[x]) name_son=i;
		}
//		html+="<li id='"+son_arr[x]+"' class='ck_s s"+son_arr[x]+"'>"+children[name_son].areaName+"</li>";
html+='<li class="s'+son_arr[x]+'" onclick="ck_delete(\'s'+son_arr[x]+'\')">'+children[name_son].areaName+'</li>';
	}
	for(var x in par_arr){
		for(var i in all_parent){
			if(all_parent[i].areaId==par_arr[x]) parent_son=i;
		}
//		html+="<li class='ck_f f"+par_arr[x]+"'>"+all_parent[parent_son].areaName+"</li>";
html+='<li class="f'+par_arr[x]+'" onclick="ck_delete(\'f'+par_arr[x]+'\')">'+all_parent[parent_son].areaName+'</li>';

	}
	$('#FuntypeSelected dd').html(html);
}
//删除已选
function ck_delete(id){
	if (id.substr(0,1) == 'f') { 
		for (var i in par_arr){
			if(par_arr[i]==id.substr(1)) par_arr.splice(i,1);	
		}
    	$('.'+id).parent().parent().find('li').find('a').removeClass('chkON');
	}
	else{ 
		 for (var i in son_arr){
		if(son_arr[i]==id.substr(1))son_arr.splice(i,1);
	}
}
showson();	
}
// 多选
function funtypeSelect() {
	var dragHtml = '<div id="FuntypeAlpha">'; // 职能类别
	dragHtml += '		<div id="FuntypeList"></div>'; // 职能类别列表
	dragHtml += '		<dl id="FuntypeSelected"><dt style="margin-top:20px;">已选区域：</dt><dd></dd></dl>';
	dragHtml += '</div>';
	$('#drag_con').html(dragHtml);
	Funtype.Show();
/*	boxAlpha();*/
/*	draglayer();*/
}

// 单选
function funtypeSelect_2() {
	var dragHtml = '<div id="FuntypeAlpha">'; // 职能类别
	dragHtml += '		<div id="FuntypeList"></div>'; // 职能类别列表
	dragHtml += '</div>';
	$('#drag_h').html('<b>请选择职能类别</b><span onclick="boxAlpha()">关闭</span>');
	$('#drag_con').html(dragHtml);
	Funtype.Show2();
	boxAlpha();
	draglayer();
}
function in_array(needle, haystack) {
	if (typeof needle == 'string' || typeof needle == 'number') {
		for ( var i in haystack) {
			if (haystack[i] == needle) {
				return true;
			}
		}
	}
	return false;
}
