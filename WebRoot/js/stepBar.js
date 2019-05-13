/**
 * author锛歺yzsyx@163.com 鍦ㄤ娇鐢ㄤ腑閬囧埌闂鎴栧彂鐜癰ug鏇存垨鑰呮湁鎶�湳浜ゆ祦鐖卞ソ鐨勬湅鍙嬭鍙戦偖浠跺埌鎴戠殑email锛屽ぇ瀹朵竴璧峰涔狅紝涓�捣杩涙锛�
 *
 *
 * 鍒濆鍖栬皟鐢ㄦ柟娉� 鍦╦s鐨刼nload浜嬩欢鎴杍q鐨�(document).ready()閲岄潰璋冪敤stepBar.init(id, option)鍗冲彲銆�
 * 绗竴涓弬鏁颁负stepBar瀹瑰櫒鐨刬d锛屽繀濉紝鍏佽浼犲叆鐨勫�鍖呮嫭濡備笅锛�
 *     jQuery瀵硅薄
 *     javascript瀵硅薄
 *     DOM鍏冪礌锛堝彲杞寲涓篒D鐨勫瓧绗︿覆锛屽 鈥渟tepBar鈥�|| 鈥�stepBar鈥濓級 绾犻敊锛氳鎶妀Query瀵硅薄鐨勨�#鈥濆啓鎴愨�.鈥濅篃鍚屾牱鑳借瘑鍒嚭鏉ワ紝浣嗘槸蹇呴』淇濊瘉娆″弬鏁拌兘杞寲鎴愬厓绱營D
 * 绗簩涓弬鏁颁负涓�釜瀵硅薄鐩存帴閲忥紝閫夊～锛屽寘鍚涓嬬殑闆朵釜鎴栧涓�
 *     step                string number   鐩爣杩涘害  榛樿涓�锛堢涓�锛夛紝閫夊～
 *     change              boolean    璁剧疆鎻掍欢鏄惁鍙鎿嶄綔锛岄�濉� 榛樿false
 *     animation           boolean    璁剧疆鎻掍欢鏄惁閲囩敤鍔ㄧ敾褰㈠紡锛堝墠鎻恠tepBar.change涓簍rue锛夛紝閫夊～  榛樿false
 *     speed               number     鍔ㄧ敾閫熷害锛堝墠鎻愶紝change鍜宎nimation涓簍rue锛�閫夊～   榛樿1000ms
 *     stepEasingForward   string     浠庡綋鍓嶆鏁板線鍓嶈繃娓″姩鐢伙紙鍓嶆彁锛宑hange鍜宎nimation涓簍rue锛�閫夊～  榛樿 "easeOutExpo"  鏇村鍙傛暟璇峰弬鐓�jquery.easing.js
 *     stepEasingBackward  string     浠庡綋鍓嶆鏁板線鍚庤繃娓″姩鐢伙紙鍓嶆彁锛宑hange鍜宎nimation涓簍rue锛�閫夊～  榛樿 "easeOutElastic"  鏇村鍙傛暟璇峰弬鐓�jquery.easing.js
 *
 *     PS锛氫笉鍚堟硶鐨勫弬鏁板皢寮鸿浣跨敤榛樿鍊�
 */

