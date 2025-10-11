
window.wavus = window.wavus || {};

wavus.highlight = (function() {
	var _map;
	var _highlightLayer;

	function getHighlightLayer() {
		return _highlightLayer;
	}
	
	//인풋 geometry로 이동
	function setMultiPolygon(geom, isFit){
		
		var wktFormat = new ol.format.WKT();	//kwt 포맷 핸들러
		
		var feature = wktFormat.readFeature(geom, {
			dataProjection : wavus.config.DBProjection,
			featureProjection : wavus.config.mapInfo.projection
		});
		
		var source = _highlightLayer.getSource();
		source.clear();
		source.addFeature(feature);
		
		if(isFit == null || isFit){
			_map.getView().fit(feature.getGeometry(),{
				duration : 500
			});
		}
	};
	
	//하이라이트 레이어 피쳐 삭제
	function clearPolygon(){
		_highlightLayer.getSource().clear();
	}
	
	
	function init(map, highlightLayer){
		_map = map;
		_highlightLayer = highlightLayer;
	}
	
	var module ={
			setMultiPolygon : setMultiPolygon,
			clearPolygon : clearPolygon,
			getHighlightLayer : getHighlightLayer,
			init : init
	}
	
	return module;
})();