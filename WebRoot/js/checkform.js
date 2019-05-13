/**
* 鍘绘帀鍓嶅悗绌烘牸
*/
function Trim(str)
{
  return str.replace(/(^\s*)|(\s*$)/g,"");
}

/**
* 鍒ゆ柇鏄惁涓虹┖
*/

function checkHasValue(checkStr)
{
    if (Trim(checkStr).length == 0){
        
        return false;
    }
    return true;
}


/**
* 鍒ゆ柇鏄惁鏄暟瀛�*
*/
function checkNumber(checkStr)
{

	 if (!checkStr.match("^[0-9]*[1-9][0-9]*$") && !checkStr.match("^((-\\d+)|(0+))$") && !checkStr.match("^\\d+(\\.\\d+)?$") && !checkStr.match("^((-\\d+(\\.\\d+)?)|(0+(\\.0+)?))$")){
		 
         return false;
	 }
     return true;
}

/**
* 鍒ゆ柇鏄惁鏁存暟
*/
function checkInt(checkStr)
{
     
     if (!checkStr.match("^-?\\d+$")){
         
         return false;
	 }
     return true;
}

/**
* 鍒ゆ柇鏄惁姝ｆ暣鏁�*/
function checkPInt(checkStr)
{
     
     if (!checkStr.match("^[1-9]+[0-9]*$")){
         
         return false;
     }
     return true;
}

/**
* 鍒ゆ柇鏄惁闈炶礋鏁存暟
*/
function checkNotNInt(checkStr){
     
     if (!checkStr.match("^[0-9]+$")){
     
         return false;
     }
     return true;
}

/**
* 鍒ゆ柇鏃ユ湡鏍煎紡     YYYY-MM-DD
*/
function  checkDate(strDate){
     var  strSeparator  =  "-";  //鏃ユ湡鍒嗛殧绗�     var  strDateArray;
     var  intYear;
     var  intMonth;
     var  intDay;
     var  boolLeapYear;

     strDateArray  =  strDate.split(strSeparator);

     if	(strDateArray.length!=3){
         
         return  false;
     }

     intYear  =  parseInt(strDateArray[0],10);
     intMonth =  parseInt(strDateArray[1],10);
     intDay   =  parseInt(strDateArray[2],10);

     if(isNaN(intYear)  ||isNaN(intMonth)  || isNaN(intDay)){
               
               return  false;
     }
     if(intMonth>12  ||intMonth<1){
               
               return  false;
     }
     if((intMonth==1  ||intMonth==3  ||intMonth==5  ||intMonth==7  ||intMonth==8  ||intMonth==10  ||intMonth==12)&&(intDay>31  ||intDay<1)){
               
               return  false;
     }
     if((intMonth==4  ||intMonth==6  ||intMonth==9  ||intMonth==11)&&(intDay>30  ||intDay<1)){
               
               return  false;
     }
     if(intMonth==2){
           if(intDay<1){
               
                   return  false;
           }
           boolLeapYear  =  false;
           if((intYear%100)==0){
                 if((intYear%400)==0)  boolLeapYear  =  true;
           }
           else{
                 if((intYear%4)==0)  boolLeapYear  =  true;
           }

           if(boolLeapYear){
                 if(intDay>29){
               
                       return  false;
               }
           }
           else{
                 if(intDay>28){
                  
                       return  false;
                 }
           }
     }
     return  true;
}

/**
* 鐢佃瘽鍙风爜楠岃瘉
*/
function checkPhone(checkStr){
    checkOkStr = "^[0-9][0-9\-]{5,20}$";
    if (!checkStr.match(checkOkStr)){
        
        return false
    }
    return true;
}


/**
* 鎵嬫満鍙风爜楠岃瘉^1[34578]\d{9}$
*/
function checkMobile(checkStr)
{
if (!checkStr.match("^1[34578]\d{9}$")){
		
		return false;
	}
    return true;
}

/**
* 閭斂缂栫爜鍒ゆ柇
*/
function isZipCode(checkStr){
	if (!isEmpty(checkStr)){
		if(!isCharsInBag (checkStr, "0123456789")){
	        
			return false;
		}
		if (checkStr.length==6){
	        
			return true;
	        
		}else{
	        
			return false;
		}
	} else {
	return true;
	}
}


/**
* Email妫�祴
*/
function checkEmail(checkStr){
    if (!checkStr.match("^[\\w-]+(\\.[\\w-]+)*@[\\w-]+(\\.[\\w-]+)+$")){
		
		return false;
	}
    return true;
}

/**
* Url妫�祴
*/
function checkUrl(checkStr){
    if (!checkStr.match("^[a-zA-z]+://(\\w+(-\\w+)*)(\\.(\\w+(-\\w+)*))*(\\?\\S*)?$")){
        
        return false;
    }
    return true;
}

/**
* 鐢ㄦ埛鍚嶆娴�*/
function checkUserName(checkStr){
    if (!checkStr.match("^([a-zA-Z0-9_]|[^\x00-\xff])+$")){
        
        return false;
    }
    return true;
}

/**
* 鐢ㄦ埛鍚嶆娴�*/
function checkDbUserName(checkStr){
    if (!checkStr.match("^[\\w-]+$")){
        
        return false;
    }
    return true;
}

/**
* 鏁版嵁搴撶敤鎴峰悕妫�祴
*/
function checkDbUserName(checkStr){
    if (!checkStr.match("^[\\w-]+$")){
        return false;
    }
    return true;
}

/**
* 瀵嗙爜妫�祴
*/
function checkPassword(checkStr){
    if (!checkStr.match("^[^\r\t\n\32]+$")){
		
        return false;
    }
    return true;
}

/**
* 身份证验证
*/
function checkIdcard(checkStr){
    if (!checkStr.match("^[1-9]{1}[0-9]{14}$|^[1-9]{1}[0-9]{16}([0-9]|[xX])$")){		
        return false;
    }
    return true;
}

/**
*妫�煡鏂囦欢鍚庣紑
*/
function checkFileExt(checkStr , arrExt){
	sflag = false;
	for (i = 0 ; i < arrExt.length ; i++){
		okStr = ".+[.]+" + arrExt[i] + "+$";
		if (checkStr.match(okStr)){
			sflag = true;
			break;
		}
	}
	if (sflag == false){
		return false;
	}
	return sflag;

}


/**
* 妫�煡鍥剧墖鏂囦欢鍚庣紑
*/
function checkImageExt(checkStr){
	var sflag = false;
	var arrExt = new Array("jpg" , "JPG" , "gif" , "GIF" , "png" , "PNG");
	sflag = checkFileExt(checkStr , arrExt);
	return sflag;
}//end function


/**
* 瀹㈡埛绔彇寰楁枃鏈唴瀹归暱搴�鍖呮嫭涓枃)
*/
function cstrLength(str) 
{ 
	var len=0; 
	for(var i=0;i<str.length;i++) 
	{ 
		char = str.charCodeAt(i); 
		if(!(char>255)) 
		{ 
			len = len + 1; 
		} 
		else 
		{ 
			len = len + 2; 
		} 
	} 
	return len; 
} 