var stepBar = {
    bar : {},
    item : {},
    barWidth : 0,
    itemCount : 2,
    itemWidth : 0,
    processWidth : 0,
    curProcessWidth : 0,
    step : 1,
    curStep : 0,
    triggerStep : 1,
    change : false,
    animation : false,
    speed : 1000,
    stepEasingForward : "easeOutCubic",
    stepEasingBackward : "easeOutElastic",
    
    init : function(id, option){
        if (typeof id == "object" || id.indexOf("#") == 0) {
            this.bar = $(id);
        } else {
            if (id.indexOf(".") == 0) {
                id = id.substring(1, id.length);
            }
            this.bar = $("#" + id);
        }
        this.change = option.change ? true : false;
        this.animation = this.change && option.animation ? true : false;
        this.layout();
        this.item = this.bar.find(".ui-stepInfo");
        if (this.item.length < 2) {
            return;
        }
        this.bar.show();
        this.itemCount = this.item.length;
        this.step = !isNaN(option.step) && option.step <= this.itemCount && option.step > 0 ? option.step : 1;
        this.triggerStep = this.step;
        if (!isNaN(option.speed) && option.speed > 0) {
            this.speed = parseInt(option.speed);
        }
        this.stepEasing(option.stepEasingForward, false);
        this.stepEasing(option.stepEasingBackward, true);
        this.stepInfoWidthFun();
    },
    stepEasing : function(stepEasing, backward){
        if(typeof jQuery.easing[stepEasing] === "function"){
            if(backward){
                this.stepEasingBackward = stepEasing;
            } else {
                this.stepEasingForward = stepEasing;
            }
        }
    },
    layout : function(){
        this.bar.find(".ui-stepInfo .ui-stepSequence").addClass("judge-stepSequence-hind");
        this.bar.find(".ui-stepInfo:first-child .ui-stepSequence").addClass("judge-stepSequence-pre");
    },
    classHover : function(){
        if(this.change){
            this.bar.find(".ui-stepInfo .judge-stepSequence-pre").removeClass("judge-stepSequence-hind-change").addClass("judge-stepSequence-pre-change");
            this.bar.find(".ui-stepInfo .judge-stepSequence-hind").removeClass("judge-stepSequence-pre-change").addClass("judge-stepSequence-hind-change");
        }
    },
    stepInfoWidthFun : function(){
        if(this.itemCount > 0){
            this.barWidth = this.bar.width();
            this.itemWidth = Math.floor((this.barWidth * 2.8) / (this.itemCount - 1));
            this.bar.find(".ui-stepLayout").width(Math.floor(this.barWidth * 2.8 + this.itemWidth));
            this.item.width(this.itemWidth);
            //this.bar.find(".ui-stepLayout").css({"margin-left": -Math.floor(this.itemWidth / 2) + 10 });
            if(this.change){
                this._event();
            }
            this.percent();
        }
    },
    _event : function(){
        var _this = this;
        _this.bar.on("click", ".ui-stepSequence", function(){
            var triggerStep = $(this).text();
            if(!isNaN(parseInt(triggerStep)) && triggerStep > 0 && triggerStep <= _this.itemCount && triggerStep != _this.curStep){
                _this.triggerStep = triggerStep;
                _this.percent();
            }
        });
    },
    percent : function(){
        var _this = this;
        var calc = 100 / (_this.itemCount - 1);
        _this.processWidth = calc * (_this.triggerStep - 1) + "%";
        if(_this.animation){
            if(_this.triggerStep < _this.curStep){
                 //_this._animate();
                 //_this.curStep--;
            } else {
                 _this.curStep++;
            }
            _this.curProcessWidth = calc * (_this.curStep - 1) + "%";
            _this.bar.find(".ui-stepProcess").stop(true).animate({"width" : _this.curProcessWidth}, _this.speed, function(){
                _this._animate();
                if(_this.processWidth != _this.curProcessWidth){
                    _this.percent();
                }
            });
        } else {
            if(_this.curProcessWidth != _this.processWidth){
                _this.curProcessWidth = _this.processWidth;
                _this.bar.find(".ui-stepProcess").width(_this.processWidth);
                _this.jump();
            }
        }
    },
    jump : function(){
        this.bar.find(".ui-stepInfo .ui-stepSequence").removeClass("judge-stepSequence-pre").addClass("judge-stepSequence-hind");
        this.bar.find(".ui-stepInfo:nth-child(-n+" + this.triggerStep + ") .ui-stepSequence").removeClass("judge-stepSequence-hind").addClass("judge-stepSequence-pre");
        this.classHover();
    },
    _animate : function(){
        var stepSequence_size = {},
            easing = this.stepEasingBackward,
            preClass,
            hindClass;

        if(this.triggerStep < this.curStep){
            //stepSequence_size.padding = "6px 10px";
            //preClass = "judge-stepSequence-pre";
            //hindClass = "judge-stepSequence-hind";
            //easing = this.stepEasingForward;
        } else {
            stepSequence_size.padding = "8px 12px";
            preClass = "judge-stepSequence-hind";
            hindClass = "judge-stepSequence-pre";
        }
        this.bar.find(".ui-stepInfo:nth-child(" + this.curStep + ") .ui-stepSequence").removeClass(preClass).addClass(hindClass);
        this.bar.find(".ui-stepInfo:nth-child(" + this.curStep + ") .ui-stepSequence").animate(stepSequence_size, 500, easing);
        this.classHover();
    }
};