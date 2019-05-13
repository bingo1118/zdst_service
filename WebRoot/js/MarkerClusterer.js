 /**
  2  * @fileoverview MarkerClusterer��Ǿۺ�������������ش�����Ҫ�ص���ͼ�ϲ���������������⣬��������ܡ�
  3  * ���������<a href="symbols/BMapLib.MarkerClusterer.html">MarkerClusterer</a>��
  4  * ����Baidu Map API 1.2��
  5  *
  6  * @author Baidu Map Api Group 
  7  * @version 1.2
  8  */


  /** 
 12  * @namespace BMap������library�������BMapLib�����ռ���
 13  */
  var BMapLib = window.BMapLib = BMapLib || {};
  (function(){
       
      /**
 18      * ��ȡһ����չ����ͼ��Χ�����������Ҷ�����һ��������ֵ��
 19      * @param {Map} map BMap.Map��ʵ��������
 20      * @param {BMap.Bounds} bounds BMap.Bounds��ʵ��������
 21      * @param {Number} gridSize Ҫ���������ֵ
 22      *
 23      * @return {BMap.Bounds} ������������ͼ��Χ��
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
 39      * ���հٶȵ�ͼ֧�ֵ����緶Χ��bounds���б߽紦��
 40      * @param {BMap.Bounds} bounds BMap.Bounds��ʵ��������
 41      *
 42      * @return {BMap.Bounds} ���ز�Խ�����ͼ��Χ
 43      */
      var cutBoundsInRange = function (bounds) {
          var maxX = getRange(bounds.getNorthEast().lng, -180, 180);
          var minX = getRange(bounds.getSouthWest().lng, -180, 180);
          var maxY = getRange(bounds.getNorthEast().lat, -74, 74);
          var minY = getRange(bounds.getSouthWest().lat, -74, 74);
          return new BMap.Bounds(new BMap.Point(minX, minY), new BMap.Point(maxX, maxY));
      }; 
   
      /**
 53      * �Ե���ֵ���б߽紦��
 54      * @param {Number} i Ҫ�������ֵ
 55      * @param {Number} min �±߽�ֵ
 56      * @param {Number} max �ϱ߽�ֵ
 57      * 
 58      * @return {Number} ���ز�Խ�����ֵ
 59      */
      var getRange = function (i, mix, max) {
          mix && (i = Math.max(i, mix));
          max && (i = Math.min(i, max));
          return i;
      };
   
      /**
 67      * �жϸ����Ķ����Ƿ�Ϊ����
 68      * @param {Object} source Ҫ���ԵĶ���
 69      *
 70      * @return {Boolean} ��������鷵��true�����򷵻�false
 71      */
      var isArray = function (source) {
          return '[object Array]' === Object.prototype.toString.call(source);
      };
   
      /**
 77      * ����item��source�е�����λ��
 78      * @param {Object} item Ҫ���ԵĶ���
 79      * @param {Array} source ����
 80      *
 81      * @return {Number} ����������ڣ��������������򷵻�-1
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
106          * @class ����������ش�����Ҫ�ص���ͼ�ϲ���������������⣬���������
107          * @constructor
108          * @param {Map} map ��ͼ��һ��ʵ����
109          * @param {Json Object} options ��ѡ��������ѡ�������<br />
110          *    markers {Array<Marker>} Ҫ�ۺϵı������<br />
111          *    girdSize {Number} �ۺϼ���ʱ��������ش�С��Ĭ��60<br />
112          *    maxZoom {Number} ���ľۺϼ��𣬴��ڸü���Ͳ�������Ӧ�ľۺ�<br />
113          *    minClusterSize {Number} ��С�ľۺ�������С�ڸ������Ĳ��ܳ�Ϊһ���ۺϣ�Ĭ��Ϊ2<br />
114          *    isAverangeCenter {Boolean} �ۺϵ�����λ���Ƿ������оۺ����ڵ��ƽ��ֵ��Ĭ��Ϊ������ھۺ��ڵĵ�һ����<br />
115          *    styles {Array<IconStyle>} �Զ���ۺϺ��ͼ������ο�TextIconOverlay��<br />
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
149      * ���Ҫ�ۺϵı�����顣
150      * @param {Array<Marker>} markers Ҫ�ۺϵı������
151      *
152      * @return �޷���ֵ��
153      */
     MarkerClusterer.prototype.addMarkers = function(markers){
         for(var i = 0, len = markers.length; i <len ; i++){
             this._pushMarkerTo(markers[i]);
         }
         this._createClusters();   
     };
  
     /**
162      * ��һ�������ӵ�Ҫ�ۺϵı��������
163      * @param {BMap.Marker} marker Ҫ��ӵı��
164      *
165      * @return �޷���ֵ��
166      */
     MarkerClusterer.prototype._pushMarkerTo = function(marker){
         var index = indexOf(marker, this._markers);
         if(index === -1){
             marker.isInCluster = false;
             this._markers.push(marker);//Marker�Ϸź�enableDragging�����仯������
         }
     };
  
     /**
176      * ���һ���ۺϵı�ǡ�
177      * @param {BMap.Marker} marker Ҫ�ۺϵĵ�����ǡ�
178      * @return �޷���ֵ��
179      */
     MarkerClusterer.prototype.addMarker = function(marker) {
         this._pushMarkerTo(marker);
         this._createClusters();
     };
  
     /**
186      * �����������ı�ǣ������ۺϵ㣬���ұ������оۺϵ�
187      * @return �޷���ֵ
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
207      * ���ݱ�ǵ�λ�ã�������ӵ�����ľۺ���
208      * @param {BMap.Marker} marker Ҫ���оۺϵĵ������
209      *
210      * @return �޷���ֵ��
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
237      * �����һ�εľۺϵĽ��
238      * @return �޷���ֵ��
239      */
     MarkerClusterer.prototype._clearLastClusters = function(){
         for(var i = 0, cluster; cluster = this._clusters[i]; i++){            
             cluster.remove();
         }
         this._clusters = [];//�ÿ�Cluster����
         this._removeMarkersFromCluster();//��Marker��cluster�����Ϊfalse
     };
  
     /**
249      * ���ĳ���ۺ��е����б��
250      * @return �޷���ֵ
251      */
     MarkerClusterer.prototype._removeMarkersFromCluster = function(){
         for(var i = 0, marker; marker = this._markers[i]; i++){
             marker.isInCluster = false;
         }
     };
     
     /**
259      * �����еı�Ǵӵ�ͼ�����
260      * @return �޷���ֵ
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
272      * ɾ���������
273      * @param {BMap.Marker} marker ��Ҫ��ɾ����marker
274      *
275      * @return {Boolean} ɾ���ɹ�����true�����򷵻�false
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
290      * ɾ���������
291      * @param {BMap.Marker} marker ��Ҫ��ɾ����marker
292      *
293      * @return {Boolean} ɾ���ɹ�����true�����򷵻�false
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
305      * ɾ��һ����
306      * @param {Array<BMap.Marker>} markers ��Ҫ��ɾ����marker����
307      *
308      * @return {Boolean} ɾ���ɹ�����true�����򷵻�false
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
325      * �ӵ�ͼ�ϳ���������еı��
326      * @return �޷���ֵ
327      */
     MarkerClusterer.prototype.clearMarkers = function() {
         this._clearLastClusters();
         this._removeMarkersFromMap();
         this._markers = [];
     };
  
     /**
335      * �������ɣ�����ı������Ե�
336      * @return �޷���ֵ
337      */
     MarkerClusterer.prototype._redraw = function () {
         this._clearLastClusters();
         this._createClusters();
     };
  
     /**
344      * ��ȡ�����С
345      * @return {Number} �����С
346      */
     MarkerClusterer.prototype.getGridSize = function() {
         return this._gridSize;
     };
  
     /**
352      * ���������С
353      * @param {Number} size �����С
354      * @return �޷���ֵ
355      */
     MarkerClusterer.prototype.setGridSize = function(size) {
         this._gridSize = size;
         this._redraw();
     };
  
     /**
362      * ��ȡ�ۺϵ�������ż���
363      * @return {Number} �ۺϵ�������ż���
364      */
     MarkerClusterer.prototype.getMaxZoom = function() {
         return this._maxZoom;       
     };
  
     /**
370      * ���þۺϵ�������ż���
371      * @param {Number} maxZoom �ۺϵ�������ż���
372      * @return �޷���ֵ
373      */
     MarkerClusterer.prototype.setMaxZoom = function(maxZoom) {
         this._maxZoom = maxZoom;
         this._redraw();
     };
  
     /**
380      * ��ȡ�ۺϵ���ʽ��񼯺�
381      * @return {Array<IconStyle>} �ۺϵ���ʽ��񼯺�
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
388      * ���þۺϵ���ʽ��񼯺�
389      * @param {Array<IconStyle>} styles ��ʽ�������
390      * @return �޷���ֵ
391      */
     MarkerClusterer.prototype.setStyles = function(styles) {
         this._styles = styles;
         this._redraw();
     };
  
     /**
398      * ��ȡ�����ۺϵ���С������
399      * @return {Number} �����ۺϵ���С������
400      */
     MarkerClusterer.prototype.getMinClusterSize = function() {
         return this._minClusterSize;
     };
  
     /**
406      * ���õ����ۺϵ���С������
407      * @param {Number} size �����ۺϵ���С������
408      * @return �޷���ֵ��
409      */
     MarkerClusterer.prototype.setMinClusterSize = function(size) {
         this._minClusterSize = size;
         this._redraw();
     };
  
     /**
416      * ��ȡ�����ۺϵ���ŵ��Ƿ��Ǿۺ������б�ǵ�ƽ�����ġ�
417      * @return {Boolean} true��false��
418      */
     MarkerClusterer.prototype.isAverageCenter = function() {
         return this._isAverageCenter;
     };
  
     /**
424      * ��ȡ�ۺϵ�Mapʵ����
425      * @return {Map} Map��ʾ����
426      */
     MarkerClusterer.prototype.getMap = function() {
       return this._map;
     };
  
     /**
432      * ��ȡ���еı�����顣
433      * @return {Array<Marker>} ������顣
434      */
     MarkerClusterer.prototype.getMarkers = function() {
         return this._markers;
     };
  
     /**
440      * ��ȡ�ۺϵ���������
441      * @return {Number} �ۺϵ���������
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
454      * @class ��ʾһ���ۺ϶��󣬸þۺϣ�������N����ǣ���N�������ɵķ�Χ������������ʾ��Map�ϵ�TextIconOverlay�ȡ�
455      * @constructor
456      * @param {MarkerClusterer} markerClusterer һ����Ǿۺ���ʾ����
457      */
     function Cluster(markerClusterer){
         this._markerClusterer = markerClusterer;
         this._map = markerClusterer.getMap();
         this._minClusterSize = markerClusterer.getMinClusterSize();
         this._isAverageCenter = markerClusterer.isAverageCenter();
         this._center = null;//���λ��
         this._markers = [];//���Cluster����������markers
         this._gridBounds = null;//�����ĵ�Ϊ׼�����ı�����gridSize�����صķ�Χ��Ҳ������Χ
         this._isReal = false; //����Ǹ��ۺ�
      
         this._clusterMarker = new BMapLib.TextIconOverlay(this._center, this._markers.length, {"styles":this._markerClusterer.getStyles()});
         //this._map.addOverlay(this._clusterMarker);
     }
     
     /**
473      * ��þۺ����һ����ǡ�
474      * @param {Marker} marker Ҫ��ӵı�ǡ�
475      * @return �޷���ֵ��
476      */
     Cluster.prototype.addMarker = function(marker){
         if(this.isMarkerInCluster(marker)){
             return false;
         }//Ҳ����marker.isInCluster�ж�,�����ж�OK�����������������
      
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
             }//�����µ�Center
         }
      
         marker.isInCluster = true;
         this._markers.push(marker);
     };
     
     /**
500      * ����dom����
501      * @return �޷���ֵ
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
518      * �ж�һ������Ƿ��ڸþۺ��С�
519      * @param {Marker} marker Ҫ�жϵı�ǡ�
520      * @return {Boolean} true��false��
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
536      * �ж�һ������Ƿ��ڸþۺ�����Χ�С�
537      * @param {Marker} marker Ҫ�жϵı�ǡ�
538      * @return {Boolean} true��false��
539      */
     Cluster.prototype.isMarkerInClusterBounds = function(marker) {
         return this._gridBounds.containsPoint(marker.getPosition());
     };
      
     Cluster.prototype.isReal = function(marker) {
         return this._isReal;
     };
  
     /**
549      * ���¸þۺϵ�����Χ��
550      * @return �޷���ֵ��
551      */
     Cluster.prototype.updateGridBounds = function() {
         var bounds = new BMap.Bounds(this._center, this._center);
         this._gridBounds = getExtendedBounds(this._map, bounds, this._markerClusterer.getGridSize());
     };
  
     /**
558      * ���¸þۺϵ���ʾ��ʽ��Ҳ��TextIconOverlay��
559      * @return �޷���ֵ��
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
588      * ɾ���þۺϡ�
589      * @return �޷���ֵ��
590      */
     Cluster.prototype.remove = function(){
         for (var i = 0, m; m = this._markers[i]; i++) {
             tmplabel = this._markers[i].getLabel(); 
             this._markers[i].getMap() && this._map.removeOverlay(this._markers[i])
             this._markers[i].setLabel(tmplabel)
         }//���ɢ�ı�ǵ�
         this._map.removeOverlay(this._clusterMarker);
         this._markers.length = 0;
         delete this._markers;
     }
  
     /**
603      * ��ȡ�þۺ������������б�ǵ���С��Ӿ��εķ�Χ��
604      * @return {BMap.Bounds} ������ķ�Χ��
605      */
     Cluster.prototype.getBounds = function() {
         var bounds = new BMap.Bounds(this._center,this._center);
         for (var i = 0, marker; marker = this._markers[i]; i++) {
             bounds.extend(marker.getPosition());
         }
         return bounds;
     };
  
     /**
615      * ��ȡ�þۺϵ���ŵ㡣
616      * @return {BMap.Point} �þۺϵ���ŵ㡣
617      */
     Cluster.prototype.getCenter = function() {
         return this._center;
     };
  
 })();