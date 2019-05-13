// JavaScript Document
$(".tip").hide(); 
$(function(){  
        // 登录验证 
		var phone = $("#userId");  
        var password = $("#password");  
        var code = $("#code");  
        createCode();
		$("form #userId:input").blur(function(){  //注册事件
			if(!(/^1[34578]\d{9}$/.test(phone.val()))){ 
				$(this).siblings(".tip").text("手机号码有误，请输入手机号码").show(); 
			} else {
				$(this).siblings(".tip").text("手机号码有误，请输入手机号码").hide(); 
			}
        });  
		$("form #password:input").blur(function(){
            if ($(this).val() == ""){  
                $(this).siblings(".tip").text("密码有误，请输入密码").show(); 
            }  else {
            	 $(this).siblings(".tip").text("密码有误，请输入密码").hide(); 
			}
        });  
		$("form #code:input").blur(function(){
			if($(this).val() == ""){ 
				$(this).siblings(".tip").text("请输入验证码").show(); 
			} else{
				$(this).siblings(".tip").text("请输入验证码").hide(); 
			}    
        });  
        $("form  input").focus(function(){
            if ($(this).val() == ""){  
                $(this).siblings(".tip").hide();  
            }              
        }); 
        $("#loginForm").submit(function () { //后台登陆
				$.ajax({
					type: "get",
					url: "checkVerify.do",
					async: false,
					dataType: "json",
					data: { UserName: phone.val(),
						PWD:password.val(),
						Code: code.val()
					},
					success: function (data) {
						switch (data.errorCode) {
						case 0:
							//window.location.href="queryItems_test.do?uid="+data.UserId;
							document.write("<form action=queryItems_test.do method=get name=formx1 style='display:none'>");
							document.write("<input type=hidden id=currentId name=currentId value='"+data.userId+"' />");
							document.write("</form>");
							document.formx1.submit();
							break;

						default:
							 alert(data.error);
							break;
						}	
					},
					error: function () { alert("用户名密码验证失败");}
					
				});
			 return false;
		});
        
        $("#loginFireSys").submit(function () { //前台登陆
        	var privId=$("#jurisdiction").val();
        	var ymcode=but();
       	 if(ymcode){
			$.ajax({
				type: "get",
				url: "checkVerify.do",
				async: false,
				dataType: "json",
				data: { UserName: phone.val(),
					PWD:password.val(),				
					privId:privId
				},
				success: function (data) {
					switch (data.errorCode) {
					case 0:
						document.write("<form action=saveCurrentId.do method=post name=formx1 style='display:none'>");
						document.write("<input type=hidden id=currentId name=currentId value='"+data.userId+"' />");
						document.write("</form>");
						document.formx1.submit();
						//window.open("saveCurrentId.do?currentId="+data.userId,"_self");
						
						break;

					default:
						 console.log(data.error);
						break;
					}	
				},
				error: function () { alert("用户名密码验证失败");}
				
			});
		 return false;
       	 }
       	 else{ alert("验证码错误"); }
        });
        

        $("#add").submit(function () {
			var username = $("#username");
			var name = $("#name");
			var quanxian = $("#quanxian");
			var arr_v = new Array(); 
		 arr_v=son_arr;
//			arr_v.join(','); 
//			alert(checked);
				if(username.val()==""){
					alert("账号不能为空")
					return false;
				}
				if(name.val()==""){
					alert("用户名称不能为空")
					return false;
				}
				if(quanxian.val()==""){
					alert("用户权限不能为空")
					return false;
				}
				if(arr_v.length==0){
					alert("区域不能为空")
					return false;
				}
				$.ajax({
					type: "post",
					url: "addUserResult.do",
					async: false,
					traditional :true, 
					dataType: "json",
					data: { 
						addressed:arr_v,
						username: username.val(),
						name: name.val(),
						quanxian: quanxian.val()
					},
					success: function (data) {
						username.val("");
						name.val("");
						quanxian.val("");
						son_arr.splice(0,son_arr.length);
						showson();	
						alert("添加账号成功");
					},
					error: function () { alert("添加账号失败")}
					
				});
			 return false;
		});
		
		//注册验证
		$("#registerForm").submit(function(){
			$(".tip").css('visibility','visible');
			var username_reg=/^[\@A-Za-z0-9\!\#\$\%\^\&\*\.\~]{6,22}$/;  
			var password_reg=/^[\@A-Za-z0-9\!\#\$\%\^\&\*\.\~]{6,22}$/;
			var phone_reg=/^1[3|4|5|7|8][0-9]\d{4,8}$/i;//验证手机正则(输入前7位至11位)    
			if(!username_reg.test(username.val())){
				$(".tip").text("用户名格式有误，请输入6~16位的数字、字母或特殊字符！");
				return false;
			}
			if(!phone_reg.test(phone.val())){
				$(".tip").text("手机号码有误，请输入手机号码");
				alert(phone.val())
				return false;
			}
			if(!password_reg.test(password.val())){
				$(".tip").text("密码格式有误，请输入6~16位的数字、字母或特殊字符！");
				return false;
			}
			if(password.val()!=password2.val()){
				$(".tip").text("两次密码不一致");
				return false;
			}
			if(code.val()==""){
				$(".tip").text("验证码有误");
				return false;
			}
			$.ajax({
					type: "post",
					url: "Login.ashx",
					async: false,
					data: { 
						jurisdiction: jurisdiction.val(),
						username: username.val(),
						password: password.val(),
						code: code.val()
					},
					success: function (data) {
						window.location.href="xinxiguanli.html";
					},
					error: function () {
						$(".tip").text("用户名不存在");
					}
					
				});
			return false;
		});
		
});  
//验证码
var  code;
function createCode()
{ //创建验证码函数
	code = "";
var codeLength =4;//验证码的长度
var selectChar = new Array('A','B','C','D','E','F','G','H','K','L','M','P','Q','R','S','T','V','W','Y','Z');//所有候选组成验证码的字符，当然也可以用中文的
var leng=selectChar.length;
for(var i=0;i<codeLength;i++)
{ 
var charIndex =Math.floor(Math.random()*leng);
code +=selectChar[charIndex]; 
}
//设置验证码的显示样式，并显示
document.getElementById("discode").style.fontFamily="Fixedsys"; //设置字体
document.getElementById("discode").style.letterSpacing="5px"; //字体间距
document.getElementById("discode").style.color="#95a998"; //字体颜色
document.getElementById("discode").innerHTML=code; // 显示
}
function but()
{//验证验证码输入是否正确
var val1=document.getElementById("code").value;
var val2=code;
val1=val1.toUpperCase();
if(val1!=val2){
 return false;
}
else{ return true; }
}