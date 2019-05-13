 /**
  2  * @fileoverview MarkerClusterer标记聚合器用来解决加载大量点要素到地图上产生覆盖现象的问题，并提高性能。
  3  * 主入口类是<a href="symbols/BMapLib.MarkerClusterer.html">MarkerClusterer</a>，
  4  * 基于Baidu Map API 1.2。
  5  *
  6  * @author Baidu Map Api Group 
  7  * @version 1.2
  8  */


  /** 
 12  * @namespace BMap的所有library类均放在BMapLib命名空间下
 13  */
  var BMapLib = window.BMapLib = BMapLib || {};
  (function(){
       
      /**
 18      * 获取一个扩展的视图范围，把上下左右都扩大一样的像素值。
 19      * @param {Map} map BMap.Map的实例化对象
 20      * @param {BMap.Bounds} bounds BMap.Bounds的实例化对象
 21      * @param {Number} gridSize 要扩大的像素值
 22      *
 23      * @return {BMap.Bounds} 返回扩大后的视图范围。
 24      */
      var getExtendedBounds = function(map, bounds, gridSize){
          bounds = cutBoundsInRange(bounds);
          var pixelNE = map.pointToPixel(bounds.getNorthEast());
          var pixelSW = map.pointToPixel(bounds.getSouthWest()); 
          pixelNE.x += gridSize;
          pixelNE.y -= gridSize;
          pixelSW.x -= gridSize;
          pixelSW.y += gridSize;
          var newNE = map.pixelToPoint(pixelNE);
          var newSW = map.pixelToPoint(pixelSW);
          return new BMap.Bounds(newSW, newNE);
      };
   
      /**
 39      * 按照百度地图支持的世界范围对bounds进行边界处理
 40      * @param {BMap.Bounds} bounds BMap.Bounds的实例化对象
 41      *
 42      * @return {BMap.Bounds} 返回不越界的视图范围
 43      */
      var cutBoundsInRange = function (bounds) {
          var maxX = getRange(bounds.getNorthEast().lng, -180, 180);
          var minX = getRange(bounds.getSouthWest().lng, -180, 180);
          var maxY = getRange(bounds.getNorthEast().lat, -74, 74);
          var minY = getRange(bounds.getSouthWest().lat, -74, 74);
          return new BMap.Bounds(new BMap.Point(minX, minY), new BMap.Point(maxX, maxY));
      }; 
   
      /**
 53      * 对单个值进行边界处理。
 54      * @param {Number} i 要处理的数值
 55      * @param {Number} min 下边界值
 56      * @param {Number} max 上边界值
 57      * 
 58      * @return {Number} 返回不越界的数值
 59      */
      var getRange = function (i, mix, max) {
          mix && (i = Math.max(i, mix));
          max && (i = Math.min(i, max));
          return i;
      };
   
      /**
 67      * 判断给定的对象是否为数组
 68      * @param {Object} source 要测试的对象
 69      *
 70      * @return {Boolean} 如果是数组返回true，否则返回false
 71      */
      var isArray = function (source) {
          return '[object Array]' === Object.prototype.toString.call(source);
      };
   
      /**
 77      * 返回item在source中的索引位置
 78      * @param {Object} item 要测试的对象
 79      * @param {Array} source 数组
 80      *
 81      * @return {Number} 如果在数组内，返回索引，否则返回-1
 82      */
      var indexOf = function(item, source){
          var index = -1;
          if(isArray(source)){
              if (source.indexOf) {
                  index = source.indexOf(item);
              } else {
                  for (var i = 0, m; m = source[i]; i++) {
                      if (m === item) {
                          index = i;
                          break;
                      }
                  }
              }
          }        
          return index;
      };
   
     /**
101      *@exports MarkerClusterer as BMapLib.MarkerClusterer
102      */
     var MarkerClusterer =  
         /**
105          * MarkerClusterer
106          * @class 用来解决加载大量点要素到地图上产生覆盖现象的问题，并提高性能
107          * @constructor
108          * @param {Map} map 地图的一个实例。
109          * @param {Json Object} options 可选参数，可选项包括：<br />
110          *    markers {Array<Marker>} 要聚合的标记数组<br />
111          *    girdSize {Number} 聚合计算时网格的像素大小，默认60<br />
112          *    maxZoom {Number} 最大的聚合级别，大于该级别就不进行相应的聚合<br />
113          *    minClusterSize {Number} 最小的聚合数量，小于该数量的不能成为一个聚合，默认为2<br />
114          *    isAverangeCenter {Boolean} 聚合点的落脚位置是否是所有聚合在内点的平均值，默认为否，落脚在聚合内的第一个点<br />
115          *    styles {Array<IconStyle>} 自定义聚合后的图标风格，请参考TextIconOverlay类<br />
116          */
         BMapLib.MarkerClusterer = function(map, options){
             if (!map){
                 return;
             }
             this._map = map;
             this._markers = [];
             this._clusters = [];
              
             var opts = options || {};
             this._gridSize = opts["gridSize"] || 60;
             this._maxZoom = opts["maxZoom"] || 18;
             this._minClusterSize = opts["minClusterSize"] || 2;           
             this._isAverageCenter = false;
             if (opts['isAverageCenter'] != undefined) {
                 this._isAverageCenter = opts['isAverageCenter'];
             }    
             this._styles = opts["styles"] || [];
          
             var that = this;
             this._map.addEventListener("zoomend",function(){
                 that._redraw();     
             });
      
             this._map.addEventListener("moveend",function(){
                  that._redraw();     
             });
  
             var mkrs = opts["markers"];
             isArray(mkrs) && this.addMarkers(mkrs);
         };
  
     /**
149      * 添加要聚合的标记数组。
150      * @param {Array<Marker>} markers 要聚合的标记数组
151      *
152      * @return 无返回值。
153      */
     MarkerClusterer.prototype.addMarkers = function(markers){
         for(var i = 0, len = markers.length; i <len ; i++){
             this._pushMarkerTo(markers[i]);
         }
         this._createClusters();   
     };
  
     /**
162      * 把一个标记添加到要聚合的标记数组中
163      * @param {BMap.Marker} marker 要添加的标记
164      *
165      * @return 无返回值。
166      */
     MarkerClusterer.prototype._pushMarkerTo = function(marker){
         var index = indexOf(marker, this._markers);
         if(index === -1){
             marker.isInCluster = false;
             this._markers.push(marker);//Marker拖放后enableDragging不做变化，忽略
         }
     };
  
     /**
176      * 添加一个聚合的标记。
177      * @param {BMap.Marker} marker 要聚合的单个标记。
178      * @return 无返回值。
179      */
     MarkerClusterer.prototype.addMarker = function(marker) {
         this._pushMarkerTo(marker);
         this._createClusters();
     };
  
     /**
186      * 根据所给定的标记，创建聚合点，并且遍历所有聚合点
187      * @return 无返回值
188      */
     MarkerClusterer.prototype._createClusters = function(){
         var mapBounds = this._map.getBounds();
         var extendedBounds = getExtendedBounds(this._map, mapBounds, this._gridSize);
         for(var i = 0, marker; marker = this._markers[i]; i++){
             if(!marker.isInCluster && extendedBounds.containsPoint(marker.getPosition()) ){ 
                 this._addToClosestCluster(marker);                
             }
         }
 
         var len = this._markers.length;
         for (var i = 0; i < len; i++) {
             if(this._clusters[i]){
                 this._clusters[i].render();
             }
         }
     };
  
     /**
207      * 根据标记的位置，把它添加到最近的聚合中
208      * @param {BMap.Marker} marker 要进行聚合的单个标记
209      *
210      * @return 无返回值。
211      */
     MarkerClusterer.prototype._addToClosestCluster = function (marker){
         var distance = 4000000;
         var clusterToAddTo = null;
         var position = marker.getPosition();
         for(var i = 0, cluster; cluster = this._clusters[i]; i++){
             var center = cluster.getCenter();
             if(center){
                 var d = this._map.getDistance(center, marker.getPosition());
                 if(d < distance){
                     distance = d;
                     clusterToAddTo = cluster;
                 }
             }
         }
      
         if (clusterToAddTo && clusterToAddTo.isMarkerInClusterBounds(marker)){
             clusterToAddTo.addMarker(marker);
         } else {
             var cluster = new Cluster(this);
             cluster.addMarker(marker);            
             this._clusters.push(cluster);
         }    
     };
  
     /**
237      * 清除上一次的聚合的结果
238      * @return 无返回值。
239      */
     MarkerClusterer.prototype._clearLastClusters = function(){
         for(var i = 0, cluster; cluster = this._clusters[i]; i++){            
             cluster.remove();
         }
         this._clusters = [];//置空Cluster数组
         this._removeMarkersFromCluster();//把Marker的cluster标记设为false
     };
  
     /**
249      * 清除某个聚合中的所有标记
250      * @return 无返回值
251      */
     MarkerClusterer.prototype._removeMarkersFromCluster = function(){
         for(var i = 0, marker; marker = this._markers[i]; i++){
             marker.isInCluster = false;
         }
     };
     
     /**
259      * 把所有的标记从地图上清除
260      * @return 无返回值
261      */
     MarkerClusterer.prototype._removeMarkersFromMap = function(){
         for(var i = 0, marker; marker = this._markers[i]; i++){
             marker.isInCluster = false;
             tmplabel = marker.getLabel();
             this._map.removeOverlay(marker);       
             marker.setLabel(tmplabel);
         }
     };
  
     /**
272      * 删除单个标记
273      * @param {BMap.Marker} marker 需要被删除的marker
274      *
275      * @return {Boolean} 删除成功返回true，否则返回false
276      */
     MarkerClusterer.prototype._removeMarker = function(marker) {
         var index = indexOf(marker, this._markers);
         if (index === -1) {
             return false;
         }
         tmplabel = marker.getLabel();
         this._map.removeOverlay(marker);
         marker.setLabel(tmplabel);
         this._markers.splice(index, 1);
         return true;
     };
  
     /**
290      * 删除单个标记
291      * @param {BMap.Marker} marker 需要被删除的marker
292      *
293      * @return {Boolean} 删除成功返回true，否则返回false
294      */
     MarkerClusterer.prototype.removeMarker = function(marker) {
         var success = this._removeMarker(marker);
         if (success) {
             this._clearLastClusters();
             this._createClusters();
         }
         return success;
     };
      
     /**
305      * 删除一组标记
306      * @param {Array<BMap.Marker>} markers 需要被删除的marker数组
307      *
308      * @return {Boolean} 删除成功返回true，否则返回false
309      */
     MarkerClusterer.prototype.removeMarkers = function(markers) {
         var success = false;
         for (var i = 0; i < markers.length; i++) {
             var r = this._removeMarker(markers[i]);
             success = success || r; 
         }
  
         if (success) {
             this._clearLastClusters();
             this._createClusters();
         }
         return success;
     };
  
     /**
325      * 从地图上彻底清除所有的标记
326      * @return 无返回值
327      */
     MarkerClusterer.prototype.clearMarkers = function() {
         this._clearLastClusters();
         this._removeMarkersFromMap();
         this._markers = [];
     };
  
     /**
335      * 重新生成，比如改变了属性等
336      * @return 无返回值
337      */
     MarkerClusterer.prototype._redraw = function () {
         this._clearLastClusters();
         this._createClusters();
     };
  
     /**
344      * 获取网格大小
345      * @return {Number} 网格大小
346      */
     MarkerClusterer.prototype.getGridSize = function() {
         return this._gridSize;
     };
  
     /**
352      * 设置网格大小
353      * @param {Number} size 网格大小
354      * @return 无返回值
355      */
     MarkerClusterer.prototype.setGridSize = function(size) {
         this._gridSize = size;
         this._redraw();
     };
  
     /**
362      * 获取聚合的最大缩放级别。
363      * @return {Number} 聚合的最大缩放级别。
364      */
     MarkerClusterer.prototype.getMaxZoom = function() {
         return this._maxZoom;       
     };
  
     /**
370      * 设置聚合的最大缩放级别
371      * @param {Number} maxZoom 聚合的最大缩放级别
372      * @return 无返回值
373      */
     MarkerClusterer.prototype.setMaxZoom = function(maxZoom) {
         this._maxZoom = maxZoom;
         this._redraw();
     };
  
     /**
380      * 获取聚合的样式风格集合
381      * @return {Array<IconStyle>} 聚合的样式风格集合
382      */
     MarkerClusterer.prototype.getStyles = function() {
    	 var that = this;
        var a=this._styles;
        if(that._styles[2]!=undefined){
        	that._styles[2]['url']="http://api.map.baidu.com/library/TextIconOverlay/1.2/src/images/m4.png";
        }
    	
         return a;
     };
  
     /**
388      * 设置聚合的样式风格集合
389      * @param {Array<IconStyle>} styles 样式风格数组
390      * @return 无返回值
391      */
     MarkerClusterer.prototype.setStyles = function(styles) {
         this._styles = styles;
         this._redraw();
     };
  
     /**
398      * 获取单个聚合的最小数量。
399      * @return {Number} 单个聚合的最小数量。
400      */
     MarkerClusterer.prototype.getMinClusterSize = function() {
         return this._minClusterSize;
     };
  
     /**
406      * 设置单个聚合的最小数量。
407      * @param {Number} size 单个聚合的最小数量。
408      * @return 无返回值。
409      */
     MarkerClusterer.prototype.setMinClusterSize = function(size) {
         this._minClusterSize = size;
         this._redraw();
     };
  
     /**
416      * 获取单个聚合的落脚点是否是聚合内所有标记的平均中心。
417      * @return {Boolean} true或false。
418      */
     MarkerClusterer.prototype.isAverageCenter = function() {
         return this._isAverageCenter;
     };
  
     /**
424      * 获取聚合的Map实例。
425      * @return {Map} Map的示例。
426      */
     MarkerClusterer.prototype.getMap = function() {
       return this._map;
     };
  
     /**
432      * 获取所有的标记数组。
433      * @return {Array<Marker>} 标记数组。
434      */
     MarkerClusterer.prototype.getMarkers = function() {
         return this._markers;
     };
  
     /**
440      * 获取聚合的总数量。
441      * @return {Number} 聚合的总数量。
442      */
     MarkerClusterer.prototype.getClustersCount = function() {
         var count = 0;
         for(var i = 0, cluster; cluster = this._clusters[i]; i++){
             cluster.isReal() && count++;     
         }
         return count;
     };
  
     /**
452      * @ignore
453      * Cluster
454      * @class 表示一个聚合对象，该聚合，包含有N个标记，这N个标记组成的范围，并有予以显示在Map上的TextIconOverlay等。
455      * @constructor
456      * @param {MarkerClusterer} markerClusterer 一个标记聚合器示例。
457      */
     function Cluster(markerClusterer){
         this._markerClusterer = markerClusterer;
         this._map = markerClusterer.getMap();
         this._minClusterSize = markerClusterer.getMinClusterSize();
         this._isAverageCenter = markerClusterer.isAverageCenter();
         this._center = null;//落脚位置
         this._markers = [];//这个Cluster中所包含的markers
         this._gridBounds = null;//以中心点为准，向四边扩大gridSize个像素的范围，也即网格范围
         this._isReal = false; //真的是个聚合
      
         this._clusterMarker = new BMapLib.TextIconOverlay(this._center, this._markers.length, {"styles":this._markerClusterer.getStyles()});
         //this._map.addOverlay(this._clusterMarker);
     }
     
     /**
473      * 向该聚合添加一个标记。
474      * @param {Marker} marker 要添加的标记。
475      * @return 无返回值。
476      */
     Cluster.prototype.addMarker = function(marker){
         if(this.isMarkerInCluster(marker)){
             return false;
         }//也可用marker.isInCluster判断,外面判断OK，这里基本不会命中
      
         if (!this._center){
             this._center = marker.getPosition();
             this.updateGridBounds();//
         } else {
             if(this._isAverageCenter){
                 var l = this._markers.length + 1;
                 var lat = (this._center.lat * (l - 1) + marker.getPosition().lat) / l;
                 var lng = (this._center.lng * (l - 1) + marker.getPosition().lng) / l;
                 this._center = new BMap.Point(lng, lat);
                 this.updateGridBounds();
             }//计算新的Center
         }
      
         marker.isInCluster = true;
         this._markers.push(marker);
     };
     
     /**
500      * 进行dom操作
501      * @return 无返回值
502      */
     Cluster.prototype.render = function(){
         var len = this._markers.length;
          
         if (len < this._minClusterSize) {
             for (var i = 0; i < len; i++) {
                 this._map.addOverlay(this._markers[i]);
             }
         } else {
             this._map.addOverlay(this._clusterMarker);
             this._isReal = true;
             this.updateClusterMarker();
         }
     }
 
     /**
518      * 判断一个标记是否在该聚合中。
519      * @param {Marker} marker 要判断的标记。
520      * @return {Boolean} true或false。
521      */
     Cluster.prototype.isMarkerInCluster= function(marker){
         if (this._markers.indexOf) {
             return this._markers.indexOf(marker) != -1;
         } else {
             for (var i = 0, m; m = this._markers[i]; i++) {
                 if (m === marker) {
                     return true;
                 }
             }
         }
         return false;
     };
 
     /**
536      * 判断一个标记是否在该聚合网格范围中。
537      * @param {Marker} marker 要判断的标记。
538      * @return {Boolean} true或false。
539      */
     Cluster.prototype.isMarkerInClusterBounds = function(marker) {
         return this._gridBounds.containsPoint(marker.getPosition());
     };
      
     Cluster.prototype.isReal = function(marker) {
         return this._isReal;
     };
  
     /**
549      * 更新该聚合的网格范围。
550      * @return 无返回值。
551      */
     Cluster.prototype.updateGridBounds = function() {
         var bounds = new BMap.Bounds(this._center, this._center);
         this._gridBounds = getExtendedBounds(this._map, bounds, this._markerClusterer.getGridSize());
     };
  
     /**
558      * 更新该聚合的显示样式，也即TextIconOverlay。
559      * @return 无返回值。
560      */
     Cluster.prototype.updateClusterMarker = function () {
         if (this._map.getZoom() > this._markerClusterer.getMaxZoom()) {
             this._clusterMarker && this._map.removeOverlay(this._clusterMarker);
             for (var i = 0, marker; marker = this._markers[i]; i++) {
                 this._map.addOverlay(marker);
             }
             return;
         }
  
         if (this._markers.length < this._minClusterSize) {
             this._clusterMarker.hide();
             return;
         }
  
         this._clusterMarker.setPosition(this._center);
          
         this._clusterMarker.setText(this._markers.length);
  
         var thatMap = this._map;
         var thatBounds = this.getBounds();
         this._clusterMarker.addEventListener("click", function(event){
             thatMap.setViewport(thatBounds);
         });
  
     };
  
     /**
588      * 删除该聚合。
589      * @return 无返回值。
590      */
     Cluster.prototype.remove = function(){
         for (var i = 0, m; m = this._markers[i]; i++) {
             tmplabel = this._markers[i].getLabel(); 
             this._markers[i].getMap() && this._map.removeOverlay(this._markers[i])
             this._markers[i].setLabel(tmplabel)
         }//清除散的标记点
         this._map.removeOverlay(this._clusterMarker);
         this._markers.length = 0;
         delete this._markers;
     }
  
     /**
603      * 获取该聚合所包含的所有标记的最小外接矩形的范围。
604      * @return {BMap.Bounds} 计算出的范围。
605      */
     Cluster.prototype.getBounds = function() {
         var bounds = new BMap.Bounds(this._center,this._center);
         for (var i = 0, marker; marker = this._markers[i]; i++) {
             bounds.extend(marker.getPosition());
         }
         return bounds;
     };
  
     /**
615      * 获取该聚合的落脚点。
616      * @return {BMap.Point} 该聚合的落脚点。
617      */
     Cluster.prototype.getCenter = function() {
         return this._center;
     };
  
 })();