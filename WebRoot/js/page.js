/* 一个简单的分页，每次点击重渲染
******by wuati*****
*/
(function ($) {
  //默认参数 (放在插件外面，避免每次调用插件都调用一次，节省内存)
  var defaults = {
    //id : '#paging',//id
    leng: 9,//总页数
    activeClass: 'page-active' ,//active类
    firstPage: '首页',//
    lastPage: '末页',
    prv: '«',
    next: '»',
  };
  //扩展
  $.fn.extend({
    //插件名称
    page: function (options) {
      //覆盖默认参数
      var opts = $.extend(defaults, options);
      //主函数
      return this.each(function () {
        //激活事件
        var obj = $(this);
        var str1 = '';
        var str = '';
        var l = opts.leng;
        if (l > 1&&l < 10) {
          str1 = '<li><a href="javascript:" class="'+ opts.activeClass +'">1</a></li>';
          for (i = 2; i < l + 1; i++) {
            str += '<li><a href="javascript:">' + i + '</a></li>';
          }
        }else if(l > 9){
          str1 = '<li><a href="javascript:" class="'+ opts.activeClass +'">1</a></li>';
          for (i = 2; i < 10; i++) {
            str += '<li><a href="javascript:">' + i + '</a></li>';
          }
          //str += '<li><a href="javascript:">...</a></li>'
        } else {
          str1 = '<li><a href="javascript:" class="'+ opts.activeClass +'">1</a></li>';
        }
        obj.html('<div class="next" style="float:right">' + opts.next + '</div><div class="last" style="float:right">' + opts.lastPage + '</div><ul class="pagingUl">' + str1 + str + '</ul><div class="first" style="float:right">' + opts.firstPage + '</div><div class="prv" style="float:right">' + opts.prv + '</div>');
        
        obj.on('click', '.next', function () {
          var pageshow = parseInt($('.' + opts.activeClass).html());
          var l = opts.leng;//总页数
          var divName=$(this).text();
          //alert(divName)
          //alert("当前页pageshow:"+pageshow+";总页数:"+l);
          //console.log("当前页pageshow:"+pageshow+";总页数:"+l);
          if(isNaN(pageshow)){
        	  //console.log("当前页pageshow1:"+isNaN(pageshow));
        	  return false;
          }
          if($(this).parents("div.pageTest1").hasClass("pageTest1")){
        	  if(pageshow>=totalPage){
        		  goPage1(totalPage,5);
      		  }else{
      			  goPage1(pageshow+1,5);      	
      		  }
          }else if($(this).parents("div.ul_pagenav").hasClass("ul_pagenav")){
        	  if(pageshow>=totalPage){
        		  goPage(totalPage,10);
      		  }else{
      			  goPage(pageshow+1,10);	
      		  }
          }	
          if(pageshow == l) {
          }else if(pageshow > l-5&&pageshow < l){
        	  //console.log("当前页pageshow2:"+isNaN(pageshow));
            $('.' + opts.activeClass).removeClass(opts.activeClass).parent().next().find('a').addClass(opts.activeClass);
          }else if(pageshow > 0&&pageshow < 6){
        	  //console.log("当前页pageshow3:"+isNaN(pageshow));
            $('.' + opts.activeClass).removeClass(opts.activeClass).parent().next().find('a').addClass(opts.activeClass);
          }else {
        	  //console.log("当前页pageshow4:"+isNaN(pageshow));
            $('.' + opts.activeClass).removeClass(opts.activeClass).parent().next().find('a').addClass(opts.activeClass);
            fpageShow(pageshow,divName);
          }
        });
        obj.on('click', '.prv', function () {
          var pageshow = parseInt($('.' + opts.activeClass).html());
          var l = opts.leng;//总页数
          var divName=$(this).text();
          //alert(divName)
          //alert("当前页pageshow:"+pageshow+";总页数:"+l);
          if($(this).parents("div.pageTest1").hasClass("pageTest1")){
        	  if(pageshow>1){
        		  goPage1(pageshow-1,5);             
	      	  }else{
	      		  goPage1(1,5);
	      	  }
          }else if($(this).parents("div.ul_pagenav").hasClass("ul_pagenav")){
        	  if(pageshow>1){
        		  goPage(pageshow-1,10);
	      	  }else{
	      		  goPage(1,10);
	      	  }
          }	
          if (pageshow == 1) {
          }else if(pageshow > l-5&&pageshow < l+1){
            $('.' + opts.activeClass).removeClass(opts.activeClass).parent().prev().find('a').addClass(opts.activeClass);
          console.log("当前页pageshow2:"+isNaN(pageshow));
          }else if(pageshow > 1&&pageshow < 6){
            $('.' + opts.activeClass).removeClass(opts.activeClass).parent().prev().find('a').addClass(opts.activeClass);
          console.log("当前页pageshow3:"+isNaN(pageshow));
          }else {
            $('.' + opts.activeClass).removeClass(opts.activeClass).parent().prev().find('a').addClass(opts.activeClass);
            console.log("当前页pageshow4:"+isNaN(pageshow));
            fpageShow(pageshow,divName);
          }
        });

        obj.on('click', '.first', function(){
          var pageshow = 1;
          if($(this).parents("div.pageTest1").hasClass("pageTest1")){
        	  goPage1(pageshow,5);
          }else if($(this).parents("div.ul_pagenav").hasClass("ul_pagenav")){
        	  goPage(pageshow,10);
          }	
          $('.' + opts.activeClass).removeClass(opts.activeClass).parent().prev().find('a').addClass(opts.activeClass);
          fpagePrv(0);
        })
        obj.on('click', '.last', function(){
          var pageshow = l;
          if($(this).parents("div.pageTest1").hasClass("pageTest1")){
        	  goPage1(pageshow,5);
          }else if($(this).parents("div.ul_pagenav").hasClass("ul_pagenav")){
        	  goPage(pageshow,10);
          }	
          if(l>9){
            $('.' + opts.activeClass).removeClass(opts.activeClass).parent().prev().find('a').addClass(opts.activeClass);
            fpageNext(8);
          }else{
            $('.' + opts.activeClass).removeClass(opts.activeClass).parent().prev().find('a').addClass(opts.activeClass);
            fpageNext(l-1);
          }
        })

        obj.on('click', 'li', function(){
          var $this = $(this);
          var divName=$this.find("a").attr("href");
          //alert(divName);
          var l = opts.leng;//总页数
          var pageshow = $this.find('a').html();
          console.log("当前页pageshow:"+pageshow+";总页数:"+l);
          if($(this).parents("div.pageTest1").hasClass("pageTest1")){
        	  goPage1(pageshow,5);
          }else if($(this).parents("div.ul_pagenav").hasClass("ul_pagenav")){
        	  goPage(pageshow,10);
          }	
          if(l>9){
            if(pageshow > l-5&&pageshow < l+1){
            	//alert("当前页pageshow2:"+pageshow+";总页数:"+l);
              $('.' + opts.activeClass).removeClass(opts.activeClass);
              $this.find('a').addClass(opts.activeClass);
              fpageNext(8-(l-pageshow));
            }else if(pageshow > 0&&pageshow < 5){
            	//alert("当前页pageshow3:"+pageshow+";总页数:"+l);
              $('.' + opts.activeClass).removeClass(opts.activeClass);
              $this.find('a').addClass(opts.activeClass);
              fpagePrv(pageshow-1);
            }else{
            	//alert("当前页pageshow4:"+pageshow+";总页数:"+l);
              $('.' + opts.activeClass).removeClass(opts.activeClass);
              $this.find('a').addClass(opts.activeClass);
              fpageShow(pageshow,divName);
            }
          }else{
            $('.' + opts.activeClass).removeClass(opts.activeClass);
            $this.find('a').addClass(opts.activeClass);
          }
        })

        function fpageShow(pageshow,divName){
        	//alert(divName!="javascript:");
        	if(divName!="javascript:"){
        		pageshow = parseInt($('.' + opts.activeClass).html());
        	}
          //var pageshow = parseInt($('.' + opts.activeClass).html());
          var pageStart = pageshow - 4;
          var pageEnd = pageshow + 5;
          var str1 = '';
          for(i=0;i<9;i++){
            str1 += '<li><a href="javascript:" class="">' + (pageStart+i) + '</a></li>'
          }
          obj.find('ul').html(str1);
          obj.find('ul li').eq(4).find('a').addClass(opts.activeClass);
        }

        function fpagePrv(prv){
          var str1 = '';
          if(l>8){
            for(i=0;i<9;i++){
              str1 += '<li><a href="javascript:" class="">' + (i+1) + '</a></li>'
            }
          }else{
            for(i=0;i<l;i++){
              str1 += '<li><a href="javascript:" class="">' + (i+1) + '</a></li>'
            }
          }
          obj.find('ul').html(str1);
          obj.find('ul li').eq(prv).find('a').addClass(opts.activeClass);
        }

        function fpageNext(next){
          var str1 = '';
          if(l>8){
            for(i=l-8;i<l+1;i++){
              str1 += '<li><a href="javascript:" class="">' + i + '</a></li>'
            }
           obj.find('ul').html(str1);
           obj.find('ul li').eq(next).find('a').addClass(opts.activeClass);
          }else{
            for(i=0;i<l;i++){
              str1 += '<li><a href="javascript:" class="">' + (i+1) + '</a></li>'
            }
           obj.find('ul').html(str1);
           obj.find('ul li').eq(next).find('a').addClass(opts.activeClass);
          }
        }
      });
    }
  })
})(jQuery);