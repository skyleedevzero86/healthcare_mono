
window.wavus = window.wavus || {};

wavus.graphic = (function() {
	var _map;
	var _graphicLayer;
	
	
	/**
	 * geometry : geometry 단일 객체
	 * symbol : 단일 심볼
	 * 
	 * list : list 지오메터리가 들어있는 객체
	 * listGeomId : string list의 지오메터리 객체 명
	 * listSymbolQryrId : string 지오메터리 심볼을 기준으로 처리할 객체 명
	 * listSymbolValue : object config에 등록 되어 있는 정의 값
	 * 
	 * clear : default true,   true 시 1개만 그림 false 시 여러개 그리는 모드
	 */
	function addGraphic(param){
		
		//그래픽 레이어 초기화
		if(param.clear !== false ){
			clear();
		}
		
		var wktFormat = new ol.format.WKT();
		
		
		//단일 지오메터리 등록
		if(param.geometry){
			var feature = wktFormat.readFeature(param.geometry, {
				dataProjection : wavus.config.DBProjection,
                featureProjection : wavus.config.mapInfo.projection
			});
			
			if(param.symbol){
				feature.setStyle(param.symbol);
			}else{
				feature.setStyle(wavus.symbol.getDefaultStyle());
			}
			
			_addGraphic(feature);
		}
		
		if(param.list){
			
			var geomId = 'geom';
			var symbolQryrId = 'symbolId';
			var symbolQryrValue = wavus.config.mapperColor;
			
			if(param.listGeomId){
				geomId = param.listGeomId; 
			}
			
			if(param.listSymbolQryrId){
				symbolQryrId = param.listSymbolQryrId; 
			}
			
			if(param.listSymbolValue){
				symbolQryrValue = param.listSymbolValue; 
			}
			
			var feautres = [];
			
			_.each(param.list, function(result){
				
				var feature = wktFormat.readFeature(result[geomId], {
                    dataProjection : wavus.config.DBProjection,
                    featureProjection : wavus.config.mapInfo.projection
                });
				
				var style;
				
				if(_.contains(_.keys(symbolQryrValue), result[symbolQryrId])){
					//값이 해당 리스트 중에 있다면
					style = wavus.symbol.getFeatureStyle(symbolQryrValue[result[symbolQryrId]])
				}else{
					style = wavus.symbol.getDefaultStyle();
				}
				
				
				//기본 속성정보 입력
				_.each(_.keys(result), function(key){
					feature.set(key, result[key]);
				});
				
				feature.setStyle(style);
				
				_addGraphic(feature);
				
			});
			
		}
	}
	
	
	/**
	 * 그래픽 레이어 초기화
	 */
	function clear(){
		_graphicLayer.getSource().clear();
	}
	
	/**
	 * 소스에 그래픽 추가하는 메소드
	 */
	function _addGraphic(feature){
		_graphicLayer.getSource().addFeature(feature);
	}
	
	/**
	 * 그래픽 레이어에 등록되어 있는 피처중 1개를 찾는다
	 * param 
	 *  - key	: 찾을 속성의 키
	 *  - value : 피처의 속성 값
	 * 
	 */
	function getFeature(param){
		var features = _graphicLayer.getSource().getFeatures();
		
		return _.find(features, function(feature){
//			console.log(feature.get(param.key));
			
			return feature.get(param.key) == param.value;
			
		});
		
//		_.each(features, function(feature){
//			console.log(feature.get(param.key));
//		});
		
//		console.log(features);
	}
	
	
	
	/**
	 * param
	 *  - graphicLayer : 그래픽 레이어
	 */
	function init(map, graphicLayer){
		_map = map;
		_graphicLayer = graphicLayer;
	}
	
	var module ={
		init : init,
		addGraphic : addGraphic,
		
		clear : clear,
		getGraphicLayer : function(){return _graphicLayer;},
		
		getFeature : getFeature
	}
	
	return module;
})();