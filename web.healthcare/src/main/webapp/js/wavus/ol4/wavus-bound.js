
window.wavus = window.wavus || {};

wavus.bound = (function() {
	var _map;
	var _boundLayer;
	
	
	//인풋 geometry로 이동
	function setMultiPolygon(geom){
		
		var wktFormat = new ol.format.WKT();	//kwt 포맷 핸들러
		
		var feature = wktFormat.readFeature(geom, {
			dataProjection : wavus.config.DBProjection,
			featureProjection : wavus.config.mapInfo.projection
		});
		
		var source = _boundLayer.getSource();
		source.clear();
		source.addFeature(feature);
		
		_map.getView().fit(feature.getGeometry(),{
			duration : 500
		});
	};
	
	//하이라이트 레이어 피쳐 삭제
	function clearPolygon(){
		_boundLayer.getSource().clear();
	}
	
	
	function init(map, boundLayer){
		_map = map;
		_boundLayer = boundLayer;
	}

	function getBoundLayer() {
		return _boundLayer;
	}
	
	var module ={
		setMultiPolygon : setMultiPolygon,
		clearPolygon : clearPolygon,
		getBoundLayer : getBoundLayer,
		init : init
	}
	
	return module;
})();